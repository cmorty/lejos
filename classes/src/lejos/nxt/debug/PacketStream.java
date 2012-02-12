

package lejos.nxt.debug;

import java.io.ByteArrayOutputStream;

import lejos.internal.charset.CharsetDecoder;
import lejos.internal.charset.CharsetEncoder;
import lejos.internal.charset.UTF8Decoder;
import lejos.internal.charset.UTF8Encoder;


class PacketStream {

	static final CharsetDecoder dec = new UTF8Decoder();
	static final CharsetEncoder enc = new UTF8Encoder();

	final JDWPDebugServer proxy;
	private int outCursor = 0;
	final Packet pkt;
	private ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
	private boolean isCommitted = false;
	private int id;
	byte[] data;

	PacketStream(JDWPDebugServer proxy, int cmdSet, int cmd) {
		this.proxy = proxy;
		this.pkt = new Packet();
		id = pkt.id;
		pkt.cmdSet = (byte) (cmdSet & 0xFF);
		pkt.cmd = (byte) (cmd & 0xFF);
	}

	PacketStream(JDWPDebugServer proxy, int id, byte flags, short errorCode) {
		this.pkt = new Packet();
		this.proxy = proxy;
		this.pkt.id = id;
		this.id = id;
		this.pkt.errorCode = errorCode;
		this.pkt.flags = flags;
	}

	PacketStream(JDWPDebugServer proxy, Packet p) {
		this.pkt = p;
		this.proxy = proxy;
		this.id = p.id;
		this.data = p.data;
	}

	int id() {
		return id;
	}

	/**
	 * send with a specified header, containing eg. a count value determinated
	 * later
	 * 
	 * @param header
	 * @throws ProxyConnectionException
	 */
	void send(byte[] header) throws ProxyConnectionException {
		if (!isCommitted) {
			pkt.data = dataStream.toByteArray();
			System.arraycopy(header, 0, pkt.data, 0, header.length);
			proxy.send(pkt);
			isCommitted = true;
		}
	}

	void send() throws ProxyConnectionException {
		if (!isCommitted) {
			pkt.data = dataStream.toByteArray();
			proxy.send(pkt);
			isCommitted = true;
		}
	}

	void waitForReply() throws PacketStreamException {
		if (!isCommitted) {
			throw new ProxyConnectionException("waitForReply without send");
		}
		proxy.waitForReply(pkt);

		if (pkt.errorCode != Packet.ReplyNoError) {
			throw new PacketStreamException(String.valueOf(pkt.errorCode));
		}
		data = pkt.data;
	}

	void writeBoolean(boolean data) {
		if (data) {
			dataStream.write(1);
		} else {
			dataStream.write(0);
		}
	}

	void writeByte(int data) {
		dataStream.write(data & 0xFF);
	}

	void writeChar(char data) {
		dataStream.write((byte) ((data >>> 8) & 0xFF));
		dataStream.write((byte) ((data >>> 0) & 0xFF));
	}

	void writeShort(int data) {
		dataStream.write((byte) ((data >>> 8) & 0xFF));
		dataStream.write((byte) ((data >>> 0) & 0xFF));
	}

	void writeInt(int data) {
		dataStream.write((byte) ((data >>> 24) & 0xFF));
		dataStream.write((byte) ((data >>> 16) & 0xFF));
		dataStream.write((byte) ((data >>> 8) & 0xFF));
		dataStream.write((byte) ((data >>> 0) & 0xFF));
	}

	void writeLong(long data) {
		dataStream.write((byte) ((data >>> 56) & 0xFF));
		dataStream.write((byte) ((data >>> 48) & 0xFF));
		dataStream.write((byte) ((data >>> 40) & 0xFF));
		dataStream.write((byte) ((data >>> 32) & 0xFF));

		dataStream.write((byte) ((data >>> 24) & 0xFF));
		dataStream.write((byte) ((data >>> 16) & 0xFF));
		dataStream.write((byte) ((data >>> 8) & 0xFF));
		dataStream.write((byte) ((data >>> 0) & 0xFF));
	}

	void writeFloat(float data) {
		writeInt(Float.floatToIntBits(data));
	}

	void writeDouble(double data) {
		writeLong(Double.doubleToLongBits(data));
	}

	private void writeId(int size, int data) {
		switch (size) {
		case 1:
			writeByte(data);
			break;
		case 2:
			writeShort(data);
			break;
		case 4:
			writeInt(data);
			break;
		}
	}

	void writeClassId(int id) {
		writeId(JDWPConstants.SIZEOF_CLASS_ID, id + 1);
	}

	void writeFieldId(int id) {
		writeId(JDWPConstants.SIZEOF_FIELD_ID, id + 1);
	}

	void writeMethodId(int id) {
		writeId(JDWPConstants.SIZEOF_METHOD_ID, id + 1);
	}

	void writeFrameId(int id) {
		writeId(JDWPConstants.SIZEOF_FRAME_ID, id + 1);
	}

	void writeObjectId(int id) {
		writeId(JDWPConstants.SIZEOF_OBJECT_ID, id);
	}

	void writeByteArray(byte[] data) {

		dataStream.write(data, 0, data.length);
	}

	void writeString(String string) {
		if (string == null) {
			writeInt(0);
			return;
		}

		int cpCount = string.length();
		int len = 0;
		byte[] buf = new byte[cpCount * 4];// worst case
		for (int i = 0; i < cpCount;) {
			int codepoint = string.codePointAt(i);
			i += Character.charCount(codepoint);
			len = enc.encode(codepoint, buf, len);

		}
		writeInt(len);

		dataStream.write(buf, 0, len);
	}

	/**
	 * Read byte represented as one bytes.
	 */
	byte readByte() throws PacketStreamException {
		checkCursor(1);
		return (data[outCursor++]);
	}

	int readUnsignedByte() throws PacketStreamException {
		checkCursor(1);
		return (data[outCursor++]) & 0xFF;
	}

	/**
	 * Read boolean represented as one byte.
	 */
	boolean readBoolean() throws PacketStreamException {
		byte ret = readByte();
		return (ret != 0);
	}

	/**
	 * Read char represented as two bytes.
	 */
	char readChar() throws PacketStreamException {
		return (char) readUnsignedShort();
	}

	/**
	 * Read short represented as two bytes.
	 */
	short readShort() throws PacketStreamException {
		return (short) readUnsignedShort();
	}

	int readUnsignedShort() throws PacketStreamException {
		int b1, b2;

		checkCursor(2);
		b1 = data[outCursor++] & 0xff;
		b2 = data[outCursor++] & 0xff;

		return ((b1 << 8) + b2);
	}

	/**
	 * Read int represented as four bytes.
	 */
	int readInt() throws PacketStreamException {
		checkCursor(4);
		return (((data[outCursor++] & 0xff) << 24) + ((data[outCursor++] & 0xff) << 16) + ((data[outCursor++] & 0xff) << 8) + ((data[outCursor++] & 0xff)));
	}

	/**
	 * Read long represented as eight bytes.
	 */
	long readLong() throws PacketStreamException {
		return (((long)readInt() << 32) + readInt());
	}

	/**
	 * Read double represented as eight bytes.
	 */
	double readDouble() throws PacketStreamException {
		return Double.longBitsToDouble(readLong());
	}

	/**
	 * Read string represented as four byte length followed by characters of the
	 * string.
	 */
	String readString() throws PacketStreamException {
		String ret;
		int len = readInt();

		checkCursor(len);

		char[] cData = new char[len];
		int lim = outCursor + len;
		for (int i = 0; outCursor < lim;) {
			int codepoint = dec.decode(data, outCursor, lim);
			i += Character.toChars(codepoint, cData, i);
			int bc = dec.estimateByteCount(data, outCursor, lim);
			outCursor += bc;
		}

		ret = new String(cData);
		return ret;
	}

	private int readId(int size) throws PacketStreamException {
		switch (size) {
		case 1:
			return readUnsignedByte();
		case 2:
			return readUnsignedShort();
		case 4:
			return readInt();
		}
		return -1;
	}

	int readClassId() {
		return readId(JDWPConstants.SIZEOF_CLASS_ID) - 1;
	}

	int readFieldId() {
		return readId(JDWPConstants.SIZEOF_FIELD_ID) - 1;
	}

	int readMethodId() {
		return readId(JDWPConstants.SIZEOF_METHOD_ID) - 1;
	}

	int readFrameId() {
		return readId(JDWPConstants.SIZEOF_FRAME_ID) - 1;
	}

	int readObjectId() {
		return readId(JDWPConstants.SIZEOF_OBJECT_ID);
	}

//	Class<?> readClassId() throws PacketStreamException{
//		
//	}
//	
//	<T> T readObjectId(Class<T> cls)throws PacketStreamException{
//		int objectId=readID(4);
//		if(objectId==0)return null;
//		Object obj=null;
//		return cls.cast(obj);
//	}

	int skipBytes(int n) throws PacketStreamException {
		checkCursor(n);
		outCursor += n;
		return n;
	}

	byte cmd() {
		return pkt.cmd;
	}

	void checkCursor(int size) throws PacketStreamException {
		if ((outCursor + size) > data.length) {
			throw new PacketStreamException("Reading past buffer");
		}
	}
}
