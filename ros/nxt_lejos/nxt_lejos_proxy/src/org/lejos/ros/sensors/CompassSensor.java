package org.lejos.ros.sensors;

import nxt_lejos_msgs.Compass;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class CompassSensor extends Sensor {
	protected String messageType = "nxt_lejos_msgs/Compass";
	protected Compass message = node.getTopicMessageFactory().newFromType(Compass._TYPE);
	protected Publisher<Compass> topic;
	
	public CompassSensor(ConnectedNode node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.setHeading((float) value);
		topic.publish(message);
	}
}
