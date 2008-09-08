package org.lejos.nxt.ldt.comm;

import lejos.pc.comm.NXTInfo;

public interface IConnectionListener {
	
	public void brickConnected(NXTInfo info);
	public void brickDetached(NXTInfo info);

}
