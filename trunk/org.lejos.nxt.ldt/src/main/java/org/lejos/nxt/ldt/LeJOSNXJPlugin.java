package org.lejos.nxt.ldt;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LeJOSNXJPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.lejos.nxt.ldt";

	// The shared instance
	private static LeJOSNXJPlugin plugin;
	
	/**
	 * The constructor
	 */
	public LeJOSNXJPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LeJOSNXJPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**
	 * logs messages to Eclipse status facilities
	 * @param message
	 */
	public void log(String message) {
		Status status = new Status(IStatus.INFO,PLUGIN_ID,IStatus.OK,message,null);
		getDefault().getLog().log(status);
	}

	/**
	 * logs messages to Eclipse status facilities
	 * @param message
	 */
	public void log(Throwable throwable) {
		Status status = new Status(IStatus.ERROR,PLUGIN_ID,throwable.getMessage(),throwable);
		getDefault().getLog().log(status);
	}
	
}

