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

import lejos.nxt.Sound;

import org.lejos.ros.nxt.navigation.PFMotorController;
import org.lejos.ros.nxt.navigation.RangeScanner;

import org.lejos.ros.nxt.sensors.ColorSensor;

import org.lejos.ros.nxt.sensors.GyroSensor;

import org.lejos.ros.nxt.navigation.DifferentialNavigationSystem;
import org.lejos.ros.nxt.sensors.AccelerationSensor;
import org.lejos.ros.nxt.sensors.CompassSensor;
import org.lejos.ros.nxt.sensors.GPS;
import org.lejos.ros.nxt.sensors.LightSensor;
import org.lejos.ros.nxt.sensors.SoundSensor;
import org.lejos.ros.nxt.sensors.TouchSensor;
import org.lejos.ros.nxt.sensors.UltrasonicSensor;

import org.lejos.pccomm.utils.SimpleConnector;
import org.lejos.ros.nxt.NXTDevice;
import org.lejos.ros.nxt.actuators.NXTServoMotor;
import org.lejos.ros.nxt.sensors.BatterySensor;
import org.ros.message.MessageListener;
import geometry_msgs.Pose2D;
import geometry_msgs.PoseWithCovarianceStamped;
import geometry_msgs.Twist;
import nxt_lejos_msgs.DNSCommand;
import nxt_lejos_msgs.JointPosition;
import nxt_lejos_msgs.JointVelocity;
import nxt_lejos_msgs.Tone;
import nxt_msgs.JointCommand;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import sensor_msgs.JointState;
import tf.tfMessage;
import turtlesim.Velocity;

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
    
	protected ConnectedNode node;
	
	DifferentialNavigationSystem df;
	UltrasonicSensor us;
	
    //Topic Management    
    ArrayList<NXTServoMotor> motorList = new ArrayList<NXTServoMotor>();
    ArrayList<NXTDevice> sensorList = new ArrayList<NXTDevice>();
    ArrayList<NXTDevice> actuatorSystemsList = new ArrayList<NXTDevice>();
    
	final JointState jointState = node.getTopicMessageFactory().newFromType(JointState._TYPE);
	
	/**
	 * Start the node. Configure it, connect to the NXT and bind with ROS.
	 */
	public void onStart(final ConnectedNode node) {
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
	        Map map = (Map)reader.read();
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

	        	System.out.println("Type is " + type);
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
        			
        			us = new UltrasonicSensor(node, port);
        			us.setName(name);
        			us.setDesiredFrequency(desiredFrequency);
        			us.publishTopic(node);
        			sensorList.add(us);		        			
	        	
	        	} else if(type.equals("color")) {
	        		System.out.println("I found a color sensor description");
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
        			
        			ColorSensor color = new ColorSensor(port);
        			color.setName(name);
        			color.setDesiredFrequency(desiredFrequency);
        			color.publishTopic(node);
        			sensorList.add(color);		        			
	        	
	        	} else if(type.equals("light")) {
	        		System.out.println("I found a light sensor description");
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
        			
        			LightSensor light = new LightSensor(port);
        			light.setName(name);
        			light.setDesiredFrequency(desiredFrequency);
        			light.publishTopic(node);
        			sensorList.add(light);		        			
	        	
	        	} else if(type.equals("sound")) {
	        		System.out.println("I found a sound sensor description");
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
        			
        			SoundSensor sound = new SoundSensor(port);
        			sound.setName(name);
        			sound.setDesiredFrequency(desiredFrequency);
        			sound.publishTopic(node);
        			sensorList.add(sound);		        			
	        	
	        	}else if(type.equals("acceleration")) {
	        		System.out.println("I found an acceleration  sensor description");
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
        			
        			AccelerationSensor accel = new AccelerationSensor(port);
        			accel.setName(name);
        			accel.setDesiredFrequency(desiredFrequency);
        			accel.publishTopic(node);
        			sensorList.add(accel);		        			
	        	
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
	        		
	        		df = new DifferentialNavigationSystem(node, motorPortLetter1, motorPortLetter2, WHEEL_DIAMETER, TRACK_WIDTH, REVERSE);
        			df.setName(name);
        			df.setDesiredFrequency(desiredFrequency);
	        		df.publishTopic(node);
	        		actuatorSystemsList.add(df);			
	        	} else if(type.equals("scanner")) {	
	        		System.out.println("I found a scanner");
	        		
	        		//Business exception
	        		if(!DIFFERENTIAL_NAVIGATION_FEATURES){
	        			String message = "Scanner detected in YAML. Navigation features not enabled.";
	        			throw new YAMLException(message);
	        		}
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
	        		
	        		RangeScanner scan = new RangeScanner(df.getPilot(), us.getSonic());
	        		
        			scan.setName(name);
        			scan.setDesiredFrequency(desiredFrequency);
	        		scan.publishTopic(node);
	        		actuatorSystemsList.add(scan);			
	        	} else if(type.equals("pfcontroller")) {	
	        		System.out.println("I found a PF controller");
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
	        		
	        		PFMotorController pf = new PFMotorController(port);
	        		
        			pf.setName(name);
        			pf.setDesiredFrequency(desiredFrequency);
	        		pf.publishTopic(node);
	        		actuatorSystemsList.add(pf);			
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
            Subscriber<JointCommand> subscriberMotor =
    	        node.newSubscriber("joint_command", "nxt_msgs/JointCommand");
            subscriberMotor.addMessageListener(new MessageListener<JointCommand>() {
    	    	@Override
    	    	public void onNewMessage(JointCommand message) {
    	    		
    	    		String name = message.getName();

    				//Actuators
    				for (NXTDevice device : motorList){
    		        	if (device instanceof org.lejos.ros.nxt.actuators.NXTServoMotor){
    		        		NXTServoMotor motor = (org.lejos.ros.nxt.actuators.NXTServoMotor) device;
    		        		//motor.updateTopic();
    		        		if (motor.getName().equals(name)){
    		    	    		node.getLog().info("State: \"" + message.getName() + " " + message.getEffort() + "\"");
    		    	    		
    		    	    		motor.updateJoint(message.getEffort());
    		        		}
    		        	}
    				}	    		
    	    	}
    	    });
            
    		//Subscription to joint_velocity
            Subscriber<JointVelocity> subscriberVelocity =
    	        node.newSubscriber("joint_velocity", "nxt_lejos_msgs/JointVelocity");
            subscriberVelocity.addMessageListener(new MessageListener<JointVelocity>() {
    	    	@Override
    	    	public void onNewMessage(JointVelocity message) {
    	    		
    	    		String name = message.getName();

    				//Actuators
    				for (NXTDevice device : motorList){
    		        	if (device instanceof org.lejos.ros.nxt.actuators.NXTServoMotor){
    		        		NXTServoMotor motor = (org.lejos.ros.nxt.actuators.NXTServoMotor) device;
    		        		//motor.updateTopic();
    		        		if (motor.getName().equals(name)){
    		    	    		node.getLog().info("State: \"" + message.getName() + " " + message.getVelocity() + "\"");
    		    	    		
    		    	    		motor.updateVelocity(message.getVelocity());
    		        		}
    		        	}
    				}	    		
    	    	}
    	    });
            
    		//Subscription to joint_position
            Subscriber<JointPosition> subscriberPosition =
    	        node.newSubscriber("joint_position", "nxt_lejos_msgs/JointPosition");
            subscriberPosition.addMessageListener(new MessageListener<JointPosition>() {
    	    	@Override
    	    	public void onNewMessage(JointPosition message) {
    	    		
    	    		String name = message.getName();

    				//Actuators
    				for (NXTDevice device : motorList){
    		        	if (device instanceof org.lejos.ros.nxt.actuators.NXTServoMotor){
    		        		NXTServoMotor motor = (org.lejos.ros.nxt.actuators.NXTServoMotor) device;
    		        		//motor.updateTopic();
    		        		if (motor.getName().equals(name)){
    		    	    		node.getLog().info("State: \"" + message.getName() + " " + message.getAngle() + "\"");
    		    	    		
    		    	    		motor.updatePosition(message.getAngle());
    		        		}
    		        	}
    				}	    		
    	    	}
    	    });
    	}
    	
    	if (actuatorSystemsList.size() > 0) {    		
    		//Subscription to DNS_command
            Subscriber<DNSCommand> subscriberDifferentialActuatorSystem =
    	        node.newSubscriber("dns_command", "nxt_lejos_msgs/DNSCommand");
            subscriberDifferentialActuatorSystem.addMessageListener(new MessageListener<DNSCommand>() {
    	    	@Override
    	    	public void onNewMessage(DNSCommand message) { 
    	    		for (NXTDevice device : actuatorSystemsList) {
    		        	if (device instanceof DifferentialNavigationSystem) {
			        		DifferentialNavigationSystem df = (DifferentialNavigationSystem) device;
		    	    		df.updateActuatorSystem(message);
    		        	} else if (device instanceof PFMotorController) {
    		        		PFMotorController pf = (PFMotorController) device;
    		        		pf.updateActuatorSystem(message);
    		        	}
    	    		}
	    		
    	    	}
    	    });
            
    		//Subscription to command_velocity topic
            Subscriber<Velocity> subscriberCommandVelocity =
    	        node.newSubscriber("/turtle1/command_velocity", "turtlesim/Velocity");
            subscriberCommandVelocity.addMessageListener(new MessageListener<Velocity>() {
    	    	@Override
    	    	public void onNewMessage(Velocity message) {   		
	        		DifferentialNavigationSystem df = (DifferentialNavigationSystem) actuatorSystemsList.get(0);
    	    		df.updateVelocity(message);	
    	    	}
    	    });
            
    		//Subscription to cmd_val topic
            Subscriber<Twist> subscriberTwist =
    	        node.newSubscriber("cmd_vel", "geometry_msgs/Twist");
            subscriberTwist.addMessageListener(new MessageListener<Twist>() {
    	    	@Override
    	    	public void onNewMessage(Twist message) {   		
	        		DifferentialNavigationSystem df = (DifferentialNavigationSystem) actuatorSystemsList.get(0);
    	    		df.updateTwist(message);	
    	    	}
    	    });  	
            
    		//Subscription to set_pose topic
            Subscriber<Pose2D> subscriberSetPose =
    	        node.newSubscriber("set_pose", "geometry_msgs/Pose2D");
            subscriberSetPose.addMessageListener(new MessageListener<Pose2D>() {
    	    	@Override
    	    	public void onNewMessage(Pose2D message) {   		
	        		DifferentialNavigationSystem df = (DifferentialNavigationSystem) actuatorSystemsList.get(0);
    	    		df.updatePose(message);	
    	    	}
    	    }); 
            
    		//Subscription to set_pose topic
            Subscriber<PoseWithCovarianceStamped> subscriberInitialPose =
    	        node.newSubscriber("initialpose", "geometry_msgs/PoseWithCovarianceStamped");
            subscriberInitialPose.addMessageListener(new MessageListener<PoseWithCovarianceStamped>() {
    	    	@Override
    	    	public void onNewMessage(PoseWithCovarianceStamped message) {   		
	        		DifferentialNavigationSystem df = (DifferentialNavigationSystem) actuatorSystemsList.get(0);
    	    		System.out.println("Received initial pose");
    	    		System.out.println("x = " + message.getPose().getPose().getPosition().getX() + ", y = " + message.getPose().getPose().getPosition().getY());
    	    		Pose2D pose = node.getTopicMessageFactory().newFromType(Pose2D._TYPE);
    	    		pose.setX(message.getPose().getPose().getPosition().getX());
    	    		pose.setY(message.getPose().getPose().getPosition().getY());
    	    		pose.setTheta(0);
    	    		df.updatePose(pose);
    	    	}
    	    });
    	}
    	
		//Subscription to play_tone_command
        Subscriber<Tone> subscriberTone =
	        node.newSubscriber("play_tone", "nxt_lejos_msgs/Tone");
        subscriberTone.addMessageListener(new MessageListener<Tone>() {
	    	@Override
	    	public void onNewMessage(Tone message) {   		
	    		Sound.playTone(message.getPitch(), message.getDuration());
	    	}
	    });

	}
	
    long seq = 0;

	/**
	 * Publish to all topics.
	 */
	protected void updateTopics() {
		System.out.println("* Updating Topics");
		
        //Publish data
		while(true){
        
			if (motorList.size() > 0) {
				//Actuators
				for (NXTDevice device : motorList) {
		        	if (device instanceof org.lejos.ros.nxt.actuators.NXTServoMotor) {
		        		NXTServoMotor motor = (org.lejos.ros.nxt.actuators.NXTServoMotor) device;
		        		motor.updateTopic(node, seq);
		        	}
				}
			}
			
			//Sensors
	        for (NXTDevice device : sensorList) {
	        	if (device instanceof org.lejos.ros.nxt.sensors.BatterySensor) {
	        		BatterySensor battery = (org.lejos.ros.nxt.sensors.BatterySensor) device;
	        		battery.updateTopic(node,seq);      		
	        	} else if (device instanceof org.lejos.ros.nxt.sensors.UltrasonicSensor) {
	        		UltrasonicSensor us = (org.lejos.ros.nxt.sensors.UltrasonicSensor) device;
	        		us.updateTopic(node,seq);
	        	} else if (device instanceof org.lejos.ros.nxt.sensors.GPS) {
	        		GPS gps = (org.lejos.ros.nxt.sensors.GPS) device;
	        		gps.updateTopic(node,seq);
	        	} else if (device instanceof org.lejos.ros.nxt.sensors.CompassSensor) {
	        		CompassSensor compass = (org.lejos.ros.nxt.sensors.CompassSensor) device;
	        		compass.updateTopic(node,seq);
	        	} else if (device instanceof org.lejos.ros.nxt.sensors.TouchSensor) {
	        		TouchSensor touch = (org.lejos.ros.nxt.sensors.TouchSensor) device;
	        		touch.updateTopic(node,seq);
	        	} else if (device instanceof org.lejos.ros.nxt.sensors.GyroSensor) {
	        		GyroSensor gyro = (org.lejos.ros.nxt.sensors.GyroSensor) device;
	        		gyro.updateTopic(node,seq);
	        	} else if (device instanceof org.lejos.ros.nxt.sensors.ColorSensor) {
	        		ColorSensor color = (org.lejos.ros.nxt.sensors.ColorSensor) device;
	        		color.updateTopic(node,seq);
	        	} else if (device instanceof org.lejos.ros.nxt.sensors.LightSensor) {
	        		LightSensor light = (org.lejos.ros.nxt.sensors.LightSensor) device;
	        		light.updateTopic(node,seq);
	        	} else if (device instanceof org.lejos.ros.nxt.sensors.SoundSensor) {
	        		SoundSensor sound = (org.lejos.ros.nxt.sensors.SoundSensor) device;
	        		sound.updateTopic(node,seq);
	        	} else if (device instanceof org.lejos.ros.nxt.sensors.AccelerationSensor) {
	        		AccelerationSensor accel = (org.lejos.ros.nxt.sensors.AccelerationSensor) device;
	        		accel.updateTopic(node,seq);
	        	}
	        }

	        //Actuator Systems
	        for (NXTDevice device : actuatorSystemsList) {
	        	if (device instanceof DifferentialNavigationSystem) {
	        		DifferentialNavigationSystem das = (org.lejos.ros.nxt.navigation.DifferentialNavigationSystem) device;
	        		if (seq % 5 == 0) das.updateTopic(node,seq); 
	        	} else if (device instanceof RangeScanner) {
	        		if (seq % 10 == 9) ((RangeScanner) device).updateTopic(node, seq);
	        	}
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
		// Nothing
	}

	@Override
	public void onShutdownComplete(Node arg0) {
		// Nothing
	}

	@Override
	public GraphName getDefaultNodeName() {
		return new GraphName("nxt_lejos_ros/lcp_proxy");
	}

	@Override
	public void onError(Node arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}	
}

