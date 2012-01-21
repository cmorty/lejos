package org.lejos.ros.nodes.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

//NXT_LEJOS_ROS
import org.lejos.pccomm.utils.SimpleConnector;
import org.lejos.ros.nodes.LEJOSNode;
import org.lejos.ros.nxt.NXTDevice;
import org.lejos.ros.nxt.actuators.NXTServoMotor;
import org.lejos.ros.nxt.sensors.BatterySensor;
import org.lejos.ros.nxt.sensors.UltrasonicSensor;
import org.lejos.ros.nxt.systems.DifferentialActuatorSystem;

import org.ros.message.MessageListener;
import org.ros.node.Node;
import org.ros.node.topic.Subscriber;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.base.Preconditions;

/**
 * 
 * 
 * @author jabrena
 *
 */
public class NXTLoader extends LEJOSNode{
	
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
	
    //Topic Management    
    ArrayList<NXTServoMotor> motorList = new ArrayList();
    ArrayList<NXTDevice> sensorList = new ArrayList();
    ArrayList<NXTDevice> actuatorSystemsList = new ArrayList();
    
	final org.ros.message.sensor_msgs.JointState jointState = 
		new org.ros.message.sensor_msgs.JointState();
	
	/**
	 *     
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
	 * 
	 */
	protected void configurate(){
		
		System.out.println("* Configurate ROS Node");
		
	    //Show information about header
	    this.showHeader();
	    
	    //Get ROS Path
	    getROSPath();
		
	    //Load properties
		readProperties();
	}
	
	/**
	 * 
	 */
	protected void getROSPath(){
		try {
		    ROSNodePath = new File(".").getCanonicalPath() + "/"; 
			System.out.println("ROS Node Path: " + ROSNodePath );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	protected void readProperties(){
		
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
	
	/**
	 * 
	 * @return
	 */
	private String getNodeName(){
		
		//Get the name given by .launch file
		String nodeName = node.getName().toString();
		//Remove "/"
		nodeName = nodeName.substring(1, nodeName.length());
		return nodeName;
	}
	
	/**
	 * 
	 */
	protected void connect(){
		System.out.println("* Connecting with a NXT brick");
	    
	    if(CONNECTION_TYPE.equals(BLUETOOTH_CONNECTION)){
			connectionStatus = SimpleConnector.connectByBT(BRICK_NAME);	    	
	    }else{
	    	connectionStatus = SimpleConnector.connectByUSB();
	    }
		
		if(connectionStatus){
			System.out.println("ROS Node connected with a NXT brick");
		}else{
			System.err.println("I can't connect with a NXT brick");
			System.exit(0);
		}
	}
	
	/**
	 * 
	 */
	protected void bind(){
		if(connectionStatus){
			System.out.println("* Bind NXT brick with ROS");
			
			//2. Process YAML file
			processYAML();
			
			//3. Process subcriptions
			processSubscriptions();
			
			//4. Update topics
			updateTopics();
		}else{
			System.err.println("I can't connect with a NXT brick");
			System.exit(0);
		}
	}
	
	/**
	 * 
	 */
	protected void processYAML(){
	    
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
	        //System.out.println(list.size());
	        
	        int i = 0;
	        
        	//Sensors
        	String type = "";
        	String name = "";
    		String port = "";
    		float desiredFrequency = 0f;

	        for (Object obj : list) {
	        	//System.out.println(list.get(i).toString());
	        	Map map2 = (Map) list.get(i);
	        	//System.out.println(map2.get("type"));
	        	
	        	type = map2.get("type").toString().trim(); 

	        	//Actuators
	        	if(type.equals("motor")){
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
        			//sm.setPort(port);
        			sm.setDesiredFrequency(desiredFrequency);
        			sm.publishTopic(node);
        			motorList.add(sm);
	        		
	        	//Sensors
	        	}else if(type.equals("battery")){
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
	        	
	        	}else if(type.equals("ultrasonic")){
	        	//}else{
	        		System.out.println("I found a sensor description");
	        		
	        		name = map2.get("name").toString().trim();
	        		port = map2.get("port").toString().trim();
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
	        		
        			//String frame_id = map2.get("frame_id").toString().trim();
        			//Float range_max = Float.parseFloat(map2.get("range_max").toString().trim());
        			//Float range_mix = Float.parseFloat(map2.get("range_min").toString().trim());
        			
        			//System.out.println(frame_id);
        			//System.out.println(range_max);
        			//System.out.println(range_mix);
        			
        			UltrasonicSensor us = new UltrasonicSensor(port);
        			us.setName(name);
        			us.setDesiredFrequency(desiredFrequency);
        			us.publishTopic(node);
        			sensorList.add(us);		        			
	        	
	        	}else if(type.equals("differential_actuator_system")){
        		
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
	        				
	        		//System.out.println(motorPortLetter1);
	        		//System.out.println(motorPortLetter2);
	        		
	        		DifferentialActuatorSystem df = new DifferentialActuatorSystem(motorPortLetter1, motorPortLetter2,WHEEL_DIAMETER, TRACK_WIDTH, REVERSE);
        			df.setName(name);
        			df.setDesiredFrequency(desiredFrequency);
	        		df.publishTopic(node);
	        		actuatorSystemsList.add(df);	
	        		
	        	}else{
	        		System.out.println("I found a rare device");
	        		
	        		name = map2.get("name").toString().trim();
	        		System.out.println(type);
	        		System.out.println(name);
	        		
	        	}
	        	
	        	i++;
	        }
	        
	    //JAB: 2012/01/06
	    //It is necessary to add this exception handling
	    }catch (Exception e) {
	    	if (node != null) {
	    		node.getLog().fatal(e);
	    		System.err.println(e.getStackTrace());
	    	} else {
	    		e.printStackTrace();
	    	}
	    }

		
	}
	
	/**
	 * 
	 */
	protected void processSubscriptions(){
		
    	System.out.println("* Enabling subscriptions");
    	
    	if(motorList.size()>0){

    		//Subscription to joint_command
            Subscriber<org.ros.message.nxt_msgs.JointCommand> subscriberMotorA =
    	        node.newSubscriber("joint_command", "nxt_msgs/JointCommand");
            subscriberMotorA.addMessageListener(new MessageListener<org.ros.message.nxt_msgs.JointCommand>() {
    	    	@Override
    	    	public void onNewMessage(org.ros.message.nxt_msgs.JointCommand message) {
    	    		
    	    		String name = message.name;
    	    		//System.err.println(message.name);

    				//Actuators
    				for (NXTDevice device : motorList){
    		        	if(device instanceof org.lejos.ros.nxt.actuators.NXTServoMotor){
    		        		NXTServoMotor motor = (org.lejos.ros.nxt.actuators.NXTServoMotor) device;
    		        		//motor.updateTopic();
    		        		if(motor.getName().equals(name)){
    		    	    		node.getLog().info("State: \"" + message.name + " " + message.effort + "\"");
    		    	    		
    		    	    		motor.updateJoint(message.effort);
    		        		}
    		        	}
    				}	    		
    	    	}
    	    });
    	}
    	
    	//TODO: Datatype must change soon
    	if(actuatorSystemsList.size() > 0){
    		
    		//Subscription to joint_command
            Subscriber<org.ros.message.std_msgs.String> subscriberDifferentialActuatorSystem =
    	        node.newSubscriber("das_command", "std_msgs/String");
            subscriberDifferentialActuatorSystem.addMessageListener(new MessageListener<org.ros.message.std_msgs.String>() {
    	    	@Override
    	    	public void onNewMessage(org.ros.message.std_msgs.String message) {
    	    		
    	    		String cmd = message.data;
    	    		System.err.println(cmd);
	    		
    	    	}
    	    });
    		
    	}

	}
	
	/**
	 * 
	 */
	protected void updateTopics(){

		System.out.println("* Updating Topics");
		
        //Publish data
        int seq = 0;
		while(true){
        
			if(motorList.size() > 0){

				//Actuators
				for (NXTDevice device : motorList){
		        	if(device instanceof org.lejos.ros.nxt.actuators.NXTServoMotor){
		        		NXTServoMotor motor = (org.lejos.ros.nxt.actuators.NXTServoMotor) device;
		        		motor.updateTopic();
		        	}
				}
				
			}

			
			//Sensors
	        for (NXTDevice device : sensorList){
	        	if(device instanceof org.lejos.ros.nxt.sensors.BatterySensor){
	        		BatterySensor battery = (org.lejos.ros.nxt.sensors.BatterySensor) device;
	        		battery.updateTopic();
	        		
	        	}else if(device instanceof org.lejos.ros.nxt.sensors.UltrasonicSensor){
	        		//System.out.println("UltrasonicSensor");
	        		UltrasonicSensor us = (org.lejos.ros.nxt.sensors.UltrasonicSensor) device;
	        		us.updateTopic();
	        	}
	        }

	        //Actuator Systems
	        for (NXTDevice device : actuatorSystemsList){

        		DifferentialActuatorSystem das = (org.lejos.ros.nxt.systems.DifferentialActuatorSystem) device;
        		das.updateTopic();
	       	
	        }
	        
			seq++;
		}
	}
	
	private void showHeader(){
		
		System.out.println("");
		System.out.println("*********************");
	    System.out.println("* Running NXTLoader *");
		System.out.println("*********************");
		System.out.println("");
	}
	
}
