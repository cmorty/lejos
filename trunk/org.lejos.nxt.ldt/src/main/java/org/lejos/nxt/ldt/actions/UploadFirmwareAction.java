package org.lejos.nxt.ldt.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.lejos.nxt.ldt.LeJOSPlugin;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * 
 * @see IWorkbenchWindowActionDelegate
 * @author Matthias Paul Scholz
 */
public class UploadFirmwareAction implements
		IWorkbenchWindowActionDelegate {

	/**
	 * The constructor.
	 */
	public UploadFirmwareAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		// open progress monitor
		Job flash = new Job("flashing leJOS firmware") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				flashFirmware(monitor);
				return Status.OK_STATUS;
			}
		};
		flash.schedule();
	}

	private void flashFirmware(IProgressMonitor progressMonitor) {
		LeJOSPlugin.getDefault().getConsole().activate();
		
		try {
			progressMonitor.beginTask("Uploading firmware...", IProgressMonitor.UNKNOWN);
			try
			{
				// upload firmware
				File nxjHome = LeJOSNXJUtil.getNXJHome();
				
				ArrayList<String> args = new ArrayList<String>();
//				args.add(new File(nxjHome, "bin/lejos_nxt_rom.bin").getAbsolutePath());
//				args.add(new File(nxjHome, "bin/StartUpText.bin").getAbsolutePath());
				
				int r = LeJOSNXJUtil.invokeTool(nxjHome, LeJOSNXJUtil.TOOL_FLASH, args);
				if (r == 0)
					LeJOSNXJUtil.message("firmware has been uploaded successfully");
				else
					LeJOSNXJUtil.message("flashing the firmware failed with exit status "+r);
			}
			finally
			{
				progressMonitor.done();
			}
		} catch (Throwable t) {
			if (t instanceof InvocationTargetException)
				t = ((InvocationTargetException)t).getTargetException();
			
			// log
			LeJOSNXJUtil.message("flashing the firmware failed", t);
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
}