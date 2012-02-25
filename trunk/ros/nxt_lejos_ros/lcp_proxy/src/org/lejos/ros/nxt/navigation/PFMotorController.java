package org.lejos.ros.nxt.navigation;

import lejos.nxt.SensorPort;
import lejos.nxt.addon.IRLink;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.message.nxt_lejos_ros_msgs.DNSCommand;
import org.ros.node.Node;

/**
 * 
 * Uses leJOS navigation classes to control a robot with differential steering.
 * 
 * @author Lawrie Griffiths
 *
 */
public class PFMotorController extends NXTDevice implements INXTDevice {
	// leJOS	
	IRLink link;
	
	public PFMotorController(String port) {
		if (port.equals("PORT_1")) {
			link = new IRLink(SensorPort.S1);
		} else if(port.equals("PORT_2")) {
			link = new IRLink(SensorPort.S2);
		} else if(port.equals("PORT_3")) {
			link = new IRLink(SensorPort.S3);
		} else if(port.equals("PORT_4")) {
			link = new IRLink(SensorPort.S4);
		}
	}
	
	public void updateActuatorSystem(DNSCommand cmd){
		String type = cmd.type;
		double value = cmd.value;
		
		System.out.println("DNS cmd = " + type + " " + value);
		
		if (type.equals("forward")) link.sendPFComboDirect(0,IRLink.PF_FORWARD, IRLink.PF_BACKWARD);
		else if (type.equals("backward")) link.sendPFComboDirect(0,IRLink.PF_BACKWARD, IRLink.PF_FORWARD);
		else if (type.equals("stop")) link.sendPFComboDirect(0,0,0);
		else if (type.equals("rotateLeft")) link.sendPFComboDirect(0,IRLink.PF_FORWARD, IRLink.PF_FORWARD);
		else if (type.equals("rotateRight")) link.sendPFComboDirect(0,IRLink.PF_BACKWARD, IRLink.PF_BACKWARD);
	}

	@Override
	public void publishTopic(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTopic(Node node, long seq) {
		// TODO Auto-generated method stub
		
	}
}
