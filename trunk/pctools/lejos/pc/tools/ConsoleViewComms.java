package lejos.pc.tools;

import java.io.*;
import lejos.pc.comm.*;

/**
 * Contains the logic for connecting to RConsole on the NXT and downloading data.
 * Can be used by different user interfaces.
 * 
 * @author Roger Glassey and Lawrie Griffiths
 *
 */
public class ConsoleViewComms
{
    private InputStream is = null;
    private OutputStream os = null;
    private NXTConnector con;
    private ConsoleViewerUI viewer;
    private Reader reader;
    private boolean connected = false;
    private boolean daemon;
    private boolean lcd;

    public ConsoleViewComms(ConsoleViewerUI viewer, boolean daemon, boolean lcd)
    {
    	this.daemon = daemon;
        this.viewer = viewer;
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
                'C', 'O', (byte)(lcd ? 'O' : 'N')
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
     * Thread to read the RConsole data and send it to the viewer append method
     */
    private class Reader extends Thread
    {
        byte [] lcdBuffer = new byte[100*64/8];
        private int readBuffer() throws IOException
        {
            int cnt = 0;

            while (cnt < lcdBuffer.length)
            {
                int len = is.read(lcdBuffer, cnt, lcdBuffer.length - cnt);
//System.out.println("cnt " + cnt + " len " + len);
                if (len < 0) return -1;
                cnt += len;
            }
            return cnt;
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
                        while ((input = is.read()) >= 0)
                        {
                            if (input == 0xff)
                            {
                                //System.out.println("Got 255 marker");
                                if (readBuffer()< 0) break;
                                viewer.updateLCD(lcdBuffer);
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

