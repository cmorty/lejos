package lejos.nxt.sensor.example;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.sensor.api.*;
import lejos.nxt.sensor.sensor.*;
import lejos.nxt.sensor.filter.*;
import lejos.util.Delay;
import lejos.util.NXTDataLogger;

public class testINS {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SensorPort			port							= SensorPort.S3;
		SampleProvider accel, gyro, compass, angle;
		SensorPort.S3.i2cEnable(I2CPort.HIGH_SPEED);
		SensorPort.S4.i2cEnable(I2CPort.HIGH_SPEED);
		
		
		NXTDataLogger log=null;
		//log=connect();
		
		CuADXL345 accelSensor=new CuADXL345(port);
		accelSensor.setSampleRate(200);
		accel=new Calibrate(accelSensor,"ADXL345");
		
		CuITG3200 gyroSensor=new CuITG3200(port);
		gyroSensor.setSampleRate(200);
		gyro=new Calibrate(gyroSensor,"ITG3200");
		
		DiCompass compassSensor=new DiCompass(SensorPort.S4);
		compassSensor.setSampleRate(75);
		compass=new Calibrate(compassSensor,"DiCompass");
		
		INS attitude= new INS(gyro,accel,compass);
		attitude.setTargetFrequency(60);
		attitude.start();
		angle=attitude.getAngleProvider();
		//angle=new SampleLogger(angle,log,"Angle",true);
		angle=new DisplaySample(angle,0,0);

		
		float[] sample=new float[angle.getElementsCount()];
		
		while (!Button.ESCAPE.isDown()) {
			angle.fetchSample(sample, 0);
			LCD.drawString("Speed:"+attitude.getFrequency(),0,3);
			Delay.msDelay(100);
		}
	}
	
	static NXTDataLogger connect() {
		LCD.clear();
		LCD.drawString("Connecting..", 0, 0);
		NXTDataLogger dlog = new lejos.util.NXTDataLogger();
		NXTConnection conn = Bluetooth.waitForConnection(15000, lejos.nxt.comm.NXTConnection.PACKET);
		try {
			dlog.startRealtimeLog(conn);
		}
		catch (IOException e) {
			dlog = null;
		}
		LCD.clear();
		return dlog;
	}
}
