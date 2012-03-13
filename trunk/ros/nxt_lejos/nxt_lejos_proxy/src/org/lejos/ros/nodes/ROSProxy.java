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
import org.ros.namespace.GraphName;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.parameter.ParameterTree;

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
	
	UltrasonicSensor sonicSensor;
	CompassSensor compassSensor;
	GyroSensor gyroSensor;
	LightSensor lightSensor;
	SoundSensor soundSensor;
	TouchSensor touchSensor;
	ColorSensor colorSensor;
	AccelerationSensor accelerationSensor;
	
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
		
		List<HashMap<String,?>> robotParams = (List<HashMap<String,?>>) params.getList("nxt_robot");
		if (robotParams != null) {
			for (HashMap<String,?> m: robotParams) {
				System.out.println(m);
				
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
			
			long start = System.currentTimeMillis();
			
			while(true) {
				byte type;
				
				try {
					type = dis.readByte();
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
	
	/**
	 * Connect to the NXT
	 */
	protected void connect() {
		System.out.println("* Connecting with a NXT brick");

		connected =  conn.connectTo((connection.equals("usb") ? "usb" : "btspp" + "://"));	    	

		if (connected) {
			System.out.println("ROS node connected with NXT brick " + brickName);
			dos = new DataOutputStream(conn.getOutputStream());
			dis = new DataInputStream(conn.getInputStream());
		} else {
			System.err.println("I can't connect with a NXT brick");
			System.exit(1);
		}
	}
	
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
	
	private byte getPort(String port) {
		if (port.equals("PORT_1")) return 0;
		else if(port.equals("PORT_2")) return 1;
	    else if(port.equals("PORT_3")) return 2;
	    else if(port.equals("PORT_4")) return 3;
		
		return -1;
	}

	@Override
	public GraphName getDefaultNodeName() {
		return new GraphName("nxt_lejos/nxt_lejos_proxy");
	}
	
	@Override
	public void onShutdown(Node arg0) {	
	}

	@Override
	public void onShutdownComplete(Node arg0) {	
	}
}
