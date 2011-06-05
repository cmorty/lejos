package org.lejos.nxt.ldt.actions;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.lejos.nxt.ldt.LeJOSPlugin;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * links and uploads a leJOS NXJ program to the brick
 * 
 * @see IWorkbenchWindowActionDelegate
 * @author Matthias Paul Scholz
 * 
 */
public class LinkAndUploadAction extends ActionDelegate {

	private ISelection _selection;

	@Override
	public void run(IAction action) {
		MessageDialog.openInformation(LeJOSPlugin.getShell(), "The plugin has changed", "Please use the 'Run As' menu.");	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		_selection = selection;
		
		boolean isEnabled = false;
		// check if selected element is a java file or a leJOS NXJ project
		IJavaElement selectedElement = LeJOSNXJUtil.getFirstJavaElementFromSelection(_selection);
		if (selectedElement != null)
		{
			IType selectedType = LeJOSNXJUtil.getJavaTypeFromElement(selectedElement);
			if (selectedType != null)
			{
				isEnabled = true;
			}
		}
		// set state
		action.setEnabled(isEnabled);
	}

}