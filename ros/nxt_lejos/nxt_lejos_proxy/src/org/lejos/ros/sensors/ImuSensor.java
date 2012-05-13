package org.lejos.ros.sensors;

import org.lejos.ros.nodes.ROSProxy;
import sensor_msgs.Imu;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

public class ImuSensor extends Sensor {
	protected String messageType = "sensor_msgs/Imu";
	protected Imu message = node.getTopicMessageFactory().newFromType(Imu._TYPE);
	protected Publisher<Imu> topic;
	protected ROSProxy proxy;
	
	public ImuSensor(ROSProxy proxy, ConnectedNode node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		this.proxy = proxy;
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.getHeader().setStamp(node.getCurrentTime());
		GyroSensor gyroSensor = proxy.getGyroSensor();
		if (gyroSensor != null) {
			message.getAngularVelocity().setZ(gyroSensor.getReading());
		}
		CompassSensor compassSensor = proxy.getCompassSensor();
		if (compassSensor != null) {
			// Set orientation quaternion
		}
		AccelerationSensor accelerationSensor = proxy.getAccelerationSensor();
		if (accelerationSensor != null) {
			message.getLinearAcceleration().setX(accelerationSensor.getReading());
		}
		topic.publish(message);
	}
}
