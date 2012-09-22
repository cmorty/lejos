package lejos.pc.charting;

import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import lejos.pc.charting.extensions.AbstractTunneledMessagePanel;
import lejos.pc.charting.extensions.MessageSender;

/**
 * Passthrough message plug-in manager. Manages GUI (<code>AbstractTunneledMessagePanel</code>) 
 * loading/unloading/message routing.
 * 
 * TODO This class is where dynamic loading of "plug-in" panels should be implemented.
 * 
 * @author Kirk P. Thompson
 *
 */
class ExtensionGUIManager {
	private JTabbedPane tabbedPane;
	private TunneledMessageManager tmm;
	private MessageConduit messageConduit;
	
	private Hashtable<Integer, ExtensionLoader.PlugInDefinition> plugins = new Hashtable<Integer, ExtensionLoader.PlugInDefinition>();
	
	/**
	 * Used for invokeLater of processMEssage()
	 * @author Kirk 
	 *
	 */
	private class MessageProcessorThread implements Runnable{
		private AbstractTunneledMessagePanel atmp;
		private byte[] dataPacket;
		private int handlerTypeID;
		
		MessageProcessorThread(AbstractTunneledMessagePanel atmp, byte[] innerdataPacket, int innerhandlerTypeID){
			this.atmp = atmp;
			this.dataPacket = innerdataPacket;
			this.handlerTypeID = innerhandlerTypeID;
		}

		public void run() {
			atmp.processMessage(dataPacket, handlerTypeID);
		}
	}
	
	ExtensionGUIManager(JTabbedPane tabbedPane){
		this.tabbedPane = tabbedPane;
		// use MessageSender type to keep ExtensionGUIManager private but still allow
		// message transfer
		this.messageConduit = new MessageConduit();
		SwingUtilities.invokeLater(new Runnable(){
            public void run() {
            	ExtensionGUIManager.this.plugins = ExtensionLoader.getPlugIns();
            }
        });
	}
	
	/**
	 * Use MessageSender type to keep ExtensionGUIManager private but still allow message transfer.
	 *
	 */
	private class MessageConduit implements MessageSender{
		public void sendMessage(int typeID, byte[] msg) {
			/**
			 * Send a command package to lejos.pc.charting.TunneledMessageManager which will package it 
			 * up as a CMD_DELIVER_PACKET and deliver to lejos.util.LogMessageManager running on the NXT.
			 * 
			 * @param typeID the handler well-defined Type ID
			 * @param msg the sub-message (i.e. from LogMessageTypeHandler)
			 */
			tmm.tunnelTheMessage(typeID, msg);
		}
	}
	
	private AbstractTunneledMessagePanel getPanel(int handlerTypeID, int handlerID){
		// find the matching tab for the plug-in
        int i=0;
        AbstractTunneledMessagePanel thePanel = null;
//        System.out.println("searching for " + handlerTypeID + "_" + handlerID);
        for (;i<tabbedPane.getTabCount();i++){
//        	System.out.println("name=" + tabbedPane.getComponentAt(i).getName());
        	String theName = tabbedPane.getComponentAt(i).getName();
        	if (theName != null && theName.equalsIgnoreCase(handlerTypeID + "_" + handlerID)){
        		tabbedPane.setSelectedIndex(i);
        		thePanel = (AbstractTunneledMessagePanel) tabbedPane.getComponentAt(i);
        		if (thePanel.requestFocusOnMessage()) {
        			thePanel.requestFocus();
        		}
        		break;
        	}
        }
		return thePanel;
	}
	
	/** 
	 * Called by lejos.pc.charting.TunneledMessageManager.processMessage(byte[]) in response to
	 * lejos.pc.charting.TunneledMessageManager.CMD_INIT_HANDLER message
	 * 
	 * @param handlerTypeID
	 * @param handlerID Sent from NXT by the lejos.util.LogMessageTypeHandler implementation
	 */
	void activateHandler(int handlerTypeID, int handlerID){
		String tabLabel = null;
        String tabHoverText = null;
        
        if (plugins==null) {
        	System.out.println("activateHandler(): null plugins");
        	return;
        }
        
		// find the matching tab for the plug-in
		AbstractTunneledMessagePanel targetPanel = getPanel(handlerTypeID, handlerID);
		
		// if not exist as tab already, get handler type
		if (targetPanel==null) {
//			System.out.println("looking for " + handlerTypeID);
			
			// Get the plug-in by handlerTypeID
			ExtensionLoader.PlugInDefinition item = plugins.get(new Integer(handlerTypeID));
			if (item==null) {
				System.out.println("could not find handlerType " + handlerTypeID);
				return;
			}
			// get the plug-in class instance from config item
			targetPanel = item.getPluginInstance(handlerID, this.messageConduit);
			if (targetPanel==null) {
        		System.out.println("null target panel");
        		return;
        	}
			// set the label and hovertext
			HashMap<String, String> tempMap = item.getMap();
			tabLabel = tempMap.get(ExtensionLoader.TAB_LABEL) + "-" + handlerID;
			tabHoverText = tempMap.get(ExtensionLoader.TAB_HOVER_TEXT);
			targetPanel.setName(handlerTypeID + "_" + handlerID);
        	if (!targetPanel.showPollButton()){
        		targetPanel.setButtonRefreshDataVisible(false);
        	}
	        tabbedPane.insertTab(tabLabel, null, targetPanel, tabHoverText, tabbedPane.getTabCount());
	    	tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
        }
        targetPanel.init();
	}

	void setPluginLabel(int handlerTypeID, int handlerID, String label) {
		// find the matching tab for the plug-in
		AbstractTunneledMessagePanel targetPanel = getPanel(handlerTypeID, handlerID);
		if (targetPanel==null) return;
		targetPanel.setPlugInLabel(label);
	}
	
	/**
	 * Notify all registered handlers based on type ID. Each notified handler must parse the handler ID 
	 * from the data packet to see if the
	 * data packet belongs to it. This is like a targeted broadcast based on handler type.
	 * 
	 * @param handlerTypeID
	 * @param dataPacket
	 */
	void notifyTypeHandlers(int handlerTypeID, byte[] dataPacket) {
		String[] theIDs;
		int compareTypeID;
		AbstractTunneledMessagePanel targetPanel;
		
		//System.out.println("notifyTypeHandlers: handlerTypeID=" + handlerTypeID + ", handlerID=" + handlerID);
		
		// accomodate broadcast handlerTypeID=0 or handlerID=0 as per protocol doc
		// notify all AbstractTunneledMessagePanel tabs with the same handlerTypeID
		int i=0;
        for (;i<tabbedPane.getTabCount();i++){
//        	System.out.println("name=" + tabbedPane.getComponentAt(i).getName());
        	
        	if (tabbedPane.getComponentAt(i) instanceof AbstractTunneledMessagePanel){
        		targetPanel = (AbstractTunneledMessagePanel)tabbedPane.getComponentAt(i);
        		theIDs = tabbedPane.getComponentAt(i).getName().split("_");
        		compareTypeID = Integer.parseInt(theIDs[0]);
        		
        		// if registered handler matches the HANDLER_TYPE_ID sent,
    			// The handler is responsible for parsing the handler ID and determining if the packet belongs to it. This
    			// allows all handlers of a specific type to receive the packets (like a type-specific broadcast).
        		if (handlerTypeID==compareTypeID) {
        			// play nice with Swing and send the packet to processing
        			SwingUtilities.invokeLater(new MessageProcessorThread(targetPanel, dataPacket, handlerTypeID));
        			if (targetPanel.requestFocusOnMessage()) {
        				tabbedPane.setSelectedIndex(i);
        				targetPanel.requestFocus();
            		}
        		}
        	}
        }
	}

	void setTunneledMessageManager(TunneledMessageManager tunneledMessageManager) {
		this.tmm = tunneledMessageManager;
	}
	
//	/**
//	 * Send a command package to lejos.pc.charting.TunneledMessageManager which will package it 
//	 * up as a CMD_DELIVER_PACKET and deliver to lejos.util.LogMessageManager running on the NXT.
//	 * 
//	 * @param typeID the handler well-defined Type ID
//	 * @param msg the sub-message (i.e. from LogMessageTypeHandler)
//	 */
//	void sendControlPacket(int typeID, byte[] msg){
////		System.out.println("sendControlPacket. msg[1]=" + msg[1]);
//		tmm.tunnelTheMessage(typeID, msg);
//	}

	/**
	 * Iterate and notify AbstractTunneledMessagePanels that the connection is severed
	 */
	void dataInputStreamEOF() {
		// iterate and notify AbstractTunneledMessagePanels that the connection is severed
		int i=0;
        for (;i<tabbedPane.getTabCount();i++){
        	if (tabbedPane.getComponentAt(i) instanceof AbstractTunneledMessagePanel) {
        		((AbstractTunneledMessagePanel) tabbedPane.getComponentAt(i)).dataInputStreamEOF();
        	}
        }
	}
}
