package lejos.pc.comm;

import java.util.Vector;

/**
 * Implementation of NXTComm over USB using libnxt.
 * 
 * Should not be used directly - use NXTCommFactory to create
 * an appropriate NXTComm object for your system and the protocol
 * you are using.
 *
 */
public class NXTCommLibnxt extends NXTCommUSB {
	private NXTInfo nxtInfo;
	
	public native String[] jlibnxt_find();
	public native long jlibnxt_open(String nxt);
	public native void jlibnxt_close(long nxt);
	public native int jlibnxt_send_data(long nxt, byte [] message, int offset, int len);
	public native int jlibnxt_read_data(long nxt, byte [] data, int offset, int len);

    Vector<NXTInfo> devFind()
    {
        // Address is in standard format so we can use the helper function
        // to do all the hard work.
		return find(jlibnxt_find());
    }
    
	long devOpen(NXTInfo nxt)
    {
        if (nxt.btResourceString == null) return 0;
        return jlibnxt_open(nxt.btResourceString);
    }
    
	void devClose(long nxt)
    {
        jlibnxt_close(nxt);
    }
    
	int devWrite(long nxt, byte [] message, int offset, int len)
    {
        return jlibnxt_send_data(nxt, message, offset, len);
    }
    
	int devRead(long nxt, byte[] data, int offset, int len)
    {
        return jlibnxt_read_data(nxt, data, offset, len);
    }
    
    
    boolean devIsValid(NXTInfo nxt)
    {
        return (nxt.btResourceString != null);
    }

	
	static {
		System.loadLibrary("jlibnxt");
	}

}
