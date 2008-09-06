// !! Needs TextBox removed from code! Use system.out or System.err
import java.io.IOException;
import javax.bluetooth.*;
import javax.microedition.lcdui.*;

public class NXTLocator implements DiscoveryListener {
	private Display mDisplay;
	private final int NXTID = 2048;
	private final int SERIALID = 0x1101;
	
    private LocalDevice local = null;

    private DiscoveryAgent agent = null;
	
	private RemoteDevice remote = null;
	private String remoteURL = null;
	
	/* Brick Name */
	private String remoteName = null;
	
	/* !!Shows TextBox to display status. Must remove if leJOS NXJ */
	private TextBox messages;
	
	// !! Must remove canvas from NXTLocator
	private RemoteCanvas canvas;
	
	private Displayable vorForm;
	
	public NXTLocator(Display d) {
		mDisplay = d;
		messages = new TextBox("NXT Status", "Welcome!\n", 8192, TextField.ANY);
		mDisplay.setCurrent(messages);
		
		// retrieve the local Bluetooth device object
		try{
			local = LocalDevice.getLocalDevice();
			agent = local.getDiscoveryAgent();
		} 
		catch(BluetoothStateException bse){
			messages.insert("no device found\n", messages.size());
		}
	}
	
	public void findNXT(){
		if(local != null){
			// place the device in inquiry mode
			try{
				agent.startInquiry(DiscoveryAgent.GIAC, this);
				messages.insert("Searching for NXT Brick\n", messages.size());
			}
			catch(BluetoothStateException bse){
				System.out.println("inquiry couldn't be started");
			}
		}
		else{
			messages.insert("Bluetooth is off or this device doesn't support JSR82\n", messages.size());
		}
	}
	
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod){
		if(cod.getMajorDeviceClass() == NXTID)
			remote = btDevice;
	}
	
	public void inquiryCompleted(int discType){
		if(remote == null){
			mDisplay.setCurrent(vorForm);
			// Wait until the canvas appears... 
			// For Nokia it is not the same?
			while(mDisplay.getCurrent() != vorForm);
			messages.insert("No NXT Brick found", messages.size());
		}
		else{
			try {
				remoteName = remote.getFriendlyName(false);
				messages.insert("Connecting to " + remoteName + "\n", messages.size());
			}catch (IOException ioe) {
				System.out.println("error retrieving friendly name");
			}
			
			try{
				//int[] attrs = {0x100}; // default attribute set plus service name
				UUID[] uuids = new UUID[]{new UUID(SERIALID)};//Serial port service
				//agent.searchServices(attrs, uuids, remote, this);
				agent.searchServices(null, uuids, remote, this);
			}
			catch(BluetoothStateException bse){
				System.out.println("service search failed");
			}
		}
	}
	
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord){
		// The NXT brick has only one service: SPP
		remoteURL = servRecord[0].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
	}
	
	public void serviceSearchCompleted(int transID, int respCode){
		if(respCode == DiscoveryListener.SERVICE_SEARCH_COMPLETED){
			// !! This code must be divorced from NXTLocator 
			canvas = new RemoteCanvas(remoteURL);
			mDisplay.setCurrent(canvas);
		}
		else{
			mDisplay.setCurrent(vorForm);
			while(mDisplay.getCurrent() != vorForm);
			switch(respCode){
				case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
					messages.insert("Error: Service search terminated", messages.size());
					break;
				case DiscoveryListener.SERVICE_SEARCH_ERROR:
					messages.insert("Error: Service search error", messages.size());
					break;
				case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
					messages.insert("Error: Service search no records", messages.size());
					break;
				case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
					messages.insert("Error: Service search device not reachable", messages.size());
					break;
				}
			}
	}
}
