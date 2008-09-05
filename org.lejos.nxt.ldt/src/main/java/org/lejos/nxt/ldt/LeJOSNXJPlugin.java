package org.lejos.nxt.ldt;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class for the plugin
 * 
 * @author Matthias Paul Scholz
 */
public class LeJOSNXJPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.lejos.nxt.ldt";

	// The shared instance
	private static LeJOSNXJPlugin plugin;

	// the leJOS NXJ console
	private MessageConsole _leJOSNXJConsole;

	/**
	 * The constructor
	 */
	public LeJOSNXJPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
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
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * logs messages to Eclipse status facilities
	 * 
	 * @param message
	 */
	public void log(String message) {
		Status status = new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK,
				message, null);
		getDefault().getLog().log(status);
	}

	/**
	 * logs messages to Eclipse status facilities
	 * 
	 * @param message
	 */
	public void log(Throwable throwable) {
		Status status = new Status(IStatus.ERROR, PLUGIN_ID, throwable
				.getMessage(), throwable);
		getDefault().getLog().log(status);
	}

	public MessageConsole getLeJOSNXJConsole() {
		if (_leJOSNXJConsole == null) {
			// create console
			_leJOSNXJConsole = new MessageConsole("leJOS NXJ",
					getImageDescriptor("icons/nxt.jpg"));
			// add to console manager
			ConsolePlugin plugin = ConsolePlugin.getDefault();
			IConsoleManager conMan = plugin.getConsoleManager();
			conMan.addConsoles(new IConsole[] { _leJOSNXJConsole });
		}

//		// make it visible
//		String id = IConsoleConstants.ID_CONSOLE_VIEW;
//		IWorkbench wb = PlatformUI.getWorkbench();
//		if (wb != null) {
//			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
//			if (win != null) {
//				IWorkbenchPage page = win.getActivePage();
//				if (page != null) {
//					try {
//						IConsoleView view = (IConsoleView) page.showView(id);
//						view.display(_leJOSNXJConsole);
//					} catch (PartInitException p) {
//						log(p);
//					}
//				}
//			}
//		}
		return _leJOSNXJConsole;
	}
}
