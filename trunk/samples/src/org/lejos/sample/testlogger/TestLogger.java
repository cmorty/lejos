package org.lejos.sample.testlogger;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;
import lejos.util.Delay;
import lejos.util.LogColumn;
import lejos.util.Logger;
import lejos.util.NXTDataLogger;


public class TestLogger {
    /** Use USB for the connection
     */
    private static final int CONN_USB = 1;
    /** Use Bluetooth for the connection
     */
    private static final int CONN_BLUETOOTH = 2;
    Logger dlog = new NXTDataLogger();
 
    public static void main(String[] args) {
        TestLogger testLogger = new TestLogger();
        testLogger.doTests();
    }
    
    private void doIOError() {
        System.out.println("IOException!");
        System.out.println("quitting..");
        Delay.msDelay(4000);
        System.exit(-1);
    }
    
    private void doTests(){
        System.out.println("Realtime mode");
        System.out.println("Press key");
        Button.waitForAnyPress();
        LCD.clear();
        NXTConnection theConnection = waitForConnection(15000,CONN_BLUETOOTH); 
        if (theConnection==null) return; 
        
        LCD.clear();
        System.out.println("sending data...");
        doRealtimeTest(theConnection);
        System.out.println("Complete!");
        System.out.println("Press key");
        Button.waitForAnyPress();
        LCD.clear();
        System.out.println("Cache mode");
        doCachedTest(theConnection);
        dlog.stopLogging();
        LCD.clear();
        System.out.println("Complete!");
        System.out.println("Press key");
        Button.waitForAnyPress();
    }
    
    private void doRealtimeTest(NXTConnection conn){
        double value=0;
        DataOutputStream dos = conn.openDataOutputStream();
        try {
            // shows use of constructor with dos, dis params
            this.dlog.startRealtimeLog(dos, conn.openDataInputStream());
        } catch (IOException e) {
            doIOError();
        }
        
        this.dlog.setColumns(new LogColumn[] {
            new LogColumn("sine(v)", LogColumn.DT_FLOAT),
            new LogColumn("iterator", LogColumn.DT_INTEGER, 2), // use different range axis (2)
            new LogColumn("Random", LogColumn.DT_FLOAT, false) // do not chart this series
        });
        
        for (int i=0;i<Math.random()*100+100;i++) ;
        int commentX = (int)(Math.random()*2200)+200;
        for (int i=0;i<2500;i++){ 
            this.dlog.writeLog((float)Math.sin(value));
            this.dlog.writeLog(i);
            this.dlog.writeLog((float)(Math.random()*5-2.5));
            if (i==commentX) this.dlog.writeComment("Comment: This shows how comments can be generated on the NXT in " +
                "realtime mode and displayed on the chart.");
            this.dlog.finishLine();
            value+=.1f;
        }
        
        try {
            // flush any data from buffer for realtime test (if exists)
            dos.flush();
        } catch (IOException e) {
            doIOError();
        }
    }
    
    private void doCachedTest(NXTConnection conn){
        double value=0;
        System.out.println("caching data...");
        this.dlog.startCachingLog();
        this.dlog.setColumns(new LogColumn[] {
            new LogColumn("sine(v)", LogColumn.DT_FLOAT)
        });
        
        for (int i=0;i<2500;i++){ 
            this.dlog.writeLog((float)Math.sin(value));
            this.dlog.finishLine();
            value+=.1f;
        }
        System.out.println("hit key to send");
        Button.waitForAnyPress();
        System.out.println("Sending..");
        try {
            // shows use of method with NXTConnection param
             this.dlog.sendCache(conn);
        } catch (IOException e) {
            doIOError();
        }
    }

    /**Wait for a connection from the PC to begin a real time logging session.  If a connection is already open, it is closed and a new
     * connection is created.
     * <p>
     * This class will use LCD rows 1 and 2 to output status.
     * 
     * @param timeout time in milliseconds to wait for the connection to establish. 0 means wait forever. 
     * @param connectionType Use <code>{@link #CONN_USB}</code> or <code>{@link #CONN_BLUETOOTH}</code>
     * @return the connection. null if invalid
     * @see NXTConnection
     */
    private NXTConnection waitForConnection(int timeout, int connectionType) {
        NXTConnection theConnection=null;
        
        final int INIT_TIME = 1500;
        if (connectionType != TestLogger.CONN_BLUETOOTH && 
            connectionType != TestLogger.CONN_USB)
            return theConnection;
        timeout = Math.abs(timeout) + INIT_TIME;
     
        LCD.drawString("Initializing.. ", 0, 2);
        LCD.drawString("Using " + 
                       (connectionType == CONN_BLUETOOTH ? "Bluetooth" : 
                        "USB"), 0, 1);
        // wait just a bit to display the WAITING prompt to give the conn some time. I found that if immediately
        // try to connect from PC, the conn fails 
        new Thread(new Runnable() {
            public void run() {
                Delay.msDelay(INIT_TIME);
                LCD.drawString("WAITING FOR CONN", 0, 2);
            }
        }).start();

        // polymorphism example with abstract class as type
        if (connectionType == CONN_USB) {
            theConnection = USB.waitForConnection(timeout, NXTConnection.PACKET);
        } else {
            theConnection = Bluetooth.waitForConnection(timeout, NXTConnection.PACKET);
        }
        if (theConnection == null) {
            LCD.drawString("  CONN FAILED!  ", 0, 2, true);
            Delay.msDelay(4000);
            return null;
        }
        LCD.drawString("   CONNECTED    ", 0, 2);

        return theConnection;
    }
}
