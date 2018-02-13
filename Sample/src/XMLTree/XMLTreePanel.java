package XMLTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.w3c.dom.Document;

public class XMLTreePanel extends JPanel {

	private JTree tree;
	private XMLTreeModel model;
	
	public XMLTreePanel() {
		setLayout(new BorderLayout());
		
		model = new XMLTreeModel();
		tree = new JTree();
		tree.setModel(model);
		tree.setShowsRootHandles(true);
		tree.setEditable(false);
		
		JScrollPane pane = new JScrollPane(tree);
		pane.setPreferredSize(new Dimension(500,600));

		add(pane, "Center");
		
		final JTextArea text = new JTextArea(2,1);
		text.setEditable(false);
		
		Font font = new Font("Arial", Font.BOLD, 24);
	    text.setFont(font);
	    text.setForeground(Color.BLACK);
	    text.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	    add(text, "South");
		//add(text);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				Object lpc = e.getPath().getLastPathComponent();
				if (lpc instanceof XMLTreeNode) {
					text.setText( ((XMLTreeNode)lpc).getText() );
				}
			}
		});
		
	}
	
	/* methods that delegate to the custom model */
	public void setDocument(Document document) {
		model.setDocument(document);
	}
	public Document getDocument() {
		return model.getDocument();
	}
	
}
