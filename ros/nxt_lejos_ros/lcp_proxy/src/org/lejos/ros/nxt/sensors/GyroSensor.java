package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class GyroSensor extends NXTDevice implements INXTDevice {	
	//NXT data
	private String port;
	
	// Heading message data
	private String frame_id;
	private String stamp;
	private float angularVelocity;
	
    final org.ros.message.nxt_msgs.Gyro message = new org.ros.message.nxt_msgs.Gyro(); 
    Publisher<org.ros.message.nxt_msgs.Gyro> topic = null;
    String messageType = "nxt_msgs/Gyro";
	
    //NXT Brick
	private lejos.nxt.addon.GyroSensor gyro;
    
	public GyroSensor(String port){
		if (port.equals("PORT_1")) {
			gyro = new lejos.nxt.addon.GyroSensor(SensorPort.S1);
		} else if(port.equals("PORT_2")){
			gyro = new lejos.nxt.addon.GyroSensor(SensorPort.S2);
		} else if(port.equals("PORT_3")){
			gyro = new lejos.nxt.addon.GyroSensor(SensorPort.S3);
		} else if(port.equals("PORT_4")){
			gyro = new lejos.nxt.addon.GyroSensor(SensorPort.S4);
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
	
	public float getAngularVelocity() {
		return angularVelocity;
	}

	public void publishTopic(Node node) {
		topic = node.newPublisher("" + super.getName(), messageType);
	}

	public void updateTopic(Node node, long seq) {
		angularVelocity = gyro.getAngularVelocity();
		message.angular_velocity.x = angularVelocity;
		topic.publish(message);		
	}
}
