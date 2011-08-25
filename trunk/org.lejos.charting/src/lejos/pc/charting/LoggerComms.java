package lejos.pc.charting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import java.util.LinkedList;

import java.util.Vector;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;


/**
 * Provides a general connection manager and threaded input reader for <code>DataLogger</code> on the PC that 
 * communicates with <code>NXTDataLogger</code> running on the NXT. 
 * <p>
 * The reader thread buffers the byte stream from the NXT into a <code>LinkedList</code> for non-blocking deferred consumption  
 * by calls to <code>getByte()</code>.
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
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private int maxQueueSize = 0;
    private boolean isConnConnected = false;
    private Vector<Byte> readBuffer;
    private boolean isEOF=true;
    private float bytesPerMillisec=0f;
    private String connectedNXTName=null;
    
    // constructor
    /** Create a LoggerComms instance
     */
    public LoggerComms() {        
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
        
        this.readBuffer = new Vector<Byte>(); 
        new InputReader().start();
    }

    private void dbg(String msg){
        System.out.println(THISCLASS + "-" + msg);
    }
    
    /**
     * reader thread that buffers to this.readBuffer
     */
    private class InputReader extends Thread {
        byte readByte;
        int queueSize;
        long beginTime;
        long endTime;
        long readCount=0;
        
        public InputReader(){
            this.setDaemon(true);
        }
        public void run() {
            dbg("thread InputReader started");
            beginTime = System.currentTimeMillis();
            while (true){
                try {                
                    if (LoggerComms.this.dis==null) {
                        doWait(10);
                        continue;
                    }
                    // get a byte (will block)
                    readByte = LoggerComms.this.dis.readByte();
                    readCount++;
                    if (readCount==Long.MAX_VALUE) {
                        readCount=1;
                        beginTime = System.currentTimeMillis();
                    }
                    synchronized(LoggerComms.this) {
                        LoggerComms.this.readBuffer.add(readByte);
                        queueSize = LoggerComms.this.readBuffer.size();
                    }
                    if (queueSize > maxQueueSize) maxQueueSize = queueSize;
                    if (readCount%100==0 || readCount<100) {
                        LoggerComms.this.bytesPerMillisec=(float)readCount / (System.currentTimeMillis() - beginTime);
                    }
                } catch (IOException e) {
                    closeConnection();
                } 
            }
        }
    }


    /** Get the number of bytes that are buffered, ready to be consumed.
     * Throws
     * <code>EOFException</code> if the number of buffered bytes is zero and the connection <code>DataInputStream</code> has thrown an 
     * <code>EOFException</code>.
     * <p>
     * Note that any <code>IOStateListener</code> registered will have its <code>EOFEvent</code> invoked at the time the 
     * actual connection <code>DataInputStream</code> throws an <code>IOException</code>.
     * @return the number of available bytes
     * @see #getByte
     * @throws EOFException
     */
    public int available() throws EOFException{
        int val;
        synchronized(this) {
            val=this.readBuffer.size();
        }
        if (val==0 && this.isEOF) throw new EOFException("available(): buffer is empty and isEOF");
        return val;
    }

    /**Get a byte from the readBuffer.
     *  Will throw <code>EOFException</code> if no bytes are buffered and
     * the connection <code>DataInputStream</code> has thrown an <code>IOException</code>.
     * @return a <code>byte</code> if one is available. A <code>null</code> if buffer is empty.
     * @throws EOFException
     * @see #available
     */
    public byte getByte() throws EOFException{
        Byte val;
        synchronized(this) {
            val=this.readBuffer.remove(0);
        }
        if (val==null && this.isEOF) throw new EOFException("getByte(): buffer is empty and isEOF");
        return val.byteValue();
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
            this.dis = new DataInputStream(this.conn.getInputStream());
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
    
    /** Flush the output streams, close the connection and clean up. This is called automatically by the buffering reader
     * thread on any <code>IOException</code>. 
     * @see #connect
     */
    public void closeConnection(){
        if (this.isEOF) return;
        this.isEOF=true;
        try {
            if (this.dis!=null) {
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
        dbg("Connection teardown complete. maxQueueSize was " + maxQueueSize);
        dbg("Average data throughput was " + getByteThroughput() + " bytes/msec");
        int avail;
        try {
            avail = available();
            dbg(avail+ " bytes left in buffer");
        } catch (EOFException e) {
            // TODO
        }
    }

    /** Get the average byte throughput
     * @return bytes/ms
     */
    public float getByteThroughput() {
        return this.bytesPerMillisec;
    }
    
    private void doWait(long milliseconds) {
         try {
             Thread.sleep(milliseconds);
         } catch (InterruptedException e) {
             //Thread.currentThread().interrupt();
         }
    }
    
}
