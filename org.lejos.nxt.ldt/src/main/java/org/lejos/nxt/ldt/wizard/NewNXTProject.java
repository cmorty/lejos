package org.lejos.nxt.ldt.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewNXTProject extends Wizard implements INewWizard {
	
	private NewNXTProjectPageOne pageOne = new NewNXTProjectPageOne();
	private NewNXTProjectPageTwo pageTwo = new NewNXTProjectPageTwo(pageOne);
	

	public NewNXTProject() {
	    setWindowTitle("New LeJOS NXT Project");
	    
		pageOne.setTitle("New LeJOS NXT Project");
		pageOne.setDescription("Create a new LeJOS Project for programs running on the NXT");
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		//nothing to do
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(pageOne);
		addPage(pageTwo);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public boolean performCancel() {
		pageTwo.performCancel();
		return super.performCancel();
	}
}
