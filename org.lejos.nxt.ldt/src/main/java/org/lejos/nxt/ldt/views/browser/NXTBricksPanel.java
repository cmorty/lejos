package org.lejos.nxt.ldt.views.browser;

import java.util.Collection;
import java.util.HashSet;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommand;
import lejos.pc.comm.NXTInfo;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.lejos.nxt.ldt.comm.IConnectionListener;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public class NXTBricksPanel {

	private TableViewer bricksTable;
	private Group bricksGroup;
	private Collection<IConnectionListener> collectionListeners;

	public NXTBricksPanel(Composite parent) {
		collectionListeners = new HashSet<IConnectionListener>();
		init(parent);

		// TODO deactivate
		NXTInfo testInfo = new NXTInfo("test", "xyz");
		NXTInfo[] testinfos = { testInfo };
		updateBricksTable(testinfos);
	}

	public void addConnectionListener(IConnectionListener listener) {
		collectionListeners.add(listener);
	}

	public void removeConnectionListener(IConnectionListener listener) {
		collectionListeners.remove(listener);
	}

	public Control getControl() {
		return bricksGroup;
	}

	private void init(Composite parent) {
		// bricks group
		bricksGroup = new Group(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		bricksGroup.setLayoutData(gridData);
		bricksGroup.setLayout(layout);
		// bricks table
		createBricksTable(bricksGroup);
		// buttons
		createButtons(bricksGroup);
	}

	private void createBricksTable(Group parent) {
		bricksTable = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		bricksTable.setContentProvider(new ArrayContentProvider());
		bricksTable.setLabelProvider(new BricksTableLabelProvider());
		final Table table = bricksTable.getTable();
		final TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);
		// table.setLinesVisible(true);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		table.setLayoutData(gridData);
		final TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setText("Name");
		nameColumn.setWidth(500);
		ColumnWeightData nameLayoutData = new ColumnWeightData(50, true);
		tableLayout.addColumnData(nameLayoutData);
		final TableColumn connectionTypeColumn = new TableColumn(table,
				SWT.NONE);
		connectionTypeColumn.setText("Connection type");
		ColumnWeightData connectionTypeLayoutData = new ColumnWeightData(25,
				true);
		tableLayout.addColumnData(connectionTypeLayoutData);
		final TableColumn statusColumn = new TableColumn(table, SWT.NONE);
		statusColumn.setText("Address");
		ColumnWeightData statusLayoutData = new ColumnWeightData(25, true);
		tableLayout.addColumnData(statusLayoutData);
	}

	private void createButtons(Composite parent) {
		Composite buttons = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.END;
		buttons.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		buttons.setLayout(layout);
		// connect
		// TODO enable/disable
		Button connect = new Button(buttons, SWT.NULL);
		connect.setText("Connect");
		connect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = bricksTable.getSelection();
				if ((selection != null)
						&& (selection instanceof IStructuredSelection)) {
					Object selected = ((IStructuredSelection) selection)
							.getFirstElement();
					if ((selected != null) && (selected instanceof NXTInfo)) {
						connectToBrick((NXTInfo) selected);
					}
				}
			}
		});
		// search
		Button search = new Button(buttons, SWT.NULL);
		search.setText("Search");
		search.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final NXTInfo[] nxtBricks = searchForNXTBricks();
				updateBricksTable(nxtBricks);
			}
		});
	}

	private NXTInfo[] searchForNXTBricks() {
		NXTInfo[] nxtBricks = null;
		// TODO show progress and hour glass
		try {
			NXTCommand nxtCommand = NXTCommand.getSingleton();
			nxtBricks = nxtCommand.search(null, NXTCommFactory.USB);
		} catch (Throwable t) {
			LeJOSNXJUtil.message(t);
		}
		return nxtBricks;
	}

	private void updateBricksTable(NXTInfo[] nxtBricks) {
		bricksTable.setInput(nxtBricks);
	}

	private void connectToBrick(NXTInfo info) {
		boolean brickConnected = false;
		try {
			System.out.println("Connecting to " + info.name);
			brickConnected = NXTCommand.getSingleton().open(info);
		} catch (Throwable t) {
			LeJOSNXJUtil.message(t);
		}
		if (!brickConnected) {
			LeJOSNXJUtil.message("Brick could not be connected");
		} else {
			// notify listeners
			for (IConnectionListener listener : collectionListeners) {
				listener.brickConnected(info);
			}
		}
	}

	class BricksTableLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			// TODO use brick icon
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof NXTInfo))
				return null;
			NXTInfo info = (NXTInfo) element;
			switch (columnIndex) {
			case 0:
				return info.name;
			case 1:
				if (info.protocol == NXTCommFactory.BLUETOOTH)
					return "Bluetooth";
				else
					return "USB";
			case 2:
				return info.btDeviceAddress;
			default:
				return null;
			}
		}

	}
}
