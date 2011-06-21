package lejos.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.InputStream;

import java.util.ArrayList;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;

import lejos.util.Delay;


/**
 * Logger class for the NXT that supports real time data logging of most primitive datatypes. <code>NXTDataLogger</code> communicates with 
 * <code>lejos.pc.charting.DataLogger</code> via Bluetooth or USB. 
 * <p>
 * Hints for "high speed" logging:
 * <ul>
 * <li>Try to keep your datatypes the same across <code>logXxx()</code> calls to avoid the protocol
 * overhead that is incurred when 
 * switching datatypes. For instance, every time you change between <code>logInt()</code> and <code>logLong()</code>, a 
 * synchronization message must be sent to change the datatype on the receiver (i.e. <code>lejos.pc.charting.DataLogger</code>).
 * <li>Use the the <code>logXxx()</code> method with the smallest datatype that fits your data. Less data means better throughput overall.
 * </ul>
 * @author Kirk P. Thompson
 */
public class NXTDataLogger {
    private final byte ATTENTION1 = (byte)(0xff&0xff);
    private final byte ATTENTION2 = (byte)(0xab&0xff);
    private final byte COMMAND_ITEMSPERLINE = 0;
    private final byte COMMAND_DATATYPE     = 1;    
    private final byte    DT_INTEGER = 0;        // sub-commands of COMMAND_DATATYPE
    private final byte    DT_LONG    = 1;
    private final byte    DT_FLOAT   = 2;
    private final byte    DT_DOUBLE  = 3;
    private final byte    DT_STRING  = 4;
    private final byte COMMAND_SETHEADERS   = 2;  
    private final byte COMMAND_FLUSH        = 3;    
    
    /** Use USB for the connection
     * @see #waitForConnection
     */
    public static final int CONN_USB = 1;
    /** Use Bluetooth for the connection
     * @see #waitForConnection
     */
    public static final int CONN_BLUETOOTH = 2;
    
    
    private DataOutputStream dos = null;
    private DataInputStream dis = null;
    private boolean isConnected = false;
    private static NXTConnection theConnection=null; // polymorphism example with abstract class as type
    private byte currentDataType = DT_INTEGER;
    private byte itemsPerLine=1;
    private int timeStampCycler=1;
    private int flushBytes=0;
    private String[] headerLabels=null;
    private ArrayList<Float> logCache = new ArrayList<Float>();
    
    /**
     * Establish a data logger instance
     */
    public NXTDataLogger() {

    }

    private void checkFlush(int byteCount) throws IOException {
        // this may seem useless but when using BT, if I don't do an initial flush, there is a block using the write() on BT doing it's
        // initial flush. This prevents that for some reason. I could have used a boolean but I was thinking I may need a flush every
        // so many x bytes. This is set up to do that.
        if (this.flushBytes==0) {
            this.dos.flush();
            this.flushBytes+=byteCount;
        }
    }
    
    /**Wait for a connection from the PC to begin a real time logging session.  If a connection is already open, it is closed and a new
     * connection is created.
     * <p>
     * This class will use LCD rows 1 and 2 to output status.
     * 
     * @param timeout time in milliseconds to wait for the connection to establish. 0 means wait forever. 
     * @param connectionType Use <code>{@link #CONN_USB}</code> or <code>{@link #CONN_BLUETOOTH}</code>
     * @return <code>true</code> for successful connection
     * @see NXTConnection
     * @see #closeConnection()
     */
    public boolean waitForConnection(int timeout, int connectionType) {
        return waitForConnection(timeout, connectionType, false);
    }
    private  boolean waitForConnection(int timeout, int connectionType, boolean burstMode) {
        final int INIT_TIME=2000;
        if (connectionType!=this.CONN_BLUETOOTH && connectionType!=this.CONN_USB) return false;
        timeout=Math.abs(timeout)+(burstMode?0:INIT_TIME);
        if (this.isConnected) {
            closeConnection(this.dos);
        }
        if (!burstMode) {
            LCD.drawString("Initializing.. ",0,2);
            LCD.drawString("Using " + (connectionType==CONN_BLUETOOTH?"Bluetooth":"USB"),0,1);
            // wait just a bit to display the WAITING prompt to give the conn some time. I found that if immediately
            // try to connect from PC, the conn fails 
            new Thread(new Runnable(){
                public void run(){
                    Delay.msDelay(INIT_TIME);
                    LCD.drawString("WAITING FOR CONN",0,2);
                }
            }).start();
        }
        
        // polymorphism example with abstract class as type
        if (connectionType==CONN_USB) {
            theConnection = USB.waitForConnection(timeout, NXTConnection.PACKET); 
        } else {
            theConnection = Bluetooth.waitForConnection(timeout, NXTConnection.PACKET); 
        }
        if (theConnection == null) {
            if (!burstMode) LCD.drawString("  CONN FAILED!  ",0,2, true);
            return false;
        }
        if (!burstMode) LCD.drawString("   CONNECTED    ",0,2);
        
        this.dis = theConnection.openDataInputStream();
        this.dos = theConnection.openDataOutputStream();
        this.isConnected = (this.dis!=null&&this.dos!=null);
        return this.isConnected;
    }

    /** Close the current open connection. The data stream is flushed and closed. After calling this method, 
     * <code>waitForConnection()</code> must be called to establish another connection.
     * @see #waitForConnection
     */
    public void closeConnection() {
        cleanConnection(this.dos, false);
    }
    private void closeConnection(DataOutputStream passedDOS) {
        cleanConnection(passedDOS, false);
    }
    private void cleanConnection(DataOutputStream passedDOS, boolean burstMode) {
        if (theConnection==null) return;
        if (!burstMode) {
            // Send ATTENTION request and remote FLUSH command
            sendATTN();
            byte[] command = {COMMAND_FLUSH,-1};
            sendCommand(command);
            this.isConnected = false;
        }
        try {
            passedDOS.flush();
            // wait for the hardware to finish any "flushing". I found that without this, the last data may be lost if the program ends
            // or dos is set to null right after the flush().
            Delay.msDelay(100); 
            if (this.dis!=null) this.dis.close();
            if (passedDOS!=null) passedDOS.close();
        } catch (IOException e) {
            ; // ignore
        } catch (Exception e) {
            // TODO What to do?
        }
        if (theConnection!=null&&!burstMode) {
            theConnection.close();
            theConnection=null;
        }
        this.dis = null;
        passedDOS = null;
    }
    
    /** lower level data sending method
     * @param command the bytes[] to send
     */
    private final synchronized void sendCommand(byte[] command){
        if (this.dos==null) return;
        try {
            this.dos.write(command);
        } catch (IOException e) {
            closeConnection(this.dos);
            //this.dos.close();
        }
    }

    /** Send an ATTENTION request. Commands usually follow. There is no response/handshake mechanism.
     */
    private void sendATTN(){
        final int XORMASK = 0xff;
        // 2 ATTN bytes
        byte[] command = {ATTENTION1,ATTENTION2,0,0};  
        int total=0;
        // add the random verifier byte
        command[2] = (byte)((int)(Math.random()*255)&0xff);        
        for (int i=0;i<3;i++) total+=command[i];
        // set the XORed checksum
        command[3]=(byte)((total^XORMASK)&0xff);
        // send it        
        sendCommand(command);
    }

    /** ensures that the System.currentTimeMillis() value is the first in every row. rows are based on header count. cyclic
     */
    private void checkTimeStamp(){
        if (this.timeStampCycler==1) writeLong(System.currentTimeMillis(),false);
        this.timeStampCycler++;
        if (this.timeStampCycler>=(((int)itemsPerLine)&0xff)) this.timeStampCycler=1;
    }


    /** send the command to set the datatype
     * @param datatype
     */
    private void setDataType(byte datatype) {
        byte[] command = {COMMAND_DATATYPE,-1};        
        sendATTN();
        this.currentDataType = datatype;
        command[1] = datatype;        
        sendCommand(command);
    }
    
     /**
      * Write an <code>int</code> to the log. Use this for <code>byte</code> and
      * <code>short</code> as well. If there is no active connection, the method returns immediately
      * without throwing any exception.
      * 
      * @param value The <code>int</code>, <code>short</code>, or <code>byte</code> value to <code>logInt()</code>.
      * @see java.io.DataOutputStream#writeInt 
      */
    public synchronized void logInt(int value) {
        if (this.dos == null) return;        
        checkTimeStamp();
        if (currentDataType != DT_INTEGER) setDataType(DT_INTEGER);        
        try {
            this.dos.writeInt(value);
            checkFlush(4);
        } catch (IOException e) {
            closeConnection(this.dos);
        }
    }
    
    /**
     * Write an <code>long</code> to the log. If there is no active connection, the method returns immediately
      * without throwing any exception.
     * 
     * @param value The <code>long</code> value to <code>logLong()</code>.
     * @see java.io.DataOutputStream#writeLong
     */
    public synchronized void logLong(long value) {
        writeLong(value, true);
    }
    
    private synchronized void writeLong(long value, boolean doTimeStampCheck) {
        if (!this.isConnected) return;    
        if (doTimeStampCheck) checkTimeStamp();
        if (currentDataType != DT_LONG) setDataType(DT_LONG);        
        try {
            this.dos.writeLong(value);
            checkFlush(8);
        } catch (IOException e) {
            closeConnection(this.dos);
        }
    }
    
     /**
      * Write an <code>float</code> to the log. If there is no active connection, the method returns immediately
      * without throwing any exception.
      * 
      * @param value The <code>float</code> value to <code>logFloat()</code>.
      * @see java.io.DataOutputStream#writeFloat
      */
    public synchronized void logFloat(float value) {
        if (!this.isConnected) return;
        checkTimeStamp();
        if (currentDataType != DT_FLOAT) setDataType(DT_FLOAT);        
        try {
//            LCD.drawString("this.dos " + value + " ",0,1);
            this.dos.writeFloat(value);
            checkFlush(4);
        } catch (IOException e) {
            closeConnection(this.dos);
        }
    }
    
    /**
     * Write an <code>double</code> to the log. If there is no active connection, the method returns immediately
      * without throwing any exception.
     * 
     * @param value The <code>double</code> value to <code>logDouble()</code>.
     * @see java.io.DataOutputStream#writeDouble
     */
    public synchronized void logDouble(double value) {
        if (!this.isConnected) return;
        checkTimeStamp();
        if (currentDataType != DT_DOUBLE) setDataType(DT_DOUBLE);        
        try {
    //            LCD.drawString("this.dos " + value + " ",0,1);
            this.dos.writeDouble(value);
            checkFlush(8);
        } catch (IOException e) {
            closeConnection(this.dos);
        }
    }


    // TODO
    /** Don't quite know how to handle this is the chart so it is private for now
     * @param strData The <code>String</code> to log
     */
    private final synchronized void writeStringLine(String strData){
        byte oldIPL = itemsPerLine;
        if (itemsPerLine!=1) setItemsPerLine((byte)1);
        // skip the [potential] timestamp
        if (currentDataType != DT_STRING) setDataType(DT_STRING); 
        writeString(strData, false);
        if (itemsPerLine!=1) setItemsPerLine(oldIPL);
    }
    
    /**
    * Write an <code>String</code> to the <code>DataOutputStream</code> as a null (0) terminated ASCII byte stream.
    * 
    * @param value The <code>String</code> value to <code>logString()</code>.
    * @see java.io.DataOutputStream#write(byte)
    */
    // TODO set to private until I figure out/decide if I want to publish this method
    private final synchronized void logString(String strData) {
        writeString(strData,true);
    }
    
    private void writeString(String strData, boolean executeNormal) {
        if (!this.isConnected) return;
        if(executeNormal) checkTimeStamp();
        if (currentDataType != DT_STRING && executeNormal) setDataType(DT_STRING);       
        
        byte[] strBytes;
        int residual ;
        int arrayLength;
        
        // make sure we have an even 4 byte boundry
        strBytes = strData.getBytes(""); 
        residual = (strBytes.length+1)%4;
        arrayLength = residual>0?strBytes.length+1+(4-residual):strBytes.length+1;
        byte[] tempBytes = new byte[arrayLength];
        System.arraycopy(strBytes,0,tempBytes,0,strBytes.length);
        
        try {
            this.dos.write(tempBytes);
            checkFlush(tempBytes.length);
        } catch (IOException e) {
            closeConnection(this.dos);
        }
    }
    
    
     private void setItemsPerLine(int itemsPerLine){
        sendATTN();
        this.itemsPerLine= (byte)(itemsPerLine&0xff);
        byte[] command = {COMMAND_ITEMSPERLINE,-1};
        command[1] = this.itemsPerLine;
        sendCommand(command);
    }

    // TODO make mandatory, make client change headers and graph without wasting log file
     /** Set the data set header labels. Element 0 is column 1, element 1 is column2, so on and so forth. The items per row
      * is automatically set according to the number of headers (the array size) passed in <code>headerLabels</code>.
      * <p>
      * The data logging is row-cyclic meaning that if you set 5 headers, you need to use 5 <code>logXxxx()</code> methods per row
      * to log your data
      * and the cycle is complete. The next <code>logXxxx()</code> method will put its value on the next line in the log. 
      * Rinse and repeat.
      * 
      * If headers are set mid-logging, the log will reflect the changes from that point on. 
      * <P>
      * If length of the passed array is 
      * zero or if length > 255, the method returns immediately. If there is no active connection, the method returns immediately
      * without throwing any exception.
      * 
      * @param headerLabels The array of header labels to use for the data log
      */
    public void setHeaders(String[] headerLabels) {
        if (headerLabels.length==0) return;
        if (headerLabels.length>255) return;
        this.headerLabels = headerLabels;
        if (!this.isConnected) return;
        this.timeStampCycler=1;
        byte[] command = {COMMAND_SETHEADERS,0};        
        sendATTN();
        command[1] = (byte)(this.headerLabels.length+1&0xff); 
        this.itemsPerLine = command[1];
        sendCommand(command);
        writeString("System_ms", false);
        for (String item: headerLabels){
            writeString(item, false);
        }
    }
    
    /**
     * write a float  value to the log cache for deferred transmit
     * @param v
     */
    public void writeLog(float v)
    {
      Float f = new Float(v);
       this.logCache.add(f);
    }

    /**
    * write 2 float values to the log cache for deferred transmit
    * @param v0
    * @param v1
    */
    public void writeLog(float v0, float v1)
    {
      writeLog(v0);
      writeLog(v1);
    }
    /**
    * write 3 float values to the log cache for deferred transmit
    * @param v0
    * @param v1
    * @param v2
    */
    public void writeLog(float v0, float v1, float v2)
    {
      writeLog(v0,v1);
      writeLog(v2);
    }
    /**
     * write 4 float values to the log cache for deferred transmit
     * @param v0
     * @param v1
     * @param v2
     * @param v3
     */
    public void writeLog(float v0, float v1, float v2, float v3)
    {
      writeLog(v0, v1);
      writeLog(v2,v3);
    }

    /**
     * Transmit the deferred log values to the PC via USB or bluetooth.<p>
     * Displays menu of choices for transmission mode. Scroll to select, press ENTER <br>
     * Then displays "wait for BT" or "wait for USB".  In DataViewer, click on "StartDownload"
     * When finished, displays the number values sent, and asks "Resend?".
     * Press ESC to exit the program, any other key to resend.  Then start the download in DataViewer.
     */
    public void transmit() {
//        NXTConnection connection = null;
//        DataOutputStream dataOut = null;
//        InputStream is = null;
        String[] items = { " USB", " Bluetooth" };
        TextMenu tm = new TextMenu(items, 2, "Transmit using");
        int s = tm.select();
        LCD.clear();
        if (s == 0) {
            LCD.drawString("wait for USB", 0, 0);
        } else {
            LCD.drawString("wait for BT", 0, 0);
        }
        if (!waitForConnection(0, s==0?this.CONN_USB:this.CONN_BLUETOOTH, true)) {
            LCD.drawString("Connect Failed", 0, 1);
            Delay.msDelay(2000);
            return;
        }
        
        LCD.drawString("connected", 0, 1);
        boolean more = true;
        while (more) {
            try {
                LCD.clear();
                LCD.drawString("Wait for Viewer", 0, 0);
                int b = 0;
                b = dis.read();
                LCD.drawInt(b, 8, 1);
            } catch (IOException ie) {
                LCD.drawString("no connection", 0, 0);
                Delay.msDelay(2000);
            }

            LCD.clear();
            LCD.drawString("sending ", 0, 0);
            LCD.drawInt(logCache.size(), 4, 8, 0);
            try {
                // send the content length    
                dos.writeInt(logCache.size());
                dos.flush();
                // burst the data
                for (int i = 0; i < logCache.size(); i++) {
                    Float v = logCache.get(i);
                    dos.writeFloat(v.floatValue());
                }
                this.dos.flush();
                Delay.msDelay(100);
            } catch (IOException e) {
                LCD.drawString("write error", 0, 0);
                LCD.refresh();
                Delay.msDelay(2000);
                break;
            }
            LCD.clear();
            Sound.beepSequence();
            LCD.drawString("Sent " + logCache.size(), 0, 0);
            tm.setTitle("Resend?         ");
            String[] itms = { "Yes", "No" };
            tm.setItems(itms);
            more = 0 == tm.select();
        }
        cleanConnection(this.dos, true);
    }
}
