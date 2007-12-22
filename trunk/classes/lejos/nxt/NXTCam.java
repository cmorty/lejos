package lejos.nxt;

import java.awt.*;

/**
 * Mindsensors NXTCam.
 * www.mindsensors.com
 * 
 * Author Lawrie Griffiths
 * 
 */
public class NXTCam extends I2CSensor {
	byte[] buf = new byte[4];
	
	public NXTCam(I2CPort port)
	{
		super(port);
		port.setType(TYPE_LOWSPEED_9V);
	}
	
	/**
	 * Get the number of objects being tracked
	 * 
	 * @return number of objects (0 - 8)
	 */
	public int getNumberOfObjects() {
		int ret = getData(0x42, buf, 1);
		if(ret != 0) return -1;
		return (0xFF & buf[0]);
	}
	
	/**
	 * Get the color number for a tracked object
	 * 
	 * @param id the object number (starting at zero)
	 * @return the color of the object (starting at zero)
	 */
	public int getObjectColor(int id) {
		int ret = getData(0x43 + (id * 5), buf, 1);
		if(ret != 0) return -1;
		return (0xFF & buf[0]);
	}
	
	/**
	 * Get the rectangle containing a tracked object
	 * 
	 * @param id the object number (starting at zero)
	 * @return the rectangle
	 */
	public Rectangle getRectangle(int id) {
		for(int i=0;i<4;i++) buf[i] = 0;
		getData(0x44 + (id * 5), buf, 4);
		return new Rectangle(buf[0] & 0xFF, buf[1] & 0xFF,
				(buf[2] & 0xFF) - (buf[0] & 0xFF),
				(buf[3] & 0xFF) - (buf[1] & 0xFF));
	}
	
	/**
	 * Send a single byte command represented by a letter
	 * @param cmd the letter that identifies the command
	 */
	public void sendCommand(char cmd) {
		sendData(0x41, (byte) cmd);
	}
}

