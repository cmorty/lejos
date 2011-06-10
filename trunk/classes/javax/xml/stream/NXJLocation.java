package javax.xml.stream;

public class NXJLocation implements Location {
	private int line, column, pos;
	
	public NXJLocation(int line, int column, int pos) {
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
