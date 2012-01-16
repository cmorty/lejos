package org.lejos.ros.nxt.sensors;

import lejos.nxt.Battery;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

/**
 * 
 * @author jabrena
 *
 */
public class BatterySensor extends NXTDevice implements INXTDevice{

	float voltage = 0;
    final org.ros.message.std_msgs.Float64 message = new org.ros.message.std_msgs.Float64(); 
    Publisher<org.ros.message.std_msgs.Float64> topic = null;
	
	public BatterySensor(){
		
	}
	
	public float getVoltage(){
		voltage = Battery.getVoltageMilliVolt();
		return voltage;
	}
	
	public void publishTopic(Node node){
		topic = node.newPublisher("" + super.getName(), "std_msgs/Float64");
	}
	
	public void updateTopic(){
		message.data = getVoltage();
		topic.publish(message);
	}

}
