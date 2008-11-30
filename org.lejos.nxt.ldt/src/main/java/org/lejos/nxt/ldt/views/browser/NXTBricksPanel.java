package org.lejos.nxt.ldt.views.browser;

import java.util.Collection;
import java.util.HashSet;

import lejos.pc.comm.NXTCommFactory;

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
import org.lejos.nxt.ldt.LeJOSNXJPlugin;
import org.lejos.nxt.ldt.comm.IConnectionListener;
import org.lejos.nxt.ldt.comm.ISearchListener;
import org.lejos.nxt.ldt.comm.NXTBrowserInfo;
import org.lejos.nxt.ldt.comm.NXTConnectionState;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public class NXTBricksPanel {

	private TableViewer bricksTable;
	private Group bricksGroup;
	private Collection<IConnectionListener> connectionListeners;
	private Collection<ISearchListener> searchListeners;

	public NXTBricksPanel(Composite parent) {
		connectionListeners = new HashSet<IConnectionListener>();
		searchListeners = new HashSet<ISearchListener>();
		init(parent);

		// TODO deactivate
		// NXTBrowserInfo testInfo = new NXTBrowserInfo(new NXTInfo("test",
		// "xyz"));
		// NXTBrowserInfo[] testinfos = { testInfo };
		// updateBricksTable(testinfos);
	}

	public void addConnectionListener(IConnectionListener listener) {
		connectionListeners.add(listener);
	}

	public void removeConnectionListener(IConnectionListener listener) {
		connectionListeners.remove(listener);
	}

	public void addSearchlistener(ISearchListener listener) {
		searchListeners.add(listener);
	}

	public void removeSearchlistener(ISearchListener listener) {
		searchListeners.remove(listener);
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
		statusColumn.setText("Status");
		ColumnWeightData statusLayoutData = new ColumnWeightData(25, true);
		tableLayout.addColumnData(statusLayoutData);
	}

	private void createButtons(Composite parent) {
		Composite buttons = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.END;
		buttons.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
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
					if ((selected != null)
							&& (selected instanceof NXTBrowserInfo)) {
						NXTBrowserInfo browserInfo = (NXTBrowserInfo) selected;
						if (!LeJOSNXJPlugin.getDefault().getConnectionManager()
								.connectToBrick(browserInfo)) {
							LeJOSNXJUtil.message("Brick "
									+ browserInfo.getNXTInfo().name
									+ " could not be connected");
						} else {
							// update table
							updateTable(browserInfo,
									NXTConnectionState.CONNECTED);
							// notify listeners
							for (IConnectionListener listener : connectionListeners) {
								listener.brickConnected(browserInfo);
							}
						}

					}
				}
			}
		});
		Button detach = new Button(buttons, SWT.NULL);
		detach.setText("Detach");
		detach.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = bricksTable.getSelection();
				if ((selection != null)
						&& (selection instanceof IStructuredSelection)) {
					Object selected = ((IStructuredSelection) selection)
							.getFirstElement();
					if ((selected != null)
							&& (selected instanceof NXTBrowserInfo)) {
						LeJOSNXJPlugin.getDefault().getConnectionManager()
								.detachFromBricks();
						NXTBrowserInfo browserInfo = (NXTBrowserInfo) selected;
						// update table
						updateTable(browserInfo,
								NXTConnectionState.DISCONNECTED);
						// notify listeners
						for (IConnectionListener listener : connectionListeners) {
							listener.brickDetached(browserInfo);
						}
						// do a re-search
						final NXTBrowserInfo[] nxtBricks = LeJOSNXJPlugin
								.getDefault().getConnectionManager()
								.searchForNXTBricks();
						updateBricksTable(nxtBricks);

					}
				}
			}
		});
		// search
		Button search = new Button(buttons, SWT.NULL);
		search.setText("Search");
		search.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (ISearchListener listener : searchListeners) {
					listener.searchStarted();
				}
				// TODO show progress and hour glass
				final NXTBrowserInfo[] nxtBricks = LeJOSNXJPlugin.getDefault()
						.getConnectionManager().searchForNXTBricks();
				for (ISearchListener listener : searchListeners) {
					listener.searchFinished();
				}
				updateBricksTable(nxtBricks);
			}
		});
	}

	private void updateBricksTable(NXTBrowserInfo[] nxtBricks) {
		bricksTable.setInput(nxtBricks);
	}

	private void updateTable(NXTBrowserInfo info, NXTConnectionState state) {
		NXTBrowserInfo[] infos = (NXTBrowserInfo[]) bricksTable.getInput();
		for (NXTBrowserInfo browserInfo : infos) {
			if (browserInfo.equals(browserInfo)) {
				browserInfo.setConnectionState(state);
				break;
			}
		}
		bricksTable.setInput(infos);
	}

	class BricksTableLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			// TODO use brick icon
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof NXTBrowserInfo))
				return null;
			NXTBrowserInfo info = (NXTBrowserInfo) element;
			switch (columnIndex) {
			case 0:
				return info.getNXTInfo().name;
			case 1:
				if (info.getNXTInfo().protocol == NXTCommFactory.BLUETOOTH)
					return "Bluetooth";
				else
					return "USB";
			case 2:
				return info.getConnectionState().name();
			default:
				return null;
			}
		}

	}
}
