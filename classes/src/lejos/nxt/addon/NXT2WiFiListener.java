package lejos.nxt.addon;

/**
 * This listener listens for Web Events from the NXT2WIFI sensor. Use the method NXT2WIFI.addListener() to
 * register the listener. 
 * 
 * @author BB
 *
 */
public interface NXT2WiFiListener {
	
	/**
	 * This method is called whenever a web event is generated from a web page on the NXT2WIFI server.
	 * 
	 * @param controlType The type of web widget that generated event. button = 0, slider = 1, checkbox = 2 
	 * @param controlID  The id of the widget, 0..255, to tell you which widget was activated.
	 * @param event Indicates state of widget 0..255. e.g. 0 is not pressed, 1 is pressed.
	 * @param value The data value of the widget, 0..255.
	 */
	public void webEventReceived(byte controlType, byte controlID, byte event, byte value);
	
}
