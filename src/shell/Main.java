package shell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat.Style;

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
	
	public static void main (String [] args) throws BadLocationException {
		final int largura_janela = 800;
		final int altura_janela = 500;	
		frame = new JFrame("Shell");
		frame.setBounds(0, 0, largura_janela, altura_janela);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		panel = new JPanel(new BorderLayout());
		panel.setBounds(0, 0, largura_janela, altura_janela);
		frame.add(panel);
		
		JTextPane textpane = new JTextPane();
		textpane.setBackground(Color.BLACK);
		//textpane.setForeground(Color.GREEN);
		
		Font fonte = new Font("Consolas", Font.PLAIN, 14);
		textpane.setFont(fonte);
		
		StyledDocument doc = textpane.getStyledDocument();
	    javax.swing.text.Style texto_verde = textpane.addStyle("", null);
	    StyleConstants.setForeground(texto_verde, Color.GREEN);
	    doc.insertString(doc.getLength(), System.getProperty("user.dir"), texto_verde);
	    
	    doc = textpane.getStyledDocument();
	    javax.swing.text.Style texto_amarelo = textpane.addStyle("", null);
	    StyleConstants.setForeground(texto_amarelo, Color.YELLOW);
	    doc.insertString(doc.getLength(), " ~$", texto_amarelo);
	    
		textpane.setBounds(0, 0, largura_janela, altura_janela);
		StyledDocument style = textpane.getStyledDocument();
		SimpleAttributeSet align = new SimpleAttributeSet();
		StyleConstants.setAlignment(align, StyleConstants.ALIGN_LEFT);
		style.setParagraphAttributes(0, style.getLength(), align, false);
		
		JScrollPane scrollpane = new JScrollPane(textpane);
		scrollpane.setBounds(0, 0, largura_janela, altura_janela);
		
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
		
		textpane.setCaretColor(Color.WHITE);
		
		doc = textpane.getStyledDocument();
	    javax.swing.text.Style texto_branco = textpane.addStyle("", null);
	    StyleConstants.setForeground(texto_branco, Color.WHITE);
	    doc.insertString(doc.getLength(), " ", texto_branco);
		
		textpane.putClientProperty("caretWidth", 5);
		


		
		//panel.setPreferredSize(new Dimension(largura_janela, altura_janela));
		
		panel.add(scrollpane, BorderLayout.CENTER);
		
		
		CaretListener caret_listener = new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				System.out.println("Caret atualizado");
				
			}
	    };
	    
	    textpane.addCaretListener(caret_listener);
	    
	}
	
	public static void fireInputChanged (String texto) {
		System.out.println("Mudou o texto");
	}

}

