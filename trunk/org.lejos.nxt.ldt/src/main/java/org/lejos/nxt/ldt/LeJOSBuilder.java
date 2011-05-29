package org.lejos.nxt.ldt;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * leJOS project builder
 * @author Matthias Paul Scholz
 *
 */
public class LeJOSBuilder extends IncrementalProjectBuilder {

	public static final String ID = "org.lejos.nxt.ldt.leJOSBuilder";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		
		// nothing to do yet
		
		return null;
	}
}

