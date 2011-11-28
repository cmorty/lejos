package lejos.pc.tools;

/**
 * @deprecated use lejos.pc.tools.NXJLink instead
 */
@Deprecated
public class ConsoleViewer
{
	public static void main(String[] args)
	{
		System.err.println("Upgrade your build file. Use the class lejos.pc.tools.NXJConsoleViewer");
		System.err.println("instead of lejos.pc.tools.ConsoleViewer.");
		System.exit(1);
	}
}