package lejos.pc.tools;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.table.AbstractTableModel;
import lejos.pc.comm.*;

public class ExtendedFileModel extends AbstractTableModel {
  private static final long serialVersionUID = -6173853132812064498L;
  private static final String[] columnNames = {"File","Size", "Start Page", "End Page", "Delete"};
  private static final int NUM_COLUMNS = 5;
  public static final int MAX_FILES = 30;

  private Object[][] fileData;
  private int numFiles;
  private FileInfo[] files = new FileInfo[MAX_FILES];
  private NXTCommand nxtCommand;

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

  public void delete(String fileName, int row) throws IOException {
	nxtCommand.delete(fileName); 
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

  public Class<?> getColumnClass(int column) {
    return fileData[0][column].getClass();
  }

  public boolean isCellEditable(int row, int column) {
    return (column == 4);
  }
  
  public String fetchFiles() {
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
	  setData(files,numFiles);
	  return null;
    } catch (IOException ioe) {
    	return "IOException fetching files";
    }
  }
  
  public FileInfo getFile(int i) {
	  return files[i];
  }
  
  public int numFiles() {
	  return numFiles;
  }
}

