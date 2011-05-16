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
		int test[] = new int[2];
        // Force an exception
		test[0] = test[1] + test[2];
        //throw new Error("Testing testing 123");
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
