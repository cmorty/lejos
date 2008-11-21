package lejos.nxt.comm;
import lejos.nxt.*;

/**
 * Low-level RS485
 * 
 * @author Andy Shaw
 *
 */
public class RS485 {
    static final int BUFSZ = 256;

    private RS485()
	{		
	}
        
    public static native void hsEnable();
    public static native void hsDisable();
    public static native int hsRead(byte [] buf, int offset, int len);
    public static native int hsWrite(byte[] buf, int offset, int len);
    
}
