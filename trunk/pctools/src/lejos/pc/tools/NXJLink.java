package lejos.pc.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import js.common.ToolProgressMonitor;
import js.tinyvm.TinyVMException;
import js.tinyvm.TinyVMTool;

import org.apache.commons.cli.ParseException;

/**
 * Tiny VM.
 */
public class NXJLink
{
	private TinyVMTool fTinyVM;
	
	/**
	 * Main entry point for command line usage.
	 * 
	 * @param args command line
	 */
	public static void main(String[] args)
	{
		ToolStarter.startTool(NXJLink.class, args);
	}

	public static int start(String[] args) throws Exception
	{
		return new NXJLink().run(args);
	}
	
	/**
	 * Constructor.
	 */
	// public TinyVM (ToolProgressMonitor monitor)
	public NXJLink()
	{
		fTinyVM = new TinyVMTool();
	}

	private static String joinCP(String cp1, String cp2)
	{
		if (cp1.length() > 0)
		{
			if (cp2.length() > 0)
				return cp1 + File.pathSeparatorChar + cp2;

			return cp1;
		}
		return cp2;
	}

	/**
	 * Execute tiny vm.
	 * 
	 * @param args command line
	 * @throws TinyVMException
	 */
	public int run(String[] args) throws TinyVMException, IOException
	{
		assert args != null : "Precondition: args != null";

		NXJLinkCommandLineParser fParser = new NXJLinkCommandLineParser(NXJLink.class, true);
		try
		{
			fParser.parse(args);
		}
		catch (ParseException e)
		{
			fParser.printHelp(System.err, e);
			return 1;
		}

		if (fParser.isHelp())
		{
			fParser.printHelp(System.out);
			return 0;
		}

		String classpath = fParser.getCP();
		String bootclasspath = fParser.getBP();
		boolean bigEndian = fParser.isBigEndian();

		// options
		boolean verbose = fParser.isVerbose();
		String output = fParser.getOutput();
		String debugFile = fParser.getDebugFile();
		boolean all = fParser.isAll();
		int debug = fParser.getDebugOptions();
		int options = fParser.getRunTimeOptions();
		// files
		String[] classnames = fParser.getClassNames();

		FileOutputStream stream = new FileOutputStream(output);
		try
		{
			start(bootclasspath, classpath, classnames, all, stream, debugFile, bigEndian, debug,
				options, verbose);
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				// ignore
			}
		}
		return 0;
	}

	void start(String bootclasspath, String classpath, String[] classes, boolean all,
		FileOutputStream stream, String debugFile, boolean bigEndian, int debug, int options,
		boolean verbose) throws FileNotFoundException, TinyVMException
	{
		FileOutputStream debugStream = null;
		try
		{
			if (debugFile != null)
				debugStream = new FileOutputStream(debugFile);

			CLIToolProgressMonitor pm = new CLIToolProgressMonitor();
			pm.setVerbose(verbose);

			TinyVMTool tinyVM = new TinyVMTool();
			tinyVM.addProgressMonitor(pm);

			tinyVM.link(joinCP(bootclasspath, classpath), classes, all, stream, bigEndian, options,
				debug, debugStream);
		}
		finally
		{
			if (debugStream != null)
			{
				try
				{
					debugStream.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
	}
	
	/**
	 * Register log listener
	 * 
	 * @param listener
	 */
	public void addProgressMonitor(ToolProgressMonitor monitor)
	{
		fTinyVM.addProgressMonitor(monitor);
	}

	/**
	 * Unregister log listener
	 * 
	 * @param listener
	 */
	public void removeProgressMonitor(ToolProgressMonitor monitor)
	{
		fTinyVM.removeProgressMonitor(monitor);
	}
}