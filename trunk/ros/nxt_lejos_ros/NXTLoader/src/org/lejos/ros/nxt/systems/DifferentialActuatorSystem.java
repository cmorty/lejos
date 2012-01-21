package org.lejos.ros.nxt.systems;

import lejos.nxt.Motor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import lejos.util.PilotProps;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class DifferentialActuatorSystem extends NXTDevice implements INXTDevice{

	//ROS
	
	//TODO: Use a new nxt_msgs type to manage this topic
    final org.ros.message.std_msgs.String message = new org.ros.message.std_msgs.String(); 
	Publisher<org.ros.message.std_msgs.String> topic = null;
	final String messageType = "std_msgs/String";
	
	//LeJOS
	private RegulatedMotor leftMotor;
	private RegulatedMotor rightMotor;
	private float wheelDiameter;
	private float trackWidth;
	private boolean reverse;
	
	private DifferentialPilot df;
	
	public DifferentialActuatorSystem(String port1, String port2, float _wheelDiameter, float _trackWidth, boolean _reverse){
		
		//TODO: Exception if letters are the same
		
		if(port1.equals("A")){
			leftMotor = Motor.A;
		}else if(port1.equals("B")){
			leftMotor = Motor.B;
		}else if(port1.equals("C")){
			leftMotor = Motor.C;
		}

		if(port2.equals("A")){
			rightMotor = Motor.A;
		}else if(port2.equals("B")){
			rightMotor = Motor.B;
		}else if(port2.equals("C")){
			rightMotor = Motor.C;
		}
		
		wheelDiameter = _wheelDiameter;
		trackWidth = _trackWidth;
		reverse = _reverse;
		
    	DifferentialPilot df = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);

	}
	
	public void publishTopic(Node node) {

		topic =  node.newPublisher("" + super.getName(), messageType);
	}

	public void updateTopic() {

		message.data = "af";		
		topic.publish(message);		
	}
	
	public void updateActuatorSystem(String cmd){
		
    	/*
        df.setAcceleration(4000);
		df.setTravelSpeed(20); // cm/sec
		df.setRotateSpeed(180); // deg/sec
		df.forward();
		Delay.msDelay(5000);
		df.stop();
    	*/

		df.forward();
		Delay.msDelay(5000);
		df.stop();
	}

}
