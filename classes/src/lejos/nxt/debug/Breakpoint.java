package lejos.nxt.debug;

/**
 * 
 * A breakpoint code structure used by the debugger.
 * 
 * @author Felix Treede
 *
 */
class Breakpoint {

	private static Breakpoint[] breakpoints;
	private static int breakpointCount;

	static void initBreakpoints() {
		breakpoints = new Breakpoint[10];
		breakpointCount = 0;
		setBreakpointList(breakpoints, breakpointCount);
	}

	static void removeAllBreakpoints() {
	    initBreakpoints();
	}
	
	static void removeBreakpoint(int methodId, int pc) {
		setBreakpointList(null, 0);
	    int newCount = breakpointCount - 1;

		int removeAt = search(methodId, pc);
		if (removeAt < 0)
			return;

		if(--breakpoints[removeAt].refCount>0)return;
		enableBreakpoint(breakpoints[removeAt], false);
		
		System.arraycopy(breakpoints, removeAt, breakpoints, removeAt + 1,
				newCount - removeAt);

		breakpointCount = newCount;
		setBreakpointList(breakpoints, newCount);
	}

	static void addBreakpoint(int methodId, int pc) {
		int newCount = breakpointCount + 1;

		int insertAt = search(methodId, pc);
		if (insertAt >= 0){
			breakpoints[insertAt].refCount++;
			return;
		}
        setBreakpointList(null, 0);
		
		insertAt = -(insertAt + 1);

		Breakpoint breakpoint = new Breakpoint(methodId, pc);
		Breakpoint[] newArray;
		if (newCount > breakpoints.length) {
			newArray = new Breakpoint[newCount * 3 / 2];
			System.arraycopy(breakpoints, 0, newArray, 0, insertAt);
		} else {
			newArray = breakpoints;
		}

		System.arraycopy(breakpoints, insertAt, newArray, insertAt + 1,
				breakpointCount - insertAt);
		newArray[insertAt] = breakpoint;

		breakpoints = newArray;
		breakpointCount = newCount;
		setBreakpointList(newArray, newCount);
		enableBreakpoint(breakpoint, true);
	}


	static int search(int methodId, int pc) {
		int low = 0;
		int high = breakpointCount - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			Breakpoint midVal = breakpoints[mid];
			int cmp = midVal.methodId - methodId;
			if (cmp == 0)
				cmp = midVal.pc - pc;

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found
	}

	private static native void setBreakpointList(Breakpoint[] breakpoints,
			int count);

	private static native void enableBreakpoint(Breakpoint breakpoint,
			boolean on);

	private int methodId;
	private int pc;
	private int refCount=1;
	private boolean __TVM_enabled;
	private byte __TVM_opcode;

	Breakpoint(int methodId, int pc) {
		this.pc = pc;
		this.methodId = methodId;
	}

	int getMethodId() {
		return methodId;
	}

	int getPc() {
		return pc;
	}

}
