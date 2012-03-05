package org.lejos.ros.nxt.sensors;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.message.nxt_lejos_msgs.Battery;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

/**
 * 
 * @author jabrena
 *
 */
public class BatterySensor extends NXTDevice implements INXTDevice{

	float voltage = 0;
    final Battery message = new Battery(); 
    Publisher<Battery> topic = null;
    final String messageType = "nxt_lejos_msgs/Battery";
    
	public BatterySensor(){
		
	}
	
	public float getVoltage(){
		voltage = lejos.nxt.Battery.getVoltageMilliVolt();
		return voltage;
	}
	
	public void publishTopic(Node node){
		topic = node.newPublisher("" + super.getName(), messageType);
	}
	
	public void updateTopic(Node node, long seq){
		message.voltage = getVoltage();
		topic.publish(message);
	}

}
