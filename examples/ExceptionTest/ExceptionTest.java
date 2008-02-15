import lejos.nxt.*;

/**
 * Simple test of leJOS exceptions.
 * 
 * This causes an ArrayIndexOutOdBoundsException.
 * 
 * Use the --verbose (-v) flag on nxj or nxjlink to see
 * the values of classes and methods (signatures) for
 * your program.
 *  
 * @author Lawrie Griffiths
 *
 */
public class ExceptionTest {	
	public static void main (String[] aArg)
	{
		SensorPort p = SensorPort.PORTS[5];
	}
}
