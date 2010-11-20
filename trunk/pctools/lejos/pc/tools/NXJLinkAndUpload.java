package lejos.pc.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import js.tinyvm.TinyVMException;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommLoggable;

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
		int r;
		try
		{
			NXJLinkAndUpload instance = new NXJLinkAndUpload();
			instance.addToolsLogListener(new ToolsLogger());
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
	 * Run the utility. Note that this can be called from other tools such as
	 * the Eclipse plug-in.
	 * 
	 * @param args the command-line arguments
	 * @throws js.tinyvm.TinyVMException
	 * @throws NXJUploadException
	 */
	private int run(String[] args) throws TinyVMException, NXJUploadException, IOException
	{
		// process arguments
		try
		{
			fParser.parse(args);
		}
		catch (ParseException e)
		{
			System.err.println(e.getMessage());
			fParser.printHelp(System.err);
			return 1;
		}

		if (fParser.isHelp())
		{
			fParser.printHelp(System.out);
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

			fUpload.upload(name, address, protocols, outputFile, nxtFileName, run);
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
