import lejos.util.*;
import lejos.gps.*;
import lejos.util.*;

/**
 * @author Juan Antonio Brenha Moral
 *
 */
public class DegreesTest{
	private static DebugMessages dm;
	private static double latitudeExample = 4807.038;
	private static double longitudeExample = 01131.000;
	private static Latitude lat;
	private static Longitude lon;
	
	public static void main(String[] args){
		dm = new DebugMessages();
		dm = new DebugMessages();
		dm.setLCDLines(7);
		dm.setDelay(500);
		dm.setDelayEnabled(true);

		lat = new Latitude(latitudeExample);
		lon = new Longitude(longitudeExample);
		dm.echo("RAW Lat:" + latitudeExample);
		dm.echo("DD  Lat:" + lat.getDecimalDegrees());
		dm.echo("L   Lat:" + lat.getDirectionLeter());
		dm.echo("");
		dm.echo("DMS Lat:");
		dm.echo("D   Lat:" + lat.getDegrees() + "º");
		dm.echo("M   Lat:" + lat.getMinutes() + "'");
		dm.echo("S   Lat:" + lat.getSeconds() + "''");
		try {Thread.sleep(10000);} catch (Exception e) {}
		
		dm.clear();
		credits();
	}

	private static void credits(){
		dm.echo("");
		dm.echo("LEGO Mindstorms");
		dm.echo("NXT Robots  ");
		dm.echo("run better with");
		dm.echo("Java leJOS");
		dm.echo("");
		dm.echo("www.lejos.org");
		try {Thread.sleep(20000);} catch (Exception e) {}
	}
}
