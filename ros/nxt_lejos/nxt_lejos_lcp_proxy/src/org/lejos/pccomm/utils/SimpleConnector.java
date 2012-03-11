package org.lejos.pccomm.utils;

import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;

/**
 * 
 * This class has been designed to help developers with a the creation of a 
 * USB connection or a Bluetooth connection using PCCOMM library from LeJOS
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class SimpleConnector {

	private static NXTConnector conn;
	
	private static final String USB_CONNECTION_PROTOCOL = "usb://";
	private static final String BT_CONNECTION_PROTOCOL = "btspp://";
	
	/**
	 * This internal method initialize a connection
	 * 
	 */
	private static void initializeConnection(){
		
		conn = new NXTConnector();
		
		conn.addLogListener(new NXTCommLogListener(){

				public void logEvent(final String message) {
					System.out.println("Log.listener: "+ message);	
				}
	
				public void logEvent(Throwable throwable) {
					System.out.println("Log.listener - stack trace: ");
					throwable.printStackTrace();
				}

			}
		);
		
		conn.setDebug(true);
	}
	
	/**
	 * This method connect with a the first NXT brick
	 * detected using USB
	 * 
	 * @return
	 */
	public static boolean connectByUSB(){

		initializeConnection();
		
		return conn.connectTo(USB_CONNECTION_PROTOCOL);
	}
	
	/**
	 * This method connect with a NXT Brick connected by USB.
	 * 
	 * @param brickName
	 * @return
	 */
	public static boolean connectByUSB(final String brickName){

		boolean connectionStatus = false;
		
		initializeConnection();
		
		if (conn.connectTo(USB_CONNECTION_PROTOCOL+brickName, NXTComm.PACKET)) {
			NXTCommandConnector.setNXTCommand(new NXTCommand(conn.getNXTComm()));			
			connectionStatus = true;
		}
		
		return connectionStatus;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean connectByBT(){
		boolean connectionStatus = false;

		initializeConnection();

		if (conn.connectTo(BT_CONNECTION_PROTOCOL, NXTComm.PACKET)) {
			NXTCommandConnector.setNXTCommand(new NXTCommand(conn.getNXTComm()));			
			connectionStatus = true;
		}
		
		return connectionStatus;
	
	}
	
	/**
	 * This method creates a Bluetooth connection with a NXT brick using the name
	 * of the brick to connect
	 * 
	 * @param brickName
	 * @return
	 */
	public static boolean connectByBT(final String brickName){
		boolean connectionStatus = false;

		initializeConnection();

		if (conn.connectTo(BT_CONNECTION_PROTOCOL+brickName, NXTComm.PACKET)) {
			NXTCommandConnector.setNXTCommand(new NXTCommand(conn.getNXTComm()));			
			connectionStatus = true;
		}
		
		return connectionStatus;
	
	}
	
	/**
	 * 
	 * Close a connection
	 * 
	 */
	public static void close(){
		try {
			conn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
