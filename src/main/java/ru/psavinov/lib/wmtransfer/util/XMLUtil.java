package ru.psavinov.lib.wmtransfer.util;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {
	
	public static String getTagValue(Document doc, String tagName) {
		NodeList nodeList = doc.getElementsByTagName(tagName);
		if (nodeList.getLength() == 1) {
			return nodeList.item(0).getTextContent();
		}
		return "";
	}

	public static String XMLToString(Document doc) throws TransformerException{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		return writer.toString();
	}
	
	public static String getAttribute(Node n, String attrName) {
		if (n.getAttributes().getNamedItem(attrName) != null) {
			return n.getAttributes().getNamedItem(attrName).getNodeValue();
		}
		return "";
	}
}
