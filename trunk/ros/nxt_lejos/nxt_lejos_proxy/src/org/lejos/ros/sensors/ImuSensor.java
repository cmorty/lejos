package org.lejos.ros.sensors;

import geometry_msgs.Quaternion;

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
			//System.out.println("Angular velcity is " + gyroSensor.getReading());
			message.getAngularVelocity().setZ(gyroSensor.getReading());
		}
		CompassSensor compassSensor = proxy.getCompassSensor();
		if (compassSensor != null) {
			// Set orientation quaternion
			
			double attitude = Math.toRadians(compassSensor.getReading()); // Why attitude, not heading?
			double bank = 0;
			double heading = 0;
		    double c1 = Math.cos(heading/2);
		    double s1 = Math.sin(heading/2);
		    double c2 = Math.cos(attitude/2);
		    double s2 = Math.sin(attitude/2);
		    double c3 = Math.cos(bank/2);
		    double s3 = Math.sin(bank/2);
		    double c1c2 = c1*c2;
		    double s1s2 = s1*s2;
		   	
			Quaternion q = node.getTopicMessageFactory().newFromType(Quaternion._TYPE);
			q.setW(c1c2*c3 - s1s2*s3);
			q.setX(c1c2*s3 + s1s2*c3);
			q.setY(s1*c2*c3 + c1*s2*s3);
			q.setZ(c1*s2*c3 - s1*c2*s3);
			//System.out.println("Setting orientation"); 
			message.setOrientation(q);
		}
		AccelerationSensor accelerationSensor = proxy.getAccelerationSensor();
		if (accelerationSensor != null) {
			message.getLinearAcceleration().setX(accelerationSensor.getReading());
		}
		topic.publish(message);
	}
}
