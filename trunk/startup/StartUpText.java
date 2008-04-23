
import java.io.*;
import java.util.Vector;
import javax.microedition.lcdui.*;
import lejos.nxt.comm.*;
import lejos.nxt.*;
import javax.bluetooth.*;


public class StartUpText
{
   static Graphics g = new Graphics();
   static boolean btPowerOn = false;
   static final String blank = "                ";
   static final String defaultProgramProperty = "lejos.default_program"; 
   static final String defaultProgramAutoRunProperty = "lejos.default_autoRun";
   static final String sleepTimeProperty = "lejos.sleep_time";
   static final int defaultSleepTime = 2;
   static final int maxSleepTime = 10;
 
   public static void playTune()
   {
      int [] freq = {523,784, 659};
      for(int i = 0; i<3; i++)
      {
         Sound.playNote(Sound.XYLOPHONE, freq[i], (i==3 ? 500 : 300));   
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
	
//	private static void drawGauge(int x, int y, int w, int h, int max, int cur)
//	{
//		int segWidth = (w/max);
//		for(int i = 0; i < cur; i++)                  
//			g.fillRect(x + i*segWidth, y+1, segWidth-1, h-1);
//		for(int i = cur; i < max; i++)
//			g.drawRect(x + i*segWidth, y+1, segWidth-1, h-1);
//	}
	
	private static String formatVol(int vol)
	{
		if (vol == 0) return "mute";
		if (vol == 10) return "10";
		return " " + vol;
	}
	
	private static String getExtension(String fileName) {
	 	int dot = fileName.lastIndexOf(".");
		if (dot < 0) return "";
		else return fileName.substring(dot+1, fileName.length());                                                                                                                                                                                                                                                                                                                         
	}
	
	private static String getBaseName(String fileName) {
		int dot = fileName.lastIndexOf(".");
		if (dot < 0) return fileName;
		else return fileName.substring(0, dot);
	}
	
	private static void runDefaultProgram()
	{
       String defaultProgram = Settings.getProperty(defaultProgramProperty, "");
       if (defaultProgram != null && defaultProgram.length() > 0) 
       {
           String progName = defaultProgram + ".nxj";
           File f = new File(progName);
           if (f.exists()) f.exec();
           else Settings.setProperty(defaultProgramProperty, "") ;
       }
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
        String sound = "Sound";
		String freeFlash = "Free flash";
		String freeMem = "Free ram";
		String battery = "Battery ";
		TextMenu filesMenu = new TextMenu(null,1);
		String[] topMenuData = {"Run Default", "Files", "Bluetooth", "Sound", "System", "Version"};
		TextMenu topMenu = new TextMenu(topMenuData,1);
		String[] fileMenuData = {"Delete file"}; 
		TextMenu fileMenu = new TextMenu(fileMenuData,2);
		String[] programMenuData = {"Execute program", "Set as Default", "Delete file"}; 
		TextMenu programMenu = new TextMenu(programMenuData,2);
		String[] wavMenuData = {"Play sample", "Delete file"}; 
		TextMenu wavMenu = new TextMenu(wavMenuData,2);
		String[] fileNames = new String[File.MAX_FILES];
		TextMenu menu = topMenu;
		String[] blueMenuData = {"Power off", "Search", "Devices","Visibility"};
		String[] blueOffMenuData = {"Power on"};
		TextMenu blueMenu = new TextMenu(blueMenuData,3);
		TextMenu blueOffMenu = new TextMenu(blueOffMenuData,3);
		String[] soundMenuData = {"Volume:    ", "Key click: "};
		String[] soundMenuData2 = new String[2];
		TextMenu soundMenu = new TextMenu(soundMenuData, 2);
		int [][] Volumes = {{Sound.getVolume()/10, 784, 250, 0}, {Button.getKeyClickVolume()/10, Button.getKeyClickTone(1), Button.getKeyClickLength(), 0}};
        int enterTone = Button.getKeyClickTone(1);
		int curItem = 0;
		String sleepTime = "Sleep Time: ";
		String[] systemMenuData = {"Format",sleepTime, "Auto Run"};
		String dot = ".";
        String[] yes_no = {"No","Yes"};
        TextMenu yes_noMenu = new TextMenu(yes_no,6);
        TextMenu systemMenu = new TextMenu(systemMenuData,5);
        int timeoutMinutes = SystemSettings.getIntSetting(sleepTimeProperty, defaultSleepTime);
        int timeout = timeoutMinutes * 60000;
        String firmwareVersionString = "Firmware Version";
        String firmwareVersion = System.getFirmwareMajorVersion() + "." +
		                         System.getFirmwareMinorVersion() + "." +
		                         System.getFirmwareRevision();
        
        playTune();
        
 // Run default program if required
        if( System.getProgramExecutionsCount() == 1 &&
            (Button.readButtons() & 2) != 2 &&  //left button down? 
            Settings.getProperty(defaultProgramAutoRunProperty, "").equals("ON")
           ) 
           runDefaultProgram();
                            
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
        
        // Make a note of starting volumes so we know if it changes        
        for(int i = 0; i < Volumes.length; i++)
            Volumes[i][3] = Volumes[i][0];

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
                systemMenuData[1] = sleepTime + timeoutMinutes;
			} else if (menu == soundMenu)
			{
   				LCD.drawString(sound, 5, 1);
				for(int i = 0; i < Volumes.length; i++)
					soundMenuData2[i] = soundMenuData[i] + formatVol(Volumes[i][0]);
				soundMenu.setItems(soundMenuData2);
			}
			int selection = menu.select(curItem, timeout);
			if (selection == -3) System.shutDown();
		    if (menu == topMenu) {
		    	 if (selection == 0) 
		    	 {
		    	    runDefaultProgram();	    	 
		    	 } else if (selection == 1) {
		    		 menu = filesMenu;
		    	 } else if (selection ==2) {
		    		 menu = (btPowerOn ? blueMenu : blueOffMenu);
		    	 } else if (selection == 3) {
					 menu = soundMenu;
					 // Turn of key click when in the sound menu so it does
					 // not screw with the feedback sounds
					 Button.setKeyClickTone(1, 0);
				 } else if (selection == 4) {
		    		 menu = systemMenu;
		    	 } else if (selection == 5) {
		    		 LCD.clear();
		    		 drawTopRow();
		    		 LCD.drawString(firmwareVersionString, 0, 2);
		    		 LCD.drawString(firmwareVersion,2,3);
		    		 Button.waitForPress();		 
		    	 } else if (selection == -1) {
		    		 quit = true;
		    	 }
		    } else if (menu == filesMenu) {
			    if (selection >= 0 && files[selection] != null) {
			    	String fileName = files[selection].getName();
			    	String ext = getExtension(fileName);
			        LCD.clear();
			        drawTopRow();
			        TextMenu subMenu = fileMenu;
			        if (ext.equals("nxj")) subMenu = programMenu;
			        if (ext.equals("wav")) subMenu = wavMenu;
					subMenu.setTitle(fileNames[selection]);
			    	int subSelection = subMenu.select(0,timeout);
			    	if (subSelection == -3) System.shutDown();
                    if ((subMenu == fileMenu && subSelection == 0) ||
                         (subMenu == wavMenu && subSelection == 1) 
                         || subSelection == 2)
			    	{
			    		files[selection].delete();
						try {
							File.defrag();
						} catch (IOException ioe) {
							File.reset();
						}
			    		LCD.clear();
			    	} else if (subMenu == programMenu && subSelection == 0) 
			    	{
			    		files[selection].exec();
			    	} else if (subMenu == programMenu && subSelection == 1)
			    	{
			    		Settings.setProperty(defaultProgramProperty, getBaseName(fileName));
			        } else if (subMenu == wavMenu && subSelection == 0) {
			        	Sound.playSample(files[selection]);
			        }
			    } else if (selection == -1) {
			    	menu = topMenu;
			    }
		    } else if (menu == blueOffMenu) {
				if (selection == 0)
				{
					LCD.clear();
					LCD.drawString("   Power on...", 0, 0);
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
		    				RemoteDevice btrd = ((RemoteDevice) devList.elementAt(i));
		    				names[i] = btrd.getFriendlyName(false);
		    			}
		    				
		    			TextMenu deviceMenu = new TextMenu(names,1);
		    			String[] subItems = {"Remove"};
		    			TextMenu subMenu = new TextMenu(subItems,5);

		    			int selected;
		    			do {
		    				LCD.clear();
				    		LCD.drawString(devices,5,0);
		    				selected = deviceMenu.select(0,timeout);
		    				if (selected == -3) System.shutDown();
		    				if (selected >=0) {
		    					RemoteDevice btrd = ((RemoteDevice) devList.elementAt(selected));
		    					LCD.clear();
		    					LCD.drawString(devices,5,0);
		    					LCD.drawString(names[selected],0,1);
		    					LCD.drawString(btrd.getBluetoothAddress(), 0, 2);
		    					for(int i=0;i<4;i++) LCD.drawInt(btrd.getDeviceClass()[i], 3, i*4, 3);
		    					int subSelection = subMenu.select(0, timeout);
		    					if (subSelection == -3) System.shutDown();
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
		    		Vector devList = Bluetooth.inquire(5, 10,cod);
					
		    		if (devList.size() > 0) {
		    			String[] names = new String[devList.size()];
		    			for (int i = 0; i < devList.size(); i++) {
		    				RemoteDevice btrd = ((RemoteDevice) devList.elementAt(i));
		    				names[i] = btrd.getFriendlyName(false);
		    			}
		    				
		    			TextMenu searchMenu = new TextMenu(names,1);
		    			String[] subItems = {"Add"};
		    			TextMenu subMenu = new TextMenu(subItems,4);
		    			
		    			int selected;
		    			do {
				    		LCD.clear();
							LCD.drawString(found,6,0);
		    				selected = searchMenu.select(0,timeout);
		    				if (selected == -3) System.shutDown();
		    				if (selected >=0) {
		    					RemoteDevice btrd = ((RemoteDevice) devList.elementAt(selected));
		    					LCD.clear();
		    					LCD.drawString(found,6,0);
		    					LCD.drawString(names[selected],0,1);
		    					LCD.drawString(btrd.getBluetoothAddress(), 0, 2);
		    					int subSelection = subMenu.select(0, timeout);
		    					if (subSelection == -3) System.shutDown();
		    					if (subSelection == 0) Bluetooth.addDevice(btrd);
		    				}
		    			} while (selected >= 0);

		    		} else {
		    			LCD.clear();
		    			LCD.drawString("no devices", 0, 0);
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
                  int subSelection = yes_noMenu.select(0,timeout);
                  if (subSelection == -3) System.shutDown();
                  if(subSelection== 1)File.format();
               } else if (selection == 1) // Sleep time
               { 
            	   timeoutMinutes++;
            	   if (timeoutMinutes > maxSleepTime) timeoutMinutes = 0;
            	   timeout = timeoutMinutes * 60000;
            	   Settings.setProperty(sleepTimeProperty, "" + timeoutMinutes);
            	   curItem = selection;
               } else if (selection == 2)
               { 
                  String defaultPrgm = Settings.getProperty(defaultProgramProperty, "");
                  if(defaultPrgm != null && defaultPrgm.length() >0)
                  {
                     LCD.drawString("Auto Run: "+Settings.getProperty(defaultProgramAutoRunProperty, "")+blank,0,2);
                     LCD.drawString("Default Program:    ",0,3);
                     LCD.drawString(" " + defaultPrgm + blank,0 , 4);
                     yes_noMenu.setTitle("Run at power up?");
                     int subSelection =  yes_noMenu.select(0, timeout) ;
                     if (subSelection == -3) System.shutDown();
                     if(subSelection == 0)
                        Settings.setProperty(defaultProgramAutoRunProperty, "OFF");
                     else if(subSelection == 1)
                        Settings.setProperty(defaultProgramAutoRunProperty, "ON");                   
                  }
               } 
               if (selection != 1) { // Return to top menu for all but sleep time
            	   curItem = 0;
                   menu = topMenu;
               }
            } else if (menu == soundMenu) {
				if (selection >= 0)
				{
					Volumes[selection][0]++;
					Volumes[selection][0] %= 11;
					for(int i = 0; i < Volumes.length; i++)
						soundMenuData2[i] = soundMenuData[i] + formatVol(Volumes[i][0]);
					soundMenu.setItems(soundMenuData2);
					if (selection == 0)
					{
						Sound.setVolume(Volumes[0][0]*10);
						Sound.playNote(Sound.XYLOPHONE, Volumes[selection][1], Volumes[selection][2]);
					}
					else
						Sound.playTone(Volumes[selection][1], Volumes[selection][2], -Volumes[selection][0]*10);
					curItem = selection;
				}
				else
				{
					// Make sure key click is back on and has new volume
					Button.setKeyClickVolume(Volumes[1][0]*10);
                    Button.setKeyClickTone(1, enterTone);
                    // Wait for any sound to complete, writing to flash distorts
                    // any playing sound...
                    Sound.pause(Sound.getTime()+250);
                    // Save in settings
                    if (Volumes[0][0] != Volumes[0][3])
                        Settings.setProperty(Sound.VOL_SETTING, Integer.toString(Volumes[0][0]*10));
                    if (Volumes[1][0] != Volumes[1][3])
                        Settings.setProperty(Button.VOL_SETTING, Integer.toString(Volumes[1][0]*10));
                    // Make a note of new volumes so we know if it changes        
                    for(int i = 0; i < Volumes.length; i++)
                        Volumes[i][3] = Volumes[i][0];
					menu = topMenu;
					curItem = 0;
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
    
    private void setAddress()
    {
        // Ensure the USB address property is set correctly. We use the
        // Bluetooth address as our serial number.
        String SerialNo = Bluetooth.addressToString(Bluetooth.getLocalAddress());
        if (!SerialNo.equals(USB.getSerialNo()))
        {
            Settings.setProperty(USB.SERIAL_NO, SerialNo);
            USB.setSerialNo(SerialNo);
        }
        byte[] fName = Bluetooth.getFriendlyName();
        char []cName = new char[fName.length];
        int cNameLen = 0;
        while (cNameLen < fName.length && fName[cNameLen] != 0)
        {
            cName[cNameLen] = (char)fName[cNameLen];
            cNameLen++;
        }
        String name = new String(cName, 0, cNameLen);
        if (!name.equals(USB.getName()))
        {
            Settings.setProperty(USB.NAME, name);
            USB.setName(name);
        }
    }
	
	public void run() {
		byte[] inMsg = new byte[64];
		byte [] reply = new byte[64];
		boolean cmdMode = true;
        USBConnection conn = null;
		int len;

        setAddress();
        
		while (true)
		{
            conn = USB.waitForConnection(0, USB.RAW);
            if (conn == null) {
                continue;
            }
            cmdMode = false;
			while(!cmdMode)
			{
				len = conn.read(inMsg,64);

				if (len > 0)
				{
					ind.ioActive();
					menu.resetTimeout();
					int replyLen = LCP.emulateCommand(inMsg,len, reply);
					if ((inMsg[0] & 0x80) == 0) conn.write(reply, replyLen);
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
						conn.close(); 
						cmdMode = true;
					}
				}
				else if (len < 0)
				{
					conn.close();
					cmdMode = true;
				}
				Thread.yield();
			}
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
		BTConnection conn = null;
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
            conn = Bluetooth.waitForConnection();
            if (conn == null) {
                continue;
            }
			cmdMode = false;

			while(!cmdMode)
			{
				len = conn.read(inMsg,64);

				if (len > 0)
				{
					ind.ioActive();
					menu.resetTimeout();
					int replyLen = LCP.emulateCommand(inMsg,len, reply);
					if ((inMsg[0] & 0x80) == 0) conn.write(reply, replyLen);
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
						conn.close(); 
						cmdMode = true;
					}
				}
				else if (len < 0)
				{
					conn.close();
					cmdMode = true;
				}
				Thread.yield();
			}
		}
	}
}



