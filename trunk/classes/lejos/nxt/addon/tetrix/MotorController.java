package lejos.nxt.addon.tetrix;

import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

import lejos.util.Delay;
import lejos.util.EndianTools;

/**HiTechnic Motor Controller abstraction. Provides <code>BasicMotor</code> instances which are used to control
 * the Tetrix motors.
 * 
 * @see ControllerProvider
 * @author Kirk P. Thompson
 */
public class MotorController extends I2CSensor {
    public static final int MOTOR_1 = 0;
    public static final int MOTOR_2 = 1;
    
    static final int CMD_FORWARD = 0;
    static final int CMD_BACKWARD = 1;
    static final int CMD_FLT = 2;
    static final int CMD_STOP = 3;
    static final int CMD_SETPOWER = 4;
    static final int CMD_ROTATE = 5;
    static final int CMD_GETPOWER = 6;
    static final int CMD_RESETTACHO = 7;
    static final int CMD_GETTACHO = 8;
    static final int CMD_SETREVERSE = 9;
    static final int CMD_ISMOVING = 10;
    
    private int[] motorState = {STATE_STOPPED, STATE_STOPPED};
    private static final int STATE_STOPPED = 0;
    private static final int STATE_RUNNING_FWD = 1;
    private static final int STATE_RUNNING_BKWD = 2;
    private static final int STATE_ROTATE_TO = 3;
    private static final int STATE_ROTATE_DONE = 4;
    
    // common registers
    private static final int REG_ALL_MOTORCONTROL = 0x40;
    private static final int REG_BATTERY = 0x54;
    // register map for the motor-specific registers
    private static final int REG_IDX_ENCODER_TARGET = 0;
    private static final int REG_IDX_MODE = 1;
    private static final int REG_IDX_POWER = 2;
    private static final int REG_IDX_ENCODER_CURRENT = 3;
    private static final int[][] REGISTER_MAP = // [REG_IDX_xxx][channel]
        {{0x40, 0x48}, // Encoder Target (write)
         {0x44, 0x47}, // Mode
         {0x45, 0x46}, // Power
         {0x4C, 0x50}  // Encoder Value (read)
        };
    
    // Mode OR masks
    private static final int MODEBIT_REVERSE = 0x08;
    private static final int MODEBIT_NTO = 0x10;
    private static final int MODEBIT_ERROR = 0x40;
    private static final int MODEBIT_BUSY = 0x80;
    private static final int MODEBIT_SEL_POWER = 0x00;
    private static final int MODEBIT_SEL_SPEED = 0x01;
    private static final int MODEBIT_SEL_POSITION = 0x02;
    private static final int MODEBIT_SEL_RST_ENCODER = 0x03;
    
    // motor
    private int[][] motorParams = new int[5][2]; 
    private static final int MOTPARAM_POWER = 0; // current power value
    private static final int MOTPARAM_ROTATE_ENCODER = 1; // target encoder value
    private static final int MOTPARAM_REGULATED = 2; // 0=false=power control, 1=speed control
    private static final int MOTPARAM_REVERSED = 3; // 1=reversed, 0= normal
    private static final int MOTPARAM_ROTATE = 4; // 1=rotate to target mode
    
    private TachoMotor[] motors= new TachoMotor[2];
    private byte[] buf = new byte[12];
    private int retVal;
    
    MotorController(I2CPort port, int i2cAddress) {
        super(port, i2cAddress, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
        address = i2cAddress;
        initController();
    }

    /**Get the <code>BasicMotor</code> instance that is associated with the <code>motorID</code>.
     * 
     * @param motorID The motor ID number. This is indicated on the HiTechnic Motor Controller and is
     * represented using <code>{@link #MOTOR_1}</code> or <code>{@link #MOTOR_2}</code>.
     * @return The <code>BasicMotor</code> instance 
     * @see BasicMotor
     * @see #getTachoMotor
     */
    public BasicMotor getBasicMotor(int motorID) {
        return getTachoMotor(motorID);
    }
    
    /**Get the <code>TachoMotor</code> instance that is associated with the <code>motorID</code>.
     * 
     * @param motorID The motor ID number. This is indicated on the HiTechnic Motor Controller and is
     * represented using <code>{@link #MOTOR_1}</code> or <code>{@link #MOTOR_2}</code>.
     * @return The <code>TachoMotor</code> instance 
     * @throws IllegalArgumentException if invalid <code>motorID</code>
     * @see TachoMotor
     * @see #getBasicMotor
     */
    public TachoMotor getTachoMotor(int motorID) {
        if (motorID<MOTOR_1 || motorID>MOTOR_2) {
            throw new IllegalArgumentException("Invalid motor ID");
        }
        if (motors[motorID]==null) motors[motorID]=new TachoMotor(this, motorID);
        return motors[motorID];
    }
    
    void dumpArray(int[] arr) {
        for (int i=0;i<arr.length;i++) {
            System.out.print(arr[i] + ":");
        }
        System.out.println("");
        Button.waitForAnyPress();
    }
    
//    private byte byteVal(int value) {
//        return 0;
//    }
    
    private byte getMode(int channel) {
        int mode=MODEBIT_SEL_POWER | MODEBIT_NTO;
        // constant speed SEL bit
        if (motorParams[MOTPARAM_REGULATED][channel]!=0) {
            mode = mode | MODEBIT_SEL_SPEED;
        } 
        // run to position SEL bit
        if (motorParams[MOTPARAM_ROTATE][channel]!=0) {
            mode = mode | MODEBIT_SEL_POSITION;
        }
        // reverse operation bit
        if (motorParams[MOTPARAM_REVERSED][channel]!=0) mode = mode | MODEBIT_REVERSE;
        
        return (byte)(mode & 0xff);
    }
    public void dbg(String msg){
        System.out.println(msg);
        Button.waitForAnyPress();
    }
    synchronized int doCommand(int command, int operand, int channel) {
        byte workingByte=0;
        int commandRetVal=0;
        switch (command) {
            case CMD_FORWARD:
                if (motorState[channel]==STATE_RUNNING_FWD) break;
                motorState[channel]=STATE_RUNNING_FWD;
            case CMD_BACKWARD:
                if (motorState[channel]==STATE_RUNNING_BKWD) break;
                // set the mode
                sendData(REGISTER_MAP[REG_IDX_MODE][channel], getMode(channel));
                // set the power to turn on the motor
                workingByte=(byte)motorParams[MOTPARAM_POWER][channel];
                if (command==CMD_BACKWARD) {
                    workingByte*=-1; // negative power runs backwards
                    motorState[channel]=STATE_RUNNING_BKWD;                    
                }
                sendData(REGISTER_MAP[REG_IDX_POWER][channel], workingByte); 
                
                break;
            case CMD_FLT:
                workingByte=-128;
            case CMD_STOP:
                if (command==CMD_STOP) workingByte=0;
                sendData(REGISTER_MAP[REG_IDX_POWER][channel], workingByte); 
                motorState[channel]=STATE_STOPPED;
                Delay.msDelay(50);
                break;
            case CMD_SETPOWER:
                motorParams[MOTPARAM_POWER][channel] = operand;
                // if not running, exit
                if (motorState[channel]==STATE_STOPPED)  break;
                
                workingByte = (byte)motorParams[MOTPARAM_POWER][channel];
                if (motorState[channel]==STATE_RUNNING_BKWD ) {
                    workingByte *= -1;
                }
                sendData(REGISTER_MAP[REG_IDX_POWER][channel], workingByte); 
                break;
            case CMD_ROTATE:
                // TODO implement
                break;
            case CMD_GETPOWER:
                commandRetVal=motorParams[MOTPARAM_POWER][channel];
                break;
            case CMD_RESETTACHO:
                // reset encoder/tacho 
                sendData(REGISTER_MAP[REG_IDX_MODE][channel], (byte)(getMode(channel) | MODEBIT_SEL_RST_ENCODER & 0xff));
                motorState[channel]=STATE_STOPPED;
                break;
            case CMD_GETTACHO:
                getData(REGISTER_MAP[REG_IDX_ENCODER_CURRENT][channel], buf, 4);
                commandRetVal=(int)(EndianTools.decodeIntBE(buf, 0)*.25);
                break;
            case CMD_SETREVERSE:
                motorParams[MOTPARAM_REVERSED][channel]=1;
                sendData(REGISTER_MAP[REG_IDX_MODE][channel], getMode(channel));
                break;
            case CMD_ISMOVING:
                commandRetVal=1;
                if (motorState[channel]==STATE_ROTATE_DONE | motorState[channel]==STATE_STOPPED) commandRetVal=0;
                break;
            default:
                throw new IllegalArgumentException("Invalid Command");
        }
        return commandRetVal;   
        
    }
    
    private void initController() {
        byte[] initBuf = {0,0,0,0,0,0,0,0,0,0,0,0};
        sendData(REG_ALL_MOTORCONTROL, initBuf, initBuf.length);
        Delay.msDelay(1000);
        // reset encoder/tacho and set NTO mode
        byte mode = (byte)(MODEBIT_SEL_RST_ENCODER | MODEBIT_NTO) & 0xff;
        sendData(REGISTER_MAP[REG_IDX_MODE][MOTOR_1], mode);
        sendData(REGISTER_MAP[REG_IDX_MODE][MOTOR_2], mode);
    }
    
    /** Return the current battery voltage supplied to the controller.
     * @return The current battery voltage in volts
     */
    public synchronized float getVoltage() {
        retVal = getData(REG_BATTERY, buf, 2);
        retVal=(buf[0] & 0xff)<<2;
        retVal=retVal | (buf[1] & 0x03);
        return (float)retVal * .02f;
    }
}

