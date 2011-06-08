package javax.xml.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

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
	private int line = 0;
	private int pos = 0;
	private char quote;
	private String attrName, attrValue;
	private boolean quoted = false;
	
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
			char ch = (char) b;
			if (ch == '\r') ch = ' ';
			if (ch == '\t') ch = ' ';
			if (ch == '\n') {
				line++;
				pos = 0;
				ch = ' ';
			}	  
			return ch;
		} catch (IOException e) {
			eof = true;
			return ' ';
		}
	}
	
	public int next() {
		StringBuffer s = new StringBuffer();
		
		if (!started) {
			started = true;
			c = getChar();
			return START_DOCUMENT;
		}
		
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

		if (c == '<') {
			attributes = new Hashtable<String,String>();
			attrNames = new Vector<String>();
			attrValues = new Vector<String>();
			numAttributes = 0;
			event = START_ELEMENT;
			
			if ((c = getChar()) == '/') {
				event=END_ELEMENT;
				c = getChar();
			}
			
			if (c == '?') {
				while (!eof && (c = getChar()) != '<'); // Skip <?xml
				c = getChar();
			}
			
			if (c == '!') {
				while (!eof && (c = getChar()) != '<'); // comment
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
	
	boolean hasNext() {
		return !eof;
	}
	
	public void close() {
		// Does nothing
	}
	
	public String getLocalName() {
		return localName;
	}
	
	public String getText() {
		return text;
	}
	
	public String getElementText() {
		return text;
	}
	
	public int getAttributeCount() {
		return numAttributes;
	}
	
	public String getAttributeLocalName(int index) {
		return attrNames.elementAt(index);
	}
	
	public String getAttributeValue(String namespaceURI, String localName) {
		return attributes.get(localName);
	}
	
	public String getAttributeValue(int index) {
		return attrValues.elementAt(index);
	}
	
	public String getAttributeType(int index) {
		return "CDATA";
	}
}
