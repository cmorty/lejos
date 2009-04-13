package lejos.pc.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

public class ConsoleViewComms
{
    private InputStream is = null;
    private OutputStream os = null;
    private NXTConnector con;
    private ConsoleViewerUI viewer;
    private Reader reader;
    private boolean _connected = false;

    public ConsoleViewComms(ConsoleViewerUI viewer)
    {
        this.viewer = viewer;
        reader = new Reader();
        reader.start();
    }

    public boolean connectTo(String name, String address, boolean useUSB)
    {
        con = new NXTConnector();
        con.addLogListener(new ToolsLogger());
        if (!con.connectTo(name, address, (useUSB ? NXTCommFactory.USB : NXTCommFactory.BLUETOOTH)))
        {
            return false;
        } else
        {
            _connected = true;
        }
        is = con.getInputStream();
        _connected = _connected && is != null;
        os = con.getOutputStream();
        _connected = _connected && os != null;
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
            System.out.println(e + " handshake failed to write ");
            _connected = false;
            return false;
        }
        if (_connected)
        {
            name = con.getNXTInfo().name;
            address = con.getNXTInfo().deviceAddress;
            viewer.connectedTo(name, address);
            System.out.println(" connection " + name + " " + address);
        }
        return _connected;
    }

    private class Reader extends Thread
    {
        public void run()
        {
            while (true)
            {              
                if (_connected)
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
                        _connected = false;
                    }
                }               
                Thread.yield();
            }
        }
    }
}

