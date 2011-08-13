package lejos.pc.charting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;


class MarkerManager {
    private IntervalMarker marker1Range;
    private ValueMarker marker1Beg;
    private ValueMarker marker1End;
    private boolean showMarker = false;
    private LoggingChart loggingChartPanel=null;
    private JFreeChart chart=null;
    
    MarkerManager(LoggingChart loggingChartPanel) {
        registerLoggingChart(loggingChartPanel);
        initMarkers();
    }
    
    void markersOff(){
        ((XYPlot)this.chart.getPlot()).removeDomainMarker(this.marker1Beg);
        ((XYPlot)this.chart.getPlot()).removeDomainMarker(this.marker1End);
        ((XYPlot)this.chart.getPlot()).removeDomainMarker(this.marker1Range, Layer.BACKGROUND);
        this.showMarker=false;
    }
    
    void registerLoggingChart(LoggingChart loggingChartPanel){
        this.loggingChartPanel=loggingChartPanel;
        this.chart=this.loggingChartPanel.getChart();
    }
    
    private Point2D getPointInRectangle(int x, int y, Rectangle2D area) {
        double xx = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
        double yy = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
        return new Point2D.Double(xx, yy);
    }
    
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
        marker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
        marker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        if (marker instanceof IntervalMarker) {
            marker.setAlpha(.15f);
        }
    }
    private void initMarkers(){
        marker1Beg= new ValueMarker(0); 
        setBaseMarkerAttributes(marker1Beg);
        
        marker1End= new ValueMarker(0);
        setBaseMarkerAttributes(marker1End);
        marker1End.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        marker1End.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
        
        
        marker1Range= new IntervalMarker(100,100);
        setBaseMarkerAttributes(marker1Range);
    }

    void mouseClicked(MouseEvent e) {
        if (e.isShiftDown()) {
            this.showMarker = !this.showMarker;
            if (showMarker) {
                this.marker1Beg.setValue(getSnapPoint(e));
                this.marker1Beg.setLabel(String.format("%1$,1.0f", this.marker1Beg.getValue()));
                this.marker1End.setValue(this.marker1Beg.getValue());
                this.marker1Range.setStartValue(this.marker1Beg.getValue());
                this.marker1Range.setEndValue(this.marker1Beg.getValue());
                ((XYPlot)this.chart.getPlot()).addDomainMarker(this.marker1Beg, Layer.FOREGROUND);
                ((XYPlot)this.chart.getPlot()).addDomainMarker(this.marker1End, Layer.FOREGROUND);
                ((XYPlot)this.chart.getPlot()).addDomainMarker(this.marker1Range, Layer.BACKGROUND);
            } else {
                markersOff();
            }
            chart.setNotify(true);
        }

        //            this.marker1.setValue(getSnapPoint(event.getTrigger()));

    }

    void mouseMoved(MouseEvent e) {
        if (e.isShiftDown() && this.showMarker) {
            double snapPoint = getSnapPoint(e);
 
            this.marker1End.setValue(snapPoint);
            
            if (snapPoint<this.marker1Beg.getValue()) {
                this.marker1Range.setEndValue(this.marker1Beg.getValue());
                this.marker1Range.setStartValue(snapPoint);
            } else {
                this.marker1Range.setEndValue(snapPoint);
                this.marker1Range.setStartValue(this.marker1Beg.getValue());
            }
            this.marker1End.setLabel(String.format("%1$+,1.0f", (snapPoint - this.marker1Beg.getValue())));
            chart.setNotify(true);
        }
    }
       
}
