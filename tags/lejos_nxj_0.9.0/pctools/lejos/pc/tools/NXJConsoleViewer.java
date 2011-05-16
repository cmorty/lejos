package lejos.pc.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

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
public class NXJConsoleViewer extends JFrame implements ActionListener, ChangeListener, ConsoleViewerUI
{
    private static final int LCD_WIDTH = 100;
    private static final int LCD_HEIGHT = 64;


    private static final long serialVersionUID = -4789857573625988062L;
    private JButton connectButton = new JButton("Connect");
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


    class LCDDisplay extends JPanel
    {
        private BufferedImage lcd = new BufferedImage(LCD_WIDTH, LCD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        private Graphics2D lcdGC = lcd.createGraphics();

        public void paint(Graphics g)
        {
            Graphics2D g2d = (Graphics2D)g;
            super.paint(g);
            int width = getWidth();
            int height = getHeight();
            int imgWidth = lcd.getWidth();
            int imgHeight = lcd.getHeight();
            // Draw a scaled version of the display, keep the aspect ratio and
            // centre it.
            if (width < (height*imgWidth)/imgHeight)
            {
                imgHeight = (width*imgHeight)/imgWidth;
                imgWidth = width;
            }
            else
            {
                imgWidth = (height*imgWidth)/imgHeight;
                imgHeight = height;
            }
            g2d.drawImage(lcd, (width-imgWidth)/2, (height-imgHeight)/2, imgWidth, imgHeight, null);

        }
        
        public void clear()
        {
            lcdGC.setColor(new Color(155, 205, 155, 255));
            lcdGC.fillRect(0, 0, lcd.getWidth(), lcd.getHeight());
        }

        public void update(byte [] buffer)
        {
            int offset = 0;
            int row = 0;
            lcdGC.setColor(new Color(155, 205, 155, 255));
            lcdGC.fillRect(0, 0, lcd.getWidth(), lcd.getHeight());
            lcdGC.setColor(new Color(0, 0, 0, 255));
            for(row = 0; row < 64; row += 8)
                for(int x = 0; x < LCD_WIDTH; x++)
                {
                    byte vals = buffer[offset++];
                    for(int y = 0; y < 8; y++)
                    {
                        if ((vals & 1) != 0)
                            lcdGC.fillRect(x, y+row, 1, 1);
                        vals >>= 1;
                    }
                }
            this.repaint();
        }

    }

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

    /**
     * Required by action listener. Used by Connect button
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == connectButton)
        {
        	statusField.setText("Connecting");
            theLog.setText("");
            lcd.clear();
            String name = nameField.getText();
            String address = addrField.getText();
            boolean _useUSB = usbButton.isSelected();
            if (comm.connectTo(name, address, _useUSB, doLcd.isSelected()))
            {
//            	usbButton.setEnabled(false);
//            	btButton.setEnabled(false);
//            	connectButton.setEnabled(false);
//            	doLcd.setEnabled(false);
            }
            else
            {
            	statusField.setText("Connection Failed");
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
        statusField.setText("Connected to " + name);
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


