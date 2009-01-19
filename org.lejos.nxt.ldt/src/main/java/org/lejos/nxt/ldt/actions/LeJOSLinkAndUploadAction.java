package org.lejos.nxt.ldt.actions;

import java.io.File;

import js.common.CLIToolProgressMonitor;
import js.tinyvm.TinyVM;
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
	// private LeJOSNXJLogListener _logListener;
	// TODO remove when no longer needed
	// private NXJCommandLineParser _parser;
	private TinyVM _tinyVM;

	/**
	 * The constructor.
	 */
	public LeJOSLinkAndUploadAction() {
		// _logListener = new LeJOSNXJLogListener();
		// _parser = new NXJCommandLineParser();
		_tinyVM = new TinyVM();
		_tinyVM.addProgressMonitor(new CLIToolProgressMonitor());
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
						pm.subTask("Uploading");
						uploadProgram(binName);
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
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 *      action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 *      .IAction, org.eclipse.jface.viewers.ISelection)
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
			// String argsString = "arguments";
			// for (int arg = 0; arg < tinyVMArgs.length; arg++) {
			// argsString += " " + tinyVMArgs[arg];
			// }
			// LeJOSNXJUtil.message("linking using " + argsString);
			// run linker
			_tinyVM = new TinyVM();
			_tinyVM.start(tinyVMArgs);
			return binary;
		} catch (Throwable t) {
			throw new LeJOSNXJException(t);
		}
	}

	/**
	 * TODO honor property "run after upload" TODO check result of upload
	 * 
	 * @param binName
	 * @throws NXJUploadException
	 */
	private void uploadProgram(String binName) throws NXJUploadException {
		// TODO uploadProgram
		// // NXT brick connected?
		// NXTInfo connectedBrick = LeJOSNXJPlugin.getDefault()
		// .getConnectionManager().getConnectedNXT();
		// if (connectedBrick == null) {
		// throw new NXJUploadException("no NXT brick connected");
		// } else {
		// // send file
		// try {
		// File f = new File(binName);
		// String result = NXTCommand.getSingleton().uploadFile(f);
		// } catch (Throwable t) {
		// throw new NXJUploadException("Exception during upload", t);
		// }

		// // instantiate link and upload delegate
		// NXJLinkAndUpload delegate = new NXJLinkAndUpload();
		// delegate.addToolsLogListener(_logListener);
		// delegate.addMonitor(_logListener);
		// // create arguments
		// int noOfArguments = 8;
		// // run after download?
		// boolean runAfterDownload = LeJOSNXJPlugin.getDefault()
		// .getPluginPreferences().getBoolean(
		// PreferenceConstants.P_RUN_AFTER_DOWNLOAD);
		// if (runAfterDownload)
		// noOfArguments++;
		// // verbosity?
		// boolean isVerbose = LeJOSNXJPlugin.getDefault()
		// .getPluginPreferences().getBoolean(
		// PreferenceConstants.P_IS_VERBOSE);
		// if (isVerbose)
		// noOfArguments++;
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
		// String args[] = new String[noOfArguments];
		// int argsCounter = 0;
		// // get selected project
		// IJavaProject project = LeJOSNXJUtil
		// .getJavaProjectFromSelection(_selection);
		// if (project == null)
		// throw new LeJOSNXJException("no leJOS project selected");
		// // class name
		// IJavaElement javaElement = LeJOSNXJUtil
		// .getFirstJavaElementFromSelection(_selection);
		// // TODO merge packages into name
		// String className = LeJOSNXJUtil
		// .getFullQualifiedClassName(javaElement);
		// args[argsCounter++] = className;
		// // classpath
		// args[argsCounter++] = "--classpath";
		// args[argsCounter++] = createClassPath(project);
		// // writeorder
		// args[argsCounter++] = "--writeorder";
		// args[argsCounter++] = "LE";
		// // name of binary
		// args[argsCounter++] = "-o";
		// File targetDir =
		// LeJOSNXJUtil.getAbsoluteProjectTargetDir(project);
		// String binaryName = LeJOSNXJUtil.getBinaryName(javaElement);
		// args[argsCounter++] = new
		// File(targetDir,binaryName).getAbsolutePath();
		// // connection type
		// String connectionType = LeJOSNXJPlugin.getDefault()
		// .getPluginPreferences().getString(
		// PreferenceConstants.P_CONNECTION_TYPE);
		// if((connectionType==null)||(connectionType.trim().length()==0)
		// )
		// connectionType = "u";
		// args[argsCounter++] = "-" + connectionType;
		// // run after download?
		// if (runAfterDownload)
		// args[argsCounter++] = "-r";
		// // verbosity
		// if (isVerbose)
		// args[argsCounter++] = "--verbose";
		// // connect to brick address?
		// if (isConnectToAddress) {
		// String connectionAddress = LeJOSNXJPlugin.getDefault()
		// .getPluginPreferences().getString(
		// PreferenceConstants.P_CONNECTION_BRICK_ADDRESS)
		// .trim();
		// if (connectionAddress.length() == 0)
		// throw new LeJOSNXJException(
		// "no address to connect to specified in the preferences");
		// args[argsCounter++] = "--address";
		// args[argsCounter++] = connectionAddress;
		// }
		// // connect to named brick?
		// if (isConnectToName) {
		// String connectionName = LeJOSNXJPlugin.getDefault()
		// .getPluginPreferences().getString(
		// PreferenceConstants.P_CONNECTION_BRICK_NAME)
		// .trim();
		// if (connectionName.length() == 0)
		// throw new LeJOSNXJException(
		// "no brick name to connect to specified in the preferences");
		// args[argsCounter++] = "--name";
		// args[argsCounter++] = connectionName;
		// }
		// // log
		// String argsString = "arguments";
		// for (int arg = 0; arg < args.length; arg++) {
		// argsString += " " + args[arg];
		// }
		// LeJOSNXJUtil.message("linking and uploading using " +
		// argsString);
		// // run link and upload
		// delegate.run(args);
		// } catch (Throwable e) {
		// throw new LeJOSNXJException(e);
		// }
		// }
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
	    switch(classpathEntry.getEntryKind()) {
	      case IClasspathEntry.CPE_SOURCE: // source => ignore
	        break;
	      case IClasspathEntry.CPE_LIBRARY: // directory with classes or jar => append
	        classPath += pathSeparator + classpathEntry.getPath().toOSString();
	        break;
	      case IClasspathEntry.CPE_PROJECT: // another project => append all its classpath
	        // TODO: how to add these classpath ???
	        break;
	      case IClasspathEntry.CPE_VARIABLE: // a project or library indirectly via a classpath variable in the first segment of the path
	        // Do we need this? Probably yes...
	        break;
	      case IClasspathEntry.CPE_CONTAINER: // set of entries referenced indirectly via a classpath container
          // Do we need this? Probably yes...
	        break;		      
	    }
	    return classPath;
	  }

	/**
	 * links a file TODO this is a copy of NXJLinkAndUpload.run from pctools
	 * without the upload (which fails when the brick is already connected)
	 * Enhance NXJLinkAndUpload so the upload works there also for already
	 * connected bricks
	 */
	// private void linkAndUpload(String[] args) throws
	// js.tinyvm.TinyVMException,
	// NXJUploadException {
	// // process arguments
	// CommandLine commandLine = _parser.parse(args);
	// String binName = commandLine.getOptionValue("o");
	// boolean run = commandLine.hasOption("r");
	// boolean blueTooth = commandLine.hasOption("b");
	// boolean usb = commandLine.hasOption("u");
	// String name = commandLine.getOptionValue("n");
	// String address = commandLine.getOptionValue("d");
	// String tinyVMArgs[];
	//
	// String firstArg = commandLine.getArgs()[0];
	//
	// int argCount = 0;
	//
	// // Count the arguments for the linker
	// for (int i = 0; i < args.length; i++) {
	// if (args[i].equals("-b"))
	// continue;
	// if (args[i].equals("--bluetooth"))
	// continue;
	// if (args[i].equals("-u"))
	// continue;
	// if (args[i].equals("--usb"))
	// continue;
	// if (args[i].equals("-n")) {
	// i++;
	// continue;
	// }
	// if (args[i].equals("--name")) {
	// i++;
	// continue;
	// }
	// if (args[i].equals("-d")) {
	// i++;
	// continue;
	// }
	// if (args[i].equals("--address")) {
	// i++;
	// continue;
	// }
	// if (args[i].equals("-r"))
	// continue;
	// if (args[i].equals("--run"))
	// continue;
	// argCount++;
	// }
	//
	// // System.out.println("Arg count is " + argCount);
	//
	// // Build the linker arguments
	// int index = 0;
	// tinyVMArgs = new String[argCount + 2];
	//
	// if (binName == null)
	// binName = firstArg + ".nxj";
	//
	// for (int i = 0; i < args.length; i++) {
	// if (args[i].equals("-b"))
	// continue;
	// if (args[i].equals("--bluetooth"))
	// continue;
	// if (args[i].equals("-u"))
	// continue;
	// if (args[i].equals("--usb"))
	// continue;
	// if (args[i].equals("-n")) {
	// i++;
	// continue;
	// }
	// if (args[i].equals("--name")) {
	// i++;
	// continue;
	// }
	// if (args[i].equals("-d")) {
	// i++;
	// continue;
	// }
	// if (args[i].equals("--address")) {
	// i++;
	// continue;
	// }
	// if (args[i].equals("-r"))
	// continue;
	// if (args[i].equals("--run"))
	// continue;
	// tinyVMArgs[index++] = args[i];
	// }
	// tinyVMArgs[argCount] = "-o";
	// tinyVMArgs[argCount + 1] = binName;
	//
	// // link
	// LeJOSNXJUtil.message("Linking...");
	// _tinyVM.start(tinyVMArgs);
	//
	// // send file
	// try {
	// File f = new File(binName);
	// String result = SendFile.sendFile(NXTCommand.getSingleton(), f);
	// } catch (Throwable t) {
	// throw new NXJUploadException("Exception during upload", t);
	// }
	// }
}