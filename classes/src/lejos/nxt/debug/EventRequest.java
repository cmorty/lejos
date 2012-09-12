package lejos.nxt.debug;

import lejos.nxt.VM;

class EventRequest {

	/** Suspend no threads when the event occurs */
	static final int SUSPEND_NONE = 0;
	/** Suspend only the thread which generated the event when the event occurs */
	static final int SUSPEND_EVENT_THREAD = 1;
	/** Suspend all threads when the event occurs */
	static final int SUSPEND_ALL = 2;

	static final int STEP_DEPTH_INTO = 0;
	static final int STEP_DEPTH_OVER = 1;
	static final int STEP_DEPTH_OUT = 2;

	static final int STEP_SIZE_MIN = 0;
	static final int STEP_SIZE_LINE = 1;

	private static final EventRequest queueHead = new EventRequest();

	static void clearAll() {
		synchronized (queueHead) {
			queueHead.next = queueHead.previous = queueHead;
		}
	}

	static void clearAllBreakpoints() {
		synchronized (queueHead) {
			for (EventRequest req = queueHead.next; req != queueHead; req = req.next) {
				System.out.println("Process request " + req);
				if (req.eventKind == JDWPConstants.EVENT_BREAKPOINT)
					req.clear();
			}
		}
	}

	/**
	 * 
	 * @param monitor
	 *            The debug monitor containing the event
	 * @param dst
	 *            The packet stream to write to
	 * @return the suspend policy
	 */
	static int processEvent(DebugInterface monitor, PacketStream dst) {
		Thread thread = monitor.thread;

		int sp = 0;
		int cnt = 0;

		dst.writeByte(0);
		dst.writeInt(0);
		System.out.println("Event " + monitor.typ);
		if (monitor.typ == DebugInterface.DBG_PROGRAM_EXIT) {
			cnt++;
			dst.writeByte(JDWPConstants.EVENT_VM_DEATH);
			dst.writeInt(0);
//			System.out.println("Special event: exit");
		} else if (monitor.typ == JDWPDebugServer.DBG_PROGRAM_START) {
			cnt++;
			dst.writeByte(JDWPConstants.EVENT_VM_INIT);
			dst.writeInt(0);
			dst.writeObjectId(JDWPDebugServer.getObjectAddress(thread));
			sp = SUSPEND_ALL; // leave program suspended
//			System.out.println("Special event: start");
		}
		synchronized (queueHead) {
			for (EventRequest req = queueHead.next; req != queueHead; req = req.next) {
				if (req.process(monitor, dst)) {
					cnt++;
					if (req.suspendPolicy > sp) {
						sp = req.suspendPolicy;
					}
				}
			}
		}
		monitor.clearEvent();
//		monitor.resumeSystemThreads();
//		System.out.println("Suspend policy: " + sp);
//		System.out.println("Number of events processed: " + cnt);
		if (cnt != 0) {
			byte[] hdr = new byte[5];
			hdr[0] = (byte) sp;
			hdr[1] = (byte) ((cnt >>> 24) & 0xff);
			hdr[2] = (byte) ((cnt >>> 16) & 0xff);
			hdr[3] = (byte) ((cnt >>> 8) & 0xff);
			hdr[4] = (byte) ((cnt >>> 0) & 0xff);
			dst.send(hdr);
			System.out.println("Event sent");
		}
		// don not resume threads until after we have sent the event to avoid
		// potential
		// issues with new events occurring before we have finished processing
		// this one.
		if (sp < 2) {
			monitor.resumeProgram();
		}
		if (sp == 1) {
			VM.suspendThread(thread);
		}
//		System.out.println("Event packet length: " + dst.pkt.data.length);

		return sp;
	}

	private EventRequest next, previous;

	byte eventKind;
	int nxtEventKind;
	byte suspendPolicy;

	// Count filter processing
	int countFilter, currentCount;
	// Thread filter processing
	Thread threadFilter;
	// Location filter processing
	private int methodFilter = -1;
	private int pcFilter = -1;
	// Exception filter processing
	private Class<?> exceptionFilter;
	private int exceptionFlags;
	private boolean setBreakpoint;
	private int stepSize = -1;
	private int stepDepth = -1;
	private int[] stepInfo;

	EventRequest() {
		next = previous = this;
	}

	private boolean process(DebugInterface monitor, PacketStream dst) {
	    // Processing of an event. My understanding is that the correct way to process filters
	    // is to perform the processing in the order in which the filters have been set. But
	    // currently we do not have a list of filters and so the order is not maintained. For
	    // now I have attempted to process the filters in an order that makes most sense and
	    // which enables them to be used to do things like set a breakpoint which firs after
	    // n times past a given point, filter on a single thread, handle an exception at a
	    // certain location etc. However it is likely that this approach may not work in all
	    // circumstances.
	    if (monitor.typ != nxtEventKind)
			return false;
		// process location filter
		if (methodFilter >= 0 && (methodFilter != monitor.method || pcFilter != monitor.pc))
		    return false;
		// process exception filter
		if (nxtEventKind == DebugInterface.DBG_EXCEPTION) {
			if (exceptionFilter != null && !exceptionFilter.isInstance(monitor.exception))
				return false;
			// if the exception is caught the catch location is on method2/pc2.
			if (monitor.method2 >= 0 ? (exceptionFlags & 1) == 0 : (exceptionFlags & 2) == 0)
				return false;
		}
		// process thread filter
		if (threadFilter != null && monitor.thread != threadFilter)
			return false;

		// process count filter
        if (countFilter > 0 && ++currentCount != countFilter)
            return false;
		// We passed all filters, now write our data
		// System.out.println("Write event");
		dst.writeByte(eventKind);
		dst.writeInt(JDWPDebugServer.getObjectAddress(this));
		switch (eventKind) {
		case JDWPConstants.EVENT_VM_INIT:
		case JDWPConstants.EVENT_THREAD_DEATH:
		case JDWPConstants.EVENT_THREAD_START:
			dst.writeObjectId(JDWPDebugServer.getObjectAddress(monitor.thread));
			break;
		case JDWPConstants.EVENT_SINGLE_STEP:
			System.out.println("Write step event");
		case JDWPConstants.EVENT_BREAKPOINT:
			dst.writeObjectId(JDWPDebugServer.getObjectAddress(monitor.thread));
			JDWPDebugServer.writeLocation(dst, VM.getVM().getMethod(monitor.method), monitor.pc);
			break;
		case JDWPConstants.EVENT_EXCEPTION:
			dst.writeObjectId(JDWPDebugServer.getObjectAddress(monitor.thread));
			JDWPDebugServer.writeLocation(dst, VM.getVM().getMethod(monitor.method), monitor.pc);
			dst.writeByte(JDWPConstants.OBJECT_TAG);
			dst.writeObjectId(JDWPDebugServer.getObjectAddress(monitor.exception));

			if (monitor.method2 >= 0)
				JDWPDebugServer.writeLocation(dst, VM.getVM().getMethod(monitor.method2), monitor.pc2);
			else
				JDWPDebugServer.writeNoLocation(dst);
			// dst.writeByte(0);
			break;
		}
		if(eventKind==JDWPConstants.EVENT_SINGLE_STEP){
			// request new stepping information.
			requestStepInformation(JDWPDebugServer.instance);
		}
		return true;
	}

	short create(PacketStream in) {
		eventKind = in.readByte();
		switch (eventKind) {
		case JDWPConstants.EVENT_VM_DEATH:
			nxtEventKind = DebugInterface.DBG_PROGRAM_EXIT;
			break;
		case JDWPConstants.EVENT_VM_INIT:
			nxtEventKind = JDWPDebugServer.DBG_PROGRAM_START;
			break;
		case JDWPConstants.EVENT_THREAD_START:
			nxtEventKind = DebugInterface.DBG_THREAD_START;
			break;
		case JDWPConstants.EVENT_THREAD_DEATH:
			nxtEventKind = DebugInterface.DBG_THREAD_STOP;
			break;
//		case JDWPConstants.EVENT_SINGLE_STEP:
		case JDWPConstants.EVENT_BREAKPOINT:
			// The breakpoint event is used for all events of a thread stopping
		    // at a specified location
			nxtEventKind = DebugInterface.DBG_BREAKPOINT;
			setBreakpoint = true;
			break;
		case JDWPConstants.EVENT_EXCEPTION_CATCH:
		case JDWPConstants.EVENT_EXCEPTION:
			nxtEventKind = DebugInterface.DBG_EXCEPTION;
			break;
		case JDWPConstants.EVENT_SINGLE_STEP:
			nxtEventKind = DebugInterface.DBG_SINGLE_STEP;
			break;
		case JDWPConstants.EVENT_CLASS_LOAD:
		case JDWPConstants.EVENT_CLASS_PREPARE:
		case JDWPConstants.EVENT_CLASS_UNLOAD:
			return 0;// These events are never ever sent in tinyvm, since it has
			         // no class loader architecture. just ignore them.
		default:
			return JDWPConstants.INVALID_EVENT_TYPE;
		}

		suspendPolicy = in.readByte();
		int modCount = in.readInt();
		for (int i = 0; i < modCount; i++) {
			// The use of multiple filters of the same kind does not make much
		    // sense.
			// Additionally, filter processing has to be fast and the filters
		    // should not need to many classes and methods.
			// So, for most of the filters we only allow one filter per filter
		    // kind.
			int kind = in.readUnsignedByte();
			switch (kind) {
			case 1:// count filter
				countFilter = in.readInt();
				break;
			case 3:// thread filter
			{
				int threadId = in.readObjectId();
				Object obj = JDWPDebugServer.memGetReference(0, threadId);
				if (obj instanceof Thread) {
					threadFilter = (Thread) obj;

				} else {
					return JDWPConstants.INVALID_THREAD;
				}
				if (DebugInterface.get().isSystemThread(threadFilter)) {
					return JDWPConstants.INVALID_THREAD;
				}
				if (nxtEventKind == DebugInterface.DBG_SINGLE_STEP && JDWPDebugServer.isStepping(threadFilter)) {
					return JDWPConstants.DUPLICATE;
				}
			}
				break;
			case 7:// Location filter
					// Ignore class information as we don't need it to
			        // understand the method id.
				in.readByte();
				in.readClassId();

				methodFilter = in.readMethodId();
				pcFilter = (int) in.readLong();
				System.out.println(methodFilter);
				System.out.println(pcFilter);
				break;
			case 8:// Exception filter
			{
				int classId = in.readClassId();
				if (classId != 0) {
					exceptionFilter = VM.getClass(classId);
				}
				exceptionFlags = 0;
				if (in.readBoolean())// caught
					exceptionFlags |= 1;
				if (in.readBoolean())// uncaught
					exceptionFlags |= 2;
			}
				break;
			case 10:// Step filter
				int threadId = in.readObjectId();
				Object obj = JDWPDebugServer.memGetReference(0, threadId);
				if (obj instanceof Thread) {
					threadFilter = (Thread) obj;

				} else {
					return JDWPConstants.INVALID_THREAD;
				}
				if (DebugInterface.get().isSystemThread(threadFilter)) {
					return JDWPConstants.INVALID_THREAD;
				}
				stepSize = in.readInt();
				stepDepth = in.readInt();
				if (stepDepth == STEP_DEPTH_OUT)
					stepSize = STEP_SIZE_MIN;
				break;
			}
		}

		if (setBreakpoint)
			Breakpoint.addBreakpoint(methodFilter, pcFilter);
		synchronized (queueHead) {
			next = queueHead.next;
			next.previous = this;
			queueHead.next = this;
			previous = queueHead;
		}
		return 0;
	}

	void requestStepInformation(JDWPDebugServer listener) {
		if (nxtEventKind == DebugInterface.DBG_SINGLE_STEP) {
			VM.VMThread thread = VM.getVM().getVMThread(threadFilter);
			VM.VMStackFrame frame = thread.getStackFrames().get(0);
			VM.VMMethod method = frame.getVMMethod();
			int codeOffset = method.getCodeOffset() + VM.getVM().getImage().address;
			int bppc = frame.pc - codeOffset;
			// currently cut out, as I'm first trying to get the MIN stepping
			// working
    		if (stepSize == STEP_SIZE_LINE) {
    			PacketStream ps = new PacketStream(listener, JDWPConstants.CSET_NXT, JDWPConstants.NXT_STEP_LINE_INFO);
    
    			ps.writeMethodId(method.getMethodNumber());
    			ps.writeShort(bppc);// internally, relative pcs are shorts
    
    			ps.send();
    			ps.waitForReply();
    
    			int nPCs = ps.readInt();
    			if (nPCs == 0) {
    				stepSize = STEP_SIZE_MIN;
    				return;
    			}
    			stepInfo = new int[nPCs];
    			for (int i = 0; i < nPCs; i++) {
    				int lpc = ps.readUnsignedShort();
    				stepInfo[i] = lpc;
    			}
    		}
    		if (stepSize == STEP_SIZE_MIN) {
    			stepInfo = new int[] { bppc };
    		}
    		JDWPDebugServer.setThreadRequest(threadFilter, new SteppingRequest(stepDepth, thread.stackFrameIndex & 0xff, method.getMethodNumber(), stepInfo));
		}
	}

	void clear() {
		if (nxtEventKind == DebugInterface.DBG_SINGLE_STEP) {
			JDWPDebugServer.setThreadRequest(threadFilter, null);
		}
		if (next != null && previous != null) {
			synchronized (queueHead) {
				next.previous = previous;
				previous.next = next;
			}
		}
	}

}
