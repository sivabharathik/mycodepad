/*
 * Created on Aug 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package tabbedPane;
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * @author KRISHNAKUMARP
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SampleApp extends JFrame {

   public SampleApp(String title) {
     super(title);
     addComponents();
     setDefaultCloseOperation(EXIT_ON_CLOSE);
     setSize(350, 200);
     setVisible(true);     
   }
   
   protected void addComponents() {
    CloseAndMaxTabbedPane tabbedPane = new CloseAndMaxTabbedPane(true);
    JButton button1 = new JButton("This is Tab 1");
    JPanel buttonPanel1 = new JPanel();
    buttonPanel1.add(button1);
    tabbedPane.addTab("Tab 1", buttonPanel1);
    
    JButton button2 = new JButton("This is Tab 2");
    JPanel buttonPanel2 = new JPanel();
    buttonPanel2.add(button2);
    tabbedPane.addTab("Tab 2", buttonPanel2);

    
    JButton button3 = new JButton("This is Tab 3");
    JPanel buttonPanel3 = new JPanel();
    buttonPanel3.add(button3);
    tabbedPane.addTab("Tab 3", buttonPanel3);
    
    getContentPane().add(tabbedPane, 
      BorderLayout.CENTER);
   }
   
   public static void main(String args[]) {
   	SampleApp fe = new SampleApp("EnhancedTabbedPane Sample App");
   }    
}