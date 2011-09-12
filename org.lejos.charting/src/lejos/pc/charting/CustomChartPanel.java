package lejos.pc.charting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeriesCollection;


/** JPanel acting as a container for the LoggingChart ChartPanel, domain slider, x-y label, and rowcount label. Use
 * SpringLayout to position components but JPanel width must be adjusted if used in other classes.
 * @author Kirk P. Thompson
 */
public class CustomChartPanel extends JPanel implements ChangeListener, AxisChangeListener, ChartProgressListener, ChartChangeListener{
    private static final int SLIDER_MAX= 10000;
    private static final float SLIDER_CURVE_POWER= 2.4f;
    private LoggingChart loggingChartPanel = new LoggingChart();
    private JSlider domainScaleSlider = new JSlider();
    private JLabel xYValueLabel = new JLabel();
    private JLabel domainWidthLabel = new JLabel();
    private boolean sliderSetFlag=false;
    private JLabel dataRowsLabel = new JLabel();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();

    public CustomChartPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setLayout( null );
        this.setSize(new Dimension(738, 540));
        this.setOpaque(true);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        this.setBackground(Color.white);
        loggingChartPanel.setOpaque(false);
        loggingChartPanel.setPreferredSize(new Dimension(800, 450));
        loggingChartPanel.setMinimumSize(new Dimension(400, 200));
        loggingChartPanel.setSize(new Dimension(770, 443));
        loggingChartPanel.getChart().getXYPlot().getDomainAxis().addChangeListener(this);
        loggingChartPanel.getChart().addProgressListener(this);
        
        domainScaleSlider.setOpaque(false);
        // .1-100
        domainScaleSlider.setMinimum(1);
        domainScaleSlider.setMaximum(SLIDER_MAX);
        domainScaleSlider.setMajorTickSpacing(1);
        domainScaleSlider.setValue(SLIDER_MAX);
        domainScaleSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        domainScaleSlider.setRequestFocusEnabled(false);
        domainScaleSlider.setFocusable(false);   
        domainScaleSlider.addChangeListener(this);
        domainScaleSlider.setToolTipText("Use slider to change domain scale");


        domainScaleSlider.setMinimumSize(new Dimension(200, 16));
        xYValueLabel.setFocusable(false);
        xYValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        xYValueLabel.setText("--, --");
        xYValueLabel.setToolTipText("The X:Y coordinate of the crosshair");
        
        // updates the x,y label and domain scale slider
        xYValueLabel.setSize(new Dimension(60, 14));
        xYValueLabel.setPreferredSize(new Dimension(150, 14));
        xYValueLabel.setMinimumSize(new Dimension(150, 14));
        xYValueLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        xYValueLabel.setMaximumSize(new Dimension(170, 14));
        xYValueLabel.setFont(new Font("Tahoma", 0, 11));
        
        domainWidthLabel.setFocusable(false);
        domainWidthLabel.setHorizontalAlignment(JTextField.LEFT);
        domainWidthLabel.setText("Domain Scale");
        domainWidthLabel.setToolTipText("The domain axis length in ms");

        domainWidthLabel.setPreferredSize(new Dimension(90, 14));
        domainWidthLabel.setSize(new Dimension(90, 14));
        domainWidthLabel.setMinimumSize(new Dimension(90, 14));
        domainWidthLabel.setMaximumSize(new Dimension(100, 14));
        domainWidthLabel.setFont(new Font("Tahoma", 0, 11));
        dataRowsLabel.setText("data rows");
        dataRowsLabel.setToolTipText("The number of rows logged");

        dataRowsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dataRowsLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        dataRowsLabel.setMaximumSize(new Dimension(100, 14));
        dataRowsLabel.setFont(new Font("Tahoma", 0, 11));
        this.setLayout(gridBagLayout1);
        this.add(domainScaleSlider, 
                 new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                        new Insets(0, 0, 0, 0), 0, 0));
        this.add(dataRowsLabel, 
                 new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                        new Insets(0, 0, 0, 0), 0, 0));
        this.add(domainWidthLabel, 
                 new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, 
                                        new Insets(0, 5, 0, 0), 0, 0));
        this.add(xYValueLabel, 
                 new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, 
                                        new Insets(0, 0, 0, 15), 0, 0));
        this.add(loggingChartPanel, 
                 new GridBagConstraints(0, 0, 4, 1, 1.0, 1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
                                        new Insets(0, 1, 0, 5), 0, 0));
        
        loggingChartPanel.getChart().addChangeListener(this); // to capture dataset changes to populate row count
    }
    
    protected LoggingChart getLoggingChartPanel() {
        return loggingChartPanel;
    }
    
    public void axisChanged(AxisChangeEvent event) {
        Range domainRange = ((NumberAxis)event.getAxis()).getRange();
        double domainWidth = domainRange.getLength();

        domainWidthLabel.setText(String.format("%1$-,3d ms",(long)domainWidth));
        
        // return if no series yet
        if (loggingChartPanel.getChart().getXYPlot().getSeriesCount()==0) return;
        
        double minXVal = ((XYSeriesCollection)loggingChartPanel.getChart().getXYPlot().getDataset()).getSeries(0).getMinX();
        double maxXVal = ((XYSeriesCollection)loggingChartPanel.getChart().getXYPlot().getDataset()).getSeries(0).getMaxX();
        // set the flag to not update the chart because the chart is updating the slider here
        int sliderVal = (int)(domainWidth/(maxXVal-minXVal)*SLIDER_MAX);
        int working=(int)Math.pow((sliderVal*Math.pow(SLIDER_MAX, SLIDER_CURVE_POWER)), (1/(1+SLIDER_CURVE_POWER)));
//        System.out.println("sliderVal=" + sliderVal + ", working=" + working);
        sliderSetFlag=false;
//        domainScaleSlider.setValue(sliderVal);
        domainScaleSlider.setValue(working);
        // this ensures that the mouse wheel zoom works after messing with slider and not clicking on chart
        if (!loggingChartPanel.getChart().isNotify()) loggingChartPanel.getChart().setNotify(true);
    }
    
    public void chartProgress(ChartProgressEvent event) {
        long xval = (long)loggingChartPanel.getChart().getXYPlot().getDomainCrosshairValue();
        double yval = loggingChartPanel.getChart().getXYPlot().getRangeCrosshairValue();
        xYValueLabel.setText(String.format("%1$,6d : %2$,7.3f", xval, yval));
    }
    
    public void stateChanged(ChangeEvent e) {
        if (e.getSource()==domainScaleSlider) {
            // if not being triggered by the chart setting slider to match scale (i.e. on a zoom, resize, etc.)...
            if (sliderSetFlag) {
                // set the domain scale based on slider value
                int sliderVal=domainScaleSlider.getValue();
                int working=(int)(Math.pow(sliderVal, SLIDER_CURVE_POWER) / Math.pow(SLIDER_MAX, SLIDER_CURVE_POWER) * 
                     sliderVal);
    //                loggingChartPanel.setDomainScale(domainScaleSlider.getValue());
                loggingChartPanel.setDomainScale(working);
            } 
            sliderSetFlag=true;
        }
    }

    public void chartChanged(ChartChangeEvent event) {
        try {
            if (event.getChart().getXYPlot().getDataset().getSeriesCount()==0) return;
            dataRowsLabel.setText(String.format("%1$-,1d rows", event.getChart().getXYPlot().getDataset().getItemCount(0)));
        } catch (NullPointerException e) {
            ; // do nothing
        }

    }
    
    /** Set the width of the x-axis (domain) scale centered around current midpoint of domain scale. Uses a scaled integer 1-1000 
     * (meaning 0.1-100.0). Values outside this
     * range will cause the method to immediately exit without doing any changes. Existing domain extents (min, max X values) define
     * the total range. 
     * 
     * @param domainWidth The [scaled integer] width in % (1-1000) of range of x domain extents
     */
    public void setDomainScale(int domainWidth) {
        loggingChartPanel.setDomainScale(domainWidth);
    }
    
    /**Add series data to the dataset. Pass an array of <code>double</code> series values that all share the same domain value 
     * defined in element 0. 
    * The number of values must match the header count in setSeries().
    * <p>
    * Element 0 is the domain (X) series and should be a timestamp.
    * @param seriesData the series data as <code>double</code>s
    * @see #setSeries
    */
    public void addDataPoints(double[] seriesData){
        loggingChartPanel.addDataPoints(seriesData);
    }

    /** Add a comment marker to the chart at specified domain position.
     * @param xVal Domain value
     * @param comment The comment text
     * @see #setCommentsVisible
     */
    public void addCommentMarker(double xVal, String comment){
        loggingChartPanel.addCommentMarker(xVal, comment);
    }

    /** Control the visibility of any comment markers defined for the chart.
     * @param visible <code>true</code> to show, <code>false</code> to hide
     * @see #addCommentMarker
     */
    public void setCommentsVisible(boolean visible){
        loggingChartPanel.setCommentsVisible(visible);
    }
    
     /** Set the passed series/header definitions as new XYseries in the dataset. Existing series and comment markers are wiped. 
      * Must be at least two items
      * in the array or any existing series is left intact and method exits with 0. First item (series 0) is set as domain label and should
      * be system time in milliseconds, and is always axis 0 for multi-axis charts.
      * <p>
      *  The string format/structure of each string field is:<br>
      *  <code>[name]:[axis ID 1-4]</code>
      *  <br>i.e. <pre>"MySeries:1"</pre>
      * @param seriesNames Array of series names
      * @return The number of series created
      * @see #addDataPoints
      */
    public int setSeries(String[] seriesNames){
    // ENHANCE change to not specify timestamp and do timestamp automatically
        return loggingChartPanel.setSeries(seriesNames);
    }
    
    public void copyChart() {
        loggingChartPanel.doCopy();
    }
}
