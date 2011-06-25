package lejos.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.NXTConnection;

// instantiation implies the start of a log
public interface Logger {
    static final int    DT_BOOLEAN = 0;
    static final int    DT_BYTE    = 1;
    static final int    DT_SHORT   = 2;
    static final int    DT_INTEGER = 3;        
    static final int    DT_LONG    = 4;
    static final int    DT_FLOAT   = 5;
    static final int    DT_DOUBLE  = 6;
    
    // or Enum..? A column/series definition struct
    class ColumnDefinition{
        String name="series";
        int datatype=DT_DOUBLE; //default is double
        boolean chartSeries=true; // true = display on chart
        int rangeAxisID = 0; // ID of range axis for multi-axis charting
    }
    
    // will ensure setCacheMode set to false
    void startRealtimeLog(DataOutputStream out, DataInputStream in) throws IOException; // streams must be valid (not null)
    void startRealtimeLog(NXTConnection connection) throws IOException; // streams must be valid (not null)
    
    // when called, init baseline for whatever mode
    void setCacheMode(boolean cacheMode); // false=realtime, true=cached (default)
    
    // sets the header names, datatypes, count, chartable attribute, range axis ID (for multiple axis charting)
    // This is mandatory and implies a new log when called
    void setColumns(ColumnDefinition[] columns) throws IllegalArgumentException; // throws IllegalArgumentException if bad datatype val
  
    // All of these throw unchecked IllegalStateException if datatypes don't match what was set in setColumns() or 
    // column counts don't match what was set in setColumns()
    void writeLog(boolean datapoint); 
    void writeLog(byte datapoint);
    void writeLog(short datapoint);
    void writeLog(int datapoint);
    void writeLog(long datapoint);
    void writeLog(float datapoint);
    void writeLog(double datapoint);
    
    // called to start each new line of log data. Logged values count per row must match rowcount/datatype set in setColumns() or
    // IllegalStateException is thrown. Does [implicit] timestamp column. finishLine() is implied at next startLine() call
    void startLine() throws IllegalStateException; 
    void sendCache(DataOutputStream out, DataInputStream in) throws IOException; // only if loggingMode=cached
    void sendCache(NXTConnection connection) throws IOException; // only if loggingMode=cached
    void closeLog(); // once closed, dos/dis cannot be reused. startRealtimeLog() must be called again.
}
