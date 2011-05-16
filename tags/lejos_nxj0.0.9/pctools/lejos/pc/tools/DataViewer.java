package lejos.pc.tools;

/**
 * @deprecated use lejos.pc.tools.NXJLink instead
 */
@Deprecated
public class DataViewer
{
	public static void main(String[] args)
	{
		System.err.println("Upgrade your build file. Use the class lejos.pc.tools.NXJDataViewer");
		System.err.println("instead of lejos.pc.tools.DataViewer.");
		System.exit(1);
	}
}