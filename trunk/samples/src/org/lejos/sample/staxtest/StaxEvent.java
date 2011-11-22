package org.lejos.sample.staxtest;
import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class StaxEvent {
	public static void main(String[] args) throws Exception {
		File f = new File("route.kml");
		FileInputStream in = new FileInputStream(f);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader reader = inputFactory.createXMLEventReader(in);
		try {
			while (reader.hasNext()) {
		        XMLEvent e = reader.nextEvent();
		        if (e.isCharacters() && ((Characters) e).isWhiteSpace())
		              continue;
		        
		        System.out.println(e);
			}
		} finally {
			reader.close();
		}
	}
}
