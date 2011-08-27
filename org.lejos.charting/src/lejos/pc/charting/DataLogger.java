package lejos.pc.charting;

import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashSet;


/**
 * This class provides the PC side of the <code>lejos.util.NXTDataLogger</code> counterpart running on the NXT. 
 * One instance per log session. The session ends when 
 * the NXT ends the connection.
 * 
 * @see LoggerComms
 * @see lejos.util.NXTDataLogger 
 * @author Kirk P. Thompson
 */
public class DataLogger {
    private static final byte ATTENTION1 = (byte)0xff;
    private static final byte ATTENTION2 = (byte)0xab;
    private static final byte COMMAND_ITEMSPERLINE = 0;
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
    
    private final String THISCLASS;
    
    /** Change listener to notify of events when log data has been recieved, a data stream EOF has occurred, a comment has been received, and 
     * header defs have been changed by the NXT-side <code>lejos.util.NXTDataLogger</code>.
     * @see  #addLoggerListener
     * @author Kirk P. Thompson
     */
    public interface LoggerListener {
        /** Invoked when a log line [whose fields are defined in <code>NXTDataLogger.setColumns()</code>] is logged and 
         * <code>NXTDataLogger.finishLine()</code> is invoked.
         * <p>
         * Each logged data field/column is represented by a 
         * <code>DataItem</code> instance which provides the datatype and value in a wrapper class.
         * @param logDataItems The array of <code>DataItem</code> instances representing a line of logged data.
         * @see DataItem
         * @see lejos.util.NXTDataLogger
         */
        void logLineAvailable(DataItem[] logDataItems);

        /**Invoked when an <code>EOFException</code> (or other <code>IOException</code>) occurs from the <code>LoggerComms</code>
         * instance.
         * @see LoggerComms
         */
        void dataInputStreamEOF();

        /** Invoked when the log field headers are initially set or changed. This is important because the number of headers
         *  determines the column count (which affects cycling).
         *  <p>
         *  The string format/structure of each string field passed by NXTDataLogger is:<br>
         *  <code>[name]![y or n to indicate if charted]![axis ID 1-4]</code>
         *  <br>i.e. <pre>"MySeries!y!1"</pre>
         * @param logFields The array of header values
         * @see lejos.util.LogColumn
         */
        void logFieldNamesChanged(String[] logFields);

        /** Invoked when a comment is logged. Comments are sent after the <code>finishLine()</code> method completes. In 
         * <code>NXTChartingLogger</code>, comments are displayed on the chart as an event marker on the domain axis with 
         * the comment text as a label and the timestamp as the domain (X) value of the marker.
         * @param timestamp The timestamp when the comment was generated on the NXT
         * @param comment The text comment
         */
        void logCommentReceived(int timestamp, String comment);
    }

    /** Internal Logger implementation 'cause we must eat our own dogfood too...
     */
    private class Self_Logger implements LoggerListener{
        public void logLineAvailable(DataItem[] logDataItems){
            if (!validLogFile) return; 
            
            try {
                DataLogger.this.fw.write(parseLogData(logDataItems));
            } catch (IOException e) {
                System.out.print("!** logLineAvailableEvent IOException");
                e.printStackTrace();
            }
        }

        public void dataInputStreamEOF() {
            dbg("!** dataInputStreamEOF from NXT");
            try {
                if (DataLogger.this.fw!=null) DataLogger.this.fw.close();
                DataLogger.this.fw=null;
            } catch (IOException e) {
                System.out.print("!** dataInputStreamEOF IOException in fw.close()");
                e.printStackTrace();
            }
            
        }

        public void logFieldNamesChanged(String[] logFields) {
            if (!validLogFile) return;
            
            StringBuilder sb = new StringBuilder();
            String[] tempFields;
            dbg("!** New headers");
            for (int i=0;i<logFields.length;i++) {
                tempFields=logFields[i].split("!");
                sb.append(tempFields[0]);
                if (i<logFields.length-1) sb.append("\t");
            }
            sb.append("\n");
            // write to file
            try {
                DataLogger.this.fw.write(sb.toString());
            } catch (IOException e) {
                System.out.print("!** logFieldNamesChanged IOException: sb.toString()=\"" + sb.toString() + "\"");
                e.printStackTrace();
            }
        }

        public void logCommentReceived(int timestamp, String comment) {
            if (!validLogFile) return;
            
            String theComment=String.format("%1$1d\t%2$s\n", timestamp, comment);
            try {
                DataLogger.this.fw.write(theComment);
            } catch (IOException e) {
                System.out.print("!** logCommentRecieved IOException: theComment=\"" + theComment + "\"");
                e.printStackTrace();
            }
        }
    }

    private LoggerComms connectionManager = null;
    private File logFile = null;
    private FileWriter fw;
    private HashSet<LoggerListener> listeners = new HashSet<LoggerListener>();
    private boolean validLogFile=false;;
    private int elementsPerLine = 1;
    private boolean fileAppend;
    private InputStream cachedInputStream;
    
    /**Create a <code>DataLogger</code> instance. The passed passed <code>logfile</code> is opened and the logging output is written 
     * to it.<p>
     * Register a <code>DataLogger.LoggerListener</code> to 
     * receive logging events.
     * <code>LoggerComms</code>
     * connection must already be established or <code>IOException</code> is thrown.
     * @param connManager The <code>LoggerComms</code> instance to use for the connection.
     * @param logFile The log file <code>File</code> to write output to. 
     * @param fileAppend If <code>false</code>, the specified file will be overwritten if exists. 
     * @see LoggerComms
     * @see #startLogging
     * @see #addLoggerListener
     */
    public DataLogger(LoggerComms connManager, File logFile, boolean fileAppend) throws IOException {
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
        this.fileAppend=fileAppend;
        
        if (!connManager.isConnected()) {
            throw new IOException("lejos.pc.charting.LoggerComms is not connected");
        }
        
        this.connectionManager = connManager;
        this.logFile = logFile;
        validLogFile=(this.logFile!=null&&!this.logFile.isDirectory()); 
        // internal logger callback object
        addLoggerListener(new Self_Logger());
    }

    /** Create a <code>DataLogger</code> instance with no file output. Register a <code>DataLogger.LoggerListener</code> to 
     * receive logging events.
     * The connection must already be established or <code>IOException</code> is thrown.
     * @param connManager the instance to use for the connection
     * @see #DataLogger(LoggerComms, File, boolean)
     * @see DataLogger.LoggerListener
     * @see #addLoggerListener
     */
    public DataLogger(LoggerComms connManager) throws IOException {
        this(connManager,null,false);
    }

    private void dbg(String msg){
        System.out.println(THISCLASS + "-" + msg);
    }
    
    /**Register a Logger listener so data received from the NXT can be managed.
     * @param listener The Logger listener instance to register
     * @see LoggerListener
     * @see #removeLoggerListener
     */
    public void addLoggerListener(LoggerListener listener) {
        this.listeners.add(listener);
    }

    /**Remove a logger listener.
     * @param listener The <code>LoggerListener</code> instance to de-register
     * @return <code>true</code> if passed <code>listener</code> was removed. <code>false</code> if passed 
     * <code>listener</code> was not registered to begin with.
     * @see DataLogger.LoggerListener
     * @see #addLoggerListener
     */
    public boolean removeLoggerListener(LoggerListener listener) {
        return this.listeners.remove(listener);
    }
    

    private void doWait(int sleepval) {
        try {
            Thread.sleep(sleepval);
        } catch (InterruptedException e) {
            ; // do nothing
        }
    }

    /** Check if ATTENTION bytes and 3rd byte meet checksum. If so, we're pretty sure this isn't data but is 
     * a command of some sort.
     * @param ba 4 bytes read from dis
     * @return true if a ATTENTION signal
     */
    private boolean isATTN(byte[] ba){
        if (!(ba[0]==ATTENTION1 && ba[1]==ATTENTION2)) return false;
        final int XORMASK = 0xff;
        int total=0;
        for (int i=0;i<4;i++) total+=ba[i];
        if (((total^XORMASK)&0xff)==0) return true;
        return false;
    }
    
    /** Start the logging. After the NXT closes the connection (i.e. on EOF or other <code>IOException</code>), 
     * the <code>dataInputStreamEOF()</code> method is 
     * invoked on registered <code>LoggerListener</code>s, the logging session ends, and this instance is no longer
     * connected. A new instance must be created to log again.
     * @throws IOException if connection has not been established
     * @see LoggerListener
     */
    public void startLogging() throws IOException { 
        int endOfLineCycler=0;
        byte[] readBytes = new byte[4];
        boolean isCommand;
        byte streamedDataType = DT_INTEGER;  // also must be set as initial default in lejo.util.NXTDataLogger
        byte[] tempBytes;
        String FQPfileName=null;
        DataItem[] readVals = new DataItem[this.elementsPerLine];
        
        if (!this.connectionManager.isConnected()) {
            throw new IOException("No Connection in startLogging()!");
        }
        this.cachedInputStream = connectionManager.getInputStream();
        if (validLogFile) {
            try {
                FQPfileName = this.logFile.getCanonicalPath();
                dbg("log file is:" + FQPfileName);
                if (!this.logFile.exists())
                    this.logFile.createNewFile();
                this.fw = new FileWriter(this.logFile, this.fileAppend);
            } catch (IOException e) {
                dbg("startLogging(): IOException in creating file " + FQPfileName + ": " + e.toString());
                validLogFile=false;
            }
        }
        
        mainloop:
        while (true) {
            // get 4 bytes from the is
            try {
                getBytes(readBytes,4);
            } catch (EOFException e) {
                dbg("startLogging(): EOFException in getBytes(4): " + e);
                break;
            } catch (IOException e){
                dbg("startLogging(): IOException in getBytes(4): " + e);
                break;
            }
            // do we need to pay attention?
            isCommand = isATTN(readBytes);
            // if we have an ATTENTION request...
            if (isCommand) {
                // get the 2 command bytes from the PCBTManager
                try {
                    getBytes(readBytes,2);
                } catch (EOFException e) {
                    dbg("startLogging(): EOFException in getBytes(command bytes): " + e);
                    break;
                }
                // do valid commands
                switch(readBytes[0]) {
                    case COMMAND_ITEMSPERLINE: // byte 3=0: Set this.elementsPerLine to 4th byte
                        this.elementsPerLine = ((int)readBytes[1])&0xff;
                        // if we have residual, output it
                        if (endOfLineCycler>0) notifyLogLineAvailable(readVals); // send readvals[] to output
                        endOfLineCycler = 0;
                        readVals = new DataItem[this.elementsPerLine]; // set to new bounds. 
                        break;
                    case COMMAND_DATATYPE:
                        streamedDataType = readBytes[1];
                        //if (streamedDataType==DT_STRING); // TODO maybe temporarily set this.elementsPerLine to 1 here?
                        break;
                    case COMMAND_SETHEADERS:
                        this.elementsPerLine=((int)readBytes[1])&0xff;
                        // if we have residual, output it
                        if (endOfLineCycler>0) notifyLogLineAvailable(readVals); // send readvals[] to output
                        // get the headers from the stream
                        String[] fieldNames = new String[this.elementsPerLine];
                        for (int i=0;i<this.elementsPerLine;i++){
                            try {
                                getBytes(readBytes,4);
                                fieldNames[i]=this.parseString(readBytes);
                            } catch (EOFException e){
                                break mainloop;
                            }
                        }
                        // notify all listeners of new header label event
                        for (LoggerListener listener: this.listeners) {
                            listener.logFieldNamesChanged(fieldNames);
                        }
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
                            int timestamp = this.parseInt(readBytes);
                            
                            // get the comment
                            getBytes(readBytes,4);
                            String comment=this.parseString(readBytes);
                            
                            // notify all listeners of new comment
                            this.notifyCommentRecieved(timestamp, comment);
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
                        // Parse an int from the 4 bytes
                        readVals[endOfLineCycler] = new DataItem();
                        readVals[endOfLineCycler].value = new Integer(this.parseInt(readBytes));
                        readVals[endOfLineCycler].datatype=streamedDataType;
                        break;
                    case DT_LONG:
                        // Parse a long from the 4 + 4 more bytes
                        readVals[endOfLineCycler] = new DataItem();
                        tempBytes = new byte[8];
                        System.arraycopy(readBytes,0,tempBytes,0,4);
                        try {
                            getBytes(readBytes,4);
                        } catch (EOFException e){
                            break mainloop;
                        }
                        System.arraycopy(readBytes,0,tempBytes,4,4);
                        readVals[endOfLineCycler].value = new Long(this.parseLong(tempBytes));
                        readVals[endOfLineCycler].datatype=streamedDataType;
                        break;
                    case DT_FLOAT:
                        readVals[endOfLineCycler] = new DataItem();
                        readVals[endOfLineCycler].value = new Float(this.parseFloat(readBytes));
                        readVals[endOfLineCycler].datatype=streamedDataType;
                        break;
                    case DT_DOUBLE:
                        // Parse a long from the 4 + 4 more bytes
                        readVals[endOfLineCycler] = new DataItem();
                        tempBytes = new byte[8];
                        System.arraycopy(readBytes,0,tempBytes,0,4);
                        try {
                            getBytes(readBytes,4);
                        } catch (EOFException e){
                            break mainloop;
                        }
                        System.arraycopy(readBytes,0,tempBytes,4,4);
                        readVals[endOfLineCycler].value = new Double(this.parseDouble(tempBytes));
//                        dbg(this.parseLong(tempBytes));
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
        
        // notify all listeners of EOF event
        for (LoggerListener listener: this.listeners) {
            listener.dataInputStreamEOF();
        }
        dbg("clearing all listeners");
        this.listeners.clear();
    }
    
    private static String getDataTypeFormat(int datatype){
        switch (datatype) {
            case DT_BOOLEAN:
                return "1d";
            case DT_BYTE:
                return "-3d";
            case DT_SHORT:
                return "-5d";
            case DT_INTEGER:
                return "-16d";
            case DT_LONG:
                return "-24d";
            case DT_FLOAT:
                return "-16.8e";
            case DT_DOUBLE:
                return "-24.16e";
            case DT_STRING:
                return "s";
            default:
                return "-1d";
        }
    }

    /** Parse an array of <code>DataItem</code>s and return a formatted string suitable for logging
     * @param logDataItems
     * @return A formatted string representation of the <code>DataItem</code>s
     * @see DataItem
     */
    public static String parseLogData(DataItem[] logDataItems){
        StringBuilder logLineBuilder = new StringBuilder();
        for (int i = 0; i < logDataItems.length; i++) {
            if (logDataItems[i]==null) continue;
            logLineBuilder.append(String.format("%1$" + getDataTypeFormat(logDataItems[i].datatype), logDataItems[i].value).toString().trim());
            if (i < logDataItems.length - 1)
                logLineBuilder.append("\t");
        }
        logLineBuilder.append("\n"); 
        return logLineBuilder.toString();
    }
    
    /** send readvals[] to output
     */
    private void notifyLogLineAvailable(DataItem[] readVals) {
        // notify all listeners that a new line of data fields is available
        for (LoggerListener listener: this.listeners) {
            listener.logLineAvailable(readVals);
        }
    }
    
    /** Notify of comment event
     */
    private void notifyCommentRecieved(int timeStamp, String comment) {
        // notify all listeners that a new comment is available
        for (LoggerListener listener: this.listeners) {
            listener.logCommentReceived(timeStamp, comment);
        }
    }
    
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
        // wait until byteCount bytes are avail or we have a EOF

        // Get n bytes from the buffer. Null pointer if the poll() method in btmanager.getByte() has no data. 
        try {
            while (this.cachedInputStream.available() < byteCount) {
            doWait(50);
        }
        } catch (IOException e) {
            throw new EOFException("getBytes: is.available(): " + e);
        }
        
        for (int i=0;i<byteCount;i++) {
            try {
                readBytes[i]=(byte)(this.cachedInputStream.read()&0xff);
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
    private final int parseInt(byte[] ba) {   
        return  (ba[0] << 24) | 
                ((ba[1] & 0xFF) << 16) | 
                ((ba[2] & 0xFF) << 8) | 
                (ba[3] & 0xFF);
    }
    
    private final long parseLong(byte[] ba) {   
        return  ((long)ba[0] << 56) | 
                ((long)(ba[1] & 0xFF) << 48) | 
                ((long)(ba[2] & 0xFF) << 40) | 
                ((long)(ba[3] & 0xFF) << 32) |
                ((long)(ba[4] & 0xFF) << 24) |
                ((ba[5] & 0xFF) << 16) | 
                ((ba[6] & 0xFF) << 8)  | 
                 (ba[7] & 0xFF);
    }
    
    private final float parseFloat(byte[] ba) {
       return Float.intBitsToFloat(parseInt(ba));
    }
    
    private final Double parseDouble(byte[] ba) {
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
