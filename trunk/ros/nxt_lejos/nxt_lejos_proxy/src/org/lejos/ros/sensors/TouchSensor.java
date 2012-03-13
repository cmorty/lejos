package org.lejos.ros.sensors;

import org.ros.message.nxt_msgs.Contact;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class TouchSensor extends Sensor {
	protected String messageType = "nxt_msgs/Contact";
	protected Contact message = new Contact();
	protected Publisher<Contact> topic;
	
	public TouchSensor(Node node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.header.stamp = node.getCurrentTime();
		message.contact = (value != 0);
		topic.publish(message);
	}
}
