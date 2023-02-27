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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
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

	public static ArrayList<String> criaListaDiretorios (String caminho) throws BadLocationException {
		String array_caminho[] = caminho.split("/");
		ArrayList<String> lista_diretorios = new ArrayList<>();
		if (caminho.equals("..") || caminho.equals("../")) {
			lista_diretorios.add("..");
			return lista_diretorios;
		} else if (caminho.equals("~") || caminho.equals("~/")) {
			lista_diretorios.add("/");
			lista_diretorios.add("home");
			lista_diretorios.add(System.getProperty("user.name"));
			return lista_diretorios;
		}
		if (array_caminho.length == 1) {
			caminho = diretorio;
			array_caminho = caminho.split("/");
		}
		if (caminho.charAt(0) == '/') lista_diretorios.add("/");
		for (String dir : array_caminho) lista_diretorios.add(dir);
		return lista_diretorios;
	}

	public static ArrayList<Boolean> existemArquivoDiretorio (String caminho_arquivo, String caminho_diretorio) throws BadLocationException {
		ArrayList<Boolean> lista_existem = new ArrayList<>();
		String array_caminho_arquivo[] = caminho_arquivo.split("/");
		String nome_arquivo = array_caminho_arquivo[array_caminho_arquivo.length - 1];
		ArrayList<String> lista_arquivo = criaListaDiretorios(caminho_arquivo);
		ArrayList<String> lista_diretorio = criaListaDiretorios(caminho_diretorio);
		String diretorio_atual = diretorio;
		cd(lista_arquivo, diretorio, true);
		boolean arquivo_existe = cd(lista_arquivo, diretorio, true);
		diretorio = diretorio_atual;
		boolean diretorio_existe = cd(lista_diretorio, diretorio, false);
		lista_existem.add(arquivo_existe);
		lista_existem.add(diretorio_existe);
		diretorio = diretorio_atual;
		return lista_existem;
	}

	public static String nomeArquivoDiretorio (String caminho_diretorio) {
		String array_caminho_diretorio[] = caminho_diretorio.split("/");
		String nome_arquivo = array_caminho_diretorio[array_caminho_diretorio.length - 1];
		return nome_arquivo;
	}

	public static String formataPastaUsuario (String caminho) {
		return String.join("/home/" + System.getProperty("user.name") + "/", caminho.split("~")); 
	}

	public static boolean cd (ArrayList<String> lista_diretorios, String diretorio_atual, boolean verifica_arquivo) throws BadLocationException {
		if (lista_diretorios.size() == 0) return true;
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
			return cd(lista_diretorios, diretorio_atual, verifica_arquivo);
		} else if (novo_dir.equals("..") || novo_dir.equals("../")) {
			String array_diretorio[] = diretorio.split("/");
			array_diretorio[array_diretorio.length - 1] = "";
			diretorio = "";
			for (int i = 0; i < array_diretorio.length; ++i) {
				if (i != 0 && i != array_diretorio.length - 1) diretorio = diretorio + "/";
				if (i != array_diretorio.length - 1) diretorio = diretorio + array_diretorio[i];
			}
			lista_diretorios.remove(0);
			diretorio_atual = diretorio;
			return cd(lista_diretorios, diretorio_atual, verifica_arquivo);
		} else if (novo_dir.equals("/")) {
			diretorio = "/";
			lista_diretorios.remove(0);
			return cd(lista_diretorios, diretorio_atual, verifica_arquivo);
		} else if (novo_dir.equals("~") || novo_dir.equals("~/")) {
			diretorio = "/home/" + System.getProperty("user.name");
			lista_diretorios.remove(0);
			return cd(lista_diretorios, diretorio_atual, verifica_arquivo);
		} else if (novo_dir.charAt(0) == '~' && novo_dir.length() > 1) {
			if (novo_dir.charAt(1) != '/') {
				adicionaMensagem(textpane, "Diretório inválido\n");
				return false;
			}
		} else {
			if (set_diretorios.contains(novo_dir)) {
				if (diretorio != "/") diretorio = diretorio + "/" + novo_dir;
				else diretorio = diretorio + novo_dir;
				lista_diretorios.remove(0);
				return cd(lista_diretorios, diretorio_atual, verifica_arquivo);
			} else {
				adicionaMensagem(textpane, "Diretório inválido\n");
				diretorio = diretorio_atual;
				return false;
			}
		}
		return false;
	}

	public static String apenasDiretorio (String caminho) {
		String apenas_diretorio = "";
		String array_caminho[] = caminho.split("/");
		for (int i = 0; i < array_caminho.length - 1; i++) {
			apenas_diretorio = apenas_diretorio + array_caminho[i] + "/";
		}
		return apenas_diretorio;
	}

	public static void cp_mv (String caminho_arquivo, String caminho_destino, String nome_arquivo, boolean copiar) throws IOException {
		String array_caminho_arquivo[] = caminho_arquivo.split("/");
		String array_caminho_destino[] = caminho_destino.split("/");
		if (array_caminho_arquivo.length == 1) caminho_arquivo = diretorio + "/" + caminho_arquivo;
		if (array_caminho_destino.length == 1) caminho_destino = diretorio + "/" + caminho_destino;
		if (caminho_arquivo.charAt(0) == '~') caminho_arquivo = formataPastaUsuario(caminho_arquivo);
		if (caminho_destino.charAt(0) == '~') caminho_destino = formataPastaUsuario(caminho_destino);
		File arquivo_origem = new File(caminho_arquivo);
		File arquivo_destino = new File(caminho_destino);
		if (arquivo_destino.isDirectory()) {
			caminho_destino = caminho_destino + "/" + nome_arquivo;
			arquivo_destino = new File(caminho_destino);
		}
		if (copiar) Files.copy(arquivo_origem.toPath(), arquivo_destino.toPath());
		else Files.move(arquivo_origem.toPath(), arquivo_destino.toPath());
	}

	public static String cat (String caminho_arquivo) throws BadLocationException, IOException, UnsupportedEncodingException {
		if (!caminho_arquivo.contains("/")) caminho_arquivo = diretorio + "/" + caminho_arquivo;
		if (caminho_arquivo.charAt(0) == '~') caminho_arquivo = formataPastaUsuario(caminho_arquivo);
		System.out.println("Caminho - " + caminho_arquivo);
		//if (caminho_arquivo.equals(diretorio)) caminho_arquivo = caminho_arqui
		boolean arquivo_encontrado = true;
		BufferedReader br = null;
		String conteudo_arquivo = "";
		try {
			br = new BufferedReader(new FileReader(new File(caminho_arquivo)));
			if (arquivo_encontrado) {
				while (br.ready()) {
					conteudo_arquivo = conteudo_arquivo + br.readLine() + "\n";
				}
			}
			br.close();
		} catch (Exception e) {
			adicionaMensagem(textpane, "Arquivo não encontrado\n");
		        arquivo_encontrado = false;
		}
		return conteudo_arquivo;
	}

	public static String aplicaCat (String caminho_arquivo) throws BadLocationException, IOException {
		ArrayList<String> lista_arquivo = criaListaDiretorios(caminho_arquivo);
		cd(lista_arquivo, diretorio, true);
		String conteudo_arquivo = cat(caminho_arquivo);
		return conteudo_arquivo;
	}

	public static String ls (boolean _a) {
		String string_listagem = "";
		if (!_a) {
			Set<String> set_arquivos = listaArquivos(diretorio);
			for (String nome_arquivo: set_arquivos) {
				if (!(nome_arquivo.charAt(0) == '.')) string_listagem = string_listagem + nome_arquivo + "\n";
			}
		} else {
		       	Set<String> set_arquivos = listaArquivos(diretorio);
		       	for (String nome_arquivo: set_arquivos) {
		        	string_listagem = string_listagem + nome_arquivo + "\n";
		        }
		}
		return string_listagem;
	}

	public static String pwd () {
		return diretorio;
	}
	
	public static void saidaLs (boolean _a, String caminho_novo_arquivo) throws BadLocationException, IOException {
		String listagem = ls(_a);
		String array_caminho_novo_arquivo[] = caminho_novo_arquivo.split("/");
		String nome_arquivo = nomeArquivoDiretorio(caminho_novo_arquivo);
		String caminho_diretorio = "";
		if (array_caminho_novo_arquivo.length == 1) {
			nome_arquivo = caminho_novo_arquivo;
			caminho_diretorio = diretorio;
		} else {
			for (int i = 0; i < array_caminho_novo_arquivo.length - 1; i++) {
				caminho_diretorio = caminho_diretorio + array_caminho_novo_arquivo[i] + "/";
			}
		}
		ArrayList<Boolean> lista_existem = existemArquivoDiretorio("", caminho_novo_arquivo);
		boolean diretorio_existe = lista_existem.get(1);
		if (diretorio_existe) {
			File novo_arquivo = new File(caminho_diretorio + "/" + nome_arquivo);
			novo_arquivo.createNewFile();
			FileWriter fw = new FileWriter(novo_arquivo);
			fw.write(listagem);
			fw.close();
		} else {
			adicionaMensagem(textpane, "O diretório informado não existe\n");
		}
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
						if (comandos.contains("&")) {
							if (!comandos.get(comandos.size() - 1).equals("&")) {
								adicionaMensagem(textpane, "Para executar arquivos em background, o caractere & deve ser o último do comando\n");	
							} else {
								String comando_linha = "";
								for (int i = 0; i < comandos.size() - 1; i++) {
									comando_linha = comando_linha + comandos.get(i);
									if (i != comandos.size() - 2) comando_linha = comando_linha + " ";
								}
								try {
									Runtime r = Runtime.getRuntime();
									Process p = r.exec(comando_linha);
									BufferedReader br_linha = new BufferedReader(new InputStreamReader(p.getInputStream()));
									br_linha.lines().forEach(System.out::println);
									BufferedReader br_erro = new BufferedReader(new InputStreamReader(p.getErrorStream()));
									br_erro.lines().forEach(System.out::println);
								} catch (Exception e) {
									adicionaMensagem(textpane, "Erro ao tentar executar o comando em segundo plano\n");
								}
								
							}
						} else if (comandos.contains(">")) { // Salva a saída de um comando em um arquivo
							if (comandos.get(0).equals("cat") && comandos.size() == 4 && comandos.get(2).equals(">")) { // Se o comando a ter a saída salva em um arquivo for o cat
								String caminho_arquivo_cat = comandos.get(1);
								String caminho_novo_arquivo = comandos.get(3);
								String nome_arquivo = nomeArquivoDiretorio(caminho_novo_arquivo);
								String array_caminho_novo_arquivo[] = caminho_novo_arquivo.split("/");
								String caminho_diretorio = "";
								if (array_caminho_novo_arquivo.length == 1) {
									nome_arquivo = comandos.get(3);
									caminho_diretorio = diretorio;
								} else {
									for (int i = 0; i < array_caminho_novo_arquivo.length - 1; i++) {
										caminho_diretorio = caminho_diretorio + array_caminho_novo_arquivo[i] + "/";
									}
								}
								ArrayList<Boolean> lista_existem = new ArrayList<>();
								lista_existem = existemArquivoDiretorio(caminho_arquivo_cat, caminho_diretorio);
								boolean arquivo_existe = lista_existem.get(0);
								boolean diretorio_existe = lista_existem.get(1);
								if (!arquivo_existe && !diretorio_existe) {
									adicionaMensagem(textpane, "Arquivo de origem e diretório de destino inválidos\n");
								} else if (!arquivo_existe) {
									adicionaMensagem(textpane, "O arquivo informado não existe\n");
								} else if (!diretorio_existe) {				
									adicionaMensagem(textpane, "O diretório informado não existe\n");
								} else {
									cp_mv(caminho_arquivo_cat, caminho_diretorio, nome_arquivo, true);
								}
							} else if (comandos.get(0).equals("ls") && comandos.size() == 3 && comandos.get(1).equals(">")) { // Se o comando a ter a saída salva em um arquivo for o ls, sem a flag -a
								String caminho_novo_arquivo = comandos.get(2);
								saidaLs(true, caminho_novo_arquivo);

							} else if (comandos.get(0).equals("ls") && comandos.size() == 4 && comandos.get(1).equals("-a") && comandos.get(2).equals(">")) { // Se o comando a ter a saída salva em um arquivo for o ls, com a flag -a
								String caminho_novo_arquivo = comandos.get(3);
								saidaLs(false, caminho_novo_arquivo);
							} else {
								adicionaMensagem(textpane, "Para o redirecionador de saída (>), o operador da esquerda deve ser o comando cat ou o ls e o da direita o nome ou caminho e nome de um novo arquivo\n");
							}
						} else if (comandos.get(0).equals("ls")) { // Comando de listar arquivos - ls
		        				if (comandos.size() == 1) {
								String string_listagem = ls(false);
								textpane.setText(textpane.getText() + string_listagem);
		        				} else if (comandos.size() == 2 && comandos.get(1).equals("-a")) {
								String string_listagem = ls(true);
		        					textpane.setText(textpane.getText() + string_listagem);
		        				}
		        			} else if (comandos.get(0).equals("cd")) {
							if (comandos.size() == 1) {
								adicionaMensagem(textpane, "Especifique o caminho\n");
							} else if (comandos.size() > 2) {
								adicionaMensagem(textpane, "O comando suporta apenas um parâmetro\n");
							}
							if (comandos.size() > 1) {
								String caminho = comandos.get(1);
								String array_caminho[] = caminho.split("/");
								if (array_caminho.length == 1 && listaDiretorios(diretorio).contains(caminho)) caminho = diretorio + "/" + caminho;
								ArrayList<String> lista_diretorio = criaListaDiretorios(caminho);
								cd(lista_diretorio, diretorio, false);
							}
		        			} else if (comandos.get(0).equals("cp") || comandos.get(0).equals("mv")) { // Comando para copiar arquivos - cp
							if (comandos.size() <= 2) {
								adicionaMensagem(textpane, "Especifique ambos o arquivo e o diretório de origem\n");
							} else {
								String caminho_arquivo = comandos.get(1);
								String caminho_diretorio = comandos.get(2);
								String array_caminho_arquivo[] = caminho_arquivo.split("/");
								String array_caminho_diretorio[] = caminho_diretorio.split("/");
								String nome_arquivo = nomeArquivoDiretorio(caminho_arquivo);
								ArrayList<Boolean> lista_existem = new ArrayList<>();
								lista_existem = existemArquivoDiretorio(caminho_arquivo, caminho_diretorio);
								boolean arquivo_existe = lista_existem.get(0);
								boolean diretorio_existe = lista_existem.get(1);
								if (array_caminho_diretorio.length == 1 && listaDiretorios(diretorio).contains(comandos.get(2))) {
									System.out.println("Lista diretórios: " + listaDiretorios(diretorio).toString());
									diretorio_existe = true;
									caminho_diretorio = diretorio + "/" + comandos.get(2);
								}
								if (!arquivo_existe) {
									adicionaMensagem(textpane, "O arquivo informado não existe\n");
								} else {
									if (arquivo_existe && diretorio_existe) {
										if (comandos.get(0).equals("cp")) {
											cp_mv(caminho_arquivo, caminho_diretorio, nome_arquivo, true);
										} else {
											System.out.println("Caminho diretório mv: " + caminho_diretorio);
											cp_mv(caminho_arquivo, caminho_diretorio, nome_arquivo, false);
										}
									} else if (arquivo_existe && array_caminho_diretorio.length == 1) {
										if (comandos.get(0).equals("mv")) {
											String novo_nome = comandos.get(2);
											File arquivo  = new File(caminho_arquivo);
											caminho_diretorio = apenasDiretorio(caminho_diretorio);
											System.out.println("Novo nome: " + novo_nome);
											System.out.println("Caminho diretório: " + caminho_diretorio);
											cp_mv(caminho_arquivo, caminho_diretorio, novo_nome, false);
											//Files.move(arquivo.toPath(), arquivo.toPath().resolveSibling(novo_nome));
										}
									}
								}
							}
						} else if (comandos.get(0).equals("touch")) {
		        				if (comandos.size() == 1) {
		        					adicionaMensagem(textpane, "O nome do arquivo deve ser passado como parâmetro\n");
		        				} else if (comandos.size() > 2) {
		        					adicionaMensagem(textpane, "O comando touch recebe apenas um argumento\n");
		        				} else {
		        					String nome_novo_arquivo = comandos.get(1);
		        					File novo_arquivo = new File(diretorio + "/" + nome_novo_arquivo);
		        					novo_arquivo.createNewFile();
		        				}
		        			} else if (comandos.get(0).equals("mkdir")) {
							
						} else if (comandos.get(0).equals("rm")) {
							
						} else if (comandos.get(0).equals("cat")) {
		        				if (comandos.size() == 1) {
		        					adicionaMensagem(textpane, "O nome do arquivo deve ser passado como parâmetro\n");
		        				} else if (comandos.size() == 2) {
								String diretorio_atual = diretorio;
								String caminho_arquivo = comandos.get(1);
								String array_caminho_arquivo[] = caminho_arquivo.split("/");
								if (array_caminho_arquivo.length == 1) caminho_arquivo = diretorio + "/" + caminho_arquivo;
								String conteudo_arquivo = aplicaCat(caminho_arquivo);
								adicionaMensagem(textpane, conteudo_arquivo + "\n");
								diretorio = diretorio_atual;
		        				} else {
								adicionaMensagem(textpane, "O comando cat recebe apenas um argumento\n");
							}
		        			} else if (comandos.get(0).equals("pwd")) {
							adicionaMensagem(textpane, pwd() + "\n");
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
