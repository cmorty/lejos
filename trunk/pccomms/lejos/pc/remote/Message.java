package lejos.pc.remote;

import java.util.HashMap;

public class Message {
	private String name;
	private HashMap<String,MessageElement> message;
	
	public Message(String name, HashMap<String,MessageElement> message) {
		this.name = name;
		this.message = message;	
	}
	
	public String getName() {
		return name;
	}
	
	public HashMap<String,MessageElement> getMessageElements() {
		return message;
	}
	
	public MessageElement getMessageElement(String name) {
		return message.get(name);
	}
}
