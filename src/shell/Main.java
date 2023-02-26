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
	
	public static void main (String [] args) throws BadLocationException {
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
		File[] arquivos = file.listFiles();
		Set<String> set_diretorios = new HashSet<String>();
		if (arquivos.length > 0) {
			for (File arquivo: arquivos) {
				if (arquivo.isDirectory()) set_diretorios.add(arquivo.getName());
			}
		}
		return set_diretorios;
	}

	public static Set<String> listaApenasArquivos (String dir) {
		File file = new File(dir);
		File[] arquivos = file.listFiles();
		Set<String> set_arquivos = new HashSet<String>();
		if (arquivos.length > 0) {
			for (File arquivo : arquivos) {
				if (!arquivo.isDirectory()) set_arquivos.add(arquivo.getName());
			}
		}
		return set_arquivos;
	}

	public static boolean cd (ArrayList<String> lista_diretorios, String diretorio_atual, boolean verifica_arquivo) throws BadLocationException {
		if (lista_diretorios.size() == 0) return false;
		if (verifica_arquivo) {
			if (lista_diretorios.size() == 1) {
				String nome_arquivo = lista_diretorios.get(0);
				File arquivo = new File(diretorio + "/" + nome_arquivo);
				if (arquivo.exists()) return true;
				else return false;	
			}
		}
		String novo_dir = lista_diretorios.get(0);
		Set<String> set_diretorios = listaDiretorios(diretorio);
		if (novo_dir.equals("")) {
			lista_diretorios.remove(0);
			cd(lista_diretorios, diretorio_atual, verifica_arquivo);
		} else if (novo_dir.equals("..") || novo_dir.equals("../")) {
			String array_diretorio[] = diretorio.split("/");
			array_diretorio[array_diretorio.length - 1] = "";
			diretorio = String.join("/", array_diretorio);
			lista_diretorios.remove(0);
			cd(lista_diretorios, diretorio_atual, verifica_arquivo);
		} else if (novo_dir.equals("/")) {
			diretorio = "/";
			lista_diretorios.remove(0);
			cd(lista_diretorios, diretorio_atual, verifica_arquivo);
		}else if (novo_dir.equals("~")) {
			diretorio = "/home/" + System.getProperty("user.name");
			lista_diretorios.remove(0);
			cd(lista_diretorios, diretorio_atual, verifica_arquivo);
		} else if (novo_dir.charAt(0) == '~' && novo_dir.length() > 1) {
			if (novo_dir.charAt(1) != '/') {
				adicionaMensagem(textpane, "Caminho inválido\n");
				return false;
			}
		} else {
			if (set_diretorios.contains(novo_dir)) {
				if (diretorio != "/") diretorio = diretorio + "/" + novo_dir;
				else diretorio = diretorio + novo_dir;
				lista_diretorios.remove(0);
				cd(lista_diretorios, diretorio_atual, verifica_arquivo);
			} else {
				adicionaMensagem(textpane, "Caminho inválido\n");
				diretorio = diretorio_atual;
				return false;
			}
		}
		return false;
	}

	public static String cat (String nome_arquivo) throws BadLocationException, IOException {
		boolean arquivo_encontrado = true;
		BufferedReader br = null;
		String conteudo_arquivo = "";
		try {
			br = new BufferedReader(new FileReader(new File(diretorio + "/" + nome_arquivo).getAbsolutePath()));
		} catch (Exception e) {
			adicionaMensagem(textpane, "Arquivo não encontrado\n");
		        arquivo_encontrado = false;
		}
		if (arquivo_encontrado) {
			while (br.ready()) {
				conteudo_arquivo = conteudo_arquivo + br.readLine() + "\n";
			}
			textpane.setText(textpane.getText() + conteudo_arquivo);
		}
		return conteudo_arquivo;
	}


	
	public static void highlight() {

	    Runnable doHighlight = new Runnable() {
	        @Override
	        public void run() {
	        	String comando = textpane.getText();
	        	String array_comando[] = comando.split("");
	        	try {
	        		// O caret não pode ser movido para a linha acima e nem para o campo em que o path se localiza
	        		int numero_linhas = comando.split("\n").length;
				int posicao_caret = 0;
				String array_linhas[] = comando.split("\n");
				for (int i = 0; i < array_linhas.length - 1; i++) {
					posicao_caret = posicao_caret + array_linhas[i].length();
				}
				posicao_caret = posicao_caret + diretorio.length() + 3 + numero_linhas;
	        		if (textpane.getCaretPosition() < posicao_caret) textpane.setCaretPosition(posicao_caret);
	        		// O usuário não pode apagar o caminho do diretório em que ele se encontra que é mostrado no terminal
	        		if (comando.length() < posicao_caret) {
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
								String array_caminho[] = comandos.get(1).split("/");
								ArrayList<String> lista_diretorios = new ArrayList<>();
								if (comandos.get(1).charAt(0) == '/') lista_diretorios.add("/");
								for (String dir : array_caminho) lista_diretorios.add(dir);
								String diretorio_atual = diretorio;	
								cd(lista_diretorios, diretorio, false);
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
								cat(comandos.get(1));
		        				} else {
								adicionaMensagem(textpane, "O comando cat recebe apenas um argumento\n");
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

