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
import geometry_msgs.Pose2D;
import geometry_msgs.PoseStamped;
import geometry_msgs.Quaternion;
import geometry_msgs.TransformStamped;
import geometry_msgs.Twist;
import nav_msgs.Odometry;
import nxt_lejos_msgs.DNSCommand;
import tf.tfMessage;
import turtlesim.Velocity;
import org.ros.node.Node;
import org.ros.node.ConnectedNode;
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
	
	private static final String WORLD_FRAME = "/world";
	private static final String ROBOT_FRAME = "/robot";
	private static final String FRONT_FRAME = "/front";
	private static final double FRONT_OFFSET = 0.1;
	
	private TransformStamped tr;
	private TransformStamped ftr;
	
	private tfMessage tf;
     
    private Publisher<tfMessage> tfTopic = null;
    private String tfMessageType = "tf/tfMessage";
    private ConnectedNode node;
    
    private Odometry od;
    
    private Publisher<Odometry> odomTopic = null;
    private String odomMessageType = "nav_msgs/Odometry";
    
    private PoseStamped poseStamped;
    private Publisher<PoseStamped> psTopic = null;
    private String psMessageType = "geometry_msgs/PoseStamped";
	
	private double oldAngular, oldLinear;
	
	public DifferentialNavigationSystem(ConnectedNode node, String port1, String port2, float _wheelDiameter, float _trackWidth, boolean _reverse){		
		this.node = node;
		
		tr = node.getTopicMessageFactory().newFromType(TransformStamped._TYPE);
		ftr = node.getTopicMessageFactory().newFromType(TransformStamped._TYPE);
		tf = node.getTopicMessageFactory().newFromType(tfMessage._TYPE);
		od = node.getTopicMessageFactory().newFromType(Odometry._TYPE);
		poseStamped = node.getTopicMessageFactory().newFromType(PoseStamped._TYPE);
		
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
	
	public void publishTopic(ConnectedNode node) {
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
		String type = cmd.getType();
		double value = cmd.getValue();
		
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
	
	/**
	 * Set the linear and angular velocity.
	 * Used by turtlesim teleoperation
	 * 
	 * @param v the Velocity message
	 */
	public void updateVelocity(Velocity v) {	
		float linear = v.getLinear();
		float angular = v.getAngular();
		
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
		posep.setPose(new Pose((float) pose.getX() * 100f, (float) pose.getY() * 100f, (float) Math.toDegrees(pose.getTheta())));
	}

	/**
	 * Apply the linear and angular velocities in the ros Twist method to the robot.
	 * Currently only does travel and rotate actions, not arc.
	 * 
	 * @param t the ros Twist message
	 */
	public void updateTwist(Twist t) {	
		double linear = t.getLinear().getX(); // Movement relative to robot co-ordinates
		double angular = t.getAngular().getZ(); 
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
		double attitude = Math.toRadians(p.getHeading()); // Why attitude, not heading?
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
	   	
		Quaternion q = node.getTopicMessageFactory().newFromType(Quaternion._TYPE);
		q.setW(c1c2*c3 - s1s2*s3);
		q.setX(c1c2*s3 + s1s2*c3);
		q.setY(s1*c2*c3 + c1*s2*s3);
		q.setZ(c1*s2*c3 - s1*c2*s3);
		
		double x = p.getX() / 100;
		double y = p.getY() /100;
	    
		// transform for robot in the world
	    tr.getHeader().setFrameId(WORLD_FRAME); 
	    tr.setChildFrameId(ROBOT_FRAME);
	    
		tr.getTransform().getTranslation().setX(x);
		tr.getTransform().getTranslation().setY(y);
		tr.getTransform().getTranslation().setZ(0);
		
		tr.getTransform().setRotation(q);
	    
	    tf.getTransforms().add(tr);
	    
	    // Transform for front tip of the robot
	    ftr.getHeader().setFrameId(ROBOT_FRAME);
	    ftr.getHeader().setStamp(node.getCurrentTime());
	    ftr.setChildFrameId(FRONT_FRAME);
	    
		ftr.getTransform().getTranslation().setX(FRONT_OFFSET); // Distance from center to front of robot
		ftr.getTransform().getTranslation().setY(0);
		ftr.getTransform().getTranslation().setZ(0);
		
		ftr.getTransform().getRotation().setW(1); // No rotation
	    
	    tf.getTransforms().add(ftr);
	    
	    od.getHeader().setStamp(node.getCurrentTime());
	    od.getHeader().setFrameId(WORLD_FRAME);
	    od.setChildFrameId(ROBOT_FRAME);
	    
	    od.getPose().getPose().getPosition().setX(x);
	    od.getPose().getPose().getPosition().setY(y);
	    od.getPose().getPose().getPosition().setZ(0);
	    
	    od.getPose().getPose().setOrientation(q);
	    
	    boolean moving = df.isMoving();
	    Move.MoveType moveType = df.getMovement().getMoveType();

	    od.getTwist().getTwist().getLinear().setX((moveType == Move.MoveType.ROTATE  ? 0 : (moving ? Math.toRadians(df.getTravelSpeed()) : 0)));
	    od.getTwist().getTwist().getLinear().setY(0);
	    od.getTwist().getTwist().getLinear().setZ(0);
	        
	    od.getTwist().getTwist().getAngular().setX(0);
	    od.getTwist().getTwist().getAngular().setY(0);	    
	    od.getTwist().getTwist().getAngular().setZ((moving  ? Math.toRadians(df.getTurnRate()): 0));
	    
	    poseStamped.getHeader().setStamp(node.getCurrentTime());	    
	    poseStamped.getHeader().setFrameId(WORLD_FRAME);
	    
	    poseStamped.getPose().getPosition().setX(x);
	    poseStamped.getPose().getPosition().setY(y);
	    poseStamped.getPose().getPosition().setZ(0);
	    poseStamped.getPose().setOrientation(q);
	}
	
	public DifferentialPilot getPilot() {
		return df;
	}

	@Override
	public void publishTopic(Node node) {
		// TODO Auto-generated method stub
		
	}
}
