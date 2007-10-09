package js.common;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Simple implementation of ToolProgressMonitor with output to System.out.
 */
public class CLIToolProgressMonitor implements ToolProgressMonitor {

	private boolean _verbose = false;
	private Collection<JSToolsLogListener> fLogListeners;
	
	public CLIToolProgressMonitor() {
		fLogListeners = new ArrayList<JSToolsLogListener>();
	}
	
	public void reset() {
		_verbose = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see js.tools.ToolProgressMonitor#operation(java.lang.String)
	 */
	public void operation(String message) {
		assert message != null : "Precondition: message != null";
		System.out.println(message);
		notifyListeners(message);
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
		System.out.println(message);
		notifyListeners(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see js.tools.ToolProgressMonitor#progress(int)
	 */
	public void progress(int progress) {
		assert progress >= 0 && progress <= 1000 : "Precondition: progress >= 0 && progress <= 1000";
		String message = "\r  " + (progress / 10) + "%\r";
		System.out.print(message);
		notifyListeners(message);
		if (progress >= 1000) {
			System.out.println();
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
	
	/**
	 * register log listener
	 * @param listener
	 */
	public void addLogListener(JSToolsLogListener listener) {
		fLogListeners.add(listener);
	}
	
	/**
	 * unregister log listener
	 * @param listener
	 */
	public void removeLogListener(JSToolsLogListener listener) {
		fLogListeners.remove(listener);
	}

	private void notifyListeners(String message) {
		for (JSToolsLogListener listener : fLogListeners) {
			listener.logEvent(message);
		}
	}
}