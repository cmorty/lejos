package lejos.pc.charting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Paint;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import lejos.util.Delay;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.XYAnnotationEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


class MouseManager {
    private static final float HIGHLIGHT_SERIES_LINE_WEIGHT = 1.5f;
    private static final float NORMAL_SERIES_LINE_WEIGHT = 0.5f;
    
    private ChartPanel chartPanelObj;
    private JFreeChart jFreeChart;
    private MyChartMouseListener mouseListener;
    private MarkerManager markerManager;
    private int chartType;
    private boolean hasSeriesLines;
    private boolean hasSeriesShapes;
    
    private class MyChartMouseListener implements ChartMouseListener{
        // used for temp storage for state management
        private class DataIndexes{
            int datasetSlashAxisIndex;
            int seriesIndex;
        }
        private DataIndexes dataIndexs = new DataIndexes();
        private HashMap<String, XYToolTipGenerator> ttgs = new HashMap<String, XYToolTipGenerator>();
        private ShapeBlinker sb=null;
        private ChartEntity lastChartEntity;
        
        private class ShapeBlinker implements Runnable{
            private final static int BLINK_DELAY=100;
            
            private XYLineAndShapeRenderer lsr;
            private volatile boolean kill = false;
            private int seriesIndex;

            private Boolean oldVisibility;
            
            public ShapeBlinker(XYLineAndShapeRenderer lsr, int seriesIndex){
                this.seriesIndex = seriesIndex;                
                this.lsr=lsr;
            }
            
            public void run() {
                if (lsr==null){
                    return;
                }
                this.oldVisibility = lsr.getSeriesShapesVisible(seriesIndex);
                        
                // workaround due to getSeriesShapesVisible() returns null if setSeriesShapesVisible() was never called even
                // though shapes are defaulted to true visibility
                if (this.oldVisibility==null){
                    lsr.setSeriesShapesVisible(this.seriesIndex, true);
                    this.oldVisibility = lsr.getSeriesShapesVisible(seriesIndex);
                }
                
                for (int i=0;i<3;i++){
                    lsr.setSeriesShapesVisible(this.seriesIndex, false);
                    jFreeChart.setNotify(true);
                    Delay.msDelay(BLINK_DELAY);
                    if (kill) break;
                    lsr.setSeriesShapesVisible(this.seriesIndex, true);
                    jFreeChart.setNotify(true);
                    Delay.msDelay(BLINK_DELAY);
                    if (kill) break;
                }
                lsr.setSeriesShapesVisible(this.seriesIndex, this.oldVisibility);
                lsr.setSeriesOutlinePaint(seriesIndex, Color.BLACK);
                
                jFreeChart.setNotify(true);
                synchronized (this) {
                    this.notify();
                    kill=true;
                }
            }
            
            public synchronized void halt(){
                if (kill==true) {return;}
                this.kill  = true;
                try {
                    this.wait(BLINK_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private XYSeries getXYSeriesForEntity(ChartEntity ce) {
            String name=(String)((LegendItemEntity)ce).getSeriesKey();
            XYPlot plot = jFreeChart.getXYPlot();
            for (int i=0;i<plot.getDatasetCount();i++){
                if (plot.getDataset(i)==null) continue;
                for (int j=0;j<plot.getDataset(i).getSeriesCount();j++){
                    if (name.compareTo((String)plot.getDataset(i).getSeriesKey(j))==0) {
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
            XYItemRenderer ir=jFreeChart.getXYPlot().getRenderer(this.dataIndexs.datasetSlashAxisIndex);
            if (ir==null) return false;
            try {
                if (((BasicStroke)ir.getSeriesStroke(this.dataIndexs.seriesIndex)).getLineWidth()!=desiredWidth) {
                    ir.setSeriesStroke(this.dataIndexs.seriesIndex, 
                        new BasicStroke(desiredWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
                    jFreeChart.setNotify(true);
                }
            } catch (NullPointerException e) {
                return false;
            }
            return true;
        }
        
        private void setOutlinePaint(Paint paint){
            if (!hasSeriesShapes) {return;}
            XYItemRenderer ir = jFreeChart.getXYPlot().getRenderer(this.dataIndexs.datasetSlashAxisIndex);
            if (ir==null) {return;}
            if (ir.getSeriesOutlinePaint(this.dataIndexs.seriesIndex)!=paint) {
                ir.setSeriesOutlinePaint(this.dataIndexs.seriesIndex, paint);
                jFreeChart.setNotify(true);
            }
            
        }
        
        // assumes DataIndexes is set by getXYSeriesForEntity()
        private void toggleSeriesVisible() {
            String key;
            
            // avoid class cast exception TODO manage bubble chart series somehow
            if (jFreeChart.getXYPlot().getRenderer(this.dataIndexs.datasetSlashAxisIndex) instanceof XYBubbleRenderer) return;
            
            XYLineAndShapeRenderer lsr=(XYLineAndShapeRenderer)(jFreeChart.getXYPlot().getRenderer(this.dataIndexs.datasetSlashAxisIndex));
            Boolean vis = lsr.getSeriesLinesVisible(this.dataIndexs.seriesIndex);
            boolean lineVisible = vis==null?false:!vis.booleanValue();
            Boolean vis2 = lsr.getSeriesShapesVisible(this.dataIndexs.seriesIndex);
            boolean ShapesVisible = vis2==null?false:!vis2.booleanValue();
            if (hasSeriesLines) lsr.setSeriesLinesVisible(this.dataIndexs.seriesIndex, lineVisible);
            if (hasSeriesShapes) {
                if (sb!=null) {
                    sb.halt();
                    sb=null;
                }
                lsr.setSeriesShapesVisible(this.dataIndexs.seriesIndex, ShapesVisible); 
            }
            // effectively disable the series tooltips if not shown by killing them. Harsh but no setVisible() method available
            // 8/21/11: It appears that the renderer, plot, whatever, does not create and/or respect all but one tooltip for multiple series that 
            // overlap/share the same datapoints on the same axis def. Only one tooltip per that coordinate is displayed. The workaround
            // at this point is to put any series with duplicate coordinates on a different range axis to be able to display its 
            // coordinate tooltips.
            if (lineVisible) {
                //XYToolTipGenerator ttg=chartPanelObj.getSeriesTTG(this.dataIndexs.datasetSlashAxisIndex, this.dataIndexs.seriesIndex);
                key = Integer.toString(this.dataIndexs.datasetSlashAxisIndex) + Integer.toString(this.dataIndexs.seriesIndex);
                XYToolTipGenerator ttg = ttgs.get(key);
                if (lsr.getSeriesToolTipGenerator(this.dataIndexs.seriesIndex)==null && ttg!=null) {
                    lsr.setSeriesToolTipGenerator(this.dataIndexs.seriesIndex, ttg);
                }
            } else {
                key = Integer.toString(this.dataIndexs.datasetSlashAxisIndex) + Integer.toString(this.dataIndexs.seriesIndex);
                if (!ttgs.containsKey(key)) {
                    ttgs.put(key, lsr.getSeriesToolTipGenerator(this.dataIndexs.seriesIndex));
                }
                lsr.setSeriesToolTipGenerator(this.dataIndexs.seriesIndex, null);
            }
        }
        
        public void chartMouseClicked(ChartMouseEvent event) {
            ChartEntity ce = event.getEntity();
            if (ce instanceof LegendItemEntity) {
                getXYSeriesForEntity(ce); // set DataIndexes.blah 
                toggleSeriesVisible();
            } else if (ce instanceof XYAnnotationEntity) {// pick up the event markers
                // do toggle of marker text here
                markerManager.toggleLabel(ce);
            }
        }
        
        private Point2D getPointInRectangle(int x, int y, Rectangle2D area) {
            double xx = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
            double yy = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
            return new Point2D.Double(xx, yy);
        }
        
        public void chartMouseMoved(ChartMouseEvent event) {
            ChartEntity ce = event.getEntity();
            
            // exit if no chart or entity
            if (ce==null || isEmptyChart()) return;
            
            // if not the same entity as last mouseover...
            boolean sameEntity = lastChartEntity == ce;
            if (!sameEntity) {
                lastChartEntity = ce; // set for state management
                // kill the blinker if alive
                if (sb!=null) {
                    sb.halt();
                    sb=null;
                }
            }
            MouseEvent mouseEvent=event.getTrigger();
            
            boolean isEventMarker = ce instanceof XYAnnotationEntity;
            boolean isLegend = ce instanceof LegendItemEntity;
            
            // set the appropriate cursor
            if (isLegend || isEventMarker) {
                // exit if not a marker and no legend item ID'ed a series
                if (!isEventMarker && getXYSeriesForEntity(ce)==null) return;
                
                // do not process on bubblechart TODO manage bubble chart series somehow
                if (jFreeChart.getXYPlot().getRenderer(this.dataIndexs.datasetSlashAxisIndex) instanceof XYBubbleRenderer) return;
                
                if (chartPanelObj.getCursor()!=Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)) {
                    chartPanelObj.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                if (!isEventMarker && !setStroke(HIGHLIGHT_SERIES_LINE_WEIGHT)) {
                    chartPanelObj.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
                
            } else {
                if (chartPanelObj.getScreenDataArea().contains(mouseEvent.getX(), mouseEvent.getY())) {
                    if (chartPanelObj.getCursor()!=Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)) {
                        //if (LoggingChart.this.getCursor()==Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
                        chartPanelObj.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR ));
                    }
                } else {
                    if (chartPanelObj.getCursor()!=Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)) {
                        //if (LoggingChart.this.getCursor()==Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
                        chartPanelObj.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR ));
                    }
                }

                // do not process on bubblechart TODO manage bubble chart series somehow
                if (jFreeChart.getXYPlot().getRenderer(this.dataIndexs.datasetSlashAxisIndex) instanceof XYBubbleRenderer) return;
                
                // set to trick below loop to process all series lines
                this.dataIndexs.seriesIndex=-1;
                this.dataIndexs.datasetSlashAxisIndex=-1;
            }
            
            // persist values from "temp storage" into locals
            int datasetSlashAxisIndex = this.dataIndexs.datasetSlashAxisIndex;
            int seriesIndex = this.dataIndexs.seriesIndex;
            XYLineAndShapeRenderer lsr= (XYLineAndShapeRenderer)(jFreeChart.getXYPlot().getRenderer(this.dataIndexs.datasetSlashAxisIndex));
            
            // set the stroke
            XYPlot plot = jFreeChart.getXYPlot();
            for (int i=0;i<plot.getDatasetCount();i++){
                if (plot.getDataset(i)==null) continue;
                for (int j=0;j<plot.getDataset(i).getSeriesCount();j++) {
                    // skip the one we IDed
                    if (datasetSlashAxisIndex==i && seriesIndex==j) {
                        // blink the series if scatter plot
                        if (chartType == LoggerProtocolManager.CT_XY_SCATTER) {
                            if (sb==null) {
                                sb = new ShapeBlinker(lsr, seriesIndex);
                                Thread t1 = new Thread(sb);
                                t1.setDaemon(true);
                                t1.start();
                            }
                        }
                        continue; 
                    }
                    // not a match, set so setStroke() and setOutlinePaint() can work on the rest
                    this.dataIndexs.datasetSlashAxisIndex=i;
                    this.dataIndexs.seriesIndex=j;
                    setStroke(NORMAL_SERIES_LINE_WEIGHT);
                    setOutlinePaint(Color.GRAY);
                }
            }
        }
    }
    
    public MouseManager(ChartPanel chart, MarkerManager markerManager) {
        this.chartPanelObj = chart;
        this.markerManager = markerManager;
        this.chartType = LoggerProtocolManager.CT_XY_TIMEDOMAIN;
        
        jFreeChart = this.chartPanelObj.getChart();
        
        // Need to check before setting due to bug
        // JFreeCHart forum: Bug in ChartPanel.setMouseWheelEnabled
        // http://www.jfree.org/forum/viewtopic.php?f=3&t=28118&hilit=setMouseWheelEnabled
        if (!this.chartPanelObj.isMouseWheelEnabled()) this.chartPanelObj.setMouseWheelEnabled(true);
        
        this.mouseListener = new MyChartMouseListener();
        this.chartPanelObj.addChartMouseListener(this.mouseListener);
    } 
    
    public void setChartType(int chartType){
        this.chartType = chartType;
        switch (MouseManager.this.chartType) {
        case LoggerProtocolManager.CT_XY_TIMEDOMAIN:
            hasSeriesLines=true;
            hasSeriesShapes=false;
            break;
        case LoggerProtocolManager.CT_XY_SCATTER:
        case LoggerProtocolManager.CT_XYZ_BUBBLE:
            hasSeriesLines=false;
            hasSeriesShapes=true;
        }
    }
    
    boolean isEmptyChart(){
        boolean retval=true;
        try {
            for (int i=0;i<jFreeChart.getXYPlot().getDatasetCount();i++ ){
                if (jFreeChart.getXYPlot().getDataset(i).getSeriesCount()>0) {
                    retval=false;
                    break;
                }
            }
        } catch (Exception e) {
             // ignore
        }
        return retval;
    }
    
    /**
     * 
     * @param e
     * @return false to flag not to propogate event to super()
     */
    boolean doMouseClicked(MouseEvent e) {
        // don't allow super() on doublecklick
        if (e.getClickCount()>=2) return false;
        
        // allow capture of shft-click for measuring tool
        if (this.markerManager!=null) {
            this.markerManager.mouseClicked(e);
        }
        
        // intercept and disable super event if "pointing to something" i.e. TextAnnotation. This usurps the super event and we
        // construct a chartEvent here and trigger locally so the chartPanel can't change the crosshair pos when clicking
        // on a "event marker" i.e. TextAnnotation
        if (this.chartPanelObj.getCursor()==Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)) {
            // build an event just like the ChartPanel
            ChartMouseEvent chartEvent = new ChartMouseEvent(jFreeChart, e, chartPanelObj.getEntityForPoint(e.getX(), e.getY()));
            mouseListener.chartMouseClicked(chartEvent);
            return false;
        }
        
        // disable super event if SHT or CTRL are down/pressed
        if (e.isShiftDown() || e.isControlDown()) {
            return false;
        }
        
        return true;
    }
    
    boolean doMouseMoved(MouseEvent e) {
        if (e.isShiftDown()) {
            if (markerManager!=null) markerManager.mouseMoved(e);
            return false;
        }
        return true;
    }
    
    boolean doMouseDragged(MouseEvent e) {
        if (!e.isShiftDown()) {
            // override to only allow left button to drag
            if ((e.getModifiersEx()&InputEvent.BUTTON1_DOWN_MASK)==InputEvent.BUTTON1_DOWN_MASK) return true;
        }
        return false;
    }
}
