package org.lejos.nxt.ldt.launch;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;
import org.eclipse.jface.operation.IRunnableContext;
import org.lejos.nxt.ldt.LeJOSPlugin;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public class LaunchNXTShortcut extends JavaApplicationLaunchShortcut
{
	@Override
	protected ILaunchConfigurationType getConfigurationType() {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(LaunchNXTConfigDelegate.ID_TYPE);
	}
	
	private IType getMainMethodType(Object o)
	{
		if (o instanceof IAdaptable)
		{
			IAdaptable adapt = (IAdaptable) o;
			IMethod element = (IMethod) adapt.getAdapter(IMethod.class);
			try
			{
				if(element != null && element.isMainMethod())
					return element.getDeclaringType();
			}
			catch (JavaModelException e)
			{
				LeJOSNXJUtil.log(e);
			}
		}
		return null;
	}
	
	@Override
	protected IType[] findTypes(Object[] elements, IRunnableContext context)
			throws InterruptedException, CoreException
	{
		if(elements.length == 1)
		{
			IType type = getMainMethodType(elements[0]);
			if(type != null)
				return new IType[] { type };
		}
		
		int constraints = IJavaSearchScope.SOURCES;
		IJavaElement[] javaElements = getJavaElements(elements);
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(javaElements, constraints);

		ArrayList<IType> result = new ArrayList<IType>();
		new MainMethodSearchHelper().searchMainMethods(context, scope, result);
		return result.toArray(new IType[result.size()]);
	}
	
	@Override
	protected IType chooseType(IType[] types, String title)
	{
		MainTypeSelectDialog mtsd = new MainTypeSelectDialog(LeJOSPlugin.getShell(), Arrays.asList(types), title);
		return mtsd.openAndGetResult();
	}
}
