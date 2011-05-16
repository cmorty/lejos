package lejos.pc.tools;

import java.io.*;
import lejos.pc.comm.*;

public class DataViewComms
{
    private NXTConnector con;
    private boolean connected = false;
    private DataViewerUI viewer;
    private OutputStream os;
    private DataInputStream dataIn;

    public DataViewComms(DataViewerUI viewer)
    {
        this.viewer = viewer;
    }
    
    public void setConnected(boolean connected)
    {
    	this.connected = connected;
    }
    
    public boolean connecTo(String name, String address, boolean useUSB) 
    {
        int protocols;
        if (useUSB)
        {
            protocols = NXTCommFactory.USB;
        } else
        {
            protocols = NXTCommFactory.BLUETOOTH;
        }
        
        return connectTo(name, address, protocols);
    }

    public boolean connectTo(String name, String address, int protocol)
    {
        viewer.logMessage("Connecting to " + name + " " + address);
        con = new NXTConnector();
        boolean res = con.connectTo(name, address, protocol);
        viewer.logMessage("Connect result " + res);
        if (!res)
        {
        	viewer.logMessage("Connection failed ");
            return false;
        }
        os = con.getOutputStream();
        dataIn = con.getDataIn();
        if (dataIn == null)
        {
        	viewer.logMessage("NULL input stream ");
            return false;
        } else
        {
            if (os == null)
            {
            	viewer.logMessage("NULL output stream");
                return false;
            } else
            {
                connected = true;
            }
        }
        name = con.getNXTInfo().name;
        address = con.getNXTInfo().deviceAddress;
        viewer.connectedTo(name, address);
        return true;
    }

    public void startDownload()
    {
        if (!connected)
        {
        	viewer.showMessage("Not yet connected");
            return;

        }
        int b = 15;
        try //handshake - ready to read data
        {
            os.write(b);
            os.flush();
        } catch (IOException e)
        {
            viewer.showMessage(e + " handshake failed ");
        }
        try
        {
            int length = dataIn.readInt();
            viewer.setStatus(" reading length " + length);
            for (int i = 0; i < length; i++)
            {
                viewer.append(dataIn.readFloat());
            }
        } catch (IOException e)
        {
            viewer.showMessage("read error " + e);
        }
        viewer.setStatus("Read all data");
    }
    
    public void close() {
    	try {
    		if (con != null) con.close();
    	} catch (IOException ioe) {}
    }
}	
