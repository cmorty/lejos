package org.lejos.nxt.ldt.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
						int result = uploadFirmware();
						pm.done();
						if (result == 0)
							LeJOSNXJUtil
									.message("firmware has been successfully uploaded to the NXT brick");
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

	private int uploadFirmware() throws LeJOSNXJException  {
		try {
		// get runtime
		Runtime rt = Runtime.getRuntime();
		// get NXJ_HOME property from preferences
		String nxjHome = LeJOSNXJPlugin.getDefault().getPluginPreferences()
				.getString(PreferenceConstants.P_NXJ_HOME);
		String nxjHomeEnv = "NXJ_HOME=" + nxjHome;
		// create call to nxjflash
		// operating system? 
		String command = null;
		String os = System.getProperty("os.name");
		if("Windows XP".equals(os) || "Windows 2000".equals(os)) {
			command = nxjHome + "\\bin\\nxjflash.exe";
		} else if("linux".equals(os)||("Mac OS X").equals(os)) {
			command = nxjHome + "/bin/nxjflash";
		} else {
			throw new UnsupportedOperationException("operating system " + os + " is presently not supported");
		}
		// check if file exists
		File commandFile = new File(command);
		if (!commandFile.exists())
			throw new LeJOSNXJException("preference NXJ_HOME is invalid or not set");
		// run command
		String[] cmd = { command };
		String[] envp = { nxjHomeEnv };
		Process proc = rt.exec(cmd, envp);
		// connect to error and output streams
		StreamMonitor errorMonitor = new StreamMonitor(proc.getErrorStream());
		StreamMonitor outputMonitor = new StreamMonitor(proc.getInputStream());
		errorMonitor.start();
		outputMonitor.start();
		// watch for result
		return proc.waitFor();
		} catch(Throwable t) {
			throw new LeJOSNXJException(t);
		}
	}

	class StreamMonitor extends Thread {
		InputStream _is;

		StreamMonitor(InputStream is) {
			_is = is;
		}

		public void run() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						_is));
				String line = null;
				while ((line = br.readLine()) != null)
					// log
					LeJOSNXJUtil.message(line);
			} catch (IOException e) {
				// log
				LeJOSNXJUtil.message(e);

			}
		}
	}
}