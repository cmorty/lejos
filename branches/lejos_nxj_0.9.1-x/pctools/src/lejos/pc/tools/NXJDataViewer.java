package lejos.pc.tools;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Downloads  data from the DataLogger running on a NXT <br>
 * Uses Bluetooth  or USB<br>
 * To use BlueTooth, click "Use Bluetooth" before "Connect"<br>
 * When the status is "Connected", click "Start Download"<br>
 * If you want to resend, press any button except ESC on the NXT, then click 
 * "Start Download"<br>
 * You can run another download session, but you have to connect again. 
 * The data can be copied and pasted into a spread sheet for analysis & graphing <br>
 * status field shows messages 
 * 
 * @author Roger Glassey revised  06.15.2008
 *
 */
public class NXJDataViewer extends JFrame implements ActionListener, ChangeListener, DataViewerUI
{
	private static final long serialVersionUID = 4275975098699509511L;
	private JButton startButton = new JButton("Download");
    private JButton connectButton = new JButton("Connect");
    private JRadioButton btButton = new JRadioButton("Bluetooth");
    private JRadioButton usbButton = new JRadioButton("USB");
    private JLabel statusField = new JLabel();
    private JTextField lengthField = new JTextField(2);
    private JTextField nameField = new JTextField(10);
    private JTextField addrField = new JTextField(12);
    private int _recordCount;  //used by append()
    private int _rowLength; // used by append();
    private DataViewComms comm;
    private boolean usbSelected = true;
    private String usingUSB = "Using USB";
    private String usingBluetooth = "Using Bluetooth";
    /**
     * Screen area to hold the downloaded data
     */
    private JTextArea theLog;
    
	// Formatter
	private static final NumberFormat FORMAT_FLOAT = NumberFormat.getNumberInstance();

    /**
     * Constructor builds GUI
     */
    public NXJDataViewer()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("View output from NXJ Datalogger");
        setSize(600, 600);
        statusField.setPreferredSize(new Dimension(200,20));
        buildGUI();
        comm = new DataViewComms(this);
    }

    private void buildGUI()
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
        connectPanel.add(connectButton);
        connectButton.addActionListener(this);
        connectPanel.add(new JLabel(" Name "));
        connectPanel.add(nameField);
        connectPanel.add(new JLabel(" Addr "));
        connectPanel.add(addrField);

        JPanel downLoadPanel = new JPanel();//  holds label and text field
        downLoadPanel.add(startButton);
        downLoadPanel.add(new JLabel("Row Length:"));
        downLoadPanel.add(lengthField);
        lengthField.setText("2");
        startButton.addActionListener(this);
        downLoadPanel.add(new JLabel("  Status:"));
        downLoadPanel.add(statusField);

        JPanel topPanel = new JPanel();  // North area of the frame
        topPanel.setLayout(new GridLayout(2, 1));
        topPanel.add(connectPanel);
        topPanel.add(downLoadPanel);
        add(topPanel, BorderLayout.NORTH);
        theLog = new JTextArea(30, 40); // Center area of the frame
        getContentPane().add(new JScrollPane(theLog), BorderLayout.CENTER);
        setStatus("Waiting to Connect");
    }

    /**
     * Required by action listener
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == connectButton)
        {
            String name = nameField.getText();
            String address = addrField.getText();
            boolean useUSB = usbButton.isSelected();
            
            if (!comm.connecTo(name, address, useUSB))
            {
                setStatus("Connect Failed");
                if (useUSB)
                {
                    JOptionPane.showMessageDialog(this, "Sorry... but USB did not connect.\n" +
                            "You might want to check:\n " +
                            " Is the NXT turned on and connected? \n " +
                            " Does it display  'wait for USB'? ", "We have a connection problem.",
                            JOptionPane.PLAIN_MESSAGE);

                } else
                {
                    JOptionPane.showMessageDialog(this, "Sorry... Bluetooth did not connect. \n" +
                            "You might want to check:\n" +
                            " Is the dongle plugged in?\n" +
                            " Is the NXT turned on?\n" +
                            " Does it display  'wait for BT'? ",
                            "We have a connection problem.",
                            JOptionPane.PLAIN_MESSAGE);
                }
            }
        } else if (e.getSource() == startButton)
        {
        	theLog.setText("");
            _rowLength = Integer.parseInt(lengthField.getText());
            _recordCount = 0;
            comm.startDownload();
        }
    }

    /**
     * Set the name and address of the NXT connected to
     */
    public void connectedTo(String name, String address)
    {
        nameField.setText(name);
        addrField.setText(address);
        setStatus("Connected ");
    }

    /**
     * Append float to the data log display
     */
    public void append(float value)
    {
        if (0 == _recordCount % _rowLength)
        {
            theLog.append("\n");
        }
		theLog.append(FORMAT_FLOAT.format(value) + "\t ");
        _recordCount++;
    }

    /**
     * Initialize the display Frame 
     */
    public static void main(String[] args)
    {
    	ToolStarter.startSwingTool(NXJDataViewer.class, args);
    }
    
    public static int start(String[] args)
    {
        NXJDataViewer frame = new NXJDataViewer();
        frame.setVisible(true);
        return 0;
    }

    /**
     * Messages generated show in the status Field
     */
    public void setStatus(String s)
    {
        statusField.setText(s);
    }
    
    /**
     * Show message in dialog box
     */
    public void showMessage(String msg)
    {
    	JOptionPane.showMessageDialog(this,msg,"Message",JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Log a message to System.out
     */
    public void logMessage(String msg)
    {
    	System.out.println(msg);
    }
    
    /**
     * Called when USB/Bluetooth radio buttons are selected or changed
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
}
