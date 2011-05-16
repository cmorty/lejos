package lejos.pc.tools;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import lejos.nxt.remote.DeviceInfo;
import lejos.nxt.remote.FileInfo;
import lejos.nxt.remote.FirmwareInfo;
import lejos.nxt.remote.InputValues;
import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.NXTProtocol;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnectionState;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;

/**
 * 
 * Graphical control center for leJOS NXJ.
 * 
 * @author Lawrie Griffiths
 */
public class NXJControl implements ListSelectionListener, NXTProtocol, DataViewerUI, ConsoleViewerUI {
	// Constants
	public static final int MAX_FILES = 30;
	
	private static final int LCP = 0;
	private static final int RCONSOLE = 1;
	private static final int DATALOG = 2;
	
	private static final Dimension frameSize = new Dimension(800, 620);
	private static final Dimension filesAreaSize = new Dimension(780, 300);
	private static final Dimension filesPanelSize = new Dimension(500, 400);
	private static final Dimension nxtButtonsPanelSize = new Dimension(220, 130);
	private static final Dimension filesButtonsPanelSize = new Dimension(700,100);
	private static final Dimension nxtTableSize = new Dimension(500, 100);	
	private static final Dimension labelSize = new Dimension(60, 20);
	private static final Dimension sliderSize = new Dimension(150, 50);
	private static final Dimension tachoSize = new Dimension(100, 20);
	private static final Dimension infoPanelSize = new Dimension(300, 110);
	private static final Dimension namePanelSize = new Dimension(300, 110);
	private static final Dimension innerInfoPanelSize = new Dimension(280, 70);
	private static final Dimension tonePanelSize = new Dimension(300, 110);
	private static final Dimension i2cPanelSize = new Dimension(480, 170);
	private static final int fileNameColumnWidth = 400;
	
	private static final String title = "NXJ Control Center";

	private static final String[] sensorTypes = { "No Sensor", "Touch Sensor",
			"Temperature", "RCX Light", "RCX Rotation", "Light Active",
			"Light Inactive", "Sound DB", "Sound DBA", "Custom", "I2C",
			"I2C 9V" };

	private static final int[] sensorTypeValues = { NO_SENSOR, SWITCH, TEMPERATURE,
			REFLECTION, ANGLE, LIGHT_ACTIVE, LIGHT_INACTIVE, SOUND_DB,
			SOUND_DBA, CUSTOM, LOWSPEED, LOWSPEED_9V };

	private static final String[] sensors = { "S1", "S2", "S3", "S4" };

	private static final String[] sensorModes = { "Raw", "Boolean", "Percentage" };

	private static final int[] sensorModeValues = { RAWMODE, BOOLEANMODE, PCTFULLSCALEMODE };
	
	private final String[] motorNames = { "A", "B", "C" };

	// GUI components
	private Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
	private Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	private JFrame frame = new JFrame(title);
	private JTable nxtTable = new JTable();
	private JScrollPane nxtTablePane;
	private JTextField nameText = new JTextField(8);
	private JTable table;
	private JScrollPane tablePane;
	private JPanel filesPanel = new JPanel();
	private JPanel consolePanel = new JPanel();
	private JPanel monitorPanel = new JPanel();
	private JPanel controlPanel = new JPanel();
	private JPanel dataPanel = new JPanel();
	private JPanel otherPanel = new JPanel();
	private JTextArea theConsoleLog = new JTextArea(22, 68);
	private JTextArea theDataLog = new JTextArea(20, 68);
	private LabeledGauge batteryGauge = new LabeledGauge("Battery", 10000);
	private JSlider[] sliders = new JSlider[3];
	private JLabel[] tachos = new JLabel[3];
	private JCheckBox[] selectors = new JCheckBox[3];
	private JCheckBox[] reversors = new JCheckBox[3];
	private JTextField[] limits = new JTextField[3];
	private JButton[] resetButtons = new JButton[3];
	private JButton connectButton = new JButton("Connect");
	private JButton dataDownloadButton = new JButton("Download");
	private TextField dataColumns = new TextField("8", 2);
	private JButton searchButton = new JButton("Search");
	private JButton monitorUpdateButton = new JButton("Update");
	private JButton forwardButton = new JButton("Forward");
	private JButton backwardButton = new JButton("Backward");
	private JButton leftButton = new JButton("Turn Left");
	private JButton rightButton = new JButton("Turn Right");
	private JButton deleteButton = new JButton("Delete Files");
	private JButton uploadButton = new JButton("Upload file");
	private JButton downloadButton = new JButton("Download file");
	private JButton runButton = new JButton("Run program");
	private JButton nameButton = new JButton("Set Name");
	private JButton formatButton = new JButton("Format");
	private JRadioButton usbButton = new JRadioButton("USB");
	private JRadioButton bluetoothButton = new JRadioButton("Bluetooth");
	private JRadioButton bothButton = new JRadioButton("Both", true);
	private JRadioButton lcpButton = new JRadioButton("LCP", true);
	private JRadioButton rconsoleButton = new JRadioButton("RConsole");
	private JRadioButton datalogButton = new JRadioButton("Data Log");
	private JFormattedTextField freq = new JFormattedTextField(new Integer(500));
	private JFormattedTextField duration = new JFormattedTextField(new Integer(1000));
	private JComboBox sensorList = new JComboBox(sensors);
	private JComboBox sensorList2 = new JComboBox(sensors);
	private JComboBox sensorModeList = new JComboBox(sensorModes);
	private JComboBox sensorTypeList = new JComboBox(sensorTypes);
	private SensorPanel[] sensorPanels = { new SensorPanel("Sensor Port 1"),
			new SensorPanel("Sensor Port 2"), new SensorPanel("Sensor Port 3"),
			new SensorPanel("Sensor Port 4") };
	private JFormattedTextField txData = new JFormattedTextField();
	private JFormattedTextField rxDataLength = new JFormattedTextField(new Integer(1));
	private JLabel rxData = new JLabel();
	private Border etchedBorder = BorderFactory.createEtchedBorder();
	private JButton soundButton = new JButton("Play Sound File");
	private JTextField newName = new JTextField(16);
	private JTabbedPane tabbedPane = new JTabbedPane();
	
	// Other instance data
	private NXTConnectionModel nm;
	private ExtendedFileModel fm;
	private NXJControl control;
	private NXTCommand nxtCommand;
	private NXTCommand[] nxtCommands;
	private NXTComm[] nxtComms;
	private NXTConnector conn = new NXTConnector();
	private NXTInfo[] nxts;
	private InputValues[] sensorValues = new InputValues[4];
	private int mv;
	private int appProtocol = LCP;
	private int rowLength = 8; // default
	private int recordCount;
	private DataViewComms dvc;
	private DataViewComms[] dvcs;
	private ConsoleViewComms cvc;
	private ConsoleViewComms[] cvcs;

	// Formatter
	private static final NumberFormat FORMAT_FLOAT = NumberFormat.getNumberInstance();

	/**
	 * Command line entry point
	 */
	public static void main(String args[])
	{
		ToolStarter.startSwingTool(NXJControl.class, args);
	}
	
	public static int start(String[] args)
	{
		return new NXJControl().run();
	}

	/**
	 * Run the program
	 */
	private int run() {
		// Close connection and exit when frame windows closed
		WindowListener listener = new WindowAdapter() {
			public void windowClosing(WindowEvent w) {
				closeAll();
				System.exit(0);
			}
		};
		frame.addWindowListener(listener);
		conn.addLogListener(new ToolsLogger());
		control = this;

		// Search Button: search for NXTs
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				search();
			}
		});

		// Connect Button: connect to selected NXT
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				connect();
			}
		});

		// Data log Connect Button: connect to the Data Logger
		dataDownloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				recordCount = 0;
				
				try {
					rowLength = Integer.parseInt(dataColumns.getText());
				} catch (NumberFormatException ex) {
					System.out.println(dataColumns.getText() + " is not a number, default reset to 8");
				}
				
				dvc.startDownload();
			}
		});

		// Monitor Update Button: get values being monitored
		monitorUpdateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				getSensorValues();
				updateSensors();
			}
		});
		
		lcpButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (appProtocol == getAppProtocol()) return;
				if (lcpButton.isSelected()) {
					createLCPTabs();
					appProtocol = LCP;
				}				
			}		
		});
		
		rconsoleButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (appProtocol == getAppProtocol()) return;
				if (rconsoleButton.isSelected()) {
					createConsoleTabs();
					appProtocol = RCONSOLE;
				}				
			}		
		});
		
		datalogButton.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (appProtocol == getAppProtocol()) return;
				if (datalogButton.isSelected()) {
					createDataLogTabs();
					appProtocol = DATALOG;
				}				
			}		
		});

		// Create the panels
		createNXTSelectionPanel();
		createConsolePanel();
		createDataPanel();
		createMonitorPanel();
		createControlPanel();
		createMiscellaneousPanel();

		// set the size of the files panel
		filesPanel.setPreferredSize(filesPanelSize);
		
		// Set up the frame
		frame.setPreferredSize(frameSize);
		
		createLCPTabs();
		
		frame.add(tabbedPane);
		frame.pack();
		frame.setVisible(true);
		return 0;
	}

	/**
	 * Get files from the NXT and display them in the files panel
	 */
	private void showFiles() {
		// Layout and populate files table
		createFilesTable();

		// Remove current content of files panel and recreate it
		filesPanel.removeAll();
		createFilesPanel();
		
		// Recreate miscellaneous panel
		otherPanel.removeAll();
		createMiscellaneousPanel();
		
		// Process buttons

		// Delete Button: delete a file from the NXT
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				deleteFiles();
			}
		});

		// Upload Button: upload a file to the NXT
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				upload();
			}
		});

		// Download Button: download a file from from the NXT
		downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				download();
			}
		});

		// Run Button: run a program on the NXT
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				runFile();
			}
		});

		// Set Name Button: set a new name for the NXT
		nameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				rename(newName.getText());
			}
		});
		
		// Sound button: Play Sound file
		soundButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playSoundFile();
			}
		});
		
		// Format button; Delete user flash memory
		formatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				format();
			}
		});

		// Pack the frame
		//frame.pack();
	}

	/**
	 * Lay out NXT Selection panel
	 */
	private void createNXTSelectionPanel() {
		JPanel nxtPanel = new JPanel();
		nxtTablePane = new JScrollPane(nxtTable);
		nxtTablePane.setPreferredSize(nxtTableSize);
		nxtPanel.add(new JScrollPane(nxtTablePane), BorderLayout.WEST);
		frame.getContentPane().add(nxtPanel, BorderLayout.NORTH);
		nxtTable.setPreferredScrollableViewportSize(nxtButtonsPanelSize);
		JLabel nameLabel = new JLabel("Name: ");
		JPanel namePanel = new JPanel();
		namePanel.add(nameLabel);
		namePanel.add(nameText);
		JPanel nxtButtonPanel = new JPanel();
		nxtButtonPanel.add(namePanel);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(searchButton);
		buttonPanel.add(connectButton);
		nxtButtonPanel.add(buttonPanel);
		nxtButtonPanel.add(usbButton);
		nxtButtonPanel.add(bluetoothButton);
		nxtButtonPanel.add(bothButton);
		ButtonGroup protocolButtonGroup = new ButtonGroup();
		protocolButtonGroup.add(usbButton);
		protocolButtonGroup.add(bluetoothButton);
		protocolButtonGroup.add(bothButton);
		nxtButtonPanel.add(lcpButton);
		nxtButtonPanel.add(rconsoleButton);
		nxtButtonPanel.add(datalogButton);
		ButtonGroup appProtocolButtonGroup = new ButtonGroup();
		appProtocolButtonGroup.add(lcpButton);
		appProtocolButtonGroup.add(rconsoleButton);
		appProtocolButtonGroup.add(datalogButton);
		nxtButtonPanel.setPreferredSize(nxtButtonsPanelSize);
		nxtPanel.add(nxtButtonPanel, BorderLayout.EAST);
	}

	/**
	 *  Lay out Console Panel
	 */
	private void createConsolePanel() {
		JLabel consoleTitleLabel = new JLabel("Output from RConsole");
		consolePanel.add(consoleTitleLabel);
		consolePanel.add(new JScrollPane(theConsoleLog));
	}

	/**
	 *  Lay out Data Console Panel
	 */
	private void createDataPanel() {
		JLabel dataTitleLabel = new JLabel("Data Log");
		dataPanel.add(dataTitleLabel, BorderLayout.NORTH);
		dataPanel.add(new JScrollPane(theDataLog), BorderLayout.CENTER);
		JPanel commandPanel = new JPanel();
		commandPanel.add(new JLabel("Columns:"));
		commandPanel.add(dataColumns);
		commandPanel.add(dataDownloadButton);
		dataPanel.add(commandPanel, BorderLayout.SOUTH);
	}

	/**
	 *  Lay out Monitor Panel
	 */
	private void createMonitorPanel() {
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		JPanel batteryPanel = new JPanel();
		batteryPanel.setBorder(etchedBorder);
		batteryPanel.add(batteryGauge);
		leftPanel.add(batteryPanel);
		JPanel setSensorPanel = new JPanel();
		setSensorPanel.setBorder(etchedBorder);
		setSensorPanel.setLayout(new BoxLayout(setSensorPanel, BoxLayout.Y_AXIS));
		JLabel setSensorLabel = new JLabel("Set Sensor type & mode");
		JPanel labelPanel = new JPanel();
		labelPanel.add(setSensorLabel);
		setSensorPanel.add(labelPanel);
		JPanel portPanel = new JPanel();
		JLabel portLabel = new JLabel("Port:");
		portPanel.add(portLabel);
		portPanel.add(sensorList2);
		setSensorPanel.add(portPanel);
		JPanel typePanel = new JPanel();
		JLabel typeLabel = new JLabel("Type:");
		typePanel.add(typeLabel);
		typePanel.add(sensorTypeList);
		setSensorPanel.add(typePanel);
		JPanel modePanel = new JPanel();
		JLabel modeLabel = new JLabel("Mode:");
		modePanel.add(modeLabel);
		modePanel.add(sensorModeList);
		setSensorPanel.add(modePanel);
		JButton setSensorButton = new JButton("Set Sensor");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(setSensorButton);
		setSensorPanel.add(buttonPanel);
		leftPanel.add(setSensorPanel);
		
		setSensorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSensor();
			}
		});
		
		monitorPanel.add(leftPanel);
		for (int i = 0; i < 4; i++) {
			monitorPanel.add(sensorPanels[i]);
		}
		monitorPanel.add(monitorUpdateButton);
	}

	/**
	 *  Create the tabs for LCP
	 */
	private void createLCPTabs() {
		tabbedPane.removeAll();
		tabbedPane.addTab("Files", filesPanel);
		tabbedPane.addTab("Monitor", monitorPanel);
		tabbedPane.addTab("Control", controlPanel);
		tabbedPane.addTab("Miscellaneous", otherPanel);
	}
	
	/**
	 *  Create the tabs for LCP
	 */
	private void createConsoleTabs() {
		tabbedPane.removeAll();
		tabbedPane.addTab("Console", consolePanel);
	}
	
	/**
	 *  Create the tabs for LCP
	 */
	private void createDataLogTabs() {
		tabbedPane.removeAll();
		tabbedPane.addTab("Data Log", dataPanel);
	}
	
	/**
	 *  Set up the files panel
	 */
	private void createFilesPanel() {
		filesPanel.add(tablePane, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(deleteButton);
		buttonPanel.add(uploadButton);
		buttonPanel.add(downloadButton);
		buttonPanel.add(runButton);
		buttonPanel.add(soundButton);
		buttonPanel.add(formatButton);
		buttonPanel.setPreferredSize(filesButtonsPanelSize);
		filesPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 *  Populate the Other Panel
	 */
	private void createMiscellaneousPanel() {
		createInfoPanel();
		createTonePanel();
		createI2cPanel();
		createNamePanel();
	}
	
	/**
	 * Create rename NXT panel
	 */
	private void createNamePanel() {
		JPanel namePanel = new JPanel();
		namePanel.setPreferredSize(namePanelSize);
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
		namePanel.setBorder(etchedBorder);
		JPanel titlePanel = new JPanel();
		JLabel title = new JLabel("Change Friendly Name");
		JPanel newNamePanel = new JPanel();
		JLabel nameLabel = new JLabel("New name:");
		titlePanel.add(title);
		namePanel.add(titlePanel);
		newNamePanel.add(nameLabel);
		newNamePanel.add(newName);
		namePanel.add(newNamePanel);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(nameButton);
		namePanel.add(buttonPanel);
		otherPanel.add(namePanel);		
	}
	
	/**
	 * Create Info Panel
	 */
	private void createInfoPanel() {
		JPanel infoPanel = new JPanel();
		DeviceInfo di = null;
		FirmwareInfo fi = null;
		infoPanel.setLayout(new GridLayout(4, 2));
		JLabel freeFlashLabel = new JLabel("Free flash:  ");
		String protocolVersionString = "Unknown";
		String firmwareVersionString = "Unknown";
		String freeFlashString = "Unknown";

		if (nxtCommand != null) {
			try {
				di = nxtCommand.getDeviceInfo();
				fi = nxtCommand.getFirmwareVersion();
				protocolVersionString = fi.protocolVersion;
				firmwareVersionString = fi.firmwareVersion;
				freeFlashString = "" + di.freeFlash;
			} catch (IOException ioe) {
				showMessage("IO Exception getting device information");
			}
		}
		
		JLabel freeFlash = new JLabel(freeFlashString);
		infoPanel.add(freeFlashLabel);
		infoPanel.add(freeFlash);
		JLabel firmwareVersionLabel = new JLabel("Firmware version:");
		JLabel firmwareVersion = new JLabel(firmwareVersionString);
		infoPanel.add(firmwareVersionLabel);
		infoPanel.add(firmwareVersion);
		JLabel protocolVersionLabel = new JLabel("Protocol version:");
		JLabel protocolVersion = new JLabel(protocolVersionString);
		infoPanel.add(protocolVersionLabel);
		infoPanel.add(protocolVersion);
		infoPanel.setPreferredSize(innerInfoPanelSize);
		JPanel outerInfoPanel = new JPanel();
		outerInfoPanel.setPreferredSize(infoPanelSize);
		outerInfoPanel.setBorder(etchedBorder);
		JPanel headingPanel = new JPanel();
		JLabel headingLabel = new JLabel("Brick Information");
		headingPanel.add(headingLabel);
		outerInfoPanel.add(headingPanel);
		outerInfoPanel.add(infoPanel);
		otherPanel.add(outerInfoPanel);
	}
	
	/**
	 * Create play tone panel
	 */
	private void createTonePanel() {
		JPanel tonePanel = new JPanel();
		tonePanel.setLayout(new BoxLayout(tonePanel, BoxLayout.Y_AXIS));
		JPanel toneHeading = new JPanel();
		JLabel toneLabel = new JLabel("Play tone");
		toneHeading.add(toneLabel);
		tonePanel.add(toneHeading);
		JPanel freqPanel = new JPanel();
		JLabel freqLabel = new JLabel("Frequency:");
		freq.setColumns(5);
		freqLabel.setLabelFor(freq);
		JLabel durationLabel = new JLabel("Duration:");
		duration.setColumns(5);
		durationLabel.setLabelFor(duration);
		JButton play = new JButton("Play tone");
		freqPanel.add(freqLabel);
		freqPanel.add(freq);
		freqPanel.add(durationLabel);
		freqPanel.add(duration);
		tonePanel.add(freqPanel);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(play);
		tonePanel.add(buttonPanel);
		tonePanel.setPreferredSize(tonePanelSize);
		tonePanel.setBorder(etchedBorder);
		otherPanel.add(tonePanel);
		
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playTone();
			}
		});
	}
	
	/**
	 * Create panel for I2C 
	 */
	private void createI2cPanel() {
		JPanel i2cPanel = new JPanel();
		i2cPanel.setLayout(new BoxLayout(i2cPanel, BoxLayout.Y_AXIS));
		JPanel labelPanel = new JPanel();
		JLabel i2cTester = new JLabel("I2C Device Tester");
		labelPanel.add(i2cTester);
		i2cPanel.add(labelPanel);
		JPanel topPanel = new JPanel();
		JPanel sensorSelectPanel = new JPanel();
		JLabel sensorLabel = new JLabel("Port:");
		sensorSelectPanel.add(sensorLabel);
		sensorSelectPanel.add(sensorList);
		JLabel addressLabel = new JLabel("Address:");
		JFormattedTextField address = new JFormattedTextField(new Integer(2));
		address.setColumns(2);
		sensorSelectPanel.add(addressLabel);
		sensorSelectPanel.add(address);
		topPanel.add(sensorSelectPanel);
		JPanel rxlPanel = new JPanel();
		JLabel rxlLabel = new JLabel("RxData length:");
		rxlLabel.setLabelFor(rxDataLength);
		rxlPanel.add(rxlLabel);
		rxlPanel.add(rxDataLength);
		topPanel.add(rxlPanel);
		i2cPanel.add(topPanel);
		txData.setColumns(32);
		JPanel txPanel = new JPanel();
		JLabel txLabel = new JLabel("Send (hex):");
		txLabel.setLabelFor(txData);
		txPanel.add(txLabel);
		txPanel.add(txData);
		i2cPanel.add(txPanel);
		rxDataLength.setColumns(2);
		JPanel rxPanel = new JPanel();
		JLabel rxLabel = new JLabel("Received (hex):");
		rxLabel.setLabelFor(rxData);
		rxPanel.add(rxLabel);
		rxPanel.add(rxData);
		i2cPanel.add(rxPanel);
		JButton txDataSend = new JButton("Send");
		JButton i2cStatus = new JButton("Status");
		JButton rxDataReceive = new JButton("Receive");
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(txDataSend);
		buttonsPanel.add(i2cStatus);
		buttonsPanel.add(rxDataReceive);
		i2cPanel.add(buttonsPanel);
		i2cPanel.setBorder(BorderFactory.createEtchedBorder());
		i2cPanel.setPreferredSize(i2cPanelSize);
		otherPanel.add(i2cPanel);
		
		txDataSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				i2cSend();
			}
		});
		
		rxDataReceive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				i2cReceive();
			}
		});
		
		i2cStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				i2cStatus();
			}
		});
	}

	/**
	 *  Set up the files table
	 */
	private void createFilesTable() {
		fm = new ExtendedFileModel();
		fm.fetchFiles(nxtCommand);
		table = new JTable(fm);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(fileNameColumnWidth);
		tablePane = new JScrollPane(table);
		tablePane.setPreferredSize(filesAreaSize);
		
        new FileDrop( System.out, tablePane, /*dragBorder,*/ new FileDrop.Listener()
        {   public void filesDropped( java.io.File[] files )
            {   for( int i = 0; i < files.length; i++ )
                {   
            		String fileName = files[i].getName();
            		int row = fm.getRow(fileName);
            		try {
            			if (row >= 0) fm.delete(fileName, row);
            		} catch (IOException e) {
            			showMessage("IOException deleting file");
            		}
                	uploadFile(files[i]);
                }
            }
        }); 
	}

	/**
	 * Create a panel for motor control
	 */
	private JPanel createMotorPanel(int index) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		final JSlider slider = new JSlider(0, 100);
		sliders[index] = slider;
		slider.setMajorTickSpacing(50);
		slider.setMinorTickSpacing(10);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		JLabel label = new JLabel("   " + motorNames[index]);
		label.setPreferredSize(labelSize);
		panel.add(label);
		final JLabel value = new JLabel("    " + slider.getValue());
		value.setPreferredSize(labelSize);
		panel.add(value);
		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent c) {
				value.setText(String.format("%6d", slider.getValue()));
			}
		});
		
		slider.setPreferredSize(sliderSize);
		panel.add(slider);
		JLabel tacho = new JLabel("");
		tacho.setPreferredSize(tachoSize);
		tachos[index] = tacho;
		panel.add(tacho);
		JCheckBox selected = new JCheckBox();
		selectors[index] = selected;
		selected.setPreferredSize(labelSize);
		panel.add(selected);
		JCheckBox reverse = new JCheckBox();
		reverse.setPreferredSize(labelSize);
		reversors[index] = reverse;
		panel.add(reverse);
		JTextField limit = new JTextField(6);
		limit.setMaximumSize(new Dimension(60, 20));
		limits[index] = limit;
		panel.add(limit);
		JButton resetButton = new JButton("Reset");
		resetButtons[index] = resetButton;
		panel.add(resetButton);
		
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetTacho((JButton) e.getSource());
			}
		});
		
		panel.setBorder(BorderFactory.createEtchedBorder());
		return panel;
	}

	/**
	 * Create the header line for the motors
	 */
	private JPanel createMotorsHeader() {
		JPanel labelPanel = new JPanel();
		JLabel motorLabel = new JLabel("Motor");
		motorLabel.setPreferredSize(labelSize);
		JLabel speedLabel = new JLabel("Speed");
		speedLabel.setPreferredSize(labelSize);
		JLabel sliderLabel = new JLabel("          Set speed");
		sliderLabel.setPreferredSize(sliderSize);
		JLabel tachoLabel = new JLabel("Tachometer");
		tachoLabel.setPreferredSize(tachoSize);
		JLabel selectedLabel = new JLabel("Selected");
		selectedLabel.setPreferredSize(labelSize);
		JLabel reverseLabel = new JLabel("Reverse");
		reverseLabel.setPreferredSize(labelSize);
		JLabel limitLabel = new JLabel("Limit");
		limitLabel.setPreferredSize(labelSize);
		JLabel resetLabel = new JLabel("Reset");
		resetLabel.setPreferredSize(labelSize);
		labelPanel.add(motorLabel);
		labelPanel.add(speedLabel);
		labelPanel.add(sliderLabel);
		labelPanel.add(tachoLabel);
		labelPanel.add(selectedLabel);
		labelPanel.add(reverseLabel);
		labelPanel.add(limitLabel);
		labelPanel.add(resetLabel);
		return labelPanel;
	}

	/**
	 * Create the control panel
	 */
	private void createControlPanel() {
		JPanel motorsPanel = new JPanel();
		motorsPanel.setLayout(new BoxLayout(motorsPanel, BoxLayout.Y_AXIS));
		motorsPanel.add(createMotorsHeader());
		for (int i = 0; i < 3; i++) {
			motorsPanel.add(createMotorPanel(i));
		}
		JPanel buttonsPanel = new JPanel();
		controlPanel.add(motorsPanel);
		buttonsPanel.add(forwardButton);
		buttonsPanel.add(backwardButton);
		buttonsPanel.add(leftButton);
		buttonsPanel.add(rightButton);
		controlPanel.add(buttonsPanel);

		forwardButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!aMotorSelected()) return;
				int[] speed = getSpeeds();
				move(speed[0], speed[1], speed[2]);
			}

			public void mouseReleased(MouseEvent e) {
				stopMotors();
			}
		});

		backwardButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!aMotorSelected()) return;
				int[] speed = getSpeeds();
				move(-speed[0], -speed[1], -speed[2]);
			}

			public void mouseReleased(MouseEvent e) {
				stopMotors();
			}
		});

		leftButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!twoMotorsSelected()) return;
				int[] speed = getSpeeds();
				int[] multipliers = leftMultipliers();
				move(speed[0] * multipliers[0], speed[1] * multipliers[1], speed[2] * multipliers[2]);
			}

			public void mouseReleased(MouseEvent e) {
				stopMotors();
			}
		});

		rightButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!twoMotorsSelected()) return;
				int[] speed = getSpeeds();
				int[] multipliers = rightMultipliers();
				move(speed[0] * multipliers[0], speed[1] * multipliers[1], speed[2] * multipliers[2]);
			}

			public void mouseReleased(MouseEvent e) {
				stopMotors();
			}
		});
	}
	
	/**
	 * Return the number of motors selected
	 */
	private int numMotorsSelected() {
		int numSelected = 0;
		
		for(int i=0;i<3;i++) {
			if (selectors[i].isSelected()) numSelected ++;
		}
		
		return numSelected;
	}
	
	/**
	 * Return true iff exactly two motors selected and show message if not
	 */
	private boolean twoMotorsSelected() {
		if (numMotorsSelected() != 2) {
			showMessage("Exactly two motors must be selected");
			return false;	
		}
		return true;
	}
	
	/**
	 * Return true iff at least one motor selected and show message if not
	 */
	private boolean aMotorSelected() {
		if (numMotorsSelected() < 1) {
			showMessage("At least one motor must be selected");
			return false;	
		}
		return true;
	}
	
	/**
	 * Calculate speed multipliers for turning left
	 */
	private int[] leftMultipliers() {
		int[] multipliers = new int[3];
		boolean firstFound = false, secondFound = false;
		
		for(int i=0;i<3;i++) {
			if (selectors[i].isSelected() && !firstFound) {
				firstFound = true;
				multipliers[i] = -1;
			} else if (selectors[i].isSelected() && !secondFound) {
				secondFound = true;
				multipliers[i] = 1;
			} else {
				multipliers[i] = 0;
			}			
		}
		return multipliers;
	}
	
	
	/**
	 * Calculate the speed multipliers for turning right
	 */
	private int[] rightMultipliers() {
		int[] multipliers = new int[3];
		boolean firstFound = false, secondFound = false;
		
		for(int i=0;i<3;i++) {
			if (selectors[i].isSelected() && !firstFound) {
				firstFound = true;
				multipliers[i] = 1;
			} else if (selectors[i].isSelected() && !secondFound) {
				secondFound = true;
				multipliers[i] = -1;
			} else {
				multipliers[i] = 0;
			}			
		}
		return multipliers;
	}

	
	/**
	 * Download a file from the NXT
	 */
	private void getFile(File file, String fileName, int size) {
		FileOutputStream out = null;
		int received = 0;

		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {}

		try {
			FileInfo fi = nxtCommand.openRead(fileName);
			do {
				byte[] data = nxtCommand.readFile((byte) 0,
						(size - received < 51 ? size - received : 51));
				received += data.length;

				out.write(data);
			} while (received < size);

			nxtCommand.closeFile(fi.fileHandle);
			out.close();
		} catch (IOException ioe) {
			showMessage("IOException downloading file");
		}
	}

	/**
	 * Show a pop-up message
	 */
	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(frame, msg);
	}
	
	/**
	 * Update the sensor dials
	 */
	private void updateSensors() {
		if (nxtCommand == null) return;
		for (int i = 0; i < 4; i++) {
			int max = 1024;
			sensorPanels[i].setRawVal(sensorValues[i].rawADValue);
			if (sensorValues[i].sensorMode == PCTFULLSCALEMODE) max = 100;
			else if (sensorValues[i].sensorMode == BOOLEANMODE) max = 1;

			sensorPanels[i].setScaledMaxVal(max);
			sensorPanels[i].setScaledVal(sensorValues[i].scaledValue);
			sensorPanels[i].setType(sensorTypes[sensorValues[i].sensorType]);
			sensorPanels[i].repaint();
		}
		batteryGauge.setVal(mv);
	}

	/**
	 * Clear the files tab.
	 */
	private void clearFiles() {
		filesPanel.removeAll();
		filesPanel.repaint();
	}

	/**
	 * Switch between NXTS in table of available NXTs
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			int row = nxtTable.getSelectedRow();
			if (row < 0) return;		
			
			if (nxts[row].connectionState != NXTConnectionState.DISCONNECTED && 
					nxts[row].connectionState != NXTConnectionState.UNKNOWN) {
				updateConnectButton(true);
				if (nxts[row].connectionState == NXTConnectionState.LCP_CONNECTED) {
					nxtCommand = nxtCommands[row];
					showFiles();
				}
				if (nxts[row].connectionState == NXTConnectionState.DATALOG_CONNECTED) {
					dvc = dvcs[row];
				}
				if (nxts[row].connectionState == NXTConnectionState.RCONSOLE_CONNECTED) {
					cvc = cvcs[row];
				}
			} else {
				updateConnectButton(false);
				clearFiles();
			}
		}
	}

	/**
	 * Search for available NXTs and populate table with results.
	 */
	private void search() {
		closeAll();
		clearFiles();
		updateConnectButton(false);
		nxtTable.setModel(new NXTConnectionModel(null, 0));
		nxts = conn.search(nameText.getText(), null, getProtocols());

		if (nxts.length == 0) {
			showMessage("No NXTS found");
			return;
		}
		
		nm = new NXTConnectionModel(nxts, nxts.length);
		nxtTable.setModel(nm);
	    TableColumn col = nxtTable.getColumnModel().getColumn(3);
	    col.setPreferredWidth(150);
		nxtTable.setRowSelectionInterval(0, 0);
		nxtTable.getSelectionModel().addListSelectionListener(control);
		nxtCommands = new NXTCommand[nxts.length];
		nxtComms = new NXTComm[nxts.length];
		dvcs = new DataViewComms[nxts.length];
		cvcs = new ConsoleViewComms[nxts.length];
	}

	/**
	 * Close all connections
	 */
	private void closeAll() {
		if (nxtCommands == null) return;
		for (int i = 0; i < nxtCommands.length; i++) {
			NXTCommand nc = nxtCommands[i];
			if (nc != null)
				try {
					nc.close();
				} catch (IOException ioe) {}
		}
		nxtCommand = null;
	}

	/**
	 * Update connection status in the connections table
	 */
	private void updateConnectionStatus(int row, NXTConnectionState state) {
		nm.setConnected(row, state);
		nxtTable.repaint();
		updateConnectButton(state != NXTConnectionState.DISCONNECTED);
		if (state != NXTConnectionState.LCP_CONNECTED) nxtCommands[row] = null;
	}

	/**
	 * Toggle Connect button between Connect and Disconnect
	 */
	private void updateConnectButton(boolean connected) {
		connectButton.setText((connected ? "Disconnect" : "Connect"));
	}

	/**
	 * Get the selected protocols 
	 */
	private int getProtocols() {
		int protocols = 0;
		if (usbButton.isSelected())	protocols = NXTCommFactory.USB;
		if (bluetoothButton.isSelected()) protocols = NXTCommFactory.BLUETOOTH;
		if (bothButton.isSelected()) protocols = NXTCommFactory.USB | NXTCommFactory.BLUETOOTH;
		return protocols;
	}
	
	/**
	 * Get the Application protocol
	 */
	private int getAppProtocol() {
		int appProtocol = 0;
		if (lcpButton.isSelected())	appProtocol = LCP;
		if (rconsoleButton.isSelected()) appProtocol = RCONSOLE;
		if (datalogButton.isSelected()) appProtocol = DATALOG;
		return appProtocol;
	}

	/**
	 * Stop the motors on the NXT and update the tachometer values
	 */
	private void stopMotors() {
		try {
			if (nxtCommand == null)	return;
			nxtCommand.setOutputState(0, (byte) 0, 0, 0, 0, 0, 0);
			nxtCommand.setOutputState(1, (byte) 0, 0, 0, 0, 0, 0);
			nxtCommand.setOutputState(2, (byte) 0, 0, 0, 0, 0, 0);

			tachos[0].setText("      " + nxtCommand.getTachoCount(0));
			tachos[1].setText("      " + nxtCommand.getTachoCount(1));
			tachos[2].setText("      " + nxtCommand.getTachoCount(2));
		} catch (IOException ioe) {
			showMessage("IOException while stopping motors");
		}
	}

	/**
	 * Get an array of the tacho limit text boxes
	 */
	private int[] getLimits() {
		int[] lim = new int[3];

		for (int i = 0; i < 3; i++) {
			try {
				lim[i] = Integer.parseInt(limits[i].getText());
			} catch (NumberFormatException nfe) {
				lim[i] = 0;
			}
		}
		return lim;
	}
	
	/**
	 * Get an array of the speed slider values
	 */
	private int[] getSpeeds() {
		int[] speed = new int[3];

		for (int i = 0; i < 3; i++) {
			speed[i] = sliders[i].getValue();
			if (reversors[i].isSelected()) speed[i] = -speed[i];
		}
		return speed;
	}

	/**
	 * Retrieve the sensor and battery values from the NXT
	 */
	private void getSensorValues() {
		try {
			for (int i = 0; i < 4; i++) {
				if (nxtCommand == null)	return;
				sensorValues[i] = nxtCommand.getInputValues(i);
			}
			mv = nxtCommand.getBatteryLevel();
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}

	/**
	 * Convert a byte array to a string of hex characters
	 */
	private String toHex(byte[] b) {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			byte j = b[i];
			output.append(Character.forDigit((j >> 4) & 0xF, 16));
			output.append(Character.forDigit(j & 0xF, 16));
		}
		return output.toString();
	}

	/**
	 * Convert a string of hex characters to a byte array
	 */
	private byte[] fromHex(String s) {
		byte[] reply = new byte[s.length() / 2];
		for (int i = 0; i < reply.length; i++) {
			char c1 = s.charAt(i * 2);
			char c2 = s.charAt(i * 2 + 1);
			reply[i] = (byte) (getHexDigit(c1) << 4 | getHexDigit(c2));
		}
		return reply;
	}

	/**
	 * Convert a character to a hex digit
	 */
	private int getHexDigit(char c) {
		if (c >= '0' && c <= '9') return c - '0';
		if (c >= 'a' && c <= 'f') return c - 'a' + 10;
		if (c >= 'A' && c <= 'F') return c - 'A' + 10;
		return 0;
	}

	/**
	 * Add one byte array to another
	 */
	private byte[] appendBytes(byte[] array1, byte[] array2) {
		byte[] array = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, array, 0, array1.length);
		System.arraycopy(array2, 0, array, array1.length, array2.length);
		return array;
	}

	/**
	 * Connect to the NXT
	 */
	private void connect() {
		int row = nxtTable.getSelectedRow();
		int currentAppProtocol = getAppProtocol();
		
		if (row >= 0) {
			if (nxts[row].connectionState == NXTConnectionState.LCP_CONNECTED) {// Connected, so disconnect
				try {
					nxtCommand = nxtCommands[row];
					nxtCommand.close();
					nxts[row].connectionState = NXTConnectionState.DISCONNECTED;
				} catch (IOException ioe) {
					showMessage("IOException while disconnecting");
				}
				updateConnectionStatus(row, nxts[row].connectionState);
				clearFiles();
				nxtCommand = null;
				return;
			}
			
			if (nxts[row].connectionState == NXTConnectionState.RCONSOLE_CONNECTED) {// Connected, so disconnect
				cvc.close();
				nxts[row].connectionState = NXTConnectionState.DISCONNECTED;
				updateConnectionStatus(row, nxts[row].connectionState);
				cvc = null;
				return;
			}
			
			if (nxts[row].connectionState == NXTConnectionState.DATALOG_CONNECTED) {// Connected, so disconnect			
				dvc.close();
				nxts[row].connectionState = NXTConnectionState.DISCONNECTED;
				updateConnectionStatus(row, nxts[row].connectionState);
				dvc.setConnected(false);
				dvc = null;
				return;
			}
			
			if (currentAppProtocol == RCONSOLE) {
				consoleConnect();
				return;
			} else if (currentAppProtocol == DATALOG) {
				dataConnect();
				return;
			}

			// Connect
			boolean open = false;
			try {
				clearFiles();
				nxtCommand = new NXTCommand();
				nxtCommands[row] = nxtCommand;
				// currentRow = row;
				NXTComm nxtComm = NXTCommFactory.createNXTComm(nxts[row].protocol);
				nxtComms[row] = nxtComm;
				open = nxtComm.open(nxts[row], NXTComm.LCP);
				nxtCommand.setNXTComm(nxtComm);
				//System.out.println("NXTInfo status " + nxts[row].connectionState);
			} catch (NXTCommException e) {
				open = false;
			}
			
			if (!open) {
				showMessage("Failed to connect");
			} else {
				updateConnectionStatus(row, nxts[row].connectionState);
				showFiles();
			}
		} else showMessage("You must do a search and select the NXT to connect to");
	}

	/**
	 * Connect to RConsole
	 */
	private void consoleConnect() {		
		int row = nxtTable.getSelectedRow();
		if (row >= 0) {
			boolean open = false;
			theConsoleLog.setText("");
			ConsoleViewerUI ui = new ConsoleViewerSwingUI(this);
			cvcs[row] = new ConsoleViewComms(ui, new ConsoleDebugDisplay(ui), true);
			cvc = cvcs[row];
			open = cvc.connectTo(nxts[row].name, nxts[row].deviceAddress, nxts[row].protocol, false);
	        if (!open) {
	            showMessage("Failed to connect to RConsole");
	            return;
	           }
			nxts[row].connectionState = NXTConnectionState.RCONSOLE_CONNECTED;
			updateConnectionStatus(row,nxts[row].connectionState);
		}
	}
	
	/**
	 * Connect to the data logger
	 */
	private void dataConnect() {
		int row = nxtTable.getSelectedRow();
		if (row >= 0) {
			boolean open = false;
			theDataLog.setText("");
			
			dvcs[row] = new DataViewComms(this);
			dvc = dvcs[row];
		    open = dvc.connectTo(nxts[row].name, nxts[row].deviceAddress, nxts[row].protocol);
            if (!open) {
            	showMessage("Failed to connect to data logger");
            	return;
            }

			nxts[row].connectionState = NXTConnectionState.DATALOG_CONNECTED;
			updateConnectionStatus(row, nxts[row].connectionState);
		}	
	}
	
	/**
	 * Append data item to the data log
	 */
	public void append(float x) {
		if (0 == recordCount % rowLength) theDataLog.append("\n");
		theDataLog.append(FORMAT_FLOAT.format(x) + "\t ");
		recordCount++;
	}
	
	/**
	 * Delete selected files
	 */
	private void deleteFiles() {
		frame.setCursor(hourglassCursor);
		try {
			for (int i = 0; i < fm.getRowCount(); i++) {
				Boolean b = (Boolean) fm.getValueAt(i,ExtendedFileModel.COL_DELETE);
				String fileName = (String) fm.getValueAt(i,ExtendedFileModel.COL_NAME);
				boolean deleteIt = b.booleanValue();
				if (deleteIt) {
					nxtCommand.delete(fileName);
				}
			}
			fm.fetchFiles(nxtCommand);
		} catch (IOException ioe) {
			showMessage("IOException deleting files");
		}
		frame.setCursor(normalCursor);
	}
	
	/**
	 * Choose a file and update it
	 */
	private void upload() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(frame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			uploadFile(file);
		}
	}
	
	private void uploadFile(File file) {
		if (file.getName().length() > 20) {
			showMessage("File name is more than 20 characters");
		} else {
			frame.setCursor(hourglassCursor);
			try {
				nxtCommand.uploadFile(file, file.getName());
				String msg = fm.fetchFiles(nxtCommand);
				if (msg != null) showMessage(msg);
			} catch (IOException ioe) {
				showMessage("IOException uploading file");
			}
			frame.setCursor(normalCursor);
		}
	}
	
	/**
	 * Download the selected file
	 */
	private void download() {
		int i = table.getSelectedRow();
		if (i < 0) return;
		
		String fileName = fm.getFile(i).fileName;
		int size = fm.getFile(i).fileSize;
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setSelectedFile(new File(fileName));
		
		int returnVal = fc.showSaveDialog(frame);
		if (returnVal == 0) {
			File file = fc.getSelectedFile();
			frame.setCursor(hourglassCursor);
			getFile(file, fileName, size);
			frame.setCursor(normalCursor);
		}
	}
	
	/**
	 * Run the selected file.
	 */
	private void runFile() {
		int row = table.getSelectedRow();
		if (row < 0) return;
		String fileName = fm.getFile(row).fileName;
		
		try {
			nxtCommand.startProgram(fileName);
			nxtCommand.close();
			nxtCommand = null;
			updateConnectionStatus(nxtTable.getSelectedRow(), nxts[row].connectionState);
			clearFiles();
		} catch (IOException ioe) {
			showMessage("IOException running program");
		}
	}
	
	/**
	 * Change the friendly name of the NXT
	 */
	private void rename(String name) {
		if (nxtCommand == null) return;
		if (name != null && name.length() <= 16 && name.length() > 0) {
			frame.setCursor(hourglassCursor);
			try {
				nxtCommand.setFriendlyName(name);
				frame.setTitle(title + " : " + name);
				newName.setText("");
			} catch (IOException ioe) {
				showMessage("IOException setting friendly name");
			}
			frame.setCursor(normalCursor);
		} else showMessage("Please supply a name from 1 to 16 chareacters");
	}
	
	/**
	 * Move the motors
	 */
	private void move(int speed0, int speed1, int speed2 ) {
		int[] lim = getLimits();
		
		try {
			if (nxtCommand == null) return;
			if (selectors[0].isSelected())
				nxtCommand.setOutputState(0, (byte) speed0, 0, 0, 0, 0, lim[0]);
			if (selectors[1].isSelected())
				nxtCommand.setOutputState(1, (byte) speed1, 0, 0, 0, 0, lim[1]);
			if (selectors[2].isSelected())
				nxtCommand.setOutputState(2, (byte) speed2, 0, 0, 0, 0, lim[2]);
		} catch (IOException ioe) {
			showMessage("IOException updating control");
		}
	}
	
	/**
	 * Set the sensor type and mode
	 */
	private void setSensor() {
		try {
			if (nxtCommand == null)	return;
			nxtCommand.setInputMode(
					sensorList.getSelectedIndex(),
					sensorTypeValues[sensorTypeList.getSelectedIndex()],
					sensorModeValues[sensorModeList.getSelectedIndex()]);
		} catch (IOException ioe) {
			showMessage("IOException setting sensor type");
		}
	}
	
	/**
	 * Play a tone
	 */
	private void playTone() {
		try {
			if (nxtCommand == null) return;
			nxtCommand.playTone((Integer) freq.getValue(), (Integer) duration.getValue());
		} catch (IOException ioe) {
			showMessage("IO Exception playing tone");
		} catch (NumberFormatException nfe) {
			showMessage("Frequency and Duration must be integers");
		}
	}
	
	/**
	 * Reset the tachometer for a motor
	 */
	private void resetTacho(JButton b) {
		int motor = -1;
		
		for (int i = 0; i < 3; i++) {
			if (b == resetButtons[i]) motor = i;
		}
		
		if (nxtCommand == null) return;
		try {
			nxtCommand.resetMotorPosition(motor, false);
			tachos[motor].setText("      " + nxtCommand.getTachoCount(motor));
		} catch (IOException ioe) {
			showMessage("IO Exception resetting motor");
		}
	}
	
	/**
	 * Play a sound file
	 */
	private void playSoundFile() {
		int row = table.getSelectedRow();
		if (row < 0) return;
		
		String fileName = fm.getFile(row).fileName;
		try {
			nxtCommand.playSoundFile(fileName, false);
		} catch (IOException ioe) {
			showMessage("IO Exception playing sound file");
		}
	}
	
	/**
	 * Send I2C request
	 */
	private void i2cSend() {
		byte[] address = new byte[1];
		address[0] = 2; // default I2C address
		
		if (nxtCommand == null)	return;
		try {
			nxtCommand.LSWrite(
					(byte) sensorList.getSelectedIndex(),
					appendBytes(address, fromHex(txData.getText())),
					((Integer) rxDataLength.getValue()).byteValue());
		} catch (IOException ioe) {
			showMessage("IO Exception sending txData");
		}
	}
	
	/** 
	 * Send an i2c status request
	 */
	private void i2cStatus() {
		if (nxtCommand == null)	return;
		try {
			byte[] reply = nxtCommand.LSGetStatus((byte) sensorList.getSelectedIndex());
			if (reply != null) {
				System.out.println("LSStatus reply length = " + reply.length);
				String hex = toHex(reply);
				rxData.setText(hex);
			} else
				rxData.setText("null");
		} catch (IOException ioe) {
			showMessage("IO Exception getting status");
		}
	}
	
	/**
	 * Read i2c reply
	 */
	private void i2cReceive() {
		if (nxtCommand == null)	return;
		try {
			byte[] reply = nxtCommand.LSRead((byte) sensorList.getSelectedIndex());
			if (reply != null) {
				String hex = toHex(reply);
				rxData.setText(hex);
			} else
				rxData.setText("null");
		} catch (IOException ioe) {
			showMessage("IO Exception reading rxData");
		}
	}
	
	/**
	 * Format the file system
	 */
	private void format() {
		if (nxtCommand == null) return;
		try {
			nxtCommand.deleteUserFlash();
			fm.fetchFiles(nxtCommand);
		} catch (IOException ioe) {
			showMessage("IO Exception formatting file system");
		}
	}
	
	public void logMessage(String msg) {
		System.out.println(msg);
	}
	public void connectedTo(String name, String address) {
	}

	public void setStatus(String msg) {
	}

	public void append(String value) {
		theConsoleLog.append(value);
		theConsoleLog.setCaretPosition(theConsoleLog.getDocument().getLength());
	}

    public void updateLCD(byte[] buffer)
    {

    }

    public void exception(int classNo, int methodNo, int pc, int[] stackTrace)
    {
    }

}
