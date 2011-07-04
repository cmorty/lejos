package lejos.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.EmptyQueueException;

import lejos.nxt.comm.NXTConnection;

import lejos.util.Delay;


/**
 * Logger class for the NXT that supports real time and deferred (cached) data logging of the primitive datatypes. 
 * <code>boolean</code>, <code>byte</code>, and <code>short</code> are all represented as [a 4 byte] <code>int</code>.
 * <p>
 * This class communicates with 
 * <code>lejos.pc.charting.DataLogger</code> via Bluetooth or USB which is used by the NXT Charting Logger tool or can be used 
 * stand-alone at a command prompt.
 * <p>When instantiated, the <code>NXTDataLogger</code> starts out in cached mode (<code>{@link #startCachingLog}</code>) as default
 * <p>Hints for real-time logging efficiency:
 * <ul>
 * <li>Try to keep your datatypes the same across <code>writeLog()</code> method calls to avoid the protocol
 * overhead that is incurred when 
 * switching datatypes. For instance, every time you change between <code>writeLog(int)</code> and <code>writeLog(long)</code>, a 
 * synchronization message must be sent to change the datatype on the receiver (<code>lejos.pc.charting.DataLogger</code>).
 * <li>Use the the <code>writeLog()</code> method with the smallest datatype that fits your data. Less data means better throughput overall.
 * </ul>
 * @author Kirk P. Thompson
 */
public class NXTDataLogger implements Logger{
    private final byte ATTENTION1 = (byte)(0xff&0xff);
    private final byte ATTENTION2 = (byte)(0xab&0xff);
//    private final byte COMMAND_ITEMSPERLINE = 0;
    private final byte COMMAND_DATATYPE     = 1;    
    // sub-commands of COMMAND_DATATYPE
//    private final byte    DT_BOOLEAN = 3;
//    private final byte    DT_BYTE    = 3;
//    private final byte    DT_SHORT   = 3;
    private final byte    DT_INTEGER = 3;        
    private final byte    DT_LONG    = 4;
    private final byte    DT_FLOAT   = 5;
    private final byte    DT_DOUBLE  = 6;
//    private final byte    DT_STRING  = 7;
    private final byte COMMAND_SETHEADERS   = 2;  
    private final byte COMMAND_FLUSH        = 3;    
    
    private DataOutputStream dos = null;
    private DataInputStream dis = null;
    private byte currentDataType = DT_INTEGER;  // also set as default in lejos.pc.charting.DataLogger.startLogging()
    private byte itemsPerLine=-1;
//    private int lineCount=0;
    private int currColumnPosition=1;
    private int flushBytes=0;
    private LogColumn[] columnDefs=null;
    private byteQueue byteCache = new byteQueue();
    private byte[] attentionRandoms = new byte[5];
    private int chksumSeedIndex=-1;
    
    // logging mode state management
    private final int LMSTATE_UNINIT=0;
    private final int LMSTATE_CACHE=1;
    private final int LMSTATE_REAL=2;
    private int logmodeState = LMSTATE_UNINIT; // 0=uninititalized, 1=startCachingLog, 2=startRealtimeLog, 
    private boolean disableWriteState=false;
    private int sessionBeginTime;
    private int setColumnsCount=0;
    /**
     * Default constructor establishes a data logger instance in cache mode.
     * @see #startCachingLog
     * @see #startRealtimeLog(NXTConnection)
     */
    public NXTDataLogger() {
        // seed some attention checkvals
        for (int i=0;i<attentionRandoms.length;i++){
            attentionRandoms[i]=(byte)((int)(Math.random()*255)&0xff);   
        }
        
        // start in caching mode
        startCachingLog();
    }
     
    /** caching dos. uses write(int) override to grab all encoded bytes from DataOutputStream which we cache to "play"
     * to receiver. Signaling protocol bytes are not saved here as they don't need to be since we know the row structure
     * (i.e. datatypes and their position in the row) from setColumns()
     */
    class CacheOutputStream extends OutputStream {
//        private long byteCount=0;
        @Override
        public void write(int b) throws IOException {
            try {
//                System.out.println("b=" + (b&0xff));
//                Button.waitForPress();
//                byteCache.push(new Byte((byte)(b&0xff)));
                byteCache.add((byte)(b&0xff));
            } catch (OutOfMemoryError e) {
//                System.out.println("bc=" + byteCount + " ");
//                System.out.println("lc=" + lineCount + " ");
//                Button.waitForPress();
                throw new IOException("OutOfMemoryError");
            }
//            byteCount++;
        }
    }
    
    private class byteQueue{
        private int addIndex=0;
        private int removeIndex=0;
        private byte[] barr;
        
        byteQueue() {
            barr=new byte[2048];
        }
        
        int add(byte b){
            barr[addIndex]=b;
            ensureCapacity(256);
            return addIndex++;
        }
        
        byte remove() throws EmptyQueueException {
            if (removeIndex>=addIndex) throw new EmptyQueueException();
            return barr[removeIndex++];
        }
        
        void ensureCapacity(int capacity) {
            if(addIndex > barr.length-2) {
                byte[] tb = new byte[barr.length+capacity];
                addIndex-=removeIndex;
                System.arraycopy(barr,removeIndex,tb,0,addIndex);
                removeIndex=0;
                barr=tb;
                tb=null;
            }
        }
    }
    
    private byte getChksumRandVal(){
        if (chksumSeedIndex>=attentionRandoms.length-1) chksumSeedIndex=-1;
        chksumSeedIndex++;
        return attentionRandoms[chksumSeedIndex];           
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
    /** Start a realtime logging session using passed data streams.
     * The <code>setColumns()</code>
     * method must be called after this method is called before the first
     * <code>writeLog()</code> method is called. 
     * <p>
     * The use of this method is mutually exclusive with <code>startCachingLog()</code> and will reset internal state 
     * to realtime mode.
     * @param out A valid <code>DataOutputStream</code>
     * @param in A valid <code>DataInputStream</code>
     * @throws IOException if the data streams are not valid
     * @see #stopLogging
     * @see #startRealtimeLog(NXTConnection)
     * @see #setColumns
     */
    public void startRealtimeLog(DataOutputStream out, DataInputStream in) throws IOException{
        logmodeState=LMSTATE_REAL;
        if (out==null) throw new IOException("DataOutputStream is null");
        if (in==null) throw new IOException("DataInputStream is null");
        
        this.dos=out;
        this.dis=in;
        sessionBeginTime=(int)System.currentTimeMillis();
        setColumnsCount=0;
    } 
    // isConnected()=true and streams must be valid (not null)

    /** Start a realtime logging session using passed <code>NXTConnection</code> to retrieve the data streams. The
     * connection must already be established.
     * The <code>setColumns()</code>
     * method must be called after this method is called before the first
     * <code>writeLog()</code> method is called. 
     * <p>
     * The use of this method is mutually exclusive with <code>startCachingLog()</code> and will reset internal state 
     * to realtime mode.
     * @param connection A connected <code>NXTConnection</code> instance
     * @throws IOException if the data streams are not valid
     * @see #stopLogging
     * @see #startRealtimeLog(DataOutputStream, DataInputStream)
     * @see #setColumns
     */
    public void startRealtimeLog(NXTConnection connection) throws IOException{
        startRealtimeLog(connection.openDataOutputStream(), connection.openDataInputStream());
    }

    /** Stop the logging session and close down the connection and data streams. 
     * @see #startRealtimeLog(NXTConnection)
     * @see #startCachingLog
     */
    public void stopLogging() {
        cleanConnection();
    }
    
    // Sets caching (deferred) logging. Default mode at instantiation and will be called on first writeLog() method if not 
    // explicitly called. Resets startRealtimeLog() state.
    // Init for logging to cache for deferred transmit using sendCache()
    // Resets startRealtimeLog() state
     // default

    /** Sets caching (deferred) logging. This is the default mode at instantiation. 
     * The <code>setColumns()</code>
     * method must be called after this method is called before the first
     * <code>writeLog()</code> method is called. 
     * <p>
     * The use of this method is mutually exclusive with the <code>startRealtimeLog()</code> methods and will reset internal state 
     * to caching mode.
     * @see #stopLogging
     * @see #sendCache(NXTConnection)
     * @see #startRealtimeLog(NXTConnection)
     * 
     */
    public void startCachingLog(){
        logmodeState=LMSTATE_CACHE;
        // set up the cacher
//        byteCache = new Queue<Byte>();
//        byteCache.ensureCapacity(2048);
        byteCache = new byteQueue();
        this.dos = new DataOutputStream(new CacheOutputStream());
        sessionBeginTime=(int)System.currentTimeMillis();
        setColumnsCount=0;
    }

    /** Sends the log cache. Valid only for caching (deferred) logging using startCachingLog(). 
     * @param out
     * @param in
     * @throws IOException if the data streams are not valid
     * @throws IllegalStateException if <code>startCachingLog()</code> has not been called
     */
    public void sendCache(DataOutputStream out, DataInputStream in) throws IOException {
        if (logmodeState!=LMSTATE_CACHE) throw new IllegalStateException("wrong mode");
        if (out==null) throw new IOException("DataOutputStream is null");
        if (in==null) throw new IOException("DataInputStream is null");
        this.dos=out;
        this.dis=in;
        
        // set to allow state protocol data sends
        logmodeState=LMSTATE_REAL;
        
        // send the headers TODO ensure that these are set but not for DataViewer GUI
        sendHeaders();  // guaranteed to be set by checkWriteState()
        setDataType(columnDefs[0].getDatatype());
        boolean doExit = false;
        while (!doExit) {
            for (int i=0;i<columnDefs.length;i++) {
                setDataType(columnDefs[i].getDatatype());
                try {
                    dos.write(getBytesFromCache(columnDefs[i].getSize()));
                } catch (EmptyQueueException e) {
//                    System.out.println("EmptyQueueException");
                    doExit=true;
                }
            }
        }
        logmodeState=LMSTATE_CACHE;
    }

    /** Sends the log cache using passed <code>NXTConnection</code> to retrieve the data streams. The
     * connection must already be established.  Valid only for caching (deferred) logging using <code>startCachingLog()</code>.
     * @param connection A connected <code>NXTConnection</code> instance
     * @throws IOException if the data streams are not valid
     * @throws IllegalStateException if <code>startCachingLog()</code> has not been called
     */
    public void sendCache(NXTConnection connection) throws IOException{
        sendCache(connection.openDataOutputStream(), connection.openDataInputStream());
    }
    private byte[] getBytesFromCache(int count) throws EmptyQueueException{
        byte[] temp = new byte[count];
        for (int i=0;i<count;i++) {
//            temp[i]=((Byte)byteCache.pop()).byteValue();
            temp[i]=byteCache.remove();
        }
        return temp;
    }
    
    private void manageClose(){
        // In cache mode, the CacheOutputStream class will throw a new IOException when OutOfMemoryError occurs so the 
        // writeLog() methods call
        // this on their trap. We then intercept the IOException and redo it as an OutOfMemoryError here
        if (logmodeState==LMSTATE_CACHE) {
            throw new OutOfMemoryError("Too Much Data!");
        }
        cleanConnection();
    }
    /** Close the current open connection. The data stream is flushed and closed. After calling this method, 
     *   another connection must be established or data streams re-created.
     */
    private void cleanConnection() {
        // Send ATTENTION request and remote FLUSH command
        sendATTN();
        byte[] command = {COMMAND_FLUSH,-1};
        sendCommand(command);
        try {
            this.dos.flush();
            // wait for the hardware to finish any "flushing". I found that without this, the last data may be lost if the program ends
            // or dos is set to null right after the flush().
            Delay.msDelay(100); 
            if (this.dis!=null) this.dis.close();
            if (this.dos!=null) this.dos.close();
        } catch (IOException e) {
            // ignore
        } catch (Exception e) {
            // ignore
        }
        this.dis = null;
        this.dos = null;
    }
    
    /** lower level data sending method
     * @param command the bytes[] to send
     */
    private final synchronized void sendCommand(byte[] command){
        if (this.dos==null) return;
        try {
            this.dos.write(command);
        } catch (IOException e) {
            cleanConnection();
        }
    }

    private void checkWriteState(int datatype){
        if (this.disableWriteState) return;
         
        if(setColumnsCount==0) throw new IllegalStateException("cols not set ");
        if (this.currColumnPosition>=((this.itemsPerLine)&0xff)) throw new IllegalStateException("too many cols ");

        // disable this state management to avoid infinite (well, until the heap blows) recursion
        this.disableWriteState=true; 
        
        // ensure first column item always is timestamp
        if (this.currColumnPosition==1) {
            setDataType(DT_INTEGER);
            writeLog((int)System.currentTimeMillis()-sessionBeginTime);
        }
        if (datatype!=columnDefs[currColumnPosition].getDatatype()) throw new IllegalStateException("datatyp mismatch ");
        // if DT needs to be changed, signal receiver
        setDataType(datatype);
        
        // keep track of current column and reset to 1 (exclude 0-timestamp) if needed
        this.currColumnPosition++;
        // I don't like this but finishLine() has majority support. Why not just wrap? Answer: So the user knows they f'ed 
        // up, that's why.
        //if (this.currColumnPosition>=(((int)this.itemsPerLine)&0xff)) this.currColumnPosition=1; // wrap if EOL
        
        // enable state management
        this.disableWriteState=false;
    }

    /** Finish the row and start a new one. 
     * <p>
     * The Column count is set by calling
     * <code>setColumns()</code> and you must ensure that you call the appropriate <code>writeLog()</code> method the same number of 
     * times as that column count before this method is called.
     * 
     * @throws IllegalStateException if all the columns defined with <code>setColumns()</code> per row have not been logged. 
     * @see #setColumns
     */
    public void finishLine() {
//        System.out.println("finline");
//        System.out.println("colpos="+ this.currColumnPosition);
//        System.out.println(("ipl="+(((int)this.itemsPerLine)&0xff)));
//        Button.waitForPress();
        if (this.currColumnPosition!=((this.itemsPerLine)&0xff)) throw new IllegalStateException("too few cols ");
        currColumnPosition=1;
//        this.lineCount++;
    }
    
    /** send the command to set the active datatype
     * @param datatype
     */
    private void setDataType(int datatype) {
        // force boolean, byte, short to int
        if (datatype<DT_INTEGER) datatype=DT_INTEGER;
        // exit if already current
        if (this.currentDataType==(datatype&0xff)) return;
        
        // return if user logging in cache mode
        if (logmodeState!=LMSTATE_REAL) return;
        
        byte[] command = {COMMAND_DATATYPE,-1};        
        this.currentDataType = (byte)(datatype&0xff);
        command[1] = this.currentDataType; 
        sendATTN();
        sendCommand(command);
    }
    
//    private void setItemsPerLine(int itemsPerLine){
//       sendATTN();
//       this.itemsPerLine= (byte)(itemsPerLine&0xff);
//       byte[] command = {COMMAND_ITEMSPERLINE,-1};
//       command[1] = this.itemsPerLine;
//       sendCommand(command);
//    }
    
    /** Send an ATTENTION request. Commands usually follow. There is no response/handshake mechanism.
     */
    private void sendATTN(){
        final int XORMASK = 0xff;
        // 2 ATTN bytes
        byte[] command = {ATTENTION1,ATTENTION2,0,0};  
        int total=0;
        // add a random verifier byte
        command[2]=getChksumRandVal();
        for (int i=0;i<3;i++) total+=command[i];
        // set the XORed checksum
        command[3]=(byte)((total^XORMASK)&0xff);
        // send it        
        sendCommand(command);
    }

    /** 
    * Write a <code>boolean</code> value as an <code>int</code> 1 (<code>true</code>) or 0 (<code>false</code>) to the log. 
    * In realtime logging mode, if an <code>IOException</code> occurs, the connection
    * and data streams are silently closed down and no exception is thrown from this method.
    * 
    * @param datapoint The <code>boolean</code> value to log.
    * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
    * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
    * <code>finishLine()</code> was not called after last column logged), or the column
    * definitions have not been set with <code>setColumns()</code>.
    * @throws OutOfMemoryError if in cache mode and memory is exhausted.
    * @see #setColumns
    * @see #finishLine
    */
    public void writeLog(boolean datapoint) {
        writeLog(datapoint ? 1 : 0);
    }

    /** 
    * Write a <code>byte</code> value to the log. 
    * In realtime logging mode, if an <code>IOException</code> occurs, the connection
    * and data streams are silently closed down and no exception is thrown from this method.
    * 
    * @param datapoint The <code>byte</code> value to log.
    * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
    * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
    * <code>finishLine()</code> was not called after last column logged), or the column
    * definitions have not been set with <code>setColumns()</code>.
    * @throws OutOfMemoryError if in cache mode and memory is exhausted.
    * @see #setColumns
    * @see #finishLine
    */
    public void writeLog(byte datapoint) {
        writeLog((int)datapoint);
    }
    
    /** 
    * Write a <code>short</code> value to the log. 
    * In realtime logging mode, if an <code>IOException</code> occurs, the connection
    * and data streams are silently closed down and no exception is thrown from this method.
    * 
    * @param datapoint The <code>short</code> value to log.
    * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
    * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
    * <code>finishLine()</code> was not called after last column logged), or the column
    * definitions have not been set with <code>setColumns()</code>.
    * @throws OutOfMemoryError if in cache mode and memory is exhausted.
    * @see #setColumns
    * @see #finishLine
    */
    public void writeLog(short datapoint) {
        writeLog((int)datapoint);
    }

    /** 
      * Write an <code>int</code> to the log. In realtime logging mode, if an <code>IOException</code> occurs, the connection
      * and data streams are silently closed down and no exception is thrown from this method.
      * 
      * @param datapoint The <code>int</code> value to log.
      * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
      * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
      * <code>finishLine()</code> was not called after last column logged), or the column
      * definitions have not been set with <code>setColumns()</code>.
      * @throws OutOfMemoryError if in cache mode and memory is exhausted.
      * @see #setColumns
      * @see #finishLine
      */
    public void writeLog(int datapoint) {
        checkWriteState(DT_INTEGER);
        try {
            this.dos.writeInt(datapoint);
            checkFlush(4);
        } catch (IOException e) {
            manageClose();
        }
    }
     
    /** 
    * Write an <code>long</code> to the log. In realtime logging mode, if an <code>IOException</code> occurs, the connection
    * and data streams are silently closed down and no exception is thrown from this method.
    * 
    * @param datapoint The <code>long</code> value to log.
    * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
    * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
    * <code>finishLine()</code> was not called after last column logged), or the column
    * definitions have not been set with <code>setColumns()</code>.
    * @throws OutOfMemoryError if in cache mode and memory is exhausted.
    * @see #setColumns
    * @see #finishLine
    */
    public void writeLog(long datapoint) {
        checkWriteState(DT_LONG);  
        try {
            this.dos.writeLong(datapoint);
            checkFlush(8);
        } catch (IOException e) {
            manageClose();
        }
    }
    
    /** 
    * Write an <code>float</code> to the log. In realtime logging mode, if an <code>IOException</code> occurs, the connection
    * and data streams are silently closed down and no exception is thrown from this method.
    * 
    * @param datapoint The <code>float</code> value to log.
    * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
    * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
    * <code>finishLine()</code> was not called after last column logged), or the column
    * definitions have not been set with <code>setColumns()</code>.
    * @throws OutOfMemoryError if in cache mode and memory is exhausted.
    * @see #setColumns
    * @see #finishLine
    */
    public void writeLog(float datapoint) {
        checkWriteState(DT_FLOAT);
        try {
//            LCD.drawString("this.dos " + value + " ",0,1);
            this.dos.writeFloat(datapoint);
            checkFlush(4);
        } catch (IOException e) {
            manageClose();
        }
    }
    
    /** 
    * Write an <code>double</code> to the log. In realtime logging mode, if an <code>IOException</code> occurs, the connection
    * and data streams are silently closed down and no exception is thrown from this method.
    * 
    * @param datapoint The <code>double</code> value to log.
    * @throws IllegalStateException if the column datatype for the column position this method was called for does not match
    * the datatype that was set in <code>setColumns()</code>, the column position exceeds the total column count (i.e.
    * <code>finishLine()</code> was not called after last column logged), or the column
    * definitions have not been set with <code>setColumns()</code>.
    * @throws OutOfMemoryError if in cache mode and memory is exhausted.
    * @see #setColumns
    * @see #finishLine
    */
    public void writeLog(double datapoint) {
        checkWriteState(DT_DOUBLE);
        try {
    //            LCD.drawString("this.dos " + value + " ",0,1);
            this.dos.writeDouble(datapoint);
            checkFlush(8);
        } catch (IOException e) {
            manageClose();
        }
    }


    // TODO
    /** Don't quite know how to handle this is the chart so it is private for now
     * @param strData The <code>String</code> to log
     */
//    private final synchronized void writeStringLine(String strData){
//        byte oldIPL = itemsPerLine;
//        if (itemsPerLine!=1) setItemsPerLine((byte)1);
//        // skip the [potential] timestamp
//        // TODO maybe checkWriteState(DT_STRING);?
//        setDataType(DT_STRING); 
//        writeStringData(strData);
//        if (itemsPerLine!=1) setItemsPerLine(oldIPL);
//    }
    
    /**
    * Write an <code>String</code> to the <code>DataOutputStream</code> as a null (0) terminated ASCII byte stream.
    * 
    * @param strData The <code>String</code> value to log.
    * @see java.io.DataOutputStream#write(byte)
    */
    // TODO set to private until I figure out/decide if I want to publish this method
//    private final synchronized void writeLog(String strData) {
//        checkWriteState(DT_STRING);
//        writeStringData(strData);
//    }
    
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
            manageClose();
        }
    }

     /** Set the data set header information for the data log and chart series. The first column in the data log 
      * is always a system timestamp
      * (element 0) so <u>your</u> first <code>writeLog()</code> item would be column 1, element 2 is column 2, so on and so forth. 
      * The items per log row
      * must match the number of headers you define in this method. 
      * <p>
      * This method must be called after the <code>startCachingLog()</code>
      * or either of the <code>startRealtimeLog()</code> methods is called or an <code>IllegalStateException</code> will be 
      * thrown in the <code>writeLog()</code> methods.
      * <p>
      * The number and datatype of <code>writeLog()</code> calls per log row must match the number of columns and the datatypes
      * you define here. You must
      * end each log row with <code>finishLine()</code> or an <code>IllegalStateException</code> will be thrown on the
      * next <code>writeLog()</code> call. If using the NXT ChartingLogger tool, the chart
      * will only reflect the new data sent after this call since the series are redefined.
      * <p>
      * In realtime mode, if headers are set during logging with the <code>writeLog()</code> methods, the log will reflect 
      * the changes from that point on. In cached mode, if headers are set during logging with the <code>writeLog()</code> 
      * methods, an <code>UnsupportedOperationException</code> is thrown.
      * <P>
      * If length of the passed array is 
      * zero or if length > 255, the method does nothing and returns immediately. 
      * 
      * @param columnDefs The array of <code>LogColumn</code> instances to use for the data log column definitions
      * @see LogColumn
      * @see #finishLine
      * @see #startCachingLog
      * @see #startRealtimeLog(NXTConnection)
      * @throws UnsupportedOperationException if <code>setColumns</code> is called more than once in cached mode.
      */
    // sets the header names, datatypes, count, chartable attribute, range axis ID (for multiple axis charting)
    // This is mandatory and implies a new log structure when called
    public void setColumns(LogColumn[] columnDefs){
        if (columnDefs.length==0) return;
        if (columnDefs.length>255) return;
        if (this.setColumnsCount>1&&logmodeState==LMSTATE_CACHE) throw new UnsupportedOperationException("already called");
        LogColumn[] tempColumnDefs = new LogColumn[columnDefs.length+1];
        tempColumnDefs[0] = new LogColumn("milliseconds", LogColumn.DT_INTEGER, true, 1);
        System.arraycopy(columnDefs, 0, tempColumnDefs, 1, columnDefs.length);
        this.columnDefs = tempColumnDefs;
        this.setColumnsCount++;
        this.itemsPerLine = (byte)(this.columnDefs.length&0xff);
        if (logmodeState==LMSTATE_REAL) sendHeaders(); 
    }
    
    private void sendHeaders(){
        this.currColumnPosition=1;
        byte[] command = {COMMAND_SETHEADERS,0};        
        command[1] = (byte)(this.columnDefs.length&0xff); 
        sendATTN();
        sendCommand(command);        
        for (LogColumn item: columnDefs){
            // format: name![Y|N]![1-4]
            writeStringData(item.getName().replace('!',' ') + "!" + (item.isChartable()?"y":"n") + "!" + item.getRangeAxisID());
        }
    }
}
