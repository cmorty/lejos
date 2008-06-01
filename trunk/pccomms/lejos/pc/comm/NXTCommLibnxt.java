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
	
	public native long jlibnxt_find(int idx);
	public native long jlibnxt_open(long nxt);
	public native void jlibnxt_close(long nxt);
	public native int jlibnxt_send_data(long nxt, byte [] message, int offset, int len);
	public native int jlibnxt_read_data(long nxt, byte [] data, int offset, int len);
    public native String jlibnxt_serial(long nxt);
    public native String jlibnxt_name(long nxt);

    Vector<NXTInfo> devFind()
    {
		Vector<NXTInfo> nxtInfos = new Vector<NXTInfo>();
        long nxt;
        for(int idx = 0; (nxt = jlibnxt_find(idx)) != 0; idx++)
        {
            String addr = jlibnxt_serial(nxt);
            if (addr == null) addr = "";
            NXTInfo info = new NXTInfo();
            info.name = null;
            info.btDeviceAddress = addr;
            info.protocol = NXTCommFactory.USB;
            info.nxtPtr = nxt;
            nxtInfos.addElement(info);
        }
        return nxtInfos;
    }
    
	long devOpen(NXTInfo nxt)
    {
        if (nxt.nxtPtr == 0) return 0;
        return jlibnxt_open(nxt.nxtPtr);
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
        return (nxt.nxtPtr != 0);
    }

	
	static {
		System.loadLibrary("jlibnxt");
	}

}
