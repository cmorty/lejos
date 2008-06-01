package lejos.pc.comm;

import java.io.*;
import java.util.Iterator;
import java.util.Vector;

/**
 * Base Implementation of NXTComm for USB
 * 
 * This module implements two types of I/O over USB. 
 * 1. The standard Lego LCP format used for LCP command processing.
 * 2. A Simple packet based protocol that can be used to transport a simple
 *    byte stream.
 * Protocol 2 is required (rather then using raw USB operations), to allow the
 * signaling of things like open, and close over the connection. 
 * 
 * Notes
 * This module assumes that the device read and write functions have a built in
 * timeout period of approx 20 seconds. This module assumes that this timeout
 * exists and uses it to timeout some requests.
 * 
 * Should not be used directly - use NXTCommFactory to create
 * an appropriate NXTComm object for your system and the protocol
 * you are using.
 *
 */
public abstract class NXTCommUSB implements NXTComm {
	private NXTInfo nxtInfo;
    private boolean packetMode = false;
    private boolean EOF = false;
    static final int USB_BUFSZ = 64;
    private byte[] inBuf = new byte[USB_BUFSZ];
    private byte[] outBuf = new byte[USB_BUFSZ];
	
    /**
     * Return a vector of available nxt devices. Each NXTInfo item should
     * have the address field populated and the other fields must contain
     * sufficient information such that a call to devIsValid will return
     * true and that devOpen will connect to the device. The name field may
     * be left empty, in which case it will be populated by code in this class.
     * @return vector of available nxt devices.
     */
	abstract Vector<NXTInfo> devFind();
    
    /**
     * Connect to the specified nxt device.
     * @param nxt The device to connect to
     * @return A handle to the device
     */
	abstract long devOpen(NXTInfo nxt);
    
    /**
     * Close the device. The device will no longer be available for use.
     * @param nxt The device to be closed.
     */
	abstract void devClose(long nxt);
    
    /**
     * Write bytes to the device. The call must timeout after approx 20 seconds
     * if it is not possible to write to the device.
     * @param nxt Device to write to.
     * @param message Bytes to be written.
     * @param offset Offset to start writing from.
     * @param len Number of bytes to write.
     * @return Number of bytes written, 0 if timed out < 0 if an error.
     */
	abstract int devWrite(long nxt, byte [] message, int offset, int len);
    
    /**
     * Read bytes from the device. The call must timeout after approx 20 seconds
     * if it is not possible to read from the device.
     * @param nxt Device to read from.
     * @param data Location to place the read bytes.
     * @param offset Offset of where to place the bytes.
     * @param len Number of bytes to read.
     * @return The number of bytes read, 0 if timeout < 0 if an error.
     */
	abstract int devRead(long nxt, byte[] data, int offset, int len);
    
    /**
     * Test to see if the contents of the NXTInfo structure are sufficient
     * to allow connection to the device.
     * @param nxt The device to check.
     * @return True if ok, False otherwise.
     */
    abstract boolean devIsValid(NXTInfo nxt);
	
    /**
     * Helper function. Open the specified nxt, get its name and close it.
     * @param nxt the device to obtain the name for
     * @return the nxt name.
     */
    private String getName(NXTInfo dev)
    {
        String name = null;
        long nxt = devOpen(dev);
        if (nxt == 0) return name;
		byte[] request = { NXTProtocol.SYSTEM_COMMAND_REPLY, NXTProtocol.GET_DEVICE_INFO };
        if (devWrite(nxt, request, 0, request.length) > 0)
        {
            int ret = devRead(nxt, inBuf, 0, 33);
            if (ret >= 33)
            {
                char nameChars[] = new char[16];
                int len = 0;

                for (int i = 0; i < 15 && inBuf[i + 3] != 0; i++) {
                    nameChars[i] = (char) inBuf[i + 3];
                    len++;
                }
                name = new String(nameChars, 0, len);
            }
        }
        devClose(nxt);
        return name;
    }
    
    /**
     * Helper function, reads a single packet from the USB device. Handles
     * packet headers and timeouts. Blocks until data is available.
     * @return date or null if at EOF
     */
    private byte[] readPacket() throws IOException
    {
        int len;
        while((len=devRead(nxtInfo.nxtPtr, inBuf, 0, inBuf.length)) == 0)
            {}
        if (len < 0) throw new IOException("Error in read");
        int offset = 0;
        if (packetMode)
        {
            if (((int)inBuf[0] & 0xff) != len - 1) throw new IOException("Bad packet format");
            if (inBuf[0] == 0) return null;
            offset = 1;
        }
        byte [] ret = new byte[len - offset];
        System.arraycopy(inBuf, offset, ret, 0, len - offset);
        return ret;
    }
    
    /**
     * Helper function. Write a single packet. Handles packet format and 
     * request timeouts.
     * @param data
     * @param offset
     * @param len
     * @return number of bytes actually written
     * @throws java.io.IOException
     */
    private int writePacket(byte[] data, int offset, int len) throws IOException
    {
        byte [] out = null;
        if (packetMode)
        {
            if (len > USB_BUFSZ-1) len = USB_BUFSZ - 1;
            outBuf[0] = (byte) len;
            System.arraycopy(data, offset, outBuf, 1, len);
            out = outBuf;
            offset = 0;
            len += 1;
        }
        else
            out = data;
        int ret;
        while ((ret = devWrite(nxtInfo.nxtPtr, out, offset, len)) == 0)
            {}
        if (ret < 0) throw new IOException("Error in write");
        return ret;
    }
    
    private boolean writeEOF()
    {
        outBuf[0] = 0;
        return (devWrite(nxtInfo.nxtPtr, outBuf, 0, 1) == 1);
    }
    
    private void waitEOF()
    {
        while(devRead(nxtInfo.nxtPtr, inBuf, 0, inBuf.length) > 1)
            {}
    }

    /**
     * Locate availabe nxt devices and return them. Optionally filter the list
     * to those that match name.
     * @param name The name to search for. If null return all devices.
     * @param protocol The protocol to search for, must be USB
     * @return The list of devices.
     */
	public NXTInfo[] search(String name, int protocol) {
		Vector<NXTInfo> nxtInfos = devFind();
        if (nxtInfos.size() == 0) return new NXTInfo[0];
        Iterator<NXTInfo> devs = nxtInfos.iterator();
        // Filter the list against name
        while (devs.hasNext())
        {
            NXTInfo nxt = devs.next();
            if (nxt.name == null)
            {
                nxt.name = getName(nxt);
                if (nxt.name == null) nxt.name = "Unknown";
            }
            if (name != null && !name.equals(nxt.name))
                devs.remove();
            else
                System.out.println("Found nxt name " + nxt.name + " address " + nxt.btDeviceAddress);
        }
		NXTInfo[] nxts = new NXTInfo[nxtInfos.size()];
		for (int i = 0; i < nxts.length; i++)
			nxts[i] = (NXTInfo) nxtInfos.elementAt(i);
		return nxts;
	}

    /**
     * Open a connection to the specified device, and make it available for use.
     * @param nxtInfo The device to connect to.
     * @return True if the device is now open, Flase otherwise.
     */
	public boolean open(NXTInfo nxtInfo) {
        // Is the info vaild enough to connect directly?
        if (!devIsValid(nxtInfo))
        {
            // not valid so search for it.
            String addr = nxtInfo.btDeviceAddress;
            if (addr == null || addr.isEmpty())
                return false;
            NXTInfo[] nxts = search(null, NXTCommFactory.USB);
            nxtInfo = null;
            for(int i = 0; i < nxts.length; i++)
                if (addr.equals(nxts[i].btDeviceAddress))
                {
                    nxtInfo = nxts[i];
                    break;
                }
        }
        if (nxtInfo == null) return false;
		this.nxtInfo = nxtInfo;
		this.nxtInfo.nxtPtr = devOpen(nxtInfo);
        if (this.nxtInfo.nxtPtr == 0) return false;
        // Now try and switch to packet mode for normal read/writes
		byte[] request = { NXTProtocol.SYSTEM_COMMAND_REPLY, NXTProtocol.NXJ_PACKET_MODE };
        byte [] ret = null;
        try {
            ret = sendRequest(request, USB_BUFSZ);
        } catch(IOException e)
        {
            ret = null;
        }
        // Check the response. We are looking for a non standard response of
        // 0x02, 0xfe, 0xef
        if (ret != null && ret.length >= 3 && ret[0] == 0x02 && ret[1] == (byte)0xfe && ret[2] == (byte)0xef)
            packetMode = true;
        EOF = false;
		return true;
	}
	
    /**
     * Close the current device.
     */
	public void close() {
        if (packetMode)
        {
            writeEOF();
            if (!EOF) waitEOF();
        }
		devClose(nxtInfo.nxtPtr);
	}

    /**
     * Send a Lego Command Protocol (LCP) request to the device.
     * @param data The command to send.
     * @param replyLen How many bytes in the optional reply.
     * @return The optional reply, or null
     * @throws java.io.IOException Thrown on errors.
     */
    public byte[] sendRequest(byte [] data, int replyLen) throws IOException {
        int written = devWrite(nxtInfo.nxtPtr, data, 0, data.length);
        if (written <= 0) throw new IOException("Failed to send data");
        if (replyLen == 0) return new byte [0];
        byte[] ret = new byte[replyLen];
        int len = devRead(nxtInfo.nxtPtr, ret, 0, replyLen);
        if (len <= 0) throw new IOException("Faild to read reply");
        return ret; 
    }
	
    /**
     * Read bytes from the device
     * @return An array of bytes read from the device. null if at EOF
     * @throws java.io.IOException
     */
	public byte [] read() throws IOException {
        if (EOF) return null;
        byte [] ret = readPacket();
        if (packetMode && ret == null) EOF = true;
        return ret;
	}
	
    /**
     * The number of bytes that can be read without blocking.
     * @return Bytes available to be read.
     * @throws java.io.IOException
     */
	public int available() throws IOException {
		return 0;
	}
	
    /**
     * Write bytes to the device.
     * @param data Data to be written.
     * @throws java.io.IOException
     */
	public void write(byte [] data) throws IOException {
        int total = data.length;
        int written = 0;
        while( written < total)
        {
            written += writePacket(data, written, total-written);
        }
	}
	
	public OutputStream getOutputStream() {
		return new NXTCommOutputStream(this);		
	}
	
	public InputStream getInputStream() {
		return new NXTCommInputStream(this);		
	}

}

