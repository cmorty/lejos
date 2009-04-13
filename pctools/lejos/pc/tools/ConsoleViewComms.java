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

    public ConsoleViewComms(ConsoleViewerUI viewer)
    {
        this.viewer = viewer;
        reader = new Reader();
        reader.start();
    }
    
    public boolean connectTo(String name, String address, boolean useUSB)
    {
    	return connectTo(name, address, (useUSB ? NXTCommFactory.USB : NXTCommFactory.BLUETOOTH));
    }

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
    
    public void close() {
    	try {
    		if (con != null) con.close();
    	} catch (IOException e) {}
    }

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
                        is.close();
                    } catch (IOException e)
                    {
                        connected = false;
                    }
                }               
                Thread.yield();
            }
        }
    }
}

