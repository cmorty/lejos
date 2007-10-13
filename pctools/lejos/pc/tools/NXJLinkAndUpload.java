package lejos.pc.tools;

import java.util.ArrayList;
import java.util.Collection;

import js.common.CLIToolProgressMonitor;
import js.common.ToolProgressMonitor;
import js.tinyvm.TinyVM;
import lejos.pc.comm.NXTCommFactory;

import org.apache.commons.cli.CommandLine;

/**
 * Links and uploads NXJ programs in one call
 * 
 * @author Lawrie Griffiths
 * 
 */
public class NXJLinkAndUpload {

	private Collection<ToolsLogListener> fLogListeners;
	private NXJCommandLineParser fParser;
	private Upload fUpload;
	private TinyVM fTinyVM;


	public NXJLinkAndUpload() {
		fParser = new NXJCommandLineParser();
		fLogListeners = new ArrayList<ToolsLogListener>();
		fUpload = new Upload(); 
		fTinyVM = new TinyVM();
		fTinyVM.addProgressMonitor(new CLIToolProgressMonitor());

	}

	/**
	 * Main entry point for command line usage
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NXJLinkAndUpload instance = new NXJLinkAndUpload();
			instance.addToolsLogListener(new ToolsLogger());
			instance.run(args);
		} catch (Throwable t) {
			System.err.println("an error occurred: " + t.getMessage());
		}
	}

	public void run(String[] args) throws js.tinyvm.TinyVMException,
			NXJUploadException {
		// process arguments
		CommandLine commandLine = fParser.parse(args);
		String binName = commandLine.getOptionValue("o");
		boolean run = commandLine.hasOption("r");
		boolean blueTooth = commandLine.hasOption("b");
		boolean usb = commandLine.hasOption("u");
		String name = commandLine.getOptionValue("n");
		String address = commandLine.getOptionValue("d");
		String tinyVMArgs[];

		String firstArg = commandLine.getArgs()[0];

		int argCount = 0;

		// Count the arguments for the linker
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-b"))
				continue;
			if (args[i].equals("--bluetooth"))
				continue;
			if (args[i].equals("-u"))
				continue;
			if (args[i].equals("--usb"))
				continue;
			if (args[i].equals("-n")) {
				i++;
				continue;
			}
			if (args[i].equals("--name")) {
				i++;
				continue;
			}
			if (args[i].equals("-d")) {
				i++;
				continue;
			}
			if (args[i].equals("--address")) {
				i++;
				continue;
			}
			if (args[i].equals("-r"))
				continue;
			if (args[i].equals("--run"))
				continue;
			argCount++;
		}

		// System.out.println("Arg count is " + argCount);

		// Build the linker arguments
		int index = 0;
		tinyVMArgs = new String[argCount + 2];

		if (binName == null)
			binName = firstArg + ".nxj";

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-b"))
				continue;
			if (args[i].equals("--bluetooth"))
				continue;
			if (args[i].equals("-u"))
				continue;
			if (args[i].equals("--usb"))
				continue;
			if (args[i].equals("-n")) {
				i++;
				continue;
			}
			if (args[i].equals("--name")) {
				i++;
				continue;
			}
			if (args[i].equals("-d")) {
				i++;
				continue;
			}
			if (args[i].equals("--address")) {
				i++;
				continue;
			}
			if (args[i].equals("-r"))
				continue;
			if (args[i].equals("--run"))
				continue;
			tinyVMArgs[index++] = args[i];
		}
		tinyVMArgs[argCount] = "-o";
		tinyVMArgs[argCount + 1] = binName;


		// link
		log("Linking...");
		fTinyVM.start(tinyVMArgs);

		// upload
		log("Uploading...");
		int protocols = 0;

		if (blueTooth)
			protocols |= NXTCommFactory.BLUETOOTH;
		if (usb)
			protocols |= NXTCommFactory.USB;

		fUpload.upload(name, address, protocols, binName, run);
	}

	/**
	 * register log listener
	 * @param listener
	 */
	public void addToolsLogListener(ToolsLogListener listener) {
		fLogListeners.add(listener);
		fUpload.addLogListener(listener);
	}
	
	/**
	 * unregister log listener
	 * @param listener
	 */
	public void removeToolsLogListener(ToolsLogListener listener) {
		fLogListeners.remove(listener);
		fUpload.removeLogListener(listener);
	}

	/**
	 * register monitor
	 * @param listener
	 */
	public void addMonitor(ToolProgressMonitor monitor) {
		fTinyVM.addProgressMonitor(monitor);
	}

	/**
	 * deregister monitor
	 * @param listener
	 */
	public void removeMonitor(ToolProgressMonitor monitor) {
		fTinyVM.removeProgressMonitor(monitor);
	}

	private void log(String message) {
		for (ToolsLogListener listener : fLogListeners) {
			listener.logEvent(message);
		}
	}

}
