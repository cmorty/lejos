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

    public ConsoleViewComms(ConsoleViewerUI viewer, boolean daemon)
    {
    	this.daemon = daemon;
        this.viewer = viewer;
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
        } else
        {
            connected = true;
        }
        is = con.getInputStream();
        connected = connected && is != null;
        os = con.getOutputStream();
        connected = connected && os != null;

        if (connected)
        {
            try  // handshake
            {
                byte[] hello = new byte[]
                {
                    'C', 'O', 'N'
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
        }
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

