package org.lejos.ros.nodes.nxtstatus;

import lejos.nxt.Battery;
import lejos.nxt.Sound;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;

import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import com.google.common.base.Preconditions;

/**
 * This class explain the way to publish data on ROS.
 * Topics are data to be used by other nodes.
 * In this example, the node NXTStatus create a topic with the name "battery"
 *  
 * @author jabrena
 *
 */
public class NXTStatus implements NodeMain{

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
	public void onStart(Node node) {
	    Preconditions.checkState(this.node == null);
	    this.node = node;
	    
	    System.out.println("Running your first node");
	    
	    String brickName = "dog";
	    
	    boolean connetionStatus = false;
		connetionStatus = connectBT(brickName);

		if(connetionStatus){
			
		    try {
		    	
		    	int voltage = 0;		    	
				voltage = Battery.getVoltageMilliVolt();
		    	
				//TODO: Change the type of the topic. voltage is a Integer
		        Publisher<org.ros.message.std_msgs.String> publisher =
		            node.newPublisher("battery", "std_msgs/String");
		        int seq = 0;
		        while (true) {
		          org.ros.message.std_msgs.String str = new org.ros.message.std_msgs.String();
		          str.data = "" + voltage;
		          publisher.publish(str);
		          
		          Sound.playTone(1000, 1000);
		          
		          node.getLog().info(seq + " NXT Battery: " + voltage);
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

	/**
	 * This method connect with a NXT brick using a USB connection
	 * 
	 * @return
	 */
	private boolean connect(){
		boolean connectionStatus = false;
		
		NXTConnector conn = new NXTConnector();
		
		conn.addLogListener(new NXTCommLogListener(){

			public void logEvent(String message) {
				System.out.println("USBSend Log.listener: "+message);	
			}

			public void logEvent(Throwable throwable) {
				System.out.println("USBSend Log.listener - stack trace: ");
				 throwable.printStackTrace();
			}
			
		} 
		);
		
		if (!conn.connectTo("usb://")){
			System.err.println("No NXT found using USB");
		}else{
			connectionStatus = true;
		}
		
		return connectionStatus;
	}
	
	/**
	 * This example connect with a NXT brick using a Bluetooth connection.
	 * It is necessary to be paired the NXT brick in your system.
	 * Besides it is necessary to know the name of the brick.
	 * 
	 * @param brickName
	 * @return
	 */
	private boolean connectBT(String brickName){
		boolean connectionStatus = false;
		
		NXTConnector conn = new NXTConnector();
		conn.addLogListener(new NXTCommLogListener() {
			public void logEvent(String message) {
				System.out.println(message);				
			}

			public void logEvent(Throwable throwable) {
				System.err.println(throwable.getMessage());			
			}			
		});
		conn.setDebug(true);
		if (!conn.connectTo("btspp://"+brickName, NXTComm.PACKET)) {
			System.err.println("Failed to connect");
			//System.exit(1);
		}else{
			NXTCommandConnector.setNXTCommand(new NXTCommand(conn.getNXTComm()));			
			connectionStatus = true;
		}
		
		return connectionStatus;
	
	}
}
