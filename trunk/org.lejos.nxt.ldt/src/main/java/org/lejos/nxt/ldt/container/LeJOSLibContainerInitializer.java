package org.lejos.nxt.ldt.container;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.lejos.nxt.ldt.util.LeJOSNXJException;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;


public class LeJOSLibContainerInitializer extends ClasspathContainerInitializer {

	@Override
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException
	{
		System.out.println("init");
		LeJOSLibContainer container;
		try {
			container = new LeJOSLibContainer(containerPath);
		} catch (LeJOSNXJException e) {
			LeJOSNXJUtil.log(e);
			// JDT will show an error when using null
			container = null;
		}
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
		this.initialize(containerPath, project);
	}

}
