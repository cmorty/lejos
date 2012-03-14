package org.lejos.ros.sensors;

import org.ros.message.sensor_msgs.Range;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class UltrasonicSensor extends Sensor {
	protected String messageType = "sensor_msgs/Range";
	protected Range message = new Range();
	protected Publisher<Range> topic;
	
	public UltrasonicSensor(Node node, String topicName, double desiredFrequency, String frameId) {
		super(node,topicName,desiredFrequency);
		this.frameId = frameId;
		topic = node.newPublisher(topicName, messageType);
		message.min_range = 0.05f;
		message.max_range = 2.5f;
		message.radiation_type = 0;
		message.field_of_view = 0.5f; // radian
		message.header.frame_id = frameId;
	}
	
	@Override
	public void publishMessage(double value) {
		message.range = (float) value / 100f;
		message.header.stamp = node.getCurrentTime();
		topic.publish(message);
	}
}
