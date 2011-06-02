package org.lejos.nxt.ldt.launch;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public class MainMethodSearchHelper
{
	private static class ResultAggregator extends SearchRequestor
	{
		private Collection<IType> result;

		public ResultAggregator(Collection<IType> dst)
		{
			this.result = dst;
		}

		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException
		{
			Object element = match.getElement();
			if (element instanceof IMethod)
			{
				IMethod method = (IMethod) element;
				try
				{
					if (method.isMainMethod())
					{
						IType type = method.getDeclaringType();
						result.add(type);
					}
				}
				catch (JavaModelException e)
				{
					LeJOSNXJUtil.log(e);
				}
			}
		}
	}

	public void searchMainMethods(IProgressMonitor pm, IJavaSearchScope scope, Collection<IType> dst)
	{
		pm.beginTask("Searching for main methods...", 100);
		try
		{
			SearchPattern pattern = SearchPattern.createPattern("main(String[]) void",
					IJavaSearchConstants.METHOD, IJavaSearchConstants.DECLARATIONS,
					SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE); //$NON-NLS-1$
			SearchParticipant[] participants = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
			IProgressMonitor searchMonitor = new SubProgressMonitor(pm, 100);
			ResultAggregator collector = new ResultAggregator(dst);
			try
			{
				new SearchEngine().search(pattern, participants, scope,	collector, searchMonitor);
			}
			catch (CoreException ce)
			{
				LeJOSNXJUtil.log(ce);
			}
		}
		finally
		{
			pm.done();
		}
	}

	public void searchMainMethods(IRunnableContext context, final IJavaSearchScope scope, final Collection<IType> dst)
		throws InvocationTargetException, InterruptedException
	{
		context.run(true, true, new IRunnableWithProgress()
			{
				public void run(IProgressMonitor pm)
						throws InvocationTargetException
				{
					searchMainMethods(pm, scope, dst);
				}
			});
	}

}
