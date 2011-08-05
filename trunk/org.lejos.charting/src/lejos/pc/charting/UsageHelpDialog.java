package lejos.pc.charting;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;


class UsageHelpDialog extends JDialog {

    private JLabel jLabel1 = new JLabel();
    private GridLayout verticalFlowLayout1 = new GridLayout(0,1);
    
    public UsageHelpDialog(Frame parent) {
        this(parent, "Data Examination Controls", false);
    }

    public UsageHelpDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void jbInit() throws Exception {
        this.setSize(new Dimension(539, 312));
        this.setLayout(verticalFlowLayout1);
        this.setModal(false);
        this.setResizable(true);


        jLabel1.setText("<html>Chart Data Examination Controls" +
        "<ul>" +
        "<li>Zooming<ul>" +
        "<li> Historical extents (including clipped due to Domain Display Limiting): Double-left click on chart area" +
        "<li>Reset Historical extents to current: CTRL+Double-left click on chart area" +
        "<li>Current extents: Left click-drag to left, and/or up then release" + 
        "<li>Zoom window: Left click-drag to right and down then release" + 
        "<li>Zoom in/out dynamic: Use mouse wheel" + 
        "</ul>" +
        "<li>Panning dynamic: CTRL+Left click-drag" +
        "<li>Slider changes domain scale dynamically from 0.1 to 100% of (domain) X dataset extents/range" + 
        "<li>Tootip: Hover over data point on a series and series name, x-y val is shown" + 
        "<li>Left-click moves crosshair to nearest datapoint and those coordinates are displayed in lower right of chart area" +
        "</ul></html>");jLabel1.setVerticalAlignment(SwingConstants.TOP);
        this.getContentPane().add(jLabel1, null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        setLocation((screenSize.width - frameSize.width-10), 10);
                          
    }
}
