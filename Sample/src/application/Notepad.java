package application;

//package p1;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import XMLTree.DemoMain;
import dockWindow.Test;
import hierarchy.App;
import javaComp.JavaCompile;
//import p1.FontChooser;
//import p1.FontDialog;
//import p1.FindDialog;
//import p1.LookAndFeelMenu;
//import p1.MyFileFilter;
/************************************/

class Splash extends JFrame {

    private JLabel imglabel;
    private ImageIcon img;
    private static JProgressBar pbar;
    Thread t = null;

    public Splash() {
        super("CodePad");
        setSize(404, 310);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        img = new ImageIcon(getClass().getResource("/application/loader1.gif"));
        setBackground(new Color(0, 255, 0, 0));
        imglabel = new JLabel(img);
        add(imglabel);
        setLayout(null);
        pbar = new JProgressBar();
        pbar.setMinimum(0);
        pbar.setMaximum(100);
        pbar.setStringPainted(true);
        pbar.setForeground(Color.LIGHT_GRAY);
        imglabel.setBounds(0, 0, 404, 310);
        //add(pbar);
        pbar.setPreferredSize(new Dimension(310, 30));
        pbar.setBounds(0, 290, 404, 20);

        Thread t = new Thread() {

            public void run() {
                int i = 0;
                while (i <= 100) {
                    pbar.setValue(i);
                    try {
                       sleep(90);
                    	
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Splash.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    i++;
                }
            }
        };
        t.start();
    }
}
class FileOperation
{
Notepad npd;

boolean saved;
boolean newFileFlag;
String fileName;
String applicationTitle="Codepad";

File fileRef;
JFileChooser chooser;
/////////////////////////////
boolean isSave(){return saved;}
void setSave(boolean saved){this.saved=saved;}
String getFileName(){return new String(fileName);}
void setFileName(String fileName){this.fileName=new String(fileName);}
/////////////////////////
FileOperation(Notepad npd)
{
this.npd=npd;

saved=true;
newFileFlag=true;
fileName=new String("Untitled");
fileRef=new File(fileName);
this.npd.f.setTitle(fileName+" - "+applicationTitle);

chooser=new JFileChooser();
chooser.addChoosableFileFilter(new MyFileFilter(".java","Java Source Files(*.java)"));
chooser.addChoosableFileFilter(new MyFileFilter(".txt","Text Files(*.txt)"));
chooser.setCurrentDirectory(new File("."));

}
//////////////////////////////////////

boolean saveFile(File temp)
{
FileWriter fout=null;
try
{
fout=new FileWriter(temp);
fout.write(npd.ta.getText());
}
catch(IOException ioe){updateStatus(temp,false);return false;}
finally
{try{fout.close();}catch(IOException excp){}}
updateStatus(temp,true);
return true;
}
////////////////////////
boolean saveThisFile()
{

if(!newFileFlag)
	{return saveFile(fileRef);}

return saveAsFile();
}
////////////////////////////////////
boolean saveAsFile()
{
File temp=null;
chooser.setDialogTitle("Save As...");
chooser.setApproveButtonText("Save Now"); 
chooser.setApproveButtonMnemonic(KeyEvent.VK_S);
chooser.setApproveButtonToolTipText("Click me to save!");

do
{
if(chooser.showSaveDialog(this.npd.f)!=JFileChooser.APPROVE_OPTION)
	return false;
temp=chooser.getSelectedFile();
if(!temp.exists()) break;
if(   JOptionPane.showConfirmDialog(
	this.npd.f,"<html>"+temp.getPath()+" already exists.<br>Do you want to replace it?<html>",
	"Save As",JOptionPane.YES_NO_OPTION
				)==JOptionPane.YES_OPTION)
	break;
}while(true);


return saveFile(temp);
}

////////////////////////
boolean openFile(File temp)
{
FileInputStream fin=null;
BufferedReader din=null;

try
{
fin=new FileInputStream(temp);
din=new BufferedReader(new InputStreamReader(fin));
String str=" ";
while(str!=null)
{
str=din.readLine();
if(str==null)
break;
this.npd.ta.append(str+"\n");
}

}
catch(IOException ioe){updateStatus(temp,false);return false;}
finally
{try{din.close();fin.close();}catch(IOException excp){}}
updateStatus(temp,true);
this.npd.ta.setCaretPosition(0);
return true;
}
///////////////////////
void openFile()
{
if(!confirmSave()) return;
chooser.setDialogTitle("Open File...");
chooser.setApproveButtonText("Open this"); 
chooser.setApproveButtonMnemonic(KeyEvent.VK_O);
chooser.setApproveButtonToolTipText("Click me to open the selected file.!");

File temp=null;
do
{
if(chooser.showOpenDialog(this.npd.f)!=JFileChooser.APPROVE_OPTION)
	return;
temp=chooser.getSelectedFile();

if(temp.exists())	break;

JOptionPane.showMessageDialog(this.npd.f,
	"<html>"+temp.getName()+"<br>file not found.<br>"+
	"Please verify the correct file name was given.<html>",
	"Open",	JOptionPane.INFORMATION_MESSAGE);

} while(true);

this.npd.ta.setText("");

if(!openFile(temp))
	{
	fileName="Untitled"; saved=true; 
	this.npd.f.setTitle(fileName+" - "+applicationTitle);
	}
if(!temp.canWrite())
	newFileFlag=true;

}
////////////////////////
void updateStatus(File temp,boolean saved)
{
if(saved)
{
this.saved=true;
fileName=new String(temp.getName());
if(!temp.canWrite())
	{fileName+="(Read only)"; newFileFlag=true;}
fileRef=temp;
npd.f.setTitle(fileName + " - "+applicationTitle);
npd.statusBar.setText("File : "+temp.getPath()+" saved/opened successfully.");
newFileFlag=false;
}
else
{
npd.statusBar.setText("Failed to save/open : "+temp.getPath());
}
}
///////////////////////
boolean confirmSave()
{
String strMsg="<html>The text in the "+fileName+" file has been changed.<br>"+
	"Do you want to save the changes?<html>";
if(!saved)
{
int x=JOptionPane.showConfirmDialog(this.npd.f,strMsg,applicationTitle,JOptionPane.YES_NO_CANCEL_OPTION);

if(x==JOptionPane.CANCEL_OPTION) return false;
if(x==JOptionPane.YES_OPTION && !saveAsFile()) return false;
}
return true;
}
///////////////////////////////////////
void newFile()
{
if(!confirmSave()) return;

this.npd.ta.setText("");
fileName=new String("Untitled");
fileRef=new File(fileName);
saved=true;
newFileFlag=true;
this.npd.f.setTitle(fileName+" - "+applicationTitle);
}
//////////////////////////////////////
}// end defination of class FileOperation
/************************************/
public class Notepad extends JFrame  implements ActionListener, MenuConstants
{

public static JFrame f;
RSyntaxTextArea  ta;
public static JLabel statusBar;
DefaultCompletionProvider provider= new DefaultCompletionProvider();
private String fileName="Untitled";
private boolean saved=true;
String applicationName="Javapad";

String searchString, replaceString;
int lastSearchIndex;

FileOperation fileHandler;
FontChooser fontDialog=null;
FindDialog findReplaceDialog=null; 
JColorChooser bcolorChooser=null;
JColorChooser fcolorChooser=null;
JDialog backgroundDialog=null;
JDialog foregroundDialog=null;
JMenuItem cutItem,copyItem, deleteItem, findItem, findNextItem, replaceItem, gotoItem, selectAllItem;
/**
 * @throws UnsupportedLookAndFeelException 
 * @throws IllegalAccessException 
 * @throws InstantiationException 
 * @throws ClassNotFoundException **************************/
Notepad() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
{
f=new JFrame(fileName+" - "+applicationName);
ta=new RSyntaxTextArea(20, 60);
ta.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
ta.setAnimateBracketMatching(true);
ta.setCodeFoldingEnabled(true);
DropTarget target=new DropTarget(ta,new DropTargetListener(){
    public void dragEnter(DropTargetDragEvent e)
    {
    }
    
    public void dragExit(DropTargetEvent e)
    {
    }
    
    public void dragOver(DropTargetDragEvent e)
    {
    }
    
    public void dropActionChanged(DropTargetDragEvent e)
    {
    
    }
    
    public void drop(DropTargetDropEvent e)
    {
        try
        {
            // Accept the drop first, important!
            e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            
            // Get the files that are dropped as java.util.List
            java.util.List list=(java.util.List) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            
            // Now get the first file from the list,
            File file=(File)list.get(0);
            f.setTitle(file.getName());
            saved=true;
            fileName=new String(file.getName());
            statusBar.setText("File : "+file.getPath()+" saved/opened successfully.");
            ta.setText(new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))));
            
        }catch(Exception ex){}
    }
});
/*ta.addKeyListener(new KeyAdapter() {
	@Override public void keyPressed(KeyEvent event) 
	{
		System.out.println(event);
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		System.out.println(provider.getParameterListSeparator());
		
	}
}
	);*/
statusBar=new JLabel("||       Ln 1, Col 1  ",JLabel.RIGHT);
try 
{
//File zip = new File(getClass().getResource("/application/english_dic.zip").toURI());
InputStream in = getClass().getResourceAsStream("/english_dic.zip");
File out=File.createTempFile("Eng", "Dic");
Files.copy(in,out.toPath() , StandardCopyOption.REPLACE_EXISTING);
boolean usEnglish = false; // "false" will use British English
SpellingParser parser;
out.deleteOnExit();
parser = SpellingParser.createEnglishSpellingParser(out, usEnglish);
ta.addParser(parser);
} catch (Exception e1) 
{
	// TODO Auto-generated catch block
	e1.printStackTrace();
	statusBar.setText(e1.toString());
}


JPanel contentPane = new JPanel(new BorderLayout());
contentPane.add(new RTextScrollPane(ta));
setContentPane(contentPane);
f.add(new JScrollPane(ta),BorderLayout.CENTER);
f.add(statusBar,BorderLayout.SOUTH);
f.add(new JLabel("  "),BorderLayout.EAST);
f.add(new JLabel("  "),BorderLayout.WEST);
createMenuBar(f);
//f.setSize(350,350);
f.pack();
f.setLocation(100,50);
f.setVisible(true);
f.setLocation(150,50);
f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

fileHandler=new FileOperation(this);

/////////////////////

ta.addCaretListener(
new CaretListener()
{
public void caretUpdate(CaretEvent e)
{
int lineNumber=0, column=0, pos=0;

try
{
pos=ta.getCaretPosition();
lineNumber=ta.getLineOfOffset(pos);
column=pos-ta.getLineStartOffset(lineNumber);
}catch(Exception excp){}
if(ta.getText().length()==0){lineNumber=0; column=0;}
statusBar.setText("||       Ln "+(lineNumber+1)+", Col "+(column+1));
}
});
//////////////////
DocumentListener myListener = new DocumentListener()
{
public void changedUpdate(DocumentEvent e){fileHandler.saved=false;}
public void removeUpdate(DocumentEvent e){fileHandler.saved=false;}
public void insertUpdate(DocumentEvent e){fileHandler.saved=false;}
};
ta.getDocument().addDocumentListener(myListener);
/////////
WindowListener frameClose=new WindowAdapter()
{
public void windowClosing(WindowEvent we)
{
if(fileHandler.confirmSave())System.exit(0);
}
};
f.addWindowListener(frameClose);
//////////////////
/*
ta.append("Hello dear hello hi");
ta.append("\nwho are u dear mister hello");
ta.append("\nhello bye hel");
ta.append("\nHello");
ta.append("\nMiss u mister hello hell");
fileHandler.saved=true;
*/
}
////////////////////////////////////
void goTo()
{
int lineNumber=0;
try
{
lineNumber=ta.getLineOfOffset(ta.getCaretPosition())+1;
String tempStr=JOptionPane.showInputDialog(f,"Enter Line Number:",""+lineNumber);
if(tempStr==null)
	{return;}
lineNumber=Integer.parseInt(tempStr);
ta.setCaretPosition(ta.getLineStartOffset(lineNumber-1));
}catch(Exception e){}
}
///////////////////////////////////
//action Performed
////////////////////////////////////
void showBackgroundColorDialog()
{
if(bcolorChooser==null)
	bcolorChooser=new JColorChooser();
if(backgroundDialog==null)
	backgroundDialog=JColorChooser.createDialog
		(Notepad.this.f,
		formatBackground,
		false,
		bcolorChooser,
		new ActionListener()
		{public void actionPerformed(ActionEvent evvv){
			Notepad.this.ta.setBackground(bcolorChooser.getColor());}},
		null);		

backgroundDialog.setVisible(true);
}
////////////////////////////////////
void showForegroundColorDialog()
{
if(fcolorChooser==null)
	fcolorChooser=new JColorChooser();
if(foregroundDialog==null)
	foregroundDialog=JColorChooser.createDialog
		(Notepad.this.f,
		formatForeground,
		false,
		fcolorChooser,
		new ActionListener()
		{public void actionPerformed(ActionEvent evvv){
			Notepad.this.ta.setForeground(fcolorChooser.getColor());}},
		null);		

foregroundDialog.setVisible(true);
}

///////////////////////////////////
JMenuItem createMenuItem(String s, int key,JMenu toMenu,ActionListener al)
{
JMenuItem temp=new JMenuItem(s,key);
temp.addActionListener(al);
toMenu.add(temp);

return temp;
}
////////////////////////////////////
JMenuItem createMenuItem(String s, int key,JMenu toMenu,int aclKey,ActionListener al)
{
JMenuItem temp=new JMenuItem(s,key);
temp.addActionListener(al);
temp.setAccelerator(KeyStroke.getKeyStroke(aclKey,ActionEvent.CTRL_MASK));
toMenu.add(temp);

return temp;
}
////////////////////////////////////
JCheckBoxMenuItem createCheckBoxMenuItem(String s, int key,JMenu toMenu,ActionListener al)
{
JCheckBoxMenuItem temp=new JCheckBoxMenuItem(s);
temp.setMnemonic(key);
temp.addActionListener(al);
temp.setSelected(false);
toMenu.add(temp);

return temp;
}
////////////////////////////////////
JMenu createMenu(String s,int key,JMenuBar toMenuBar)
{
JMenu temp=new JMenu(s);
temp.setMnemonic(key);
toMenuBar.add(temp);
return temp;
}
/**
 * @throws UnsupportedLookAndFeelException 
 * @throws IllegalAccessException 
 * @throws InstantiationException 
 * @throws ClassNotFoundException *******************************/
void createMenuBar(JFrame f) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
{
JMenuBar mb=new JMenuBar();
JMenuItem temp;

JMenu fileMenu=createMenu(fileText,KeyEvent.VK_F,mb);
JMenu editMenu=createMenu(editText,KeyEvent.VK_E,mb);
JMenu formatMenu=createMenu(formatText,KeyEvent.VK_O,mb);
JMenu theme=createMenu("Theme",KeyEvent.VK_V,mb);
JMenu viewMenu=createMenu(viewText,KeyEvent.VK_V,mb);
JMenu langMenu=createMenu(langText,KeyEvent.VK_F11,mb);
JMenu XMLView=createMenu(xmlText,KeyEvent.VK_8,mb);
JMenu cmdView=createMenu(cmdText,KeyEvent.VK_F10,mb);
JMenu shareView=createMenu(shareText,KeyEvent.VK_F10,mb);

JMenu helpMenu=createMenu(helpText,KeyEvent.VK_H,mb);

createMenuItem(fileNew,KeyEvent.VK_N,fileMenu,KeyEvent.VK_N,this);
createMenuItem(fileOpen,KeyEvent.VK_O,fileMenu,KeyEvent.VK_O,this);
createMenuItem(fileSave,KeyEvent.VK_S,fileMenu,KeyEvent.VK_S,this);
createMenuItem(fileSaveAs,KeyEvent.VK_A,fileMenu,this);
fileMenu.addSeparator();
temp=createMenuItem(filePageSetup,KeyEvent.VK_U,fileMenu,this);
temp.setEnabled(false);
createMenuItem(filePrint,KeyEvent.VK_P,fileMenu,KeyEvent.VK_P,this);
fileMenu.addSeparator();
createMenuItem(fileExit,KeyEvent.VK_X,fileMenu,this);

temp=createMenuItem(editUndo,KeyEvent.VK_U,editMenu,KeyEvent.VK_Z,this);
temp.setEnabled(false);
editMenu.addSeparator();
cutItem=createMenuItem(editCut,KeyEvent.VK_T,editMenu,KeyEvent.VK_X,this);
copyItem=createMenuItem(editCopy,KeyEvent.VK_C,editMenu,KeyEvent.VK_C,this);
createMenuItem(editPaste,KeyEvent.VK_P,editMenu,KeyEvent.VK_V,this);
deleteItem=createMenuItem(editDelete,KeyEvent.VK_L,editMenu,this);
deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
editMenu.addSeparator();
findItem=createMenuItem(editFind,KeyEvent.VK_F,editMenu,KeyEvent.VK_F,this);
findNextItem=createMenuItem(editFindNext,KeyEvent.VK_N,editMenu,this);
findNextItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
replaceItem=createMenuItem(editReplace,KeyEvent.VK_R,editMenu,KeyEvent.VK_H,this);
gotoItem=createMenuItem(editGoTo,KeyEvent.VK_G,editMenu,KeyEvent.VK_G,this);
editMenu.addSeparator();
selectAllItem=createMenuItem(editSelectAll,KeyEvent.VK_A,editMenu,KeyEvent.VK_A,this);
createMenuItem(editTimeDate,KeyEvent.VK_D,editMenu,this).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));

createCheckBoxMenuItem(formatWordWrap,KeyEvent.VK_W,formatMenu,this);

createMenuItem(formatFont,KeyEvent.VK_F,formatMenu,this);
formatMenu.addSeparator();
createMenuItem(formatForeground,KeyEvent.VK_T,formatMenu,this);
createMenuItem(formatBackground,KeyEvent.VK_P,formatMenu,this);
createMenuItem("Suggestion",KeyEvent.VK_P,formatMenu,this);

createMenuItem(java,KeyEvent.VK_F3,langMenu,this).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));;
createMenuItem(XML,KeyEvent.VK_F4,langMenu,this);
createMenuItem(HTML,KeyEvent.VK_F5,langMenu,this);
createMenuItem(JSON,KeyEvent.VK_F6,langMenu,this);
createMenuItem(others,KeyEvent.VK_F7,langMenu,this);
langMenu.addSeparator();



createMenuItem("Sea",KeyEvent.VK_SPACE,theme,this);
createMenuItem("Metal",KeyEvent.VK_SPACE,theme,this);
createMenuItem("Windows",KeyEvent.VK_SPACE,theme,this);
createMenuItem("WindowsClassic",KeyEvent.VK_SPACE,theme,this);
createMenuItem("Nimbus",KeyEvent.VK_SPACE,theme,this);
createMenuItem("Motif",KeyEvent.VK_SPACE,theme,this);
createMenuItem("Dark",KeyEvent.VK_SPACE,theme,this);

createMenuItem("SendMail",KeyEvent.VK_SPACE,shareView,this);
createMenuItem("Whatsapp",KeyEvent.VK_SPACE,shareView,this);
createMenuItem("etc",KeyEvent.VK_Q,shareView,this);

createMenuItem("XML Editor",KeyEvent.VK_SPACE,XMLView,this);
createMenuItem("Align",KeyEvent.VK_SPACE,XMLView,this);
createMenuItem("XML Tree",KeyEvent.VK_Q,XMLView,this);
createMenuItem("XML Structure",KeyEvent.VK_Q,XMLView,this);
createMenuItem("Remove Node",KeyEvent.VK_Q,XMLView,this);
/************CMD***/  
createMenuItem("Custom CMD",KeyEvent.VK_SPACE,cmdView,this);
createMenuItem("IP Address(Full)",KeyEvent.VK_SPACE,cmdView,this);
createMenuItem("IP Address(IP only)",KeyEvent.VK_SPACE,cmdView,this);
createMenuItem("SystemInfo",KeyEvent.VK_0,cmdView,this);
createMenuItem("EnviromentInfo",KeyEvent.VK_0,cmdView,this);
createMenuItem("Notepad",KeyEvent.VK_SPACE,cmdView,this);
createMenuItem("Calculator",KeyEvent.VK_SPACE,cmdView,this);
createMenuItem("Ping",KeyEvent.VK_SPACE,cmdView,this);
createMenuItem("Remote Desktop",KeyEvent.VK_SPACE,cmdView,this);

createCheckBoxMenuItem(viewStatusBar,KeyEvent.VK_S,viewMenu,this).setSelected(true);
/************For Look and Feel, May not work properly on different operating environment***/
//LookAndFeelMenu.createLookAndFeelMenuItem(viewMenu,this.f);


temp=createMenuItem(helpHelpTopic,KeyEvent.VK_H,helpMenu,this);
//temp.setEnabled(false);
helpMenu.addSeparator();
createMenuItem(helpAboutNotepad,KeyEvent.VK_A,helpMenu,this);

MenuListener editMenuListener=new MenuListener()
{
   public void menuSelected(MenuEvent evvvv)
	{
	if(Notepad.this.ta.getText().length()==0)
	{
	findItem.setEnabled(false);
	findNextItem.setEnabled(false);
	replaceItem.setEnabled(false);
	selectAllItem.setEnabled(false);
	gotoItem.setEnabled(false);
	}
	else
	{
	findItem.setEnabled(true);
	findNextItem.setEnabled(true);
	replaceItem.setEnabled(true);
	selectAllItem.setEnabled(true);
	gotoItem.setEnabled(true);
	}
	if(Notepad.this.ta.getSelectionStart()==ta.getSelectionEnd())
	{
	cutItem.setEnabled(false);
	copyItem.setEnabled(false);
	deleteItem.setEnabled(false);
	}
	else
	{
	cutItem.setEnabled(true);
	copyItem.setEnabled(true);
	deleteItem.setEnabled(true);
	}
	}
   public void menuDeselected(MenuEvent evvvv){}
   public void menuCanceled(MenuEvent evvvv){}
};
editMenu.addMenuListener(editMenuListener);
f.setJMenuBar(mb);
}

public void actionPerformed(ActionEvent ev)
{
String cmdText=ev.getActionCommand();
////////////////////////////////////
if(cmdText.equals(fileNew))
	fileHandler.newFile();
else if(cmdText.equals(fileOpen))
	fileHandler.openFile();
////////////////////////////////////
else if(cmdText.equals(fileSave))
	fileHandler.saveThisFile();
////////////////////////////////////
else if(cmdText.equals(fileSaveAs))
	fileHandler.saveAsFile();
////////////////////////////////////
else if(cmdText.equals(fileExit))
	{if(fileHandler.confirmSave())System.exit(0);}
////////////////////////////////////
else if(cmdText.equals(filePrint))
JOptionPane.showMessageDialog(
	Notepad.this.f,
	"Get ur printer repaired first! It seems u dont have one!",
	"Bad Printer",
	JOptionPane.INFORMATION_MESSAGE
	);
////////////////////////////////////
else if(cmdText.equals(editCut))
	ta.cut();
////////////////////////////////////
else if(cmdText.equals(editCopy))
	ta.copy();
////////////////////////////////////
else if(cmdText.equals(editPaste))
	ta.paste();
////////////////////////////////////
else if(cmdText.equals(editDelete))
	ta.replaceSelection("");
////////////////////////////////////
else if(cmdText.equals(editFind))
{
if(Notepad.this.ta.getText().length()==0)
	return;	// text box have no text
if(findReplaceDialog==null)
	findReplaceDialog=new FindDialog(Notepad.this.ta);
findReplaceDialog.showDialog(Notepad.this.f,true);//find
}
////////////////////////////////////
else if(cmdText.equals(editFindNext))
{
if(Notepad.this.ta.getText().length()==0)
	return;	// text box have no text

if(findReplaceDialog==null)
	statusBar.setText("Nothing to search for, use Find option of Edit Menu first !!!!");
else
	findReplaceDialog.findNextWithSelection();
}
////////////////////////////////////
else if(cmdText.equals(editReplace))
{
if(Notepad.this.ta.getText().length()==0)
	return;	// text box have no text

if(findReplaceDialog==null)
	findReplaceDialog=new FindDialog(Notepad.this.ta);
findReplaceDialog.showDialog(Notepad.this.f,false);//replace
}
////////////////////////////////////
else if(cmdText.equals(editGoTo))
{
if(Notepad.this.ta.getText().length()==0)
	return;	// text box have no text
goTo();
}
////////////////////////////////////
else if(cmdText.equals(editSelectAll))
	ta.selectAll();
////////////////////////////////////
else if(cmdText.equals(editTimeDate))
	ta.insert(new Date().toString(),ta.getSelectionStart());
////////////////////////////////////
else if(cmdText.equals(formatWordWrap))
{
JCheckBoxMenuItem temp=(JCheckBoxMenuItem)ev.getSource();
ta.setLineWrap(temp.isSelected());
}
////////////////////////////////////
else if(cmdText.equals(formatFont))
{
if(fontDialog==null)
	fontDialog=new FontChooser(ta.getFont());

if(fontDialog.showDialog(Notepad.this.f,"Choose a font"))
	Notepad.this.ta.setFont(fontDialog.createFont());
}
////////////////////////////////////
else if(cmdText.equals(formatForeground))
	showForegroundColorDialog();
////////////////////////////////////
else if(cmdText.equals(formatBackground))
	showBackgroundColorDialog();
////////////////////////////////////

else if(cmdText.equals(viewStatusBar))
{
JCheckBoxMenuItem temp=(JCheckBoxMenuItem)ev.getSource();
statusBar.setVisible(temp.isSelected());
}
////////////////////////////////////
else if(cmdText.equals(helpAboutNotepad))
{
//InputStream in = getClass().getResourceAsStream("/index.html");
//StringBuilder XMLGenerationString = new StringBuilder(
		//new Scanner(in).useDelimiter("\\Z").next());
JOptionPane.showMessageDialog(Notepad.this.f,aboutText,"Dedicated 2 u!",JOptionPane.INFORMATION_MESSAGE);
}
else if(cmdText.equals("IP Address(Full)"))  
{
	try {
		StringBuilder sb=JavaCompile.runProcess("ipconfig /all");
		JOptionPane.showMessageDialog(Notepad.this.f,sb,"result :",  
				JOptionPane.INFORMATION_MESSAGE); 
		if(sb.length()>0)
		{
		Notepad.this.ta.setText(Notepad.this.ta.getText()+"\n******************************************************************\n"+sb); 
		}
	}
	catch(Exception e){}
}
else if(cmdText.equals("IP Address(IP only)"))  
{
	try {
		StringBuilder sb=JavaCompile.runProcess("ipconfig /all");
		String temp[]=sb.toString().split(" IPv4 Address. . . . . . . . . . . :");
		
		JOptionPane.showMessageDialog(Notepad.this.f,temp[1].substring(0, 15),"result :",  
				JOptionPane.INFORMATION_MESSAGE); 
		if(sb.length()>0)
		{
		Notepad.this.ta.setText(Notepad.this.ta.getText()+"\n******************************************************************\nIPV4 :"+temp[1].substring(0, 15)); 
		}
	}
	catch(Exception e){JOptionPane.showMessageDialog(Notepad.this.f,"Network not connected !!\nPlease try again aftersome times","result :",  
			JOptionPane.INFORMATION_MESSAGE); }
}
else if(cmdText.equals("Remote Desktop"))  
{
	try {
		JavaCompile.runProcess("mstsc");
	}
	catch(Exception e){}
}


else if(cmdText.equals("SystemInfo"))  
{
	try {
		JavaCompile.runProcess("systeminfo");
	}
	catch(Exception e){
		System.out.println(e);
	}
}
else if(cmdText.equals("EnviromentInfo"))  
{
Map<String, String> map=System.getenv();
StringBuilder sb=new StringBuilder();
for (Map.Entry<String, String> entry : map.entrySet())
{
	if(entry.getKey().equals("Path"))
	{
		sb.append(" "+entry.getKey() + "\t\t ==>\n");
		String[] paths=(entry.getValue()).split(";");
		for (String s: paths) {
			
		
			sb.append(" \t\t\t ==>  " + s+"\n");
		}
	}
	else
	{
	sb.append(" "+entry.getKey() + "  ==>  " + entry.getValue()+"\n");
	}
}
JOptionPane.showMessageDialog(Notepad.this.f,sb,"System Info :",  
		JOptionPane.INFORMATION_MESSAGE); 
if(sb.length()>0)
{
Notepad.this.ta.setText(Notepad.this.ta.getText()+"\n******************************************************************\n"+sb); 
}
}
else if(cmdText.equals("Notepad"))  
{
	try {
		JavaCompile.runProcess("notepad");
	}
	catch(Exception e){}
}
else if(cmdText.equals("Sea"))
{
	try {
		UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	SwingUtilities.updateComponentTreeUI(f);
}
else if(cmdText.equals("Metal"))
{
	try {
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	SwingUtilities.updateComponentTreeUI(f);
}
else if(cmdText.equals("Motif"))
{
	try {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	SwingUtilities.updateComponentTreeUI(f);
}
else if(cmdText.equals("Nimbus"))
{
	try {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	SwingUtilities.updateComponentTreeUI(f);
}
else if(cmdText.equals("Windows"))
{
	try {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	SwingUtilities.updateComponentTreeUI(f);
}
else if(cmdText.equals("WindowsClassic"))
{
	try {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	SwingUtilities.updateComponentTreeUI(f);
}
else if(cmdText.equals("Dark"))
{
	try {
		DefaultMetalTheme darkTheme = new DefaultMetalTheme() {

	        @Override
	        public ColorUIResource getPrimary1() {
	            return new ColorUIResource(new Color(30, 30, 30));
	        }


	        @Override
	        public ColorUIResource getPrimary2() {
	            return new ColorUIResource(new Color(20, 20, 20));
	        }

	        @Override
	        public ColorUIResource getPrimary3() {
	            return new ColorUIResource(new Color(30, 30, 30));
	        }

	        @Override
	        public ColorUIResource getBlack(){
	                    return new ColorUIResource(new Color(30, 30, 30));
	                }

	        @Override
	        public ColorUIResource getWhite() {
	            return new ColorUIResource(new Color(240, 240, 240));
	        }


	        @Override
	        public ColorUIResource getMenuForeground() {
	            return new ColorUIResource(new Color(200, 200, 200));
	        }

	        @Override
	        public ColorUIResource getMenuBackground() {
	            return new ColorUIResource(new Color(25, 25, 25));
	        }

	         @Override
	        public ColorUIResource getMenuSelectedBackground(){
	            return new ColorUIResource(new Color(50, 50, 50));
	        }

	        @Override
	        public ColorUIResource getMenuSelectedForeground() {
	            return new ColorUIResource(new Color(255, 255, 255));
	        }


	        @Override
	        public ColorUIResource getSeparatorBackground() {
	            return new ColorUIResource(new Color(15, 15, 15));
	        }


	        @Override
	        public ColorUIResource getUserTextColor() {
	            return new ColorUIResource(new Color(240, 240, 240));
	        }

	        @Override
	        public ColorUIResource getTextHighlightColor() {
	            return new ColorUIResource(new Color(80, 40, 80));
	        }


	        @Override
	        public ColorUIResource getAcceleratorForeground(){
	            return new ColorUIResource(new Color(30, 30,30));
	        }


	        @Override
	        public ColorUIResource getWindowTitleInactiveBackground() {
	            return new ColorUIResource(new Color(30, 30, 30));
	        }


	        @Override
	        public ColorUIResource getWindowTitleBackground() {
	            return new ColorUIResource(new Color(30, 30, 30));
	        }


	        @Override
	        public ColorUIResource getWindowTitleForeground() {
	            return new ColorUIResource(new Color(230, 230, 230));
	        }

	        @Override
	        public ColorUIResource getPrimaryControlHighlight() {
	            return new ColorUIResource(new Color(40, 40, 40));
	        }

	        @Override
	        public ColorUIResource getPrimaryControlDarkShadow() {
	            return new ColorUIResource(new Color(40, 40, 40));
	        }

	        @Override
	        public ColorUIResource getPrimaryControl() {
	            //color for minimize,maxi,and close
	            return new ColorUIResource(new Color(60, 60, 60));
	        }

	        @Override
	        public ColorUIResource getControlHighlight() {
	            return new ColorUIResource(new Color(20, 20, 20));
	        }

	        @Override
	        public ColorUIResource getControlDarkShadow() {
	            return new ColorUIResource(new Color(50, 50, 50));
	        }

	        @Override
	        public ColorUIResource getControl() {
	            return new ColorUIResource(new Color(25, 25, 25));
	        }

	        @Override
	        public ColorUIResource getControlTextColor() {
	            return new ColorUIResource(new Color(230, 230, 230));
	        }

	        @Override
	        public ColorUIResource getFocusColor() {
	            return new ColorUIResource(new Color(0, 100, 0));
	        }

	        @Override
	        public ColorUIResource getHighlightedTextColor() {
	            return new ColorUIResource(new Color(250, 250, 250));
	        }

	    };
	    MetalLookAndFeel.setCurrentTheme(darkTheme);
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	SwingUtilities.updateComponentTreeUI(f);
}
	catch(Exception e) {
		
	}
}
else if(cmdText.equals("Calculator"))  
{
	try {
		JavaCompile.runProcess("calc");
	}
	catch(Exception e){}
}
else if(cmdText.equals("SendMail"))  
{
	 

     try {
    	 String subject="CodePad%20Content";
         String body="sent%20from%20CodePad";
         Desktop.getDesktop().mail( new URI( "mailto:appinous@gmail.com?subject="+subject+"&body="+body) );
     } 
     catch ( Exception ex )
     {
    	 System.out.println(ex);
     }
}
else if(cmdText.equals("Ping"))  
{
	try {
		String message = JOptionPane.showInputDialog(null, "Enter Address :");
		StringBuilder sb=JavaCompile.runProcess("ping "+message);
		JOptionPane.showMessageDialog(Notepad.this.f,sb,"CompileTime result :",  
				JOptionPane.INFORMATION_MESSAGE); 
		if(sb.length()>0)
		{
		Notepad.this.ta.setText(Notepad.this.ta.getText()+"\n******************************************************************\n"+sb); 
		}
	        //System.out.println();
	} catch (Exception e) {
		Notepad.this.ta.setText(Notepad.this.ta.getText()+"\n******************************************************************\n"
				+e.toString().substring(e.toString().indexOf(':')+1)); 
		System.out.println(e.toString());
	}
}
else if(cmdText.equals("Custom CMD"))  
{
	try {
		String message = JOptionPane.showInputDialog(null, "Enter command :");
		StringBuilder sb=JavaCompile.runProcess(message);
		JOptionPane.showMessageDialog(Notepad.this.f,sb,"result :",  
				JOptionPane.INFORMATION_MESSAGE); 
		if(sb.length()>0)
		{
		Notepad.this.ta.setText(Notepad.this.ta.getText()+"\n******************************************************************\n"+sb); 
		}
	        //System.out.println();
	} catch (Exception e) {
		Notepad.this.ta.setText(Notepad.this.ta.getText()+"\n******************************************************************\n"
				+e.toString().substring(e.toString().indexOf(':')+1)); 
		System.out.println(e.toString());
	}
}

else if(cmdText.equals("Suggestion"))  
{
	JTextField field1 = new JTextField();
	JTextField field2 = new JTextField();
	JTextField field3 = new JTextField();
	Object[] message = {
	    "Key : ", field1,
	    "Value : ", field2,
	    "Discription:", field3,
	};
	int option = JOptionPane.showConfirmDialog(this, message, "Enter all your values", JOptionPane.OK_CANCEL_OPTION);
	if (option == JOptionPane.OK_OPTION)
	{
	    String key = field1.getText();
	    String value = field2.getText();
	    String description = field3.getText();
	    provider.addCompletion(new ShorthandCompletion(provider, key,
	    		value, description));
		AutoCompletion ac = new AutoCompletion(provider);
		ac.install(ta);
	    
	}
	

}
else if(cmdText.equals("Remove Node"))  
{  
	String payload=Notepad.this.ta.getText();
		try
		{
	String message = JOptionPane.showInputDialog(null, "Enter tag name (Case sencitive) :");
	File file=File.createTempFile("temp", ".xml");
	OutputStream out=new FileOutputStream(file);
	out.write(payload.getBytes());
	out.close();

	ArrayList<Node> policylist=new ArrayList<Node>();
	ArrayList<Node> parentlist=new ArrayList<Node>();
	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	 dbf.setValidating(false);
	 DocumentBuilder db = dbf.newDocumentBuilder();
	 Document doc = db.parse(new FileInputStream(file));
	 NodeList entries = doc.getElementsByTagName("*");
	 int count=0;
	 for (int i=0; i<entries.getLength(); i++) {
	 Node node = (Element) entries.item(i);
	 if (message.equals(node.getNodeName())) 
	   {
		 parentlist.add(node.getParentNode());
		 policylist.add(node);
		 System.out.println("Removed element " + node.getNodeName());
		 statusBar.setText("Removing Node :"+count);
		 count++;
	   }
	
	 }
	 for(int i=0;i<policylist.size();i++)
	 {
		 parentlist.get(i).removeChild(policylist.get(i));
	 }
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(doc);
	StreamResult result = new StreamResult(file);
	transformer.transform(source, result);
	byte[] data = Files.readAllBytes(file.toPath());
	Notepad.this.ta.setText(new String(data));
	statusBar.setText(" Node "+message+" "+count+" found and successfully removed");
	file.deleteOnExit();
	 
		}
		catch(Exception e){System.out.println(e);}
	
	
	
  
} 
else if(cmdText.equals("XML Structure"))  
{
	System.out.println("hdchk");
	
	try {
	File file = File.createTempFile("temp", ".xml");
	String payload=Notepad.this.ta.getText();
	OutputStream out=new FileOutputStream(file);
	out.write(payload.getBytes());
	out.close();
	App.setFile(file.getAbsolutePath());
	new App();
	
	file.deleteOnExit();
	} catch (IOException e) {
		statusBar.setText("Wrong XML DATA");
		e.printStackTrace();
	}
}
else if(cmdText.equals("Help Topic"))
{
	try {
        Desktop.getDesktop().browse( new URI( "https://appinous.blogspot.in/2018/02/codepad.html") );
    } 
    catch ( Exception ex )
    {
   	 System.out.println(ex);
    }
}
else if(cmdText.equals("XML Editor"))  
{
	try {
		File file=File.createTempFile("temp", ".xml");
		String payload=Notepad.this.ta.getText();
		OutputStream out=new FileOutputStream(file);
		out.write(payload.getBytes());
		out.close();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		 dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new FileInputStream(file));
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out1 = new StringWriter();
		tf.transform(new DOMSource(doc), new StreamResult(out1));
		Notepad.this.ta.setText(out1.toString());
		Test t=new Test();
		t.textArea.setText(Notepad.this.ta.getText());
        file.deleteOnExit();
    } catch (Exception e) {
    	statusBar.setText("Wrong XML DATA");
    	int reply= JOptionPane.showConfirmDialog(f,"XML not in well form or untitled file ?\nDo you want to continue ?","Error",JOptionPane.YES_NO_OPTION,0);
      	if(reply==JOptionPane.YES_OPTION)
      	{
      		Test t=new Test();
      	}
    }
	
	
}
else if(cmdText.equals("XML Tree"))  
{
	try {
		File file=File.createTempFile("temp", ".xml");
		String payload=Notepad.this.ta.getText();
		OutputStream out=new FileOutputStream(file);
		out.write(payload.getBytes());
		out.close();
		DemoMain.setFileLocation(file.getAbsolutePath());
		DemoMain d=new DemoMain();
		d.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	      file.deleteOnExit();

	} catch (IOException e) {
		statusBar.setText("Wrong XML DATA");
		e.printStackTrace();
	}
}
else if(cmdText.equals(java))
{
	ta.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
	ta.setCodeFoldingEnabled(true);
	 JOptionPane messagePane = new JOptionPane(
	            "Loading suggestion list...\nDon't close this window... Please wait...",
	            JOptionPane.DEFAULT_OPTION);
	      final JDialog dialog = messagePane.createDialog(this, "Loading");

	      new SwingWorker<Void, Void>() {

	         @Override
	         protected Void doInBackground() throws Exception {
	        	 
	        	 CompletionProvider provider = createCompletionProvider("java");
	        		AutoCompletion ac = new AutoCompletion(provider);
	        		ac.install(ta);
	            Thread.sleep(3000); 

	            return null;
	         }
	         protected void done() {
	            dialog.dispose();
	         };
	      }.execute();

	      dialog.setVisible(true);

	      JOptionPane.showMessageDialog(this, "All done..",
	            "Done", JOptionPane.INFORMATION_MESSAGE);
	      JOptionPane.getRootFrame().dispose();
}

else if(cmdText.equals(HTML))
{
	ta.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
	ta.setCodeFoldingEnabled(true);
	ta.setAutoIndentEnabled(true);
	 JOptionPane messagePane = new JOptionPane(
	            "Loading suggestion list...\nDon't press close or OK button...\nPlease wait...",
	            JOptionPane.PLAIN_MESSAGE);
	    
	      final JDialog dialog = messagePane.createDialog(this, "Loading");
	      dialog.setVisible(false);

	      new SwingWorker<Void, Void>() {

	         @Override
	         protected Void doInBackground() throws Exception {
	        	 CompletionProvider provider = createCompletionProvider("html");
	        		AutoCompletion ac = new AutoCompletion(provider);
	        		ac.install(ta);
	            //Thread.sleep(3000); 

	            return null;
	         }
	         protected void done() {
	            dialog.dispose();
	         };
	      }.execute();

	      dialog.setVisible(true);

	      JOptionPane.showMessageDialog(this, "All done..",
	            "Done", JOptionPane.INFORMATION_MESSAGE);
	      JOptionPane.getRootFrame().dispose();
	    
}
else if(cmdText.equals(JSON))
{
	ta.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
	ta.setCodeFoldingEnabled(true);
}
else if(cmdText.equals(XML))  
{
	try {
		/*File file=File.createTempFile("temp", ".xml");
		String payload=Notepad.this.ta.getText();
		OutputStream out=new FileOutputStream(file);
		out.write(payload.getBytes());
		out.close();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		 dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new FileInputStream(file));
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out1 = new StringWriter();
		tf.transform(new DOMSource(doc), new StreamResult(out1));
		Notepad.this.ta.setText(out1.toString());
        file.deleteOnExit();*/
        ta.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        ta.setCodeFoldingEnabled(true);
    } catch (Exception e) {
    	JOptionPane.showMessageDialog(Notepad.this.f,"Not a Proper XML","Java",JOptionPane.ERROR_MESSAGE);
    }
}
else if(cmdText.equals(others))
{
	String input = (String) JOptionPane.showInputDialog(null, "Select Language...",
	        "The Choice of Languages", JOptionPane.QUESTION_MESSAGE, null, // Use
	                                                                        // default
	                                                                        // icon
	        langList, // Array of choices
	        langList[0]); // Initial choice
	System.out.println(input.split("/")[1]);
	ta.setSyntaxEditingStyle(input);
    ta.setCodeFoldingEnabled(true);
    //JOptionPane.showOptionDialog(null, "Hello","Empty?", JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
    //JOptionPane.showMessageDialog(f, "Loading......");
    
    JOptionPane messagePane = new JOptionPane(
            "Loading suggestion list...\nDon't press close or OK button...\nPlease wait...",
            JOptionPane.PLAIN_MESSAGE);
    
      final JDialog dialog = messagePane.createDialog(this, "Loading");
      dialog.setVisible(false);

      new SwingWorker<Void, Void>() {

         @Override
         protected Void doInBackground() throws Exception {
        	 System.out.println(input.split("/")[1]);
        	 CompletionProvider provider = createCompletionProvider(input.split("/")[1]);
        		AutoCompletion ac = new AutoCompletion(provider);
        		ac.install(ta);
            //Thread.sleep(3000); 

            return null;
         }
         protected void done() {
            dialog.dispose();
         };
      }.execute();

      dialog.setVisible(true);

      JOptionPane.showMessageDialog(this, "All done..",
            "Done", JOptionPane.INFORMATION_MESSAGE);
      JOptionPane.getRootFrame().dispose();
    
   
}
else
	statusBar.setText("This "+cmdText+" command is yet to be implemented");
}
/*************Constructor
 * @throws UnsupportedLookAndFeelException 
 * @throws IllegalAccessException 
 * @throws InstantiationException 
 * @throws ClassNotFoundException **************/
////////////////////////////////////
@SuppressWarnings("static-access")
public static void main(String[] s) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
{
//UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
	  
	try
	{
	 Splash s1=new Splash();
     s1.setVisible(true);
     Thread t=Thread.currentThread();
     t.sleep(10000);
    
     SwingUtilities.invokeLater(new Runnable(){
         public void run()
         {
             //opening the main application
        	// new Splash().setVisible(true);
         }
     });
     s1.dispose();
     UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
     Notepad tb = new Notepad();
     //LookAndFeelAction.setWindowsLookAndFeel();
     
	}
	catch(Exception e)
	
	{
		System.out.println(e);
	}
	
}



private CompletionProvider createCompletionProvider(String language) {
    // A DefaultCompletionProvider is the simplest concrete implementation
    // of CompletionProvider. This provider has no understanding of
    // language semantics. It simply checks the text entered up to the
    // caret position for a match against known completions. This is all
    // that is needed in the majority of cases.
    
	
    // Add completions for all Java keywords. A BasicCompletion is just
    // a straightforward word completion.
    if(language.equals("java"))
    {
    	provider.clear();
    for(int i=0;i<keyWord.length;i++)
    {
    provider.addCompletion(new BasicCompletion(provider, keyWord[i]));
    }
    // Add a couple of "shorthand" completions. These completions don't
    // require the input text to be the same thing as the replacement text.
    provider.addCompletion(new ShorthandCompletion(provider, "sysout",
          "System.out.println(", "System.out.println("));
    provider.addCompletion(new ShorthandCompletion(provider, "syserr",
          "System.err.println(", "System.err.println("));
  
    long startTime = System.nanoTime();
	
	try {
		
		
		/*public static void main(String[] args) {
			 
	        long startTime = System.nanoTime();
	        try (Scanner sc = new Scanner(new FileInputStream(getClass().getResource("/java.txt").getFile()), "UTF-8")) {
	            long freeMemoryBefore = Runtime.getRuntime().freeMemory();
	            while (sc.hasNextLine()) {
	                
	                String line = sc.nextLine();
	                provider.addCompletion(new ShorthandCompletion(provider, line.split("-")[0],
	    	    			line.split("-")[0], line.split("-")[1]));
	                // parse line
	                //System.out.println(line);
	            }
	            
	            // note that Scanner suppresses exceptions
	            if (sc.ioException() != null) {
	                sc.ioException().printStackTrace();
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	            
	        long endTime = System.nanoTime();
	        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
	        System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms"  );
	    }*/
		
		
		
		InputStream in = getClass().getResourceAsStream("/java.txt");
		    
		//Reader rdr = Channels.newReader(fis.getChannel(), "UTF-8");
    	BufferedReader bufferReaderInputPayload = null;
		String line = null;

		
			bufferReaderInputPayload = new BufferedReader(new InputStreamReader(in));
			
			while ((line = bufferReaderInputPayload.readLine()) != null) {
				 provider.addCompletion(new ShorthandCompletion(provider, line.split("-")[0],
						 line.split("-")[0], line.split("-")[1]));
			}
			JOptionPane.getRootFrame().dispose(); 
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		statusBar.setText(e.toString());
	}
	finally {
		long endTime = System.nanoTime();
        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
        statusBar.setText("Total elapsed time: " + elapsedTimeInMillis + " ms to import suggestion" );
        System.out.println("Total elapsed time: " + elapsedTimeInMillis + " ms"  );
	    //input.close();
	}
    
    
    /*ZipInputStream zip;
	try {
		String binPath=System.getenv("Path");
		String path[]=binPath.split(";");
		for(String a:path)
		{
			if(a.endsWith("/bin") &&a.contains("Java"))
			{
				binPath=a;
			}
		}
		File zipFile=new File("C:\\Program Files\\Java\\jdk-9.0.1\\lib\\src.zip");
		
		zip = new ZipInputStream(new FileInputStream(zipFile));
	FileOutputStream fos=new FileOutputStream(new File("D:\\javaClassNameList.txt"));
	StringBuilder sb=new StringBuilder();
    for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
        if (!entry.isDirectory() && entry.getName().endsWith(".java")) {
            // This ZipEntry represents a class. Now, what class does it represent?
            String className = entry.getName().replace('/', '.').replace(".java", ""); // including ".class"
            String name=className;
            name=name.substring(name.lastIndexOf('.')+1);
            sb.append("\n"+name+"  -  "+className);
            provider.addCompletion(new ShorthandCompletion(provider, name,
            		name, className));
        }
        
    }
    fos.write(sb.toString().getBytes());
    fos.close();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
    }
    else if(language.equals("html"))
    {
    	provider.clear();
    	
		try {
			
			InputStream in = getClass().getResourceAsStream("/html.txt");
	    	
	    	BufferedReader bufferReaderInputPayload = null;
			String line = null;

			
				bufferReaderInputPayload = new BufferedReader(new InputStreamReader(in));
				
				while ((line = bufferReaderInputPayload.readLine()) != null) {
					 provider.addCompletion(new ShorthandCompletion(provider, line.substring(0, line.indexOf(',')),
							 line.substring(0, line.indexOf(',')), line.substring( line.indexOf(','))));
				}
				JOptionPane.getRootFrame().dispose(); 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	 /*provider.addCompletion(new ShorthandCompletion(provider, "siva",
    	            "sivabharathi", "sivabharathi"));*/
    	
    }
    else if(language.equals("plain"))
    {
    	provider.clear();
    	
		try {
			
			InputStream in = getClass().getResourceAsStream("/english.txt");
	    	
	    	BufferedReader bufferReaderInputPayload = null;
			String line = null;

			
				bufferReaderInputPayload = new BufferedReader(new InputStreamReader(in));
				
				while ((line = bufferReaderInputPayload.readLine()) != null) {
					 provider.addCompletion(new ShorthandCompletion(provider, line,
			    	            line, line));
				}
				JOptionPane.getRootFrame().dispose(); 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	 /*provider.addCompletion(new ShorthandCompletion(provider, "siva",
    	            "sivabharathi", "sivabharathi"));*/
    	
    }
    else
    {
    	//provider.clear();
    }
	
    return provider;

 }
}
/**************************************/
//public
interface MenuConstants
{
final String fileText="File";
final String editText="Edit";
final String formatText="Format";
final String viewText="View";
final String langText="Language";
final String xmlText="XML";
final String cmdText="Command";
final String shareText="Share";
final String helpText="Help";

final String fileNew="New";
final String fileOpen="Open...";
final String fileSave="Save";
final String fileSaveAs="Save As...";
final String filePageSetup="Page Setup...";
final String filePrint="Print";
final String fileExit="Exit";

final String editUndo="Undo";
final String editCut="Cut";
final String editCopy="Copy";
final String editPaste="Paste";
final String editDelete="Delete";
final String editFind="Find...";
final String editFindNext="Find Next";
final String editReplace="Replace";
final String editGoTo="Go To...";
final String editSelectAll="Select All";
final String editTimeDate="Time/Date";

final String formatWordWrap="Word Wrap";
final String formatFont="Font...";
final String formatForeground="Set Text color...";
final String formatBackground="Set Pad color...";

final String viewStatusBar="Status Bar";

final String helpHelpTopic="Help Topic";
final String helpAboutNotepad="About Javapad";
final String java="Java";
final String XML="XML";
final String HTML="HTML";
final String JSON="JSON";
final String others="Others";
final String[] langList={"text/plain","text/actionscript","text/asm","text/bbcode","text/c","text/clojure","text/cpp","text/cs","text/css","text/d","text/dockerfile","text/dart","text/delphi","text/dtd","text/fortran","text/groovy","text/hosts","text/htaccess","text/html","text/ini","text/java","text/javascript","text/json","text/jshintrc","text/jsp","text/latex","text/less","text/lisp","text/lua","text/makefile","text/mxml","text/nsis","text/perl","text/php","text/properties","text/python","text/ruby","text/sas","text/scala","text/sql","text/tcl","text/typescript","text/unix","text/vb","text/bat","text/xml","text/yaml"};
final String[] keyWord={"abstract","assert","boolean","break","byte","case","catch","char","class","const","continue","default","double","do","else","enum","extends","false","final","finally","float","for","goto","if","implements","import","instanceof","int","interface","long","native","new","null","package","private","protected","public","return","short","static","strictfp","super","switch","synchronized","this","throw","throws","transient","true","try","void","volatile","while"};
final String aboutText=
	"<html><big>Your Javapad</big><hr><hr>"
	+"<p align=right>Prepared by a Siva!"
	+"<hr><p align=left>I Used jdk1.8 to compile the source code.<br><br>"
	+"<strong>Thanx 4 using Javapad</strong><br>"
	+"Ur Comments as well as bug reports r very welcome at<p align=center>"
	+"<hr><em><big>sivabharathik96@gmail.com</big></em><hr><html>";
}
