package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;
import lejos.robotics.Color;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class ColorSensor extends NXTDevice implements INXTDevice{
	
	//NXT data
	private String port;
	
	//Range message data
	private String frame_id;
	private String stamp;
	
    final org.ros.message.nxt_msgs.Color message = new org.ros.message.nxt_msgs.Color(); 
    Publisher<org.ros.message.nxt_msgs.Color> topic = null;
    String messageType = "nxt_msgs/Color";
	
    //NXT Brick
	private lejos.nxt.ColorSensor color;
    
	public ColorSensor(String port){
		if(port.equals("PORT_1")){
			color = new lejos.nxt.ColorSensor(SensorPort.S1);
		}else if(port.equals("PORT_2")){
			color = new lejos.nxt.ColorSensor(SensorPort.S2);
		}else if(port.equals("PORT_3")){
			color = new lejos.nxt.ColorSensor(SensorPort.S3);
		}else if(port.equals("PORT_4")){
			color = new lejos.nxt.ColorSensor(SensorPort.S4);
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

	public void publishTopic(Node node) {
		topic = node.newPublisher("" + super.getName(), messageType);
	}

	public void updateTopic(Node node, long seq) {
		message.header.seq = seq;
		message.header.stamp = node.getCurrentTime();
		message.header.frame_id = "/robot";
		Color c = color.getColor();
		message.r = c.getRed();
		message.g = c.getGreen();
		message.b = c.getBlue();
		message.intensity = color.getLightValue();
		topic.publish(message);		
	}
}
