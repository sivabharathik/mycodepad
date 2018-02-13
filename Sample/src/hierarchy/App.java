package hierarchy;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.event.ActionEvent;
import java.awt.*;

public class App extends JFrame  {
	
    public static JEditorPane edit = new JEditorPane();
    static String file;
    public static  String testXML="";
    
    public String getFile() {
		return file;
	}

	public static  void setFile(String file) {
		App.file = file;
	}

	public App() throws IOException {
        byte[] data = Files.readAllBytes(new File(file).toPath());
        testXML=new String(data);
        edit.setEditorKit(new XMLEditorKit());
        edit.setText(testXML);
        edit.setEditable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        getContentPane().add(new JScrollPane(edit));
        setTitle("XML Structure View");
        setSize(620, 450);
       setLocationRelativeTo(null);
       setVisible(true);
    }
}
