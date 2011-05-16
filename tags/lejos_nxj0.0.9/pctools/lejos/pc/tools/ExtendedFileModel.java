package lejos.pc.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import lejos.nxt.remote.*;

import javax.swing.table.AbstractTableModel;

/**
 * Swing Table Model for manipulating NXT files.
 * 
 * @author Lawrie Griffiths
 */
public class ExtendedFileModel extends AbstractTableModel {
  private static final long serialVersionUID = -6173853132812064498L;
  private static final String[] columnNames = {"File", "Size", "Delete"};
  private static final int NUM_COLUMNS = 3;
  public static final int MAX_FILES = 30;
  public static final int COL_NAME = 0;
  public static final int COL_SIZE = 1;
  public static final int COL_DELETE = 2;

  private ArrayList<Boolean> delete = new ArrayList<Boolean>();
  private ArrayList<FileInfo> files = new ArrayList<FileInfo>();

  /**
   * Fetch files from the NXT and create the model
   * 
   * @param nxtCommand used to send LCP commands to the NXT
   */
  public ExtendedFileModel() {
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
	files.remove(row);
	delete.remove(row);
	this.fireTableRowsDeleted(row, row);
  }

  /**
   * Get the number of rows in the model
   * 
   * @return the number of files in the model
   */
  public int getRowCount() {
    return files.size();
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
		FileInfo f = files.get(row);
		switch (column)
		{
		case COL_NAME:
			return f.fileName;
		case COL_SIZE:
			return Integer.valueOf(f.fileSize);
		case COL_DELETE:
			return delete.get(row);
		default:
			throw new RuntimeException("unknown column");
		}
  }

  /**
   * Set the value of a cell
   */
  public void setValueAt(Object value, int row, int column) {
	  if (column != COL_DELETE)
		  throw new RuntimeException("invalid column");
	  
	  delete.set(row, (Boolean)value);
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
	  switch (column)
	  {
	  case COL_NAME:
		  return String.class;
	  case COL_SIZE:
		  return Integer.class;
	  case COL_DELETE:
		  return Boolean.class;
	  default:
		  throw new RuntimeException("unknown column");
	  }
  }

  /**
   * Check if a cell is editable
   * @return true iff the cell is editable
   */
  public boolean isCellEditable(int row, int column) {
    return (column == COL_DELETE);
  }
  
  /**
   * Fetch the files from the NXT
   * 
   * @return null for success or the error message
   */
  public String fetchFiles(NXTCommand nxtCommand) {
	  files.clear();
	  delete.clear();
    try {
      FileInfo f = nxtCommand.findFirst("*.*");	
	  while (f != null)
	  {
		  files.add(f);
		  delete.add(Boolean.FALSE);
		  
		  f = nxtCommand.findNext(f.fileHandle);
	  }
	  
	  Collections.sort(files, new Comparator<FileInfo>() {
		public int compare(FileInfo o1, FileInfo o2) {
			return o1.fileName.compareTo(o2.fileName);
		}
	  });
	  
	  this.fireTableDataChanged();
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
	  return files.get(i);
  }
  
  /**
   * Return the number of files
   * 
   * @return the number of files
   */
  public int numFiles() {
	  return files.size();
  }
  
  /**
   * Return the row for a given file
   * @param fileName the filename
   * @return the row of -1 if not found
   */
  public int getRow(String fileName) {
	int len = files.size();
	for (int i = 0; i < len; i++)
		if (fileName.equals(files.get(i).fileName))
			return i;
			
	return -1;
  }
}

