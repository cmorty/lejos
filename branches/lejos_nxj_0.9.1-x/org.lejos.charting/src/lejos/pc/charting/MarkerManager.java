package lejos.pc.charting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
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


/** manages the shift-click-drag domain measurement markers & delta display, and event markers, on chart in LoggingChart
 */
class MarkerManager implements PlotChangeListener, RendererChangeListener, AxisChangeListener{
    private final static int DIR_BACKWARD=0;
    private final static int DIR_FORWARD=1;
    private static final int MAX_COMMENT_LABEL_LENGTH=10;
    private static final Color MARKER_COLOR =  Color.MAGENTA.darker();
    
    private boolean showMarker = false;
    private ChartPanel loggingChartPanel=null;
    private JFreeChart chart=null;
    private int dragDir=DIR_FORWARD;
    Vector<CommentMarker> comments = new Vector<CommentMarker>();
    private boolean doRendererChangedRegistration=false;
    private boolean commentMarkersVisible=true;
    private DomainMeasureTool dMT;
   
    private class DomainMeasureTool{
        private XYPlot xYplot;
        
        public DomainMeasureTool(XYPlot xYplot){
            this.xYplot = xYplot;
            initMarkers();
        }
        private IntervalMarker marker1Range;
        private ValueMarker marker1Beg;
        private ValueMarker marker1End;
        private ValueMarker marker2End;
        
        void toolOff(){
            xYplot.removeDomainMarker(this.marker1Beg, Layer.FOREGROUND);
            xYplot.removeDomainMarker(this.marker1End, Layer.FOREGROUND);
            xYplot.removeDomainMarker(this.marker2End, Layer.FOREGROUND);
            xYplot.removeDomainMarker(this.marker1Range, Layer.BACKGROUND);
            
        }
        
        void toolOn(MouseEvent e){
            marker1Beg.setValue(getSnapPoint(e));
            marker1Beg.setLabel(String.format("%1$,1.0f", this.marker1Beg.getValue()));
            marker1End.setValue(this.marker1Beg.getValue());
            marker2End.setValue(this.marker1Beg.getValue());
            marker1Range.setStartValue(this.marker1Beg.getValue());
            marker1Range.setEndValue(this.marker1Beg.getValue());
            
            xYplot.addDomainMarker(this.marker1Beg, Layer.FOREGROUND);
            xYplot.addDomainMarker(this.marker1End, Layer.FOREGROUND);
            xYplot.addDomainMarker(this.marker2End,  Layer.FOREGROUND);
            xYplot.addDomainMarker(this.marker1Range, Layer.BACKGROUND);
        }
        
        void doMeasure(MouseEvent e){
            double snapPoint = getSnapPoint(e);
            
            this.marker1End.setValue(snapPoint);
            this.marker2End.setValue(snapPoint);
            
            Block1: if (snapPoint<this.marker1Beg.getValue()) {
                marker1Range.setStartValue(snapPoint);
                if (dragDir==DIR_BACKWARD) break Block1;
                dragDir=DIR_BACKWARD;
                marker1Range.setEndValue(this.marker1Beg.getValue());
                marker1End.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
                marker1End.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
                marker2End.setLabelAnchor(RectangleAnchor.TOP_LEFT);
                marker2End.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
                marker1Beg.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
                marker1Beg.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            } else {
                marker1Range.setEndValue(snapPoint);
                if (dragDir==DIR_FORWARD) break Block1;
                dragDir=DIR_FORWARD;
                marker1Range.setStartValue(this.marker1Beg.getValue());
                marker1End.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
                marker1End.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
                marker2End.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
                marker2End.setLabelTextAnchor(TextAnchor.TOP_LEFT);
                marker1Beg.setLabelAnchor(RectangleAnchor.TOP_LEFT);
                marker1Beg.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            }
            marker1End.setLabel(String.format("%1$+,1.0f", (snapPoint - this.marker1Beg.getValue())));
            marker2End.setLabel(String.format("%1$,1.0f", snapPoint ));
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
        
        private void setBaseMarkerAttributes(Marker marker){
            marker.setPaint(Color.RED.darker());
            marker.setStroke(new BasicStroke(.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,10f, new float[] {2.5f,2f},0f));
            marker.setLabelFont(new Font("SansSerif", Font.PLAIN, 9));
            marker.setLabelPaint(Color.RED.darker());
            
            if (marker instanceof IntervalMarker) {
                marker.setAlpha(.10f);
            }
        }
    }
    
    class CommentMarker{
        private ValueMarker markerLine;
        private XYPointerAnnotation commentMarkerLabel;
        private int labelState=0;
        private String markerLabel;
        
        CommentMarker(double xVal,String comment) {
            this.markerLabel=comment;
            markerLine= new ValueMarker(xVal);
            markerLine.setPaint(MARKER_COLOR);
            markerLine.setStroke(new BasicStroke(.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,10f, new float[] {5f,2.5f},0f));
            markerLine.setLabelAnchor(RectangleAnchor.TOP_LEFT);
            markerLine.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            commentMarkerLabel=new XYPointerAnnotation(getFittedLabel(comment), xVal,0,0);
            commentMarkerLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
            commentMarkerLabel.setPaint(MARKER_COLOR);
            commentMarkerLabel.setArrowLength(8);
            commentMarkerLabel.setArrowPaint(MARKER_COLOR);
            commentMarkerLabel.setTipRadius(0);
            commentMarkerLabel.setTextAnchor(TextAnchor.CENTER_LEFT);
            commentMarkerLabel.setBaseRadius(8);
            commentMarkerLabel.setToolTipText(String.format("(%1$,1.0f) ", xVal) + comment);
            
            
        }
        
        private String getFittedLabel(String label) {
            if (label.length()>MAX_COMMENT_LABEL_LENGTH) {
                label=label.substring(0, MAX_COMMENT_LABEL_LENGTH) + "...";
            }
            return label;
        }
        
        CommentMarker cloneMarker() {
            return new CommentMarker(markerLine.getValue(), markerLabel);
        }
        
        void toggleLabel() {
            String theLabel;
            
            labelState++;
            if (labelState>1) labelState=0;
            
            switch (labelState) {
                case 0:
                    theLabel=getFittedLabel(this.markerLabel);
                    break;
                case 1:
                    theLabel=this.markerLabel;
                    break;
//                case 2:
//                    theLabel="      ";
//                    break;
                default:
                    return;
            }
            
            commentMarkerLabel.setText(theLabel);
            chart.setNotify(true);
        }
        
        void setY(){
            if (commentMarkerLabel==null) return;
            commentMarkerLabel.setY(getCommentY());
//            System.out.println("*** y=" + commentMarkerLabel.getY());
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
    
    MarkerManager(ChartPanel loggingChartPanel) {
        this.loggingChartPanel=loggingChartPanel;
//        System.out.println("this.loggingChartPanel=" + this.loggingChartPanel);
        this.chart=this.loggingChartPanel.getChart();
        this.chart.getPlot().addChangeListener(this);
        this.dMT=new DomainMeasureTool(chart.getXYPlot());
    }
    
    // get the domain value closest to the x-click pos
    private double getSnapPoint(MouseEvent e){
        int mouseX = e.getX();
        int mouseY = e.getY();
    //            System.out.println("mx=" + mouseX + ", my=" + mouseY);
        
        XYPlot plot = chart.getXYPlot();
        if (plot.getDataset().getSeriesCount()==0) return Double.NaN;
        
        Rectangle2D dataArea = loggingChartPanel.getScreenDataArea();
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
    
    private Point2D getPointInRectangle(int x, int y, Rectangle2D area) {
        double xx = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
        double yy = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
        return new Point2D.Double(xx, yy);
    }
    
    boolean isEmptyChart(){
        boolean retval=true;
        try {
            for (int i=0;i<loggingChartPanel.getChart().getXYPlot().getDatasetCount();i++ ){
                if (loggingChartPanel.getChart().getXYPlot().getDataset(i).getSeriesCount()>0) {
                    retval=false;
                    break;
                }
            }
        } catch (Exception e) {
            ; // ignore
        }
        return retval;
    }
    
    void mouseClicked(MouseEvent e) {
        if (!isEmptyChart() && e.isShiftDown()) {
            
            // click and shift toggles marker visibility
            this.showMarker = !this.showMarker;
            if (showMarker) {
                dMT.toolOn(e);
            } else {
                dMT.toolOff();
                this.showMarker=false;
            }
            chart.setNotify(true);
        }
    }

    void mouseMoved(MouseEvent e) {
        if (!isEmptyChart() && e.isShiftDown() && this.showMarker) {
            dMT.doMeasure(e);
            chart.setNotify(true);
        }
    }
    
    void addCommentMarker(double xVal, String comment){
        if (this.chart.getXYPlot().getRenderer()==null) return;
        CommentMarker cm = new CommentMarker(xVal, comment);
        cm.setY();
        //System.out.println("y=" + cm.commentMarkerLabel.getY());
        cm.setVisible(true);
        comments.add(cm);
    }

    Vector<CommentMarker> cloneMarkers(){
        Vector<CommentMarker> commentClones = new Vector<CommentMarker>(comments.size());
        for (CommentMarker item: comments){
            commentClones.add(item.cloneMarker());
        }
        return commentClones;
    }
    
    void importMarkers(Vector<CommentMarker> cloneMarkers){
        if (cloneMarkers==null) return;
        
        for (CommentMarker item: cloneMarkers){
            addCommentMarker(item.markerLine.getValue(), item.markerLabel);
        }
    }
    
    /**
     * find the comment that matched passed entity and toggle the label
     * @param entity
     */
    void toggleLabel(Object entity){
//        String tttxt;
        for (CommentMarker item: comments){
            //tttxt=item.commentMarkerLabel.getToolTipText();
            if (entity.toString().indexOf(item.commentMarkerLabel.getToolTipText())!=-1) {
                
                //System.out.println("FOUND! " + item.commentMarkerLabel.getToolTipText());
                item.toggleLabel();
                return; 
            }
        }
        return; 
    }
    
    /**
     * clear all event markers (logging comments)
     */
    void clearComments(){
        if (comments.isEmpty()) return;
        for (CommentMarker item: comments){
            item.die();
        }
        comments.removeAllElements();
    }

    /**
     * Turn off measuring tools
     */
    void measureToolsOff(){
        dMT.toolOff();
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
    
    boolean isCommentsVisible(){
        return this.commentMarkersVisible;
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
        doAxisChangedRegistration();
        this.doRendererChangedRegistration=false;
    }
    
    // use in case of non-changing plots and rendereer (i.e. spawned charts)
    void doAxisChangedRegistration(){
        this.chart.getXYPlot().getRangeAxis().removeChangeListener(this);
        this.chart.getXYPlot().getRangeAxis().addChangeListener(this);
    }
    
    // fired when zooming and y scale changes
    public synchronized void axisChanged(AxisChangeEvent event) {
        if (comments.isEmpty()) return;
        //System.out.println("axisChanged: " + event.toString());
        // set the Y value of comment as the axis scale changes (due to zooming, autoFit, etc.)
        for (CommentMarker item: comments){
            item.setY();
        }
    }
    
    // get the Y (Range axis) coordinate in data values after using java2d space to ensure absolute relative pos from top of axis.
    private double getCommentY() {
        Rectangle2D dataArea = loggingChartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();
//        System.out.println("da=" + dataArea);
        XYPlot plot = this.chart.getXYPlot();
//        System.out.println("plot=" + plot);
        
        double ub=plot.getRangeAxis().getUpperBound(); // top of Y in data units
//        System.out.println("ub=" + ub); 
        
        double rangeMaxJava2d = plot.getRangeAxis().valueToJava2D(ub, dataArea, plot.getRangeAxisEdge()); // convert to real units
        rangeMaxJava2d+=18; // add offset
        double retval = 
            plot.getRangeAxis().java2DToValue(rangeMaxJava2d, dataArea, plot.getRangeAxisEdge()); // convert back to data units
//        System.out.println("retval=" + retval);
        return retval;
    }
}
