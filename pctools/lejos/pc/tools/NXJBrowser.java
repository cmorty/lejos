package lejos.pc.tools;

import lejos.pc.comm.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.io.*;

/**
 *  Thus is a PC example that uses iCommand and the BTRespond example running on the NXT
 *  to implement a Bluetooth file browser for leJOS NXJ.
 *
 *  @author Lawrie Griffiths <lawrie.griffiths@ntlworld.com>
 */
public class NXJBrowser {
  public static final int MAX_FILES = 30;
  static int numFiles;
  static FileInfo[] files= new FileInfo[MAX_FILES];
  static NXTCommand nxtCommand;

  public static void main(String args[]) {

    final JFrame frame = new JFrame("NXJ File Browser");

    WindowListener listener = new WindowAdapter() {
      public void windowClosing(WindowEvent w) {
        nxtCommand.close();
        System.exit(0);
      }
    };
    frame.addWindowListener(listener);

    nxtCommand = NXTCommand.getSingleton();
    
    final NXTInfo[] nxts = nxtCommand.search(null, NXTCommand.USB | NXTCommand.BLUETOOTH);
    
    if (nxts.length == 0) {
      System.out.println("No NXT found");
      System.exit(1);
    }
    
    final NXTModel nm = new NXTModel(frame, nxts, nxts.length);
    
    final JTable nxtTable = new JTable(nm);
    
    final JScrollPane nxtTablePane = new JScrollPane(nxtTable);
    
    nxtTable.setRowSelectionInterval(0, 0);
    
    frame.getContentPane().add(nxtTablePane, BorderLayout.CENTER);
    
    JButton connectButton = new JButton("Connect");
    
    connectButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          int row = nxtTable.getSelectedRow();
          if (row >= 0) showFiles(frame,nxts[row]);
          
        }
      });

    JPanel buttonPanel = new JPanel();
    
    buttonPanel.add(connectButton);

    frame.getContentPane().add(new JScrollPane(buttonPanel), BorderLayout.SOUTH);

    frame.pack();
    frame.setVisible(true);
  }
  
  private static void showFiles(final JFrame frame, NXTInfo nxt) {
    nxtCommand.open(nxt);

    frame.getContentPane().removeAll();
    
    fetchFiles();

    final FileModel fm = new FileModel(frame, files, numFiles);
      
    final JTable table = new JTable(fm);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    TableColumn col = table.getColumnModel().getColumn(0);
    col.setPreferredWidth(300);

    final JScrollPane tablePane = new JScrollPane(table);
    tablePane.setPreferredSize(new Dimension(450, 500));

    frame.getContentPane().add(tablePane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();

    JButton deleteButton = new JButton("Delete Files");
    JButton uploadButton = new JButton("Upload file");
    JButton downloadButton = new JButton("Download file");
    JButton runButton = new JButton("Run program");
    
    buttonPanel.add(deleteButton);
    buttonPanel.add(uploadButton);
    buttonPanel.add(downloadButton);
    buttonPanel.add(runButton);

    frame.getContentPane().add(new JScrollPane(buttonPanel), BorderLayout.SOUTH);

    deleteButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        
        for(int i=0;i<fm.getRowCount();i++) {
          Boolean b = (Boolean) fm.getValueAt(i,2);
          boolean deleteIt = b.booleanValue();
          String fileName = (String) fm.getValueAt(i,0);
          if (deleteIt) {
            //System.out.println("Deleting " + fileName);
            nxtCommand.delete(fileName); 
            fm.delete(i);
            numFiles--;
            i--;
            table.invalidate();
            tablePane.revalidate();         
          }
        }
      }
    });

    uploadButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {       
        JFileChooser fc = new JFileChooser();

        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file = fc.getSelectedFile();
          SendFile.sendFile(nxtCommand, file);
          fetchFiles();
          fm.setData(files, numFiles);
          table.invalidate();
          tablePane.revalidate(); 
        } else {
        	//System.out.println("returnVal = " + returnVal);
        }
      }
    });
    
    downloadButton.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
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
          NXJBrowser.getFile(file, fileName, size);
        }
      }
    });

    runButton.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
    	int i = table.getSelectedRow();
    	if (i<0) return;
        String fileName = files[i].fileName;
        NXJBrowser.runProgram(fileName);
        System.exit(0);
      }
    });

    frame.pack();
    frame.setVisible(true);
  }

  private static void fetchFiles() {
    files[0] = nxtCommand.findFirst("*.*");
    //System.out.println(files[0].fileName);

    if (files[0] != null) {
      numFiles = 1;

      for(int i=1;i<MAX_FILES;i++) {
        files[i] = nxtCommand.findNext(files[i-1].fileHandle);
        if (files[i] == null) break;
        else {
          //System.out.println(files[i].fileName);
          numFiles++;
        }
      }
    } else numFiles = 0;
  }

  public static void getFile(File file, String fileName, int size) {
    FileOutputStream out = null; 
    int received = 0;

    try {
      out = new FileOutputStream(file);
    } catch (FileNotFoundException e) {}

    nxtCommand.openRead(fileName);

    try {
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
    } catch (IOException ioe) {}
  }

  public static void runProgram(String fileName) {
    nxtCommand.startProgram(fileName);
  }
}


class FileModel extends AbstractTableModel {
  private static final String[] columnNames = {"File","Size", "Delete", "Download", "Run"};
  private static final int NUM_COLUMNS = 3;

  Object[][] fileData;
  int numFiles;
  JFrame frame;

  public FileModel(JFrame frame, FileInfo[] files, int numFiles) {
    this.frame = frame;
    setData(files, numFiles);
  }

  public void setData(FileInfo[] files, int numFiles) {
    this.numFiles = numFiles;

    fileData = new Object[30][NUM_COLUMNS];

    for(int i=0;i<numFiles;i++) {
      fileData[i][0]  = files[i].fileName;
      fileData[i][1] = new Integer(files[i].fileSize);
      fileData[i][2] = new Boolean(false);

     }
  }

  public void delete(int row) {
    for(int i=row;i<numFiles-1;i++) {
      fileData[i] = fileData[i+1];
    }
    numFiles--;
  }

  public int getRowCount() {
    return numFiles;
  }

  public int getColumnCount() {
    return NUM_COLUMNS;
  }

  public Object getValueAt(int row, int column) {
    return fileData[row][column];
  }

  public void setValueAt(Object value, int row, int column) {
    fileData[row][column] = value;
  }

  public String getColumnName(int column) {
    return columnNames[column];
  }

  public Class getColumnClass(int column) {
    return fileData[0][column].getClass();
  }

  public boolean isCellEditable(int row, int column) {
    return (column == 2);
  }
}

class NXTModel extends AbstractTableModel {
  private static final String[] columnNames = {"Name","Protocol", "Address"};
  private static final int NUM_COLUMNS = 3;

  Object[][] nxtData;
  int numNXTs;
  JFrame frame;

  public NXTModel(JFrame frame, NXTInfo[] nxts, int numNXTs) {
    this.frame = frame;
    setData(nxts, numNXTs);
  }

  public void setData(NXTInfo[] nxts, int numNXTs) {
    this.numNXTs = numNXTs;
    System.out.println("Number of NXTS is " + numNXTs);
    nxtData = new Object[numNXTs][NUM_COLUMNS];

    for(int i=0;i<numNXTs;i++) {
      nxtData[i][0]  = nxts[i].name;
      nxtData[i][1] = (nxts[i].protocol == NXTCommand.USB ? "USB" : "Bluetooth");
      nxtData[i][2] = (nxts[i].btDeviceAddress == null ? "" : nxts[i].btDeviceAddress);
    }
  }

  public int getRowCount() {
    return numNXTs;
  }

  public int getColumnCount() {
    return NUM_COLUMNS;
  }

  public Object getValueAt(int row, int column) {
    return nxtData[row][column];
  }

  public String getColumnName(int column) {
    return columnNames[column];
  }

  public Class getColumnClass(int column) {
    return nxtData[0][column].getClass();
  }
}

