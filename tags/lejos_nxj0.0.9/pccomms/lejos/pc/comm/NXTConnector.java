package lejos.pc.comm;

import java.io.*; 
import java.util.*;

/**
 * Connects to a NXT using Bluetooth or USB (or either) and supplies input and output
 * data streams.
 * 
 * @author Lawrie Griffiths and Roger Glassey
 */
public class NXTConnector extends NXTCommLoggable
{
	private DataInputStream dataIn;
	private DataOutputStream dataOut;
	private InputStream is;
	private OutputStream os;
	private NXTInfo nxtInfo;
	private NXTInfo[] nxtInfos;
	private NXTComm nxtCommUSB = null, nxtCommBluetooth = null, nxtComm = null;
	private boolean debugOn = false;
    
	/**
	 * Connect to any NXT over any protocol in PACKET mode
	 * 
	 * @return 0 true iff the open succeeded
	 */
    public boolean connectTo() {
    	return connectTo(null, null, NXTCommFactory.ALL_PROTOCOLS, NXTComm.PACKET);
    }
    
	/**
	 * Connect to any NXT over any protocol specifying mode
	 * @param mode the NXTComm mode (PACKET, LCP, or RAW)
	 * 
	 * @return 0 true iff the open succeeded
	 */
    public boolean connectTo(int mode) {
    	return connectTo(null, null, NXTCommFactory.ALL_PROTOCOLS, mode);
    }
    
    /**
     * Connect to a specified NXT in packet mode
     * 
     * @param nxt the name of the NXT to connect to or null for any
     * @param addr the address of the NXT to connect to or null
     * @param protocols the protocols to use
     * @return true iff the open succeeded
     */
    public boolean connectTo(String nxt, String addr, int protocols) {
    	return connectTo(nxt, addr, protocols, NXTComm.PACKET);
    }
    
    /**
     * Search for a NXT
     * 
     * @param nxt the name of the NXT to connect to or null for any
     * @param addr the address of the NXT to connect to or null 
     * @param protocols the protocols to use
     * @return 0 if opened successfully, -1 if failed, 1 if there is a list to choose from
     */
	public NXTInfo[] search(String nxt, String addr, int protocols)
	{
		String name = (nxt == null || nxt.length() == 0 ? nxt: "Unknown");
		String searchParam = (nxt == null || nxt.length() == 0 || nxt.equals("*") ? null : nxt);
		String searchFor = (nxt == null || nxt.length() == 0 ? "any NXT" : nxt);
       	Properties props = null;
       	
       	// reset the relevant instance variables
		nxtComm = null;
		nxtInfos = new NXTInfo[0];

		debug("Protocols = " + protocols);
		debug("Search Param = " + searchParam);
		
		// Try USB first
		if ((protocols & NXTCommFactory.USB) != 0) {
			try {
				nxtComm = nxtCommUSB = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
			} catch (NXTCommException e) {
				log("Failed to load USB comms driver: " + e.getMessage());
			}
			if (addr != null && addr.length() > 0) {
				log("Using USB device with address = " + addr);
				nxtInfo = new NXTInfo(NXTCommFactory.USB, name, addr);
				nxtInfos = new NXTInfo[1];
				nxtInfos[0] = nxtInfo;
			} else if (nxtComm != null){
				debug("Searching for " + searchFor + " using USB");
				try {
					nxtInfos = nxtComm.search(searchParam, NXTCommFactory.USB);
					if (nxtInfos.length == 0) 
						debug((searchParam == null ? "No NXT found using USB: " : (searchParam + " not found using USB: ")) +  "Is the NXT switched on and the USB cable connected?");
				} catch (NXTCommException ex) {
					log("Search Failed: " + ex.getMessage());
				}
			}
		}
		
		if (nxtInfos.length > 0) return nxtInfos;
		
		// If nothing found on USB, try Bluetooth		
		if ((protocols & NXTCommFactory.BLUETOOTH) != 0) {
			// Load Bluetooth driver
			try {
				nxtComm = nxtCommBluetooth = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			} catch (NXTCommException e) {
				log("Failed to load Bluetooth comms driver: " + e.getMessage());
				return nxtInfos;
			}
			
			// If address specified, connect by address
			if (addr != null && addr.length() > 0) {
				log("Using Bluetooth device with address = " + addr);
				nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, name, addr);
				nxtInfos = new NXTInfo[1];
				nxtInfos[0] = nxtInfo;
				return nxtInfos;
			}
			
			// Get known NXT names and addresses from the properties file
			try {	       	
				props = NXTCommFactory.getNXJCache();
								
				// Create an array of NXTInfos from the properties
				if (props.size() > 0 && !(nxt != null && nxt.equals("*"))) {	
					Hashtable<String,String> nxtNames = new Hashtable<String,String>();
					
					debug("Searching cache file for known Bluetooth devices");
					
					// Populate hashTable from NXT_<name> entries, filtering by name, if supplied
					for (Map.Entry<?, ?> e : props.entrySet()) {
				        // Get property name						
				        String propName = (String)e.getKey();
				        
				        if (propName.startsWith("NXT_")) {
				        	String nxtAddr = propName.substring(4);
				        	String nxtName = (String)e.getValue();
					        
				        	if (isAddress(nxtAddr) && (searchParam == null || nxtName.equals(nxt))) {
				        		debug("Found " + nxtName + " " + nxtAddr + " in cache file");
				        		nxtNames.put(nxtName, nxtAddr);
				        	}				        	
				        }				    
				    }
				    
				    debug("Found " + nxtNames.size() + " matching NXTs in cache file");
				    
				    // If any found, create the NXTInfo array from the hashtable
				    if (nxtNames.size() > 0) {					    
						nxtInfos = new NXTInfo[nxtNames.size()];
						
						Enumeration<?> enNXTs = nxtNames.keys();
						int i=0;
					    for (; enNXTs.hasMoreElements(); ) {
					    	String ne = (String)enNXTs.nextElement();
					    	nxtInfos[i++] = new NXTInfo(NXTCommFactory.BLUETOOTH, ne, nxtNames.get(ne));			    							
					    }				    	
				    }
				} else {
					debug("No NXTs found in cache file");
				}
			} catch (NXTCommException ex) {
				log("Failed to load cache file");
			}
		
			// If none found, do a Bluetooth inquiry
			if (nxtInfos.length == 0) {
				log("Searching for " + searchFor + " using Bluetooth inquiry");
				try {
					nxtInfos = nxtComm.search(searchParam, NXTCommFactory.BLUETOOTH);
				} catch (NXTCommException ex) { 
					log("Search Failed: " + ex.getMessage());
				}
				
				debug("Inquiry found " + nxtInfos.length + " NXTs");
				
				// Save the results in the properties file
				for(int i=0;i<nxtInfos.length;i++) {
					log("Name " + i + " = " + nxtInfos[i].name);
					log("Address " + i + " = " + nxtInfos[i].deviceAddress);
					props.put("NXT_" + nxtInfos[i].deviceAddress, nxtInfos[i].name);
				}
				
				debug("Saving cached names");
				try {
					NXTCommFactory.saveNXJCache(props,"Results from Bluetooth inquiry");
				} catch (IOException ex) {
					log("Failed to write cache file: " + ex.getMessage());
				}
			}
		}
		
		// If nothing found, log a message
		if (nxtInfos.length == 0) {
			log("Failed to find any NXTs");
		}
	
		return nxtInfos;
	}
	
    /**
     * Connect to a NXT
     * 
     * @param nxt the name of the NXT to connect to or null for any
     * @param addr the address of the NXT to connect to or null 
     * @param protocols the protocols to use
     * @return 0 if opened successfully, -1 if failed, 1 if there is a list to choose from
     */
	public boolean connectTo(String nxt, String addr, int protocols, int mode)
	{
		boolean opened = false;
       	
       	// reset all the instance variables associated with the connection
		nxtInfo = null;
		is = null;
		os = null;
		dataIn = null;
		dataOut = null;
		
		// Search for matching NXTs
		search(nxt, addr, protocols);
		
		// Try each available NXT in turn
		for(int i=0;i<nxtInfos.length;i++) {
			try {
				debug("Connecting to " + nxtInfos[i].name + " " + nxtInfos[i].deviceAddress + " in mode " + mode);
				opened = nxtComm.open(nxtInfos[i], mode);
				if (opened) {
					nxtInfo = nxtInfos[i];
					log("Connected to " + nxtInfo.name);
					break;
				} else {
					log("Failed to open " + nxtInfos[i].name + " " + nxtInfos[i].deviceAddress);
				}
			} catch (NXTCommException ex) { 
				log("Exception in open: " + ex.getMessage());
			}
		}

		if (!opened) {
			log("Failed to connect to any NXT");
			return false;
		}

		setStreams();
		return true;
	}
	
	/**
	 * Connect to a NXT using a NXTInfo
	 * @param nxtInfo
	 * @param mode
	 * @return true iff the connection succeeded
	 */
	public boolean connectTo(NXTInfo nxtInfo, int mode) {
		this.nxtInfo = nxtInfo;
		if (nxtInfo.protocol == NXTCommFactory.USB ) {
			if (nxtCommUSB == null) {
				try {
					nxtComm = nxtCommUSB = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
				} catch (NXTCommException e) {
					log("Failed to load USB comms driver: " + e.getMessage());
					return false;
				}
			}
			nxtComm = nxtCommUSB;
		}
		if (nxtInfo.protocol == NXTCommFactory.BLUETOOTH ) {
			if (nxtCommBluetooth == null) {
				try {
					nxtComm = nxtCommBluetooth = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
				} catch (NXTCommException e) {
					log("Failed to load Bluetooth comms driver: " + e.getMessage());
					return false;
				}
			}
			nxtComm = nxtCommBluetooth;
		}
		
		try {
			boolean opened =  nxtComm.open(nxtInfo, mode);
			if (!opened) {
				log("Failed to connect to the specified NXT");
				return false;
			}
			setStreams();
			return true;
		} catch (NXTCommException e) {
			log("Exception connecting to NXT: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Connect to a device by URL
	 * @param deviceURL
	 * @param mode the mode (NXTComm.LCP or NXTComm.PACKET)
	 * @return true iff the connection succeeded
	 */
	public boolean connectTo(String deviceURL, int mode) {
		String protocolString = "";
		int colonIndex = deviceURL.indexOf(':');
		if (colonIndex >= 0) {
			protocolString = deviceURL.substring(0,colonIndex);
		}
		String addr = null;
		String name = null;
		
		int protocols = NXTCommFactory.ALL_PROTOCOLS;
		if (protocolString.equals("btspp")) protocols = NXTCommFactory.BLUETOOTH;
		if (protocolString.equals("usb")) protocols = NXTCommFactory.USB;
		
		if (colonIndex >= 0) colonIndex +=2; // Skip "//"
		
		String nameString = deviceURL.substring(colonIndex+1);		
		boolean isAddress = isAddress(nameString);
		
		if (isAddress) {
			addr = nameString;
			name = "Unknown";
		} else {
			name = nameString;
			addr = null;
		}
		
		return connectTo(name, addr, protocols, mode);
	}
	
	private boolean isAddress(String s)
	{
		return s != null && s.startsWith("00");
	}
	
	/**
	 * Connect to a device by URL in packet mode
	 * @param deviceURL
	 * @return true iff the connection succeeded
	 */
	public boolean connectTo(String deviceURL) {
		return connectTo(deviceURL, NXTComm.PACKET);
	}
	
	private void setStreams() {
		is = nxtComm.getInputStream();
		dataIn = new DataInputStream(nxtComm.getInputStream());
		os = nxtComm.getOutputStream();
		dataOut = new DataOutputStream(os);
	}

	/**
	 * @return the InputStream for this connection;
	 */
	public InputStream getInputStream() {return is;}
    
	/**
	 * @return the DataInputStream for this connection;
	 */
	public DataInputStream getDataIn() {return dataIn;}

	/**
	 * @return the OutputSteram for this connection;
	 */
	public OutputStream getOutputStream() {return os;}
 
	/**
	 * @return the DataOutputStream for this connection
	 */
	public DataOutputStream getDataOut() {return dataOut;}

	/**
	 * @return the NXTInfo for this connection
	 */   
	public  NXTInfo getNXTInfo () {return nxtInfo;}
	
	/**
	 * @return the array of NXTInfos for this connection
	 */   
	public  NXTInfo[] getNXTInfos () {return nxtInfos;}
	
	/**
	 * @return the NXTComm for this connection
	 */   
	public  NXTComm getNXTComm () {return nxtComm;}
	
	/**
	 * Close the connection
	 *
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (nxtComm != null) nxtComm.close();
	}
	
	private void debug(String msg) {
		if (debugOn) log(msg);
	}
	
	/**
	 * Set debugging on or off
	 * 
	 * @param debug true for on, false for off
	 */
	public void setDebug(boolean debug) {
		debugOn = debug;
	}
}
