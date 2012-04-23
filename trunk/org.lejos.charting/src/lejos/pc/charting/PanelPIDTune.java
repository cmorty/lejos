package lejos.pc.charting;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import lejos.util.EndianTools;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelPIDTune extends JPanel {
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

	private JFormattedTextField textKp;
	private JFormattedTextField textKi;
	private JFormattedTextField textMVLimitLow;
	private JFormattedTextField textMVLimitHigh;
	private JFormattedTextField textIntegralLimitLow;
	private JFormattedTextField textIntegralLimitHigh;
	private JFormattedTextField textKd;
	private JFormattedTextField textDelay;
	private JFormattedTextField textDeadband;
	private JFormattedTextField textSetPoint;
	private JFormattedTextField textRampExponent;
	private JFormattedTextField textRampThreshold;
	private JLabel lblPluginName = new JLabel("Name: undefined");
	private JCheckBox chckbxFreezeIntegral;
	
	private int handlerID = 0;
	private ExtensionGUIManager extensionGUIManager;
	private ArrayList<FieldManager> arraylistFieldManager = new ArrayList<FieldManager>();
	
	private NumberFormat formatterInteger =  NumberFormat.getIntegerInstance();
	private NumberFormat formatterDecimal =  NumberFormat.getInstance();
	
//	private class validator extends InputVerifier{
//
//		@Override
//		public boolean verify(JComponent input) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//		
//	}
	/**
	 * Textfield manager. send data on lost focus, validations, etc.
	 * 
	 * @author Kirk P. Thompson
	 * 
	 */
	private class FieldManager {
		static final int DT_DECIMAL = 1;
		static final int DT_INTEGER = 2;
		static final int DT_CHAR = 3;
		private static final int DT_BOOLEAN = 4;

		JFormattedTextField fieldObject = null;
		AbstractButton buttonObject = null;
		
		private int GETcommandID, SETcommandID;
		private int datatype;
		
		FieldManager(JFormattedTextField fieldObject, int GETcommandID, int SETcommandID, int datatype) {
			if (datatype<DT_DECIMAL || datatype>DT_CHAR) {
				throw new IllegalArgumentException("Datatype must be DT_DECIMAL,DT_INTEGER, or DT_CHAR");
			}
			this.GETcommandID = GETcommandID;
			this.SETcommandID = SETcommandID;
			this.fieldObject = fieldObject;
			this.datatype = datatype;
			this.fieldObject.addPropertyChangeListener("value", new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					// TODO Auto-generated method stub
//					System.out.println(evt.getSource().getClass().getSimpleName() + ": old=" + evt.getOldValue() + ", new=" + evt.getNewValue());
					
					// PropertyChangeEvent fires when value changes so just test for resultant null
					if (evt.getNewValue()!=null && evt.getNewValue()!=null) {
						sendDataFromField(evt.getSource());
					}
				}
			});
		}
		
		FieldManager(AbstractButton buttonObject, int GETcommandID, int SETcommandID) {
			this.GETcommandID = GETcommandID;
			this.SETcommandID = SETcommandID;
			this.buttonObject = buttonObject;
			this.datatype = DT_BOOLEAN;
			this.buttonObject.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
//					System.out.println(((AbstractButton)e.getSource()).isSelected());
					sendDataFromField(e.getSource());
				}
			});
		}
		
		private void sendDataFromField(Object objRef){
			for (FieldManager item: arraylistFieldManager){
				if (item.fieldObject==objRef || item.buttonObject==objRef) {
					System.out.println("sendDataFromField: item.getSETCommandID()" + item.getSETCommandID());
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
			switch (this.datatype) {
			case DT_DECIMAL:
				//System.out.println("decodeAndSetText: int=" + EndianTools.decodeIntBE(value, off));
				
				float fval = Float.intBitsToFloat(EndianTools.decodeIntBE(value, off));
				//System.out.println("float val: " + fval);
				//previousValue = fieldObject.getText();
				fieldObject.setText("" + fval);
				break;
			case DT_INTEGER:
				int ival = EndianTools.decodeIntBE(value, off);
				//previousValue = fieldObject.getText();
				fieldObject.setText("" + ival);
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
		}

		byte[] encodeItem() {
			byte[] value = new byte[4];
			
			switch (this.datatype) {
			case DT_DECIMAL:
				int fval = Float.floatToIntBits(Float.parseFloat(fieldObject.getText()));
				//System.out.println("DT_DECIMAL: " + fieldObject.getText() + ", value=" + fieldObject.getValue());
				EndianTools.encodeIntBE(fval, value, 0);
				break;
			case DT_INTEGER:
				EndianTools.encodeIntBE(Integer.parseInt(fieldObject.getText()), value, 0);
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

	}

	/**
	 * Register a <code>JTextField</code> for protocol handling. Used for IDing associated command, formatting
	 *  and processing text field
	 * values. Each <code>JTextField</code> represents a prototype datapoint that has 2 command IDs 
	 * (GETTER and a SETTER) and is managed by the
	 * handler type protocol. 
	 * 
	 * @param textfield The JTextComponent
	 * @param SETcommandID
	 *            The SET command ID as specified in the handler type protocol
	 * @param GETcommandID
	 *            The GET command ID as specified in the handler type protocol
	 * @param datatype The datatype code. See constants.
	 */
	protected final void registerField(JFormattedTextField textfield, int SETcommandID, int GETcommandID, int datatype) {
		FieldManager fm = new FieldManager(textfield, GETcommandID, SETcommandID, datatype);
		arraylistFieldManager.add(fm);
	}
	/**
	 * Register a <code>AbstractButton</code> for protocol handling. Used for IDing associated command (bollean only),
	 *  formatting and processing checkbox, etc. field
	 * values. Each <code>AbstractButton</code> represents a prototype datapoint that has 2 command IDs 
	 * (GETTER and a SETTER) and is managed by the
	 * handler type protocol. 
	 * 
	 * @param abstractButton
	 * @param SETcommandID The SET command ID as specified in the handler type protocol
	 * @param GETcommandID The GET command ID as specified in the handler type protocol
	 */
	protected final void registerField(AbstractButton abstractButton, int SETcommandID,	int GETcommandID) {
		FieldManager fm = new FieldManager(abstractButton, GETcommandID, SETcommandID);
		arraylistFieldManager.add(fm);
	}
	
	private void initPanelPIDTune() {
		setPreferredSize(new Dimension(621, 177));
		setMinimumSize(new Dimension(621, 177));
		setLayout(null);

		JButton btnTransmit = new JButton("Transmit");
		btnTransmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pollForRemoteHandlerValues();
			}
		});
		
		btnTransmit.setBounds(522, 143, 89, 23);
		add(btnTransmit);

		JPanel panelLimits = new JPanel();
		panelLimits.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Limits",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLimits.setBounds(329, 32, 205, 82);
		add(panelLimits);
		panelLimits.setLayout(null);

		textMVLimitLow = new JFormattedTextField(formatterDecimal);
		textMVLimitLow.setHorizontalAlignment(SwingConstants.RIGHT);
		textMVLimitLow.setBounds(65, 27, 60, 20);
		textMVLimitLow.setText("0.0");
		textMVLimitLow.setToolTipText("");
		textMVLimitLow.setColumns(10);
		panelLimits.add(textMVLimitLow);
		registerField(textMVLimitLow, 14, 15, FieldManager.DT_DECIMAL);

		JLabel lblLow = new JLabel("Low");
		lblLow.setHorizontalAlignment(SwingConstants.CENTER);
		lblLow.setBounds(72, 11, 46, 14);
		panelLimits.add(lblLow);

		JLabel lblHigh = new JLabel("High");
		lblHigh.setHorizontalAlignment(SwingConstants.CENTER);
		lblHigh.setBounds(143, 11, 46, 14);
		panelLimits.add(lblHigh);

		JLabel lblIntegralLimit = new JLabel("Integral:");
		lblIntegralLimit.setToolTipText("To control Integral windup");
		lblIntegralLimit.setBounds(10, 54, 53, 14);
		panelLimits.add(lblIntegralLimit);

		textMVLimitHigh = new JFormattedTextField(formatterDecimal);
		textMVLimitHigh.setHorizontalAlignment(SwingConstants.RIGHT);
		textMVLimitHigh.setToolTipText("");
		textMVLimitHigh.setText("0.0");
		textMVLimitHigh.setColumns(10);
		textMVLimitHigh.setBounds(136, 27, 60, 20);
		panelLimits.add(textMVLimitHigh);
		registerField(textMVLimitHigh, 12, 13, FieldManager.DT_DECIMAL);
		
		textIntegralLimitLow = new JFormattedTextField(formatterDecimal);
		textIntegralLimitLow.setHorizontalAlignment(SwingConstants.RIGHT);
		textIntegralLimitLow.setToolTipText("");
		textIntegralLimitLow.setText("0.0");
		textIntegralLimitLow.setColumns(10);
		textIntegralLimitLow.setBounds(65, 51, 60, 20);
		panelLimits.add(textIntegralLimitLow);
		registerField(textIntegralLimitLow, 16, 17, FieldManager.DT_DECIMAL);
		
		JLabel lblMvLimit = new JLabel("MV:");
		lblMvLimit.setToolTipText("To control MV (output) limiting");
		lblMvLimit.setBounds(10, 30, 53, 14);
		panelLimits.add(lblMvLimit);

		textIntegralLimitHigh = new JFormattedTextField(formatterDecimal);
		textIntegralLimitHigh.setHorizontalAlignment(SwingConstants.RIGHT);
		textIntegralLimitHigh.setToolTipText("");
		textIntegralLimitHigh.setText("0.0");
		textIntegralLimitHigh.setColumns(10);
		textIntegralLimitHigh.setBounds(136, 51, 60, 20);
		panelLimits.add(textIntegralLimitHigh);
		registerField(textIntegralLimitHigh, 18, 19, FieldManager.DT_DECIMAL);
		
		JPanel panelPIDConstants = new JPanel();
		panelPIDConstants.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "PID Constants",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelPIDConstants.setBounds(11, 32, 103, 101);
		add(panelPIDConstants);
		panelPIDConstants.setLayout(null);

		textKp = new JFormattedTextField(formatterDecimal);
		textKp.setHorizontalAlignment(SwingConstants.RIGHT);
		textKp.setBounds(30, 22, 60, 20);
		panelPIDConstants.add(textKp);
		textKp.setToolTipText("Enter the Kp value");
		textKp.setColumns(10);
		registerField(textKp, 0, 1, FieldManager.DT_DECIMAL);

		JLabel lblKp = new JLabel("Kp:");
		lblKp.setHorizontalAlignment(SwingConstants.RIGHT);
		lblKp.setToolTipText("<html>The Proportional gain constant. Larger values typically<br>mean faster response since the larger the error, the<br>larger the proportional term compensation.</html>");
		lblKp.setBounds(10, 25, 19, 14);
		panelPIDConstants.add(lblKp);

		textKi = new JFormattedTextField(formatterDecimal);
		textKi.setHorizontalAlignment(SwingConstants.RIGHT);
		textKi.setBounds(30, 47, 60, 20);
		panelPIDConstants.add(textKi);
		textKi.setColumns(10);
		registerField(textKi, 2, 3, FieldManager.DT_DECIMAL);
		
		JLabel lblKi = new JLabel("Ki:");
		lblKi.setHorizontalAlignment(SwingConstants.RIGHT);
		lblKi.setBounds(10, 50, 19, 14);
		panelPIDConstants.add(lblKi);

		textKd = new JFormattedTextField(formatterDecimal);
		textKd.setHorizontalAlignment(SwingConstants.RIGHT);
		textKd.setBounds(30, 73, 60, 20);
		panelPIDConstants.add(textKd);
		textKd.setColumns(10);
		registerField(textKd, 4, 5, FieldManager.DT_DECIMAL);
		
		JLabel lblKd = new JLabel("Kd:");
		lblKd.setHorizontalAlignment(SwingConstants.RIGHT);
		lblKd.setBounds(10, 76, 19, 14);
		panelPIDConstants.add(lblKd);

		chckbxFreezeIntegral = new JCheckBox("Freeze Integral");
		chckbxFreezeIntegral.setBounds(11, 140, 123, 23);
		add(chckbxFreezeIntegral);
		registerField(chckbxFreezeIntegral, 24, 25);
		
		JLabel lblLoopDelay = new JLabel("Loop Delay (ms):");
		lblLoopDelay.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLoopDelay.setToolTipText("To control Integral windup");
		lblLoopDelay.setBounds(135, 62, 103, 14);
		add(lblLoopDelay);

		textDelay = new JFormattedTextField(formatterInteger);
		textDelay.setToolTipText("");
		textDelay.setText("0");
		textDelay.setHorizontalAlignment(SwingConstants.RIGHT);
		textDelay.setColumns(10);
		textDelay.setBounds(242, 59, 60, 20);
		add(textDelay);
		registerField(textDelay, 22, 23, FieldManager.DT_INTEGER);

		JLabel lblDeadband = new JLabel("Deadband:");
		lblDeadband.setToolTipText("To control Integral windup");
		lblDeadband.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDeadband.setBounds(163, 90, 75, 14);
		add(lblDeadband);

		textDeadband = new JFormattedTextField(formatterDecimal);
		textDeadband.setToolTipText("");
		textDeadband.setText("0");
		textDeadband.setHorizontalAlignment(SwingConstants.RIGHT);
		textDeadband.setColumns(10);
		textDeadband.setBounds(242, 86, 60, 20);
		add(textDeadband);
		registerField(textDeadband, 10, 11, FieldManager.DT_DECIMAL);
		
		JLabel lblSetpointsp = new JLabel("SetPoint (SP):");
		lblSetpointsp.setToolTipText("To control Integral windup");
		lblSetpointsp.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSetpointsp.setBounds(149, 36, 89, 14);
		add(lblSetpointsp);

		textSetPoint = new JFormattedTextField(formatterDecimal);
		textSetPoint.setToolTipText("");
		textSetPoint.setText("0");
		textSetPoint.setHorizontalAlignment(SwingConstants.RIGHT);
		textSetPoint.setColumns(10);
		textSetPoint.setBounds(242, 32, 60, 20);
		add(textSetPoint);
		registerField(textSetPoint, 20, 21, FieldManager.DT_DECIMAL);
		
		JLabel lblRampExponent = new JLabel("Ramp Exponent:");
		lblRampExponent.setToolTipText("To control Integral windup");
		lblRampExponent.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRampExponent.setBounds(135, 117, 103, 14);
		add(lblRampExponent);

		textRampExponent = new JFormattedTextField(formatterDecimal);
		textRampExponent.setToolTipText("");
		textRampExponent.setText("0");
		textRampExponent.setHorizontalAlignment(SwingConstants.RIGHT);
		textRampExponent.setColumns(10);
		textRampExponent.setBounds(242, 113, 60, 20);
		add(textRampExponent);
		registerField(textRampExponent, 6, 7, FieldManager.DT_DECIMAL);
		
		textRampThreshold = new JFormattedTextField(formatterDecimal);
		textRampThreshold.setToolTipText("");
		textRampThreshold.setText("0");
		textRampThreshold.setHorizontalAlignment(SwingConstants.RIGHT);
		textRampThreshold.setColumns(10);
		textRampThreshold.setBounds(242, 141, 60, 20);
		add(textRampThreshold);
		registerField(textRampThreshold, 8, 9, FieldManager.DT_DECIMAL);
		
		JLabel lblRampThreshold = new JLabel("Ramp Threshold:");
		lblRampThreshold.setToolTipText("To control Integral windup");
		lblRampThreshold.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRampThreshold.setBounds(135, 145, 103, 14);
		add(lblRampThreshold);

		lblPluginName.setBounds(13, 9, 598, 14);
		add(lblPluginName);
	}

	public PanelPIDTune(int handlerID, ExtensionGUIManager extensionGUIManager) {
		// TODO Auto-generated constructor stub
		initPanelPIDTune();
		this.extensionGUIManager = extensionGUIManager;
		
		// TODO mayeb need some sort of registration system?
		// if (extensionGUIManager==null) {
		// throw new IllegalArgumentException("no lmm");
		// }
		// this.extensionGUIManager = extensionGUIManager;
		// this.extensionGUIManager.registerMessageTypeHandler(this);
		this.handlerID= handlerID;		
	}

	void setPlugInName(String name) {
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
	protected int getHandlerTypeID() {
		return TYPE_PID_TUNER; // TODO make abstract
	}

	/**
	 * Provides the unique (per handler Type ID) handler ID. This ID is used by
	 * the PC-side <code>NXJChartingLogger</code> to route the message to the
	 * corresponding PC-side message handler and tabbed pane and to ensure that
	 * messages are routed back to the correct
	 * <code>LogMessageTypeHandler</code> concrete subclass instance.
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

	synchronized void processMessage(byte[] message, int typeID) {
		// ignores broadcast messages
		if (typeID==TYPE_ALWAYS_RECEIVE) return;
		
		// TODO Skip processing if not for me
		//if (getHandlerID() != (message[0] & 0xff)) return; // byte 0 => handler ID
		//System.out.println("PanelPIDTune.processMessage: message[0]=" + message[0] + ", getHandlerID()=" + getHandlerID() );
		
		// parse the command
		int command = message[1] & 0xff;
		System.out.println("PanelPIDTune.processMessage: command=" + command);
		
		// iterate through the fields and GET or SET
		for (FieldManager item : arraylistFieldManager) {
//			System.out.println("-------");
//			System.out.println("getGETCommandID=" + item.getGETCommandID());
//			System.out.println("getSETCommandID=" + item.getSETCommandID());
			
			// reply with SET command on a GET request from the NXT
			if (item.getGETCommandID()==command) {
				sendMessage(item.getSETCommandID(), item.encodeItem());
			}
			
			if (item.getSETCommandID()==command) {
				item.decodeAndSetItem(message, 2); // TODO use validation, formatting, etc
			}
		}
	}
	
	/**
	 * Query the remote handler to send all defined values. Used in INIT.
	 */
	void pollForRemoteHandlerValues(){
		for (FieldManager item : arraylistFieldManager) {
			requestValueMessage(item);
		}
	}
	
	private float bytesToFloat(byte[] values) {
		// include the 1 offset to skip over first command byte
		return Float.intBitsToFloat(EndianTools.decodeIntBE(values, 1));
	}

	/**
	 * Tunnel a Type handler-specific protocol message to the PC. This method
	 * builds the Handler ID, command value and message into an array that is
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
		byte[] buf = new byte[len + 2]; // size the packet
		buf[0] = (byte) (handlerID & 0xff); // set the unique handler ID
		buf[1] = (byte) (command & 0xff); // set the handler-specific command value
		System.arraycopy(msg, off, buf, 2, len); // aggregate it all in one array
		System.out.println("PanelPIDTune.sendMessage: command=" + command);
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
		extensionGUIManager.sendControlPacket(TYPE_PID_TUNER, buf);	
	}
	
	// Do any intialization, Default is to poll handler on NXT for current values.
	protected void init(){
		pollForRemoteHandlerValues();
	}
}
