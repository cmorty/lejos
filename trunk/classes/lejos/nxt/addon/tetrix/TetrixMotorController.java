package lejos.nxt.addon.tetrix;

import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

import lejos.util.Delay;
import lejos.util.EndianTools;

/**HiTechnic Motor Controller abstraction. Provides <code>TetrixMotor</code> and <code>TetrixEncoderMotor</code> instances 
 * which are used to control the Tetrix motors.
 * 
 * @see lejos.nxt.addon.tetrix.TetrixControllerFactory
 * @see lejos.nxt.addon.tetrix.TetrixMotor
 * @see lejos.nxt.addon.tetrix.TetrixEncoderMotor
 * @author Kirk P. Thompson
 */
public class TetrixMotorController extends I2CSensor {
    public static final int MOTOR_1 = 0;
    public static final int MOTOR_2 = 1;
    private static final int CHANNELS = 2;
    
    static final int CMD_FORWARD = 0;
    static final int CMD_BACKWARD = 1;
    static final int CMD_FLT = 2;
    static final int CMD_STOP = 3;
    static final int CMD_SETPOWER = 4;
    static final int CMD_ROTATE = 5;
    static final int CMD_ROTATE_TO = 6;
    static final int CMD_GETPOWER = 7;
    static final int CMD_RESETTACHO = 8;
    static final int CMD_SETREVERSE = 10;
    static final int CMD_ISMOVING = 11;
    static final int CMD_ROTATE_WAIT = 12;
    static final int CMD_ROTATE_TO_WAIT = 13;
    static final int CMD_SETREGULATE = 14;
    
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
    private int[][] motorParams = new int[4][CHANNELS]; 
    private static final int MOTPARAM_POWER = 0; // current power value
    private static final int MOTPARAM_REGULATED = 1; // 0=false=power control, 1=speed control
    private static final int MOTPARAM_REVERSED = 2; // 1=reversed, 0= normal
    private static final int MOTPARAM_ROTATE = 3; // 1=rotate to target mode
    
    private TetrixEncoderMotor[] motors= new TetrixEncoderMotor[CHANNELS];
    private BUSYMonitor[] bUSYMonitors = new BUSYMonitor[CHANNELS];
    
    private byte[] buf = new byte[12];
    private int retVal;
    
    TetrixMotorController(I2CPort port, int i2cAddress) {
        super(port, i2cAddress, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
        address = i2cAddress;
        initController();
    }

    private class BUSYMonitor extends Thread {
        int channel;
        BUSYMonitor(int channel){
            this.channel = channel;
        }
        public void run(){
            waitUntilBUSYClear();
            motorState[channel]=STATE_ROTATE_DONE;
        }
        void waitUntilBUSYClear(){
            byte buf[] = {(byte)MODEBIT_BUSY};
            while ((buf[0] & MODEBIT_BUSY) == MODEBIT_BUSY) {
                Delay.msDelay(50);
                if (getData(REGISTER_MAP[REG_IDX_MODE][channel], buf, 1)!=0) break;
            }
        }
    }
    
    /**Get the <code>TetrixMotor</code> instance that is associated with the <code>motorID</code>.
     * 
     * @param motorID The motor ID number. This is indicated on the HiTechnic Motor Controller and is
     * represented using <code> {@link #MOTOR_1}</code> or <code> {@link #MOTOR_2}</code>.
     * @return The <code>TetrixMotor</code> instance 
     * @see lejos.nxt.addon.tetrix.TetrixMotor
     * @see #getEncoderMotor
     */
    public TetrixMotor getBasicMotor(int motorID) {
        return getEncoderMotor(motorID);
    }
    
    /**Get the <code>TetrixEncoderMotor</code> instance that is associated with the <code>motorID</code>.
     * 
     * @param motorID The motor ID number. This is indicated on the HiTechnic Motor Controller and is
     * represented using <code> {@link #MOTOR_1}</code> or <code> {@link #MOTOR_2}</code>.
     * @return The <code>TetrixEncoderMotor</code> instance 
     * @throws IllegalArgumentException if invalid <code>motorID</code>
     * @see lejos.nxt.addon.tetrix.TetrixEncoderMotor
     * @see #getBasicMotor
     */
    public TetrixEncoderMotor getEncoderMotor(int motorID) {
        if (motorID<MOTOR_1 || motorID>MOTOR_2) {
            throw new IllegalArgumentException("Invalid motor ID");
        }
        if (motors[motorID]==null) motors[motorID]=new TetrixEncoderMotor(this, motorID);
        return motors[motorID];
    }
    
    void dumpArray(int[] arr) {
        for (int i=0;i<arr.length;i++) {
            System.out.print(arr[i] + ":");
        }
        System.out.println("");
        Button.waitForAnyPress();
    }
    
    public void dbg(String msg){
        System.out.println(msg);
        Button.waitForAnyPress();
    }
    
    private int setMode(int channel, boolean resetEncoder) {
        int mode=MODEBIT_SEL_POWER | MODEBIT_NTO;
        // constant speed SEL bit and not in ROTATE (POS) state, set the bit. This is done because
        // if we set both the MODEBIT_SEL_SPEED and MODEBIT_SEL_POSITION, it equals MODEBIT_SEL_RST_ENCODER and
        // cancels out the ROTATE command
        if (motorParams[MOTPARAM_REGULATED][channel]!=0 &&  motorState[channel]!=STATE_ROTATE_TO) {
            mode = mode | MODEBIT_SEL_SPEED;
        } 
        // run to position SEL bit
        if (motorParams[MOTPARAM_ROTATE][channel]!=0) {
            mode = mode | MODEBIT_SEL_POSITION;
        }
        // reverse operation bit
        if (motorParams[MOTPARAM_REVERSED][channel]!=0) {
            mode = mode | MODEBIT_REVERSE;
        }
        // if encoder reset requested
        if (resetEncoder) {
            mode = mode |  MODEBIT_SEL_RST_ENCODER;
        }
        
        // set the mode
        return sendData(REGISTER_MAP[REG_IDX_MODE][channel], (byte)(mode & 0xff));
    }
    
    int getEncoderValue(int channel) {
        getData(REGISTER_MAP[REG_IDX_ENCODER_CURRENT][channel], buf, 4);
        return EndianTools.decodeIntBE(buf, 0);
    }
    
    private void rotate(int channel, int value, int cmd){
        byte workingByte=0;
        motorParams[MOTPARAM_ROTATE][channel]=1;
        
        if (cmd==CMD_ROTATE || cmd==CMD_ROTATE_WAIT) {
            // set the target based current + degrees passed
            value = getEncoderValue(channel) + value * 4;
        } else if(cmd==CMD_ROTATE_TO || cmd==CMD_ROTATE_TO_WAIT) {
            value *= 4;
        } else return;
        
        // set the encoder position
        EndianTools.encodeIntBE(value, buf, 0);
        sendData(REGISTER_MAP[REG_IDX_ENCODER_TARGET][channel], buf, 4); 
        motorState[channel]=STATE_ROTATE_TO;
        
        // set the mode
        setMode(channel, false);
        
        // set the power to turn on the motor. Ensure it is positive (do not adjust for BACKWARDS)
        workingByte=(byte)motorParams[MOTPARAM_POWER][channel];
        sendData(REGISTER_MAP[REG_IDX_POWER][channel], workingByte); 
        bUSYMonitors[channel] = new BUSYMonitor(channel);
        bUSYMonitors[channel].start();
        
        return;
    }
    
    boolean rotateIsBUSY(int channel) {
        return motorState[channel]!=STATE_ROTATE_DONE;
    }
    
    private void motorGo(int channel, int command) {
        byte workingByte=0;
        motorState[channel]=command + 1; // STATE_RUNNING_FWD, STATE_RUNNING_BKWD assuming command IN(CMD_FORWARD,CMD_BACKWARD)
        motorParams[MOTPARAM_ROTATE][channel]=0; //false
        // set the mode
        setMode(channel, false);
        // set the power to turn on the motor
        workingByte=(byte)motorParams[MOTPARAM_POWER][channel];
        if (command==CMD_BACKWARD) {
            workingByte*=-1; // negative power runs backwards
        }
        sendData(REGISTER_MAP[REG_IDX_POWER][channel], workingByte); 
    }
    
    synchronized int doCommand(int command, int operand, int channel) {
        byte workingByte=0;
        int commandRetVal=0;
        switch (command) {
            case CMD_FORWARD:
                if (motorState[channel]==STATE_RUNNING_FWD) break;
            case CMD_BACKWARD:
                if (motorState[channel]==STATE_RUNNING_BKWD) break;
                motorGo(channel, command);
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
                if (motorState[channel]==STATE_STOPPED || motorState[channel]==STATE_ROTATE_DONE)  break;
                
                // set the power if running to take effect immediately
                workingByte = (byte)motorParams[MOTPARAM_POWER][channel];
                if (motorState[channel]==STATE_RUNNING_BKWD ) {
                    workingByte *= -1;
                }
                sendData(REGISTER_MAP[REG_IDX_POWER][channel], workingByte); 
                break;
            case CMD_ROTATE:
            case CMD_ROTATE_TO:
            case CMD_ROTATE_WAIT:
            case CMD_ROTATE_TO_WAIT:
                rotate(channel, operand, command);
                break;
            case CMD_GETPOWER:
                commandRetVal=motorParams[MOTPARAM_POWER][channel];
                break;
            case CMD_RESETTACHO:
                // reset encoder/tacho 
                setMode(channel, true);
                Delay.msDelay(15); // small delay to allow encoder value reset in controller to happen
                motorState[channel]=STATE_STOPPED;
                break;
            case CMD_SETREVERSE:
                motorParams[MOTPARAM_REVERSED][channel]=1;
                setMode(channel, false);
                break;
            case CMD_ISMOVING:
                commandRetVal=1;
                if ((motorState[channel]==STATE_ROTATE_DONE) || (motorState[channel]==STATE_STOPPED)) commandRetVal=0;
                break;
            case CMD_SETREGULATE:
                motorParams[MOTPARAM_REGULATED][channel]=operand; //1=true, 0=false
                break;
            default:
                throw new IllegalArgumentException("Invalid Command");
        }
        return commandRetVal;   
        
    }
    
    private void initController() {
        byte[] initBuf = {0,0,0,0,0,0,0,0,0,0,0,0};
        sendData(REG_ALL_MOTORCONTROL, initBuf, initBuf.length);
        Delay.msDelay(50);
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

