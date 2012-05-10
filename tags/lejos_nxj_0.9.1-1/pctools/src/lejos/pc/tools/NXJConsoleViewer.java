package lejos.pc.tools;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 * Downloads data from the RConsole running on a NXT.<br>
 * Uses USB by default, or Bluetooth if selected from buttons.
 * If using Bluetooth, you can get a quicker connection entering the name or address
 * of your NXT.<br>
 * Do NOT click "connect" unless the NXT displays the correct "Console" message.
 * Status field shows messages.
 *
 * @author Roger Glassey 6.1.2008
 *
 */
public class NXJConsoleViewer extends JFrame implements ActionListener, ChangeListener, ConsoleViewerUI
{
    private static final int LCD_WIDTH = 100;
    private static final int LCD_HEIGHT = 64;
    private static final String S_CONNECT = "Connect";    
    private static final long serialVersionUID = -4789857573625988062L;
    
    private JButton connectButton = new JButton(S_CONNECT);
    private JRadioButton usbButton = new JRadioButton("USB");
    private JRadioButton btButton = new JRadioButton("BlueTooth");
    private JCheckBox doLcd = new JCheckBox("show remote LCD screen", true);

    private JLabel statusField = new JLabel();

    private JTextField nameField = new JTextField(10);
    private JTextField addrField = new JTextField(12);
    private ConsoleViewComms comm;
    private static final String USING_USB = "Using USB";
    private static final String USING_BT = "Using Bluetooth";
    /**
     * Screen area to hold the downloaded data
     */
    private JTextArea theLog;
    private LCDDisplay lcd;

    /**
     * Constructor builds GUI
     * @param debugFile File containing debug information.
     * @throws IOException 
     */
    public NXJConsoleViewer(String debugFile) throws IOException
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("View RConsole output from NXT");

        setSize(700, 600);

        statusField.setPreferredSize(new Dimension(200,20));

        buildGui();
        ConsoleViewerUI ui = new ConsoleViewerSwingUI(this);
        ConsoleDebugDisplay debug = new ConsoleDebugDisplay(ui, debugFile);
        comm = new ConsoleViewComms(ui, debug, true);
    }

	public void buildGui()
    {
        JPanel connectPanel1 = new JPanel();  //holds text fields
        JPanel connectPanel2 = new JPanel();  //holds buttons
        ButtonGroup choiceGroup = new ButtonGroup();
        choiceGroup.add(usbButton);
        usbButton.setSelected(true);
        usbButton.addChangeListener(this);
        btButton.addChangeListener(this);
        choiceGroup.add(btButton);
        connectPanel2.add(usbButton);
        connectPanel2.add(btButton);
        connectPanel2.add(doLcd);
        connectPanel1.add(new JLabel(" Name"));
        connectPanel1.add(nameField);
        connectButton.addActionListener(this);
        connectPanel1.add(new JLabel("Addr"));
        connectPanel1.add(addrField);

        JPanel statusPanel = new JPanel(new BorderLayout());//  holds label and text field
        statusPanel.add(new JLabel("Status: "), BorderLayout.LINE_START);
        statusPanel.add(statusField, BorderLayout.CENTER);

        JPanel topLeftPanel = new JPanel();  // North area of the frame
        topLeftPanel.setLayout(new GridLayout(3, 1));
        topLeftPanel.add(connectPanel1);
        topLeftPanel.add(connectButton);
        topLeftPanel.add(connectPanel2);
        lcd = new LCDDisplay();
        lcd.clear();
        //screen.add(new JLabel("Screen"));
        lcd.setMinimumSize(new Dimension(LCD_WIDTH*2, LCD_HEIGHT*2));
        lcd.setEnabled(true);
        lcd.setPreferredSize(lcd.getMinimumSize());
        FlowLayout topPanelLayout = new FlowLayout();
        topPanelLayout.setHgap(20);
        JPanel topPanel = new JPanel(topPanelLayout);
        topPanel.add(topLeftPanel);
        topPanel.add(lcd);
        add(topPanel, BorderLayout.NORTH);
        theLog = new JTextArea(); // Center area of the frame
        add(new JScrollPane(theLog), BorderLayout.CENTER);

        add(statusPanel, BorderLayout.SOUTH);

        statusField.setText(USING_USB);

    }
	
	// set the various component's cursor
	private void setTheCursor(int cursor){
		Cursor c1 = Cursor.getPredefinedCursor(cursor);
		this.setCursor(c1);
		if (cursor== Cursor.DEFAULT_CURSOR) c1=Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
		theLog.setCursor(c1);
		nameField.setCursor(c1);
		addrField.setCursor(c1);
	}
	
	private void connectButtonState(final String label, final boolean enabled){
		connectButton.setText(label);
		connectButton.setEnabled(enabled);
	}
	
    /**
     * Required by action listener. Used by Connect button
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == connectButton)
        {
            final String name = nameField.getText();
            final String address = addrField.getText();
            final boolean _useUSB = usbButton.isSelected();
            final boolean updateLCD = doLcd.isSelected();
            
            // the thread is so that the GUI will update the button label, etc. while the
            // connection is being established.
            Runnable connectWorker = new Runnable() {
            	public void run() {
                    // try to establish a connection
            	    final boolean result = comm.connectTo(name, address, _useUSB, updateLCD);
            	    
            	    Runnable guiWorker = new Runnable() {
            	        public void run() {
        					if (result) {
        						connectButtonState("Dis" + S_CONNECT.toLowerCase(), true);
        		            	theLog.setText("");
        		                lcd.clear();
                                setTheCursor(Cursor.DEFAULT_CURSOR);
        					} else {
        						statusField.setText(S_CONNECT + "ion Failed!");
        						connectButtonState(statusField.getText(), false);
        		                if (_useUSB)
        		                {
        		                    JOptionPane.showMessageDialog(NXJConsoleViewer.this, "Sorry... USB did not connect.\n" +
        		                            "You might want to check:\n " +
        		                            " Is the NXT turned on and connected? \n " +
        		                            " Does it display  'USB Console...'? ", "We have a connection problem.",
        		                            JOptionPane.PLAIN_MESSAGE);
        		                } else
        		                {
        		                    JOptionPane.showMessageDialog(NXJConsoleViewer.this, "Sorry... Bluetooth did not connect. \n" +
        		                            "You might want to check:\n" +
        		                            " Is the dongle plugged in?\n" +
        		                            " Is the NXT turned on?\n" +
        		                            " Does it display  'BT Console....'? ",
        		                            "We have a connection problem.",
        		                            JOptionPane.PLAIN_MESSAGE);
        		                }
        		                // reset state
        		            	connectButtonState(S_CONNECT, true);
        		            	setTheCursor(Cursor.DEFAULT_CURSOR);
        					}
            	        }
            	    };
            	    SwingUtilities.invokeLater(guiWorker);
            	}
            };
            
            if (connectButton.getText().equals(S_CONNECT)) {
                setTheCursor(Cursor.WAIT_CURSOR);                
            	// try to make a connection
            	statusField.setText(S_CONNECT + "ing...");
            	connectButtonState(statusField.getText(), false);
            	new Thread(connectWorker).start();
        	} else {
        		// assume the button is "Disconnect" so lets do so
        		setTheCursor(Cursor.WAIT_CURSOR);
        		comm.close();
        		// reset state
            	connectButtonState(S_CONNECT, true);
            	setTheCursor(Cursor.DEFAULT_CURSOR);
        	}
        }
    }

    /**
     * Initialize the display Frame <br>
     */
    public static void main(String[] args)
    {
    	ToolStarter.startSwingTool(NXJConsoleViewer.class, args);
    }
    
    public static int start(String[] args) throws IOException
    {
        ConsoleViewerCommandLineParser fParser = new ConsoleViewerCommandLineParser(NXJConsoleViewer.class, "[options]");
    	CommandLine commandLine;
		try
		{
            commandLine = fParser.parse(args);
		}
		catch (ParseException e)
		{
			fParser.printHelp(System.err, e);
			return 1;
		}
		
		if (commandLine.hasOption("h"))
		{
			fParser.printHelp(System.out);
			return 0;
		}
		
        String debugFile = AbstractCommandLineParser.getLastOptVal(commandLine, "di");
        NXJConsoleViewer frame = new NXJConsoleViewer(debugFile);
        frame.setVisible(true);        
        return 0;
    }

    /**
     * Update the status field when USB or Bluetooth radio buttons selected
     */
	public void stateChanged(ChangeEvent e)
	{
		if (usbButton.isSelected())
		{
			statusField.setText(USING_USB);
		}
		else 
		{
			statusField.setText(USING_BT);
		}
	}

    /**
     * Messages generated by ConsoleViewComms show in the status Field
     */
    public void setStatus(final String s)
    {
        statusField.setText(s);
    }
    
    public void connectedTo(final String name, final String address)
    {
        nameField.setText(name);
        addrField.setText(address);
        statusField.setText(S_CONNECT + "ed to " + name);
    }

    public void append(String s)
	{
        theLog.append(s);
        theLog.setCaretPosition(theLog.getDocument().getLength());
	}

    public void updateLCD(final byte[] buffer)
    {
        lcd.update(buffer);
    }

	/**
	 * Log a progress message
	 */
	public void logMessage(String msg) {
		System.out.println(msg);
	}
}


