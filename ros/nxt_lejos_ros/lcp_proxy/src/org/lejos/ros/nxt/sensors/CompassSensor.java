package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class CompassSensor extends NXTDevice implements INXTDevice {	
	//NXT data
	private String port;
	
	// Heading message data
	private String frame_id;
	private String stamp;
	private float heading;
	
    final org.ros.message.nxt_lejos_ros_msgs.Compass message = new org.ros.message.nxt_lejos_ros_msgs.Compass(); 
    Publisher<org.ros.message.nxt_lejos_ros_msgs.Compass> topic = null;
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

	public void publishTopic(Node node) {
		topic = node.newPublisher("" + super.getName(), messageType);
	}

	public void updateTopic(Node node, long seq) {
		heading = compass.getDegrees();
		message.heading = heading;
		topic.publish(message);		
	}
}
