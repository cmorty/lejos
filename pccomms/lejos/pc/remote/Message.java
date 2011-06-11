package lejos.pc.remote;

import java.util.Hashtable;

public class Message {
	private String name;
	private Hashtable<String,MessageElement> message;
	
	public Message(String name, Hashtable<String,MessageElement> message) {
		this.name = name;
		this.message = message;	
	}
	
	public String getName() {
		return name;
	}
	
	public Hashtable<String,MessageElement> getMessage() {
		return message;
	}
	
	public MessageElement getMessageElement(String name) {
		return message.get(name);
	}
}
