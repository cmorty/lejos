package org.lejos.nxt.ldt.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.lejos.nxt.ldt.LeJOSNature;
import org.lejos.nxt.ldt.LeJOSPlugin;
import org.lejos.nxt.ldt.preferences.PreferenceConstants;

/**
 * utility methods for the plugin
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class LeJOSNXJUtil {

	public static final String LIBDIR = "lib";
	public static final String LIBSUBDIR_PC = "pc";
	public static final String LIBSUBDIR_NXT = "nxt";
	
	public static boolean isWindows() {
		return System.getProperty("os.name", "").toLowerCase().startsWith("windows ");
	}

	public static boolean isOSX() {
		return System.getProperty("os.name", "").toLowerCase().startsWith("mac os x");
	}

	public static boolean getJavaProjectFromSelection(ISelection selection, Collection<IJavaProject> dst) {
		boolean foundInvalid = false;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection)selection;
			for (Iterator<?> it = ss.iterator(); it.hasNext();) {
				Object element = it.next();
				if (element instanceof IJavaProject) {
					// we see IJavaProjects when selecting in package explorer
					dst.add((IJavaProject) element);
				} else if (element instanceof IProject) {
					// we see IProjects when selecting in project explorer, or navigator.
					IProject p = (IProject) element;
					try {
						if (p.isOpen() && p.isNatureEnabled(JavaCore.NATURE_ID))
							dst.add(JavaCore.create(p));
					} catch (CoreException e) {
						foundInvalid = true;
					}
				} else {
					foundInvalid = true;
				}
			}
		}
		return foundInvalid;
	}
	
	public static boolean getFilesFromSelection(ISelection selection, Collection<File> dst)
	{
		boolean foundInvalid = false;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			for (Iterator<?> it = ss.iterator(); it.hasNext(); ) {
				Object element = it.next();
				if (element instanceof IFile) {
					IFile f = (IFile)element;
					dst.add(f.getLocation().toFile());
				} else {
					foundInvalid = true;
				}
			}
		}
		return foundInvalid;
	}

	public static IJavaElement getFirstJavaElementFromSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			for (Iterator<?> it = ss.iterator(); it.hasNext(); ) {
				Object element = it.next();
				if (element instanceof IJavaElement) {
					// we see IJavaProjects when selecting in package explorer
					IJavaElement e = (IJavaElement) element;
					return e;
				} else if (element instanceof IFile) {
					// we see IFile when selecting in project explorer, or navigator.
					IJavaElement e = JavaCore.create((IFile)element);
					if (e != null)
						return e;
				}
			}
		}
		return null;
	}

	public static IType getJavaTypeFromElement(IJavaElement element) {
		switch (element.getElementType())
		{
			case IJavaElement.COMPILATION_UNIT:
				return ((ICompilationUnit)element).findPrimaryType();
			case IJavaElement.TYPE:
				return (IType)element;
		}		
		return null;
	}

	public static boolean isLeJOSProject(IProject project) throws CoreException {
		return project.isNatureEnabled(LeJOSNature.ID);
	}

	public static boolean isLeJOSProject(IJavaProject project) throws CoreException {
		return isLeJOSProject(project.getProject());
	}

	public static void message(String message) {
		message(message, null);
	}

	public static void message(String msg, Throwable throwable) {
		// log to leJOS NXJ console
		PrintWriter pw = LeJOSPlugin.getDefault().getConsoleWriter();
		if (msg != null)
			pw.println(msg);
		if (throwable != null)
		{
			pw.println(throwable);
			Throwable t = throwable.getCause();
			while (t != null)
			{
				pw.println("Caused by "+t);
				t = t.getCause();
			}
			pw.println("See Eclipse error log for detailed stack trace.");
			log(throwable);
		}
	}

	public static void error(String message) {
		error(message, null);
	}

	public static void error(String msg, Throwable throwable) {
		LeJOSPlugin.getDefault().getConsole().activate();
		message(msg, throwable);
	}

	public static void log(Throwable throwable) {
		// log to error log
		Status status = new Status(IStatus.ERROR, LeJOSPlugin.ID, throwable.getMessage(), throwable);
		LeJOSPlugin.getDefault().getLog().log(status);
	}

	private static void walkTreeAndReturnJARS(File dir, Collection<File> dst)
	{
		for (File e : dir.listFiles())
		{
			if (e.isDirectory())
				walkTreeAndReturnJARS(e, dst);
			else
			{
				if (e.getName().toLowerCase().endsWith(".jar"))
				{
					dst.add(e);
				}
			}
		}
	}
	
	public static void buildClasspath(File nxjHome, String subdir, Collection<File> dst) throws LeJOSNXJException
	{
		File f1 = new File(nxjHome, LIBDIR);
		File f2 = new File(f1, subdir);
		if (!f2.isDirectory())
			throw new LeJOSNXJException(f2+" is not a directory");
		
		walkTreeAndReturnJARS(f2, dst);
	}
	
	public static void buildNXTClasspath(File nxjHome, Collection<File> dst) throws LeJOSNXJException
	{
		buildClasspath(nxjHome, LIBSUBDIR_NXT, dst);
	}
	
	public static void buildPCClasspath(File nxjHome, Collection<File> dst) throws LeJOSNXJException
	{
		buildClasspath(nxjHome, LIBSUBDIR_PC, dst);
	}
	
	
	public static File getNXJHome() throws LeJOSNXJException
	{
		// get NXJ_HOME
		PrefsResolver p = new PrefsResolver(LeJOSPlugin.ID, null);
		String nxjHome = p.getString(PreferenceConstants.KEY_NXJ_HOME, null);
		
		if (nxjHome == null || nxjHome.length() <= 0)
			throw new LeJOSNXJException("NXJ_HOME is not set. Please specify it in the plug-in's preferences");
		
		File f = new File(nxjHome);
		if (!f.isDirectory())
			throw new LeJOSNXJException("NXJ_HOME="+f+" is not a directory");

		return f;
	}
	
	public static void getUploadOpts(List<String> dst) throws LeJOSNXJException
	{
		PrefsResolver p = new PrefsResolver(LeJOSPlugin.ID, null);
		
		String connectionType = p.getString(PreferenceConstants.KEY_CONNECTION_TYPE, null);
		if (PreferenceConstants.VAL_CONNECTION_TYPE_BLUETOOTH.equals(connectionType))
			dst.add("-b");
		else if (PreferenceConstants.VAL_CONNECTION_TYPE_USB.equals(connectionType))
			dst.add("-u");
		else if (PreferenceConstants.VAL_CONNECTION_TYPE_BOTH.equals(connectionType))
		{
			// don't add anything, since usb+bluetooth is default
		}
		else
			throw new LeJOSNXJException("illegal connection type");
		
		if (p.getBoolean(PreferenceConstants.KEY_CONNECT_TO_BRICK_ADDRESS, false))
		{
			dst.add("-d");
			dst.add(p.getString(PreferenceConstants.KEY_CONNECTION_BRICK_ADDRESS, ""));
		}
		if (p.getBoolean(PreferenceConstants.KEY_CONNECT_TO_NAMED_BRICK, false))
		{
			dst.add("-n");
			dst.add(p.getString(PreferenceConstants.KEY_CONNECTION_BRICK_NAME, ""));
		}
	}

	public static File resolvePath(IPath path)
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource res = root.findMember(path);
		if (res != null)
			path = res.getLocation();
		
		return path.toFile();
	}
	
	public static IPath toPath(File f)
	{
		return Path.fromOSString(f.getAbsolutePath());
	}

	/**
	 * 
	 * build the classpath for the link and upload utility
	 * 
	 * @param project
	 * @return String classpath
	 * @throws JavaModelException
	 * @throws LeJOSNXJException 
	 */
	public static void getProjectClassPath(IJavaProject project, boolean onlyExported, List<File> dst) throws JavaModelException, LeJOSNXJException {
		dst.add(resolvePath(project.getOutputLocation()));
		// project's classpath
		IClasspathEntry[] entries = project.getResolvedClasspath(true);
		// build string
		for (IClasspathEntry classpathEntry : entries) {
			if (!onlyExported || classpathEntry.isExported())
			{
				switch (classpathEntry.getEntryKind()) {
					case IClasspathEntry.CPE_SOURCE: // source => ignore
						IPath p = classpathEntry.getOutputLocation();
						if (p != null)
							dst.add(resolvePath(p));
						break;
					case IClasspathEntry.CPE_PROJECT: // another project =>
						// append classpath of other project
						IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
						IProject referencedProject = workspaceRoot.getProject(classpathEntry.getPath().toString());
				        IJavaProject referencedJavaProject = JavaCore.create(referencedProject);
				        getProjectClassPath(referencedJavaProject, true, dst);
						break;
					case IClasspathEntry.CPE_LIBRARY:
						dst.add(resolvePath(classpathEntry.getPath()));
						break;
					case IClasspathEntry.CPE_VARIABLE:
					case IClasspathEntry.CPE_CONTAINER:
						// variable and container should never occur, since we use resolved classpath
					default:
						throw new LeJOSNXJException("unsupported classpath entry "+classpathEntry);
				}
			}
		}
	}
	
	public static String getClasspathString(List<File> c)
	{
		StringBuilder sb = new StringBuilder();
		Iterator<File> i = c.iterator();
		if (i.hasNext())
		{
			sb.append(i.next().getAbsolutePath());
			while (i.hasNext())
			{
				sb.append(File.pathSeparatorChar);
				sb.append(i.next().getAbsolutePath());
			}
		}
		return sb.toString();
	}
	
	private static ToolStarter currentStarter;
	private static ToolStarter currentStarterExt;
	private static boolean currentStarterType;

	private static synchronized void updateStarters() throws LeJOSNXJException
	{
		PrefsResolver p = new PrefsResolver(LeJOSPlugin.ID, null);
		boolean separateJVM = p.getBoolean(PreferenceConstants.KEY_SEPARATE_JVM, false);
		File nxjHome = getNXJHome();
		
		if (currentStarterExt == null || !nxjHome.equals(currentStarterExt.getNxjHome()) || !currentStarterExt.isUp2Date())
		{
			currentStarterExt = new ExternalJVMToolStarter(nxjHome);
		}
		if (currentStarter == null || currentStarterType != separateJVM || !nxjHome.equals(currentStarter.getNxjHome()) || !currentStarter.isUp2Date())
		{
			if (separateJVM)
				currentStarter = currentStarterExt;
			else
				currentStarter = new ClassLoaderToolStarter(nxjHome);
			
			currentStarterType = separateJVM;
		}
	}
	
	public static synchronized ToolStarter getCachedExternalStarter() throws LeJOSNXJException
	{
		updateStarters();
		return currentStarterExt;
	}
	
	public static synchronized ToolStarter getCachedToolStarter() throws LeJOSNXJException
	{
		updateStarters();
		return currentStarter;
	}

	public static final String TOOL_UPLOAD = "lejos.pc.tools.NXJUpload";
	public static final String TOOL_FLASH = "lejos.pc.tools.NXJFlash";
	public static final String TOOL_FLASHG = "lejos.pc.tools.NXJFlashG";
	public static final String TOOL_LINK_AND_UPLOAD = "lejos.pc.tools.NXJLinkAndUpload";
	public static final String TOOL_LINK = "lejos.pc.tools.NXJLink";

	public static Process exec(List<String> args2) throws IOException
	{
		if (!isWindows())
		{
			String[] args3 = args2.toArray(new String[args2.size()]);
			return Runtime.getRuntime().exec(args3);
		}
		
		if (args2.isEmpty())
			throw new IndexOutOfBoundsException("command is an empty list");
		
		// Both java.lang.Runtime.exec(String[]) as well as in java.lang.ProcessBuilder
		// don't escape the arguments that are passed to the program. Also, they fail to
		// handle the empty string correctly. Hence, we manually escape all arguments.
		StringBuilder sb = new StringBuilder();
		for (String t : args2)
		{
			sb.append(' ');
			escapeWindowsArg(t, sb);
		}
		
		return Runtime.getRuntime().exec(sb.substring(1));
	}

	private static void escapeWindowsArg(String t, StringBuilder sb) {
		// escaping according to CommandLineToArgvW 
		// http://msdn.microsoft.com/de-de/site/bb776391
		int len = t.length();
		sb.append("\"");
		for (int i=0; i<len; i++)
		{
			char c = t.charAt(i);
			switch (c)
			{
				case '\\':
				case '"':
					sb.append('\\');
				default:
					sb.append(c);
			}
		}
		sb.append("\"");
	}
}
