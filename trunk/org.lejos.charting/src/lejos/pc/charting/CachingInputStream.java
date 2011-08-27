package lejos.pc.charting;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/** Provide a read-ahead buffer input stream of bytes.
 * 
 * @author Kirk P. Thompson
 */
public class CachingInputStream extends InputStream {
    private final InputStream in; 
    private volatile byte[] buf;
    private volatile int wIndex=0;
    private volatile int rIndex=0;
    private Object readLock=new Object();
    private volatile int byteCount=0;
    private volatile boolean flagEOF=false;
    private volatile IOException anIOException=null;
    private int maxQueuedBytes=0;

    /** Construct an input stream with the specified buffer size and source.
     * @param in The source input stream
     * @param bufferSize The byte buffer size
     */
    public CachingInputStream(InputStream in, int bufferSize) {
        this.in=in;
        buf = new byte[bufferSize];
        new InputReader().start();
    }
    
    private class InputReader extends Thread {
        private int readVal=-1;
        
        public InputReader(){
            this.setDaemon(true);
        }
        
        public void run() {
            while (true){
                if (wIndex>=buf.length) {
                    wIndex=0;
                }
                
                if (byteCount>=buf.length) {
                    //if (byteCount!=50 )System.out.println("byteCount=" + byteCount);
                    doWait(10);
                    continue;
                }
                try {                
                   // get a byte (will block)
                    readVal = in.read();
//                    System.out.println("readVal=" + readVal);
                } catch (IOException e) {
//                    System.out.println("########## InputReader: IOException-" + e.toString());
                    anIOException=e;
                    break; // will cause thread to exit
                } 
                
                if (readVal==-1) {
//                    System.out.println("readVal==-1");
                    // end of stream reached. flag EOFException
                    flagEOF=true;
                    break;
                }
                
                synchronized(readLock) {
                    buf[wIndex++] = (byte)(readVal&0xff);
                    byteCount++;
                    if (byteCount>maxQueuedBytes) maxQueuedBytes=byteCount;
                    readLock.notify();
                }
            }
            synchronized(readLock) {
                readLock.notify();
            }
        }
    }
    
    private void doWait(long milliseconds) {
         try {
             Thread.sleep(milliseconds);
         } catch (InterruptedException e) {
             //Thread.currentThread().interrupt();
         }
    }
    
    private void checkExceptions() throws IOException{
//        if (flagEOF) System.out.println("########   flagEOF! byteCount=" + byteCount + ", idx delta=" + (wIndex-rIndex));
        if (byteCount==0 && flagEOF) throw new EOFException("read: No more data!!");
        if (anIOException!=null) throw anIOException;
    }
    
    public synchronized int read() throws IOException{
        int retVal;
        
        if (rIndex>=buf.length){
            rIndex=0;
        }
        
        while (!flagEOF && byteCount==0) {
            synchronized(readLock) {
                // block if no data buffered
                try {
                    readLock.wait();
                } catch (InterruptedException e) {
                    ; // ignore
                }
            }
        }
        
        synchronized(readLock) {
            checkExceptions();
            retVal=buf[rIndex++]&0xff;
            byteCount--;
        }
        return retVal;
    }
    
    public synchronized int available() throws IOException {
        checkExceptions();
        return byteCount;
    }

    /** Return the maximum number of bytes buffered.
     * @return byte count
     */
    public int getMaxQueuedBytes(){
        return maxQueuedBytes;
    }
}
