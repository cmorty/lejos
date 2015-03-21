package lejos.util;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The protocol manager for passthrough messaging through <code>NXTDataLogger</code> and controller endpoint for
 * <code>LogMessageTypeHandler</code> instances.
 * <p>
 * The <code>NXTDataLogger</code> instance must be real-time mode via <code>startRealtimeLog()</code>.
 * <p>
 * Example showing how to use <code>PIDTuner</code> with a PIDController instance: <pre>
 *    NXTDataLogger dlog = new NXTDataLogger();
 *    NXTConnection conn = Bluetooth.waitForConnection(5000, NXTConnection.PACKET);
 *    try {
 *        dlog.startRealtimeLog(conn);
 *    } catch (IOException e) {
 *        // Do nothing
 *    }
 *       
 *    // Test passthrough message management and PID tuning framework
 *    LogMessageManager lmm = LogMessageManager.getLogMessageManager(dlog);
 *    PIDController pid = new PIDController(0, 50);
 *    pid.setPIDParam(PIDController.PID_KP, 2);
 *    PIDTuner pidTuner = new PIDTuner(pid, lmm);
 *    ...
 * </pre>
 * 
 * @author Kirk P. Thompson
 *
 * @see LogMessageTypeHandler
 * @see NXTDataLogger
 * @see PIDTuner
 * @see SimpleDrive
 * @see LoggerDebugConsole
 */
public class LogMessageManager  {
	private static final LogMessageManager SELF_LMM = new LogMessageManager();
	private static final int CMD_INIT_HANDLER = 0;
	private static final int CMD_SET_PLUGIN_NAME = 2;
	private static final int CMD_DELIVER_PACKET = 3;
	
	volatile boolean amLogging = false;
	DataInputStream dis;
	volatile boolean killListener;
	private NXTDataLogger logger;
//	private int counter = 0;
	
	ArrayList<LogMessageTypeHandler> arrayMessageTypeHandlers = null;

	/**
	 * singleton pattern
	 */
	private LogMessageManager(){
		super();
	}
	
	/**
	 * Get the <code>LogMessageManager</code> singleton and set up the passed <code>NXTDataLogger</code>
	 * for passthrough message handling. 
	 * 
	 * @param logger An instance of <code>NXTDataLogger</code> that is in realtime mode.
	 * @return The singleton <code>LogMessageManager</code> instance ref
	 * @throws IllegalStateException if <code>NXTDataLogger.startRealtimeLog()</code> has not been called and active.
	 */
	public static LogMessageManager getLogMessageManager(NXTDataLogger logger){
		SELF_LMM.setLogger(logger);
		
		return SELF_LMM;
	}
	
	private void setLogger(NXTDataLogger logger){
		this.logger = logger;
		// remove any handlers on new logger
		if (arrayMessageTypeHandlers!= null) {
			arrayMessageTypeHandlers.removeAll(arrayMessageTypeHandlers);
		}
		this.logger.registerTunnelManager(new LoggerHook());
	}
	

	/**
	 * @param dataStream
	 */
	void startNewListener(DataInputStream dataStream) {
		//  kill the existing thread and wait until exits
		ensureListenerDies();
		
		this.dis = dataStream;
		amLogging = true;
		Thread t1 = new Thread(new Runnable(){
			private static final int CMD_IGNORE = -1;
			
			public void run() {
				int handlerTypeID=0;
				int packetSize=0;
				int command;
				byte[] buf;
				
				killListener = false;
//				System.out.println("threadrun");
				
				while(!killListener) {
					try {
						if (dis != null) {
							command	= dis.read();
							handlerTypeID = dis.read();
							packetSize = dis.readShort() & 0xffff;
						} else {
							Delay.msDelay(100);
							command = CMD_IGNORE;
						}
						
						// process the command
						switch (command){
							case CMD_IGNORE:
								break;
							case CMD_DELIVER_PACKET:
								if (packetSize<=0) break;
								buf = new byte[packetSize];
								getBytes(dis, buf, packetSize, 0);
								notifyTypeHandlers(handlerTypeID, buf);
								//System.out.println("Type=" + handlerTypeID + ",Size=" + packetSize);
								break;
							default:
								break;
						}
					} catch (IOException e) {
						//System.out.println("ioerror");
						break;
					}
					
				}
				
				synchronized(dis) {
					amLogging = false;
					dis.notify();
				}
			}
			
			private void getBytes(DataInputStream dis1, byte[] readBytes, int byteCount, int offset) throws EOFException
		    {
		        // Get byteCount bytes from the buffer.
		        int readVal=-1;
		        for (int i=0;i<byteCount;i++) {
		            try {
		                readVal=dis1.read();
		                if (readVal==-1) throw new EOFException();
		                readBytes[offset + i]=(byte)readVal;
		            } catch (IOException e) {
		                throw new EOFException("getBytes: is.read(): " + e);
		            }
		        }
		    }
        });
        t1.setDaemon(true);
        t1.start();
	}
	
	
	
	/**
	 * Notify all registered handlers based on type ID. Each notified handler must parse the handler ID 
	 * from the data packet to see if the
	 * data packet belongs to it. This is like a targeted broadcast based on handler type.
	 * 
	 * @param typeID
	 * @param packet
	 */
	void notifyTypeHandlers(int typeID, byte[] packet){
//		System.out.println("nth: " + typeID);
		for (LogMessageTypeHandler curItem : arrayMessageTypeHandlers){
			// if registered handler matches the TYPE_ID sent, or registered handler is set as
			// broadcast receiver, or the the TYPE_ID sent is broadcast (zero :0)
			// The handler is responsible for parsing the ID and determining if the packet belongs to it. This
			// allows all handlers of a specific type to receive the packets (like a type-specific broadcast).
			if (curItem.getHandlerTypeID()==typeID || 
					curItem.getHandlerTypeID()==LogMessageTypeHandler.TYPE_ALWAYS_RECEIVE || 
					typeID == LogMessageTypeHandler.TYPE_ALWAYS_RECEIVE) {
				curItem.processMessage(packet, typeID);
			}
		}
		
		
	}
	
	void ensureListenerDies() {
		//System.out.println("ensureListenerDies");
		if (amLogging) {
			synchronized(dis) {
				while (amLogging) { 
					try {
						killListener = true;
						dis.wait();
					} catch (InterruptedException e) {
						// do nothing
					}
				}
			}
		}
	}
	
	private class LoggerHook implements LogMessageListener {
		public LoggerHook() {
			// do nothing
		}

		/* (non-Javadoc)
		 * @see lejos.util.LogMessageListener#connectionClosing()
		 */
		public void connectionClosing() {
			//  tell the existing listener thread (if any) to die
			killListener=true;
		}
	
		/* (non-Javadoc)
		 * @see lejos.util.LogMessageListener#setInputStream(java.io.DataInputStream)
		 */
		public void setInputStream(DataInputStream dis) {
//			System.out.println("lmm setIS");
			startNewListener(dis);
		}
	}
	
	/**
	 * Register a message type handler to do type-specific messages via the <code>NXTDataLogger</code>.
	 * Called by <code>LogMessageTypeHandler</code> on instantiation by passed <code>LogMessageManager</code>
	 * concrete subclass instance.
	 * 
	 * @param typeHandler the LogMessageTypeHandler
	 */
	void registerMessageTypeHandler(LogMessageTypeHandler typeHandler) {
		if (arrayMessageTypeHandlers==null){
			arrayMessageTypeHandlers = new ArrayList<LogMessageTypeHandler>();
		}
		if (!arrayMessageTypeHandlers.contains(typeHandler)) {
			// notify TunneledMessageManager (PC) about this type of module and unique handler ID so it can
			// init and load appropriate JPanel, etc. on PC NXJChartingLogger
			byte[] buf= { (byte)(typeHandler.getHandlerID() & 0xff)};
			
			arrayMessageTypeHandlers.add(typeHandler);
//			System.out.println("reghndrlr " + typeHandler.getHandlerTypeID());
			// send the handler info to NXJChartingLogger so it can load the required JPanel in the TabbedPane
			tunnelTheMessage(CMD_INIT_HANDLER,typeHandler.getHandlerTypeID(), buf);
		}
	}
	
	/**
	 * write to logger.writePassthroughMessage which will do a COMMAND_PASSTHROUGH protocol exchange to
	 * send data to PC.
	 * 
	 * @param command The passthrough message header-specific command
	 * @param typeID The Target Handler type ID. See <code>LogMessageTypeHandler</code> constants.
	 * @param msg the sub-message (i.e. from <code>LogMessageTypeHandler</code>)
	 */
	private synchronized void tunnelTheMessage(int command, int typeID, byte[] msg){
		if (msg==null) msg = new byte[0];
		byte[] buf = new byte[4 + msg.length]; 
		
		// send the handler type to NXJChartingLogger so it can load the required JPanel in the TabbedPane
		buf[0] = (byte)(command & 0xff); // set the command
		buf[1] = (byte)(typeID & 0xff); //set the handler type ID
		EndianTools.encodeShortBE(msg.length, buf, 2); // set packet size 
		System.arraycopy(msg, 0, buf, 4, msg.length); // pack it in
		logger.writePassthroughMessage(buf); // Bon Voyage!
		//System.out.println("tunneled");
	}
	
	/**
	 * Used by LogMessageTypeHandler
	 * 
	 * @return the NXTDataLogger set at instantiation
	 */
	NXTDataLogger getNXTDataLogger(){
		return this.logger;
	}
	
	/**
	 * Package handler control message with common header and send
	 * 
	 * @param typeID
	 * @param msg handler control message
	 */
	void sendControlPacket(int typeID, byte[] msg){
		tunnelTheMessage(CMD_DELIVER_PACKET, typeID, msg);
	}
	
	
	/**
	 * Set the display name in the charting logger plugin
	 * 
	 * @param lmm the LogMessageTypeHandler
	 */
	void setPluginDisplayName(LogMessageTypeHandler lmm){
		byte[] str = lmm.getDisplayName().getBytes();
		byte[] buf = new byte[2 + str.length]; 
//		System.out.println("str.length=" + str.length);
//		Button.waitForAnyPress();
		
		// set the handler ID
		buf[0] = (byte)(lmm.getHandlerID() & 0xff); // set the handler ID
		buf[1] = (byte)(str.length & 0xff); // set the string length
		System.arraycopy(str, 0, buf, 2, str.length);
		tunnelTheMessage(CMD_SET_PLUGIN_NAME, lmm.getHandlerTypeID(), buf);
	}
}
