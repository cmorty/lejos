package lejos.pc.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import js.tinyvm.TinyVMException;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommLoggable;
import lejos.pc.comm.SystemContext;

import org.apache.commons.cli.ParseException;

/**
 * Command-line utility that links and uploads NXJ programs in one call
 * 
 * @author Lawrie Griffiths
 */
public class NXJLinkAndUpload extends NXTCommLoggable
{
	private NXJLinkAndUploadCommandLineParser fParser;
	private NXJLink fLink;
	private Upload fUpload;

	public NXJLinkAndUpload()
	{
		super();
		fParser = new NXJLinkAndUploadCommandLineParser(NXJLinkAndUpload.class);
		fUpload = new Upload();
		fLink = new NXJLink();
	}

	/**
	 * Main entry point for command line usage
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		ToolStarter.startTool(NXJLinkAndUpload.class, args);
	}

	public static int start(String[] args) throws Exception
	{
		NXJLinkAndUpload instance = new NXJLinkAndUpload();
		instance.addToolsLogListener(new ToolsLogger());
		return instance.run(args);
	}

	/**
	 * Run the utility. Note that this can be called from other tools such as
	 * the Eclipse plug-in.
	 * 
	 * @param args the command-line arguments
	 * @throws js.tinyvm.TinyVMException
	 * @throws NXTNotFoundException
	 */
	private int run(String[] args) throws TinyVMException, IOException
	{
		// process arguments
		try
		{
			fParser.parse(args);
		}
		catch (ParseException e)
		{
			fParser.printHelp(SystemContext.err, e);
			return 1;
		}

		if (fParser.isHelp())
		{
			fParser.printHelp(SystemContext.out);
			return 0;
		}

		String binName = fParser.getOutput();
		boolean run = fParser.isRun();
		boolean blueTooth = fParser.isBluetooth();
		boolean usb = fParser.isUSB();
		String name = fParser.getName();
		String address = fParser.getAddress();
		String[] classnames = fParser.getClassNames();
		String mainClass = classnames[0];

		File outputFile;
		boolean deleteOutputFile;
		if (binName == null)
		{
			outputFile = File.createTempFile("nxjlink", "nxj");
			deleteOutputFile = true;
		}
		else
		{
			outputFile = new File(binName);
			deleteOutputFile = false;
		}

		try
		{
			// link
			log("Linking...");			
			FileOutputStream stream = new FileOutputStream(outputFile);
			try
			{
				fLink.start(fParser.getBP(), fParser.getCP(), classnames, fParser.isAll(), stream,
					fParser.getDebugFile(), fParser.isBigEndian(), fParser.getDebugOptions(),
					fParser.getRunTimeOptions(), fParser.isVerbose());
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

			// upload
			log("Uploading...");
			int protocols = 0;

			if (blueTooth)
				protocols |= NXTCommFactory.BLUETOOTH;
			if (usb)
				protocols |= NXTCommFactory.USB;

			// Build the linker arguments
			String nxtFileName;
			if (binName != null)
				nxtFileName = outputFile.getName();
			else
			{
				// extract classname, throw away packagename
				int i = mainClass.lastIndexOf('.') + 1;
				if (i < 0)
					i = 0;

				nxtFileName = mainClass.substring(i) + ".nxj";
			}
			
			if (nxtFileName.length() > NXTCommand.MAX_FILENAMELENGTH)
			{
				SystemContext.err.println("Filename must not be larger than "+NXTCommand.MAX_FILENAMELENGTH+" characters.");
				return 1;
			}

			try
			{
				fUpload.upload(name, address, protocols, outputFile, nxtFileName, run);
			}
			catch (NXTNotFoundException e)
			{
				SystemContext.err.println(e.getMessage());
				return 1;
			}
			return 0;
		}
		finally
		{
			if (deleteOutputFile)
				outputFile.delete();
		}
	}

	/**
	 * Register log listener
	 * 
	 * @param listener
	 */
	public void addToolsLogListener(ToolsLogListener listener)
	{
		fLogListeners.add(listener);
		fUpload.addLogListener(listener);
	}

	/**
	 * Unregister log listener
	 * 
	 * @param listener
	 */
	public void removeToolsLogListener(ToolsLogListener listener)
	{
		fLogListeners.remove(listener);
		fUpload.removeLogListener(listener);
	}
}
