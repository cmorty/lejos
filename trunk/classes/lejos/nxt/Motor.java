package lejos.nxt;

/**
 * Motor class contains 3 instances of regulated motors.
 * <p>
 * Example:<p>
 * <code><pre>
 *   Motor1.A.setSpeed(720);// 2 RPM
 *   Motor1.C.setSpeed(720);
 *   Motor1.A.forward();
 *   Motor1.C.forward();
 *   Thread.sleep (1000);
 *   Motor1.A.stop();
 *   Motor1.C.stop();
 *   Motor1.A.rotateTo( 360);
 *   Motor1.A.rotate(-720,true);
 *   while(Motor1.A.isRotating() :Thread.yield();
 *   int angle = Motor1.A.getTachoCount(); // should be -360
 *   LCD.drawInt(angle,0,0);
 * </pre></code>
 * @author Roger Glassey/Andy Shaw
 */
public class Motor
{
    /**
     * Motor A.
     */
    public static final NXTRegulatedMotor A = new NXTRegulatedMotor(MotorPort.A);
    /**
     * Motor B.
     */
    public static final NXTRegulatedMotor B = new NXTRegulatedMotor(MotorPort.B);
    /**
     * Motor C.
     */
    public static final NXTRegulatedMotor C = new NXTRegulatedMotor(MotorPort.C);

}
