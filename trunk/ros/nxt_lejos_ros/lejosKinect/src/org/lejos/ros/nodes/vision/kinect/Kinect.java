package org.lejos.ros.nodes.vision.kinect;


import org.ros.node.Node;
import org.ros.node.NodeMain;

import com.google.common.base.Preconditions;


//Kinect
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.OpenKinectFrameGrabber;
import com.googlecode.javacv.cpp.freenect;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * This is the an example to know how to integrate javacv to process
 * kinect data in ROS
 * 
 * @author jabrena
 *
 */
public class Kinect implements NodeMain{

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
	    
	    System.out.println("Running your first node using a Kinect");
	    
		try{
			// TODO Auto-generated method stub
			CanvasFrame frame = new CanvasFrame("Image Frame");
			
			OpenKinectFrameGrabber grabber = new OpenKinectFrameGrabber(0);
			grabber.setFormat("depth");
			grabber.start();
			while (true) {
			    IplImage image = grabber.grab();
			    String filename = "demo.jpg";
			    frame.showImage(image);
			}
		}catch(Exception e){
	    	if (node != null) {
	    		node.getLog().fatal(e);
	    	} else {
	    		e.printStackTrace();
	    	}
		}
	 }
}
