package org.lejos.nxt.ldt.views.browser;

import java.util.ArrayList;
import java.util.Collection;

import lejos.pc.comm.FileInfo;
import lejos.pc.comm.NXTCommand;

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
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

public class NXTMemoryPanel {

	private TableViewer filesTable;
	private Group filesGroup;

	public NXTMemoryPanel(Composite parent) {
		init(parent);

		// TODO deactivate
		Collection<FileInfo> testFileInfos = new ArrayList<FileInfo>();
		testFileInfos.add(new FileInfo("test1.java"));
		testFileInfos.add(new FileInfo("test2.java"));
		update(testFileInfos);
	}

	public Control getControl() {
		return filesGroup;
	}

	public void update(Collection<FileInfo> fileInfos) {
		filesTable.setInput(fileInfos);
	}
	
	public void reset() {
		filesTable.setInput(null);
	}

	private void init(Composite parent) {
		// files group
		filesGroup = new Group(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		filesGroup.setLayoutData(gridData);
		filesGroup.setLayout(layout);
		// files table
		createFilesTable(filesGroup);
		// buttons
		createButtons(filesGroup);
	}

	private void createFilesTable(Group parent) {
		filesTable = new TableViewer(parent, SWT.FULL_SELECTION);
		filesTable.setContentProvider(new ArrayContentProvider());
		filesTable.setLabelProvider(new FilesTableLabelProvider());
		final Table table = filesTable.getTable();
		final TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		table.setLayoutData(gridData);
		final TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setText("name");
		nameColumn.setWidth(500);
		ColumnWeightData nameLayoutData = new ColumnWeightData(70, true);
		tableLayout.addColumnData(nameLayoutData);
		final TableColumn sizeColumn = new TableColumn(table, SWT.NONE);
		sizeColumn.setText("size");
		ColumnWeightData sizeLayoutData = new ColumnWeightData(30, true);
		tableLayout.addColumnData(sizeLayoutData);
	}

	private void createButtons(Composite parent) {
		Composite buttons = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.END;
		buttons.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		buttons.setLayout(layout);
		// delete
		// TODO enable/disable
		Button delete = new Button(buttons, SWT.NULL);
		delete.setText("Delete");
		delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = filesTable.getSelection();
				if ((selection != null)
						&& (selection instanceof IStructuredSelection)) {
					Object selected = ((IStructuredSelection) selection)
							.getFirstElement();
					// TODO honor multiple selection
					if ((selected != null) && (selected instanceof FileInfo)) {
						deleteFileOnBrick((FileInfo) selected);
					}
				}
			}
		});
	}

	private void deleteFileOnBrick(FileInfo file) {
		// TODO show progress and hour glass
		try {
			NXTCommand nxtCommand = NXTCommand.getSingleton();
			nxtCommand.delete(file.fileName);
		} catch (Throwable t) {
			LeJOSNXJUtil.message(t);
		}
		// update table
		filesTable.remove(file);
		// updateFilesTable(nxtBricks);
	}

	class FilesTableLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			// TODO use brick icon
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof FileInfo))
				return null;
			FileInfo info = (FileInfo) element;
			switch (columnIndex) {
			case 0:
				return info.fileName;
			case 1:
				return info.fileSize + " kb";
			default:
				return null;
			}
		}

	}
}
