package org.lejos.nxt.ldt.launch;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.jdi.Bootstrap;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.lejos.nxt.ldt.LeJOSPlugin;
import org.lejos.nxt.ldt.preferences.PreferenceConstants;
import org.lejos.nxt.ldt.util.ExternalJVMToolStarter;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;
import org.lejos.nxt.ldt.util.PipeThread;
import org.lejos.nxt.ldt.util.PrefsResolver;
import org.lejos.nxt.ldt.util.ToolStarter;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Connector.IntegerArgument;

public class LaunchNXTConfigDelegate extends AbstractJavaLaunchConfigurationDelegate {
	public static final String ID_TYPE = "org.lejos.nxt.ldt.LaunchType";
	
	//TODO we should make sure, that uploads to the same NXT are executed sequentially, not in parallel
	
	private boolean resolve(PrefsResolver p, ILaunchConfiguration config,
			String defSwitch, String suffix, boolean def) throws CoreException {
		if (config.getAttribute(defSwitch, true))
			return p.getBoolean(suffix, def);
		else
			return config.getAttribute(LaunchConstants.PREFIX+suffix, def);
	}

	private String resolve(PrefsResolver p, ILaunchConfiguration config,
			String defSwitch, String suffix, String def) throws CoreException {
		if (config.getAttribute(defSwitch, true))
			return p.getString(suffix, def);
		else
			return config.getAttribute(LaunchConstants.PREFIX+suffix, def);
	}

	public void launch(ILaunchConfiguration config, String mode,	ILaunch launch, IProgressMonitor monitor)
		throws CoreException
	{
		if (monitor == null)
			monitor = new NullProgressMonitor();
		
		monitor.beginTask("Launching "+config.getName()+"...", 3); //$NON-NLS-1$
		
		PrefsResolver p = new PrefsResolver(LeJOSPlugin.ID, null);
		
		String connType = resolve(p, config, LaunchConstants.KEY_TARGET_USE_DEFAULTS,
				PreferenceConstants.KEY_TARGET_BUS, PreferenceConstants.VAL_TARGET_BUS_BOTH);
		boolean connectByAddr = resolve(p, config, LaunchConstants.KEY_TARGET_USE_DEFAULTS,
				PreferenceConstants.KEY_TARGET_CONNECT_BY_ADDR, false);
		boolean connectByName = resolve(p, config, LaunchConstants.KEY_TARGET_USE_DEFAULTS,
				PreferenceConstants.KEY_TARGET_CONNECT_BY_NAME, false);
		String brickAddr = resolve(p, config, LaunchConstants.KEY_TARGET_USE_DEFAULTS,
				PreferenceConstants.KEY_TARGET_BRICK_ADDR, "");
		String brickName = resolve(p, config, LaunchConstants.KEY_TARGET_USE_DEFAULTS,
				PreferenceConstants.KEY_TARGET_BRICK_NAME, "");
		
		String defSwitch = LaunchConstants.PREFIX+mode+LaunchConstants.SUFFIX_USE_DEFAULT;
		boolean verbose = resolve(p, config, defSwitch, mode+LaunchConstants.SUFFIX_LINK_VERBOSE, false);
		boolean run = resolve(p, config, defSwitch, mode+LaunchConstants.SUFFIX_RUN_AFTER_UPLOAD, true);
		boolean debugNormal, debugRemote, debugJDWP;
		if (!ILaunchManager.DEBUG_MODE.equals(mode))
			debugNormal = debugRemote = debugJDWP = false;
		else
		{
			String monType = resolve(p, config, defSwitch, mode+LaunchConstants.SUFFIX_MONITOR_TYPE,
					PreferenceConstants.VAL_DEBUG_TYPE_NORMAL);
			debugNormal = PreferenceConstants.VAL_DEBUG_TYPE_NORMAL.equals(monType);
			debugRemote = PreferenceConstants.VAL_DEBUG_TYPE_RCONSOLE.equals(monType);
			debugJDWP = PreferenceConstants.VAL_DEBUG_TYPE_JDWP.equals(monType);
		}
		
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
				else if (debugJDWP)
					args.add("-gj");
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
				LeJOSNXJUtil.getUploadOpts(args, connType, connectByAddr ? brickAddr : null,
						connectByName ? brickName : null);
				if (run)
					args.add("-r");			
				args.add(binaryPath);
				
				r = starter.invokeTool(LeJOSNXJUtil.TOOL_UPLOAD, args);
				if (r != 0)
					LeJOSNXJUtil.error("uploading the program failed with exit status "+r);
				else {
					LeJOSNXJUtil.message("program has been uploaded");
					
					if(run && debugJDWP) {
						LeJOSNXJUtil.message("Starting proxy ...");
						monitor.subTask("Starting proxy ...");		
						
						// Wait a bit to give the program time to start
						Thread.sleep(3000);
						
						// Find a free port
						int port = DebugUtil.findFreePort();
						
						// Start nxjdebugproxy. 
						ArrayList<String> proxyargs = new ArrayList<String>();
						LeJOSNXJUtil.getUploadOpts(proxyargs, connType, connectByAddr ? brickAddr : null,
								connectByName ? brickName : null);
						proxyargs.add("-l");
						proxyargs.add(Integer.toString(port));
						
						proxyargs.add("-di");
						proxyargs.add(binaryDebugPath);
						
						
						// The proxy has to run asyncronously, so we create a new process
						ExternalJVMToolStarter externalStarter;
						if (starter instanceof ExternalJVMToolStarter) {
							externalStarter = (ExternalJVMToolStarter) starter;
						} else {
							externalStarter = (ExternalJVMToolStarter) LeJOSNXJUtil.getCachedExternalStarter();
						}
						Process proxy = externalStarter.createProcess(LeJOSNXJUtil.TOOL_DEBUG_PROXY, proxyargs);
						
						LeJOSPlugin p2 = LeJOSPlugin.getDefault();
						Writer consw = p2.getConsoleWriter();
						
						new PipeThread(new InputStreamReader(proxy.getInputStream()), consw).start();
						new PipeThread(new InputStreamReader(proxy.getErrorStream()), consw).start();
						
						LeJOSNXJUtil.message("Proxy listening on port " + port);
						
						LeJOSNXJUtil.message("Starting debugger ...");
						monitor.subTask("Starting debugger ...");
						
						// Find the socket attach connector
						VirtualMachineManager mgr=Bootstrap.virtualMachineManager();
						
						List<?> connectors = mgr.attachingConnectors();
						
						AttachingConnector chosen=null;
						for (Iterator<?> iterator = connectors.iterator(); iterator
								.hasNext();) {
							AttachingConnector conn = (AttachingConnector) iterator.next();
							if(conn.name().contains("SocketAttach")) {
								chosen=conn;
								break;
							}
						}
						
						if(chosen == null) 
							LeJOSNXJUtil.error("No suitable connector");
						else {
							Map<?, ?> connectorArgs = chosen.defaultArguments();
							Connector.IntegerArgument portArg = (IntegerArgument) connectorArgs.get("port");
							portArg.setValue(port);
							
							VirtualMachine vm = chosen.attach(connectorArgs);
							LeJOSNXJUtil.message("Connection established");
							
							JDIDebugModel.newDebugTarget(launch, vm, simpleName, null, true, true, true);
						}
					}
				}
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
