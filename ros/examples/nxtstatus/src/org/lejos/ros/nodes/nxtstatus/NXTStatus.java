package org.lejos.ros.nodes.nxtstatus;

import lejos.nxt.Battery;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import com.google.common.base.Preconditions;

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
	    
	    boolean connetionStatus = false;
		connetionStatus = connect();

		if(connetionStatus){
			
		    try {
		    	
		    	int voltage = 0;		    	
				voltage = Battery.getVoltageMilliVolt();
		    	
		        Publisher<org.ros.message.std_msgs.String> publisher =
		            node.newPublisher("battery", "std_msgs/String");
		        int seq = 0;
		        while (true) {
		          org.ros.message.std_msgs.String str = new org.ros.message.std_msgs.String();
		          str.data = "" + voltage;
		          publisher.publish(str);
		          node.getLog().info(seq + " NXT Battery: " + voltage);
		          seq++;
		          Thread.sleep(1000);
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
}
