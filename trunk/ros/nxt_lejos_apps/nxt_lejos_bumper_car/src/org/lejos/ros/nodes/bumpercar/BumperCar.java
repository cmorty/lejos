package org.lejos.ros.nodes.bumpercar;

import lejos.util.Delay;
import org.ros.message.MessageListener;
import org.ros.message.nxt_lejos_msgs.DNSCommand;
import org.ros.message.sensor_msgs.Range;
import org.ros.namespace.GraphName;
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
    private DNSCommand message = new DNSCommand(); 
    private Publisher<DNSCommand> topic;
    private String messageType = "nxt_lejos_msgs/DNSCommand";
    private boolean processing = false;
    private static final float limit = 0.6f; // minimum distance to obstacle

	@Override
	public void onStart(Node node) {		
		//Subscription to ultrasonic_sensor topic
        Subscriber<Range> subscriberRange =
	        node.newSubscriber("ultrasonic_sensor", "sensor_msgs/Range");
        subscriberRange.addMessageListener(new MessageListener<Range>() {
	    	@Override
	    	public void onNewMessage(Range msg) {
	    		if (msg.range < 1.0) System.out.println("Range is " + msg.range + " (" + processing + ")");
				if (msg.range < limit && !processing) {
					processing = true;
					message.type = "travel"; 
					message.value = -20;
					topic.publish(message); // Go back 20cm
					Delay.msDelay(2000);
					message.type = "rotate";  
					message.value = 30;
					topic.publish(message); // Rotate 30 degrees
					Delay.msDelay(1000);
					message.type = "forward";
					message.value = 0;
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
        message.type = "setTravelSpeed";
        message.value = 20;
		topic.publish(message);	
		Delay.msDelay(1000);
		
		// Set the rotate speed
        message.type = "setRotateSpeed";
        message.value = 100;
		topic.publish(message);
		Delay.msDelay(1000);
		
		// Start going forward
		message.type = "forward";
		message.value = 0;
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
		message.type = "stop";
		message.value = 0;
		topic.publish(message);
		Delay.msDelay(300);
	}

	@Override
	public void onShutdownComplete(Node arg0) {
		// Nothing	
	}
}
