package org.lejos.pccomm.tests;

import lejos.nxt.Battery;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

public class NXTStatusTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		boolean connetionStatus = false;
		
		connetionStatus = connect();

		if(connetionStatus){
			
			int voltage = 0;
			
			while(true){

				voltage = Battery.getVoltageMilliVolt();
				System.out.println(voltage);
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
			}
			
		}{
			System.out.println("I can't know NXT Status");
		}
		

		
	}

	private static boolean connect(){
		boolean connectionStatus = false;
		
		NXTConnector conn = new NXTConnector();
		
		conn.addLogListener(new NXTCommLogListener(){

			public void logEvent(String message) {
				System.out.println("USBSend Log.listener: "+message);	
			}

			public void logEvent(Throwable throwable) {
				System.out.println("USBSend Log.listener - stack trace: ");
				 throwable.printStackTrace();
			}
			
		} 
		);
		
		if (!conn.connectTo("usb://")){
			System.err.println("No NXT found using USB");
		}else{
			connectionStatus = true;
		}
		
		return connectionStatus;
	}
}
