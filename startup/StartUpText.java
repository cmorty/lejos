
import java.io.*;
import java.util.Vector;
import javax.microedition.lcdui.*;
import lejos.nxt.comm.*;
import lejos.nxt.*;

public class StartUpText
{
   static Graphics g = new Graphics();
   static  boolean btPowerOn = false;
   static String blank = "               ";
   
   static int [] freq = {523,784, 659};
   public static void playTune()
   {
      for(int i = 0; i<3; i++)
      {
         Sound.playTone(freq[i],250);
         Sound.pause(260);       
      }
   }
   static void drawTopRow()
   {
      LCD.drawString(blank,0,0);
      g.drawRect(0,1, 13,5);  // battery icon
      g.drawRect(14,3,1,1);
      // 2.5 v shows as empty, 9 as full;
      int b = -5+Battery.getVoltageMilliVolt()/500 ;
      g.fillRect(0,2, b,4);
      byte [] nam = Bluetooth.getFriendlyName();
      for(int i=0; i<nam.length; i++)
      {
         if(nam[i] <32)break;
         g.drawChar((char)nam[i],(4+i)*6,0,false);
      }
      g.drawString(" BT",82, 0,!btPowerOn);  // invert when power is off
      g.refresh();    
   }
	
	private static boolean setBluetoothPower(boolean powerOn, boolean needReset)
	{
		// Set the state of the Bluetooth power to be powerOn. Also record the
		// current state of this in the BT status bytes.

		// If power is not on we need it on to check things
		if (!Bluetooth.getPower())
		{
			Bluetooth.setPower(true);
		}
		// First update the status bytes if needed
		int status = Bluetooth.getStatus();
		if (powerOn != (status == 0))
			Bluetooth.setStatus((powerOn ? 0 : 1));
		Bluetooth.setPower(powerOn);
		return powerOn;
	}
    
	public static void main(String[] args) throws Exception {
		Indicators ind = new Indicators();
		USBRespond usb = new USBRespond();
		BTRespond bt = new BTRespond();
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
		String freeMem = "Free ram";
		String battery = "Battery ";
		
		TextMenu filesMenu = new TextMenu(null,1);
		String[] topMenuData = {"Files", "Bluetooth", "System"};
		TextMenu topMenu = new TextMenu(topMenuData,1);
		String[] fileMenuData = {"Execute program", "Delete file"}; 
		TextMenu fileMenu = new TextMenu(fileMenuData,2);
		String[] fileNames = new String[File.MAX_FILES];
		TextMenu menu = topMenu;
		String[] blueMenuData = {"Power off", "Search", "Devices","Visibility"};
		String[] blueOffMenuData = {"Power on"};
		TextMenu blueMenu = new TextMenu(blueMenuData,3);
		TextMenu blueOffMenu = new TextMenu(blueOffMenuData,3);
		String[] systemMenuData = {"Format"};
		String dot = ".";
        String[] yes_no = {"No","Yes"};
        TextMenu yes_noMenu = new TextMenu(yes_no,6);
        playTune();
		TextMenu systemMenu = new TextMenu(systemMenuData,5);
		File[] files = null;
		boolean quit = false;
		int visibility = 0;
		btPowerOn = setBluetoothPower(Bluetooth.getStatus() == 0, false);		
		ind.setDaemon(true);	
		ind.start();
		usb.setDaemon(true);
		usb.setIndicator(ind);
		usb.start();
		bt.setDaemon(true);
		bt.setIndicator(ind);
		bt.start();		
		// Defrag the file system	
		files = File.listFiles();
		try {
			File.defrag();
		}
		catch (IOException ioe) {
			File.reset();
		}

		while (!quit) 
		{ 
		   LCD.clear();
		   drawTopRow();
            usb.setMenu(menu);
            bt.setMenu(menu);			
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
				visibility = Bluetooth.getVisibility();
				LCD.drawString(btPowerOn ? on : off, 7, 2);
				LCD.drawString(visibility == 1 ? visible : invisible, 11, 2);			
				LCD.refresh();
			} else if (menu == blueOffMenu) {
				LCD.drawString(bluetooth, 3, 1);
				LCD.drawString(status,0,2);
				LCD.drawString(btPowerOn ? on : off, 7, 2);	
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
				LCD.drawString(battery, 0,3);
               int  millis = Battery.getVoltageMilliVolt() + 50;
                LCD.drawInt((millis - millis%1000)/1000,11,3);
                LCD.drawString(dot, 12, 3);
                LCD.drawInt((millis% 1000)/100,13,3);
                LCD.drawString(freeMem,0,4);
                LCD.drawInt((int)(Runtime.getRuntime().freeMemory()),11,4);				
			}
			int selection = menu.select();
		    if (menu == topMenu) {
		    	 if (selection == 0) {
		    		 menu = filesMenu;
		    	 } else if (selection ==1) {
		    		 menu = (btPowerOn ? blueMenu : blueOffMenu);
		    	 } else if (selection == 2) {
		    		 menu = systemMenu;
		    	 } else if (selection == -1) {
		    		 quit = true;
		    	 }
		    } else if (menu == filesMenu) {
			    if (selection >= 0 && files[selection] != null) {
			       LCD.clear();
			       drawTopRow();
//					LCD.clear();
//					LCD.drawString(title,6,0);
//				    LCD.drawInt( (int)(Runtime.getRuntime().freeMemory()),0,0);
//					LCD.refresh();
					fileMenu.setTitle(fileNames[selection]);
			    	int subSelection = fileMenu.select();
			    	if (subSelection == 0) 
			    	{
			    		files[selection].exec();
			    	} else if (subSelection == 1)
			    	{
			    		files[selection].delete();
						try {
							File.defrag();
						} catch (IOException ioe) {
							File.reset();
						}
			    		LCD.clear();
			    		LCD.refresh();
			    	}
			    } if (selection == -1) {
			    	menu = topMenu;
			    }
		    } else if (menu == blueOffMenu) {
				if (selection == 0)
				{
					LCD.clear();
					LCD.drawString("   Power on...", 0, 0);
					LCD.refresh();
				    btPowerOn = setBluetoothPower(true, true);
					menu = blueMenu;
				}
				else
					menu = topMenu;
			} else if (menu == blueMenu) {
		    	if (selection == 2) { //Devices
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
		    		//byte[] cod = {0,0,8,4}; // Toy, Robot
					byte[] cod = {0,0,0,0}; // All
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
		        } else if (selection == 0) // On/Off
		        {
					LCD.clear();
					LCD.drawString("   Power off...", 0, 0);
					LCD.refresh();
					btPowerOn = setBluetoothPower(false, true);
					menu = blueOffMenu;
		        }else if (selection == 3) // Visibility
		        {
		        	Bluetooth.setVisibility((byte) (visibility == 1 ? 0 : 1));
		        } else if (selection == -1) {
		    		menu = topMenu;
		    	}
		    	
			} else if (menu == systemMenu) {
               if (selection == 0) 
               {
                  yes_noMenu.setTitle("Delete all files?");
                   if(yes_noMenu.select()== 1)File.format();
               }
//             } else if (selection == -1) {
                   menu = topMenu;
//             }
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
		String [] ioProgress = {".   ", " .  ", "  . "};
		int ioIndex = 0;
		boolean rewrite = false;
		while(true) 
		{
			try 
			{
			  if (io)
			  {
			     StartUpText.g.drawString("     ", 76, 0);
			     ioIndex = (ioIndex + 1) % ioProgress.length;
				  StartUpText.g.drawString(ioProgress[ioIndex], 78, 0);
				  io = false;
				  rewrite = true;
			  }
			  else if(rewrite)
			  {
			     LCD.drawString("   ",13,0);
			      StartUpText.g.drawString(" BT",82, 0,!StartUpText.btPowerOn);  // invert when power is off
			      StartUpText.g.refresh();   
			      rewrite = false;
			  }
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
				ind.ioActive();
				int replyLen = LCP.emulateCommand(inMsg,len, reply);
				if ((inMsg[0] & 0x80) == 0) USB.usbWrite(reply, replyLen);
				if (inMsg[1] == LCP.CLOSE|| inMsg[1] == LCP.DELETE) {
					if (inMsg[1] == LCP.DELETE) {
						try {
							File.defrag();
						} catch (IOException ioe) {
							File.reset();
						}
					}
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
		
		while (true)
		{
			// Wait for power on
			while (!Bluetooth.getPower())
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}				
			}
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

			while(!cmdMode)
			{
				len = btc.read(inMsg,64);

				if (len > 0)
				{
					ind.ioActive();
					int replyLen = LCP.emulateCommand(inMsg,len, reply);
					if ((inMsg[0] & 0x80) == 0) btc.write(reply, replyLen);
					if (inMsg[1] == LCP.CLOSE|| inMsg[1] == LCP.DELETE) {
						if (inMsg[1] == LCP.DELETE) {
							try {
								File.defrag();
							} catch (IOException ioe) {
								File.reset();
							}
						}
						Sound.beepSequenceUp();
						menu.quit();
					}
					if (inMsg[1] == LCP.NXJ_DISCONNECT) { 
						btc.close(); 
						cmdMode = true;
					}
				}
				else if (len < 0)
				{
					btc.close();
					cmdMode = true;
				}
				Thread.yield();
			}
		}
	}
}



