package js.tinyvm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import js.common.CLIToolProgressMonitor;
import js.common.ToolProgressMonitor;
import js.tinyvm.util.TinyVMCommandLineParser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

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
			System.exit(1);
		}
	}

	/**
	 * Constructor.
	 */
	// public TinyVM (ToolProgressMonitor monitor)
	public TinyVM() {
		// super(monitor);
		fParser = new TinyVMCommandLineParser();
	}
	
	private String joinCP(String cp1, String cp2)
	{
		if (cp1.length() > 0)
		{
			if (cp2.length() > 0)
				return cp1+File.pathSeparatorChar+cp2;
			
			return cp1;
		}
		return cp2;
	}

	private String mangleClassPath(String cp) throws ParseException
	{
		StringBuilder sb = new StringBuilder();
		
		int start = 0;
		int len = cp.length();
		
		while (start < len)
		{
			int end = cp.indexOf(File.pathSeparatorChar, start);
			if (end < 0)
				end = len;
			
			String file = cp.substring(start, end);
			File f = new File(file);
			
			if (!f.exists())
				throw new ParseException("File does not exist: "+file);
			
			if (start > 0)
				sb.append(File.pathSeparatorChar);
			
			//sb.append(f.getAbsolutePath());
			//sb.append(f.getCanonicalPath());
			sb.append(file);
			
			start = end+1;
		}
		
		return sb.toString();
	}
	
	private String getLastOptVal(CommandLine cmdline, String key)
	{
		String[] vals = cmdline.getOptionValues(key);
		if (vals == null || vals.length <= 0)
			return null;
		
		return vals[vals.length - 1];
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

		String classpath;
		String bootclasspath;
		
		CommandLine commandLine;
		try
		{
			commandLine= fParser.parse(args);
			
			bootclasspath = getLastOptVal(commandLine, "bp");
			classpath = getLastOptVal(commandLine, "cp");
			
			//TODO someday: parse Classpath and keep list of File objects instead of working with Strings
			bootclasspath = mangleClassPath(bootclasspath);
			classpath = mangleClassPath(classpath);			
		}
		catch (ParseException e)
		{
			System.out.println(e.getMessage());
			fParser.printHelp();
			return;
		}
		
		if (commandLine == null)
		{
			fParser.printHelp();
			return;
		}

		// options
		boolean verbose = commandLine.hasOption("v");
		String output = getLastOptVal(commandLine, "o");
		boolean all = commandLine.hasOption("a");
        boolean debug = commandLine.hasOption("g");
		boolean bigEndian = "be".equalsIgnoreCase(getLastOptVal(commandLine, "wo"));
		
		// files
		String[] classes = commandLine.getArgs();

		// verbosity
		for(ToolProgressMonitor monitor : _monitors) {
			monitor.setVerbose(verbose);
		}
		//((CLIToolProgressMonitor) getProgressMonitor()).setVerbose(verbose);

		OutputStream stream = null;
		try {
			stream = output == null ? (OutputStream) System.out
					: (OutputStream) new FileOutputStream(output);
			link(joinCP(bootclasspath, classpath), classes, all, stream, bigEndian, debug);
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