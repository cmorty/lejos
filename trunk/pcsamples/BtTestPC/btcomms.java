package bttestprog;

import java.io.*;
import java.net.*;
import lejos.pc.comm.*;

public class btcomms  extends Thread {

	private String host;
	private int port;
	private InputStream is;
	private OutputStream os;
    NXTComm nxtComm = null;
	private byte[] remoteData = new byte[256];
	private byte remoteReq = -1;
	private int remoteLen;
	private byte[] empty = new byte[0];
    
	/**
	 * Constructor
	 * An instance of Socket proxy will allow for transparent forwarding
	 * of messages between server and NXT using a socket connection
	 * @param NXTName The name of the NXT to connect to
	 * @param NXTaddress The physical address of the NXT
	 */
	public btcomms()
    {
    }
    
    public boolean connect(String NXTName, String NXTaddress)
    {
        boolean isOpen = false;
        try {
			//  create a Bluetooth connection with the NXT
            nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
            NXTInfo[] nxtInfo=null;
            if (NXTName != null && NXTaddress != null)
            {
                nxtInfo = new NXTInfo[1]; 
                nxtInfo[0] = new NXTInfo(NXTName, NXTaddress);
            }
            else
                nxtInfo = nxtComm.search(NXTName, NXTCommFactory.BLUETOOTH);
			for(int i = 0; i < nxtInfo.length; i++)
				System.out.println("Found " + nxtInfo[i].name);

			System.out.println("Connecting to " + nxtInfo[0].btResourceString);

			// check to see if NXT really exists, if not exit

			try {
				isOpen = nxtComm.open(nxtInfo[0]);
			} catch(NXTCommException n) {
				System.err.println(n.getMessage());
				isOpen = false;
			}
			if (!isOpen) {
				System.out.println("Failed to open " + nxtInfo[0].name);
                nxtComm = null;
			}
		}
		catch (NXTCommException e) {e.printStackTrace();}
		if (isOpen)
		{
			is = nxtComm.getInputStream();
			os = nxtComm.getOutputStream();
			remoteReq = -1;
			this.setDaemon(true);
			start();
		}
        return isOpen;
    }
    
    public void close() throws IOException
    {
		if (nxtComm != null)
		{
			//System.out.println("Closing");
			send((byte)0);
			// Drain any commands and wait for the remote end to end the session
			for(int timeout = 0; timeout < 5000 && recv(false) != 0; timeout++)
			{
				if (recv(false) < 0)
					try{Thread.sleep(1);}catch(Exception e){}
				else
					recv(empty);
			}
			// force the connection closed
			remoteReq = 0;
			is.close();
			os.close();
			nxtComm.close();
			is = null;
			os = null;
			nxtComm = null;
		}
		//System.out.println("Closed");
    }
	
	public int send(byte req, byte[] data, int len)
	{
		// Write a set of data to the host
		synchronized(os)
		{
			if (len > 255) return -1;
			try
			{
				os.write((int)req);
				os.write(len & 0xff);
				for(int i =0; i < len; i++)
					os.write((int)data[i]);
				os.flush();
			}
			catch (Exception e)
			{
				return -1;
			}		
		}
		return len;
	}
	
	public int send(byte req)
	{
		return send(req, empty, 0);
	}
    
	public byte recv(boolean wait)
	{
		// Check for an available request if there is one return the type
		if (!wait) return remoteReq;
		synchronized(this)
		{
			while (remoteReq < 0)
				try {wait();} catch(Exception e){}
			return remoteReq;
		}
	}
	
	public int recv(byte [] data)
	{
		if (remoteReq <= 0) return -1;
		synchronized(this)
		{
			if (remoteLen <= data.length)
				for(int i = 0; i < remoteLen; i++)
					data[i] = remoteData[i];
			else
				remoteLen = -1;
			// Indicate that we have consumed the request
			remoteReq = -1;
			notifyAll();
			return remoteLen;
		}
	}
	
	public void run()
	{
		System.out.println("Running...");
		while(remoteReq != 0)
		{
			try
			{
				// Wait for a request to arrive and read the header
				int req = is.read();
				int len = is.read();
				//System.out.println("Read " + req + " len " + len);
				synchronized(this)
				{
					// Wait for there to be a place to put the message
					//System.out.println("About to wait");
					while (remoteReq >= 0)
						wait();
					//System.out.println("Wait complete");
					if (len < 0 || req < 0)
					{
						remoteReq = 0;
						remoteLen = 0;
					}
					else
					{
						// Get the message
						remoteReq = (byte)req;
						remoteLen = len;
						//System.out.println("Need " + len + " bytes");
						for(int cnt = 0; cnt < len; cnt++)
						{
							req = is.read();
							if (req < 0)
							{
								remoteReq = 0;
								remoteLen = 0;
								break;
							}
							remoteData[cnt] = (byte)req;
						}
						//System.out.println("Notify " + remoteReq + " len " + remoteLen);
					}
					// Tell everyone it is available
					notifyAll();
				}
			}
			catch (Exception e)
			{
				System.out.println("Exception" + e);
				remoteReq = 0;
			}
		}
		System.out.println("Terminated");		
	}
	
	
    public byte [] readPacket() throws IOException
    {
        return nxtComm.read();
    }
    
    public int readPacket(byte[] data, int len) throws IOException
    {
        byte[] buf = nxtComm.read();
        
        if (buf.length > len)
            System.arraycopy(buf, 0, data, 0, len);
        else
            System.arraycopy(buf, 0, data, 0, buf.length);
        return buf.length;
    }
    
    public void writePacket(byte[] data, int len) throws IOException
    {
        // Due to the odd non symetric I/O model in the bluetooth comms
        // we need to construct the "packet" here, rather than at the lower level

        byte [] buf = new byte[len+2];
        System.arraycopy(data, 0, buf, 2, len);
        buf[0] = (byte)(len & 0xff);
        buf[1] = (byte)((len >> 8) & 0xff);
        nxtComm.write(buf);
    }
    
    public void writePacket(byte[] data) throws IOException
    {
        writePacket(data, data.length);
    }


}
