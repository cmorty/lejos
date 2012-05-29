package org.lejos.ros.nodes.bumpercar;

import org.lejos.pccomm.utils.SimpleConnector;
import org.lejos.ros.nodes.LEJOSNode;
import org.ros.message.MessageListener;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import com.google.common.base.Preconditions;

public class BumperCar extends LEJOSNode{

	public void onStart(final Node node) {
	    Preconditions.checkState(this.node == null);
	    this.node = node;

	    //Connection
	    boolean connetionStatus = false;
	    final String BRICK_NAME = "ROSBRICK1";
	    
		connetionStatus = SimpleConnector.connectByBT(BRICK_NAME);
		
		if(connetionStatus){
		
			final Robot robot = new Robot();
			
			try{

				//Ultrasonic Sensor
		        Publisher<org.ros.message.std_msgs.Int32> range1 =
		            node.newPublisher("range1", "std_msgs/Int32");
				
		        //Battery
		        Publisher<org.ros.message.std_msgs.Int32> battery =
		            node.newPublisher("battery", "std_msgs/Int32");
		        
		        //Robot Receives CMD to execute
			    Subscriber<org.ros.message.std_msgs.Int32> subscriber =
			        node.newSubscriber("state", "std_msgs/Int32");
			    subscriber.addMessageListener(new MessageListener<org.ros.message.std_msgs.Int32>() {
			    	@Override
			    	public void onNewMessage(org.ros.message.std_msgs.Int32 message) {

			    		int state = message.data;
			    		robot.setState(state);
			    		
			    		if(state == 1){
			    			robot.forward();
			    		}else if(state == 2){
			    			robot.backward();
			    		}else if(state == 3){
			    			robot.backward();
			    		    try {Thread.sleep(1000);} catch (InterruptedException e) {}
			    			robot.rotate();
			    		    try {Thread.sleep(500);} catch (InterruptedException e) {}
			    			robot.stop();
			    		}else if(state == 4){
			    			robot.stop();
			    		}else if(state == 99){
			    			robot.stop();
			    			SimpleConnector.close();
			    			System.exit(0);
			    		}else{
			    			robot.stop();
			    		}
			    		
			    		node.getLog().info("State: \"" + message.data + "\"");
			    	}
			    });
			    
			    //Publish values about sensors
		        int seq = 0;
	        	final org.ros.message.std_msgs.Int32 distance = new org.ros.message.std_msgs.Int32();		      
	        	final org.ros.message.std_msgs.Int32 energy = new org.ros.message.std_msgs.Int32();		      
	        		        	
		        while (true) {
		        	distance.data = robot.getDistance();
		        	range1.publish(distance);
		        	energy.data = robot.getBattery();
		        	battery.publish(energy);
          
		        	node.getLog().info(seq + 
		        			" State: " + robot.getState() +
		        			" Battery: " + energy.data + 
		        			" Range1: " + distance.data);
		        	
		        	seq++;
		        	Thread.sleep(50);
		        }
			    
				
		    //JAB: 2012/01/06
		    //It is necessary to add this exception handling
		    }catch (Exception e) {
		    	if (node != null) {
		    		node.getLog().fatal(e);
		    	} else {
		    		e.printStackTrace();
		    	}
		    }
			
		}else{
			System.err.println("I can't connect with a NXT brick");
			System.exit(0);
		}
	    
	}
}
