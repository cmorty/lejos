package lejos.pc.charting;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import org.jfree.chart.JFreeChart;


public class ChartDisplay extends JFrame {
    private CustomChartPanel customChartPanel1 = new CustomChartPanel();
    private BorderLayout borderLayout1 = new BorderLayout();

    public ChartDisplay() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    JFreeChart getChart(){
        return this.customChartPanel1.getLoggingChartPanel().getChart();
    }
    void setChart(JFreeChart chart){
        this.customChartPanel1.getLoggingChartPanel().setChart(chart);
        this.customChartPanel1.getLoggingChartPanel().getChart().setNotify(true);
    }
    
    private void jbInit() throws Exception {
        this.getContentPane().setLayout(borderLayout1);
        this.setSize(new Dimension(626, 319));
        this.setTitle( "Chart Title Goes here" );
//        customChartPanel1.setPreferredSize(new Dimension(600, 470));
        this.getContentPane().add(customChartPanel1, BorderLayout.CENTER);
    }
}
