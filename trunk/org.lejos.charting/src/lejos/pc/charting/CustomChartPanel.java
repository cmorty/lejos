package lejos.pc.charting;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
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
    private final int SLIDER_MAX= 10000;
    private LoggingChart loggingChartPanel = new LoggingChart();
    private JSlider domainScaleSlider = new JSlider();
    private JLabel xYValueLabel = new JLabel();
    private JLabel domainWidthLabel = new JLabel();
    private boolean sliderSetFlag=false;
    private JLabel dataRowsLabel = new JLabel();
        
    public CustomChartPanel() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setLayout( null );
        this.setSize(new Dimension(732, 477));
        this.setOpaque(true);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        this.setBackground(Color.white);
//        this.setBounds(new Rectangle(1, 1, 800, 300));
        loggingChartPanel.setOpaque(false);
        loggingChartPanel.setPreferredSize(new Dimension(800, 300));
        loggingChartPanel.setMinimumSize(new Dimension(400, 200));
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
        
        
        xYValueLabel.setFocusable(false);
        xYValueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        xYValueLabel.setText("--, --");
        xYValueLabel.setToolTipText("The X:Y coordinate of the crosshair");
        
        // updates the x,y label and domain scale slider
        domainWidthLabel.setFocusable(false);
        domainWidthLabel.setHorizontalAlignment(JTextField.LEFT);
        domainWidthLabel.setText("Domain Scale");
        domainWidthLabel.setToolTipText("The domain axis length in ms");
        
        dataRowsLabel.setText("data rows");
        dataRowsLabel.setToolTipText("The number of rows logged");
        
        SpringLayout layout=new SpringLayout();
        this.setLayout(layout);
//        this.setSize(new Dimension(732, 419));
//        this.setPreferredSize(new Dimension(600, 300));
        this.add(domainScaleSlider, null);
        this.add(dataRowsLabel, null);
        this.add(domainWidthLabel, null);
        this.add(xYValueLabel, null);
        this.add(loggingChartPanel, null);
        
        loggingChartPanel.getChart().addChangeListener(this); // to capture dataset changes to populate row count
        
        // set up springs
        layout.putConstraint(SpringLayout.WEST, loggingChartPanel, 0, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.SOUTH, loggingChartPanel, -20, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.NORTH, loggingChartPanel, 0, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.EAST, loggingChartPanel, -5, SpringLayout.EAST, this);
        
        layout.putConstraint(SpringLayout.SOUTH, domainScaleSlider, -5, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.WEST, domainScaleSlider, 20, SpringLayout.WEST, loggingChartPanel);
        
        layout.putConstraint(SpringLayout.SOUTH, domainWidthLabel, 0, SpringLayout.SOUTH, domainScaleSlider);
        layout.putConstraint(SpringLayout.WEST, domainWidthLabel, 10, SpringLayout.EAST, domainScaleSlider);
        
        layout.putConstraint(SpringLayout.SOUTH, dataRowsLabel, 0, SpringLayout.SOUTH, domainScaleSlider);
        layout.putConstraint(SpringLayout.EAST, dataRowsLabel, 430, SpringLayout.WEST, this);
        
        layout.putConstraint(SpringLayout.SOUTH, xYValueLabel, 0,SpringLayout.SOUTH, domainScaleSlider);
        layout.putConstraint(SpringLayout.EAST, xYValueLabel, -30, SpringLayout.EAST, this);
        
    }
    
    protected LoggingChart getLoggingChartPanel() {
        return loggingChartPanel;
    }
    
    public void axisChanged(AxisChangeEvent event) {
        Range domainRange = ((NumberAxis)event.getAxis()).getRange();
        domainWidthLabel.setText(String.format("%1$-,3d ms",(long)domainRange.getLength()));
        
        // return if no series yet
        if (loggingChartPanel.getChart().getXYPlot().getSeriesCount()==0) return;
        
        double minXVal = ((XYSeriesCollection)loggingChartPanel.getChart().getXYPlot().getDataset()).getSeries(0).getMinX();
        double maxXVal = ((XYSeriesCollection)loggingChartPanel.getChart().getXYPlot().getDataset()).getSeries(0).getMaxX();
        // set the flag to not update the chart because the chart is updating the slider here
        sliderSetFlag=false;
        domainScaleSlider.setValue((int)((float)domainRange.getLength()/(maxXVal-minXVal)*SLIDER_MAX));
        // this ensures that the mouse wheel zoom works after messing with slider and not clicking on chart
        if (!loggingChartPanel.getChart().isNotify()) loggingChartPanel.getChart().setNotify(true);
    }
    
    public void chartProgress(ChartProgressEvent event) {
//        System.out.println("chartProgress");
        long xval = (long)loggingChartPanel.getChart().getXYPlot().getDomainCrosshairValue();
        double yval = loggingChartPanel.getChart().getXYPlot().getRangeCrosshairValue();
        xYValueLabel.setText(String.format("%1$,6d : %2$,7.3f", xval, yval));
    }
    
    public void stateChanged(ChangeEvent e) {
        if (e.getSource()==domainScaleSlider) {
            // if not being triggered by the chart setting slider to match scale (i.e. on a zoom, resize, etc.)...
            if (sliderSetFlag) {
                // set the domain scale based on slider value
                loggingChartPanel.setDomainScale(domainScaleSlider.getValue());
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
    

     /** Set the passed series/header definitions as new XYseries in the dataset. Existing series are wiped. Must be at least two items
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
