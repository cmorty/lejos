import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;

import lejos.util.Delay;
import lejos.util.LogColumn;
import lejos.util.NXTDataLogger;


public class TestLogger {
    /** Use USB for the connection
     */
    private static final int CONN_USB = 1;
    /** Use Bluetooth for the connection
     */
    private static final int CONN_BLUETOOTH = 2;
    private DataOutputStream dos = null;
    private DataInputStream dis = null;
    NXTDataLogger dlog = new NXTDataLogger();
 
 
    public TestLogger() {
    }

    public static void main(String[] args) {
        TestLogger testLogger = new TestLogger();
        testLogger.doTests();
    }
    
    private NXTConnection getConnection(int ConnType){
        NXTConnection theConnection=waitForConnection(15000,ConnType);
        if (theConnection==null) {
            LCD.drawString("IO error 1! ",0,3);
            LCD.drawString("Press ENT ",0,4, true);
            Button.ENTER.waitForAnyPress();
            return null;
        }
        return theConnection;
    }
    
    private void doTests(){
        System.out.println("Realtime mode");
        System.out.println("Press key");
        Button.waitForAnyPress();
        LCD.clear();
        NXTConnection theConnection = getConnection(CONN_BLUETOOTH);
        LCD.clear();
        System.out.println("sending data");
        doRealtimeTest(theConnection);
        System.out.println("Press key");
        Button.waitForAnyPress();
        LCD.clear();
        System.out.println("Cache mode");
        System.out.println("Press key");
        Button.waitForAnyPress();
        doCachedTest(theConnection);
        dlog.stopLogging();
        closeConnection(theConnection);
        LCD.clear();
        System.out.println("Complete!");
        System.out.println("Press key");
        Button.waitForAnyPress();
    }
    
    private void doRealtimeTest(NXTConnection conn){
        double value=0;
        DataOutputStream dos = conn.openDataOutputStream();
        try {
            dlog.startRealtimeLog(dos, conn.openDataInputStream());
        } catch (IOException e) {
            LCD.drawString("IO error 2! ", 0, 3);
            LCD.drawString("Press ENT ", 0, 4, true);
            Button.ENTER.waitForAnyPress();
            return;
        }
        
        dlog.setColumns(new LogColumn[] {
            new LogColumn("sine(v)", LogColumn.DT_FLOAT),
            new LogColumn("upper", LogColumn.DT_FLOAT), 
            new LogColumn("lower", LogColumn.DT_FLOAT),
            new LogColumn("Random", LogColumn.DT_FLOAT, false) // do not chart this series
        });

        for (int i=0;i<975;i++){ // 975
            dlog.writeLog((float)Math.sin(value));
            dlog.writeLog(1f);
            dlog.writeLog(-1f);
            dlog.writeLog((float)(Math.random()*5-2.5));
            dlog.finishLine();
            value+=.1f;
        }
        
        try {
            dos.flush();
        } catch (IOException e) {
            // ignore
        }
    }
    
    private void doCachedTest(NXTConnection conn){
        double value=0;
        System.out.println("caching data");
        dlog.startCachingLog();
        dlog.setColumns(new LogColumn[] {
            new LogColumn("sine(v)", LogColumn.DT_FLOAT)
        });
        
        for (int i=0;i<512;i++){ 
            dlog.writeLog((float)Math.sin(value));
            dlog.finishLine();
            value+=.1f;
//            Delay.msDelay(10);
        }
        System.out.println("hit key to send");
        Button.ENTER.waitForAnyPress();
        System.out.println("Sending..");
        try {
             dlog.sendCache(conn);
        } catch (IOException e) {
            LCD.drawString("IO error 3! ", 0, 3);
            LCD.drawString("Press ENT ", 0, 4, true);
            Button.ENTER.waitForAnyPress();
            return;
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
     * @see #closeConnection()
     */
    private NXTConnection waitForConnection(int timeout, int connectionType) {
        NXTConnection theConnection=null;
        
        final int INIT_TIME = 2000;
        if (connectionType != this.CONN_BLUETOOTH && 
            connectionType != this.CONN_USB)
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
            return null;
        }
        LCD.drawString("   CONNECTED    ", 0, 2);

        return theConnection;
    }
    
    private void closeConnection(NXTConnection theConnection) {
        if (theConnection==null) return;
 
        try {
            dos.flush();
            // wait for the hardware to finish any "flushing". I found that without this, the last data may be lost if the program ends
            // or dos is set to null right after the flush().
            Delay.msDelay(100); 
            if (this.dis!=null) this.dis.close();
            if (dos!=null) dos.close();
        } catch (IOException e) {
            ; // ignore
        } catch (Exception e) {
            // TODO What to do?
        }
        if (theConnection!=null) {
            theConnection.close();
            theConnection=null;
        }
        this.dis = null;
        dos = null;
    }
}
