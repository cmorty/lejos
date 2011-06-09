package javax.xml.stream;

public class Location {
	private int line, pos;
	
	public Location(int line, int pos) {
		this.line = line;
		this.pos = pos;
	}
	
	public int getCharacterOffset() {
		return pos;
	}
	
	public int getColumnNumber() {
		return -1;
	}
	
	public int getLineNumber() {
		return line;
	}
}
