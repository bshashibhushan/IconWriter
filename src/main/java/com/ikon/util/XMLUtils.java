package com.ikon.util;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ikon.core.Config;

public class XMLUtils {
	
	private DocumentBuilderFactory docFactory = null;
	private DocumentBuilder docBuilder = null;
	private Document doc = null;
	private File XMLFile = null;
	private Node propertyGroupsNode = null;
	private static final String PROPERTY_GROUP_ELEMENT = "property-group";
	
	/**
	 * Initialize an XML document for the given file and sets the doc object
	 * @param XMLFile
	 * @throws Exception
	 */
	public XMLUtils(File XMLFile) throws Exception{
		this.XMLFile = XMLFile;
		docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setIgnoringElementContentWhitespace(true);
		docBuilder = docFactory.newDocumentBuilder();
		doc = docBuilder.parse(XMLFile);
		doc.normalize();		
		propertyGroupsNode = doc.getDocumentElement();
	}
	
	/**
	 * This is the main method which creates the property Group along with its children. This sets all the
	 * attributes for the property group as well as its children. Since, the children attributes differs,
	 * first the children are checked whether they contain checkbox or textarea before proceeding. Finally, it builds the 
	 * document.
	 * @param isEdit if it has to be edited
	 * @throws Exception
	 */
	public void addPropertyGroup(String propertyGroupLabel, Map<String, String> propertiesMap) throws Exception {		
		String propertyGroupName = propertyGroupLabel.toLowerCase().replace(" ", "");
		
		Element propertyGroupElement = doc.createElement(PROPERTY_GROUP_ELEMENT);
		propertyGroupElement.setAttribute("label", propertyGroupLabel);
		propertyGroupElement.setAttribute("name", "okg:" + propertyGroupName);
		propertyGroupElement.setAttribute("readonly", "false");
		propertyGroupElement.setAttribute("visible", "true");
		
		for(Entry<String, String> entry : propertiesMap.entrySet()){
			String propertyIndexType = entry.getValue();
			String propertyIndexLabel = entry.getKey();
			String propertyIndexName = propertyIndexLabel.toLowerCase().replace(" ", "");
			
			if(propertyIndexType.equals("checkbox") || propertyIndexType.equals("textarea")){
				Element propertyGroupElementChild = doc.createElement(propertyIndexType);
				propertyGroupElementChild.setAttribute("label", propertyIndexLabel);
				propertyGroupElementChild.setAttribute("name", "okp:" + propertyGroupName + "." + propertyIndexName);
				propertyGroupElement.appendChild(propertyGroupElementChild);
			} else {
				Element propertyGroupElementChild = doc.createElement("input");
				propertyGroupElementChild.setAttribute("label", propertyIndexLabel);
				propertyGroupElementChild.setAttribute("name", "okp:" + propertyGroupName + "." + propertyIndexName);
				propertyGroupElementChild.setAttribute("readonly", "false");
				propertyGroupElementChild.setAttribute("type", propertyIndexType);
				propertyGroupElement.appendChild(propertyGroupElementChild);
			}				
		}
		
		propertyGroupsNode.appendChild(propertyGroupElement);
		buildXMLDocument();
		
	}
	
	/**
	 * For editing the propertyGroup. It basically deletes the existing one and adds the new one.
	 */
	public void editPropertyGroup(String propertyGroupLabel, Map<String, String> propertiesMap) throws Exception {
		String propertyGroupName = propertyGroupLabel.toLowerCase().replace(" ", "");
		
		String xPath = "/property-groups/property-group[@name='okg:" + propertyGroupName + "'][1]";
		deleteNode(xPath);
		
		addPropertyGroup(propertyGroupLabel, propertiesMap);
	}
	
	/**
	 * For editing the propertyGroup. It basically deletes the existing one and adds the new one.
	 */
	public void deletePropertyGroup(String propertyGroupLabel) throws Exception {
		String propertyGroupName = propertyGroupLabel.toLowerCase().replace(" ", "");
		
		String xPath = "/property-groups/property-group[@name='okg:" + propertyGroupName + "'][1]";
		deleteNode(xPath);
		
		buildXMLDocument();
		
	}
	
	/**
	 * Deletes the entire node from the xml.
	 * @throws Exception
	 */
	private void deleteNode(String xPath) throws Exception{
		getXMLNodePath(xPath).getParentNode().removeChild(getXMLNodePath(xPath));
	}

	/**
	 * It uses XPath to obtain the specific node and then returns that node. This is a general method which
	 * can be used to obtain any node.
	 * @return the specific node.
	 * @throws Exception
	 */
	private Node getXMLNodePath(String path)  throws Exception{
		XPathExpression xp = XPathFactory.newInstance().newXPath().compile(path);
		NodeList nodes = (NodeList) xp.evaluate(doc, XPathConstants.NODESET);
		
		return nodes.item(0);
	}
	
	/**
	 * Final step for building the XMLDocument. It contains the basic building functions which even creates the XML DOCTYPE.
	 * @throws Exception
	 */
	private void buildXMLDocument() throws Exception{
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//openkm//DTD Property Groups 2.0//EN");
    	transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, Config.HOME_DIR+"/"+"property-groups-2.1.dtd");
		StreamResult result = new StreamResult(XMLFile);
		transformer.transform(source, result);
	}


	public boolean isPGXMLEmpty() throws Exception {
		return propertyGroupsNode.hasChildNodes()?false:true;
	}
}
