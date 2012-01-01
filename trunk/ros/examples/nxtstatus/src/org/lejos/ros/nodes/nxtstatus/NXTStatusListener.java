package org.lejos.ros.nodes.nxtstatus;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

public class NXTStatusListener implements NodeMain{

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
	    final Log log = node.getLog();
	    Subscriber<org.ros.message.std_msgs.String> subscriber =
	        node.newSubscriber("battery", "std_msgs/String");
	    subscriber.addMessageListener(new MessageListener<org.ros.message.std_msgs.String>() {
	    	@Override
	    	public void onNewMessage(org.ros.message.std_msgs.String message) {
	    		log.info("NXTBattery: \"" + message.data + "\"");
	    	}
	    });
	}

}
