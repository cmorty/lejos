package lejos.pc.tools.debug;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Connection implements Closeable, Runnable {

	private Closeable conn;

	private DataOutputStream out;
	private DataInputStream in;

	private ProxyListener proxy;

	private boolean stop;
	private Thread thread;

	private String name;

	public Connection(String name, Closeable conn, InputStream inputStream, OutputStream outputStream) {
		this.conn = conn;
		this.name = name;
		in = new DataInputStream(new BufferedInputStream(inputStream));
		out = new DataOutputStream(new BufferedOutputStream(outputStream));
	}

	void start(ProxyListener proxy) {
		this.proxy = proxy;
		thread = new Thread(proxy.getThreadGroup(), this, name);
		thread.setDaemon(true);
		thread.start();
	}

	public boolean isOpen() {
		return thread != null && thread.isAlive();
	}

	public void close() {
		stop = true;
		if (thread != null)
			thread.interrupt();
	}

	byte receiveByte() throws IOException {
		int b = in.read();
		return (byte) b;
	}

	void sendByte(byte b) throws IOException {
		out.write(b);
		out.flush();
	}

	public void run() {
		// Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		try {
			while (!stop) {
				Packet p = receivePacket();
				if ((p.flags & Packet.Reply) == 0 || p.id >= 0) {
					proxy.newPacket(p);
				} else {
					// A reply to a packet we generated and sent to VM
					proxy.replyReceived(p);
				}
			}
		} catch (EOFException ignore) {// When we recieve EOF, we want to stop
			proxy.setStop();
		} catch (Exception e) {
			e.printStackTrace();
			proxy.setStop();
		} finally {
			try {
				out.flush();
				out.close();
				in.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	Packet receivePacket() throws IOException {

		Packet p = new Packet();

		// length
		int length = in.readInt();

		// id
		p.id = in.readInt();

		p.flags = in.readByte();
		if ((p.flags & Packet.Reply) == 0) {
			p.cmdSet = in.readByte();
			p.cmd = in.readByte();
		} else {
			p.errorCode = in.readShort();
		}

		length -= 11; // subtract the header

		if (length < 0) {
			// This shouldn't be happening!
			throw new IOException("packet length < 0");
		}
		p.data = new byte[length];

		int n = 0;
		while (n < p.data.length) {
			int count = in.read(p.data, n, p.data.length - n);
			if (count < 0) {
				throw new EOFException();
			}
			n += count;
		}

		return p;
	}

	void send(Packet p) throws IOException {

		int length = p.data.length + 11;

		// Length
		out.writeInt(length);

		// id
		out.writeInt(p.id);

		out.write(p.flags);

		if ((p.flags & Packet.Reply) == 0) {
			out.write(p.cmdSet);
			out.write(p.cmd);
		} else {
			out.writeShort(p.errorCode);
		}
		out.write(p.data);

		out.flush();
	}

}