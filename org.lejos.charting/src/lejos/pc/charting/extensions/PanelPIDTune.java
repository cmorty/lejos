package lejos.pc.charting.extensions;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import java.awt.Font;

/**
 * PID tuning implementation. Utilized by <code>lejos.util.PIDTuner</code>.
 * 
 * @author Kirk P. Thompson
 *
 */
public final class PanelPIDTune extends AbstractTunneledMessagePanel {
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
	private JCheckBox chckbxFreezeIntegral;

	public PanelPIDTune(int handlerID, MessageSender messageSender) {
		super(handlerID, messageSender);
	}
	
	@Override
	protected final void initGUI() {
		super.initGUI();
		Font stdFont = new Font("Dialog", Font.PLAIN, 12);
		
		JPanel panelLimits = new JPanel();
		panelLimits.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Limits",
				TitledBorder.LEADING, TitledBorder.TOP, stdFont, null));
		panelLimits.setBounds(329, 32, 205, 82);
		add(panelLimits);
		panelLimits.setLayout(null);

		textMVLimitLow = getBoundTextField(14, 15, AbstractTunneledMessagePanel.DT_DECIMAL);
		textMVLimitLow.setBounds(65, 27, 60, 20);
		panelLimits.add(textMVLimitLow);

		JLabel lblLow = new JLabel("Low");
		lblLow.setFont(stdFont);
		lblLow.setHorizontalAlignment(SwingConstants.CENTER);
		lblLow.setBounds(72, 11, 46, 14);
		panelLimits.add(lblLow);

		JLabel lblHigh = new JLabel("High");
		lblHigh.setFont(stdFont);
		lblHigh.setHorizontalAlignment(SwingConstants.CENTER);
		lblHigh.setBounds(143, 11, 46, 15);
		panelLimits.add(lblHigh);

		JLabel lblIntegralLimit = new JLabel("Integral:");
		lblIntegralLimit.setFont(stdFont);
		lblIntegralLimit.setToolTipText("To control Integral windup");
		lblIntegralLimit.setBounds(10, 54, 53, 15);
		panelLimits.add(lblIntegralLimit);

		textMVLimitHigh = getBoundTextField(12, 13, AbstractTunneledMessagePanel.DT_DECIMAL);
		textMVLimitHigh.setBounds(136, 27, 60, 20);
		panelLimits.add(textMVLimitHigh);
		
		textIntegralLimitLow = getBoundTextField(16, 17, AbstractTunneledMessagePanel.DT_DECIMAL);
		textIntegralLimitLow.setBounds(65, 51, 60, 20);
		panelLimits.add(textIntegralLimitLow);
		
		JLabel lblMvLimit = new JLabel("MV:");
		lblMvLimit.setFont(stdFont);
		lblMvLimit.setToolTipText("To control MV (output) limiting");
		lblMvLimit.setBounds(10, 30, 53, 14);
		panelLimits.add(lblMvLimit);

		textIntegralLimitHigh = getBoundTextField(18, 19, AbstractTunneledMessagePanel.DT_DECIMAL);
		textIntegralLimitHigh.setBounds(136, 51, 60, 20);
		panelLimits.add(textIntegralLimitHigh);
		
		JPanel panelPIDConstants = new JPanel();
		panelPIDConstants.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "PID Constants",
				TitledBorder.LEADING, TitledBorder.TOP, stdFont, null));
		panelPIDConstants.setBounds(11, 32, 103, 101);
		add(panelPIDConstants);

		textKp = getBoundTextField(0, 1, AbstractTunneledMessagePanel.DT_DECIMAL);
		textKp.setBounds(30, 22, 60, 20);
		panelPIDConstants.add(textKp);
		textKp.setToolTipText("Enter the Kp value");
		panelPIDConstants.setLayout(null);

		JLabel lblKp = new JLabel("Kp:");
		lblKp.setBounds(10, 25, 19, 14);
		lblKp.setFont(stdFont);
		lblKp.setHorizontalAlignment(SwingConstants.RIGHT);
		lblKp.setToolTipText("<html>The Proportional gain constant. Larger values typically<br>" +
				"mean faster response since the larger the error, the<br>larger the proportional " +
				"term compensation.</html>");
		panelPIDConstants.add(lblKp);
		
		textKi = getBoundTextField(2, 3, AbstractTunneledMessagePanel.DT_DECIMAL);
		textKi.setBounds(30, 47, 60, 20);
		panelPIDConstants.add(textKi);
		
		JLabel lblKi = new JLabel("Ki:", SwingConstants.LEADING);
		lblKi.setBounds(10, 50, 19, 14);
		lblKi.setFont(stdFont);
		lblKi.setHorizontalAlignment(SwingConstants.RIGHT);
		lblKi.setToolTipText("<html>Larger values imply steady state errors are eliminated more<br>" +
				"quickly. The trade-off is larger overshoot: any negative error integrated<br>" +
				"during transient response must be integrated away by positive error before<br>" +
				"reaching steady state.</html>");
		panelPIDConstants.add(lblKi);
		
		textKd = getBoundTextField(4, 5, AbstractTunneledMessagePanel.DT_DECIMAL);
		textKd.setBounds(30, 73, 60, 20);
		panelPIDConstants.add(textKd);
		
		JLabel lblKd = new JLabel("Kd:");
		lblKd.setBounds(10, 76, 19, 14);
		lblKd.setFont(stdFont);
		lblKd.setHorizontalAlignment(SwingConstants.RIGHT);
		panelPIDConstants.add(lblKd);

		chckbxFreezeIntegral = new JCheckBox("Freeze Integral") ; //getBoundChkbox("Freeze Integral", 24, 25);
		chckbxFreezeIntegral.setBounds(10, 141, 118, 23);
		chckbxFreezeIntegral.setFont(stdFont);
		add(chckbxFreezeIntegral);
		
		JLabel lblLoopDelay = new JLabel("Loop Delay (ms):");
		lblLoopDelay.setFont(stdFont);
		lblLoopDelay.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLoopDelay.setBounds(122, 62, 116, 16);
		add(lblLoopDelay);

		textDelay = getBoundTextField(22, 23, AbstractTunneledMessagePanel.DT_INTEGER);
		textDelay.setBounds(242, 59, 60, 20);
		add(textDelay);

		JLabel lblDeadband = new JLabel("Deadband:");
		lblDeadband.setFont(stdFont);
		lblDeadband.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDeadband.setBounds(163, 90, 75, 14);
		add(lblDeadband);

		textDeadband = getBoundTextField(10, 11, AbstractTunneledMessagePanel.DT_DECIMAL);
		textDeadband.setBounds(242, 86, 60, 20);
		add(textDeadband);
		
		JLabel lblSetpointsp = new JLabel("SetPoint (SP):");
		lblSetpointsp.setFont(stdFont);
		lblSetpointsp.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSetpointsp.setBounds(149, 36, 89, 14);
		add(lblSetpointsp);

		textSetPoint = getBoundTextField(20, 21, AbstractTunneledMessagePanel.DT_DECIMAL);
		textSetPoint.setBounds(242, 32, 60, 20);
		add(textSetPoint);
		
		JLabel lblRampExponent = new JLabel("Ramp Exponent:");
		lblRampExponent.setFont(stdFont);
		lblRampExponent.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRampExponent.setBounds(135, 117, 103, 14);
		add(lblRampExponent);

		textRampExponent = getBoundTextField(6, 7, AbstractTunneledMessagePanel.DT_DECIMAL);
		textRampExponent.setBounds(242, 113, 60, 20);
		add(textRampExponent);
		
		textRampThreshold = getBoundTextField(8, 9, AbstractTunneledMessagePanel.DT_DECIMAL);
		textRampThreshold.setBounds(242, 141, 60, 20);
		add(textRampThreshold);
		
		JLabel lblRampThreshold = new JLabel("Ramp Threshold:");
		lblRampThreshold.setFont(stdFont);
		lblRampThreshold.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRampThreshold.setBounds(122, 145, 116, 14);
		add(lblRampThreshold);
	}


	/* (non-Javadoc)
	 * @see lejos.pc.charting.AbstractTunneledMessagePanel#getHandlerTypeID()
	 */
	@Override
	public final int getHandlerTypeID() {
		return TYPE_PID_TUNER; 
	}

	@Override
	protected void customSETMessage(int command, byte[] message) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean requestFocusOnMessage() {
		return false;
	}
}
