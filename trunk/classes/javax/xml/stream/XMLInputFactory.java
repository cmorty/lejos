package javax.xml.stream;

import java.io.InputStream;

public abstract class XMLInputFactory {
	public static XMLInputFactory newInstance() {
		return new NXJXMLInputFactory();
	}
	
	public abstract XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException;
	
	public abstract XMLStreamReader createStreamReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException;
}
