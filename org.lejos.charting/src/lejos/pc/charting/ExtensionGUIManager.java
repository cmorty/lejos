package lejos.pc.charting;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;


/**
 * Passthrough message plug-in manager. Manages GUI (JPanel) loading/unloading/message routing
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
	
//	/**
//	 * Set the ref to the tabbed pane that will hold passthrough message control panels.
//	 * 
//	 * @param tabbedPane
//	 */
//	void setJTabbedPane(JTabbedPane tabbedPane){
//		this.tabbedPane = tabbedPane;
//		hasInited=true;
//	}
	
	// TODO use abstract class or interface for retval
	private JPanel getPanel(int handlerTypeID, int handlerID){
		// find the matching tab for the plug-in
        int i=0;
        JPanel thePanel = null;
//        System.out.println("searching for " + handlerTypeID + "_" + handlerID);
        for (;i<tabbedPane.getTabCount();i++){
//        	System.out.println("name=" + tabbedPane.getComponentAt(i).getName());
        	String theName = tabbedPane.getComponentAt(i).getName();
        	if (theName != null && theName.equalsIgnoreCase(handlerTypeID + "_" + handlerID)){
        		tabbedPane.setSelectedIndex(i);
        		thePanel = (JPanel) tabbedPane.getComponentAt(i);
        		thePanel.requestFocus();
        		break;
        	}
        }
		return thePanel;
	}
	
	private void tabSetup(int handlerTypeID, int handlerID){
		// find the matching tab for the plug-in
		PanelPIDTune targetPanel = (PanelPIDTune) getPanel(handlerTypeID, handlerID);
        
		if (targetPanel==null) {
        	targetPanel = new PanelPIDTune(handlerID, this); 
        	System.out.println("tabSetup: handlerID=" + handlerID);
        	targetPanel.setName(handlerTypeID + "_" + handlerID);
	    	//jTabbedPane1.addTab("PID Tuning", jp1);
	        tabbedPane.insertTab("PID Tuning-" + handlerID, null, targetPanel, "PIDController Tuning interface", tabbedPane.getTabCount());
//	    	System.out.println("name=" + jp1.getName());
	    	tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
        }
        targetPanel.init();
    	System.out.println("done tab setup");
	}

	void activateHandler(int handlerTypeID, int handlerID) {
		// make sure we have a handler JPanel installed for the requested type & instance ID
		System.out.println("ExtensionGUIManager.activateHandler: handlerTypeID=" + handlerTypeID + ", handlerID=" + handlerID);
		switch(handlerTypeID){
			case TYPE_PID_TUNER:
				tabSetup(handlerTypeID, handlerID);
				break;
			default:
				System.out.println("!** Invalid type handler specified in ExtensionGUIManager.activateHandler");
				return;
		}
	}

	void setPluginName(int handlerTypeID, int handlerID, String name) {
		// TODO use abstract or interface
		// find the matching tab for the plug-in
		PanelPIDTune targetPanel = (PanelPIDTune) getPanel(handlerTypeID, handlerID);
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
		// TODO Auto-generated method stub. Spin through [YTBD] collection and notify of 
		int handlerID = dataPacket[0];
		final PanelPIDTune targetPanel = (PanelPIDTune) getPanel(handlerTypeID, handlerID);
		//System.out.println("notifyTypeHandlers: handlerTypeID=" + handlerTypeID + ", handlerID=" + handlerID);
		
		if (targetPanel==null) return;
		//System.out.println("found panel: " + handlerTypeID + "_" + handlerID);
		
		// message to all handlerTypeID, or zeros (broadcast)
		new Thread (new Runnable(){
			// TODO test case. assume pid tuner
			public void run() {
				targetPanel.processMessage(dataPacket, handlerTypeID);
			}
		}
		).start();
	}

	void setTunneledMessageManager(TunneledMessageManager tunneledMessageManager) {
		// TODO Auto-generated method stub
		tmm = tunneledMessageManager;
		
	}
	
	/**
	 * Send a command to the lejos.util.LogMessageManager.
	 * 
	 * @param command TODO
	 * @param typeID
	 * @param msg the sub-message (i.e. from LogMessageTypeHandler)
	 */
	void sendControlPacket(int typeID, byte[] msg){
		tmm.tunnelTheMessage(typeID, msg);
	}
}
