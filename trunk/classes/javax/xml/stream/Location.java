package javax.xml.stream;

public class Location {
	private int line, column, pos;
	
	public Location(int line, int column, int pos) {
		this.line = line;
		this.column = column;
		this.pos = pos;
	}
	
	public int getCharacterOffset() {
		return pos;
	}
	
	public int getColumnNumber() {
		return column;
	}
	
	public int getLineNumber() {
		return line;
	}
}
