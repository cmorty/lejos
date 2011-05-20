package org.lejos.nxt.ldt.container;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * This classpath container initializer constructs a SimpleDirContainer with the
 * give container path and Java project. It then validates the container before
 * setting it in the classpath. If the container is invalid, it fails silently
 * and logs an error to the Eclipse error log.
 * 
 * @author Aaron J Tarter
 */
public class LeJOSLibContainerInitializer extends ClasspathContainerInitializer {

	@Override
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException
	{
		LeJOSLibContainer container = new LeJOSLibContainer(containerPath);
		JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project }, new IClasspathContainer[] { container }, null);
	}

	@Override
	public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project)
	{
		return true;
	}

	@Override
	public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion)	throws CoreException
	{
		JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project }, new IClasspathContainer[] { containerSuggestion }, null);
	}

}
