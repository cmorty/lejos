package org.lejos.ros.sensors;

import lejos.robotics.navigation.Pose;

import org.lejos.ros.nodes.ROSProxy;
import geometry_msgs.PoseStamped;
import geometry_msgs.Quaternion;
import geometry_msgs.TransformStamped;
import nav_msgs.Odometry;
import tf.tfMessage;
import org.ros.node.ConnectedNode;
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
	
	private tfMessage tf = node.getTopicMessageFactory().newFromType(tfMessage._TYPE);
	private TransformStamped tr = node.getTopicMessageFactory().newFromType(TransformStamped._TYPE);
	private Odometry od = node.getTopicMessageFactory().newFromType(Odometry._TYPE);
	private PoseStamped poseStamped = node.getTopicMessageFactory().newFromType(PoseStamped._TYPE);
	
	protected ROSProxy proxy;
	
	public OdometrySensor(ROSProxy proxy, ConnectedNode node, double desiredFrequency) {
		super(node,null,desiredFrequency);
		this.proxy = proxy;
		tfTopic = node.newPublisher("tf", tfMessageType);
		odomTopic = node.newPublisher("odom", odomMessageType);
		poseTopic = node.newPublisher("pose", poseMessageType);
	}
	
	@Override
	public synchronized void publishMessage(double value) {
		//System.out.println("Publishing transforms");
		poseToTransform(node, proxy.getPose(), proxy.getAngularVelocity(), proxy.getLinearVelocity());
		tfTopic.publish(tf);
		odomTopic.publish(od);
		poseTopic.publish(poseStamped);
	}
	
	/*
	 * Convert a leJOS pose into a world to robot transform
	 */
	public void poseToTransform(ConnectedNode node, Pose p, float angularVelocity, float linearVelocity) {
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
		
		tf = node.getTopicMessageFactory().newFromType(tfMessage._TYPE);

		// transform for robot in the world
	    tr.getHeader().setFrameId(WORLD_FRAME); 
	    tr.getHeader().setStamp(node.getCurrentTime());
	    tr.setChildFrameId(ROBOT_FRAME);
	    
	    tr.getTransform().getTranslation().setX(x);
	    tr.getTransform().getTranslation().setY(y);
		tr.getTransform().getTranslation().setZ(0);
		
		tr.getTransform().setRotation(q);
	    
		java.util.List<TransformStamped> trs = tf.getTransforms();
		trs.add(tr);
	    
	    od.getHeader().setStamp(node.getCurrentTime());
	    od.getHeader().setFrameId(WORLD_FRAME);
	    od.setChildFrameId(ROBOT_FRAME);
	    
	    od.getPose().getPose().getPosition().setX(x);
	    od.getPose().getPose().getPosition().setY(y);
	    od.getPose().getPose().getPosition().setZ(0);
	    
	    od.getPose().getPose().setOrientation(q);

	    od.getTwist().getTwist().getLinear().setX(linearVelocity);
	    od.getTwist().getTwist().getLinear().setY(0);
	    od.getTwist().getTwist().getLinear().setZ(0);
	        
	    od.getTwist().getTwist().getAngular().setX(0);
	    od.getTwist().getTwist().getAngular().setY(0);	    
	    od.getTwist().getTwist().getAngular().setZ(angularVelocity);
	    
	    poseStamped.getHeader().setStamp(node.getCurrentTime());	    
	    poseStamped.getHeader().setFrameId(WORLD_FRAME);
	    
	    poseStamped.getPose().getPosition().setX(x);
	    poseStamped.getPose().getPosition().setY(y);
	    poseStamped.getPose().getPosition().setZ(0);
	    poseStamped.getPose().setOrientation(q);
	}
}
