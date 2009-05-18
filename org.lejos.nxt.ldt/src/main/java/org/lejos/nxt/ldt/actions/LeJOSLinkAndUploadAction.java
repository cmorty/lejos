package org.lejos.nxt.ldt.actions;

import java.io.File;

import js.common.CLIToolProgressMonitor;
import js.tinyvm.TinyVM;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import lejos.pc.tools.NXJUploadException;

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
	private LeJOSNXJLogListener logListener;
	private TinyVM tinyVM;
	private NXTConnector connector;

	/**
	 * The constructor.
	 */
	public LeJOSLinkAndUploadAction() {
		logListener = new LeJOSNXJLogListener();
		// _parser = new NXJCommandLineParser();
		tinyVM = new TinyVM();
		tinyVM.addProgressMonitor(new CLIToolProgressMonitor());
		connector = new NXTConnector();
		connector.addLogListener(logListener);
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
						pm.subTask("Linking");
						String binName = linkProgram();
						 pm.subTask("Connecting");
						NXTComm nxtComm = connect();
						if (nxtComm!=null) {
							pm.subTask("Uploading");
							uploadProgram(binName,nxtComm);
							// log
							LeJOSNXJUtil
									.message("Program has been successfully uploaded to the NXT brick");
						} else {
							LeJOSNXJUtil
									.message("Program could not be uploaded to the NXT brick");
						}
						pm.done();
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

	/**
	 * TODO refactor this to use a dedicated utility in the pctools classes
	 * 
	 * @throws LeJOSNXJException
	 */
	private String linkProgram() throws LeJOSNXJException {
		try {
			int noOfMandatoryArguments = 7;
			int noOfOptionalArgumentsUsed = 0;
			// verbosity?
			boolean isVerbose = LeJOSNXJPlugin.getDefault()
					.getPluginPreferences().getBoolean(
							PreferenceConstants.P_IS_VERBOSE);
			if (isVerbose)
				noOfOptionalArgumentsUsed++;
			String tinyVMArgs[] = new String[noOfMandatoryArguments
					+ noOfOptionalArgumentsUsed];
			int argsCounter = 0;
			// class name
			IJavaElement javaElement = LeJOSNXJUtil
					.getFirstJavaElementFromSelection(_selection);
			// TODO merge packages into name
			String className = LeJOSNXJUtil
					.getFullQualifiedClassName(javaElement);
			tinyVMArgs[argsCounter++] = className;
			// get selected project
			IJavaProject project = LeJOSNXJUtil
					.getJavaProjectFromSelection(_selection);
			if (project == null)
				throw new LeJOSNXJException("no leJOS project selected");
			// binary
			String binaryName = LeJOSNXJUtil.getBinaryName(javaElement);
			File targetDir = LeJOSNXJUtil.getAbsoluteProjectTargetDir(project);
			String binary = new File(targetDir, binaryName).getAbsolutePath();
			tinyVMArgs[argsCounter++] = "-o";
			tinyVMArgs[argsCounter++] = binary;
			// classpath
			tinyVMArgs[argsCounter++] = "--classpath";
			tinyVMArgs[argsCounter++] = createClassPath(project);
			// writeorder
			tinyVMArgs[argsCounter++] = "--writeorder";
			tinyVMArgs[argsCounter++] = "LE";
			// optional arguments
			if (isVerbose)
				tinyVMArgs[argsCounter++] = "--verbose";
			// log
			if (isVerbose) {
				String argsString = "arguments";
				for (int arg = 0; arg < tinyVMArgs.length; arg++) {
					argsString += " " + tinyVMArgs[arg];
				}
				LeJOSNXJUtil.message("linking using " + argsString);
			}
			// run linker
			tinyVM = new TinyVM();
			tinyVM.start(tinyVMArgs);
			return binary;
		} catch (Throwable t) {
			throw new LeJOSNXJException(t);
		}
	}

	/**
	 * TODO honor property "run after upload" TODO check result of upload
	 * 
	 * @param binName
	 * @param nxtComm
	 * @throws NXJUploadException
	 */
	private void uploadProgram(String binName, NXTComm nxtComm) throws NXJUploadException {
		// uploadProgram
		try {
			NXTCommand nxtCommand = NXTCommand.getSingleton();
			nxtCommand.setNXTComm(nxtComm);
			nxtCommand.uploadFile(new File(binName));
		} catch (Throwable t) {
			throw new NXJUploadException("Exception during upload", t);
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
			switch (classpathEntry.getEntryKind()) {
			case IClasspathEntry.CPE_SOURCE: // source => ignore
				break;
			default:
				classPath += pathSeparator
						+ classpathEntry.getPath().toOSString();
				// case IClasspathEntry.CPE_LIBRARY: // directory with classes
				// or
				// jar
				// // => append
				// classPath += pathSeparator
				// + classpathEntry.getPath().toOSString();
				// break;
				// case IClasspathEntry.CPE_PROJECT: // another project =>
				// append
				// all
				// // its classpath
				// // TODO: how to add these classpath ???
				// break;
				// case IClasspathEntry.CPE_VARIABLE: // a project or library
				// // indirectly via a classpath
				// // variable in the first segment
				// // of the path
				// // Do we need this? Probably yes...
				// break;
				// case IClasspathEntry.CPE_CONTAINER: // set of entries
				// referenced
				// // indirectly via a classpath
				// // container
				// // Do we need this? Probably yes...
				// break;
			}
		}
		return classPath;
	}

	/**
	 * connects to a NXT as configured in the preferences 
	 */
	private NXTComm connect() throws LeJOSNXJException {
		NXTComm nxtComm = null;
		boolean isConnectToAddress = false;
		boolean isConnectToName = false;
		String nxtAddress = null;
		String nxtName = null;
		// protocols
		int protocols = NXTCommFactory.USB;
		String connectionType = LeJOSNXJPlugin.getDefault()
				.getPluginPreferences().getString(
						PreferenceConstants.P_CONNECTION_TYPE);
		if ((connectionType != null)
				&& (connectionType
						.equals(PreferenceConstants.P_PROTOCOL_BLUETOOTH))) {
			protocols |= NXTCommFactory.BLUETOOTH;
		}
		// connect to address?
		isConnectToAddress = LeJOSNXJPlugin.getDefault().getPluginPreferences()
				.getBoolean(PreferenceConstants.P_CONNECT_TO_BRICK_ADDRESS);
		if (isConnectToAddress) {
			nxtAddress = LeJOSNXJPlugin.getDefault().getPluginPreferences()
					.getString(PreferenceConstants.P_CONNECTION_BRICK_ADDRESS)
					.trim();
			if (nxtAddress.length() == 0) {
				throw new LeJOSNXJException(
						"connect to address specified in the preferences, but no address given");
			}
		} else {
			// connect to named brick? (only when no address is specified)
			isConnectToName = LeJOSNXJPlugin.getDefault()
					.getPluginPreferences().getBoolean(
							PreferenceConstants.P_CONNECT_TO_NAMED_BRICK);
			if (isConnectToName) {
				// retrieve name
				nxtName = LeJOSNXJPlugin.getDefault().getPluginPreferences()
						.getString(PreferenceConstants.P_CONNECTION_BRICK_NAME)
						.trim();
				if (nxtName.length() == 0) {
					throw new LeJOSNXJException(
							"connect to name specified in the preferences, but no name given");
				}
			}
		}
		// connect
		try {
			connector.connectTo(nxtName, nxtAddress, protocols);
			nxtComm = connector.getNXTComm();
		} catch (Exception e) {
			throw new LeJOSNXJException("Exception during connecting to brick",
					e);
		}
		return nxtComm;
	}
}