/**
 * Command to write the leJOS Virtual Machine and Menu system to the NXT Flash.
 */
package lejos.pc.tools;

import lejos.pc.comm.*;
import java.io.*;



import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author andy
 */
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
	public int run(String[] args) throws Exception {
		NXJFlashCommandLineParser parser = new NXJFlashCommandLineParser(NXJFlash.class, "[options] [firmware-file] [startup-menu-file]");
		CommandLine commandLine;
		try
		{
			commandLine = parser.parse(args);
		}
		catch (ParseException e)
		{
			System.err.println(e.getMessage());
			parser.printHelp(System.err);
			return 1;
		}
		
		if (commandLine.hasOption("h"))
		{
			parser.printHelp(System.out);
			return 0;
		}
		
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
		return 0;
	}

	/**
	 * @param args
	 *            the command line arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		int r;
		try {
			r = new NXJFlash().run(args);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			r = 1;
		}
		System.exit(r);
	}
}
