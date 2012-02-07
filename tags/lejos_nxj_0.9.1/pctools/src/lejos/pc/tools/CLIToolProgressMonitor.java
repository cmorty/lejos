package lejos.pc.tools;

import js.common.ToolProgressMonitor;
import lejos.pc.comm.SystemContext;

/**
 * Simple implementation of ToolProgressMonitor with output to System.out.
 */
public class CLIToolProgressMonitor implements ToolProgressMonitor {

	private boolean _verbose = false;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see js.tools.ToolProgressMonitor#operation(java.lang.String)
	 */
	public void operation(String message) {
		assert message != null : "Precondition: message != null";
		SystemContext.out.println(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see js.tools.ToolProgressMonitor#log(java.lang.String)
	 */
	public void log(String message) {
		if (!_verbose)
			return;
		assert message != null : "Precondition: message != null";
		SystemContext.out.println(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see js.tools.ToolProgressMonitor#progress(int)
	 */
	public void progress(int progress) {
		assert progress >= 0 && progress <= 1000 : "Precondition: progress >= 0 && progress <= 1000";
		String message = "\r  " + (progress / 10) + "%\r";
		SystemContext.out.print(message);
		if (progress >= 1000) {
			SystemContext.out.println();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see js.common.ToolProgressMonitor#isCanceled()
	 */
	public boolean isCanceled() {
		return Thread.currentThread().isInterrupted();
	}

	/**
	 * Be verbose?
	 */
	public void setVerbose(boolean verbose) {
		_verbose = verbose;
	}
	
}