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
    NXTInfo[] nxts = nxtCommand.search(null, NXTCommand.BLUETOOTH);
    if (nxts.length == 0) {
      System.out.println("No NXT found");
      System.exit(1);
    }
    nxtCommand.open(nxts[0]);
    nxtCommand.setVerify(true);

    fetchFiles();

    final FileModel fm = new FileModel(frame, files, numFiles);
      
    final JTable table = new JTable(fm);

    TableCellRenderer defaultRenderer = table.getDefaultRenderer(JButton.class);
    table.setDefaultRenderer(JButton.class,
			       new JTableButtonRenderer(defaultRenderer));

    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    TableColumn col = table.getColumnModel().getColumn(0);
    col.setPreferredWidth(300);
    col = table.getColumnModel().getColumn(3);
    col.setPreferredWidth(100);

    table.addMouseListener(new JTableButtonMouseListener(table));

    final JScrollPane tablePane = new JScrollPane(table);
    tablePane.setPreferredSize(new Dimension(640, 500));

    frame.getContentPane().add(tablePane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();

    JButton deleteButton = new JButton("Delete Files");
    JButton uploadButton = new JButton("Upload file");

    buttonPanel.add(deleteButton);
    buttonPanel.add(uploadButton);

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
        if (returnVal == 0) {
          File file = fc.getSelectedFile();
          sendFile(file);
          fetchFiles();
          fm.setData(files, numFiles);
          table.invalidate();
          tablePane.revalidate(); 
        }
      }
    });

    frame.pack();
    frame.setVisible(true);
  }

  private static void sendFile(File file) {
    byte[] data = new byte[60];
    int len, sent = 0;
    FileInputStream in = null;
    int index;

    //System.out.println("Filename is " + file.getName());

    try {
      in = new FileInputStream(file);
    } catch (FileNotFoundException e) {}

    nxtCommand.openWrite(file.getName(), 0); // File size not currently used

    try {
      while ((len = in.read(data)) > 0) {
        byte[] sendData = new byte[len];
        for(int i=0;i<len;i++) sendData[i] = data[i];
        // System.out.println("Sending " + len + " bytes");
        sent += len;
        nxtCommand.writeFile((byte) 0,sendData); // Handles not yet used
      }
    } catch (IOException ioe) {}
    //System.out.println("Sent " + sent + " bytes");
    nxtCommand.closeFile((byte) 0);
  }

  private static void fetchFiles() {
    files[0] = nxtCommand.findFirst("*.*");
    System.out.println(files[0].fileName);

    if (files[0] != null) {
      numFiles = 1;

      for(int i=1;i<MAX_FILES;i++) {
        files[i] = nxtCommand.findNext(files[i-1].fileHandle);
        if (files[i] == null) break;
        else {
          System.out.println(files[i].fileName);
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
        byte [] data = nxtCommand.readFile((byte) 0,51);
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
  private static final int NUM_COLUMNS = 5;

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
      fileData[i][3] = new JButton("Download");
      fileData[i][4] = new JButton("Run");

      JButton downloadButton = (JButton) fileData[i][3];
      JButton runButton = (JButton) fileData[i][4];

      final int row = i;
      final String fileName = files[i].fileName;
      final int size = files[i].fileSize;

      downloadButton.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          JFileChooser fc = new JFileChooser();
	  int returnVal = fc.showSaveDialog(frame);
          if (returnVal == 0) {
            File file = fc.getSelectedFile();
            NXJBrowser.getFile(file, fileName, size);
          }
        }
      });

      runButton.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          NXJBrowser.runProgram(fileName);
        }
      });
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

class JTableButtonRenderer implements TableCellRenderer {
  private TableCellRenderer __defaultRenderer;

  public JTableButtonRenderer(TableCellRenderer renderer) {
    __defaultRenderer = renderer;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
						 boolean isSelected,
						 boolean hasFocus,
						 int row, int column)
  {
    if(value instanceof Component)
      return (Component)value;
    return __defaultRenderer.getTableCellRendererComponent(
	   table, value, isSelected, hasFocus, row, column);
  }
}

class JTableButtonMouseListener implements MouseListener {
  private JTable __table;

  private void __forwardEventToButton(MouseEvent e) {
    TableColumnModel columnModel = __table.getColumnModel();
    int column = columnModel.getColumnIndexAtX(e.getX());
    int row    = e.getY() / __table.getRowHeight();
    Object value;
    JButton button;
    MouseEvent buttonEvent;

    if(row >= __table.getRowCount() || row < 0 ||
       column >= __table.getColumnCount() || column < 0)
      return;

    value = __table.getValueAt(row, column);

    if(!(value instanceof JButton))
      return;

    button = (JButton)value;

    buttonEvent =
      (MouseEvent)SwingUtilities.convertMouseEvent(__table, e, button);
    button.dispatchEvent(buttonEvent);
    // This is necessary so that when a button is pressed and released
    // it gets rendered properly.  Otherwise, the button may still appear
    // pressed down when it has been released.
    __table.repaint();
  }

  public JTableButtonMouseListener(JTable table) {
    __table = table;
  }

  public void mouseClicked(MouseEvent e) {
    __forwardEventToButton(e);
  }

  public void mouseEntered(MouseEvent e) {
    __forwardEventToButton(e);
  }

  public void mouseExited(MouseEvent e) {
    __forwardEventToButton(e);
  }

  public void mousePressed(MouseEvent e) {
    __forwardEventToButton(e);
  }

  public void mouseReleased(MouseEvent e) {
    __forwardEventToButton(e);
  }
}



    

  

