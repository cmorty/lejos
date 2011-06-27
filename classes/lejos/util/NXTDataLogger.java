package lejos.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.InputStream;

import java.io.OutputStream;

import java.util.ArrayList;

import lejos.nxt.Button;
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
    // sub-commands of COMMAND_DATATYPE
    private final byte    DT_BOOLEAN = 0;
    private final byte    DT_BYTE    = 1;
    private final byte    DT_SHORT   = 2;
    private final byte    DT_INTEGER = 3;        
    private final byte    DT_LONG    = 4;
    private final byte    DT_FLOAT   = 5;
    private final byte    DT_DOUBLE  = 6;
    private final byte    DT_STRING  = 7;
    private final byte COMMAND_SETHEADERS   = 2;  
    private final byte COMMAND_FLUSH        = 3;    
    
    private DataOutputStream dos = null;
    private DataOutputStream dosReal = null;
    private DataInputStream dis = null;
//    private boolean isConnected = false;
//    private static NXTConnection theConnection=null; // polymorphism example with abstract class as type
    private byte currentDataType = DT_INTEGER;
    private byte itemsPerLine=1;
    private int currColumnPosition=1;
    private int flushBytes=0;
//    private String[] headerLabels=null;
    private LogColumn[] columnDefs=null;
    private ArrayList<Float> logCache = new ArrayList<Float>();
    private ArrayList<Byte> byteCache = new ArrayList<Byte>(512);
    
    // logging mode state management
    private final int LMSTATE_UNINIT=0;
    private final int LMSTATE_CACHE=1;
    private final int LMSTATE_REAL=2;
    private int logmodeState = LMSTATE_UNINIT; // 0=uninititalized, 1=startCachingLog, 2=startRealtimeLog, 
    private boolean headersSet = false;
    private boolean disableWriteState=false;
    
    /**
     * Establish a data logger instance
     */
    public NXTDataLogger() {

    }

    /** caching dos. uses write(int) override to grab all encoded bytes from DataOutputStream which we cache to "play"
     * to receiver. Signaling protocol bytes are not saved here as they don't need to be since we know the row structure
     * (i.e. datatypes and their position in the row) from setColumns()
     */
    class CacheOutputStream extends OutputStream {
        private long byteCount=0;
        @Override
        public void write(int b) throws IOException {
//            System.out.println("b=" + b);
//            Byte bval = new Byte((byte)(b&0xff));
//            byteCache.add(bval);
//            LCD.drawString(""+byteCount,0,2);
            try {
                dosReal.write(b);
            } catch (IOException e) {
                closeConnection(dosReal);
            }
        }
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
    
    // Starts realtime logging. Must be called before any writeLog() methods. Resets startCachingLog() state
    // streams must be valid (not null)
    public void startRealtimeLog(DataOutputStream out, DataInputStream in) throws IOException{
        logmodeState=LMSTATE_REAL;
        if (out==null) throw new IOException("DataOutputStream is null");
        if (in==null) throw new IOException("DataInputStream is null");
        this.dos=out;
        this.dis=in;
    } 
    // isConnected()=true and streams must be valid (not null)
    public void startRealtimeLog(NXTConnection connection) throws IOException{
        startRealtimeLog(connection.openDataOutputStream(), connection.openDataInputStream());
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
       // if (theConnection==null) return;
        if (!burstMode) {
            // Send ATTENTION request and remote FLUSH command
            sendATTN();
            byte[] command = {COMMAND_FLUSH,-1};
            sendCommand(command);
//            this.isConnected = false;
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
//        if (theConnection!=null&&!burstMode) {
//            theConnection.close();
//            theConnection=null;
//        }
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

    /** ensures that the System.currentTimeMillis() value is the first in every row. rows are based on header count. cyclic
     */
    private void checkTimeStamp(){
        if (this.currColumnPosition==1) {
            writeLog(System.currentTimeMillis());
        }
        this.currColumnPosition++;
        if (this.currColumnPosition>=(((int)this.itemsPerLine)&0xff)) this.currColumnPosition=1;
    }
   
    private void checkWriteState(int datatype){
        // TODO add LMSTATE_CACHE handling
        if (this.disableWriteState) return;
        if (logmodeState==LMSTATE_UNINIT) throw new IllegalStateException("mode not set ");
        if(!headersSet) throw new IllegalStateException("columnns not set ");
        if (datatype!=columnDefs[currColumnPosition].getDatatype()) throw new IllegalStateException("datatype mismatch ");
        this.disableWriteState=true; //avoid infinite recursion
        // ensure 1st item is timestamp
        checkTimeStamp();
        // if DT needs to be changed, signal receiver
        if (currentDataType != datatype) setDataType((byte)(datatype&0xff));
        this.disableWriteState=false;
    }

    /** send the command to set the active datatype
     * @param datatype
     */
    private void setDataType(byte datatype) {
        byte[] command = {COMMAND_DATATYPE,-1};        
        sendATTN();
        this.currentDataType = datatype;
        command[1] = datatype;        
        sendCommand(command);
    }
    
    private void setItemsPerLine(int itemsPerLine){
       sendATTN();
       this.itemsPerLine= (byte)(itemsPerLine&0xff);
       byte[] command = {COMMAND_ITEMSPERLINE,-1};
       command[1] = this.itemsPerLine;
       sendCommand(command);
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

    
    // TODO firm up comments
    /** 
      * Write an <code>int</code> to the log. Position in the log row is important.
      * 
      * @param datapoint The <code>int</code> value to log.
      * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
      * the datatype that was set in <code>setColumns()</code>, the column position exceeds the column number, or the column
      * definitions have not been set.
      * 
      * @see #setColumns
      * @see #finishLine
      */
    public void writeLog(int datapoint) {
        checkWriteState(DT_INTEGER);
        try {
            this.dos.writeInt(datapoint);
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
     * 
     */
 
    
    public void writeLog(long datapoint) {
        checkWriteState(DT_LONG);  
        try {
            this.dos.writeLong(datapoint);
            checkFlush(8);
        } catch (IOException e) {
            closeConnection(this.dos);
        }
    }
    
     /**
      * Write an <code>float</code> to the log. If there is no active connection, the method returns immediately
      * without throwing any exception.
      * 
      * @param datapoint The <code>float</code> value to log
      * 
      */
    public void writeLog(float datapoint) {
        checkWriteState(DT_FLOAT);
        try {
//            LCD.drawString("this.dos " + value + " ",0,1);
            this.dos.writeFloat(datapoint);
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
    public void writeLog(double datapoint) {
        checkWriteState(DT_DOUBLE);
        try {
    //            LCD.drawString("this.dos " + value + " ",0,1);
            this.dos.writeDouble(datapoint);
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
        // TODO maybe checkWriteState(DT_STRING);?
        if (currentDataType != DT_STRING) setDataType(DT_STRING); 
        writeStringData(strData);
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
        checkWriteState(DT_STRING);
        writeStringData(strData);
    }
    
    private void writeStringData(String strData) {
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
      * @see ColumnDefinition
      */
    // sets the header names, datatypes, count, chartable attribute, range axis ID (for multiple axis charting)
    // This is mandatory and implies a new log structure when called
    
    public void setColumns(LogColumn[] columnDefs){
        if (columnDefs.length==0) return;
        if (columnDefs.length>255) return;
        LogColumn[] tempColumnDefs = new LogColumn[columnDefs.length+1];
        tempColumnDefs[0] = new LogColumn("System_ms", LogColumn.DT_LONG, true, 1);
        System.arraycopy(columnDefs, 0, tempColumnDefs, 1, columnDefs.length);
        this.columnDefs = tempColumnDefs;
        headersSet=true;
        sendHeaders(); 
    }
    
    private void sendHeaders(){
        // TODO implement cache mode and check pos?
        this.currColumnPosition=1;
        byte[] command = {COMMAND_SETHEADERS,0};        
        sendATTN();
        command[1] = (byte)(this.columnDefs.length&0xff); 
        this.itemsPerLine = command[1];
        sendCommand(command);        
        for (LogColumn item: columnDefs){
            writeStringData(item.getName());
            System.out.println(item.getName());
        }
        Button.waitForPress();
    }
}
