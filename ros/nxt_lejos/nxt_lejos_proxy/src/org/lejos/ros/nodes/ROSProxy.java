package org.lejos.ros.nodes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import lejos.pc.comm.NXTConnector;
import lejos.robotics.navigation.Pose;

import org.lejos.ros.sensors.AccelerationSensor;
import org.lejos.ros.sensors.BatterySensor;
import org.lejos.ros.sensors.ColorSensor;
import org.lejos.ros.sensors.CompassSensor;
import org.lejos.ros.sensors.GyroSensor;
import org.lejos.ros.sensors.ImuSensor;
import org.lejos.ros.sensors.LaserSensor;
import org.lejos.ros.sensors.LightSensor;
import org.lejos.ros.sensors.OdometrySensor;
import org.lejos.ros.sensors.SoundSensor;
import org.lejos.ros.sensors.TouchSensor;
import org.lejos.ros.sensors.UltrasonicSensor;
import org.ros.message.MessageListener;

import geometry_msgs.PoseStamped;
import geometry_msgs.PoseWithCovarianceStamped;
import geometry_msgs.Twist;
import nxt_lejos_msgs.DNSCommand;
import nxt_lejos_msgs.Tone;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
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
	private static final byte PLAY_TONE = 27;
	private static final byte LASER = 28;
	private static final byte CALIBRATE_COMPASS = 29;
	private static final byte CALIBRATE_GYRO = 30;
	private static final byte IMU = 31;
	private static final byte BATTERY = 32;
	private static final byte GOTO = 33;
	
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
	private OdometrySensor odometrySensor;
	private LaserSensor laserSensor;
	private ImuSensor imuSensor;
	private BatterySensor batterySensor;
	
	private float angularVelocity = 0, linearVelocity = 0;
	private Pose pose;

	
	@Override
	public void onStart(ConnectedNode node) {
		params = node.getParameterTree();
			
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
		                subscriberTwist.addMessageListener(new MessageListener<geometry_msgs.Twist>() {
		        	    	@Override
		        	    	public void onNewMessage(geometry_msgs.Twist message) { 
		        	    		float linear = (float) message.getLinear().getX();
		        	    		float angular = (float) message.getAngular().getZ();
		        	    		
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
	            	    		String t = message.getType();
	            	    		System.out.println("Sending " + t + " " + message.getValue());
	            	    		
	            	    		if (t.equals("forward")) forward();
	            	    		else if (t.equals("backward")) backward();
	            	    		else if (t.equals("stop")) stop();
	            	    		else if (t.equals("rotateLeft")) rotateLeft();
	            	    		else if (t.equals("rotateRight")) rotateRight();
	                    		else if (t.equals("shutdown")) shutDown();
	                    		else if (t.equals("travel")) travel((float) message.getValue());
	                    		else if (t.equals("rotate")) rotate((float) message.getValue());		
	                    		else if (t.equals("setTravelSpeed")) setTravelSpeed((float) message.getValue());
	                    		else if (t.equals("setRotateSpeed")) setRotateSpeed((float) message.getValue());
	            	    	}
	            	    });
	                    
            		//Subscription to set_initialpose topic
                    Subscriber<PoseWithCovarianceStamped> subscriberInitialPose =
            	        node.newSubscriber("initialpose", "geometry_msgs/PoseWithCovarianceStamped");
                    subscriberInitialPose.addMessageListener(new MessageListener<PoseWithCovarianceStamped>() {
            	    	@Override
            	    	public void onNewMessage(PoseWithCovarianceStamped message) {  
            	    		//System.out.println("Setting pose to " + message.pose.pose.position.x + ", " + message.pose.pose.position.y);
            	    		setPose((float) message.getPose().getPose().getPosition().getX() * 100, (float) message.getPose().getPose().getPosition().getY() * 100, 
            	    				(float) Math.toDegrees(quatToHeading(message.getPose().getPose().getOrientation().getZ(), message.getPose().getPose().getOrientation().getW())));	            	    		
            	    	}
            	    });
                    
            		//Subscription to goal topic
                    Subscriber<PoseStamped> subscriberGoal =
            	        node.newSubscriber("goal", "geometry_msgs/PoseStamped");
                    subscriberGoal.addMessageListener(new MessageListener<PoseStamped>() {
            	    	@Override
            	    	public void onNewMessage(PoseStamped message) { 
            	    		//System.out.println("Going to " + message);
            	            goTo((float) message.getPose().getPosition().getX() * 100, (float) message.getPose().getPosition().getY() * 100, 
            	    				(float) Math.toDegrees(quatToHeading(message.getPose().getOrientation().getZ(), message.getPose().getOrientation().getW())));	            	    		
            	    	}
            	    });
                    
                    // Create an odometry sensor to publish the odometry data
                    odometrySensor = new OdometrySensor(this,node,frequency);
		                
				} else {
					if (!type.equals("laser") && !type.equals("imu")) { // Skip virtual sensors
						configureSensor(getType(type), getPort(port));
					}
					
					switch (getType(type)) {
					case SONIC:
						sonicSensor = new UltrasonicSensor(node,name,frequency, OdometrySensor.ROBOT_FRAME);
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
					case LASER:
						laserSensor = new LaserSensor(node,name,frequency);
						break;
					case IMU:
						imuSensor = new ImuSensor(this,node,name,frequency);
						break;
					}
				}				
			}
			
			//Subscription to play_tone_command
	        Subscriber<Tone> subscriberTone =
		        node.newSubscriber("play_tone", "nxt_lejos_msgs/Tone");
	        subscriberTone.addMessageListener(new MessageListener<Tone>() {
		    	@Override
		    	public void onNewMessage(Tone message) {   		
		    		playTone(message.getPitch(), message.getDuration())	;
		    	}
		    });
	        
	        // Always publish battery readings
	        batterySensor = new BatterySensor(node,"battery",10.0);
			
			// Endless loop to read sensor values from the NXT and publish them at required frequencies
			long start = System.currentTimeMillis();
			
			while(true) {			
				try {
					byte type = dis.readByte();
					
					switch (type) {
					case SONIC:
						floatValue = dis.readFloat();
						sonicSensor.publish(start, floatValue);
						if (laserSensor != null) laserSensor.publish(start, floatValue);
						break;
					case COMPASS:
						floatValue = dis.readFloat();
						compassSensor.publish(start, floatValue);
						if (imuSensor != null) imuSensor.publish(start,0f);
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
						float x = dis.readFloat();
						float y = dis.readFloat();
						float heading = dis.readFloat();
						pose = new Pose(x,y,heading);
						linearVelocity = dis.readFloat();
						angularVelocity = dis.readFloat();
						odometrySensor.publish(start,0f); // Dummy argument
						break;
					case GYRO:
						floatValue = dis.readFloat();
						gyroSensor.publish(start, floatValue);
						if (imuSensor != null) imuSensor.publish(start,0f);
						break;
					case LIGHT:
						floatValue = dis.readFloat();
						lightSensor.publish(start,  floatValue);
						break;
					case ACCEL:
						floatValue = dis.readFloat();
						accelerationSensor.publish(start, floatValue);
						if (imuSensor != null) imuSensor.publish(start,0f);
						break;
					case COLOR:
						floatValue = dis.readFloat();
						colorSensor.publish(start, floatValue);
						break;
					case BATTERY:
						floatValue = dis.readFloat();
						batterySensor.publish(start, floatValue);
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
		else if (type.equals("laser")) return LASER;
		else if (type.equals("imu")) return IMU;
		
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
	 * Make the robot go to a destination pose
	 */
	private void goTo(float x, float y, float heading) {
		try {
			dos.writeByte(GOTO);
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
	
	/*
	 * Play a tome on the NXT
	 */
	private void playTone(short freq, short duration) {
		try {
			dos.writeByte(PLAY_TONE);
			dos.writeShort(freq);
			dos.writeShort(duration);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private void calibrateCompass() {
		try {
			dos.writeByte(CALIBRATE_COMPASS);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private void calibrateGyro() {
		try {
			dos.writeByte(CALIBRATE_GYRO);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	/**
	 * Get the current angular velocity
	 * @return the current angular velocity
	 */
	public float getAngularVelocity() {
		return angularVelocity;
	}
	
	/**
	 * Get the current linear velocity
	 * @return the current linear velocity
	 */
	public float getLinearVelocity() {
		return linearVelocity;
	}
	
	/**
	 * Get the current pose
	 * @return the current pose
	 */
	public Pose getPose() {
		return pose;
	}
	
	public GyroSensor getGyroSensor() {
		return gyroSensor;
	}
	
	public CompassSensor getCompassSensor() {
		return compassSensor;
	}
	
	public AccelerationSensor getAccelerationSensor() {
		return accelerationSensor;
	}
	
	private double quatToHeading(double z, double w) {
		return 2 * Math.atan2(z, w);
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

	@Override
	public void onError(Node arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}
}
