package XMLTable;

 
// Core Java APIs
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// SAX
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

// Swing
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;


 
public class XmlTreeViewerSax extends JFrame {


    // Default parser to use
    private String vendorParserClass =
        "org.apache.xerces.parsers.SAXParser";

    // The base tree to render
    private JTree jTree;

    // Tree Model to use
    DefaultTreeModel defaultTreeModel;

    public XmlTreeViewerSax() {
        // Handle Swing setup
        super("SAX Tree Viewer");
        setSize(600, 450);
    }


    public void init(String xmlURI)
        throws IOException, SAXException {

        DefaultMutableTreeNode base = new DefaultMutableTreeNode(
                                            "XML Do*****ent: " + xmlURI);

        // Build Tree Model
        defaultTreeModel = new DefaultTreeModel(base);
        jTree = new JTree(defaultTreeModel);

        // Construct the tree hierarchy
        buildTree(defaultTreeModel, base, xmlURI);

        // Display the results
        getContentPane().add(new JScrollPane(jTree), BorderLayout.CENTER);

    }


    public void buildTree(    DefaultTreeModel treeModel
                            , DefaultMutableTreeNode base
                            , String xmlURI) 
                            throws IOException, SAXException {

        // Create instances needed for parsing
        XMLReader reader = XMLReaderFactory.createXMLReader(vendorParserClass);

        // Register content handler

        // Register error handler

        // Parse
        InputSource inputSource = new InputSource(xmlURI);
        reader.parse(inputSource);

    }

    
    /**
     * Sole entry point to the class and application.
     * @param args Array of String arguments.
     */
    public static void main(String[] args) {

        

        String doentURI ="";

        try {
            XmlTreeViewerSax viewer = new XmlTreeViewerSax();
            viewer.init(doentURI);
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}