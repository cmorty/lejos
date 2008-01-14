package lejos.nxt.comm;

import java.io.*;
import javax.microedition.io.*;


/**
 * Provides a Bluetooth connection
 * Supports both packetized and stream based commincation.
 * Blocking and non-blocking I/O.
 * Notes:
 * Because of the limited buffer space and the way that several connections
 * have to share the interface to the Bluetooth device data may be lost. This
 * will happen if a switch into command mode is required when there is data
 * arriving from the remote connection that can not be placed into the input
 * buffer. Every attempt is made to avoid this but it can happen. Application
 * programs can help avoid this problem by:
 * 1) Using just a single Bluetooth connection
 * 2) Using Bluetooth commands while data transfers are in progress.
 * 3) Performing application level flow control to avoid more then 256 bytes
 *    of data being sent from the remote side at any one time.
 * 4) Reading any pending data as soon as possible.
 * If data is lost then calls to read and write will return -2 to indicate the
 * problem. If using packet mode then the input stream can be re-synchronized
 * by issuing a read to discard the partial packet which may be in the input
 * buffer.
 */
public class BTConnection implements StreamConnection
{
	static final int CS_IDLE = 0;
	static final int CS_DISCONNECTED = 1;
	static final int CS_CONNECTED = 2;
	static final int CS_DATALOST = 3;
	static final int CS_DISCONNECTING = 4;
	
	private static int BTC_BUFSZ = 256;
	private static int BTC_CLOSETIMEOUT1 = 1000;
	private static int BTC_CLOSETIMEOUT2 = 250;
	private static int BTC_FLUSH_WAIT = 10;
	
	public static final int AM_DISABLE = 0;
	public static final int AM_ALWAYS = 1;
	public static final int AM_OUTPUT = 2;

	int state = CS_IDLE;
	int chanNo;
	byte handle;
	int header = 2;
	int switchMode;
	byte [] inBuf;
	byte [] outBuf;
	int inCnt;
	int inOffset;
	int outCnt;
	int outOffset;
	int pktOffset;
	int pktLen;
	InputStream is;
	OutputStream os;
	static int inBufSz = BTC_BUFSZ;
	static int outBufSz = BTC_BUFSZ;


	public BTConnection(int chan)
	{
		state = CS_IDLE;
		chanNo = chan;
		is = null;
		os = null;
	}
	
	synchronized void reset()
	{
		// Called by the low level implementation if things go wrong!
		state = CS_IDLE;
		inBuf = null;
		outBuf = null;
		notifyAll();
	}

	/**
	 * Bind the low level I/O handle to a connection object
	 * set things up ready to go.
	 */
	synchronized void bind(byte handle)
	{
		if (inBuf == null )
			inBuf = new byte[inBufSz];
		if (outBuf == null)
			outBuf = new byte[outBufSz];
		inCnt = 0;
		inOffset = 0;
		outCnt = 0;
		outOffset = 0;
		state = CS_CONNECTED;
		header = 2;
		switchMode = AM_ALWAYS;
		this.handle = handle;
		pktOffset = -header;
		pktLen = 0;
	}

	/**
	 * Called when the remote side of the connection disconnects.
	 * Mark the connection as now disconected.
	 */
	synchronized boolean disconnected()
	{
		// Connection has been closed wake up anything waiting
		//1 Debug.out("Disconnected " + handle + "\n");
		notifyAll();
		// don't allow multiple disconnects, or disconnect of a closed connection'
		if (state <= CS_DISCONNECTED) return false;
		state = CS_DISCONNECTED;
		outCnt = 0;
		return true;
	}
	
	/**
	 * Close the connection. Flush any pending output. Inform the remote side
	 * that the connection is now closed. Free resources.
	 */
	public void close()
	{
		//Debug.out("Close\n");
		if (state == CS_IDLE) return;
		synchronized (this)
		{
			if (state >= CS_CONNECTED)
				state = CS_DISCONNECTING;
		}
		//Debug.out("Close1\n");
		// If we have any output pending give it chance to go... and discard
		// any input. We allow longer if we have pending output, just in case we
		// need to switch streams.
		for(int i = 0; state == CS_DISCONNECTING && (i < BTC_CLOSETIMEOUT2 || (outCnt > 0 && i < BTC_CLOSETIMEOUT1)); i++ )
		{
			read(null, inBuf.length, false);
			try{Thread.sleep(1);} catch (Exception e) {}
		}
		// Dump any remaining output
		outCnt = 0;
		//Debug.out("Close2\n");
		if (state == CS_DISCONNECTING)
			// Must not be synchronized here or we get a deadlock
			Bluetooth.closeConnection(handle);
		synchronized(this)
		{
		//Debug.out("Close3\n");
			while (state == CS_DISCONNECTING)
				try{wait();}catch(Exception e){}
		//Debug.out("Close4\n");
			state = CS_IDLE;
			inBuf = null;
			outBuf = null;
		}
		//Debug.out("Close complete\n");

	}
	
	/**
	 * Low level output function. Take any data in the output buffer and write
	 * it to the device. Called by the Bluetooth thread when this channel is
	 * active, to perform actual data I/O.
	 */
	synchronized void send()
	{
		//Debug.out("send\n");
		if (outOffset >= outCnt) return;
		// Transmit the data in the output buffer
		int cnt = Bluetooth.btWrite(outBuf, outOffset, outCnt - outOffset);
		//1 Debug.out("Send " + cnt + "\n");
		outOffset += cnt;
		if (outOffset >= outCnt)
		{
			//Debug.out("Send complete\n");
			outOffset = 0;
			outCnt = 0;
			notifyAll();
		}
		else
		{
			//Debug.out("send remaining " + (outCnt - outOffset) + "\n");
		}
	}

	/**
	 * Attempt to write bytes to the Bluetooth connection. Optionally wait if it
	 * is not possible to write at the moment. Supports both packet and stream
	 * write opperations. If in packet mode a set of header bytes indicating
	 * the size of the packet will be sent ahead of the data.
	 * NOTE: If in packet mode and writing large packets (> 254 bytes), then
	 * the blocking mode (wait = true), should be used to ensure that the packet
	 * is sent correctly.
	 * @param	data	The data to be written.
	 * @param	len		The number of bytes to write.
	 * @param	wait	True if the call should block until all of the data has
	 *					been sent.
	 * @return			> 0 number of bytes written.
	 *					0 Request would have blocked (and wait was false).
	 *					-1 An error occurred
	 *					-2 Data has been lost (See notes above).
	 */
	public synchronized int write(byte [] data, int len, boolean wait)
	{
		// Place the data to be sent in the output buffer. If there is no
		// space and wait is true then wait for space.
		int offset = -header;
		int hdr = len;

		//1 Debug.out("write " + len +" bytes\n");
		if (state == CS_DATALOST)
		{
			state = CS_CONNECTED;
			return -2;
		}
		if (state != CS_CONNECTED) return -1;
		if (outCnt > 0 && !wait) return 0;
		// Make sure we have a place to put the data
		while (offset < len)
		{
			while (outCnt >= outBuf.length)
			{
				//Debug.out("Buffer cnt " + outCnt + "\n");
				if (!wait && header == 0) return offset;
				//Debug.out("Waiting in write\n");
				try {wait();} catch(Exception e){}
				//Debug.out("Wakeup state " + state + "\n");
				if (state != CS_CONNECTED) return offset;
			}
			if (offset < 0)
			{
				// need to add header byte(s)
				outBuf[outCnt++] = (byte) hdr;
				hdr >>= 8;
				offset++;
			}
			else
			{
				int cnt = (outBuf.length - outCnt);
				if (cnt > len - offset) cnt = len - offset;
				System.arraycopy(data, offset, outBuf, outCnt, cnt);
				outCnt += cnt;
				offset += cnt;
			}
		}
		return offset;
	}

	public int write(byte [] data, int len)
	{
		return write(data, len, true);
	}
	
	/**
	 * Low level input function. Called by the Bluetooth thread to transfer
	 * input from the system into the input buffer.
	 */
	synchronized void recv()
	{
		//1 Debug.out("recv\n");
		// Read data into the input buffer
		while (inCnt < inBuf.length)
		{
			if (inCnt == 0) inOffset = 0;
			int offset = (inOffset + inCnt) % inBuf.length;
			int len = (offset >= inOffset ? inBuf.length - offset : inOffset - offset);
			//Debug.out("inCnt " + inCnt + " inOffset " + inOffset + " offset " + offset + " len " + len + "\n");
			int cnt = Bluetooth.btRead(inBuf, offset, len);
			if (cnt <= 0) break;
			inCnt += cnt;
			//1 Debug.out("recv " + inCnt + "\n");
		}
		if (inCnt > 0) notifyAll();
	}
	
	/**
	 * Attempt to read data from the connection. Optionally wait for data to
	 * become available. Supports both packet and stream mode operations. When
	 * in packet mode the packet length bytes are automatically processed. The
	 * read will return just a single packet. If the packet is larger then the
	 * requested length then the rest of the packet will be returned in the
	 * following reads. If wait is true then in packet mode the call will wait
	 * until either the entire packet can be read or outLen bytes are available.
	 * In stream mode the call will return if at least 1 byte has been read.
	 * @param	data	Location to return the data. If null the data is discarded.
	 * @param	outLen	Max number of bytes to read.
	 * @param	wait	Should the call block waiting for data.
	 * @return			> 0 number of bytes read.
	 * @return			0 no bytes available (and wait was false).
	 *					-1 an error occurred.
	 *					-2 data lost (see notes).
	 */
	public synchronized int read(byte [] data, int outLen, boolean wait)
	{
		// If wait is true wait until we can read at least one byte. if the
		// packet has a header and data is not large enough for the data then
		// the next read will continue to read the packet
		int offset = 0;
		//Debug.out("read\n");
		if (header == 0)
		{
			// Stream mode just read what we can
			pktOffset = 0;
			pktLen = outLen;
		}
		if (state == CS_IDLE) return -1;
		if (state == CS_DATALOST)
		{
			state = CS_CONNECTED;
			return -2;
		}
		if (state == CS_DISCONNECTED && inCnt <= 0) return -1;
		if (!wait && inCnt <= 0) return 0;
		while (pktOffset < pktLen)
		{
			//Debug.out(" inCnt " + inCnt + " pktOffset " + pktOffset + " pktLen " + pktLen + "\n");
			// Make sure we have something to read
			while (inCnt <= 0)
			{
				//Debug.out("About to wait inOff " + inOffset + " inCnt " + inCnt + "\n");
				if (!wait) return offset;
				try{wait();}catch(Exception e){}
				if (state != CS_CONNECTED) return offset;
				//Debug.out("wakeup cnt " + inCnt + "\n");
			}
			if (pktOffset < 0)
			{
				// Deal with the header, at this point we have at least one header byte
				pktLen += ((int) inBuf[inOffset++] & 0xff) << (header + pktOffset)*8;
				pktOffset++;
				inCnt--;
				//Debug.out("Header len " +pktLen + " offset " + pktOffset + "\n");
			}
			else
			{
				if (offset >= outLen) return offset;
				// Transfer as much as we can in one go...
				int len = (inOffset + inCnt > inBuf.length ? inBuf.length - inOffset : inCnt);
				if (len > outLen - offset) len = outLen - offset;
				if (len > pktLen - pktOffset) len = pktLen - pktOffset;
				if (data != null)
					System.arraycopy(inBuf, inOffset, data, offset, len);
				offset += len;
				inOffset += len;
				pktOffset += len;
				inCnt -= len;
				// If not in packet mode we can return anytime now we have some data
				if (header == 0) wait = false;
			}
			inOffset = inOffset % inBuf.length;
		}
		// End of packet set things up for next time
		//Debug.out("Read len " + offset + "\n");
		pktOffset = -header;
		pktLen = 0;
		return offset;
	}
	
	public int read(byte [] data, int len)
	{
		return read(data, len, true);
	}
	
	/**
	 * Indicate the number of bytes available to be read. Supports both packet
	 * mode and stream connections. 
	 * @param	what	0 (all modes) return the number of bytes that can be
	 *					read without blocking.
	 *					1 (packet mode) return the number of bytes still to be
	 *					read from the current packet.
	 *					2 (packet mode) return the length of the current packet.
	 */
	public synchronized int available(int what)
	{
		if (state == CS_IDLE) return -1;
		if (state == CS_DATALOST)
		{
			state = CS_CONNECTED;
			return -2;
		}
		if (header > 0)
		{
			// if not in a packet try and read the header
			if (pktOffset < 0) read(null, 0, false);
			if (pktOffset < 0) return 0;
			if (what == 2) return pktLen; 
			int ret = pktLen - pktOffset;
			// If we have been asked what is actually available limit it.
			// otherwise we return the number of bytes in the current packet
			if (what == 0 && ret > inCnt) ret = inCnt;
			return ret;
		}
		else
			return inCnt;
	}
	
	public int available()
	{
		return available(0);
	}
	
	/**
	 * Set operating mode. Controls the packet/stream mode of this channel.
	 * For packet mode it defines the header size to be used.
	 * @param mode	Size of header, 0 indicates stream mode.
	 */
	public void setIOMode(int mode)
	{
		header = mode;
	}
	
	/**
	 * Read a packet from the stream. Do not block and for small packets
	 * (< 254 bytes), do not return a partial packet.
	 * @param	buf		Buffer to read data into.
	 * @param	len		Number of bytes to read.
	 * @return			> 0 number of bytes read.
	 *					other values see read.
	 */
	public int readPacket(byte buf[], int len)
	{
		// Check to see if we have a full packet if the packet is small
		int pkt = available(1);
		if (pkt == -2) return -2;
		if (pkt < 255 && available(0) < pkt) return 0;
		return read(buf, len, false);
	}
	
	/**
	 * Send a data packet.
	 * Must be in data mode.
	 * @param buf the data to send
	 * @param bufLen the number of bytes to send
	 */
	public void sendPacket(byte [] buf, int bufLen)
	{
		if (bufLen <= 254)
	    {
			write(buf, bufLen, false);
	    }
	}
	
	/**
	 * Low level function called by the Bluetooth thread. It basically answers
	 * the question: Should I switch to this channel and perform I/O? The answer
	 * to this question can be controlled using the setActiveMode method.
	 * @ return			true if the channel is interesting!
	 */
	synchronized boolean needsAttention()
	{
		//1 if (chanNo == 0) Debug.out("na s" + state + " i " + inCnt + "\n");
		//Debug.out("needs attention\n");
		// return true if we need to perform low level I/O on this channel
		if (state < CS_CONNECTED || switchMode == AM_DISABLE) return false;
		// If we have any output then need to send it
		if (outOffset < outCnt) return true;
		if (switchMode == AM_OUTPUT) return false;
		// If we do not have any input need to see if there is more waiting
		if (inCnt <= 0) return true;
		return false;
	}
	
	/**
	 * Set the channel switching mode. Allows control of when we will switch to
	 * this channel. By default we will switch to this channel to check for
	 * input. However if AM_OUTPUT is set we only switch if we have output
	 * waiting to be sent.
	 * @param	mode	The switch control mode.
	 */
	public void setActiveMode(int mode)
	{
		switchMode = mode;
	}

	/**
	 * Set the size to be used for the input buffer. This will effect all
	 * new connections after this call is made.
	 * @param	sz	The required size. if < 0 the default size will be used
	 */
	public static void setInputBufferSize(int sz)
	{
		inBufSz = (sz >= 0 ? sz : BTC_BUFSZ);
	}
	
	/**
	 * Set the size to be used for the output buffer. This will effect all
	 * new connections after this call is made.
	 * @param	sz	The required size. if < 0 the default size will be used
	 */
	public static void setOutputBufferSize(int sz)
	{
		outBufSz = (sz >= 0 ? sz : BTC_BUFSZ);
	}
	
	private boolean pendingInput()
	{
		return (Bluetooth.btPending() & Bluetooth.BT_PENDING_INPUT) != 0;
	}

	/**
	 * Prepare the low level Bluetooth interface for a switch into command mode.
	 * To switch to command mode we need to be sure that there is no pending
	 * input for this channel. To do this we ready any data into the available
	 * input buffers. If all else fails we discard data. When we return the
	 * interface should be ready to be switched.
	 */
	synchronized void flushInput()
	{
		// Need to be sure that there is no input in the input buffer before
		// we switch mode. 
		if (state == CS_IDLE) return;
		//Debug.out("Flush\n");
		// Try to empty the low level input buffer while giving the 
		// application chance to help by reading the data.
		int timeout = (int)System.currentTimeMillis() + BTC_FLUSH_WAIT;
		while (timeout > (int)System.currentTimeMillis())
		{
			// Read as much as we can
			while (pendingInput() && inCnt < inBuf.length)
				recv();
			// Give the app chance to process it
			try{wait(1);}catch(Exception e){}
		}
		if (!pendingInput()) return;
		//1 Debug.out("Dropping packets\n");
		// If we still have input we are now in big trouble we will have
		// to discard data. Note even if we read all of the data we need
		// to linger a little to see if more arrives.
		timeout = (int)System.currentTimeMillis() + BTC_FLUSH_WAIT;
		while (pendingInput() || (timeout > (int)System.currentTimeMillis()))
		{
			while (read(null, inBuf.length, false) > 0)
				;
			recv();
		}
		// Mark the channel as having lost data
		if (state == CS_CONNECTED)
			state = CS_DATALOST;
	}
	
	/**
	 * Return the InputStream for this connection.
	 * 
	 * @return the input stream
	 */
	public InputStream openInputStream() throws IOException {
		return (is != null ? is : new BTInputStream(this));
	}

	/**
	 * Return the OutputStream for this connection
	 * 
	 * @return the output stream
	 */
	public OutputStream openOutputStream() throws IOException {
		return (os != null ? os : new BTOutputStream(this));
	}

	/**
	 * Return the DataInputStream for this connect
	 * 
	 * @return the data input stream
	 */
	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}

	/**
	 * Return the DataOutputStream for this connection.
	 * 
	 * @return the data output stream
	 */
	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}

	/**
	 * Close the stream for this connection.
	 * This suspends the connection and switch the BC4 chip to command mode.
	 *
	 */
	public void closeStream() {
		// Nothing to do for Bluetooth
	}
	
	/**
	 * Open the stream for this connection.
	 * This resumes the connection and switches the BC4 chip to data mode.
	 *
	 */
	public void openStream() {
		// Nothing to do for Bluetooth
	}
	
	/**
	 * Get the signal strength of this connection.
	 * This necessitates closing and reopening the data stream.
	 *  
	 * @return a value from 0 to 255
	 */
	public int getSignalStrength() {
		int strength = Bluetooth.getSignalStrength((byte) handle); 
		return strength;
	}
}