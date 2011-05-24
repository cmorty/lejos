package org.lejos.nxt.ldt.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.lejos.nxt.ldt.actions.ConvertToLeJOSProjectAction;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public class NewNXTProjectPageTwo extends NewJavaProjectWizardPageTwo {

	public NewNXTProjectPageTwo(NewJavaProjectWizardPageOne mainPage) {
		super(mainPage);
	}

	@Override
	protected IProject createProvisonalProject() {
		IProject p = super.createProvisonalProject();
		try {
			ConvertToLeJOSProjectAction.addLeJOSNature(p);
		} catch (CoreException e) {
			//TODO not sure how to handle this. There don't seem to be any ways to report an error back to the caller.
			LeJOSNXJUtil.log(e);
		}
		return p;
	}
}
