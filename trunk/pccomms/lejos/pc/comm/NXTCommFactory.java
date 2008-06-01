package lejos.pc.comm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * Creates a version of NTComm appropriate to the OS in use
 * and protocol (Bluetooth or USB) that is requested.
 *
 */
public class NXTCommFactory { 
    
    public static final int USB = 1; 
    public static final int BLUETOOTH = 2; 
 
	private static String os = System.getProperty("os.name");
    private static String SEP = System.getProperty("file.separator");

   	public static NXTComm createNXTComm(int protocol) throws NXTCommException {       		
       	boolean windows = false; 
       	boolean mac = false;    	
       	String home = System.getProperty("nxj.home");
       	String propFile = null;
       	Properties props = new Properties();
       	
       	// try environment variable if system property not set
       	if (home == null) {
       		home = System.getenv("NXJ_HOME");
       	}
       	
       	if (home != null) {
       		propFile = home + SEP + "bin" + SEP + "nxj.properties";
       	}
		
		if (propFile != null) {
			try {
				props.load(new FileInputStream(propFile));
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				throw new NXTCommException("Cannot read nxj.properties file");
			}
		}
				
	    if (os.length() >= 7 && os.substring(0,7).equals("Windows")) { 
       		windows = true; 
       	}
       	
       	if (os.equals("Mac OS X")) { 
       		mac=true;          
       	} 
       	
		// Look for USB comms driver first
		if ((protocol & NXTCommFactory.USB) != 0) {
			String nxtCommName = props.getProperty("NXTCommUSB",
					"lejos.pc.comm.NXTCommLibnxt");
			try {
				Class c = Class.forName(nxtCommName);
				return (NXTComm) c.newInstance();
			} catch (Throwable t) {
				throw new NXTCommException("Cannot load USB driver");
			}
		}
		        
		// Look for a Bluetooth one
		String defaultDriver = (windows || mac ? "lejos.pc.comm.NXTCommBluecove"
				: "lejos.pc.comm.NXTCommBluez");

		if ((protocol & NXTCommFactory.BLUETOOTH) != 0) {
			String nxtCommName = props.getProperty("NXTCommBluetooth",
					defaultDriver);
			try {
				Class c = Class.forName(nxtCommName);
				return (NXTComm) c.newInstance();
			} catch (Throwable t) {
				throw new NXTCommException("Cannot load Bluetooth driver");
			}
		}	
		return null;
   }   	
} 

