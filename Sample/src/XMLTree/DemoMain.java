package XMLTree;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import application.Notepad;

public class DemoMain extends JFrame {
static String FileLocation;
	public String getFileLocation() {
	return FileLocation;
}


public static void setFileLocation(String fileLocation) {
	FileLocation = fileLocation;
}
	
	
	public DemoMain() {
		Document document = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbFactory.newDocumentBuilder();
			document = builder.parse(new File(getFileLocation()));
			document.normalize();
		
		
		XMLTreePanel panel = new XMLTreePanel();
		panel.setDocument(document);
		getContentPane().add(panel, "Center");
		JFileChooser chooser;
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("XML Tree");
		setLocationRelativeTo(null);
		setVisible(true);
		
		
		}
		catch (Exception e) {
			String error=e.toString();
			String err[]=error.split("\\.");
			JOptionPane.showMessageDialog(application.Notepad.f,err[err.length-1],"Error Log",  
					JOptionPane.INFORMATION_MESSAGE);  
			application.Notepad.statusBar.setText("XML not in well form !!");
		}
	}


	

}
