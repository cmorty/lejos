package lejos.j2me.comm;

/**
 * 
 * Creates a version of NTComm appropriate to the OS in use
 * and protocol that is requested.
 *
 */
public class NXTCommFactory {    
    public static final int USB = 1; 
    public static final int BLUETOOTH = 2; 
    public static final int ALL_PROTOCOLS = USB | BLUETOOTH;

    /**
     * Load a comms driver for a protocol (USB or Bluetooth)
     * 
     * @param protocol the protocol
     * 
     * @return a driver that supports the nxtComm interface
     * @throws NXTCommException
     */
   	public static NXTComm createNXTComm(int protocol) throws NXTCommException {       		
   		return new NXTCommBluetooth();
   	}	
 } 

