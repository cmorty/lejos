package lejos.pc.charting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import lejos.pc.charting.data.XYZDataItem;
import lejos.pc.charting.data.XYZSeriesCollection;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

/**
 * Base XY ChartPanel implementation. Use this to create XYZSeriesCollection-based JFreeChart GUIs
 * 
 * @author kirk
 *
 */
abstract class BaseXYChart extends ChartPanel {
    // final String THISCLASS;
    /**
     * The maximum number of axes you should support
     */
    static final int RANGE_AXIS_COUNT = 4;
    /**
     * The suggested refresh frequency in milliseconds for updater threads
     */
    static final int REFRESH_FREQUENCY_MS = 500;
    
    private static final float NORMAL_SERIES_LINE_WEIGHT = 0.5f;
   

    /**
     * No domain range limiting
     */
    protected static final int DAL_UNLIMITED = 0;

    /**
     * Domain range limiting by Time (in ms)
     */
    protected static final int DAL_TIME = 1;

    /**
     * Domain range limiting by Count (# of domain items in dataset(s))
     */
    protected static final int DAL_COUNT = 2;
    /**
     * Used to indicate the chart data has been changed and needs a redraw
     */
    volatile boolean chartDirty = false;

    /**
     * metadata of each series added to chart via setSeries()
     * 
     * @author kirk
     * 
     */
    private class SeriesDef {
        String seriesLabel; 
        int axisIndex; // Corresponds to dataset(index) for axis 1-4. could be sparse meaning we can have non-consecutive axis indexes
        int seriesIndex; // Corresponds to series(index) in a dataset. is not sparse.
    }

    /**
     * each seriesdef is series definition of a series with label, axis idx (1-4), and
     * series idx
     */
    private SeriesDef[] seriesDefs;
    
    /**
     * use solely for synchronization across this and subclasses. Mainly for paint/graphics/render because we
     * don't want to be adding data in one thread and have the chart do a paint/render in another (null pointer
     * exceptions based empirical experience)
     */
    Object lockObj1 = new Object();
    
    
    boolean emptyChart = true;
    boolean scrollDomain = true;
    MarkerManager markerManager = null;
    MouseManager mouseManager = null;

    private int chartType;
    Shape[] seriesShapes = new Shape[10];
    
    /**
     * Used to track series collection (i.e dataset and one collection of series per chart axis). Maximum # of axes
     * is RANGE_AXIS_COUNT (4). used in setSeries()
     * 
     * @author kirk
     * 
     */
    private class DatasetAndAxis {
        XYZSeriesCollection datasetObj = null;
        //XYItemRenderer axisRenderer; // TODO to hold a renderer ref
        NumberAxis numberAxisObj = null;
    }

    /**
     * Used to track series collection (one per chart axis) and axes (up to 4 (RANGE_AXIS_COUNT))
     */
    private DatasetAndAxis[] datasetsAndAxes = null; // set in constructor and
                                                     // in setSeries()
    
    protected BaseXYChart(JFreeChart chart) {
        super(chart);

        // init the instance datasets and axes holding object
        this.datasetsAndAxes = initDatasetAndAxisArray();

        // Effectively disable font scaling/skewing
        // JFreeChart forum: Graphics context and custom shapes
        // http://www.jfree.org/forum/viewtopic.php?f=3&t=24499&hilit=font+scaling
        this.setMaximumDrawWidth(1800);
        this.setMaximumDrawHeight(1280);
    }
    
    /**
     * @return a new DatasetAndAxis array of RANGE_AXIS_COUNT length
     */
    private DatasetAndAxis[] initDatasetAndAxisArray() {
        DatasetAndAxis[] dAndDaArray = new DatasetAndAxis[RANGE_AXIS_COUNT];
        for (int i = 0; i < dAndDaArray.length; i++) {
            dAndDaArray[i] = new DatasetAndAxis();
        }
        return dAndDaArray;
    }

//    /**
//     * Return a new or cached/pooled NumberAxis. If a cached one doesn't exist
//     * for index, create one for index and return it.
//     * 
//     * @param index
//     *            The Axis index (1-4 as per RANGE_AXIS_COUNT)
//     * @param dAnda
//     *            the
//     * @return the axis ref
//     */
//    private NumberAxis getTheRangeAxis(int index, DatasetAndAxis[] dAnda) {
////        String axisLabel;
////        if (this.chartType == LoggerProtocolManager.CT_XY_TIMEDOMAIN) {
////            axisLabel = "Range";
////        } else {
////            axisLabel = "Y";
////        }
//        
//        if (dAnda[index].numberAxisObj != null) {
////            dAnda[index].numberAxisObj.setLabel(axisLabel);
//            return dAnda[index].numberAxisObj;
//        }
//
//        Color axisColor = getAxisColor(index);
//        
////        if (index > 0) {
////            axisLabel += "-" + (index + 1);
////        }
//
//        NumberAxis rangeAxis = this.getAxisInstance("undefined", axisColor);
//        dAnda[index].numberAxisObj = rangeAxis;
//
//        return rangeAxis;
//    }

    /**
     * helper method to Get a single XYZSeriesCollection based on axis metadata
     * in passed DatasetAndAxis[] array. Return from pool if exists otherwise it
     * creates one, and adds it to pool, and returns it.
     * <p>
     * One renderer per axis/dataset(XYZSeriesCollection)
     * 
     * @param axisIndex The axis index value 0-(RANGE_AXIS_COUNT-1)
     * @param chart The target JFreeChart to get plot info from
     * @param dAnda the metadata to manage single XYZSeriesCollection (comprised of series) per axis
     * @return a XYZSeriesCollection to use for a graph XY series per axis
     */
    private XYZSeriesCollection getAxisDataset(int axisIndex, JFreeChart chart, DatasetAndAxis[] dAnda) {
        String axisLabel;
        if (this.chartType == LoggerProtocolManager.CT_XY_TIMEDOMAIN) {
            axisLabel = "Range";
        } else {
            axisLabel = "Y";
        }
        if (axisIndex > 0) {
            axisLabel += "-" + (axisIndex + 1);
        }
        
        if (dAnda[axisIndex].datasetObj != null) {
            dAnda[axisIndex].numberAxisObj.setLabel(axisLabel);
            chart.getXYPlot().getRangeAxis(axisIndex).setLabel(axisLabel);
            return dAnda[axisIndex].datasetObj;
        }

        XYZSeriesCollection dataset = new XYZSeriesCollection();
        
        chart.getXYPlot().setDataset(axisIndex, dataset);
        // get the axis def
//        NumberAxis rangeAxis = getTheRangeAxis(axisIndex, dAnda);
        
        Color axisColor = getAxisColor(axisIndex);
        NumberAxis rangeAxis = this.getAxisInstance(axisLabel, axisColor);
        dAnda[axisIndex].numberAxisObj = rangeAxis;

        chart.getXYPlot().setRangeAxis(axisIndex, rangeAxis);

        AxisLocation axloc;
        if (axisIndex % 2 == 0) {
            // even on left
            axloc = AxisLocation.BOTTOM_OR_LEFT;
        } else {
            axloc = AxisLocation.BOTTOM_OR_RIGHT;
        }
        chart.getXYPlot().setRangeAxisLocation(axisIndex, axloc);
        chart.getXYPlot().mapDatasetToRangeAxis(axisIndex, axisIndex);
        chart.getXYPlot().setRangeCrosshairVisible(true);
      
        // assign back into the passed array
        dAnda[axisIndex].datasetObj = dataset;
        
        return dataset;
    }

    /**
     * adds XYSeries and maps axis/dataseries from datasetsAndAxes[] as per
     * pre-defined this.seriesDefs[] set in the setSeries() method. setSeries() does the creation of the
     * seriesDefs[]. Creates the datasets per axisID one-to-one using defs in seriesDefs[] 
     * 
     * addDataSets(getChart(), false);
     * 
     * @param chart
     *            the JFreeChart
     * @param spawnable
     *            true to reassign axis labels (use if a spawn)
     */
    private void addDataSets(JFreeChart chart, boolean spawnable) { 
        DatasetAndAxis[] tempDandA;
        String[] axisLabels = null;

        // if flagged as a copy, make new DatasetAndAxis[]
        if (spawnable) {
            tempDandA = initDatasetAndAxisArray();
            // track existing [possible custom] axis labels
            axisLabels = new String[tempDandA.length];
            for (int i = 0; i < tempDandA.length; i++) {
                if (getChart().getXYPlot().getRangeAxis(i) != null) {
                    axisLabels[i] = getChart().getXYPlot().getRangeAxis(i).getLabel();
                }
            }
        } else { // otherwise, use the established instance var
            // set the ref to the instance var to use in in getAxisDataset()
            // method call below
            tempDandA = this.datasetsAndAxes;
        }

        // create a set of renderers for [potential] axes 
        // TODO fix inefficiency by only creating per needed axisIndex based on seriesDefs[]
        XYItemRenderer[] rendererSet = new XYItemRenderer[RANGE_AXIS_COUNT];
        for (int i=0;i<rendererSet.length;i++){
            switch (this.chartType) {
            case LoggerProtocolManager.CT_XY_TIMEDOMAIN:
                rendererSet[i] = getTimeSeriesRenderer(i);
                break;
            case LoggerProtocolManager.CT_XY_SCATTER:
                rendererSet[i] = getScatterRenderer(i);
                break;
            case LoggerProtocolManager.CT_XYZ_BUBBLE:
                rendererSet[i] = getBubbleRenderer(i);
                break;
            default:
                // TODO do we need this?
                rendererSet[i] = getTimeSeriesRenderer(i);
            }
                
            
        }
        
        // create the datasets per axisID one-one. this.seriesDefs[] set in setSeries()
        for (int i = 0; i < this.seriesDefs.length; i++) {
            XYZSeriesCollection xysc = getAxisDataset(this.seriesDefs[i].axisIndex, chart, tempDandA);
            
            // set the renderer per axisIndex
            chart.getXYPlot().setRenderer(this.seriesDefs[i].axisIndex, rendererSet[this.seriesDefs[i].axisIndex]);
            
            // change alpha of bubble plot to show transparency
            if (this.chartType == LoggerProtocolManager.CT_XYZ_BUBBLE) {
                chart.getXYPlot().setForegroundAlpha(0.65f);
            } else {
                chart.getXYPlot().setForegroundAlpha(1.0f);
            }
            
            // change autosort=true for XY timeseries. No sorting screws up performance in mouseManager
            XYSeries xys; 
            if (this.chartType == LoggerProtocolManager.CT_XY_TIMEDOMAIN) {
                xys = new XYSeries(this.seriesDefs[i].seriesLabel, true, true); 
            } else {
                xys = new XYSeries(this.seriesDefs[i].seriesLabel, false, true); 
            }
            xysc.addSeries(xys);
            // assign tooltip generators to each series
            chart.getXYPlot()
                    .getRenderer(this.seriesDefs[i].axisIndex)
                    .setSeriesToolTipGenerator(this.seriesDefs[i].seriesIndex,
                            new StandardXYToolTipGenerator());

        }

        // reassign axis labels if a spawn
        if (spawnable) {
            for (int i = 0; i < tempDandA.length; i++) {
                if (chart.getXYPlot().getRangeAxis(i) != null) {
                    chart.getXYPlot().getRangeAxis(i).setLabel(axisLabels[i]);
                }
            }
        }

        // set colors for series
        int colorIndex = 0;
        for (int i = 0; i < chart.getXYPlot().getDatasetCount(); i++) {
            if (chart.getXYPlot().getDataset(i) == null)
                continue;
            for (int ii = 0; ii < chart.getXYPlot().getDataset(i).getSeriesCount(); ii++) {
                chart.getXYPlot().getRenderer(i).setSeriesPaint(ii, getSeriesColor(colorIndex));
//                getSeriesShape
                chart.getXYPlot().getRenderer(i).setSeriesShape(ii, getSeriesShape(ii));
                colorIndex++;
                chart.getXYPlot().getRenderer(i).setSeriesStroke(ii,
                                new BasicStroke(NORMAL_SERIES_LINE_WEIGHT,
                                        BasicStroke.CAP_BUTT,
                                        BasicStroke.JOIN_BEVEL));
            }
        }
    }
    
    
    /**
     * Return a baseline, initialized chart object as per desired type
     * @return a chart
     */
    //abstract JFreeChart getBaselineChart();
    JFreeChart getBaselineChart(){
        // create the plot object to pass to the chart
        XYPlot plot = new XYPlot(); //this.dataset[0], domainAxis, getRangeAxis(1), renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        
        // set the domain axis
        NumberAxis domainAxis = getAxisInstance("Time (ms)", Color.black);
        plot.setDomainAxis(domainAxis);
        
        plot.setDomainPannable(true);
        plot.setDomainCrosshairVisible(true); 
        plot.setRangePannable(true);
        plot.setDataset(new XYZSeriesCollection());
        
        // create and return the chart        
        JFreeChart chart = new JFreeChart("", new Font("Arial", Font.BOLD, 14), plot, true);       
        chart.setNotify(false);
        chart.setBackgroundPaint(Color.white);
        chart.getLegend().setPosition(RectangleEdge.RIGHT); 
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 10));
        
        return chart;
    }
    
    
    /**
     * Spawn a copy of the current chart in a new window.
     * 
     * @return true for success
     * @throws OutOfMemoryError
     */
    boolean spawnChartCopy() throws OutOfMemoryError {
        if (this.emptyChart) return false;
        dbg("Constructing chart clone: \"" + getChart().getTitle().getText() + "\"..");
        JFreeChart chartClone=getBaselineChart();
        int pointCounter=0;
        
        //adds XYSeries and maps axis/dataseries as per seriesDefs[]
        addDataSets(chartClone, true);
        
        XYSeries seriesClone=null;
        XYItemRenderer ir;
        for (int i=0;i<chartClone.getXYPlot().getDatasetCount();i++) {
            if (chartClone.getXYPlot().getDataset(i)==null) continue;
            ir=getChart().getXYPlot().getRenderer(i);
            
            int seriesCount=chartClone.getXYPlot().getDataset(i).getSeriesCount();
            for (int j=0;j<seriesCount;j++) {
                // hide the series if "hidden" by user through GUI and is a timeseries chart
                if (this.chartType != LoggerProtocolManager.CT_XYZ_BUBBLE) { // TODO cleanup handling of bubble chart
                    XYLineAndShapeRenderer temp_ir = (XYLineAndShapeRenderer) ir;
                    if (temp_ir.getSeriesLinesVisible(j)==null) temp_ir.setSeriesLinesVisible(j,true); // because will return null if never defined through API
                    if (!temp_ir.getSeriesLinesVisible(j).booleanValue()) {
                        chartClone.getXYPlot().getRenderer(i).setSeriesVisible(j, new Boolean(false));
                        dbg("Excluding series \"" + ir.getLegendItem(i,j).getLabel() + "\"");
                        if (!(i==0 && j==0)) continue; //and don't add data if not base dataset (we need it for marker calcs)
                    }
                }
                // dupe the data
                seriesClone=((XYZSeriesCollection)getChart().getXYPlot().getDataset(i)).getSeries(j);
                int seriesLength = seriesClone.getItemCount();
                for (int k=0;k<seriesLength;k++){
                    XYZDataItem xyzCloneItem = (XYZDataItem)seriesClone.getDataItem(k);
                    ((XYZSeriesCollection)chartClone.getXYPlot().getDataset(i)).getSeries(j).add
                        (new XYZDataItem(xyzCloneItem), false);
                    pointCounter++;
                    if (pointCounter%1000==0) { 
                        chartClone.setNotify(true);
                        chartClone.setNotify(false);
                    }
                }
            }
        }

        // hide unused axes
        for (int i = 0; i < chartClone.getXYPlot().getDatasetCount(); i++) {
            if (chartClone.getXYPlot().getDataset(i) == null) {
                continue;
            }
            boolean hasData=false;
            for (int j=0;j<chartClone.getXYPlot().getDataset(i).getSeriesCount();j++) {
                if (((XYZSeriesCollection)chartClone.getXYPlot().getDataset(i)).getSeries(j).getItemCount()>0) {
                    hasData=true;
                    break; // we can break I think because of the assumption of consecutive series IDs?
                }
            }
            if (!hasData) {
                chartClone.getXYPlot().getRangeAxis(i).setVisible(false);
                dbg("Removing unused axis \"" + chartClone.getXYPlot().getRangeAxis(i).getLabel() + "\"");
            }
        }
        
        SpawnChartPanel spawnpanel = new SpawnChartPanel(chartClone, this.chartType);
//        chartClone.setNotify(false);
        SpawnChartFrame frame = new SpawnChartFrame(spawnpanel);
        frame.setIconImage(((JFrame)this.getTopLevelAncestor()).getIconImage()); 
        
//        SpawnChartPanel spawnpanel = new SpawnChartPanel(chartClone);
        if (markerManager.isCommentsVisible()) {
            // add comment markers
            spawnpanel.importMarkers(markerManager.cloneMarkers());
        }
                
        // copy the current title        
        chartClone.getTitle().setText(getChart().getTitle().getText());
        frame.setTitle("Chart: " + getChart().getTitle().getText());
        chartClone.getXYPlot().getDomainAxis().setLabel(getChart().getXYPlot().getDomainAxis().getLabel());
        
        dbg("Cloning complete");
        return true;
    }
    
    /*
     * public BaseXYChart(JFreeChart chart, boolean useBuffer) { super(chart,
     * useBuffer); }
     * 
     * public BaseXYChart(JFreeChart chart, boolean properties, boolean save,
     * boolean print, boolean zoom, boolean tooltips) { super(chart, properties,
     * save, print, zoom, tooltips); }
     * 
     * public BaseXYChart(JFreeChart chart, int width, int height, int
     * minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int
     * maximumDrawHeight, boolean useBuffer, boolean properties, boolean save,
     * boolean print, boolean zoom, boolean tooltips) { super(chart, width,
     * height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth,
     * maximumDrawHeight, useBuffer, properties, save, print, zoom, tooltips); }
     * 
     * public BaseXYChart(JFreeChart chart, int width, int height, int
     * minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int
     * maximumDrawHeight, boolean useBuffer, boolean properties, boolean copy,
     * boolean save, boolean print, boolean zoom, boolean tooltips) {
     * super(chart, width, height, minimumDrawWidth, minimumDrawHeight,
     * maximumDrawWidth, maximumDrawHeight, useBuffer, properties, copy, save,
     * print, zoom, tooltips); }
     */
    void setDomainScrolling(boolean scrollDomain) {
        this.scrollDomain = scrollDomain;
    }

    void setChartDirty() {
        getChart().setNotify(true);
        this.chartDirty = true;
    }

    boolean isEmptyChart() {
        return this.emptyChart;
    }

    /**
     * 
     * @return If any data has been added to the data series (using [timestamp] series 0)
     */
    boolean hasData() {
        boolean retval = false;
        if (getChart().getXYPlot().getDataset() != null) {
            try {
//                dbg("series 0 item count=" + getChart().getXYPlot().getDataset().getItemCount(0));
                retval = (getChart().getXYPlot().getDataset().getItemCount(0) > 0);
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }
        return retval;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouseManager.doMouseMoved(e)) {
            super.mouseMoved(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseManager.doMouseDragged(e)) {
            super.mouseDragged(e);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (mouseManager.doMouseClicked(e)) {
            super.mouseClicked(e);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        synchronized (lockObj1) {
            try {
                super.paintComponent(g);
            } catch (NullPointerException e) {
                // ignore
            }
        }
    }

    void doWait(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // Thread.currentThread().interrupt();
        }
    }

    /**
     * Set the passed series/header definitions as new XYseries in the dataset.
     * Existing series are wiped. Must be at least two items in the array or any
     * existing series is left intact and method exits with 0. First item
     * (series 0) is set as domain label and should always be system [relative] time in
     * milliseconds, and is always axis 0 for multi-axis charts.
     * <p>
     * For X-Y scatter, 2 seriesNames for each dataset, first is X, second is Y. Timestamp (column 0, series Names[0])
     * is not displayed but TODO may be used in series DataItem comment. NXT-side client appends "-X" and "-Y" to 
     * seriesNames from setColumns() call which should be stripped here to set the series name [taken from the 
     * X column name for the series] (see {@link LoggerProtocolManager#CT_XY_TIMEDOMAIN}, etc.)
     * 
     * <p>
     * The string format/structure of each string field is:<br>
     * <code>[name]:[axis ID 1-4]</code> <br>
     * i.e.
     * 
     * <pre>
     * &quot;MySeries:1&quot;
     * </pre>
     * 
     * @param seriesNames Array of series names
     * @param chartType The chart type. 
     * @return The number of series created
     * @see LoggerProtocolManager#CT_XY_POLAR
     * @see LoggerProtocolManager#CT_XY_SCATTER
     * @see LoggerProtocolManager#CT_XY_TIMEDOMAIN
     * @see LoggerProtocolManager#CT_XYZ_BUBBLE
     */
    int setSeries(String[] seriesNames, int chartType) { 
        // don't allow empty range (length==1 means only domain series
        // (timestamp) defined)
//        System.out.println("*** Chart type=" + chartType);
        if (seriesNames.length < 2) {
            return 0;
        }
        
        // set the chart type to use for setting data points correctly in seriesCollections, etc.
        this.chartType = chartType;
        mouseManager.setChartType(chartType);

        // ************* Clear/init any series, etc.
        // init a new instance datasets and axes holding object of length RANGE_AXIS_COUNT
        this.datasetsAndAxes = initDatasetAndAxisArray();

        // clear all series in all datasets (i.e. axes) and set all axes to axis 0
        int dsCount = getChart().getXYPlot().getDatasetCount(); // should always be 4 or less as per RANGE_AXIS_COUNT
        for (int i = 0; i < dsCount; i++) {
            getChart().getXYPlot().mapDatasetToRangeAxis(i, 0);
            getChart().getXYPlot().setRangeAxis(i, null);
            // delete all series in the dataset if exists (clear the chart)
            XYDataset ds = getChart().getXYPlot().getDataset(i);
            if (ds != null) {
                ((XYZSeriesCollection)ds).removeAllSeries();
            }
        }
        // used to disable zoom extent calc when there is no data
        this.emptyChart = true;
        // remove any measuring markers and clear any comments
        this.markerManager.measureToolsOff();
        this.markerManager.clearComments();
        // int yExtents so extents will be calculated for new data
        //resetYExtents();

        // remove domain def (series 0 => milliseconds) from series def string array
        String[] fields;
        String[] theSeries;
        
        // create series as per chartType
        int seriesOffset = 1;
        String domainTimeseriesAxisLabel = "X"; 
        switch (this.chartType) {
        case LoggerProtocolManager.CT_XY_SCATTER:
            seriesOffset = 2;
            break;
        case LoggerProtocolManager.CT_XYZ_BUBBLE:
            seriesOffset = 3;
            break;
        case LoggerProtocolManager.CT_XY_TIMEDOMAIN:
        default:
            // Parse and set the domain (X => milliseconds) label
            fields = seriesNames[0].split(":");
            domainTimeseriesAxisLabel = fields[0];
        }
        // set length of series name array based on chart type
        theSeries = new String[(seriesNames.length - 1) / seriesOffset];
        int tempIndex = 0;
        // skip element 0 (timestamp)
        for (int i=1;i<seriesNames.length;i=i+seriesOffset){
            try {
                theSeries[tempIndex++] = seriesNames[i]; 
            } catch (ArrayIndexOutOfBoundsException e) {
//                dbg("setSeries(): ArrayIndexOutOfBoundsException caught. Most likely " +
//                		"a column count mismatch per chartType (" + this.chartType +"). " +
//        				"Columns after index " + i + " are ignored.");
                break;
            }
        }
        
//        System.arraycopy(seriesNames, 1, theSeries, 0, theSeries.length);

        getChart().getXYPlot().getDomainAxis().setLabel(domainTimeseriesAxisLabel); 
        // ************ End of clear/init

        // create seriesDefs. This is metadata used to construct the chart object specifics in addDataSets() 
        this.seriesDefs = new SeriesDef[theSeries.length];

        // used as a consecutive series counter contextual for each axis id
        int[] axisSeriesIndex = new int[RANGE_AXIS_COUNT];
        for (int i = 0; i < RANGE_AXIS_COUNT; i++) {
            axisSeriesIndex[i] = 0;
        }
        
        // fill the seriesDefs array
        int minAxisId = RANGE_AXIS_COUNT;
        for (int i = 0; i < theSeries.length; i++) {
            
            
            this.seriesDefs[i] = new SeriesDef();
            fields = theSeries[i].split(":");
//            dbg("fields[0]=" + fields[0]);
            this.seriesDefs[i].seriesLabel = parseOutXYZ(fields[0]); // TODO may need to process by 2 to get both X and Y labels depending on chart type
            
            // if no axis and chartable attributes defined in series def string, and not a timeseries charttype,
            // use default axis index
            if (fields.length == 1 || this.chartType != LoggerProtocolManager.CT_XY_TIMEDOMAIN) {
                this.seriesDefs[i].axisIndex = 0;
            } else {
                try {
                    this.seriesDefs[i].axisIndex = Integer.valueOf(fields[1])
                            .intValue() - 1; // 1-based label [from datalogger
                                             // header] to zero-based internal
                    // / check and force if violated
                    if (this.seriesDefs[i].axisIndex < 0 || seriesDefs[i].axisIndex > 3) {
                        this.seriesDefs[i].axisIndex = 0;
                    }
                } catch (Exception e) {
                    this.seriesDefs[i].axisIndex = 0;
                }
            }
            // need to ensure that dataset(0) is always created (TODO JFreeChart is unhappy if not? KPT 9.14.13)
            // shift all series defs to ensure zero-based dataset
            if (this.seriesDefs[i].axisIndex < minAxisId)
                minAxisId = this.seriesDefs[i].axisIndex;

            // bump series index per axis (contiguous)
            this.seriesDefs[i].seriesIndex = axisSeriesIndex[this.seriesDefs[i].axisIndex]++;
        }

        // We need to ensure that dataset(0) is always created so a shift offset is used
        for (int i = 0; i < this.seriesDefs.length; i++) {
            this.seriesDefs[i].axisIndex -= (minAxisId); // [potentially] shift all
                                                    // axisIDs down to ensure we
                                                    // have a axisID=0
        }

        // create the datasets (XYZSeriesCollections) per axisID one-to-one using defs in seriesDefs[]
        addDataSets(getChart(), false);
        chartDirty = true;

        return getChart().getXYPlot().getSeriesCount();
    }
    
    private String parseOutXYZ(String fieldName){
        if (fieldName.substring(fieldName.length()-2).matches("-([xX]|[yY]|[zZ])")){
            return fieldName.substring(0, fieldName.length()-2);
        }
        return fieldName;
    }
    
    void dbg(String msg) {
        System.out.println("BaseXYChart" + "-" + msg);
    }

    /**Add series data to the dataset. Pass a double array of series values that all share the same domain value (element 0). 
     * The number of values must match the header count in setSeries().
     * <p>
     * Element 0 is the timestamp and should be the domain (X) series for {@link LoggerProtocolManager#CT_XY_TIMEDOMAIN} 
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
        
        int seriesOffset = 1;
        switch (this.chartType) {
        case LoggerProtocolManager.CT_XY_SCATTER:
            seriesOffset = 2;
            break;
        case LoggerProtocolManager.CT_XYZ_BUBBLE:
            seriesOffset = 3;
            break;
        case LoggerProtocolManager.CT_XY_TIMEDOMAIN:
        default:
        }
        
        synchronized (lockObj1) {
            int tempIndex = 0;
            for (int i=1;i<seriesData.length;i=i+seriesOffset) { // 1-based to ignore the ms value that is the first element/column sent
                XYZDataItem dataItem;
                
                try {tempSeries=((XYZSeriesCollection)plot.getDataset(this.seriesDefs[tempIndex].axisIndex))
                        .getSeries(this.seriesDefs[tempIndex].seriesIndex);
                } catch (ArrayIndexOutOfBoundsException e) {
                    // ignore the rest of the data. This state will happen when the same thing happens in setSeries()
//                    dbg("addDataPoints(): ArrayIndexOutOfBoundsException caught. Most likely " +
//                            "a column count mismatch per chartType (" + this.chartType +"). " +
//                            "Columns after index " + i + " are ignored.");
                    break;
                }
                tempIndex++;
                switch (this.chartType) {
                case LoggerProtocolManager.CT_XY_SCATTER:
                    dataItem = new XYZDataItem(seriesData[i], seriesData[i+1], 0); 
                    break;
                case LoggerProtocolManager.CT_XYZ_BUBBLE:
                    dataItem = new XYZDataItem(seriesData[i], seriesData[i+1], seriesData[i+2]); 
                    break;
                case LoggerProtocolManager.CT_XY_TIMEDOMAIN:
                default:
                    dataItem = new XYZDataItem(seriesData[0], seriesData[i], 0); 
                }
                
//                tempSeries=((XYZSeriesCollection)plot.getDataset(this.seriesDefs[i-1].axisIndex)).getSeries(this.seriesDefs[i-1].seriesIndex);
                // TODO change below for Time series , XY Scatter, etc.
//                XYZDataItem dataItem = new XYZDataItem(seriesData[0], seriesData[i], -99.8); // TODO use real Z
                tempSeries.add(dataItem, false);
            }
        }
        
        // the updater thread picks this up every xxx ms and has the JFreeChart do it's notifications for rendering, 
        // etc. The domain axis is also scrolled for incoming data in this thread 
        this.chartDirty=true;
        
        if (this.emptyChart) this.emptyChart=false;
    }

    private NumberAxis getAxisInstance(String axisLabel, Color axisColor) {

        NumberAxis workingAxis;
        workingAxis = new NumberAxis(axisLabel);
        workingAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 9));
        workingAxis.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        workingAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        workingAxis.setTickLabelsVisible(true);
        workingAxis.setLowerMargin(.01);
        workingAxis.setUpperMargin(.01);
        workingAxis.setAutoRange(false);
        workingAxis.setAutoRangeIncludesZero(false);

        // set colors
        workingAxis.setLabelPaint(axisColor);
        workingAxis.setAxisLinePaint(axisColor);
        workingAxis.setLabelPaint(axisColor);
        workingAxis.setTickLabelPaint(axisColor);
        workingAxis.setTickMarkPaint(axisColor);

        return workingAxis;
    }

    /**
     * Instantiate a XYLineAndShapeRenderer. One per axis is the intended use. Color is determined by axisIndex
     * with getAxisColor().
     * 
     * @param axisIndex
     * @return the renderer
     */
    private XYLineAndShapeRenderer getTimeSeriesRenderer(int axisIndex) {
        Color seriesColor = getAxisColor(axisIndex);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        setBaseRenderer(renderer, seriesColor);
        renderer.setBaseShapesVisible(false);
        return renderer;
    }

    private XYLineAndShapeRenderer getScatterRenderer(int axisIndex){
        XYLineAndShapeRenderer renderer = getTimeSeriesRenderer(axisIndex);
        renderer.setUseOutlinePaint(true);
        renderer.setBaseShapesVisible(true); 
        renderer.setBaseLinesVisible(false);
        return renderer;
    }
    
    private XYBubbleRenderer getBubbleRenderer(int axisIndex) {
        Color seriesColor = getAxisColor(axisIndex);
        XYBubbleRenderer renderer = new XYBubbleRenderer(XYBubbleRenderer.SCALE_ON_DOMAIN_AXIS);
        setBaseRenderer(renderer, seriesColor);
        renderer.setBaseOutlinePaint(Color.DARK_GRAY);
        return renderer;
    }

    private void setBaseRenderer(AbstractRenderer renderer, Color seriesColor) {
        // set the renderer
        renderer.setBaseStroke(new BasicStroke(NORMAL_SERIES_LINE_WEIGHT,
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        renderer.clearSeriesPaints(true);
        renderer.setAutoPopulateSeriesPaint(false); 
        renderer.setAutoPopulateSeriesShape(false);
        if (seriesColor == null) {
            seriesColor = Color.black;
        }
        renderer.setSeriesOutlinePaint(0, seriesColor);
//        renderer.setSeriesOutlinePaint(1, seriesColor);
        renderer.setBaseLegendTextPaint(seriesColor);
    }

    void addCommentMarker(double xVal, String comment) {
        if (this.markerManager == null)
            return;
        this.markerManager.addCommentMarker(xVal, comment);
    }

    void setCommentsVisible(boolean visible) {
        if (this.markerManager == null)
            return;
        this.markerManager.setCommentsVisible(visible);
    }

    Color getAxisColor(int index) {
        switch (index) {
        case 0:
            return Color.BLACK;
        case 1:
            return Color.MAGENTA.darker();
        case 2:
            return Color.BLUE;
        case 3:
            return Color.RED;
        }
        return Color.BLACK;
    }

    Color getSeriesColor(int index) {
        if (index > 10)
            index = index % 11;
        switch (index) {
        case 0:
            return Color.BLUE.brighter().brighter();
        case 1:
            return Color.RED.brighter();
        case 2:
            return Color.GREEN.darker();
        case 3:
            return Color.CYAN.darker();
        case 4:
            return Color.BLUE.darker().darker();
        case 5:
            return Color.DARK_GRAY;
        case 6:
            return Color.MAGENTA;
        case 7:
            return Color.ORANGE.darker();
        case 8:
            return Color.CYAN.darker().darker();
        case 9:
            return Color.BLACK.brighter();
        case 10:
            return Color.RED.darker();
        }
        return Color.LIGHT_GRAY;
    }
    
    private Shape getSeriesShape(int index) {
        if (!CheckChartAndPlotRef()) return null;
        // init cache array of shapes to use
        if (this.seriesShapes[0] == null) {
            DefaultDrawingSupplier ds = new DefaultDrawingSupplier();
            for (int i=0;i<10;i++) {
                seriesShapes[i] = ds.getNextShape();
            }
        }
        if (index > 10) index = index % 11;
        return seriesShapes[index];
    }
    
    /**
     * TODO define axis meaning and how many per chart type.
     * 
     * @param axisIndex The axis index you want the label from
     * @return The label for given index. null if not exist.
     */
    String getAxisLabel(int axisIndex){
        if (!CheckChartAndPlotRef()) return null;
        ValueAxis v3;
        v3 = getChart().getXYPlot().getRangeAxis(axisIndex);
        if (v3==null) return null;
        return v3.getLabel();
    }

    /**
     * @param axisIndex The axis index to set the label for
     * @param axisLabel The label
     */
    void setAxisLabel(int axisIndex, String axisLabel){ 
        if (!CheckChartAndPlotRef()) return;
        ValueAxis v3;
        v3 = getChart().getXYPlot().getRangeAxis(axisIndex);
        if (v3==null) return;
        v3.setLabel(axisLabel);
        getChart().setNotify(true);
    }
    
    /**
     * Set the chart title.
     * 
     * @param title
     */
    void setChartTitle(String title) {
        getChart().setTitle(title); 
        getChart().setNotify(true);
    }
    
    /**
     * @return The chart's main title
     */
    String getChartTitle() {
        return getChart().getTitle().getText(); 
    }
    
    boolean axisExists(int axisIndex){
        if (!CheckChartAndPlotRef()) return false;
        return getChart().getXYPlot().getRangeAxis(axisIndex)!=null;
    }
    
    void registerListeners(ChartModel customChartPanel){
//        if (!CheckChartAndPlotRef()) {
////            throw new ;
//            return;
//        }
        getChart().getXYPlot().getDomainAxis().addChangeListener(customChartPanel);
        getChart().addProgressListener(customChartPanel);
        getChart().addChangeListener(customChartPanel); // to capture dataset changes to populate row count
    }
    
    /**
     * @return True if chart and xyplot are valid refs (not null)
     */
    private boolean CheckChartAndPlotRef(){
        JFreeChart c = getChart();
        if (c==null) return false;
        if (c.getXYPlot()==null) return false;
        return true;
    }
}