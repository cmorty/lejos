package org.lejos.ros.nodes.jointcommand;

import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import com.google.common.base.Preconditions;

/**
 * This class explain the way to publish data on ROS.
 * Topics are data to be used by other nodes.
 *  
 * @author jabrena
 *
 */
public class JointCommandTest implements NodeMain{

	
	private final int BATTERY_LIMIT = 6000;
	
	private Node node;
	
	@Override
	public void onShutdown(Node arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onShutdownComplete(Node arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStart(final Node node) {
	    Preconditions.checkState(this.node == null);
	    this.node = node;
		
	    try {
	    	
		    
			//TODO: Change the type of the topic. voltage is a Integer
	        Publisher<org.ros.message.nxt_msgs.JointCommand> publisher =
	            node.newPublisher("joint_command", "nxt_msgs/JointCommand");
	        int seq = 0;
	        
	        while (true) {
	        	
	        	org.ros.message.nxt_msgs.JointCommand message = new org.ros.message.nxt_msgs.JointCommand();
	        	message.name = "l_motor_joint";
	        	message.effort = 360.0f;
	        	publisher.publish(message);
	          
	        	node.getLog().info(seq + " State: " + message.name);
	          
	          seq++;
	          Thread.sleep(5000);
	        }
	    } catch (Exception e) {
	    	if (node != null) {
	    		node.getLog().fatal(e);
	    	} else {
	    		e.printStackTrace();
	    	}
	    }
	 }

}
