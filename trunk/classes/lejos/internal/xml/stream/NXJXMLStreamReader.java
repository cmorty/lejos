package lejos.internal.xml.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


/**
 * Simple subset implementation of Stax parser.
 * Does not deal with namespaces.
 * QName not fully supported.
 * Has very little error checking.
 * Embedded CDATA not supported.
 * Only supports attributes of type CDATA.
 * Never gives SPACE events. White space is mainly thrown away.
 * Does not support DTDs.
 * XML event streams are not supported (only the cursor API).
 * Event allocators and consumers not supported.
 * XML event classes not supported.
 * Only ascii encoding supported.
 * Processing instructions not supported.
 * Properties are not supported.
 * Filter semantics are not correct.
 * XML writing is not supported.
 * Resolving not supported.
 * Reporting not supported.
 * getVersion not supported.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NXJXMLStreamReader implements XMLStreamReader {
	private InputStream in;
	private boolean eof = false;
	private String localName;
	private int event;
	private Hashtable<String, String> attributes = new Hashtable<String,String>();
	private ArrayList<String> attrNames = new ArrayList<String>();
	private ArrayList<String> attrValues = new ArrayList<String>();
	private boolean started = false , ended = false;
	private String text;
	private int numAttributes = 0;
	private char c;
	private int line = 0, pos = 0, column = 0;
	private char quote;
	private String attrName,attrValue;
	private boolean quoted = false;
	private String version;
	private StreamFilter filter = null;
	
	public NXJXMLStreamReader(InputStream stream) {
		in = stream;
	}
	
	private char getChar() {
		try {
			int b = in.read();
			if (b < 0) {
				eof = true;
				return ' ';
			}
			pos++;
			column++;
			char ch = (char) b;
			if (ch == '\r') ch = ' ';
			if (ch == '\t') ch = ' ';
			if (ch == '\n') {
				line++;
				column = 0;
				ch = ' ';
			}	  
			return ch;
		} catch (IOException e) {
			eof = true;
			return ' ';
		}
	}
	
	/**
	 * Read the next token from the input stream and return the corresponding event.
	 * 
	 * @return the event
	 */
	public int next() throws XMLStreamException {
		if (filter == null) return unfilteredNext();
		
		while(true) {
			int e = unfilteredNext();
			if (filter.accept(this)) return e;
		}
	}
	
	/**
	 * Read the next token from the input stream and return the corresponding event.
	 * 
	 * @return the event
	 */
	private int unfilteredNext() throws XMLStreamException {
		StringBuffer s = new StringBuffer();
		text = null;
		localName = null;
		
		// Generate START_DOCUMENT event at the start of the document
		if (!started) {
			started = true;
			c = getChar();
			event = START_DOCUMENT;
			return event;
		}
		
		// Generate END_DOCUMENT event at the end of the document
		if (eof) {
			if (!ended) {
				ended = true;
				event = END_DOCUMENT;
				return event;
			}
			throw new XMLStreamException("Read past end of document");
		}
		
		// Empty tag (start + end)
		if (c == '>') {
			event = END_ELEMENT;
			c = getChar();
			return event;
		}

		// Tag
		if (c == '<') {
			attributes = new Hashtable<String,String>();
			attrNames = new ArrayList<String>();
			attrValues = new ArrayList<String>();
			numAttributes = 0;
			event = START_ELEMENT;
			
			if ((c = getChar()) == '/') { // end tag
				event=END_ELEMENT;
				c = getChar();
			}
			
			if (c == '?') { // ?xml tag
				while (!eof && (c = getChar()) != '<'); // Skip <?xml
				c = getChar();
			}
			
			if (c == '!') { // Comment
				getChar();getChar(); //Skip --
				while (!eof && (c = getChar()) != '>') {
					s.append(c);
				}
				s.delete(s.length()-2,s.length()); // remove --
				text = s.toString();
				c = getChar();
				event = COMMENT;
				
				return event;
			}
			
			while (!eof) {
				if ((c == '>' || c == '/') && !quoted) {
					c = getChar();
					break;
				}
				if (c == ' ' && !quoted) {
					if (numAttributes == 0) {
						localName = s.toString();
						s = new StringBuffer();
					}
				} else if (c == '=' && !quoted) {
					attrName = s.toString();
					s = new StringBuffer();
					quote = getChar();
					quoted = true;
				} else if (c == quote) {
					quoted = false;
					attrValue = s.toString();
					s = new StringBuffer();
					if (!attrName.equals("xmlns")) {
						attributes.put(attrName, attrValue);
						attrNames.add(attrName);
						attrValues.add(attrValue);
						numAttributes++;
					}
				} else {
					s.append(c);
				}
				c = getChar();
			}
			
			if (numAttributes == 0) localName = s.toString();
			return event;
		} 		
		
		// Must be text
		while (!eof) {
			if (c == '<') break;
			s.append(c);
			c = getChar();
		}
		text = s.toString();
		event = CHARACTERS;
		
		return event;
	}
	
	/**
	 * Check if there are more tokens in the stream
	 * 
	 * @return true iff there are more tokens
	 */
	public boolean hasNext() throws XMLStreamException {
		return !eof;
	}
	
	/**
	 * Close the stream.
	 */
	public void close() throws XMLStreamException {
		// Does nothing
	}
	
	/**
	 * Get the local name for the tag.
	 * Only applies if the current event is START_ELEMENT or END_ELEMENT.
	 * 
	 * @return the local name
	 */
	public String getLocalName() {
		if (event != START_ELEMENT && event != END_ELEMENT) throw new IllegalStateException();
		return localName;
	}
	
	/**
	 * Get the text for the contents of a tag.
	 * Only applies if the current event is CHARACTERS or COMMENT
	 * 
	 * @return the text
	 */
	public String getText() {
		if (event != CHARACTERS && event != COMMENT) throw new IllegalStateException();
		return text;
	}
	
	/**
	 * Get the attribute count for the current element
	 * 
	 * @return the attribute count
	 */
	public int getAttributeCount() {
		if (event != START_ELEMENT) throw new IllegalStateException();
		return numAttributes;
	}
	
	/**
	 * Get the local name of an attribute
	 * 
	 * @param index the attribute index
	 * @return the local name
	 */
	public String getAttributeLocalName(int index) {
		if (event != START_ELEMENT) throw new IllegalStateException();
		return attrNames.get(index);
	}
	
	/**
	 * Look up an attribute value using its name
	 * 
	 * @param namespaceURI not used
	 * @param localName the name of the attribute
	 * @return the attribute value
	 */
	public String getAttributeValue(String namespaceURI, String localName) {
		if (event != START_ELEMENT) throw new IllegalStateException();
		return attributes.get(localName);
	}
	
	/**
	 * Get the value of an attribute
	 * 
	 * @param index the index of the attribute
	 * @return the attribute value
	 */
	public String getAttributeValue(int index) {
		if (event != START_ELEMENT) throw new IllegalStateException();
		return attrValues.get(index);
	}
	
	/**
	 * Get the type of an attribute. Current only CDATA attributes are supported.
	 * 
	 * @param index the index of the attribute
	 * @return the attribute type
	 */
	public String getAttributeType(int index) {
		if (event != START_ELEMENT) throw new IllegalStateException();
		return "CDATA";
	}
	
	/**
	 * Get the current event
	 * 
	 * @return the event type
	 */
	public int getEventType(){
		return event;
	}

	/**
	 * Get the length of the text
	 * 
	 * @return the text length
	 */
	public int getTextLength() {
		if (event != CHARACTERS && event != COMMENT) throw new IllegalStateException();
		return text.length();
	}
	
	/**
	 * Get the text as a character array
	 * 
	 * @return the text as a character array
	 */
	public char[] getTextCharacters() {
		if (event != CHARACTERS && event != COMMENT) throw new IllegalStateException();
		return text.toCharArray();
	}
	
	/**
	 * Get the next tag
	 * 
	 * @return the tag event
	 */
	public int nextTag() throws XMLStreamException {
		while (true) {
			int e = next();
			if (e == START_ELEMENT || e == END_ELEMENT) return e;
		}
	}
	
	/**
	 * Test if current element has text
	 * 
	 * @return true iff the current element has text
	 */
	public boolean hasText() {
		return (text != null && text.length() > 0);
	}
	
	/**
	 * Get the location in the document
	 * 
	 * @return the location in the document
	 */
	public Location getLocation() {
		return new NXJLocation(line, column, pos);
	}
	
	/**
	 * Get the full text for an element, omitting comments
	 * 
	 * @return the text
	 * @throws XMLStreamException
	 */
	public String getElementText() throws XMLStreamException {
		if(getEventType() != XMLStreamConstants.START_ELEMENT) {
			throw new XMLStreamException(
					"parser must be on START_ELEMENT to read next text", getLocation());
		}
		int eventType = next();
		StringBuffer buf = new StringBuffer();
		while(eventType != XMLStreamConstants.END_ELEMENT ) {
			if(eventType == XMLStreamConstants.CHARACTERS
				 || eventType == XMLStreamConstants.CDATA
				 || eventType == XMLStreamConstants.SPACE
				 || eventType == XMLStreamConstants.ENTITY_REFERENCE) {
				buf.append(getText());
			} else if(eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
				 || eventType == XMLStreamConstants.COMMENT) {
				// skipping
			} else if(eventType == XMLStreamConstants.END_DOCUMENT) {
			 throw new XMLStreamException(
					 "unexpected end of document when reading element text content");
			} else if(eventType == XMLStreamConstants.START_ELEMENT) {
			 throw new XMLStreamException(
					 "element text content may not contain START_ELEMENT", getLocation());
			} else {
			 throw new XMLStreamException(
					 "Unexpected event type "+eventType, getLocation());
			}
			eventType = next();
		}
		return buf.toString();
    }
	
	/**
	 * Test whether the current event is white space
	 * 
	 * @return true iff on white space
	 */
	public boolean isWhiteSpace() {
		return (c == ' ');
	}
	
	/**
	 * Test whether the current event is START_DOCUMENT
	 * 
	 * @return true iff at start of document
	 */
	public boolean isStartElement() {
		return !started;
	}
	
	/**
	 * Test whether the current event is END_DOCUMENT
	 * 
	 * @return true iff at the end of the document
	 */
	public boolean isEndElement() {
		return eof;
	}
	
	/**
	 * Test whether the current event is CHARACTERS
	 * 
	 * @return true iff on a CHARACTER event
	 */
	public boolean isCharacters() {
		return (event == CHARACTERS);
	}
	
	/**
	 * Get the offset of this chunk of text
	 * 
	 * @return the offset (always zero)
	 */
	public int getTextStart() {
		return 0;
	}
	
	public InputStream getInputStream() {
		return in;
	}
	
	public void setFilter(StreamFilter filter) {
		this.filter = filter;
	}
	
	public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
		if (type != event || (localName != null && !this.localName.equals(localName))) {
			throw new XMLStreamException(localName + " required");
		}
	}
	
	public String getAttributeNamespace(int index) {
		return null;
	}
	
	public Object getProperty(String name) {
		return null;
	}
	
	public String getAttributePrefix(int index) {
		return null;
	}
	
	public QName getAttributeName(int index) {
		return new QName(attrNames.get(index));
	}
	
	public String getCharacterEncodingScheme() {
		return null;
	}
	
	public String getEncoding() {
		return null;
	}
	
	public QName getName() {
		return new QName(localName);
	}
	
	public int getNamespaceCount() {
		return 0;
	}
	
	public String getNamespaceURI() {
		return null;
	}
	
	public String getNamespaceURI(int index) {
		return null;
	}
	
	public String getNamespaceURI(String prefix) {
		return null;
	}
	
	public String getPIData() {
		return null;
	}
	
	public String getPITarget() {
		return null;
	}

	public boolean isAttributeSpecified(int index) {
		return false;
	}

	public String getNamespacePrefix(int index) {
		return null;
	}

	public NamespaceContext getNamespaceContext() {
		return null;
	}

	public int getTextCharacters(int sourceStart, char[] target,
			int targetStart, int length) throws XMLStreamException {
		return 0;
	}

	public boolean hasName() {
		return false;
	}

	public String getPrefix() {
		return null;
	}

	public String getVersion() {
		return null;
	}

	public boolean isStandalone() {
		return false;
	}

	public boolean standaloneSet() {
		return false;
	}
}
