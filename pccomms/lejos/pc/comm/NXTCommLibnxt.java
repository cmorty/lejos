package lejos.pc.comm;

import java.io.*;
import java.util.Vector;

/**
 * Implementation of NXTComm over USB using libnxt.
 * 
 * Should not be used directly - use NXTCommFactory to create
 * an appropriate NXTComm object for your system and the protocol
 * you are using.
 *
 */
public class NXTCommLibnxt implements NXTComm {
	private NXTInfo nxtInfo;
	
	public native long jlibnxt_find(int idx);
	public native int jlibnxt_open(long nxt);
	public native void jlibnxt_close(long nxt);
	public native void jlibnxt_send_data(long nxt, byte [] message) throws IOException;
	public native byte[] jlibnxt_read_data(long nxt, int len) throws IOException;
    public native String jlibnxt_serial(long nxt);
    public native String jlibnxt_name(long nxt);
	
	public NXTInfo[] search(String name, int protocol) {
		if ((protocol & NXTCommFactory.USB) == 0) {
			return new NXTInfo[0];
		}
		Vector nxtInfos = new Vector();
        long nxt;
        for(int idx = 0; (nxt = jlibnxt_find(idx)) != 0; idx++)
        {
            String nam = jlibnxt_name(nxt);
            if (nam == null) nam = "Unknown";
            if (name != null && !name.equals(nam))
                continue;
            String addr = jlibnxt_serial(nxt);
            if (addr == null) addr = "";
            NXTInfo info = new NXTInfo();
            info.name = nam;
            info.btDeviceAddress = addr;
            info.protocol = NXTCommFactory.USB;
            info.nxtPtr = nxt;
            nxtInfos.add(info);
            System.out.println("Found nxt name " + nam + " address " + addr);
        }
		NXTInfo[] nxts = new NXTInfo[nxtInfos.size()];
		for (int i = 0; i < nxts.length; i++)
			nxts[i] = (NXTInfo) nxtInfos.elementAt(i);
		return nxts;
	}

	public boolean open(NXTInfo nxtInfo) {
        if (nxtInfo.nxtPtr == 0)
        {
            // No particular device has been found yet, so search for one.
            NXTInfo []devs = search(nxtInfo.name, NXTCommFactory.USB);
            if (devs.length == 0) return false;
            if (nxtInfo.btDeviceAddress.length() > 0)
            {
                for(int i = 0; i < devs.length; i++)
                    if (nxtInfo.btDeviceAddress.equals(devs[i].btDeviceAddress))
                    {
                        this.nxtInfo = devs[i];
                        break;
                    }
            }
            else
                this.nxtInfo = devs[0];
        }
        else
            this.nxtInfo = nxtInfo;
		int open = jlibnxt_open(nxtInfo.nxtPtr);
		return (open == 0);
	}
	
	public void close() throws IOException {
		if (nxtInfo != null && nxtInfo.nxtPtr != 0) 
        {
            jlibnxt_close(nxtInfo.nxtPtr);
            nxtInfo.nxtPtr = 0;
        }
	}
	
	public byte[] sendRequest(byte [] data, int replyLen) throws IOException {
		jlibnxt_send_data(nxtInfo.nxtPtr, data);
        if (replyLen == 0) return new byte [0];
		return jlibnxt_read_data(nxtInfo.nxtPtr, replyLen);
	}
	
	public byte [] read() throws IOException
	{
		byte [] ret = jlibnxt_read_data(nxtInfo.nxtPtr, 64);
        if (ret != null && ret.length == 0) return null;
        return ret;
	}
	
	public int available() throws IOException {
		return 0;
	}
	
	public void write(byte [] data) throws IOException {
		jlibnxt_send_data(nxtInfo.nxtPtr, data);
	}
	
	public OutputStream getOutputStream() {
		return new NXTCommOutputStream(this);		
	}
	
	public InputStream getInputStream() {
		return new NXTCommInputStream(this);		
	}
	
	static {
		System.loadLibrary("jlibnxt");
	}

}
