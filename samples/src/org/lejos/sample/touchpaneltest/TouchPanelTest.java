package org.lejos.sample.touchpaneltest;

import java.awt.Point;
import java.util.ArrayList;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.TouchPanel;
import lejos.util.Delay;

public class TouchPanelTest {

	public static void main(String[] args) {
		TouchPanel tp = new TouchPanel(SensorPort.S3);
		LCD.clear();
		Point point = new Point();
		LCD.setAutoRefreshPeriod(1);
		boolean draw = true;
		boolean direct = true; // if false, draw in gesture mode
		
		ArrayList<Point> gesture = new ArrayList<Point>();
		
		while (!Button.ESCAPE.isDown()) {
			
			if (direct) { 
				point = tp.getPoint();
				if (draw)
					LCD.setPixel(point.x, point.y, 1);
				else LCD.drawString("X: "+point.x+" Y: "+point.y+"   ", 0, 6);

			} else { // gesture mode
				gesture = tp.getGesture();
				if (gesture.size()>0) {
					for (int i = 0; i<gesture.size(); i++) {
						LCD.setPixel(gesture.get(i).x, gesture.get(i).y, 1);
					}
				}
			}
			
			if (tp.isAnyButtonDown()) {
				// R1 clears screen
				if (tp.R1.isDown()) {
					Sound.playTone(261,10);
					LCD.clear();
					tp.R1.waitForRelease();
				}	
				// R2 enables direct drawing mode
				if (tp.R2.isDown()) {
					Sound.playTone(294,10);
					draw = true;
					direct = true;
					LCD.clear();
					tp.R2.waitForRelease();
				}	
				// R3 enables textual mode
				if (tp.R3.isDown()) {
					Sound.playTone(330,10);
					draw = false;
					LCD.clear();
					tp.R3.waitForRelease();
				}	
				// R4 enables gesture mode
				if (tp.R4.isDown()) {
					Sound.playTone(350,10);
					direct = false;
					LCD.drawString("gesture mode ON      ", 0, 7);
					tp.R4.waitForRelease();
				}	
				// L1 starts calibration routine
				if (tp.L1.isDown()) {
					Sound.playTone(392,10);
					tp.calibrate(); 
					LCD.clear();
					tp.L1.waitForRelease();
				}
				// L2 sets calibrated mode
				if (tp.L2.isDown()) {
					Sound.playTone(440,10);
					tp.setCalibratedMode(false);
					LCD.drawString("cal. mode OFF      ", 0, 7);
					tp.L2.waitForRelease();
				}
				// L3 sets uncalibrated mode
				if (tp.L3.isDown()) {
					Sound.playTone(494,10);
					tp.setCalibratedMode(true);
					LCD.drawString("cal. mode ON      ", 0, 7);
					tp.L3.waitForRelease();
				}	
				// L4 resets factory default calibration
				if (tp.L4.isDown()) {
					Sound.playTone(522,10);
					tp.restoreDefaultCalibration();
					LCD.drawString("reset DEFAULTS      ", 0, 7);
					tp.L4.waitForRelease();
				}	
			}
			Delay.msDelay(1);
			
		}

	}

}
