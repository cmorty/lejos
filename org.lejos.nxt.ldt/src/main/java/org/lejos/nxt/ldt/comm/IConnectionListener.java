package org.lejos.nxt.ldt.comm;

public interface IConnectionListener {

	public void brickConnected(NXTBrowserInfo info);

	public void brickDetached(NXTBrowserInfo info);

}
