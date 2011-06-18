package lejos.pc.charting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;

import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import javax.swing.border.BevelBorder;

import lejos.util.Delay;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;


/** JFreeChart object in a ChartPanel. Includes methods to add titles (headers), series datapoints, and change domain scaling
 */
public class LoggingChart extends ChartPanel{
    private final String THISCLASS;
    private final float DOMAIN_SIZE_MULT=1.01f; // provides a small gap between series and chart right,left edges on domain axis
    
    private JFreeChart theChart; // TODO Needed as inst var?
    private Range domainRange;
    private XYSeriesCollection dataset;
    private NumberAxis domainAxis;
    private NumberAxis rangeAxis;
    private int refreshFrequency_ms=100; // used by update thread 
    private boolean chartDirty = false; // used by update thread to flag an event notification for repaints
    private double domainWidth=10000; // milli seconds defining how big to initially  make the domain scale (for sliding it)
    
    public LoggingChart() {
        super(null);
        
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
 
        try {
            jbInit();
        } catch (Exception e) {
            dbg("!** LogChartPanel Exception: " + e.toString());
            e.printStackTrace();
        }
        
        // chart updater thread
        final Thread t1 = new Thread(new Runnable(){
            public void run() {
                while (true) {
                    doWait(refreshFrequency_ms);
                    if (chartDirty && theChart!=null) {
                        theChart.setNotify(true);
                        //_JFChartPrimary.fireChartChanged();
                        theChart.setNotify(false);
                        chartDirty=false;
                    }
                }
            }
        });
        t1.setDaemon(true);
        // create thread with invokeLater() to ensure swing thread safety for update thread
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                t1.start();
            }
        });
        
        // set the double-click to restore zoom to extents
        setMouseListener();
        setAxisChangeListener();
    }
   
    // z..z..z..z..z..z...z...
    private void doWait(long milliseconds) {
         try {
             Thread.sleep(milliseconds);
         } catch (InterruptedException e) {
             //Thread.currentThread().interrupt();
         }
    }
    
    // required by JDeveloper to use/render in graphical GUI editor
    private void jbInit() throws Exception {
        setLayout(null);
        setChart(getBaselineChart());
        setVisible(true);
        getChart().setNotify(true);
    }

    private void dbg(String msg) {
        System.out.println(THISCLASS + "-" + msg);
    }

    
    protected void setTitle(String title){
        //TextTitle title = new TextTitle("", new Font("Arial", Font.PLAIN, 12));
        getChart().setTitle(title);
        this.chartDirty=true;
    }
    
//    private JFreeChart getBlankChart(Plot plot){
//        return new JFreeChart(null, new Font("Arial", Font.PLAIN, 12), plot, true);
//    }

    private JFreeChart getBaselineChart(){
        // create dataset
        this.dataset = new XYSeriesCollection();
        
        // set the domain axis
        this.domainAxis = new NumberAxis("Time (ms)");
        this.domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 9));
        this.domainAxis.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        this.domainAxis.setLowerMargin(0.0);
        this.domainAxis.setUpperMargin(0.0);
        this.domainAxis.setTickLabelsVisible(true);
        this.domainAxis.setAutoRange(true); 
        this.domainRange = new Range(0, domainWidth);
        this.domainAxis.setRange(this.domainRange);
        this.domainAxis.setAutoRangeIncludesZero(false);
        
        // set the value axis
        this.rangeAxis = new NumberAxis("Range");
        this.rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 9));
        this.rangeAxis.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        this.rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        this.rangeAxis.setAutoRange(true);
        this.rangeAxis.setAutoRangeIncludesZero(false);
        
        // set the renderer
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setBaseStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_BEVEL));
//        renderer.setSeriesStroke(0,new BasicStroke(2.0f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_BEVEL, 1f, new float[] { 10.0f, 4.0f, 1.0f, 4.0f }, 5f));
        renderer.setSeriesStroke(0,new BasicStroke(1.0f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_BEVEL));
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        
        // create the plot object to pass to the chart
        XYPlot plot = new XYPlot(this.dataset, this.domainAxis, this.rangeAxis, renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainPannable(true);
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setRangePannable(true);
            
        // create and return the chart        
        JFreeChart chart = new JFreeChart("", new Font("Arial", Font.BOLD, 14), plot, true);
        
        chart.setBackgroundPaint(Color.white);
        chart.getLegend().setPosition(RectangleEdge.RIGHT); 
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 10));
        //this.setHorizontalAxisTrace(true);
        this.setMouseWheelEnabled(true);
        
        return chart;
    }


    /** Set the passed series/header names as new XYseries in the dataset. Existing series are wiped. Must be at least two items
     * in the array or any existing series is left intact and method exits with 0. First item is set as domain label and should
     * be: system time in millseconds.
     * 
     * @param seriesNames Array of series names
     * @return The number of series created
     */
    protected int setSeries(String[] seriesNames){
        // TODO clone existing chart and put it in a new frame and display before we whack the dataset
        if (seriesNames.length<2) return 0;
        
        this.dataset = new XYSeriesCollection();
        getChart().getXYPlot().setDataset(this.dataset);
        getChart().getXYPlot().getDomainAxis().setLabel(seriesNames[0]);
        for (int i=1;i<seriesNames.length;i++) {
            this.dataset.addSeries(new XYSeries(seriesNames[i],true, true));
        }
        chartDirty=true;
        return getChart().getXYPlot().getSeriesCount();
    }
    

    private void setMouseListener() {
        final JFreeChart parent = getChart();
        
        class ml extends MouseAdapter{
            public void mouseClicked(MouseEvent e) {
                if ((e.getButton()&MouseEvent.BUTTON1)==MouseEvent.BUTTON1) {
//                    dbg("mouseclicked");
                    // doubleclick zooms extents of data
                    if (e.getClickCount()==2) {
                        parent.setNotify(true);
                        restoreAutoBounds();
                    }
                }
            }
            
            public void mouseReleased(MouseEvent e){
                if ((e.getButton()&MouseEvent.BUTTON1)==MouseEvent.BUTTON1){
                    //dbg(e.toString());
                    // ensure the chart renders any changes on left-click release
                    if (e.getClickCount()==1) parent.setNotify(true);
                }
            }
            
        }
        this.addMouseListener(new ml());
    }
    
    /**Add series data to the dataset. Pass a string representing a line of tab-delimited numeric values. The number of
     * values must match the header count in setSeries().
     * 
     * @param logLine the logger line from the NXT. This is a sting of tab-delimited numeric values.
     */
    protected void addDataPoints(String logLine) {
        
        String[] values = logLine.split("\t");
        if (values.length<2) {
            dbg("!** addDataPoints: No series values defined with setHeaders(). logLine=" + logLine);
            return;
        }
        
        // get number of series (primary dataset)
        int seriesCount = getChart().getXYPlot().getSeriesCount();
        
        // create generic series if not exist
        if (seriesCount==0) {
            for(String item: values){
                try {
                    Double.parseDouble(item);
                    seriesCount++;
                } catch (NumberFormatException e){
                    ; // don't add to series count
                }
            }
            String[] seriesNames = new String[seriesCount];
            for (int i=0;i<seriesCount;i++) seriesNames[i]="Series" + i;
            setSeries(seriesNames);
        }
        
        //get the timestamp
        long timeStamp;
        try {
            timeStamp = Long.parseLong(values[0]); // first column should always be timestamp from NXT
//            dbg("timeStamp=" + timeStamp);
        } catch (NumberFormatException e){
            dbg("!** addDataPoints: invalid timestamp");
//            e.printStackTrace();
            return;
        }
        
        Double seriesTempvalue;
        int index=0;
        XYSeriesCollection tempDs=(XYSeriesCollection)getChart().getXYPlot().getDataset();
        for (int i=1;i<values.length;i++) {            
//            dbg("values[" + i +  "]: " + values[i]);
            if (values[i].trim().length()==0) continue; // we can get an empty value if NXT doesn't log all vars before it closes a connection
            // this will weed out non-numeric data
            try {
                seriesTempvalue = new Double(values[i]);
//                dbg("seriesTempvalue=" + seriesTempvalue + ":" + tempDs.getSeries(index).getDescription());
                tempDs.getSeries(index).add(timeStamp,seriesTempvalue);
                index++;
            } catch (NumberFormatException e){
                System.err.format("%1$s.addDataPoints: iterator [%2$d] invalid value: %3$s", this.getClass().getName(), i, values[i]);
//                e.printStackTrace();
            }
        }
        
        // slide the the domain range to show animation of data coming in
        double maxXVal = ((XYSeriesCollection)getChart().getXYPlot().getDataset()).getSeries(0).getMaxX();
        this.domainRange = new Range(maxXVal-this.domainWidth, maxXVal);
        getChart().getXYPlot().getDomainAxis().setRange(this.domainRange);
        
        // ensure value (range) axis displays extents of data as it scrolls
        Range yRange=((XYSeriesCollection)getChart().getXYPlot().getDataset()).getRangeBounds(true);
        getChart().getXYPlot().getRangeAxis().setRange(yRange);
        
        // the updater thread picks this up every 100 ms and has the JFreeChart do it's notifcations for rendering, etc.
        chartDirty=true;
    }

    /** Set the width of the x-axis (domain) scale centered around current midpoint of domain scale. Uses a scaled integer 1-1000 
     * (meaning 0.1-100.0). Values outside this
     * range will cause the method to immediately exit without doing any changes. Existing domain extents (min, max X values) define
     * the total range. 
     * 
     * @param domainWidth The [scaled integer] width in % (1-1000) of range of x domain extents
     */
    protected void setDomainScale(int domainWidth) { 
        // set this.domainWidth
        setDomainWidth(domainWidth);
        
        double xval = getChart().getXYPlot().getDomainCrosshairValue();
        Range currRange = getChart().getXYPlot().getDomainAxis().getRange();
        // if x crosshair is in the range, gradually scale around it as the origin
        double scaleOrigin=currRange.getCentralValue();
        if (currRange.contains(xval)) {
            scaleOrigin=xval+(scaleOrigin-xval)/1.1f;
        }
        
        this.domainRange = new Range(scaleOrigin-this.domainWidth/2, scaleOrigin+this.domainWidth/2);
        getChart().getXYPlot().getDomainAxis().setRange(this.domainRange);
        chartDirty=true;
    }

    /** Calc and set the domainWidth based on slider value (.1-100% as 1-1000)
     * @param domainWidthPercent
     * @return
     */
    private boolean setDomainWidth(int domainWidthPercent){
         if (domainWidthPercent<1 || domainWidthPercent>1000) return false;
         if (getChart().getXYPlot().getDataset()==null) return false;
         try {
         double minXVal = ((XYSeriesCollection)getChart().getXYPlot().getDataset()).getSeries(0).getMinX();
         double maxXVal = ((XYSeriesCollection)getChart().getXYPlot().getDataset()).getSeries(0).getMaxX();
         this.domainWidth=(maxXVal-minXVal)*(float)domainWidthPercent/1000f*DOMAIN_SIZE_MULT;
         } catch (Exception e) {
             ; // do nothing
         }
         return true;
     }
    
    /** Set a listener to set the width of the x-axis (domain) scale instance var this.domainWidth due to a chart resize event.
     * This ensures that the slider value is respected (updated elsewhere) on user mouse zooms.
     */
    private void setAxisChangeListener() {
        final JFreeChart parent=getChart();
        //this.domainAxis.setRange(this.domainRange);
        getChart().getXYPlot().getDomainAxis().addChangeListener(
        new AxisChangeListener() {
            public void axisChanged(AxisChangeEvent event) {
                domainWidth = parent.getXYPlot().getDomainAxis().getRange().getLength();
            }
        });
        
    }
}
