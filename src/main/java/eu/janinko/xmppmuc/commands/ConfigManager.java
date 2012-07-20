package eu.janinko.xmppmuc.commands;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigManager {
	Document doc;
	String path;
	
	public ConfigManager(String path) {
		this.path = path;
		try {
			File fXmlFile = new File(path);
			doc = DocumentBuilderFactory
			                    .newInstance()
			                    .newDocumentBuilder()
			                    .parse(fXmlFile);
			doc.getDocumentElement().normalize();
		} catch (SAXException e) {
			System.err.println("ConfigManager.ConfigManager() A");
			e.printStackTrace();
			doc = null;
		} catch (IOException e) {
			System.err.println("ConfigManager.ConfigManager() B");
			e.printStackTrace();
			doc = null;
		} catch (ParserConfigurationException e) {
			System.err.println("ConfigManager.ConfigManager() C");
			e.printStackTrace();
			doc = null;
		}
	}

	public Document getDocument() {
		return doc;
	}
	
	public Map<String, String> getConfig(String attribute){
		HashMap<String, String> ret = new HashMap<String, String>();
		
		Node documentNode = doc.getFirstChild();
		NodeList documentNodes = documentNode.getChildNodes();
		for(int i=0; i< documentNodes.getLength(); i++){
			Node node = documentNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element) node;
				String key = element.getAttribute(attribute);
				String value = node.getTextContent();
				ret.put(key, value);
			}
		}
		return ret;
	}
	
	public void setConfig(String attribute, String key, String value){
		Node keyNode = null;
		
		Node documentNode = doc.getFirstChild();
		NodeList documentNodes = documentNode.getChildNodes();
		
		
		for(int i=0; i< documentNodes.getLength(); i++){
			Node node = documentNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element) node;
				if(element.getAttribute(attribute).equals(key)){
					keyNode = node;
					break;
				}
			}
		}
		if(keyNode != null){
			keyNode.setTextContent(value);
		}else{
			Element element = doc.createElement("user");
			element.setAttribute(attribute,key);
			element.appendChild(doc.createTextNode(value));
			documentNode.appendChild(element);
		}
		try {
			Transformer transformer = TransformerFactory
			 										.newInstance()
			 										.newTransformer();
	     DOMSource source = new DOMSource(doc);
	     StreamResult result =  new StreamResult(new File(path));
	     transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			System.err.println("ConfigManager.setConfig() A");
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			System.err.println("ConfigManager.setConfig() B");
			e.printStackTrace();
		} catch (TransformerException e) {
			System.err.println("ConfigManager.setConfig() C");
			e.printStackTrace();
		}
	}
	
	public void removeConfig(String attribute, String key){

		Node documentNode = doc.getFirstChild();
		NodeList documentNodes = documentNode.getChildNodes();
		
		
		for(int i=0; i< documentNodes.getLength(); i++){
			Node node = documentNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element) node;
				if(element.getAttribute(attribute).equals(key)){
					documentNode.removeChild(node);
					break;
				}
			}
		}
		try {
			Transformer transformer = TransformerFactory
			 										.newInstance()
			 										.newTransformer();
	     DOMSource source = new DOMSource(doc);
	     StreamResult result =  new StreamResult(new File(path));
	     transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			System.err.println("ConfigManager.removeConfig() A");
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			System.err.println("ConfigManager.removeConfig() B");
			e.printStackTrace();
		} catch (TransformerException e) {
			System.err.println("ConfigManager.removeConfig() C");
			e.printStackTrace();
		}
	}
}
