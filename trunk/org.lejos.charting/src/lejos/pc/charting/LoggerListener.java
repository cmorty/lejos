package lejos.pc.charting;

/** 
 * Change listener to notify of events when log data has been received, a data stream EOF has occurred, a comment has been received, and 
 * header defs have been changed by the NXT-side <code>lejos.util.NXTDataLogger</code>.
 * @see  LoggerProtocolManager#addLoggerListener
 * @author Kirk P. Thompson
 */
public interface LoggerListener {
    /** 
     * Invoked when a log line [whose fields are defined in <code>NXTDataLogger.setColumns()</code>] is logged and 
     * <code>NXTDataLogger.finishLine()</code> is invoked.
     * <p>
     * Each logged data field/column is represented by a 
     * <code>DataItem</code> instance which provides the datatype and value in a wrapper class.
     * @param logDataItems The array of <code>DataItem</code> instances representing a line of logged data.
     * @see DataItem
     * @see lejos.util.NXTDataLogger
     */
    void logLineAvailable(DataItem[] logDataItems);

    /**
     * Invoked when an <code>EOFException</code> (or other <code>IOException</code>) occurs from the <code>LoggerComms</code>
     * instance.
     * @see LoggerComms
     */
    void dataInputStreamEOF();

    /** 
     * Invoked when the log field headers are initially set or changed. This is important because the number of headers
     *  determines the column count (which affects cycling). 
     *  <p>
     *  Different chart types can be requested via the protocol
     *  <p>
     *  The string format/structure of each string field passed by NXTDataLogger is:<br>
     *  <code>[name]![y or n to indicate if charted]![axis ID 1-4]</code>
     *  <br>i.e. <pre>"MySeries!y!1"</pre>
     * @param logFields The array of header values
     * @param chartType The type of chart/plot the charting logger needs to display. Defined in ChartModel. 
     * //TODO firm up this description
     * 
     * @see LoggerProtocolManager#CT_XY_TIMEDOMAIN
     * @see LoggerProtocolManager#CT_XY_SCATTER
     * @see LoggerProtocolManager#CT_XYZ_BUBBLE
     * @see LoggerProtocolManager#CT_XY_POLAR
     * @see lejos.util.LogColumn
     */
    void logFieldNamesChanged(String[] logFields, int chartType);

    /** 
     * Invoked when a comment is logged. Comments are sent after the <code>finishLine()</code> method completes. In 
     * <code>NXTChartingLogger</code>, comments are displayed on the chart as an event marker on the domain axis with 
     * the comment text as a label and the timestamp as the domain (X) value of the marker.
     * @param timestamp The timestamp when the comment was generated on the NXT
     * @param comment The text comment
     */
    void logCommentReceived(int timestamp, String comment);
    
    /**
     * Invoked when a pass-through message is received from <code>NXTDataLogger</code>. 
     * 
     * @param message an array that contains the passthrough message
     * @see lejos.util.NXTDataLogger.registerTunnelManager()
     */
    @SuppressWarnings("javadoc")
	void tunneledMessageReceived(byte[] message);
}
