package lejos.pc.charting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;


class SpawnChartFrame extends JFrame {
    private SpawnChartPanel chartPanel;
    private BorderLayout borderLayout1 = new BorderLayout();

    //public SpawnChartFrame(SpawnChartPanel chartPanel){
    public SpawnChartFrame(SpawnChartPanel chartPanel){
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setChartPanel(chartPanel);
        
        // do the bounds restore after the intial render so any comments markers Y get set correctly
        final SpawnChartPanel threadchartPanel = chartPanel;
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                threadchartPanel.restoreAutoBounds();
            }
        });
        
    }
    
    private void setChartPanel(SpawnChartPanel chartPanel){
        this.chartPanel=chartPanel;
        this.getContentPane().add(this.chartPanel, BorderLayout.CENTER);
        this.pack();
        this.chartPanel.setVisible(true);
        setMouseListener();
    }
    
    private void jbInit() throws Exception {
        this.getContentPane().setLayout(borderLayout1);
        this.setSize(new Dimension(634, 412));
        
        Dimension frameSize = this.getSize();
        // Toolkit.getDefaultToolkit().getScreenSize() doesn't work for multi monitor setups (SK)
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        this.setLocation(center.x - frameSize.width / 2, 
                          center.y - frameSize.height / 2);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        this.setVisible(true);
    }
    
    ChartPanel getChartPanel() {
        return this.chartPanel; 
    }
    
    // set the double-click to restore zoom to extents
    private void setMouseListener() {
        class ml extends MouseAdapter{
            public void mouseClicked(MouseEvent e) {
                if ((e.getButton()&MouseEvent.BUTTON1)==MouseEvent.BUTTON1) {
                    // doubleclick zooms extents of data
                    if (e.getClickCount()>=2) {
                        chartPanel.restoreAutoBounds();
                        
                    }
                }
            }
            public void mouseReleased(MouseEvent e) {
                // workaround bug that range crosshair will show up on rangeAxisIndex > 0 plots
                chartPanel.getChart().getXYPlot().setRangeCrosshairVisible(false);
            }
        }
        chartPanel.addMouseListener(new ml());
    }
    
   
}
