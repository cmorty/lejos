package lejos.pc.charting;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeriesCollection;


/** JFreeChart object in a ChartPanel for XYZ charts. Includes methods to add titles (headers), 
 * series datapoints, and change domain scaling
 * 
 * @author Kirk P. Thompson
 */
class LoggingChart extends BaseXYChart{
    private static final int INIT_DOMAIN_WIDTH = 10000;
    private static final float DOMAIN_SIZE_MULT = 1.01f;
    private static final int MAX_SCALE = 10000; // percentage as scaled int
    
    private Range[] yExtents = new Range[RANGE_AXIS_COUNT];
    private double domainWidth = INIT_DOMAIN_WIDTH; // milliseconds defining how big to initially make the domain scale
    /**
     * The domain limiting mode. Set by setDomainLimiting()
     */
    private int domainAxisLimitMode = DAL_UNLIMITED;
    /**
     * the domain limiting value. Set by setDomainLimiting()
     */
    private int domainAxisLimitValue = 0;
    
    public LoggingChart() {
        super(null);
        
//        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
//        THISCLASS=thisClass[thisClass.length-1];
        
        try {
            jbInit();
        } catch (Exception e) {
            dbg("!** LogChartPanel Exception: " + e.toString());
            e.printStackTrace();
        }
        // start the updater thread
        this.runUpdater();
        
        // Set a listener to set the width of the x-axis (domain) scale instance var this.domainWidth due to a chart resize event
        setAxisChangeListener();
        
        // set up managers for comment event markers and domain measuring markers
        this.markerManager = new MarkerManager(this);
        this.mouseManager = new MouseManager(this, this.markerManager);
        setMouseListener();
    }
    
    private void runUpdater() {
        // chart updater, clipping, and domain scroller thread
        final Thread t1 = new Thread(new Runnable(){
            public void run() {
                while (true) {
                    doWait(REFRESH_FREQUENCY_MS);
                   
                    if (chartDirty && LoggingChart.this.getChart()!=null) {
                        // slide the the domain range to show animation of data coming in
                        XYPlot plot = getChart().getXYPlot();
                        if (plot.getDataset().getSeriesCount()>0) {
                                double maxXVal = ((XYSeriesCollection)plot.getDataset()).getSeries(0).getMaxX();
                                double minXVal = ((XYSeriesCollection)plot.getDataset()).getSeries(0).getMinX();
                                // sets this.domainRange
                                Range dRange=null;
                                synchblock1: synchronized (lockObj1) {
                                    if (LoggingChart.this.scrollDomain){
                                        dRange=new Range(maxXVal-LoggingChart.this.domainWidth, maxXVal);
                                    } else {
                                        dRange=new Range(minXVal, maxXVal);
                                    }
                                    setDomainRange(dRange);
                                    // ensure value (range) axis displays extents of data as it scrolls
                                    doRangeExtents(false);
                                    
                                    // if we have a clipping mode set...
                                    if (LoggingChart.this.domainAxisLimitMode!=DAL_UNLIMITED) {
                                        // coordinate with addDataPoints to prevent it from adding whilst we remove
                                        int length=((XYSeriesCollection)plot.getDataset()).getItemCount(0);
                                        int endIndex=0;
                                    
                                        // identify clipping range here
                                        if (LoggingChart.this.domainAxisLimitMode==DAL_COUNT) {
                                            endIndex = length-LoggingChart.this.domainAxisLimitValue;
                                        } else if (LoggingChart.this.domainAxisLimitMode==DAL_TIME){
                                            // find the range to meet TIME criteria
                                            for (int i=length-1;i>=0;i--) {
                                                if (maxXVal-(((XYSeriesCollection)plot.getDataset()).getXValue(0,i))>
                                                    LoggingChart.this.domainAxisLimitValue) 
                                                {
                                                    endIndex=i+1;
                                                    break;
                                                }
                                            }
                                        } else {
                                            dbg("BAD VALUE: Notify Developer. domainAxisLimitMode=" + LoggingChart.this.domainAxisLimitMode);
                                            break synchblock1; 
                                        }
                                        // delete the identified range from all datasets and their series
                                        if (endIndex>0) {
                                            LoggingChart.this.getChart().setNotify(false);
                                            //delete markers here
                                            double xval =((XYSeriesCollection)plot.getDataset(0)).getSeries(0).getX(endIndex-1).doubleValue();
                                            markerManager.deleteComments(xval);
                                            
                                            for (int i=0;i<plot.getDatasetCount();i++){
                                                for (int j=0;j<plot.getDataset(i).getSeriesCount();j++) {
                                                    ((XYSeriesCollection)plot.getDataset(i)).getSeries(j).delete(0,endIndex-1);
                                                }
                                            }
                                            
                                            LoggingChart.this.getChart().setNotify(true);
                                        }
                                        try {
                                            lockObj1.wait(50);
                                        } catch (InterruptedException e) {
                                        	// do nothing
                                        }
                                    }
                                }
                        }
                        chartDirty=false;
                    }
                    LoggingChart.this.getChart().setNotify(false);
                }
            }
        });
        t1.setDaemon(true);
        t1.start();
    }
    
    // set the double-click to restore zoom to extents
    private void setMouseListener() {
        class ml extends MouseAdapter{
            @Override
			public void mouseClicked(MouseEvent e) {
                if ((e.getButton()&MouseEvent.BUTTON1)==MouseEvent.BUTTON1) {
                    // doubleclick zooms extents of data
                    if (e.getClickCount()==2) {
                        
                        if (getCursor()==Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)) {
                            return;
                        }
                        
                        if (!isEmptyChart()) {
                            restoreAutoDomainBounds();
                            // reset [session] historical range zoom extents if CTRL-Doubleleftclick
                            if (e.isControlDown()) {
                                restoreAutoRangeBounds();
                                // effectively resets the Y range zoom extents
                                resetYExtents();
                            }
                            // restore calced Y range bounds for all axes
                            doRangeExtents(true);
                        }
                    }
                }
            }
            
            @Override
			public void mouseReleased(MouseEvent e) {
                getChart().setNotify(true);
            }
            
        }
        this.addMouseListener(new ml());
    }
    
    
    // required by JDeveloper to use/render in graphical GUI editor
    /**
	 * @throws Exception  
	 */
    private void jbInit() throws Exception {
        setLayout(null);
        setChart(getBaselineChart());
        chartDirty=true;
        setVisible(true);
    }
	
    /**
     * Set the domain axis limiting mode. When limited, the domain "width" will
     * not exceed the specified value by dropping the first-most datapoints to
     * meet the threshold defined by <code>limitMode</code> and
     * <code>value</code> as datapoints are added.
     * 
     * @param limitMode
     *            Mode to use. Default is <code>DAL_UNLIMITED</code>.
     * @param value
     *            Time in millisecs for mode <code>DAL_TIME</code>, domain item
     *            count (corresponds to rows in log) for mode
     *            <code>DAL_COUNT</code>. Ignored if mode is
     *            <code>DAL_UNLIMITED</code>
     * @see #addDataPoints
     * @see #DAL_UNLIMITED
     * @see #DAL_TIME
     * @see #DAL_COUNT
     */
    void setDomainLimiting(int limitMode, int value) {
        if (limitMode < DAL_UNLIMITED || limitMode > DAL_COUNT)
            return;
        this.domainAxisLimitMode = limitMode;
        this.domainAxisLimitValue = value;
    }
    
    private synchronized void setDomainRange(Range range) {
        if (range == null)
            return;
        // this.domainRange=range;
        getChart().getXYPlot().getDomainAxis().setRange(range);
    }

    /**
     * Set the width of the x-axis (domain) scale centered around current
     * midpoint of domain scale. Uses a scaled integer 1-1000 (meaning
     * 0.1-100.0). Values outside this range will cause the method to
     * immediately exit without doing any changes. Existing domain extents (min,
     * max X values) define the total range.
     * 
     * @param domainWidth
     *            The [scaled integer] width in % (1-1000) of range of x domain
     *            extents
     */
    void setDomainScale(int domainWidth) {
        // set this.domainWidth
        setDomainWidth(domainWidth);
        // System.out.println("setDomainScale: domainWidth=" + domainWidth );

        XYPlot plot = getChart().getXYPlot();
        double xval = plot.getDomainCrosshairValue();
        Range currRange = plot.getDomainAxis().getRange();
        // if x crosshair is in the range, gradually scale around it as the
        // origin
        double scaleOrigin = currRange.getCentralValue();
        if (currRange.contains(xval)) {
            scaleOrigin = xval + (scaleOrigin - xval) / 1.1f;
        }
        // sets this.domainRange
        setDomainRange(new Range(scaleOrigin - this.domainWidth / 2,
                scaleOrigin + this.domainWidth / 2));
        this.getChart().setNotify(true);
    }
    
    /**
     * Calc and set the domainWidth based on slider value (.01-100% as 1-10000)
     * 
     * @param domainWidthPercent
     * @return true of setting was successful
     */
    private boolean setDomainWidth(int domainWidthPercent) {
        if (domainWidthPercent < 1 || domainWidthPercent > MAX_SCALE)
            return false;
        if (getChart().getXYPlot().getDataset() == null)
            return false;
        try {
            double minXVal = ((XYSeriesCollection) getChart().getXYPlot()
                    .getDataset()).getSeries(0).getMinX();
            double maxXVal = ((XYSeriesCollection) getChart().getXYPlot()
                    .getDataset()).getSeries(0).getMaxX();
            this.domainWidth = (maxXVal - minXVal) * domainWidthPercent
                    / MAX_SCALE * DOMAIN_SIZE_MULT;
        } catch (Exception e) {
            // do nothing but ignore error
        }
        return true;
    }
    
    /**
     *  Ensure value (range) axis displays extents of data as it scrolls
     * @param forceExtents true to always do a setRange. false to only do one if extents expand
     */
    private synchronized void doRangeExtents(boolean forceExtents) {
        // ensure value (range) axis displays extents of data as it scrolls
        // dataset corresponds to axis one-to-one
        Range yRange=null;
        XYPlot plot = getChart().getXYPlot();
        for (int i=0;i<plot.getDatasetCount();i++){
            if (plot.getDataset(i)==null) continue;
            yRange=((XYSeriesCollection)plot.getDataset(i)).getRangeBounds(true);
            if (yRange==null) continue;
            yRange=Range.expand(yRange,.05,.05);
            
            if (yExtents[i]==null) {
                yExtents[i]=yRange; 
                plot.getRangeAxis(i).setRange(yExtents[i]);
            }
            // ensure that range axis scale is always fits the biggest value per session (does not shrink)
            if (!(yExtents[i].contains(yRange.getLowerBound())&&yExtents[i].contains(yRange.getUpperBound()))) {
                yExtents[i]=yRange;
                if (!forceExtents) plot.getRangeAxis(i).setRange(yExtents[i]);
            }
            if (forceExtents) plot.getRangeAxis(i).setRange(yExtents[i]);
        }
    }
    
    private synchronized void resetYExtents(){
        yExtents=new Range[RANGE_AXIS_COUNT];
    }
    
 
    @Override
    int setSeries(String[] seriesNames, int chartType){
        int seriesCreatedCount  = super.setSeries(seriesNames, chartType);
        // int yExtents so extents will be calculated for new data
        resetYExtents();
        return seriesCreatedCount;
    }
    
    /** Set a listener to set the width of the x-axis (domain) scale instance var this.domainWidth due to a chart resize event.
     * This ensures that the slider value is respected (updated elsewhere) on user mouse zooms.
     */
    private void setAxisChangeListener() {
        getChart().getXYPlot().getDomainAxis().addChangeListener(
        new AxisChangeListener() {
            public void axisChanged(AxisChangeEvent event) {
                domainWidth = LoggingChart.this.getChart().getXYPlot().getDomainAxis().getRange().getLength();
                if (Double.isNaN(domainWidth)) domainWidth=INIT_DOMAIN_WIDTH;
                // set domain width to 10 seconds if new chart (no series data and domain is empty)
                if (domainWidth==1.0 && LoggingChart.this.getChart().getXYPlot().getSeriesCount()==0) domainWidth=INIT_DOMAIN_WIDTH;
            }
        });
        
    }
}
