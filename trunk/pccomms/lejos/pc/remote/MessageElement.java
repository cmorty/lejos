package lejos.pc.remote;

public class MessageElement {
	private String name;
	public enum ElementType {BYTE, SHORT, INT, FLOAT};
	private ElementType type;
	
	public MessageElement(String name, ElementType type) {
		this.name = name;
		this.type = type;
	}
	
	public ElementType getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
}
