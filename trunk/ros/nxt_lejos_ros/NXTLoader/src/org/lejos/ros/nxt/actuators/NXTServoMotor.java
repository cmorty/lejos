package org.lejos.ros.nxt.actuators;

import java.util.ArrayList;

import lejos.nxt.Motor;
import lejos.nxt.remote.RemoteMotor;

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
	private RemoteMotor motor;
	
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
		message.effort = arrEffort;
		message.position = arrPosition;
		message.velocity = arrVelocity;
		topic.publish(message);	
	}
	
	public void updateJoint(double effort){
		
		//TODO: Learn calculus to update this method
		arrEffort[0] = effort;
		
	    final float POWER_TO_NM = 0.01f;
	    final float POWER_MAX = 125f;
		
		float cmd;
		cmd = (float) (effort / POWER_TO_NM);
	    if (cmd > POWER_MAX){
	        cmd = POWER_MAX;
	    }else if( cmd < -POWER_MAX){
	        cmd = -POWER_MAX;
	    }
	    
		arrPosition[0] = 0f;//234 * Math.PI / 180.0;
		arrEffort[0] = 0f;//234 *  POWER_TO_NM;
		
		motor.rotate((int)effort);
	}

}
