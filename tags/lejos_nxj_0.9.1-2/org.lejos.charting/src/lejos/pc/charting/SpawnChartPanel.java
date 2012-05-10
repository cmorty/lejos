package lejos.pc.charting;

import java.awt.event.MouseEvent;
import java.util.Vector;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;


/** JFreeChart object in a ChartPanel with mouse and marker managers
 * @author Kirk P. Thompson
 */
class SpawnChartPanel extends ChartPanel{
    private final String THISCLASS;
    private MarkerManager markerManager=null; // supplies the domain distance delta marker service
    private MouseManager mouseManager=null; // handles mouse events,
    
    public SpawnChartPanel(JFreeChart chart) {
        super(null);
        try {
            setLayout(null);
            //setVisible(true);
        } catch (Exception e) {
            dbg("!** SpawnChartPanel Exception: " + e.toString());
            e.printStackTrace();
        }
        
        String[] thisClass = this.getClass().getName().split("[\\s\\.]");
        THISCLASS=thisClass[thisClass.length-1];
        
        // Effectively disable font scaling/skewing 
        // JFreeChart forum: Graphics context and custom shapes
        // http://www.jfree.org/forum/viewtopic.php?f=3&t=24499&hilit=font+scaling
        this.setMaximumDrawWidth(1800);
        this.setMaximumDrawHeight(1280);
        
        
        chart.setNotify(false);
        chart.getXYPlot().setDomainPannable(true);
        chart.getXYPlot().setDomainCrosshairVisible(false);
        chart.getXYPlot().setRangePannable(true);
        chart.getXYPlot().setRangeCrosshairVisible(false);
        setChart(chart);
        chart.setNotify(true);
        setMouseZoomable(true);
        setVisible(true);
        
        // Need to check before setting due to bug
        // JFreeCHart forum: Bug in ChartPanel.setMouseWheelEnabled
        // http://www.jfree.org/forum/viewtopic.php?f=3&t=28118&hilit=setMouseWheelEnabled
        if (!isMouseWheelEnabled()) setMouseWheelEnabled(true);
        
        // set up managers for comment event markers and domain measuring markers
        this.markerManager = new MarkerManager(this);
        this.mouseManager = new MouseManager(this, this.markerManager);
        this.markerManager.doAxisChangedRegistration();
    }
    
    
    public void mouseMoved(MouseEvent e) {
        if (mouseManager.doMouseMoved(e))  {
            super.mouseMoved(e);
        }
    }
    
    public void mouseDragged(MouseEvent e) {
        if (mouseManager.doMouseDragged(e)) {
            super.mouseDragged(e);
        }
    }
    
    public void mouseClicked(MouseEvent e) {
        if (mouseManager.doMouseClicked(e)) {
            super.mouseClicked(e);
        }    
    }
    
    private void dbg(String msg) {
        System.out.println(THISCLASS + "-" + msg);
    }
    
    void importMarkers(Vector<MarkerManager.CommentMarker> commentMarkers) {
        if (markerManager==null) return;
        markerManager.importMarkers(commentMarkers);
        
    }
    
}
