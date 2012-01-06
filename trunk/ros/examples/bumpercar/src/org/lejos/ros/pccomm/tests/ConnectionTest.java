package org.lejos.ros.pccomm.tests;

import org.lejos.pccomm.utils.SimpleConnector;

import lejos.nxt.Battery;

public class ConnectionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		boolean connetionStatus = false;
		
		connetionStatus = SimpleConnector.connectByBT("ROSBRICK1");

		if(connetionStatus){
			
			int voltage = 0;
			
			while(true){

				voltage = Battery.getVoltageMilliVolt();
				System.out.println("NXT Voltage: " + voltage);
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
			}
			
		}else{
			System.out.println("I can't know NXT Status");
		}
		
	}

}
