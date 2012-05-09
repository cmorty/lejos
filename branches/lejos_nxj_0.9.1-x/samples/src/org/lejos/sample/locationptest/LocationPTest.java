package org.lejos.sample.locationptest;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.Delay;

public class LocationPTest {

	private static LocationProvider lp = null;
	private static Location l = null;
	private static Coordinates current = null;
	private static double course = 0;
	
	private static final int oneSecond = 1000;
	
	private static boolean getConnection(){
		
		boolean connected = false;
		
		Criteria criteria = new Criteria();		
		
		// Get an instance of the provider:
		try {
			System.out.println("1. Connecting with a BT GPS ");
			lp = LocationProvider.getInstance(criteria);
			System.out.println("2. Connected");
			
			connected = true;
			
		}catch(LocationException e) {
			System.err.println(e.getMessage());
		}
		
		return connected;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		long iteration = 0;

		if(!getConnection()){
			Button.waitForAnyPress();
			System.exit(0);
		}else{
			
			System.out.println("3. Extracting data");
			
			Delay.msDelay(oneSecond);
			
			while(!Button.ESCAPE.isDown()){

				//Get a location in every iteration 
				try {
					l = lp.getLocation(-1);
				} catch (Exception e) {
					System.err.println(e.getMessage());
					continue;
				}
				
				//Get a coordinate object
				current = l.getQualifiedCoordinates();
				course = l.getCourse();
				
				iteration ++;
				
				System.out.println("Iteration: " + iteration);
				System.out.println("Lat: " + current.getLatitude());
				System.out.println("Lon: " + current.getLongitude());
				System.out.println("Course: " + course);
				
				Delay.msDelay(oneSecond);
				
				LCD.clearDisplay();
			}
			
		}
		
		credits(2);
		System.exit(0);
	}
	
	private static void credits(int seconds){
		LCD.clear();
		LCD.drawString("LEGO Mindstorms",0,1);
		LCD.drawString("NXT Robots  ",0,2);
		LCD.drawString("run better with",0,3);
		LCD.drawString("Java leJOS",0,4);
		LCD.drawString("www.lejos.org",0,6);
		LCD.refresh();
		try {Thread.sleep(seconds*1000);} catch (Exception e) {}
	}

}
