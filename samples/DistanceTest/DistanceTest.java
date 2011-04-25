import lejos.util.*;
import javax.microedition.location.*;

/**
 * This example show how to use the class Coordinates from JSR-179
 * The example explain how to use GPS points (Lat/Lon) to calculate the distances
 * in Kilometers
 * 
 * The example has been tested with others Online tools as:
 * http://www.gpsvisualizer.com/calculators
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class DistanceTest {
	private static DebugMessages dm;
	private static Coordinates origin;
	private static Coordinates to;

	public static void main(String[] args){
		dm = new DebugMessages();
		dm.setLCDLines(6);
		dm.setDelayEnabled(true);

		double[] madrid = {40.41705754418463, -3.703717589378357};
		double[] london = {51.499513113816974, -0.12516260147094727};
		double[] paris = {48.87401614213272, 2.295691967010498};
		double[] berlin = {52.54318996285548, 13.405380249023438};
		double[] taipei = {25.149790941461944, 121.78018569946289};
		double[] tokyo = {35.41535532818056, 139.62318420410156};
		double[] sanfrancisco = {37.812767557570204, -122.47824668884277};
		double[] seychelles = {-4.64760483755757, 55.5523681640625};
		double[] mauritius = {-20.34462694382967, 57.23876953125};

		//Set origin on Madrid, The capital of Spain.
		origin = new Coordinates(madrid[0],madrid[1]);
		to = new Coordinates(0,0);

		
		dm.echo("Testing JSR-179 in leJOS");
		dm.echo("Coordinates.java");
		dm.echo("");
		try {Thread.sleep(1000);} catch (Exception e) {}
		dm.clear();
		
		showCalculus("Madrid-London",london[0],london[1]);
		showCalculus("Madrid-Paris",paris[0],paris[1]);
		showCalculus("Madrid-Berlin",berlin[0],berlin[1]);
		showCalculus("Madrid-Taipei",taipei[0],taipei[1]);
		showCalculus("Madrid-Tokyo",tokyo[0],tokyo[1]);
		showCalculus("Madrid-San Francisco",sanfrancisco[0],sanfrancisco[1]);
		dm.echo("and..");
		dm.echo("");
		dm.echo("my favorites");
		dm.echo("islands");
		try {Thread.sleep(1000);} catch (Exception e) {}
		dm.clear();
		showCalculus("Madrid-Seychelles",seychelles[0],seychelles[1]);
		showCalculus("Madrid-Mauritius",mauritius[0],mauritius[1]);
		try {Thread.sleep(500);} catch (Exception e) {}
		credits();
	}
	
	private static void showCalculus(String label,double lat,double lon){
		double distance;
		double azimuth;
		to.setLatitude(lat);
		to.setLongitude(lon);
		distance = origin.distance(to);
		azimuth = origin.azimuthTo(to);
		dm.echo(label);
		dm.echo(" ");
		dm.echo("Distance: ");
		dm.echo("" +distance/1000 + " Km.");
		dm.echo(" ");
		dm.echo("Azimuth: ");
		dm.echo(""+azimuth + " Deg.");
		try {Thread.sleep(2000);} catch (Exception e) {}
		dm.clear();
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