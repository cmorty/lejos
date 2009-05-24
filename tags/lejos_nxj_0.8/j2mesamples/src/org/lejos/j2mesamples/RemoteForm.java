package org.lejos.j2mesamples;
import javax.microedition.lcdui.*;
import lejos.j2me.comm.*;
import javax.microedition.midlet.*;


/**
 * Form for connecting to the NXT
 * 
 * @author Lawrie Griffiths
 *
 */
public class RemoteForm extends Form {
	MIDlet midlet;
	
	public RemoteForm(MIDlet m){
		super("NXT Remote");
		this.midlet = m;
	}
	
	public void connect() {		
		NXTComm nxtComm = null;
		NXTInfo[] nxtInfos = null;
		
		try {
			nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			do {
				log("Searching...");
				nxtInfos = nxtComm.search(null, NXTCommFactory.BLUETOOTH);
				log("Found " + nxtInfos.length + " NXTs");
				if (nxtInfos.length == 0) {
					log("Please switch your NXT on");
				}
			} while (nxtInfos.length == 0);
			RemoteCanvas rc = new RemoteCanvas(nxtComm, nxtInfos[0]);
			log("Created remote canvas");
			Display.getDisplay(midlet).setCurrent(rc);
		} catch (NXTCommException e) {
			log(e.getMessage());
		}		
	}
	
	public void log(String message){
		this.append(message + "\n");
	}
}
