
import java.io.*;

import lejos.nxt.*;
import lejos.nxt.comm.USB;

public class Menu {
	static boolean update = true;
    
	public static void main(String[] args) throws Exception {

		Indicators ind = new Indicators();
		USBRespond usb = new USBRespond();
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
		
		while (!quit) 
		{
			File[] files = File.listFiles();
			int len = 0;
			for(int i=0;i<files.length && files[i] != null;i++) len++;		
			for(int i=0;i<len;i++) fileNames[i] = files[i].getName();
			for(int i = len; fileNames[i] != null && i<files.length;i++)fileNames[i] = null;
			menu.setItems(fileNames);
			usb.setMenu(menu);
			LCD.clear();
			LCD.drawString(title,2,0);

		    int selection = menu.select();
		    
		    if (selection >= 0) {
		    	LCD.clear();
		    	LCD.drawString(title,2,0);
				fileMenu.setTitle(fileNames[selection]);
		    	int subSelection = fileMenu.select();
		    	if (subSelection == 0) 
		    	{
		    		LCD.clear();
		    		LCD.refresh();
		    		files[selection].exec();
		    	} else if (subSelection == 1)
		    	{
		    		files[selection].delete();	 
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
			  LCD.drawInt(millis/1000,13,0);
			  LCD.drawString(dot, 14, 0);
			  LCD.drawInt((millis%1000)/100,15,0);
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
		byte[] buf = new byte[64];
		int bytes = 0;
		int size = 0;
		boolean sending = false;
		File f = null;
		FileOutputStream out = null;
		byte[] reply = new byte[32];
		int replyLen;
		boolean  disconnected = false;

		while(true)
		{				
			disconnected = false;
			sending = false;

			USB.usbReset();
			
			while(!disconnected)
			{
				int dataLen = USB.usbRead(buf,64);
				try {
					if (dataLen != 0) 
					{
						for(int i=0;i<32;i++) reply[i] = 0;
						reply[0] = 0x02;
						reply[1] = buf[1];
						replyLen = 3;
						if (sending) {
							bytes += dataLen;
							out.write(buf,0,dataLen);
							if (bytes == size) {
								sending = false;
								reply[2] = (byte) 0x83;
								buf[0] = 0x01;
								replyLen = 6;
							}
						} else if (buf[1] == (byte) 0x81) { // OPEN WRITE
							size = buf[22] & 0xFF;
							size += ((buf[23] & 0xFF) << 8);
							size += ((buf[24] & 0xFF) << 16);
							size += ((buf[25] & 0xFF) << 24);
							int filenameLength = 0;
							for(int i=2;i<22 && buf[i] != 0;i++) filenameLength++;
							char [] chars = new char[filenameLength];
							for(int i=0;i<filenameLength;i++) chars[i] = (char) buf[i+2];
							String fileName = new String(chars,0,filenameLength);
							f = new File(fileName);
							if (f.exists()) f.delete();
							f.createNewFile();
	    					bytes = 0;
							replyLen = 4;
						} else if (buf[1] == (byte) 0x83) { // WRITE
							out = new FileOutputStream(f);
							replyLen = 6;
							sending = true;			
						} else if (buf[1] == (byte) 0x84) { // CLOSE
						    out.flush();
						    out.close();
						    Sound.beepSequenceUp();
						    menu.quit(); // Force redisplay of menu
							replyLen = 4;
						} else if (buf[1] == (byte) 0x00) { // STARTPROGRAM
							f.exec();
					    } else if (buf[1] == (byte) 0x09) { // MESSAGE
					    	disconnected = true;
					    }
						if (!sending && ((buf[0] & 0x80) == 0)) {
							USB.usbWrite(reply,replyLen);
						}
					}
				} catch (IOException ie) {}
				Thread.yield();
			}
		}
	}
}

