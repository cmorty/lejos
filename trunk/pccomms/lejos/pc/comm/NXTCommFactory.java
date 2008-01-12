package lejos.pc.comm;

/**
 * 
 * Creates a version of NTComm appropriate to the OS in use
 * and protocol (Bluetooth or USB) that is requested.
 *
 */
public class NXTCommFactory { 
    
    public static final int USB = 1; 
    public static final int BLUETOOTH = 2; 
    
   	public static NXTComm createNXTComm(int protocol) {     
   		String os = System.getProperty("os.name"); 
       	boolean windows = false; 
       	boolean mac = false; 
        
       	if (os.length() >= 7 && os.substring(0,7).equals("Windows")) { 
       		windows = true; 
       	}
       	
       	if (os.equals("Mac OS X")) { 
       		mac=true;          
       	} 
        
       	if (protocol == BLUETOOTH) { 
       		if (windows||mac) return new NXTCommBluecove(); 
       		else return new NXTCommBluez(); 
       	} else return new NXTCommLibnxt();          
   } 
} 

