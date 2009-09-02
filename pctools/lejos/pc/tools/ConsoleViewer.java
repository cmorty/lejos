package lejos.pc.tools;

import java.awt.*;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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

    private static final int LCD_WIDTH = 100;
    private static final int LCD_HEIGHT = 64;


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

        public void update(byte [] buffer)
        {
            int offset = 0;
            int row = 0;
            lcdGC.setColor(new Color(255, 255,255, 255));
            lcdGC.fillRect(0, 0, lcd.getWidth(), lcd.getHeight());
            lcdGC.setColor(new Color(0, 128, 0, 100));
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
     */
    public ConsoleViewer()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("View RConsole output from NXT");

        setSize(650, 600);

        statusField.setPreferredSize(new Dimension(200,20));

        buildGui();
        comm = new ConsoleViewComms(this, true, true);
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

        JPanel statusPanel = new JPanel(new BorderLayout());//  holds label and text field
        statusPanel.add(new JLabel("Status: "), BorderLayout.LINE_START);
        statusPanel.add(statusField, BorderLayout.CENTER);

        JPanel topLeftPanel = new JPanel();  // North area of the frame
        topLeftPanel.setLayout(new GridLayout(2, 1));
        topLeftPanel.add(connectPanel);
        topLeftPanel.add(connectButton);
        lcd = new LCDDisplay();
        //screen.add(new JLabel("Screen"));
        lcd.setMinimumSize(new Dimension(LCD_WIDTH*2, LCD_HEIGHT*2));
        lcd.setEnabled(true);
        lcd.setPreferredSize(lcd.getMinimumSize());
        JPanel topPanel = new JPanel();
        //topPanel.setLayout(new GridLayout(1, 2));
        topPanel.add(topLeftPanel);
        topPanel.add(lcd);
        add(topPanel, BorderLayout.NORTH);
        theLog = new TextArea(40, 40); // Center area of the frame
        add(theLog, BorderLayout.CENTER);

        add(statusPanel, BorderLayout.SOUTH);

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
        theLog.setText(theLog.getText()+data);
        theLog.setCaretPosition(0x7fffffff);
    }

    public void updateLCD(byte[] buffer)
    {
        lcd.update(buffer);
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


