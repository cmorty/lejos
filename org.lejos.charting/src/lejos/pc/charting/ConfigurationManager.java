package lejos.pc.charting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import lejos.internal.config.ConfigManager;
import lejos.internal.config.SAXErrorHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * manage config items.
 * 
 * @author Kirk P. Thompson
 *
 */
class ConfigurationManager {
	private static Document doc=null;
	//private static boolean dirty=false;
	
	final static String CONFIG_FILE_EXTENSION = "settings.user.fileExtension";
	final static String CONFIG_NXTNAME = "settings.user.NXTname";
	final static String CONFIG_FILE_PATH = "settings.user.filePath";
	final static String CONFIG_ADD_PLUGINS_PATH= "extensions.additionalPath";
	final static String CONFIG_WINDOWSTATE= "settings.user.windowState";
	
	static {
		getConfigDOM();
	}
	/**
	 * Open and parse the NXTChartimngLogger.xml file from /resource (or same package in JAR) and return its DOM.
	 * 
	 * @return The DOM. null if file could not be parsed, opened, or error.
	 */
	private static Document getDOM() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
	    dbf.setValidating(false);
	    Document doc = null;	    
		DocumentBuilder db = null;
		
		//ConfigManager.CONFIG_CHARTINGLOGGER
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} 
		
		OutputStreamWriter errorWriter;
		try {
			errorWriter = new OutputStreamWriter(System.err, ConfigManager.DOM_OUTPUT_ENCODING);
			db.setErrorHandler(new SAXErrorHandler (new PrintWriter(errorWriter, true)));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}
		
		InputStream theFile = dbf.getClass().getResourceAsStream("/lejos/pc/charting/" + ConfigManager.CONFIG_CHARTINGLOGGER);
//		File theFile = new File(filename);
		try {
			doc = db.parse(theFile);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return doc;
	}
	
	/**
	 * Find the named subnode in a node's sublist.
	 * <ul>
	 * <li>Ignores comments and processing instructions.
	 * <li>Ignores TEXT nodes (likely to exist and contain
	 *         ignorable whitespace, if not validating.
	 * <li>Ignores CDATA nodes and EntityRef nodes.
	 * <li>Examines element nodes to find one with
	 *        the specified name.
	 * </ul>
	 * @param name  the tag name for the element to find
	 * @param node  the element node to start searching from
	 * @return the Node found
	 */
	static Node findSubNode(String name, Node node) {
	    if (node.getNodeType() != Node.ELEMENT_NODE) {
	        
	        Node nextSibling = node.getNextSibling();
	        if (nextSibling==null) {
	        	System.err.println("ConfigurationManager Error: could not find element type" );
		        return null;
	        }
	        // recurse
	        return findSubNode(name, nextSibling);
	    }
	    if (! node.hasChildNodes()) return null;

	    NodeList list = node.getChildNodes();
	    for (int i=0; i < list.getLength(); i++) {
	        Node subnode = list.item(i);
	        if (subnode.getNodeType() == Node.ELEMENT_NODE) {
	           if (subnode.getNodeName().equals(name)) 
	               return subnode;
	        }
	    }
	    return null;
	}
	
	/**
	 * Load the NXJChartingLogger.xml into static doc 
	 * @return the DOM doc
	 */
	static Document getConfigDOM(){
		if (doc != null) return doc;
		
		try {
			doc = ConfigManager.loadDOM(ConfigManager.CONFIG_CHARTINGLOGGER);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		
		// if no XML, copy the one in /resource into user area defined by ConfigManager
		if (doc==null) {
			System.out.println("ConfigurationManager: creating " + ConfigManager.CONFIG_CHARTINGLOGGER);
			//doc = getDOM("./resource/" + ConfigManager.CONFIG_CHARTINGLOGGER); 
			doc = getDOM();
			if (!saveConfig()) {
				System.out.println("getConfigDOM(): failed to save config file");
			}
			
		}
		
		return doc;
	}
	
	/** persit the DOM back to the filesystem
	 * @return true if saved, false if problem
	 */
	static boolean saveConfig(){
		if (doc==null) return false;
		boolean saved=true;
		// save to user area
		try {
			ConfigManager.saveDOM(doc, ConfigManager.CONFIG_CHARTINGLOGGER);
		} catch (IOException e) {
			e.printStackTrace();
			saved=false;
		} catch (TransformerException e) {
			e.printStackTrace();
			saved=false;
		}
		return saved;
	}
	
	private static Node getElementTextNode(String itemPath){
		String[] elements =  itemPath.split("\\.");
		if (elements.length==0) return null;
		
		Node workingNode = findSubNode(elements[0], doc.getFirstChild());
		Node previousNode = workingNode;
		for (int i=1;i<elements.length;i++){
			workingNode = findSubNode(elements[i], workingNode);
			if (workingNode==null){
				System.out.println("<getElementNode> working is null. creating element \"" + elements[i] + "\"");
//				return null;
				Element we = doc.createElement(elements[i]);
				workingNode = we;
				previousNode.appendChild(we);
			}
			previousNode = workingNode;
//			System.out.println(workingNode.getNodeName() + ": " + workingNode.getNodeValue());
		}
		if (!workingNode.hasChildNodes()) {
//			return null;
			Text wt = doc.createTextNode("");
			workingNode.appendChild(wt);
			System.out.println("added text node");
		}
		return workingNode.getChildNodes().item(0);
	}
	
	/**
	 * Get a text node value from the DOM via x.y.z element path structure
	 * 
	 * @param itemPath x.y.z element hierarchy path structure
	 * @param defaultIfNull thsi value will be returned if text node is null 
	 * @return the text node value
	 */
	static String getConfigItem(String itemPath, String defaultIfNull) {
		Node workingNode = getElementTextNode(itemPath);
		if (workingNode==null) return defaultIfNull;
//		System.out.println("name=" + workingNode.getNodeName());
		String nodeVal = workingNode.getNodeValue();
		if (nodeVal.equals("")) return defaultIfNull;
		return nodeVal;
	}
	
	/**
	 * Set a text node value. If the itemPath doesn't exist, it will be created.
	 * 
	 * @param itemPath x.y.z element hierarchy path structure
	 * @param textValue the value to save as the text node
	 * @return true if sucessfull, False node doesn't exist and could not be auto-created
	 */
	static boolean setConfigItem(String itemPath, String textValue) {
		Node workingNode = getElementTextNode(itemPath);
		if (workingNode==null) return false;
//		System.out.println("setConfigItem: " + itemPath);
		workingNode.setNodeValue(textValue);
		return true;
	}

	
}
