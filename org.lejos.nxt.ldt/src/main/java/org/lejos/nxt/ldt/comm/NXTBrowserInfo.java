package org.lejos.nxt.ldt.comm;

import lejos.pc.comm.NXTInfo;

/**
 * TODO refactor NXTInfo to include this
 * @author scholz
 *
 */
public class NXTBrowserInfo {
	
	private NXTInfo info;
	private NXTConnectionState connectionState = NXTConnectionState.DISCONNECTED;
	
	public NXTInfo getNXTInfo() {
		return info;
	}

	public void setNXTInfo(NXTInfo info) {
		this.info = info;
	}

	public NXTConnectionState getConnectionState() {
		return connectionState;
	}

	public void setConnectionState(NXTConnectionState connectionState) {
		this.connectionState = connectionState;
	}

	public NXTBrowserInfo(NXTInfo info) {
		this.info = info;
	}

}
