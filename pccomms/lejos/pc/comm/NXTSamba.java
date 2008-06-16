package lejos.pc.comm;

import java.io.IOException;

/**
 * 
 * Sends LCP requests to the NXT and receives replies.
 * Uses an object that implements the NXTComm interface 
 * for low-level communication.
 *
 */
public class NXTSamba {

    public static final int PAGE_SIZE = 256;
    
	private NXTCommUSB nxtComm = null;
    private String version;
    
    
	public NXTSamba() {
	}

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
    
    private byte[] read() throws IOException
    {
        byte [] ret = nxtComm.read(false);
        if (ret == null || ret.length == 0)
            throw new IOException("Read timeout");
        /*
        System.out.println("Read returns len " + ret.length);
        for(int i =0; i < ret.length; i++)
            System.out.printf("0x%2x %c", ret[i]& 0xff, (char)ret[i]);
        System.out.println();*/
        return ret;
    }
    
    private void write(byte[] data) throws IOException
    {
        if (nxtComm.write(data, true) != data.length)
            throw new IOException("Write timeout");
    }
    
    private void sendString(String str) throws IOException
    {
        write(str.getBytes("US-ASCII"));
    }
    
    private void sendCommand(char cmd, int addr, int word) throws IOException
    {
        String command = String.format("%c%08X,%08X#", cmd, addr, word);
        sendString(command);
    }

    private void sendCommand(char cmd, int addr) throws IOException
    {
        String command = String.format("%c%08X#", cmd, addr);
        sendString(command);
    }
    
    public void writeWord(int addr, int val) throws IOException
    {
        sendCommand('W', addr, val);
    }
    
    public int readWord(int addr) throws IOException
    {
        sendCommand('w', addr, 4);
        byte [] ret = read();
        if (ret.length < 4)
            throw new IOException("Bad return length");
        return ((int)ret[0] & 0xff) | (((int)ret[1] & 0xff) << 8) |
                (((int)ret[2] & 0xff) << 16) | (((int)ret[3] & 0xff) << 24);
    }
    
    public void writeBytes(int addr, byte[] data) throws IOException
    {
        sendCommand('S', addr, data.length);
        write(data);
    }

    public void jump(int addr) throws IOException
    {
        sendCommand('G', addr);
    }
    
    private void waitReady() throws IOException
    {
        while ((readWord(0xffffff68) & 0x1) == 0)
            Thread.yield();
    }
    
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
          
    public void unlockAllPages() throws IOException
    {
        for(int i = 0; i < 16; i++)
            changeLock(i, false);
    }
    
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
            
	public boolean open(NXTInfo nxt) throws IOException
    {
		if (nxtComm.open(nxt, NXTComm.RAW))
        {
            try
            {
                //System.out.println("Device open");
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
    
    public void close()
    {
        nxtComm.close();
    }
   
    public String getVersion() throws IOException
    {
        return version;
    }

	
}
