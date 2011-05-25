package org.lejos.nxt.ldt.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.lejos.nxt.ldt.actions.ConvertToLeJOSProjectAction;

public class NewNXTProjectPageTwo extends NewJavaProjectWizardPageTwo {

	public NewNXTProjectPageTwo(NewJavaProjectWizardPageOne mainPage) {
		super(mainPage);
	}

	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException, InterruptedException {
		super.performFinish(monitor);
		
		IJavaProject p = this.getJavaProject();
		ConvertToLeJOSProjectAction.addLeJOSNature(p.getProject());
	}
}
