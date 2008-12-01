package lejos.pc.tools;

import lejos.pc.comm.*;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.io.*;

/**
 * 
 *  Graphical control center for leJOS NXJ.
 *
 *  @author Lawrie Griffiths
 */
public class NXJControl implements ListSelectionListener {
  public static final int MAX_FILES = 30;
  private static final Dimension frameSize = new Dimension(800,600);
  private static final Dimension filesAreaSize = new Dimension(780,300);
  private static final Dimension filesPanelSize = new Dimension(500, 400);
  private static final Dimension nxtButtonsPanelSize = new Dimension(200,100);
  private static final Dimension filesButtonsPanelSize = new Dimension(605,100);
  private static final Dimension nxtTableSize = new Dimension(450,100);
  private static final String title = "NXJ Control Center";
  
  private int numFiles;
  private FileInfo[] files= new FileInfo[MAX_FILES];
  private NXTCommand nxtCommand;
  private NXTCommand[] nxtCommands;
  private Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
  private Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
  private JFrame frame = new JFrame(title); 
  private NXTConnector conn = new NXTConnector();
  private NXTInfo[] nxts;
  private NXTConnectionModel nm;
  private JTable nxtTable = new JTable();
  private JScrollPane nxtTablePane;
  private JTextField nameText = new JTextField(8);
  private FileModel fm;
  private JTable table;
  private JScrollPane tablePane;
  private JPanel filesPanel = new JPanel();
  private JPanel consolePanel = new JPanel();
  private JPanel monitorPanel = new JPanel();
  private JPanel controlPanel = new JPanel();
  private JPanel dataPanel = new JPanel();
  private JPanel otherPanel = new JPanel();
  private int currentRow;
  private NXJControl control;
  private JTextArea theConsoleLog = new JTextArea(20,68);
  private JTextArea theDataLog = new JTextArea(20,68);
  private DataOutputStream os;
  private DataInputStream is;
  private ReaderThread readerThread;
  private LabeledGauge batteryGauge = new LabeledGauge("Battery", 10000);
  private JSlider aSlide = new JSlider(-100,100);
  private JSlider bSlide = new JSlider(-100,100);
  private JSlider cSlide = new JSlider(-100,100);
  private JButton connectButton = new JButton("Connect");
  private JButton dataConnectButton = new JButton("Connect");
  private boolean consoleConnected = false;
  private boolean dataConnected = false;
  private JRadioButton usbButton = new JRadioButton("USB");
  private JRadioButton bluetoothButton = new JRadioButton("Bluetooth");
  private JRadioButton bothButton = new JRadioButton("Both", true);
  private NXTComm nxtCommConsole;
  private NXTComm nxtCommData;
  
  /**
   * Command line entry point
   * 
   * @param args
   */
  public static void main(String args[]) {
	try {
		NXJControl instance = new NXJControl();
		instance.run();
	} catch(Throwable t) {
         System.err.println("Error: " + t.getMessage());
	}
  }
  
  /**
   * Run the program
   * 
   * @throws NXTCommException
   */
  public void run() {
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
	
    // Create buttons
    JButton searchButton = new JButton("Search");
    final JButton consoleConnectButton = new JButton("Connect");
    JButton monitorUpdateButton = new JButton("Update");
    JButton controlUpdateButton = new JButton("Update");
    
    // Search Button: search for NXTs
    searchButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          search();
        }
      });
     
    // Connect Button: connect to selected NXT
    connectButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          int row = nxtTable.getSelectedRow();
          if (row >= 0) {
        	  if (nxtCommands[row] != null) {// Connected, so disconnect
        		  try {
        			  nxtCommand = nxtCommands[row];
        			  nxtCommand.close();
        		  } catch (IOException ioe) {
        			showMessage("IOException while disconnecting");  
        		  }
        		  updateConnectionStatus(row,false);
        		  clearFiles();
        		  return;
        	  }
        	  
        	  // Connect
        	  boolean open = false;
        	  try {
        		  clearFiles();
        		  nxtCommand = new NXTCommand();
        		  nxtCommand.addLogListener(new ToolsLogger());
        		  nxtCommands[row] = nxtCommand;
        		  currentRow = row;
        		  System.out.println("Opening " + nxts[row].name);
        		  open = nxtCommand.open(nxts[row]);       		  
        	  } catch(NXTCommException e) {
        		  //showMessage("Exception opening NXT: " + e.getMessage());
        		  open = false;
        	  }
        	  if (!open) {
        		  showMessage("Failed to connect");
        	  } else {
        		  updateConnectionStatus(row, true);
        		  showFiles();
        	  }
          }
        }
      });
    
    // Console Connect Button: connect the RConsole viewer
    consoleConnectButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          if (consoleConnected) {
        	  try {
        		  nxtCommConsole.close();
        	  } catch (IOException ioe) {
        		 showMessage("Failed to close connection: " + ioe.getMessage());
        	  }
        	  consoleConnected = false;
        	  consoleConnectButton.setText("Connect");
        	  return;
          }
          int row = nxtTable.getSelectedRow();
          if (row >= 0) {
            boolean open = false;
      	    theConsoleLog.setText("");
        	try {
        		nxtCommConsole = NXTCommFactory.createNXTComm(nxts[row].protocol);
        		open = nxtCommConsole.open(nxts[row]);
        	} catch (NXTCommException e) {
        		showMessage("Exception in open:" + e.getMessage());
        		open = false;
        	}
        	
	        if (!open) {
	          showMessage("Failed to connect");
	          return;
	        }
	        
	        consoleConnected = true;
	        os = new DataOutputStream(nxtCommConsole.getOutputStream());
	        is = new DataInputStream(nxtCommConsole.getInputStream());
	
	        try { // handshake
	          byte[] hello = new byte[] {'C', 'O', 'N'};
	          os.write(hello);
	          os.flush();
	        } catch (IOException e){
	          showMessage("Handshake failed");
	        }
	        consoleConnectButton.setText("Disconnect");
	        readerThread = new ReaderThread();
	        readerThread.start();
          }
        }
      });
    
    // Data log Connect Button: connect to the Data Logger
    dataConnectButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
            if (dataConnected) {
          	  try {
          		  nxtCommData.close();
          	  } catch (IOException ioe) {
          		 showMessage("Failed to close connection: " + ioe.getMessage());
          	  }
          	  dataConnected = false;
          	  dataConnectButton.setText("Disconnect");
          	  return;
            }
            
            int row = nxtTable.getSelectedRow();
            if (row >= 0) {
              boolean open = false;
        	  theDataLog.setText("");
          	  try {
          		nxtCommData = NXTCommFactory.createNXTComm(nxts[row].protocol);
          		open = nxtCommData.open(nxts[row]);
          	  } catch (NXTCommException e) {
          	    showMessage("Exception in open:" + e.getMessage());
          	    open = false;
          	  }
          	
  	          if (!open) {
  	            showMessage("Failed to connect");
  	            return;
  	          }
  	        
  	          dataConnected = true;
  	          os = new DataOutputStream(nxtCommData.getOutputStream());
  	          is = new DataInputStream(nxtCommData.getInputStream());
          
              int b = 15;
              int recordCount = 0;
              int rowLength = 8;
              try { //handshake - ready to read data
                os.write(b);
                os.flush();
              } catch (IOException e) {
                System.out.println(e + " handshake failed ");
              }
              float x = 0;
              try
              {
                int length = is.readInt();
                System.out.println(" reading length " + length);
                for (int i = 0; i < length; i++) {
                  if (0 == recordCount % rowLength) theDataLog.append("\n");
                  x = is.readFloat();
                  theDataLog.append(x + "\t ");
                  recordCount++;
                }
                nxtCommData.close();
              } catch (IOException e) {
                System.out.println("read error " + e);
              }
               System.out.println("Read all data");  
               dataConnected = false;
            }
        }
      });
    
    // Monitor Update Button: get values being monitored
    monitorUpdateButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {      	
          int mv = 0;
          try {
        	  if (nxtCommand == null) return;
        	  mv = nxtCommand.getBatteryLevel();
        	  System.out.println("Millivolts = " + mv);
          } catch (IOException ioe) {
        	  showMessage("IOException updating monitor " + ioe.getMessage());
          }
          batteryGauge.setVal(mv);
        }
      });
    
    // Control Update Button: send new control values
    controlUpdateButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {      	
          int mv = 0;
          try {
        	  if (nxtCommand == null) return;
        	  nxtCommand.setOutputState(0, (byte) aSlide.getValue(), 0, 0, 0, 0, 0);
        	  nxtCommand.setOutputState(1, (byte) bSlide.getValue(), 0, 0, 0, 0, 0);
        	  nxtCommand.setOutputState(2, (byte) cSlide.getValue(), 0, 0, 0, 0, 0);
          } catch (IOException ioe) {
        	  showMessage("IOException updating control");
          }
          batteryGauge.setVal(mv);
        }
      });
    
    // Lay out NXT Selection panel
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
    nxtButtonPanel.add(searchButton);
    nxtButtonPanel.add(connectButton);
	nxtButtonPanel.add(usbButton);
	nxtButtonPanel.add(bluetoothButton);
	nxtButtonPanel.add(bothButton);
	ButtonGroup protocolButtonGroup = new ButtonGroup();
	protocolButtonGroup.add(usbButton);
	protocolButtonGroup.add(bluetoothButton);
	protocolButtonGroup.add(bothButton);
    nxtButtonPanel.setPreferredSize(new Dimension(200,100));   
    nxtPanel.add(nxtButtonPanel, BorderLayout.EAST);
    
    // Lay out Console Panel
    JLabel consoleTitleLabel = new JLabel("Output from RConsole");
    consolePanel.add(consoleTitleLabel, BorderLayout.NORTH);
    consolePanel.add(new JScrollPane(theConsoleLog), BorderLayout.CENTER);
    consolePanel.add(consoleConnectButton, BorderLayout.SOUTH);
    
    // Lay out Data Panel
    JLabel dataTitleLabel = new JLabel("Data Log");
    dataPanel.add(dataTitleLabel, BorderLayout.NORTH);
    dataPanel.add(new JScrollPane(theDataLog), BorderLayout.CENTER);
    dataPanel.add(dataConnectButton, BorderLayout.SOUTH);
    
    // Lay out Monitor Panel
    monitorPanel.add(batteryGauge, BorderLayout.NORTH);
    monitorPanel.add(monitorUpdateButton,BorderLayout.SOUTH);
    
    // Lay out Control Panel
    JPanel motorPanel = new JPanel();
    motorPanel.setLayout(new GridLayout(3,1));   
    JPanel aPanel = new JPanel();   
    aSlide.setMajorTickSpacing(100);
    aSlide.setMinorTickSpacing(10);
    aSlide.setPaintLabels(true);
    aSlide.setPaintTicks(true);   
    JLabel aLabel = new JLabel("Motor.A:  ");
    aPanel.add(aLabel);
    aPanel.add(aSlide);  
    JPanel bPanel = new JPanel();    
    bSlide.setMajorTickSpacing(100);
    bSlide.setMinorTickSpacing(10);
    bSlide.setPaintLabels(true);
    bSlide.setPaintTicks(true);    
    JLabel bLabel = new JLabel("Motor.B:  ");
    bPanel.add(bLabel);
    bPanel.add(bSlide);    
    JPanel cPanel = new JPanel();   
    cSlide.setMajorTickSpacing(100);
    cSlide.setMinorTickSpacing(10);
    cSlide.setPaintLabels(true);
    cSlide.setPaintTicks(true);   
    JLabel cLabel = new JLabel("Motor.C:  ");
    cPanel.add(cLabel);
    cPanel.add(cSlide);    
    motorPanel.add(aPanel);
    motorPanel.add(bPanel);
    motorPanel.add(cPanel);   
    controlPanel.add(motorPanel);
    controlPanel.add(controlUpdateButton);

    // set the size of the files panel
    filesPanel.setPreferredSize(filesPanelSize); 
    
    // Create the tabs
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Files", filesPanel);
    tabbedPane.addTab("Console", consolePanel);
    tabbedPane.addTab("Data Log", dataPanel);
    tabbedPane.addTab("Monitor", monitorPanel);
    tabbedPane.addTab("Control", controlPanel);
    tabbedPane.addTab("Miscellaneous", otherPanel);
    frame.getContentPane().add(tabbedPane, BorderLayout.SOUTH);

    // Set up the frame
    frame.setPreferredSize(frameSize);
    frame.pack();
    frame.setVisible(true);
  }
  
  /**
   * Thread to display RConsole output
   *
   */
  private class ReaderThread extends Thread
  {
     public  void run()
     {     
        while(consoleConnected)
        {
          try
          {  
             int input;
             while ((input = is.read()) >= 0) 
             {   
                theConsoleLog.append(""+(char)input);
                //System.out.print(""+(char)input);
             } 
             is.close();
          }
          catch(IOException e) 
          {
             //System.out.println( "read error"); 
             consoleConnected = false;
          }
          Thread.yield();
        }
        try {
       	 nxtCommConsole.close();
        } catch (IOException ioe) {}
     }
  }
  
  /**
   * Get files from the NXT and display them in the files panel
   */
  private void showFiles() {
	// Fetch the files
    fetchFiles();
    
    // Set up the files table
    fm = new FileModel(frame, files, numFiles);     
    table = new JTable(fm);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);    
    TableColumn col = table.getColumnModel().getColumn(0);
    col.setPreferredWidth(300);
    tablePane = new JScrollPane(table);
    tablePane.setPreferredSize(filesAreaSize);
    
    // Remove current content of files panel
    filesPanel.removeAll();
    
    // Set up the files panel
    filesPanel.add(tablePane, BorderLayout.CENTER);
    JPanel buttonPanel = new JPanel();
    JButton deleteButton = new JButton("Delete Files");
    JButton uploadButton = new JButton("Upload file");
    JButton downloadButton = new JButton("Download file");
    JButton runButton = new JButton("Run program");
  
    buttonPanel.add(deleteButton);
    buttonPanel.add(uploadButton);
    buttonPanel.add(downloadButton);
    buttonPanel.add(runButton); 
    buttonPanel.setPreferredSize(filesButtonsPanelSize);
    filesPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    // Remove existing content from the others panel
    otherPanel.removeAll();
   
    // Populate the Other Panel
    JButton nameButton = new JButton("Set Name"); 
    JPanel infoPanel = new JPanel();
    DeviceInfo di;
    FirmwareInfo fi;
    infoPanel.setLayout(new GridLayout(3,2));
    infoPanel.setPreferredSize(new Dimension(300,100));  
    try {
    	di = nxtCommand.getDeviceInfo();
    	JLabel freeFlashLabel = new JLabel("Free flash:  ");
    	JLabel freeFlash = new JLabel(""+di.freeFlash);
    	infoPanel.add(freeFlashLabel);
    	infoPanel.add(freeFlash);
    	fi = nxtCommand.getFirmwareVersion();
    	JLabel firmwareVersionLabel = new JLabel("Firmware version: ");
    	JLabel firmwareVersion = new JLabel("" + fi.firmwareVersion);
    	infoPanel.add(firmwareVersionLabel);
    	infoPanel.add(firmwareVersion);
    	JLabel protocolVersionLabel = new JLabel("Protocol version:  ");
    	JLabel protocolVersion = new JLabel("" + fi.protocolVersion);
    	infoPanel.add(protocolVersionLabel);
    	infoPanel.add(protocolVersion);
    	otherPanel.add(infoPanel);
        otherPanel.add(nameButton);
    } catch (IOException ioe) {
    	showMessage("IO Exception getting device information");
    }

    // Delete Button: delete a file from the NXT
    deleteButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
    	frame.setCursor(hourglassCursor);      
    	try {
	        for(int i=0;i<fm.getRowCount();i++) {
	          Boolean b = (Boolean) fm.getValueAt(i,4);
	          boolean deleteIt = b.booleanValue();
	          String fileName = (String) fm.getValueAt(i,0);
	          if (deleteIt) {
	            nxtCommand.delete(fileName); 
		        fm.delete(i);
		        numFiles--;
		        i--;
		        table.invalidate();
		        tablePane.revalidate();   
	          }
	        }
        } catch (IOException ioe) {
        	showMessage("IOException deleting files");
        }
        frame.setCursor(normalCursor);
      }
    });

    // Upload Button: upload a file to the NXT
    uploadButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {       
        JFileChooser fc = new JFileChooser();

        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          frame.setCursor(hourglassCursor);
          try {
        	  File file = fc.getSelectedFile();
        	  if (file.getName().length() > 20) {
        		  showMessage("File name is more than 20 characters");
        	  } else {   	
		          nxtCommand.uploadFile(file);
		          fetchFiles();
		          fm.setData(files, numFiles);
		          table.invalidate();
		          tablePane.revalidate();
        	  }
          } catch (IOException ioe) {
        	  showMessage("IOException uploading file");
          }
          frame.setCursor(normalCursor);
        }
      }
    });
    
    // Download Button: download a file from from the NXT
    downloadButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        int i = table.getSelectedRow();
        if (i<0) return;
        String fileName = files[i].fileName;
        int size = files[i].fileSize;
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
    });
    
    // Run Button: run a program on the NXT
    runButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
      	  int row = table.getSelectedRow();
      	  if (row<0) return;
          String fileName = files[row].fileName;
          try {
        	  runProgram(fileName);
        	  nxtCommand.close();
        	  updateConnectionStatus(row,false);
        	  clearFiles();
          } catch (IOException ioe) {
        	  showMessage("IOException running program");
          }
        }
      });
    
    // Set Name Button: set a new name for the NXT
    nameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          String name = JOptionPane.showInputDialog(frame,"New Name");
          
          if (name != null && name.length() <= 16) {
        	  frame.setCursor(hourglassCursor);        
              try {
            	  nxtCommand.setFriendlyName(name);
            	  frame.setTitle(title + " : " + name);
              } catch (IOException ioe) {
            	  showMessage("IOException setting friendly name");
              }
        	  frame.setCursor(normalCursor);
          }
        }
      });
    
    // Pack the frame
    frame.pack();
  }

  /**
   * Fetch filenames from the NXT into an array of FileInfo structures
   */
  private void fetchFiles() {
	numFiles = 0;
    try {
      files[0] = nxtCommand.findFirst("*.*");
	
	  if (files[0] != null) {
	    numFiles = 1;
	
	    for(int i=1;i<MAX_FILES;i++) {
	      files[i] = nxtCommand.findNext(files[i-1].fileHandle);
	      if (files[i] == null) break;
	      else {
	        numFiles++;
	      }
	    }
	  }
    } catch (IOException ioe) {
    	showMessage("IOException fetching files");
    }
  }

  /**
   * Download a file from the NXT
   * 
   * @param file the destination PC file
   * @param fileName the NXT filename
   * @param size the size in bytes of the NXT file
   */
  public void getFile(File file, String fileName, int size) {
    FileOutputStream out = null; 
    int received = 0;

    try {
      out = new FileOutputStream(file);
    } catch (FileNotFoundException e) {}

    try {  	
      nxtCommand.openRead(fileName);
      do
      {
        byte [] data = nxtCommand.readFile((byte) 0,(size-received < 51 ? size-received : 51));
        received += data.length;
      
        out.write(data);

      } while (received < size);

      nxtCommand.closeFile((byte) 0);
      out.close();
    } catch (IOException ioe) {
    	showMessage("IOException downloading file");
    }
  }

  /**
   * Run a program on the NXT
   * 
   * @param fileName the program to run
   * 
   * @throws IOException
   */
  public void runProgram(String fileName) throws IOException {
    nxtCommand.startProgram(fileName);
  }
  
  /**
   * Show a pop-up message
   * 
   * @param msg the message
   */
  public void showMessage(String msg) {
	  JOptionPane.showMessageDialog(frame, msg);
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
		  //System.out.println("Row = " + row);
		  //System.out.println(nxtCommands[row]);
		  if (nxtCommands[row] != null && nxtCommands[row].isOpen()) {
			  nxtCommand = nxtCommands[row];
			  //System.out.println("Fetching files");
			  updateConnectButton(true);
			  showFiles();
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
	nxtTable.setModel(new NXTConnectionModel(null,0));
    int connected = conn.connectTo(nameText.getText(), null, 
                    getProtocols(), NXTComm.LCP, true);

    if (connected < 0) {
      showMessage("No NXTS found");
      return;
    }

    nxts = conn.getNXTInfos();
    nm = new NXTConnectionModel(nxts, nxts.length);
    nxtTable.setModel(nm);	           	
    nxtTable.setRowSelectionInterval(0, 0);
    nxtTable.getSelectionModel().addListSelectionListener(control);
    nxtCommands = new NXTCommand[nxts.length];
  }
  
  private void closeAll() {
	  if (nxtCommands == null) return;
	  for(int i=0;i<nxtCommands.length;i++) {
		  NXTCommand nc = nxtCommands[i];
		  if (nc != null) 
			  try {
				  nc.close();
			  } catch (IOException ioe) {}
	  }
  }
  
  private void updateConnectionStatus(int row, boolean connected) {
	  nm.setConnected(row, connected);
	  nxtTable.repaint();
	  updateConnectButton(connected);
	  if (!connected) nxtCommands[row] = null;
  }
  
  private void updateConnectButton(boolean connected) {
	  connectButton.setText((connected ? "Disconnect" : "Connect"));
  }
  
  private int getProtocols() {
	  int protocols = 0;
	  if (usbButton.isSelected()) protocols = NXTCommFactory.USB;
	  if (bluetoothButton.isSelected()) protocols = NXTCommFactory.BLUETOOTH;
	  if (bothButton.isSelected()) protocols = NXTCommFactory.USB | NXTCommFactory.BLUETOOTH;
	  return protocols;
  }
  
}

