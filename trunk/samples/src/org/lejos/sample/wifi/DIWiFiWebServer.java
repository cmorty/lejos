package org.lejos.sample.wifi;

import java.io.IOException;

import lejos.nxt.Battery;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;

import lejos.nxt.addon.DexterWifiSensor;


/**
 * Demonstrates how to do a Webserver with the Dexter Wifisensor
 * @author Lasse S. Lauesen
 *
 */
public class DIWiFiWebServer implements Runnable {
	
	private DexterWifiSensor wifi = null;
	
	private final static String HTTP_HEADER = "HTTP/1.1 200 OK\r\n"+                          // protocol ver 1.1, code 200, reason OK
			  								  "Content-Type: text/html\r\n"+                  // type of data we want to send
			                                  "\r\n";
	
	
	//TODO insert information about your wireless network:
	//private static final String SSID = "Lauesen";
	//private static final String passphrase = "61340881";
	private static final String SSID = "free_viruses";
	private static final String passphrase = "geocaching";
	
	
	private int port;
	private String webpage;
	
	private int serverConID; //The connection ID of our TCP Server
	private int clientConID;//The connection ID of the incomming connection.
	
	public DIWiFiWebServer(int port){
		this.port = port;
		serverConID = -1;
		clientConID = -1;
	}
	
	public void run() {
		try {
			//Create the sensor:
			LCD.drawString("Connecting Sensor...   ", 0, 2);
			wifi = new DexterWifiSensor(DexterWifiSensor.BAUD6_460800);
			//Connect to a network:
			LCD.drawString("Connecting WLAN...   ", 0, 2);
			String conData = wifi.connectWPAPSK(SSID, passphrase, true);
			
			// TODO: Without a delay here, it doesn't seem to work for me. - BB
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			String ip = conData.substring(conData.indexOf(":")+1);
			ip = ip.substring(0, ip.indexOf(":"));
						
			//Start a TCP server on the wifi sensor:
			serverConID = wifi.startTCPServer(port);
			
			LCD.drawString("Listening on:          ", 0, 2);
			LCD.drawString(ip+":"+port+"      ", 0, 3);
			
			LCD.drawString("Waiting...          ", 0, 5);
			
			while(Button.ESCAPE.isUp()){
				String input = wifi.readFully(false);
				if(input.length() > 0){
					RConsole.println("Received:");
					RConsole.println(input);
					//Search input for incomming connection to our TCP-Server:
					int i = input.indexOf("CONNECT "+DexterWifiSensor.intToConIDChar(serverConID)); 
					
					if(i >= 0){
						//Found incomming connection:
						
						//Get the connection id of the incomming connection:
						clientConID = DexterWifiSensor.conIdCharToInt(input.charAt(i+10));
						if(clientConID >= 0){
							//We have a valid connection from a client
							//Search for a HTTP GET command:
							if(input.indexOf("GET /") > 0){
								//We have received a HTTP Get:
								//Send webpage:
								LCD.drawString("Sending page...      ", 0, 5);
								webpage = HTTP_HEADER + "<html><body>Your first LeJOS webpage!<br/>battery = "+Battery.getVoltage()+"</body></html>";
								RConsole.println("Sending content:");
								wifi.sendTCPData(clientConID, webpage);
								Delay.msDelay(100);
								wifi.closeAllConns();
								serverConID = wifi.startTCPServer(port);
							}
							
						}
						
					}
				}
				
				Delay.msDelay(20);
			}
				
		} catch (IOException e) {
			LCD.clear();
			LCD.drawString("##### Exception: ",0,0);
			LCD.drawString(e.getMessage(),0,1);
		}
	}
	
	public static void main(String[] args) {
		DIWiFiWebServer webserver = new DIWiFiWebServer(81);
		Thread prg = new Thread(webserver);
		prg.start();
	}
	
	
}
