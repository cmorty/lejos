package lejos.pc.charting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;


/**
 * Provides a general connection manager on the PC that 
 * provides an InputStream and OutputStream from/to the <code>NXTDataLogger</code> running on the NXT. 
 * <p>
 * The InputStream flavor used is a <code>CachingInputStream</code> which buffers the byte stream from the NXT to minimize 
 * blocking on the NXT writes. The OutputStream is standard. 
 * <p>
 * The <code>connect()</code> method will attempt to connect via USB first, then Bluetooth via the <code>NXTConnector</code> class.
 * @see lejos.pc.charting.DataLogger
 * @see lejos.util.NXTDataLogger
 * @see NXTConnector
 * @see CachingInputStream
 * @author Kirk P. Thompson
 */
public class LoggerComms {
    /** 
     * Used to get more details from <code>NXTConnector</code> for the Status pane. Notice that the GUI
     * forks STDOUT to the Status textarea so using <code>System.out.println()</code> works to put data into
     * the Status textarea.
     */
    private class ll implements NXTCommLogListener {

        public void logEvent(String message) {
            dbg(message);
        }

        public void logEvent(Throwable throwable) {
            throwable.printStackTrace();
        }
    }
    private final String THISCLASS;
    
    private NXTConnector conn;
    private InputStream in = null;
    private OutputStream out = null;
    private boolean isConnConnected = false;
    private boolean isEOF=true;
    private String connectedNXTName=null;
    
    // constructor
    /** Create a LoggerComms instance
     */
    public LoggerComms() {        
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
    }

    private void dbg(String msg){
        System.out.println(THISCLASS + "-" + msg);
    }
   
    /**Connect to a listening NXT. The NXT must be running <code>NXTDataLogger</code> with the <code>waitForConnection()</code>
     * method called. 
     * @param NXT The name or address of the NXT to connect to. Be aware that NXT names are case-sensitive.
     * @return <code>true</code> if successful connection with Data input/output streams established. <code>false</code>
     * if the connection failed.
     * @see lejos.util.NXTDataLogger
     * 
     */
    public boolean connect(String NXT){
        this.conn = new NXTConnector();
        this.conn.setDebug(true);
        this.conn.addLogListener(new ll());

        dbg("connect() to: " + NXT + ", NXTConnector this.conn=" + this.conn.toString());
        this.isConnConnected = false;
        isConnConnected = conn.connectTo(NXT, null, NXTCommFactory.ALL_PROTOCOLS);
        // ref the DIS/DOS to class vars
        if (this.isConnConnected) {
        	this.connectedNXTName = conn.getNXTInfo().name;
            this.in = new CachingInputStream(this.conn.getInputStream(), 100000); 
            this.out = this.conn.getOutputStream();
            this.isEOF=false; // used to flag EOF
        }
        return this.isConnConnected;
    }

    /** Is there a current valid connection?
     * @return <code>true</code> if so
     */
    public boolean isConnected(){
        return this.isConnConnected;
    }

    /** Return the name of the NXT last successfully connected to.
     * @return name of the NXT
     */
    public String getConnectedNXTName() {
        return this.connectedNXTName;
    }
    
    /** Return the <code>InputStream</code> from the NXT.
     * @return the <code>InputStream</code>
     */
    public InputStream getInputStream() {
        return this.in;
    }
    
    /** Return the <code>OutputStream</code> to the NXT.
     * @return the <code>OutputStream</code>
     */
    public OutputStream getOutputStream() {
        return this.out;
    }
    
    /** Flush the streams, close the connection and clean up. 
     * @see #connect
     */
    public void closeConnection(){
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
            // TODO how to handle?
            dbg("closeConnection(): this.dis.close() IOException: " + e.toString());
        } catch (NullPointerException e) {
            ; // ignore
        }
        try {
            if (this.out!=null) {
                this.out.flush();
                doWait(100);
                this.out.close();
                this.out=null;
            }
        } catch (IOException e) {
            // TODO how to handle?
            dbg("closeConnection(): dos.close() IOException: " + e.toString());
        } catch (NullPointerException e) {
            ; // ignore
        }
        try {
            if (this.conn!=null) this.conn.close();
        } catch (IOException e) {
            // TODO how to handle?
            dbg("closeConnection(): this.conn.close() IOException: " + e.toString());
        }
        this.conn=null;
        this.isConnConnected = false;
        System.gc();
        dbg("Connection teardown complete. maxQueuedBytes was " + maxQueuedBytes);
    }

    private void doWait(long milliseconds) {
         try {
             Thread.sleep(milliseconds);
         } catch (InterruptedException e) {
             //Thread.currentThread().interrupt();
         }
    }
    
}
