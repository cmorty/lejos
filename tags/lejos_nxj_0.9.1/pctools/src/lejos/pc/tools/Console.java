package lejos.pc.tools;

/**
 * @deprecated use lejos.pc.tools.NXJConsole instead
 */
@Deprecated
public class Console
{
	public static void main(String[] args)
	{
		System.err.println("Upgrade your build file. Use the class lejos.pc.tools.NXJConsole");
		System.err.println("instead of lejos.pc.tools.Console.");
		System.exit(1);
	}
}