package org.lejos.nxt.ldt.views.browser;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTCommand;
import lejos.pc.comm.NXTInfo;
import lejos.pc.comm.NXTProtocol;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.lejos.nxt.ldt.comm.IConnectionListener;
import org.lejos.nxt.ldt.util.LeJOSNXJUtil;

/**
 * 
 * @author Matthias Paul Scholz
 * 
 * TODO correct setting of brick panel contents when connection fails due to the fact that the brick is already connected
 * TODO correct setting of brick panel contents when browser window is closed and reopened in one session
 *
 */
public class NXTBrowserView extends ViewPart {

	public static String VIEW_ID = "org.lejos.nxt.ldt.views.NXTBrowserView";
	private NXTBrickPanel brickPanel;

	public NXTBrowserView() {
		// TODO Auto-generated constructor stub
	}

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
		// TODO Auto-generated method stub

	}

	private void createTabbedPane(Composite parent) {
		// TODO create tabs
		// add bricks tab
		createBricksPanel(parent);

	}

	private void createBricksPanel(Composite parent) {
		NXTBricksPanel bricksPanel = new NXTBricksPanel(parent);
		bricksPanel.addConnectionListener(new NXTBrowserConnectionListener());

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
			// TODO Auto-generated method stub

		}

	}
}
