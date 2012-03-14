package org.lejos.ros.sensors;

import org.ros.message.sensor_msgs.LaserScan;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class LaserSensor extends Sensor {
	protected String messageType = "sensor_msgs/LaserScan";
	protected LaserScan message = new LaserScan();
	protected Publisher<LaserScan> topic;
	
	public LaserSensor(Node node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.header.stamp = node.getCurrentTime();
		message.header.frame_id = OdometrySensor.ROBOT_FRAME;
		message.angle_min = -0.02f;
		message.angle_max = 0.02f;
		message.angle_increment = 0.02f; 
		message.time_increment = 0.1f;
		message.scan_time = 1f;
		message.range_min = 0.05f;
		message.range_max = 2.0f;
		
		// Show a spread of 3 points as exact location is not known
		float[] ranges = new float[3];
		for(int i=0;i<ranges.length;i++) ranges[i] = (float) value / 100f;
		message.ranges = ranges;		
		topic.publish(message);
	}
}
