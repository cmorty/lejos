package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import nxt_lejos_msgs.Compass;

import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import tf.tfMessage;

public class CompassSensor extends NXTDevice implements INXTDevice {	
	//NXT data
	private String port;
	
	// Heading message data
	private String frame_id;
	private String stamp;
	private float heading;
	
    Compass message; 
    Publisher<Compass> topic = null;
    String messageType = "nxt_lejos_ros_msgs/Compass";
	
    //NXT Brick
	private lejos.nxt.addon.CompassHTSensor compass;
    
	public CompassSensor(String port){
		if (port.equals("PORT_1")) {
			compass = new lejos.nxt.addon.CompassHTSensor(SensorPort.S1);
		} else if(port.equals("PORT_2")){
			compass = new lejos.nxt.addon.CompassHTSensor(SensorPort.S2);
		} else if(port.equals("PORT_3")){
			compass = new lejos.nxt.addon.CompassHTSensor(SensorPort.S3);
		} else if(port.equals("PORT_4")){
			compass = new lejos.nxt.addon.CompassHTSensor(SensorPort.S4);
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
	
	public float getHeading() {
		return heading;
	}

	public void publishTopic(ConnectedNode node) {
		topic = node.newPublisher("" + super.getName(), messageType);
	}

	public void updateTopic(Node node, long seq) {
		message = node.getTopicMessageFactory().newFromType(Compass._TYPE);
		heading = compass.getDegrees();
		message.setHeading(heading);
		topic.publish(message);		
	}

	@Override
	public void publishTopic(Node node) {
		// TODO Auto-generated method stub
		
	}
}
