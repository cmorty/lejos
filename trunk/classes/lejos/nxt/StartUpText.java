import java.io.*;
import lejos.nxt.comm.*;
import lejos.nxt.*;

public class StartUpText {
	static boolean update = true;
    
	public static void main(String[] args) throws Exception {

		Indicators ind = new Indicators();
		USBRespond usb = new USBRespond();
		BTRespond bt = new BTRespond(); 
		String title = " leJOS NXJ";
		TextMenu menu = new TextMenu(null,1);
		String[] fileMenuData = {"Execute program", "Delete file"}; 
		TextMenu fileMenu = new TextMenu(fileMenuData,2);
		String[] fileNames = new String[File.MAX_FILES];
		boolean quit = false;
		ind.setDaemon(true);
		ind.start();
		usb.setDaemon(true);
		usb.start();
		bt.setDaemon(true);
		bt.start();
		
		while (!quit) 
		{
		    LCD.drawInt( (int)(Runtime.getRuntime().freeMemory()),0,0);
			File[] files = File.listFiles();
			int len = 0;
			for(int i=0;i<files.length && files[i] != null;i++) len++;		
			for(int i=0;i<len;i++) fileNames[i] = files[i].getName();
			for(int i = len; fileNames[i] != null && i<files.length;i++)fileNames[i] = null;
			menu.setItems(fileNames);
			usb.setMenu(menu);
			bt.setMenu(menu);
			LCD.clear();
			LCD.drawString(title,6,0);
		    LCD.drawInt( (int)(Runtime.getRuntime().freeMemory()),0,0);
			LCD.refresh();

		    int selection = menu.select();
		    
		    if (selection >= 0) {
				LCD.clear();
				LCD.drawString(title,6,0);
			    LCD.drawInt( (int)(Runtime.getRuntime().freeMemory()),0,0);
				LCD.refresh();
				fileMenu.setTitle(fileNames[selection]);
		    	int subSelection = fileMenu.select();
		    	if (subSelection == 0) 
		    	{
		    		Bluetooth.btSetCmdMode(1);
		    		files[selection].exec();
		    	} else if (subSelection == 1)
		    	{
		    		files[selection].delete();	 
		    		LCD.clear();
		    		LCD.refresh();
		    	}
		    } else if (selection == -1) quit = true;
		}
	}
}

class Indicators extends Thread 
{
	public void run() 
	{
		String dot = ".";
		int millis;
		while(true) 
		{
			try 
			{
			  millis = Battery.getVoltageMilliVolt() + 50;
			  LCD.drawInt((millis - millis%1000)/1000,13,0);
			  LCD.drawString(dot, 14, 0);
			  LCD.drawInt((millis% 1000)/100,15,0);
			  LCD.refresh();
			  Thread.sleep(1000);
			} catch (InterruptedException ie) {}
		}
	}
}

class USBRespond extends Thread 
{
	TextMenu menu;
	
	public void setMenu(TextMenu menu) {
		this.menu = menu;
	}
	
	public void run() {

		byte[] inMsg = new byte[64];
		byte [] reply = new byte[64];
		int len;
		
		USB.usbReset();
		
		while (true)
		{
		
			len = USB.usbRead(inMsg,64);
			
			if (len > 0)
			{
				//LCD.drawInt(len,3,0,1);
				//LCD.drawInt(inMsg[0] & 0xFF,3,3,1);
				//LCD.drawInt(inMsg[1] & 0xFF,3,6,1);
				//LCD.drawInt(inMsg[2] & 0xFF,3,9,1);
				//LCD.drawInt(inMsg[3] & 0xFF,3,12,1);
				//LCD.refresh();
				int replyLen = LCP.emulateCommand(inMsg,len, reply);
				if ((inMsg[0] & 0x80) == 0) USB.usbWrite(reply, replyLen);
				if (inMsg[1] == (byte) 0x84 || inMsg[1] == (byte) 0x85) {
					Sound.beepSequenceUp();
					menu.quit();
				}
			}			
		}
	}
}

class BTRespond  extends Thread {
	TextMenu menu;
	
	public void setMenu(TextMenu menu) {
		this.menu = menu;
	}
	
	public void run() 
	{

		byte[] inMsg = new byte[64];
		byte [] reply = new byte[64];
		boolean cmdMode = true;
		BTConnection btc = null;
		int len;
		String connected = "Connected";
		
		while (true)
		{
			if (cmdMode) {
				btc = Bluetooth.waitForConnection();
				//LCD.clear();
				//LCD.drawString(connected,0,0);
				//LCD.refresh();			
				cmdMode = false;
			}
			
			len = Bluetooth.readPacket(inMsg,64);
			
			if (len > 0)
			{
				//LCD.drawInt(len,3,0,1);
				//LCD.drawInt(inMsg[0] & 0xFF,3,3,1);
				//LCD.drawInt(inMsg[1] & 0xFF,3,6,1);
				//LCD.drawInt(inMsg[2] & 0xFF,3,9,1);
				//LCD.drawInt(inMsg[3] & 0xFF,3,12,1);
				//LCD.refresh();
				int replyLen = LCP.emulateCommand(inMsg,len, reply);
				if ((inMsg[0] & 0x80) == 0) Bluetooth.sendPacket(reply, replyLen);
				if (inMsg[1] == (byte) 0x84 || inMsg[1] == (byte) 0x85) {
					Sound.beepSequenceUp();
					menu.quit();
				}
				if (inMsg[0] == (byte) 0x20) { // Disconnect
					Bluetooth.btSetCmdMode(1); // set Command mode
					cmdMode = true;
				}
			}			
		}
	}
}

