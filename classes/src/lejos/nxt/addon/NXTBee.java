//package lejos.nxt.addon;
package lejos.nxt.addon;

import java.io.*;

import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.RS485;
import lejos.util.Delay;

import lejos.internal.io.*;

/**
 * Send and receive data using a Dexter Industries NXTBee attached to Port 4 
 * The NXTBee uses a RS485 serial link. By default it operates at 9600 baud, but this can be changed
 * For more details on the NXTBee see: http://www.dexterindustries.com/NXTBee.html,
 * 
 * The NXTBee class has two modes of operation; by default a thread can be started by calling the 
 * NXTBee nb = new NXTBee();
 * new Thread(nb).start();
 * 
 * The thread will act as a data pump continually polling the RS485 port and storing data received 
 * on the port in an internal circular buffer, and reading data from the circular buffer and
 * writing it onto the RS485 port. 
 * 
 * Direct read/write access to the RS485 port is supported by calling the stop() and statrt()
 * methods which pause the internal data reading thread.
 * 
 * The NXTBee class returns an InputStream and an OutputStream object which can be used by
 * programs to read and write data to and from the NXTBee. Use of the Stream objects is recommended
 * over direct access to the RS485 port. For example, the standard DataInputStream/DataOutputStream classes
 * provide a clean interface for reading/writing basic Java types.
 * 
 * The NXTBee configuration is changed by entering a dedicated command mode by sending the sequence +++ with
 * a one second pause before and after the +++ characters. In this mode the configuration of the NXTBee
 * can be changed. To exit the command mode the sequence ATCN\n is sent.
 * 
 * Portions of this class use code from Stephen Ostermiller's Java utilities:
 * http://ostermiller.org/utils/CircularBuffer.html
 * 
 * @author Mark Crosbie (mark@mastincrosbie.com)
 * @author http://mastincrosbie.com/Marks_LEGO_projects/LEGO_Projects.html
 * 
 * @version 0.1
 * 
 */
public class NXTBee implements Runnable  {

	private int bufferSize = 1024;
	
	private boolean keepRunning = true;
	private boolean savedRunningFlag;
	
	private int readTimeout = 250; // how long to wait for a reply, in ms
	
	private CircularByteBuffer inbound; // stores data from the NXTBee to be read by leJOS
	private CircularByteBuffer outbound; // stores data from leJOS to send to the NXTBee

	private InputStream readInbound, readOutbound;
	private OutputStream writeInbound, writeOutbound;
	
	private int baudRate = 9600;	// Change this to change the default baud rate
	private boolean debug = false;	// Wait for remote console if true
	
	protected boolean outputStreamClosed = false;
	protected boolean inputStreamClosed = false;

	
	/**
	 * Default constructor initialises a NXTBee object at 9600 baud
	 * Enables the internal data reading thread
	 */
	public NXTBee() {
		this(9600, true, false);
		keepRunning = true;
	}
	
	/**
	 * Constuctor for the NXTBee running at default 9600 baud.
	 * @param runThread If true the internal data reading thread is started, if false
	 * the thread does not read/write data and all data transfer must be performed
	 * manually using the read/write methods.
	 */
	public NXTBee(boolean runThread) {
		this(9600, runThread, false);
	}
	
	/**
	 * Constructor to create a NXTBee object to communicate at a specified baud rate
	 * and to select if the internal data reading thread is running.
	 * @param baud - baud rate to select when communicating with the NXTBee.
	 * @param runThread If true the internal data reading thread is started, if false
	 * the thread does not read/write data and all data transfer must be performed
	 * manually using the read/write methods.
	 * @param debugEnabled If true then debug output is written to RConsole. Can be disabled using
	 * the setDebug call. You must open RConsole to get any output on it first.
	 */
	public NXTBee(int baud, boolean runThread, boolean debugEnabled) {
		
		baudRate = baud;

		keepRunning = runThread;
		debug = debugEnabled;
		
		// Enable the RS485 line on port 4
		RS485.hsEnable(baudRate, bufferSize);	

		// Allocate two circular buffers - one for the inbound data from the NXTBee->leJOS, and the
		// other for outbound data from leJOS -> NXTBee
		inbound = new CircularByteBuffer();
		outbound = new CircularByteBuffer();
		
		// The inbound buffer is used for data from the NXTBee
		readInbound = inbound.getInputStream(); // for the leJOS side to read data from the buffer
		writeInbound = inbound.getOutputStream(); // for the driver to write data from the NXTBee
		
		// The outbound buffer is used for data from the leJOS side to the NXTBee
		readOutbound = outbound.getInputStream(); // for the driver to read data to send to the NXTBee
		writeOutbound = outbound.getOutputStream(); // for the leJOS side to write data to send to the NXTBee
		
		if(debug) RConsole.println("NXTBee initialised");
	}
	
	/**
	 * Constructor to create a NXTBee object at a specified baud rate
	 * @param baud Baud rate used to communicate with the NXTBee on port 4
	 */
	public NXTBee(int baud) {
		
		this(baud, true, false);
	}
	
	/**
	 * Enable or disable debug output. Output is written to the RConsole which must be opened
	 * prior to calling.
	 * @param debugMode Boolean flag if true debug output is enabled, if false it is disabled
	 */
	void setDebug(boolean debugMode) {
		
		debug = debugMode;
		if(debug) {
			RConsole.println("NXTBee: DEBUG ENABLED at " + System.currentTimeMillis());
		}
	}
	
	/**
	 * Return an Input stream for receiving data from the NXTBee
	 * @return an input stream for the NXTBee
	 */
	public InputStream getInputStream() {
		return readInbound;
	}

	/**
	 * Return an Output stream for writing to the NXTBee
	 * @return an output stream for the NXTBee
	 */
	public OutputStream getOutputStream() {
		return writeOutbound;
	}
	
	/**
	 * Stop the internal data reading thread, allowing direct access to the underlying sensor on  port 4
	 * The data reading thread acts as a data pump to an internal circular buffer, filling the buffer
	 * with data received from the RS485 port, and sending data on the port when the buffer is filled.
	 * If you want to communicate directly with the NXTBee then call the stop() method to halt the buffer
	 * thread. Call start() to re-start the data reading thread.
	 * The contents of the circular data buffer are unaffected by calling start() and stop()
	 */
	synchronized public void stop() {
		if(debug) RConsole.println("NXTBee: data thread disabled");
		keepRunning = false;
	}
	
	/**
	 * Start the internal data reading thread,
	 * The data reading thread acts as a data pump to an internal circular buffer, filling the buffer
	 * with data received from the RS485 port, and sending data on the port when the buffer is filled.
	 * If you want to communicate directly with the NXTBee then call the stop() method to halt the buffer
	 * thread. Call start() to re-start the data reading thread.
	 * The contents of the circular data buffer are unaffected by calling start() and stop()
	 */
	synchronized public void start() {
		if(debug) RConsole.println("NXTBee: data thread enabled");
		keepRunning = true;
	}
	
	/** 
	 * Read bytes directly from the NXTBee RS485 buffer to get data if available
	 * 
	 * Bypasses the internal circular buffer. It is not recommend to call this method
	 * if the internal data reading thread is running. The recommended method of calling
	 * is to first call stop(), then read(), then start() so as to avoid collisions
	 * between the data reading thread and this method.
	 * 
	 * @param b - byte buffer to place the data into
	 * @param off - the offset in b to start storing data read into.
	 * @param len - the number of bytes to read. The contract of read is to read anywhere from 0 up to len
	 * bytes and store them in b[off]..b[off+len-1].
	 * 
	 * @return The number of bytes read
	 */
	public int read(byte b[], int off, int len) {
		
		return RS485.hsRead(b, off, len);
	}
	
	/**
	 * Write bytes directly to the RS485 port for transmission by the NXTBee
	 * 
	 * Bypasses the internal circular buffer. It is not recommend to call this method
	 * if the internal data reading thread is running. The recommended method of calling
	 * is to first call stop(), then write(), then start() so as to avoid collisions
	 * between the data reading thread and this method.
	 * 
	 * @param b - the byte buffer to write data from
	 * @param offset - start writing bytes from the byte buffer at b[offset]
	 * @param len - write len bytes from b[offset] to the RS485 port
	 * @return The number of bytes written
	 */
	public int write(byte b[], int offset, int len) {
		
		return RS485.hsWrite(b, offset, len);
	}
	
	/**
	 * Thread that reads data coming from the NXTBee and updates the internal circular
	 * buffer. The circular buffer stores data received from the RS485 port and in turn
	 * stores data to send on the port. This is an internal function.
	 * 
	 * @param timeout - how long to wait for data to arrive in milliseconds, 0 = forever
	 * @return Number of bytes actually read
	 */
	synchronized private int waitForData(int timeout) {
		int bytesRead = 0;
		int ba = 0;
		long time = System.currentTimeMillis();
		
		int readBufSize = 100;	// NXTBee has maximum of 100 bytes in the buffer
		byte[] readBuf = new byte[readBufSize];
		
		try {
			while(bytesRead==0 && ( (timeout==0) || (System.currentTimeMillis()-time<timeout) ) ) {
				
				// First we check to see if any data is on the RS485 port that needs to be read. If it is then
				// we pull it in
				ba = RS485.hsRead(readBuf, 0, readBufSize);
				
				if (ba > 0) {
					
					bytesRead += ba;
					writeInbound.write(readBuf, 0, ba);	// only write what we actually read in
					
					if(debug) {
						String bufDbg = "NXTBee: waitForData read "+ba+" bytes = ";
						for (int i = 0; i<ba; i++) 
							bufDbg += readBuf[i] + " ";
						RConsole.println(bufDbg);
						
						String inMsg = new String(readBuf,0,ba);
						RConsole.println("waitForData :"+inMsg);
					}
				}
				
				// Now see if there is data available in the outbound buffer that needs to be sent
				// onto the RS485 port.
				if(( ba = readOutbound.available()) > 0) {
					ba = readOutbound.read(readBuf);
					if(debug) RConsole.println("NXTBee: waitForData sending " + ba + " bytes...");

					while(ba > 0 ) {
						int bytesWritten = RS485.hsWrite(readBuf, 0, ba);
						ba = ba - bytesWritten;
					}
				}
				
				
				Delay.msDelay(2);
			}
		} catch(IOException e) {
			return -1;
		}

		//if(debug) RConsole.println("NXTBee waitForData returning " + bytesRead);
		return bytesRead;
	}
	
	/**
	 * Start the data reading thread which keeps the circular buffer full of data
	 */
	public void run() {
		while(true) {
			if(keepRunning) {
				waitForData(readTimeout);
			}
			Thread.yield();
		}
	}
	
	/**
	 * Force the NXTBee to go into a direct command mode. This mode bypasses the usual data exchange
	 * and allows the configuration of the NXTBee to be changed.
	 * 
	 * To enter command mode the sequence +++ must be sent with a one-second guard on either side of
	 * the bytes. Once in command mode the NXTBee will not forward data received on the wireless antenna.
	 * To exit command mode call exitCommandMode().
	 * 
	 * Commands can be sent to the NXTBee using the write() method, or by calling hsWrite directly.
	 * 
	 * The contents of the circular buffer are not read/written to the RS485 port while in command mode. 
	 * However data can still be stored in the circular buffer for future transmission when command mode
	 * is exited.
	 * 
	 * @return A boolean true if command mode entered successfully, else false
	 */
	public synchronized boolean enterCommandMode() {
		
		
		byte[] cmd = {'+', '+', '+'};
		
		savedRunningFlag = keepRunning;
		keepRunning = false;

		Delay.msDelay(1000);
		
		if(debug) RConsole.println("NXTBee entering command mode");
		
		RS485.hsWrite(cmd, 0, cmd.length);
		
		Delay.msDelay(1000);
		
		// Make sure we get OK\r back in reply
		boolean ret = waitForOK(readTimeout);
		
		// If we didn't get a OK reply then do not disable read thread
		if(!ret) {keepRunning = savedRunningFlag;}
		
		return ret;
		
	}
	
	/**
	 * Force the NXTBee to exit direct command mode. This mode reverts the NXTBee to normal operation
	 * where data sent and received on the RS485 port is transmitted over the wireless link.
	 * 
	 * The character sequence to exit command mode is ATCN\n. If a OK reply is received then command
	 * mode is disabled and normal data reading resumes. If no OK is received then data reading/writing
	 * is not enabled and the NXTBee remains in command mode.
	 * 
	 */
	public synchronized boolean exitCommandMode() {
	
		byte cmd[] = {'A','T','C','N', 13};
		
		if(debug) RConsole.println("NXTBee leaving command mode");

		RS485.hsWrite(cmd, 0, cmd.length);
		
		boolean ret = waitForOK(readTimeout);
		
		// if we get OK back then we can resume whatever mode the data reading thread was in
		if(ret) { keepRunning = savedRunningFlag;}
	
		return ret;
	}
	
	/*
	 * Did a successful reply come back from the NXTBee?
	 * @param timeout How long to wait for a reply in milliseconds, 0 is forever
	 * @return Boolean true if OK received within timeout, else false returned
	 */
	private boolean waitForOK(int timeout) {
		int bytesRead = 0;
		int ba = 0;
		long time = System.currentTimeMillis();

		int readBufSize = 100;	// NXTBee has maximum of 100 bytes in the buffer
		byte[] readBuf = new byte[readBufSize];
		byte[] replyBuf = new byte[readBufSize]; // accumulate reply here
		
		try {
			int replyIndex = 0;
			int i;
			while(bytesRead==0 && ( (timeout==0) || (System.currentTimeMillis()-time<timeout) ) ) {
				
				// First we check to see if any data is on the RS485 port that needs to be read. If it is then
				// we pull it in
				ba = RS485.hsRead(readBuf, 0, readBufSize);
				
				if (ba > 0) {
					if(debug) RConsole.println("waitForOK: received " + ba + " bytes");
					bytesRead += ba;
					// copy the bytes we just read into the reply buf
					for(i=0; (i < ba) && (replyIndex < readBufSize); i++, replyIndex++) {
						replyBuf[replyIndex] = readBuf[i];
					}
				}
			}
			
			if(debug) RConsole.println("waitForOK: read total " + bytesRead + " bytes");
			
			return (replyBuf[0] == 'O') && (replyBuf[1] == 'K');
			
		} catch (Exception e) {
			if(debug) RConsole.println("checkReplyOK: Exception " + e);
			return false;
		}
		
	}
	
	
	/**
	 * Save the configuration on the device into FLASH so that it survives a power cycle
	 * See the NXTBee documentation for more details.
	 * 
	 * You must call enterCommandMode() prior to calling this function.
	 * 
	 */
	public void saveConfiguration() {
		
		byte[] cmd = {'A','T',' ', 'W','R', 13};
		
		RS485.hsWrite(cmd, 0, cmd.length);
	}
	
	/**
	 * Reset the NXTBee to the default configuration
	 * See the NXTBee documentation for more details.
	 * 
	 * You must call enterCommandMode() prior to calling this function
	 */
	public void resetNXTBee() {
		
		byte[] cmd = {'A','T',' ', 'F','R', 13};
		RS485.hsWrite(cmd, 0, cmd.length);
	}
	
	/**
	 * Set the D7 pin to the correct mode
	 * @return True if the pin mode was set, else false
	 */
	public boolean setPinmode() {

		boolean ret = false;
		
		// Setup pin command
		byte cmd[] = {'A','T',' ', 'D','7','7', 13};
		
		if(debug) RConsole.println("setPinmode");
		
		RS485.hsWrite(cmd, 0, cmd.length);
		ret = waitForOK(readTimeout);
		
		return ret;
	}


	/**
	 * Set the baud rate used by the NXTBee. This command will change the baud rate the NXTBee
	 * communicates at, so you will have to reset the baud rate that leJOS for the RS485 port
	 * by calling RS485.hsEnable()
	 * 
	 * Supported baud rates are: 1200, 2400, 4800, 9600 (default), 19200, 38400, 57600, 115200
	 * If a baud rate is selected that is not one of these values then no change is made to
	 * the baud rate of the NXTBee.
	 * 
	 * @param baud The baud rate to set the the NXTBee to use. Can only be one of the values above
	 * @return True if the baud rate was set, else false
	 */
	public boolean setNXTBeeBaudrate(int baud) {
		
		byte i;
		boolean ret = false;
		
		// baud rates settable by users
		int baud_rates_set[] = {1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200};

		for(i=0; i < baud_rates_set.length; i++) {
			if(baud == baud_rates_set[i]) {
				// Set baud rate command
				String cmd = "AT BD"+i+"\r";

				if(debug) RConsole.println("NXTBee setting baud to ["+i+"] = " + baud_rates_set[i]);
				RS485.hsWrite(cmd.getBytes(), 0, cmd.length());
				ret = waitForOK(readTimeout);
			}
		}
		
		return ret;
	}
	
	/**
	 * Class for reading from the NXTBee, returns a standard Java InputStream that can be
	 * used as an input source
	 * Based on CircularByteBuffer from Ostermiller Java utils.
	 * 
	 * @author mcrosbie
	 *
	 */
	protected class NXTBeeInputStream extends InputStream {

		/**
		 * Returns the number of bytes that can be read (or skipped over) from this
		 * input stream without blocking by the next caller of a method for this input
		 * stream. The next caller might be the same thread or or another thread.
		 *
		 * @return the number of bytes that can be read from this input stream without blocking.
		 * @throws IOException if the stream is closed.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public int available() throws IOException {
			synchronized (NXTBee.this){
				if (inputStreamClosed) throw new IOException("InputStream has been closed, it is not ready.");
				return (NXTBee.this.readInbound.available());
			}
		}

		/**
		 * Close the stream. Once a stream has been closed, further read(), available(),
		 * mark(), or reset() invocations will throw an IOException. Closing a
		 * previously-closed stream, however, has no effect.
		 *
		 * @throws IOException never.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public void close() throws IOException {
			synchronized (NXTBee.this){
				inputStreamClosed = true;
			}
		}

		/**
		 * Not implemented - read ahead is not supported
		 */
		@Override public void mark(int readAheadLimit) {
		}

		/**
		 * Tell whether this stream supports the mark() operation.
		 *
		 * @return false, mark is NOT supported.
		 */
		@Override public boolean markSupported() {
			return false;
		}

		/**
		 * Read a single byte.
		 * This method will block until a byte is available, an I/O error occurs,
		 * or the end of the stream is reached.
		 *
		 * @return The byte read, as an integer in the range 0 to 255 (0x00-0xff),
		 *     or -1 if the end of the stream has been reached
		 * @throws IOException if the stream is closed.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public int read() throws IOException {
			return (NXTBee.this.readInbound.read());
		}
		
		/**
		 * Read bytes into an array.
		 * This method will block until some input is available,
		 * an I/O error occurs, or the end of the stream is reached.
		 *
		 * @param cbuf Destination buffer.
		 * @return The number of bytes read, or -1 if the end of
		 *   the stream has been reached
		 * @throws IOException if the stream is closed.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public int read(byte[] cbuf) throws IOException {
			return NXTBee.this.readInbound.read(cbuf, 0, cbuf.length);
		}

		/**
		 * Read bytes into a portion of an array.
		 * This method will block until some input is available,
		 * an I/O error occurs, or the end of the stream is reached.
		 *
		 * @param cbuf Destination buffer.
		 * @param off Offset at which to start storing bytes.
		 * @param len Maximum number of bytes to read.
		 * @return The number of bytes read, or -1 if the end of
		 *   the stream has been reached
		 * @throws IOException if the stream is closed.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public int read(byte[] cbuf, int off, int len) throws IOException {
			synchronized (NXTBee.this){
				return NXTBee.this.readInbound.read(cbuf, off, len);
			}
		}
	
		/**
		 * Reset the stream.
		 * @throws IOException if the stream is closed.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public void reset() throws IOException {
			synchronized (NXTBee.this){
				if (inputStreamClosed) throw new IOException("InputStream has been closed; cannot reset a closed InputStream.");
				NXTBee.this.readInbound.reset();
			}
		}

		/**
		 * Skip bytes.
		 * This method will block until some bytes are available,
		 * an I/O error occurs, or the end of the stream is reached.
		 *
		 * @param n The number of bytes to skip
		 * @return The number of bytes actually skipped
		 * @throws IllegalArgumentException if n is negative.
		 * @throws IOException if the stream is closed.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public long skip(long n) throws IOException, IllegalArgumentException {
			synchronized (NXTBee.this){
				return NXTBee.this.readInbound.skip(n);
			}
		}

	}

	/**
	 * Class for writing to a circular byte buffer.
	 * If the buffer is full, the writes will either block
	 * until there is some space available or throw an IOException
	 * based on the CircularByteBuffer's preference.
	 *
	 * @since ostermillerutils 1.00.00
	 */

	protected class NXTBeeOutputStream extends OutputStream {
		/**
		 * Close the stream, flushing it first.
		 * This will cause the InputStream associated with this circular buffer
		 * to read its last bytes once it empties the buffer.
		 * Once a stream has been closed, further write() or flush() invocations
		 * will cause an IOException to be thrown. Closing a previously-closed stream,
		 * however, has no effect.
		 *
		 * @throws IOException never.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public void close() throws IOException {
			synchronized (NXTBee.this){
				NXTBee.this.writeOutbound.close();
			}
		}
		
		/**
		 * Flush the stream.
		 *
		 * @throws IOException if the stream is closed.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public void flush() throws IOException {
			synchronized (NXTBee.this){
				if (outputStreamClosed) throw new IOException("OutputStream has been closed; cannot flush a closed OutputStream.");
				if (inputStreamClosed) throw new IOException("Buffer closed by inputStream; cannot flush.");
			}
			// this method needs to do nothing
		}

		/**
		 * Write an array of bytes.
		 * If the buffer allows blocking writes, this method will block until
		 * all the data has been written rather than throw an IOException.
		 *
		 * @param cbuf Array of bytes to be written
		 * @throws BufferOverflowException if buffer does not allow blocking writes
		 *   and the buffer is full.  If the exception is thrown, no data
		 *   will have been written since the buffer was set to be non-blocking.
		 * @throws IOException if the stream is closed, or the write is interrupted.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public void write(byte[] cbuf) throws IOException {
			NXTBee.this.writeOutbound.write(cbuf, 0, cbuf.length);
		}

		/**
		 * Write a portion of an array of bytes.
		 * If the buffer allows blocking writes, this method will block until
		 * all the data has been written rather than throw an IOException.
		 *
		 * @param cbuf Array of bytes
		 * @param off Offset from which to start writing bytes
		 * @param len - Number of bytes to write
		 * @throws BufferOverflowException if buffer does not allow blocking writes
		 *   and the buffer is full.  If the exception is thrown, no data
		 *   will have been written since the buffer was set to be non-blocking.
		 * @throws IOException if the stream is closed, or the write is interrupted.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public void write(byte[] cbuf, int off, int len) throws IOException {
			synchronized (NXTBee.this){
				NXTBee.this.writeOutbound.write(cbuf, off, len);
			}
		}
			
		/**
		 * Write a single byte.
		 * The byte to be written is contained in the 8 low-order bits of the
		 * given integer value; the 24 high-order bits are ignored.
		 * If the buffer allows blocking writes, this method will block until
		 * all the data has been written rather than throw an IOException.
		 *
		 * @param c number of bytes to be written
		 * @throws BufferOverflowException if buffer does not allow blocking writes
		 *   and the buffer is full.
		 * @throws IOException if the stream is closed, or the write is interrupted.
		 *
		 * @since ostermillerutils 1.00.00
		 */
		@Override public void write(int c) throws IOException {
			synchronized (NXTBee.this){
				NXTBee.this.writeOutbound.write(c);
			}
		}		
	}
}

