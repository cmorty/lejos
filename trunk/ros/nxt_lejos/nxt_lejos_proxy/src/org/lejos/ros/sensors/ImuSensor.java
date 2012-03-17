package org.lejos.ros.sensors;

import org.lejos.ros.nodes.ROSProxy;
import org.ros.message.sensor_msgs.Imu;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class ImuSensor extends Sensor {
	protected String messageType = "sensor_msgs/Imu";
	protected Imu message = new Imu();
	protected Publisher<Imu> topic;
	protected ROSProxy proxy;
	
	public ImuSensor(ROSProxy proxy, Node node, String topicName, double desiredFrequency) {
		super(node,topicName,desiredFrequency);
		this.proxy = proxy;
		topic = node.newPublisher(topicName, messageType);
	}
	
	@Override
	public void publishMessage(double value) {
		message.header.stamp = node.getCurrentTime();
		GyroSensor gyroSensor = proxy.getGyroSensor();
		if (gyroSensor != null) {
			message.angular_velocity.z = gyroSensor.getReading();
		}
		CompassSensor compassSensor = proxy.getCompassSensor();
		if (compassSensor != null) {
			// Set orientation quaternion
		}
		AccelerationSensor accelerationSensor = proxy.getAccelerationSensor();
		if (accelerationSensor != null) {
			message.linear_acceleration.x = accelerationSensor.getReading();
		}
		topic.publish(message);
	}
}
