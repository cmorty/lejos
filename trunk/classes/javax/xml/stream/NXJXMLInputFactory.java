package javax.xml.stream;

import java.io.InputStream;

/**
 * A leJOS NXJ implementation of XMLInputFactory. 
 * Creates instances of NXJXMLStreamReader.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NXJXMLInputFactory extends XMLInputFactory  {
	
	public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
		return new NXJXMLStreamReader(stream);
	}
	
	public XMLStreamReader createStreamReader(XMLStreamReader reader, StreamFilter filter) throws XMLStreamException {
		NXJXMLStreamReader r = new NXJXMLStreamReader(((NXJXMLStreamReader) reader).getInputStream());
		r.setFilter(filter);
		return r;
	}
}
