package XMLTree;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLTreeNode {
	Element element;
	public XMLTreeNode(Element element) {
		this.element = element;
	}
	public Element getElement() {
		return element;
	}
	public String toString() {
		
		String name= element.getNodeName();
		String value=element.getTextContent();
		String result=name;
		if(!element.hasChildNodes())
		{
		if(value.trim().length()>0)
		{
			result=result+" = "+value;
		}
		}
		return result;
	}
	public String getText() {
		NodeList list = element.getChildNodes();
		for (int i=0 ; i<list.getLength() ; i++) {
			if (list.item(i) instanceof Text) {
				return ((Text)list.item(i)).getTextContent();
			}
		}
		return "";
	}
}
