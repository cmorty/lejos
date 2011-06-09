package net.mosen.nxt.instrumentation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

import lejos.nxt.comm.USB;

import lejos.util.Delay;


/**
 * Convenient NXT-side Bluetooth/USB manager layer to communicate with doppleganger on the PC side
 * 
 */
public class NXTConnectionManager{
    private static DataInputStream dis = null;
    private static DataOutputStream dos = null;
    private static boolean isConnected = false;
    
    private static NXTConnection theConnection=null; // polymorphism example with abstract class as type
    
    /**Wait for a Bluetooth connection from the PC.
     * @param timeout time in msec to wait. 0 means wait forever. If connection is already open, it is closed.
     * @throws IOException
     * @return <code>true</code> for successful connection
     */
    public static boolean waitForBTConnection(int timeout)  {
        if (isConnected) {
            closeConnection();
        }
        theConnection = Bluetooth.waitForConnection(timeout, NXTConnection.PACKET); // polymorphism example with abstract class as type
        if (theConnection==null) {
            theConnection=null;
            return false;
//            throw new IOException("Bluetooth connection failed");
        }
        dis = theConnection.openDataInputStream();
        dos = theConnection.openDataOutputStream();
        isConnected = true;
        return true;
    }
    /**Wait for a USB connection from the PC.
    * @param timeout time in msec to wait. 0 means wait forever. If connection is already open, it is closed.
    * @return <code>true</code> for successful connection
    */
    public static boolean waitForUSBConnection(int timeout) {
        if (isConnected) {
            closeConnection();
        }
        theConnection = USB.waitForConnection(timeout, NXTConnection.PACKET); // polymorphism example with abstract class as type
        if (theConnection == null) {
            theConnection=null;
            return false;
//            throw new IOException("USB connection failed");
        }
        dis = theConnection.openDataInputStream();
        dos = theConnection.openDataOutputStream();
        isConnected = true;
        return true;
    }

    public static void closeConnection() {
        isConnected = false;
        if (theConnection==null) return;
        try {
            dos.flush();
            // wait for the hardware to finish any "flushing". I found that without this, the last data may be lost.
            Delay.msDelay(50); 
            dis.close();
            dos.close();
        } catch (IOException e) {
            // TODO What to do?
            LCD.drawString("10!" + e.toString(), 0, 7);
        } catch (Exception e) {
            // TODO
        }
        //        doWait(50);
        if (theConnection!=null) {
            theConnection.close();
            theConnection=null;
        }
        dis = null;
        dos = null;
    }

    /**
     * The DataOutputStream will be null if no connection
     * @return The DataOutputStream
     */
    public static DataOutputStream getDataOutputStream() {
        return dos;
    }
    
    /**
     * The DataInputStream will be null if no connection
     * @return The DataInputStream
     */
    public static DataInputStream getDataInputStream() {
        return dis;
    }
    
    public static boolean isConnected() {
        return isConnected;
    }
}
