package lejos.pc.charting;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import lejos.util.EndianTools;


/**
 * Passthrough message manager. Get's DOS. routes message packets to appropriate handlers.
 * 
 * @author Kirk P Thompson
 *
 */
class TunneledMessageManager {
	private static final int CMD_INIT_HANDLER = 0;
	private static final int CMD_SET_PLUGIN_NAME = 2;
	private static final int CMD_DELIVER_PACKET = 3;
	
	
	private DataOutputStream dos;
	private ExtensionGUIManager eGuiManager;
	
	/**
	 * 
	 */
	TunneledMessageManager(ExtensionGUIManager eGuiManager) {
		this.eGuiManager = eGuiManager;
		this.eGuiManager.setTunneledMessageManager(this);
	}

	/**
	 * Set the DataOutputStream (to the NXT from the connection)
	 * 
	 * @param dos
	 */
	void setDataOutputStream(DataOutputStream dos){
		this.dos = dos;
	}
	
	/**
	 * Process a passthrough message. This should be called by an implementation of 
	 * <code>lejos.pc.charting.LoggerListener.tunneledMessageReceived(byte[])</code> when a 
	 * passthrough message is received by the <code>lejos.pc.charting.LoggerProtocolManager</code>.
	 * 
	 * @param message
	 */
	void processMessage(byte[] message){
		int command = message[0] & 0xff;
		int handlerTypeID = message[1] & 0xff;
		int handlerID=0;
		int packetSize =  EndianTools.decodeShortBE(message, 2);
//		System.out.println("TunneledMessageManager.processMessage: packetSize=" + packetSize);
//		System.out.println("message.len=" + message.length);
		// all command must have a packet defined
		if (packetSize<=0) {
			System.out.println("Type=" + handlerTypeID + ": !*** Bad packetSize: " + packetSize);
			return;
		}

		byte[] dataPacket = new byte[packetSize];
		System.arraycopy(message, 4, dataPacket, 0, packetSize);
//		System.out.println("Type=" + handlerTypeID + ",Size=" + dataPacket.length);
//		System.out.println("TunneledMessageManager.processMessage: command val=" + command);
		
		// process the command
		switch (command){
		// TODO add all commands per protocol spec	
		case CMD_INIT_HANDLER:
			// add/init JPanel per handler Type and associate with handler ID
			handlerID=dataPacket[0];
			eGuiManager.activateHandler(handlerTypeID, handlerID); // TODO manage with arrays
			break;
		case CMD_SET_PLUGIN_NAME:
			handlerID=dataPacket[0];
			int strLength = dataPacket[1] & 0xff;
			byte[] strVal = new byte[strLength];
			System.arraycopy(dataPacket, 2, strVal, 0, strLength);
			String displayname = null;
			try {
				displayname = new String(strVal, "US-ASCII");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			eGuiManager.setPluginName(handlerTypeID, handlerID, displayname); 
			break;
		case CMD_DELIVER_PACKET:
			System.out.println("CMD_DELIVER_PACKET");
			eGuiManager.notifyTypeHandlers(handlerTypeID, dataPacket); 
			break;
		default:
			break;
		}
	}
	
	/**
	 * Send a command to the lejos.util.LogMessageManager.
	 * 
	 * @param typeID
	 * @param msg the sub-message (i.e. from LogMessageTypeHandler)
	 */
	void tunnelTheMessage(int typeID, byte[] msg){
		if (dos==null) {
			return;
		}
		byte[] buf = new byte[4 + msg.length]; 
		
		// send the handler type, etc. to NXTDataLogger so it can process the command
		buf[0] = (byte)(CMD_DELIVER_PACKET & 0xff); // set the command
		buf[1] = (byte)(typeID & 0xff); //set the handler type ID
		EndianTools.encodeShortBE(msg.length, buf, 2); // set packet size 
		System.arraycopy(msg, 0, buf, 4, msg.length); // pack it in
		try {
			dos.write(buf);
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("!** tunnelTheMessage failed");
			dos=null;
//			e.printStackTrace();
		}
	}
}
