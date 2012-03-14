package org.lejos.ros.sensors;

import lejos.robotics.navigation.Pose;

import org.lejos.ros.nodes.ROSProxy;
import org.ros.message.geometry_msgs.PoseStamped;
import org.ros.message.geometry_msgs.Quaternion;
import org.ros.message.geometry_msgs.TransformStamped;
import org.ros.message.nav_msgs.Odometry;
import org.ros.message.tf.tfMessage;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class OdometrySensor extends Sensor {
	protected String tfMessageType = "tf/tfMessage";
	protected Publisher<tfMessage> tfTopic;
	
	protected String odomMessageType = "nav_msgs/Odometry";
	protected Publisher<Odometry> odomTopic;
	
	protected String poseMessageType = "geometry_msgs/PoseStamped";
	protected Publisher<PoseStamped> poseTopic;
	
	public static final String WORLD_FRAME = "/world";
	public static final String ROBOT_FRAME = "/robot";
	
	private tfMessage tf = new tfMessage();
	private TransformStamped tr = new TransformStamped();
	private Odometry od = new Odometry();
	private PoseStamped poseStamped = new PoseStamped();
	
	protected ROSProxy proxy;
	
	public OdometrySensor(ROSProxy proxy, Node node, double desiredFrequency) {
		super(node,null,desiredFrequency);
		this.proxy = proxy;
		tfTopic = node.newPublisher("tf", tfMessageType);
		odomTopic = node.newPublisher("odom", odomMessageType);
		poseTopic = node.newPublisher("pose", poseMessageType);
	}
	
	@Override
	public void publishMessage(double value) {
		//System.out.println("Publishing transforms");
		poseToTransform(node, proxy.getPose(), proxy.getAngularVelocity(), proxy.getLinearVelocity());
		tfTopic.publish(tf);
		odomTopic.publish(od);
		poseTopic.publish(poseStamped);
	}
	
	/*
	 * Convert a leJOS pose into a world to robot transform
	 */
	public void poseToTransform(Node node, Pose p, float angularVelocity, float linearVelocity) {
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
	   	
		Quaternion q = new Quaternion();
		q.w = c1c2*c3 - s1s2*s3;
		q.x = c1c2*s3 + s1s2*c3;
		q.y = s1*c2*c3 + c1*s2*s3;
		q.z = c1*s2*c3 - s1*c2*s3;
		
		double x = p.getX() / 100;
		double y = p.getY() /100;

		// transform for robot in the world
	    tr.header.frame_id = WORLD_FRAME; 
	    tr.header.stamp = node.getCurrentTime();
	    tr.child_frame_id = ROBOT_FRAME;
	    
		tr.transform.translation.x = x;
		tr.transform.translation.y = y;
		tr.transform.translation.z = 0;
		
		tr.transform.rotation = q;
	    
	    tf.transforms.add(tr);
	    
	    od.header.stamp = node.getCurrentTime();
	    od.header.frame_id = WORLD_FRAME;
	    od.child_frame_id = ROBOT_FRAME;
	    
	    od.pose.pose.position.x = x;
	    od.pose.pose.position.y = y;
	    od.pose.pose.position.z = 0;
	    
	    od.pose.pose.orientation = q;

	    od.twist.twist.linear.x = linearVelocity;
	    od.twist.twist.linear.y = 0;
	    od.twist.twist.linear.z = 0;
	        
	    od.twist.twist.angular.x = 0;
	    od.twist.twist.angular.y = 0;	    
	    od.twist.twist.angular.z = angularVelocity;
	    
	    poseStamped.header.stamp = node.getCurrentTime();	    
	    poseStamped.header.frame_id = WORLD_FRAME;
	    
	    poseStamped.pose.position.x = x;
	    poseStamped.pose.position.y = y;
	    poseStamped.pose.position.z = 0;
	    poseStamped.pose.orientation = q;
	}
}
