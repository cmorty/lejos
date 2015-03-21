package lejos.util;

/**
 * Debug message handler extension to output messages to the NXT Charting Logger Debug Output Console.
 * 
 * @author Kirk P. Thompson
 * @see LogMessageManager
 */
public class LoggerDebugConsole extends LogMessageTypeHandler {
	private final static int CMD_SET_MESSAGE = 0;
	
	/**
	 * Create a <code>LoggerDebugConsole</code> instance using the passed <code>LogMessageManager</code> singleton instance. 
	 * 
	 * @param loggerMessageManager lmm The <code>LogMessageManager</code> singleton. See 
	 * 		{@link LogMessageManager#getLogMessageManager(NXTDataLogger)}.
	 * @see LogMessageManager
	 */
	public LoggerDebugConsole(LogMessageManager loggerMessageManager) {
		super(loggerMessageManager);
	}

	@Override
	protected int getHandlerTypeID() {
		return TYPE_DEBUG_CONSOLE;
	}

	@Override
	void processMessage(byte[] message, int typeID) {
		// ignores broadcast messages
//		if (typeID==TYPE_ALWAYS_RECEIVE) return;
		
//		System.out.println(getHandlerID() + ": hndlr " + (message[0] & 0xff));
		// Skip processing if not for me
//		if (getHandlerID() != (message[0] & 0xff)) return; // byte 0 => handler ID
		
		// get the command
//		int command = message[1] & 0xff;
//		System.out.println("cmd=" + command + ",l=" + (message.length - HEADER_DATA_OFFSET));
		
		// *** Don't do anything since no messages will coem from PC
	}
	
	/**
	 * Send a message to the NXT Charting Logger debug output console. Messages will have a newline appended.
	 * @param message the string to display.
	 */
	public void println(String message) {
		this.sendMessage(CMD_SET_MESSAGE, message.getBytes());
	}
}
