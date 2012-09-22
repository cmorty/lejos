package lejos.pc.charting.extensions;

/**
 * Passed to AbstractTunneledMessage as conduit for message transfer to NXT. For internal use by 
 * <code>lejos.pc.charting.ExtensionGUIManager</code>.
 * 
 * @author Kirk P. Thompson
 *
 */
public interface MessageSender {
	/**
	 * Send a command package to the <code>lejos.pc.charting.TunneledMessageManager</code> which will package it 
	 * up as a CMD_DELIVER_PACKET and deliver to <code>lejos.util.LogMessageManager</code> running on the NXT.
	 * 
	 * @param typeID the handler well-defined Type ID
	 * @param msg the sub-message (i.e. from <code>LogMessageTypeHandler</code>)
	 */
	void sendMessage(int typeID, byte[] msg);
	
}
