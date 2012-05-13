package org.lejos.ros.sensors;

import nxt_lejos_msgs.Decibels;

import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class SoundSensor extends Sensor {
	protected String messageType = "nxt_lejos_msgs/Decibels";
	protected Decibels message = node.getTopicMessageFactory().newFromType(Decibels._TYPE);
	protected Publisher<Decibels> topic;
	
	public SoundSensor(ConnectedNode node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.setDecibels((short) value);
		topic.publish(message);
	}
}
