import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Delay;

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
    static void m1()
    {
        // Force an exception
		SensorPort p = SensorPort.PORTS[5];
        //Error t = new Error("Testing testing 123");
        //throw t;
    }

    static void m2()
    {
        m1();
    }

	public static void main (String[] aArg) throws Exception
	{
        System.out.println("Running");
        m2();
	}
}
