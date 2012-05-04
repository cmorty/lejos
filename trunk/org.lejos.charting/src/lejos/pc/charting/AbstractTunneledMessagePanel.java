package lejos.pc.charting;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lejos.util.EndianTools;

/**
 * Base class for logger extension GUI plug-ins for utilizing the tunneled message functionality
 * of NXT Charting Logger.
 * 
 * @author Kirk P. Thompson
 *
 */
public abstract class AbstractTunneledMessagePanel extends JPanel {
	// **** Types must coorespond to lejos.util.LogMessageTypeHandler
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
	/**
	 * Use this type to ID as simple robot drive
	 */
	public static final int TYPE_ROBOT_DRIVE = 2;
	
	/**
	 * Use for GET or SET to ignore
	 */
	public static final int  CMD_IGNORE = 256;
	
	private JLabel lblPluginName = new JLabel("");
	private int handlerID = 0;
	private int handlerTypeID = TYPE_ALWAYS_RECEIVE;
	private ExtensionGUIManager extensionGUIManager;
	private ArrayList<CommandManager> arraylistFieldManager = new ArrayList<CommandManager>();
	private NumberFormat formatterInteger =  NumberFormat.getIntegerInstance();
	private DecimalFormat formatterDecimal =  (DecimalFormat)NumberFormat.getInstance();
	private volatile boolean skipPropertyEvent=false;
	
	public AbstractTunneledMessagePanel(int handlerID, ExtensionGUIManager extensionGUIManager) {
		formatterDecimal.setMinimumFractionDigits(1);
		formatterDecimal.setMaximumFractionDigits(7);
		this.extensionGUIManager = extensionGUIManager;
		this.handlerID= handlerID;	
		this.handlerTypeID = getHandlerTypeID();
		initGUI();
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
	final class CommandManager {
		/**
		 * Use for Decimal formatted numbers (i.e. float). Data will be encoded and sent as a float.
		 */
		static final int DT_DECIMAL = 1;
		/**
		 * Use for Integer formatted numbers. Data will be encoded and sent as an int.
		 */
		static final int DT_INTEGER = 2;
		/**
		 * Use for unformatted input. Data will be encoded and sent as a String of US-ASCII.
		 */
		static final int DT_CHAR = 3;
		private static final int DT_SELECTED_BOOLEAN = 4;
		private static final int DT_CUSTOM_MESSAGE = 5;
		private static final int DT_MOMENTARY_BOOLEAN = 6;
		private static final int DT_CLICKED_BOOLEAN = 7;
		private static final int DT_SLIDER = 8;
		
		private JComponent commandObject = null;
		private boolean buttonState = false;
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
//				System.out.println(e.toString());
				buttonState = ((AbstractButton)e.getSource()).isSelected();
//				System.out.println("buttonState=" + buttonState);
				sendDataFromField(e.getSource());
			}
		}
		
		private class ButtonMouseUpDownListener implements MouseListener {
			public void mouseClicked(MouseEvent e) {
				// ignore
			}

			public void mousePressed(MouseEvent e) {
				if (!((AbstractButton)e.getSource()).isEnabled()) return;
				buttonState = true;
				sendDataFromField(e.getSource());
			}

			public void mouseReleased(MouseEvent e) {
				if (!((AbstractButton)e.getSource()).isEnabled()) return;
				buttonState = false;
				sendDataFromField(e.getSource());
			}

			public void mouseEntered(MouseEvent e) {
				// ignore
			}

			public void mouseExited(MouseEvent e) {
				// ignore
			}
		}
		
		private class GenericChangeListener implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				if (skipPropertyEvent) return;
				if (e.getSource() instanceof JSlider) {
					JSlider s = (JSlider) e.getSource();
					if (!s.getValueIsAdjusting()) {
						sendDataFromField(e.getSource());
					}
				}
			}
		}
		
		NumberFieldListener nfl = null;
		ButtonFieldListener bfl = null;
		ButtonMouseUpDownListener bmudl = null;
		GenericChangeListener gcl = null;
		
		/** Create and register a JFormattedTextField
		 * @param fieldObject
		 * @param GETcommandID
		 * @param SETcommandID
		 * @param datatype
		 */
		CommandManager(JFormattedTextField fieldObject, int GETcommandID, int SETcommandID, int datatype) {
			if (datatype<DT_DECIMAL || datatype>DT_CHAR) {
				throw new IllegalArgumentException("CommandManager: Datatype must be DT_DECIMAL,DT_INTEGER, or DT_CHAR");
			}
			this.GETcommandID = GETcommandID;
			this.SETcommandID = SETcommandID;
			this.commandObject = fieldObject;
			this.datatype = datatype;
			if (nfl==null) nfl = new NumberFieldListener();
			this.commandObject.addPropertyChangeListener("value", nfl);
		}
		
		/**
		 * Create and register an AbstractButton. 
		 * @param buttonObject
		 * @param GETcommandID
		 * @param SETcommandID
		 * @param datatype DT_CLICKED_BOOLEAN, DT_SELECTED_BOOLEAN, DT_MOMENTARY_BOOLEAN
		 */
		CommandManager(AbstractButton buttonObject, int GETcommandID, int SETcommandID, int datatype) {
			this.GETcommandID = GETcommandID;
			this.SETcommandID = SETcommandID;
			this.commandObject = buttonObject;
			this.datatype = datatype;
			
			switch (datatype) {
				case CommandManager.DT_CLICKED_BOOLEAN:
					// common with DT_SELECTED_BOOLEAN
				case CommandManager.DT_SELECTED_BOOLEAN:
					if (bfl==null) bfl = new ButtonFieldListener();
					((AbstractButton)this.commandObject).addActionListener(bfl);
					break;
				case CommandManager.DT_MOMENTARY_BOOLEAN:
					if (bmudl==null) bmudl = new ButtonMouseUpDownListener();
					this.commandObject.addMouseListener(bmudl);
					break;
				default:
					throw new IllegalArgumentException("CommandManager: Invalid Datatype value: " + datatype);
			}
		}
		
		/**
		 * Register <code>SETcommandID</code> for callback via <code>customSETMessage()</code>
		 * 
		 * @param SETcommandID The SET command that will trigger <code>customSETMessage()</code>
		 * @param GETcommandID The GET command that will be sent to request the SET command on the NXT
		 */
		CommandManager(int SETcommandID, int GETcommandID) {
			this.GETcommandID = GETcommandID;  
			this.SETcommandID = SETcommandID;
			this.datatype = DT_CUSTOM_MESSAGE;
		}
		
		/**
		 * Register <code>JSlider</code> for callback via <code>customSETMessage()</code>
		 * 
		 * @param SETcommandID The SET command that will trigger <code>customSETMessage()</code>
		 * @param GETcommandID The GET command that will be sent to request the SET command on the NXT
		 */
		CommandManager(JSlider jsldr, int SETcommandID, int GETcommandID) {
			this.GETcommandID = GETcommandID;  
			this.SETcommandID = SETcommandID;
			this.commandObject = jsldr;
			this.datatype = DT_SLIDER;
			if (gcl==null) gcl = new GenericChangeListener();
			((JSlider)this.commandObject).addChangeListener(gcl);
		}
		
		/** Called only by listeners to send data from sourceObject on event
		 * 
		 * @param objRef the object ref
		 */
		void sendDataFromField(Object objRef){
			for (CommandManager item: arraylistFieldManager){
				if (item.commandObject==objRef) {
//					System.out.println("sendDataFromField: item.getSETCommandID()" + item.getSETCommandID());
					byte[] value = item.encodeItem(); // can return null to indicate "do not send anything"
//					System.out.println("value[0]=" + value[0] + ", cmd=" + item.getSETCommandID());
					if (value!=null) {
						sendMessage(item.getSETCommandID(), value); 
					}
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

		/**
		 * set the field on SET request from NXT.
		 * 
		 * @param value
		 * @param off
		 */
		void decodeAndSetItem(byte[] value, int off) {
			skipPropertyEvent=true;
			Integer ival;
			
			switch (this.datatype) {
			case DT_DECIMAL:
				Float fval = new Float(Float.intBitsToFloat(EndianTools.decodeIntBE(value, off)));
//				System.out.println("cmd " + SETcommandID + ": float val: " + 
				((JFormattedTextField)commandObject).setValue(fval); 
				break;
			case DT_INTEGER:
				ival = new Integer(EndianTools.decodeIntBE(value, off));
				((JFormattedTextField)commandObject).setValue(ival);
				break;
			case DT_CHAR:
				byte[] strVal = new byte[value.length - off];
				String fieldValue = null;
				try {
					fieldValue = new String(strVal, "US-ASCII");
				} catch (UnsupportedEncodingException e) {
					fieldValue = "decode_error";
				}
				((JFormattedTextField)commandObject).setText(fieldValue);
				break;
			// don't do any SETS with these button types
			case DT_MOMENTARY_BOOLEAN:
				// common with DT_CLICKED_BOOLEAN
			case DT_CLICKED_BOOLEAN:
				break;
			case DT_SELECTED_BOOLEAN:
				boolean bval = (value[off] != 0);
//				System.out.println("DT_SELECTED_BOOLEAN: " + bval);
				((AbstractButton)commandObject).setSelected(bval);
				break;
			case DT_CUSTOM_MESSAGE:
				// build new array with only the command value
				byte[] buf = new byte[value.length - off];
				System.arraycopy(value, off, buf, 0, value.length - off);
				customSETMessage(SETcommandID, buf);
				break;
			case DT_SLIDER:
				((JSlider)commandObject).setValue(EndianTools.decodeIntBE(value, off));
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
							Float.parseFloat(((JFormattedTextField)commandObject).getValue().toString())
							);
				} catch(NumberFormatException e) {
					System.out.println("GetValue=" + ((JFormattedTextField)commandObject).getValue());
					System.out.println("cmd=" + SETcommandID);
					
					e.printStackTrace();
					break;
				}
				//System.out.println("DT_DECIMAL: " + fieldObject.getText() + ", value=" + fieldObject.getValue());
				EndianTools.encodeIntBE(fval, value, 0);
				break;
			case DT_INTEGER:
				EndianTools.encodeIntBE(((Long)((JFormattedTextField)commandObject).getValue()).intValue(), value, 0);
				break;
			case DT_CHAR:
				byte[] strVal = null;
				try {
					strVal = ((JFormattedTextField)commandObject).getText().getBytes("US-ASCII");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				value = strVal;
				break;
			case DT_CLICKED_BOOLEAN:
				value = new byte[1];
				value[0] = 1;
				break;
			case DT_MOMENTARY_BOOLEAN:
				// common with DT_SELECTED_BOOLEAN
			case DT_SELECTED_BOOLEAN:
				value = new byte[1];
				value[0] = (byte)(buttonState?1:0);
				break;
			case DT_CUSTOM_MESSAGE:
				value = null; // no message to send to NXT
				break;
			case DT_SLIDER:
				EndianTools.encodeIntBE(((JSlider)commandObject).getValue(), value, 0);
				break;
			default:
				System.out.println("!** encodeItem: bad datatype! " + this.datatype);
				return null;
			}
			return value;
		}
		
		void setEnabled(boolean enabled){
			if (commandObject!=null) {
				if (commandObject instanceof JFormattedTextField){
					((JFormattedTextField)commandObject).setEditable(enabled);
				}
				// only disable buttons that have on/off state (toggle, chkbox, etc.) because they should have
				// a SETter from the NXT side
				if (this.datatype == DT_SELECTED_BOOLEAN) ((AbstractButton)commandObject).setEnabled(enabled);
			}
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
	 * @param datatype The datatype code for formatting. See <code>CommandManager</code> constants.
	 * @return A <code>JFormattedTextField</code> instance
	 * @see CommandManager#DT_DECIMAL
	 * @see CommandManager#DT_INTEGER
	 * @see CommandManager#DT_CHAR
	 */
	protected final JFormattedTextField getBoundTextField(int SETcommandID, int GETcommandID, int datatype) {
		if (datatype<CommandManager.DT_DECIMAL || datatype>CommandManager.DT_CHAR) {
			throw new IllegalArgumentException("Datatype must be DT_DECIMAL,DT_INTEGER, or DT_CHAR");
		}
		
		JFormattedTextField textfield;
		int alignment = SwingConstants.RIGHT;
		switch (datatype) {
		case CommandManager.DT_DECIMAL:
			textfield = new JFormattedTextField(formatterDecimal);
			break;
		case CommandManager.DT_INTEGER:
			textfield = new JFormattedTextField(formatterInteger);
			break;
		default:
//			System.out.println("default");
			textfield = new JFormattedTextField();
			alignment = SwingConstants.LEFT;
			break;
		}
		 
		textfield.setHorizontalAlignment(alignment);
		textfield.setColumns(10);
		arraylistFieldManager.add(new CommandManager(textfield, GETcommandID, SETcommandID, datatype));
		return textfield;
	}
	/**
	 * Create and register a <code>JCheckBox</code> for protocol handling. Used for IDing associated command (boolean only),
	 *  formatting and processing checkbox, etc. field
	 * values. Each <code>JCheckBox</code> represents a prototype datapoint that has 2 command IDs 
	 * (GETTER and a SETTER) and is managed by the
	 * handler type protocol. 
	 * <p>
	 * 1 byte is sent as value: 1=<code>true</code>, 0=<code>false</code>.
	 * 
	 * @param label The label for the <code>JCheckBox</code>
	 * @param SETcommandID The SET command ID as specified in the handler type protocol. 
	 * <code>false</code> values are sent as a byte with value of zero (0).
	 * @param GETcommandID The GET command ID as specified in the handler type protocol
	 * @return A <code>JCheckBox</code> instance
	 */
	protected final JCheckBox getBoundChkbox(String label, int SETcommandID, int GETcommandID) {
		JCheckBox cb = new JCheckBox(label);
		CommandManager fm = new CommandManager(cb, GETcommandID, SETcommandID, CommandManager.DT_SELECTED_BOOLEAN);
		arraylistFieldManager.add(fm);
		return cb;
	}
	
	/**
	 * Create and register a <code>JToggleButton</code> for protocol handling. Used for IDing associated command (boolean only),
	 *  formatting and processing the field
	 * values. Each <code>JToggleButton</code> represents a prototype datapoint that has 2 command IDs 
	 * (GETTER and a SETTER) and is managed by the
	 * handler type protocol. 
	 * <p>
	 * 1 byte is sent as value: 1=<code>true</code>, 0=<code>false</code> cooresponding to button state returned by
	 * <code>isSelected()</code>.
	 * 
	 * @param label The label for the <code>JToggleButton</code>
	 * @param SETcommandID The SET command ID as specified in the handler type protocol. 
	 * <code>false</code> values are sent as a byte with value of zero (0).
	 * @param GETcommandID The GET command ID as specified in the handler type protocol
	 * @return A <code>JToggleButton</code> instance
	 */
	protected final JToggleButton getBoundToggleButton(String label, int SETcommandID, int GETcommandID) {
		JToggleButton cb = new JToggleButton(label);
		cb.setSelected(false);
		CommandManager fm = new CommandManager(cb, GETcommandID, SETcommandID, CommandManager.DT_SELECTED_BOOLEAN);
		arraylistFieldManager.add(fm);
		return cb;
	}
	
	/**
	 * Create and register a <code>JButton</code> for protocol handling. Used for IDing associated command (boolean only),
	 *  formatting and processing the field
	 * values. Each <code>JButton</code> represents a prototype datapoint that has 1 command ID 
	 * (SETTER) and is managed by the
	 * handler type protocol. This means that there is not SETter function from the NXT.
	 * <p>
	 * 1 byte is sent as value: 1=<code>true</code>, 0=<code>false</code>. For momentary,  <code>true</code> 
	 * for event on <code>mousePressed</code> and <code>false</code> for event on 
	 * <code>mouseReleased</code> will be sent as value 1 and 0, respectively.
	 * 
	 * @param label The label for the <code>JButton</code>
	 * @param SETcommandID The SET command ID as specified in the handler type protocol. 
	 * <code>false</code> values are sent as a byte with value of zero (0).
	 * @param momentary <code>true</code> for event on <code>mousePressed</code> and  
	 * <code>mouseReleased</code>. <code>false</code> for single <code>true</code> value sent when clicked.
	 * @return A <code>JButton</code> instance
	 * @see #getBoundToggleButton
	 */
	protected final JButton getBoundButton(String label, int SETcommandID, boolean momentary) {
		JButton button = new JButton(label);
		int dataType;
		
		if (momentary) {
			dataType = CommandManager.DT_MOMENTARY_BOOLEAN;
		} else {
			dataType = CommandManager.DT_CLICKED_BOOLEAN;
		}
		CommandManager fm = new CommandManager(button, CMD_IGNORE, SETcommandID, dataType);
		arraylistFieldManager.add(fm);
		return button;
	}
	
	/**
	 * Create and register a <code>JSlider</code> for protocol handling. Used for IDing associated command and
	 *  processing the slider data
	 * values. Each <code>JSlider</code> represents a prototype datapoint that has 2 command IDs 
	 * (GETTER and a SETTER) and is managed by the
	 * handler type protocol. 
	 * <p>
	 * 1 <code>int</code> is sent as value.
	 * 
	 * @param SETcommandID The SET command ID as specified in the handler type protocol. 
	 * @param GETcommandID The GET command ID as specified in the handler type protocol
	 * @return A registered <code>JSlider</code> instance
	 */
	protected final JSlider getBoundSlider(int SETcommandID, int GETcommandID) {
		JSlider slider = new JSlider();
		CommandManager fm = new CommandManager(slider, SETcommandID, GETcommandID);
		arraylistFieldManager.add(fm);
		return slider;
	}
	
	/**
	 * Register a SET command to call the <code>customSETMessage()</code> method (you provide the implementation)
	 * with the command ID and data packet.
	 * 
	 * @param SETcommandID The SET command ID as specified in the handler type protocol.
	 * @param GETcommandID The GET command that will be sent to request the SET command on the NXT
	 * @see #customSETMessage
	 */
	protected final void registerCommandCallback(int SETcommandID, int GETcommandID){ 
		CommandManager fm = new CommandManager(SETcommandID, GETcommandID);
		arraylistFieldManager.add(fm);
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

		lblPluginName.setBounds(9, 3, 598, 14);
		add(lblPluginName);
	}

	

	/**
	 * Sets the name label displayed in the GUI panel.
	 * @param label The label
	 */
	protected void setPlugInLabel(String label) {
		lblPluginName.setText(label);
	}

	/**
	 * Must provide the well defined handler Type ID. See the constants for
	 * <code>LogMessageTypeHandler</code>. The type ID is used by the PC-side
	 * NXT Charting Logger to load the appropriate message handler in
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
	 * Process the message sent from NXT via <code>CommandManager</code>. The message will contain
	 * handler-specific ID, commands and data as per the handler's protocol
	 * implementation.
	 * <p>
	 * Must be able to handle
	 * <code>typeID=LogMessageTypeHandler.TYPE_ALWAYS_RECEIVE</code> in some way
	 * even if that means just ignoring it.
	 * 
	 * @param message
	 *            The data packet received by (PC-side) NXT Charting Logger from
	 *            (NXT-side) NXTDataLogger.
	 * @param typeID
	 *            the TYPE_ID that was sent from NXTDataLogger
	 */

	synchronized final void processMessage(byte[] message, int typeID) {
		// ignores broadcast messages
		if (typeID==TYPE_ALWAYS_RECEIVE) return;
		
//		System.out.println("AbstractTunneledMessagePanel.processMessage: message[0]=" + message[0] + ", getHandlerID()=" + getHandlerID() );
		
		// Skip processing if not for me
		if (getHandlerID() != (message[0] & 0xff)) return; // byte 0 => handler ID
		
		// parse the command
		int command = message[1] & 0xff;
//		System.out.println("AbstractTunneledMessagePanel.processMessage: command=" + command);
		
		// iterate through the fields and GET or SET
		for (CommandManager item : arraylistFieldManager) {
			// reply with SET command on a GET request from the NXT
			if (item.getGETCommandID()==command && item.getGETCommandID()!=CMD_IGNORE) {// TODO verify CMD_IGNORE
				byte[] value = item.encodeItem();
				if (value!=null ) { 
					sendMessage(item.getSETCommandID(), value); // TODO exclude custom Message handler if requested?
				}
			}
			
			// set the field on SET request from NXT
			if (item.getSETCommandID()==command && item.getSETCommandID()!=CMD_IGNORE) {// TODO verify CMD_IGNORE
				item.decodeAndSetItem(message, 2); 
			}
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
	 */
	protected final void sendMessage(int command, byte[] msg){
		if (msg==null) {
			msg = new byte[0];
		}
		sendMessage(command, msg, msg.length, 0);	
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
	 *            how many bytes to send of <code>msg</code> array
	 * @param off
	 *            starting at element <code>off</code> in <code>msg</code> array
	 */
	protected final void sendMessage(int command, byte[] msg, int len, int off) {
		if (msg==null) {
			System.out.println("** sendMessage: NULL MESSAGE. cmd=" + command);
			msg = new byte[0];
		}
		byte[] buf = new byte[len + 2]; // size the packet
		buf[0] = (byte) (handlerID & 0xff); // set the unique handler ID
		buf[1] = (byte) (command & 0xff); // set the handler-specific command value
		System.arraycopy(msg, off, buf, 2, len); // aggregate it all in one array
		//System.out.println("AbstractTunneledMessagePanel.sendMessage: command=" + command);
		// Will be packed into a common header for CMD_DELIVER_PACKET.
		extensionGUIManager.sendControlPacket(handlerTypeID, buf);
	}
	
	// TODO firm up javadoc
	/**
	 * Called by <code>CommandManager</code> for DT_CUSTOM_MESSAGE type for a command SET from the NXT. 
	 * Your implementation must use customMessage constructor of <code>CommandManager</code>. Implement as 
	 * empty method if not used in your subclass.
	 * 
	 * @param command The command (byte 1 value of handler message)
	 * @param message the byte array packet  with byte 0-1 stripped out. Byte 1 (command) is passed as <code>command</code>
	 * @see #registerCommandCallback
	 */
	protected abstract void customSETMessage(int command, byte[] message);
	
	protected String decodeString(byte[] array){
		String theString = null;
		try {
			theString = new String(array, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			System.out.println("**! TunneledMessageManager.decodeString:");
			e.printStackTrace();
		}
		return theString;
	}
	
	private void disableRegisteredFields(){
		for (CommandManager item : arraylistFieldManager) {
			if (item.getGETCommandID()!=CMD_IGNORE) item.setEnabled(false);
		}
		this.repaint();
	}
	
	/**
	 * Query the remote handler to send all defined values. Used in init().
	 * @see #init
	 */
	void pollForRemoteHandlerValues(){
		disableRegisteredFields();  
		for (CommandManager item : arraylistFieldManager) {
			requestValueMessage(item);
		}
	}
	
	/** 
	 * Send a GET request to NXT for CommandManager object instance
	 * @param fieldItem
	 */
	private void requestValueMessage(CommandManager fieldItem){
		if (fieldItem.getGETCommandID() == CMD_IGNORE) return;
		byte[] buf = new byte[2];
		buf[0] = (byte) (handlerID & 0xff); // set the unique handler ID
		buf[1] = (byte) (fieldItem.getGETCommandID() & 0xff); // set the handler-specific command value
		// Will be packed into a common header for CMD_DELIVER_PACKET.
		//System.out.println("AbstractTunneledMessagePanel.requestValueMessage: command=" + fieldItem.getGETCommandID());
		extensionGUIManager.sendControlPacket(handlerTypeID, buf);	
	}
	
	/**
	 * Do any required intialization. Called by <code>ExtensionGUIManager.activateHandler()</code> on
	 * plugin init command (CMD_INIT_HANDLER) from <code>lejos.util.LogMessageManager</code> . Default is to
	 * clear the plugin label and poll <code>lejos.util.LogMessageTypeHandler</code> concrete 
	 * subclass (via GET commands) on NXT for all current values.
	 * 
	 * @see #pollForRemoteHandlerValues
	 */
	protected void init(){
		setPlugInLabel("");
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
