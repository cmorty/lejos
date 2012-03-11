/*
 * Copyright 1990-2007 Sun Microsystems, Inc. All Rights Reserved. DO NOT ALTER
 * OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 only, as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License version 2 for
 * more details (a copy is included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License version 2
 * along with this work; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara, CA
 * 95054 or visit www.sun.com if you need additional information or have any
 * questions.
 */

package lejos.pc.tools.debug;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import js.tinyvm.DebugData;

abstract class ProxyListener extends Thread {

	Map<String, Packet> waitingQueue = new HashMap<String, Packet>(8, 0.75f);
	protected List<Packet> packetQueue;
	protected DebugProxyTool tool;
	protected Connection conn;
	protected boolean Ready = false;
	protected boolean stop = false;
	protected Thread myThread;
	protected DebugData data;

	ProxyListener(DebugProxyTool tool, String name) {
		super(tool.getThreadGroup(), name);
		this.tool = tool;
		packetQueue = Collections.synchronizedList(new LinkedList<Packet>());
	}

	ProxyListener(ThreadGroup group, String name) {
		super(group, name);
		packetQueue = Collections.synchronizedList(new LinkedList<Packet>());
	}

	void newPacket(Packet p) {

		if (p == null) {
			synchronized (packetQueue) {
				packetQueue.notify();
			}
			return;
		}
		synchronized (packetQueue) {
			packetQueue.add(p);
			packetQueue.notify();
		}
	}

	Packet waitForPacket() {

		synchronized (packetQueue) {
			while (packetQueue.size() == 0) {
				try {
					packetQueue.wait();
				} catch (InterruptedException e) {
					throw new ProxyConnectionException();
				}
			}
		}
		return packetQueue.remove(0);
	}

	void replyReceived(Packet p) {
		Packet p2;
		if (p == null) {
			synchronized (waitingQueue) {
				Iterator<?> iter = waitingQueue.values().iterator();
				while (iter.hasNext()) {
					p2 = (Packet) iter.next();
					synchronized (p2) {
						p2.notify();
					}
				}
			}
			return;
		}

		String idString = String.valueOf(p.id);
		synchronized (waitingQueue) {
			p2 = waitingQueue.get(idString);
			if (p2 != null)
				waitingQueue.remove(idString);
		}
		if (p2 == null) {
			System.err.println("Received reply with no sender!");
			return;
		}
		p2.errorCode = p.errorCode;
		p2.data = p.data;
		p2.replied = true;
		synchronized (p2) {
			p2.notify();
		}

	}

	void waitForReply(Packet p) {

		synchronized (p) {
			while (!p.replied) {
				try {
					p.wait();
				} catch (InterruptedException e) {
					debug(this + " waitForReply Interrupted");
					throw new ProxyConnectionException();
				}
			}
			if (!p.replied)
				throw new RuntimeException();
		}
	}
	
	protected void debug(String message) {
		tool.debug(message);
	}


	protected void log(String message) {
		tool.log(message);
	}

	protected void log(Throwable t) {
		tool.log(t);
	}
	
	

	public synchronized void send(Packet p) throws ProxyConnectionException {
		while (!Ready) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		String id = String.valueOf(p.id);
		synchronized (waitingQueue) {
			if ((p.flags & Packet.Reply) == 0 && p.id < 0) {
				waitingQueue.put(id, p);
			}
		}
		try {
			conn.send(p);
		} catch (IOException e) {
			throw new ProxyConnectionException();
		}
	}

	public void setStop() {
		myThread.interrupt();
		stop = true;
		synchronized (packetQueue) {
			packetQueue.notify();
		}
		synchronized (waitingQueue) {
			waitingQueue.notify();
		}
		if (conn != null) {
			conn.close();
		}
	}

} // ProxyListener
