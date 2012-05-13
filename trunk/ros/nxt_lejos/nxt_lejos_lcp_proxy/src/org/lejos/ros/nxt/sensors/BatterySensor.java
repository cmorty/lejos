package org.lejos.ros.nxt.sensors;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import nxt_lejos_msgs.Battery;

import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

import tf.tfMessage;

/**
 * 
 * @author jabrena
 *
 */
public class BatterySensor extends NXTDevice implements INXTDevice{

	float voltage = 0;
    Battery message; 
    Publisher<Battery> topic = null;
    final String messageType = "nxt_lejos_msgs/Battery";
    
	public BatterySensor(){
		
	}
	
	public float getVoltage(){
		voltage = lejos.nxt.Battery.getVoltageMilliVolt();
		return voltage;
	}
	
	public void publishTopic(ConnectedNode node){
		topic = node.newPublisher("" + super.getName(), messageType);
	}
	
	public void updateTopic(Node node, long seq){
		message = node.getTopicMessageFactory().newFromType(Battery._TYPE);
		message.setVoltage(getVoltage());
		topic.publish(message);
	}

	@Override
	public void publishTopic(Node node) {
		// TODO Auto-generated method stub
		
	}

}
