package js.tinyvm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import js.common.CLIToolProgressMonitor;
import js.common.ToolProgressMonitor;
import js.tinyvm.util.TinyVMCommandLineParser;

import org.apache.commons.cli.CommandLine;

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

	/**
	 * Execute tiny vm.
	 * 
	 * @param args
	 *            command line
	 * @throws TinyVMException
	 */
	public void start(String[] args) throws TinyVMException {
		assert args != null : "Precondition: args != null";

		CommandLine commandLine = fParser.parse(args);

		// options
		boolean verbose = commandLine.hasOption("v");
		String classpath = commandLine.getOptionValue("cp");
		String output = commandLine.getOptionValue("o");
		boolean all = commandLine.hasOption("a");
        boolean debug = commandLine.hasOption("g");
		boolean bigEndian = "be".equalsIgnoreCase(commandLine
				.getOptionValue("wo"));

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
			link(classpath, classes, all, stream, bigEndian, debug);
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