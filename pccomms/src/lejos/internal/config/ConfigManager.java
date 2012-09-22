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
import javax.xml.transform.OutputKeys;
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
	
	/**
	 * Returns the path (in form of a File object) of the config file specified
	 * or <code>null</code> if the folder for config files cannot be determined.
	 */
	public static File getConfigFile(String name) {
		String userHome = System.getProperty("user.home");
		if (userHome == null)
			return null;
		
		return new File(userHome+File.separator+".config"+File.separator+"leJOS NXJ", name);
	}

	/**
	 * Opens a config file and returns a fileinputstream or returns <code>null</code> if the config
	 * file does not exist.
	 * @throws FileNotFoundException if the file cannot be opened for reading
	 */
	public static FileInputStream openConfigInputStream(String name) throws FileNotFoundException {
		File f = getConfigFile(name);
		if (f == null || !f.exists())
			return null;
		
		return new FileInputStream(f);
	}
	
	/**
	 * Opens a config file for writing and returns a fileoutputstream.
	 * The parent folder of the config file and the config file itself are created if necessary.
	 * The method returns <code>null</code> if the folder for config files cannot be determined.
	 * @throws FileNotFoundException if the file cannot be opened for writing
	 */
	public static FileOutputStream openConfigOutputStream(String name) throws FileNotFoundException {
		File f = getConfigFile(name);
		if (f == null)
			return null;
		
		File p = f.getParentFile();
		if (!p.isDirectory() && !p.mkdirs())
			throw new FileNotFoundException("unable to create directory "+p);
		
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
	 * @return The DOM or <code>null</code> if file does not exist.
	 * @throws IOException on error
	 * @throws SAXException on error
	 * @throws ParserConfigurationException on error 
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
	 * @return true on success, false if the folder for config files cannot be determined.
	 * @throws IOException on error
	 * @throws TransformerException on error 
	 */
	public static boolean saveDOM(Document doc, String filename) throws IOException, TransformerException {
		// Use a Transformer for output
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    //transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	    
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
