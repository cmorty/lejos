
import java.io.*;
import java.util.Vector;

import lejos.nxt.comm.*;
import lejos.nxt.*;

public class StartUpText {
	static boolean update = true;
    
	public static void main(String[] args) throws Exception {
		Indicators ind = new Indicators();
		USBRespond usb = new USBRespond();
		BTRespond bt = new BTRespond(); 
		String title = " leJOS NXJ";
		String devices = "Devices";
		String found = "Found";
		String status = "Status ";
		String on = "on ";
		String off = "off";
		String visible = "vis  ";
		String invisible = "invis";
		String bluetooth = "Bluetooth";
		String system = "System";
		String freeFlash = "Free flash";
		TextMenu filesMenu = new TextMenu(null,1);
		String[] topMenuData = {"Files", "Bluetooth", "System"};
		TextMenu topMenu = new TextMenu(topMenuData,1);
		String[] fileMenuData = {"Execute program", "Delete file"}; 
		TextMenu fileMenu = new TextMenu(fileMenuData,2);
		String[] fileNames = new String[File.MAX_FILES];
		TextMenu menu = topMenu;
		String[] blueMenuData = {"Devices", "Search", "On/Off","Visibility"};
		TextMenu blueMenu = new TextMenu(blueMenuData,3);
		String[] systemMenuData = {"Defrag", "Format"};
		TextMenu systemMenu = new TextMenu(systemMenuData,3);
		File[] files = null;
		boolean quit = false;
		int blueStatus = 0, visibility = 0;
		
		ind.setDaemon(true);
		ind.start();
		usb.setDaemon(true);
		usb.setIndicator(ind);
		usb.start();
		bt.setDaemon(true);
		bt.setIndicator(ind);
		bt.start();
		
		while (!quit) 
		{
		    LCD.drawInt( (int)(Runtime.getRuntime().freeMemory()),0,0);
			usb.setMenu(menu);
			bt.setMenu(menu);
			LCD.clear();
			LCD.drawString(title,6,0);
		    LCD.drawInt( (int)(Runtime.getRuntime().freeMemory()),0,0);
			LCD.refresh();
			
			if (menu == filesMenu) {
				files = File.listFiles();
				int len = 0;
				for(int i=0;i<files.length && files[i] != null;i++) len++;		
				for(int i=0;i<len;i++) fileNames[i] = files[i].getName();
				for(int i = len; fileNames[i] != null && i<files.length;i++)fileNames[i] = null;
				filesMenu.setItems(fileNames);
			} else if (menu == blueMenu) {
				LCD.drawString(bluetooth, 3, 1);
				LCD.drawString(status,0,2);
				blueStatus = Bluetooth.getStatus();
				visibility = Bluetooth.getVisibility();
				LCD.drawString(blueStatus == 0 ? on : off, 7, 2);
				LCD.drawString(visibility == 1 ? visible : invisible, 11, 2);			
				LCD.refresh();
			} else if (menu == systemMenu) {
				LCD.drawString(system, 4, 1);
				LCD.drawString(freeFlash, 0, 2);
				int free = File.freeMemory();
				int size = 5;
				int pos = 11;
				if (free >= 100000) {
					size = 6;
					pos = 10;
				}
				LCD.drawInt(free,size, pos, 2);
				LCD.refresh();				
			}

		    int selection = menu.select();
		    
		    if (menu == topMenu) {
		    	 if (selection == 0) {
		    		 menu = filesMenu;
		    	 } else if (selection ==1) {
		    		 menu = blueMenu;
		    	 } else if (selection == 2) {
		    		 menu = systemMenu;
		    	 } else if (selection == -1) {
		    		 quit = true;
		    	 }
		    } else if (menu == filesMenu) {
			    if (selection >= 0 && files[selection] != null) {
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
			    } if (selection == -1) {
			    	menu = topMenu;
			    }
		    } else if (menu == blueMenu) {
		    	if (selection == 0) { //Devices
    	    		Vector devList = Bluetooth.getKnownDevicesList();
		    		if (devList.size() > 0) {
		    			String[] names = new String[devList.size()];
		    			for (int i = 0; i < devList.size(); i++) {
		    				BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(i));
		    				names[i] = btrd.getFriendlyName();
		    			}
		    				
		    			TextMenu deviceMenu = new TextMenu(names,1);
		    			String[] subItems = {"Remove"};
		    			TextMenu subMenu = new TextMenu(subItems,5);

		    			int selected;
		    			do {
		    				LCD.clear();
				    		LCD.drawString(devices,5,0);
				    		LCD.refresh();
		    				selected = deviceMenu.select();
		    				if (selected >=0) {
		    					BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(selected));
		    					LCD.clear();
		    					LCD.drawString(devices,5,0);
		    					LCD.drawString(names[selected],0,1);
		    					LCD.drawString(btrd.getAddressString(), 0, 2);
		    					for(int i=0;i<4;i++) LCD.drawInt(btrd.getDeviceClass()[i], 3, i*4, 3);
		    					int subSelection = subMenu.select();
		    					if (subSelection == 0) {
		    						Bluetooth.removeDevice(btrd);
		    						selected = -1;
		    					}
		    				}
		    			} while (selected >= 0);

		    		} else {
		    			LCD.clear();
		    			LCD.drawString("no known devices", 0, 0);
		    			LCD.refresh();
		    			try {
		    				Thread.sleep(2000);
		    			} catch (InterruptedException e) {}
		    		}
		    	} else if (selection == 1) { // Search    		
		    		byte[] cod = {0,0,8,4}; // Toy, Robot
		    		LCD.clear();
		    		LCD.drawString("Searching ...", 0, 0);
		    		LCD.refresh();
		    		Vector devList = Bluetooth.inquire(5, 10,cod);
					
		    		if (devList.size() > 0) {
		    			String[] names = new String[devList.size()];
		    			for (int i = 0; i < devList.size(); i++) {
		    				BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(i));
		    				names[i] = btrd.getFriendlyName();
		    			}
		    				
		    			TextMenu searchMenu = new TextMenu(names,1);
		    			String[] subItems = {"Add"};
		    			TextMenu subMenu = new TextMenu(subItems,4);
		    			
		    			int selected;
		    			do {
				    		LCD.clear();
							LCD.drawString(found,6,0);
							LCD.refresh();
		    				selected = searchMenu.select();
		    				if (selected >=0) {
		    					BTRemoteDevice btrd = ((BTRemoteDevice) devList.elementAt(selected));
		    					LCD.clear();
		    					LCD.drawString(found,6,0);
		    					LCD.drawString(names[selected],0,1);
		    					LCD.drawString(btrd.getAddressString(), 0, 2);
		    					int subSelection = subMenu.select();
		    					if (subSelection == 0) Bluetooth.addDevice(btrd);
		    				}
		    			} while (selected >= 0);

		    		} else {
		    			LCD.clear();
		    			LCD.drawString("no devices", 0, 0);
		    			LCD.refresh();
		    			try {
		    				Thread.sleep(2000);
		    			} catch (InterruptedException e) {}
		    		}
		        } else if (selection == 2) // On/Off
		        {
		        	//LCD.clear();
		        	//LCD.refresh();
		        	Bluetooth.setStatus((byte) (blueStatus == 0 ? 1 : 0));
		        }else if (selection == 3) // Visibility
		        {
		        	Bluetooth.setVisibility((byte) (visibility == 1 ? 0 : 1));
		        } else if (selection == -1) {
		    		menu = topMenu;
		    	}
		    	
		    } else if (menu == systemMenu) {
		    	if (selection == 0) {
		    		File.defrag();
		    	} else if (selection == 1) {
		    		File.format();
		    	} else if (selection == -1) {
		    		menu = topMenu;
		    	}
		    }
		}
		System.shutDown();
	}
}

class Indicators extends Thread 
{
	private boolean io = false;
	
	public void ioActive()
	{
		io = true;
	}
	
	public void run() 
	{
		String dot = ".";
		String [] ioProgress = {".  ", " . ", "  ."};
		int ioIndex = 0;
		int millis;
		while(true) 
		{
			try 
			{
			  if (io)
			  {
				  ioIndex = (ioIndex + 1) % ioProgress.length;
				  LCD.drawString(ioProgress[ioIndex], 13, 0);
				  io = false;
			  }
			  else
			  {
				  millis = Battery.getVoltageMilliVolt() + 50;
				  LCD.drawInt((millis - millis%1000)/1000,13,0);
				  LCD.drawString(dot, 14, 0);
				  LCD.drawInt((millis% 1000)/100,15,0);
			  }
			  LCD.refresh();
			  Thread.sleep(1000);
			} catch (InterruptedException ie) {}
		}
	}
}

class USBRespond extends Thread 
{
	TextMenu menu;
	Indicators ind;
	
	public void setMenu(TextMenu menu) {
		this.menu = menu;
	}
	
	public void setIndicator(Indicators ind) {
		this.ind = ind;
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
				ind.ioActive();
				int replyLen = LCP.emulateCommand(inMsg,len, reply);
				if ((inMsg[0] & 0x80) == 0) USB.usbWrite(reply, replyLen);
				if (inMsg[1] == (byte) 0x84 || inMsg[1] == (byte) 0x85) {
					Sound.beepSequenceUp();
					menu.quit();
				}
			}
			Thread.yield();
		}
	}
}

class BTRespond  extends Thread {
	TextMenu menu;
	Indicators ind;
	
	public void setMenu(TextMenu menu) {
		this.menu = menu;
	}
	
	public void setIndicator(Indicators ind) {
		this.ind = ind;
	}
	
	public void run() 
	{

		byte[] inMsg = new byte[64];
		byte [] reply = new byte[64];
		boolean cmdMode = true;
		BTConnection btc = null;
		int len;
		String connected = "Connected";
		String [] progress = {".  ", " . ", "  ."};
		int progressPos = 0;
		
		while (true)
		{
			if (cmdMode) {
				btc = Bluetooth.waitForConnection();
				if (btc == null) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {}
					continue;
				}
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
				ind.ioActive();
				int replyLen = LCP.emulateCommand(inMsg,len, reply);
				if ((inMsg[0] & 0x80) == 0) Bluetooth.sendPacket(reply, replyLen);
				if (inMsg[1] == (byte) 0x84 || inMsg[1] == (byte) 0x85) {
					Sound.beepSequenceUp();
					menu.quit();
				}
				if (inMsg[1] == (byte) 0x20) { // Disconnect
					Bluetooth.btSetCmdMode(1); // set Command mode
					cmdMode = true;
				}
			}
			Thread.yield();
		}
	}
}


