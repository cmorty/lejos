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
	
//	public void convertToLeJOSProject() {
//		IResource resource = Res;
//		IProject project = resource.getProject();
//	}

}

///**
//176     * sets a project's nature to "leJOS project"
//177     *
//178     * @param IProject the project
//179     * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
//180     * @throws CoreException
//181     */
//182    public static void addLeJOSNature (IProject aProject)
//183    {
//184       try
//185       {
//186          IProjectDescription description = aProject.getDescription();
//187          String[] natures = description.getNatureIds();
//188          String[] newNatures = new String[natures.length + 1];
//189          System.arraycopy(natures, 0, newNatures, 0, natures.length);
//190          newNatures[natures.length] = LEJOS_NATURE;
//191          description.setNatureIds(newNatures);
//192          aProject.getProject().setDescription(description, null);
//193       }
//194       catch (CoreException e)
//195       {
//196          LejosPlugin.debug(e);
//197          e.printStackTrace();
//198       }
//199    }
//200 
//201    /**
//202     * checks a project for leJOS nature
//203     *
//204     * @param IProject the project
//205     * @return boolean true, if the project has leJOS nature
//206     * @throws CoreException
//207     * @author <a href="mailto:mp.scholz@t-online.de">Matthias Paul Scholz </a>
//208     */
//209    public static boolean checkForLeJOSNature (IProject aProject)
//210       throws CoreException
//211    {
//212       // check project's natures
//213       IProjectDescription description = aProject.getDescription();
//214       String[] natures = description.getNatureIds();
//215       for (int i = 0; i < natures.length; i++)
//216       {
//217          if (natures[i].equals(LEJOS_NATURE))
//218             return true;
//219       }
//220       return false;
//221    }
//222 