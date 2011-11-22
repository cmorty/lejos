package org.lejos.sample.staxtest;
import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.Location;
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
		
    	System.out.println("Version = " + parser.getVersion());
    	System.out.println("Character Encoding = " + parser.getCharacterEncodingScheme());
    	System.out.println("Encoding = " + parser.getEncoding());
    	
		while (true) {
		    int event = parser.next();
		    if (event == XMLStreamConstants.END_DOCUMENT) {;
		       parser.close();
		       break;
		    } else if (event == XMLStreamConstants.COMMENT) {
		    	System.out.print("Comment:" + parser.getText() + " at ");
		    	Location l = parser.getLocation();
		    	System.out.println("line " + l.getLineNumber() + ", column " + l.getColumnNumber());
		    } else if (event == XMLStreamConstants.START_ELEMENT) {
		    	int numAttrs = parser.getAttributeCount();
		        System.out.print(parser.getLocalName() + " ");
		    	for(int i=0;i<numAttrs;i++) { 
		    		System.out.print(parser.getAttributeLocalName(i) + "=\"" + parser.getAttributeValue(i) + "\" ");
		    	}
		    	System.out.println();
		    } else if (event == XMLStreamConstants.END_ELEMENT) {
		    	System.out.println("/" + parser.getLocalName());
		    }
		}
		Button.waitForAnyPress();
	}
}
