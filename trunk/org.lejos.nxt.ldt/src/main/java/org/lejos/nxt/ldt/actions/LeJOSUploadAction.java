package org.lejos.nxt.ldt.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
public class LeJOSUploadAction implements IObjectActionDelegate {

	private ISelection _selection;

	/**
	 * The constructor.
	 */
	public LeJOSUploadAction() {
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
		for (File f : fileList)
		{
			try {
				progressMonitor.beginTask("Uploading file...", IProgressMonitor.UNKNOWN);
				try
				{
					File nxjHome = LeJOSNXJUtil.getNXJHome();
					ClassLoader cl = LeJOSNXJUtil.getCachedPCClassLoader(nxjHome);
					Class<?> c = cl.loadClass("lejos.pc.tools.NXJUpload");
					
					//LeJOSNXJPlugin.getDefault().getConsole().activate();
					
					ArrayList<String> args = new ArrayList<String>();
					LeJOSNXJUtil.getUploadOpts(args, false);
					args.add(f.getAbsolutePath());
					String[] args2 = new String[args.size()];
					args.toArray(args2);
					
					Method m = c.getDeclaredMethod("start", String[].class);
					Object r1 = m.invoke(null, (Object)args2);
					int r2 = ((Integer)r1).intValue();
					
					if (r2 == 0)
						LeJOSNXJUtil.message("file has been uploaded successfully");
					else
						LeJOSNXJUtil.message("uploading the file failed with exit status "+r2);
				}
				finally
				{
					progressMonitor.done();					
				}
			} catch (Throwable t) {
				if (t instanceof InvocationTargetException)
					t = ((InvocationTargetException)t).getTargetException();
				
				// log
				LeJOSNXJUtil.message("upload of " + f+" failed.", t);
			}
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