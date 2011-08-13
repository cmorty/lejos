package lejos.pc.charting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;


/** JFreeChart object in a ChartPanel. Includes methods to add titles (headers), series datapoints, and change domain scaling
 * @author Kirk P. Thompson
 */
class LoggingChart extends ChartPanel{
    private final String THISCLASS;
    private static final float DOMAIN_SIZE_MULT=1.01f; // provides a small gap between series and chart right,left edges on domain axis
    private static final int MAX_SCALE=10000; // percentage as scaled int
    private static final int INIT_DOMAIN_WIDTH=10000; 
    private static final int RANGE_AXIS_COUNT=4;
    private  static final int REFRESH_FREQUENCY_MS=500; // used by update thread 
    
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
    private XYSeriesCollection[] dataset = new XYSeriesCollection[RANGE_AXIS_COUNT];
    private NumberAxis[] rangeAxis = new NumberAxis[RANGE_AXIS_COUNT];
    private boolean chartDirty = false; // used by update thread to flag an event notification for repaints
    private boolean doDomainAdjust=false;
    private double domainWidth=INIT_DOMAIN_WIDTH; // milli seconds defining how big to initially  make the domain scale (for sliding it)
    private SeriesDef[] seriesDefs; 
    private Object lockObj1 = new Object(); 
    private int domainAxisLimitMode=DAL_UNLIMITED;
    private int domainAxisLimitValue=0;
    private boolean emptyChart=true;
    private Range[] yExtents=new Range[RANGE_AXIS_COUNT];
    private MarkerManager markerManager=null;
    
    private class SeriesDef {
        String label;
        int axisIndex; // cooresponds to dataset(index). could be sparse
        int seriesIndex; //
    }

    /** Allows user control of chart series visibility and series highlight on mouseover
     */
    private class MyChartMouseListener implements ChartMouseListener{
        private class DataIndexes{
            int datasetSlashAxisIndex;
            int seriesIndex;
        }
        private DataIndexes dataIndexs = new DataIndexes();
      
//        private int getSeriesDefsIndex(ChartEntity ce){
//            LegendItemEntity lie= (LegendItemEntity)ce;
//            for (int i=0;i<seriesDefs.length;i++){
//                if (seriesDefs[i].label.equals(lie.getSeriesKey().toString())) return i;
//            }
//            return -1;
//        }
        
        private XYSeries getXYSeriesForEntity(ChartEntity ce) {
            Comparable name=((LegendItemEntity)ce).getSeriesKey();
            XYPlot plot = getChart().getXYPlot();
            for (int i=0;i<plot.getDatasetCount();i++){
                for (int j=0;j<plot.getDataset(i).getSeriesCount();j++){
                    if (name.compareTo(plot.getDataset(i).getSeriesKey(j))==0) {
                        this.dataIndexs.datasetSlashAxisIndex=i;
                        this.dataIndexs.seriesIndex=j;
                        return ((XYSeriesCollection)plot.getDataset(i)).getSeries(j);
                    }
                }
            }
            return null;
        }
        
        // assumes DataIndexes is set by getXYSeriesForEntity()
        private boolean setStroke(float desiredWidth) {
            XYItemRenderer ir=getChart().getXYPlot().getRenderer(this.dataIndexs.datasetSlashAxisIndex);
            if (ir==null) return false;
            try {
                if (((BasicStroke)ir.getSeriesStroke(this.dataIndexs.seriesIndex)).getLineWidth()!=desiredWidth) {
                    ir.setSeriesStroke(this.dataIndexs.seriesIndex, 
                        new BasicStroke(desiredWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
                    getChart().setNotify(true);
                }
            } catch (NullPointerException e) {
                return false;
            }
            return true;
        }
        
        // assumes DataIndexes is set by getXYSeriesForEntity()
        private void toggleSeriesVisible() {
            XYItemRenderer ir=getChart().getXYPlot().getRenderer(this.dataIndexs.datasetSlashAxisIndex);
//            XYItemRenderer ir=getChart().getXYPlot().getRenderer(seriesDefIndex);
            XYLineAndShapeRenderer lsr = (XYLineAndShapeRenderer)ir;
            Boolean vis = lsr.getSeriesLinesVisible(this.dataIndexs.seriesIndex);
            lsr.setSeriesLinesVisible(this.dataIndexs.seriesIndex, vis==null?false:!vis.booleanValue());
        }
        
        public void chartMouseClicked(ChartMouseEvent event) {
            ChartEntity ce = event.getEntity();
            if (ce instanceof LegendItemEntity) {
                getXYSeriesForEntity(ce); // set DataIndexes.blah 
                toggleSeriesVisible();
            }
        }

        public void chartMouseMoved(ChartMouseEvent event) {
            ChartEntity ce = event.getEntity();
            if (ce==null || seriesDefs==null) return;
    
            if (ce instanceof LegendItemEntity) {
                if (getXYSeriesForEntity(ce)==null) return;
                LoggingChart.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                if (!setStroke(1.5f)) {
                    LoggingChart.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
            } else {
                LoggingChart.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                this.dataIndexs.seriesIndex=-1;
                this.dataIndexs.datasetSlashAxisIndex=-1;
            }
            
            XYPlot plot = getChart().getXYPlot();
            int datasetSlashAxisIndex = this.dataIndexs.datasetSlashAxisIndex;
            int seriesIndex = this.dataIndexs.seriesIndex;
            for (int i=0;i<plot.getDatasetCount();i++){
                for (int j=0;j<plot.getDataset(i).getSeriesCount();j++) {
                    if (datasetSlashAxisIndex==i && seriesIndex==j) continue; 
                    this.dataIndexs.datasetSlashAxisIndex=i;
                    this.dataIndexs.seriesIndex=j;
                    setStroke(.5f);
                }
            }
        }
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
        
        // chart updater, clipping, and domain scroller thread
        final Thread t1 = new Thread(new Runnable(){
            public void run() {
                while (true) {
                    doWait(REFRESH_FREQUENCY_MS);
                   
                    if (chartDirty && LoggingChart.this.getChart()!=null) {
                        // slide the the domain range to show animation of data coming in
                        XYPlot plot = getChart().getXYPlot();
                        if (doDomainAdjust && plot.getDataset().getSeriesCount()>0) {
                                double maxXVal = ((XYSeriesCollection)plot.getDataset()).getSeries(0).getMaxX();
                                // sets this.domainRange
                                setDomainRange( new Range(maxXVal-LoggingChart.this.domainWidth, maxXVal));
                                // ensure value (range) axis displays extents of data as it scrolls
                                doRangeExtents(false);
                                
                                // if we have a clipping mode set...
                                if (LoggingChart.this.domainAxisLimitMode!=DAL_UNLIMITED) {
                                    // coordinate with addDataPoints to prevent it from adding whilst we remove
                                    int length=((XYSeriesCollection)plot.getDataset()).getItemCount(0);
                                    int endIndex=0;
                                    synchblock1: synchronized (lockObj1) {
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
                                            for (int i=0;i<plot.getDatasetCount();i++){
                                                for (int j=0;j<plot.getDataset(i).getSeriesCount();j++) {
                                                    ((XYSeriesCollection)plot.getDataset(i)).getSeries(j).delete(0,endIndex-1);
                                                }
                                            }
                                            LoggingChart.this.getChart().setNotify(true);
                                        }
                                    }
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
        t1.start();
        
        // Set a listener to set the width of the x-axis (domain) scale instance var this.domainWidth due to a chart resize event
        setAxisChangeListener();
        // set the double-click to restore zoom to extents
        setMouseListener();
        
        this.addChartMouseListener(new MyChartMouseListener());
    }
    
    public void mouseMoved(MouseEvent e) {
        if (!e.isShiftDown()) {
            super.mouseMoved(e);
            return;
        }
        if (this.markerManager!=null) this.markerManager.mouseMoved(e);
    }
    
    public void mouseDragged(MouseEvent e) {
        if (!e.isShiftDown()) {
            // override to only allow left button to drag
            if ((e.getModifiersEx()&e.BUTTON1_DOWN_MASK)==e.BUTTON1_DOWN_MASK) super.mouseDragged(e);
            return;
        }
    }
    
    public void mouseClicked(MouseEvent e) {
        if (!e.isShiftDown()&&!e.isControlDown()) {
            super.mouseClicked(e);
            return;
        }
        if (this.markerManager!=null) this.markerManager.mouseClicked(e);
    }
    
    /**
     * @param forceExtents true to always do a setRange. false to only do one if extents expand
     */
    private synchronized void doRangeExtents(boolean forceExtents) {
        // ensure value (range) axis displays extents of data as it scrolls
        // dataset cooresponds to axis one-to-one
        Range yRange=null;
        XYPlot plot = getChart().getXYPlot();
        for (int i=0;i<plot.getDatasetCount();i++){
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
    
    public void paintComponent(Graphics g) {
        synchronized (lockObj1) {
            try {
                super.paintComponent(g);
            } catch (NullPointerException e) {
                ; // ignore
            }
        }
    }
    
    private synchronized void setDomainRange(Range range){
        if (range==null) return;
            this.domainRange=range;
            getChart().getXYPlot().getDomainAxis().setRange(range);
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
        chartDirty=true;
        this.markerManager = new MarkerManager(this);
        setVisible(true);
    }

    private void dbg(String msg) {
        System.out.println(THISCLASS + "-" + msg);
    }


    private NumberAxis getRangeAxis(int index) {
        if (this.rangeAxis[index]!=null) {
            return this.rangeAxis[index];
        }
        
        // create if doesn't exist
        NumberAxis rangeAxis;
        Color axisColor=getAxisColor(index);
        String axisLabel="Range";
        if (index>0) axisLabel+="-" + (index+1);
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

        this.rangeAxis[index]=rangeAxis;
        
        return rangeAxis;
    }
    
    private Color getAxisColor(int index) {
        switch(index) {
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
    
    private Color getSeriesColor(int index){
        if (index>10) index=index%11;
        switch(index) {
            case 0:
                return Color.BLUE.brighter().brighter();
            case 1:
                return Color.RED.brighter();
            case 2:
                return Color.GREEN.darker();
            case 3:
                return Color.CYAN.darker();
            case 4:
                return Color.BLUE;
            case 5:
                return Color.DARK_GRAY;
            case 6:
                return Color.MAGENTA;
            case 7:
                return Color.CYAN;
            case 8:
                return Color.YELLOW.darker();
            case 9:
                return Color.GREEN;
            case 10:
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
        
//        CrosshairOverlay cho = new CrosshairOverlay();
//        cho.addDomainCrosshair(new Crosshair(5000));
//        this.addOverlay(cho);
//        this.setHorizontalAxisTrace(true);

//                Marker cooling = new IntervalMarker(500, 1001);
//                cooling.setLabelOffsetType(LengthAdjustmentType.EXPAND);
//                cooling.setPaint(new Color(150, 150, 255));
//                cooling.setLabel("Automatic Cooling");
//                cooling.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
//                cooling.setLabelPaint(Color.blue);
//                cooling.setLabelAnchor(RectangleAnchor.TOP_LEFT);
//                cooling.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
//                plot.addDomainMarker(cooling, Layer.BACKGROUND);

//                Marker coolingStart = new ValueMarker(millis1, Color.blue,
//                        new BasicStroke(2.0f));
//                Marker coolingEnd = new ValueMarker(millis2, Color.blue,
//                        new BasicStroke(2.0f));
//                plot.addDomainMarker(coolingStart, Layer.BACKGROUND);
//                plot.addDomainMarker(coolingEnd, Layer.BACKGROUND);
                
                
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

    private XYSeriesCollection getAxisDataset(int axisIndex){
        if (this.dataset[axisIndex]!=null) {
            //System.out.println("getAxisDataset found axisIndex " + axisIndex);
            return this.dataset[axisIndex];
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        // set the renderer
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        renderer.setBaseStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_BEVEL)); 
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        renderer.setBaseLegendTextPaint(getAxisColor(axisIndex));
        renderer.setBaseShapesVisible(false);
        renderer.clearSeriesPaints(true);
        renderer.setAutoPopulateSeriesPaint(false);
        
//        System.out.println(getChart().getXYPlot());
        getChart().getXYPlot().setDataset(axisIndex, dataset);
        getChart().getXYPlot().setRenderer(axisIndex, renderer);
        // get the  axis def
        getChart().getXYPlot().setRangeAxis(axisIndex, getRangeAxis(axisIndex));
        AxisLocation axloc;
        if (axisIndex%2==0) { 
            // even on left
            axloc=AxisLocation.BOTTOM_OR_LEFT;
        } else {
            axloc=AxisLocation.BOTTOM_OR_RIGHT; 
        }
        getChart().getXYPlot().setRangeAxisLocation(axisIndex, axloc);
        getChart().getXYPlot().mapDatasetToRangeAxis(axisIndex, axisIndex);
        getChart().getXYPlot().setRangeCrosshairVisible(true);
        
        this.dataset[axisIndex]=dataset;
        return dataset; 
    }
    
    private void spawnChartCopy(){
        if (this.emptyChart) return;
        JFrame frame = new ChartDisplay();
        JFreeChart chartClone;
        try {
            chartClone = (JFreeChart)getChart().clone();
            // clear all series in all datasets/axes and set all axes to axis 0
            int dsCount=getChart().getXYPlot().getDatasetCount();
            for (int i=0;i<dsCount;i++) {
                if (getChart().getXYPlot().getDataset(i)!=null) {
                    chartClone.getXYPlot().setDataset(i, (XYSeriesCollection)((XYSeriesCollection)getChart().getXYPlot().getDataset(i)).clone());
                }
            }
            
        } catch (CloneNotSupportedException e) {
            // TODO
            return;
        }
        ((ChartDisplay)frame).setChart(chartClone); 
        Dimension frameSize = frame.getSize();
        // Toolkit.getDefaultToolkit().getScreenSize() doesn't work for multi monitor setups
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        frame.setLocation(center.x - frameSize.width / 2, 
                          center.y - frameSize.height / 2);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        frame.addWindowListener(myWindowListener);
        frame.pack();
        frame.setVisible(true);
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
        // TODO clone existing chart and put it in a new frame and display before we whack the dataset
         // spawnChartCopy(); Not ready. cloning problems with legend items and listeners
         
        // don't allow empty range (length==1 means only domain series defined)
        if (seriesNames.length<2) return 0;
        
        // clear any series, etc.
        dataset = new XYSeriesCollection[RANGE_AXIS_COUNT];
        rangeAxis = new NumberAxis[RANGE_AXIS_COUNT];
        // clear all series in all datasets/axes and set all axes to axis 0
        int dsCount=getChart().getXYPlot().getDatasetCount();
        for (int i=0;i<dsCount;i++) {
            getChart().getXYPlot().mapDatasetToRangeAxis(i, 0);
            getChart().getXYPlot().setRangeAxis(i,null);
            if (getChart().getXYPlot().getDataset(i)!=null) {
                ((XYSeriesCollection)getChart().getXYPlot().getDataset(i)).removeAllSeries();
            }
        }
        // used to disable zoom extent calc when there is no data
        this.emptyChart=true;
        // remove any markers
        this.markerManager.markersOff();
        // clear so extents will be calculated for new data
        yExtents=new Range[RANGE_AXIS_COUNT];
        
        // remove domain def from series def
        String[] fields;
        String[] theSeries = new String[seriesNames.length-1];
        System.arraycopy(seriesNames, 1, theSeries, 0, theSeries.length);
        fields=seriesNames[0].split(":");
        String domainLabel=fields[0];
        
        // create seriesDefs
        seriesDefs= new SeriesDef[theSeries.length];
       
        // used as a consecutive series counter contextual for each axis id
        int[] axisSeriesIndex = new int[RANGE_AXIS_COUNT]; 
        for (int i=0;i<RANGE_AXIS_COUNT;i++) axisSeriesIndex[i]=0;
        
        // fill the array
        int minAxisId = RANGE_AXIS_COUNT; 
        for (int i=0;i<theSeries.length;i++) {
//            System.out.println(theSeries[i]);
            seriesDefs[i]=new SeriesDef();
            fields=theSeries[i].split(":");
            seriesDefs[i].label=fields[0];
            // if no axis and chartable attributes defined in series def string, use default axis index
            if (fields.length==1) {
                seriesDefs[i].axisIndex=0;
            } else {
                try {
                    seriesDefs[i].axisIndex=Integer.valueOf(fields[1])-1; // 1-based label [from datalogger header] to zero-based internal
                    /// check and force
                    if (seriesDefs[i].axisIndex<0 || seriesDefs[i].axisIndex>3) seriesDefs[i].axisIndex=0;
                } catch (Exception e) {
                    seriesDefs[i].axisIndex=0;
                }
            }
            // need to ensure that dataset(0) is always created
            // shift all series defs to ensure zero-based dataset
            if (seriesDefs[i].axisIndex<minAxisId) minAxisId=seriesDefs[i].axisIndex;
            
            // bump series index per axis (contiguous)
            seriesDefs[i].seriesIndex=axisSeriesIndex[seriesDefs[i].axisIndex]++;
        }
        
        // create the datasets per axisID one-one. We need to ensure that dataset(0) is always created
        // so a shift offset is used
        for (int i=0;i<seriesDefs.length;i++){
            seriesDefs[i].axisIndex-=(minAxisId); // shift all axisIDs down to ensure we have a axisID=0 
            // add series to dataset 
            getAxisDataset(seriesDefs[i].axisIndex).addSeries(new XYSeries(seriesDefs[i].label,true, true));
        }
        
        // set colors for series
        int colorIndex=0;
        for (int i=0;i<getChart().getXYPlot().getDatasetCount();i++){
            if (getChart().getXYPlot().getDataset(i)==null) continue;
            for (int ii=0;ii< getChart().getXYPlot().getDataset(i).getSeriesCount();ii++) {
                getChart().getXYPlot().getRenderer(i).setSeriesPaint(ii,getSeriesColor(colorIndex++));
                getChart().getXYPlot().getRenderer(i).setSeriesStroke(
                    ii,new BasicStroke(.50f, BasicStroke.CAP_BUTT,  BasicStroke.JOIN_BEVEL));
            }
            
        }
        getChart().getXYPlot().getDomainAxis().setLabel(domainLabel); 
        chartDirty=true;
        return getChart().getXYPlot().getSeriesCount();
    }
    

    private void setMouseListener() {
        class ml extends MouseAdapter{
            public void mouseClicked(MouseEvent e) {
                if ((e.getButton()&MouseEvent.BUTTON1)==MouseEvent.BUTTON1) {
                    // doubleclick zooms extents of data
                    if (e.getClickCount()==2) {
                        if (!emptyChart) {
                            LoggingChart.this.restoreAutoDomainBounds();
                            // reset [session] historical range zoom extents if CTRL-Doubleleftclick
                            if (e.isControlDown()) {
                                LoggingChart.this.restoreAutoRangeBounds();
                                // effectively resets the Y range zoom extents
                                yExtents=new Range[RANGE_AXIS_COUNT];
                            }
                            // restore calced Y range bounds for all axes
                            doRangeExtents(true);
                        }
                    }
                }
            }
            public void mouseReleased(MouseEvent e) {
                getChart().setNotify(true);
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
        synchronized (lockObj1) {
            for (int i=1;i<seriesData.length;i++) { // 1-based to ignore ms value of first element/column      
                tempSeries=((XYSeriesCollection)plot.getDataset(seriesDefs[i-1].axisIndex)).getSeries(seriesDefs[i-1].seriesIndex);
                tempSeries.add(seriesData[0], seriesData[i], false);
            }
        }
        // the updater thread picks this up every xxx ms and has the JFreeChart do it's notifications for rendering, 
        // etc. The domain axis is also scrolled for incoming data in this thread via doDomainAdjust=true
        this.chartDirty=true;
        this.doDomainAdjust=true;
        this.emptyChart=false;
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
        if (limitMode<DAL_UNLIMITED||limitMode>DAL_COUNT) return;
        this.domainAxisLimitMode=limitMode;
        this.domainAxisLimitValue=value;
    }
    
    /** Set the width of the x-axis (domain) scale centered around current midpoint of domain scale. Uses a scaled integer 1-1000 
     * (meaning 0.1-100.0). Values outside this 
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
                // set domain width to 10 seconds if new chart (no sereis data and domain is empty)
                if (domainWidth==1.0 && LoggingChart.this.getChart().getXYPlot().getSeriesCount()==0) domainWidth=INIT_DOMAIN_WIDTH;
            }
        });
        
    }
}
