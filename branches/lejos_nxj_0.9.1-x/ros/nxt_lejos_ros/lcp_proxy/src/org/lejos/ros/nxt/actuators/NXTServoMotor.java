package org.lejos.ros.nxt.actuators;

import java.util.ArrayList;

import lejos.nxt.Motor;
import lejos.robotics.RegulatedMotor;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class NXTServoMotor extends NXTDevice implements INXTDevice{

	//ROS topic
    final org.ros.message.sensor_msgs.JointState message = new org.ros.message.sensor_msgs.JointState(); 
	Publisher<org.ros.message.sensor_msgs.JointState> topic = null;
	ArrayList<String> nameList = new ArrayList();
	double[] arrEffort = new double[1];
	double[] arrPosition = new double[1];
	double[] arrVelocity = new double[1];

	//NXT data
	private String port;
	private RegulatedMotor motor;
	
	public NXTServoMotor(final String port){
		
		if(port.equals("PORT_A")){
			motor = Motor.A;
		}else if(port.equals("PORT_B")){
			motor = Motor.B;
		}else if(port.equals("PORT_C")){
			motor = Motor.C;
		}
		
	}
	
	public void setPort(final String _port){
		port = _port;
	}
	
	public String getPort(){
		return port;
	}
	
	public void publishTopic(Node node){		
		topic =  node.newPublisher("" + super.getName(), "sensor_msgs/JointState");
	}

	public void updateTopic() {

		nameList.clear();
		nameList.add(super.getName());
		message.name = nameList;
		arrEffort[0] = (double) (motor.getSpeed() / 9);
		message.effort = arrEffort;
		arrPosition[0] =  motor.getTachoCount();
		message.position = arrPosition;
		arrVelocity[0] = (motor.isMoving() ? motor.getSpeed() : 0);
		message.velocity = arrVelocity;
		topic.publish(message);	
	}
	
	public void updateJoint(double effort){
		System.out.println("JOINTCommanD: effort = " + effort);
		
		motor.setSpeed((int) effort * 9);
		if (effort > 0) motor.forward();
		else motor.backward();
	}
}
