package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;
import lejos.robotics.RangeFinder;
import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import sensor_msgs.LaserScan;
import sensor_msgs.Range;

import org.ros.node.ConnectedNode;
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
	
    Range message; 
    Publisher<Range> topic = null;
    String messageType = "sensor_msgs/Range";
    
    private LaserScan laserMessage;
    Publisher<LaserScan> laserTopic = null;
    String laserMessageType = "sensor_msgs/LaserScan";
	
    //NXT Brick
	private lejos.nxt.UltrasonicSensor usSensor;
    
	public UltrasonicSensor(ConnectedNode node, String port){
		if(port.equals("PORT_1")){
			usSensor = new lejos.nxt.UltrasonicSensor(SensorPort.S1);
		}else if(port.equals("PORT_2")){
			usSensor = new lejos.nxt.UltrasonicSensor(SensorPort.S2);
		}else if(port.equals("PORT_3")){
			usSensor = new lejos.nxt.UltrasonicSensor(SensorPort.S3);
		}else if(port.equals("PORT_4")){
			usSensor = new lejos.nxt.UltrasonicSensor(SensorPort.S4);
		}
		//usSensor.continuous();
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

	public void publishTopic(ConnectedNode node) {
		topic = node.newPublisher("" + super.getName(), messageType);
		laserTopic = node.newPublisher("scan", laserMessageType);
	}

	public void updateTopic(ConnectedNode node, long seq) {
		float range = usSensor.getDistance() / 100f;
		
		// Range message
		message.getHeader().setStamp(node.getCurrentTime());
		message.getHeader().setFrameId("/front");
		message.setMaxRange(range_max);
		message.setMinRange(range_min);
		message.setFieldOfView(spread_angle);
		message.setRadiationType((byte) 0); // Ultrasonic
		message.setRange(range);
		topic.publish(message);	
		
		// Laser scan message
		laserMessage.getHeader().setStamp(node.getCurrentTime());
		laserMessage.getHeader().setFrameId("/robot");
		laserMessage.setAngleMin(-0.02f);
		laserMessage.setAngleMax(0.02f);
		laserMessage.setAngleIncrement(0.02f); 
		laserMessage.setTimeIncrement(0.1f);
		laserMessage.setScanTime(1f);
		laserMessage.setRangeMin(0.05f);
		laserMessage.setRangeMax(2.0f);
		
		// Show a spread of 3 points as exact location is not known
		float[] ranges = new float[3];
		for(int i=0;i<ranges.length;i++) ranges[i] = range;
		laserMessage.setRanges(ranges);
		laserTopic.publish(laserMessage);
	}
	
	public RangeFinder getSonic() {
		return usSensor;
	}

	@Override
	public void publishTopic(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTopic(Node node, long seq) {
		// TODO Auto-generated method stub
		
	}
}
