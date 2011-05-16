package org.lejos.nxt.ldt.util;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
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
import org.eclipse.ui.console.IOConsole;
import org.lejos.nxt.ldt.LeJOSNXJPlugin;
import org.lejos.nxt.ldt.builder.leJOSNature;
import org.lejos.nxt.ldt.preferences.PreferenceConstants;

/**
 * utility methods for the plugin
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class LeJOSNXJUtil {

	public static boolean getJavaProjectFromSelection(ISelection selection, Collection<IJavaProject> dst) {
		boolean foundInvalid = false;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection)selection;
			for (Iterator<?> it = ss.iterator(); it.hasNext();) {
				Object element = it.next();
				if (element instanceof IJavaProject) {
					dst.add((IJavaProject) element);
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
					IJavaElement e = (IJavaElement) element;
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

	public static boolean isLeJOSProject(IJavaProject project) throws CoreException {
		return project.getProject().hasNature(leJOSNature.NATURE_ID);
	}

	public static void message(String message) {
		// log to leJOS NXJ console
		PrintWriter console = LeJOSNXJPlugin.getDefault().getConsoleWriter();
		console.println(message);
		// log to error log
		//LeJOSNXJPlugin.getDefault().logEvent(message);
	}

	public static void message(String msg, Throwable throwable) {
		// log to leJOS NXJ console
		PrintWriter pw = LeJOSNXJPlugin.getDefault().getConsoleWriter();
		if (msg != null)
			pw.println(msg);
		if (throwable != null)
		{
			pw.println(throwable);
			pw.println("See Eclipse error log for detailed stack trace.");
			log(throwable);
		}
	}

	public static void log(Throwable throwable) {
		// log to error log
		Status status = new Status(IStatus.ERROR, LeJOSNXJPlugin.PLUGIN_ID, throwable.getMessage(), throwable);
		LeJOSNXJPlugin.getDefault().getLog().log(status);
	}

	public static String getFullQualifiedClassName(IType element) {
		String fullQualifiedName = element.getFullyQualifiedName('$');
		return fullQualifiedName;
	}

	public static String getSimpleClassName(IType element) {
		// returns only "Nested" for class "foo.bar.Outer.Nested"
		return element.getElementName();
	}

	private static void walkDir(File dir, Collection<File> dst)
	{
		for (File e : dir.listFiles())
		{
			if (e.isDirectory())
				walkDir(e, dst);
			else
			{
				if (e.getName().toLowerCase().endsWith(".jar"))
				{
					dst.add(e);
				}
			}
		}
	}
	
	public static File getNXJHome() throws LeJOSNXJException
	{
		// get NXJ_HOME
		String nxjHome = LeJOSNXJPlugin.getDefault().getPluginPreferences()
				.getString(PreferenceConstants.KEY_NXJ_HOME);
		
		if (nxjHome == null || nxjHome.isEmpty())
			throw new LeJOSNXJException("NXJ_HOME is not set. Please specify it in the plug-in's preferences");
		
		File f = new File(nxjHome);
		if (!f.isDirectory())
			throw new LeJOSNXJException("NXJ_HOME="+f+" is not a directory");

		return f;
	}
	
	public static void buildNXTClasspath(File nxjHome, Collection<File> dst) throws LeJOSNXJException
	{
		File f = new File(nxjHome, "lib/nxt");
		if (!f.isDirectory())
			throw new LeJOSNXJException("NXJ_HOME="+nxjHome+" does not point to a leJOS installation");
			
		walkDir(f, dst);
	}
	
	public static void buildPCClasspath(File nxjHome, Collection<File> dst) throws LeJOSNXJException
	{
		File f = new File(nxjHome, "lib/pc");
		if (!f.isDirectory())
			throw new LeJOSNXJException("NXJ_HOME="+nxjHome+" does not point to a leJOS installation");
		
		walkDir(f, dst);
	}
	
	
	private static File currentNxjHome;
	private static ClassLoader currentClassLoader;
	
	public static ClassLoader getCachedPCClassLoader(File nxjHome) throws LeJOSNXJException
	{
		if (currentClassLoader == null || !nxjHome.equals(currentNxjHome))
		{
			message("Initializing LeJOS JDK at "+nxjHome);
			
			ArrayList<File> tmp = new ArrayList<File>();
			buildPCClasspath(nxjHome, tmp);
			URL[] urls = new URL[tmp.size()];
			int i = 0;
			for (File e : tmp)
			{
				try
				{
					urls[i++] = e.toURI().toURL();
				}
				catch (MalformedURLException e1)
				{
					throw new RuntimeException(e1);
				}
			}
			
			URLClassLoader cl = new URLClassLoader(urls);
			initializeSystemContext(cl, nxjHome);
			
			currentNxjHome = nxjHome;
			currentClassLoader = cl;
		}
		return currentClassLoader;
	}

	private static void initializeSystemContext(ClassLoader cl, File nxjHome) throws LeJOSNXJException
	{
		LeJOSNXJPlugin p = LeJOSNXJPlugin.getDefault();
		IOConsole con = p.getConsole();
		Writer consw = p.getConsoleWriter();
//		OutputStream cons = con.newOutputStream();
//		OutputStreamWriter consw;
		InputStream cins = con.getInputStream();
		InputStreamReader cinsr;
		try
		{
//			consw = new OutputStreamWriter(cons, "utf8");
			cinsr = new InputStreamReader(cins, "utf8");
		}
		catch (UnsupportedEncodingException e1)
		{
			throw new RuntimeException(e1);
		}
		
		try
		{
			Class<?> c = cl.loadClass("lejos.pc.comm.SystemContext");
			Method m;
			
			m = c.getDeclaredMethod("setNxjHome", String.class);
			m.invoke(null, nxjHome.getAbsolutePath());
			
			m = c.getDeclaredMethod("setOut", Writer.class);
			m.invoke(null, consw);
			
			m = c.getDeclaredMethod("setErr", Writer.class);
			m.invoke(null, consw);
			
			m = c.getDeclaredMethod("setIn", Reader.class);
			m.invoke(null, cinsr);
		}
		catch (Exception e)
		{
			throw new LeJOSNXJException("unanble to initialize system context", e);
		}
	}
	
	public static void getCmdLineOpts(List<String> dst, boolean linker) throws LeJOSNXJException
	{
		Preferences p = LeJOSNXJPlugin.getDefault().getPluginPreferences();
		
		String connectionType = p.getString(PreferenceConstants.KEY_CONNECTION_TYPE);
		if (PreferenceConstants.VAL_PROTOCOL_BLUETOOTH.equals(connectionType))
			dst.add("-b");
		else if (PreferenceConstants.VAL_PROTOCOL_USB.equals(connectionType))
			dst.add("-u");
		else if (PreferenceConstants.VAL_PROTOCOL_BOTH.equals(connectionType))
		{
			// don't add anything, since usb+bluetooth is default
		}
		else
			throw new LeJOSNXJException("illegal connection type");
		
		if (p.getBoolean(PreferenceConstants.KEY_CONNECT_TO_BRICK_ADDRESS))
		{
			dst.add("-d");
			dst.add(p.getString(PreferenceConstants.KEY_CONNECTION_BRICK_ADDRESS));
		}
		else if (p.getBoolean(PreferenceConstants.KEY_CONNECT_TO_NAMED_BRICK))
		{
			dst.add("-n");
			dst.add(p.getString(PreferenceConstants.KEY_CONNECTION_BRICK_NAME));
		}
		
		if (linker)
		{
			if (p.getBoolean(PreferenceConstants.KEY_IS_VERBOSE))
				dst.add("-v");
			if (p.getBoolean(PreferenceConstants.KEY_RUN_AFTER_UPLOAD))
				dst.add("-r");			
		}
	}


	public static File resolvePath(IPath path) throws JavaModelException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource res = root.findMember(path);
		if (res != null)
			path = res.getLocation();
		
		return path.toFile();
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
}
