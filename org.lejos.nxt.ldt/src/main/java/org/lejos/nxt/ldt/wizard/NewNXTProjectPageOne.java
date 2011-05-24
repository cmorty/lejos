package org.lejos.nxt.ldt.wizard;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.lejos.nxt.ldt.container.LeJOSLibContainer;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public class NewNXTProjectPageOne extends NewJavaProjectWizardPageOne {
	@Override
	protected Control createJRESelectionControl(Composite composite) {
		return new Composite(composite, SWT.NONE);
	}
	
	@Override
	public IClasspathEntry[] getDefaultClasspathEntries() {
		Path lcp = new Path(LeJOSLibContainer.ID+"/"+LeJOSNXJUtil.LIBSUBDIR_NXT);
		IClasspathEntry lc = JavaCore.newContainerEntry(lcp);
		
		return new IClasspathEntry[] {lc};
	}
}
