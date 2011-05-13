package org.lejos.nxt.ldt.actions;

import java.io.IOException;

import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.comm.NXTInfo;
import lejos.pc.comm.NXTSamba;
import lejos.pc.tools.NXJFlashUI;
import lejos.pc.tools.NXJFlashUpdate;

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
		IWorkbenchWindowActionDelegate, NXJFlashUI {

	private NXJFlashUpdate updater;
	private IProgressMonitor progressMonitor;

	/**
	 * The constructor.
	 */
	public LeJOSUploadFirmwareAction() {
		updater = new NXJFlashUpdate(this);
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
						progressMonitor = pm;
						// upload firmware
						progressMonitor.beginTask("Uploading firmware...",
								IProgressMonitor.UNKNOWN);
						flashFirmware();
						progressMonitor.done();
						progressMonitor = null;
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
		try {
			String nxjHome = LeJOSNXJPlugin.getDefault()
			.getPluginPreferences().getString(
					PreferenceConstants.P_NXJ_HOME);
			if((nxjHome==null)||(nxjHome.length()==0)) {
				throw new LeJOSNXJException("NXJ_HOME is not set. Please specify it in the plug-in's preferences");
			}
			byte[] memoryImage = updater.createFirmwareImage(null, null,nxjHome);
			// TODO allow user to choose if all files should be erased
			boolean format = true;
			byte[] fs = null;
			if (format)
				fs = updater.createFilesystemImage();
			NXTSamba nxt = openDevice();
			// TODO allow user to choose if firmware should be verified
			boolean verify = false;
			if (nxt != null)
				updater.updateDevice(nxt, memoryImage, fs, verify);
		} catch (Exception e) {
			LeJOSNXJUtil.message(e);
		}
	}

	public void message(String message) {
		LeJOSNXJUtil.message(message);
	}

	public void progress(String msg, int percent) {
		if(progressMonitor!=null) {
			progressMonitor.subTask(msg);
			progressMonitor.worked(percent);
		} else {
			LeJOSNXJUtil.message(percent + " " + msg);
		}
	}

	/**
	 * Locate and open an NXT device ready for the firmware to be updated. First
	 * we look for devices that are already in SAM-BA mode. If we do not find
	 * any we look for devices in normal mode and attempt to re-boot them into
	 * SAM-BA mode.
	 * 
	 * @return
	 */
	private NXTSamba openDevice() throws NXTCommException, IOException {
		// First look to see if there are any devices already in SAM-BA mode
		NXTSamba samba = updater.openSambaDevice(0);
		if (samba == null) {
			NXTInfo[] nxts;
			LeJOSNXJUtil
					.message("No devices in firmware update mode were found");
			LeJOSNXJUtil.message("Searching for connected devices");
			NXTConnector conn = new NXTConnector();
			nxts = conn.search(null, null, NXTCommFactory.USB);
			if (nxts.length <= 0) {
				LeJOSNXJUtil
						.message("No connected devices found. Make sure that at least one brick is connected and turned on.");
			} else {
				LeJOSNXJUtil.message("Found " + nxts[0].name + ". Booting...");
				// boot brick
				updater.resetDevice(nxts[0]);
				samba = updater.openSambaDevice(30000);
			}
		}
		return samba;
	}

}