package org.lejos.nxt.ldt.comm;

import java.io.IOException;

import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommand;
import lejos.pc.comm.NXTInfo;

import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public class NXTConnectionManager {

	public NXTInfo connectedNXT;
	
	public NXTInfo getConnectedNXT() {
		return connectedNXT;
	}

	public NXTInfo[] searchForNXTBricks() {
		// disconnect
		NXTCommand nxtCommand = NXTCommand.getSingleton();
		try {
			nxtCommand.close();
			connectedNXT = null;
		} catch (IOException ioe) {
			LeJOSNXJUtil
					.message("something went wrong when trying to close the connection to NXT bricks"
							+ ioe.getMessage());
		}
		// search for bricks
		NXTInfo[] nxtBricks = null;
		NXTInfo[] nxtUSBBricks = null;
		NXTInfo[] nxtBluetoothBricks = null;
		try {
			nxtUSBBricks = nxtCommand.search(null, NXTCommFactory.USB);
		} catch (NXTCommException nce) {
			LeJOSNXJUtil
					.message("something went wrong when searching for NXT bricks via USB: "
							+ nce.getMessage());
		}
		try {
			nxtBluetoothBricks = nxtCommand.search(null,
					NXTCommFactory.BLUETOOTH);
		} catch (NXTCommException nce) {
			LeJOSNXJUtil
					.message("something went wrong when searching for NXT bricks via Bluetooth: "
							+ nce.getMessage());
		}
		int noOfUSBBricksFound = 0;
		if (nxtUSBBricks != null) {
			noOfUSBBricksFound = nxtUSBBricks.length;
		}
		int noOfBluetoothBricksFound = 0;
		if (nxtBluetoothBricks != null) {
			noOfBluetoothBricksFound = nxtBluetoothBricks.length;
		}
		int noOfBricksFound = noOfUSBBricksFound + noOfBluetoothBricksFound;
		if (noOfBricksFound > 0) {
			nxtBricks = new NXTInfo[noOfBricksFound];
			int i = 0;
			for (int j = 0; j < noOfUSBBricksFound; j++) {
				nxtBricks[i++] = new NXTInfo(nxtUSBBricks[j]);
			}
			for (int j = 0; j < noOfBluetoothBricksFound; j++) {
				nxtBricks[i++] = new NXTInfo(nxtBluetoothBricks[j]);
			}
		}
		return nxtBricks;
	}

	public boolean connectToBrick(NXTInfo browserInfo) {
		boolean brickConnected = false;
		try {
			brickConnected = NXTCommand.getSingleton().open(browserInfo);
		} catch (Throwable t) {
			LeJOSNXJUtil.message(t);
		}
		if(brickConnected)
			connectedNXT = browserInfo;
		return brickConnected;
	}
	
	public void detachFromBricks() {
		try {
			NXTCommand.getSingleton().close();
			connectedNXT = null;
		} catch (Throwable t) {
			LeJOSNXJUtil.message(t);
		}
	}
	

}
