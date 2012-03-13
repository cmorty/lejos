package org.lejos.ros.sensors;

import org.ros.message.nxt_lejos_msgs.Compass;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class CompassSensor extends Sensor {
	protected String messageType = "nxt_lejos_msgs/Compass";
	protected Compass message = new Compass();
	protected Publisher<Compass> topic;
	
	public CompassSensor(Node node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.heading = (float) value;
		topic.publish(message);
	}
}
