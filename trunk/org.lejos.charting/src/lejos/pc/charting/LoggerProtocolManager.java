package lejos.pc.charting;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.HashSet;

import lejos.util.EndianTools;

/**
 * This class provides the communications protocol manager for receiving and processing messages from the
 * <code>lejos.util.NXTDataLogger</code> class. It uses an event model for notifications of the events specified
 * in <code>LoggerListener</code>.
 * 
 * @see LoggerComms
 * @see LoggerListener
 * @see lejos.util.NXTDataLogger 
 * @author Kirk P. Thompson
 */
@SuppressWarnings("javadoc")
public class LoggerProtocolManager {
    private static final byte ATTENTION1 = (byte)0xff;
    private static final byte ATTENTION2 = (byte)0xab;
//    private static final byte COMMAND_ITEMSPERLINE = 0;
    private static final byte COMMAND_DATATYPE     = 1;    
    // sub-commands of COMMAND_DATATYPE
    private static final byte    DT_BOOLEAN = 0;
    private static final byte    DT_BYTE    = 1;
    private static final byte    DT_SHORT   = 2;
    private static final byte    DT_INTEGER = 3;        
    private static final byte    DT_LONG    = 4;
    private static final byte    DT_FLOAT   = 5;
    private static final byte    DT_DOUBLE  = 6;
    private static final byte    DT_STRING  = 7;
    private static final byte COMMAND_SETHEADERS   = 2;  
    private static final byte COMMAND_FLUSH        = 3; 
    private static final byte COMMAND_COMMENT      = 4;   
    private static final byte COMMAND_PASSTHROUGH  = 5;   
    
    private final String THISCLASS;
    private HashSet<LoggerListener> listeners = new HashSet<LoggerListener>();
    private int elementsPerLine = 1;
    private InputStream nXTInputStream;
    private OutputStream nXTOutputStream;
	private volatile boolean pauseInput;
    
    /**
     * Create a <code>LoggerProtocolManager</code> instance. 
     * <P>
     * You must register a <code>LoggerListener</code> to 
     * receive logging events.
     * The connection must already be established and the passed <code>InputStream</code> and <code>OutputStream</code>
     * are valid or <code>IOException</code> is thrown.
     * 
     * @param is The established connection's <code>InputStream</code> from the NXT
     * @see LoggerComms
     * @see #startListen
     * @see #addLoggerListener
     * @throws IOException If the passed input or output stream is null 
     */
    public LoggerProtocolManager(InputStream is, OutputStream os) throws IOException {
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
        if (is==null) {
            throw new IOException("lejos.pc.charting.LoggerComms InputStream is null");
        }
        if (os==null) {
            throw new IOException("lejos.pc.charting.LoggerComms OutputStream is null");
        }
        this.nXTInputStream=is;
        this.nXTOutputStream=os;
    }

    private void dbg(String msg){
        System.out.println(THISCLASS + "-" + msg);
    }
    
    /**
     * Register a <code>LoggerListener</code> so data can be managed and acted upon when it is received from the NXT.
     * @param listener The <code>LoggerListener</code> instance to register
     * @see LoggerListener
     * @see #removeLoggerListener
     */
    public synchronized void addLoggerListener(LoggerListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove a logger listener.
     * @param listener The <code>LoggerListener</code> instance to de-register
     * @return <code>true</code> if passed <code>listener</code> was removed. <code>false</code> if passed 
     * <code>listener</code> was not registered to begin with.
     * @see LoggerListener
     * @see #addLoggerListener
     */
    public synchronized boolean removeLoggerListener(LoggerListener listener) {
        return this.listeners.remove(listener);
    }

    /** 
     * Check if ATTENTION bytes and 3rd byte meet checksum. If so, we're pretty sure this isn't data but is 
     * a command of some sort.
     * @param ba 4 bytes read from dis
     * @return true if an ATTENTION signal
     */
    private boolean isATTN(byte[] ba){
        if (!(ba[0]==ATTENTION1 && ba[1]==ATTENTION2)) return false;
        final int XORMASK = 0xff;
        int total=0;
        for (int i=0;i<4;i++) total+=ba[i];
        if (((total^XORMASK)&0xff)==0) return true;
        return false;
    }
    
    boolean isReaderPaused() {
    	return this.pauseInput;
    }
    
    synchronized void setReaderPaused(boolean doPause){
    	if (this.pauseInput && !doPause) {
    		this.pauseInput=doPause;
    		this.notify();
    		return;
    	}
    	this.pauseInput=doPause;
    }
    /** 
     * Start listening for and processing logging data. After the NXT closes the connection (i.e. on EOF or other 
     * <code>IOException</code>), 
     * the <code>dataInputStreamEOF()</code> method is 
     * invoked on registered <code>LoggerListener</code>s, the logging session ends, and this instance is no longer
     * connected. A new instance must be created to log again.
     * @throws IOException if connection has not been established
     * @see LoggerListener
     */
    public void startListen() throws IOException { 
        int endOfLineCycler=0;
        byte[] readBytes = new byte[4];
        boolean isCommand;
        byte streamedDataType = DT_INTEGER;  // also must be set as initial default in lejo.util.NXTDataLogger
        byte[] tempBytes;
        DataItem[] readVals = new DataItem[this.elementsPerLine];
        
        if (this.nXTInputStream==null) {
            throw new IOException("Null InputStream in startLogging()!");
        }
        this.pauseInput=false;
        mainloop:
        while (true) {
            // get 4 bytes from the is
            try {
                getBytes(readBytes,4);
            } catch (EOFException e) {
                dbg("startLogging(): EOFException in getBytes(4): " + e);
                break;
            }
            
            // do we need to pay attention?
            isCommand = isATTN(readBytes);
            // if we have an ATTENTION request (4 bytes)...
            if (isCommand) {
                // get the next 2 [command] bytes from the is
                try {
                    getBytes(readBytes,2);
                } catch (EOFException e) {
                    dbg("startLogging(): EOFException in getBytes(command bytes): " + e);
                    break;
                }
                // do valid commands
                switch(readBytes[0]) {
                    case COMMAND_DATATYPE:
                        streamedDataType = readBytes[1];
                        break;
                    case COMMAND_SETHEADERS:
                        this.elementsPerLine=readBytes[1]&0xff;
                        // if we have residual, output it
                        if (endOfLineCycler>0) notifyLogLineAvailable(readVals); // send readvals[] to output
                        // get the headers from the stream
                        String[] fieldNames = new String[this.elementsPerLine];
                        for (int i=0;i<this.elementsPerLine;i++){
                            try {
                                getBytes(readBytes,4);  // preload for parseString() 
                                fieldNames[i]=this.parseString(readBytes);
                            } catch (EOFException e){
                                break mainloop;
                            }
                        }
                        // notify all listeners of new header label event
                        notifyHeaderChange(fieldNames);
                        
                        endOfLineCycler = 0;
                        readVals = new DataItem[this.elementsPerLine]; // set to new bounds. 
                        break;
                    case COMMAND_FLUSH:
                        if (endOfLineCycler>0) notifyLogLineAvailable(readVals); // send readvals[] to output
                        endOfLineCycler = 0;
                        readVals = new DataItem[this.elementsPerLine]; // init the row holding array
                        break;
                    case COMMAND_COMMENT:
                        try {
                            // get the timestamp
                            getBytes(readBytes,4);
                            int timestamp = LoggerProtocolManager.parseInt(readBytes);
                            
                            // get the comment
                            getBytes(readBytes,4);  // preload for parseString() 
                            String comment=this.parseString(readBytes);
                            
                            // notify all listeners of new comment
                            this.notifyCommentRecieved(timestamp, comment);
                        } catch (EOFException e){
                            break mainloop;
                        }
                        break;
                    case COMMAND_PASSTHROUGH:
                        try {
                            // get the number of bytes in the passthrough message
                            getBytes(readBytes,4);
                            int followingByteCount = LoggerProtocolManager.parseInt(readBytes);
                            // create and fill an array with the specified number of bytes
                            tempBytes = new byte[followingByteCount];
                            getBytes(tempBytes, followingByteCount);
                            // pass the tempBytes array through registered callback 
                            this.notifyPassthrough(tempBytes);
                        } catch (EOFException e){
                            break mainloop;
                        }
                        break;
                    default:
                        // allow the bytes to pass through to datatype parsers if no CASE matches (this should not happen but...)
                        isCommand = false;
                }
            }
            
            // If not a command, output the data
            if (!isCommand) {
                switch(streamedDataType) {
                    case DT_BOOLEAN:
                    case DT_BYTE:
                    case DT_SHORT:
                    case DT_INTEGER:
                    case DT_FLOAT:
                        // Parse an int from the 4 bytes
                        readVals[endOfLineCycler] = new DataItem();
                        readVals[endOfLineCycler].value = new Integer(LoggerProtocolManager.parseInt(readBytes));
                        if (streamedDataType==DT_FLOAT) {
                        	readVals[endOfLineCycler].value = new Float(LoggerProtocolManager.parseFloat(readBytes));
                        } else {
                        	readVals[endOfLineCycler].value = new Integer(LoggerProtocolManager.parseInt(readBytes));
                        }
                        readVals[endOfLineCycler].datatype=streamedDataType;
                        break;
                    case DT_LONG:
                    case DT_DOUBLE:
                        // Parse a long or double from the 4 + 4 more bytes
                        readVals[endOfLineCycler] = new DataItem();
                        tempBytes = new byte[8];
                        System.arraycopy(readBytes,0,tempBytes,0,4);
                        try {
                            getBytes(readBytes,4);
                        } catch (EOFException e){
                            break mainloop;
                        }
                        System.arraycopy(readBytes,0,tempBytes,4,4);
                        if (streamedDataType==DT_LONG) {
                        	readVals[endOfLineCycler].value = new Long(LoggerProtocolManager.parseLong(tempBytes));
                        } else {
                        	readVals[endOfLineCycler].value = new Double(LoggerProtocolManager.parseDouble(tempBytes));
                        }
                        readVals[endOfLineCycler].datatype=streamedDataType;
                        break;
                    case DT_STRING:
                        readVals[endOfLineCycler] = new DataItem();
                        try {
                            readVals[endOfLineCycler].value = new String(this.parseString(readBytes));
                        } catch (EOFException e){
                            break mainloop;
                        }
                        readVals[endOfLineCycler].datatype=streamedDataType;
                        break;
                    default:
                        dbg("!** Invalid streamedDataType:" + streamedDataType);
                }
                // if we have cycled through enough items, do EOL and output the entire line as a formatted string
                if (endOfLineCycler == this.elementsPerLine - 1) {
                    // send readvals[] to output and reset endOfLineCycler, readVals[]
                    notifyLogLineAvailable(readVals); 
                    endOfLineCycler = 0;
                    readVals = new DataItem[this.elementsPerLine]; // set to new bounds. 
                } else
                    endOfLineCycler++;
            } // END BLOCK: If not a command, output the data
        } // END BLOCK: while
        
        // notify all listeners of EOF event and clear the listener list. This is [basically] the end of life for this instance
        notifyISEOF();
    }
    
    private static final DecimalFormat integerFormat = new DecimalFormat("0");
    private static final DecimalFormat floatFormat = new DecimalFormat("0.0############E0");
    private static final DecimalFormat doubleFormat = new DecimalFormat("0.0########################E0");
    
    private static String formatByDataType(int datatype, Object value){
        switch (datatype) {
            case DT_BOOLEAN:
            	return ((Number)value).intValue() == 0 ? "false" : "true";
            case DT_BYTE:
            case DT_SHORT:
            case DT_INTEGER:
            case DT_LONG:
                return integerFormat.format(((Number)value).longValue());
            case DT_FLOAT:
                return floatFormat.format(((Number)value).floatValue());
            case DT_DOUBLE:
                return doubleFormat.format(((Number)value).doubleValue());
            case DT_STRING:
                return String.valueOf(value);
            default:
                throw new RuntimeException("unknown data type "+datatype);
        }
    }

    /** 
     * Parse an array of <code>DataItem</code>s and return a formatted string suitable for logging
     * @param logDataItems
     * @return A formatted string representation of the <code>DataItem</code>s
     * @see DataItem
     */
    public static final String parseLogData(DataItem[] logDataItems){
        StringBuilder logLineBuilder = new StringBuilder();
        for (int i = 0; i < logDataItems.length; i++) {
            if (logDataItems[i]==null) continue;
            String value = formatByDataType(logDataItems[i].datatype, logDataItems[i].value).trim();
            logLineBuilder.append(value);
            if (i < logDataItems.length - 1)
                logLineBuilder.append("\t");
        }
        logLineBuilder.append("\n"); 
        return logLineBuilder.toString();
    }
    
    /**
     * Send readvals[] to output
     * @param readVals
     */
    private synchronized void notifyLogLineAvailable(DataItem[] readVals) {
        // notify all listeners that a new line of data fields is available
        for (LoggerListener listener: this.listeners) {
            listener.logLineAvailable(readVals);
        }
        
        if (this.pauseInput) {
    		while (this.pauseInput) {
    			try {
    				this.wait();
    			} catch (InterruptedException e){
    				// DO NOTHING
    			}
    		}
        }
    }
    
    /**
     * Notify of comment event
     * @param timeStamp
     * @param comment
     */
    private void notifyCommentRecieved(int timeStamp, String comment) {
        // notify all listeners that a new comment is available
        for (LoggerListener listener: this.listeners) {
            listener.logCommentReceived(timeStamp, comment);
        }
    }

    /**
     * Notify of headers change
     * @param fieldNames
     */
    private void notifyHeaderChange(String[] fieldNames) {
        // notify all listeners of new header label event
        for (LoggerListener listener: this.listeners) {
            listener.logFieldNamesChanged(fieldNames);
        }
    }
    
    /**
     * Notify all listeners of EOF event and clear the listener list. This is [basically] the end of life for this instance
     */
    private void notifyISEOF() {
        for (LoggerListener listener: this.listeners) {
            listener.dataInputStreamEOF();
        }
        dbg("clearing all listeners");
        this.listeners.clear();
    }
    
    private void notifyPassthrough(byte[] message){
        // pass the bytes back through the registered callback         
         for (LoggerListener listener: this.listeners) {
             listener.tunneledMessageReceived(message);
         }
    }
    
    @SuppressWarnings({ "unused", "boxing" })
	private void hexByteOut(String desc, long value, int bits) {
//        value = value & 0xffffffff;
        StringBuilder sb1 = new StringBuilder(bits-bits/8-1);
        int pow=0;
        for (int i=bits-1;i>=0;i--) {
            pow= (int)Math.pow(2,i);
            if((pow&value)==pow) sb1.append("1"); else sb1.append("0");
           if(i%8==0)sb1.append(" ");
        }
        //long BinValReprsntn = Long.valueOf(strBinary);
        System.out.format("%3$7s: %1$ 9d 0x%1$0" + bits/4 + "x: %2$s\n" , value, sb1.toString(), desc);
    }
    
    private void getBytes(byte[] readBytes, int byteCount) throws EOFException
    {
        // Get byteCount bytes from the buffer.
        int readVal=-1;
        for (int i=0;i<byteCount;i++) {
            try {
                readVal=this.nXTInputStream.read();
                if (readVal==-1) throw new EOFException();
                readBytes[i]=(byte)readVal;
            } catch (IOException e) {
                throw new EOFException("getBytes: is.read(): " + e);
            }
        }
    }
    
    /**
     * big endian
     * @param ba The 4 byte array to convert to an int
     * @return The integer value of the 4 bytes
     */
    public static final int parseInt(byte[] ba) {   
    	return EndianTools.decodeIntBE(ba, 0);
    }
    
    public static final long parseLong(byte[] ba) {   
    	return EndianTools.decodeLongBE(ba, 0);
    }
    
    public static final float parseFloat(byte[] ba) {
       return Float.intBitsToFloat(parseInt(ba));
    }
    
    public static final double parseDouble(byte[] ba) {
       return Double.longBitsToDouble(parseLong(ba));
    }
    
    private final String parseString(byte[] ba) throws EOFException{
        byte[] tempBytes = new byte[4];
        StringBuilder sb = new StringBuilder();
        
        tempBytes = ba;   
        // get all the next bytes (chunks of 4) up until the first null (0) to make the string. 
        outerloop: 
        for(;;) {
            for(int i=0;i<4;i++) {
                if (tempBytes[i]==0) break outerloop;
                sb = sb.append(new String(tempBytes, i, 1));
            }
            getBytes(tempBytes,4);
        }
        
        return sb.toString();
    }
}
