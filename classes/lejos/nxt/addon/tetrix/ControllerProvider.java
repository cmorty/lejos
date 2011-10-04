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
    
    private final I2CPort i2cport;
    // those arrays are Object[] in order to not pull in all classes
    private Object[] motorControllers = new Object[MAX_CHAINED_CONTROLLERS];
    private Object[] servoControllers = new Object[MAX_CHAINED_CONTROLLERS];
    private int motorControllerCount = 0;
    private int servoControllerCount = 0;

    /** 
     * Instantiate a <code>ControllerProvider</code> using the specified NXT sensor port.
     * @param port The NXT port the controller is connected to
     */
    public ControllerProvider(I2CPort port) throws IllegalStateException{
        i2cport = port;
    }
    
    //TODO replace this workaround with some function from internal utility class
    static class DummySensor extends I2CSensor {
        public DummySensor(I2CPort port, int address) {
            super(port, address, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
        }

        @Override
        public String getProductID() {
            // TODO Auto-generated method stub
            return super.getProductID();
        }
        
        @Override
        public String getSensorType() {
            // TODO Auto-generated method stub
            return super.getSensorType();
        }
    }

    /**
     * Automatically detect and add all Tetrix devices attached to this daisy chain.
     * Calling this method will remove any previously added motor or servo controllers.
     * 
     * @return the number of devices discovered
     */
    public int autoDetect() {
        
        this.motorControllerCount = 0;
        this.servoControllerCount = 0;
        
        // spin through and ID sensor types
        for (int i = 0; i < MAX_CHAINED_CONTROLLERS; i++) {
            int address = I2CADDRESS_DEVICE0 << i;
            DummySensor s = new DummySensor(this.i2cport, address);
            String sID = s.getProductID(); // TODO use full names for these once the I2C stuff gets fixed
            String sType = s.getSensorType();

            if (sID.equalsIgnoreCase("HiTechnc")) { 
                if (sType.equalsIgnoreCase("MotorCon")) {
                    this.motorControllers[this.motorControllerCount++] = new MotorController(this.i2cport, address);
                } else if (sType.equalsIgnoreCase("ServoCon")) {
                    this.servoControllers[this.servoControllerCount++] = new ServoController(this.i2cport, address);
                } else {
                    throw new RuntimeException("unknown controller typer "+sType);
                }
            } else {
                throw new RuntimeException("unknown product ID "+sID);
            }
        }
        
        for (int i=motorControllerCount; i < MAX_CHAINED_CONTROLLERS; i++)
            this.motorControllers[i] = null;
        
        for (int i=servoControllerCount; i < MAX_CHAINED_CONTROLLERS; i++)
            this.servoControllers[i] = null;
        
        return motorControllerCount + servoControllerCount;
    }
    
    /**
     * Manually adds a Tetrix motor controller to this daisy chain.
     * The returned motor controller can be used directly. The same motor
     * controller instance can also be obtained using {@link #getMotorControllers()},
     * after all controllers have been added to the daisy chain.
     * 
     * @return the motor controller
     */
    public MotorController addMotorController()
    {
        int i = this.motorControllerCount + this.servoControllerCount;
        if (i >= MAX_CHAINED_CONTROLLERS)
            throw new IllegalStateException("no more controllers allowed");
        
        MotorController r = new MotorController(this.i2cport, I2CADDRESS_DEVICE0 << i);
        this.motorControllers[this.motorControllerCount++] = r;
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
    public ServoController addServoController()
    {
        int i = this.motorControllerCount + this.servoControllerCount;
        if (i >= MAX_CHAINED_CONTROLLERS)
            throw new IllegalStateException("no more controllers allowed");
        
        ServoController r = new ServoController(this.i2cport, I2CADDRESS_DEVICE0 << i);
        this.servoControllers[this.servoControllerCount++] = r;
        return r;
    }
    
    /**
     * Get all available Motor controllers.
     * The array contains the motor controllers in the order they occur in the daisy chain.
     * The method returns a newly created copy of an array. Hence the returned array may be modified. 
     * 
     * @return An array of all <code>MotorController</code> in the daisy chain.
     * @see #getServoController
     */
    public MotorController[] getMotorControllers() throws IllegalStateException {
        MotorController[] r = new MotorController[this.motorControllerCount];
        System.arraycopy(this.motorControllers, 0, r, 0, this.motorControllerCount);
        return r;
    }
    
     /**
      * Get the all available Servo controllers.
      * The array contains the servo controllers in the order they occur in the daisy chain.
      * The method returns a newly created copy of an array. Hence the returned array may be modified. 
      * 
      * @return The next available servo controller.
      * @throws IllegalStateException If no more servo controllers can be returned. If there are no servo controllers
      * in the daisy-chain, this exception is also thrown.
      * @see #getMotorController
      */
    public ServoController[] getServoControllers() throws IllegalStateException { 
        ServoController[] r = new ServoController[this.motorControllerCount];
        System.arraycopy(this.motorControllers, 0, r, 0, this.motorControllerCount);
        return r;
    }


}
