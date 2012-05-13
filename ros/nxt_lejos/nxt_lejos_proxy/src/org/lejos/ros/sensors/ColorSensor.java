package org.lejos.ros.sensors;

import nxt_msgs.Color;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class ColorSensor extends Sensor {
	protected String messageType = "nxt_msgs/Color";
	protected Color message = node.getTopicMessageFactory().newFromType(Color._TYPE);
	protected Publisher<Color> topic;
	
	public ColorSensor(ConnectedNode node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.getHeader().setStamp(node.getCurrentTime());
		message.setIntensity(value);
		topic.publish(message);
	}
}
