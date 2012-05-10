package org.lejos.ros.nodes.bumpercar;

//import org.ros.message.std_msgs.Float32;

import lejos.nxt.Battery;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.remote.RemoteMotor;

public class Robot {

	//Robot definition area
	
	//Actuators
	private RemoteMotor leftMotor;
	private RemoteMotor rightMotor;
	
	//Sensors
	private UltrasonicSensor usSensor1;
	private int battery;
	
	//Control
	private int state = 0;
	
	/**
	 * Constructor
	 * 
	 */
	public Robot(){
		
		leftMotor = Motor.A;
		rightMotor = Motor.C;
		usSensor1 = new UltrasonicSensor(SensorPort.S1);

		leftMotor.setPower(30);
		rightMotor.setPower(30);
		
	}
	
	public int getBattery(){
		battery = Battery.getVoltageMilliVolt();
		return battery;
	}
	
	public int getDistance(){
		usSensor1.ping();
		return usSensor1.getDistance();
	}
	
	public void stop(){
		leftMotor.stop();
		rightMotor.stop();
	}
	
	public void forward(){
		leftMotor.forward();
		rightMotor.forward();
	}

	public void backward(){
		leftMotor.backward();
		rightMotor.backward();
	}
	
	public void rotate(){
	    //leftMotor.rotate(-180, true);// start Motor.A rotating backward
	    //rightMotor.rotate(-360);  // rotate C farther to make the turn
	    leftMotor.forward();
	}
	
	public void setState(final int _state){
		state = _state;
	}
	
	public int getState(){
		return state;
	}
	
}
