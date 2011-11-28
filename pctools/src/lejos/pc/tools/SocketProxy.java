package lejos.pc.tools;

/**
 * @deprecated use lejos.pc.tools.NXJLink instead
 */
@Deprecated
public class SocketProxy
{
	public static void main(String[] args)
	{
		System.err.println("Upgrade your build file. Use the class lejos.pc.tools.NXJSocketProxy");
		System.err.println("instead of lejos.pc.tools.SocketProxy.");
		System.exit(1);
	}
}