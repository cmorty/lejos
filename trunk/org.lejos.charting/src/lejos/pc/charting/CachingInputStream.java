package lejos.pc.charting;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/** Provide a read-ahead buffer input stream of bytes. The source <code>InputStream</code> (passed in the constructor) is
 * constantly read into a <code>byte</code> buffer which values are then made available through the <code>read</code> methods. 
 * This methodology is used to help ensure the NXT does not block on a <code>write()</code> (using BlueTooth or USB) as it would if its limited
 * buffer is filled and nothing is reading from the stream.
 * <p>
 * If the buffer fills completely, the reader thread will wait/block until bytes are <code>read()</code> so make sure the size of the
 * buffer is appropriate for the application intended.
 * 
 * @author Kirk P. Thompson
 * @author Sven K\u00F6hler
 */
public class CachingInputStream extends InputStream {
    private InputReader ir;
    
    /** Construct an <code>CachingInputStream</code> with the specified buffer size and source.
     * @param in The source input stream
     * @param bufferSize The byte buffer size
     */
    public CachingInputStream(InputStream in, int bufferSize) {
        this.ir = new InputReader(in, bufferSize);
        this.ir.start();
    }

    /** read from input stream passed in constructor and save to buf array
     */
    private class InputReader extends Thread {
        private int readVal=-1;
        private int maxQueuedBytes=0;
        private boolean flagEOF=false;
        private int byteCount=0;
        private byte[] buf;
        private final InputStream in;
        private IOException anIOException=null;
        private int wIndex=0;
        private int rIndex=0;
        
        InputReader(InputStream in, int bufferSize) {
            this.in=in;
            this.buf = new byte[bufferSize];
            this.setDaemon(true);
        }
        
        public void run() {
            while (true){
                if (wIndex>=buf.length) {
                    wIndex=0;
                }
                
                // if buf is full, wait until a read()
                synchronized(this) {
                    while (byteCount>=buf.length) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            ; // Do nothing
                        }
                    }
                }
                
                try {                
                   // get a byte (will block)
                    readVal = in.read();
                } catch (IOException e) {
                    anIOException=e;
                    break; // will cause thread to exit
                } 
                
                if (readVal==-1) {
                    // end of stream reached. flag EOFException
                    flagEOF=true;
                    break;
                }
                
                synchronized(this) {
                    buf[wIndex++] = (byte)(readVal&0xff);
                    byteCount++;
                    if (byteCount>maxQueuedBytes) maxQueuedBytes=byteCount;
                    this.notifyAll(); // wake up the read() wait (if waiting)
                }
            }
            // do final notify on loop end
            synchronized(this) {
                this.notify();
            }
        }
        
        synchronized int getMaxQueuedBytes(){
            return maxQueuedBytes;
        }
        
        synchronized int read() throws IOException{
            int retVal;
            
            if (rIndex>=buf.length){
                rIndex=0;
            }
            
            while (!flagEOF && byteCount==0) {
                // block if no data buffered
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    ; // ignore
                }
            }
           
            checkExceptions();
            retVal=buf[rIndex++]&0xff;
            byteCount--;
            this.notifyAll(); // wake up the in read() from NXT (if waiting because the buffer is full)
            
            return retVal;
        }
        
        private void checkExceptions() throws IOException{
            if (byteCount==0 && flagEOF) throw new EOFException("read: No more data!!");
            if (anIOException!=null) throw anIOException;
        }
        
        synchronized int available() throws IOException {
            checkExceptions();
            return byteCount;
        }
    }
    
    public int read() throws IOException{
        return ir.read();
    }
    
    @Override
    public int available() throws IOException {
        return ir.available();
    }

    /** Return the maximum number of bytes buffered.
     * @return byte count
     */
    public int getMaxQueuedBytes(){
        return ir.getMaxQueuedBytes();
    }
}
