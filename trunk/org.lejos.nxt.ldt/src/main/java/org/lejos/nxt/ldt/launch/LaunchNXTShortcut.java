package org.lejos.nxt.ldt.launch;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;

public class LaunchNXTShortcut extends JavaApplicationLaunchShortcut
{
	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(LaunchNXTConfigDelegate.ID_TYPE);
	}
}
