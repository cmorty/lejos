package javax.xml.stream;

import java.io.InputStream;

public class XMLInputFactory {
	public static XMLInputFactory newInstance() {
		return new XMLInputFactory();
	}
	
	public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
		return new XMLStreamReader(stream);
	}
	
	public XMLStreamReader createStreamReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
		XMLStreamReader r = new XMLStreamReader(reader.getInputStream());
		r.setFilter(filter);
		return r;
	}
}
