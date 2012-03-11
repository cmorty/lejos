package lejos.pc.tools.debug;

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

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import js.tinyvm.DebugData;
import js.tinyvm.DebugData.ClassData;
import js.tinyvm.DebugData.FieldData;
import js.tinyvm.DebugData.LineNo;
import js.tinyvm.DebugData.LocalVar;
import js.tinyvm.DebugData.MethodData;

class DebuggerListener extends ProxyListener implements JDWPConstants {

	NXTListener nxtListener;

	boolean holdEvents;
	Queue<Packet> holdEventQueue = new LinkedList<Packet>();
	
	String version = "0.9.0";

	DebuggerListener(DebugProxyTool tool, Connection conn, DebugData debugData) {
		super(tool, "DebuggerListener");
		this.conn = conn;
		data = debugData;
	}
	
	void setHoldEvents(boolean b) {
		if (holdEvents == b)
			return;
		holdEvents = b;
		if (!b) {
			while (!holdEventQueue.isEmpty()) {
				Packet p = (Packet) holdEventQueue.poll();
				send(p);
			}
		}
	}
	
	@Override
	public synchronized void send(Packet p) throws ProxyConnectionException {
		if(holdEvents&&(p.cmdSet==CSET_EVENT)){
			holdEventQueue.offer(p);
		}
		super.send(p);
	}

	private void sendErrorReply(ProxyListener proxy, PacketStream in, short errorCode) throws ProxyConnectionException {
		debug("Error occured: "+errorCode);
		PacketStream ps = new PacketStream(proxy, in.id(), Packet.Reply, errorCode);
		ps.send();
	}

	byte getJDWPTypeTag(ClassData data) {
		if (data.signature.charAt(0) == '[') {
			return TYPE_TAG_ARRAY;
		}
		if ((data.modifiers & Modifier.INTERFACE) != 0)
			return TYPE_TAG_INTERFACE;
		return TYPE_TAG_CLASS;
	}

	@SuppressWarnings("unused")
	private byte getJDWPTag(String signature) {
		switch (signature.charAt(0)) {
		case 'L':
			if ("Ljava/lang/String;".equals(signature)) {
				return STRING_TAG;
			}
			if ("Ljava/lang/Thread;".equals(signature)) {
				return THREAD_TAG;
			}
			if ("Ljava/lang/ThreadGroup;".equals(signature)) {
				return THREAD_GROUP_TAG;
			}
			if ("Ljava/lang/Class;".equals(signature)) {
				return CLASS_OBJECT_TAG;
			}
			if ("Ljava/lang/ClassLoader;".equals(signature)) {
				return CLASS_LOADER_TAG;
			}
			return OBJECT_TAG;
		default:
			return (byte) (signature.charAt(0) & 0xFF);
		}
	}

	private int getClassStatus(int classId) {
		try {
			PacketStream ps=new PacketStream(nxtListener, CSET_REFERENCE_TYPE, RT_STATUS);
			ps.writeByte(classId);
			ps.send();
			ps.waitForReply();
			
			return ps.readInt();
		} catch (PacketStreamException e) {
			return JDWP_CLASS_STATUS_PREPARED|JDWP_CLASS_STATUS_VERIFIED|JDWP_CLASS_STATUS_INITIALIZED;
		}
	}

	public void run() {
		Packet p = null;
		PacketStream in = null;

		byte[] handshake = "JDWP-Handshake".getBytes();
		this.myThread = Thread.currentThread();

		// debugger -> proxy
		try {
			for (int i = 0; i < handshake.length; i++)
				conn.receiveByte();

			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}

			// debugger <- proxy
			for (int i = 0; i < handshake.length; i++)
				conn.sendByte(handshake[i]);
		} catch (IOException e) {
		}

		conn.start(this);
		synchronized (this) {
			Ready = true;
			notify();
		}

//		new Thread(conn).start();

		try {
			while (!stop) {

				p = waitForPacket(); /* wait for a packet */
				debug("From Debugger " + p);
				if (p == null) {
					break; // must be time to quit
				}

				if ((p.flags & Packet.Reply) == 1) {
					continue;
				}
//				debug("From Debugger "+p);

				boolean handled = false;
				in = new PacketStream(this, p);
				switch (p.cmdSet) {
				case CSET_VIRTUAL_MACHINE:
					handled = doVirtualMachineCmdset(in);
					break;

				case CSET_REFERENCE_TYPE:
					handled = doReferenceTypeCmdset(in);
					break;

				case CSET_CLASS_TYPE:
					handled = doClassTypeCmdset(in);
					break;

				case CSET_METHOD:
					handled = doMethodCmdset(in);
					break;

				case CSET_THREAD_REFERENCE:
					handled = doThreadReferenceCmdset(in);
					break;

//				case CSET_EVENT_REQUEST:
//					handled = doEventRequestCmdset(in);
//					break;

				case CSET_THREAD_GROUP_REFERENCE:
					handled = doThreadGroupReferenceCmdset(in);
					break;

				// case CSET_STACK_FRAME:
				// handled = doStackFrameCmdset(in);
				// break;

				}
				if (!handled) {
					nxtListener.send(p);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			log(p.cmdSet + "/" + p.cmd + " caused: " + e);
			e.printStackTrace();
			PacketStream error = new PacketStream(this, in.id(), Packet.Reply, NOT_FOUND);
			error.send();
		} catch (ProxyConnectionException e) {
			// connection has dropped, time to quit
			nxtListener.setStop();
			return;
		}
	}

//	private boolean doEventRequestCmdset(PacketStream in) {
//		PacketStream ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
//		switch (in.cmd()) {
//		case ER_SET:
//
//			ps.writeInt(0);
//			break;
//		}
//		ps.send();
//		return false;
//	}

	private boolean doClassTypeCmdset(PacketStream in) throws ProxyConnectionException {

		boolean handled = false;
		int classId;
		ClassData cData;

		try {
			classId = in.readUnsignedByte();
		} catch (PacketStreamException e) {
			sendErrorReply(this, in, INVALID_OBJECT);
			handled = true;
			return true;
		}

		if (!checkClassId(classId)) {
			sendErrorReply(this, in, INVALID_OBJECT);
			handled = true;
			return true;
		}

		cData = data.classData.get(classId);

		PacketStream ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

		switch (in.cmd()) {
		case CT_SUPERCLASS:
			ps.writeByte(cData.superclass);
			ps.send();
			handled = true;
			break;
		}
		ps.send();
		return handled;
	}

	private boolean doVirtualMachineCmdset(PacketStream in) throws ProxyConnectionException {
		boolean handled = false;
		PacketStream ps;

		switch (in.cmd()) {
		case VM_VERSION:
			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.writeString("Version 1.0");
			ps.writeInt(1); /* major version */
			ps.writeInt(0); /* minor version */
			ps.writeString(version);
			ps.writeString("LeJOS NXJ VM");
			ps.send();

			handled = true;
			break;

		case VM_CLASSES_BY_SIGNATURE:
			String classToMatch = null;
			try {
				ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
				classToMatch = in.readString();
				if (classToMatch == null || classToMatch.length() < 2 || (classToMatch.length() == 2 && classToMatch.charAt(0) == 'L')) {
					ps.writeInt(0);
					ps.send();
					handled = true;
					break;
				}
				for (ClassData cData : data.classData) {
					if (cData.signature.equals(classToMatch)) {
						ps.writeInt(1);
						byte jdwpTypeTag = getJDWPTypeTag(cData);
						ps.writeByte(jdwpTypeTag);
						ps.writeClassId(cData.id);
						ps.writeInt(getClassStatus(cData.id));
						ps.send();
						return true;
					}
				}
				debug("did not find class "+classToMatch);
				sendErrorReply(this, in, NOT_FOUND);
				handled = true;
			} catch (PacketStreamException e) {
				// need revisit : where the exception happened so
				// just send an error back
				sendErrorReply(this, in, ILLEGAL_ARGUMENT);
				handled = true;
				break;
			}
			break;

		case VM_ALL_CLASSES: {
			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.writeInt(data.getClassNameCount() - 9);
			for (ClassData cData : data.classData) {
				if (cData.signature.length() == 1)
					continue;
				byte jdwpTypeTag = getJDWPTypeTag(cData);
				ps.writeByte(jdwpTypeTag);
				ps.writeClassId(cData.id);
				ps.writeString(cData.signature);
				ps.writeInt(getClassStatus(cData.id));
			}
			ps.send();
			handled = true;
		}
			break;

		case VM_ALL_CLASSES_WITH_GENERIC: {
			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.writeInt(data.getClassNameCount() - 9);
			for (ClassData cData : data.classData) {
				if (cData.signature.length() == 1)
					continue;
				ps.writeByte(getJDWPTypeTag(cData));
				ps.writeClassId(cData.id);
				ps.writeString(cData.signature);
				ps.writeString("");
				ps.writeInt(getClassStatus(cData.id));
			}
			ps.send();
			handled = true;
		}
			break;

		case VM_TOP_LEVEL_THREAD_GROUPS:
			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.writeInt(0);
//			ps.writeObjectId(ONLY_THREADGROUP_ID);
			ps.send();
			handled = true;
			break;

		case VM_ID_SIZES:
			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.writeInt(SIZEOF_FIELD_ID); // sizeof field ID

			ps.writeInt(SIZEOF_METHOD_ID); // sizeof method ID
			ps.writeInt(SIZEOF_OBJECT_ID); // sizeof object ID
			ps.writeInt(SIZEOF_CLASS_ID); // sizeof reference type ID
			ps.writeInt(SIZEOF_FRAME_ID); // sizeof frame ID
			ps.send();
			handled = true;
			break;

		case VM_CAPABILITIES:
			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(true);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.send();
			handled = true;
			break;

		case VM_CAPABILITIES_NEW:
			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(true);// syntethic
			ps.writeBoolean(false);
			ps.writeBoolean(false);// canGetCurrentContendedMonitor: maybe
			ps.writeBoolean(false);// canGetMonitorInfo: maybe
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(true);// instance filters
			ps.writeBoolean(false);// source debug extension maybe
			ps.writeBoolean(true);// vmDeath event currently not working
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);// sourceNameInfo maybe
			ps.writeBoolean(false);
			ps.writeBoolean(false);// early return maybe
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.writeBoolean(false);
			ps.send();
			handled = true;
			break;

		case VM_CLASS_PATHS:
			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.writeString(".");
			ps.writeInt(0); // # of paths in classpath
			ps.writeInt(0); // # of paths in bootclasspath
			ps.send();
			handled = true;
			break;

		case VM_DISPOSE_OBJECTS:
			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.send();
			handled = true;
			break;

		case VM_HOLD_EVENTS:
			setHoldEvents(true);
			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.send();
			handled = true;
			break;

		case VM_RELEASE_EVENTS:
			setHoldEvents(false);
			ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.send();
			handled = true;
			break;
		}
		return handled;
	}

	private boolean doReferenceTypeCmdset(PacketStream in) throws ProxyConnectionException {
		boolean handled = false;
		int classId;
		ClassData cData;

		try {
			classId = in.readClassId();
		} catch (PacketStreamException e) {
			sendErrorReply(this, in, INVALID_OBJECT);
			return true;
		}

		if (!checkClassId(classId)) {
			sendErrorReply(this, in, INVALID_OBJECT);
			return true;
		}
		cData = data.classData.get(classId);

		PacketStream ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

		switch (in.cmd()) {
		case RT_SIGNATURE:
			ps.writeString(cData.signature);
			ps.send();
			handled = true;
			break;

		case RT_SIGNATURE_WITH_GENERIC:
			ps.writeString(cData.signature);
			ps.writeString("");
			ps.send();
			handled = true;
			break;

		case RT_CLASS_LOADER:
			ps.writeObjectId(0); // the null classloader object
			ps.send();
			handled = true;
			break;

		case RT_MODIFIERS:
			ps.writeInt(cData.modifiers);
			ps.send();
			handled = true;
			break;

		case RT_FIELDS:
			if (processFields(cData, classId, ps, in, false)) {
				ps.send();
			}
			handled = true;
			break;

		case RT_METHODS:
			if (getJDWPTypeTag(cData) == TYPE_TAG_ARRAY) {
				ps.writeInt(0);
				ps.send();
				handled = true;
				break;
			}
			ArrayList<MethodData> miList = cData.methods;
			ps.writeInt(miList.size());
			for (MethodData mi : miList) {

				if (mi == null) {
					sendErrorReply(this, in, NOT_FOUND);
					return true;
				}
				ps.writeMethodId(mi.id);
				ps.writeString(mi.name);
				ps.writeString(mi.signature);
				ps.writeInt(mi.modifiers);
			}
			ps.send();
			handled = true;
			break;

		case RT_FIELDS_WITH_GENERIC:
			if (processFields(cData, classId, ps, in, true)) {
				ps.send();
			}
			handled = true;
			break;

		case RT_METHODS_WITH_GENERIC:
			if (getJDWPTypeTag(cData) == TYPE_TAG_ARRAY) {
				ps.writeInt(0);
				ps.send();
				handled = true;
				break;
			}
			miList = cData.methods;
			ps.writeInt(miList.size());
			for (MethodData mi : miList) {

				if (mi == null) {
					sendErrorReply(this, in, NOT_FOUND);
					return true;
				}
				ps.writeMethodId(mi.id);
				ps.writeString(mi.name);
				ps.writeString(mi.signature);
				ps.writeString("");
				ps.writeInt(mi.modifiers);
			}
			ps.send();
			handled = true;
			break;

		// case RT_GET_VALUES:
		// cData = data.classData.get(classId);
		//
		// int numValues=in.readInt();
		// ps.writeInt(numValues);
		//
		// for (int i = 0; i < numValues; i++) {
		// int fieldId=ps.readShort();
		// FieldData info=getFieldData(in,fieldId);
		// if (info==null) {
		// break;
		// }
		//
		// int size=TinyVMType.tinyVMTypeFromSignature(info.signature).size();
		//
		// ReplyPacket
		// mem=getMemory(NXTDebugProtocol.MEM_STATICS,info.offset,size);
		// ps.writeByte(getJDWPTag(info.signature));
		// ps.writeByteArray(mem.getData());
		// }
		//
		// ps.send();
		// handled = true;
		// break;

		case RT_SOURCE_FILE:
			cData = data.classData.get(classId);

			if (cData.sourceName != null) {
				String s = cData.sourceName;
				ps.writeString(s);
			} else {
				sendErrorReply(this, in, ABSENT_INFORMATION);
				return true;
// ps.writeString(cData.name.substring(cData.name.lastIndexOf('.') + 1)
// + ".java");
			}
			ps.send();
			handled = true;
			break;

		// case RT_STATUS:
		// try {
		// classId = in.readUnsignedByte();
		// } catch (PacketStreamException e) {
		// sendErrorReply(this, in, INVALID_OBJECT);
		// handled = true;
		// break;
		// }
		//
		// if (!checkClassId(classId)) {
		// sendErrorReply(this, in, INVALID_OBJECT);
		// handled = true;
		// break;
		// }
		// ps.writeInt(getClassStatus(classId));
		// ps.send();
		// handled = true;
		// break;

		case RT_INTERFACES:
			int[] iList = cData.interfaces;
			ps.writeInt(iList.length);
			for (int intfId : iList) {
				ps.writeClassId(intfId);

			}
			ps.send();
			handled = true;
			break;

		// case RT_CLASS_OBJECT:
		// try {
		// classId = in.readUnsignedByte();
		// } catch (PacketStreamException e) {
		// debug("Interfaces cmd: exception: " + e);
		// sendErrorReply(this, in, INVALID_OBJECT);
		// handled = true;
		// break;
		// }
		//
		// if (!checkClassId(classId)) {
		// sendErrorReply(this, in, INVALID_OBJECT);
		// handled = true;
		// break;
		// }
		//
		// // ps.writeInt(classId*); TODO
		// ps.send();
		// handled = true;
		// break;

		case RT_SOURCE_DEBUG_EXTENSION:
			sendErrorReply(this, in, NOT_IMPLEMENTED);
			handled = true;
			break;
		}
		return handled;
	}

	// private FieldData getFieldData(PacketStream in, int fieldId) {
	// int classId=(fieldId>>>8)&0xFF;
	// if (!checkClassId(classId)) {
	// sendErrorReply(this, in, INVALID_CLASS);
	// return null;
	// }
	//
	// ClassData cData = data.classData.get(classId);
	// fieldId&=0xFF;
	// if(fieldId<0||fieldId>=cData.fields.length){
	// sendErrorReply(this, in, INVALID_FIELDID);
	// return null;
	// }
	// return cData.fields[fieldId];
	// }

	private boolean checkClassId(int classId) {
		return classId >= 0 && classId < data.getClassNameCount();
	}

	private boolean doMethodCmdset(PacketStream in) throws ProxyConnectionException {
		boolean handled = false;

		in.readByte();
		int methodId = in.readMethodId();

		/* Method */
		switch (in.cmd()) {

		case M_LINE_TABLE: // LineTable
			lineTable(methodId, in);
			handled = true;
			break;

		case M_VARIABLE_TABLE: // VariableTable
			variableTable(methodId, in, false);
			handled = true;
			break;

		case M_VARIABLE_TABLE_WITH_GENERIC: // VariableTable
			variableTable(methodId, in, true);
			handled = true;
			break;

		case M_BYTECODES: // Bytecodes
			sendErrorReply(this, in, NOT_IMPLEMENTED);
			handled = true;
			break;
		case M_OBSOLETE:
			PacketStream ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);
			ps.writeBoolean(false);
			ps.send();
			handled = true;
		}
		return handled;
	}

	private boolean doThreadReferenceCmdset(PacketStream in) throws ProxyConnectionException {
		boolean handled = false;
		PacketStream ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

// int threadId;
// try {
// threadId = in.readUnsignedByte();
// } catch (PacketStreamException e) {
// debug("Methods cmd: exception: " + e);
// sendErrorReply(this, in, INVALID_OBJECT);
// handled = true;
// return true;
// }

		// if (!checkRefType(threadId,"Ljava/lang/Thread;")) {
		// sendErrorReply(this, in, INVALID_OBJECT);
		// handled = true;
		// return true;
		// }

		switch (in.cmd()) {

		case TR_THREAD_GROUP:
			ps.writeInt(0);
			ps.send();
			handled = true;
			break;

		}
		return handled;
	}

	private boolean doThreadGroupReferenceCmdset(PacketStream in) throws ProxyConnectionException {

		boolean handled = false;
		PacketStream ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

		int tgID = 0;
		try {
			tgID = in.readObjectId(); // threadgroup ID
		} catch (PacketStreamException e) {

		}
		if (tgID != ONLY_THREADGROUP_ID) {
			sendErrorReply(this, in, INVALID_THREAD_GROUP);
			return true;
		}

		switch (in.cmd()) {
		case TGR_NAME:
			ps.writeString(THREADGROUP_NAME);
			ps.send();
			handled = true;
			break;
		case TGR_PARENT:
			ps.writeInt(0); // we're the top level
			ps.send();
			handled = true;
			break;
		case TGR_CHILDREN:
			PacketStream ps2 = new PacketStream(nxtListener, CSET_VIRTUAL_MACHINE, VM_ALL_THREADS);
			ps2.send();
			ps2.waitForReply();
			ps.writeByteArray(ps2.data);
			ps.writeInt(0); // number of child threadgroups
			ps.send();
			handled = true;
			break;
		}
		return handled;
	}

	/*
	 * private boolean doStackFrameCmdset(PacketStream in) throws
	 * ProxyConnectionException { boolean handled = false; int
	 * frame_index_adjust = 1; PacketStream ps = new PacketStream(this, in.id(),
	 * Packet.Reply, Packet.ReplyNoError);
	 * 
	 * switch (in.cmd()) {
	 * 
	 * case SF_THIS_OBJECT: Log.LOGN(3, "Stackframe: thisobject"); int tID =
	 * in.readInt(); // get thread id; int fID = in.readInt(); // get frame id;
	 * // We need to get the method of this frame so we can check to // see if
	 * it is a static method. This is so we can pass the // test suite. A normal
	 * debugger should never try to get the // 'this' object of a static method
	 * // need to query VM to get method ID of the method of this frame
	 * 
	 * // First we need to find out if the VM sends us 0 or 1 based // Frame
	 * ID's. Unfortunately, this isn't covered by a minor // version number.
	 * PacketStream ps2 = new PacketStream(KVMListener, THREADREFERENCE_CMDSET,
	 * FRAMES_CMD); ps2.writeInt(tID); ps2.writeInt(0); ps2.writeInt(1); // just
	 * want one frame ps2.send(); try { ps2.waitForReply(); ps2.readInt(); //
	 * framecount int id = ps2.readInt(); if (id == 0) { // VM sent 0 back as
	 * ID, must be older KVM frame_index_adjust = 0; } } catch
	 * (ProxyConnectionException e) { // What to do? Nothing, just use frame
	 * index as it it // set already }
	 * 
	 * ps2 = new PacketStream(KVMListener, THREADREFERENCE_CMDSET, FRAMES_CMD);
	 * 
	 * ps2.writeInt(tID); ps2.writeInt(fID - frame_index_adjust);
	 * ps2.writeInt(1); // just want one frame ps2.send(); try { ClassFile cf =
	 * null; MethodInfo mi; int typeTag; int fID2; int cid;
	 * 
	 * ps2.waitForReply();
	 * 
	 * ps2.readInt(); // get number of frames, should be 1 fID2 = ps2.readInt();
	 * // get frame ID if (fID2 != fID) { Log.LOGN(3,
	 * "Stackframe: thisobject: mismatched frames"); throw new
	 * ProxyConnectionException(); } typeTag = ps2.readByte(); if (typeTag !=
	 * TYPE_TAG_CLASS) { Log.LOGN(3, "Stackframe: thisobject: wrong type tag");
	 * throw new ProxyConnectionException(); } cid = ps2.readInt(); MethodID mid
	 * = new MethodID(); int methodIndex = mid.readMethodPart(ps2) -
	 * method_index_base; if ((cf = (ClassFile) manager.classMap.get(new
	 * Integer(cid))) == null) { VMCall vmCall = new VMCall(); cf =
	 * vmCall.callVMForClass(cid, "ThisObject"); if (cf == null) { Log.LOGN(3,
	 * "Stackframe: thisobject: could not find class object"); throw new
	 * ProxyConnectionException(); } } mi =
	 * cf.getMethodInfoByIndex(methodIndex); if (mi == null) { Log.LOGN(3,
	 * "Stackframe: thisobject: could not find method object"); throw new
	 * ProxyConnectionException(); } if (mi.is_static()) { // we're done, no
	 * 'this' in a static method ps.writeByte((byte) 'L'); ps.writeInt(0);
	 * ps.send(); handled = true; break; }
	 * 
	 * } catch (ProxyConnectionException e) { // What to do? Nothing, just fall
	 * through and try to get // object in slot '0'. }
	 * 
	 * ps2 = new PacketStream(KVMListener, STACKFRAME_CMDSET,
	 * STACKFRAME_GETVALUES_CMD); ps2.writeInt(tID); ps2.writeInt(fID);
	 * ps2.writeInt(1); // just want the 'this' object ps2.writeInt(0); // it's
	 * at slot 0 ps2.writeByte((byte) 'L'); // it's an object type ps2.send();
	 * try { ps2.waitForReply(); } catch (ProxyConnectionException e) {
	 * ps.writeByte((byte) 'L'); ps.writeInt(0); ps.send(); handled = true;
	 * break; } int num = ps2.readInt(); // get number of values byte tag =
	 * ps2.readByte(); // get tag type int objectID = ps2.readInt(); // and get
	 * object ID Log.LOGN(3, "Stackframe: thisobject tag: " + tag + " objectID "
	 * + Log.intToHex(objectID)); ps.writeByte(tag); ps.writeInt(objectID);
	 * ps.send(); handled = true; break; } return handled; }
	 */

	public String toString() {
		return (new String("DebuggerListener: "));
	}

	protected boolean processFields(ClassData cData, int classId, PacketStream ps, PacketStream in, boolean generic) {

		ArrayList<FieldData> fiList = cData.fields;
		if (fiList == null) {
			ps.writeInt(0);
			return true;
		}
		// int fieldID = classId << 8;
		ps.writeInt(fiList.size());
		debug("Get fields for class "+cData);
		for (FieldData fi : fiList) {
			if (fi == null) {
				sendErrorReply(this, in, INVALID_OBJECT);
				return false;
			}
			debug(fi.toString());
			ps.writeFieldId(fi.id);
			ps.writeString(fi.name);
			ps.writeString(fi.signature);
			if (generic)
				ps.writeString("");
			ps.writeInt(fi.modifiers);
		}
		return true;
	}

	public void lineTable(int methodId, PacketStream in) {
		MethodData mData = data.methodData.get(methodId);
		debug("Retrieving line table for method "+mData.name);

		if (mData.lineNumbers == null) {
			debug("Absent information");
			sendErrorReply(this, in, ABSENT_INFORMATION);
			return;
		}
		
		PacketStream ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

		if (mData.lineNumbers.length == 0) {
			ps.writeLong(-1);
			ps.writeLong(-1);
			ps.writeInt(0);
			ps.send();
			return;
		}

		ps.writeLong(0);
		ps.writeLong(mData.codeLength);
		ps.writeInt(mData.lineNumbers.length);

		for (LineNo var : mData.lineNumbers) {
			ps.writeLong(var.pc);
			ps.writeInt(var.line);
		}

		ps.send();
	}

	public void variableTable(int methodId, PacketStream in, boolean generics) {
		MethodData mData = data.methodData.get(methodId);
		if (mData.localVariables == null) {
			sendErrorReply(this, in, ABSENT_INFORMATION);
			return;
		}

		PacketStream ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

		ps.writeInt(mData.numParamWords);
		ps.writeInt(mData.localVariables.length);

		for (LocalVar var : mData.localVariables) {
			ps.writeLong(var.fromPc);
			ps.writeString(var.name);
			ps.writeString(var.signature);
			if (generics)
				ps.writeString("");
			ps.writeInt(var.length);
			ps.writeInt(var.index);
		}

		ps.send();
	}
} // DebuggerListener

