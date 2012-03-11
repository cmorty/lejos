package lejos.pc.tools.debug;

import java.util.ArrayList;

import js.tinyvm.DebugData;

/**
 * Proxy filter for Packets from the NXT
 * 
 * @author Felix Treede
 *
 */
class NXTListener extends ProxyListener implements JDWPConstants {

	private static final int MIN_VERSION = 0x00000901;
	DebuggerListener debuggerListener;

	NXTListener(DebugProxyTool tool, Connection conn, DebugData debugData) {
		super(tool, "DebuggerListener");
		this.conn = conn;
		data = debugData;
	}

	@SuppressWarnings("boxing")
	@Override
	public void run() {

		boolean handled;
		PacketStream ps;

		this.myThread = Thread.currentThread();

		conn.start(this);
		synchronized (this) {
			Ready = true;
			this.notify();
		}

		ps = new PacketStream(this, CSET_NXT, NXT_HANDSHAKE);
		ps.writeString("LeJOS NXJ debugger agent");
		ps.writeByte((byte) 1); // Major version
		ps.writeByte((byte) 0); // minor version
		try {
			ps.send();
			ps.waitForReply();
		} catch (Exception e) {
			log("Exception during handshake");
			log(e);
			tool.stop();
			return;
		}
		int rawVersion = ps.readInt();

		if (rawVersion < MIN_VERSION) {
			log("Insufficient VM version");
			tool.stop();
			return;
		}
		debuggerListener.version = String.format("%d.%d.%d", rawVersion & 0xFF, (rawVersion >> 8) & 0xFF, (rawVersion >> 16) & 0xFF);
		synchronized (debuggerListener) {// Wake the debuggerlistener who waited
// for the handshake
			debuggerListener.notifyAll();
		}

		try {
			while (!stop) {
				PacketStream in;

				handled = false;
				Packet p = waitForPacket();
				debug("From NXT " + p);
				if (p == null) {
					debuggerListener.setStop();
					break; // must be time to quit
				}

//				debug("From NXT "+p);

				in = new PacketStream(this, p);
				if ((p.flags & Packet.Reply) == 0) {
					switch (p.cmdSet) {
					case CSET_NXT:
						handled = true;
						doNXTCmdset(in);
						break;
					}
				}
				if (!handled) {
					debuggerListener.send(p);
				}
			}
		} catch (ProxyConnectionException e) {
			debuggerListener.setStop();
			return;
		}
	}

	@SuppressWarnings("boxing")
	private boolean doNXTCmdset(PacketStream in) {
		boolean handled = false;
		PacketStream ps;
		switch (in.cmd()) {
		case NXT_STEP_LINE_INFO:
			int methodId = in.readMethodId();
			int pc = in.readUnsignedShort();

			DebugData.MethodData method = data.methodData.get(methodId);

			int line=data.getLineNumber(methodId, pc);
			
			ArrayList<Integer> pcs=new ArrayList<Integer>();
			
			for (int i = pc; i < method.codeLength; i++) {
				if(line==data.getLineNumber(methodId, i)){
					pcs.add(i);
				}
			}

			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

			ps.writeInt(pcs.size());
			for(Integer lpc:pcs){
				ps.writeShort(lpc.intValue());
			}
			
			ps.send();
			handled = true;
			break;
		}
		return handled;
	}

	@SuppressWarnings("unused")
	private void sendErrorReply(ProxyListener proxy, PacketStream in, short errorCode) throws ProxyConnectionException {
		PacketStream ps = new PacketStream(proxy, in.id(), Packet.Reply, errorCode);
		ps.send();
	}
}
