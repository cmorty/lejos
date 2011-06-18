package lejos.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;

import lejos.util.Delay;


/**
 * Logger class for the NXT that supports real time data logging of most primitive datatypes. <code>NXTDataLogger</code> communicates with 
 * <code>lejos.pc.charting.DataLogger</code> via Bluetooth or USB. 
 * <p>
 * Hints for high speed logging:
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
            dos.flush();
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
        if (connectionType!=CONN_BLUETOOTH && connectionType!=CONN_USB) return false;
        timeout=Math.abs(timeout);
        if (isConnected) {
            closeConnection(dos);
        }
        LCD.drawString("Initializing.. ",0,2);
        LCD.drawString("Using " + (connectionType==CONN_BLUETOOTH?"Bluetooth":"USB"),0,1);
        // wait just a bit to display the WAITING prompt to give the conn some time. I found that if immediately
        // try to connect from PC, the conn fails 
        new Thread(new Runnable(){
            public void run(){
                Delay.msDelay(1000);
                LCD.drawString("WAITING FOR CONN",0,2);
            }
        }).start();
        
        // polymorphism example with abstract class as type
        if (connectionType==CONN_USB) {
            theConnection = USB.waitForConnection(timeout, NXTConnection.PACKET); 
        } else {
            theConnection = Bluetooth.waitForConnection(timeout, NXTConnection.PACKET); 
        }
        if (theConnection == null) {
            LCD.drawString("  CONN FAILED!  ",0,2, true);
            return false;
        }
        LCD.drawString("   CONNECTED    ",0,2);
        
        dis = theConnection.openDataInputStream();
        dos = theConnection.openDataOutputStream();
        isConnected = true;
        return isConnected;
    }

    /** Close the current open connection. The data stream is flushed and closed. After calling this method, 
     * <code>waitForConnection()</code> must be called to establish another connection.
     * @see #waitForConnection
     */
    public void closeConnection() {
        closeConnection(dos);
    }
    private void closeConnection(DataOutputStream passedDOS) {
        if (theConnection==null) return;
        // Send ATTENTION request and remote FLUSH command
        sendATTN();
        byte[] command = {COMMAND_FLUSH,-1};
        sendCommand(command);
        isConnected = false;
        
        try {
            passedDOS.flush();
            // wait for the hardware to finish any "flushing". I found that without this, the last data may be lost.
            Delay.msDelay(100); 
            if (dis!=null) dis.close();
            if (passedDOS!=null) passedDOS.close();
        } catch (IOException e) {
            ; // ignore
        } catch (Exception e) {
            // TODO What to do?
        }
        if (theConnection!=null) {
            theConnection.close();
            theConnection=null;
        }
        dis = null;
        passedDOS = null;
    }
    
    /** lower level data sending method
     * @param command the bytes[] to send
     */
    private final synchronized void sendCommand(byte[] command){
        if (dos==null) return;
        try {
            dos.write(command);
        } catch (IOException e) {
            closeConnection(dos);
            //dos.close();
        }
    }

    /** Send an ATTENTION request. Commands usually follow. There is no response mechanism.
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
        if (timeStampCycler==1) writeLong(System.currentTimeMillis(),false);
        timeStampCycler++;
        if (timeStampCycler>=(((int)itemsPerLine)&0xff)) timeStampCycler=1;
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
        if (dos == null) return;        
        checkTimeStamp();
        if (currentDataType != DT_INTEGER) setDataType(DT_INTEGER);        
        try {
            dos.writeInt(value);
            checkFlush(4);
        } catch (IOException e) {
            closeConnection(dos);
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
        if (!isConnected) return;    
        if (doTimeStampCheck) checkTimeStamp();
        if (currentDataType != DT_LONG) setDataType(DT_LONG);        
        try {
            dos.writeLong(value);
            checkFlush(8);
        } catch (IOException e) {
            closeConnection(dos);
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
        if (!isConnected) return;
        checkTimeStamp();
        if (currentDataType != DT_FLOAT) setDataType(DT_FLOAT);        
        try {
//            LCD.drawString("dos " + value + " ",0,1);
            dos.writeFloat(value);
            checkFlush(4);
        } catch (IOException e) {
            closeConnection(dos);
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
        if (!isConnected) return;
        checkTimeStamp();
        if (currentDataType != DT_DOUBLE) setDataType(DT_DOUBLE);        
        try {
    //            LCD.drawString("dos " + value + " ",0,1);
            dos.writeDouble(value);
            checkFlush(8);
        } catch (IOException e) {
            closeConnection(dos);
        }
    }


    // TODO
    /** Don't quite know how to handle this is the chart so it is commented for now
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
    private final synchronized void logString(String strData) {
        writeString(strData,true);
    }
    
    private void writeString(String strData, boolean executeNormal) {
        if (!isConnected) return;
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
            dos.write(tempBytes);
            checkFlush(tempBytes.length);
        } catch (IOException e) {
            closeConnection(dos);
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
        if (!isConnected) return;
        if (headerLabels.length==0) return;
        if (headerLabels.length>255) return;
        timeStampCycler=1;
        byte[] command = {COMMAND_SETHEADERS,0};        
        sendATTN();
        command[1] = (byte)(headerLabels.length+1&0xff); 
        this.itemsPerLine = command[1];
        sendCommand(command);
        writeString("System_ms", false);
        for (String item: headerLabels){
            writeString(item, false);
        }
    }
}
