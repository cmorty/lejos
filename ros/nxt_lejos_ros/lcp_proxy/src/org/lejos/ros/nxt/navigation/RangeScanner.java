package org.lejos.ros.nxt.navigation;


import lejos.nxt.UltrasonicSensor;
import lejos.robotics.FixedRangeScanner;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReadings;
import lejos.robotics.navigation.DifferentialPilot;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

/**
 * 
 * @author Lawrie Griffiths
 *
 */
public class RangeScanner extends NXTDevice implements INXTDevice {
	// leJOS
	
	private FixedRangeScanner scanner;
	private float[] angles = {-45f,-30f,-15f,0f,15f,30f,45f};
	private RangeFinder finder;
	
	// ROS

    private final org.ros.message.sensor_msgs.LaserScan message = new org.ros.message.sensor_msgs.LaserScan(); 
    private Publisher<org.ros.message.sensor_msgs.LaserScan> topic = null;
    private String messageType = "sensor_msgs/LaserScan";

	
	public RangeScanner(DifferentialPilot pilot, RangeFinder finder){	
		this.finder = finder;
		scanner = new FixedRangeScanner(pilot,finder);
		scanner.setAngles(angles);
	}
	
	public void publishTopic(Node node) {
		topic = node.newPublisher("scan", messageType);
	}

	public void updateTopic(Node node, long seq) {
		message.header.stamp = node.getCurrentTime();
		message.header.frame_id = "/robot";
		message.angle_min = (float) Math.toRadians(-45);
		message.angle_max = (float) Math.toRadians(45);
		message.angle_increment = (float) Math.toRadians(15); 
		message.time_increment = 1.0f;
		message.scan_time = 5.0f;
		message.range_min = 0.05f;
		message.range_max = 2.0f;
		if (finder instanceof UltrasonicSensor) 
			((UltrasonicSensor) finder).continuous();
		RangeReadings readings = scanner.getRangeValues();
		readings.printReadings();
		float[] ranges = new float[7];
		for(int i=0;i<7;i++) ranges[i] = readings.getRange(i) / 100;
		message.ranges = ranges;
		topic.publish(message);
	}
	
}
