package org.lejos.pcsample.rostest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

public class ROSTest {	
	
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
	
	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	public static void main(String[] args) {
		NXTConnector conn = new NXTConnector();
	
		conn.addLogListener(new NXTCommLogListener(){
			public void logEvent(String message) {
				System.out.println("ROSTest: "+message);			
			}
			public void logEvent(Throwable throwable) {
				System.out.println("ROSTest - stack trace: ");
				 throwable.printStackTrace();			
			}		
		} 
		);
		// Connect to any NXT over Bluetooth
		boolean connected = conn.connectTo("btspp://");
	
		if (!connected) {
			System.err.println("Failed to connect to any NXT");
			System.exit(1);
		}
		
		dos = new DataOutputStream(conn.getOutputStream());
		dis = new DataInputStream(conn.getInputStream());
		
		int numMessages = 0;
		long start = System.currentTimeMillis();
		
		float range = 255;
		float heading = 0;
		int tachoA = 0, tachoB = 0, tachoC = 0;
		boolean contact = false;
		short decibels = 0;
		float linear = 0, angular = 0;
		float x = 0, y = 0, theta = 0;
		float angularVelocity = 0;
		float intensity = 0;
		float accel = 0;
		
		configureSensor(SONIC, (byte) 0);
		configureSensor(COMPASS, (byte) 1);
		configureMotor(MOTOR_A);
		configureMotor(MOTOR_C);
		twist(0.2f,0.5f);
		
		while(true) {
			byte type;
			
			try {
				type = dis.readByte();
				switch (type) {
				case SONIC:
					range = dis.readFloat();
					break;
				case COMPASS:
					heading = dis.readFloat();
					break;
				case TOUCH:
					contact = dis.readBoolean();
					break;
				case SOUND:
					decibels = dis.readShort();
					break;
				case MOTOR_A:
					tachoA = dis.readInt();
					break;
				case MOTOR_B:
					tachoB = dis.readInt();
					break;
				case MOTOR_C:
					tachoC = dis.readInt();
					break;
				case BASE:
					x = dis.readFloat();
					y = dis.readFloat();
					theta = dis.readFloat();
					linear = dis.readFloat();
					angular = dis.readFloat();
					break;
				case GYRO:
					angularVelocity = dis.readFloat();
					break;
				case LIGHT:
					intensity = dis.readFloat();
					break;
				case ACCEL:
					accel = dis.readFloat();
					break;
				case COLOR:
					intensity = dis.readFloat();
					break;
				}
				numMessages++;
			} catch (IOException e) {
				System.err.println("IOException");
				System.exit(1);
			}
			if ((numMessages % 100 ) == 0) {
				System.out.println("Frequency: " + (1000f / ((System.currentTimeMillis() - start) / (float) numMessages)));
				System.out.println("Range = " + range + ", Heading = " + heading);
				System.out.println("Tacho A = " + tachoA + " B = " + tachoB + " C = " + tachoC);
				System.out.println("Linear = " + linear + " angular = " + angular);
				System.out.println("x = " + x + ", y =" + y + ", theta = " + theta);
				System.out.println("Contact = " + contact + ", Decibels = " + decibels);
				System.out.println("Angular velocity = " + angularVelocity);
				System.out.println("Intensity = " + intensity + ", Acceleration = " + accel);
				System.out.println("Number of messages: " + numMessages);
			}
		}
	}
	
	private static void configureSensor(byte type, byte portId) {
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
	
	private static void configureMotor(byte type) {
		try {
			dos.writeByte(CONFIGURE_MOTOR);
			dos.writeByte(type);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private static void twist(float linear, float angular) {
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
	
	private static void forward() {
		try {
			dos.writeByte(FORWARD);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private static void backward() {
		try {
			dos.writeByte(BACKWARD);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private static void stop() {
		try {
			dos.writeByte(STOP);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private static void shutDown() {
		try {
			dos.writeByte(SHUT_DOWN);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private static void rotateLeft() {
		try {
			dos.writeByte(ROTATE_LEFT);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private static void rotateRight() {
		try {
			dos.writeByte(ROTATE_RIGHT);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private static void travel(float distance) {
		try {
			dos.writeByte(TRAVEL);
			dos.writeFloat(distance);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private static void rotate(float angle) {
		try {
			dos.writeByte(TRAVEL);
			dos.writeFloat(angle);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private static void setTravelSpeed(float speed) {
		try {
			dos.writeByte(SET_TRAVEL_SPEED);
			dos.writeFloat(speed);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
	
	private static void setRotateSpeed(float speed) {
		try {
			dos.writeByte(SET_ROTATE_SPEED);
			dos.writeFloat(speed);
			dos.flush();
		} catch (IOException e) {
			System.err.println("IO Exception");
			System.exit(1);
		}
	}
}