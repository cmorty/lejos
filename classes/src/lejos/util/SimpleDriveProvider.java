package lejos.util;

/**
 * An interface used for creating a simple robot remote control provider through NXTDataLogger passthrough 
 * messaging via a <code>SimpleDrive</code> instance.
 * <P>
 * Implement and pass an instance to <code>SimpleDrive</code> to remote control your robot from NXT Charting Logger
 * while you [optionally] log and chart data.
 * 
 * @author Kirk P. Thompson
 * @see SimpleDrive
 * @see NXTDataLogger
 */
public interface SimpleDriveProvider {
	/**
	 * Command button "Forward" state from NXT Charting Logger Simple Drive GUI. 
	 * <code>keyPressed</code> indicates whether the button was pressed or released.
	 * @param keyPressed <code>true</code> means button was pressed, <code>false</code> means released.
	 */
	void driveForward(boolean keyPressed);
	/**
	 * Command button "Backward" state from NXT Charting Logger Simple Drive GUI. 
	 * <code>keyPressed</code> indicates whether the button was pressed or released.
	 * @param keyPressed <code>true</code> means button was pressed, <code>false</code> means released.
	 */
	void driveBackward(boolean keyPressed);
	/**
	 * Command button "Turn Left" state from NXT Charting Logger Simple Drive GUI. 
	 * <code>keyPressed</code> indicates whether the button was pressed or released.
	 * @param keyPressed <code>true</code> means button was pressed, <code>false</code> means released.
	 */
	void driveTurnLeft(boolean keyPressed);
	/**
	 * Command button "Turn Right" state from NXT Charting Logger Simple Drive GUI. 
	 * <code>keyPressed</code> indicates whether the button was pressed or released.
	 * @param keyPressed <code>true</code> means button was pressed, <code>false</code> means released.
	 */
	void driveTurnRight(boolean keyPressed);
	/**
	 * The power 0-100% value to set from NXT Charting Logger Simple Drive GUI.
	 * @param value The power 0-100%
	 */
	void setPower(int value);
	/**
	 * The power 0-100% value to send to NXT Charting Logger Simple Drive GUI.
	 * @return The current power value 0-100%
	 */
	int getPower();
	/**
	 * Provide a custom Command Button 1 label to NXT Charting Logger Driver GUI. 
	 * @return The label to use for the button. Return <code>null</code> to leave current label intact.
	 */
	String getCommand1Label();
	/**
	 * Provide a custom Command Button 2 label to NXT Charting Logger Driver GUI. 
	 * @return The label to use for the button. Return <code>null</code> to leave current label intact.
	 */
	String getCommand2Label();
	/**
	 * Provide a custom Command Button 3 label to NXT Charting Logger Driver GUI. 
	 * @return The label to use for the button. Return <code>null</code> to leave current label intact.
	 */
	String getCommand3Label();
	/**
	 * Provide a custom Command Button 4 label to NXT Charting Logger Driver GUI. 
	 * @return The label to use for the button. Return <code>null</code> to leave current label intact.
	 */
	String getCommand4Label();
	/**
	 * Command 1 button state from NXT Charting Logger Simple Drive GUI. 
	 * <code>keyPressed</code> indicates whether the button was pressed or released.
	 * @param keyPressed <code>true</code> means button was pressed, <code>false</code> means released.
	 */
	void doCommand1(boolean keyPressed);
	/**
	 * Command 2 button state from NXT Charting Logger Simple Drive GUI. 
	 * <code>keyPressed</code> indicates whether the button was pressed or released.
	 * @param keyPressed <code>true</code> means button was pressed, <code>false</code> means released.
	 */
	void doCommand2(boolean keyPressed);
	/**
	 * Command 3 button state from NXT Charting Logger Simple Drive GUI. 
	 * <code>keyPressed</code> indicates whether the button was pressed or released.
	 * @param keyPressed <code>true</code> means button was pressed, <code>false</code> means released.
	 */
	void doCommand3(boolean keyPressed);
	/**
	 * Command 4 button state from NXT Charting Logger Simple Drive GUI. 
	 * <code>keyPressed</code> indicates whether the button was pressed or released.
	 * @param keyPressed <code>true</code> means button was pressed, <code>false</code> means released.
	 */
	void doCommand4(boolean keyPressed);
	
	/**
	 * Sets the value from Value 1 field in NXT Charting Logger Simple Drive GUI.
	 *  
	 * @param value The <code>float</code> value sent from NXT Charting Logger
	 */
	void setValue1(float value);
	
	/**
	 * Return the value to Value 1 field in NXT Charting Logger Simple Drive GUI. Polled at
	 * startup and when user-requested.
	 * 
	 * @return the <code>float</code> value you provide in your implementation.
	 */
	float getValue1();
	
	/**
	 * Sets the value from Value 2 field in NXT Charting Logger Simple Drive GUI.
	 *  
	 * @param value The <code>float</code> value sent from NXT Charting Logger
	 */
	void setValue2(float value);
	
	/**
	 * Return the value to Value 2 field in NXT Charting Logger Simple Drive GUI. Polled at
	 * startup and when user-requested.
	 * 
	 * @return the <code>float</code> value you provide in your implementation.
	 */
	float getValue2();
	
	/**
	 * Sets the value from Value 3 field in NXT Charting Logger Simple Drive GUI.
	 *  
	 * @param value The <code>float</code> value sent from NXT Charting Logger
	 */
	void setValue3(float value);
	
	/**
	 * Return the value to Value 3 field in NXT Charting Logger Simple Drive GUI. Polled at
	 * startup and when user-requested.
	 * 
	 * @return the <code>float</code> value you provide in your implementation.
	 */
	float getValue3();
	
	/**
	 * Sets the value from Value 4 field in NXT Charting Logger Simple Drive GUI.
	 *  
	 * @param value The <code>float</code> value sent from NXT Charting Logger
	 */
	void setValue4(float value);
	
	/**
	 * Return the value to Value 4 field in NXT Charting Logger Simple Drive GUI. Polled at
	 * startup and when user-requested.
	 * 
	 * @return the <code>float</code> value you provide in your implementation.
	 */
	float getValue4();
	
	/**
	 * Provide a custom Value 1 field label to NXT Charting Logger Driver GUI. 
	 * @return The label to use for the Value 1 field. Return <code>null</code> to leave current label intact.
	 */
	String getValue1Label();
	
	/**
	 * Provide a custom Value 2 field label to NXT Charting Logger Driver GUI. 
	 * @return The label to use for the Value 2 field. Return <code>null</code> to leave current label intact.
	 */
	String getValue2Label();
	
	/**
	 * Provide a custom Value 3 field label to NXT Charting Logger Driver GUI. 
	 * @return The label to use for the Value 3 field. Return <code>null</code> to leave current label intact.
	 */
	String getValue3Label();
	
	/**
	 * Provide a custom Value 4 field label to NXT Charting Logger Driver GUI. 
	 * @return The label to use for the Value 4 field. Return <code>null</code> to leave current label intact.
	 */
	String getValue4Label();
}
