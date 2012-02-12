package org.lejos.ros.nxt.sensors;

import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.GPSSensor;

import org.lejos.ros.nxt.INXTDevice;
import org.lejos.ros.nxt.NXTDevice;
import org.ros.message.sensor_msgs.NavSatFix;
import org.ros.message.sensor_msgs.NavSatStatus;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;

public class GPS extends NXTDevice implements INXTDevice {

	//NXT data
	private String port;
	
	//ROS  
    final org.ros.message.sensor_msgs.NavSatStatus navSatStatus = new org.ros.message.sensor_msgs.NavSatStatus(); 
    
    //http://www.ros.org/doc/api/sensor_msgs/html/msg/NavSatFix.html
    final org.ros.message.sensor_msgs.NavSatFix message = new org.ros.message.sensor_msgs.NavSatFix(); 
    Publisher<org.ros.message.sensor_msgs.NavSatFix> topic = null;
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
		
		navSatStatus.service = NavSatStatus.SERVICE_GPS;
	}
	
	public void publishTopic(Node node) {
		topic = node.newPublisher(topicName, messageType);
	}

	public void updateTopic(Node node, long seq) {

		if(gps.linkStatus()){
			navSatStatus.status = NavSatStatus.STATUS_FIX;			
		}else{
			navSatStatus.status = NavSatStatus.STATUS_NO_FIX;
		}

		// Convert from ddmmmmmm to degrees in WGS84 datum 
		double latitude = gps.getLatitude()/1000000.0;
		// Convert from dddmmmmmm to degrees in WGS84 datum
		double longitude = gps.getLongitude()/1000000.0;
		
		//dGPS doesn't return altitude;
		message.altitude = 0f;
		message.latitude = latitude;
		message.longitude = longitude;
		message.status = navSatStatus;
		//TODO: How to calculate
		//message.position_covariance = null;
		//TODO: dGPS doesn't return a value about quality
		//http://answers.ros.org/question/1276/calculate-navsatfix-covariance
		message.position_covariance_type = NavSatFix.COVARIANCE_TYPE_UNKNOWN;
		
		topic.publish(message);
		
	}

}
