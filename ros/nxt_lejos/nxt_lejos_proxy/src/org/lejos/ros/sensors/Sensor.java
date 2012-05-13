package org.lejos.ros.sensors;

import org.ros.node.ConnectedNode;

public abstract class Sensor {
	protected double desiredFrequency;
	protected String frameId;
	protected double rate;
	protected long period;
	protected long lastPublishTime;
	protected long fudge = 4;
	protected int numMessages;	
	protected String topicName;
	protected ConnectedNode node;
	protected float reading;
	
	public Sensor(ConnectedNode node, String topicName, double desiredFrequency) {
		this.node = node;
		this.topicName = topicName;
		this.desiredFrequency = desiredFrequency;
		period = (long) (1000.0/desiredFrequency);
	}
	
	public void publish(long start, double value) {	
		reading = (float) value;
		long now = System.currentTimeMillis();
		if ((now - lastPublishTime) >= (period - fudge)) {
			publishMessage(value);
			numMessages++;
			double rate = (double) (numMessages * 1000.0) / (double) (now - start);
			//System.out.println(topicName + " rate = " + rate);
			double error = rate/desiredFrequency;
			if (error < 0.9) fudge++;
			if (error > 1.1) fudge--;
			lastPublishTime = now;
		}
	}
	
	public abstract void publishMessage(double value);
	
	public double getDesiredFrequency() {
		return desiredFrequency;
	}
	
	public String getFrameId() {
		return frameId;
	}
	
	public float getReading() {
		return reading;
	}
}
