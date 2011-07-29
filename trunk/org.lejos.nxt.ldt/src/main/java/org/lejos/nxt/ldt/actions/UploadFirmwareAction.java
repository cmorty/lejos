package org.lejos.nxt.ldt.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.lejos.nxt.ldt.LeJOSPlugin;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;
import org.lejos.nxt.ldt.util.ToolStarter;

/**
 * 
 * @see IWorkbenchWindowActionDelegate
 * @author Matthias Paul Scholz
 */
public class UploadFirmwareAction extends ActionDelegate implements IWorkbenchWindowActionDelegate {

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	@Override
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
				ToolStarter starter = LeJOSNXJUtil.getCachedToolStarter();
				
				ArrayList<String> args = new ArrayList<String>();
//				args.add(new File(nxjHome, "bin/lejos_nxt_rom.bin").getAbsolutePath());
//				args.add(new File(nxjHome, "bin/StartUpText.bin").getAbsolutePath());
				
				int r = starter.invokeTool(LeJOSNXJUtil.TOOL_FLASH, args);
				if (r == 0)
					LeJOSNXJUtil.message("firmware has been uploaded successfully");
				else
					LeJOSNXJUtil.error("flashing the firmware failed with exit status "+r);
			}
			finally
			{
				progressMonitor.done();
			}
		} catch (Throwable t) {
			if (t instanceof InvocationTargetException)
				t = ((InvocationTargetException)t).getTargetException();
			
			// log
			LeJOSNXJUtil.error("flashing the firmware failed", t);
		}
	}

	public void init(IWorkbenchWindow window) {
		// do nothing
	}
}