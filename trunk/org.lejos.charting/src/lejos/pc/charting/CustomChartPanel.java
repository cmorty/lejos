package lejos.pc.charting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeriesCollection;


/** JPanel acting as a container for the LoggingChart ChartPanel, domain slider, x-y label, and rowcount label. Use
 * SpringLayout to position components but JPanel width must be adjusted if used in other classes.
 * @author Kirk P. Thompson
 */
 class CustomChartPanel extends ChartModel{
    private static final int SLIDER_MAX= 10000;
    private static final float SLIDER_CURVE_POWER= 2.4f;
    private BaseXYChart loggingChartPanel = new LoggingChart();
    //private JFreeChart jfreeChart = loggingChartPanel.getChart();
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

    /**
	 * @throws Exception  
	 */
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
//        jfreeChart.getXYPlot().getDomainAxis().addChangeListener(this);
//        jfreeChart.addProgressListener(this);
        
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
        domainWidthLabel.setHorizontalAlignment(SwingConstants.LEFT);
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
        
//        jfreeChart.addChangeListener(this); // to capture dataset changes to populate row count
        loggingChartPanel.registerListeners(this);
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#axisChanged(org.jfree.chart.event.AxisChangeEvent)
	 */
    @Override
	public void axisChanged(AxisChangeEvent event) {
        Range domainRange = ((NumberAxis)event.getAxis()).getRange();
        double domainWidth = domainRange.getLength();
//        JFreeChart theChart = event.getChart(); Does not return valid chart object sometimes 11/3/13  
        JFreeChart theChart = loggingChartPanel.getChart();
        
        domainWidthLabel.setText(String.format("%1$-,3d ms",Long.valueOf((long)domainWidth)));
        
        // return if no series yet
        if (theChart.getXYPlot().getSeriesCount()==0) return;
        
        double minXVal = ((XYSeriesCollection)theChart.getXYPlot().getDataset()).getSeries(0).getMinX();
        double maxXVal = ((XYSeriesCollection)theChart.getXYPlot().getDataset()).getSeries(0).getMaxX();
        // set the flag to not update the chart because the chart is updating the slider here
        int sliderVal = (int)(domainWidth/(maxXVal-minXVal)*SLIDER_MAX);
        int working=(int)Math.pow((sliderVal*Math.pow(SLIDER_MAX, SLIDER_CURVE_POWER)), (1/(1+SLIDER_CURVE_POWER)));
//        System.out.println("sliderVal=" + sliderVal + ", working=" + working);
        sliderSetFlag=false;
//        domainScaleSlider.setValue(sliderVal);
        domainScaleSlider.setValue(working);
        // this ensures that the mouse wheel zoom works after messing with slider and not clicking on chart
        if (!theChart.isNotify()) theChart.setNotify(true);
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#chartProgress(org.jfree.chart.event.ChartProgressEvent)
	 */
    @Override
	public void chartProgress(ChartProgressEvent event) {
        JFreeChart theChart = event.getChart();
        long xval = (long)theChart.getXYPlot().getDomainCrosshairValue();
        double yval = theChart.getXYPlot().getRangeCrosshairValue();
        xYValueLabel.setText(String.format("%1$,6d : %2$,7.3f", Long.valueOf(xval), Double.valueOf(yval)));
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#stateChanged(javax.swing.event.ChangeEvent)
	 */
    @Override
	public void stateChanged(ChangeEvent e) {
        if (e.getSource()==domainScaleSlider) {
            // if not being triggered by the chart setting slider to match scale (i.e. on a zoom, resize, etc.)...
            if (sliderSetFlag) {
                // set the domain scale based on slider value
                int sliderVal=domainScaleSlider.getValue();
                int working=(int)(Math.pow(sliderVal, SLIDER_CURVE_POWER) / Math.pow(SLIDER_MAX, SLIDER_CURVE_POWER) * 
                     sliderVal);
    //                loggingChartPanel.setDomainScale(domainScaleSlider.getValue());
                ((LoggingChart)loggingChartPanel).setDomainScale(working);
            } 
            sliderSetFlag=true;
        }
    }

    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#chartChanged(org.jfree.chart.event.ChartChangeEvent)
	 */
    @Override
	public void chartChanged(ChartChangeEvent event) {
        try {
            if (event.getChart().getXYPlot().getDataset().getSeriesCount()==0) return;
            dataRowsLabel.setText(String.format("%1$-,1d rows", Integer.valueOf(event.getChart().getXYPlot().getDataset().getItemCount(0))));
        } catch (NullPointerException e) {
             // do nothing
        }

    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#setChartDirty()
	 */
    @Override
	public void setChartDirty(){
    	loggingChartPanel.setChartDirty();
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#isEmptyChart()
	 */
    @Override
	public boolean isEmptyChart(){
    	return loggingChartPanel.isEmptyChart();
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#setDomainScrolling(boolean)
	 */
    @Override
	public void setDomainScrolling(boolean doScrollDomain){
    	loggingChartPanel.setDomainScrolling(doScrollDomain);
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#setDomainLimiting(int, int)
	 */
    @Override
	public void setDomainLimiting(int limitMode, int value){
    	((LoggingChart) loggingChartPanel).setDomainLimiting(limitMode, value);
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#hasData()
	 */
    @Override
	public boolean hasData(){
    	return loggingChartPanel.hasData();
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#spawnChartCopy()
	 */
    @Override
	public boolean spawnChartCopy() throws OutOfMemoryError {
    	return loggingChartPanel.spawnChartCopy();
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#addDataPoints(double[])
	 */
    @Override
	public void addDataPoints(double[] seriesData){
        loggingChartPanel.addDataPoints(seriesData);
    }

    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#addCommentMarker(double, java.lang.String)
	 */
    @Override
	public void addCommentMarker(double xVal, String comment){
        loggingChartPanel.addCommentMarker(xVal, comment);
    }

    @Override
	public boolean axisExists(int axisIndex){
		return loggingChartPanel.axisExists(axisIndex);
	}
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#setCommentsVisible(boolean)
	 */
    @Override
	public void setCommentsVisible(boolean visible){
        loggingChartPanel.setCommentsVisible(visible);
    }
    
	/* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#setSeries(java.lang.String[])
	 */
    @Override
	public int setSeries(String[] seriesNames, int chartType){
    // ENHANCE change to not specify timestamp and do timestamp automatically
        // TODO need new chart type specifier command in protocol 
        return loggingChartPanel.setSeries(seriesNames, chartType); // TODO define series chart-type better
    }
    
    /* (non-Javadoc)
     * @see lejos.pc.charting.ChartModel#getAxisLabel(int)
     */
    @Override
	public String getAxisLabel(int axisIndex){
    	return loggingChartPanel.getAxisLabel(axisIndex);
    }
    
    /* (non-Javadoc)
     * @see lejos.pc.charting.ChartModel#setAxisLabel(int, java.lang.String)
     */
    @Override
	public void setAxisLabel(int axisIndex, String axisLabel){ 
        loggingChartPanel.setAxisLabel(axisIndex, axisLabel);
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#setChartTitle(java.lang.String)
	 */
    @Override
	public void setChartTitle(String title) {
        loggingChartPanel.setChartTitle(title);
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#getChartTitle()
	 */
    @Override
	public String getChartTitle() {
        return loggingChartPanel.getChartTitle();
    }
    
    /* (non-Javadoc)
	 * @see lejos.pc.charting.ChartModel#copyChart()
	 */
    @Override
	public void copyChart() {
        loggingChartPanel.doCopy();
    }
}
