package lejos.pc.charting;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Simple robot driver panel implementation.
 * 
 * @author Kirk P. Thompson
 * 
 */
public class PanelRobotDrive extends AbstractTunneledMessagePanel {
	private JButton btnCommand1;
	private JButton btnCommand2;
	private JButton btnCommand3;
	private JButton btnCommand4;
	private JToggleButton[] directionButtons = new JToggleButton[4];
	private JLabel lblValue1;
	private JLabel lblValue2;
	private JLabel lblValue3;
	private JLabel lblValue4;
	
	private class ToggleButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (int i=0; i<directionButtons.length;i++){
				if (!directionButtons[i].equals(e.getSource())) directionButtons[i].setSelected(false);
			}
		}
		
	}
	private ToggleButtonListener tbl = new ToggleButtonListener();
	
	public PanelRobotDrive(int handlerID, ExtensionGUIManager extensionGUIManager) {
		super(handlerID, extensionGUIManager);
		
		JSlider sliderPower = getBoundSlider(4,5); //new JSlider();
		sliderPower.setToolTipText("Power Percentage");
		sliderPower.setValue(0);
		sliderPower.setPaintLabels(true);
		sliderPower.setMajorTickSpacing(25);
		sliderPower.setPaintTicks(true);
		sliderPower.setOrientation(SwingConstants.VERTICAL);
		sliderPower.setBounds(173, 22, 50, 134);
		add(sliderPower);
		
		JLabel lblPower = new JLabel("Power");
		lblPower.setHorizontalAlignment(SwingConstants.CENTER);
		lblPower.setBounds(170, 154, 53, 16);
		add(lblPower);
		
		//JButton btnForward = new JButton("");
//		JButton btnForward  = super.getBoundButton(null, 0, true);
		directionButtons[0] = super.getBoundToggleButton(null, 0, AbstractTunneledMessagePanel.CMD_IGNORE);
		directionButtons[0].setToolTipText("Forward");
		directionButtons[0].setBounds(56, 34, 40, 40);
		add(directionButtons[0]);
		directionButtons[0].setIcon(getIcon("/lejos/pc/charting/arrow_N.png"));
		directionButtons[0].setSelectedIcon(getIcon("/lejos/pc/charting/green_arrow_N.png"));
		directionButtons[0].addActionListener(tbl);
		directionButtons[0].getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "forward");
		directionButtons[0].getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "forward");
		directionButtons[0].getActionMap().put("forward", 
			new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							directionButtons[0].doClick();
						}
					});
				}
			}
		);
		
//		JButton btnRight  = super.getBoundButton(null, 3, true);
		directionButtons[1] = super.getBoundToggleButton(null, 3, AbstractTunneledMessagePanel.CMD_IGNORE);
		directionButtons[1].setToolTipText("Turn Right");
		directionButtons[1].setBounds(96, 75, 40, 40);
		add(directionButtons[1]);
		directionButtons[1].setIcon(getIcon("/lejos/pc/charting/arrow_E.png"));
		directionButtons[1].setSelectedIcon(getIcon("/lejos/pc/charting/green_arrow_E.png"));
		directionButtons[1].addActionListener(tbl);
		directionButtons[1].getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "right");
		directionButtons[1].getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
		directionButtons[1].getActionMap().put("right", 
			new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							directionButtons[1].doClick();
						}
					});
				}
			}
		);
		
//		JButton btnBackward  = super.getBoundButton(null, 1, true);
		directionButtons[2] = super.getBoundToggleButton(null, 1, AbstractTunneledMessagePanel.CMD_IGNORE);
		directionButtons[2].setToolTipText("Backward");
		directionButtons[2].setBounds(56, 116, 40, 40);
		add(directionButtons[2]);
		directionButtons[2].setIcon(getIcon("/lejos/pc/charting/arrow_S.png"));
		directionButtons[2].setSelectedIcon(getIcon("/lejos/pc/charting/green_arrow_S.png"));
		directionButtons[2].addActionListener(tbl);
		directionButtons[2].getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "back");
		directionButtons[2].getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "back");
		directionButtons[2].getActionMap().put("back", 
			new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							directionButtons[2].doClick();
						}
					});
				}
			}
		);
		
//		JButton btnLeft  = super.getBoundButton(null, 2, true);
		directionButtons[3] = super.getBoundToggleButton(null, 2, AbstractTunneledMessagePanel.CMD_IGNORE);
		directionButtons[3].setToolTipText("Turn Left");
		directionButtons[3].setBounds(16, 75, 40, 40);
		add(directionButtons[3]);
		directionButtons[3].setIcon(getIcon("/lejos/pc/charting/arrow_W.png"));
		directionButtons[3].setSelectedIcon(getIcon("/lejos/pc/charting/green_arrow_W.png"));
		directionButtons[3].addActionListener(tbl);
		directionButtons[3].getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "left");
		directionButtons[3].getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
		directionButtons[3].getActionMap().put("left", 
			new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							directionButtons[3].doClick();
						}
					});
				}
			}
		);
		
		JPanel panel = new JPanel();
		panel.setBorder(UIManager.getBorder("TitledBorder.border"));
		panel.setBounds(241, 22, 168, 133);
		add(panel);
		
		
		
		btnCommand1 = super.getBoundButton("Command 1", 6, true);
		panel.add(btnCommand1);
		this.registerCommandCallback(10, 11);// for setting the label
		
		btnCommand2 = super.getBoundButton("Command 2", 7, true);
		panel.add(btnCommand2);
		this.registerCommandCallback(12, 13);// for setting the label
		
		btnCommand3 = super.getBoundButton("Command 3", 8, true);
		panel.add(btnCommand3);
		this.registerCommandCallback(14, 15);// for setting the label
		
		btnCommand4 = super.getBoundButton("Command 4", 9, true);
		panel.add(btnCommand4);
		super.registerCommandCallback(16, 17);// for setting the label
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(409, 22, 193, 114);
		add(panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{104, 104, 0};
		gbl_panel_1.rowHeights = new int[]{23, 23, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		lblValue1 = new JLabel("Value 1");
		lblValue1.setPreferredSize(new Dimension(32, 16));
		GridBagConstraints gbc_lblValue1 = new GridBagConstraints();
		gbc_lblValue1.anchor = GridBagConstraints.EAST;
		gbc_lblValue1.fill = GridBagConstraints.VERTICAL;
		gbc_lblValue1.insets = new Insets(0, 0, 5, 5);
		gbc_lblValue1.gridx = 0;
		gbc_lblValue1.gridy = 0;
		panel_1.add(lblValue1, gbc_lblValue1);
		this.registerCommandCallback(26, 27);// for setting the label
		
		JFormattedTextField jftfValue1 = getBoundTextField(18, 19, CommandManager.DT_DECIMAL);
		jftfValue1.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_jftfValue1 = new GridBagConstraints();
		gbc_jftfValue1.fill = GridBagConstraints.HORIZONTAL;
		gbc_jftfValue1.insets = new Insets(0, 0, 5, 0);
		gbc_jftfValue1.gridx = 1;
		gbc_jftfValue1.gridy = 0;
		panel_1.add(jftfValue1, gbc_jftfValue1);
		
		lblValue2 = new JLabel("Value 2");
		GridBagConstraints gbc_lblValue2 = new GridBagConstraints();
		gbc_lblValue2.anchor = GridBagConstraints.EAST;
		gbc_lblValue2.fill = GridBagConstraints.VERTICAL;
		gbc_lblValue2.insets = new Insets(0, 0, 5, 5);
		gbc_lblValue2.gridx = 0;
		gbc_lblValue2.gridy = 1;
		panel_1.add(lblValue2, gbc_lblValue2);
		this.registerCommandCallback(28, 29);// for setting the label
		
		JFormattedTextField jftfValue2 = getBoundTextField(20, 21, CommandManager.DT_DECIMAL);
		jftfValue2.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_jftfValue2 = new GridBagConstraints();
		gbc_jftfValue2.insets = new Insets(0, 0, 5, 0);
		gbc_jftfValue2.fill = GridBagConstraints.HORIZONTAL;
		gbc_jftfValue2.gridx = 1;
		gbc_jftfValue2.gridy = 1;
		panel_1.add(jftfValue2, gbc_jftfValue2);
		
		lblValue3 = new JLabel("Value 3");
		GridBagConstraints gbc_lblValue3 = new GridBagConstraints();
		gbc_lblValue3.anchor = GridBagConstraints.EAST;
		gbc_lblValue3.insets = new Insets(0, 0, 5, 5);
		gbc_lblValue3.gridx = 0;
		gbc_lblValue3.gridy = 2;
		panel_1.add(lblValue3, gbc_lblValue3);
		this.registerCommandCallback(30, 31);// for setting the label
		
		JFormattedTextField jftfValue3 = getBoundTextField(22, 23, CommandManager.DT_DECIMAL);
		jftfValue3.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_jftfValue3 = new GridBagConstraints();
		gbc_jftfValue3.insets = new Insets(0, 0, 5, 0);
		gbc_jftfValue3.fill = GridBagConstraints.HORIZONTAL;
		gbc_jftfValue3.gridx = 1;
		gbc_jftfValue3.gridy = 2;
		panel_1.add(jftfValue3, gbc_jftfValue3);
		
		lblValue4 = new JLabel("Value 4");
		GridBagConstraints gbc_lblValue4 = new GridBagConstraints();
		gbc_lblValue4.anchor = GridBagConstraints.EAST;
		gbc_lblValue4.insets = new Insets(0, 0, 0, 5);
		gbc_lblValue4.gridx = 0;
		gbc_lblValue4.gridy = 3;
		panel_1.add(lblValue4, gbc_lblValue4);
		this.registerCommandCallback(32, 33);// for setting the label
		
		JFormattedTextField jftfValue4 = getBoundTextField(24, 25, CommandManager.DT_DECIMAL);
		jftfValue4.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_jftfValue4 = new GridBagConstraints();
		gbc_jftfValue4.fill = GridBagConstraints.HORIZONTAL;
		gbc_jftfValue4.gridx = 1;
		gbc_jftfValue4.gridy = 3;
		panel_1.add(jftfValue4, gbc_jftfValue4);
		
	}

	/* (non-Javadoc)
	 * @see lejos.pc.charting.AbstractTunneledMessagePanel#getHandlerTypeID()
	 */
	@Override
	protected int getHandlerTypeID() {
		return TYPE_ROBOT_DRIVE;
	}

	/* (non-Javadoc)
	 * @see lejos.pc.charting.AbstractTunneledMessagePanel#customSETMessage(int, byte[])
	 */
	@Override
	protected void customSETMessage(int command, byte[] message) {
//		System.out.println("customSETMessage: command=" + command);
		String label = super.decodeString(message);
		switch (command){
			case 10: //Set Command 1 1abel
				btnCommand1.setText(label);
				break;
			case 12: //Set Command 2 1abel
				btnCommand2.setText(label);
				break;
			case 14: //Set Command 3 1abel
				btnCommand3.setText(label);
				break;
			case 16: //Set Command 4 1abel
				btnCommand4.setText(label);
				break;
			case 26: //Set Value 1 1abel
				lblValue1.setText(label);
				break;
			case 28: //Set Value 2 1abel
				lblValue2.setText(label);
				break;
			case 30: //Set Value 3 1abel
				lblValue3.setText(label);
				break;
			case 32: //Set Value 4 1abel
				lblValue4.setText(label);
				break;
			default:
				return;
		}

	}
	
	private ImageIcon getIcon(String name){
		ImageIcon appIcon=null;
        try {
            appIcon = new ImageIcon(getClass().getResource(name)); //.getImage()
        } catch (Exception e){
            //appIcon = frame.getIconImage();
        }
        return appIcon;
	}
	
	@Override
	protected void init(){
		super.init();
		btnCommand1.setText("Command 1");
		btnCommand2.setText("Command 2");
		btnCommand3.setText("Command 3");
		btnCommand4.setText("Command 4");
		lblValue1.setText("Value 1");
		lblValue2.setText("Value 2");
		lblValue3.setText("Value 3");
		lblValue4.setText("Value 4");
		for (int i=0; i<directionButtons.length;i++){
			directionButtons[i].setSelected(false);
		}
	}
}
