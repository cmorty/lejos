package org.lejos.nxt.ldt.variables;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.lejos.nxt.ldt.LeJOSPlugin;
import org.lejos.nxt.ldt.util.LeJOSNXJException;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public class NXJHomeResolver implements IDynamicVariableResolver {

	public String resolveValue(IDynamicVariable variable, String argument) throws CoreException
	{
		try {
			return LeJOSNXJUtil.getNXJHome().getAbsolutePath();
		} catch (LeJOSNXJException e) {
			throw new CoreException(new Status(IStatus.ERROR, LeJOSPlugin.ID, "could not determine NXJ_HOME", e));
		}
	}
}
