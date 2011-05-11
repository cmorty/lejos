package lejos.pc.tools;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Downloads  data from the RConsole running on a NXT <br>
 * Uses USB by default, or Bluetooth  if selected from buttons.
 * If using Bluetooth, you can get a quicker connection entering the name or address 
 * of you NXT.<br>
 * Do NOT click "connect" unless the NXT displays the correct "Console" message.
 * status field shows messages
 * 
 * @author Roger Glassey 6.1.2008
 *
 */
public class ConsoleViewer extends JFrame implements ConsoleViewerUI, ActionListener, ChangeListener
{
    private static final long serialVersionUID = -4789857573625988062L;
    private JButton connectButton = new JButton("Connect");
    private JRadioButton usbButton = new JRadioButton("USB");
    private JRadioButton btButton = new JRadioButton("BlueTooth");
    private JLabel statusField = new JLabel();
    private TextField nameField = new TextField(10);
    private TextField addrField = new TextField(12);
    private ConsoleViewComms comm;
    private boolean usbSelected = true;
    private String usingUSB = "Using USB";
    private String usingBluetooth = "Using Bluetooth";
    /**
     * Screen area to hold the downloaded data
     */
    private TextArea theLog;

    /**
     * Constructor builds GUI
     */
    public ConsoleViewer()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("View RConsole output from NXT");
        statusField.setPreferredSize(new Dimension(200,20));
        setSize(500, 600);
        buildGui();
        comm = new ConsoleViewComms(this, true);
    }

    public void buildGui()
    {
        JPanel connectPanel = new JPanel();  //holds  button and text field
        ButtonGroup choiceGroup = new ButtonGroup();
        choiceGroup.add(usbButton);
        usbButton.setSelected(true);
        usbButton.addChangeListener(this);
        btButton.addChangeListener(this);
        choiceGroup.add(btButton);
        connectPanel.add(usbButton);
        connectPanel.add(btButton);
        connectPanel.add(new JLabel(" Name"));
        connectPanel.add(nameField);
        connectButton.addActionListener(this);
        connectPanel.add(new JLabel("Addr"));
        connectPanel.add(addrField);

        JPanel statusPanel = new JPanel();//  holds label and text field
        statusPanel.add(connectButton);
        statusPanel.add(new JLabel("Status:"));
        statusPanel.add(statusField);

        JPanel topPanel = new JPanel();  // North area of the frame
        topPanel.setLayout(new GridLayout(2, 1));
        topPanel.add(connectPanel);
        topPanel.add(statusPanel);
        add(topPanel, BorderLayout.NORTH);

        theLog = new TextArea(40, 40); // Center area of the frame
        add(theLog, BorderLayout.CENTER);
        statusField.setText(usingUSB);
    }

    /**
     * Required by action listener. Used by Connect button
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == connectButton)
        {
            setStatus("Connecting");
            theLog.setText("");
            String name = nameField.getText();
            String address = addrField.getText();
            boolean _useUSB = usbButton.isSelected();
            if (!comm.connectTo(name, address, _useUSB))
            {
                setStatus("Connection Failed");
                if (_useUSB)
                {
                    JOptionPane.showMessageDialog(this, "Sorry... USB did not connect.\n" +
                            "You might want to check:\n " +
                            " Is the NXT turned on and connected? \n " +
                            " Does it display  'USB Console...'? ", "We have a connection problem.",
                            JOptionPane.PLAIN_MESSAGE);
                } else
                {
                    JOptionPane.showMessageDialog(this, "Sorry... Bluetooth did not connect. \n" +
                            "You might want to check:\n" +
                            " Is the dongle plugged in?\n" +
                            " Is the NXT turned on?\n" +
                            " Does it display  'BT Console....'? ",
                            "We have a connection problem.",
                            JOptionPane.PLAIN_MESSAGE);
                }
            }
        }
    }

    /**
     * Append data to the log display
     */
    public void append(String data)
    {
        theLog.append(data);
    }

    public void connectedTo(String name, String address)
    {
        nameField.setText(name);
        addrField.setText(address);
        setStatus("Connected to " + name);
    }

    /**
     * Initialize the display Frame <br>
     */
    public static void main(String[] args)
    {
        ConsoleViewer frame = new ConsoleViewer();
        frame.setVisible(true);
    }

    /**
     * Messages generated by ConsoleViewComms show in the status Field
     */
    public void setStatus(String s)
    {
        statusField.setText(s);
    }

    /**
     * Update the status field when USB or Bluetooth radio buttons selected
     */
	public void stateChanged(ChangeEvent e) {
		if (usbSelected && usbButton.isSelected()) return;
		if (usbButton.isSelected())
		{
			setStatus(usingUSB);
			usbSelected = true;
		}
		else 
		{
			setStatus(usingBluetooth);
			usbSelected = false;
		}
	}

	/**
	 * Log a progress message
	 */
	public void logMessage(String msg) {
		System.out.println(msg);
	}
}

