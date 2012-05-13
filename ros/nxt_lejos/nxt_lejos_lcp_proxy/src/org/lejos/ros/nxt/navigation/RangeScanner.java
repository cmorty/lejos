package org.lejos.ros.nxt.navigation;


import lejos.nxt.UltrasonicSensor;
import lejos.robotics.FixedRangeScanner;
import lejos.robotics.RangeFinder;
import lejos.robotics.RangeReadings;
import lejos.robotics.navigation.DifferentialPilot;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import sensor_msgs.LaserScan;
import tf.tfMessage;

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

    private LaserScan message; 
    private Publisher<LaserScan> topic = null;
    private String messageType = "sensor_msgs/LaserScan";

	
	public RangeScanner(DifferentialPilot pilot, RangeFinder finder){	
		this.finder = finder;
		scanner = new FixedRangeScanner(pilot,finder);
		scanner.setAngles(angles);
	}
	
	public void publishTopic(ConnectedNode node) {
		topic = node.newPublisher("scan", messageType);
	}

	public void updateTopic(ConnectedNode node, long seq) {
		message = node.getTopicMessageFactory().newFromType(LaserScan._TYPE);
		message.getHeader().setStamp(node.getCurrentTime());
		message.getHeader().setFrameId("/robot");
		message.setAngleMin((float) Math.toRadians(-45));
		message.setAngleMax((float) Math.toRadians(45));
		message.setAngleIncrement((float) Math.toRadians(15)); 
		message.setTimeIncrement(1.0f);
		message.setScanTime(5.0f);
		message.setRangeMin(0.05f);
		message.setRangeMax(2.0f);
		if (finder instanceof UltrasonicSensor) 
			((UltrasonicSensor) finder).continuous();
		RangeReadings readings = scanner.getRangeValues();
		readings.printReadings();
		float[] ranges = new float[7];
		for(int i=0;i<7;i++) ranges[i] = readings.getRange(i) / 100;
		message.setRanges(ranges);
		topic.publish(message);
	}

	@Override
	public void publishTopic(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTopic(Node node, long seq) {
		// TODO Auto-generated method stub
		
	}
	
}
