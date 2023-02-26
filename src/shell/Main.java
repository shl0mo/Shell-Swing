//package shell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.text.NumberFormat.Style;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Main {
	static JFrame frame;
	static JPanel panel; 
	static JTextPane textpane;
	static int largura_janela = 800;
	static int altura_janela = 500;
	static String diretorio = System.getProperty("user.dir");
	static String contrabarra = "\\";
	static String barra = "/";
	static boolean posicao_caret_setada;
	static int posicao_inicial_caret;
	
	public static void main (String [] args) throws BadLocationException {
		contrabarra = barra;
		frame = new JFrame("Shell");
		frame.setBounds(0, 0, largura_janela, altura_janela);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		panel = new JPanel(new BorderLayout());
		panel.setBounds(0, 0, largura_janela, altura_janela);
		frame.add(panel);
		
		textpane = criaTextPane(largura_janela, altura_janela);
		JScrollPane scrollpane = criaScrollPane(textpane, largura_janela, altura_janela);
		
		panel.add(scrollpane, BorderLayout.CENTER);
		
		
		CaretListener caret_listener = new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent caretEvent) {
				highlight();
			}
	    };
	    
	    textpane.addCaretListener(caret_listener);
	    
	}
	
	public static JTextPane criaTextPane (int largura_janela, int altura_janela) throws BadLocationException {
		JTextPane textpane = new JTextPane();
		textpane.setBackground(Color.BLACK);		
		Font fonte = new Font("Consolas", Font.PLAIN, 14);
		textpane.setFont(fonte);		
		textpane.setBounds(0, 0, largura_janela, altura_janela);
		StyledDocument style = textpane.getStyledDocument();
		SimpleAttributeSet align = new SimpleAttributeSet();
		StyleConstants.setAlignment(align, StyleConstants.ALIGN_LEFT);
		style.setParagraphAttributes(0, style.getLength(), align, false);
		adicionaPath(textpane);
		adicionaCaret(textpane);
		return textpane;
	}
	
	
	public static JScrollPane criaScrollPane (JTextPane textpane, int largura_janela, int altura_janela) {
		JScrollPane scrollpane = new JScrollPane(textpane);
		scrollpane.setBounds(0, 0, largura_janela, altura_janela);
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		return scrollpane;
	}
	
	
	public static void adicionaPath (JTextPane textpane) throws BadLocationException {
		StyledDocument doc = textpane.getStyledDocument();
	    javax.swing.text.Style texto_verde = textpane.addStyle("", null);
	    StyleConstants.setForeground(texto_verde, Color.GREEN);
	    doc.insertString(doc.getLength(), diretorio, texto_verde);
	    
	    doc = textpane.getStyledDocument();
	    javax.swing.text.Style texto_amarelo = textpane.addStyle("", null);
	    StyleConstants.setForeground(texto_amarelo, Color.YELLOW);
	    doc.insertString(doc.getLength(), " ~$", texto_amarelo);
	}
	
	
	public static void adicionaCaret (JTextPane textpane) throws BadLocationException {
		textpane.setCaretColor(Color.WHITE);		
		StyledDocument doc = textpane.getStyledDocument();
	    javax.swing.text.Style texto_branco = textpane.addStyle("", null);
	    StyleConstants.setForeground(texto_branco, Color.WHITE);
	    doc.insertString(doc.getLength(), " ", texto_branco);
		textpane.putClientProperty("caretWidth", 5);
	}
	
	public static void adicionaMensagem (JTextPane textpane, String mensagem) throws BadLocationException {
		StyledDocument doc = textpane.getStyledDocument();
		javax.swing.text.Style texto_branco = textpane.addStyle("", null);
	    StyleConstants.setForeground(texto_branco, Color.WHITE);
	    doc.insertString(doc.getLength(), mensagem, texto_branco);
	}
	
	public static String removeEnter (String string) {
		String nova_string = "";
		String array_string[] = string.split("");
		for (int i = 0; i < string.length() - 1; i++) {
			nova_string = nova_string + array_string[i];
		}
		return nova_string;
	}
	
	public static Set<String> listaArquivos(String dir) {
		File file = new File(dir);
		File[] arquivos = file.listFiles();
		Set<String> set_arquivos = new HashSet<String>();
		if (arquivos.length > 0) {
			for (File arquivo: arquivos) {
				if (arquivo.isDirectory()) set_arquivos.add(arquivo.getName() + "/");
				else set_arquivos.add(arquivo.getName());
			}
		}
		return set_arquivos;
	}
	
	public static Set<String> listaDiretorios (String dir) {
		File file = new File(dir);
		File[] diretorios = file.listFiles();
		Set<String> set_diretorios = new HashSet<String>();
		if (diretorios.length > 0) {
			for (File arquivo: diretorios) {
				if (arquivo.isDirectory()) set_diretorios.add(arquivo.getName());
			}
		}
		return set_diretorios;
	}

	public static void cd (String novo_dir) {
		Set<String> set_diretorios = listaDiretorios(diretorio);
		if (novo_dir.equals("..") || novo_dir.equals("../")) {
			String array_diretorio[] = diretorio.split(contrabarra);
			array_diretorio[array_diretorio.length - 1] = "";
			diretorio = String.join(contrabarra, array_diretorio);
		} else if (novo_dir.equals("~")) {
			diretorio = "/home/" + System.getProperty("user.name");
		}else {
			if (set_diretorios.contains(novo_dir)) {
				diretorio = diretorio + "/" + novo_dir;
			} else {
				File dir = new File(novo_dir).getAbsoluteFile();
			        boolean dir_existe = false;
			   	if (dir.exists() || dir.mkdirs()) {
			        	dir_existe = (System.setProperty("user.dir", dir.getAbsolutePath()) != null);
			        }
			        System.out.println(dir_existe);        
			}
		}
		//adicionaMensagem(textpane, set_diretorios.toString() + " " + novo_dir + "\n");
	}


	
	public static void highlight() {

	    Runnable doHighlight = new Runnable() {
	        @Override
	        public void run() {
	        	if (!posicao_caret_setada) {
	        		posicao_inicial_caret = textpane.getCaretPosition();
	        		posicao_caret_setada = true;
	        	}
	        	String comando = textpane.getText();
	        	String array_comando[] = comando.split("");
	        	try {
	        		// O caret não pode ser movido para a linha acima e nem para o campo em que o path se localiza
	        		int numero_linhas = comando.split("\n").length;
	        		if (textpane.getCaretPosition() < posicao_inicial_caret * numero_linhas + numero_linhas) {
	        			int nova_posicao = posicao_inicial_caret * numero_linhas + numero_linhas - 1;
	        			if (comando.length() >= nova_posicao) textpane.setCaretPosition(nova_posicao);
	        		}
	        		// O usuário não pode apagar o caminho do diretório em que ele se encontra que é mostrado no terminal
	        		if (comando.length() < posicao_inicial_caret) {
	        			textpane.setText("");
	        			adicionaPath(textpane);
	        			adicionaCaret(textpane);
	        		}
	        		// Se o usuário pressionar enter, a nova linha irá exibir o caminho do diretório em que ele se encontra, assim como a linha acima
	        		if (comando.charAt(comando.length() - 1) == '\n') {
	        			array_comando = comando.split("\n");
		        		String ultima_linha = array_comando[array_comando.length - 1];
		        		String array_ultima_linha[] = ultima_linha.split(" ");
		        		System.out.println(array_ultima_linha[array_ultima_linha.length - 1]);
		        		ArrayList<String> comandos = new ArrayList<>();
		        		for (int i = 2; i < array_ultima_linha.length; i++) {
		        			if (!array_ultima_linha[i].equals("")) {
		        				if (i == array_ultima_linha.length - 1) comandos.add(array_ultima_linha[i].replace("\n", ""));
		        				else comandos.add(array_ultima_linha[i]);
		        			}
		        		}
		        		ArrayList<String> novos_comandos = new ArrayList<>();
		        		for (String cmd: comandos) {
		        			if (!cmd.equals("\n") && !(cmd.length() == 0)) novos_comandos.add(cmd);
		        		}
		        		comandos = novos_comandos;
		        		System.out.println(comandos.toString());
		        		if (comandos.size() > 0) { // Caso algum comando tenha sido executado
			        		// Interpretação e execução do comando de criar arquivos - touch
		        			if (comandos.get(0).equals("ls")) {
		        				if (comandos.size() == 1) {
		        					String string_listagem = "";
		        					Set<String> set_arquivos = listaArquivos(diretorio);
		        					for (String nome_arquivo: set_arquivos) {
		        						if (!(nome_arquivo.charAt(0) == '.')) string_listagem = string_listagem + nome_arquivo + "\n";
		        					}
		        					System.out.println(string_listagem);
		        					textpane.setText(textpane.getText() + string_listagem);
		        				} else if (comandos.size() == 2 && comandos.get(1).equals("-a")) {
		        					String string_listagem = "";
		        					Set<String> set_arquivos = listaArquivos(diretorio);
		        					for (String nome_arquivo: set_arquivos) {
		        						string_listagem = string_listagem + nome_arquivo + "\n";
		        					}
		        					textpane.setText(textpane.getText() + string_listagem);
		        				}
		        			} else if (comandos.get(0).equals("cd")) {
							if (comandos.size() == 1) {
								adicionaMensagem(textpane, "Especifique o caminho\n");
							} else if (comandos.size() > 2) {
								adicionaMensagem(textpane, "O comando suporta apenas um parâmetro\n");
							}
							if (comandos.size() > 1) {
								cd(comandos.get(1));
							}
		        			} else if (comandos.get(0).equals("touch")) {
		        				if (comandos.size() == 1) {
		        					adicionaMensagem(textpane, "O nome do arquivo deve ser passado como parâmetro\n");
		        				} else if (comandos.size() > 2) {
		        					adicionaMensagem(textpane, "O comando touch recebe apenas um argumento\n");
		        				} else {
		        					String nome_novo_arquivo = comandos.get(1);
		        					File novo_arquivo = new File(diretorio + nome_novo_arquivo);
		        					novo_arquivo.createNewFile();
		        				}
		        			} else if (comandos.get(0).equals("cat")) {
		        				if (comandos.size() == 1) {
		        					adicionaMensagem(textpane, "O nome do arquivo deve ser passado como parâmetro\n");
		        				} else if (comandos.size() == 2) {
		        					boolean arquivo_encontrado = true;
		        					String nome_arquivo = comandos.get(1);
		        					BufferedReader br = null;
		        					try {
		        						br = new BufferedReader(new FileReader(new File(nome_arquivo).getAbsolutePath()));
		        					} catch (Exception e) {
		        						adicionaMensagem(textpane, "Arquivo não encontrado\n");
		        						arquivo_encontrado = false;
		        					}
		        					if (arquivo_encontrado) {
		        						String conteudo_arquivo = "";
			        					while (br.ready()) {
			        						conteudo_arquivo = conteudo_arquivo + br.readLine() + "\n";
			        					}
			        					textpane.setText(textpane.getText() + conteudo_arquivo);
		        					}
		        				}
		        			} else if (comandos.get(0).equals("clear")) { // Comando clear
		        				textpane.setText("");
		        			} else if (comandos.get(0).equals("exit")) {
		        				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		        			} else {
		        				adicionaMensagem(textpane, "Comando inválido\n");
		        			}
		        		}
	        			adicionaPath(textpane);
	        			adicionaCaret(textpane);
	        		}
	        	} catch (BadLocationException e) {
	        		e.printStackTrace();
	        	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	Thread.currentThread().interrupt();
	        }
	    };       
	    SwingUtilities.invokeLater(doHighlight);
	}
	
	
	/*class Keyboard {
	    private static final Map<Integer, Boolean> pressedKeys = new HashMap<>();

	    static {
	        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event -> {
	            synchronized (Keyboard.class) {
	                if (event.getID() == KeyEvent.KEY_PRESSED) pressedKeys.put(event.getKeyCode(), true);
	                else if (event.getID() == KeyEvent.KEY_RELEASED) pressedKeys.put(event.getKeyCode(), false);
	                return false;
	            }
	        });
	    }

	    public static boolean isKeyPressed(int keyCode) { // Any key code from the KeyEvent class
	        return pressedKeys.getOrDefault(keyCode, false);
	    }
	}*/
}

