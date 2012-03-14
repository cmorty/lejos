package org.lejos.sample.rosresponder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lejos.nxt.ColorSensor;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.SoundSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.AccelMindSensor;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.addon.GyroSensor;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.USB;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;

public class ROSResponder {
	private static boolean blueTooth = true;
	
	private static RegulatedMotor leftMotor;
	private static RegulatedMotor rightMotor;
	
	private static DifferentialPilot robot;
	private static OdometryPoseProvider posep;
	
	private static float linearVelocity = 0;
	private static float angularVelocity = 0;
	
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
	
	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	private static ArrayList<SensorReader> sensorReaders = new ArrayList<SensorReader>();
	private static ArrayList<MotorReader> motorReaders = new ArrayList<MotorReader>();
	
	private static float trackWidth, wheelDiameter;
	private static boolean reverse;
	
	public static void main(String[] args) {
		
		NXTConnection conn = ((blueTooth)
				? Bluetooth.waitForConnection()
		        : USB.waitForConnection());
		
	    dis = conn.openDataInputStream();
		dos = conn.openDataOutputStream();
		
		Responder r = new Responder();
		Thread t = new Thread(r);
		t.start();
		
		while(true) {
			try {
				synchronized(sensorReaders) {
					for(SensorReader sr: sensorReaders) {
						dos.writeByte(sr.getType());
						dos.writeFloat(sr.getReading());
					}
				}
				synchronized(motorReaders) {
					for(MotorReader mr: motorReaders) {
						dos.writeByte(mr.getType());
						dos.writeInt(mr.getReading());
					}
				}
				if (posep != null) {
					dos.writeByte(BASE);
					Pose p = posep.getPose();
					dos.writeFloat(p.getX());
					dos.writeFloat(p.getY());
					dos.writeFloat(p.getHeading());
					dos.writeFloat(linearVelocity);
					dos.writeFloat(angularVelocity);
				}
				dos.flush();
			} catch (IOException e) {
				System.exit(1);
			}
		}
	}
	
	static class SensorReader implements Runnable {
		private float reading;
		private byte type;
		private UltrasonicSensor sonic;
		private CompassHTSensor compass;
		private GyroSensor gyro;
		private AccelMindSensor accel;
		private TouchSensor touch;
		private SoundSensor sound;
		private ColorSensor color;
		private LightSensor light;
		
		private boolean set = false;
		
		public void setTypeAndPort(byte type, SensorPort port) {
			this.type = type;
			set=true;
			
			if (type == SONIC) {
				sonic = new UltrasonicSensor(port);
			} else if (type == COMPASS) {
				compass = new CompassHTSensor(port);
			} else if (type == GYRO) {
				gyro = new GyroSensor(port);
			} else if (type == ACCEL) {
				accel = new AccelMindSensor(port);
			} else if (type == TOUCH) {
				touch = new TouchSensor(port);
			} else if (type == COLOR) {
				color = new ColorSensor(port);
			} else if (type == LIGHT) {
				light = new LightSensor(port);
			} else if (type == SOUND) {
				sound = new SoundSensor(port);
			}
		}
		
		public float getReading() {
			return reading;
		}
		
		public byte getType() {
			return type;
		}
		
		public void run() {
			while(true) {
				if (set) {
					switch (type) {
					case SONIC:
						reading = sonic.getRange();
						break;					
					case COMPASS:
						reading = compass.getDegrees();
						break;
					case GYRO:
						reading = gyro.getAngularVelocity();
						break;
					case ACCEL:
						reading = accel.getXAccel();
						break;
					case SOUND:
						reading = sound.readValue();
						break;
					case LIGHT:
						reading = light.getLightValue();
						break;
					case COLOR:
						reading = color.getLightValue();
						break;
					case TOUCH:
						reading = (touch.isPressed() ? 1 : 0);
						break;
					}
				}
				Thread.yield();
			}			
		}	
	}
	
	static class MotorReader implements Runnable {
		private int reading;
		private NXTRegulatedMotor motor;
		private byte type;
		private boolean set = false;
		
		public void setMotor(byte type, NXTRegulatedMotor motor) {
			this.motor = motor;
			this.type = type;
			set=true;
		}
		
		public byte getType() {
			return type;
		}
		
		public int getReading() {
			return reading;
		}
		
		public void run() {
			while(true) {
				if (set) {
					reading = motor.getTachoCount();
				}
				Thread.yield();
			}			
		}	
	}
	
	static class Responder implements Runnable {
		public void run() {
			while (true) {
				try {
					byte type = dis.readByte();
					switch (type) {
					case TWIST:
						float linear = dis.readFloat();
						float angular = dis.readFloat();
						
						if (angular == 0 && linear != 0) { // Straight line
							robot.setTravelSpeed(Math.abs(linear * 100));
							boolean forward = (linear > 0);
							if (forward) robot.forward();
							else robot.backward();
						} else if (linear != 0) { // Arc
							boolean forward = (linear > 0);
							float radius = (linear/angular) * 100;
							// Increase speed so center of the robot goes at the linear speed
							robot.setTravelSpeed(Math.abs(linear * 100) * ((radius + (trackWidth/2)) / radius));
							if (forward) {
								robot.arcForward(radius);
							} else {
								robot.arcBackward(radius);
							}
						} else if (angular != 0) { // Rotate
							boolean left = (angular > 0);
							robot.setRotateSpeed(Math.abs(Math.toDegrees(angular)));
							if (left) robot.rotateLeft();
							else robot.rotateRight();
						} else if (linear == 0 && angular == 0) {
							robot.stop();
						}
						
						angularVelocity = angular;
						linearVelocity = linear;
						break;
					case CONFIGURE_SENSOR:
						byte sType = dis.readByte();
						byte portId = dis.readByte();
						SensorReader sr = new SensorReader();
						SensorPort port = SensorPort.getInstance(portId);
						sr.setTypeAndPort(sType, port);
						synchronized(sensorReaders) {
							sensorReaders.add(sr);
						}
						Thread t = new Thread(sr);
						t.start();
						break;
					case CONFIGURE_MOTOR:
						byte mType = dis.readByte();
						MotorReader mr = new MotorReader();
						NXTRegulatedMotor motor = Motor.getInstance(mType - MOTOR_A);
						mr.setMotor(mType, motor);
						synchronized(motorReaders) {
							motorReaders.add(mr);
						}
						Thread tm = new Thread(mr);
						tm.start();
						break;
					case FORWARD:
						robot.forward();
						break;
					case BACKWARD:
						robot.backward();
						break;
					case ROTATE_LEFT:
						robot.rotateLeft();
						break;
					case ROTATE_RIGHT:
						robot.rotateRight();
						break;
					case TRAVEL:
						float distance = dis.readFloat();
						robot.travel(distance);
						break;
					case ROTATE:
						float angle = dis.readFloat();
						robot.rotate(angle);
						break;
					case SET_TRAVEL_SPEED:
						float speed = dis.readFloat();
						robot.setTravelSpeed(speed);
						break;
					case SET_ROTATE_SPEED:
						float rotateSpeed = dis.readFloat();
						robot.setRotateSpeed(rotateSpeed);
						break;
					case STOP:
						robot.stop();
						break;
					case SHUT_DOWN:
						System.exit(0);
					case SET_POSE:
						float x = dis.readFloat();
						float y = dis.readFloat();
						float heading = dis.readFloat();
						posep.setPose(new Pose(x,y,heading));
						break;
					case CONFIGURE_PILOT:
						byte leftMotorId = dis.readByte();
						byte rightMotorId = dis.readByte();
						wheelDiameter = dis.readFloat();
						trackWidth = dis.readFloat();
						reverse = dis.readBoolean();
						leftMotor = Motor.getInstance(leftMotorId);
						rightMotor = Motor.getInstance(rightMotorId);
				    	robot = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
				    	posep = new OdometryPoseProvider(robot);
				    	break;
					}
				} catch (IOException e) {
					System.exit(1);
				}				
			}
		}		
	}
}
