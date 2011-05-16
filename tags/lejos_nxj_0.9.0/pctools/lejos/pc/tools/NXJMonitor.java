package lejos.pc.tools;

import java.awt.*;
import java.awt.event.*;
import lejos.nxt.remote.*;
import javax.swing.*;
import javax.swing.border.*;
import lejos.pc.comm.*;

import java.io.*;

/**
 * Monitors sensors and motors of NXT using LCP.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NXJMonitor extends JFrame implements ActionListener {
	private static final long serialVersionUID = 2107625009450493352L;
	public static final int MODE_RAW = 0x00;
	public static final int MODE_BOOLEAN = 0x20;
	public static final int MODE_TRANSITIONCNT = 0x40;
	public static final int MODE_PERIODCOUNTER = 0x60;
	public static final int MODE_PCTFULLSCALE = 0x80;
	public static final int MODE_CELSIUS = 0xA0;
	public static final int MODE_FARENHEIT = 0xC0;
	public static final int MODE_ANGLESTEP = 0xE0;
	
	public static final int TYPE_NO_SENSOR = 0x00;
	public static final int TYPE_SWITCH = 0x01;
	public static final int TYPE_TEMPERATURE = 0x02;
	public static final int TYPE_REFLECTION = 0x03;
	public static final int TYPE_ANGLE = 0x04;
	public static final int TYPE_LIGHT_ACTIVE = 0x05;
	public static final int TYPE_LIGHT_INACTIVE = 0x06;
	public static final int TYPE_SOUND_DB = 0x07; 
	public static final int TYPE_SOUND_DBA = 0x08;
	public static final int TYPE_CUSTOM = 0x09;
	public static final int TYPE_LOWSPEED = 0x0A;
	public static final int TYPE_LOWSPEED_9V = 0x0B;
	
	private static String[] sensorTypes = {
			"No Sensor",
			"Touch Sensor",
			"Remperature",
			"RCX Light",
			"RCX Rotation",
			"Light Active",
			"Light Inactive",
			"Sound DB",
			"Sound DBA",
			"Custom",
			"I2C",
			"I2C 9V"};
	
	private String title = "NXJ Monitor";
	private NXTCommand nxtCommand = new NXTCommand();
	private Timer timer;
	private SensorPanel [] sensorPanels = {
			new SensorPanel("Sensor 1"),
			new SensorPanel("Sensor 2"),
			new SensorPanel("Sensor 3"),
			new SensorPanel("Sensor 4")};
	private LabeledGauge[] motorPanels = {
			new LabeledGauge("Motor A Tacho", 270),
			new LabeledGauge("Motor B Tacho", 270),
			new LabeledGauge("Motor C Tacho", 270)};
	private LabeledGauge batteryGauge = new LabeledGauge("Battery", 10000);
	private InputValues[] sensorValues = new InputValues[4];
	private OutputState[] motorValues = new OutputState[3];
	private int mv;
	private JLabel textLabel = new JLabel("Trace messages");
	private JTextArea text = new JTextArea(10,60);
	private String[] textStrings = new String[10];
	private int numStrings = 0;
	
	public NXJMonitor() {
		setTitle(title);
		
		WindowListener listener = new WindowAdapter() {
	        public void windowClosing(WindowEvent w) {
	          try {
	          	if (nxtCommand != null) nxtCommand.close();
	          } catch (IOException ioe) {}
	          System.exit(0);
	        }
	      };
	      
	    addWindowListener(listener);      
		setSize(500,300);
	}
	
	public int run() throws NXTCommException {
	    int protocols = NXTCommFactory.USB | NXTCommFactory.BLUETOOTH;
	    final JFrame frame = this;
	    
		NXTConnector conn = new NXTConnector();
		conn.addLogListener(new ToolsLogger());
		
		final NXTInfo[] nxts = conn.search(null, null, protocols);
		
	    if (nxts.length == 0) {
	        System.err.println("No NXT found - is it switched on and plugged in (for USB)?");
	        return 1;
	    }
	    
	    // Display a list of NXT
	    
	    final NXTConnectionModel nm = new NXTConnectionModel(nxts, nxts.length);
	    
	    final JTable nxtTable = new JTable(nm);
	    
	    final JScrollPane nxtTablePane = new JScrollPane(nxtTable);
	    
	    nxtTable.setRowSelectionInterval(0, 0);
	    
	    getContentPane().add(nxtTablePane, BorderLayout.CENTER);
	    
	    JButton connectButton = new JButton("Connect");
	    
	    connectButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent ae) {
	          int row = nxtTable.getSelectedRow();
	          if (row >= 0) {
	        	  boolean open = false;
	        	  try {
	        		  NXTComm nxtComm = NXTCommFactory.createNXTComm(nxts[row].protocol);
	        		  open = nxtComm.open(nxts[row], NXTComm.LCP);
	        		  nxtCommand.setNXTComm(nxtComm);
	        	  } catch(NXTCommException n) {
	        		  open = false;
	        	  }
	        	  if (!open) {
	        		  JOptionPane.showMessageDialog(frame, "Failed to connect");
	        	  } else {
	        		  showMonitor(nxts[row].name);
	        	  }
	          }
	        }
	      });

	    JPanel buttonPanel = new JPanel();	    
	    buttonPanel.add(connectButton);
	    getContentPane().add(new JScrollPane(buttonPanel), BorderLayout.SOUTH);

	    pack();
	    setVisible(true);
	    return 0;
	}
	
	private void showMonitor(String name) {
		Container contentPane = getContentPane();
		JPanel contentPanel = new JPanel();

	    setTitle(title + " : " + name);

		contentPane.removeAll();
			
		contentPane.add(contentPanel);
		
		JPanel sensorsPanel = new JPanel();
		JPanel motorsPanel = new JPanel();
		JPanel textPanel = new JPanel();
		
		sensorsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		sensorsPanel.setBackground(Color.DARK_GRAY);
		motorsPanel.setBackground(Color.DARK_GRAY);

		for(int i=0;i<4;i++) {
			sensorsPanel.add(sensorPanels[i]);
		}

		motorsPanel.add(batteryGauge);
		motorsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		for(int i=0;i<3;i++) {
			motorsPanel.add(motorPanels[i]);
		}
		
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
		textPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		textPanel.add(textLabel);
		textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		textPanel.add(text);
		
		contentPanel.setLayout(new BorderLayout());	
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		contentPanel.add(sensorsPanel, BorderLayout.NORTH);
		contentPanel.add(textPanel, BorderLayout.CENTER);
		contentPanel.add(motorsPanel, BorderLayout.SOUTH);
		
		pack();
		setVisible(true);
	    
		timer = new Timer(1000, this);
		timer.setInitialDelay(2000);
		timer.start();
	}
	
	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}
	
	public void actionPerformed(ActionEvent e) {
    	getValues();
    	for(int i=0;i<4;i++) {
    		int max = 1024;
    		sensorPanels[i].setRawVal(sensorValues[i].rawADValue);
    		if (sensorValues[i].sensorMode == (byte) MODE_PCTFULLSCALE) {
    			max = 100;
    		}
    		//System.out.println("Sensor mode " + i + " = " + sensorValues[i].sensorMode);
    		sensorPanels[i].setScaledMaxVal(max);
    		sensorPanels[i].setScaledVal(sensorValues[i].scaledValue);
    		sensorPanels[i].setType(sensorTypes[sensorValues[i].sensorType]);
    		sensorPanels[i].repaint();
    	}
    	for(int i=0;i<3;i++) {
    		motorPanels[i].setVal(motorValues[i].tachoCount);
    		motorPanels[i].repaint();
    	}
		batteryGauge.setVal(mv);
		text.setText("");
		for(int i=0;i<numStrings;i++) {
			text.append(textStrings[i] + "\n");
		}
		text.repaint();
    	repaint();
	}

	public void getValues() {
		try {
			for(int i=0;i<4;i++) {
				//System.out.println("Getting values for port " + i);
				sensorValues[i] = nxtCommand.getInputValues(i);
				//System.out.println("Got values for port " + i);
			}
			for(int i=0;i<3;i++) {
				//System.out.println("Getting values for motor " + i);
				motorValues[i] = nxtCommand.getOutputState(i);
				//System.out.println("Got values for motor " + i);
			}
			//System.out.println("Getting Battery value");
			mv = nxtCommand.getBatteryLevel();
			//System.out.println("Got Battery value");
			
			// Read trace messages from the NXT in mailbox 0
			while(true) {				
				byte[] msg = nxtCommand.messageRead((byte)0, (byte) 0, true);
				if (msg.length == 0) break;
				String msgString = new String(msg);
				if (numStrings == textStrings.length) {
					for(int i=0;i<textStrings.length-1;i++) {
						textStrings[i] = textStrings[i+1];						
					}
					numStrings = textStrings.length-1;
				}
				textStrings[numStrings++] = msgString;
			}			

		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}
	
	public static void main(String[] args)
	{
		ToolStarter.startSwingTool(NXJMonitor.class, args);
	}

	public static int start(String[] args) throws Exception
	{
		return new NXJMonitor().run();
	}
}




