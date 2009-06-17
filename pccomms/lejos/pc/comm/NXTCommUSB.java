package lejos.pc.comm;

import lejos.nxt.remote.*;
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
    static final String VENDOR_ATMEL = "0x03EB";
    static final String PRODUCT_SAMBA = "0x6124";
    
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
     * Helper function to return the nth string that is part of a standard
     * double colon separated USB address. Note that first entry in a string is
     * entry 1 (not 0), -ve values may be used to access the address in reverse
     * so that the last entry is entry -1.
     * @param addr The address containing the string
     * @param loc The location of the entry.
     * @return The string at location loc or null if not found.
     */
    String getAddressString(String addr, int loc)
    {
        if (addr == null || addr.length() == 0) return null;
        int start, end;
        if (loc < 0)
        {
            end = addr.length();
            start = end;
            for(;;)
            {
                start = addr.lastIndexOf("::", end - 2) + 2;
                if (start < 2) start = 0;
                if (++loc >= 0) break;
                if (start <= 0) return null;
                end = start - 2;
            }
        }
        else
        {
            start = 0;
            end = 0;
            for(;;)
            {
               end = addr.indexOf("::", start);
               if (end < 0) end = addr.length();
               if (start > end) return null;
               if (--loc <= 0) break;
               if (end >= addr.length()) return null;
               start = end+2;
            }
        }
        if (start > end) return null;
        return addr.substring(start, end);
    }
	
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
     * @param block true if request should block rather than timeout
     * @return date or null if at EOF
     */
    private byte[] readPacket(boolean block) throws IOException
    {
        int len;
        while((len=devRead(nxtInfo.nxtPtr, inBuf, 0, inBuf.length)) == 0 && block)
            {}
        if (len < 0) throw new IOException("Error in read");
        int offset = 0;
        if (packetMode)
        {
            if (((int)inBuf[0] & 0xff) != len - 1) throw new IOException("Bad packet format");
            if (inBuf[0] == 0) return null;
            offset = 1;
        }
        if (len == 0) return new byte[0];
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
     * @param block true if requests should block rather than timeout
     * @return number of bytes actually written
     * @throws java.io.IOException
     */
    private int writePacket(byte[] data, int offset, int len, boolean block) throws IOException
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
        while ((ret = devWrite(nxtInfo.nxtPtr, out, offset, len)) == 0 && block)
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
     * Helper function, convert an array of names into an NXTInfo vector. This
     * function takes an array of standard Lego USB string adresses and converts
     * them into an nxtVector. It handles the both NXT and Samba type devices.
     * @param nxtNames an array of device address strings.
     * @return
     */
	Vector<NXTInfo> find(String[] nxtNames)
    {
        if (nxtNames == null) return new Vector<NXTInfo>();
		Vector<NXTInfo> nxtInfos = new Vector<NXTInfo>();
        for(int idx = 0; idx < nxtNames.length; idx++)
        {
            String addr = nxtNames[idx];
            NXTInfo info = new NXTInfo();
            // Use the default way to obtain the name
            info.name = null;
            info.btResourceString = addr;
            info.protocol = NXTCommFactory.USB;
            // Look to see if this is a Samba device
            if (getAddressString(addr, 2).equals(VENDOR_ATMEL) && 
                    getAddressString(addr, 3).equals(PRODUCT_SAMBA))
                info.name = "%%NXT-SAMBA%%";
            info.deviceAddress = getAddressString(addr, -2);
            // if the device address is "000000000000" then it is not
            // supplying a serial number. This is either a very old version
            // of leJOS, or leJOS is not responding. Either way we ignore
            // this device.
            if (info.deviceAddress != null && !info.deviceAddress.equals("000000000000"))
                nxtInfos.addElement(info);
            else
                System.out.println("Ignoring device " + addr);
        }
        return nxtInfos;
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
        // Keep track of how many of the devices have names... We put these
        // first in the returned list
        int nameCnt = 0;
        // Filter the list against name
        while (devs.hasNext())
        {
            NXTInfo nxt = devs.next();
            if (nxt.deviceAddress == null)
                nxt.deviceAddress = "000000000000";
            if (nxt.name == null)
            {
                nxt.name = getName(nxt);
            }
            if (name != null && (nxt.name == null || !name.equals(nxt.name)))
                devs.remove();
            else
                if (nxt.name != null)
                    nameCnt++;
        }
		NXTInfo[] nxts = new NXTInfo[nxtInfos.size()];
        int named = 0;
        int unnamed = nameCnt;
        // Copy the elements over placing the ones with names first.
		for (int i = 0; i < nxts.length; i++)
        {
            NXTInfo nxt = nxtInfos.elementAt(i);
            if (nxt.name == null)
            {
                nxt.name = "Unknown";
                nxts[unnamed++] = nxt;
            }
            else
                nxts[named++] = nxt;
        }
        // Print out the list
		for (int i = 0; i < nxts.length; i++)
            System.out.println("Found NXT: " + nxts[i].name + " " + nxts[i].deviceAddress);
		return nxts;
	}

    /**
     * Open a connection to the specified device, and make it available for use.
     * @param nxtInfo The device to connect to.
     * @param mode the I/O mode to be used on this connection.
     * @return true if the device is now open, false otherwise.
     */
	public boolean open(NXTInfo nxtInfo, int mode) {
		nxtInfo.connectionState = NXTConnectionState.DISCONNECTED;
        // Is the info valid enough to connect directly?
        if (!devIsValid(nxtInfo))
        {
            // not valid so search for it.
            String addr = nxtInfo.deviceAddress;
            if (addr == null || addr.length() == 0)
                return false;
    		Vector<NXTInfo> nxtInfos = devFind();
            Iterator<NXTInfo> devs = nxtInfos.iterator();
            while (devs.hasNext())
            {
                NXTInfo nxt = devs.next();
                if (addr.equalsIgnoreCase(nxt.deviceAddress))
                {
                    nxtInfo = nxt;
                    break;
                }
            }
        }
        if (nxtInfo == null) return false;
		this.nxtInfo = nxtInfo;
		this.nxtInfo.nxtPtr = devOpen(nxtInfo);
        if (this.nxtInfo.nxtPtr == 0) return false;
        // now the connection is open
		nxtInfo.connectionState = (mode == LCP ? NXTConnectionState.LCP_CONNECTED : NXTConnectionState.PACKET_STREAM_CONNECTED);
        if (mode == RAW || mode == LCP) return true;
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
	
    public boolean open(NXTInfo nxt) throws NXTCommException
    {
        return open(nxt, PACKET);
    }

    /**
     * Close the current device.
     */
	public void close() {
        if (nxtInfo == null || nxtInfo.nxtPtr == 0) return;
        if (packetMode)
        {
            writeEOF();
            if (!EOF) waitEOF();
        }
		devClose(nxtInfo.nxtPtr);
        nxtInfo.nxtPtr = 0;
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
        if (len <= 0) throw new IOException("Failed to read reply");
        return ret; 
    }
	
    /**
     * Read bytes from the device
     * @param block true if requests should block rather than timeout
     * @return An array of bytes read from the device. null if at EOF
     * @throws java.io.IOException
     */
	byte [] read(boolean timeout) throws IOException {
        if (EOF) return null;
        byte [] ret = readPacket(timeout);
        if (packetMode && ret == null) EOF = true;
        return ret;
	}
    /**
     * Read bytes from the device
     * @return An array of bytes read from the device. null if at EOF
     * @throws java.io.IOException
     */
	public byte [] read() throws IOException {
        return read(true);
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
     * @param block true if request should block rather than timeout
     * @throws java.io.IOException
     */
	int write(byte [] data, boolean block) throws IOException {
        int total = data.length;
        int written = 0;
        while( written < total)
        {
            int len = writePacket(data, written, total-written, block);
            if (len <= 0) return written;
            written += len;
        }
        return written;
	}
    
    /**
     * Write bytes to the device.
     * @param data Data to be written.
     * @throws java.io.IOException
     */
	public void write(byte [] data) throws IOException {
        write(data, true);
	}
	
	public OutputStream getOutputStream() {
		return new NXTCommOutputStream(this);		
	}
	
	public InputStream getInputStream() {
		return new NXTCommInputStream(this);		
	}

}

