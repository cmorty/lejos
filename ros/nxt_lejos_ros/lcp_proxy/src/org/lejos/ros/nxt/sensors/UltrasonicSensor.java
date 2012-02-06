package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class UltrasonicSensor extends NXTDevice implements INXTDevice{
	
	//NXT data
	private String port;
	
	//Range message data
	private String frame_id;
	private String stamp;
	private float range;
	private float spread_angle = 0.5f;
	private float range_min = 0.05f;
	private float range_max = 2.0f;
	
    final org.ros.message.sensor_msgs.Range message = new org.ros.message.sensor_msgs.Range(); 
    Publisher<org.ros.message.sensor_msgs.Range> topic = null;
    String messageType = "sensor_msgs/Range";
    
    Node node;
	
    //NXT Brick
	private lejos.nxt.UltrasonicSensor usSensor;
    
	public UltrasonicSensor(Node node, String port){
		this.node = node;
		if(port.equals("PORT_1")){
			usSensor = new lejos.nxt.UltrasonicSensor(SensorPort.S1);
		}else if(port.equals("PORT_2")){
			usSensor = new lejos.nxt.UltrasonicSensor(SensorPort.S2);
		}else if(port.equals("PORT_3")){
			usSensor = new lejos.nxt.UltrasonicSensor(SensorPort.S3);
		}else if(port.equals("PORT_4")){
			usSensor = new lejos.nxt.UltrasonicSensor(SensorPort.S4);
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
	
	public void setRange(float _range){
		range = _range;
	}
	
	public float getRange(){
		return range;
	}
	
	public void setSpreadAngle(float angle){
		spread_angle = angle;
	}
	
	public float getSpreadAngle(){
		return spread_angle;
	}
	
	public void setRangeMin(float range){
		range_min = range;
	}
	
	public float getRangeMin(){
		return range_min;
	}
	
	public void setRangeMax(float range){
		range_max = range;
	}
	
	public float getRangeMax(){
		return range_max;
	}

	public void publishTopic(Node node) {
		topic = node.newPublisher("" + super.getName(), messageType);
	}

	public void updateTopic() {
		usSensor.ping();
		range = usSensor.getDistance();
		message.header.stamp = node.getCurrentTime();
		message.header.frame_id = "/front";
		message.max_range= range_max;
		message.min_range = range_min;
		message.field_of_view = spread_angle;
		message.range = range / 100; // in meters
		message.radiation_type = 0; // Ultrasonic
		topic.publish(message);		
	}
}
