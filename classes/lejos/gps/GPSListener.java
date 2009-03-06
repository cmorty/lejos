package lejos.gps;

import java.util.EventListener;
import javax.microedition.location.Coordinates;

/**
 * This is the interface to manage events with GPS
 * 
 * @author Juan Antonio Brenha Moral
 *
 */

public interface GPSListener extends EventListener{
	
	// TODO: Probably just one sentenceReceived() method, with NMEA sentence.
	// Compare GGASentence.HEADER with the NMEASentence.getHeader() using .equals;
	public void sentenceReceived(NMEASentence sen);
	
	/*
	public void ggaSentenceReceived (GPS gpsReceiver, GGASentence ggaSentence);
	
	public void rmcSentenceReceived (GPS gpsReceiver, RMCSentence rmcSentence);

	public void vtgSentenceReceived (GPS gpsReceiver, VTGSentence vtgSentence);

	public void gsvSentenceReceived (GPS gpsReceiver, GSVSentence gsvSentence);

	public void gsaSentenceReceived (GPS gpsReceiver, GSASentence gsagSentence);
	*/
}
