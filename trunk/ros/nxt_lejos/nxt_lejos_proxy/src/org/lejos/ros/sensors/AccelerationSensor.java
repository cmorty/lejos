package org.lejos.ros.sensors;

import nxt_msgs.Accelerometer;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class AccelerationSensor extends Sensor {
	protected String messageType = "nxt_msgs/Accelerometer";
	protected Accelerometer message = node.getTopicMessageFactory().newFromType(Accelerometer._TYPE);
	protected Publisher<Accelerometer> topic;
	
	public AccelerationSensor(ConnectedNode node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.getHeader().setStamp(node.getCurrentTime());
		topic.publish(message);
	}
}
