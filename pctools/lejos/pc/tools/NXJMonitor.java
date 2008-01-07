package lejos.pc.tools;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import lejos.pc.comm.*;
import java.io.*;

/**
 * Monitors sensors and motors of NXT using LCP.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NXJMonitor extends JFrame implements ActionListener {

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
	private NXTCommand nxtCommand = null;
	private Timer timer;
	private SensorPanel [] sensorPanels = {
			new SensorPanel("S1"),
			new SensorPanel("S2"),
			new SensorPanel("S3"),
			new SensorPanel("S4")};
	private MotorPanel[] motorPanels = {
			new MotorPanel("A"),
			new MotorPanel("B"),
			new MotorPanel("C")};
	private Gauge batteryGauge = new Gauge();
	private JLabel batteryLabel = new JLabel("Battery");
	private InputValues[] sensorValues = new InputValues[4];
	private OutputState[] motorValues = new OutputState[3];
	private int mv;
	private JTextArea text = new JTextArea(10,58);
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
	
	public void run() throws NXTCommException {
	    nxtCommand = NXTCommand.getSingleton();
	    int protocols = NXTCommFactory.USB | NXTCommFactory.BLUETOOTH;
	    final NXTInfo[] nxts = nxtCommand.search(null, protocols);
	    final JFrame frame = this;
	   
	    if (nxts.length == 0) {
	      System.err.println("No NXT found - is it switched on and plugged in (for USB)?");
	      System.exit(1);
	    }
	    
	    final NXTTableModel nm = new NXTTableModel(this, nxts, nxts.length);
	    
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
	        		  open = nxtCommand.open(nxts[row]);
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
	}
	
	private void showMonitor(String name) {
	    setTitle(title + " : " + name);

		getContentPane().removeAll();
		
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();

		for(int i=0;i<4;i++) {
			p1.add(sensorPanels[i]);
		}
		
		batteryGauge.setMaxVal(10000);
		p2.add(batteryLabel);
		p2.add(batteryGauge);
		
		for(int i=0;i<3;i++) {
			p3.add(motorPanels[i]);
		}

		getContentPane().add(p1, BorderLayout.NORTH);
		getContentPane().add(p2, BorderLayout.WEST);
		getContentPane().add(text, BorderLayout.CENTER);
		getContentPane().add(p3, BorderLayout.SOUTH);
		pack();
	    
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
    		motorPanels[i].setTachoVal(motorValues[i].tachoCount);
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
	
	public static void main(String[] args) {
      	NXJMonitor frame = new NXJMonitor();
		try {
			frame.run();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}

class SensorPanel  extends Panel {
	Gauge rawGauge, scaledGauge;
	String name;
	JLabel nameLabel;
	JLabel typeLabel = new JLabel("No Sensor");
	
	public SensorPanel(String name) {
		this.name = name;
		nameLabel = new JLabel(name);
		rawGauge = new Gauge();
		scaledGauge = new Gauge();
		add(rawGauge,BorderLayout.NORTH);
		add(scaledGauge,BorderLayout.CENTER);
		JPanel p1 = new JPanel();
		p1.add(nameLabel);
		p1.add(typeLabel);
		add(p1,BorderLayout.SOUTH);
		Dimension size = new Dimension(110,250);
		setSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
	}
	
	public void setRawVal(int val) {
		rawGauge.setVal(val);
	}
	
	public void setRawMaxVal(int val) {
		rawGauge.setMaxVal(val);
	}
	
	public void setScaledVal(int val) {
		scaledGauge.setVal(val);
	}
	
	public void setScaledMaxVal(int val) {
		scaledGauge.setMaxVal(val);
	}
	
	public void setType(String type) {
		typeLabel.setText(type);
	}
}

class MotorPanel  extends Panel {
	Gauge tachoGauge;
	String name;
	JLabel nameLabel;
	
	public MotorPanel(String name) {
		this.name = name;
		nameLabel = new JLabel(name);
		tachoGauge = new Gauge();
		tachoGauge.setMaxVal(360);

		JPanel p1 = new JPanel();
		p1.add(nameLabel);
		add(tachoGauge,BorderLayout.NORTH);
		add(p1,BorderLayout.SOUTH);
		Dimension size = new Dimension(110,150);
		setSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
	}
	
	public void setTachoVal(int val) {
		tachoGauge.setVal(val);
	}
}

class Gauge extends JComponent {
	int value = 0, MAX_VALUE = 1024;
	Dimension size;
	double gaugeWidth, gaugeHeight;
	int    centerX,  centerY;
	double zeroAngle = 225.0;
	double maxAngle  = -45; 
	double range = zeroAngle - maxAngle;
	double offsetX, offsetY;
	
	public Gauge() {
		size = new Dimension(100,100);
		gaugeWidth 	= size.width  * 0.8;
		gaugeHeight = size.height * 0.8;
		offsetX = size.width  * 0.1;
		offsetY = size.width  * 0.1;
		centerX = (int) offsetX + (int)(gaugeWidth/2.0);
		centerY = (int) offsetY + (int)(gaugeHeight/2.0);

		setSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
	}
	
	public void setVal( int i ){ value = i; }
	public void setMaxVal( int i) { MAX_VALUE = i; }	
	
	public void paint(Graphics g){
		int x1 = centerX, y1 = centerY,
	    x2 = x1, y2 = y1;
		double angle = zeroAngle - 1.0 * range *( value * 1.0 / MAX_VALUE * 1.0);
		x2 += (int)( Math.cos(Math.toRadians(angle))*(gaugeWidth/2));
		y2 -= (int)( Math.sin(Math.toRadians(angle))*(gaugeHeight/2));

		g.setColor(Color.black);
		g.fillRect(0, 0, size.width, size.height);
		g.setColor(Color.white);
		g.fillOval((int) offsetX, (int) offsetY, (int)gaugeWidth, (int)gaugeHeight);
		g.setColor( Color.blue);
		g.drawArc( (int) offsetX+10, (int) offsetY+10, (int)gaugeWidth-20, (int)gaugeHeight-20, -45, 270);
		g.setColor(Color.red);
		g.drawLine(x1, y1, x2, y2 );
		g.setColor(Color.black);
		g.drawString(""+ value, centerX - 10, centerY + 30);
	}

}



