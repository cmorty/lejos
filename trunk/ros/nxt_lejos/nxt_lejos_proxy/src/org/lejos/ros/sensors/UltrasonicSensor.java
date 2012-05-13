package org.lejos.ros.sensors;

import sensor_msgs.Range;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class UltrasonicSensor extends Sensor {
	protected String messageType = "sensor_msgs/Range";
	protected Range message = node.getTopicMessageFactory().newFromType(Range._TYPE);
	protected Publisher<Range> topic;
	
	public UltrasonicSensor(ConnectedNode node, String topicName, double desiredFrequency, String frameId) {
		super(node,topicName,desiredFrequency);
		this.frameId = frameId;
		topic = node.newPublisher(topicName, messageType);
		message.setMinRange(0.05f);
		message.setMaxRange(2.5f);
		message.setRadiationType((byte) 0);
		message.setFieldOfView(0.5f); // radian
		message.getHeader().setFrameId(frameId);
	}
	
	@Override
	public void publishMessage(double value) {
		message.setRange((float) value / 100f);
		message.getHeader().setStamp(node.getCurrentTime());
		topic.publish(message);
	}
}
