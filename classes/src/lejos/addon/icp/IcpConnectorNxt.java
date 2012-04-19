package lejos.addon.icp;

import java.io.*; // TODO: DELETE LINE

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

/**
 * <p>
 * <b>NxtBluetoothConnector</b> provides a concrete implementation of the
 * IcpConnector interface.  It uses the leJOS NXT classes to initiate
 * a Bluetooth connection to the target address.  Additionally, it
 * attempts to close and re-establish a connection to the device when
 * requested.
 * </p>
 * 
 * @author Jason Healy <jhealy@logn.net>
 * 
 * @see IcpConnector
 */
public class IcpConnectorNxt extends IcpConnectorAdapter {

    /** The Bluetooth connection to the iControlPad */
    private BTConnection conn;

    /**
     * @see IcpConnectorAdapter#IcpConnectorAdapter(String, byte[])
     */
    public IcpConnectorNxt(String target, byte[] pin) {
	super(target.toUpperCase(), pin);
    }
    

    /**
     * @see IcpConnectorAdapter#IcpConnectorAdapter(String, char[])
     */
    public IcpConnectorNxt(String target, char[] pin) {
	super(target.toUpperCase(), pin);
    }


    /**
     * @see IcpConnectorAdapter#IcpConnectorAdapter(String, String)
     */
    public IcpConnectorNxt(String target, String pin) {
	super(target.toUpperCase(), pin);
    }


    /**
     * @see IcpConnectorAdapter#IcpConnectorAdapter(String)
     */
    public IcpConnectorNxt(String target) {
	super(target.toUpperCase());
    }

    
    /**
     * <p>
     * Attempts to create a new leJOS BTConnection object
     * to the target (specified at construction time).
     * </p>
     * 
     * @see IcpConnector#connect()
     * @see Bluetooth#connect(String, int, byte[])
     */
    public void connect() {
    	//System.out.println("Try:" + target);

    	// TODO: UNCOMMENT FOLLOWING CODE
    	/* conn = Bluetooth.connect(target, NXTConnection.RAW, pin);
    	if (null != conn) {
    		is = conn.openInputStream();
    		os = conn.openOutputStream();
    	}
    	*/
    	is = new DummyInputStream(); //TODO: DELETE TEMP LINE
    	os = new DummyOutputStream(); //TODO: DELETE TEMP LINE
    }

    
    /**
     * <p>
     * Tears down any existing bluetooth connection, and then
     * attempts to re-establish a connection to the target by
     * re-calling connect().
     * </p>
     * 
     * @see IcpConnector#connect()
     * @see #connect()
     */
    public void reconnect() {
	super.reconnect();
	// reconnect may be called in an invalid state,
	// so guard against null variables
	if (null != conn) {
	    conn.close();
	}
	connect();
    }
    
    /**
     *  // TODO: DELETE TEMP CLASS
     * @author Brian
     *
     */
    private class DummyInputStream extends InputStream {

		@Override
		public int read() throws IOException {
			System.out.println("READ CALLED");
			return 0;
		}
    	
    }
    
    /**
     * // TODO: DELETE TEMP CLASS
     * @author Brian
     *
     */
    private class DummyOutputStream extends OutputStream {

		@Override
		public void write(int arg0) throws IOException {
			System.out.println("WRITE CALLED");
		}
    }
}
