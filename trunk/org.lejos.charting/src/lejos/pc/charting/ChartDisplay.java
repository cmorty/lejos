package lejos.pc.charting;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;


public class ChartDisplay extends JFrame {
    private ChartPanel chartPanel;
    private BorderLayout borderLayout1 = new BorderLayout();

    public ChartDisplay(JFreeChart chart) {
        try {
            this.chartPanel = new ChartPanel(chart);
            chartPanel.getChart().setNotify(false);
            chartPanel.setMouseWheelEnabled(true);
            chartPanel.getChart().getXYPlot().setDomainPannable(true);
            chartPanel.getChart().getXYPlot().setDomainCrosshairVisible(true);
            chartPanel.getChart().getXYPlot().setRangePannable(true);
            chartPanel.getChart().getXYPlot().setRangeCrosshairVisible(true);
            chartPanel.setMouseZoomable(true);
            
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void jbInit() throws Exception {
        this.getContentPane().setLayout(borderLayout1);
        this.setSize(new Dimension(626, 319));
        this.getContentPane().add(chartPanel, BorderLayout.CENTER);
    }
    
    ChartPanel getChartPanel() {
        return this.chartPanel; 
    }
}
