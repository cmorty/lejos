package org.lejos.ros.sensors;

import org.ros.message.nxt_msgs.Accelerometer;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class AccelerationSensor extends Sensor {
	protected String messageType = "nxt_msgs/Accelerometer";
	protected Accelerometer message = new Accelerometer();
	protected Publisher<Accelerometer> topic;
	
	public AccelerationSensor(Node node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.header.stamp = node.getCurrentTime();
		message.linear_acceleration.x = value;
		topic.publish(message);
	}
}
