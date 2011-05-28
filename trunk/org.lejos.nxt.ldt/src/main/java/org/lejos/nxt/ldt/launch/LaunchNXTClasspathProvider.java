package org.lejos.nxt.ldt.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.StandardClasspathProvider;

public class LaunchNXTClasspathProvider extends StandardClasspathProvider {
	
	public final static String ID = "org.lejos.nxt.ldt.LaunchNXTClasspathProvider";

	public IRuntimeClasspathEntry[] computeUnresolvedClasspath(ILaunchConfiguration configuration) throws CoreException {
		return super.computeUnresolvedClasspath(configuration);
	}
}
