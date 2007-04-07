
import lejos.nxt.*;

public class FlashTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		byte[] buf = new byte[256];
		
		for(int i=0;i<256;i++) buf[i] = (byte) i;
		
		Flash.writePage(buf,0);
		
		for(int i=0;i<256;i++) buf[i] = 0;
		
		Flash.readPage(buf,0);
		
		LCD.drawInt(buf[100],0,0);
		LCD.refresh();
		
		Button.ESCAPE.waitForPressAndRelease();
	}

}
