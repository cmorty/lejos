package org.lejos.ros.nxt.navigation;

import lejos.nxt.Motor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import lejos.util.PilotProps;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.message.nxt_lejos_ros_msgs.DNSCommand;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class DifferentialNavigationSystem extends NXTDevice implements INXTDevice{

	//ROS
	
	//TODO: Use a new nxt_msgs type to manage this topic
	//Topic 1
    final org.ros.message.std_msgs.String message = new org.ros.message.std_msgs.String(); 
	Publisher<org.ros.message.std_msgs.String> topic = null;
	final String messageType = "std_msgs/String";
	
    final org.ros.message.geometry_msgs.Quaternion quaternion = new org.ros.message.geometry_msgs.Quaternion();
    
    //Topic 2
    final org.ros.message.geometry_msgs.Transform tr = new org.ros.message.geometry_msgs.Transform();
    Publisher<org.ros.message.std_msgs.String> topic2 = null;
    final String messageType2 = "geometry_msgs/Transform";
    final String topicName2 = "tr";
    
    //Topic 3
    final org.ros.message.nav_msgs.Odometry odom = new org.ros.message.nav_msgs.Odometry();
    Publisher<org.ros.message.std_msgs.String> topic3 = null;
    final String messageType3 = "nav_msgs/Odometry";
    final String topicName3 = "odom";
	
	//LeJOS
	private RegulatedMotor leftMotor;
	private RegulatedMotor rightMotor;
	private float wheelDiameter;
	private float trackWidth;
	private boolean reverse;
	
	private DifferentialPilot df;
	private PoseProvider posep;
	
	public DifferentialNavigationSystem(String port1, String port2, float _wheelDiameter, float _trackWidth, boolean _reverse){
		
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
    	posep = new OdometryPoseProvider(df);
	}
	
	public void publishTopic(Node node) {

		//Original topic
		topic =  node.newPublisher("" + super.getName(), messageType);
		
		topic2 =  node.newPublisher(topicName2, messageType2);
		topic3 =  node.newPublisher(topicName3, messageType3);
	}

	public void updateTopic() {

		message.data = "af";		
		topic.publish(message);		
	}
	
	public void updateActuatorSystem(DNSCommand cmd){
		
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
