package org.lejos.ros.sensors;

import org.ros.message.nxt_lejos_msgs.Battery;
import org.ros.message.nxt_msgs.Contact;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class BatterySensor extends Sensor {
	protected String messageType = "nxt_lejos_msgs/Battery";
	protected Battery message = new Battery();
	protected Publisher<Battery> topic;
	
	public BatterySensor(Node node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.voltage = value;
		topic.publish(message);
	}
}
