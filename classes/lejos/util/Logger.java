package lejos.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.NXTConnection;

/** Defines the [minimum] required functionality for the data logger
 */
public interface Logger {
    // Starts realtime logging. Must be called before any writeLog() methods. Resets startCachingLog() state
    void startRealtimeLog(DataOutputStream out, DataInputStream in) throws IOException; // streams must be valid (not null)
    void startRealtimeLog(NXTConnection connection) throws IOException; // isConnected()=true and streams must be valid (not null)
    
    // flush any buffered bytes. throws unchecked IllegalStateException if startRealtimeLog() has not been called
    //void stopRealtimeLog();
    
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
    void setColumns(LogColumn[] columnDefs) throws IllegalArgumentException; 
  
    // All of these throw unchecked IllegalStateException if datatypes don't match what was set in setColumns(), 
    // column counts don't match what was set in setColumns(), or startxxxLog() has not been called
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
    
    // once closed, dos/dis cannot be reused. startRealtimeLog() must be called again.
    void stopLogging(); 
    
}
