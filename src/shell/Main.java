package shell;

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat.Style;
import java.util.ArrayList;
import java.util.Set;
import java.awt.event.KeyEvent;
import java.util.HashMap;
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
	    doc.insertString(doc.getLength(), System.getProperty("user.dir"), texto_verde);
	    
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


	
	public static void highlight() {

	    Runnable doHighlight = new Runnable() {
	        @Override
	        public void run() {
	        	String comando = textpane.getText();
	        	String array_comando[] = comando.split("");
	        	try {
	        		// O caret não pode ser movido para a linha acima e nem para o campo em que o path se localiza
	        		int numero_linhas = comando.split("\n").length;
	        		if (textpane.getCaretPosition() < 42 * numero_linhas + numero_linhas) {
	        			int nova_posicao = 42 * numero_linhas + numero_linhas - 1;
	        			if (comando.length() >= nova_posicao) textpane.setCaretPosition(nova_posicao);
	        		}
	        		// O usuário não pode apagar o caminho do diretório em que ele se encontra que é mostrado no terminal
	        		if (comando.length() < 42) {
	        			textpane.setText("");
	        			adicionaPath(textpane);
	        			adicionaCaret(textpane);
	        		}
	        		// Se o usuário pressionar enter, a nova linha irá exibir o caminho do diretório em que ele se encontra, assim como a linha acima
	        		if (array_comando[comando.length() - 1].equals("\n")) {
	        			array_comando = comando.split("\n");
		        		String ultima_linha = array_comando[array_comando.length - 1];
		        		String array_ultima_linha[] = ultima_linha.split(" ");
		        		ArrayList<String> comandos = new ArrayList<>();
		        		for (int i = 2; i < array_ultima_linha.length; i++) {
		        			if (!array_ultima_linha[i].equals("")) {
		        				if (i == array_ultima_linha.length - 1) comandos.add(removeEnter(array_ultima_linha[i]));
		        				else comandos.add(array_ultima_linha[i]);
		        			}
		        		}
		        		System.out.println(comandos.toString());
		        		if (comandos.size() > 0) { // Caso algum comando tenha sido executado
			        		// Interpretação e execução do comando de criar arquivos - touch
		        			if (comandos.get(0).equals("touch")) {
		        				if (comandos.size() == 1) {
		        					adicionaMensagem(textpane, "Para criar um arquivo com o comando touch, o nome do arquivo deve ser passado como parâmetro\n");
		        				} else if (comandos.size() > 2) {
		        					adicionaMensagem(textpane, "O comando touch recebe apenas um argumento\n");
		        				} else {
		        					String nome_arquivo = comandos.get(1);
		        					Runtime r = Runtime.getRuntime();
		        					Process p = r.exec("notepad arquivo.txt");
		        				}
		        			} else if (comandos.get(0).equals("clear")) { // Comando clear
		        				textpane.setText("");
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
	        	//System.out.println(textpane.getText());
	        	Thread.currentThread().interrupt();
	        }
	    };       
	    SwingUtilities.invokeLater(doHighlight);
	}
	
	
	public class Keyboard {
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
	}
}

