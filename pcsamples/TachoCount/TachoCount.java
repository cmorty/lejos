import lejos.nxt.*;
import lejos.pc.comm.*;

/**
 * Sample to spin motors and output Tachometer counts.
 * @author BB
 *
 */
public class TachoCount {
	
	public static void main(String [] args) throws Exception {
		System.out.println("Tachometer A: " + Motor.A.getTachoCount());
		System.out.println("Tachometer C: " + Motor.C.getTachoCount());
		Motor.A.rotate(5000);
		Motor.C.rotate(-5000);
		Thread.sleep(10000);
		Sound.playTone(1000, 1000);
		System.out.println("Tachometer A: " + Motor.A.getTachoCount());
		System.out.println("Tachometer C: " + Motor.C.getTachoCount());
	}	
}