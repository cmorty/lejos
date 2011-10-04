package lejos.nxt.addon.tetrix;

import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

/** HiTechnic Tetrix Motor and Servo controller base class. When instantiated, any single controller or 
 * all controllers in a daisy-chain are discovered and made available as instances of the
 * <code>MotorController</code> and <code>ServoController</code> classes.
 * <p>
 * Motor and Servo controller abstractions are provided by this class
 * to use to obtain motor and servo instances. These abstraction classes are <code>MotorController</code> and 
 * <code>ServoController</code>, respectively.
 * <p>
 * Motor and servo controllers are enumerated starting at the controller connected to one of the NXT's sensor ports and then
 * working outwards along the daisy chain. 4 controllers can be daisy-chained, with a mixture of servo and/or motor controllers.
 * 
 * @author Kirk P. Thompson
 */
public class ControllerProvider {
    private static final int MAX_CHAINED_CONTROLLERS=4;
    
    private final I2CPort sensorPortNXT;
    private MotorController[] motorControllers;
    private ServoController[] servoControllers;
    private int motorControllerCount = 0;
    private int motorControllerIndex = 0;
    private int servoControllerCount = 0;
    private int servoControllerIndex = 0;

    /** Instantiate a <code>ControllerProvider</code> using the specified NXT sensor port.
     * @param port The NXT port the controller is connected to
     * @throws IllegalStateException if no Hitechnic Motor or Servo controllers could be found
     */
    public ControllerProvider(I2CPort port) throws IllegalStateException{
        sensorPortNXT = port;
        enumerateControllers();
    }

    private void enumerateControllers() throws IllegalStateException {
        class GetControllers extends I2CSensor {
            private static final int TYPE_MOTOR = 1;
            private static final int TYPE_SERVO = 2;
            
            private int[] controllerType = new int[MAX_CHAINED_CONTROLLERS];

            GetControllers(I2CPort sensorPort) throws IllegalStateException {
                super(sensorPort, 0x02, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
                String sID, sType;
                
                for (int i = 0; i < MAX_CHAINED_CONTROLLERS; i++) controllerType[i]=-1;
                
                // spin through and ID sensor types
                for (int i = 0; i < MAX_CHAINED_CONTROLLERS; i++) {
                    this.address = (i + 1) * 2;
                    sID = getProductID(); // TODO use full names for these once the I2C stuff gets fixed
                    if (sID.length() == 0) sID = "n/a";
//                    for (int j=0;j<sID.length();j++) {
//                        System.out.print(Integer.valueOf(sID.charAt(j))+":");
//                    }
//                    Button.waitForAnyPress();

                    sType = getSensorType();
                    if (sType.length() == 0) sType = "-";
                    //System.out.println(address + " " + sID + "," + sType); // TODO remove after testing

                    if (sID.equalsIgnoreCase("HiTechnc")) { 
                        if (sType.equalsIgnoreCase("MotorCon")) {
                            controllerType[i] = TYPE_MOTOR;
                            motorControllerCount++;
                        }
                        if (sType.equalsIgnoreCase("ServoCon")) {
                            controllerType[i] = TYPE_SERVO;
                            servoControllerCount++;
                        } 
                    }
                }
                
                // no controllers? Throw exception
                if (motorControllerCount == 0 && servoControllerCount == 0) {
                    throw new IllegalStateException("No controllers found");
                }
            }

            MotorController[] getMotorControllers() {
                MotorController[] theMotors = new MotorController[motorControllerCount];
                int index = 0;
                for (int i = 0; i < MAX_CHAINED_CONTROLLERS; i++) {
                    this.address = (i + 1) * 2;
                    if (controllerType[i] == TYPE_MOTOR) {
                        theMotors[index++] = new MotorController(sensorPortNXT, this.address);
                    }
                }
                return theMotors;
            }
            
            ServoController[] getServoControllers() {
                ServoController[] theServos = new ServoController[servoControllerCount];
                int index = 0;
                for (int i = 0; i < MAX_CHAINED_CONTROLLERS; i++) {
                    this.address = (i + 1) * 2;
                    if (controllerType[i] == TYPE_SERVO) {
                        theServos[index++] = new ServoController(sensorPortNXT, this.address);
                    }
                }
                return theServos;
            }
            
        }
        GetControllers controllerFinder = new GetControllers(sensorPortNXT);
        // get array of discovered motor controllers
        if (motorControllerCount==0) {
            motorControllerCount=-1; // will cause IllegalStateException on getMotorController()
        } else {
            motorControllers = controllerFinder.getMotorControllers();
        }
        // get array of discovered servo controllers
        if (servoControllerCount==0) {
            servoControllerCount=-1; // will cause IllegalStateException on getServoController()
        } else {
            servoControllers = controllerFinder.getServoControllers();
        }
        controllerFinder=null;
    }

    /**Get the next available Motor controller.
     * Successive controllers in a daisy-chain go "outwards" from controller closest to the NXT as #1 to #4 for each controller
     * in the chain. Once a controller has been retrieved using this method, it cannot be retrieved again.
     * <p>
     * A combination of Servo and Motor controllers can be daisy-chained.
     * @return The next available <code>MotorController</code>.
     * @throws IllegalStateException If no more motor controllers can be returned. If there are no motor controllers
     * in the daisy-chain, this exception is also thrown.
     * @see #getServoController
     */
    public MotorController getMotorController() throws IllegalStateException {
        if (motorControllerIndex>motorControllerCount) throw new IllegalStateException("No available motor controllers");
        return motorControllers[motorControllerIndex++];
    }
    
     /**Get the next available Servo controller.
      * Successive controllers in a daisy-chain go "outwards" from controller closest to the NXT as #1 to #4 for each controller
      * in the chain. Once a controller has been retrieved using this method, it cannot be retrieved again.
      * <p>
      * A combination of Servo and Motor controllers can be daisy-chained.
      * @return The next available servo controller.
      * @throws IllegalStateException If no more servo controllers can be returned. If there are no servo controllers
      * in the daisy-chain, this exception is also thrown.
      * @see #getMotorController
      */
    public ServoController getServoController() throws IllegalStateException { 
        // TODO use as yet to be defined servo interface
        if (servoControllerIndex>servoControllerCount) throw new IllegalStateException("No available servo controllers");
        return servoControllers[servoControllerIndex++];
    }


}
