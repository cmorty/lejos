package js.tinyvm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import js.common.CLIToolProgressMonitor;
import js.common.ToolProgressMonitor;
import js.tinyvm.util.TinyVMCommandLineParser;

/**
 * Tiny VM.
 */
public class TinyVM extends TinyVMTool {
	private TinyVMCommandLineParser fParser;

	/**
	 * Main entry point for command line usage.
	 * 
	 * @param args
	 *            command line
	 */
	public static void main(String[] args) {
		try {
			//TinyVM tinyVM = new TinyVM(new CLIToolProgressMonitor());
			TinyVM tinyVM = new TinyVM();
			tinyVM.addProgressMonitor(new CLIToolProgressMonitor());
			tinyVM.start(args);
		} catch (TinyVMException e) {
			System.err.println(e.getMessage());
            e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Constructor.
	 */
	// public TinyVM (ToolProgressMonitor monitor)
	public TinyVM() {
		// super(monitor);
		fParser = new TinyVMCommandLineParser(true);
	}
	
	public static String joinCP(String cp1, String cp2)
	{
		if (cp1.length() > 0)
		{
			if (cp2.length() > 0)
				return cp1+File.pathSeparatorChar+cp2;
			
			return cp1;
		}
		return cp2;
	}

	/**
	 * Execute tiny vm.
	 * 
	 * @param args
	 *            command line
	 * @throws TinyVMException
	 */
	public void start(String[] args) throws TinyVMException {
		assert args != null : "Precondition: args != null";

		if (!fParser.parseOrHelp(TinyVM.class, args))
			return;

		String classpath = fParser.getCP();
		String bootclasspath = fParser.getBP();
		boolean bigEndian = fParser.isBigEndian();
		
		// options
		boolean verbose = fParser.isVerbose();
		String output = fParser.getOutput();
		boolean all = fParser.isAll();
        boolean debug = fParser.isDebug();
        int options = fParser.getRunTimeOptions();
		// files
		String[] classes = fParser.getRestArgs();

		start(joinCP(bootclasspath, classpath), classes, all, output, bigEndian, debug, options, verbose);
	}

	public void start(String classpath, String[] classes, boolean all, String output, boolean bigEndian,
			boolean debug, int options, boolean verbose) throws TinyVMException {
		// verbosity
		for(ToolProgressMonitor monitor : _monitors) {
			monitor.setVerbose(verbose);
		}
		//((CLIToolProgressMonitor) getProgressMonitor()).setVerbose(verbose);

		OutputStream stream = null;
		try {
			stream = output == null ? (OutputStream) System.out
					: (OutputStream) new FileOutputStream(output);
			link(classpath, classes, all, stream, bigEndian, options, debug);
		} catch (FileNotFoundException e) {
			throw new TinyVMException(e.getMessage(), e);
		} finally {
			if (stream instanceof FileOutputStream) {
				try {
					stream.close();
				} catch (IOException e) {
					throw new TinyVMException(e);
				}
			}
		}
	}
}