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
    
	private static final String CHARSET = "iso-8859-1";
	
	private static final char CMD_INIT = 'N';
	private static final char CMD_GOTO = 'G';
	private static final char CMD_VERBOSE = 'V';
	private static final char CMD_READ_OCTET = 'o';
	private static final char CMD_READ_HWORD = 'h';  
	private static final char CMD_READ_WORD = 'w';
	private static final char CMD_READ_STREAM = 'R';
	private static final char CMD_WRITE_OCTET = 'O';
	private static final char CMD_WRITE_HWORD = 'H';  
	private static final char CMD_WRITE_WORD = 'W';  
	private static final char CMD_WRITE_STREAM = 'S';
	
    private static final int ADDR_HELPER;
    private static final int ADDR_PAGEDATA;
    
    static
    {
    	ADDR_HELPER = 0x208000;
    	ADDR_PAGEDATA = ADDR_HELPER + FlashWrite.CODE.length;
    }
    
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
    
    private int readAnswerWord(int len) throws IOException
    {
        byte [] ret = read();
        if (ret.length < len)
            throw new IOException("Bad return length");
        
        int r = 0;
        for (int i=len; i>0;)
        {
        	r <<= 8;
        	r |= ret[--i] & 0xFF;
        }
        return r;
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
    private void writeString(String str) throws IOException
    {
    	write(str.getBytes(CHARSET));
    }
    
    private void sendInitCommand(int cmd) throws IOException
    {
    	String command = cmd + "#";
        writeString(command);
    }
    
    private void sendGotoCommand(int addr) throws IOException
    {
    	String command = CMD_GOTO + hexFormat(addr, 8) + "#";
        writeString(command);
    }
    
    private void sendStreamCommand(char cmd, int addr, int len) throws IOException
    {
    	String command = cmd + hexFormat(addr, 8) + "," + hexFormat(len, 8) + "#";
        writeString(command);
    }
    
    private void sendWriteCommand(char cmd, int addr, int len, int value) throws IOException
    {
    	String command = cmd + hexFormat(addr, 8) + "," + hexFormat(value, 2 * len) + "#";
        writeString(command);
    }
    
    private void sendReadCommand(char cmd, int addr, int len) throws IOException
    {
        String command = cmd + hexFormat(addr, 8) + "," + len + "#";
        writeString(command);
    }
    
    /**
     * Generated <b>exactly</b> as many hex digits as specified.
     */
    private static String hexFormat(int value, int len)
    {
    	char[] buf = new char[len];
    	for (int i=0; i<len; i++)
    	{
    		int shift = 4 * (len - i - 1);    		
    		int c = (value >>> shift) & 0x0F;
    		if (c < 10)
    			c += '0';
    		else
    			c += 'A' - 10;
    		
    		buf[i] = (char)c;
    	}
    	return String.valueOf(buf);
    }
    
    /**
     * Write a 8 bit octet to the specified address.
     * @param addr
     * @param val
     * @throws java.io.IOException
     */
    public void writeOctet(int addr, int val) throws IOException
    {
        sendWriteCommand(CMD_WRITE_OCTET, addr, 1, val);
    }

    /**
     * Write a 16 bit halfword to the specified address.
     * @param addr
     * @param val
     * @throws java.io.IOException
     */
    public void writeHalfword(int addr, int val) throws IOException
    {
        sendWriteCommand(CMD_WRITE_HWORD, addr, 2, val);
    }

    /**
     * Write a 32 bit word to the specified address.
     * @param addr
     * @param val
     * @throws java.io.IOException
     */
    public void writeWord(int addr, int val) throws IOException
    {
        sendWriteCommand(CMD_WRITE_WORD, addr, 4, val);
    }

    /**
     * Read a 8 bit octet from the specified address.
     * @param addr
     * @return value read from addr
     * @throws java.io.IOException
     */
    public int readOctet(int addr) throws IOException
    {
    	sendReadCommand(CMD_READ_OCTET, addr, 1);
    	return readAnswerWord(1);
    }

    /**
     * Read a 16 bit halfword from the specified address.
     * @param addr
     * @return value read from addr
     * @throws java.io.IOException
     */
    public int readHalfword(int addr) throws IOException
    {
    	sendReadCommand(CMD_READ_HWORD, addr, 2);
    	return readAnswerWord(2);
    }

    /**
     * Read a 32 bit word from the specified address.
     * @param addr
     * @return value read from addr
     * @throws java.io.IOException
     */
    public int readWord(int addr) throws IOException
    {
    	sendReadCommand(CMD_READ_WORD, addr, 4);
    	return readAnswerWord(4);
    }

    /**
     * Write a series of bytes to the device.
     * @param addr
     * @param data
     * @throws java.io.IOException
     */
    public void writeBytes(int addr, byte[] data) throws IOException
    {
    	sendStreamCommand(CMD_WRITE_STREAM, addr, data.length);
        write(data);
    }

    /**
     * Start execution of code at the specified address.
     * @param addr
     * @throws java.io.IOException
     */
    public void jump(int addr) throws IOException
    {
        sendGotoCommand(addr);
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
    	this.writePage(page, data, offset, data.length - offset);
    }
    
    /**
     * Write a single page to flash memory. We write the page to ram and then
     * use the FlashWriter code to transfer this data to flash. The FlashWriter
     * code must have already been downloaded.
     * @param page
     * @param data
     * @param offset
     * @param len
     * @throws java.io.IOException
     */
    public void writePage(int page, byte[] data, int offset, int len) throws IOException
    {
    	if (len > PAGE_SIZE)
    		len = PAGE_SIZE;
        // Generate data chunk (32 bit int pagenum + 256 byte data)
        byte [] buf = new byte[4 + PAGE_SIZE];
        System.arraycopy(data, offset, buf, 4, len);
        encodeInt(buf, 0, page);
        // And the data into ram
        writeBytes(ADDR_PAGEDATA, buf);
        // And now use the flash writer to write the data into flash.
        jump(ADDR_HELPER);
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
        while (len > 0)
        {
            writePage(first, data, start, len);
            start += PAGE_SIZE;
            len -= PAGE_SIZE;
            first++;
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
            	sendInitCommand(CMD_VERBOSE);
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
                	sendInitCommand(CMD_INIT);
                }
                else
                {
                    // Not in verbose mode
                    read(); // Date.
                    read(); // space
                    read(); // time
                    
                }
                // Save the version string
                version = new String(ret, CHARSET);
                // Check that we are all in sync
                ret = read();
                if (ret.length == 2 && ret[0] == (byte)'\n' && ret[1] == (byte)'\r')
                {
                    System.out.println("Connected to SAM-BA " + version);
                }
                // Now upload the flash writer helper routine
                writeBytes(ADDR_HELPER, getModifiedHelper());
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
	
	private static byte[] getModifiedHelper()
	{
		final byte[] code = FlashWrite.CODE;
		final int len = code.length;
		
		byte[] r = new byte[len];
		System.arraycopy(code, 0, r, 0, len);
		
		//encodeMagicInt(code, len + PAGEDATA_OFF, PAGEDATA_MAGIC, ADDR_PAGEDATA);
		
		return r;
	}
	
	private static void assertMagicInt(byte[] code, int off, int magic)
	{
		for (int i = 0; i < 4; i++)
		{
			if ((code[off + i] & 0xFF) != (magic & 0xFF))
				throw new RuntimeException("magic number not found");
			magic >>>= 8;
		}
	}
    
	private static void encodeInt(byte[] code, int off, int value)
	{
		for (int i = 0; i < 4; i++)
		{
			code[off + i] = (byte)value;
			value >>>= 8;
		}
	}
	
	private static void encodeMagicInt(byte[] code, int off, int magic, int value)
	{
		assertMagicInt(code, off, magic);
		encodeInt(code, off, value);
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
