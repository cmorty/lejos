package org.lejos.ros.sensors;

import org.ros.message.nxt_msgs.Color;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class LightSensor extends Sensor {
	protected String messageType = "nxt_msgs/Color";
	protected Color message = new Color();
	protected Publisher<Color> topic;
	
	public LightSensor(Node node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.header.stamp = node.getCurrentTime();
		message.intensity = value;
		topic.publish(message);
	}
}
