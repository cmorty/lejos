package org.lejos.ros.sensors;

import nxt_msgs.Contact;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class TouchSensor extends Sensor {
	protected String messageType = "nxt_msgs/Contact";
	protected Contact message = node.getTopicMessageFactory().newFromType(Contact._TYPE);
	protected Publisher<Contact> topic;
	
	public TouchSensor(ConnectedNode node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.getHeader().setStamp(node.getCurrentTime());
		message.setContact((value != 0));
		topic.publish(message);
	}
}
