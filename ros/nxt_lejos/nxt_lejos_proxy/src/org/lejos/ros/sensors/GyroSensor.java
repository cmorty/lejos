package org.lejos.ros.sensors;

import org.ros.message.nxt_msgs.Gyro;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class GyroSensor extends Sensor {
	protected String messageType = "nxt_msgs/Gyro";
	protected Gyro message = new Gyro();
	protected Publisher<Gyro> topic;
	
	public GyroSensor(Node node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.header.stamp = node.getCurrentTime();
		message.angular_velocity.z = value;
		topic.publish(message);
	}
}
