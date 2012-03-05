package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class TouchSensor extends NXTDevice implements INXTDevice {	
	//NXT data
	private String port;
	
	// Heading message data
	private String frame_id;
	private String stamp;
	private boolean pressed;
	
    final org.ros.message.nxt_msgs.Contact message = new org.ros.message.nxt_msgs.Contact(); 
    Publisher<org.ros.message.nxt_msgs.Contact> topic = null;
    String messageType = "nxt_msgs/Contact";
	
    //NXT Brick
	private lejos.nxt.TouchSensor touch;
    
	public TouchSensor(String port){
		if (port.equals("PORT_1")) {
			touch = new lejos.nxt.TouchSensor(SensorPort.S1);
		} else if(port.equals("PORT_2")){
			touch = new lejos.nxt.TouchSensor(SensorPort.S2);
		} else if(port.equals("PORT_3")){
			touch = new lejos.nxt.TouchSensor(SensorPort.S3);
		} else if(port.equals("PORT_4")){
			touch = new lejos.nxt.TouchSensor(SensorPort.S4);
		}
	}
	
	public void activate(){

	}

	public void setPort(final String _port){
		port = _port;
	}
	
	public String getPort(){
		return port;
	}
	
	public void setFrameID(String id){
		frame_id = id;
	}
	
	public String getFrameID(){
		return frame_id;
	}
	
	public void setStamp(String _stamp){
		stamp = _stamp;
	}
	
	public String getStamp(){
		return stamp;
	}
	
	public boolean isPressed() {
		return pressed;
	}

	public void publishTopic(Node node) {
		topic = node.newPublisher("" + super.getName(), messageType);
	}

	public void updateTopic(Node node, long seq) {
		message.header.seq = seq;
		message.header.stamp = node.getCurrentTime();
		message.header.frame_id = "/robot";
		pressed = touch.isPressed();
		message.contact = pressed;
		topic.publish(message);		
	}
}
