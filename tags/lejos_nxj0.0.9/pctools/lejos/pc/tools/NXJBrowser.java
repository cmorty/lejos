package lejos.pc.tools;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 * 
 *  Graphical file browser for leJOS NXJ.
 *  Supports uploading,, downloading, and deleting files.
 *  Also supports running programs, defragging the file system
 *  and setting the name of the NXT.
 *
 *  @author Lawrie Griffiths <lawrie.griffiths@ntlworld.com>
 */
public class NXJBrowser
{
	public static final int MAX_FILES = 30;
	private NXTCommand nxtCommand;
	private Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
	private Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	private static String title = "NXJ File Browser";
	private JFrame frame;

	public static void main(String args[])
	{
		ToolStarter.startSwingTool(NXJBrowser.class, args);
	}
	
	public static int start(String[] args)
	{
		return new NXJBrowser().run(args);
	}
  
  private int run(String[] args)
  {
	NXJBrowserCommandLineParser fParser = new NXJBrowserCommandLineParser(NXJBrowser.class, "[options]");
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

    nxtCommand = NXTCommand.getSingleton();
    
    String name = AbstractCommandLineParser.getLastOptVal(commandLine, "n");
	boolean blueTooth = commandLine.hasOption("b");
	boolean usb = commandLine.hasOption("u");
	String address = AbstractCommandLineParser.getLastOptVal(commandLine, "d");
	
    int protocols = 0;
    
	if (blueTooth) protocols |= NXTCommFactory.BLUETOOTH;
	if (usb) protocols |= NXTCommFactory.USB;
	
	if (protocols == 0) protocols = NXTCommFactory.USB | NXTCommFactory.BLUETOOTH;
	
	NXTConnector conn = new NXTConnector();
	conn.addLogListener(new ToolsLogger());
	
	final NXTInfo[] nxts = conn.search(name, address, protocols);
	
    if (nxts.length == 0) {
        System.err.println("No NXT found - is it switched on and plugged in (for USB)?");
        return 1;
    }
    
    // Display a list of NXTs
    
    final NXTConnectionModel nm = new NXTConnectionModel(nxts, nxts.length);
    
    final JTable nxtTable = new JTable(nm);
    
    final JScrollPane nxtTablePane = new JScrollPane(nxtTable);
    
    nxtTable.setRowSelectionInterval(0, 0);
    
    frame = new JFrame(title);

    WindowListener listener = new WindowAdapter() {
      public void windowClosing(WindowEvent w) {
        try {
        	nxtCommand.close();
        } catch (IOException ioe) {}
        System.exit(0);
      }
    };
    frame.addWindowListener(listener);

    frame.getContentPane().add(nxtTablePane, BorderLayout.CENTER);
    
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
        	  } else showFiles(frame,nxts[row]);
          }
        }
      });

    JPanel buttonPanel = new JPanel();
    
    buttonPanel.add(connectButton);

    frame.getContentPane().add(new JScrollPane(buttonPanel), BorderLayout.SOUTH);

    frame.pack();
    frame.setVisible(true);
    return 0;
  }
  
  private void showFiles(final JFrame frame, NXTInfo nxt) {
	  
	try {
		frame.setTitle(title + " : " + nxtCommand.getFriendlyName());
	} catch (IOException ioe) {
		showMessage("IOException getting friendly name");
	}
	
    frame.getContentPane().removeAll();

    final ExtendedFileModel fm = new ExtendedFileModel();
    fm.fetchFiles(nxtCommand);
      
    final JTable table = new JTable(fm);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    TableColumnModel tcm = table.getColumnModel();
    tcm.getColumn(0).setPreferredWidth(450);

    final JScrollPane tablePane = new JScrollPane(table);
    tablePane.setPreferredSize(new Dimension(605, 500));

    frame.getContentPane().add(tablePane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();

    JButton deleteButton = new JButton("Delete Files");
    JButton uploadButton = new JButton("Upload file");
    JButton downloadButton = new JButton("Download file");
    JButton runButton = new JButton("Run program");
    JButton defragButton = new JButton("Defrag");
    JButton nameButton = new JButton("Set Name");
    
    buttonPanel.add(deleteButton);
    buttonPanel.add(uploadButton);
    buttonPanel.add(downloadButton);
    buttonPanel.add(runButton);
    buttonPanel.add(defragButton);
    buttonPanel.add(nameButton);

    frame.getContentPane().add(new JScrollPane(buttonPanel), BorderLayout.SOUTH);

    deleteButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
    	deleteFile(frame, fm);
      }
    });

    uploadButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {       
        uploadFile(frame, fm);
      }
    });
    
    downloadButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        int i = table.getSelectedRow();
        downloadFile(frame, fm, i);
      }
    });

    defragButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          defragFS(frame, fm);
        }
      });
    
    runButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
      	  int i = table.getSelectedRow();
      	  runFile(fm, i);
        }
      });
    
    nameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          changeName(frame);
        }
      });


    frame.pack();
    frame.setVisible(true);
  }


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
        //System.out.println("Received " + data.length + " bytes");
        received += data.length;
      
        out.write(data);

      } while (received < size);

      //System.out.println("Received " + received + " bytes");
      nxtCommand.closeFile((byte) 0);
      out.close();
    } catch (IOException ioe) {
    	showMessage("IOException downloading file");
    }
  }

  public void runProgram(String fileName) throws IOException {
    nxtCommand.startProgram(fileName);
  }
  
  public void showMessage(String msg) {
	  JOptionPane.showMessageDialog(frame, msg);
  }

private void deleteFile(final JFrame frame, final ExtendedFileModel fm) {
	frame.setCursor(hourglassCursor);
	
	try {
	    for(int i=0;i<fm.getRowCount();i++) {
	      Boolean b = (Boolean) fm.getValueAt(i,ExtendedFileModel.COL_DELETE);
	      String fileName = (String) fm.getValueAt(i,ExtendedFileModel.COL_NAME);
	      boolean deleteIt = b.booleanValue();
	      if (deleteIt) {
	        //System.out.println("Deleting " + fileName);
	        nxtCommand.delete(fileName); 
	      }
	    }
	    fm.fetchFiles(nxtCommand);
	} catch (IOException ioe) {
		showMessage("IOException deleting files");
	}
	frame.setCursor(normalCursor);
}

private void uploadFile(final JFrame frame, final ExtendedFileModel fm) {
	JFileChooser fc = new JFileChooser();

	int returnVal = fc.showOpenDialog(frame);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	  frame.setCursor(hourglassCursor);
	  try {
		  File file = fc.getSelectedFile();
		  if (file.getName().length() > 20) {
			  showMessage("File name is more than 20 characters");
		  } else {   	
	          nxtCommand.uploadFile(file, file.getName());
	          String s = fm.fetchFiles(nxtCommand);
	          if (s != null) throw new IOException();
		  }
	  } catch (IOException ioe) {
		  showMessage("IOException uploading file");
	  }
	  frame.setCursor(normalCursor);
	}
}

private void downloadFile(final JFrame frame, final ExtendedFileModel fm, int i) {
	if (i<0) return;
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

private void defragFS(final JFrame frame, final ExtendedFileModel fm) {
	frame.setCursor(hourglassCursor);
	  try {
		  nxtCommand.defrag();
	      String s = fm.fetchFiles(nxtCommand);
	      if (s != null) throw new IOException();
	  } catch (IOException ioe) {
		  showMessage("IOException during defrag");
	  }
	  frame.setCursor(normalCursor);
}

private void runFile(final ExtendedFileModel fm, int i) {
	if (i<0) return;
	  String fileName = fm.getFile(i).fileName;
	  try {
		  runProgram(fileName);
		  System.exit(0);
	  } catch (IOException ioe) {
		  showMessage("IOException running program");
	  }
}

private void changeName(final JFrame frame) {
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
}


