package lejos.pc.charting;

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
        private final InputStream in;
        private int maxQueuedBytes=0;
        private boolean flagEOF=false;
        private int byteCount=0;
        private final byte[] buf;
        private IOException anIOException=null;
        private int rIndex=0;
        
        InputReader(InputStream in, int bufferSize) {
            this.in=in;
            this.buf = new byte[bufferSize];
            this.setDaemon(true);
        }

        public void run() {
        	IOException e = null;
        	try	{
	            while (true){
	            	int readVal = in.read();
	                
	                if (readVal < 0) {
	                    // end of stream reached.
	                    break;
	                }
	                
                    write(readVal);
                }
            } catch (IOException t) {
                e = t;
            } catch (Throwable t) {
                e = new IOException("exception during read");
                e.initCause(t);
            } finally {
                // do final notify on loop end
                signalEOF(e);
            }
        }
        
        synchronized void signalEOF(IOException e) {
            anIOException = e;
            flagEOF = true;
            this.notifyAll();
        }

        synchronized void write(int val) {
            // if buf is full, wait until a read()
            while (byteCount >= buf.length) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    ; // Do nothing
                }
            }
            buf[(rIndex + byteCount) % buf.length] = (byte)val;
            if (byteCount == 0)
                this.notifyAll(); // wake up the read() wait (if waiting)
            byteCount++;
            if (byteCount > maxQueuedBytes)
                maxQueuedBytes = byteCount;
        }
        
        synchronized int getMaxQueuedBytes(){
            return maxQueuedBytes;
        }
        
        synchronized int read() throws IOException{
            int retVal;
            
            while (!flagEOF && byteCount == 0) {
                // block if no data buffered
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    ; // ignore
                }
            }

            checkExceptions();
            if (flagEOF && byteCount == 0)
                return -1;
            
            retVal=buf[rIndex] & 0xff;
            rIndex = (rIndex + 1) % buf.length;
            if (byteCount >= buf.length)
                this.notifyAll(); // wake up the in read() from NXT (if waiting because the buffer is full)
            byteCount--;
            
            return retVal;
        }
        
        private void checkExceptions() throws IOException{
            if (flagEOF && anIOException != null)
                throw anIOException;
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
