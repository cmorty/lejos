package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.GPSSensor;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import sensor_msgs.NavSatFix;
import sensor_msgs.NavSatStatus;
import tf.tfMessage;

import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class GPS extends NXTDevice implements INXTDevice {

	//NXT data
	private String port;
	
	//ROS  
    NavSatStatus navSatStatus; 
    
    //http://www.ros.org/doc/api/sensor_msgs/html/msg/NavSatFix.html
    NavSatFix message;; 
    Publisher<NavSatFix> topic = null;
    final String messageType = "sensor_msgs/NavSatFix";
    final String topicName = "navSatFix";

    //LeJOS
	private GPSSensor gps;
	
	public GPS(String port){

		if(port.equals("PORT_1")){
			gps = new GPSSensor(SensorPort.S1);
		}else if(port.equals("PORT_2")){
			gps = new GPSSensor(SensorPort.S2);
		}else if(port.equals("PORT_3")){
			gps = new GPSSensor(SensorPort.S3);
		}else if(port.equals("PORT_4")){
			gps = new GPSSensor(SensorPort.S4);
		}
		
		navSatStatus.setService(NavSatStatus.SERVICE_GPS);
	}
	
	public void publishTopic(ConnectedNode node) {
		topic = node.newPublisher(topicName, messageType);
	}

	public void updateTopic(Node node, long seq) {
		
		message = node.getTopicMessageFactory().newFromType(NavSatFix._TYPE);

		if(gps.linkStatus()){
			navSatStatus.setStatus(NavSatStatus.STATUS_FIX);			
		}else{
			navSatStatus.setStatus(NavSatStatus.STATUS_NO_FIX);
		}

		// Convert from ddmmmmmm to degrees in WGS84 datum 
		double latitude = gps.getLatitude()/1000000.0;
		// Convert from dddmmmmmm to degrees in WGS84 datum
		double longitude = gps.getLongitude()/1000000.0;
		
		//dGPS doesn't return altitude;
		message.setAltitude(0f);
		message.setLatitude(latitude);
		message.setLongitude(longitude);
		message.setStatus(navSatStatus);
		//TODO: How to calculate
		//message.position_covariance = null;
		//TODO: dGPS doesn't return a value about quality
		//http://answers.ros.org/question/1276/calculate-navsatfix-covariance
		message.setPositionCovarianceType(NavSatFix.COVARIANCE_TYPE_UNKNOWN);
		
		topic.publish(message);
		
	}

	@Override
	public void publishTopic(Node node) {
		// TODO Auto-generated method stub
		
	}

}
