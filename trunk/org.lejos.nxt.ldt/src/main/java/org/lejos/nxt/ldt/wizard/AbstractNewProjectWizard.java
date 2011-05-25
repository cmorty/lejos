package org.lejos.nxt.ldt.wizard;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public abstract class AbstractNewProjectWizard extends Wizard implements INewWizard, IExecutableExtension{

	/*
	 * code partly inspired or copied from
	 * org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard 
	 */
	
	protected NewJavaProjectWizardPageOne pageOne;
	protected NewJavaProjectWizardPageTwo pageTwo;
	protected IConfigurationElement fConfigElement;
	protected IStructuredSelection fSelection;
	protected IWorkbench fWorkbench;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		fWorkbench = workbench;
		fSelection = selection;
	}

	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		fConfigElement= cfig;
	}

	private IWorkbenchPart getActivePart() {
		IWorkbenchWindow activeWindow= fWorkbench.getActiveWorkbenchWindow();
		if (activeWindow != null) {
			IWorkbenchPage activePage= activeWindow.getActivePage();
			if (activePage != null) {
				return activePage.getActivePart();
			}
		}
		return null;
	}
	
	@Override
	public void addPages() {
		addPage(pageOne);
		addPage(pageTwo);
		
		pageOne.init(fSelection, getActivePart());
	}

	@Override
	public boolean performCancel() {
		pageTwo.performCancel();
		return super.performCancel();
	}

	@Override
	public boolean performFinish() {
		NullProgressMonitor pm = new NullProgressMonitor();
		try {
			pageTwo.performFinish(pm);
		} catch (Exception e) {
			//TODO not sure how to handle this. There don't seem to be any ways to report an error back to the caller.
			LeJOSNXJUtil.log(e);
		}
		
		IJavaProject newProject = pageTwo.getJavaProject();
		IWorkingSet[] workingSets= pageOne.getWorkingSets();
		if (workingSets != null && workingSets.length > 0) {
			fWorkbench.getWorkingSetManager().addToWorkingSets(newProject, workingSets);
		}
	
		BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
		BasicNewResourceWizard.selectAndReveal(newProject.getProject(), fWorkbench.getActiveWorkbenchWindow());
		
		return true;
	}

}
