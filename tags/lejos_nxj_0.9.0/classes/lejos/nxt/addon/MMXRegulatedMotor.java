package lejos.nxt.addon;

import lejos.nxt.addon.NXTMMX;

import lejos.robotics.DCMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

import lejos.util.Delay;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Supports Mindsensors NXTMMX motor multiplexer. This device allows you to connect two 
 * additional motors to your robot using a sensor port. Multiple NXTMMXs can be chained together.
 * <p>
 * Create an instance of this class passing a <code>NXTMMX</code> instance and Motor ID 
 * (<code>{@link NXTMMX#MMX_MOTOR_1 MMX_MOTOR_1}</code> or <code>{@link NXTMMX#MMX_MOTOR_2 MMX_MOTOR_2}</code>)
 * in the constructor.
 * 
 * @see NXTMMX
 * @author Michael D. Smith &lt;mdsmitty@gmail.com&gt;
 * @author Kirk P. Thompson  
 *
 */
public class MMXRegulatedMotor implements RegulatedMotor, DCMotor{
    private NXTMMX mux;
    private boolean rampUp = true; 
    private final boolean controlSpeed = true;  // no public accessor so I made final to ensure
    private boolean tachoLock = false;
    private boolean tachoBrake = true;
    private byte[] buffer = new byte[4];

    //bits for the command register 0-7   
    private final int CONTROL_SPEED =       0x01; //0
    private final int CONTROL_RAMP =        0x02; //1
    private final int CONTROL_RELATIVE =    0x04; //2
    private final int CONTROL_TACHO =       0x08; //3
    private final int CONTROL_TACHO_BRAKE = 0x10; //4
    private final int CONTROL_TACHO_LOCK =  0x20; //5
//    private final int CONTROL_TIME =        0x40; //6
    private final int CONTROL_GO =          0x80; //7
    
    //bits for status register 0-7
//    private final int STATUS_SPEED =        0x01; //0
//    private final int STATUS_RAMP =         0x02; //1
//    private final int STATUS_POWERED =      0x04; //2
//    private final int STATUS_POSIONAL =     0x08; //3
//    private final int STATUS_BREAK =        0x10; //4
//    private final int STATUS_OVERLOAD =     0x20; //5
//    private final int STATUS_TIME =         0x40; //6
    private final int STATUS_STALL =        0x80; //7
    
    //motor registers                     A         B
    private int REG_RotateTo =           0x42;//   0x4A
    private int REG_MotorSpeed =         0x46;//   0x4e
    private int REG_MotorRunTime =       0x47;//   0x4F
    private int REG_MotorCommandRegA =   0x49;//   0x51
//    private int REG_MotorCommandRegB =   0x48;//   0x51 command register B not used
    private int REG_TacPos =             0x62;//   0x66
    private int REG_Status =             0x72;//   0x73 
    private int REG_Tasks =              0x76;//   0x77
    
    //Commands for register 0x41
    private int COMMAND_ResetTaco =      0x72;//   0x73 
    private int COMMAND_Stop =           'A';//    'B'
    private int COMMAND_Float =          'a';//    'b' 
    
    //mux registers
    private int REG_MUX_Command =        0x41;

    /** Use to specify motor float when a rotate method completes.
     * @see #setRotateStopMode
     */
    public static final int ROTSTOP_FLOAT=1;
    
    /** Use to specify motor brake when a rotate method completes.
     * @see #setRotateStopMode
     */
    public final static int ROTSTOP_BRAKE=2;
    
    /** Use to specify active hold when a rotate method completes. The NXTMMX will actively attempt to hold the motor angle.
     * @see #setRotateStopMode
     */
    public final static int ROTSTOP_LOCK=4;
    
    private final int POWER_INIT = -9999;
    private boolean _isRunCmd = false;
    private boolean _isRotateCmd = false;
    private float _degpersec = 0f;
    private Thread _monitorThread;
    private final TachoStatusMonitor _tachoMonitor = new TachoStatusMonitor();
    private int _currentPower = POWER_INIT;
    private int _status=0;
    private int _direction=1; // 1=forward, -1=backward
    private boolean _isStalled = false;
    private boolean _isMoving = false;
    private final int MOTOR_MAX_DPS; 
    private int _limitAngle=0;
    private RegulatedMotorListener _listener = null;
    private boolean _resetTacho = false;
    private boolean latchRotStart=false;
    private boolean latchRotEnd=false;
    private int _tachoCount=0;
    
    /**
     * Create an instance of a <code>MMXRegulatedMotor</code>.
     * 
     * @param mux the motor multiplexor <code>NXTMMX</code> instance to bind this motor to.
     * @param motor the index of the motor connected to the NXTMMX: <code>NXTMMX.MMX_MOTOR_1</code> or <code>NXTMMX.MMX_MOTOR_2</code>
     * @see NXTMMX
     * @see NXTMMX#MMX_MOTOR_1
     * @see NXTMMX#MMX_MOTOR_2
     */
    public MMXRegulatedMotor (NXTMMX mux, int motor){
        this.mux = mux;
        MOTOR_MAX_DPS=mux.getVoltage()/10;
        REG_RotateTo = REG_RotateTo + (motor * 8);
        REG_MotorSpeed = REG_MotorSpeed + (motor * 8);
        REG_MotorRunTime = REG_MotorRunTime + (motor * 8);
        REG_MotorCommandRegA = REG_MotorCommandRegA + (motor * 8);
        REG_TacPos = REG_TacPos + (motor * 4);
        REG_Status = REG_Status + motor;
        REG_Tasks = REG_Tasks + motor;
        
        COMMAND_ResetTaco = COMMAND_ResetTaco + motor;
        COMMAND_Stop = COMMAND_Stop + motor;
        COMMAND_Float = COMMAND_Float + motor;
        
        // start the tachomonitor
        _monitorThread = new Thread(_tachoMonitor);
        _monitorThread.setDaemon(true);
        _monitorThread.start();
        
        // start the regulator
//        Thread regulatorThread = new Thread(new Regulator());
//        regulatorThread.setDaemon(true);
//        regulatorThread.start();
    }

    /**
     * Add a motor listener. Move operations will be reported to this object.
     * @param listener An instance of type <code>RegulatedMotorListener</code>
     * @see RegulatedMotorListener
     */
    public void addListener(RegulatedMotorListener listener) {
        _listener = listener;
    }

    /**	 Return the maximum speed of the motor. It is a general assumption that the maximum speed of a Motor is
	 *    100 degrees/second * Voltage.
     * @return The maximum speed in degrees per second
     */
    public float getMaxSpeed() {        
        return MOTOR_MAX_DPS;
    }
    
    /**
     * Returns the actual speed.
     * 
     * @return speed in degrees per second. Negative value means motor is rotating backward.
     */
    public int getRotationSpeed() {
        return (int)_degpersec * _direction; 
    }
    
    private void waitForMotorCompletion(){
        // wait until tachomonitor registers a move (without a stall). This is done when the motor is started.
        while (!_isStalled&&!_isMoving) Thread.yield();
        // We have started, wait until tachomonitor registers no movement or a stall
        while (!_isStalled&&_isMoving) Thread.yield();
    }
    
    private void motorRotate(boolean relative){
        int command = 0;
        _isRunCmd = false;
        _isRotateCmd=true;
        if (relative) command |= CONTROL_RELATIVE; //2
        command |= CONTROL_TACHO; //3
        if(tachoBrake) command |= CONTROL_TACHO_BRAKE; //4
        if(tachoLock) command |= CONTROL_TACHO_LOCK; //5
        setRotateAngle(_limitAngle);
        motorGO(command);
    }
    
    private void setRotateAngle(int angle){
        buffer = intToByteArray(angle);
        mux.sendData(REG_RotateTo, buffer, 4);
    }
    
    /**
     * Rotate by the requested number of degrees. Negative values rotate opposite positive ones.
     * 
     * @param angle number of degrees to rotate relative to the current position
     * @param immediateReturn <code>true</code> will not block, <code>false</code> will wait until completion or stall.
     * @see #rotate(int)
     * @see #setRotateStopMode
     */
    public void rotate(int angle, boolean immediateReturn) {
        _limitAngle=angle;
        motorRotate(true); // use relative mode
        if (immediateReturn) return;
        waitForMotorCompletion();
    }

    /**  Rotate by the requested number of degrees. Wait for the move to complete.
     * @param angle number of degrees to rotate relative to the current position
     * @see #rotate(int,boolean)
     * @see #setRotateStopMode
     */
    public void rotate(int angle){
        rotate(angle, false);
    }
    
    /**
     * Rotate to the target angle. If <code>immediateReturn</code> is <code>true</code>, the method returns immediately and the motor 
     * stops by itself and <code>getTachoCount()</code> should be within +- 2 degrees if the limit angle. If any motor method is called before 
     * the limit is reached, the rotation is canceled. 
     * <p>
     * When the angle is reached and the motor stops completely, the method 
     * <code>isMoving()</code> returns  <code>false</code>.
     * @param limitAngle Angle to rotate to.
     * @see #getTachoCount
     * @see #setRotateStopMode
     */
    public void rotateTo(int limitAngle, boolean immediateReturn) {
        _limitAngle=limitAngle;
        motorRotate(false); // use absolute mode
        if (immediateReturn) return;
        waitForMotorCompletion();
    }
    
    /**
     * Rotate to the target angle. Do not return until the move is complete. 
     * @param limitAngle Angle to rotate to.
     * @see #rotateTo(int, boolean)
     * @see #setRotateStopMode
     */
    public void rotateTo(int limitAngle)
    {
        rotateTo(limitAngle, false);
    }
    
    /**
     * Return the absolute angle that this Motor is rotating to. 
     * @return angle in degrees. 0 if no rotate method has been intiated.
     */
    public int getLimitAngle()
    {
        return (!_isRotateCmd)?0:getTachoCount()+_limitAngle;
    }
    
    /**
     * Return the current target speed.
     * @return Motor speed in degrees per second.
     * @see #setSpeed
     * @see #getPower
     */
    public int getSpeed(){
        return Math.round(8.1551f*_currentPower+32.253f);
    }
    
    /**
     * Sets desired motor speed, in degrees per second. 
     * <p>
     * The NXTMMX does not provide speed control per se (just power) so we approximate the power value used
     * based on the requested degress/sec (dps) passed in <code>speed</code>. This means if you request 400 dps, the actual dps value
     * may not reflect that. Setting speed during a rotate method will have no effect on the running rotate but will on the next rotate
     * method call.
     * <p> 
     * experimental data gives: dps=8.1551*power+32.253 (unloaded @ 8.83V)
     * <p>
     * <b>Note:</b>The NXTMMX doesn't seem to want to drive the motor below ~40 dps.
     * @param speed Motor speed in degrees per second
     * @see #getSpeed
     * @see #setPower
     */
    public void setSpeed(int speed) {
        speed=Math.abs(speed);
        if (speed > MOTOR_MAX_DPS) speed=MOTOR_MAX_DPS;
        float power=(speed-32.253f)/8.1551f;
        if (power<0) power=0;
        setPower(Math.round(power));
    }

    /**
     * Returns the current motor power setting (%). 
     * @return current power 0-100
     * @see #setPower
     * @see #getSpeed
     */
    public int getPower() {
        int power = (_currentPower==POWER_INIT)?0:Math.abs(_currentPower);
        return power;
    }
    
    private void motorGO(int command){
        if(controlSpeed) command |= CONTROL_SPEED; //0
        if(rampUp) command |= CONTROL_RAMP; //1  
        // if authstart or we already have started [a non-rotate] and need to effect power change
        if(mux.isAutoStart() || _isRunCmd) command |= CONTROL_GO; // 7
        // send the command
        mux.sendData(REG_MotorCommandRegA, (byte)command);
    }
    
    /**
     * Set the power level 0-100% to be applied to the motor. Setting power during a rotate method will have no effect on the 
     * running rotate but will on the next rotate
     * method call.
     * @param power new motor power 0-100%
     * @see #setSpeed
     * @see #getPower
     */
    public void setPower(int power){
        
        if(power < 0) power = 0;
        if (power > 100) power = 100;
        power *= _direction;
        // this is why we use _currentPower==POWER_INIT on intialization: If power is not sent, the MMX runs @ 100% by default
        // on powerup. This forces an i2c send to set the power.
        if (_currentPower!=power) {
            // send the new power value. This needs to be done for reverses as well
            mux.sendData(REG_MotorSpeed, (byte)power);
            // if motor is running on non-rotate/timing command, effect power change immediately
            if (_isRunCmd) motorGO(0);
            _currentPower=power;
        }
    }
    
    /**
     * Causes motor to rotate forward or backward .
     */
    private void doMotorDirection(int direction) {
        boolean switchDirection=(_direction!=direction);
        
        // effect power change
        _direction=direction; // 1=forward, -1=backward
        _isRunCmd = true;
        _isRotateCmd=false;
        _limitAngle=0;
        if (switchDirection) {
            // use ABS so setPower() doesn't set to zero on negative _currentPower values. setPower() uses _direction to determine -power
            setPower(Math.abs(_currentPower)); 
        } else motorGO(0);
    }
    
    /**
     * Causes motor to rotate forward.
     * @see #backward
     */
    public void forward(){
        doMotorDirection(1);
    }
    
    /**
     * Causes motor to rotate backwards.
     * @see #forward
     */
    public void backward() {
        doMotorDirection(-1);
    }
    
    /**
     * Causes motor to float. This will stop the motor without braking
     * and the position of the motor will not be maintained.
     * 
     * @param immediateReturn If <code>true</code> do not wait for the motor to actually stop
     * @see #flt()
     */
    public void flt(boolean immediateReturn) {
        // REG_MUX_Command =        0x41;
        // COMMAND_Float =          'a';//    'b' 
        setStopState();
        mux.sendData(REG_MUX_Command, (byte) COMMAND_Float);
        if (!immediateReturn) while(_isMoving) Delay.msDelay(20);
    }
    /**
     * Causes motor to float. This will stop the motor without braking
     * and the position of the motor will not be maintained. This method will not wait for the motor to stop rotating
     * before returning.
     * 
     * @see #flt(boolean)
     * @see #lock()
     * @see #stop()
     */
    public void flt() {
        flt(true);
    }
    
    /**
     * Causes motor to stop pretty much instantaneously. In other words, the
     * motor doesn't just stop; it will resist any further motion.
     * <p>
     * Cancels any <code>rotate()</code> orders in progress.
     * 
     * @param immediateReturn if <code>true</code> do not wait for the motor to actually stop
     * @see #stop()
     */
    public void stop(boolean immediateReturn) {
        // REG_MUX_Command =        0x41;
        // COMMAND_Float =          'a';//    'b' 
        setStopState();        
        mux.sendData(REG_MUX_Command, (byte) COMMAND_Stop);
        
        // note that there is no wait for motor to stop no matter what immediateReturn is as it is pretty much instantaneous and
        // the tachomonitor thread could take up to 100+ ms to set _isMoving.
        // Is this signature really necessary in RegulatedMotor? // TODO query lejos dev team
    }

    /** Causes motor to stop pretty much instantaneously. In other words, the
     * motor doesn't just stop; it will resist any further motion. The motor must stop rotating before <code>stop()</code> is
     * complete.
     * <p>
     * Cancels any <code>rotate()</code> orders in progress. 
     * @see #stop(boolean)
     * @see #flt()
     * @see #lock()
     */
    public void stop() {
        stop(false);
    }
    
    private void setStopState(){
        _isRunCmd = false;
        _isRotateCmd=false;
        _limitAngle=0;
    }
    
    /**
     * Sets the motor stopping mode used for the rotate methods after rotation completion.
     * <p>
     * Default on instantiation is <code>ROTSTOP_BRAKE</code>.
     * @param mode <code>{@link #ROTSTOP_FLOAT ROTSTOP_FLOAT}</code>, <code>{@link #ROTSTOP_BRAKE ROTSTOP_BRAKE}</code>, or
     * <code>{@link #ROTSTOP_LOCK ROTSTOP_LOCK}</code>
     * @see #rotate(int)
     * @see #rotateTo(int)
     */
    public void setRotateStopMode(int mode){
        tachoBrake=true;
        tachoLock=true;
        switch (mode){
            case ROTSTOP_FLOAT: 
                tachoBrake=false;
            case ROTSTOP_BRAKE:
                tachoLock=false;
                break;
            case ROTSTOP_LOCK:
                break;
            default:
                tachoLock=false;
        }
    }
    
    /**
     * Locks the motor in current position. Uses active feed back to hold it. 
     * @see #stop()
     * @see #flt()
     */
    public void lock(){
        int command = 0;
        stop(true);
        Delay.msDelay(50);
        int position = getTacho();
        setRotateAngle(position);
        command |= CONTROL_TACHO; //3
        command |= CONTROL_TACHO_BRAKE; //4
        command |= CONTROL_TACHO_LOCK; //5
        _isRunCmd = true; // trick motorGO() to send GO command
        motorGO(command);
        _isRunCmd=false;
    }
    
    /**
     * Wait until the current movement operation is complete. This can include
     * the motor stalling.
     */
    public void waitComplete() {
        while(_isMoving) Thread.yield();
    }
    
    private int getTacho(){
        int retVal=0;
        
        // try up to three times for tacho count if zero. This is because sometimes zero is returned even if motor is running. I don't
        // know if this is a problem with the NXTMMX or i2c
        for (int i=0;i<3;i++) {
            //REG_TacPos =             0x62;//   0x66
            mux.getData(REG_TacPos, buffer, 4);
            retVal=byteArrayToInt(buffer);
            if (retVal!=0) break;
            Delay.msDelay(3);
        }
        return retVal;
    }
    
    /**
     * Returns the tachometer count.
     * @return tachometer count in degrees
     * @see #resetTachoCount
     */
    public synchronized int getTachoCount() {

        return _tachoCount;
    }
    
    /**
     * Resets the tachometer count to zero. 
     * @see #getTachoCount
     */
    public void resetTachoCount() {
        // COMMAND_ResetTaco =      0x72;//   0x73
        mux.sendData(REG_MUX_Command, (byte) COMMAND_ResetTaco);
        
        // wait until tachmonitor resets
        try {
            _resetTacho=true;
            synchronized (_tachoMonitor) {_tachoMonitor.wait();}
        } catch (InterruptedException e) {
            ;// do nothing with e
        }

    }
    
     /**
      * Sets speed ramping is enabled/disabled for this motor. The <code>RegulatedMotor</code> interface specifies this in degrees/sec/sec
      * but the NXTMMX does not allow the rate to be changed, just if the motor uses smooth acceleration or not so we use the <code>acceleration</code>
      * parameter to specify ramping state. <p>Default at instantiation is ramping enabled.
      * @param acceleration >0 means NXTMMX internal ramping is enabled otherwise disabled
      */
    public void setAcceleration(int acceleration){
        rampUp=(acceleration>0);
    }
    
    /**
     * Return <code>true</code> if the motor is currently stalled after a motor action method is executed.
     * @return <code>true</code> if the motor is stalled, else <code>false</code>
     * @see #forward
     * @see #backward
     * @see #rotate(int)
     */
    public boolean isStalled(){
        return _isStalled;
    }
    
     
    /**
    * This method returns <code>true</code> if the motor is rotating, whether under power or not.
    * The return value corresponds to the actual motor movement so if something external is rotating the motor,
    * <code>isMoving()</code>  will return <code>true</code>. 
    * After <code>flt()</code> is called, this method will return <code>true</code> until the motor
    * axle stops rotating by inertia, etc.
    * 
    * @return <code>true</code> if the motor is rotating, <code>false</code> otherwise.
    * @see #flt()
    */
    public boolean isMoving(){
        return _isMoving;
    }
    
    private byte[] intToByteArray(int value) {
        return new byte[] {
            (byte)(value),
            (byte)(value >>> 8),
            (byte)(value >>> 16),
            (byte)(value >>> 24)} ;
    }
    
    private int byteArrayToInt( byte[] buffer){
        return (buffer[3] << 24)
        + ((buffer[2] & 0xFF) << 16)
        + ((buffer[1] & 0xFF) << 8)
        + (buffer[0] & 0xFF); 
    }

    private int getStatus(){ 
        mux.getData(REG_Status, buffer, 1);
        return buffer[0] & 0xff;
    }
    
    private void notifyListener(int state){
        if (_listener==null) return;
        if (state==0)
            _listener.rotationStopped(this, getTachoCount(), _isStalled, System.currentTimeMillis());
        else if(state==1)
            _listener.rotationStarted(this, getTachoCount(), _isStalled, System.currentTimeMillis());
    }
    
    /** calcs degrees/sec. checks status.
     */
    private class TachoStatusMonitor implements Runnable{
        private float degpersecAccum=0f;
        
        public void run(){            
            int tc, tcBegin, tcDelta, index=0;
            long stime, etime, tdelta;
            float[] samples= {0f,0f}; 
            int statusCheck=0;
            
            stime = System.currentTimeMillis();
            _tachoCount=tcBegin=getTacho();
            while(true) {
                if (_resetTacho) {
                    _resetTacho=false;
                    stime = System.currentTimeMillis();
                    _tachoCount=tcBegin=getTacho();
                    synchronized (_tachoMonitor) {_tachoMonitor.notifyAll();} // wake up the resetTachoCount() method
                }
                Delay.msDelay(100); 
                _tachoCount=tc=getTacho();
                tcDelta=Math.abs(tc-tcBegin); 
                tcBegin=tc;
                etime=System.currentTimeMillis();
                tdelta = etime - stime;
                stime=etime;
                
                // save a dps sample
                samples[index]=(float)tcDelta/tdelta * 1000;
                // set if moving
                _isMoving=(Math.round(samples[index])!=0);
                
                // get status every ~200 ms
                statusCheck++;
                if(statusCheck==2){
                    statusCheck=0;
                    _status=getStatus();
                }
                // set stall if detected. Defer to stall bit on rotates so we can not get screwed up when the angle has been reached 
                // and the motor stops (for immediate returns on rotate(), rotateTo()).
                _isStalled=(((STATUS_STALL&_status)==STATUS_STALL) || (!_isMoving && _isRunCmd)); 
                
                
                // if moving and first start, notifyListener()
                if (_isMoving && !latchRotStart) {
                    latchRotStart=true;
                    notifyListener(1); // rotation started
                }
                // if not moving and started and not ended...
                if (!_isMoving && latchRotStart && !latchRotEnd) {
                    latchRotEnd=true;
                    notifyListener(0); // rotation stopped
                }
                 // if not moving and started and ended...
                if (!_isMoving && latchRotStart && latchRotEnd) {
                    // RST latches
                    latchRotEnd=false;
                    latchRotStart=false;
                }      
                
                // don't process weird tacho values that come in every so often (quadrature encoder read problem on NXTMMX?)
                if (samples[index]>MOTOR_MAX_DPS) continue;
                
                // do 3 pt moving average
                index++;
                if (index>=samples.length) index=0;
                // defeat moving avg on stall. Note that this really only works well/fast for unlimited duration runs because we have to
                // wait for NXTMMX status on rotates.
                if (_isStalled) {
                    _degpersec=0f; 
                } else {
                    // average the samples and the last result (_degpersec)
                    for (int i=0;i<samples.length;i++) {
                        degpersecAccum+=samples[i];
                    }
                    _degpersec = degpersecAccum/(samples.length+1);
                }
                degpersecAccum=_degpersec;
            }
        }
    }
}
