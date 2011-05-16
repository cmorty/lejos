package lejos.j2me.comm;

import lejos.nxt.remote.*;

public class NXTCommandConnector {

	/**
	 * Get the singleton NXTCommand object. Use of this is optional.
	 * 
	 * @return the singleton NXTCommand instance
	 */
	public static NXTCommand getSingletonOpen() {
		NXTCommand singleton = NXTCommand.getSingleton();
		if (!singleton.isOpen()) {
			try {				
				NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
				NXTInfo[] nxtInfos = nxtComm.search(null, NXTCommFactory.BLUETOOTH);
				if (nxtInfos.length == 0) return null;
				nxtComm.open(nxtInfos[0], NXTComm.LCP);
				singleton.setNXTComm(nxtComm);
			} catch (NXTCommException e) {
				return null;
			}
		}
		return singleton;
	}
}
