package lejos.pc.charting;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;

/**
 * Base class for chart implementations.
 * 
 * @author Kirk P. Thompson
 *
 */
public abstract class ChartModel extends JPanel implements ChangeListener, AxisChangeListener, ChartProgressListener, ChartChangeListener  {

	/** Add a comment marker to the chart at specified domain position.
	 * @param xVal Domain value
	 * @param comment The comment text
	 * @see #setCommentsVisible
	 */
	public abstract void addCommentMarker(double xVal, String comment);

	/**Add series data to the dataset. Pass an array of <code>double</code> series values that all share the same domain value 
	 * defined in element 0. 
	 * The number of values must match the header count in setSeries().
	 * <p>
	 * Element 0 is the domain (X) series and should be a timestamp.
	 * @param seriesData the series data as <code>double</code>s
	 * @see #setSeries
	 */
	public abstract void addDataPoints(double[] seriesData);

	/* (non-Javadoc)
	 * @see org.jfree.chart.event.AxisChangeListener#axisChanged(org.jfree.chart.event.AxisChangeEvent)
	 */
	public abstract void axisChanged(AxisChangeEvent event);

	/**
	 * @param axisIndex The axis index to test for
	 * @return true if the axis has been defined
	 */
	public abstract boolean axisExists(int axisIndex);
	
	/* (non-Javadoc)
	 * @see org.jfree.chart.event.ChartChangeListener#chartChanged(org.jfree.chart.event.ChartChangeEvent)
	 */
	public abstract void chartChanged(ChartChangeEvent event);

	/* (non-Javadoc)
	 * @see org.jfree.chart.event.ChartProgressListener#chartProgress(org.jfree.chart.event.ChartProgressEvent)
	 */
	public abstract void chartProgress(ChartProgressEvent event);

	/**
	 * Copies the current chart image to the system clipboard.
	 * 
	 */
	public abstract void copyChart();

	/**
	 * TODO define axis meaning and how many per chart type
	 * 
	 * @param axisIndex The axis index you want the label from
	 * @return The label for given index. null if not exist.
	 */
	public abstract String getAxisLabel(int axisIndex);
	
	/**
	 * @param axisIndex The axis index to set the label for
	 * @param axisLabel The label
	 */
	public abstract void setAxisLabel(int axisIndex, String axisLabel);
	
	/**
	 * @return The chart's main title
	 */
	public abstract String getChartTitle();

	/**
	 * If any data has been added to the data series (using timestamp series)
	 * @return true if chart has data
	 */
	public abstract boolean hasData();

	/**
	 * 
	 * @return true if chart has no series defined
	 */
	public abstract boolean isEmptyChart();

	/**
	 * Flags to repaint the chart
	 */
	public abstract void setChartDirty();

	/**
	 * Set the chart title.
	 * 
	 * @param title
	 */
	public abstract void setChartTitle(String title);

	/** Control the visibility of any comment markers defined for the chart.
	 * @param visible <code>true</code> to show, <code>false</code> to hide
	 * @see #addCommentMarker
	 */
	public abstract void setCommentsVisible(boolean visible);

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
	public abstract void setDomainLimiting(int limitMode, int value);

	/**
	 * Set domain scrolling.
	 * 
	 * @param doScrollDomain true to tell chart to scroll domain as data comes in.
	 */
	public abstract void setDomainScrolling(boolean doScrollDomain);

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
	public abstract int setSeries(String[] seriesNames);

	/**
	 * Spawn a copy of the current chart in a new window.
	 * 
	 * @return true for success
	 * @throws OutOfMemoryError
	 */
	public abstract boolean spawnChartCopy() throws OutOfMemoryError;

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public abstract void stateChanged(ChangeEvent e);

}