package lejos.pc.tools;

import lejos.pc.comm.*;
import lejos.nxt.remote.*;

import java.io.*;

/**
 * Class to allow the updating and verification of the leJOS firmware.
 * 
 * @author andy
 */
public class NXJFlashUpdate {

	private static final int MAX_FIRMWARE_PAGES = 368;
	private static final int TOTAL_PAGES = 1024;
	private static final int SETTINGS_PAGES = 1;
	private static final int DIRECTORY_PAGES = 2;
	private static final int MENU_ADDRESS_LOC = 0x40;
	private static final int MENU_LENGTH_LOC = MENU_ADDRESS_LOC + 4;
	private static final int FLASH_START_PAGE_LOC = MENU_LENGTH_LOC + 4;
	private static final String VM = "lejos_nxt_rom.bin";
	private static final String MENU = "StartUpText.bin";
	
	private NXJFlashUI ui;

	public NXJFlashUpdate(NXJFlashUI ui) {
		this.ui = ui;
	}

	/**
	 * Format and store a 32 bit value into a memory image.
	 * 
	 * @param mem
	 *            The image in which to store the value
	 * @param offset
	 *            The location in bytes in the image
	 * @param val
	 *            The value to be stored.
	 */
	void storeWord(byte[] mem, int offset, int val) {
		mem[offset++] = (byte) (val & 0xff);
		mem[offset++] = (byte) ((val >> 8) & 0xff);
		mem[offset++] = (byte) ((val >> 16) & 0xff);
		mem[offset++] = (byte) ((val >> 24) & 0xff);
	}

	/**
	 * Create the memory image ready to be flashed to the device. Load the
	 * firmware and menu images into memory ready for flashing. The command line
	 * provides details of the location of the image files to be used.
	 * 
	 * @param commandLine
	 *            Options for the location of the firmware and menu.
	 * @return Memory image ready to be flashed to the device.
	 */
	public byte[] createFirmwareImage(String vmName, String menuName,
			String leJOSHomeDir) throws IOException, FileNotFoundException {
		ui.message("Building firmware image.");
		byte[] memoryImage = new byte[MAX_FIRMWARE_PAGES * NXTSamba.PAGE_SIZE];
		String home = leJOSHomeDir;
		// String home = System.getProperty("nxj.home");
		// if (home == null)
		// home = System.getenv("NXJ_HOME");
		if (home == null)
			home = "";
		String SEP = System.getProperty("file.separator");
		if (vmName == null)
			vmName = home + SEP + "bin" + SEP + VM;
		if (menuName == null)
			menuName = home + SEP + "bin" + SEP + MENU;
		ui.message("VM file: " + vmName);
		ui.message("Menu file: " + menuName);
		FileInputStream vm = new FileInputStream(vmName);
		FileInputStream menu = new FileInputStream(menuName);
		int vmLen = vm.read(memoryImage, 0, memoryImage.length);
		// Round up to page and use as base for the menu location
		int menuStart = ((vmLen + NXTSamba.PAGE_SIZE - 1) / NXTSamba.PAGE_SIZE)
				* NXTSamba.PAGE_SIZE;
		// Read the menu. Note we may read less than the full size of the menu.
		// If so this will be caught by the overall size check below.
		int menuLen = menu.read(memoryImage, menuStart, memoryImage.length
				- menuStart);
		// We store the length and location of the Menu in special locations
		// that are known to the firmware.
		storeWord(memoryImage, MENU_LENGTH_LOC, menuLen);
		storeWord(memoryImage, MENU_ADDRESS_LOC, menuStart
				+ NXTSamba.FLASH_BASE);
		storeWord(memoryImage, FLASH_START_PAGE_LOC, MAX_FIRMWARE_PAGES);
		// Check overall size allow for size/length markers in last block.
		if (menuStart + menuLen >= memoryImage.length) {
			throw new IOException("Combined size of VM and Menu > "
					+ memoryImage.length);
		}
		ui.message("VM size: " + vmLen + " bytes.");
		ui.message("Menu size: " + menuLen + " bytes.");
		ui.message("Total image size " + (menuStart + menuLen) + "/"
				+ memoryImage.length + " bytes.");
		return memoryImage;
	}

	/**
	 * Create a memory image for the leJOS file system. We create an in memory
	 * image for the settinigs, and directory pages. We then fill the remainder
	 * of the space with a test pattern to help detect any flash memory problems
	 * 
	 * @return byte array containing the file system data
	 */
	public byte[] createFilesystemImage() {
		ui.message("Building filesystem image.");
		byte[] fs = new byte[NXTSamba.PAGE_SIZE
				* (TOTAL_PAGES - MAX_FIRMWARE_PAGES)];
		// First few pages are settings are directory, these must all be
		// cleared to zero. After that we fill with a test pattern, to help
		// spot any flash problems
		int addr = (SETTINGS_PAGES + DIRECTORY_PAGES) * NXTSamba.PAGE_SIZE;
		while (addr <= (fs.length - 32)) {
			storeWord(fs, addr, addr);
			addr += 4;
			storeWord(fs, addr, ~addr);
			addr += 4;
			storeWord(fs, addr, 0xf0f0f0f0);
			addr += 4;
			storeWord(fs, addr, 0x0f0f0f0f);
			addr += 4;
			storeWord(fs, addr, 0xaaaaaaaa);
			addr += 4;
			storeWord(fs, addr, 0x55555555);
			addr += 4;
			storeWord(fs, addr, 0x00000000);
			addr += 4;
			storeWord(fs, addr, 0xffffffff);
			addr += 4;
		}
		return fs;
	}

	/**
	 * Locate and open an nxt device in SAM-BA mode. If none are present wait up
	 * to timeout ms checking to see if one has become available.
	 * 
	 * @return
	 */
	public NXTSamba openSambaDevice(int timeout) throws NXTCommException,
			IOException {
		NXTSamba samba = new NXTSamba();

		ui.message("Locating device in firmware update mode.");
		// Look for devices in SAM-BA mode
		NXTInfo[] nxts = samba.search();
		if (nxts.length == 0) {
			for (int i = 0; i < timeout / 1000; i++) {
				nxts = samba.search();
				if (nxts.length > 0)
					break;
				try {
					ui.progress("Searching", (i * 100) / (timeout / 1000));
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
		}
		if (nxts.length > 1) {
			throw new NXTCommException(
					"Too many devices in firmware update mode.");
		}
		if (nxts.length == 0) {
			return null;
		}
		// Must be just the one. Try and open it!
		if (!samba.open(nxts[0])) {
			throw new NXTCommException("Failed to open device in SAM-BA mode.");
		}
		ui.message("Opened device in firmware update mode.");
		return samba;
	}

	/**
	 * Attempt to restart the nxt in SAM-BA mode.
	 * 
	 * @param nxt
	 *            The device to reset
	 * @throws lejos.pc.comm.NXTCommException
	 * @throws java.io.IOException
	 */
	public void resetDevice(NXTInfo nxt) throws NXTCommException, IOException {
		ui.message("Attempting to reboot the device.");
		NXTComm nxtComm = NXTCommFactory.createNXTComm(nxt.protocol);
		NXTCommand cmd = NXTCommand.getSingleton();
		if (!nxtComm.open(nxt, NXTComm.LCP)) {
			throw new NXTCommException("Failed to open device in command mode.");
		}
		cmd.setNXTComm(nxtComm);
		// Force into firmware update mode.
		cmd.boot();
		cmd.close();
	}
	
	private static int getPageAddr(int page)
	{
		return NXTSamba.FLASH_BASE + page * NXTSamba.PAGE_SIZE; 
	}

	/**
	 * Verify that the contents of the nxt flash memory match the supplied
	 * image.
	 * 
	 * @param nxt
	 *            device to verify
	 * @param first
	 *            starting address
	 * @param memoryImage
	 *            memory address to compare with
	 * @return number of mismatched bytes found
	 * @throws java.io.IOException
	 */
	public int verifyPages(NXTSamba nxt, int first, byte[] memoryImage) throws IOException {
		int failCnt = 0;
		int len = memoryImage.length;
		InputStream is = nxt.createInputStream(getPageAddr(first), len);
		try
		{
			int p = -1;
			for (int i = 0; i < len; i++)
			{
				int np = i * 100 / len;
				if (np > p)
				{
					p = np;
					ui.progress("Verifying", np);
				}
				
				int b = is.read();
				if (b < 0)
					throw new IOException("EOF came too soon");
				
				if ((byte)b != memoryImage[i]) {
					ui.message(String.format(
						"Verify failed at address 0x%08X: expected 0x%02X, found 0x%02X\n",
						i,	memoryImage[i] & 0xff,	b));
					failCnt++;
				}
			}
		}
		finally
		{
			is.close();
		}
		ui.progress("", 0);
		if (failCnt == 0)
			ui.message("Verified " + memoryImage.length + " bytes ok.");
		else
			ui.message("Failed to verify " + failCnt + " of " + memoryImage.length + " bytes.");
		return failCnt;
	}

	public void writePages(NXTSamba nxt, int first, byte[] memoryImage)
			throws IOException {
		int pages = memoryImage.length / NXTSamba.PAGE_SIZE;
		int p = -1;
		for (int page = 0; page < pages; page++) {
			int np = page * 100 / pages;
			if (np > p)
			{
				p = np;
				ui.progress("Writing", np);
			}
			nxt.writePage(first + page, memoryImage, page	* NXTSamba.PAGE_SIZE);
		}
		
		//workaround the problem, that verification and rebooting fails directly after write
		nxt.readWord(getPageAddr(first));

		ui.progress("", 0);
	}

	/**
	 * Update the NXT with the new memory image.
	 * 
	 * @param nxt
	 *            Device to update, must be open in SAM-BA mode.
	 * @param memoryImage
	 *            New image for the device
	 * @param commandLine
	 *            Update options
	 */
	public void writeFirmware(NXTSamba nxt, byte[] memoryImage)
			throws IOException {
		ui.message("Unlocking pages.");
		nxt.unlockAllPages();
		ui.message("Writing firmware image.");
		writePages(nxt, 0, memoryImage);
	}

	/**
	 * Format the nxt file system.
	 * 
	 * @param nxt
	 *            Device to format
	 * @param fs
	 *            File system image to use
	 * @throws java.io.IOException
	 */
	public void writeFilesystem(NXTSamba nxt, byte[] fs) throws IOException {
		ui.message("Unlocking pages.");
		nxt.unlockAllPages();
		ui.message("Writing filesystem image.");
		writePages(nxt, MAX_FIRMWARE_PAGES, fs);
	}

	/**
	 * Verify the firware downloaded to the device.
	 * 
	 * @param nxt
	 *            device to verify
	 * @param image
	 *            firmware image to compare against
	 * @return the number of mismatched bytes.
	 * @throws java.io.IOException
	 */
	public int verifyFirmware(NXTSamba nxt, byte[] image) throws IOException {
		ui.message("Verifying firmware.");
		return verifyPages(nxt, 0, image);
	}

	public int verifyFilesystem(NXTSamba nxt, byte[] fs) throws IOException {
		ui.message("Verifying filesystem.");
		return verifyPages(nxt, MAX_FIRMWARE_PAGES, fs);
	}

	public void rebootDevice(NXTSamba nxt) throws IOException {
		ui.message("Restarting the device.");
		nxt.reboot();
		nxt.close();
	}

	/**
	 * Update the NXT with the new memory image.
	 * 
	 * @param nxt
	 *            Device to update, must be open in SAM-BA mode.
	 * @param memoryImage
	 *            New firmware image for the device
	 * @param fs
	 *            File system image.
	 * @param verify
	 *            Should we verify the updates?
	 */
	public void updateDevice(NXTSamba nxt, byte[] memoryImage, byte[] fs,
			boolean verify) throws IOException {
		updateDevice(nxt, memoryImage, fs, verify, verify, true);
	}

	public void updateDevice(NXTSamba nxt, byte[] memoryImage, byte[] fs,
			boolean verifyFirm, boolean verifyFS, boolean reboot) throws IOException {
		if (memoryImage != null) {
			writeFirmware(nxt, memoryImage);
			if (verifyFirm)
				verifyFirmware(nxt, memoryImage);
		}
		if (fs != null) {
			writeFilesystem(nxt, fs);
			if (verifyFS)
				verifyFilesystem(nxt, fs);
		}
		if (reboot) {
			rebootDevice(nxt);
		}
	}

}
