package lejos.internal.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	
	public static File getConfigFile(String name) {
		String userHome = System.getProperty("user.home");
		if (userHome == null)
			return null;
		
		return new File(userHome+File.separator+".config"+File.separator+"leJOS NXJ", name);
	}
	
	public static FileInputStream openConfigInputStream(String name) throws FileNotFoundException {
		File f = getConfigFile(name);
		if (f == null || !f.exists())
			return null;
		
		return new FileInputStream(f);
	}
	
	public static FileOutputStream openConfigOutputStream(String name) throws FileNotFoundException {
		File f = getConfigFile(name);
		if (f == null)
			return null;
		
		f.getParentFile().mkdirs();
		return new FileOutputStream(f);
	}
	
	public static boolean loadPropertiesFile(String name, Properties dst) throws IOException {
		FileInputStream fis = openConfigInputStream(name);
		if (fis == null)
			return false;		
		try {
			dst.load(fis);
		} finally {
			fis.close();
		}
		return true;
	}

	public static boolean savePropertiesFile(String name, Properties src) throws IOException {
		FileOutputStream fos = openConfigOutputStream(name);
		if (fos == null)
			return false;
		try {
			src.store(fos, "Automatic save");					
		} finally {
			fos.close();
		}
		return true;
	}
	
	/**
	 * Open and parse a XML file and return its DOM.
	 * 
	 * @param filename The XML file to parse
	 * @return The DOM. null if file could not be parsed, opened, or error.
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static Document loadDOM(String filename) throws IOException, SAXException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
	    dbf.setValidating(false);
		
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		try {
			OutputStreamWriter errorWriter = new OutputStreamWriter(System.err, DOM_OUTPUT_ENCODING);
			db.setErrorHandler(new SAXErrorHandler (new PrintWriter(errorWriter, true)));
		} catch (UnsupportedEncodingException e1) {
			// this should never happen, as encoding should be a valid constant
			throw new RuntimeException("internal error", e1);
		}
		
	    Document doc;	    
		FileInputStream fis = openConfigInputStream(filename);
		if (fis==null)
			return null;		
		try {
			doc = db.parse(fis);
		} finally {
			fis.close();
		}
		
		return doc;
	}
	
	/**
	 * Save the passed DOM to <code>filename</code>.
	 * 
	 * @param doc The valid DOM
	 * @param filename the XML filename
	 * @return true on success, false otherwise
	 * @throws TransformerException 
	 */
	public static boolean saveDOM(Document doc, String filename) throws IOException, TransformerException {
		// Use a Transformer for output
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer();
	    
		FileOutputStream fos = openConfigOutputStream(filename);
		if (fos == null)
			return false;
	    try {
		    DOMSource source = new DOMSource(doc);
		    StreamResult result = new StreamResult(fos);
			transformer.transform(source, result);
		} finally {
			fos.close();
		}
		return true;
	}
}
