package org.lejos.nxt.ldt.views.browser;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommand;
import lejos.pc.comm.NXTInfo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;
import org.lejos.nxt.ldt.comm.IConnectionListener;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * 
 * @author Matthias Paul Scholz
 * 
 *         TODO correct setting of brick panel contents when connection fails
 *         due to the fact that the brick is already connected 
 *         TODO correct setting of brick panel contents when browser window is closed and
 *         reopened in one session
 * 
 */
public class NXTBrowserView extends ViewPart {

	public static String VIEW_ID = "org.lejos.nxt.ldt.views.NXTBrowserView";
	private NXTBrickPanel brickPanel;

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		main.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		main.setLayout(layout);
		// tabbed pane
		createTabbedPane(main);
		// NXT brick panel
		brickPanel = new NXTBrickPanel(main);

	}

	@Override
	public void setFocus() {
		// do nothing
	}

	private void createTabbedPane(Composite parent) {
		Group tabGroup = new Group(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		tabGroup.setLayoutData(gridData);
		tabGroup.setLayout(layout);
		TabFolder tabs = new TabFolder(tabGroup, SWT.TOP);
		tabs.setLayoutData(gridData);
		// create tabs
		// add communication tab
		NXTBricksPanel bricksPanel = new NXTBricksPanel(tabs);
		bricksPanel.addConnectionListener(new NXTBrowserConnectionListener());
		TabItem bricksPanelItem = new TabItem(tabs, SWT.NONE);
		bricksPanelItem.setControl(bricksPanel.getControl());
		bricksPanelItem.setText("Communication");
		// add memory tab
		NXTMemoryPanel memoryPanel = new NXTMemoryPanel(tabs);
		TabItem memoryPanelItem = new TabItem(tabs, SWT.NONE);
		memoryPanelItem.setControl(memoryPanel.getControl());
		memoryPanelItem.setText("Memory");
		tabs.pack();

	}

	class NXTBrowserConnectionListener implements IConnectionListener {

		public void brickConnected(NXTInfo info) {
			brickPanel.reset();
			if (info != null) {
				try {
					int batteryLevel = NXTCommand.getSingleton()
							.getBatteryLevel();
					String name = NXTCommand.getSingleton().getFriendlyName();
					String connectionType = "?";
					if (info.protocol == NXTCommFactory.USB)
						connectionType = "USB";
					else if (info.protocol == NXTCommFactory.BLUETOOTH)
						connectionType = "BT";
					brickPanel.setInput(name, batteryLevel, connectionType);
				} catch (Throwable t) {
					LeJOSNXJUtil.message(t);
				}

			}

		}

		public void brickDetached(NXTInfo info) {
			// do nothing
		}

	}
}
