package lejos.pc.charting;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import lejos.util.EndianTools;

/**
 * Base class for logger extension GUI plug-ins for utilizing the tunneled message functionality
 * of NXT Charting Logger.
 * 
 * @author Kirk P. Thompson
 *
 */
public abstract class AbstractTunneledMessagePanel extends JPanel {
	/**
	 * Use this type to always receive the data package. Basically equivalent to
	 * broadcast address.
	 */
	public static final int TYPE_ALWAYS_RECEIVE = 0;
	/**
	 * Use this type to ID as PID Tuner. Use <code>PIDTuner</code> to run your
	 * <code>LoggerPIDTune</code> implementation.
	 */
	public static final int TYPE_PID_TUNER = 1;


	private JLabel lblPluginName = new JLabel("Name: undefined");
	private int handlerID = 0;
	private ExtensionGUIManager extensionGUIManager;
	private ArrayList<FieldManager> arraylistFieldManager = new ArrayList<FieldManager>();
	private NumberFormat formatterInteger =  NumberFormat.getIntegerInstance();
	private DecimalFormat formatterDecimal =  (DecimalFormat)NumberFormat.getInstance();
	private volatile boolean skipPropertyEvent=false;
	
	public AbstractTunneledMessagePanel(int handlerID, ExtensionGUIManager extensionGUIManager) {
		formatterDecimal.setMinimumFractionDigits(1);
		formatterDecimal.setMaximumFractionDigits(7);
		initGUI();
		this.extensionGUIManager = extensionGUIManager;
		this.handlerID= handlerID;	
	}
	
	/**
	 * Called when session connection is closed form NXT
	 */
	void connectionClosed(){
		disableRegisteredFields();
	}
	/**
	 * User Input/message output manager. Sends data on lost focus & enter. Does validations via
	 * <code>JFormattedTextField</code>, etc.
	 * 
	 * @author Kirk P. Thompson
	 * 
	 */
	final class FieldManager {
		/**
		 * Use for Decimal formatted numbers (i.e. float)
		 */
		static final int DT_DECIMAL = 1;
		/**
		 * Use for Integer formatted numbers
		 */
		static final int DT_INTEGER = 2;
		/**
		 * Use for unformatted input
		 */
		static final int DT_CHAR = 3;
		private static final int DT_BOOLEAN = 4;

		JFormattedTextField fieldObject = null;
		AbstractButton buttonObject = null;
		
		private int GETcommandID, SETcommandID;
		private int datatype;
		
		private class NumberFieldListener implements PropertyChangeListener{
			public void propertyChange(PropertyChangeEvent evt) {
//				System.out.println(evt.getSource().getClass().getSimpleName() + ": old=" + evt.getOldValue() + ", new=" + evt.getNewValue());
				
				// PropertyChangeEvent fires when value changes so just test for resultant null
				if (!skipPropertyEvent && evt.getNewValue()!=null) {
					sendDataFromField(evt.getSource());
				}
			}
		}
		
		private class ButtonFieldListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
//				System.out.println(((AbstractButton)e.getSource()).isSelected());
				sendDataFromField(e.getSource());
			}
		}
		
		NumberFieldListener nfl = new NumberFieldListener();
		ButtonFieldListener bfl = new ButtonFieldListener();
		
		FieldManager(JFormattedTextField fieldObject, int GETcommandID, int SETcommandID, int datatype) {
			if (datatype<DT_DECIMAL || datatype>DT_CHAR) {
				throw new IllegalArgumentException("Datatype must be DT_DECIMAL,DT_INTEGER, or DT_CHAR");
			}
			this.GETcommandID = GETcommandID;
			this.SETcommandID = SETcommandID;
			this.fieldObject = fieldObject;
			this.datatype = datatype;
			this.fieldObject.addPropertyChangeListener("value", nfl);
		}
		
		FieldManager(AbstractButton buttonObject, int GETcommandID, int SETcommandID) {
			this.GETcommandID = GETcommandID;
			this.SETcommandID = SETcommandID;
			this.buttonObject = buttonObject;
			this.datatype = DT_BOOLEAN;
			this.buttonObject.addActionListener(bfl);
		}
		
		void sendDataFromField(Object objRef){
			for (FieldManager item: arraylistFieldManager){
				if (item.fieldObject==objRef || item.buttonObject==objRef) {
//					System.out.println("sendDataFromField: item.getSETCommandID()" + item.getSETCommandID());
					sendMessage(item.getSETCommandID(), item.encodeItem());
					break;
				}
			}
		}
		
		int getGETCommandID() {
			return this.GETcommandID;
		}

		int getSETCommandID() {
			return this.SETcommandID;
		}

		void decodeAndSetItem(byte[] value, int off) {
			skipPropertyEvent=true;
			switch (this.datatype) {
			case DT_DECIMAL:
				Float fval = new Float(Float.intBitsToFloat(EndianTools.decodeIntBE(value, off)));
//				System.out.println("cmd " + SETcommandID + ": float val: " + 
				fieldObject.setValue(fval); 
				break;
			case DT_INTEGER:
				Integer ival = new Integer(EndianTools.decodeIntBE(value, off));
				fieldObject.setValue(ival);
				break;
			case DT_CHAR:
				// TODO
				break;
			case DT_BOOLEAN:
				boolean bval = (value[off] != 0);
//				System.out.println("DT_BOOLEAN: " + bval);
				//previousValue = buttonObject.isSelected()?"1":"0";
				buttonObject.setSelected(bval);
				break;
			default:
				break;
			}
			skipPropertyEvent=false;
			setEnabled(true);
		}

		byte[] encodeItem() {
			byte[] value = new byte[4];
			
			switch (this.datatype) {
			case DT_DECIMAL:
				int fval=0;
				try {
					fval = Float.floatToIntBits(
							Float.parseFloat(fieldObject.getValue().toString())
							);
				} catch(NumberFormatException e) {
					System.out.println("GetValue=" + fieldObject.getValue());
					System.out.println("cmd=" + SETcommandID);
					
					e.printStackTrace();
					break;
				}
				//System.out.println("DT_DECIMAL: " + fieldObject.getText() + ", value=" + fieldObject.getValue());
				EndianTools.encodeIntBE(fval, value, 0);
				break;
			case DT_INTEGER:
				EndianTools.encodeIntBE(((Long)fieldObject.getValue()).intValue(), value, 0);
				break;
			case DT_CHAR:
				// TODO
				break;
			case DT_BOOLEAN:
				value = new byte[1];
				value[0] = (byte)(buttonObject.isSelected()?1:0);
				break;
			default:
				System.out.println("!** encodeItem: bad datatype! " + this.datatype);
				return null;
			}
			return value;
		}
		
		void setEnabled(boolean enabled){
			if (fieldObject!=null) fieldObject.setEditable(enabled);
			if (buttonObject!=null) buttonObject.setEnabled(enabled);
		}
	}

	/**
	 * Create and register a <code>JFormattedTextField</code> for protocol handling. Used for IDing 
	 * associated command, formatting and processing text field
	 * values. Each <code>JFormattedTextField</code> represents a prototype datapoint that has 2 command IDs 
	 * (GETTER and a SETTER) and is managed by the
	 * handler type protocol. 
	 * 
	 * @param SETcommandID The SET command ID as specified in the handler type protocol
	 * @param GETcommandID The GET command ID as specified in the handler type protocol
	 * @param datatype The datatype code for formatting. See <code>FieldManager</code> constants.
	 * @return A <code>JFormattedTextField</code> instance
	 * @see FieldManager#DT_DECIMAL
	 * @see FieldManager#DT_INTEGER
	 * @see FieldManager#DT_CHAR
	 */
	protected final JFormattedTextField getBoundTextField(int SETcommandID, int GETcommandID, int datatype) {
		if (datatype<FieldManager.DT_DECIMAL || datatype>FieldManager.DT_CHAR) {
			throw new IllegalArgumentException("Datatype must be DT_DECIMAL,DT_INTEGER, or DT_CHAR");
		}
		
		JFormattedTextField textfield;
		
		switch (datatype) {
		case FieldManager.DT_DECIMAL:
			textfield = new JFormattedTextField(formatterDecimal);
			break;
		case FieldManager.DT_INTEGER:
			textfield = new JFormattedTextField(formatterInteger);
			break;
		default:
			System.out.println("default");
			textfield = new JFormattedTextField();
			break;
		}
		 
		textfield.setHorizontalAlignment(SwingConstants.RIGHT);
		textfield.setColumns(10);
		arraylistFieldManager.add(new FieldManager(textfield, GETcommandID, SETcommandID, datatype));
		return textfield;
	}
	/**
	 * Create and register a <code>JCheckBox</code> for protocol handling. Used for IDing associated command (boolean only),
	 *  formatting and processing checkbox, etc. field
	 * values. Each <code>JCheckBox</code> represents a prototype datapoint that has 2 command IDs 
	 * (GETTER and a SETTER) and is managed by the
	 * handler type protocol. 
	 * 
	 * @param label The label for the <code>JCheckBox</code>
	 * @param SETcommandID The SET command ID as specified in the handler type protocol. 
	 * <code>false</code> values are sent as a byte with value of zero (0).
	 * @param GETcommandID The GET command ID as specified in the handler type protocol
	 * @return A <code>JCheckBox</code> instance
	 */
	protected final JCheckBox getBoundChkbox(String label, int SETcommandID, int GETcommandID) {
		JCheckBox cb = new JCheckBox(label);
		FieldManager fm = new FieldManager(cb, GETcommandID, SETcommandID);
		arraylistFieldManager.add(fm);
		return cb;
	}
	
	/**
	 * Init the JPanel and children containers, components, etc. Make sure your subclass
	 * calls this or the poll button will not be instantiated.
	 */
	protected void initGUI() {
		setPreferredSize(new Dimension(621, 177));
		setMinimumSize(new Dimension(621, 177));
		setLayout(null);

		JButton btnRefreshData = new JButton("Poll NXT");
		btnRefreshData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pollForRemoteHandlerValues();
			}
		});
		btnRefreshData.setBounds(522, 143, 89, 23);
		add(btnRefreshData);

		lblPluginName.setBounds(13, 9, 598, 14);
		add(lblPluginName);
	}

	

	/**
	 * Sets the name label displayed in the GUI panel.
	 * @param name The label
	 */
	protected void setPlugInName(String name) {
		lblPluginName.setText("Name: " + name);
	}

	/**
	 * Must provide the well defined handler Type ID. See the constants for
	 * <code>LogMessageTypeHandler</code>. The type ID is used by the PC-side
	 * <code>NXJChartingLogger</code> to load the appropriate message handler in
	 * the tabbed pane and route the message to the corresponding PC-side
	 * message handler type (in conjunction with <code>getHandlerID()</code>).
	 * 
	 * @return The handler type ID.
	 */
	abstract protected int getHandlerTypeID();

	/**
	 * Provides the [unique] handler ID passed in the constructor. 
	 * This ID is used to route the message to the
	 * corresponding message handler and tabbed pane and to ensure that
	 * messages are routed back to the correct
	 * <code>lejos.util.LogMessageTypeHandler</code> concrete subclass instance.
	 * 
	 * @return The handler instance ID.
	 */
	protected final int getHandlerID() {
		return handlerID;
	}

	/**
	 * Process the message and respond as appropriate. The message will contain
	 * handler-specific ID, commands and data as per the handler's protocol
	 * implementation.
	 * <p>
	 * Must be able to handle
	 * <code>typeID=LogMessageTypeHandler.TYPE_ALWAYS_RECEIVE</code> in some way
	 * even if that means just ignoring it.
	 * 
	 * @param message
	 *            The data packet received by (PC-side) NXJChartingLogger from
	 *            (NXT-side) NXTDataLogger.
	 * @param typeID
	 *            the TYPE_ID that was sent from NXTDataLogger
	 */

	synchronized final void processMessage(byte[] message, int typeID) {
		// ignores broadcast messages
		if (typeID==TYPE_ALWAYS_RECEIVE) return;
		
		// Skip processing if not for me
		if (getHandlerID() != (message[0] & 0xff)) return; // byte 0 => handler ID
		//System.out.println("AbstractTunneledMessagePanel.processMessage: message[0]=" + message[0] + ", getHandlerID()=" + getHandlerID() );
		
		// parse the command
		int command = message[1] & 0xff;
//		System.out.println("AbstractTunneledMessagePanel.processMessage: command=" + command);
		
		// iterate through the fields and GET or SET
		for (FieldManager item : arraylistFieldManager) {
			// reply with SET command on a GET request from the NXT
			if (item.getGETCommandID()==command) {
				sendMessage(item.getSETCommandID(), item.encodeItem());
			}
			
			// set the field on SET request from NXT
			if (item.getSETCommandID()==command) {
				item.decodeAndSetItem(message, 2); 
			}
		}
	}
	
	private void disableRegisteredFields(){
		for (FieldManager item : arraylistFieldManager) {
			item.setEnabled(false);
		}
		this.repaint();
	}
	
	/**
	 * Query the remote handler to send all defined values. Used in init().
	 * @see #init
	 */
	void pollForRemoteHandlerValues(){
		disableRegisteredFields();
		for (FieldManager item : arraylistFieldManager) {
			requestValueMessage(item);
		}
	}
	
	/**
	 * Tunnel a Type handler-specific protocol message to the PC. This method
	 * builds the Handler ID, command value and message into a packet that is
	 * sent through the logger protocol <code>COMMAND_PASSTHROUGH</code> via
	 * <code>writePassthroughMessage()</code>.
	 * 
	 * @param command
	 *            The handler type-specific protocol command
	 * @param msg
	 *            array of bytes containing handler type-specific data to send
	 * @param len
	 *            how many bytes to send
	 * @param off
	 *            starting at
	 */
	protected final void sendMessage(int command, byte[] msg, int len, int off) {
		if (msg==null) {
			System.out.println("** sendMessage: NULL MESSAGE. cmd=" + command);
		}
		byte[] buf = new byte[len + 2]; // size the packet
		buf[0] = (byte) (handlerID & 0xff); // set the unique handler ID
		buf[1] = (byte) (command & 0xff); // set the handler-specific command value
		System.arraycopy(msg, off, buf, 2, len); // aggregate it all in one array
		//System.out.println("AbstractTunneledMessagePanel.sendMessage: command=" + command);
		// Will be packed into a common header for CMD_DELIVER_PACKET.
		extensionGUIManager.sendControlPacket(TYPE_PID_TUNER, buf);
	}
	
	private void sendMessage(int command, byte[] msg){
		sendMessage(command, msg, msg.length, 0);	
	}
	
	private void requestValueMessage(FieldManager fieldItem){
		byte[] buf = new byte[2];
		buf[0] = (byte) (handlerID & 0xff); // set the unique handler ID
		buf[1] = (byte) (fieldItem.getGETCommandID() & 0xff); // set the handler-specific command value
		// Will be packed into a common header for CMD_DELIVER_PACKET.
		//System.out.println("AbstractTunneledMessagePanel.requestValueMessage: command=" + fieldItem.getGETCommandID());
		extensionGUIManager.sendControlPacket(TYPE_PID_TUNER, buf);	
	}
	
	/**
	 * Do any intialization, Default is to poll <code>lejos.util.LogMessageTypeHandler</code> concrete 
	 * subclass (via GET commands) on NXT for all current values.
	 * 
	 * @see #pollForRemoteHandlerValues
	 */
	protected void init(){
		pollForRemoteHandlerValues();
	}
	
	/**
	 * Called by <code>ExtensionGUIManager</code> when NXT logging session connection ends. This
	 * disables all registered inputs in the panel. Can be overridden if that behavior is unwanted.
	 */
	protected void dataInputStreamEOF(){
		disableRegisteredFields();
	}
}
