
import lejos.nxt.*;
import lejos.nxt.comm.*;

public class USBTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		byte[] buf = new byte[5];
		
		USB.usbWaitForConnection();
		
		while(true)
		{
			int i = USB.usbRead(buf,5);
			if (i != 0) 
			{
				LCD.drawInt(i, 0, 0);	
				LCD.drawInt(buf[0], 0, 1);
				buf[0] = 'O';
				buf[1] = 'K';
				USB.usbWrite(buf,2);
			}
			
			LCD.refresh();
		}
		//Button.ENTER.waitForPressAndRelease();
	}

}
