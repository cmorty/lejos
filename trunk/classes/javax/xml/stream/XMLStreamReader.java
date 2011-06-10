package javax.xml.stream;

import javax.xml.namespace.QName;

public interface XMLStreamReader extends XMLStreamConstants {

	public int next() throws XMLStreamException;
	
	public boolean hasNext() throws XMLStreamException;
	
	public void close() throws XMLStreamException;
	
	public String getLocalName();
	
	public String getText();
	
	public int getAttributeCount();
	
	public String getAttributeLocalName(int index);
	
	public String getAttributeValue(String namespaceURI, String localName);
	
	public String getAttributeValue(int index);
	
	public String getAttributeType(int index);
	
	public int getEventType();
	
	public int getTextLength();
	
	public char[] getTextCharacters();
	
	public int nextTag() throws XMLStreamException;
	
	public boolean hasText();
	
	public Location getLocation();
	
	public String getElementText() throws XMLStreamException;
	
	public boolean isWhiteSpace();
	
	public boolean isStartElement();
	
	public boolean isEndElement();
	
	public boolean isCharacters();
	
	public int getTextStart();
	
	public void require(int type, String namespaceURI, String localName) throws XMLStreamException;
	
	public String getAttributeNamespace(int index);
	
	public Object getProperty(String name);
	
	public String getAttributePrefix(int index);
	
	public QName getAttributeName(int index);
	
	public String getCharacterEncodingScheme();
	
	public String getEncoding();
	
	public QName getName();
	
	public int getNamespaceCount();
	
	public String getNamespacePrefix();
	
	public String getNamespaceURI();
	
	public String getNamespaceURI(int index);
	
	public String getNamespaceURI(String prefix);
	
	public String gePIData();
	
	public String getPITarget();
}
