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
	private float spread_angle;
	private float range_min;
	private float range_max;
	
    final org.ros.message.nxt_msgs.Range message = new org.ros.message.nxt_msgs.Range(); 
    Publisher<org.ros.message.nxt_msgs.Range> topic = null;
	
    //NXT Brick
	private lejos.nxt.UltrasonicSensor usSensor;
    
	public UltrasonicSensor(String port){
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
		topic = node.newPublisher("" + super.getName(), "nxt_msgs/Range");
	}

	public void updateTopic() {
		usSensor.ping();
		range = usSensor.getDistance();
		//message.header.frame_id = frame_id;
		message.range_max = range_max;
		message.range_min = range_min;
		message.spread_angle = spread_angle;
		message.range = range;
		topic.publish(message);		
	}

}
