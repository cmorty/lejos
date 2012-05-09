package lejos.pc.tools;
import lejos.pc.comm.*;
import javax.swing.table.AbstractTableModel;

/**
 * A table model for PC GUI programs.
 * This is used by NXJBrowser and NXJMonitor to allow the user to choose
 * which NXT to connect to.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NXTConnectionModel extends AbstractTableModel {
  private static final long serialVersionUID = 3540880662561527501L;
  private static final String[] columnNames = {"Name","Protocol", "Address","Status"};
  private static final int NUM_COLUMNS = 4;
	
  private Object[][] nxtData;
  private int numNXTs;

  /**
   * Create the model from an array of NXTInfo.
   * 
   * @param nxts the NXTInfo array
   * @param numNXTs the number of NXTs in the array
   */
  public NXTConnectionModel(NXTInfo[] nxts, int numNXTs) {
    setData(nxts, numNXTs);
  }

  /**
   * Update the data in the model.
   * 
   * @param nxts the NXTInfo array
   * @param numNXTs the number of NXTs
   */
  public void setData(NXTInfo[] nxts, int numNXTs) {
    this.numNXTs = numNXTs;
    nxtData = new Object[numNXTs][NUM_COLUMNS];

    for(int i=0;i<numNXTs;i++) {
      nxtData[i][0]  = nxts[i].name;
      nxtData[i][1] = (nxts[i].protocol == NXTCommFactory.USB ? "USB" : "Bluetooth");
      nxtData[i][2] = (nxts[i].deviceAddress == null ? "" : nxts[i].deviceAddress);
      nxtData[i][3] = NXTConnectionState.DISCONNECTED;
    }
  }
  
  public void setConnected(int row, NXTConnectionState state) {
	  nxtData[row][3] = state;
  }
 

  /**
   * Return the number of rows
   * @return the number of rows
   */
  public int getRowCount() {
    return numNXTs;
  }

  /**
   * Return the number of columns
   * @return the number of columns
   */
  public int getColumnCount() {
    return NUM_COLUMNS;
  }

  /**
   * Get the data in a specific cell
   * @return the Object from the specific cell
   */
  public Object getValueAt(int row, int column) {
    return nxtData[row][column];
  }

  /**
   * Get the column name
   * @param column the column index
   * @return the column name
   */
  public String getColumnName(int column) {
    return columnNames[column];
  }

  /** 
   * Get the class of the object held in the column cells
   * @param column the column index
   * @return the class
   */
  public Class<?> getColumnClass(int column) {
    return nxtData[0][column].getClass();
  }
}


