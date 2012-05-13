package org.lejos.ros.sensors;

import nxt_msgs.Gyro;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class GyroSensor extends Sensor {
	protected String messageType = "nxt_msgs/Gyro";
	protected Gyro message = node.getTopicMessageFactory().newFromType(Gyro._TYPE);
	protected Publisher<Gyro> topic;
	
	public GyroSensor(ConnectedNode node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.getHeader().setStamp(node.getCurrentTime());
		message.getAngularVelocity().setX(value);
		topic.publish(message);
	}
}
