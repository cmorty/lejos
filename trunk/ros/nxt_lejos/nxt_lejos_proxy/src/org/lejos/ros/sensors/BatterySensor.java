package org.lejos.ros.sensors;

import nxt_lejos_msgs.Battery;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class BatterySensor extends Sensor {
	protected String messageType = "nxt_lejos_msgs/Battery";
	protected Battery message = node.getTopicMessageFactory().newFromType(Battery._TYPE);
	protected Publisher<Battery> topic;
	
	public BatterySensor(ConnectedNode node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.setVoltage(value);
		topic.publish(message);
	}
}
