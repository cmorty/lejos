package lejos.nxt.addon.tetrix;

import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;

/**HiTechnic Tetrix Motor and Servo controller factory class. 
 * 
 * Motor and Servo controller abstractions are provided by this class
 * to use to obtain motor and servo instances. These abstraction classes are <code>TetrixMotorController</code> and 
 * <code>TetrixServoController</code>, respectively.
 * <p>
 * Motor and servo controllers are enumerated starting at the controller connected to one of the NXT's sensor ports and then
 * working outwards along the daisy chain. 4 controllers can be daisy-chained, with a mixture of servo and/or motor controllers.
 * No other sensors can be connected to the daisy-chain.
 * 
 * <p>Code Example:<br>
 * <pre>
 * // Instantiate the factory and get a Motor and servo controller. We assume that there is one of 
 * // each daisy-chained.
 * TetrixControllerFactory cf = new TetrixControllerFactory(SensorPort.S1);
 * TetrixMotorController mc = cf.newMotorController();
 * TetrixServoController sc = cf.newServoController();
 * 
 * // Display the voltage from the motor controller
 * System.out.println("v=" + mc.getVoltage());'
 * 
 * // Get an encoder motor instance. The physical motor (with encoder) is connected to the Motor 2 terminals on the controller
 * TetrixEncoderMotor mot1 = mc.getEncoderMotor(TetrixMotorController.MOTOR_2);
 * </pre>
 * 
 * @author Kirk P. Thompson
 */
public class TetrixControllerFactory extends I2CSensor {
    private static final int MAX_CHAINED_CONTROLLERS=4;
    private static final String TETRIX_VENDOR_ID = "HiTechnc";
    private static final String TETRIX_MOTORCON_PRODUCT_ID = "MotorCon";
    private static final String TETRIX_SERVOCON_PRODUCT_ID = "ServoCon";
    
    private int currentMotorIndex=0;
    private int currentServoIndex=0;
    
    /**
     * Instantiate a <code>TetrixControllerFactory</code> using the specified NXT sensor port.
     * @param port The NXT sensor port the Tetrix controller is connected to
     */
    public TetrixControllerFactory(I2CPort port){
        super(port, I2CPort.LEGO_MODE);
    }
    
    /**
     * @param i where to start searching index-wise
     * @param product the product ID string
     * @return the index the product ID was found. -1 if not found or outside MAX_CHAINED_CONTROLLERS bounds
     */
    private int findProduct(int i, String product){
        if (i<0 || i>=MAX_CHAINED_CONTROLLERS) return -1;
        for (;i<MAX_CHAINED_CONTROLLERS;i++) {
            address=(i + 1) * 2;
            if (getVendorID().equalsIgnoreCase(TETRIX_VENDOR_ID) && getProductID().equalsIgnoreCase(product)) return i;
        }
        return -1;
    }
    
    /**
     * Get the next available Tetrix Motor controller. Servo controllers in the daisy-chain are skipped in the search.
     * <p>
     * Successive controllers in a daisy-chain go "outwards" from controller closest to the NXT as #1 to #4 for each controller
     * in the chain. Once a specific controller has been retrieved using this method, it cannot be retrieved again.
     * <p>
     * A combination of Servo and Motor controllers can be daisy-chained. 
     * @return The next available <code>TetrixMotorController</code> instance.
     * @throws IllegalStateException If no more motor controllers can be returned. If there are no motor controllers
     * in the daisy-chain, this exception is also thrown.
     * @see #newServoController
     */
    public TetrixMotorController newMotorController()
    {
        this.currentMotorIndex = findProduct(this.currentMotorIndex, TETRIX_MOTORCON_PRODUCT_ID);
        if (this.currentMotorIndex<0) throw new IllegalStateException("no motor controllers available");
        TetrixMotorController mc = new TetrixMotorController(this.port, (this.currentMotorIndex + 1) * 2);
        this.currentMotorIndex++;
        return mc;
    }
    
    /**
     * Get the next available Tetrix Servo controller. Motor controllers in the daisy-chain are skipped in the search.
     * <p>
     * Successive controllers in a daisy-chain go "outwards" from controller closest to the NXT as #1 to #4 for each controller
     * in the chain. Once a specific controller has been retrieved using this method, it cannot be retrieved again.
     * <p>
     * A combination of Servo and Motor controllers can be daisy-chained.
     * @return The next available <code>TetrixServoController</code> instance.
     * @throws IllegalStateException If no more servo controllers can be returned. If there are no servo controllers
     * in the daisy-chain, this exception is also thrown.
     * @see #newMotorController
     */
    public TetrixServoController newServoController()
    {
        this.currentServoIndex = findProduct(this.currentServoIndex, TETRIX_SERVOCON_PRODUCT_ID);
        if (this.currentServoIndex<0) throw new IllegalStateException("no servo controllers available");
        TetrixServoController sc = new TetrixServoController(this.port, (this.currentServoIndex + 1) * 2);
        this.currentServoIndex++;
        return sc;
    }
}
