package lejos.internal.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ConfigManager {
	public static final String CONFIG_BTCACHE = "nxj.cache";
	public static final String CONFIG_NAVPANEL = "nav.props";
	public static final String CONFIG_CHARTINGLOGGER = "NXJChartingLogger.xml";
	
	public static final String DOM_OUTPUT_ENCODING = "UTF-8";
	
	public static File getPropFile(String name) {
		String userHome = System.getProperty("user.home");
		if (userHome == null)
			return null;
		
		return new File(userHome+File.separator+".config"+File.separator+"leJOS NXJ", name);
	}
	
	public static boolean loadPropFile(String name, Properties dst) throws IOException {
		File propsFile = getPropFile(name);
		if (propsFile == null || !propsFile.exists())
			return false;
		
		FileInputStream fis =new FileInputStream(propsFile);
		try {
			dst.load(fis);
		} finally {
			fis.close();
		}
		return true;
	}

	public static boolean savePropFile(String name, Properties src) throws IOException {
		File propsFile = getPropFile(name);
		if (propsFile == null)
			return false;
		
		propsFile.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(propsFile);
		try {
			src.store(out, "Automatic save");					
		} finally {
			out.close();
		}
		return true;
	}
	
	/**
	 * Open and parse a XML file and return its DOM.
	 * 
	 * @param filename The XML file to parse
	 * @return The DOM. null if file could not be parsed, opened, or error.
	 */
	public static Document getDOM(String filename) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
	    dbf.setValidating(false);
	    Document doc = null;	    
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} 
		
		OutputStreamWriter errorWriter;
		try {
			errorWriter = new OutputStreamWriter(System.err, DOM_OUTPUT_ENCODING);
			db.setErrorHandler(new SAXErrorHandler (new PrintWriter(errorWriter, true)));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}
		
		File theFile = getPropFile(filename);
		if (theFile==null) return null;
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
	 * Save the passed DOM to <code>filename</code>.
	 * 
	 * @param doc The valid DOM
	 * @param filename the XML filename
	 * @return true on success, false otherwise
	 */
	public static boolean saveDOM(Document doc, String filename) throws IOException {
		// Use a Transformer for output
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = null;
		try {
			transformer = tFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return false;
		}
		
		File theFile = getPropFile(filename);
		if (theFile==null || transformer==null) {
			return false;
		}
		theFile.getParentFile().mkdirs();
		
		FileOutputStream fos = new FileOutputStream(theFile);
	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(fos);
	    try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		} finally {
			fos.close();
		}

		return true;
	}
}
