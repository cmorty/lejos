package org.lejos.sample.imutest;

import lejos.nxt.addon.DIMUAccel;
import lejos.nxt.addon.DIMUGyro;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;

public class ImuTest {
	public static void main(String[] args) {
		DIMUAccel accel = new DIMUAccel(SensorPort.S1);
		DIMUGyro gyro = new DIMUGyro(SensorPort.S1);
		float[] accelData = new float[3];
		float[] gyroData = new float[3];
		
		LCD.drawString("A X", 0, 0);
		LCD.drawString("A Y", 0, 1);
		LCD.drawString("A Z", 0, 2);
		
		LCD.drawString("G X", 0, 4);
		LCD.drawString("G Y", 0, 5);
		LCD.drawString("G Z", 0, 6);
		
		while(!Button.ESCAPE.isDown()) {
			accel.fetchAllAccel(accelData);
			gyro.fetchAllRate(gyroData);
			
			LCD.drawInt((int) accelData[0],6, 4, 0);
			LCD.drawInt((int) accelData[1],6, 4, 1);
			LCD.drawInt((int) accelData[2],6, 4, 2);
			LCD.drawInt((int) gyroData[0],6, 4, 4);
			LCD.drawInt((int) gyroData[1],6, 4, 5);
			LCD.drawInt((int) gyroData[2],6, 4, 6);
		}
	}
}
