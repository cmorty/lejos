package lejos.nxt.addon.tetrix;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;

/**
 * This class represents a daisy chain of HiTechnic Tetrix Motor and Servo controllers.
 * The daisy chain can be configured manually by calling {@link #addMotorController()} and {@link #addServoController()}
 * in the order the devices are attached to the port. It also supports the automatic detection of the controllers 
 * in a daisy-chain. It then provides them as instances of the
 * <code>MotorController</code> and <code>ServoController</code> classes. Those can be obtained by calling
 * {@link #getMotorControllers()} and {@link #getServoControllers()}.
 * <p>
 * Motor and Servo controller abstractions are provided by this class
 * to use to obtain motor and servo instances. These abstraction classes are <code>MotorController</code> and 
 * <code>ServoController</code>, respectively.
 * <p>
 * Motor and servo controllers are enumerated starting at the controller connected to one of the NXT's sensor ports and then
 * working outwards along the daisy chain. 4 controllers can be daisy-chained, with a mixture of servo and/or motor controllers.
 * <p>Code Example:<br>
 * <code>
 *  {@link ControllerProvider} daisyChain = new {@link ControllerProvider}({@link SensorPort#S1});<br>
 *  daisyChain.{@link #addMotorController()};<br>
 *  daisyChain.{@link #addServoController()};<br>
 *  daisyChain.{@link #addMotorController()};<br>
 *  {@link MotorController}[] r1 = daisyChain.{@link #getMotorControllers()}; //will return 2 motor controller<br>
 *  {@link ServoController}[] r2 = daisyChain.{@link #getServoControllers()}; //will return 1 servo controller<br>
 * </code>
 * 
 * @author Kirk P. Thompson
 */
public class ControllerProvider {
    /** I2C address of the first device in a Tetrix daisy chain. */
    public static final int I2CADDRESS_DEVICE0 = 0x02;
    /** I2C address of the second device in a Tetrix daisy chain. */
    public static final int I2CADDRESS_DEVICE1 = I2CADDRESS_DEVICE0 << 1;
    /** I2C address of the third device in a Tetrix daisy chain. */
    public static final int I2CADDRESS_DEVICE2 = I2CADDRESS_DEVICE0 << 2;
    /** I2C address of the fourth device in a Tetrix daisy chain. */
    public static final int I2CADDRESS_DEVICE3 = I2CADDRESS_DEVICE0 << 3;
    
    private static final int MAX_CHAINED_CONTROLLERS=4;
    
    private static final String TETRIX_VENDOR_ID = "HiTechnc";
    private static final String TETRIX_MOTORCON_PRODUCT_ID = "MotorCon";
    private static final String TETRIX_SERVOCON_PRODUCT_ID = "ServoCon";
    
    private final I2CPort i2cport;
    private int currentPosition;

    /** 
     * Instantiate a <code>ControllerProvider</code> using the specified NXT sensor port.
     * @param port The NXT port the controller is connected to
     */
    public ControllerProvider(I2CPort port) throws IllegalStateException{
        i2cport = port;
    }
    
    private void verifyType(I2CSensor s, String product) {
        
        String sID = s.getVendorID(); // TODO use full names for these once the I2C stuff gets fixed
        String sType = s.getProductID();
        
        if (sID.equals("") || sType.equals("")) {
        	throw new RuntimeException("controller does not respond, i2c error");
        }
        if (!sID.equalsIgnoreCase(TETRIX_VENDOR_ID)) { 
            throw new RuntimeException("wrong vendor ID "+sID);
        }
        if (!sType.equalsIgnoreCase(product)) {
            throw new RuntimeException("wrong product ID "+sType);
        }
    }
    
    /**
     * Manually adds a Tetrix motor controller to this daisy chain.
     * The returned motor controller can be used directly. The same motor
     * controller instance can also be obtained using {@link #getMotorControllers()},
     * after all controllers have been added to the daisy chain.
     * 
     * @return the motor controller
     */
    public MotorController nextMotorController()
    {
        if (this.currentPosition >= MAX_CHAINED_CONTROLLERS)
            throw new IllegalStateException("no more controllers allowed");
        
        MotorController r = new MotorController(this.i2cport, I2CADDRESS_DEVICE0 << this.currentPosition);
        this.verifyType(r, TETRIX_MOTORCON_PRODUCT_ID);
        currentPosition++;
        return r;
    }
    
    /**
     * Manually add a Tetrix servo controller to this daisy chain.
     * The returned servo controller can be used directly. The same servo
     * controller instance can also be obtained using {@link #getServoControllers()},
     * after all controllers have been added to the daisy chain.
     * 
     * @return
     */
    public ServoController nextServoController()
    {
        if (this.currentPosition >= MAX_CHAINED_CONTROLLERS)
            throw new IllegalStateException("no more controllers allowed");
        
        ServoController r = new ServoController(this.i2cport, I2CADDRESS_DEVICE0 << this.currentPosition);
        this.verifyType(r, TETRIX_SERVOCON_PRODUCT_ID);
        currentPosition++;
        return r;
    }
}
