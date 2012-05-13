package org.lejos.ros.sensors;

import sensor_msgs.LaserScan;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class LaserSensor extends Sensor {
	protected String messageType = "sensor_msgs/LaserScan";
	protected LaserScan message = node.getTopicMessageFactory().newFromType(LaserScan._TYPE);
	protected Publisher<LaserScan> topic;
	
	public LaserSensor(ConnectedNode node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.getHeader().setStamp(node.getCurrentTime());
		message.getHeader().setFrameId(OdometrySensor.ROBOT_FRAME);
		message.setAngleMin(-0.02f);
		message.setAngleMax(0.02f);
		message.setAngleIncrement(0.02f); 
		message.setTimeIncrement(0.1f);
		message.setScanTime(1f);
		message.setRangeMin(0.05f);
		message.setRangeMax(2.0f);
		
		// Show a spread of 3 points as exact location is not known
		float[] ranges = new float[3];
		for(int i=0;i<ranges.length;i++) ranges[i] = (float) value / 100f;
		message.setRanges(ranges);		
		topic.publish(message);
	}
}
