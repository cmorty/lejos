package net.mosen.nxt.instrumentation;

import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;

import lejos.util.Delay;

import net.mosen.nxt.BTManager;

/**
 * Logger class for the NXT side of things. Bluetooth on the NXT manage data buffering so I don't do any buffering
 * threads, etc. Maybe I need to for USB though...
 * <p>
 * I have noticed that USB is dropping data 
 * @author Kirk P. Thompson
 */
public class NXTDataLogger implements BTLogger {
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
    
    private DataOutputStream dos = null;
    private NXTConnectionManager bTManager = null;
    
    private byte currentDataType = DT_INTEGER;
    private byte itemsPerLine=1;
    private boolean immediateFlush=false;
    private int timeStampCycler=1;

//    /**
//     * convenience constructor. creates a new <code>BTManager</code> instance and waits 5 secs for a connection
//     * @see #getBTManager
//     */
//    public NXTDataLogger(){
//        this(new BTManager(), 5000);
//    }

    /**
     * TODO fix these comments
     * Pass a <code>NXTConnectionManager</code> instance and the connection timeout (0=wait forever). If the
     * <code>NXTConnectionManager</code> is already connected, the <code>timeout</code> value is ignored.
     * <p>
     * <code>IOException</code> is thrown if connection attempt fails
     * @param bTManager The <code>NXTConnectionManager</code> instance, already connected or not
     * @param timeout The timeout in seconds to wait for a connection (if not already connected)
     * @throws IOException
     */
    public NXTDataLogger(NXTConnectionManager bTManager) {
        if (!bTManager.isConnected()) return;
        
        this.bTManager = bTManager;
        if (!bTManager.isConnected()) {
//            byte[] theDisplay = LCD.getDisplay();
//            LCD.clearDisplay();
//            LCD.drawString("Connect Wait..", 0, 7, true);
//            this.bTManager.waitForBTConnection(timeout);
            
//            try {
//                this.bTManager.waitForBTConnection(timeout);
//            } catch (IOException e) {
//                // TODO
//                 LCD.drawString("connection err", 0, 7);
//                LCD.drawString(e.toString(), 0, 6);
//                 Button.ENTER.waitForPress();
//            }
            LCD.drawString("                ", 0, 7);
// LCD.bitBlt();
        }
        dos = bTManager.getDataOutputStream();
    }
    
     
    public void setImmediateFlush(boolean immediateFlush){
        this.immediateFlush=immediateFlush; 
    }

    /** lower level data sending
     * @param command teh bytes[] to send
     */
    private final synchronized void sendCommand(byte[] command){
        if (dos==null) return;
        try {
            dos.write(command);
        } catch (IOException e) {
            // TODO clean this up after testing
             LCD.drawString("20! " + e.toString(), 0, 7);
             Button.waitForPress();
        }
    }
    
    private void sendATTN(){
        final int XORMASK = 0xff;
        byte[] command = {ATTENTION1,ATTENTION2,0,0};  
        int total=0;
        // add the random verifier byte
        command[2] = (byte)((int)(Math.random()*255)&0xff);        
        for (int i=0;i<3;i++) total+=command[i];
        // set the XORed checksum
        command[3]=(byte)((total^XORMASK)&0xff);
        sendCommand(command);
    }
    
    private void checkTimeStamp(){
        if (timeStampCycler==1) writeLong(System.currentTimeMillis(),false);
        timeStampCycler++;
        if (timeStampCycler>=(((int)itemsPerLine)&0xff)) timeStampCycler=1;
//        if (timeStampCycler>10) timeStampCycler=1;
    }
    
    private void setDataType(byte datatype) {
        byte[] command = {COMMAND_DATATYPE,-1};        
        sendATTN();
        this.currentDataType = datatype;
        command[1] = datatype;        
        sendCommand(command);
    }
    
    /**
     * As specified by BTLogger. 
     * @param value
     */
    public synchronized void logInt(int value) {
        if (dos == null) return;        
        checkTimeStamp();
        if (currentDataType != DT_INTEGER) setDataType(DT_INTEGER);        
        try {
            dos.writeInt(value);
            if (immediateFlush) dos.flush();
        } catch (IOException e) {
            // TODO clean this up after testing
            LCD.drawString("1! " + e.toString(), 0, 7);
            Button.waitForPress();
        }
    }
    
    /**
     * As specified by BTLogger. 
     * @param value
     */
    public synchronized void logLong(long value) {
        writeLong(value, true);
    }
    
    private synchronized void writeLong(long value, boolean doTimeStampCheck) {
        if (dos == null) return;    
        if (doTimeStampCheck) checkTimeStamp();
        if (currentDataType != DT_LONG) setDataType(DT_LONG);        
        try {
            dos.writeLong(value);
            if (immediateFlush) dos.flush();
        } catch (IOException e) {
            // TODO clean this up after testing
            LCD.drawString("1.5!" + e.toString(), 0, 7);
            Button.waitForPress();
        }
    }
    
    /**
     * As specified by BTLogger. 
     * @param value
     */
    public synchronized void logFloat(float value) {
        if (dos == null) {
            LCD.drawString("null dos wFloat", 0, 7);
            return;        
        }
        checkTimeStamp();
        if (currentDataType != DT_FLOAT) setDataType(DT_FLOAT);        
        try {
//            LCD.drawString("dos " + value + " ",0,1);
            dos.writeFloat(value);
            if (immediateFlush) dos.flush();
        } catch (IOException e) {
            // TODO clean this up after testing
            LCD.drawString("2! " + e.toString(), 0, 7);
            Button.waitForPress();
        }
    }
    
    /**
     * As specified by BTLogger. 
     * @param value
     */
    public synchronized void logDouble(double value) {
        if (dos == null) {
            LCD.drawString("null dos wDoub", 0, 7);
            return;        
        }
        checkTimeStamp();
        if (currentDataType != DT_DOUBLE) setDataType(DT_DOUBLE);        
        try {
    //            LCD.drawString("dos " + value + " ",0,1);
            dos.writeDouble(value);
            if (immediateFlush) dos.flush();
        } catch (IOException e) {
            // TODO clean this up after testing
            LCD.drawString("2.5! " + e.toString(), 0, 7);
            Button.waitForPress();
        }
    }
    
    public final synchronized void writeStringLine(String strData){
        byte oldIPL = itemsPerLine;
        if (itemsPerLine!=1) setItemsPerLine((byte)1);
        // skip the [potential] timestamp
        if (currentDataType != DT_STRING) setDataType(DT_STRING); 
        writeString(strData, false);
        if (itemsPerLine!=1) setItemsPerLine(oldIPL);
    }
    
    /**
     * As specified by BTLogger. 
     * @param strData
     */
    public final synchronized void logString(String strData) {
        writeString(strData,true);
    }
    
    private void writeString(String strData, boolean executeNormal) {
        if (dos==null) return;
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
            if (immediateFlush) dos.flush();
        } catch (IOException e) {
             // TODO clean this up after testing
             LCD.drawString("3! " + e.toString(), 0, 7);
             Button.waitForPress();
        }
    }
    
    /**
     * As specified by BTLogger
     */
    public void closeConnection() {
        sendATTN();
        byte[] command = {COMMAND_FLUSH,-1};
        sendCommand(command);
        
        bTManager.closeConnection();
    }

    
     private void setItemsPerLine(int itemsPerLine){
        sendATTN();
        this.itemsPerLine= (byte)(itemsPerLine&0xff);
        byte[] command = {COMMAND_ITEMSPERLINE,-1};
        command[1] = this.itemsPerLine;
        sendCommand(command);
    }
    
    
    /**
     * As specified by BTLogger. If length of the passed array is zero, nothing is done. If length > 255, nothhing is done.
     * @param headerLabels
     */
    public void setHeaders(String[] headerLabels) {
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

    /**
     * Returns the class <code>BTManager</code> instance ref whether passed into a constructor or created by this class using
     * a convenience constructor.
     * @return <code>BTManager</code> class instance reference
     * @see #NXTDataLogger()
     */
    //    public BTManager getBTManager() {
    //        return bTManager;
    //    }

     /**
      * As specified by BTLogger
      */
    public void flush() {
        try {
            dos.flush();
        } catch (IOException e) {
            // TODO
            ; //do nothing
        }
    }
}
