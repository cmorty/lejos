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
	private JTabbedPane tabbedPane;
	private TunneledMessageManager tmm;
	
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
			case AbstractTunneledMessagePanel.TYPE_PID_TUNER:
				targetPanel = new PanelPIDTune(handlerID, this);
				tabLabel = "PID Tuning-" + handlerID;
				tabHoverText = "PID Tuning interface";
				break;
			case AbstractTunneledMessagePanel.TYPE_ROBOT_DRIVE:
				targetPanel = new PanelRobotDrive(handlerID, this);
				tabLabel = "Drive-" + handlerID;
				tabHoverText = "Robot Driver interface";
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
        			tabbedPane.setSelectedIndex(i);
        			targetPanel.requestFocus();
        		}
        	}
        }
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
//		System.out.println("sendControlPacket. msg[1]=" + msg[1]);
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
