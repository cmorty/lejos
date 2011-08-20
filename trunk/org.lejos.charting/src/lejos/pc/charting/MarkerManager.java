package lejos.pc.charting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.Vector;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;


/** manages the shift-click-drag domain marker & delta display on chart in LoggingChart
 */
class MarkerManager implements PlotChangeListener, RendererChangeListener, AxisChangeListener{
    private final static int DIR_BACKWARD=0;
    private final static int DIR_FORWARD=1;
    private static final Color MARKER_COLOR =  Color.MAGENTA.darker();
    private static final int MAX_COMMENT_LABEL_LENGTH=10;
    
    private IntervalMarker marker1Range;
    private ValueMarker marker1Beg;
    private ValueMarker marker1End;
    private ValueMarker marker2End;
    private boolean showMarker = false;
    private LoggingChart loggingChartPanel=null;
    private JFreeChart chart=null;
    private int dragDir=DIR_FORWARD;
    Vector<CommentMarker> comments = new Vector<CommentMarker>();
    private boolean doRendererChangedRegistration=false;
    private boolean commentMarkersVisible=true;
    
    private class CommentMarker{
        private ValueMarker markerLine;
        private XYPointerAnnotation commentMarkerLabel;
        
        CommentMarker(double xVal,String comment) {
            String markerLabel=comment;
            if (comment.length()>MAX_COMMENT_LABEL_LENGTH) {
                markerLabel=comment.substring(0, MAX_COMMENT_LABEL_LENGTH) + "...";
            }
            markerLine= new ValueMarker(xVal);
            markerLine.setPaint(MARKER_COLOR);
            markerLine.setStroke(new BasicStroke(.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,10f, new float[] {5f,2.5f},0f));
            markerLine.setLabelAnchor(RectangleAnchor.TOP_LEFT);
            markerLine.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            commentMarkerLabel=new XYPointerAnnotation(markerLabel, xVal,0,0);
            commentMarkerLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
            commentMarkerLabel.setPaint(MARKER_COLOR);
            commentMarkerLabel.setArrowLength(8);
            commentMarkerLabel.setArrowPaint(MARKER_COLOR);
            commentMarkerLabel.setTipRadius(0);
            commentMarkerLabel.setTextAnchor(TextAnchor.CENTER_LEFT);
            commentMarkerLabel.setBaseRadius(8);
            commentMarkerLabel.setToolTipText(String.format("(%1$,1.0f) ",xVal) + comment);
            if (MarkerManager.this.commentMarkersVisible) {
                ((XYPlot)MarkerManager.this.chart.getPlot()).addDomainMarker(markerLine);
                ((XYPlot)MarkerManager.this.chart.getPlot()).addAnnotation(commentMarkerLabel);
            }
            double lb=MarkerManager.this.chart.getXYPlot().getRangeAxis().getLowerBound();
            double ub=MarkerManager.this.chart.getXYPlot().getRangeAxis().getUpperBound();
            setY(ub-(ub-lb)*.05);
        }
        
        void setY(double yVal){
            if (commentMarkerLabel==null) return;
            commentMarkerLabel.setY(yVal);
        }
        
        void die(){
            if (commentMarkerLabel==null) return;
            setVisible(false);
            markerLine=null;
            commentMarkerLabel=null;
        }
        
        void setVisible(boolean visible){
            if (commentMarkerLabel==null) return;
            if (!visible){
                ((XYPlot)MarkerManager.this.chart.getPlot()).removeDomainMarker(markerLine);
                ((XYPlot)MarkerManager.this.chart.getPlot()).removeAnnotation(commentMarkerLabel);
            } else {
                ((XYPlot)MarkerManager.this.chart.getPlot()).addDomainMarker(markerLine);
                ((XYPlot)MarkerManager.this.chart.getPlot()).addAnnotation(commentMarkerLabel);
            }
        }
    }
    
    MarkerManager(LoggingChart loggingChartPanel) {
        registerLoggingChart(loggingChartPanel);
        initMarkers();
    }
    
    void markersOff(){
        ((XYPlot)this.chart.getPlot()).removeDomainMarker(this.marker1Beg, Layer.FOREGROUND);
        ((XYPlot)this.chart.getPlot()).removeDomainMarker(this.marker1End, Layer.FOREGROUND);
        ((XYPlot)this.chart.getPlot()).removeDomainMarker(this.marker2End, Layer.FOREGROUND);
        ((XYPlot)this.chart.getPlot()).removeDomainMarker(this.marker1Range, Layer.BACKGROUND);
        this.showMarker=false;
    }
    
    void registerLoggingChart(LoggingChart loggingChartPanel){
        this.loggingChartPanel=loggingChartPanel;
        this.chart=this.loggingChartPanel.getChart();
        this.chart.getPlot().addChangeListener(this);
    }
    
    private Point2D getPointInRectangle(int x, int y, Rectangle2D area) {
        double xx = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
        double yy = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
        return new Point2D.Double(xx, yy);
    }
    
    // get the domain value closest to the x-click pos
    private double getSnapPoint(MouseEvent e){
        int mouseX = e.getX();
        int mouseY = e.getY();
    //            System.out.println("mx=" + mouseX + ", my=" + mouseY);
        
        XYPlot plot = chart.getXYPlot();
        if (plot.getDataset().getSeriesCount()==0) return Double.NaN;
        
        Rectangle2D dataArea = this.loggingChartPanel.getScreenDataArea();
        Point2D domPoint = getPointInRectangle(mouseX, mouseY,dataArea);
        if (!dataArea.contains(domPoint)) return Double.NaN;
        
        double domX = plot.getDomainAxis().java2DToValue(domPoint.getX(), dataArea, plot.getDomainAxisEdge());
        // assumes sorted dataset so binarySearch on collection returns -(insert index if not found)
        int nearestSeriesIndex = ((XYSeriesCollection)plot.getDataset()).getSeries(0).indexOf(domX);
        int startIndex=(nearestSeriesIndex-3<0)?0:nearestSeriesIndex-3;
        // assumes datset[0](axis 0) will always have a series[0] (domain)
        XYSeries theSeries=((XYSeriesCollection)plot.getDataset()).getSeries(0);
        double minVal=Double.MAX_VALUE, d=0, domValue=0, cacheVal=-1;
        int i=startIndex;
        for (;i<plot.getDataset().getItemCount(0);i++){
            domValue=theSeries.getDataItem(i).getXValue();
            d = Math.abs(domX - domValue);
            // assumes lower index vals on domain axis will always be smaller
            if (d<minVal) {
                minVal=d;
                cacheVal=domValue;
            } else {
                break; 
            }
        }
        
    //            System.out.println("nearest=" + cacheVal);
    //            System.out.println("domX=" + domX);
        return cacheVal;
    }
    
    private void setBaseMarkerAttributes(Marker marker){
        marker.setPaint(Color.RED.darker());
        marker.setStroke(new BasicStroke(.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,10f, new float[] {2.5f,2f},0f));
        marker.setLabelFont(new Font("SansSerif", Font.PLAIN, 9));
        marker.setLabelPaint(Color.RED.darker());
        
        if (marker instanceof IntervalMarker) {
            marker.setAlpha(.10f);
        }
    }
    
    private void initMarkers(){
        marker1Beg= new ValueMarker(0); 
        setBaseMarkerAttributes(marker1Beg);
        marker1Beg.setLabelAnchor(RectangleAnchor.TOP_LEFT);
        marker1Beg.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        
        marker1End= new ValueMarker(0);
        setBaseMarkerAttributes(marker1End);
        marker1End.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        marker1End.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
        
        marker2End= new ValueMarker(0);
        setBaseMarkerAttributes(marker2End);
        marker2End.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        marker2End.setLabelTextAnchor(TextAnchor.TOP_LEFT);
        
        marker1Range= new IntervalMarker(0,0);
        setBaseMarkerAttributes(marker1Range);
    }
    
    void mouseClicked(MouseEvent e) {
        if (!loggingChartPanel.isEmptyChart() && e.isShiftDown()) {
            this.showMarker = !this.showMarker;
            if (showMarker) {
                this.marker1Beg.setValue(getSnapPoint(e));
                this.marker1Beg.setLabel(String.format("%1$,1.0f", this.marker1Beg.getValue()));
                this.marker1End.setValue(this.marker1Beg.getValue());
                this.marker2End.setValue(this.marker1Beg.getValue());
                this.marker1Range.setStartValue(this.marker1Beg.getValue());
                this.marker1Range.setEndValue(this.marker1Beg.getValue());
                
                ((XYPlot)this.chart.getPlot()).addDomainMarker(this.marker1Beg, Layer.FOREGROUND);
                ((XYPlot)this.chart.getPlot()).addDomainMarker(this.marker1End, Layer.FOREGROUND);
                ((XYPlot)this.chart.getPlot()).addDomainMarker(this.marker2End,  Layer.FOREGROUND);
                ((XYPlot)this.chart.getPlot()).addDomainMarker(this.marker1Range, Layer.BACKGROUND);
            } else {
                markersOff();
            }
            chart.setNotify(true);
        }
    }

    void mouseMoved(MouseEvent e) {
        if (!loggingChartPanel.isEmptyChart() && e.isShiftDown() && this.showMarker) {
            double snapPoint = getSnapPoint(e);
 
            this.marker1End.setValue(snapPoint);
            this.marker2End.setValue(snapPoint);
            
            Block1: if (snapPoint<this.marker1Beg.getValue()) {
                this.marker1Range.setStartValue(snapPoint);
                if (dragDir==DIR_BACKWARD) break Block1;
                dragDir=DIR_BACKWARD;
                this.marker1Range.setEndValue(this.marker1Beg.getValue());
                marker1End.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
                marker1End.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
                marker2End.setLabelAnchor(RectangleAnchor.TOP_LEFT);
                marker2End.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
                marker1Beg.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
                marker1Beg.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            } else {
                this.marker1Range.setEndValue(snapPoint);
                if (dragDir==DIR_FORWARD) break Block1;
                dragDir=DIR_FORWARD;
                this.marker1Range.setStartValue(this.marker1Beg.getValue());
                marker1End.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
                marker1End.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
                marker2End.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
                marker2End.setLabelTextAnchor(TextAnchor.TOP_LEFT);
                marker1Beg.setLabelAnchor(RectangleAnchor.TOP_LEFT);
                marker1Beg.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            }
            this.marker1End.setLabel(String.format("%1$+,1.0f", (snapPoint - this.marker1Beg.getValue())));
            this.marker2End.setLabel(String.format("%1$,1.0f", snapPoint ));
            
            chart.setNotify(true);
        }
    }
    
    void addCommentMarker(double xVal, String comment){
        if (this.chart.getXYPlot().getRenderer()==null) return;
        comments.add(new CommentMarker(xVal, comment));
    }
    
    void clearComments(){
        if (comments.isEmpty()) return;
        for (CommentMarker item: comments){
            item.die();
        }
        comments.removeAllElements();
    }
    
    void setCommentsVisible(boolean visible){
        this.commentMarkersVisible=visible;
        if (comments.isEmpty()) return;
//        System.out.println("setCommentsVisible " + visible + ". comments.size=" + comments.size());
        for (CommentMarker item: comments){
            item.setVisible(visible);
        }
        this.chart.setNotify(true);
    }
    
    // event hooks to ensure we can register rendererChanged when new axis is created
    public void plotChanged(PlotChangeEvent event) {
        if (event.getType().toString().indexOf("DATASET_UPDATED")==-1) return;
        // this keeps us registered as an axisChangeListener when the range axes are inited on new data (setSeries())
        if (this.chart.getXYPlot().getRenderer()!=null) {
            this.doRendererChangedRegistration=true;
            this.chart.getXYPlot().getRenderer().removeChangeListener(this);
            this.chart.getXYPlot().getRenderer().addChangeListener(this);
        }
    }

    // ensure we are registered as a axisChanged listener
    public void rendererChanged(RendererChangeEvent event) {
        if (!this.doRendererChangedRegistration) return;
//        System.out.println("** RCE: " + event.getType().toString());
        this.chart.getXYPlot().getRangeAxis().removeChangeListener(this);
        this.chart.getXYPlot().getRangeAxis().addChangeListener(this);
        this.doRendererChangedRegistration=false;
    }
    
    // fired when zooming and y scale changes
    public synchronized void axisChanged(AxisChangeEvent event) {
//        System.out.println("axisChanged: " + event.getType().toString());
        double lb=this.chart.getXYPlot().getRangeAxis().getLowerBound();
        double ub=this.chart.getXYPlot().getRangeAxis().getUpperBound();
        for (CommentMarker item: comments){
            item.setY(ub-(ub-lb)*.05);
        }
    }
}
