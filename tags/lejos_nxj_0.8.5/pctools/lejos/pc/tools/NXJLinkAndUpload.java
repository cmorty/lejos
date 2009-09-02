package lejos.pc.tools;

import js.common.CLIToolProgressMonitor;
import js.common.ToolProgressMonitor;
import js.tinyvm.TinyVM;
import lejos.pc.comm.*;

/**
 * 
 * Command-line utility that links and uploads NXJ programs in one call
 * 
 * @author Lawrie Griffiths
 * 
 */
public class NXJLinkAndUpload extends NXTCommLoggable {
	private NXJCommandLineParser fParser;
	private Upload fUpload;
	private TinyVM fTinyVM;

	public NXJLinkAndUpload() {
		super();
		fParser = new NXJCommandLineParser();
		fUpload = new Upload(); 
		fTinyVM = new TinyVM();
		fTinyVM.addProgressMonitor(new CLIToolProgressMonitor());
	}

	/**
	 * Main entry point for command line usage
	 * 
	 * @param args the command line arguments
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

	/**
	 * Run the utility. 
	 * Note that this can be called from other tools such as the Eclipse plug-in.
	 * 
	 * @param args the command-line arguments
	 *
	 * @throws js.tinyvm.TinyVMException
	 * @throws NXJUploadException
	 */
	public void run(String[] args) throws js.tinyvm.TinyVMException, NXJUploadException {
		// process arguments
		if (!fParser.parseOrHelp(NXJLinkAndUpload.class, args))
			return;

		String binName = fParser.getOutput();
		boolean run = fParser.isRun();
		boolean blueTooth = fParser.isBluetooth();
		boolean usb = fParser.isUSB();
		String name = fParser.getName();
		String address = fParser.getAddress();
		String[] args1 = fParser.getRestArgs();
		String firstArg = args1[0];

		// System.out.println("Arg count is " + argCount);

		// Build the linker arguments
		if (binName == null)
		{
			//extract classname, throw away packagename
			int i = firstArg.lastIndexOf('.') + 1;
			if (i < 0)
				i = 0;
			
			binName = firstArg.substring(i) + ".nxj";
		}
		
		String classpath = TinyVM.joinCP(fParser.getBP(), fParser.getCP());

		// link
		log("Linking...");
		//TODO switch to streams or temp file if no filename was given
		fTinyVM.start(classpath, args1, fParser.isAll(), binName, fParser.isBigEndian(),
				fParser.isDebug(), fParser.getRunTimeOptions(), fParser.isVerbose());
		
		// upload
		log("Uploading...");
		int protocols = 0;

		if (blueTooth) protocols |= NXTCommFactory.BLUETOOTH;		
		if (usb) protocols |= NXTCommFactory.USB;

		fUpload.upload(name, address, protocols, binName, run);
	}

	/**
	 * Register log listener
	 * 
	 * @param listener
	 */
	public void addToolsLogListener(ToolsLogListener listener) {
		fLogListeners.add(listener);
		fUpload.addLogListener(listener);
	}
	
	/**
	 * Unregister log listener
	 * 
	 * @param listener
	 */
	public void removeToolsLogListener(ToolsLogListener listener) {
		fLogListeners.remove(listener);
		fUpload.removeLogListener(listener);
	}

	/**
	 * Register monitor
	 * 
	 * @param listener
	 */
	public void addMonitor(ToolProgressMonitor monitor) {
		fTinyVM.addProgressMonitor(monitor);
	}

	/**
	 * Unregister monitor
	 * 
	 * @param listener
	 */
	public void removeMonitor(ToolProgressMonitor monitor) {
		fTinyVM.removeProgressMonitor(monitor);
	}
}
