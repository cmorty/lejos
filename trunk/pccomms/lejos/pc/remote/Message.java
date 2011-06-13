package lejos.pc.remote;
import java.util.ArrayList;

public class Message {
	private String name;
	private ArrayList<MessageElement> message;
	
	public Message(String name, ArrayList<MessageElement> message) {
		this.name = name;
		this.message = message;	
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<MessageElement> getMessageElements() {
		return message;
	}
}
