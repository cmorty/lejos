package org.lejos.nxt.ldt.actions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommand;
import lejos.pc.comm.NXTInfo;
import lejos.pc.comm.NXTSamba;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.lejos.nxt.ldt.LeJOSNXJPlugin;
import org.lejos.nxt.ldt.preferences.PreferenceConstants;
import org.lejos.nxt.ldt.util.LeJOSNXJException;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * 
 * @see IWorkbenchWindowActionDelegate
 * @author Matthias Paul Scholz
 */
public class LeJOSUploadFirmwareAction implements
		IWorkbenchWindowActionDelegate {

	/**
	 * The constructor.
	 */
	public LeJOSUploadFirmwareAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		// open progress monitor
		IWorkbench wb = PlatformUI.getWorkbench();
		IProgressService ps = wb.getProgressService();
		try {
			ps.busyCursorWhile(new IRunnableWithProgress() {
				public void run(IProgressMonitor pm) {
					try {
						// upload firmware
						pm.beginTask("Uploading firmware...",
								IProgressMonitor.UNKNOWN);
						flashFirmware();
						pm.done();
						// if (result == 0)
						// LeJOSNXJUtil
						// .message(
						// "firmware has been successfully uploaded to the NXT brick"
						// );
					} catch (Throwable t) {
						// log
						LeJOSNXJUtil.message(t);
					}
				}
			});
		} catch (Throwable t) {
			// log
			LeJOSNXJUtil.message(t);
		}
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
	}

	private void flashFirmware() throws LeJOSNXJException {
		// TODO show progress monitor
		try {
			// TODO option for the user to not erase all files on the NXT
			boolean eraseAllFiles = true;
			NXTSamba nxt = openDevice();
			byte[] memoryImage = createImage();
			if (nxt != null)
				updateDevice(nxt, memoryImage, eraseAllFiles);

			// // get runtime
			// Runtime rt = Runtime.getRuntime();
			// // get NXJ_HOME property from preferences
			// String nxjHome =
			// LeJOSNXJPlugin.getDefault().getPluginPreferences()
			// .getString(PreferenceConstants.P_NXJ_HOME);
			// String nxjHomeEnv = "NXJ_HOME=" + nxjHome;
			// // create call to nxjflash
			// // operating system?
			// String command = null;
			// String os = System.getProperty("os.name");
			// if("Windows XP".equals(os) || "Windows 2000".equals(os)) {
			// command = nxjHome + "\\bin\\nxjflash.exe";
			// } else if("linux".equals(os)||("Mac OS X").equals(os)) {
			// command = nxjHome + "/bin/nxjflash";
			// } else {
			// throw new UnsupportedOperationException("operating system " + os
			// + " is presently not supported");
			// }
			// // check if file exists
			// File commandFile = new File(command);
			// if (!commandFile.exists())
			// throw new
			// LeJOSNXJException("preference NXJ_HOME is invalid or not set");
			// // run command
			// String[] cmd = { command };
			// String[] envp = { nxjHomeEnv };
			// Process proc = rt.exec(cmd, envp);
			// // connect to error and output streams
			// StreamMonitor errorMonitor = new
			// StreamMonitor(proc.getErrorStream());
			// StreamMonitor outputMonitor = new
			// StreamMonitor(proc.getInputStream());
			// errorMonitor.start();
			// outputMonitor.start();
			// // watch for result
			// return proc.waitFor();
		} catch (Throwable t) {
			throw new LeJOSNXJException(t);
		}
	}

	NXTSamba openDevice() throws NXTCommException, IOException {
		NXTSamba samba = new NXTSamba();
		NXTInfo[] nxts = samba.search();
		if (nxts.length == 0) {
			LeJOSNXJUtil.message("Found not devices in firmware update mode");
			LeJOSNXJUtil.message("Searching for connected NXT bricks...");
			NXTCommand cmd = NXTCommand.getSingleton();
			nxts = cmd.search(null, NXTCommFactory.USB);
			if (nxts.length <= 0) {
				throw new NXTCommException("No connected NXT brick found");
			}
			LeJOSNXJUtil.message("Found connected NXT brick " + nxts[0].name);
			if (!cmd.open(nxts[0])) {
				throw new NXTCommException("Could not open NXT brick "
						+ nxts[0].name + "in command mode");
			}
			LeJOSNXJUtil.message("Found connected NXT brick " + nxts[0].name);
			cmd.boot();
			LeJOSNXJUtil.message("Waiting for NXT brick(s) to reboot");
			// search until first NXT in samba mode is found
			// wait 30 seconds
			int noOfBricksFound = 0;
			int noOfSecondsWaited = 0;
			while ((noOfBricksFound == 0) && (noOfSecondsWaited < 30)) {
				nxts = samba.search();
				noOfBricksFound = nxts.length;
				// wait
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			if (noOfBricksFound == 0)
				throw new NXTCommException(
						"Could not find a NXT brick in firmware update mode");
		}
		if (nxts.length > 1) {
			throw new NXTCommException(
					"More than one NXT brick found in update mode");
		}
		if (!samba.open(nxts[0])) {
			throw new NXTCommException("Failed to open " + nxts[0].name
					+ " in SAMBA mode");
		}
		return samba;
	}

	/**
	 * TODO refactor NXJFlash to be usable for clients also, so all these
	 * methods do not have to implemented twice
	 * 
	 * @param commandLine
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	byte[] createImage() throws IOException, FileNotFoundException,
			LeJOSNXJException {
		LeJOSNXJUtil.message("Building firmware image");
		byte[] memoryImage = new byte[MAX_FIRMWARE_PAGES * NXTSamba.PAGE_SIZE];
		String vmName = null;
		String menuName = null;
		// get NXJ_HOME property from preferences
		String nxjHome = LeJOSNXJPlugin.getDefault().getPluginPreferences()
				.getString(PreferenceConstants.P_NXJ_HOME);
		if ((nxjHome == null) || (nxjHome.length() == 0))
			throw new LeJOSNXJException(
					"preference NXJ_HOME is invalid or not set");

		String SEP = System.getProperty("file.separator");
		vmName = nxjHome + SEP + "bin" + SEP + VM;
		menuName = nxjHome + SEP + "bin" + SEP + MENU;
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
		// We store the length and location of the Menu in the last page.
		storeWord(memoryImage, memoryImage.length - 4, menuLen);
		storeWord(memoryImage, memoryImage.length - 8, menuStart);
		// Check overall size allow for size/length markers in last block.
		if (menuStart + menuLen + 8 > memoryImage.length)
			throw new LeJOSNXJException("Combined size of VM and Menu > "
					+ memoryImage.length);
		LeJOSNXJUtil.message("VM size is " + vmLen + " bytes");
		LeJOSNXJUtil.message("Menu size is " + menuLen + " bytes.");
		LeJOSNXJUtil.message("Total image size is " + (menuStart + menuLen)
				+ "/" + memoryImage.length + " bytes");
		return memoryImage;
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

	private static final int MAX_FIRMWARE_PAGES = 320;
	private static final String VM = "lejos_nxt_rom.bin";
	private static final String MENU = "StartUpText.bin";

	void updateDevice(NXTSamba nxt, byte[] memoryImage, boolean format)
			throws IOException {
		LeJOSNXJUtil.message("NXT now open in firmware update mode");
		LeJOSNXJUtil.message("Unlocking pages");
		nxt.unlockAllPages();
		LeJOSNXJUtil.message("Writing memory image...");
		nxt.writePages(0, memoryImage, 0, memoryImage.length);
		if (format) {
			LeJOSNXJUtil.message("Formatting...");
			byte[] zeroPage = new byte[NXTSamba.PAGE_SIZE];
			for (int i = 0; i < 3; i++)
				nxt.writePage(MAX_FIRMWARE_PAGES + i, zeroPage, 0);
		}
		LeJOSNXJUtil.message("Starting new image");
		nxt.jump(0x00100000);
		nxt.close();
	}

	// class StreamMonitor extends Thread {
	// InputStream _is;
	//
	// StreamMonitor(InputStream is) {
	// _is = is;
	// }
	//
	// public void run() {
	// try {
	// BufferedReader br = new BufferedReader(new InputStreamReader(
	// _is));
	// String line = null;
	// while ((line = br.readLine()) != null)
	// // log
	// LeJOSNXJUtil.message(line);
	// } catch (IOException e) {
	// // log
	// LeJOSNXJUtil.message(e);
	//
	// }
	// }
	// }
}