package lejos.pc.comm;

import java.io.IOException;

/**
 * Implements a sub-set of the Atmel SAM-BA download protocol. Only those
 * functions required for program download to the NXT flash are currently
 * implemented.
 *
 */
public class NXTSamba {

    public static final int PAGE_SIZE = 256;
    public static final int FLASH_BASE = 0x00100000;
    
	private NXTCommUSB nxtComm = null;
    private String version;
    
    
	public NXTSamba() {
	}


    /**
     * Locate all NXT devices that are running in SAM-BA mode.
     * @return An array of devices in SAM-BA mode
     * @throws lejos.pc.comm.NXTCommException
     */
    public NXTInfo[] search() throws NXTCommException {
		NXTInfo[] nxtInfos;

		if (nxtComm == null) {
			try {
                nxtComm = (NXTCommUSB) NXTCommFactory.createNXTComm(NXTCommFactory.USB);
			} catch (NXTCommException e) {
			}

			if (nxtComm == null) {
				throw new NXTCommException("Cannot load a comm driver");
			}
		}

		// Look for a USB one first

        nxtInfos = nxtComm.search("%%NXT-SAMBA%%", NXTCommFactory.USB);
        if (nxtInfos.length > 0) {
            return nxtInfos;
        }
		return new NXTInfo[0];
	}
   
    /**
     * Helper function perform a read with timeout. 
     * @return Bytes read from the device.
     * @throws java.io.IOException
     */
    private byte[] read() throws IOException
    {
        byte [] ret = nxtComm.read(false);
        if (ret == null || ret.length == 0)
            throw new IOException("Read timeout");
        return ret;
    }
    
    /**
     * Helper function perform a write with timeout.
     * @param data Data to be written to the device.
     * @throws java.io.IOException
     */
    private void write(byte[] data) throws IOException
    {
        if (nxtComm.write(data, true) != data.length)
            throw new IOException("Write timeout");
    }
    
    /**
     * Helper function, send a string to the device. Convert from Unicode to
     * ASCII and send the string.
     * @param str String to be sent.
     * @throws java.io.IOException
     */
    private void sendString(String str) throws IOException
    {
        write(str.getBytes("US-ASCII"));
    }
    
    /**
     * Format and send a SAM-BA command.
     * @param cmd Command character
     * @param addr Address
     * @param word Addional parameter
     * @throws java.io.IOException
     */
    private void sendCommand(char cmd, int addr, int word) throws IOException
    {
        String command = String.format("%c%08X,%08X#", cmd, addr, word);
        sendString(command);
    }
    
    /**
     * Format and send a SAM-BA command.
     * @param cmd Command character
     * @param addr Address
     * @throws java.io.IOException
     */
    private void sendCommand(char cmd, int addr) throws IOException
    {
        String command = String.format("%c%08X#", cmd, addr);
        sendString(command);
    }

    /**
     * Write a 32 bit word to the specified address.
     * @param addr
     * @param val
     * @throws java.io.IOException
     */
    public void writeWord(int addr, int val) throws IOException
    {
        sendCommand('W', addr, val);
    }

    /**
     * Read a 32 bit value from the specified address.
     * @param addr
     * @return value read from addr
     * @throws java.io.IOException
     */
    public int readWord(int addr) throws IOException
    {
        sendCommand('w', addr, 4);
        byte [] ret = read();
        if (ret.length < 4)
            throw new IOException("Bad return length");
        return ((int)ret[0] & 0xff) | (((int)ret[1] & 0xff) << 8) |
                (((int)ret[2] & 0xff) << 16) | (((int)ret[3] & 0xff) << 24);
    }

    /**
     * Write a series of bytes to the device.
     * @param addr
     * @param data
     * @throws java.io.IOException
     */
    public void writeBytes(int addr, byte[] data) throws IOException
    {
        sendCommand('S', addr, data.length);
        write(data);
    }

    /**
     * Start execution of code at the specified address.
     * @param addr
     * @throws java.io.IOException
     */
    public void jump(int addr) throws IOException
    {
        sendCommand('G', addr);
    }
    
    /**
     * Wait for the flash controller to be ready to accept commands.
     * @throws java.io.IOException
     */
    private void waitReady() throws IOException
    {
        while ((readWord(0xffffff68) & 0x1) == 0)
            Thread.yield();
    }
    
    /**
     * Change the lock bits for a region of flash memory.
     * @param rgn
     * @param lock
     * @throws java.io.IOException
     */
    private void changeLock(int rgn, boolean lock) throws IOException
    {
        int cmd = 0x5a000000 | ((64*rgn) << 8);
        if (lock)
            cmd |= 0x2;
        else
            cmd |= 0x4;
        waitReady();
        writeWord(0xffffff60, 0x00050100);
        writeWord(0xffffff64, cmd);
        writeWord(0xffffff60, 0x00340100);
    }
  
    /**
     * Turn off the lock bits for all of flash memory.
     * @throws java.io.IOException
     */
    public void unlockAllPages() throws IOException
    {
        for(int i = 0; i < 16; i++)
            changeLock(i, false);
    }

    /**
     * Write a single page to flash memory. We write the page to ram and then
     * use the FlashWriter code to transfer this data to flash. The FlashWriter
     * code must have already been downloaded.
     * @param page
     * @param data
     * @param offset
     * @throws java.io.IOException
     */
    public void writePage(int page, byte[] data, int offset) throws IOException
    {
        //System.out.println("Write page " + page);
        byte [] buf = new byte[PAGE_SIZE];
        System.arraycopy(data, offset, buf, 0, (offset + PAGE_SIZE < data.length ? PAGE_SIZE : data.length - offset));
        // Write the address to write to
        writeWord(0x202300, page);
        // And the data into ram
        writeBytes(0x202100, buf);
        // And now use the flash writer to write the data into flash.
        jump(0x202000);
    }

    /**
     * Write a series of pages to flash memory.
     * @param first
     * @param data
     * @param start
     * @param len
     * @throws java.io.IOException
     */
    public void writePages(int first, byte[] data, int start, int len) throws IOException
    {
        int offset = start;
        int page = first;
        while (offset < start + len)
        {
            writePage(page, data, offset);
            page++;
            offset += PAGE_SIZE;
        }
    }

    /**
     * Read a single page from flash memory.
     * @param page
     * @param data
     * @param offset
     * @throws java.io.IOException
     */
    public void readPage(int page, byte[] data, int offset) throws IOException
    {
        //System.out.println("Write page " + page);
        int addr = FLASH_BASE + page*PAGE_SIZE;
        for(int i = 0; i < PAGE_SIZE/4; i++)
        {
            int w = readWord(addr);
            data[offset++] = (byte) w;
            data[offset++] = (byte) (w >> 8);
            data[offset++] = (byte) (w >> 16);
            data[offset++] = (byte) (w >> 24);
            addr += 4;
        }
    }

    /**
     * Read a series of pages from flash memory.
     * @param first
     * @param data
     * @param start
     * @param len
     * @throws java.io.IOException
     */
    public void readPages(int first, byte[] data, int start, int len) throws IOException
    {
        int offset = start;
        int page = first;
        while (offset < start + len)
        {
            readPage(page, data, offset);
            page++;
            offset += PAGE_SIZE;
        }
    }

    /**
     * Open the specified USB device and check that it is in SAM-BA mode. We
     * switch the device into "quiet" mode and also download the FlashWrite
     * program.
     * @param nxt Device to open.
     * @return true if the device is now open, false otherwise.
     * @throws java.io.IOException
     */
	public boolean open(NXTInfo nxt) throws IOException
    {
		if (nxtComm.open(nxt, NXTComm.RAW))
        {
            try
            {
                // We need to work out if the device is in verbose mode. If
                // so we switch it into quiet mode. We also check to ensure
                // that it responds to commands.
                // Ask for the version number.
                sendString("V#");
                byte []ret = read();
                // If we are in verbose mode we will get back "\n", "\r"
                if (ret.length == 2 && ret[0] == (byte)'\n' && ret[1] == (byte)'\r')
                {
                    // In verbose mode. Read and ignore the version info plus
                    // Prompt then switch into quiet mode.
                    ret = read(); // Version 1.
                    read(); // Date.
                    read(); // space
                    read(); // time
                    read(); // newline
                    read(); // Prompt.
                    sendString("N#");
                }
                else
                {
                    // Not in verbose mode
                    read(); // Date.
                    read(); // space
                    read(); // time
                    
                }
                // Save the version string
                version = new String(ret);
                // Check that we are all in sync
                ret = read();
                if (ret.length == 2 && ret[0] == (byte)'\n' && ret[1] == (byte)'\r')
                {
                    System.out.println("Connected to SAM-BA " + version);
                }
                // Now upload the flash writer helper routine
                writeBytes(0x202000, FlashWrite.CODE);
                // And set the the clock into PLL/2 mode ready for writing
                writeWord(0xfffffc30, 0x7);
                return true;
            }
            catch (IOException e)
            {
                // Some sort of error
            }
            // Unable to sync things make sure the device is closed.
            nxtComm.close();
        }
        return false;
	}
    
    /**
     * Close the device.
     */
    public void close()
    {
        nxtComm.close();
    }
   
    /**
     * returns the SAM-BA version string for the current device.
     * @return The SAM-BA version.
     * @throws java.io.IOException
     */
    public String getVersion() throws IOException
    {
        return version;
    }

	
}
