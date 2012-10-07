package lejos.pc.charting;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Communications provider contract used by the Charting Logger to manage connections and I/O streams from the target.
 * 
 * @author Kirk P. Thompson
 *
 */
public interface ConnectionProvider {

	/**Connect to a listening device. The device must be waiting for a connection and this method implementation must
	 * establish a connection to it.
	 * <p>
	 * This method is called by lejos.pc.charting.LogChartFrame.closeCurrentConnection() when:
	 * <ol>
	 * <li>The user clicks the Connect button
	 * </ol>
	 * @param deviceID The name or address of the device to connect to. 
	 * @return <code>true</code> if successful connection with Data input/output streams established. <code>false</code>
	 * if the connection failed.
	 * 
	 */
	public boolean connect(String deviceID);

	/** Is there a current valid connection?
	 * @return <code>true</code> if so
	 */
	public boolean isConnected();

	/** Return the name/ID of the device last successfully connected to.
	 * @return name of the NXT
	 */
	public String getConnectedName();

	/** Return the <code>InputStream</code> from the device.
	 * @return the <code>InputStream</code>
	 */
	public abstract InputStream getInputStream();

	/** Return the <code>OutputStream</code> to the device.
	 * @return the <code>OutputStream</code>
	 */
	public OutputStream getOutputStream();

	/** Flush the streams, close the connection to the connected device  and clean up. Called by 
	 * lejos.pc.charting.LogChartFrame.closeCurrentConnection() when:
	 * <ol>
	 * <li>An EOFException (or other IOException) occurs from the LoggerComms instance
	 * <li>The user clicks the Disconnect button
	 * <li>The windowClosing event executes from lejos.pc.charting.ChartingLogger
	 * </ol>
	 * @see #connect
	 */
	public void closeConnection();

}