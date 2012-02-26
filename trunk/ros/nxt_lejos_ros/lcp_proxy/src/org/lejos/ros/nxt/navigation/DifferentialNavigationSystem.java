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
import org.ros.message.geometry_msgs.Pose2D;
import org.ros.message.geometry_msgs.PoseStamped;
import org.ros.message.geometry_msgs.Quaternion;
import org.ros.message.geometry_msgs.TransformStamped;
import org.ros.message.geometry_msgs.Twist;
import org.ros.message.nav_msgs.Odometry;
import org.ros.message.nxt_lejos_ros_msgs.DNSCommand;
import org.ros.message.tf.tfMessage;
import org.ros.message.turtlesim.Velocity;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

/**
 * 
 * Uses leJOS navigation classes to control a robot with differential steering.
 * 
 * @author Juan Antonio Brenha Moral and Lawrie Griffiths
 *
 */
public class DifferentialNavigationSystem extends NXTDevice implements INXTDevice {
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
	private TransformStamped ftr = new TransformStamped();
	
	private tfMessage tf = new tfMessage();
     
    private Publisher<tfMessage> tfTopic = null;
    private String tfMessageType = "tf/tfMessage";
    private Node node;
    
    private Odometry od = new Odometry();
    
    private Publisher<Odometry> odomTopic = null;
    private String odomMessageType = "nav_msgs/Odometry";
    
    private PoseStamped poseStamped = new PoseStamped();
    private Publisher<PoseStamped> psTopic = null;
    private String psMessageType = "geometry_msgs/PoseStamped";
	
	public DifferentialNavigationSystem(Node node, String port1, String port2, float _wheelDiameter, float _trackWidth, boolean _reverse){		
		this.node = node;
		
		//TODO: Exception if letters are the same
		
		if (port1.equals("A")) { 
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
		} else if (port2.equals("C")) {
			rightMotor = Motor.C;
		}
		
		wheelDiameter = _wheelDiameter;
		trackWidth = _trackWidth;
		reverse = _reverse;
		
    	df = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
    	posep = new OdometryPoseProvider(df);  	
	}
	
	public void publishTopic(Node node) {
		tfTopic = node.newPublisher("tf", tfMessageType);
		odomTopic = node.newPublisher("odom", odomMessageType);
		psTopic = node.newPublisher("pose", psMessageType);
	}

	public void updateTopic(Node node, long seq) {
		poseToTransform(posep.getPose());
		tfTopic.publish(tf);
		odomTopic.publish(od);
		psTopic.publish(poseStamped);
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
	
	/**
	 * Update the leJOS pose in the odometry pose provider from the ROS Pose2D message
	 * 
	 * @param pose the Pose2D message
	 */
	public void updatePose(Pose2D pose) {
		posep.setPose(new Pose((float) pose.x * 100f, (float) pose.y * 100f, (float) Math.toDegrees(pose.theta)));
	}
	
	double oldAngular, oldLinear;
	/**
	 * Apply the linear and angular velocities in the ros Twist method to the robot.
	 * Currently only does travel and rotate actions, not arc.
	 * 
	 * @param t the ros Twist message
	 */
	public void updateTwist(Twist t) {	
		double linear = t.linear.x;
		double angular = t.angular.z;
		boolean suppress = false;
		
		
		System.out.println("Velocity: linear = " + linear + ", angular = " + angular);
		if (suppress) {
			System.out.println("Suppresssed");
			return;
		}
		
		if (angular != 0 && angular != oldAngular) df.setRotateSpeed(Math.abs(Math.toDegrees(angular)));
		if (linear != 0 && linear != oldLinear) df.setTravelSpeed(Math.abs(linear * 100));
		
		if (linear != 0 && linear != oldLinear) {
			oldAngular = angular;
			oldLinear = linear;
			boolean forward = (linear > 0);
			if (forward) {
				System.out.println("Twist: Steer " + Math.toDegrees(angular));
				df.steer(Math.toDegrees(angular));
			} else {
				System.out.println("Twist: Steer backwards " + Math.toDegrees(angular));
				df.steerBackward(Math.toDegrees(angular));
			}
		} else if (angular != 0 && angular != oldAngular && linear == 0) {
			System.out.println("Twist: Rotate " + angular);
			oldAngular = angular;
			oldLinear = linear;
			boolean left = (angular > 0);
			if (left) df.rotateLeft();
			else df.rotateRight();
		} else if (linear == 0 && angular == 0 && (linear != oldLinear || angular != oldAngular)) {
			oldAngular = angular;
			oldLinear = linear;
			System.out.println("Twist: Stopping");
			df.stop();
		}	
	}
	
	/*
	 * Convert a leJOS pose into a world to robot transform
	 */
	private void poseToTransform(Pose p) {
		double attitude = Math.toRadians(p.getHeading());
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
	   	
		Quaternion q = new Quaternion();
		q.w = c1c2*c3 - s1s2*s3;
		q.x =c1c2*s3 + s1s2*c3;
		q.y =s1*c2*c3 + c1*s2*s3;
		q.z =c1*s2*c3 - s1*c2*s3;
		
		double x = p.getX() / 100;
		double y = p.getY() /100;
	    
		// transform for robot in the world
	    tr.header.frame_id = "/world"; 
	    tr.header.stamp = node.getCurrentTime();
	    tr.child_frame_id = "/robot";
	    
		tr.transform.translation.x = x;
		tr.transform.translation.y = y;
		tr.transform.translation.z = 0;
		
		tr.transform.rotation = q;
	    
	    tf.transforms.add(tr);
	    
	    // Transform for front tip of the robot
	    ftr.header.frame_id = "/robot"; 
	    ftr.header.stamp = node.getCurrentTime();
	    ftr.child_frame_id = "/front";
	    
		ftr.transform.translation.x = 0.1; // 10cm from center
		ftr.transform.translation.y = 0;
		ftr.transform.translation.z = 0;
		
		ftr.transform.rotation.w = 1; // No rotation
	    
	    tf.transforms.add(ftr);
	    
	    od.header.stamp = node.getCurrentTime();
	    od.header.frame_id = "/world";
	    od.child_frame_id = "/robot";
	    
	    od.pose.pose.position.x = x;
	    od.pose.pose.position.y = y;
	    od.pose.pose.position.z = 0;
	    
	    od.pose.pose.orientation = q;
	    
	    boolean moving = df.isMoving();
	    Move.MoveType moveType = df.getMovement().getMoveType();

	    od.twist.twist.linear.x = (moveType == Move.MoveType.ROTATE  ? 0 : (moving ? Math.toRadians(df.getTravelSpeed()) : 0));
	    od.twist.twist.linear.y = 0;
	    od.twist.twist.linear.z = 0;
	        
	    od.twist.twist.angular.x = 0;
	    od.twist.twist.angular.y = 0;	    
	    od.twist.twist.angular.z = (moving  ? Math.toRadians(df.getTurnRate()): 0);
	    
	    poseStamped.header.stamp = node.getCurrentTime();	    
	    poseStamped.header.frame_id = "/world";
	    
	    poseStamped.pose.position.x = x;
	    poseStamped.pose.position.y = y;
	    poseStamped.pose.position.z = 0;
	    poseStamped.pose.orientation = q;
	}
	
	public DifferentialPilot getPilot() {
		return df;
	}
}
