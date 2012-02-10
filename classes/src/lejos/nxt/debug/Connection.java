package lejos.nxt.debug;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import lejos.nxt.VM;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;

class Connection extends Thread{

	private static class ConnectThread extends Thread {
		private NXTCommConnector connector;

		private NXTConnection[] out;

		private boolean finished;

		ConnectThread other;

		private ConnectThread(NXTCommConnector connector, NXTConnection[] out) {
			super();
			this.connector = connector;
			this.out = out;
		}

		@Override
		public void run() {
			NXTConnection conn = connector.waitForConnection(0,
					NXTConnection.PACKET);
			finished = true;
			synchronized (out) {
				if (conn != null && out[0] == null)
					out[0] = conn;
			}
			while (!other.finished)
				other.connector.cancel();
		}
	}

	/**
	 * Opens a connection from bluetooth or usb.
	 * @return the opened connection
	 */
	static NXTConnection openAnyConnection() {
		ConnectThread bt, usb;

		NXTConnection[] out = new NXTConnection[1];
		usb = new ConnectThread(USB.getConnector(), out);
		bt = new ConnectThread(Bluetooth.getConnector(), out);

		usb.other = bt;
		bt.other = usb;

		usb.start();
		bt.start();

		try {
			usb.join();
			bt.join();
		} catch (InterruptedException e) {
			// ignore
		}

		return out[0];
	}

	protected DataOutputStream out;

	protected DataInputStream in;
	protected JDWPDebugServer controller;

	NXTConnection conn;

	private boolean running=true;

	Connection(JDWPDebugServer controller, NXTConnection conn) {
		this.controller = controller;
		this.conn = conn;
		in = conn.openDataInputStream();
		out = conn.openDataOutputStream();
	}

	void close() {
		running=false;
	}

	byte receiveByte() throws IOException {
		int b = in.read();
		return (byte) b;
	}

	void sendByte(byte b) throws IOException {
		out.write(b);
		out.flush();
	}

	@Override
	public void run() {
	    VM.updateThreadFlags(this, VM.VM_THREAD_SYSTEM, 0);
		setDaemon(true);
		 setPriority(Thread.MAX_PRIORITY);
		try {
			while (running) {
				Packet p = receivePacket();
				if ((p.flags & Packet.Reply) == 0) {
					controller.newPacket(p);
				} else {
					// A reply to a packet we generated and sent to VM
					controller.replyReceived(p);
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
			controller.exit(true);
		} finally {
			try {
				out.flush();
				out.close();
				in.close();
				conn.close();
			} catch (Exception e2) {
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