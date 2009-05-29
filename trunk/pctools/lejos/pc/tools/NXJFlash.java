/**
 * Command to write the leJOS Virtual Machine and Menu system to the NXT Flash.
 */
package lejos.pc.tools;

import lejos.pc.comm.*;
import java.io.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import js.tinyvm.TinyVMException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author andy
 */
/**
 * CommandLineParser
 */
class CommandLineParser {

	/**
	 * Parse commandline.
	 * 
	 * @param args
	 *            command line
	 * @return the parsed commands
	 * @throws TinyVMException
	 */
	public CommandLine parse(String[] args) throws TinyVMException {
		Options options = new Options();
		options.addOption("h", "help", false, "help");
		options.addOption("f", "format", false, "format file system");
		options.addOption("v", "verify", false, "backward compatibility switch (verify is now default)");
		options.addOption("q", "quiet", false,
				"quiet mode - do not report progress");

		CommandLine result;
		try {
			try {
				result = new GnuParser().parse(options, args);
			} catch (ParseException e) {
				System.out.println("Parse error " + e);
				throw new TinyVMException(e.getMessage(), e);
			}

			if (result.hasOption("h"))
				throw new TinyVMException("Help:");

			if (result.getArgs().length == 1)
				throw new TinyVMException(
						"Must provide firmware and menu files");

			if (result.getArgs().length > 2)
				throw new TinyVMException("Too many files");
		} catch (TinyVMException e) {
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			printWriter.println(e.getMessage());

			String commandName = System.getProperty("COMMAND_NAME",
					"java lejos.pc.tools.NXJFlash");

			String usage = commandName + " [options] [firmware menu]";
			new HelpFormatter().printHelp(printWriter, 80, usage.toString(),
					null, options, 0, 2, null);

			throw new TinyVMException(writer.toString());
		}
		return result;
	}
}

public class NXJFlash implements NXJFlashUI {
	NXJFlashUpdate updater = new NXJFlashUpdate(this);

	public void message(String str) {
		System.out.println(str);
	}

	public void progress(String str, int percent) {
		System.out.printf("%s %3d%%\r", str, percent);
		System.out.flush();
	}

	int getChoice() throws IOException {
		// flush any old input
		while (System.in.available() > 0)
			System.in.read();
		char choice = (char) System.in.read();
		// flush any old input
		while (System.in.available() > 0)
			System.in.read();
		if (choice >= '0' && choice <= '9')
			return (int) (choice - '0');
		return -1;
	}

	/**
	 * Locate and open an NXT device ready for the firmware to be updated. First
	 * we look for devices that are already in SAM-BA mode. If we do not find
	 * any we look for devices in normal mode and attempt to re-boot them into
	 * SAM-BA mode.
	 * 
	 * @param commandLine
	 * @return
	 */
	NXTSamba openDevice() throws NXTCommException, IOException {
		// First look to see if there are any devices already in SAM-BA mode
		NXTSamba samba = updater.openSambaDevice(0);

		// Look for devices in SAM-BA mode
		if (samba == null) {
			NXTInfo[] nxts;
			System.out
					.println("No devices in firmware update mode were found.\nSearching for other NXT devices.");
			NXTConnector conn = new NXTConnector();
			nxts = conn.search(null, null, NXTCommFactory.USB);
			if (nxts.length <= 0) {
				System.out
						.println("No NXT found. Please check that the device is turned on and connected.");
				return null;
			}
			int devNo = 0;
			do {
				System.out
						.println("The following NXT devices have been found:");
				for (int i = 0; i < nxts.length; i++)
					System.out.println("  " + (i + 1) + ":  " + nxts[i].name
							+ "  " + nxts[i].deviceAddress);
				System.out
						.println("Select the device to update, or enter 0 to exit.");
				System.out.print("Device number to update: ");
				devNo = getChoice();
			} while (devNo < 0 || devNo > nxts.length);
			if (devNo == 0)
				return null;
			updater.resetDevice(nxts[devNo - 1]);
			samba = updater.openSambaDevice(30000);
		}
		if (samba == null)
			System.out
					.println("No NXT found. Please check that the device is turned on and connected.");
		return samba;
	}

	/**
	 * Run the flash command.
	 * 
	 * @param args
	 *            the command line arguments
	 * @throws Exception
	 */
	public void run(String[] args) throws Exception {
		CommandLineParser parser = new CommandLineParser();
		CommandLine commandLine = parser.parse(args);
		String vmFile = null;
		String menuFile = null;
		if (commandLine.getArgs().length > 0)
			vmFile = commandLine.getArgs()[0];
		if (commandLine.getArgs().length > 1)
			menuFile = commandLine.getArgs()[1];
		String home = System.getProperty("nxj.home");
		if (home == null)
			home = System.getenv("NXJ_HOME");
		byte[] memoryImage = updater
				.createFirmwareImage(vmFile, menuFile, home);
		byte[] fs = null;
		if (commandLine.hasOption("f"))
			fs = updater.createFilesystemImage();
		NXTSamba nxt = openDevice();
		if (nxt != null) {
			updater.updateDevice(nxt, memoryImage, fs, true, true, true);
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		try {
			NXJFlash instance = new NXJFlash();
			instance.run(args);
		} catch (Throwable t) {
			System.err.println("an error occurred: " + t.getMessage());
		}
	}
}
