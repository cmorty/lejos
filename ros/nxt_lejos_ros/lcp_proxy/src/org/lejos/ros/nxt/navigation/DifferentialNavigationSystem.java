package org.lejos.ros.nxt.navigation;

import lejos.nxt.Motor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;
import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.message.geometry_msgs.TransformStamped;
import org.ros.message.geometry_msgs.Twist;
import org.ros.message.nav_msgs.Odometry;
import org.ros.message.nxt_lejos_ros_msgs.DNSCommand;
import org.ros.message.tf.tfMessage;
import org.ros.message.turtlesim.Velocity;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class DifferentialNavigationSystem extends NXTDevice implements INXTDevice{
	
	// leJOS
	private RegulatedMotor leftMotor;
	private RegulatedMotor rightMotor;
	private float wheelDiameter;
	private float trackWidth;
	private boolean reverse;
	
	private DifferentialPilot df;
	private PoseProvider posep;
	
	// ROS
	private TransformStamped tr = new TransformStamped();
	private tfMessage tf = new tfMessage();

    private final org.ros.message.geometry_msgs.Pose2D message = new org.ros.message.geometry_msgs.Pose2D(); 
    private Publisher<org.ros.message.geometry_msgs.Pose2D> topic = null;
    private String messageType = "geometry_msgs/Pose2D";
     
    private Publisher<tfMessage> tfTopic = null;
    private String tfMessageType = "tf/tfMessage";
    private Node node;
    
    private Odometry od = new Odometry();
    
    private Publisher<Odometry> odomTopic = null;
    private String odomMessageType = "nav_msgs/Odometry";
	
	public DifferentialNavigationSystem(Node node, String port1, String port2, float _wheelDiameter, float _trackWidth, boolean _reverse){		
		this.node = node;
		
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
    	posep = new OdometryPoseProvider(df);
    	
	}
	
	public void publishTopic(Node node) {
		topic = node.newPublisher("pose", messageType);
		tfTopic = node.newPublisher("tf", tfMessageType);
		odomTopic = node.newPublisher("odom", odomMessageType);
	}

	public void updateTopic() {
		Pose p = posep.getPose();
		message.theta = p.getHeading();
		message.x = p.getX();
		message.y = p.getY();
		topic.publish(message);
		poseToTransform(p);
		tfTopic.publish(tf);
		odomTopic.publish(od);
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
		else if (type.equals("setTravelSpeed")) df.setTravelSpeed(value);
		else if (type.equals("setRotateSpeed")) df.setRotateSpeed(value);
		else if (type.equals("setAcceleration")) df.setAcceleration((int) value);
	}
	
	public void updateVelocity(Velocity v) {	
		float linear = v.linear;
		float angular = v.angular;
		
		System.out.println("Velocity: linear = " + linear + ", angular = " + angular);
		
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
	
	public void updateTwist(Twist t) {	
		double linear = t.linear.x;
		double angular = t.angular.z;
		
		System.out.println("Velocity: linear = " + linear + ", angular = " + angular);
		
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
	
	private long seq = 0;
	
	/*
	 * Convert a leJOS pose into a world to robot transform
	 */
	private void poseToTransform(Pose p) {
		double attitude = 0;
		double bank = 0;
		double heading = Math.toRadians(p.getHeading());
	    double c1 = Math.cos(heading/2);
	    double s1 = Math.sin(heading/2);
	    double c2 = Math.cos(attitude/2);
	    double s2 = Math.sin(attitude/2);
	    double c3 = Math.cos(bank/2);
	    double s3 = Math.sin(bank/2);
	    double c1c2 = c1*c2;
	    double s1s2 = s1*s2;
	    
	    tr.header.frame_id = "world"; 
	    tr.header.stamp = node.getCurrentTime();
	    tr.header.seq = seq++;
	    tr.child_frame_id = "robot";
	    
		tr.transform.translation.x = p.getX();
		tr.transform.translation.y = p.getY();
		tr.transform.translation.z = 0;
		
	    tr.transform.rotation.w =c1c2*c3 - s1s2*s3;
	    tr.transform.rotation.x =c1c2*s3 + s1s2*c3;
	    tr.transform.rotation.y =s1*c2*c3 + c1*s2*s3;
	    tr.transform.rotation.z =c1*s2*c3 - s1*c2*s3;
	    
	    tf.transforms.add(tr);
	    
	    od.header.seq = seq;
	    od.header.stamp = node.getCurrentTime();
	    od.header.frame_id = "world";
	    od.child_frame_id = "robot";
	    
	    od.pose.pose.position.x = p.getX();
	    od.pose.pose.position.y = p.getY();
	    od.pose.pose.position.z = 0;
	    
	    od.pose.pose.orientation.w =c1c2*c3 - s1s2*s3;
	    od.pose.pose.orientation.x =c1c2*s3 + s1s2*c3;
	    od.pose.pose.orientation.y =s1*c2*c3 + c1*s2*s3;
	    od.pose.pose.orientation.z =c1*s2*c3 - s1*c2*s3;
	    
	    od.twist.twist.angular.x = 0;
	    od.twist.twist.angular.y = 0;
	    System.out.println("Move type is " + df.getMovement().getMoveType());
	    
	    od.twist.twist.angular.z = (df.isMoving() && df.getMovement().getMoveType() == Move.MoveType.ROTATE ? df.getRotateSpeed() : 0);
	    
	    od.twist.twist.linear.x = (df.isMoving() && df.getMovement().getMoveType() == Move.MoveType.TRAVEL  ? df.getTravelSpeed() : 0);
	    od.twist.twist.linear.y = 0;
	    od.twist.twist.linear.z = 0;	   
	}
}
