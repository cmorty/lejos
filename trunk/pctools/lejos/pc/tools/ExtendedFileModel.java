package lejos.pc.tools;

import java.io.IOException;
import lejos.nxt.remote.*;
import javax.swing.table.AbstractTableModel;

/**
 * Swing Table Model for manipulating NXT files.
 * 
 * @author Lawrie Griffiths
 */
public class ExtendedFileModel extends AbstractTableModel {
  private static final long serialVersionUID = -6173853132812064498L;
  private static final String[] columnNames = {"File","Size", "Start Page", "End Page", "Delete"};
  private static final int NUM_COLUMNS = 5;
  public static final int MAX_FILES = 30;

  private Object[][] fileData;
  private int numFiles;
  private FileInfo[] files = new FileInfo[MAX_FILES];
  private NXTCommand nxtCommand;

  /**
   * Fetch files from the NXT and create the model
   * 
   * @param nxtCommand used to send LCP commands to the NXT
   */
  public ExtendedFileModel(NXTCommand nxtCommand) {
	this.nxtCommand = nxtCommand;
	fetchFiles();
    setData(files, numFiles);
  }

  private void setData(FileInfo[] files, int numFiles) {
    this.numFiles = numFiles;

    fileData = new Object[30][NUM_COLUMNS];

    for(int i=0;i<numFiles;i++) {
      fileData[i][0]  = files[i].fileName;
      fileData[i][1] = new Integer(files[i].fileSize);
      fileData[i][2] = new Integer(files[i].startPage);
      fileData[i][3] = new Integer(files[i].startPage + ((files[i].fileSize -1)/256));
      fileData[i][4] = new Boolean(false);

     }
  }

  /**
   * Delete a file on the NXT and update the model
   * 
   * @param fileName the file to delete
   * @param row the row in the file model
   * 
   * @throws IOException
   */
  public void delete(String fileName, int row) throws IOException {
	nxtCommand.delete(fileName); 
    for(int i=row;i<numFiles-1;i++) {
      fileData[i] = fileData[i+1];
    }
    numFiles--;
  }

  /**
   * Get the number of rows in the model
   * 
   * @return the number of files in the model
   */
  public int getRowCount() {
    return numFiles;
  }

  /**
   * Get the number of columns in the mode
   * 
   * @return the column count
   */
  public int getColumnCount() {
    return NUM_COLUMNS;
  }

  /**
   * Get the object at the specified location
   * 
   * @return the object at the specified location
   */
  public Object getValueAt(int row, int column) {
    return fileData[row][column];
  }

  /**
   * Set the value of a cell
   */
  public void setValueAt(Object value, int row, int column) {
    fileData[row][column] = value;
  }

  /**
   * Get the name of a column
   * 
   * @return the column name
   */
  public String getColumnName(int column) {
    return columnNames[column];
  }

  /**
   * Get the class of a specific column
   * 
   * @return the class of the column
   */
  public Class<?> getColumnClass(int column) {
    return fileData[0][column].getClass();
  }

  /**
   * Check if a cell is editable
   * @return true iff the cell is editable
   */
  public boolean isCellEditable(int row, int column) {
    return (column == 4);
  }
  
  /**
   * Fetch the files from the NXT
   * 
   * @return null for success or the error message
   */
  public String fetchFiles() {
	numFiles = 0;
    try {
      files[0] = nxtCommand.findFirstNXJ("*.*");
	
	  if (files[0] != null) {
	    numFiles = 1;
	
	    for(int i=1;i<MAX_FILES;i++) {
	      files[i] = nxtCommand.findNextNXJ(files[i-1].fileHandle);
	      if (files[i] == null) break;
	      else {
	        numFiles++;
	      }
	    }
	  }
	  setData(files,numFiles);
	  return null;
    } catch (IOException ioe) {
    	return "IOException fetching files";
    }
  }
  
  /**
   * Get the FileInfo object for a specific file
   * 
   * @param i the row number of the file
   * @return the FileInfo object
   */
  public FileInfo getFile(int i) {
	  return files[i];
  }
  
  /**
   * Return the number of files
   * 
   * @return the number of files
   */
  public int numFiles() {
	  return numFiles;
  }
}

