package org.lejos.ros.nodes.loader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//NXT_LEJOS_ROS
import org.lejos.pccomm.utils.SimpleConnector;
import org.lejos.ros.nodes.LEJOSNode;
import org.lejos.ros.nxt.NXTDevice;
import org.lejos.ros.nxt.actuators.NXTServoMotor;
import org.lejos.ros.nxt.sensors.BatterySensor;
import org.lejos.ros.nxt.sensors.UltrasonicSensor;

import org.ros.message.MessageListener;
import org.ros.message.nxt_msgs.Range;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.base.Preconditions;

public class NXTLoader extends LEJOSNode{

	//NXT Connection
	
    //TODO: It is necessary to pass a parameter to node. 
    //Maybe using properties in a easy evolution, but betterh using roslaunch
    
    //final String BRICK_NAME = "ROSBRICK1";
    final String BRICK_NAME = "dog";
    final String path = "/home/jabrena/ros3/workspace/nxt_lejos_ros/NXTLoader/src/test/resources/nxt/";
    //String RobotDescriptor = "robot.yaml";
    final String RobotDescriptor = "TurtleNXT_V1.yaml";
	
    //Topic Management    
    ArrayList<NXTServoMotor> motorList = new ArrayList();
    ArrayList<NXTDevice> sensorList = new ArrayList();
    
	final org.ros.message.sensor_msgs.JointState jointState = 
		new org.ros.message.sensor_msgs.JointState();
	
	/**
	 *     
	 */
	public void onStart(final Node node) {
	    Preconditions.checkState(this.node == null);
	    this.node = node;
	    
	    //1. Connect with NXT Brick
	    boolean connetionStatus = false;
		connetionStatus = SimpleConnector.connectByBT(BRICK_NAME);
		
		if(connetionStatus){
			System.out.println("ROS Node connected with NXT brick");
			
			//2. Process YAML file
			processYAML();
			
			//3. Process subcriptions
			if(motorList.size() > 0){
			    processSubscriptions();
			}
			
			//4. Update topics
			if(sensorList.size() >0){
				updateTopics();				
			}
		}else{
			System.err.println("I can't connect with a NXT brick");
			System.exit(0);
		}
	}
		
	private void processYAML(){
	    		
		try{
		
			//Read a YAML File
	        YamlReader reader = new YamlReader(new FileReader(path+RobotDescriptor));
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
	        	}else{
	        		System.out.println("I found a sensor description");
	        		
	        		name = map2.get("name").toString().trim();
	        		if(map2.get("port") != null){
	        			port = map2.get("port").toString().trim();
	        		}else{
	        			port = "PORT_0";
	        		}
	        		desiredFrequency = Float.parseFloat(map2.get("desired_frequency").toString().trim());
	        				        		
	        		System.out.println(name);
	        		System.out.println(port);
	        		System.out.println(desiredFrequency);
	        		
	        		if(type.equals("battery")){

				        BatterySensor batteryObj = new BatterySensor();
				        batteryObj.setName(name);
				        batteryObj.setDesiredFrequency(desiredFrequency);
				        batteryObj.publishTopic(node);
				        sensorList.add(batteryObj);
	        		
	        		}else if(type.equals("ultrasonic")){
	        			
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
	        			
	        		}
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
	
	private void processSubscriptions(){
		
    	System.out.println("Enabling subscriptions");
    	
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
	
	private void updateTopics(){

        //Publish data
        int seq = 0;
		while(true){
        
			//Actuators
			for (NXTDevice device : motorList){
	        	if(device instanceof org.lejos.ros.nxt.actuators.NXTServoMotor){
	        		NXTServoMotor motor = (org.lejos.ros.nxt.actuators.NXTServoMotor) device;
	        		motor.updateTopic();
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

			seq++;
		}
	}
	
}
