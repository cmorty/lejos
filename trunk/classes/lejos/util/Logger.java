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
        int datatype=DT_FLOAT; //default is float
        boolean chartSeries=true; // true = display on chart
        int rangeAxisID = 0; // zero-based ID of range axis for multi-axis charting. limit to 4 axes
    }
    
    // Starts realtime logging. Must be called before any writeLog() methods. Resets startCachingLog() state
    void startRealtimeLog(DataOutputStream out, DataInputStream in) throws IOException; // streams must be valid (not null)
    void startRealtimeLog(NXTConnection connection) throws IOException; // isConnected()=true and streams must be valid (not null)
    // once closed, dos/dis cannot be reused. startRealtimeLog() must be called again.
    void stopRealtimeLog(); 
    
    // Sets caching (deferred) logging. Default mode at instantiation and will be called on first writeLog() method if not 
    // explicitly called. Resets startRealtimeLog() state.
    // Init for logging to cache for deferred transmit using sendCache()
    // Resets startRealtimeLog() state
    void startCachingLog(); // default
    // Sends log cache. Valid only for caching (deferred) logging using startCachingLog().
    void sendCache(DataOutputStream out, DataInputStream in) throws IOException; // only if loggingMode=cached
    void sendCache(NXTConnection connection) throws IOException; // only if loggingMode=cached
    
    // sets the header names, datatypes, count, chartable attribute, range axis ID (for multiple axis charting)
    // This is mandatory and implies a new log structure when called
    // throws IllegalArgumentException if bad datatype val
    void setColumns(ColumnDefinition[] columnDefs) throws IllegalArgumentException; 
  
    // All of these throw unchecked IllegalStateException if datatypes don't match what was set in setColumns() or 
    // column counts don't match what was set in setColumns()
    void writeLog(boolean datapoint); 
    void writeLog(byte datapoint);
    void writeLog(short datapoint);
    void writeLog(int datapoint);
    void writeLog(long datapoint);
    void writeLog(float datapoint);
    void writeLog(double datapoint);
    
    // called to end each new line of log data. Logged values count per row must match rowcount/datatype set in setColumns() or
    // IllegalStateException is thrown. 
    void finishLine() throws IllegalStateException; 
    
    
    
}
