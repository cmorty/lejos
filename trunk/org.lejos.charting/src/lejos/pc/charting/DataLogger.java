package lejos.pc.charting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class provides the PC side of the <code>lejos.util.NXTDataLogger</code> counterpart running on the NXT. 
 * One instance per log session. The session ends when the NXT ends the connection.
 * 
 * @see LoggerComms
 * @see lejos.util.NXTDataLogger 
 * @author Kirk P. Thompson
 */
public class DataLogger {
    private final String THISCLASS;
    private File logFile = null;
    private FileWriter fw;
    private boolean fileAppend;
    private DataLoggerCallback loggerCallback;
    private LoggerProtocolManager lpm;
    
    /** Internal Logger callback implementation to manage the logging events used to save the data
     */
    private class DataLoggerCallback implements LoggerListener{
        public void logLineAvailable(DataItem[] logDataItems){
            try {
                DataLogger.this.fw.write(LoggerProtocolManager.parseLogData(logDataItems));
            } catch (IOException e) {
                System.out.print("!** logLineAvailableEvent IOException");
                e.printStackTrace();
            }
        }

        public void dataInputStreamEOF() {
            dbg("!** dataInputStreamEOF from NXT");
            try {
                if (DataLogger.this.fw!=null) DataLogger.this.fw.close();
                DataLogger.this.fw=null;
            } catch (IOException e) {
                System.out.print("!** dataInputStreamEOF IOException in fw.close()");
                e.printStackTrace();
            }
            lpm.removeLoggerListener(loggerCallback);
        }

        public void logFieldNamesChanged(String[] logFields) {
            StringBuilder sb = new StringBuilder();
            String[] tempFields;
//            dbg("!** New headers");
            for (int i=0;i<logFields.length;i++) {
                tempFields=logFields[i].split("!");
                sb.append(tempFields[0]);
                if (i<logFields.length-1) sb.append("\t");
            }
            sb.append("\n");
            // write to file
            try {
                DataLogger.this.fw.write(sb.toString());
            } catch (IOException e) {
                System.out.print("!** logFieldNamesChanged IOException: sb.toString()=\"" + sb.toString() + "\"");
                e.printStackTrace();
            }
        }

        public void logCommentReceived(int timestamp, String comment) {
            String theComment=String.format("%1$1d\t%2$s\n", timestamp, comment);
            try {
                DataLogger.this.fw.write(theComment);
            } catch (IOException e) {
                System.out.print("!** logCommentRecieved IOException: theComment=\"" + theComment + "\"");
                e.printStackTrace();
            }
        }
    }
    
    /**Create a <code>DataLogger</code> instance. If valid, the passed passed <code>logfile</code> is opened and the 
     * logging output is written  to it.
     * <p>
     * This class registers an internal <code>LoggerListener</code> implementation to receive the logging events from
     * <code>LoggerProtocolManager</code>.
     * 
     * @param lpm The <code>LoggerProtocolManager</code> instance that is managing the NXT communication
     * @param logFile The log file <code>File</code> to write output to. 
     * @param fileAppend If <code>false</code>, the specified file will be overwritten if exists. 
     * @see LoggerComms
     */
    public DataLogger(LoggerProtocolManager lpm, File logFile, boolean fileAppend)  {
        String FQPfileName=null;
        
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
        
        this.fileAppend=fileAppend;
        this.logFile = logFile;
        // if not a valid file, return without registering
        if (!(this.logFile!=null&&!this.logFile.isDirectory())) return;
        try {
            FQPfileName = this.logFile.getCanonicalPath();
            dbg("log file is:" + FQPfileName);
            if (!this.logFile.exists())
                this.logFile.createNewFile();
            this.fw = new FileWriter(this.logFile, this.fileAppend);
        } catch (IOException e) {
            dbg("startLogging(): IOException in creating file " + FQPfileName + ": " + e.toString());
            return;
        }
                
        this.lpm=lpm;
        loggerCallback = new DataLoggerCallback();
        this.lpm.addLoggerListener(loggerCallback);
    }

    private void dbg(String msg){
        System.out.println(THISCLASS + "-" + msg);
    }
}
