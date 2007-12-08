package lejos.pc.tools;

import javax.swing.JFrame;
import javax.swing.table.AbstractTableModel;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class NXTTableModel extends AbstractTableModel {
  private static final String[] columnNames = {"Name","Protocol", "Address"};
  private static final int NUM_COLUMNS = 3;

  Object[][] nxtData;
  int numNXTs;
  JFrame frame;

  public NXTTableModel(JFrame frame, NXTInfo[] nxts, int numNXTs) {
    this.frame = frame;
    setData(nxts, numNXTs);
  }

  public void setData(NXTInfo[] nxts, int numNXTs) {
    this.numNXTs = numNXTs;
    nxtData = new Object[numNXTs][NUM_COLUMNS];

    for(int i=0;i<numNXTs;i++) {
      nxtData[i][0]  = nxts[i].name;
      nxtData[i][1] = (nxts[i].protocol == NXTCommFactory.USB ? "USB" : "Bluetooth");
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


