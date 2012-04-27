package lejos.util;

/**
 * Base class for passthrough/tunneling message handlers. Must provide a type. Responsible for processing
 * messages from <code>LogMessageManager</code>.
 * <p>
 * A corresponding protocol handler subclass of 
 * <code>lejos.pc.charting.AbstractTunneledMessagePanel</code> must be implemented on the PC-side 
 * <code>NXJChartingLogger</code> 
 * to do anything useful with the messages that are produced with subclasses of this class. 
 * 
 * @author Kirk P. Thompson
 * @see LogMessageManager
 */
public abstract class LogMessageTypeHandler {
	/**
	 * Use this type to always receive the data package. Basically equivalent to broadcast address.
	 */
	protected static final int TYPE_ALWAYS_RECEIVE = 0;
	/**
	 * Use this type to ID as PID Tuner. Use <code>PIDTuner</code> to run your <code>PIDTuningProvider</code> implementation.
	 * @see PIDTuner
	 * @see PIDTuningProvider
	 */
	protected static final int TYPE_PID_TUNER = 1;
	protected static final int HEADER_DATA_OFFSET = 2;
	
	private static int statichandlerID = 0;
	private int handlerID = 0;
	private LogMessageManager loggerMessageManager=null;
	private String displayname= " ";
	
	/**
	 * Takes care of registering with <code>LogMessageManager</code> and incrementing the instance count.
	 * @param loggerMessageManager
	 */
	protected LogMessageTypeHandler(LogMessageManager loggerMessageManager){
		if (loggerMessageManager==null) {
			throw new IllegalArgumentException("no lmm");
		}
		this.loggerMessageManager = loggerMessageManager;
		this.handlerID = ++statichandlerID;
		this.loggerMessageManager.registerMessageTypeHandler(this);
	}
	
	/**
	 * Must provide the well defined handler Type ID. See the constants for <code>LogMessageTypeHandler</code>.
	 * The type ID is used by the PC-side <code>NXJChartingLogger</code> to load the appropriate message
	 * handler in the tabbed pane and route the message to the corresponding
	 * PC-side message handler type (in conjunction with <code>getHandlerID()</code>).
	 * 
	 * @return The handler type ID.
	 */
	protected abstract int getHandlerTypeID();
	
	/**
	 * Provides the unique handler ID. 
	 * This ID is used by the PC-side <code>NXJChartingLogger</code> to route the message to the corresponding
	 * PC-side message handler and tabbed pane and to ensure that messages are routed back to the correct 
     * <code>LogMessageTypeHandler</code> concrete subclass instance.
	 * 
	 * @return The handler instance ID.
	 */
	protected final int getHandlerID(){
		return handlerID;
	}
	
	
	/**
	 * Process the message from <code>LogMessageManager</code> and respond as appropriate. The 
	 * message will contain handler-specific ID, commands and data as per the handler's protocol implementation.
	 * <p>
	 * Must be able to handle <code>typeID=LogMessageTypeHandler.TYPE_ALWAYS_RECEIVE</code> in some way even
	 * if that means just ignoring it.
	 * 
	 * @param message The data packet received from (PC-side) NXJChartingLogger via (NXT-side) NXTDataLogger.
	 * @param typeID the TYPE_ID that was send from NXJChartingLogger
	 */
	abstract void processMessage(byte[] message, int typeID);
	
	/**
	 * @return The reference to the <code>NXTDataLogger</code>.
	 */
	protected final NXTDataLogger getNXTDataLogger(){
		return loggerMessageManager.getNXTDataLogger();
	}
	
	
	/**
	 * Tunnel a Type handler-specific protocol message to the PC. This method builds the Handler ID, command value
	 * and message into an array that is sent through the logger protocol <code>COMMAND_PASSTHROUGH</code> via 
	 * <code>writePassthroughMessage()</code>.
	 * 
	 * @param command The handler type-specific protocol command
	 * @param msg array of bytes containing handler type-specific data to send
	 * @param len how many bytes to send
	 * @param off starting at
	 */
	protected final void sendMessage(int command, byte[] msg, int len, int off){
		byte[] buf = new byte[len + HEADER_DATA_OFFSET]; // size the packet
		buf[0] = (byte)(handlerID & 0xff); // set the unique handler ID
		buf[1] = (byte)(command & 0xff); // set the handler-specific command value
		System.arraycopy(msg, off, buf, HEADER_DATA_OFFSET, len); // aggregate it all in one array

		// Will be packed into a common header for CMD_DELIVER_PACKET.
		loggerMessageManager.sendControlPacket(TYPE_PID_TUNER, buf);
	}
	
	/**
	 * Set the name that is displayed in the NXT Charting Logger tab panel for the associated plugin.
	 * Limited to 40 characters
	 * 
	 * @param displayname
	 */
	public final void setDisplayName(String displayname){
		if (displayname.length()>40) {
			this.displayname = displayname.substring(0, 40);
		} else {
			this.displayname = displayname;
		}
		loggerMessageManager.setPluginDisplayName(this);
	}
	
	final String getDisplayName(){
		return displayname;
	}
	
	/**
	 * Call underlying <code>NXTDataLogger</code>'s writeComment() method so your implementation can
	 * note interesting things as it processes message from NXT Charting Logger
	 * @param comment The comment text
	 */
	protected final void logComment(String comment){
		loggerMessageManager.getNXTDataLogger().writeComment(comment);
	}
}
