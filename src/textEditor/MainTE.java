package textEditor;

//@author Torin

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
public class MainTE {
	private static boolean saved = true;
	
	public static void main(String[] args) {
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }catch(Exception ex) {
	        ex.printStackTrace();
	    }
		
		@SuppressWarnings("unused")
		Window w = new Window();
	}

	
	private static class Window extends JFrame {
		String fileLocation = "";
		JRadioButton rbss = new JRadioButton("Sans-Serif", true), rbs = new JRadioButton("Serif"), rbm = new JRadioButton("Monospace");
		ButtonGroup fontRBg;
		JTextArea ta = new JTextArea(20, 40);
		JScrollPane sp = new JScrollPane(ta);
		private static int fSize = 14;
		private static JTextField fstf = new JTextField(2);
		String fStr = "Sans-Serif";
		JMenuItem savei = new JMenuItem("Save"), exiti = new JMenuItem("Exit"), openi = new JMenuItem("Open"), saveasi = new JMenuItem("Save As");
		JMenu sizem = new JMenu("Size");
		
		private Window() {
			Timer tmr = new Timer(250, new fsLstnr());
			tmr.start();
			
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
			ta.setFont(new Font(fStr, Font.PLAIN, fSize));
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
			JMenu filem = new JMenu("File"), stylem = new JMenu("Style"), textm = new JMenu("Text"), fontm = new JMenu("Font");
			JLabel fsl = new JLabel("Font Size:");
			
			JMenu[] mArr = {filem, stylem};
			
			mb.setBackground(Color.WHITE);
			
			exiti.addActionListener(new ExitLstnr());
			openi.addActionListener(new OpenLstnr());
			savei.addActionListener(new SaveLstnr());
			saveasi.addActionListener(new SaveLstnr());
			
			filem.setMnemonic('f');
			stylem.setMnemonic('s');
			
			textm.setMnemonic('t');
			fontm.setMnemonic('o');
			sizem.setMnemonic('i');
			
			
			fsl.setDisplayedMnemonic('z');
			fsl.setLabelFor(fstf);
			
			rbss.setMnemonic('n');
			rbs.setMnemonic('r');
			rbm.setMnemonic('m');
			
			openi.setMnemonic('o');
			savei.setMnemonic('v');
			saveasi.setMnemonic('a');
			exiti.setMnemonic('x');

			for(JRadioButton rb : fontRBarr) {
				rb.setBackground(new Color(248, 248, 248));
				fontRBg.add(rb);
				fontm.add(rb);
				rb.addActionListener(new FontLstnr());
			}
			
			fsp.add(fsl);
			fsp.add(fstf);
			fstf.setText("14");
			textm.add(fontm);
			textm.add(sizem);
			stylem.add(textm);
			filem.add(openi);
			filem.add(savei);
			filem.add(saveasi);
			filem.addSeparator();
			filem.add(exiti);
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
			fc = new JFileChooser();
			fc.showDialog(null, "Open");
			f = fc.getSelectedFile();
				
			try {
				fScan = new Scanner(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!f.equals(null)) {
				try {
					fileLocation = f.getAbsolutePath();
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
		
		public void saveAsToFile(String s) {
			boolean checkOW = true;
			JFileChooser fc = new JFileChooser();
			fc.showDialog(null, "Save");
			File f = fc.getSelectedFile();
			FileWriter fw = null;
			PrintWriter pw = null;
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
						fileLocation = f.getAbsolutePath();
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
		
		public void saveToFile(String s) {
			boolean checkOW = true;
			File f = new File(fileLocation);
			FileWriter fw = null;
			PrintWriter pw = null;
			if(!f.exists()) {
				f = null;
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
		
		//LISTENERS
		private class SaveLstnr implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				if(e.getSource().equals(savei)) {
					if(!fileLocation.equals("")) {
						saveToFile(ta.getText());
					} else {
						saveAsToFile(ta.getText());
					}
				} else {
					saveAsToFile(ta.getText());
				}
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
				if(!fstf.getText().equals("") && !sizem.isPopupMenuVisible()) {
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
			
		}
		
		private class OpenLstnr implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				String fStr = readFromFile();
				if(ta.getText().equals("") || ta.getText().equals(fStr)) {
					ta.setText(fStr);
				} else {
					if(JOptionPane.showConfirmDialog(null, "This will overwrite current contents of text editor.\nAre you sure you want to continue", "Open File", 0) == JOptionPane.OK_OPTION) {
						ta.setText(readFromFile());
					}
				}
				
			}

		}

	}

}
