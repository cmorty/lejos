package lejos.nxt.sensor.example;

import java.io.IOException;

import lejos.nxt.I2CPort;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.sensor.api.*;
import lejos.nxt.sensor.filter.*;
import lejos.nxt.sensor.sensor.*;
import lejos.util.Delay;

public class Compass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Compass c=new Compass();
	}
	
	public Compass() {
		float[] sample=new float[3];
	// connect to NXTchartingLogger
			lejos.util.NXTDataLogger dlog = new lejos.util.NXTDataLogger();
			lejos.nxt.comm.NXTConnection conn = Bluetooth.waitForConnection(15000, lejos.nxt.comm.NXTConnection.PACKET);
			try {
			    dlog.startRealtimeLog(conn);
			} catch (IOException e) {
			    // Do nothing
			}
		
		SensorPort.S1.i2cEnable(I2CPort.HIGH_SPEED);			
		SensorPort.S2.i2cEnable(I2CPort.HIGH_SPEED);			
		DiCompass compassSensor=new DiCompass(SensorPort.S1);
		compassSensor.setSampleRate(30f);
		SampleProvider compass=compassSensor;
//		compass=new SampleLogger(compass,dlog,"Raw");
		compass=new Calibrate(compass,"DiCompass");
		compass=new SampleLogger(compass,dlog,"Compass");
		
		SampleProvider accel=new MsAccelV3(SensorPort.S2);
		accel=new Calibrate(accel,"MsAccel");
		accel=new LowPass(accel,0.1f);
		accel=new SampleLogger(accel,dlog,"Accel");

		SampleProvider direction=new Azimuth(compass,accel);
		direction=new SampleLogger(direction,dlog,"Heading");
		
		
		while(true) {
			LCD.clear();
			//LCD.drawString("Direction:"+Math.toDegrees(direction.fetchSample()), 0, 0);
			//LCD.drawString("Compensated:"+Math.toDegrees(compensatedDirection.fetchSample()), 0, 1);
			direction.fetchSample(sample,0);
			dlog.finishLine();
			LCD.drawInt((int) Math.toDegrees(sample[0]),0,0);
			Delay.msDelay(30);
		}
		
	}

}
