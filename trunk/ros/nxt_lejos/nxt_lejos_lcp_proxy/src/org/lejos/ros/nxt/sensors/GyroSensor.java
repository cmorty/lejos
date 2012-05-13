package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;

import nxt_msgs.Gyro;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import tf.tfMessage;

public class GyroSensor extends NXTDevice implements INXTDevice {	
	//NXT data
	private String port;
	
	// Heading message data
	private String frame_id;
	private String stamp;
	private float angularVelocity;
	
    Gyro message; 
    Publisher<Gyro> topic = null;
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

	public void publishTopic(ConnectedNode node) {
		topic = node.newPublisher("" + super.getName(), messageType);
	}

	public void updateTopic(Node node, long seq) {
		message = node.getTopicMessageFactory().newFromType(Gyro._TYPE);
		angularVelocity = gyro.getAngularVelocity();
		message.getAngularVelocity().setX(angularVelocity);
		topic.publish(message);		
	}

	@Override
	public void publishTopic(Node node) {
		// TODO Auto-generated method stub
		
	}
}
