package lejos.nxt.sensor.api;

/** 
 * A multipleSampleProvider represents a sensor that can provide different kind of samples having different quantities. <P>
 * 
 * @author Aswin
 *
 */

public interface MultipleSampleProvider {
	public int[] getSupportedQuantities() ;
	public Object getSampleProvider(int quantity);
}
