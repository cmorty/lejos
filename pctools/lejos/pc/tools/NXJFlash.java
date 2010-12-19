/**
 * Command to write the leJOS Virtual Machine and Menu system to the NXT Flash.
 */
package lejos.pc.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;
import lejos.pc.comm.NXTSamba;

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
			return choice - '0';
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
		try
		{
			parser.parse(args);
		}
		catch (ParseException e)
		{
			parser.printHelp(System.err, e);
			return 1;
		}
		
		if (parser.isHelp())
		{
			parser.printHelp(System.out);
			return 0;
		}
		
		//TODO what about quiet option ?
		
		byte[] memoryImage;
		byte[] fs;
		
		if (parser.isBinary())
		{
			memoryImage = createFileImage(parser.getFirmwareFile());
			fs = null;
		}
		else
		{
			File vmFile = parser.getFirmwareFile();
			File menuFile = parser.getMenuFile();
			
			String home = System.getProperty("nxj.home");
			if (home == null)
				home = System.getenv("NXJ_HOME");
			
			memoryImage = updater.createFirmwareImage(vmFile, menuFile, home);
			if (parser.doFormat())
				fs = updater.createFilesystemImage();
			else
				fs = null;
		}
		
		NXTSamba nxt = openDevice();
		if (nxt != null) {
			updater.updateDevice(nxt, memoryImage, fs, true, true, true);
		}
		return 0;
	}

	private byte[] createFileImage(File file) throws IOException
	{
		FileInputStream in = new FileInputStream(file);
		try
		{
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			
			byte[] buf = new byte[4096];
			while (true)
			{
				int len = in.read(buf, 0 , buf.length);
				if (len < 0)
					break;
				
				os.write(buf, 0, len);
				if (os.size() > NXTSamba.FLASH_SIZE)
					throw new IOException("file is too large for flash memory");
			}
			
			return os.toByteArray();
		}
		finally
		{
			in.close();
		}
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
