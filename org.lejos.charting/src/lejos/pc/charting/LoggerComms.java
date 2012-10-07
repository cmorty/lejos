package lejos.pc.charting;

import java.io.IOException;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
import lejos.util.Delay;


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
public class LoggerComms extends AbstractConnectionManager{
    private static final int MAX_IS_BUFFER_SIZE = 500000;
    private final String THISCLASS;
    
    private NXTConnector conn;
    private boolean isConnConnected = false;
    private boolean isEOF=true;
    
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
    @Override
	public boolean connect(String NXT){
        this.conn = new NXTConnector();
        this.conn.setDebug(true);
        this.conn.addLogListener(new ll());

        dbg("connect() to: " + NXT + ", NXTConnector this.conn=" + this.conn.toString());
        this.isConnConnected = false;
        isConnConnected = conn.connectTo(NXT, null, NXTCommFactory.ALL_PROTOCOLS);
        // ref the DIS/DOS to class vars
        if (this.isConnConnected) {
        	this.connectedDeviceName = conn.getNXTInfo().name;
            this.in = new CachingInputStream(this.conn.getInputStream(), MAX_IS_BUFFER_SIZE); 
            this.out = this.conn.getOutputStream();
            this.isEOF=false; // used to flag EOF
        }
        return this.isConnConnected;
    }

    /* (non-Javadoc)
	 * @see lejos.pc.charting.ConnectionProvider#closeConnection()
	 */
    @Override
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
             // ignore
        }
        try {
            if (this.out!=null) {
                this.out.flush();
                Delay.msDelay(100);
                this.out.close();
                this.out=null;
            }
        } catch (IOException e) {
            // TODO how to handle?
            dbg("closeConnection(): dos.close() IOException: " + e.toString());
        } catch (NullPointerException e) {
             // ignore
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
    
    @Override
	public boolean isConnected(){
    	return this.isConnConnected;
    }
}
