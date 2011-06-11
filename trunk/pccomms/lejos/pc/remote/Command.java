package lejos.pc.remote;

public class Command {
	byte value;
	Message request;
	Message reply;
	
	public Command(byte value, Message request, Message reply) {
		this.value = value;
		this.request = request;
		this.reply = reply;
	}
	
	public byte getValue() {
		return value;
	}
	
	public Message getRequest() {
		return request;
	}
	
	public Message getReply() {
		return reply;
	}

}
