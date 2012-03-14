package org.lejos.ros.nodes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import lejos.pc.comm.NXTConnector;

import org.lejos.ros.sensors.AccelerationSensor;
import org.lejos.ros.sensors.ColorSensor;
import org.lejos.ros.sensors.CompassSensor;
import org.lejos.ros.sensors.GyroSensor;
import org.lejos.ros.sensors.LightSensor;
import org.lejos.ros.sensors.SoundSensor;
import org.lejos.ros.sensors.TouchSensor;
import org.lejos.ros.sensors.UltrasonicSensor;
import org.ros.message.MessageListener;
import org.ros.message.geometry_msgs.PoseWithCovarianceStamped;
import org.ros.message.geometry_msgs.Twist;
import org.ros.message.nxt_lejos_msgs.DNSCommand;
import org.ros.namespace.GraphName;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.topic.Subscriber;

public class ROSProxy implements NodeMain {
	
	private static final byte SONIC = 0;
	private static final byte COMPASS = 1;
	private static final byte GYRO = 2;
	private static final byte ACCEL = 3;
	private static final byte TOUCH = 4;
	private static final byte SOUND = 5;
	private static final byte LIGHT = 6;
	private static final byte COLOR = 7;
	private static final byte MOTOR_A = 8;
	private static final byte MOTOR_B = 9;
	private static final byte MOTOR_C = 10;
	private static final byte TWIST = 11;
	private static final byte CONFIGURE_SENSOR = 12;
	private static final byte CONFIGURE_MOTOR = 13;
	private static final byte BASE = 14;
	private static final byte FORWARD = 15;
	private static final byte BACKWARD = 16;
	private static final byte ROTATE_LEFT = 17;
	private static final byte ROTATE_RIGHT = 18;
	private static final byte TRAVEL = 19;
	private static final byte ROTATE = 20;
	private static final byte SET_TRAVEL_SPEED = 21;
	private static final byte SET_ROTATE_SPEED = 22;
	private static final byte STOP = 23;
	private static final byte SHUT_DOWN = 24;
	private static final byte SET_POSE = 25;
	private static final byte CONFIGURE_PILOT = 26;
	
	private NXTConnector conn = new NXTConnector();
	
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private ParameterTree params;
	private String brickName;
	private String connection;	
	private boolean connected = false;
	private float floatValue;
	private int intValue;
	
	private int numMessages = 0;
	
	private UltrasonicSensor sonicSensor;
	private CompassSensor compassSensor;
	private GyroSensor gyroSensor;
	private LightSensor lightSensor;
	private SoundSensor soundSensor;
	private TouchSensor touchSensor;
	private ColorSensor colorSensor;
	private AccelerationSensor accelerationSensor;
	
	private float angularVelocity = 0, linearVelocity = 0;
	
	@Override
	public void onStart(Node node) {
		params = node.newParameterTree();
			
		brickName = params.getString("brick_name");
		System.out.println("Brick name is " + brickName);
		
		connection = params.getString("connection");
		System.out.println("Connection is " + connection);
		
		// Connect to the NXT brick
		connect();
		
		dos = new DataOutputStream(conn.getOutputStream());
		dis = new DataInputStream(conn.getInputStream());
		
		// Process nxt_robot configuration
		List<HashMap<String,?>> robotParams = (List<HashMap<String,?>>) params.getList("nxt_robot");
		if (robotParams != null) {
			for (HashMap<String,?> m: robotParams) {
				//System.out.println(m);
				
				String type = (String) m.get("type");
				System.out.println("Type is " + type);
				
				String name = (String) m.get("name");
				System.out.println("Name is " + name);
				
				String port = (String) m.get("port");
				System.out.println("Port is " + port);
				
				double frequency = (Double) m.get("desired_frequency");
				System.out.println("Desired frequency is " + frequency);
				
				if (type.equals("differential_pilot")) {
					double trackWidth = (Double) m.get("track_width");
					System.out.println("Track width is " + trackWidth);
					
					double wheelDiameter = (Double) m.get("wheel_diameter");
					System.out.println("Wheel diameter is " + wheelDiameter);
					
					boolean reverse = (Boolean) m.get("reverse");
					System.out.println("reverse is " + reverse);
					
					String leftMotorName = port.substring(5,6);
					String rightMotorName = port.substring(6,7);
					
					byte leftMotorId = (byte) (leftMotorName.charAt(0) - 'A');
					byte rightMotorId = (byte) (rightMotorName.charAt(0) - 'A');
					
					System.out.println("left motor id = " + leftMotorId);
					System.out.println("right motor id = " + rightMotorId);
					
					configurePilot(leftMotorId, rightMotorId,(float) wheelDiameter, (float) trackWidth, reverse);
					
					//Subscribe to the Twist message on "cmd_vel" topic
		            Subscriber<Twist> subscriberTwist =
		        	        node.newSubscriber("cmd_vel", "geometry_msgs/Twist");
		                subscriberTwist.addMessageListener(new MessageListener<org.ros.message.geometry_msgs.Twist>() {
		        	    	@Override
		        	    	public void onNewMessage(org.ros.message.geometry_msgs.Twist message) { 
		        	    		float linear = (float) message.linear.x;
		        	    		float angular = (float) message.angular.z;
		        	    		
		        	    		if (angular != angularVelocity || linear != linearVelocity) {
		        	    			angularVelocity = angular;
		        	    			linearVelocity = linear;
		        	    			twist(linear, angular);
		        	    		}
		        	    	}
		        	    });
		                
		            // Subscribe to the DNSCommand message on the dns_command topic
	                Subscriber<DNSCommand> subscriberDifferentialActuatorSystem =
	            	        node.newSubscriber("dns_command", "nxt_lejos_msgs/DNSCommand");
	                    subscriberDifferentialActuatorSystem.addMessageListener(new MessageListener<DNSCommand>() {
	            	    	@Override
	            	    	public void onNewMessage(DNSCommand message) { 
	            	    		String t = message.type;
	            	    		System.out.println("Sending " + t + " " + message.value);
	            	    		
	            	    		if (t.equals("forward")) forward();
	            	    		else if (t.equals("backward")) backward();
	            	    		else if (t.equals("stop")) stop();
	            	    		else if (t.equals("rotateLeft")) rotateLeft();
	            	    		else if (t.equals("rotateRight")) rotateRight();
	                    		else if (t.equals("shutdown")) shutDown();
	                    		else if (t.equals("travel")) travel((float) message.value);
	                    		else if (t.equals("rotate")) rotate((float) message.value);		
	                    		else if (t.equals("setTravelSpeed")) setTravelSpeed((float) message.value);
	                    		else if (t.equals("setRotateSpeed")) setRotateSpeed((float) message.value);
	            	    	}
	            	    });
	                    
	            		//Subscription to set_initialpose topic
	                    Subscriber<PoseWithCovarianceStamped> subscriberInitialPose =
	            	        node.newSubscriber("initialpose", "geometry_msgs/PoseWithCovarianceStamped");
	                    subscriberInitialPose.addMessageListener(new MessageListener<PoseWithCovarianceStamped>() {
	            	    	@Override
	            	    	public void onNewMessage(PoseWithCovarianceStamped message) {   		
	            	    		setPose((float) message.pose.pose.position.x * 100, (float) message.pose.pose.position.y * 100, 0f);	            	    		
	            	    	}
	            	    });
		                
				} else {
					configureSensor(getType(type), getPort(port));
					
					switch (getType(type)) {
					case SONIC:
						sonicSensor = new UltrasonicSensor(node,name,frequency);
						break;
					case COMPASS: 
						compassSensor = new CompassSensor(node,name,frequency);
						break;
					case GYRO:
						gyroSensor = new GyroSensor(node,name,frequency);
						break;
					case LIGHT:
						lightSensor = new LightSensor(node,name,frequency);
						break;
					case SOUND:
						soundSensor = new SoundSensor(node,name,frequency);
						break;
					case TOUCH:
						touchSensor = new TouchSensor(node,name,frequency);
						break;
					case COLOR:
						colorSensor = new ColorSensor(node,name,frequency);
						break;
					case ACCEL:
						accelerationSensor = new AccelerationSensor(node,name,frequency);
						break;
					}
				}				
			}
			
			// Endless loop to read sensor values from the NXT and publish them at required frequencies
			long start = System.currentTimeMillis();
			
			while(true) {			
				try {
					byte type = dis.readByte();
					
					switch (type) {
					case SONIC:
						floatValue = dis.readFloat();
						sonicSensor.publish(start, floatValue);
						break;
					case COMPASS:
						floatValue = dis.readFloat();
						compassSensor.publish(start, floatValue);
						break;
					case TOUCH:
						floatValue = dis.readFloat();
						touchSensor.publish(start, floatValue);
						break;
					case SOUND:
						floatValue = dis.readFloat();
						soundSensor.publish(start, floatValue);
						break;
					case MOTOR_A:
						intValue = dis.readInt();
						break;
					case MOTOR_B:
						intValue = dis.readInt();
						break;
					case MOTOR_C:
						intValue = dis.readInt();
						break;
					case BASE:
						floatValue = dis.readFloat();
						floatValue = dis.readFloat();
						floatValue = dis.readFloat();
						floatValue = dis.readFloat();
						floatValue = dis.readFloat();
						break;
					case GYRO:
						floatValue = dis.readFloat();
						gyroSensor.publish(start, floatValue);
						break;
					case LIGHT:
						floatValue = dis.readFloat();
						lightSensor.publish(start,  floatValue);
						break;
					case ACCEL:
						floatValue = dis.readFloat();
						accelerationSensor.publish(start, floatValue);
						break;
					case COLOR:
						floatValue = dis.readFloat();
						colorSensor.publish(start, floatValue);
						break;
					}
					numMessages++;
				} catch (IOException e) {
					System.err.println("IOException");
					System.exit(1);
				}
			}
		}
	}
	
	/*
	 * Connect to the NXT
	 */
	private void connect() {
		System.out.println("* Connecting with a NXT brick");

		connected =  conn.connectTo((connection.equals("usb") ? "usb" : "btspp" + "://"));	    	

		if (connected) {
			System.out.println("ROS node connected to NXT brick " + brickName);
			dos = new DataOutputStream(conn.getOutputStream());
			dis = new DataInputStream(conn.getInputStream());
		} else {
			System.err.println("I can't connect to a NXT brick");
			System.exit(1);
		}
	}
	
	/*
	 * Send sensor configuration to the NXT 
	 */
	private void configureSensor(byte type, byte portId) {
		try {
			dos.writeByte(CONFIGURE_SENSOR);
			dos.writeByte(type);
			dos.writeByte(portId);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Send motor configuration to the NXT
	 */
	private void configureMotor(byte type) {
		try {
			dos.writeByte(CONFIGURE_MOTOR);
			dos.writeByte(type);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Send linear and angular velocity to the NXT
	 */
	private void twist(float linear, float angular) {
		try {
			dos.writeByte(TWIST);
			dos.writeFloat(linear);
			dos.writeFloat(angular);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Convert type string to encoded value
	 */
	private byte getType(String type) {
		if (type.equals("ultrasonic")) return SONIC;
		else if (type.equals("compass")) return COMPASS;
		else if (type.equals("gyro")) return GYRO;
		else if (type.equals("light")) return LIGHT;
		else if (type.equals("color")) return COLOR;
		else if (type.equals("touch")) return TOUCH;
		else if (type.equals("acceleration")) return ACCEL;
		else if (type.equals("sound")) return SOUND;
		
		return -1;
	}
	
	/*
	 * Convert port string to port id
	 */
	private byte getPort(String port) {
		if (port.equals("PORT_1")) return 0;
		else if(port.equals("PORT_2")) return 1;
	    else if(port.equals("PORT_3")) return 2;
	    else if(port.equals("PORT_4")) return 3;
		
		return -1;
	}
	
	/*
	 * Move the robot forward
	 */
	private void forward() {
		try {
			dos.writeByte(FORWARD);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Move the robot backwards
	 */
	private void backward() {
		try {
			dos.writeByte(BACKWARD);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Stop the robot
	 */
	private void stop() {
		try {
			dos.writeByte(STOP);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Shut down the ROS responder on the NXT
	 */
	private void shutDown() {
		try {
			dos.writeByte(SHUT_DOWN);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Rotate the robot left
	 */
	private void rotateLeft() {
		try {
			dos.writeByte(ROTATE_LEFT);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Rotate the robot right
	 */
	private void rotateRight() {
		try {
			dos.writeByte(ROTATE_RIGHT);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Make the robot travel forward or backwards a specified distance
	 */
	private void travel(float distance) {
		try {
			dos.writeByte(TRAVEL);
			dos.writeFloat(distance);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Make the robot rotate a specified angle
	 */
	private void rotate(float angle) {
		try {
			dos.writeByte(ROTATE);
			dos.writeFloat(angle);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Set the robot forward/backward travel speed
	 */
	private void setTravelSpeed(float speed) {
		try {
			dos.writeByte(SET_TRAVEL_SPEED);
			dos.writeFloat(speed);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Set the robot rotate speed
	 */
	private void setRotateSpeed(float speed) {
		try {
			dos.writeByte(SET_ROTATE_SPEED);
			dos.writeFloat(speed);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Set the pose of the robot
	 */
	private void setPose(float x, float y, float heading) {
		try {
			dos.writeByte(SET_POSE);
			dos.writeFloat(x);
			dos.writeFloat(y);
			dos.writeFloat(heading);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/*
	 * Send the pilot configuration to the BXT
	 */
	private void configurePilot(byte leftMotorId, byte rightMotorId, 
			float wheelDiameter, float trackWidth, boolean reverse) {
		try {
			dos.writeByte(CONFIGURE_PILOT);
			dos.writeByte(leftMotorId);
			dos.writeByte(rightMotorId);
			dos.writeFloat(wheelDiameter);
			dos.writeFloat(trackWidth);
			dos.writeBoolean(reverse);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}

	@Override
	public GraphName getDefaultNodeName() {
		return new GraphName("nxt_lejos/nxt_lejos_proxy");
	}
	
	@Override
	public void onShutdown(Node node) {
		// No action
	}

	@Override
	public void onShutdownComplete(Node node) {
		// No action
	}
}
