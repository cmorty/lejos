package org.lejos.ros.nodes.bumpercar;

import lejos.util.Delay;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

public class BumperCar implements NodeMain {
	
    final org.ros.message.nxt_lejos_ros_msgs.DNSCommand message = new org.ros.message.nxt_lejos_ros_msgs.DNSCommand(); 
    Publisher<org.ros.message.nxt_lejos_ros_msgs.DNSCommand> topic = null;
    String messageType = "nxt_lejos_ros_msgs/DNSCommand";
    
    private float range = 255;

	@Override
	public void onShutdown(Node arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShutdownComplete(Node arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(Node node) {
		
		//Subscription to ultrasonic message
        Subscriber<org.ros.message.sensor_msgs.Range> subscriberMotor =
	        node.newSubscriber("ultrasonic_sensor", "sensor_msgs/Range");
        subscriberMotor.addMessageListener(new MessageListener<org.ros.message.sensor_msgs.Range>() {
	    	@Override
	    	public void onNewMessage(org.ros.message.sensor_msgs.Range msg) {
	    		range = (float)  msg.range;
	    		System.out.println("Range is " + range);
				if (range < 50) {
					message.type = "travel";
					message.value = -20;
					topic.publish(message);
					Delay.msDelay(300);
					message.type = "rotate";
					message.value = 30;
					topic.publish(message);
					Delay.msDelay(300);
					message.type = "forward";
					topic.publish(message);
				}		
	    	}
	    });
        
        topic = node.newPublisher("dns_command", messageType);
        Delay.msDelay(1000);
        
        message.type = "setTravelSpeed";
        message.value = 10;
		topic.publish(message);
		
		Delay.msDelay(500);
		
        message.type = "setRotateSpeed";
        message.value = 30;
		topic.publish(message);
		
		Delay.msDelay(500);
		
		// Go forward
		message.type = "forward";
		topic.publish(message);
	}

	@Override
	public GraphName getDefaultNodeName() {
		// TODO Auto-generated method stub
		return null;
	}

}
