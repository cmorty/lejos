package lejos.pc.charting;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;


/**
 * Passthrough message plug-in manager. Manages GUI (<code>AbstractTunneledMessagePanel</code>) 
 * loading/unloading/message routing
 * 
 * @author Kirk P. Thompson
 *
 */
class ExtensionGUIManager {
	// **** Types must coorespond to lejos.util.LogMessageTypeHandler
	/**
	 * Use this type to always receive the data package. Basically equivalent to broadcast address.
	 */
	static final int TYPE_ALWAYS_RECEIVE = 0;
	/**
	 * Use this type to ID as PID Tuner. Use <code>PIDTuner</code> to run your <code>LoggerPIDTune</code> implementation.
	 */
	static final int TYPE_PID_TUNER = 1;
		
	private JTabbedPane tabbedPane;
	private TunneledMessageManager tmm;
	
	ExtensionGUIManager(JTabbedPane tabbedPane){
		this.tabbedPane = tabbedPane;
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
        		thePanel.requestFocus();
        		break;
        	}
        }
		return thePanel;
	}
	
	void activateHandler(int handlerTypeID, int handlerID){
		String tabLabel;
        String tabHoverText;
        
		// find the matching tab for the plug-in
		AbstractTunneledMessagePanel targetPanel = getPanel(handlerTypeID, handlerID);
		
		// switch to select handler type
		if (targetPanel==null) {
			switch(handlerTypeID){
			case TYPE_PID_TUNER:
				targetPanel = new PanelPIDTune(handlerID, this);
				tabLabel = "PID Tuning-" + handlerID;
				tabHoverText = "PID Tuning interface";
				break;
			default:
				System.out.println("!** Invalid type handler specified in ExtensionGUIManager.tabSetup");
				return;
			}
			
        	targetPanel.setName(handlerTypeID + "_" + handlerID);
	    	//jTabbedPane1.addTab("PID Tuning", jp1);
	        tabbedPane.insertTab(tabLabel, null, targetPanel, tabHoverText, tabbedPane.getTabCount());
//	    	System.out.println("name=" + jp1.getName());
	    	tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
        }
        targetPanel.init();
	}

	void setPluginName(int handlerTypeID, int handlerID, String name) {
		// find the matching tab for the plug-in
		AbstractTunneledMessagePanel targetPanel = getPanel(handlerTypeID, handlerID);
		if (targetPanel==null) return;
		targetPanel.setPlugInName(name);
	}
	
	/**
	 * Notify all registered handlers based on type ID. Each notified handler must parse the handler ID 
	 * from the data packet to see if the
	 * data packet belongs to it. This is like a targeted broadcast based on handler type.
	 * 
	 * @param handlerTypeID
	 * @param dataPacket
	 */
	void notifyTypeHandlers(final int handlerTypeID, final byte[] dataPacket) {
		// get the handler ID
		int handlerID = dataPacket[0];
		
		// find the AbstractTunneledMessagePanel
		// TODO this doesn't accomodate broadcast handlerTypeID=0 or handlerID=0 as per protocol doc
		final AbstractTunneledMessagePanel targetPanel = getPanel(handlerTypeID, handlerID);
		//System.out.println("notifyTypeHandlers: handlerTypeID=" + handlerTypeID + ", handlerID=" + handlerID);
		
		if (targetPanel==null) return;
		//System.out.println("found panel: " + handlerTypeID + "_" + handlerID);
		
//		// message to all handlerTypeID, or zeros (broadcast)
//		for (LogMessageTypeHandler curItem : arrayMessageTypeHandlers){
//			// if registered handler matches the TYPE_ID sent, or registered handler is set as
//			// broadcast receiver, or the the TYPE_ID sent is broadcast (zero :0)
//			// The handler is responsible for parsing the ID and determining if the packet belongs to it. This
//			// allows all handlers of a specific type to receive the packets (like a type-specific broadcast).
//			if (curItem.getHandlerTypeID()==typeID || 
//					curItem.getHandlerTypeID()==LogMessageTypeHandler.TYPE_ALWAYS_RECEIVE || 
//					typeID == LogMessageTypeHandler.TYPE_ALWAYS_RECEIVE) {
//				curItem.processMessage(packet, typeID);
//			}
//		}
		
		// play nice with Swing and send the packet to processing
		SwingUtilities.invokeLater(new Runnable(){
            public void run() {
            	targetPanel.processMessage(dataPacket, handlerTypeID);
            }
        });
		
	}

	void setTunneledMessageManager(TunneledMessageManager tunneledMessageManager) {
		this.tmm = tunneledMessageManager;
	}
	
	/**
	 * Send a command package to the lejos.util.TunneledMessageManager which will package it 
	 * up as a CMD_DELIVER_PACKET and send to NXT.
	 * 
	 * @param typeID the handler well-defined Type ID
	 * @param msg the sub-message (i.e. from LogMessageTypeHandler)
	 */
	void sendControlPacket(int typeID, byte[] msg){
		tmm.tunnelTheMessage(typeID, msg);
	}

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
