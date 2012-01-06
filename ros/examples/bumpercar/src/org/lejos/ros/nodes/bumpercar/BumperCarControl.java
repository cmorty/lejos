package org.lejos.ros.nodes.bumpercar;

import lejos.nxt.Battery;
import lejos.nxt.Sound;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;

import org.ros.message.MessageListener;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import com.google.common.base.Preconditions;

/**
 * This class explain the way to publish data on ROS.
 * Topics are data to be used by other nodes.
 * In this example, the node NXTStatus create a topic with the name "battery"
 *  
 * @author jabrena
 *
 */
public class BumperCarControl implements NodeMain{

	
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
		
	    final DataExchange de = new DataExchange();
	    
	    try {
	    	
	    	//Distance
		    Subscriber<org.ros.message.std_msgs.Int32> subscriber =
		        node.newSubscriber("range1", "std_msgs/Int32");
		    subscriber.addMessageListener(new MessageListener<org.ros.message.std_msgs.Int32>() {
		    	@Override
		    	public void onNewMessage(org.ros.message.std_msgs.Int32 message) {
		    		
		    		de.setRange(message.data);
		    		
		    		//node.getLog().info("Range1: \"" + message.data + "\"");
		    	}
		    });
		    
	    	//Battery
		    Subscriber<org.ros.message.std_msgs.Int32> subscriber2 =
		        node.newSubscriber("battery", "std_msgs/Int32");
		    subscriber2.addMessageListener(new MessageListener<org.ros.message.std_msgs.Int32>() {
		    	@Override
		    	public void onNewMessage(org.ros.message.std_msgs.Int32 message) {
		    		
		    		de.setBattery(message.data);
		    		
		    		//node.getLog().info("Battery: \"" + message.data + "\"");
		    	}
		    });
	    	
		    int nextState = 0;
		    
			//TODO: Change the type of the topic. voltage is a Integer
	        Publisher<org.ros.message.std_msgs.Int32> publisher =
	            node.newPublisher("state", "std_msgs/Int32");
	        int seq = 0;
	        while (true) {
	        
	        	nextState = getNextState(de.getRange(),de.getBattery());
	        	
	        	org.ros.message.std_msgs.Int32 status = new org.ros.message.std_msgs.Int32();
	        	status.data = nextState;
	        	publisher.publish(status);
	          
	          //node.getLog().info(seq + " State: " + state);
	          
	          seq++;
	          Thread.sleep(50);
	        }
	    } catch (Exception e) {
	    	if (node != null) {
	    		node.getLog().fatal(e);
	    	} else {
	    		e.printStackTrace();
	    	}
	    }
	 }
	
	private int getNextState(int range, int battery){
		
		int nextState = 0;
		
		System.err.println("" + range + "" + battery);
		
		if(battery > BATTERY_LIMIT){
			
			if(range > 200){
				nextState = 1;
			}else if(range > 100){
				nextState = 1;
			}else if(range >=40){
				nextState = 1;
			}else if(range <40){
				nextState = 3;
			}
			
		}else{
			nextState = 4;
		}
		
		return nextState;
	}
	
	private class DataExchange{
		private int range;
		private int battery;
		
		public DataExchange(){
			
		}
		
		public int getRange(){
			return range;
		}
		
		public void setRange(int _range){
			range = _range;
		}
		
		public int getBattery(){
			return battery;
		}
		
		public void setBattery(int _battery){
			battery = _battery;
		}
	}
}
