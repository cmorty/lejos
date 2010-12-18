package lejos.pc.tools;

import java.io.File;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommLoggable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 * Command-line utility to upload a linked binary to the NXT.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NXJUpload {
	private Upload fUpload = new Upload();

	/**
	 * Main entry point
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		int r;
		try
		{
			NXJUpload instance = new NXJUpload();
			instance.addLogListener(new ToolsLogger());
			r = instance.run(args);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			r = 1;
		}
		System.exit(r);
	}
	
	/** 
	 * Run the utility.
	 * Note that this method can be called from other tools such as the Eclipse plug-in.
	 * 
	 * @param args the command line arguments
	 * 
	 * @throws js.tinyvm.TinyVMException
	 * @throws NXJUploadException
	 */
	private int run(String[] args) throws NXJUploadException {
		NXJUploadCommandLineParser fParser = new NXJUploadCommandLineParser(NXJUpload.class, "[options] filename");
		CommandLine commandLine;
		
		try
		{
			commandLine = fParser.parse(args);
		}
		catch (ParseException e)
		{
			fParser.printHelp(System.err, e);
			return 1;
		}
		
		if (commandLine.hasOption("h"))
		{
			fParser.printHelp(System.out);
			return 0;
		}
		
		int protocols = 0;
		boolean run = commandLine.hasOption("r");
		boolean blueTooth = commandLine.hasOption("b");
		boolean usb = commandLine.hasOption("u");
		String name = commandLine.getOptionValue("n");
		String address = commandLine.getOptionValue("d");
		
		String fileName = commandLine.getArgs()[0];
		File inputFile = new File(fileName);
		String nxtFileName = inputFile.getName();
		
		if (blueTooth) protocols |= NXTCommFactory.BLUETOOTH;
		if (usb) protocols |= NXTCommFactory.USB;
		
		fUpload.upload(name, address, protocols, inputFile, nxtFileName, run);
		return 0;
	}	
	
	/**
	 * Register log listener
	 * 
	 * @param listener
	 */
	public void addLogListener(ToolsLogListener listener) {
		fUpload.addLogListener(listener);
	}
	
	/**
	 * Unregister log listener
	 * 
	 * @param listener
	 */
	public void removeLogListener(ToolsLogListener listener) {
		fUpload.removeLogListener(listener);
	}
}
