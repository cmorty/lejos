package javax.xml.stream;

import java.io.InputStream;

public class XMLInputFactory {
	public static XMLInputFactory newInstance() {
		return new XMLInputFactory();
	}
	
	public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
		return new XMLStreamReader(stream);
	}
}
