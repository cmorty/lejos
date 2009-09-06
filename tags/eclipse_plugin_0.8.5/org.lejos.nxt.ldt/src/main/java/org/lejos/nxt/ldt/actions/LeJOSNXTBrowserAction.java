package org.lejos.nxt.ldt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;
import org.lejos.nxt.ldt.views.browser.NXTBrowserView;

/**
 * 
 * UI action for displaying the NXT browser view
 * 
 * @author Matthias Paul Scholz
 * 
 */
public class LeJOSNXTBrowserAction implements IWorkbenchWindowActionDelegate {

	// private NXTBrowserView browserView;

	public void dispose() {
		// do nothing
	}

	public void init(IWorkbenchWindow window) {
		// do nothing
	}

	public void run(IAction action) {
		showNXTBrowserView();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing
	}

	private void showNXTBrowserView() {
		try {
			final IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			activePage.showView(NXTBrowserView.VIEW_ID);
			// result = (BacklinkView)
			// activePage.findView(BacklinkView.VIEW_ID);
		} catch (PartInitException e) {
			LeJOSNXJUtil.message(e);
		}
	}

}
