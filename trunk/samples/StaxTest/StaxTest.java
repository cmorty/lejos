import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import lejos.nxt.Button;

/**
 * Test of parsing XML using javax.xml.stream.
 * The file produced by the FileTest sample is parsed.
 * 
 * @author Lawrie Griffiths
 *
 */
public class StaxTest {
	public static void main(String[] args) throws Exception {
		File f = new File("route.kml");
		FileInputStream in = new FileInputStream(f);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser = factory.createXMLStreamReader(in);
		int numAttrs;
		
		while (true) {
		    int event = parser.next();
		    if (event == XMLStreamConstants.END_DOCUMENT) {
		       parser.close();
		       break;
		    }
		    if (event == XMLStreamConstants.START_ELEMENT) {
		    	numAttrs = parser.getAttributeCount();
		        System.out.println(parser.getLocalName());
		    	for(int i=0;i<numAttrs;i++) { 
		    		System.out.println("Attribute(" + parser.getAttributeType(i) + "):" + parser.getAttributeLocalName(i) + "=" + parser.getAttributeValue(i));
		    	}
		    }
		}
		Button.waitForPress();
	}
}
