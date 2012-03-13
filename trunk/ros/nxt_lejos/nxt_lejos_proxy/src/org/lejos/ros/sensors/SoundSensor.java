package org.lejos.ros.sensors;

import org.ros.message.nxt_lejos_msgs.Decibels;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class SoundSensor extends Sensor {
	protected String messageType = "nxt_lejos_msgs/Decibels";
	protected Decibels message = new Decibels();
	protected Publisher<Decibels> topic;
	
	public SoundSensor(Node node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.decibels = (short) value;
		topic.publish(message);
	}
}
