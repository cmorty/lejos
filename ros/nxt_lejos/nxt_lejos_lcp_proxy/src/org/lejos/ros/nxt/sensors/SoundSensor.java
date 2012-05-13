package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import nxt_lejos_msgs.Decibels;

import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class SoundSensor extends NXTDevice implements INXTDevice {	
	//NXT data
	private String port;
	
	// Heading message data
	private String frame_id;
	private String stamp;
	private int  volume;
	
    Decibels message; 
    Publisher<Decibels> topic = null;
    String messageType = "nxt_lejos_msgs/Decibels";
	
    //NXT Brick
	private lejos.nxt.SoundSensor sound;
    
	public SoundSensor(String port){
		if (port.equals("PORT_1")) {
			sound = new lejos.nxt.SoundSensor(SensorPort.S1);
		} else if(port.equals("PORT_2")){
			sound = new lejos.nxt.SoundSensor(SensorPort.S2);
		} else if(port.equals("PORT_3")){
			sound = new lejos.nxt.SoundSensor(SensorPort.S3);
		} else if(port.equals("PORT_4")){
			sound = new lejos.nxt.SoundSensor(SensorPort.S4);
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
	
	public int getVolume() {
		return volume;
	}

	public void publishTopic(ConnectedNode node) {
		topic = node.newPublisher("" + super.getName(), messageType);
	}

	public void updateTopic(Node node, long seq) {
		message = node.getTopicMessageFactory().newFromType(Decibels._TYPE);
		message.setDecibels((short) sound.readValue());
		topic.publish(message);		
	}

	@Override
	public void publishTopic(Node node) {
		// TODO Auto-generated method stub
		
	}
}
