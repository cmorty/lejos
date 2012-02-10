package org.lejos.nxt.ldt.launch;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.lejos.nxt.ldt.LeJOSPlugin;
import org.lejos.nxt.ldt.preferences.PreferenceConstants;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;
import org.lejos.nxt.ldt.util.PrefsResolver;
import org.lejos.nxt.ldt.util.ToolStarter;

public class LaunchNXTConfigDelegate extends AbstractJavaLaunchConfigurationDelegate {
	public static final String ID_TYPE = "org.lejos.nxt.ldt.LaunchType";
	
	//TODO we should make sure, that uploads to the same NXT are executed sequentially, not in parallel
	
	private boolean resolve(PrefsResolver p, ILaunchConfiguration config,
			String mode, String suffix, boolean def) throws CoreException {
		boolean useDef = config.getAttribute(LaunchConstants.PREFIX+mode
				+LaunchConstants.SUFFIX_USE_DEFAULT, true);
		if (useDef)
			return p.getBoolean(mode+suffix, def);
		else
			return config.getAttribute(LaunchConstants.PREFIX+mode+suffix, def);
	}

	private String resolve(PrefsResolver p, ILaunchConfiguration config,
			String mode, String suffix, String def) throws CoreException {
		boolean useDef = config.getAttribute(LaunchConstants.PREFIX+mode
				+LaunchConstants.SUFFIX_USE_DEFAULT, true);
		if (useDef)
			return p.getString(mode+suffix, def);
		else
			return config.getAttribute(LaunchConstants.PREFIX+mode+suffix, def);
	}

	public void launch(ILaunchConfiguration config, String mode,	ILaunch launch, IProgressMonitor monitor)
		throws CoreException
	{
		if (monitor == null)
			monitor = new NullProgressMonitor();
		
		monitor.beginTask("Launching "+config.getName()+"...", 3); //$NON-NLS-1$
		
		PrefsResolver p = new PrefsResolver(LeJOSPlugin.ID, null);
		boolean verbose = resolve(p, config, mode, LaunchConstants.SUFFIX_LINK_VERBOSE, false);
		boolean run = resolve(p, config, mode, LaunchConstants.SUFFIX_RUN_AFTER_UPLOAD, true);
		String monType = resolve(p, config, mode, LaunchConstants.SUFFIX_MONITOR_TYPE,
				PreferenceConstants.VAL_DEBUG_TYPE_NORMAL);
		boolean debugNormal = PreferenceConstants.VAL_DEBUG_TYPE_NORMAL.equals(monType);
		boolean debugRemote = PreferenceConstants.VAL_DEBUG_TYPE_REMOTE.equals(monType);

		if (monitor.isCanceled())
			return;
		
		try
		{
			monitor.subTask("Verifying lauch configuration ..."); 
			
			String mainTypeName = this.verifyMainTypeName(config);	
			String[] bootpath = this.getBootpath(config);
			String[] classpath = this.getClasspath(config);
			IJavaProject project = this.verifyJavaProject(config);
			
			//TODO for some reason, the debug view fills with launch entries
			
			monitor.worked(1);			
			if (monitor.isCanceled())
				return;
		
			ToolStarter starter = LeJOSNXJUtil.getCachedToolStarter();

			String simpleName;
			int i = mainTypeName.lastIndexOf('.');
			if (i < 0)
				simpleName = mainTypeName;
			else
				simpleName = mainTypeName.substring(i+1);
			
			IProject project2 = project.getProject();
			IFile binary = project2.getFile(simpleName+".nxj");
			IFile binaryDebug = project2.getFile(simpleName+".nxd");
			String binaryPath = binary.getLocation().toOSString();
			String binaryDebugPath = binaryDebug.getLocation().toOSString();
			
			monitor.worked(1);			
			monitor.beginTask("Linking and uploading program to the brick...", IProgressMonitor.UNKNOWN);
			int r;
			try
			{
				LeJOSNXJUtil.message("Linking ...");
				monitor.subTask("Linking ...");
				ArrayList<String> args = new ArrayList<String>();
				args.add("--writeorder");
				args.add("LE");
				if (verbose)
					args.add("-v");
				if (debugNormal)
					args.add("-g");
				else if (debugRemote)
					args.add("-gr");
				args.add("--bootclasspath");
				args.add(classpathToString(bootpath));
				args.add("--classpath");
				args.add(classpathToString(classpath));
				args.add("--output");
				args.add(binaryPath);
				args.add("--outputdebug");
				args.add(binaryDebugPath);
				args.add(mainTypeName);

				r = starter.invokeTool(LeJOSNXJUtil.TOOL_LINK, args);
			}
			finally
			{
				binary.refreshLocal(IResource.DEPTH_ZERO, monitor);
				binaryDebug.refreshLocal(IResource.DEPTH_ZERO, monitor);
			}
		
			if (r != 0)
				LeJOSNXJUtil.error("Linking the file failed with exit status "+r);
			else
			{
				LeJOSNXJUtil.message("Program has been linked successfully");
				
				LeJOSNXJUtil.message("Uploading ...");
				monitor.subTask("Uploading ...");					
				ArrayList<String> args = new ArrayList<String>();
				LeJOSNXJUtil.getUploadOpts(args);
				if (run)
					args.add("-r");			
				args.add(binaryPath);
				
				r = starter.invokeTool(LeJOSNXJUtil.TOOL_UPLOAD, args);
				if (r == 0)
					LeJOSNXJUtil.message("program has been uploaded");
				else
					LeJOSNXJUtil.error("uploading the program failed with exit status "+r);
			}
		}
		catch (Exception t)
		{
			Throwable t2 = t;
			if (t2 instanceof InvocationTargetException)
				t2 = ((InvocationTargetException)t).getTargetException();
			
			// log
			LeJOSNXJUtil.error("Linking or uploading the program failed", t2);
		}
		finally
		{
			monitor.done();
		}
	}

	private static String classpathToString(String[] cp)
	{
		if (cp == null || cp.length <= 0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		for (String f : cp)
		{
			sb.append(File.pathSeparatorChar);
			sb.append(f);
		}
		return sb.substring(1);
	}
}
