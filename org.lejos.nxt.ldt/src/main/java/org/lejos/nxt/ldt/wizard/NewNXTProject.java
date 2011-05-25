package org.lejos.nxt.ldt.wizard;


public class NewNXTProject extends AbstractNewProjectWizard {
	/*
	 * code partly inspired or copied from
	 * org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard 
	 */
	
	public NewNXTProject() {
		super();
		pageOne = new NewNXTProjectPageOne();
		pageTwo = new NewNXTProjectPageTwo(pageOne);
		
		pageOne.setTitle("New LeJOS NXT Project");
		pageOne.setDescription("Create a new LeJOS Project for programs running on the NXT");
		
	    setWindowTitle("New LeJOS NXT Project");
	}
}
