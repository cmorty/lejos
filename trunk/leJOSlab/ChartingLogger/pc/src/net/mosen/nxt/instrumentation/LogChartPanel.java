package net.mosen.nxt.instrumentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;

import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import lejos.util.Delay;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
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


public class LogChartPanel extends ChartPanel{
    private final String THISCLASS;
    private final float DOMAIN_SIZE_MULT=1.01f; // provides a small gap between series and chart right,left edges on domain axis
    
    private JFreeChart _JFChartPrimary;
    private Range domainRange;
    private XYSeriesCollection dataset;
    private NumberAxis domainAxis;
    private NumberAxis rangeAxis;
    private int refreshFrequency_ms=100; // used by update thread 
    private boolean chartDirty = false; // used by update thread to flag an event notification for repaints
    private double domainWidth=10000; // milli seconds defining how big to make the domain scale (for sliding it)
    
    private JLabel jLabelXYval = new JLabel();
     
    public LogChartPanel() {
        super(null);
//        super(new JFreeChart(new XYPlot()));
        
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
 
        try {
            jbInit();
            //            setForeground(Color.GREEN);
        } catch (Exception e) {
            dbg("!** LogChartPanel Exception: " + e.toString());
            e.printStackTrace();
        }
        
        // chart updater thread
        final Thread t1 = new Thread(new Runnable(){
            public void run() {
                while (true) {
                    doWait(refreshFrequency_ms);
                    if (chartDirty) {
                        _JFChartPrimary.setNotify(true);
                        _JFChartPrimary.fireChartChanged();
                        _JFChartPrimary.setNotify(false);
                        chartDirty=false;
                    }
                }
            }
        });
        t1.setDaemon(true);
        // create thread wiuth invokeLater() to ensure swing thread safety for update thread
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                t1.start();
            }
        });
        
        // set the double-click to restore zoom to extents
        setMouseListener();
    }
   
    // z..z..z..z..z..z...z...
    private void doWait(long milliseconds) {
         try {
             Thread.sleep(milliseconds);
         } catch (InterruptedException e) {
             //Thread.currentThread().interrupt();
         }
    }
    
    // required by Jdevloper to use (render) graphical GUI editor
    private void jbInit() throws Exception {
        this.setLayout( null );
        _JFChartPrimary=dynamic();
        this.setChart(_JFChartPrimary);
        
        // updates the x,y label
        _JFChartPrimary.addProgressListener(new ChartProgressListener() {
                public void chartProgress(ChartProgressEvent event) {
                    long xval = (long)_JFChartPrimary.getXYPlot().getDomainCrosshairValue();
                    double yval = _JFChartPrimary.getXYPlot().getRangeCrosshairValue();
                    jLabelXYval.setText(String.format("%1$,6d : %2$,7.3f", xval, yval));
                }
            }
        );
                     
                     
        jLabelXYval.setText("jLabelXYval");
        jLabelXYval.setBounds(new Rectangle(595, 275, 120, 20));
        jLabelXYval.setFocusable(false);
        jLabelXYval.setFont(new Font("Arial", 0, 10));
        jLabelXYval.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(jLabelXYval, null);
        jLabelXYval.setText("--, --");
        
        this.setSize(new Dimension(800, 300));
        this.setVisible(true);
    }

    private void dbg(String msg) {
        System.out.println(THISCLASS + "-" + msg);
    }
    
    private XYSeries getSampleSeries(){
        XYSeries samp = new XYSeries("NXT data",true, true);
        float value=0;
        int x=0;
        for (int i=0;i<domainWidth;i++){
            if (i%10==0) {
                samp.add(x, Math.sin(value));
                x+=10;
                value+=.1f;
            }
        }
        return samp;
    }
    
    protected void setTitle(String title){
        //TextTitle title = new TextTitle("", new Font("Arial", Font.PLAIN, 12));
        this.getChart().setTitle(title);
        this.chartDirty=true;
    }
    
//    private JFreeChart getBlankChart(Plot plot){
//        return new JFreeChart(null, new Font("Arial", Font.PLAIN, 12), plot, true);
//    }

    private JFreeChart dynamic(){
        // create dataset
        dataset = new XYSeriesCollection();
        dataset.addSeries(getSampleSeries());
//        dataset.getSeries(0).add(0,0);
//        dataset.getSeries(0).add(domainWidth/2,5);
//        dataset.getSeries(0).add(domainWidth,7.5);
        
        // set the domain axis
        domainAxis = new NumberAxis("Time (ms)");
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 9));
        domainAxis.setLabelFont(new Font("Arial", Font.PLAIN, 12));
//        domainAxis.setRange(0,DOMAIN_WIDTH);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setTickLabelsVisible(true);
        domainAxis.setAutoRange(true); // TODO change when panning figured out
        domainRange = new Range(0, domainWidth);
        domainAxis.setRange(domainRange);
        domainAxis.setAutoRangeIncludesZero(false);
        
        // set the value axis
        rangeAxis = new NumberAxis("Range");
        rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 9));
        rangeAxis.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        rangeAxis.setAutoRange(true);
        
        // set the renderer
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setBaseStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_BEVEL));
//        renderer.setSeriesStroke(0,new BasicStroke(2.0f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_BEVEL, 1f, new float[] { 10.0f, 4.0f, 1.0f, 4.0f }, 5f));
        renderer.setSeriesStroke(0,new BasicStroke(1.0f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_BEVEL));
        renderer.setSeriesPaint(0, Color.RED);
//        renderer.setSeriesPaint(1, Color.green);
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        
        
        // create the plot object to pass to the chart
        XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
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
        
        this.setMouseWheelEnabled(true);
        chart.setNotify(false);
        return chart;
    }


    /** Set the passed series names as new XYseries in the dataset. Existing series are wiped. Must be at least two items
     * in the array or any existing series is left intact and method exits with 0. First item is set as domain label.
     * @param seriesNames Array of series names
     * @return The number of series created
     */
    protected int setSeries(String[] seriesNames){
        // TODO clone existing chart and put it in a new frame and display before we whack the dataset
        if (seriesNames.length<2) return 0;
        
        dataset = new XYSeriesCollection();
        _JFChartPrimary.getXYPlot().setDataset(dataset);
        _JFChartPrimary.getXYPlot().getDomainAxis().setLabel(seriesNames[0]);
        for (int i=1;i<seriesNames.length;i++) {
            dataset.addSeries(new XYSeries(seriesNames[i],true, true));
        }
        chartDirty=true;
        return _JFChartPrimary.getXYPlot().getSeriesCount();
    }
    

    private void setMouseListener() {
        class ml extends MouseAdapter{
            public void mouseClicked(MouseEvent e) {
                if ((e.getButton()&MouseEvent.BUTTON1)==MouseEvent.BUTTON1) {
                    //_JFChartPrimary.setNotify(true); // fals eis ste in plotchange lsitener
//                    dbg("mouseclicked");
                    if (e.getClickCount()==2) {
                        restoreAutoBounds();
                    }
                }
//                (new Thread(new Runnable(){
//                    public void run() {
//                        doWait(200);
//                        long xval = (long)_JFChartPrimary.getXYPlot().getDomainCrosshairValue();
//                        double yval = _JFChartPrimary.getXYPlot().getRangeCrosshairValue();
//                        jLabelXYval.setText(String.format("%1$6d, %2$7.3f",xval, yval));
//                    }
//                }
//                )).start();
            }
            
            public void mousePressed(MouseEvent e){
                if ((e.getButton()&MouseEvent.BUTTON1)==MouseEvent.BUTTON1){
                    //dbg(e.toString());
                    
                }
            }
        }
        this.addMouseListener(new ml());
    }
    
    /**add data for the series
     * 
     * @param logLine the logger line from the NXT
     */
    protected void addDataPoints(String logLine) {
        
        String[] values = logLine.split("\t");
        if (values.length<2) {
            dbg("!** addDataPoints: No series values defined. Returning");
            return;
        }
        
        // get number of series (primary dataset)
        int seriesCount = _JFChartPrimary.getXYPlot().getSeriesCount();
        
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
            timeStamp = Long.parseLong(values[0]);
//            dbg("timeStamp=" + timeStamp);
        } catch (NumberFormatException e){
            dbg("!** addDataPoints: invalid timestamp");
            e.printStackTrace();
            return;
        }
        
        // TODO filter out string values
        Double seriesTempvalue;
        
        int index=0;
        XYSeriesCollection tempDs=(XYSeriesCollection)_JFChartPrimary.getXYPlot().getDataset();
        for (int i=1;i<values.length;i++) {            
//            dbg("values[" + i +  "]: " + values[i]);
            if (values[i].trim().length()==0) continue; // we can get an empty value if NXT doesn't log all vars before it closes a connection
            try {
                seriesTempvalue = new Double(values[i]);
//                dbg("seriesTempvalue=" + seriesTempvalue + ":" + tempDs.getSeries(index).getDescription());
                tempDs.getSeries(index).add(timeStamp,seriesTempvalue);
                index++;
            } catch (NumberFormatException e){
                System.err.format("%1$s.addDataPoints: iterator [%2$d] invalid value: %3$s", this.getClass().getName(), i, values[i]);
                e.printStackTrace();
            }
        }
        
        
        //(XYDataset)        
//        double minXVal = ((XYSeriesCollection)this.getChart().getXYPlot().getDataset()).getSeries(0).getMinX();
        double maxXVal = ((XYSeriesCollection)this.getChart().getXYPlot().getDataset()).getSeries(0).getMaxX();
        domainRange = new Range(maxXVal-this.domainWidth, maxXVal);
        domainAxis.setRange(domainRange);
        
        chartDirty=true;
        
//        _JFChartPrimary.setNotify(true);
//        _JFChartPrimary.fireChartChanged();
        
        
//            if (domainVal > DOMAIN_BASE+DOMAIN_WIDTH) {
////                this.mySeries.remove(new Double(domainVal-DOMAIN_WIDTH));
//                this.mySeries.remove(0);
//    //            dbg(this.mySeries.remove(new Double(domainVal-DOMAIN_WIDTH)));
//                this.domainRange = new Range(domainVal-DOMAIN_WIDTH+1, domainVal+1);
//                _JFChartPrimary.getXYPlot().getDomainAxis().setRange(domainRange);
//            }
//            domainVal++;
        
        // add to master set
//        dataValues.add(valueRecord);
    }

    /** Set the width of the x-axis (domain) scale. Uses a scaled integer 1-1000 (meaning 0.1-100.0). Values outside this
     * range will cause the method to immediately exit without doing any changes. Existing domain extents (min, max X values) define
     * the total range. 
     * 
     * @param domainWidth The [scaled integer] width in % (1-1000) of range of x domain extents
     */
    protected void setDomainWidth(int domainWidth) {
//        if (true) return;
        if (domainWidth<1 || domainWidth>1000) return;
        double minXVal = ((XYSeriesCollection)this.getChart().getXYPlot().getDataset()).getSeries(0).getMinX();
        double maxXVal = ((XYSeriesCollection)this.getChart().getXYPlot().getDataset()).getSeries(0).getMaxX();
        this.domainWidth=(maxXVal-minXVal)*(float)domainWidth/1000f;
        domainRange = new Range(minXVal-this.domainWidth*DOMAIN_SIZE_MULT+this.domainWidth, minXVal+this.domainWidth*DOMAIN_SIZE_MULT);
        domainAxis.setRange(domainRange);
        chartDirty=true;
    }

    /** Get the width of the x-axis (domain) scale.
     * @return The width in domain units
     */
    protected double getDomainWidth() {
        return this.domainWidth;
    }
}
