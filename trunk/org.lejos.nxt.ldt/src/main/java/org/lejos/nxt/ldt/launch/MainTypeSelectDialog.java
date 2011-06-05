package org.lejos.nxt.ldt.launch;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.lejos.nxt.ldt.LeJOSPlugin;

/**
 * This is a specialization of <code>FilteredItemsSelectionDialog</code> used to
 * present users with a listing of <code>IType</code>s that contain main methods
 * 
 * @since 3.3
 * 
 */
public class MainTypeSelectDialog extends FilteredItemsSelectionDialog
{
	static protected IJavaElement getDeclaringElement(IType type)
	{
		IJavaElement e = type.getDeclaringType();
		if (e == null)
			e = type.getPackageFragment();
		
		return e;
	}
	
	static protected String getDeclaringElementName(IType type)
	{
		IType e = type.getDeclaringType();
		if (e != null)
			return e.getFullyQualifiedName();
		
		IPackageFragment p = type.getPackageFragment();
		if (p.isDefaultPackage())
			return "(default package)";
		
		return p.getElementName();
	}

	/**
	 * This class imitates 
	 * org.eclipse.jdt.internal.debug.ui.launcher.DebugTypeSelectionDialog.DebugTypeLabelProvider
	 */
	static class ListLabelProvider extends JavaElementLabelProvider
	{
		public ListLabelProvider()
		{
			super(JavaElementLabelProvider.SHOW_OVERLAY_ICONS
					| JavaElementLabelProvider.SHOW_SMALL_ICONS);
		}
		
		@Override
		public String getText(Object element)
		{
			if (element instanceof IType)
			{
				IType type = (IType) element;
				String label = type.getTypeQualifiedName();
				String container = getDeclaringElementName(type);
				return label + " - "+ container;
			}
			return super.getText(element);
		}
		
		@Override
		public StyledString getStyledText(Object element)
		{
			return new StyledString(this.getText(element));
		}
	}

	/**
	 * This class imitates 
	 * org.eclipse.jdt.internal.debug.ui.launcher.DebugTypeSelectionDialog.DebugTypeDetailsLabelProvider
	 */
	static class DetailsLabelProvider extends JavaElementLabelProvider
	{
		public DetailsLabelProvider()
		{
			super(JavaElementLabelProvider.SHOW_OVERLAY_ICONS
					| JavaElementLabelProvider.SHOW_SMALL_ICONS
					| JavaElementLabelProvider.SHOW_QUALIFIED);
		}
		
		@Override
		public String getText(Object element)
		{
			if (element instanceof IType)
				element = getDeclaringElement((IType) element);
			
			return super.getText(element);
		}

		@Override
		public StyledString getStyledText(Object element)
		{
			return new StyledString(this.getText(element));
		}
		
		@Override
		public Image getImage(Object element)
		{
			if (element instanceof IType)
				element = getDeclaringElement((IType) element);
				
			return super.getImage(element);
		}
	}

	/**
	 * This class imitates 
	 * org.eclipse.jdt.internal.debug.ui.launcher.DebugTypeSelectionDialog.DebugTypeItemsFilter
	 */
	class SimpleItemsFilter extends ItemsFilter
	{
		@Override
		public boolean isConsistentItem(Object item)
		{
			return item instanceof IType;
		}

		@Override
		public boolean matchItem(Object item)
		{
			return elements.contains(item) && matches(((IType) item).getTypeQualifiedName());
		}
	}

	/**
	 * This class imitates 
	 * org.eclipse.jdt.internal.debug.ui.launcher.DebugTypeSelectionDialog.DebugTypeSelectionHistory
	 */
	static class SimpleSelectionHistory extends SelectionHistory
	{
		@Override
		protected Object restoreItemFromMemento(IMemento memento)
		{
			IJavaElement element = JavaCore.create(memento.getTextData());
			return (element instanceof IType ? element : null);
		}

		@Override
		protected void storeItemToMemento(Object item, IMemento memento)
		{
			if (item instanceof IType)
			{
				memento.putTextData(((IType) item).getHandleIdentifier());
			}
		}
	}

	private static final String SETTINGS_ID = LeJOSPlugin.ID + ".MAIN_METHOD_SELECTION_DIALOG"; //$NON-NLS-1$
	
	private HashSet<IType> elements = new HashSet<IType>();


	public MainTypeSelectDialog(Shell shell, Collection<IType> result, String title)
	{
		super(shell, false);
		this.elements.addAll(result);
		setTitle(title);
		setMessage("Select &type (? = any character, * = any String, TZ = TimeZone)");
		setInitialPattern("**"); //$NON-NLS-1$
		setListLabelProvider(new ListLabelProvider());
		setDetailsLabelProvider(new DetailsLabelProvider());
		setSelectionHistory(new SimpleSelectionHistory());
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Control ctrl = super.createDialogArea(parent);
		return ctrl;
	}

	@Override
	protected IDialogSettings getDialogSettings()
	{
		IDialogSettings settings = LeJOSPlugin.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(SETTINGS_ID);
		if (section == null)
			section = settings.addNewSection(SETTINGS_ID);
		
		return section;
	}

	@Override
	protected Comparator<IType> getItemsComparator()
	{
		Comparator<IType> comp = new Comparator<IType>()
			{
				public int compare(IType o1, IType o2)
				{
					return o1.getElementName().compareTo(o2.getElementName());
				}
			};
		return comp;
	}

	@Override
	protected IStatus validateItem(Object item)
	{
		return Status.OK_STATUS;
	}

	@Override
	protected Control createExtendedContentArea(Composite parent)
	{
		return null;
	}

	@Override
	protected ItemsFilter createFilter()
	{
		return new SimpleItemsFilter();
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException
	{
		for (IType t : elements)
			if (itemsFilter.isConsistentItem(t))
				contentProvider.add(t, itemsFilter);
	}

	@Override
	public String getElementName(Object item)
	{
		if (item instanceof IType)
			return ((IType) item).getElementName();
		
		return null;
	}
}
