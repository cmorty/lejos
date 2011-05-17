package org.lejos.nxt.ldt.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
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
public class LeJOSLinkAndUploadAction implements IObjectActionDelegate {

	private ISelection _selection;

	/**
	 * The constructor.
	 */
	public LeJOSLinkAndUploadAction() {
	}

	public void run(IAction action) {
		IJavaElement javaElement = LeJOSNXJUtil.getFirstJavaElementFromSelection(_selection);
		if (javaElement == null)
			return;
		final IType javaType = LeJOSNXJUtil.getJavaTypeFromElement(javaElement);
		if (javaType == null)
			return;
		final IJavaProject project = javaElement.getJavaProject();
		
		// open progress monitor
		IWorkbench wb = PlatformUI.getWorkbench();
		IProgressService ps = wb.getProgressService();
		try {
			ps.busyCursorWhile(new IRunnableWithProgress() {
				public void run(IProgressMonitor pm) {
					linkAndUpload(pm, project, javaType);
				} // end run
			});
		} catch (Throwable t) {
			// log
			LeJOSNXJUtil.log(t);
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
		
		boolean isEnabled = false;
		// check if selected element is a java file or a leJOS NXJ project
		IJavaElement selectedElement = LeJOSNXJUtil.getFirstJavaElementFromSelection(_selection);
		if (selectedElement != null)
		{
			IType selectedType = LeJOSNXJUtil.getJavaTypeFromElement(selectedElement);
			if (selectedType != null)
			{
				isEnabled = true;
			}
		}
		// set state
		action.setEnabled(isEnabled);
	}

	private void linkAndUpload(IProgressMonitor pm, IJavaProject project, IType javaType) {
		try {
			ArrayList<File> cpl = new ArrayList<File>();
			LeJOSNXJUtil.getProjectClassPath(project, false, cpl);
			
			File nxjHome = LeJOSNXJUtil.getNXJHome();
			ArrayList<File> bcpl = new ArrayList<File>();
			LeJOSNXJUtil.buildNXTClasspath(nxjHome, bcpl);

			String fullClass =LeJOSNXJUtil.getFullQualifiedClassName(javaType);
			String simpleClass = LeJOSNXJUtil.getSimpleClassName(javaType);
			
			IProject project2 = project.getProject();
			IFile binary = project2.getFile(simpleClass+".nxj");
			IFile binaryDebug = project2.getFile(simpleClass+".nxd");
			String binaryPath = binary.getLocation().toOSString();
			String binaryDebugPath = binaryDebug.getLocation().toOSString();
			
			// upload program
			pm.beginTask("Linking and uploading program to the brick...", IProgressMonitor.UNKNOWN);
			try
			{
				int r;
				try
				{
					pm.subTask("Linking ...");
					ArrayList<String> args = new ArrayList<String>();
					LeJOSNXJUtil.getLinkerOpts(args);
					args.add("--bootclasspath");
					args.add(classpathToString(cpl));
					args.add("--classpath");
					args.add(classpathToString(bcpl));
					args.add("--output");
					args.add(binaryPath);
					args.add("--outputdebug");
					args.add(binaryDebugPath);
					args.add(fullClass);
	
					r = LeJOSNXJUtil.invokeTool(nxjHome, LeJOSNXJUtil.TOOL_LINK, args);
				}
				finally
				{
					binary.refreshLocal(IResource.DEPTH_ZERO, pm);
					binaryDebug.refreshLocal(IResource.DEPTH_ZERO, pm);
				}
			
				if (r != 0)
					LeJOSNXJUtil.message("linking the file failed with exit status "+r);
				else
				{
					LeJOSNXJUtil.message("program has been linked successfully");
					
					pm.subTask("Uploading ...");					
					ArrayList<String> args = new ArrayList<String>();
					LeJOSNXJUtil.getUploadOpts(args, true);
					args.add(binaryPath);
					
					r = LeJOSNXJUtil.invokeTool(nxjHome, LeJOSNXJUtil.TOOL_UPLOAD, args);
					if (r == 0)
						LeJOSNXJUtil.message("program has been uploaded");
					else
						LeJOSNXJUtil.message("uploading the program failed with exit status "+r);
				}
			}
			finally
			{
				pm.done();
			}
		} catch (Throwable t) {
			if (t instanceof InvocationTargetException)
				t = ((InvocationTargetException)t).getTargetException();
			
			// log
			LeJOSNXJUtil.message("Linking or uploading the program failed", t);
		}
	}

	private String classpathToString(ArrayList<File> cpl)
	{
		if (cpl.isEmpty())
			return "";
		
		StringBuilder sb = new StringBuilder();
		for (File f : cpl)
		{
			sb.append(File.pathSeparatorChar);
			sb.append(f.getAbsolutePath());
		}
		return sb.substring(1);
	}
}