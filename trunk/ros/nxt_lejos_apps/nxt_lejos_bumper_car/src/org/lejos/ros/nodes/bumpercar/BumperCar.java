package org.lejos.ros.nodes.bumpercar;

import lejos.util.Delay;
import org.ros.message.MessageListener;
import nxt_lejos_msgs.DNSCommand;
import sensor_msgs.Range;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

/**
 * A bumper car using the lcp_proxy API.
 * Drives forward using dns_command forward.
 * Uses the ultrasonic_sensor topic to detect obstacles and then 
 * uses a sequence of dns commands to avoid the obstacle.
 * 
 * @author Lawrie Griffiths
 *
 */
public class BumperCar implements NodeMain {	
    private DNSCommand message; 
    private Publisher<DNSCommand> topic;
    private String messageType = "nxt_lejos_msgs/DNSCommand";
    private boolean processing = false;
    private static final float limit = 0.6f; // minimum distance to obstacle

	@Override
	public void onStart(ConnectedNode node) {	
		
		message = node.getTopicMessageFactory().newFromType(DNSCommand._TYPE);
				
		//Subscription to ultrasonic_sensor topic
        Subscriber<Range> subscriberRange =
	        node.newSubscriber("ultrasonic_sensor", "sensor_msgs/Range");
        subscriberRange.addMessageListener(new MessageListener<Range>() {
	    	@Override
	    	public void onNewMessage(Range msg) {
	    		if (msg.getRange() < 1.0) System.out.println("Range is " + msg.getRange() + " (" + processing + ")");
				if (msg.getRange() < limit && !processing) {
					processing = true;
					message.setType("travel"); 
					message.setValue(-20);
					topic.publish(message); // Go back 20cm
					Delay.msDelay(2000);
					message.setType("rotate");  
					message.setValue(30);
					topic.publish(message); // Rotate 30 degrees
					Delay.msDelay(1000);
					message.setType("forward");
					message.setValue(0);
					topic.publish(message);  // Go forward
					//Delay.msDelay(500);
					processing = false;
				}		
	    	}
	    });
        
        // Create publisher for dns_command
        topic = node.newPublisher("dns_command", messageType);
        Delay.msDelay(1000);
        
        // Set the robot travel speed
        message.setType("setTravelSpeed");
        message.setValue(20);
		topic.publish(message);	
		Delay.msDelay(1000);
		
		// Set the rotate speed
        message.setType("setRotateSpeed");
        message.setValue(100);
		topic.publish(message);
		Delay.msDelay(1000);
		
		// Start going forward
		message.setType("forward");
		message.setValue(0);
		topic.publish(message);
		Delay.msDelay(300);
	}

	@Override
	public GraphName getDefaultNodeName() {
		return new GraphName("nxt_lejos_ros_robots/bumperCar");
	}
	
	@Override
	public void onShutdown(Node arg0) {
		// Stop the robot
		message.setType("stop");
		message.setValue(0);
		topic.publish(message);
		Delay.msDelay(300);
	}

	@Override
	public void onError(Node arg0, Throwable arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onShutdownComplete(Node arg0) {
		//Do nothing
	
	}
}
