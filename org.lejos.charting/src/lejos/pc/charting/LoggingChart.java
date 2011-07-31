package lejos.pc.charting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;


/** JFreeChart object in a ChartPanel. Includes methods to add titles (headers), series datapoints, and change domain scaling
 * @author Kirk P. Thompson
 */
public class LoggingChart extends ChartPanel{
    private final String THISCLASS;
    private static final float DOMAIN_SIZE_MULT=1.01f; // provides a small gap between series and chart right,left edges on domain axis
    private static final int MAX_SCALE=10000;

    /** No domain range limiting
     */
    final static int DAL_UNLIMITED=0;
    /** Domain range limiting by Time (in ms)
     */
    final static int DAL_TIME=1;
    /** Domain range limiting by Count (# of domain items in dataset(s))
     */
    final static int DAL_COUNT=2;
    
    private Range domainRange;
    private XYSeriesCollection[] dataset = new XYSeriesCollection[4];
//    private NumberAxis domainAxis;
    private NumberAxis[] rangeAxis = new NumberAxis[4];
    private int refreshFrequency_ms=100; // used by update thread 
    private boolean chartDirty = false; // used by update thread to flag an event notification for repaints
    private boolean doDomainAdjust=false;
    private double domainWidth=10000; // milli seconds defining how big to initially  make the domain scale (for sliding it)
    private SeriesDef[] seriesDefs; 
    private Object lockObj1 = new Object();
    private int domainAxisLimitMode=DAL_UNLIMITED;
    private int domainAxisLimitValue=0;
    
    private class SeriesDef {
        String label;
        int axisIndex;
        int seriesIndex;
    }
    
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
                    if (chartDirty && LoggingChart.this.getChart()!=null) {
                        // slide the the domain range to show animation of data coming in
                        XYPlot plot = getChart().getXYPlot();
                        if (doDomainAdjust && plot.getDataset().getSeriesCount()>0) {
                            double maxXVal = ((XYSeriesCollection)plot.getDataset()).getSeries(0).getMaxX();
                            // sets this.domainRange
                            setDomainRange( new Range(maxXVal-LoggingChart.this.domainWidth, maxXVal));
                            
                            // ensure value (range) axis displays extents of data as it scrolls
                            // dataset cooresponds to axis one-one
                            for (int i=0;i<plot.getDatasetCount();i++){
                                if (plot.getDataset(i)==null) continue;
                                Range yRange=((XYSeriesCollection)plot.getDataset(i)).getRangeBounds(true);
                                if (plot.getRangeAxis(i)==null || yRange==null) continue;
                                plot.getRangeAxis(i).setRange(yRange);
                                //plot.getRangeAxis().setAutoRange(true);
                             }
 
                             doDomainAdjust=false;
                        }
                        chartDirty=false;
                    }
                    LoggingChart.this.getChart().setNotify(false);
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
    
    public void paintComponent(Graphics g) {
        // try two times for intermittent repaint problem (identified 7/22/2011) with jfreechart
        for (int i=0;i<2;i++) {
            try {
                super.paintComponent(g);
            } catch (NullPointerException e) {
                ; // ignore
//                System.out.println("!*** NullPointerException in chart paintComponent(try #" + i + ")");
            } catch (IndexOutOfBoundsException e) {
                ; // ignore. this happens when I clip the dataset probably due to a synchronization issue
            }
        }
    }
    
    private void setDomainRange(Range range){
        if (range==null) return;
        synchronized (lockObj1) {
            this.domainRange=range;
            getChart().getXYPlot().getDomainAxis().setRange(range);
        }
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
       // setDomainScale(MAX_SCALE);
        chartDirty=true;
    }

    private void dbg(String msg) {
        System.out.println(THISCLASS + "-" + msg);
    }


    private NumberAxis getRangeAxis(int index) {
        if (this.rangeAxis[index-1]!=null) {
            return this.rangeAxis[index-1];
        }
        
        // create if doesn't exist
        NumberAxis rangeAxis;
        Color axisColor=getAxisColor(index);
        String axisLabel="Range";
        if (index>1) axisLabel+="-" + index;
        rangeAxis=new NumberAxis(axisLabel);
        rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 9));
        rangeAxis.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        rangeAxis.setAutoRange(true);
        rangeAxis.setAutoRangeIncludesZero(false);
        
        // set colors
        rangeAxis.setLabelPaint(axisColor);
        rangeAxis.setAxisLinePaint(axisColor);
        rangeAxis.setLabelPaint(axisColor);
        rangeAxis.setTickLabelPaint(axisColor);
        rangeAxis.setTickMarkPaint(axisColor);

        this.rangeAxis[index-1]=rangeAxis;
        
        return rangeAxis;
    }
    
    private Color getAxisColor(int index) {
        switch(index) {
            case 1:
                return Color.BLACK;
            case 2:
                return Color.MAGENTA.darker();
            case 3:
                return Color.BLUE;
            case 4:
                return Color.RED;
            
        }
        return Color.BLACK;
    }
    
    private Color getSeriesColor(int index){
        index++;
        if (index>11) index=index%11;
//        System.out.println("index="+index);
        switch(index) {
            case 1:
                return Color.BLUE.brighter().brighter();
            case 2:
                return Color.RED.brighter();
            case 3:
                return Color.GREEN.darker();
            case 4:
                return Color.CYAN.darker();
            case 5:
                return Color.BLUE;
            case 6:
                return Color.DARK_GRAY;
            case 7:
                return Color.MAGENTA;
            case 8:
                return Color.CYAN;
            case 9:
                return Color.YELLOW.darker();
            case 10:
                return Color.GREEN;
            case 11:
                return Color.RED.darker();
        }
        return Color.LIGHT_GRAY;
    }
    
    private NumberAxis getDomainAxis() {
        NumberAxis domainAxis;
        // set the domain axis
        domainAxis = new NumberAxis("Time (ms)");
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 9));
        domainAxis.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setTickLabelsVisible(true);
        domainAxis.setAutoRange(true); 
        this.domainRange= new Range(0, domainWidth);
        domainAxis.setRange(this.domainRange);
        domainAxis.setAutoRangeIncludesZero(false);
        
        return domainAxis;
    }
    
    private JFreeChart getBaselineChart(){
        // create the plot object to pass to the chart
        XYPlot plot = new XYPlot(); //this.dataset[0], domainAxis, getRangeAxis(1), renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainAxis(getDomainAxis());
        plot.setDomainPannable(true);
        plot.setDomainCrosshairVisible(true);
        plot.setRangePannable(true);
        
        plot.setDataset(new XYSeriesCollection());
        
        // create and return the chart        
        JFreeChart chart = new JFreeChart("", new Font("Arial", Font.BOLD, 14), plot, true);       
        chart.setNotify(false);
        chart.setBackgroundPaint(Color.white);
        chart.getLegend().setPosition(RectangleEdge.RIGHT); 
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 10));
        
        //this.setHorizontalAxisTrace(true);
        this.setMouseWheelEnabled(true);
        
        return chart;
    }

    // axisIndex is one-based
    private XYSeriesCollection getAxisDataset(int axisIndex){
        if (this.dataset[axisIndex-1]!=null) {
            //System.out.println("getAxisDataset found axisIndex " + axisIndex);
            return this.dataset[axisIndex-1];
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        // set the renderer
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setBaseStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_BEVEL)); 
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        renderer.setBaseLegendTextPaint(getAxisColor(axisIndex));
        renderer.setBaseShapesVisible(false);
        renderer.clearSeriesPaints(true);
        renderer.setAutoPopulateSeriesPaint(false);
        
//        System.out.println(getChart().getXYPlot());
        getChart().getXYPlot().setDataset(axisIndex-1, dataset);
        getChart().getXYPlot().setRenderer(axisIndex-1, renderer);
        // get the one-based axis def
        getChart().getXYPlot().setRangeAxis(axisIndex-1, getRangeAxis(axisIndex));
        AxisLocation axloc;
        if (axisIndex%2==0) {
            // even on right
            axloc=AxisLocation.BOTTOM_OR_RIGHT; 
        } else {
            axloc=AxisLocation.BOTTOM_OR_LEFT;
        }
        getChart().getXYPlot().setRangeAxisLocation(axisIndex-1, axloc);
        getChart().getXYPlot().mapDatasetToRangeAxis(axisIndex-1, axisIndex-1);
        getChart().getXYPlot().getRangeAxis(axisIndex-1).setAutoRange(true);
        getChart().getXYPlot().setRangeCrosshairVisible(true);
        
        this.dataset[axisIndex-1]=dataset;
        return dataset; 
    }
    
    
    /** Set the passed series/header definitions as new XYseries in the dataset. Existing series are wiped. Must be at least two items
     * in the array or any existing series is left intact and method exits with 0. First item (series 0) is set as domain label and should
     * be system time in milliseconds, and is always axis 0 for multi-axis charts.
     * <p>
     *  The string format/structure of each string field is:<br>
     *  <code>[name]:[axis ID 1-4]</code>
     *  <br>i.e. <pre>"MySeries:1"</pre>
     * @param seriesNames Array of series names
     * @return The number of series created
     */
    int setSeries(String[] seriesNames){
//        System.out.println("setSeries");
        // TODO clone existing chart and put it in a new frame and display before we whack the dataset
        // don't allow empty range (length==1 means only domain series defined)
        if (seriesNames.length<2) return 0;
        
        // clear any series
        dataset = new XYSeriesCollection[4];
        rangeAxis = new NumberAxis[4];
        
        int dsCount=getChart().getXYPlot().getDatasetCount();
        for (int i=0;i<dsCount;i++) {
            getChart().getXYPlot().mapDatasetToRangeAxis(i, 0);
            getChart().getXYPlot().setRangeAxis(i,null);
            if (getChart().getXYPlot().getDataset(i)!=null) {
                ((XYSeriesCollection)getChart().getXYPlot().getDataset(i)).removeAllSeries();
            }
        }

        String[] fields;
        
        // remove domain def from series def
        String[] theSeries = new String[seriesNames.length-1];
        System.arraycopy(seriesNames, 1, theSeries, 0, theSeries.length);
        fields=seriesNames[0].split(":");
        String domainLabel=fields[0];
        
        seriesDefs= new SeriesDef[theSeries.length];
        int axisSeriesIndex[] = {0,0,0,0}; // one-based. used as series counter for each axis id
        int minAxisId = 4;
        for (int i=0;i<theSeries.length;i++) {
//            System.out.println(theSeries[i]);
            seriesDefs[i]=new SeriesDef();
            fields=theSeries[i].split(":");
            seriesDefs[i].label=fields[0];
            if (fields.length==1) {
                seriesDefs[i].axisIndex=1;
            } else {
                try {
                    seriesDefs[i].axisIndex=Integer.valueOf(fields[1]);
                } catch (Exception e) {
                    seriesDefs[i].axisIndex=1;
                }
            }
            // need to ensure that dataset(0) is always created
            // shift all series defs to ensure zero-based dataset
            if (seriesDefs[i].axisIndex<minAxisId) minAxisId=seriesDefs[i].axisIndex;
            
            // bump series index per axis
            seriesDefs[i].seriesIndex=++axisSeriesIndex[seriesDefs[i].axisIndex-1];
        }
        
        // create the datasets per axisID one-one. We need to ensure that dataset(0) is always created
        // so a shift offset is used
        for (int i=0;i<seriesDefs.length;i++){
            seriesDefs[i].axisIndex-=(minAxisId-1); // shift all axisIDs down to ensure we have a axisID=1 (one-based)
            // add series to dataset 
            getAxisDataset(seriesDefs[i].axisIndex).addSeries(new XYSeries(seriesDefs[i].label,true, true));
        }
//        System.out.println("getDatasetCount=" + getChart().getXYPlot().getDatasetCount());
        int colorIndex=0;
        for (int i=0;i<getChart().getXYPlot().getDatasetCount();i++){
            if (getChart().getXYPlot().getDataset(i)==null) continue;
//            System.out.println("   DS-"+ i + ": getSeriesCount=" + getChart().getXYPlot().getDataset(i).getSeriesCount());
            for (int ii=0;ii< getChart().getXYPlot().getDataset(i).getSeriesCount();ii++) {
                getChart().getXYPlot().getRenderer(i).setSeriesPaint(ii,getSeriesColor(colorIndex++));
                getChart().getXYPlot().getRenderer(i).setSeriesStroke(
                    ii,new BasicStroke(.50f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_BEVEL));
            }
            
        }
//        System.out.println("getRendererCount=" + getChart().getXYPlot().getRendererCount());
//        System.out.println("getSeriesCount=" + getChart().getXYPlot().getSeriesCount());
        
        getChart().getXYPlot().getDomainAxis().setLabel(domainLabel); 
        chartDirty=true;
        return getChart().getXYPlot().getSeriesCount();
    }
    

    private void setMouseListener() {
        class ml extends MouseAdapter{
            public void mouseClicked(MouseEvent e) {
                if ((e.getButton()&MouseEvent.BUTTON1)==MouseEvent.BUTTON1) {
//                    dbg("mouseclicked");
                    // doubleclick zooms extents of data
                    if (e.getClickCount()==2) {
                        restoreAutoBounds();
                    }
                }
            }
        }
        this.addMouseListener(new ml());
    }
    
    /**Add series data to the dataset. Pass a double array of series values that all share the same domain value (element 0). 
     * The number of values must match the header count in setSeries().
     * <p>
     * Element 0 is the domain (X) series and should be a timestamp.
     * @param seriesData the series data as <code>double</code>s
     * @see #setDomainLimiting
     */
    void addDataPoints(double[] seriesData) {
        if (seriesData.length<2) {
            dbg("!** addDataPoints: Not enough data. length=" + seriesData.length);
            return;
        }

        // seriesData[0]: first element should always be timestamp from NXT
        // add the datapoint series by series in correct axis ID
        XYPlot plot= getChart().getXYPlot();
        XYSeries tempSeries=null;
        for (int i=1;i<seriesData.length;i++) {            
            tempSeries=((XYSeriesCollection)plot.getDataset(seriesDefs[i-1].axisIndex-1)).getSeries(seriesDefs[i-1].seriesIndex-1);
            tempSeries.add(seriesData[0], seriesData[i], false);
            if (this.domainAxisLimitMode!=DAL_UNLIMITED) clipSeries(tempSeries, seriesData[0]);
        }
        
        // the updater thread picks this up every xxx ms and has the JFreeChart do it's notifications for rendering, 
        // etc. The domain axis is also scrolled for incoming data in this thread via doDomainAdjust=true
        chartDirty=true;
        doDomainAdjust=true;
    }
    
    private void clipSeries(XYSeries tempSeries, double currentDomainVal){
        while (tempSeries.getItemCount()>0) {
            if (this.domainAxisLimitMode==DAL_TIME) {
                if (currentDomainVal-tempSeries.getX(0).doubleValue()<=this.domainAxisLimitValue) break;
            } else if (this.domainAxisLimitMode==DAL_COUNT) {
                if (tempSeries.getItemCount()<=this.domainAxisLimitValue) break;
            }
            tempSeries.remove(0);
        }
    }
    
    /** Set the domain axis limiting mode. When limited, the domain "width" will not exceed the specified value by dropping the
     * first-most datapoints to meet the threshold defined by <code>limitMode</code> and <code>value</code> as datapoints are added.
     * @param limitMode Mode to use. Default is <code>DAL_UNLIMITED</code>.
     * @param value Time in millisecs for mode <code>DAL_TIME</code>, domain item count (corresponds to rows in log) for 
     *  mode <code>DAL_COUNT</code>. Ignored if mode is <code>DAL_UNLIMITED</code>
     * @see #addDataPoints
     * @see #DAL_UNLIMITED
     * @see #DAL_TIME
     * @see #DAL_COUNT
     */
    void setDomainLimiting(int limitMode, int value){
        if (limitMode<0||limitMode>2) return;
        this.domainAxisLimitMode=limitMode;
        this.domainAxisLimitValue=value;
    }
    
    /** Set the width of the x-axis (domain) scale centered around current midpoint of domain scale. Uses a scaled integer 1-1000 
     * (meaning 0.1-100.0). Values outside this // TODO change range
     * range will cause the method to immediately exit without doing any changes. Existing domain extents (min, max X values) define
     * the total range. 
     * 
     * @param domainWidth The [scaled integer] width in % (1-1000) of range of x domain extents
     */
    void setDomainScale(int domainWidth) { 
        // set this.domainWidth
        setDomainWidth(domainWidth);
        
        XYPlot plot= getChart().getXYPlot();
        double xval = plot.getDomainCrosshairValue();
        Range currRange = plot.getDomainAxis().getRange();
        // if x crosshair is in the range, gradually scale around it as the origin
        double scaleOrigin=currRange.getCentralValue();
        if (currRange.contains(xval)) {
            scaleOrigin=xval+(scaleOrigin-xval)/1.1f;
        }
        // sets this.domainRange
        setDomainRange(new Range(scaleOrigin-this.domainWidth/2, scaleOrigin+this.domainWidth/2));
        this.getChart().setNotify(true);
//        chartDirty=true;
    }

    /** Calc and set the domainWidth based on slider value (.1-100% as 1-1000) 
     * @param domainWidthPercent
     * @return
     */
    private boolean setDomainWidth(int domainWidthPercent){
         if (domainWidthPercent<1 || domainWidthPercent>MAX_SCALE) return false;
         if (getChart().getXYPlot().getDataset()==null) return false;
         try {
             double minXVal = ((XYSeriesCollection)getChart().getXYPlot().getDataset()).getSeries(0).getMinX();
             double maxXVal = ((XYSeriesCollection)getChart().getXYPlot().getDataset()).getSeries(0).getMaxX();
             this.domainWidth=(maxXVal-minXVal)*(float)domainWidthPercent/(float)MAX_SCALE*DOMAIN_SIZE_MULT;
         } catch (Exception e) {
             ; // do nothing
         }
         return true;
     }
    
    /** Set a listener to set the width of the x-axis (domain) scale instance var this.domainWidth due to a chart resize event.
     * This ensures that the slider value is respected (updated elsewhere) on user mouse zooms.
     */
    private void setAxisChangeListener() {
        getChart().getXYPlot().getDomainAxis().addChangeListener(
        new AxisChangeListener() {
            public void axisChanged(AxisChangeEvent event) {
                domainWidth = LoggingChart.this.getChart().getXYPlot().getDomainAxis().getRange().getLength();
            }
        });
        
    }
}
