package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import lejos.nxt.addon.AccelMindSensor;

public class AccelerationSensor extends NXTDevice implements INXTDevice {
	
	//NXT data
	private String port;
	
	//Accelerometer message data
	private String frame_id;
	private String stamp;

	
    final org.ros.message.nxt_msgs.Accelerometer message = new org.ros.message.nxt_msgs.Accelerometer(); 
    Publisher<org.ros.message.nxt_msgs.Accelerometer> topic = null;
    String messageType = "nxt_msgs/Accelerometer";
	
    //NXT Brick
	private lejos.nxt.addon.AccelMindSensor accel;
    
	public AccelerationSensor(String port){
		if(port.equals("PORT_1")){
			accel = new AccelMindSensor(SensorPort.S1);
		}else if(port.equals("PORT_2")){
			accel = new AccelMindSensor(SensorPort.S2);
		}else if(port.equals("PORT_3")){
			accel = new AccelMindSensor(SensorPort.S3);
		}else if(port.equals("PORT_4")){
			accel = new AccelMindSensor(SensorPort.S4);
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
		message.linear_acceleration.x = accel.getXAccel();
		message.linear_acceleration.y = accel.getYAccel();
		message.linear_acceleration.z = accel.getZAccel();
		message.header.seq = seq;
		message.header.stamp = node.getCurrentTime();
		message.header.frame_id = "/robot";
		topic.publish(message);		
	}
}
