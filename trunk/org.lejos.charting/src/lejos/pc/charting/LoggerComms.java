package lejos.pc.charting;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;


/**
 * Provides a general connection manager on the PC that 
 * provides a InputStream from the <code>NXTDataLogger</code> running on the NXT. 
 * <p>
 * The InputStream buffers the byte stream from the NXT to minimize blocking on the NXT writes. 
 * <p>
 * The <code>connect()</code> method will attempt to connect via USB first, then Bluetooth via the <code>NXTConnector</code> class.
 * @see lejos.pc.charting.DataLogger
 * @see lejos.util.NXTDataLogger
 * @see NXTConnector
 * @author Kirk P. Thompson
 */
public class LoggerComms {
    /** Used to get more details from <code>NXTConnector</code> for the Status pane. Notice that the GUI
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
    private InputStream dis = null;
    private DataOutputStream dos = null;
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
    
    /** Write a byte to the DataOutputStream
     * @param value The byte to write
     */
//    public void writeByte(int value){
//        try {
//            this.dos.writeByte(value);
//            this.dos.flush();
//        } catch (IOException e) {
//            // TODO
//            dbg("!** writeByte() error: " + e.toString());
//            e.printStackTrace();
//        }
//    }
    
    /** Write a byte array to the DataOutputStream
     * @param value The byte array to write
     *
     **/
//    public void write(byte[] value){
//        try {
//            this.dos.write(value);
//            this.dos.flush();
//        } catch (IOException e) {
//            // TODO
//            dbg("!** write() error: " + e.toString());
//            e.printStackTrace();
//        }
//    }
    
    /** Write an <code>int</code> to the DataOutputStream
     * @param value The <code>int</code> value to write
     *
     **/
//    public void writeInt(int value){
//        try {
//            this.dos.writeInt(value);
//            this.dos.flush();
//        } catch (IOException e) {
//            // TODO
//            dbg("!** writeInt() error: " + e.toString());
//            e.printStackTrace();
//        }
//    }
    
    /** Write an <code>float</code> to the DataOutputStream
     * @param value The <code>float</code> value to write
     *
     **/
//    public void writeFloat(float value) {
//       writeInt(Float.floatToIntBits(value));
//    }
    
    /** Write an <code>long</code> to the DataOutputStream
     * @param value The <code>long</code> value to write
     *
     **/
//    public void writeLong(long value) {
//       //writeInt(Long.??? floatToIntBits(value));
//        writeInt((int)value<<24);
//    }
    
   
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
        NXTInfo[] theNXTInfo=null;
        this.isConnConnected = false;
        tryBlock1:
        try {
            // connect to NXT over USB or BT
            theNXTInfo = this.conn.search(NXT,null,NXTCommFactory.ALL_PROTOCOLS);
            if (theNXTInfo.length==0) {
                dbg("No NXT found. Returning false.");
                break tryBlock1;
            }
            this.isConnConnected = this.conn.connectTo(theNXTInfo[0], NXTComm.PACKET);
            dbg("isConnConnected=" + this.isConnConnected);
        } catch (Error e) {
            dbg("!** Problem with establishing connection. Error: " + e.toString());
            
        } catch (Exception e) {
            dbg("!** Problem with establishing connection. Exception: " + e.toString());
        }
        // ref the DIS/DOS to class vars
        if (this.isConnConnected) {
            this.connectedNXTName=theNXTInfo[0].name;
            this.dis = new CachingInputStream(this.conn.getInputStream(), 20000); 
            this.dos = new DataOutputStream(this.conn.getOutputStream());
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
    
    /** Return the InputStream from the NXT.
     * @return the InputStream
     */
    public InputStream getInputStream() {
        return dis;
    }
    
    /** Flush the output streams, close the connection and clean up. This is called automatically by the buffering reader
     * thread on any <code>IOException</code>. 
     * @see #connect
     */
    public void closeConnection(){
        if (this.isEOF) return;
        this.isEOF=true;
        int maxQueuedBytes=0;
        try {
            if (this.dis!=null) {
                maxQueuedBytes=((CachingInputStream)dis).getMaxQueuedBytes();
                this.dis.close();
                this.dis=null;
            }
        } catch (IOException e) {
            // TODO
            dbg("closeConnection(): this.dis.close() IOException: " + e.toString());
        } catch (NullPointerException e) {
            ; // ignore
        }
        try {
            if (this.dos!=null) {
                this.dos.flush();
                doWait(100);
                this.dos.close();
                this.dos=null;
            }
        } catch (IOException e) {
            // TODO
            dbg("closeConnection(): dos.close() IOException: " + e.toString());
        } catch (NullPointerException e) {
            ; // ignore
        }
        try {
            if (this.conn!=null) this.conn.close();
        } catch (IOException e) {
            // TODO
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
