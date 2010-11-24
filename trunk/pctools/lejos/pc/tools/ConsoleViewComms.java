package lejos.pc.tools;

import java.io.*;
import lejos.pc.comm.*;

/**
 * Contains the logic for connecting to RConsole on the NXT and downloading data.
 * Can be used by different user interfaces.
 * 
 * @author Roger Glassey, Lawrie Griffiths and Andy Shaw
 *
 */
public class ConsoleViewComms
{
    private static final int MODE_SWITCH = 0xff;
    private static final int MODE_LCD = 0x0;
    private static final int MODE_EVENT = 0x1;
    private static final int OPT_LCD = 1;
    private static final int OPT_EVENT = 2;
    private InputStream is = null;
    private OutputStream os = null;
    private NXTConnector con;
    private ConsoleViewerUI viewer;
    private ConsoleDebug debug;
    private Reader reader;
    private boolean connected = false;
    private boolean daemon;
    private boolean lcd;

    public ConsoleViewComms(ConsoleViewerUI viewer, ConsoleDebug debug, boolean daemon, boolean lcd)
    {
    	this.daemon = daemon;
        this.viewer = viewer;
        this.debug = debug;
        this.lcd = lcd;
        reader = new Reader();
        reader.setDaemon(daemon);
        reader.start();
    }
    
    /**
     * Connect to RConsole on the NXT uusing either USB or Bluetooth
     * 
     * @param name the name of the NXT or null
     * @param address the address of the NXT or null
     * @param useUSB use USB if true, else use Bluetooth
     * @return true iff the connection was successful
     */
    public boolean connectTo(String name, String address, boolean useUSB)
    {
    	return connectTo(name, address, (useUSB ? NXTCommFactory.USB : NXTCommFactory.BLUETOOTH));
    }

    /**
     * Connect to RConsole on the NXT using the specified protocols
     * 
     * @param name the name of the NXT or null
     * @param address the address of the NXT or null
     * @param protocol USB or Bluetooth or both
     * @return true iff the connection was successful
     */
    public boolean connectTo(String name, String address, int protocol)
    {
        con = new NXTConnector();
        con.addLogListener(new ToolsLogger());
        if (!con.connectTo(name, address, protocol))
        {
            return false;
        }
        is = con.getInputStream();
        os = con.getOutputStream();
        if (is == null || os == null) return false;
        try  // handshake
        {
            byte[] hello = new byte[]
            {
                'R', 'C', (byte)((lcd ? OPT_LCD : 0) | (debug != null ? OPT_EVENT : 0))
            };
            os.write(hello);
            os.flush();
        } catch (IOException e)
        {
            viewer.logMessage("Handshake failed to write: " + e.getMessage());
            connected = false;
            return false;
        }
        name = con.getNXTInfo().name;
        address = con.getNXTInfo().deviceAddress;
        viewer.connectedTo(name, address);
        viewer.logMessage("Connected to " + name + " " + address);
        connected = true;
        return connected;
    }
    
    /**
     * Close the connection
     */
    public void close() {
    	try {
    		if (con != null) con.close();
    	} catch (IOException e) {}
    	connected = false;
    }

    /**
     * Wait for the console session to end
     */
    public void waitComplete()
    {
        if (!this.daemon && reader != null)
            try {
                reader.join();
            } catch(InterruptedException e)
            {

            }
    }
    /**
     * Thread to read the RConsole data and send it to the viewer append method
     */
    private class Reader extends Thread
    {
        byte [] lcdBuffer = new byte[100*64/8];


        private int processLCDData() throws IOException
        {
            int cnt = 0;

            while (cnt < lcdBuffer.length)
            {
                int len = is.read(lcdBuffer, cnt, lcdBuffer.length - cnt);
                if (len < 0) return -1;
                cnt += len;
            }
            viewer.updateLCD(lcdBuffer);
            return cnt;
        }

        private int processExceptionData() throws IOException
        {
            int classNo = is.read();
            if (classNo < 0) return -1;
            int msgCnt = is.read() | (is.read() << 8);
            if (msgCnt < 0) return -1;
            char [] msg = new char[msgCnt];
            for(int i = 0; i < msgCnt; i++)
            {
                int ch = is.read();
                if (ch < 0) return -1;
                msg[i] = (char) ch;
            }
            int traceCnt = is.read();
            if (traceCnt < 0) return -1;
            int [] stackTrace = new int[traceCnt];
            for(int i = 0; i < traceCnt; i++)
            {
                stackTrace[i] = is.read() | (is.read() << 8) | (is.read() << 16) | (is.read() << 24);
                if (stackTrace[i] < 0) return -1;
            }
            debug.exception(classNo, new String(msg), stackTrace);
            return 0;
        }

        public void run()
        {
            while (true)
            {              
                if (connected)
                {
                    try
                    {
                        int input;
                        ioloop:while ((input = is.read()) >= 0)
                        {
                            if (input == MODE_SWITCH)
                            {
                                // Extended data types, first byte tells us the type
                                switch(is.read())
                                {
                                    case MODE_LCD:
                                        if (processLCDData()< 0) break ioloop;
                                        break;
                                    case MODE_EVENT:
                                        if (processExceptionData() < 0) break ioloop;
                                        break;
                                }
                                //System.out.println("Got 255 marker");
                            }
                            else
                                viewer.append("" + (char) input);
                        }
                        close();
                        if (!daemon) return;
                    } catch (IOException e)
                    {
                        close();
                        if (!daemon) return;
                    }
                }               
                Thread.yield();
            }
        }
    }
}

