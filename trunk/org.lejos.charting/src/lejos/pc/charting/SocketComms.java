package lejos.pc.charting;

import java.io.IOException;
import java.net.Socket;

import lejos.pc.comm.NXTConnector;
import lejos.util.Delay;

/**
 * Connection manager for socket connections to port 50010 (default) or user-specified via constructor.
 * 
 * @author Kirk P. Thompson
 * @see lejos.pc.charting.DataLogger
 * @see CachingInputStream
 *
 */
public class SocketComms extends AbstractConnectionManager {
    private final String THISCLASS;
    private static final int MAX_IS_BUFFER_SIZE = 500000;
    /**
     * The default ServerSocket listen port 
     */
    public static final int LOGGER_SERV_PORT = 50010;
    
    private boolean isConnConnected = false;
    private boolean isEOF=true;
    private Socket conn;
    private int connectionPort;
    
    private void dbg(String msg){
        System.out.println(THISCLASS + "-" + msg);
    }
    
    /**
     * 
     */
    public SocketComms() {
        this(LOGGER_SERV_PORT);
    }
    
    public SocketComms(int port) {
        this.connectionPort = port;
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
    }
    /* (non-Javadoc)
     * @see lejos.pc.charting.AbstractConnectionManager#isConnected()
     */
    @Override
    public boolean isConnected() {
        return this.isConnConnected;
    }

    /* (non-Javadoc)
     * @see lejos.pc.charting.AbstractConnectionManager#closeConnection()
     */
    @Override
    public void closeConnection() {
        if (this.isEOF) return;
        this.isEOF=true;
        int maxQueuedBytes=0;
        try {
            if (this.in!=null) {
                maxQueuedBytes=((CachingInputStream)this.in).getMaxQueuedBytes();
                this.in.close();
                this.in=null;
            }
        } catch (IOException e) {
            dbg("closeConnection(): this.is.close() IOException: " + e.toString());
        } catch (NullPointerException e) {
             // ignore
        }
        try {
            if (this.out!=null) {
                this.out.flush();
                Delay.msDelay(100); // wait a bit for the flush to complete
                this.out.close();
                this.out=null;
            }
        } catch (IOException e) {
            dbg("closeConnection(): this.out.close() IOException: " + e.toString());
        } catch (NullPointerException e) {
             // ignore
        }
        try {
            if (this.conn!=null) this.conn.close();
        } catch (IOException e) {
            dbg("closeConnection(): this.conn.close() IOException: " + e.toString());
        }
        this.conn=null;
        this.isConnConnected = false;
        dbg("Connection teardown complete. maxQueuedBytes was " + maxQueuedBytes);
        

    }

    /* (non-Javadoc)
     * @see lejos.pc.charting.AbstractConnectionManager#connect(java.lang.String)
     */
    @Override
    public boolean connect(String remoteDevice) {
        this.isConnConnected = false;
        
        try {
            this.conn = new Socket(remoteDevice, this.connectionPort);
            this.in = new CachingInputStream(this.conn.getInputStream(), MAX_IS_BUFFER_SIZE);
            this.out = this.conn.getOutputStream();
            this.isConnConnected = true;
            this.isEOF=false; // used to flag EOF
            this.connectedDeviceName = remoteDevice;
            dbg("connect() to: " + remoteDevice + ", this.conn=" + this.conn.toString());
        } catch (IOException e) {
            dbg(e.toString());
        } catch (Exception e) {
            dbg("General exception: " + e.toString());
        }
        
        return this.isConnConnected;
    }

}
