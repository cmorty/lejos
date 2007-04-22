

import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

public class USBRespond {
	
	public static void main(String[] args) throws Exception {
		byte[] buf = new byte[64];
		int bytes = 0;
		int size = 0;
		boolean sending = false;
		File f = null;
		FileOutputStream out = null;
		byte[] reply = new byte[32];
		int replyLen;
		boolean  disconnected = false;
		String reading = "Reading";
		String writing = "writing";
		String written = "written";

		//File.format();
		
		// Process USB messages

		while(true)
		{				
			disconnected = false;
			sending = false;
			 
			USB.usbReset();
	
			while(!disconnected)
			{
				//LCD.drawString(reading, 0, 2);
				//LCD.refresh();
				int dataLen = USB.usbRead(buf,64);
				//LCD.drawInt(dataLen, 0, 2);
				//LCD.drawInt(USB.usbGetIsr(),9, 0, 5);
				//LCD.refresh();
				if (dataLen != 0) 
				{
					for(int i=0;i<32;i++) reply[i] = 0;
					reply[0] = 0x02;
					reply[1] = buf[1];
					replyLen = 3;
					if (sending) {
						bytes += dataLen;
						out.write(buf,0,dataLen);
					    //LCD.drawInt(bytes,6,0,6);
						//LCD.refresh();
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
						//LCD.drawInt(size,6,0,3);
						//LCD.drawString(fileName,0,4);
						//LCD.refresh();						
						f = new File(fileName);
						//LCD.drawString("New file",0,5);
						//LCD.refresh();
						boolean created = f.createNewFile();
						//LCD.drawString("File created",0,5);
						//LCD.refresh();						
						bytes = 0;
						replyLen = 4;
					} else if (buf[1] == (byte) 0x83) { // WRITE
						out = new FileOutputStream(f);
						replyLen = 6;
						sending = true;			
					} else if (buf[1] == (byte) 0x84) { // CLOSE
					    out.flush();
					    out.close();
						//LCD.drawString("File Received",0,6);
					    //LCD.drawInt(bytes,6,0,6);
					    //LCD.clear();
						//LCD.refresh();
						replyLen = 4;
						disconnected = true;
					} else if (buf[1] == (byte) 0x00) { // STARTPROGRAM
						f.exec();
					}
					if (!sending && ((buf[0] & 0x80) == 0)) {
						//LCD.drawString(writing,0,7);
						//LCD.refresh();
						USB.usbWrite(reply,replyLen);
						//LCD.drawString(written, 0, 7);
						//LCD.drawInt(replyLen, 2, 8, 7);
						////LCD.drawInt(reply[1] & 0xFF, 3, 10, 7);
						//LCD.refresh();
					}
				}
			}
		}
	}
}
