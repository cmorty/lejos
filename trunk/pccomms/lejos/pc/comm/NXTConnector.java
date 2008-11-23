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
	private NXTComm nxtComm = null;
    
	/**
	 * Connect to any NXT over any protocol
	 * 
	 * @return 0 if opened successfully, -1 if failed, 1 if there is a list to choose from
	 */
    public int connectTo() {
    	return connectTo(null, null, NXTCommFactory.ALL_PROTOCOLS, NXTComm.PACKET, false);
    }
    
	/**
	 * Connect to any NXT over any protocol specifying mode
	 * @param mode the NXTComm mode (PACKET, LCP, or RAW)
	 * 
	 * @return 0 if opened successfully, -1 if failed, 1 if there is a list to choose from
	 */
    public int connectTo(int mode) {
    	return connectTo(null, null, NXTCommFactory.ALL_PROTOCOLS, mode, false);
    }
    
    /**
     * Connect to a NXT
     * 
     * @param nxt the name of the NXT to connect to or null for any
     * @param addr the address of the NXT to connect to or null
     * @param protocols the protocols to use
     * @param mode the NXTComm mode (PACKET, LCP, or RAW)
     * @param choose true if the user wishes to choose which NXT to connect to
     * @return 0 if opened successfully, -1 if failed, 1 if there is a list to choose from
     */
    public int connectTo(String nxt, String addr, int protocols, boolean choose) {
    	return connectTo(nxt, addr, protocols, NXTComm.PACKET, choose);
    }
    
    /**
     * Connect to a NXT
     * 
     * @param nxt the name of the NXT to connect to or null for any
     * @param addr the address of the NXT to connect to or null 
     * @param protocols the protocols to use
     * @param choose true if the user wishes to choose which NXT to connect to
     * @return 0 if opened successfully, -1 if failed, 1 if there is a list to choose from
     */
	public int connectTo(String nxt, String addr, int protocols, int mode, boolean choose)
	{
		boolean opened = false;
		String name = (nxt == null || nxt.length() == 0 ? nxt: "Unknown");
		String searchParam = (nxt == null || nxt.length() == 0 ? null : nxt);
		String searchFor = (nxt == null || nxt.length() == 0 ? "any NXT" : nxt);
       	Properties props = null;
       	
       	// reset all the instance variables associated with the connection
		nxtInfo = null;
		nxtComm = null;
		nxtInfos = null;
		is = null;
		os = null;
		dataIn = null;
		dataOut = null;
		
		log("Protocols = " + protocols);
		log("Mode = " + mode);
		
		// Try USB first
		if ((protocols & NXTCommFactory.USB) != 0) {
			log("Trying USB");
			try {
				nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
			} catch (NXTCommException e) {
				log("Failed to load USB comms driver: " + e.getMessage());
				return -1;
			}
			if (addr != null && addr.length() > 0) {
				log("Connecting to address " + addr + " using USB");
				nxtInfo = new NXTInfo(NXTCommFactory.USB, name, addr);
				nxtInfos = new NXTInfo[1];
				nxtInfos[0] = nxtInfo;
				try {					
					opened = nxtComm.open(nxtInfo, mode);
					if (opened) {
						setStreams();
						return 0;
					} else {
						log("Connect by address over USB failed");
					}
				} catch (NXTCommException e) {
					log("Connect by address over USB failed: " + e.getMessage());
				}
				opened = false;
				nxtInfos = null;
			} else {
				log("Searching for " + searchFor + " using USB");
				try {
					nxtInfos = nxtComm.search(searchParam, NXTCommFactory.USB);
					if (nxtInfos.length == 0) 
						log((searchParam == null ? "No NXT found: " : (searchParam + " not found: ")) +  "Is the NXT switched on and the USB cable connected?");
				} catch (NXTCommException ex) {
					log("Search Failed: " + ex.getMessage());
				}
			}
		}
		
		// If nothing found on USB, try Bluetooth		
		if (!opened && (nxtInfos == null || nxtInfos.length==0) && 
				(protocols & NXTCommFactory.BLUETOOTH) != 0) {
			log("Trying Bluetooth");
			// Load Bluetooth driver
			try {
				nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			} catch (NXTCommException e) {
				log("Failed to load Bluetooth comms driver: " + e.getMessage());
				return -1;
			}
			
			// If address specified, connect by address
			if (addr != null && addr.length() > 0) {
				log("Connecting to address " + addr + " using Bluetooth");
				nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, name, addr);
				nxtInfos = new NXTInfo[1];
				nxtInfos[0] = nxtInfo;
				try {					
					opened = nxtComm.open(nxtInfo, mode);
					if (opened) {
						setStreams();
						return 0;
					}
				} catch (NXTCommException e) {
					log("Connect by address over Bluetooth failed: " + e.getMessage());
					opened = false;
				}
				return -1;
			}
			
			// Get known NXT names and addresses from the properties file
			try {	       	
				props = NXTCommFactory.getNXJProperties();
								
				// Create an array of NXTInfos from the properties
				if (props.size() > 0) {	
					Hashtable<String,String> nxtNames = new Hashtable<String,String>();
					Enumeration<?> enProps = props.propertyNames();
					
					// Populate hashTable from NXT_<name> entries, filtering by name, if supplied
				    for (; enProps.hasMoreElements(); ) {
				        // Get property name
				        String propName = (String)enProps.nextElement();
				        
				        if (propName.startsWith("NXT_")) {
				        	String nxtName = propName.substring(4);
					        
				        	if (searchParam == null || nxtName.equals(nxt)) {
				        		nxtNames.put(nxtName, (String)props.get(propName));
				        	}				        	
				        }				    
				    }
				    
				    log("Found " + nxtNames.size() + " matching NXTs in properties file");
				    
				    // If any found, create the NXTInfo array from the hashtable
				    if (nxtNames.size() > 0) {					    
						nxtInfos = new NXTInfo[nxtNames.size()];
						
						Enumeration<?> enNXTs = nxtNames.keys();
						int i=0;
					    for (; enNXTs.hasMoreElements(); ) {
					    	String ne = (String)enNXTs.nextElement();
					    	log("Setting nxtInfo " + i + " to " + ne);
					    	nxtInfos[i++] = new NXTInfo(NXTCommFactory.BLUETOOTH, ne, nxtNames.get(ne));			    							
					    }				    	
				    }
				} else {
					log("No NXTs found in properties file");
				}
			} catch (NXTCommException ex) {
				log("Failed to load properties file");
			}
		
			// If none found, do a Bluetooth inquiry
			if (nxtInfos == null || nxtInfos.length == 0) {
				log("Searching for " + searchFor + " using Bluetooth");
				try {
					nxtInfos = nxtComm.search(searchParam, NXTCommFactory.BLUETOOTH);
				} catch (NXTCommException ex) { 
					log("Search Failed: " + ex.getMessage());
				}
				
				log("Inquiry found " + nxtInfos.length + " NXTs");
				
				// Save the results in the properties file
				for(int i=0;i<nxtInfos.length;i++) {
					log("Name " + i + " = " + nxtInfos[i].name);
					log("Address " + i + " = " + nxtInfos[i].deviceAddress);
					props.put("NXT_" + nxtInfos[i].name, nxtInfos[i].deviceAddress);
				}
				
				log("Saving properties");
				try {
					NXTCommFactory.saveNXJProperties(props,"Results from Bluetooth inquiry");
				} catch (IOException ex) {
					log("Failed to write properties: " + ex.getMessage());
				}
			}
		}
		
		// If nothing found, fail
		if (nxtInfos == null || nxtInfos.length == 0) {
			log("Failed to find any NXTs");
			return -1;
		}
	
		if (choose && nxtInfos.length > 1) {
			log("List of NXTS available to choose from");
			return 1;
		}
		
		// Try each available NXT in turn
		for(int i=0;i<nxtInfos.length;i++) {
			try {
				log("Connecting to " + nxtInfos[i].name + " " + nxtInfos[i].deviceAddress);
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
			return -1; 
		}

		setStreams();
		return 0;
	}
	
	
	public int connectTo(String deviceURL) {
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
		boolean isAddress = nameString.startsWith("00");
		
		if (isAddress) {
			addr = nameString;
			name = "Unknown";
		} else {
			name = nameString;
			addr = null;
		}
		
		return connectTo(name, addr, protocols, false);
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
}
