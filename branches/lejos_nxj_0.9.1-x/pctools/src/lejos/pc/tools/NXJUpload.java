package lejos.pc.tools;

import java.io.File;
import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.SystemContext;

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
	public static void main(String[] args)
	{
		ToolStarter.startTool(NXJUpload.class, args);
	}

	public static int start(String[] args) throws Exception
	{
		NXJUpload instance = new NXJUpload();
		instance.addLogListener(new ToolsLogger());
		return instance.run(args);
	}
	
	/** 
	 * Run the utility.
	 * Note that this method can be called from other tools such as the Eclipse plug-in.
	 * 
	 * @param args the command line arguments
	 * 
	 * @throws js.tinyvm.TinyVMException
	 * @throws NXTNotFoundException
	 */
	private int run(String[] args) throws IOException {
		NXJUploadCommandLineParser fParser = new NXJUploadCommandLineParser(NXJUpload.class, "[options] filename [more filenames]");
		CommandLine commandLine;
		
		try
		{
			commandLine = fParser.parse(args);
		}
		catch (ParseException e)
		{
			fParser.printHelp(SystemContext.err, e);
			return 1;
		}
		
		if (commandLine.hasOption("h"))
		{
			fParser.printHelp(SystemContext.out);
			return 0;
		}
		
		int protocols = 0;
		boolean run = commandLine.hasOption("r");
		boolean blueTooth = commandLine.hasOption("b");
		boolean usb = commandLine.hasOption("u");
		String name = AbstractCommandLineParser.getLastOptVal(commandLine, "n");
		String address = AbstractCommandLineParser.getLastOptVal(commandLine, "d");
		
		if (blueTooth) protocols |= NXTCommFactory.BLUETOOTH;
		if (usb) protocols |= NXTCommFactory.USB;

		String[] files = commandLine.getArgs();
		for (int i=0; i<files.length; i++)
		{
			File inputFile = new File(files[i]);
			String nxtFileName = inputFile.getName();
			
			if (nxtFileName.length() > NXTCommand.MAX_FILENAMELENGTH)
			{
				SystemContext.err.println("Filename must not be larger than "+NXTCommand.MAX_FILENAMELENGTH+" characters.");
				return 1;
			}
			
			//TODO improve dirty hack: open connection only once and reuse it
			try
			{
				fUpload.upload(name, address, protocols, inputFile, nxtFileName, run && (i == files.length - 1));
			}
			catch (NXTNotFoundException e)
			{
				SystemContext.err.println(e.getMessage());
				return 1;
			}
		}
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
