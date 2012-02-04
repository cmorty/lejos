package org.lejos.ros.nodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.lejos.ros.nxt.sensors.GyroSensor;

import org.lejos.ros.nxt.navigation.DifferentialNavigationSystem;
import org.lejos.ros.nxt.sensors.CompassSensor;
import org.lejos.ros.nxt.sensors.GPS;
import org.lejos.ros.nxt.sensors.TouchSensor;
import org.lejos.ros.nxt.sensors.UltrasonicSensor;

import org.lejos.pccomm.utils.SimpleConnector;
import org.lejos.ros.nxt.NXTDevice;
import org.lejos.ros.nxt.actuators.NXTServoMotor;
import org.lejos.ros.nxt.sensors.BatterySensor;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.base.Preconditions;

/**
 * 
 * @author jabrena and lawrie griffiths
 *
 */
public class LCPProxy implements NodeMain {
	//Path
	String ROSNodePath = "";
	
	//Parameters
	String BRICK_NAME = "";
    String CONNECTION_TYPE = "";
	String YAML = "";

	boolean DIFFERENTIAL_NAVIGATION_FEATURES = false;
	float WHEEL_DIAMETER = 0.0f;
	float TRACK_WIDTH = 0.0f;
	boolean REVERSE = false;
	
    final String USB_CONNECTION = "USB";
    final String BLUETOOTH_CONNECTION = "BLUETOOTH";

    boolean connectionStatus = false;
    
	protected Node node;
	
    //Topic Management    
    ArrayList<NXTServoMotor> motorList = new ArrayList<NXTServoMotor>();
    ArrayList<NXTDevice> sensorList = new ArrayList<NXTDevice>();
    ArrayList<NXTDevice> actuatorSystemsList = new ArrayList<NXTDevice>();
    
	final org.ros.message.sensor_msgs.JointState jointState = 
		new org.ros.message.sensor_msgs.JointState();
	
	/**
	 * Start the node. Configure it, connect to the NXT and bind with ROS.
	 */
	public void onStart(final Node node) {
	    Preconditions.checkState(this.node == null);
	    this.node = node;

	    //1. Configurate Node
	    configurate();
		
	    //2. Connect with NXT Brick
	    connect();
	    
	    //3. Bind NXT with ROS
	    bind();
	}

	/**
	 * Configure the node
	 */
	protected void configurate() {
		System.out.println("* Configurate ROS Node");
		
	    //Show information about header
	    this.showHeader();
	    
	    //Get ROS Path
	    getROSPath();
		
	    //Load properties
		readProperties();
	}

	/**
	 * Get the ROS path name
	 */
	protected void getROSPath() {
		try {
		    ROSNodePath = new File(".").getCanonicalPath() + "/"; 
			System.out.println("ROS Node Path: " + ROSNodePath );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the properties file
	 */
	protected void readProperties() {	
		Properties prop = new Properties();

		System.out.println("* Reading property file");
		
		String nodeName = this.getNodeName();
		String file = nodeName + ".properties";
		String path = ROSNodePath + file;
		System.out.println("Path: " + path);
		
		//load a properties file
		try {
			prop.load(new FileInputStream(path));
			
			BRICK_NAME = prop.getProperty("NXT-BRICK");
			CONNECTION_TYPE = prop.getProperty("CONNECTION-TYPE");
			YAML = prop.getProperty("YAML-ROBOT-DESCRIPTOR");
			
            //get the property value and print it out
            System.out.println("BRICK-NAME: " + BRICK_NAME);
    		System.out.println("CONNECTION: " + CONNECTION_TYPE);
    		System.out.println("YAML: " + YAML);
    		
    		if(Boolean.parseBoolean(prop.getProperty("DIFFERENTIAL_NAVIGATION_FEATURES"))){
    			System.out.println("Differential navigation features enabled");
    			
    			DIFFERENTIAL_NAVIGATION_FEATURES = Boolean.parseBoolean(prop.getProperty("DIFFERENTIAL_NAVIGATION_FEATURES"));
    			WHEEL_DIAMETER = Float.parseFloat(prop.getProperty("WHEEL_DIAMETER"));
    			TRACK_WIDTH = Float.parseFloat(prop.getProperty("TRACK_WIDTH"));
    			REVERSE = Boolean.parseBoolean(prop.getProperty("REVERSE"));

                System.out.println("WHEEL_DIAMETER: " + WHEEL_DIAMETER);
        		System.out.println("TRACK_WIDTH: " + TRACK_WIDTH);
        		System.out.println("REVERSE: " + REVERSE);   		
    		}	
		} catch (FileNotFoundException e) {
			System.err.println("File not found");	
			e.printStackTrace();
			System.exit(0);			
		} catch (IOException e) {
			System.err.println("IO Error");
			e.printStackTrace();
			System.exit(0);
		}
	}

	private String getNodeName() {
		//Get the name given by .launch file
		String nodeName = node.getName().toString();
		//Remove "/"
		nodeName = nodeName.substring(1, nodeName.length());
		return nodeName;
	}
	
	/**
	 * Connect to the NXT
	 */
	protected void connect() {
		System.out.println("* Connecting with a NXT brick");
	    
	    if (CONNECTION_TYPE.equals(BLUETOOTH_CONNECTION)) {
			connectionStatus = SimpleConnector.connectByBT(BRICK_NAME);	    	
	    } else {
	    	connectionStatus = SimpleConnector.connectByUSB();
	    }
		
		if (connectionStatus) {
			System.out.println("ROS Node connected with a NXT brick");
		} else{
			System.err.println("I can't connect with a NXT brick");
			System.exit(0);
		}
	}
	
	/**
	 * Bind with ROS
	 */
	protected void bind(){
		if (connectionStatus) {
			System.out.println("* Bind NXT brick with ROS");
			
			//2. Process YAML file
			processYAML();
			
			//3. Process subcriptions
			processSubscriptions();
			
			//4. Update topics
			updateTopics();
		} else{
			System.err.println("I can't connect with a NXT brick");
			System.exit(0);
		}
	}
	
	/**
	 * Process the YAML file to get the NXT robot configuration.
	 */
	protected void processYAML() {    
		System.out.println("* Processing YAML file");
		
		try{
		
			String path = ROSNodePath + YAML;
			System.out.println("YAML Path: " + path);
			
			//Read a YAML File
	        YamlReader reader = new YamlReader(new FileReader(path));
	        Object object = reader.read();
	        Map map = (Map)object;
	        //System.out.println(map.size());
	        
	        //Get component list for NXT Robot
	        List list =  (List) map.get("nxt_robot");
	        
	        int i = 0;
	        
        	//Sensors
        	String type = "";
        	String name = "";
    		String port = "";
    		float desiredFrequency = 0f;

	        for (Object obj : list) {
	        	Map map2 = (Map) list.get(i);
	        	
	        	type = map2.get("type").toString().trim(); 

	        	//Actuators
	        	if (type.equals("motor")) {
	        		System.out.println("I found a motor description");

	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);

	        		//Instance of NXTServoMotor
        			NXTServoMotor sm = new NXTServoMotor(port);
        			sm.setName(name);
        			sm.setDesiredFrequency(desiredFrequency);
        			sm.publishTopic(node);
        			motorList.add(sm);
	        		
	        	//Sensors
	        	} else if(type.equals("battery")) {
	        		System.out.println("I found a sensor description");	        		
	        		
	        		port = "PORT_0";
	        		
	        		name = map2.get("name").toString().trim();

	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);

			        BatterySensor batteryObj = new BatterySensor();
			        batteryObj.setName(name);
			        batteryObj.setDesiredFrequency(desiredFrequency);
			        batteryObj.publishTopic(node);
			        sensorList.add(batteryObj);
	        	
	        	} else if(type.equals("ultrasonic")) {
	        		System.out.println("I found an ultrasonic  sensor description");
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
        			
        			UltrasonicSensor us = new UltrasonicSensor(port);
        			us.setName(name);
        			us.setDesiredFrequency(desiredFrequency);
        			us.publishTopic(node);
        			sensorList.add(us);		        			
	        	
	        	} else if(type.equals("compass")) {
	        		System.out.println("I found a compass sensor description");
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
        			
        			CompassSensor compass = new CompassSensor(port);
        			compass.setName(name);
        			compass.setDesiredFrequency(desiredFrequency);
        			compass.publishTopic(node);
        			sensorList.add(compass);		        			
	        	
	        	} else if(type.equals("touch")) {
	        		System.out.println("I found a touch sensor description");
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
        			
        			TouchSensor touch = new TouchSensor(port);
        			touch.setName(name);
        			touch.setDesiredFrequency(desiredFrequency);
        			touch.publishTopic(node);
        			sensorList.add(touch);		        			
	        	
	        	} else if(type.equals("gyro")) {
	        		System.out.println("I found a gyro sensor description");
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
        			
        			GyroSensor gyro = new GyroSensor(port);
        			gyro.setName(name);
        			gyro.setDesiredFrequency(desiredFrequency);
        			gyro.publishTopic(node);
        			sensorList.add(gyro);		        			
	        	
	        	} else if(type.equals("gps")){
	        		System.out.println("I found a sensor description");
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
	        		
        			GPS gps = new GPS(port);
        			gps.setDesiredFrequency(desiredFrequency);
        			gps.publishTopic(node);
        			sensorList.add(gps);	     			
	        	} else if(type.equals("differential_navigation_system")) {	
	        		System.out.println("I found a differential actuator system");
	        		
	        		//Business exception
	        		if(!DIFFERENTIAL_NAVIGATION_FEATURES){
	        			String message = "Differential pilot detected in YAML. Navigation features not enabled.";
	        			throw new YAMLException(message);
	        		}
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
	        		
	        		final String pattern = "PORT_";
	        		String motorPortLetter1 = port.substring(pattern.length(), port.length()-1);
	        		String motorPortLetter2 = port.substring(pattern.length()+1, port.length());
	        		
	        		DifferentialNavigationSystem df = new DifferentialNavigationSystem(motorPortLetter1, motorPortLetter2, WHEEL_DIAMETER, TRACK_WIDTH, REVERSE);
        			df.setName(name);
        			df.setDesiredFrequency(desiredFrequency);
	        		df.publishTopic(node);
	        		actuatorSystemsList.add(df);			
	        	} else {
	        		System.out.println("I found a rare device");
	        		
	        		name = map2.get("name").toString().trim();
	        		System.out.println(type);
	        		System.out.println(name);
	        	}     	
	        	i++;
	        }
	    } catch (Exception e) {
	    	if (node != null) {
	    		node.getLog().fatal(e);
	    		System.err.println(e.getStackTrace());
	    	} else {
	    		e.printStackTrace();
	    	}
	    }		
	}
	
	/**
	 * Subscribe to all relevant topics
	 */
	protected void processSubscriptions(){
    	System.out.println("* Enabling subscriptions");
    	
    	if (motorList.size()>0) {
    		//Subscription to joint_command
            Subscriber<org.ros.message.nxt_msgs.JointCommand> subscriberMotor =
    	        node.newSubscriber("joint_command", "nxt_msgs/JointCommand");
            subscriberMotor.addMessageListener(new MessageListener<org.ros.message.nxt_msgs.JointCommand>() {
    	    	@Override
    	    	public void onNewMessage(org.ros.message.nxt_msgs.JointCommand message) {
    	    		
    	    		String name = message.name;

    				//Actuators
    				for (NXTDevice device : motorList){
    		        	if (device instanceof org.lejos.ros.nxt.actuators.NXTServoMotor){
    		        		NXTServoMotor motor = (org.lejos.ros.nxt.actuators.NXTServoMotor) device;
    		        		//motor.updateTopic();
    		        		if (motor.getName().equals(name)){
    		    	    		node.getLog().info("State: \"" + message.name + " " + message.effort + "\"");
    		    	    		
    		    	    		motor.updateJoint(message.effort);
    		        		}
    		        	}
    				}	    		
    	    	}
    	    });
    	}
    	
    	//TODO: Datatype must change soon
    	if (actuatorSystemsList.size() > 0) {    		
    		//Subscription to DNS_command
            Subscriber<org.ros.message.nxt_lejos_ros_msgs.DNSCommand> subscriberDifferentialActuatorSystem =
    	        node.newSubscriber("dns_command", "nxt_lejos_ros_msgs/DNSCommand");
            subscriberDifferentialActuatorSystem.addMessageListener(new MessageListener<org.ros.message.nxt_lejos_ros_msgs.DNSCommand>() {
    	    	@Override
    	    	public void onNewMessage(org.ros.message.nxt_lejos_ros_msgs.DNSCommand message) {  	    		
	        		DifferentialNavigationSystem df = (org.lejos.ros.nxt.navigation.DifferentialNavigationSystem) actuatorSystemsList.get(0);
    	    		df.updateActuatorSystem(message);
	    		
    	    	}
    	    });
            
    		//Subscription to command_velocity_command
            Subscriber<org.ros.message.turtlesim.Velocity> subscriberCommandVelocity =
    	        node.newSubscriber("/turtle1/command_velocity", "turtlesim/Velocity");
            subscriberCommandVelocity.addMessageListener(new MessageListener<org.ros.message.turtlesim.Velocity>() {
    	    	@Override
    	    	public void onNewMessage(org.ros.message.turtlesim.Velocity message) {   		
	        		DifferentialNavigationSystem df = (org.lejos.ros.nxt.navigation.DifferentialNavigationSystem) actuatorSystemsList.get(0);
    	    		df.updateVelocity(message);	
    	    	}
    	    });
    		
    	}

	}

	/**
	 * Publish to all topics.
	 */
	protected void updateTopics() {
		System.out.println("* Updating Topics");
		
        //Publish data
        int seq = 0;
		while(true){
        
			if (motorList.size() > 0) {
				//Actuators
				for (NXTDevice device : motorList) {
		        	if (device instanceof org.lejos.ros.nxt.actuators.NXTServoMotor) {
		        		NXTServoMotor motor = (org.lejos.ros.nxt.actuators.NXTServoMotor) device;
		        		motor.updateTopic();
		        	}
				}
			}
			
			//Sensors
	        for (NXTDevice device : sensorList) {
	        	if (device instanceof org.lejos.ros.nxt.sensors.BatterySensor) {
	        		BatterySensor battery = (org.lejos.ros.nxt.sensors.BatterySensor) device;
	        		battery.updateTopic();
	        		
	        	} else if (device instanceof org.lejos.ros.nxt.sensors.UltrasonicSensor) {
	        		//System.out.println("UltrasonicSensor");
	        		UltrasonicSensor us = (org.lejos.ros.nxt.sensors.UltrasonicSensor) device;
	        		us.updateTopic();
	        	} else if (device instanceof org.lejos.ros.nxt.sensors.GPS) {
	        		//System.out.println("UltrasonicSensor");
	        		GPS gps = (org.lejos.ros.nxt.sensors.GPS) device;
	        		gps.updateTopic();
	        	}
	        }

	        //Actuator Systems
	        for (NXTDevice device : actuatorSystemsList) {
        		DifferentialNavigationSystem das = (org.lejos.ros.nxt.navigation.DifferentialNavigationSystem) device;
        		das.updateTopic();       	
	        }      
			seq++;
		}
	}
	
	private void showHeader() {	
		System.out.println("");
		System.out.println("*********************");
	    System.out.println("* Running LCPProxy *");
		System.out.println("*********************");
		System.out.println("");
	}

	@Override
	public void onShutdown(Node arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onShutdownComplete(Node arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public GraphName getDefaultNodeName() {
		// TODO Auto-generated method stub
		return null;
	}	
}

