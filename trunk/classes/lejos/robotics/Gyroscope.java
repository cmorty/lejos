package lejos.robotics;

/**  Abstraction for Gryoscope defines minimal implementation
 * 
 * @author Kirk P. Thompson 4/7/2011
 */
public interface Gyroscope {
    /** Implementor must calculate and return the angular velocity in degrees per second.
     * @return Angular velocity in degrees/second
     */
    public double getAngularVelocity();

    /** Implementor must calculate and set the offset/bias value for use in <code>getAngularVelocity()</code>.
     */
    public void setOffset();

 }
