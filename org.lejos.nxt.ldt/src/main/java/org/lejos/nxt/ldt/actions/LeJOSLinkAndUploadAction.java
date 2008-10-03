package org.lejos.nxt.ldt.actions;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import lejos.pc.tools.NXJLinkAndUpload;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.lejos.nxt.ldt.LeJOSNXJPlugin;
import org.lejos.nxt.ldt.preferences.PreferenceConstants;
import org.lejos.nxt.ldt.util.LeJOSNXJException;
import org.lejos.nxt.ldt.util.LeJOSNXJLogListener;
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
	private LeJOSNXJLogListener _logListener;

	/**
	 * The constructor.
	 */
	public LeJOSLinkAndUploadAction() {
		_logListener = new LeJOSNXJLogListener();
	}

	/**
	 * TODO present error in progress dialog
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
						// upload program
						pm
								.beginTask(
										"Linking and uploading program to the brick...",
										IProgressMonitor.UNKNOWN);
						linkAndUploadProgram();
						pm.done();
						// log
						LeJOSNXJUtil
								.message("Program has been successfully uploaded to the NXT brick");
					} catch (Throwable t) {
						// log
						LeJOSNXJUtil.message(t);
					}
				} // end run
			});
		} catch (Throwable t) {
			// log
			LeJOSNXJUtil.message(t);
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
		enableDueToSelection(action);
	}

	private void linkAndUploadProgram() throws LeJOSNXJException {
		// NXT brick connected?
		NXTInfo connectedBrick = LeJOSNXJPlugin.getDefault()
				.getConnectionManager().getConnectedNXT();
		if (connectedBrick == null) {
			// TODO show message dialog
			throw new LeJOSNXJException("no NXT brick connected");
		} else {
			try {
				// instantiate link and upload delegate
				NXJLinkAndUpload delegate = new NXJLinkAndUpload();
				delegate.addToolsLogListener(_logListener);
				delegate.addMonitor(_logListener);
				// create arguments
				int noOfArguments = 10;
				// run after download?
				boolean runAfterDownload = LeJOSNXJPlugin.getDefault()
						.getPluginPreferences().getBoolean(
								PreferenceConstants.P_RUN_AFTER_DOWNLOAD);
				if (runAfterDownload)
					noOfArguments++;
				// verbosity?
				boolean isVerbose = LeJOSNXJPlugin.getDefault()
						.getPluginPreferences().getBoolean(
								PreferenceConstants.P_IS_VERBOSE);
				if (isVerbose)
					noOfArguments++;
				// // connect to brick address?
				// boolean isConnectToAddress = LeJOSNXJPlugin.getDefault()
				// .getPluginPreferences().getBoolean(
				// PreferenceConstants.P_CONNECT_TO_BRICK_ADDRESS);
				// if (isConnectToAddress)
				// noOfArguments += 2;
				// // connect to named brick
				// boolean isConnectToName = LeJOSNXJPlugin.getDefault()
				// .getPluginPreferences().getBoolean(
				// PreferenceConstants.P_CONNECT_TO_BRICK_NAME);
				// if (isConnectToName)
				// noOfArguments += 2;
				String args[] = new String[noOfArguments];
				int argsCounter = 0;
				// get selected project
				IJavaProject project = LeJOSNXJUtil
						.getJavaProjectFromSelection(_selection);
				if (project == null)
					throw new LeJOSNXJException("no leJOS project selected");
				// class name
				IJavaElement javaElement = LeJOSNXJUtil
						.getFirstJavaElementFromSelection(_selection);
				// TODO merge packages into name
				String className = LeJOSNXJUtil
						.getFullQualifiedClassName(javaElement);
				args[argsCounter++] = className;
				// classpath
				args[argsCounter++] = "--classpath";
				args[argsCounter++] = createClassPath(project);
				// writeorder
				args[argsCounter++] = "--writeorder";
				args[argsCounter++] = "LE";
				// name of binary
				args[argsCounter++] = "-o";
				args[argsCounter++] = LeJOSNXJUtil.getBinaryName(javaElement);
				// connection type
				String connectionType = "u";
				if(connectedBrick.protocol==NXTCommFactory.BLUETOOTH)
					connectionType = "b";
				args[argsCounter++] = "-" + connectionType;
				// run after download?
				if (runAfterDownload)
					args[argsCounter++] = "-r";
				// verbosity
				if (isVerbose)
					args[argsCounter++] = "--verbose";
				// // connect to brick address?
				// if (isConnectToAddress) {
				// String connectionAddress = LeJOSNXJPlugin
				// .getDefault()
				// .getPluginPreferences()
				// .getString(
				// PreferenceConstants.P_CONNECTION_BRICK_ADDRESS)
				// .trim();
				// if (connectionAddress.isEmpty())
				// throw new LeJOSNXJException(
				// "no address to connect to specified in the preferences");
				// args[argsCounter++] = "--address";
				// args[argsCounter++] = connectionAddress;
				// }
				// connect to named brick?
				// if (isConnectToName) {
				String connectionName = connectedBrick.name;
				// if (connectionName.isEmpty())
				// throw new LeJOSNXJException(
				// "no brick name to connect to specified in the preferences");
				args[argsCounter++] = "--name";
				args[argsCounter++] = connectionName;
				// }
				// log
				String argsString = "arguments";
				for (int arg = 0; arg < args.length; arg++) {
					argsString += " " + args[arg];
				}
				LeJOSNXJUtil.message("linking and uploading using "
						+ argsString);
				// run link and upload
				delegate.run(args);
			} catch (Throwable e) {
				throw new LeJOSNXJException(e);
			}
		}
	}

	private void enableDueToSelection(IAction action) {
		boolean isEnabled = false;
		// check if selected element is a java file or a leJOS NXJ project
		IJavaElement selectedElement = LeJOSNXJUtil
				.getFirstJavaElementFromSelection(_selection);
		if (selectedElement != null) {
			if (selectedElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
				// check if selected project is a leJOS project
				IJavaProject project = LeJOSNXJUtil
						.getJavaProjectFromSelection(_selection);
				if (project != null) {
					try {
						if (LeJOSNXJUtil.isLeJOSProject(project)) {
							isEnabled = true;
						}
					} catch (CoreException e) {
						LeJOSNXJUtil.message(e);
					}
				}
			}
		}
		// set state
		action.setEnabled(isEnabled);
	}

	/**
	 * 
	 * build the classpath for the link and upload utility
	 * 
	 * @param project
	 * @return String classpath
	 * @throws JavaModelException
	 */
	private String createClassPath(IJavaProject project)
			throws JavaModelException {
		String classPath = LeJOSNXJUtil.getAbsoluteProjectTargetDir(project)
				.getAbsolutePath();
		// path separator
		String pathSeparator = System.getProperty("path.separator");
		// project's classpath
		IClasspathEntry[] entries = project.getResolvedClasspath(true);
		// build string
		for (IClasspathEntry classpathEntry : entries) {
			classPath += pathSeparator + classpathEntry.getPath().toOSString();
		}
		return classPath;
	}

}