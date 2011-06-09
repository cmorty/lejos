package javax.xml.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Simple subset implementation of Stax parser.
 * Does not deal with namespaces.
 * QName not supported.
 * Has very little error checking.
 * Only supports CDATA attributes.
 * Does not currently respect class, abstract class and interface distinctions.
 * Never gives SPACE events. White space is mainly thrown away.
 * Does not support DTDs.
 * XML event classes not supported.
 * Only ascii encoding supported.
 * Processing instructions not supported.
 * 
 * @author Lawrie Griffiths
 *
 */
public class XMLStreamReader implements XMLStreamConstants {
	private InputStream in;
	private boolean eof = false;
	private String localName;
	private int event;
	private Hashtable<String, String> attributes = new Hashtable<String,String>();
	private Vector<String> attrNames = new Vector<String>();
	private Vector<String> attrValues = new Vector<String>();
	private boolean started = false , ended = false;
	private String text;
	private int numAttributes = 0;
	private char c;
	private int line = 0, pos = 0, column = 0;;
	private char quote;
	private String attrName,attrValue;
	private boolean quoted = false;
	private String version;
	
	public XMLStreamReader(InputStream stream) {
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
	public int next() {
		StringBuffer s = new StringBuffer();
		
		// Generate START_DOCUMENT event at the start of the document
		if (!started) {
			started = true;
			c = getChar();
			return START_DOCUMENT;
		}
		
		// Generate END_DOCUMENT event at the end of the document
		if (eof) {
			if (!ended) {
				ended = true;
				return END_DOCUMENT;
			}
			return 0;
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
			attrNames = new Vector<String>();
			attrValues = new Vector<String>();
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
				return COMMENT;
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
						attrNames.addElement(attrName);
						attrValues.addElement(attrValue);
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

		return CHARACTERS;
	}
	
	/**
	 * Check if there are more tokens in the stream
	 * 
	 * @return true iff there are more tokens
	 */
	boolean hasNext() {
		return !eof;
	}
	
	/**
	 * Close the stream.
	 */
	public void close() {
		// Does nothing
	}
	
	/**
	 * Get the local name for the tag.
	 * Only applies if the current event is START_ELEMENT or END_ELEMENT.
	 * 
	 * @return the local name
	 */
	public String getLocalName() {
		return localName;
	}
	
	/**
	 * Get the text for the contents of a tag.
	 * Only applies if the current event is CHARACTERS or COMMENT
	 * 
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Get the attribute count for the current element
	 * 
	 * @return the attribute count
	 */
	public int getAttributeCount() {
		return numAttributes;
	}
	
	/**
	 * Get the local name of an attribute
	 * 
	 * @param index the attribute index
	 * @return the local name
	 */
	public String getAttributeLocalName(int index) {
		return attrNames.elementAt(index);
	}
	
	/**
	 * Look up an attribute value using its name
	 * 
	 * @param namespaceURI not used
	 * @param localName the name of the atrribute
	 * @return the attribute value
	 */
	public String getAttributeValue(String namespaceURI, String localName) {
		return attributes.get(localName);
	}
	
	/**
	 * Get the value of an attribute
	 * 
	 * @param index the index of the attribute
	 * @return the attribute value
	 */
	public String getAttributeValue(int index) {
		return attrValues.elementAt(index);
	}
	
	/**
	 * Get the type of an attribute. Current only CDATA attributes are supported.
	 * 
	 * @param index the index of the attribute
	 * @return the attribute type
	 */
	public String getAttributeType(int index) {
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
		return text.length();
	}
	
	/**
	 * Get the text as a character array
	 * 
	 * @return the text as a character array
	 */
	public char[] getTextCharacters() {
		return text.toCharArray();
	}
	
	/**
	 * Get the next tag
	 * 
	 * @return the tag event
	 */
	public int nextTag() {
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
		return new Location(line, column, pos);
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
					 "unexpected end of document when reading element text content", this);
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
}
