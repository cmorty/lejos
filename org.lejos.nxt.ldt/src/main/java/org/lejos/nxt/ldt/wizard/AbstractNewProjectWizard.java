package org.lejos.nxt.ldt.wizard;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public abstract class AbstractNewProjectWizard extends Wizard implements INewWizard, IExecutableExtension{

	protected NewJavaProjectWizardPageOne pageOne;
	protected NewJavaProjectWizardPageTwo pageTwo;
	protected IConfigurationElement fConfigElement;
	protected IWorkbench fWorkbench;
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		fWorkbench = workbench;
	}

	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		fConfigElement= cfig;
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(pageOne);
		addPage(pageTwo);
	}

	@Override
	public boolean performCancel() {
		pageTwo.performCancel();
		return super.performCancel();
	}

	@Override
	public boolean performFinish() {
		final IJavaProject newProject = pageTwo.getJavaProject();
	
		IWorkingSet[] workingSets= pageOne.getWorkingSets();
		if (workingSets != null && workingSets.length > 0) {
			fWorkbench.getWorkingSetManager().addToWorkingSets(newProject, workingSets);
		}
	
		BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
		BasicNewResourceWizard.selectAndReveal(newProject.getProject(), fWorkbench.getActiveWorkbenchWindow());
		
		return true;
	}

}
