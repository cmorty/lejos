package org.lejos.nxt.ldt.views.browser;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NXTBrickPanel {

	private Text name;
	private Text batteryLevel;
	private Text connectionType;

	public NXTBrickPanel(Composite parent) {
		init(parent);
	}

	private void init(Composite parent) {
		// main group
		Group mainGroup = new Group(parent, SWT.NULL);
		GridLayout mainLayout = new GridLayout();
		mainLayout.numColumns = 1;
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.BEGINNING;
		mainGroup.setLayoutData(gridData);
		mainGroup.setLayout(mainLayout);
		// header
		Label title = new Label(mainGroup, SWT.BOLD);
		title.setText("Connected NXT:");
		// brick group
		Group brickGroup = new Group(mainGroup, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		GridData brickGroupGridData = new GridData();
		brickGroupGridData.horizontalAlignment = GridData.FILL;
		brickGroupGridData.grabExcessHorizontalSpace = true;
		brickGroup.setLayoutData(brickGroupGridData);
		brickGroup.setLayout(layout);
		// name
		Label nameLabel = new Label(brickGroup, SWT.NONE);
		nameLabel.setText("Name:");
		name = new Text(brickGroup, SWT.BORDER);
		// battery level
		// TODO resize text fields to fill cell
		Label batteryLevelLabel = new Label(brickGroup, SWT.NONE);
		batteryLevelLabel.setText("Battery:");
		batteryLevel = new Text(brickGroup, SWT.NONE);
		batteryLevel.setEditable(false);
		// connection type
		Label connectionTypeLabel = new Label(brickGroup, SWT.NONE);
		connectionTypeLabel.setText("Connection:");
		connectionType = new Text(brickGroup, SWT.NONE);
		connectionType.setEditable(false);
	}

	public void reset() {
		name.setText("");
		batteryLevel.setText("");
		connectionType.setText("");
	}
	
	public void setInput(String name, int batteryLevel, String connectionType) {
		this.name.setText(name);
		this.batteryLevel.setText(DecimalFormat.getInstance().format(batteryLevel/1000.0));
		this.connectionType.setText(connectionType);
	}

}
