package org.lejos.nxt.ldt.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * links and uploads a leJOS NXJ program to the brick
 * 
 * @see IWorkbenchWindowActionDelegate
 * @author Matthias Paul Scholz
 * 
 */
public class UploadAction implements IObjectActionDelegate {

	private ISelection _selection;

	/**
	 * The constructor.
	 */
	public UploadAction() {
	}

	public void run(IAction action) {
		final ArrayList<File> tmp = new ArrayList<File>();
		LeJOSNXJUtil.getFilesFromSelection(_selection, tmp);
		if (!tmp.isEmpty())
		{
			// open progress monitor
			IWorkbench wb = PlatformUI.getWorkbench();
			IProgressService ps = wb.getProgressService();
			try {
				ps.busyCursorWhile(new IRunnableWithProgress() {
					public void run(IProgressMonitor pm) {
						uploadFile(pm, tmp);
					} // end run
				});
			} catch (Throwable t) {
				// log
				LeJOSNXJUtil.log(t);
			}
		}
	}

	private void uploadFile(IProgressMonitor progressMonitor, ArrayList<File> fileList) {
		try {
			progressMonitor.beginTask("Uploading files...", IProgressMonitor.UNKNOWN);
			try
			{
				File nxjHome = LeJOSNXJUtil.getNXJHome();
				//LeJOSNXJPlugin.getDefault().getConsole().activate();
				
				ArrayList<String> args = new ArrayList<String>();
				LeJOSNXJUtil.getUploadOpts(args, false);
				for (File f : fileList)
					args.add(f.getAbsolutePath());
				
				int r = LeJOSNXJUtil.invokeTool(nxjHome, LeJOSNXJUtil.TOOL_UPLOAD, args);
				if (r == 0)
					LeJOSNXJUtil.message("files have been uploaded successfully");
				else
					LeJOSNXJUtil.message("uploading "+fileList+" failed with exit status "+r);
			}
			finally
			{
				progressMonitor.done();					
			}
		} catch (Throwable t) {
			if (t instanceof InvocationTargetException)
				t = ((InvocationTargetException)t).getTargetException();
			
			// log
			LeJOSNXJUtil.message("upload of " + fileList + " failed", t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 * action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		_selection = selection;
		ArrayList<File> tmp = new ArrayList<File>();
		LeJOSNXJUtil.getFilesFromSelection(selection, tmp);
		action.setEnabled(!tmp.isEmpty());
	}
}