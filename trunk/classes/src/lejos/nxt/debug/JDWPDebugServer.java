package lejos.nxt.debug;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import lejos.nxt.LCD;
import lejos.nxt.NXT;
import lejos.nxt.VM;
import lejos.nxt.VM.VMMethods;
import lejos.nxt.VM.VMValue;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.USB;

/**
 * 
 * The main debugger class. If you use full debugging, the linker will include
 * this class. It also provides a test method to check whether debugging is
 * active.
 * 
 * @author Felix Treede
 * 
 */
public final class JDWPDebugServer extends Thread implements JDWPConstants {

	// I copied some of the constants and methods from the VM class as they were
	// private and I need them here

	static final byte THREAD_STATE_NEW = 0; // Just been created
	static final byte THREAD_STATE_DEAD = 1; // run() has exited
	static final byte THREAD_STATE_STARTED = 2; // start() has been called but
												// we haven't run yet
	static final byte THREAD_STATE_RUNNING = 3; // We're running!
	static final byte THREAD_STATE_MON_WAITING = 4; // Trying to enter a
													// synchronized block
	static final byte THREAD_STATE_CONDVAR_WAITING = 5; // Someone called wait()
														// on us in a
														// synchronized block.
	static final byte THREAD_STATE_SLEEPING = 6; // ZZZZZzzzzzzzz
	static final byte THREAD_STATE_JOIN = 7; // Waiting for another thread to
												// exit
	static final byte THREAD_STATE_SYSTEM_WAITING = 8; // Waiting on a system
														// var
	static final byte THREAD_STATE_SUSPENDED = (byte) 0x80; // Or with the above
															// to suspend

	static final byte INTERRUPT_CLEARED = (byte) 0;
	static final byte INTERRUPT_REQUESTED = (byte) 1;
	static final byte INTERRUPT_GRANTED = (byte) 2;

	/**
	 * A virtual debug event used to fastly signal the vm start
	 */
	static final int DBG_PROGRAM_START = 5;

	// Low level memory access functions

	/**
	 * Return up to 4 bytes from a specified memory location.
	 * 
	 * @param base
	 *            Base section of memory.
	 * @param offset
	 *            Offset (in bytes) of the location
	 * @param typ
	 *            The primitive data type to access
	 * @return Memory location contents.
	 */
	private static native int memPeek(int base, int offset, int typ);

	/**
	 * Return up to 4 bytes from a specified memory location.
	 * 
	 * @param base
	 *            Base section of memory.
	 * @param offset
	 *            Offset (in bytes) of the location
	 * @param typ
	 *            The primitive data type to access
	 */
	private static native void memPut(int base, int offset, int typ, int data);

	/**
	 * Copy the specified number of bytes from memory into the given object.
	 * 
	 * @param obj
	 *            Object to copy to
	 * @param objoffset
	 *            Offset (in bytes) within the object
	 * @param base
	 *            Base section to copy from
	 * @param offset
	 *            Offset within the section
	 * @param len
	 *            Number of bytes to copy
	 */
	private static native void memCopy(Object obj, int objoffset, int base,
			int offset, int len);

	/**
	 * Return the address of the given objects first data field.
	 * 
	 * @param obj
	 * @return the required address
	 */
	private native static int getDataAddress(Object obj);

	/**
	 * Return the address of the given object.
	 * 
	 * @param obj
	 * @return the required address
	 */
	native static int getObjectAddress(Object obj);

	/**
	 * Return a Java object reference the points to the location provided.
	 * 
	 * @param base
	 *            Memory section that offset refers to.
	 * @param offset
	 *            The offset from the base in bytes.
	 * @return
	 */
	native static Object memGetReference(int base, int offset);

	/**
	 * Return a single byte from the specified memory location.
	 * 
	 * @param base
	 * @param offset
	 * @return byte value from memory
	 */
	private static int memPeekByte(int base, int offset) {
		return memPeek(base, offset, VM.VM_BYTE);
	}

	/**
	 * Return a 16 bit word from the specified memory location.
	 * 
	 * @param base
	 * @param offset
	 * @return short value from memory
	 */
	private static int memPeekShort(int base, int offset) {
		return memPeek(base, offset, VM.VM_SHORT);
	}

	/**
	 * Return a 32 bit word from the specified memory location.
	 * 
	 * @param base
	 * @param offset
	 * @return int value from memory
	 */
	private static int memPeekInt(int base, int offset) {
		return memPeek(base, offset, VM.VM_INT);
	}

	/**
	 * Set a debugging request. This method should NEVER be used from user code.
	 * 
	 * @param thread
	 * @param request
	 */
	static native void setThreadRequest(Thread thread, SteppingRequest request);

	static native boolean isStepping(Thread thread);

	static JDWPDebugServer instance;

	/**
	 * Checks whether a debugger is installed and active. Note that this is not
	 * constant throughout the execution, but can change eg. if the debugger
	 * disconnects later.
	 * 
	 * @return <code>true</code> if a debugger is active, <code>false</code>
	 *         otherwise
	 */
	public static boolean isDebugging() {
		return instance != null;
	}

	static void main(String[] args) {
		// enable following to have the output display on the remote console
		//RConsole.openUSB(0);
		//System.setOut(new PrintStream(RConsole.getPrintStream()));
	    // Following enables firmware debug to be sent to the remote console.
		//USB.usbEnable(2);
		
		LCD.drawString("Listening...", 0, 0);
		DebugInterface monitor = DebugInterface.get();

		NXTConnection conn = Bluetooth.waitForConnection(0,
				NXTConnection.PACKET);
		LCD.drawString("Connected", 0, 2);
		JDWPDebugServer requestHandler = new JDWPDebugServer(conn, monitor);

		requestHandler.start();

		requestHandler.handleDebugEvents();
	}

	Connection conn;
	boolean Ready = false;
	boolean stopListener = false;
	Thread eventThread;
	DebugInterface monitor;
	boolean holdEvents;
	private boolean finished;

	VM.VMStaticFields statics = VM.getVM().getImage().getVMStaticFields();
	private boolean programStarted;

	private JDWPDebugServer(NXTConnection conn, DebugInterface monitor) {
		instance = this;
		this.conn = new Connection(this, conn);
		this.monitor = monitor;
		packetQueue = new LinkedList<Packet>();
	}

	private void handleDebugEvents() {
		eventThread = Thread.currentThread();
		VM.updateThreadFlags(eventThread, VM.VM_THREAD_SYSTEM, 0);
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY - 1);

		try {
			while (!stopListener) {
				int event = monitor.waitEvent(0);
				if (event == 0)
					continue;

				PacketStream ps = null;

				ps = new PacketStream(this, CSET_EVENT, E_COMPOSITE);
				EventRequest.processEvent(monitor, ps);

				switch (event) {
				case DebugInterface.DBG_PROGRAM_EXIT:
					exit(true);
					return;
				}
			}
		} catch (ProxyConnectionException e) {
			return;
		} finally {
			finished = true;
			System.out.println("Event listener loop exited");
		}
	}

	synchronized void send(Packet p) throws ProxyConnectionException {
		while (!Ready) {
			try {
				wait();
			} catch (InterruptedException e) {
				// ignore
			}
		}
		String id = String.valueOf(p.id);
		synchronized (waitingQueue) {
			if ((p.flags & Packet.Reply) == 0) {
				waitingQueue.put(id, p);
			}
		}
		try {
			conn.send(p);
		} catch (IOException e) {
			throw new ProxyConnectionException();
		}
	}

	private void sendErrorReply(PacketStream in, short errorCode)
			throws ProxyConnectionException {

		PacketStream ps = new PacketStream(this, in.id(), Packet.Reply,
				errorCode);
		ps.send();
	}

	@Override
	public void run() {
		VM.updateThreadFlags(this, VM.VM_THREAD_SYSTEM, 0);
		setPriority(Thread.MAX_PRIORITY - 1);
		Packet p = null;
		PacketStream in = null;
		PacketStream ps;

		conn.start();

		synchronized (this) {
			Ready = true;
			notifyAll();
		}

		Breakpoint.initBreakpoints();
		try {
			while (!stopListener) {
				System.gc();
				if (programStarted)
					setDaemon(true);
				p = waitForPacket(); /* wait for a packet */
				setDaemon(false);
				if (p == null) {
					break; // must be time to quit
				}
				
				if ((p.flags & Packet.Reply) == 1) {
					continue;
				}
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

				case CSET_ARRAY_TYPE:
					if (p.cmd == AT_NEW_INSTANCE) {
						int classId;
						int length;
						try {
							classId = in.readClassId();
							length = in.readInt();
						} catch (PacketStreamException e) {
							sendErrorReply(in, INVALID_OBJECT);
							handled = true;
							break;
						}

						if (!checkClassId(classId)) {
							sendErrorReply(in, INVALID_OBJECT);
							handled = true;
							break;
						}
						Object array = allocate(classId, length);

						ps = new PacketStream(this, in.id(), Packet.Reply,
								Packet.ReplyNoError);
						ps.writeInt(getObjectAddress(array));
						ps.send();

						handled = true;
					}
					break;

				case CSET_OBJECT_REFERENCE:
					handled = doObjectReferenceCmdset(in);
					break;

				case CSET_ARRAY_REFERENCE:
					handled = doArrayReferenceCmdset(in);
					break;
				case CSET_STRING_REFERENCE:
					if (p.cmd == SR_VALUE) {
						String s;
						try {
							int objectId = in.readObjectId();
							if (objectId == 0) {
								sendErrorReply(in, INVALID_OBJECT);
								handled = true;
								break;
							}
							Object object = memGetReference(0, objectId);
							if (!(object instanceof String)) {
								sendErrorReply(in, INVALID_STRING);
								handled = true;
								break;
							}
							s = (String) object;
						} catch (PacketStreamException e) {
							sendErrorReply(in, INVALID_OBJECT);
							handled = true;
							break;
						}

						ps = new PacketStream(this, in.id(), Packet.Reply,
								Packet.ReplyNoError);
						ps.writeString(s);
						ps.send();

						handled = true;
					}
					break;

				case CSET_CLASS_OBJECT_REFERENCE:
					if (p.cmd == COR_REFLECTED_TYPE) {
						ps = new PacketStream(this, in.id(), Packet.Reply,
								Packet.ReplyNoError);

						Class<?> cls;
						try {
							int objectId = in.readObjectId();
							if (objectId == 0) {
								sendErrorReply(in, INVALID_OBJECT);
								handled = true;
								break;
							}
							Object object = memGetReference(0, objectId);
							if (!(object instanceof Class<?>)) {
								sendErrorReply(in, INVALID_OBJECT);
								handled = true;
								break;
							}
							cls = (Class<?>) object;
						} catch (PacketStreamException e) {
							sendErrorReply(in, INVALID_OBJECT);
							handled = true;
							break;
						}
						ps.writeByte(VM.getClassNumber(cls));
						handled = true;
					}
					break;

				case CSET_THREAD_REFERENCE:
					handled = doThreadReferenceCmdset(in);
					break;

				case CSET_STACK_FRAME:
					handled = doStackFrameCmdset(in);
					break;

				case CSET_EVENT_REQUEST:
					handled = doEventRequestCmdset(in);
					break;

				case CSET_NXT:
					handled = doNXTCmdset(in);
					break;

				}
				if (!handled) {
					sendErrorReply(in, NOT_IMPLEMENTED);
				}
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(p.cmdSet + "/" + p.cmd + " caused: " + e);
			e.printStackTrace();
			PacketStream error = new PacketStream(this, in.id(), Packet.Reply,
					NOT_FOUND);
			error.send();
		} catch (ProxyConnectionException e) {
			return;
		} finally {
			finished = true;
			System.out.println("JDWP listener loop exited");
		}
	}

	private boolean doEventRequestCmdset(PacketStream in) {
		boolean handled = false;
		PacketStream ps;
		EventRequest request;

		switch (in.cmd()) {
		case ER_SET:
			request = new EventRequest();
			short err = request.create(in);
			ps = new PacketStream(this, in.id(), Packet.Reply, err);
			if (err == 0) {
				ps.writeInt(getObjectAddress(request));
			}
			ps.send();
			request.requestStepInformation(this);
			handled = true;
			break;

		case ER_CLEAR:
			try {
				in.readByte();
				int objectId = in.readInt();
				if (objectId == 0) {
					sendErrorReply(in, INVALID_OBJECT);
					return true;
				}
				try {
					request = (EventRequest) memGetReference(0, objectId);
				} catch (ClassCastException e) {
					sendErrorReply(in, INVALID_OBJECT);
					return true;
				}
			} catch (PacketStreamException e) {
				sendErrorReply(in, INVALID_OBJECT);
				return true;
			}
			request.clear();
			sendErrorReply(in, NONE);
			handled = true;
			break;
		}
		return handled;
	}

	private boolean doNXTCmdset(PacketStream in) {
		boolean handled = false;
		PacketStream ps;
		switch (in.cmd()) {
		case NXT_HANDSHAKE:
			String proxy = in.readString();
			int majVers = in.readUnsignedByte();
			int minVers = in.readUnsignedByte();

			System.out.println(proxy);
			System.out.print("Version ");
			System.out.print(majVers);
			System.out.print('.');
			System.out.println(minVers);

			ps = new PacketStream(this, in.id(), Packet.Reply,
					Packet.ReplyNoError);

			ps.writeInt(NXT.getFirmwareRawVersion());
			ps.send();
			handled = true;

			// monitor.suspendProgram();
			// LCD.clear();

			monitor.setEventOptions(DBG_PROGRAM_START,
					DebugInterface.DBG_EVENT_ENABLE);
			monitor.setEventOptions(DebugInterface.DBG_PROGRAM_EXIT,
					DebugInterface.DBG_EVENT_ENABLE);
			monitor.setEventOptions(DebugInterface.DBG_THREAD_START,
					DebugInterface.DBG_EVENT_ENABLE);
			monitor.setEventOptions(DebugInterface.DBG_THREAD_STOP,
					DebugInterface.DBG_EVENT_ENABLE);
			monitor.setEventOptions(DebugInterface.DBG_EXCEPTION,
					DebugInterface.DBG_EVENT_ENABLE);
			monitor.setEventOptions(DebugInterface.DBG_BREAKPOINT,
					DebugInterface.DBG_EVENT_ENABLE);
			monitor.setEventOptions(DebugInterface.DBG_SINGLE_STEP,
					DebugInterface.DBG_EVENT_ENABLE);
			
			Thread prog = monitor.startProgram(1);
			programStarted = true;

			break;
		}

		return handled;
	}

	private static native Object allocate(int classId, int length);

	private boolean doArrayReferenceCmdset(PacketStream in) {
		boolean handled = false;
		PacketStream ps;
		ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

		Object object;
		VM.VMElements vmElements;
		try {
			int objectId = in.readObjectId();
			if (objectId == 0) {
				sendErrorReply(in, INVALID_OBJECT);
				return true;
			}
			object = memGetReference(0, objectId);
			vmElements = VM.getVM().getElements(object);
			if (vmElements.length() < 0) {
				sendErrorReply(in, INVALID_ARRAY);
				return true;
			}
		} catch (PacketStreamException e) {
			sendErrorReply(in, INVALID_OBJECT);
			return true;
		}

		switch (in.cmd()) {
		case AR_LENGTH:
			ps.writeInt(vmElements.length());
			handled = true;
			break;

		case AR_GET_VALUES:
			int index = in.readInt();
			int length = in.readInt();

			if (index < 0 || index >= vmElements.length() || length < 0
					|| index + length > vmElements.length()) {
				sendErrorReply(in, INVALID_LENGTH);
				return true;
			}

			int tag = getTagFromClass(object.getClass().getComponentType());
			ps.writeByte(tag);
			ps.writeInt(length);

			// We can't directly copy the data because of byte order differences
			if (length > 0) {
				for (int i = index; i < index + length; i++) {
					VM.VMValue val = vmElements.get(i);
					writeValue(ps, val, false);
				}
			}
			handled = true;
			break;

		case AR_SET_VALUES:
			index = in.readInt();
			length = in.readInt();

			if (index < 0 || index >= vmElements.length() || length < 0
					|| index + length > vmElements.length()) {
				sendErrorReply(in, INVALID_LENGTH);
				return true;
			}

			if (length > 0) {
				for (int i = index; i < index + length; i++) {
					VM.VMValue val = vmElements.get(i);

					switch (val.type) {
					case VM.VM_BOOLEAN:
					case VM.VM_BYTE:
						memPut(0, val.addr, VM.VM_BYTE, in.readUnsignedByte());
						break;
					case VM.VM_SHORT:
					case VM.VM_CHAR:
						memPut(0, val.addr, VM.VM_SHORT, in.readUnsignedShort());
						break;
					case VM.VM_INT:
					case VM.VM_FLOAT:
					case VM.VM_OBJECT:
						memPut(0, val.addr, VM.VM_INT, in.readInt());
						break;
					case VM.VM_LONG:
					case VM.VM_DOUBLE:
						memPut(0, val.addr + 4, VM.VM_INT, in.readInt());
						memPut(0, val.addr, VM.VM_INT, in.readInt());
						break;
					}
				}
			}
			handled = true;
		}
		if (handled)
			ps.send();
		return handled;
	}

	private boolean doStackFrameCmdset(lejos.nxt.debug.PacketStream in) {
		boolean handled = false;
		PacketStream ps;
		Thread thread;
		VM.VMThread vmThread;
		VM.VMStackFrame frame;
		try {
			int threadId = in.readObjectId();
			Object obj = memGetReference(0, threadId);
			if (obj instanceof Thread) {
				thread = (Thread) obj;

			} else {
				sendErrorReply(in, INVALID_THREAD);
				return true;
			}
			if (monitor.isSystemThread(thread)) {
				sendErrorReply(in, INVALID_THREAD);
				return true;
			}
			vmThread = VM.getVM().getVMThread(thread);
			int frameId = in.readFrameId();
			if (frameId < 0 || frameId >= vmThread.stackFrameIndex) {
				sendErrorReply(in, INVALID_FRAMEID);
				return true;
			}
			frame = vmThread.getStackFrames().get(frameId);
		} catch (PacketStreamException e) {
			sendErrorReply(in, INVALID_OBJECT);
			return true;
		}

		ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

		switch (in.cmd()) {
		case SF_THIS_OBJECT:
			if ((frame.getVMMethod().getFlags() & (VM.VMMethod.M_STATIC | VM.VMMethod.M_NATIVE)) != 0) {
				ps.writeByte(0);
				ps.writeObjectId(-1);
				break;
			}
			int offset = frame.localsBase;
			int handle = memPeekInt(0, offset);
			ps.writeByte(getTagFromObject(memGetReference(0, handle)));
			ps.writeObjectId(handle);
			handled = true;
			break;
		case SF_GET_VALUES:
			int count = in.readInt();
			ps.writeInt(count);

			for (int i = 0; i < count; i++) {
				int slot = in.readInt();
				if (slot < 0 || slot >= frame.getVMMethod().numLocals) {
					sendErrorReply(in, INVALID_SLOT);
					return true;
				}
				int tag = in.readUnsignedByte();
				offset = frame.localsBase + slot * 4;
				if (tag == OBJECT_TAG) {
					handle = memPeekInt(0, offset);
					ps.writeByte(getTagFromObject(memGetReference(0, handle)));
					ps.writeInt(handle);
				} else {
					ps.writeByte(tag);
					switch (tag) {
					case BOOLEAN_TAG:
					case BYTE_TAG:
						ps.writeByte(memPeekByte(0, offset));
						break;
					case SHORT_TAG:
					case CHAR_TAG:
						ps.writeShort(memPeekShort(0, offset));
						break;
					default:
						ps.writeInt(memPeekInt(0, offset));
						break;
					case LONG_TAG:
					case DOUBLE_TAG:
						ps.writeInt(memPeekInt(0, offset));
						ps.writeInt(memPeekInt(0, offset + 4));
						break;
					}
				}
			}
			handled = true;
			break;
		case SF_SET_VALUES:
			count = in.readInt();
			for (int i = 0; i < count; i++) {
				int slot = in.readInt();
				if (slot < 0 || slot >= frame.getVMMethod().numLocals) {
					sendErrorReply(in, INVALID_SLOT);
					return true;
				}
				int tag = in.readUnsignedByte();
				offset = frame.localsBase + slot * 4;
				switch (tag) {
				case BOOLEAN_TAG:
				case BYTE_TAG:
					memPut(0, offset, VM.VM_BYTE, in.readUnsignedByte());
					break;
				case SHORT_TAG:
				case CHAR_TAG:
					memPut(0, offset, VM.VM_SHORT, in.readUnsignedShort());
					break;
				case LONG_TAG:
				case DOUBLE_TAG:
					memPut(0, offset, VM.VM_INT, in.readInt());
					memPut(0, offset + 4, VM.VM_INT, in.readInt());
					break;
				default:
					memPut(0, offset, VM.VM_INT, in.readInt());
					break;
				}
			}
			handled = true;
		}
		if (handled)
			ps.send();
		return handled;
	}

	private static int getTagFromClass(Class<?> cls) {
		if (cls == null) {
			return NULL_TAG;
		}
		if (cls.isPrimitive()) {
			if (cls == boolean.class) {
				return BOOLEAN_TAG;
			}
			if (cls == byte.class) {
				return BYTE_TAG;
			}
			if (cls == short.class) {
				return SHORT_TAG;
			}
			if (cls == char.class) {
				return CHAR_TAG;
			}
			if (cls == int.class) {
				return INT_TAG;
			}
			if (cls == float.class) {
				return FLOAT_TAG;
			}
			if (cls == double.class) {
				return DOUBLE_TAG;
			}
			if (cls == long.class) {
				return LONG_TAG;
			}
		}
		if (cls.isArray()) {
			return ARRAY_TAG;
		}
		if (cls == String.class) {
			return STRING_TAG;
		}
		if (Thread.class.isAssignableFrom(cls)) {
			return THREAD_TAG;
		}
		if (cls == Class.class) {
			return CLASS_OBJECT_TAG;
		}
		return OBJECT_TAG;
	}

	private static int getTagFromObject(Object obj) {
		if (obj == null)
			return NULL_TAG;
		Class<?> cls = obj.getClass();
		if (cls == null) {
			return NULL_TAG;
		}
		if (cls.isArray()) {
			return ARRAY_TAG;
		}
		if (cls == String.class) {
			return STRING_TAG;
		}
		if (cls == Class.class) {
			return CLASS_OBJECT_TAG;
		}
		if (Thread.class.isAssignableFrom(cls)) {
			return THREAD_TAG;
		}
		return OBJECT_TAG;
	}

	private static int getTypeTagFromClass(Class<?> cls) {
		if (cls == null) {

			return 0;
		}
		if (cls.isArray()) {
			return TYPE_TAG_ARRAY;
		}
		if (cls.isInterface()) {
			return TYPE_TAG_INTERFACE;
		}
		return TYPE_TAG_CLASS;
	}

	private static int getTypeTagFromClass(VM.VMClass cls) {
		if (cls == null) {

			return 0;
		}
		if ((cls.flags & VM.VMClass.C_ARRAY) != 0) {
			return TYPE_TAG_ARRAY;
		}
		if ((cls.flags & VM.VMClass.C_INTERFACE) != 0) {
			return TYPE_TAG_INTERFACE;
		}
		return TYPE_TAG_CLASS;
	}

	private boolean doReferenceTypeCmdset(lejos.nxt.debug.PacketStream in) {
		int classId;
		boolean handled = false;
		try {
			classId = in.readClassId();
		} catch (PacketStreamException e) {
			sendErrorReply(in, INVALID_OBJECT);
			return true;
		}

		if (!checkClassId(classId)) {
			sendErrorReply(in, INVALID_CLASS);
			return true;
		}
		PacketStream ps;

		switch (in.cmd()) {
		case RT_GET_VALUES:
			ps = new PacketStream(this, in.id(), Packet.Reply,
					Packet.ReplyNoError);

			if (!getFields(in, ps, null)) {
				return true;
			}
			handled = true;
			ps.send();
			break;

		case RT_CLASS_OBJECT:
			ps = new PacketStream(this, in.id(), Packet.Reply,
					Packet.ReplyNoError);
			ps.writeInt(getObjectAddress(VM.getClass(classId)));
			ps.send();
			handled = true;
			break;

		case RT_STATUS:
			ps = new PacketStream(this, in.id(), Packet.Reply,
					Packet.ReplyNoError);
			int res = JDWP_CLASS_STATUS_PREPARED | JDWP_CLASS_STATUS_VERIFIED;
			if (isInitialized(classId)) {
				res |= JDWP_CLASS_STATUS_INITIALIZED;
			}
			ps.writeInt(res);
			ps.send();
			handled = true;
			break;
		}
		return handled;
	}

	private static native boolean isInitialized(int classId);

	private boolean doObjectReferenceCmdset(lejos.nxt.debug.PacketStream in) {
		boolean handled = false;
		PacketStream ps;
		ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

		Object object;
		try {
			int objectId = in.readInt();
			if (objectId == 0) {
				sendErrorReply(in, INVALID_OBJECT);
				return true;
			}
			object = memGetReference(0, objectId);
		} catch (PacketStreamException e) {
			sendErrorReply(in, INVALID_OBJECT);
			return true;
		}

		switch (in.cmd()) {
		case OR_REFERENCE_TYPE:
			Class<? extends Object> cls = object.getClass();
			if (cls == null) {
				sendErrorReply(in, INVALID_OBJECT);
				return true;
			}
			ps.writeByte(getTypeTagFromClass(cls));
			ps.writeClassId(VM.getClassNumber(cls));

			handled = true;
			break;

		case OR_IS_COLLECTED:// Well, actually we can't determine this.
			ps.writeBoolean(false);
			handled = true;
			break;

		case OR_GET_VALUES:
			if (!getFields(in, ps, object)) {
				return true;
			}
			handled = true;
			break;

		case OR_SET_VALUES:
			if (!setFields(in, ps, object)) {
				return true;
			}
			handled = true;
			break;
		}
		if (handled)
			ps.send();
		return handled;
	}

	private boolean setFields(PacketStream in, PacketStream ps, Object object) {
		VM.VMFields fields = null;
		if (object != null)
			fields = VM.getVM().getFields(object);

		int count = in.readInt();
		for (int i = 0; i < count; i++) {
			try {
				short fieldId = (short) in.readFieldId();
				VMValue val;
				if (fieldId < 0) {
					val = statics.get(-fieldId - 1);
				} else {
					val = fields.get(fieldId);
				}
				switch (val.type) {
				case VM.VM_BOOLEAN:
				case VM.VM_BYTE:
					memPut(0, val.addr, VM.VM_BYTE, in.readUnsignedByte());
					break;
				case VM.VM_SHORT:
				case VM.VM_CHAR:
					memPut(0, val.addr, VM.VM_SHORT, in.readUnsignedShort());
					break;
				case VM.VM_INT:
				case VM.VM_FLOAT:
				case VM.VM_OBJECT:
					memPut(0, val.addr, VM.VM_INT, in.readInt());
					break;
				case VM.VM_LONG:
				case VM.VM_DOUBLE:
					memPut(0, val.addr, VM.VM_INT, in.readInt());
					memPut(0, val.addr + 4, VM.VM_INT, in.readInt());
					break;
				}
			} catch (NoSuchFieldError e) {
				sendErrorReply(in, INVALID_FIELDID);
				return true;
			}
		}

		return true;
	}

	private boolean getFields(PacketStream in, PacketStream ps, Object object) {
		VM.VMFields fields = null;
		if (object != null)
			fields = VM.getVM().getFields(object);

		int count = in.readInt();
		ps.writeInt(count);
		for (int i = 0; i < count; i++) {
			try {
				short id = (short) in.readFieldId();
				VMValue val;
				if (id < 0) {
					val = statics.get(-id - 1);
				} else {
					val = fields.get(id);
				}
				writeValue(ps, val, true);
			} catch (NoSuchFieldError e) {
				sendErrorReply(in, INVALID_FIELDID);
				return true;
			}
		}

		return true;
	}

	// place holder method. Invocation is not supported yet
	private void invokeMethod(int methodId, Thread thread, Object thisObject,
			PacketStream ps) {
		VM.VMMethod method = VM.getVM().getMethod(methodId);
		// TODO implement this.
	}

	private void writeValue(PacketStream ps, VMValue val, boolean tagged) {
		int size = 4;
		switch (val.type) {
		case VM.VM_BOOLEAN:
			if (tagged)
				ps.writeByte(BOOLEAN_TAG);
			size = 1;
			break;
		case VM.VM_BYTE:
			if (tagged)
				ps.writeByte(BYTE_TAG);
			size = 1;
			break;
		case VM.VM_CHAR:
			if (tagged)
				ps.writeByte(CHAR_TAG);
			size = 2;
			break;
		case VM.VM_SHORT:
			if (tagged)
				ps.writeByte(SHORT_TAG);
			size = 2;
			break;
		case VM.VM_INT:
			if (tagged)
				ps.writeByte(INT_TAG);
			size = 4;
			break;
		case VM.VM_FLOAT:
			if (tagged)
				ps.writeByte(FLOAT_TAG);
			size = 4;
			break;
		case VM.VM_OBJECT:
			int addr = memPeekInt(0, val.addr);
			if (tagged)
				ps.writeByte(getTagFromObject(memGetReference(0, addr)));
			ps.writeObjectId(addr);
			return;
		case VM.VM_LONG:
			if (tagged)
				ps.writeByte(LONG_TAG);
			size = 8;
			break;
		case VM.VM_DOUBLE:
			if (tagged)
				ps.writeByte(DOUBLE_TAG);
			size = 8;
			break;
		}
		switch (size) {
		case 1:
			ps.writeByte(memPeekByte(0, val.addr));
			break;
		case 2:
			ps.writeShort(memPeekShort(0, val.addr));
			break;
		case 4:
			ps.writeInt(memPeekInt(0, val.addr));
			break;
		case 8:
			ps.writeInt(memPeekInt(0, val.addr));
			ps.writeInt(memPeekInt(0, val.addr + 4));
			break;
		}
	}

	private boolean doClassTypeCmdset(lejos.nxt.debug.PacketStream in) {
		int classId;
		boolean handled = false;
		try {
			classId = in.readClassId();
		} catch (PacketStreamException e) {
			sendErrorReply(in, INVALID_OBJECT);
			return true;
		}

		if (!checkClassId(classId)) {
			sendErrorReply(in, INVALID_CLASS);
			return true;
		}
		PacketStream ps;
		switch (in.cmd()) {
		case CT_SET_VALUES:
			ps = new PacketStream(this, in.id(), Packet.Reply,
					Packet.ReplyNoError);

			if (!setFields(in, ps, null)) {
				return true;
			}
			handled = true;
			ps.send();
			break;

		case CT_INVOKE_METHOD:

		}
		return handled;
	}

	private boolean checkClassId(int classId) {
		return classId >= 0 && classId <= VM.getVM().getImage().lastClass;
	}

	private boolean doThreadReferenceCmdset(lejos.nxt.debug.PacketStream in) {
		boolean handled = false;
		PacketStream ps;
		ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

		Thread thread;
		VM.VMThread vmThread;
		try {
			int threadId = in.readObjectId();
			Object obj = memGetReference(0, threadId);
			if (obj instanceof Thread) {
				thread = (Thread) obj;

			} else {
				System.out.println("No thread");
				sendErrorReply(in, INVALID_THREAD);
				return true;
			}
			if (monitor.isSystemThread(thread)) {
				System.out.println("System thread");
				sendErrorReply(in, INVALID_THREAD);
				return true;
			}
		} catch (PacketStreamException e) {
			sendErrorReply(in, INVALID_OBJECT);
			handled = true;
			return true;
		}

		switch (in.cmd()) {
		case TR_NAME:
			String name = thread.getName();
			ps.writeString(name);
			handled = true;
			break;

		case TR_SUSPEND:
			VM.suspendThread(thread);
			handled = true;
			break;

		case TR_RESUME:
			VM.resumeThread(thread);
			handled = true;
			break;

		case TR_STATUS:
			vmThread = VM.getVM().getVMThread(thread);
			int jdwpState;
			switch (vmThread.state & 0x7f) {
			case THREAD_STATE_NEW:
				jdwpState = JDWP_THREAD_STATUS_NOT_STARTED;
				break;
			case THREAD_STATE_STARTED:
			case THREAD_STATE_RUNNING:
				jdwpState = JDWP_THREAD_STATUS_RUNNING;
				break;
			case THREAD_STATE_DEAD:
				jdwpState = JDWP_THREAD_STATUS_ZOMBIE;
				break;
			case THREAD_STATE_CONDVAR_WAITING:
			case THREAD_STATE_JOIN:
				jdwpState = JDWP_THREAD_STATUS_WAIT;
				break;
			case THREAD_STATE_MON_WAITING:
			case THREAD_STATE_SYSTEM_WAITING:
				jdwpState = JDWP_THREAD_STATUS_MONITOR;
				break;
			case THREAD_STATE_SLEEPING:
				jdwpState = JDWP_THREAD_STATUS_SLEEPING;
				break;
			default:
				jdwpState = JDWP_THREAD_STATUS_UNKNOWN;
				break;
			}
			ps.writeInt(jdwpState);
			if ((vmThread.state & THREAD_STATE_SUSPENDED) != 0) {
				ps.writeInt(1);
			} else {
				ps.writeInt(0);
			}
			handled = true;
			break;

		case TR_FRAME_COUNT:
			vmThread = VM.getVM().getVMThread(thread);
			if ((vmThread.state & THREAD_STATE_SUSPENDED) == 0) {
				sendErrorReply(in, THREAD_NOT_SUSPENDED);
				return true;
			}

			ps.writeInt(vmThread.stackFrameIndex & 0xFF);
			handled = true;
			break;

		case TR_FRAMES:
			vmThread = VM.getVM().getVMThread(thread);
			if ((vmThread.state & THREAD_STATE_SUSPENDED) == 0) {
				sendErrorReply(in, THREAD_NOT_SUSPENDED);
				return true;
			}

			int startFrame = in.readInt();
			int length = in.readInt();
			int frameCount = vmThread.stackFrameIndex & 0xFF;

			if (startFrame >= frameCount) {
				sendErrorReply(in, INVALID_THREAD);
				return true;
			}

			if (startFrame + length >= frameCount) {
				sendErrorReply(in, INVALID_THREAD);
				return true;
			} else if (length == -1) {
				length = frameCount - startFrame;
			}

			ps.writeInt(frameCount);

			VM.VMStackFrames frames = vmThread.getStackFrames();

			for (int i = startFrame; i < startFrame + length; i++) {
				VM.VMStackFrame frame = frames.get(i);
				ps.writeFrameId(i);
				writeLocation(ps, vmThread, frame, i == 0);
			}
			handled = true;
			break;

		case TR_INTERRUPT:
			thread.interrupt();
			handled = true;
			break;

		case TR_SUSPEND_COUNT:
			vmThread = VM.getVM().getVMThread(thread);
			if ((vmThread.state & THREAD_STATE_SUSPENDED) != 0) {
				ps.writeInt(1);
			} else {
				ps.writeInt(0);
			}
			handled = true;
			break;
		}
		if (handled) {
			ps.send();
		}
		return handled;
	}

	static void writeLocation(lejos.nxt.debug.PacketStream ps,
			VM.VMThread thread, VM.VMStackFrame frame, boolean isTopFrame) {
		VM.VMMethod method = frame.getVMMethod();
		int pc = frame.pc - method.getCodeOffset() - VM.getVM().getImage().address;
		// <clinit> is not called by an invoke instruction
		if (!isTopFrame && method.signature != 3) {
			// Needed for correct method call line display
			pc -= 2;
		}

		writeLocation(ps, method, pc);
	}

	static void writeLocation(lejos.nxt.debug.PacketStream ps,
			VM.VMMethod method, int pc) {
		int methodId = method.getMethodNumber();
		VM.VMClasses classes = VM.getVM().getImage().getVMClasses();

		int typeTag = 0;
		int classId = 0;

		int low = 0;
		int high = VM.getVM().getImage().lastClass & 0xFF;
		
		while (low <= high) {
			classId = (low + high) / 2;
			VM.VMClass cls = classes.get(classId);
			
			VMMethods methods = cls.getMethods();
			while (methods.size() == 0) {
				if (classId > ((VM.getVM().getImage().lastClass & 0xFF) * 3 / 4)) {
					classId--;
				} else {
					classId++;
				}
				cls = classes.get(classId);
				methods = cls.getMethods();
			}
			int firstClassMethod = methods.get(0).getMethodNumber();
			if (firstClassMethod > methodId)
				high = classId - 1;
			else if ((firstClassMethod + methods.size()) <= methodId)
				low = classId + 1;
			else {
				typeTag = getTypeTagFromClass(cls);
				break;
			}
		}
		ps.writeByte(typeTag);
		ps.writeClassId(classId);
		ps.writeMethodId(methodId);
		ps.writeLong(pc);
	}

	static void writeNoLocation(lejos.nxt.debug.PacketStream ps) {
		ps.writeByte(JDWPConstants.TYPE_TAG_CLASS);
		ps.writeClassId(0);
		ps.writeMethodId(0);
		ps.writeLong(0);
	}

	private boolean doVirtualMachineCmdset(lejos.nxt.debug.PacketStream in) {
		boolean handled = false;
		PacketStream ps;
		ps = new PacketStream(this, in.id(), Packet.Reply, Packet.ReplyNoError);

		switch (in.cmd()) {
		case VM_ALL_THREADS:
			ArrayList<Thread> threads = new ArrayList<Thread>();
			for (VM.VMThread th : VM.getVM().getVMThreads())
				if (!monitor.isSystemThread(th.getJavaThread()))
					threads.add(th.getJavaThread());

			ps.writeInt(threads.size());
			for (Thread th : threads) {
				ps.writeObjectId(getObjectAddress(th));
			}
			handled = true;
			break;

		case VM_DISPOSE:
			ps.send();
			exit(false);
			return true;

		case VM_EXIT:
			// ps.send();
			// exit(true, true);
			while (!monitor.notifyEvent(DebugInterface.DBG_PROGRAM_EXIT, null))
				Thread.yield();
			handled = true;
			break;

		case VM_SUSPEND:
			monitor.suspendProgram();
			handled = true;
			break;

		case VM_RESUME:
			monitor.resumeProgram();
			handled = true;
			break;

		}
		if (handled)
			ps.send();
		return handled;
	}

	void exit(boolean kill) {
		//RConsole.println("Stopping Debugger " + kill);
		if (stopListener)
			return;
		instance = null;
		stopListener = true;
		// we are stopping now, and we will finish that.
		monitor.setEventOptions(DebugInterface.DBG_PROGRAM_EXIT,
				DebugInterface.DBG_EVENT_IGNORE);
		monitor.suspendProgram();
		this.interrupt();
		eventThread.interrupt();
		
		synchronized (packetQueue) {
			packetQueue.notify();
		}
		synchronized (waitingQueue) {
			waitingQueue.notify();
		}
		// Wait for the other debugger thread to shutdown
		while (!finished) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		EventRequest.clearAll();
		Breakpoint.removeAllBreakpoints();

		if (conn != null) {
			conn.close();
		}
		//RConsole.println("Debugger stopped");
		monitor.setEventOptions(DebugInterface.DBG_PROGRAM_EXIT,
				DebugInterface.DBG_EVENT_DISABLE);
		monitor.resumeProgram();
		if (kill) {
			System.exit(0);
		}
	}

	private int getTypeSize(int jdwpTag) {
		switch (jdwpTag) {
		case BOOLEAN_TAG:
		case BYTE_TAG:
			return 1;
		case SHORT_TAG:
		case CHAR_TAG:
			return 2;
		case LONG_TAG:
		case DOUBLE_TAG:
			return 8;
		default:
			return 4;
		}
	}

	Hashtable<String, Packet> waitingQueue = new Hashtable<String, Packet>();
	protected LinkedList<Packet> packetQueue;

	void newPacket(Packet p) {
		synchronized (packetQueue) {
			if (p != null)
				packetQueue.add(p);
			packetQueue.notify();
		}
	}

	private Packet waitForPacket() {
		Packet r;
		synchronized (packetQueue) {
			while (packetQueue.isEmpty()) {
				try {
					packetQueue.wait();
				} catch (InterruptedException e) {
					throw new ProxyConnectionException();
				}
			}
			r = packetQueue.remove(0);
		}
		return r;
	}

	void replyReceived(Packet p) {
		Packet p2;
		if (p == null) {
			synchronized (waitingQueue) {
				Enumeration<String> iter = waitingQueue.keys();
				while (iter.hasMoreElements()) {
					p2 = waitingQueue.get(iter.nextElement());
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
				waitingQueue.put(idString, null);
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
					throw new ProxyConnectionException();
				}
			}
			if (!p.replied)
				throw new RuntimeException();
		}
	}
}
