package org.lejos.nxt.ldt.launch;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public class LaunchNXTConfigTabGroup extends AbstractLaunchConfigurationTabGroup
{
	public void createTabs(ILaunchConfigurationDialog dialog, String mode)
	{
		setTabs(new ILaunchConfigurationTab[] {
					new LaunchNXTMainTab(),
					new JavaClasspathTab(),
					new SourceLookupTab(),
					new CommonTab(),
				});
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		super.setDefaults(configuration);
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
				LaunchNXTClasspathProvider.ID);
	}
}
