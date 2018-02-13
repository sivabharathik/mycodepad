package dockWindow;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import XMLTree.XMLTreePanel;
import hierarchy.XMLEditorKit;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.awt.*;

public class Test {
    Desktop desktop;
    public JTextArea textArea;
    @SuppressWarnings("deprecation")
	public Test() {
        JFrame frame=new JFrame("XML Editor");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        desktop=new Desktop();
        desktop.setBackground(Color.white);
        init(desktop);

        frame.getContentPane().add(desktop,BorderLayout.CENTER);
        JButton b=new JButton("New XML");
        frame.getContentPane().add(b,BorderLayout.SOUTH);
        ActionListener lst=new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                desktop.removeAll();
                init(desktop);
                desktop.refresh();
            }
        };
        b.addActionListener(lst);

        frame.setBounds(50,50,600,400);
        frame.show();
    }

    public void init(Desktop panel) {
        DockablePanel dp=new DockablePanel();
        try {
        dp=new DockablePanel();
        dp.title="XML Source";
        dp.setLayout(new BorderLayout());
        textArea = new JTextArea ();
        dp.add(new JScrollPane(textArea),BorderLayout.CENTER);
        JButton button=new JButton("Align");
        dp.add(button,BorderLayout.SOUTH);
        panel.addDesktopComponent(dp,0,2,2,1);
        button.addActionListener(new  ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
         try
          {
           File file=File.createTempFile("temp", ".xml");
           OutputStream out=new FileOutputStream(file);
           out.write(textArea.getText().getBytes());
           out.close();
            
        
        DockablePanel dp=new DockablePanel();
        dp.title="XML Tree";
        dp.setLayout(new BorderLayout());
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();
		Document document = builder.parse(file);
		document.normalize();
		XMLTreePanel xmlpanel = new XMLTreePanel();
		xmlpanel.setDocument(document);
        
		dp.add(xmlpanel,BorderLayout.CENTER);
        panel.addDesktopComponent(dp,2,1,1,2);
        
        dp=new DockablePanel();
        dp.title="XML Structure";
        dp.setLayout(new BorderLayout());
        JEditorPane edit = new JEditorPane();
        String testXML="";
        byte[] data = Files.readAllBytes(file.toPath());
        testXML=new String(data);
        edit.setEditorKit(new XMLEditorKit());
        edit.setText(testXML);
        edit.setEditable(false);
        dp.add(new JScrollPane(edit),BorderLayout.CENTER);
        panel.addDesktopComponent(dp,0,3,3,1);
        panel.refresh();
        }
        catch(Exception ex)
        {
        	JOptionPane.showMessageDialog(null,"XML not in well form","result :",  
    				JOptionPane.INFORMATION_MESSAGE); 
        }
            }
        });
        
        } catch (Exception e) {
        	
		}
    }
}