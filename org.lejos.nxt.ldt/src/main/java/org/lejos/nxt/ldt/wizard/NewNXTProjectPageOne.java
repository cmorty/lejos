package org.lejos.nxt.ldt.wizard;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.lejos.nxt.ldt.container.LeJOSLibContainer;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public class NewNXTProjectPageOne extends NewJavaProjectWizardPageOne {
	@Override
	protected Control createJRESelectionControl(Composite composite) {
		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		gl.verticalSpacing= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		gl.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		gl.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		
		Group g= new Group(composite, SWT.NONE);
		g.setFont(composite.getFont());
		g.setLayout(gl);
		g.setText("JRE");
		
		Label l = new Label(g, SWT.NONE);
		l.setFont(g.getFont());
		l.setText("Project will use LeJOS NXT Runtime");
		
		return g;
	}
	
	@Override
	public IClasspathEntry[] getDefaultClasspathEntries() {
		Path lcp = new Path(LeJOSLibContainer.ID+"/"+LeJOSNXJUtil.LIBSUBDIR_NXT);
		IClasspathEntry lc = JavaCore.newContainerEntry(lcp);
		
		return new IClasspathEntry[] {lc};
	}
}
