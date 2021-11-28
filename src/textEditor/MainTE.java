package textEditor;

//@author Torin

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
public class MainTE {
	private static boolean saved = true;
	
	public static void main(String[] args) {
		Window w = new Window();
	}
	
	private static class Window extends JFrame {
		FindWindow fw;
		MyHighlightPainter myHighlightPainter = this.new MyHighlightPainter(Color.YELLOW);
		JRadioButton rbss = new JRadioButton("Sans-Serif", true), rbs = new JRadioButton("Serif"), rbm = new JRadioButton("Monospace");
		ButtonGroup fontRBg;
		JTextArea ta = new JTextArea(20, 40);
		JScrollPane sp = new JScrollPane(ta);
		private static int fSize = 14;
		private static JTextField fstf = new JTextField(2);
		String fStr;
		
		private Window() {
			try {
		        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    }catch(Exception ex) {
		        ex.printStackTrace();
		    }
			setIconImage(new ImageIcon("images\\spaceBarIcon.png").getImage());
			setSize(600, 470);
			setTitle("WhiteSpace Text Editor");
			setDefaultCloseOperation(0);
			addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent e) {
			    	if(!saved) {
						if(JOptionPane.showConfirmDialog(null, "Any Unsaved Data Will Be Lost, Are You Sure You Want To Exit", "Exit", 0) == JOptionPane.OK_OPTION) {
							System.exit(0);
						}
				    } else {
						System.exit(0);
					}
			    }
			});
			
			setJMenuBar(buildMenuBar());
			
			MatteBorder mbrdr = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.GRAY);
			Border lbrdr = BorderFactory.createLineBorder(Color.WHITE, 1);
			CompoundBorder cbrdr = BorderFactory.createCompoundBorder(mbrdr, lbrdr);
			ta.setBorder(cbrdr);
			ta.setFont(new Font("Sans-Serif", Font.PLAIN, fSize));
			ta.getDocument().addDocumentListener(new sbLstnr());
			add(sp);
			
			setLocationRelativeTo(null);
			setVisible(true);
		}
		
		private JMenuBar buildMenuBar() {
			JPanel fsp = new JPanel();
			fontRBg = new ButtonGroup();
			JRadioButton[] fontRBarr = {rbss, rbs, rbm};
			JMenuBar mb = new JMenuBar();
			JMenu filem = new JMenu("File"), stylem = new JMenu("Style"), textm = new JMenu("Text"), fontm = new JMenu("Font"), sizem = new JMenu("Size");
			JMenuItem savei = new JMenuItem("Save"), exiti = new JMenuItem("Exit"), openi = new JMenuItem("Open"), findi = new JMenuItem("Find");
			JLabel fsl = new JLabel("Font Size:");
			JButton fsb = new JButton("Set");
			
			JMenu[] mArr = {filem, stylem};
			
			mb.setBackground(Color.WHITE);
			
			findi.addActionListener(new FindWindowLstnr());
			exiti.addActionListener(new ExitLstnr());
			fsb.addActionListener(new fsLstnr());
			openi.addActionListener(new OpenLstnr());
			
			for(JRadioButton rb : fontRBarr) {
				rb.setBackground(new Color(248, 248, 248));
				fontRBg.add(rb);
				fontm.add(rb);
				rb.addActionListener(new FontLstnr());
			}
			
			fsp.add(fsl);
			fsp.add(fstf);
			fsp.add(fsb);
			fstf.setText("14");
			textm.add(fontm);
			textm.add(sizem);
			stylem.add(textm);
			filem.add(openi);
			filem.add(savei);
			filem.addSeparator();
			filem.add(findi);
			filem.addSeparator();
			filem.add(exiti);
			savei.addActionListener(new SaveLstnr());
			sizem.add(fsp);
			
			for(JMenu m : mArr) {
				m.setBorder(BorderFactory.createLineBorder(Color.GRAY));
				mb.add(m);
			}
			
			mb.setBorderPainted(false);
			
			return mb;
		}
		
		public String readFromFile() {
			JFileChooser fc;
			File f;
			Scanner fScan = null;
			String fStr = "";
			do {
				fc = new JFileChooser();
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", ".txt", "txt");
				fc.setFileFilter(filter);
				fc.showDialog(null, "Open");
				f = fc.getSelectedFile();
				if(!f.getAbsolutePath().endsWith(".txt")){
				    f = new File(fc.getSelectedFile()+".txt");
				}
				if(!f.exists()) {
					JOptionPane.showMessageDialog(null, "Please choose a \".txt\" file", "Error", 0);
				}
			} while(!f.getAbsolutePath().endsWith(".txt"));

			try {
				fScan = new Scanner(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!f.equals(null)) {
				try {
					while(fScan.hasNextLine()) {
						fStr += fScan.nextLine();
					}
				} catch(NullPointerException e) {}
			}
			try {
				fScan.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return fStr;
		}
		
		public void saveToFile(String s) {
			boolean checkOW = true;
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", ".txt");
			fc.setFileFilter(filter);
			fc.showDialog(null, "Save");
			File f = fc.getSelectedFile();
			FileWriter fw = null;
			PrintWriter pw = null;
			if(!f.getAbsolutePath().endsWith(".txt")){
			    f = new File(fc.getSelectedFile()+".txt");
			}
			if(f.exists()) {
				if(JOptionPane.showConfirmDialog(null, f.getName()+" already exists. Would you like to overwrite it?", "Overwrite", 0) == JOptionPane.YES_OPTION) {
					checkOW = true;
				} else {
					checkOW = false;
				}
			}
			
			if(checkOW) {
				try {
					fw = new FileWriter(f);
					pw = new PrintWriter(fw);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(!f.equals(null)) {
					try {
						pw.print(s);
						saved = true;
					} catch(NullPointerException e) {}
				}
				try {
					pw.close();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void highlight(JTextComponent textComp, String pattern) throws Exception {
			removeHighlights(textComp);
			
			Highlighter hilite = textComp.getHighlighter();
			Document doc = textComp.getDocument();
			String text = doc.getText(0, doc.getLength());
			int pos = 0;
			
			while ((pos = text.indexOf(pattern, pos)) >= 0) {
				hilite.addHighlight(pos, pos + pattern.length(), this.myHighlightPainter);
				pos += pattern.length();
			}
		}
			
		public static void removeHighlights(JTextComponent textComp) {
			Highlighter hilite = textComp.getHighlighter();
			Highlighter.Highlight[] hilites = hilite.getHighlights();
			
			for (int i = 0; i < hilites.length; i++) {
				if (hilites[i].getPainter() instanceof MyHighlightPainter) {
					hilite.removeHighlight(hilites[i]);
				}
			}
			
		}

		class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
		
			public MyHighlightPainter(Color color) {
				super(color);
			}
			
		
		}

		
		private class FindWindow extends JFrame {
			JPanel ptf = new JPanel();
			JTextField ftf = new JTextField(18);
			
			public FindWindow() {
				setSize(350, 200);
				setTitle("Find");
				setDefaultCloseOperation(2);
				setLayout(new GridLayout(2, 1));
				
				JButton sb = new JButton("Find");
				
				sb.addActionListener(new FindLstnr());
				
				ptf.add(ftf);
				ptf.add(sb);
				
				add(ptf);
				
				setLocationRelativeTo(null);
				setVisible(true);
			}
			
			public class FindLstnr implements ActionListener {

				public void actionPerformed(ActionEvent e) {
					if(!ftf.getText().equals("")) {
						try {
							highlight(ta, ftf.getText());
						} catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(fw, "Whoops! There Was An Error!", "Find Error", 0);
						}
					}
					fw.setVisible(false);
				}
				
			}			
		}
		
		//LISTENERS
		private class SaveLstnr implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				saveToFile(ta.getText());
			}
			
		}
		
		private class ExitLstnr implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				if(!saved) {
					if(JOptionPane.showConfirmDialog(null, "Any Unsaved Data Will Be Lost, Are You Sure You Want To Exit", "Exit", 0) == JOptionPane.OK_OPTION) {
						System.exit(0);
					}
				} else {
					System.exit(0);
				}
			}
			
		}
		
		private class sbLstnr implements DocumentListener {

			public void insertUpdate(DocumentEvent e) {
				saved = false;
			}

			public void removeUpdate(DocumentEvent e) {
				saved = false;
			}

			public void changedUpdate(DocumentEvent e) {
				saved = false;
			}
			
		}
		
		private class FontLstnr implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				if(e.getSource().equals(rbm)) {
					fStr = "Monospaced";
				} else if(e.getSource().equals(rbs)) {
					fStr = "Serif";
				} else {
					fStr = "Sans-Serif";
				}
						
				ta.setFont(new Font(fStr , Font.PLAIN, fSize));
			}
			
		}
		
		private class fsLstnr implements ActionListener {
			
			public void actionPerformed(ActionEvent e) {
				int input = Integer.parseInt(fstf.getText());
				if(input > 99) {
					input = 99;
				} else if(input < 1) {
					input = 1;
				}
				
				fstf.setText(Integer.toString(input));
				 
				fSize = input;
				ta.setFont(new Font(fStr , Font.PLAIN, fSize));
			}
			
		}
		
		private class OpenLstnr implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				String fStr = readFromFile();
				if(!ta.getText().equals("") || !ta.getText().equals(fStr)) {
					if(JOptionPane.showConfirmDialog(null, "This will overwrite current contents.\nAre you sure you want to continue", "Open File", 0) == JOptionPane.OK_OPTION) {
						ta.setText(fStr);
					}
				} else {
					ta.setText(readFromFile());
				}
			}
			
		}
		
		private class FindWindowLstnr implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				fw = new FindWindow();
			}
			
		}
		
	}
	
}
