package js.tinyvm;

/**
 * @deprecated use lejos.pc.tools.NXJLink instead
 */
@Deprecated
public class TinyVM extends TinyVMTool
{
	public static void main(String[] args)
	{
		System.err.println("Upgrade your build file. Use the class lejos.pc.tools.NXJLink");
		System.err.println("instead of js.tinyvm.TinyVM.");
		System.exit(1);
	}
}