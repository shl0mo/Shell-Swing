package shell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Main {
	static JFrame frame;
	static JPanel panel; 
	static JTextPane textpane;
	
	public static void main (String [] args) {
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
		textpane.setForeground(Color.WHITE);
		
		//Font fonte = new Font("Terminal", Font.ITALIC, 14);
		//textpane.setFont(fonte);
		
		
		textpane.setText(System.getProperty("user.dir") + " > ");
		textpane.setBounds(0, 0, largura_janela, altura_janela);
		StyledDocument style = textpane.getStyledDocument();
		SimpleAttributeSet align= new SimpleAttributeSet();
		StyleConstants.setAlignment(align, StyleConstants.ALIGN_LEFT);
		style.setParagraphAttributes(0, style.getLength(), align, false);
		
		JScrollPane scrollpane = new JScrollPane(textpane);
		scrollpane.setBounds(0, 0, largura_janela, altura_janela);
		
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
		
		textpane.setCaretColor(Color.WHITE);
		textpane.putClientProperty("caretWidth", 5);
		
		MutableAttributeSet jTextPaneSet = new SimpleAttributeSet(textpane.getParagraphAttributes());

		
		//panel.setPreferredSize(new Dimension(largura_janela, altura_janela));
		
		panel.add(scrollpane, BorderLayout.CENTER);
		
		Action action = new AbstractAction(){
			@Override
			    public void actionPerformed(ActionEvent e) {

			    String userInput = textpane.getText();
			    boolean enterPressed = true;
			    textpane.setText("");
			    //call next method;

			    }
			};
		
	}
}
