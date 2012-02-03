package org.lejos.ros.nxt.navigation;

import lejos.nxt.Motor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.message.nxt_lejos_ros_msgs.DNSCommand;
import org.ros.message.turtlesim.Velocity;
import org.ros.node.Node;

public class DifferentialNavigationSystem extends NXTDevice implements INXTDevice{
	
	//LeJOS
	private RegulatedMotor leftMotor;
	private RegulatedMotor rightMotor;
	private float wheelDiameter;
	private float trackWidth;
	private boolean reverse;
	
	private DifferentialPilot df;
	
	public DifferentialNavigationSystem(String port1, String port2, float _wheelDiameter, float _trackWidth, boolean _reverse){		
		//TODO: Exception if letters are the same
		
		if (port1.equals("A")){ 
			leftMotor = Motor.A;
		} else if (port1.equals("B")) {
			leftMotor = Motor.B;
		} else if (port1.equals("C")) {
			leftMotor = Motor.C;
		}

		if (port2.equals("A")) {
			rightMotor = Motor.A;
		} else if (port2.equals("B")) {
			rightMotor = Motor.B;
		}else if (port2.equals("C")) {
			rightMotor = Motor.C;
		}
		
		wheelDiameter = _wheelDiameter;
		trackWidth = _trackWidth;
		reverse = _reverse;
		
    	df = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
	}
	
	public void publishTopic(Node node) {
	}

	public void updateTopic() {		
	}
	
	public void updateActuatorSystem(DNSCommand cmd){
		String type = cmd.type;
		double value = cmd.value;
		
		System.out.println("DNS cmd = " + type + " " + value);
		
		if (type.equals("forward")) df.forward();
		else if (type.equals("backward")) df.backward();
		else if (type.equals("stop")) df.stop();
		else if (type.equals("rotateLeft")) df.rotateLeft();
		else if (type.equals("rotateRight")) df.rotateRight();
		else if (type.equals("travel")) df.travel(value);
		else if (type.equals("rotate")) df.rotate(value);
	}
	
	public void updateVelocity(Velocity v) {	
		float linear = v.linear;
		float angular = v.angular;
		
		System.out.println("Velocity: linear = " + v.linear + ", angular = " + angular);
		
		if (linear != 0 && angular == 0) {
			boolean forward = (linear > 0);
			if (forward) df.forward();
			else df.backward();
		} else if (angular != 0 && linear == 0) {
			boolean left = (angular > 0);
			if (left) df.rotateLeft();
			else df.rotateRight();
		}
		
		Delay.msDelay(1000);	
		df.stop();	
	}
}
