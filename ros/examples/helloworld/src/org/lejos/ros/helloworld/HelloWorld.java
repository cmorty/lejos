package org.lejos.ros.helloworld;

import org.ros.node.Node;
import org.ros.node.NodeMain;

import com.google.common.base.Preconditions;

/**
 * This is the first example to understand the technology ROS using ROSJava and
 * LeJOS project
 * 
 * @author jabrena
 *
 */
public class HelloWorld implements NodeMain{

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
	    
	    try {
	      int seq = 0;
	      while (true) {
	        System.out.println("Hello, world! " + seq);
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
