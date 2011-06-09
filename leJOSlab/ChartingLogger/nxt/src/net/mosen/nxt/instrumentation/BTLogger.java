package net.mosen.nxt.instrumentation;

import java.io.DataOutputStream;

/**
 * Common logging functionality interface that utilizes the connection provided by <code>NXTConnectionManager</code>. The implementation must
 * be provided based on your specific logging requirements. Try to utilize buffering techniques to minimize
 * blocking on calls to your <code>write<u>xxx</u>()</code> implementations.
 * @see NXTConnectionManager
 * @see NXTDataLogger
 * @author Kirk P. Thompson  
 * @version v0.3_70  7/2/09
 */
interface BTLogger {

    /**
     * Write an <code>int</code> to the <code>DataOutputStream</code>. Use this for <code>byte</code> and
     * <code>short</code> as well.
     * 
     * @param value The <code>int</code> value to <code>logInt()</code>.
     * @see java.io.DataOutputStream#writeInt 
     */
    public void logInt(int value);

    /**
     * Write an <code>long</code> to the <code>DataOutputStream</code>.
     * 
     * @param value The <code>long</code> value to <code>logLong()</code>.
     * @see java.io.DataOutputStream#writeLong
     */
    public void logLong(long value);
    
    /**
     * Take care of any open DataOutputStream, queued data, etc..
     * 
     * @see java.io.DataOutputStream#close
     */
    public void closeConnection();
    
    /**
     * Flushes the output stream and forces any buffered output bytes 
     * to be written out. 
     *
     */
    public void flush();
    
    /**
     * @param itemsPerLine The number of data items per line
     * @see #setHeaders
     */
   // public void setItemsPerLine(int itemsPerLine);

    /** Set the data set header labels. Element 0 is column 1, element 1 is column2, so on and so forth. The items per line
     * is automatically set according to the number of headers (the array size) passed in <code>headerLabels</code>.
     * <p>
     * If headers are set mid-logging, the log will reflect the changes from that point on.
     * @param headerLabels The array of header labels to use for the data log
     * 
     */
    public void setHeaders(String[] headerLabels);

    /**
     * Write an <code>float</code> to the <code>DataOutputStream</code>.
     * 
     * @param value The <code>float</code> value to <code>logFloat()</code>.
     * @see java.io.DataOutputStream#writeFloat
     */
    public void logFloat(float value);
    
     /**
     * Write an <code>String</code> to the <code>DataOutputStream</code> as a null (0) terminated ASCII byte stream.
     * 
     * @param value The <code>String</code> value to <code>logString()</code>.
     * @see java.io.DataOutputStream#write(byte)
     */
    public void logString(String value);
    
    /**
     * Write an <code>double</code> to the <code>DataOutputStream</code>.
     * 
     * @param value The <code>double</code> value to <code>logDouble()</code>.
     * @see java.io.DataOutputStream#writeDouble
     */
    public void logDouble(double value);
}
