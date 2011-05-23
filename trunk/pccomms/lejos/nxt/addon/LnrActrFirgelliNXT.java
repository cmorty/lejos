package lejos.nxt.addon;

//import lejos.nxt.LCD;

import lejos.nxt.NXTMotor;
import lejos.nxt.TachoMotorPort;

import lejos.robotics.EncoderMotor;

import lejos.robotics.LinearActuator;

import lejos.util.Delay;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/** A Linear Actuator class that provides non-blocking actions and stall detection. Developed for the Firgelli L12-NXT-50 and L12-NXT-100
 * but may work for others. These linear actuators are self contained units which include an electric motor and encoder. They will push 
 * up to 25N and move at 12mm/s unloaded. 
 * <p>
 * See <a href="http://www.firgelli.com">www.firgelli.com.</a>.
 * @author Kirk P. Thompson, 3/3/2011 &lt;lejos@mosen.net&gt;
 */
public class LnrActrFirgelliNXT implements LinearActuator{
    private final int MIN_POWER = 30;
    private EncoderMotor _encoderMotor;
    private int _realPower =0;
    private int _userSpecifiedPower =0;
    private int _tick_wait; // this is calculated in setPower() to fit the power setting. Variable because lower powers move it slower.
    private volatile boolean _isMoveCommand = false;
    private volatile boolean _isStalled=false;
    private volatile boolean _doActuate = false;;
    private Thread stallDetector;
    private Thread actuator;
    private boolean _dirExtend = true;
    private int _distanceTicks;
    private boolean _killCurrentAction=true;
    private boolean _enableStallStop;

//    private int dbgcount=0;
    
    
    /**Create a <code>LnrActrFirgelliNXT</code> instance.
     * Use this constructor to assign an instance of <code>EncoderMotor</code> used to drive the actuater motor. This constructor
     * allows any motor class that implements the <code>EncoderMotor</code> interface to drive the actuator. You must instantiate
     * the <code>EncoderMotor</code>-type motor before passing it to this constructor.
     * @param encoderMotor A motor instance of type <code>EncoderMotor</code> which will drive the actuator
     * @see lejos.nxt.NXTMotor
     * @see MMXRegulatedMotor
     * @see EncoderMotor
     */
    public LnrActrFirgelliNXT(EncoderMotor encoderMotor) {
        //this._motorPort= motorPort;
        _encoderMotor=encoderMotor;
        _encoderMotor.flt();
        setPower(0);
        // set up the threads
        stallDetector = new Thread(new StallDetector());
        stallDetector.setPriority(Thread.MAX_PRIORITY - 1);
        stallDetector.setDaemon(true);
        stallDetector.start();
        actuator = new Thread(new Actuator());
        actuator.setDaemon(true);
        actuator.start();
    }

    /** Convenience constructor that creates an instance of a <code>NXTMotor</code> using the specified motor port. This instance is then
     * use to drive the actuater motor.
     * @param port The motor port that the motor will be attached to.
     * @see lejos.nxt.MotorPort
     * @see NXTMotor
     */
    public LnrActrFirgelliNXT(TachoMotorPort port) {
        this(new NXTMotor(port));
    }
    
    /** Sets the power for the actuator. This is called before the <code>actuate()</code> method is called to set the power.
     * Using lower power values and pushing/pulling
     * an excessive load may cause a stall. Stall detection will stop the current actuator action.
     * <p>
     * Default power value on instantiation is zero.
     * @param power power setting: 0-100%
     * @see #actuate
     */
    public void setPower(int power){
        

        power=Math.abs(power);
        power = (power>100)?100:power;
        _userSpecifiedPower=power;
        // calc real power with proper ranging
         _realPower = Math.round((float)power/100 * (100-MIN_POWER) + MIN_POWER);
        _encoderMotor.setPower(_realPower);
        
        // calc encoder tick/ms based on my testing. y=mm/sec, x=power
        // y=.135 * x -1.5 + 20%  R2=0.989
         _tick_wait = (int)(500/(0.135f * _realPower - 1.5)*1.2) ;
//        LCD.drawString(_realPower + "," + _tick_wait + " ",0,7);
    }

    public int getPower() {
        return _userSpecifiedPower;
    }
 
    /** Returns true if the actuator is in motion.
     * @return true if the actuator is in motion.
     */
    public boolean isMoving() {
        return _isMoveCommand ; //&& !_isStalled;
    }
    
    public boolean isStalled() {
        return _isStalled; //&& _isMoveCommand; 
    }
    
    
    /**Causes the actuator to move <code>distance</code> in encoder ticks. The <code>distance</code> is relative to the actuator 
     * shaft position at the time of
     * calling this method. Stall detection stops the actuator in the event of a stall condition.
     * <P>
     * If <code>immediateReturn</code> is <code>true</code>, this method returns immediately (does not block) and the actuator stops when the
     * stroke <code>distance</code> is met [or a stall is detected]. If <code>actuate</code> is called before the stroke
     * distance is reached, the current actuator action is cancelled. 
     * <p>
     * If the stroke <code>distance</code> specified exceeds the maximum 
     * stroke length (fully extended or retracted against the end stop), stall detection will stop the action. It is advisable 
     * not to extend or retract to the 
     * stop as this is hard on the actuator. If you must go all the way to an end stop and rely on stall detection to stop the
     * action, use a lower power setting.
     * 
     * @param distance The Stroke distance in encoder ticks. See <tt>{@link #getTachoCount}</tt>.
     * @param immediateReturn Set to <code>true</code> to cause the action to occur in its own thread and immediately return. 
     * <code>false</code> will block until the action is completed (which includes a stall)
     * @see #setPower
     */
    public synchronized void actuate(int distance, boolean immediateReturn ){
        // set globals
         _dirExtend=distance>=0;
        _distanceTicks = Math.abs(distance);
         // initiate the action
        doAction(immediateReturn);
    }

    private void doAction(boolean immediateReturn){
        if (_realPower<=MIN_POWER) return;
        
        // If we already have an active command, signal it to cease and wait until cleared
        _killCurrentAction=true;
        if (_isMoveCommand) while(_killCurrentAction) Thread.yield();
        
        // initiate the action
        _doActuate=true; 
        
        // if told to block, wait until the actuator thread calls stopActuator() (via toExtent())
        if (!immediateReturn) {
            while(_doActuate) {
                Thread.yield();
            }
        }
    }
    
    
    /** This thread does the actuator control
     */
    private class Actuator implements Runnable{
        public void run() {
            while(true) {
                doWait(50);
                if (_doActuate) {
                    _isMoveCommand = true; 
                    toExtent(); // this blocks so _killCurrentAction will work like this
                    _doActuate = false;
                }
            }
        }
    }

    /** This thread determines if the actuator has stalled.
     */
    private class StallDetector implements Runnable{
        private final int STALL_COUNT = 2; 
        private int begTacho;
        private long begTime;
        
        public void run() {
            begTacho = _encoderMotor.getTachoCount();
            
            while (true) {                
                Delay.msDelay(_tick_wait);
                if (!_isMoveCommand) continue;
                
                // Stall check. if no tacho change...
                if (begTacho==_encoderMotor.getTachoCount()) {
                    // ...and we exceed STALL_COUNT wait periods and have been command to move, it probably means we have stalled
                    if (System.currentTimeMillis()- begTime>_tick_wait*STALL_COUNT) {
                        // so set this so toExtent() will exit it's tacho monitor loop and call stopActuator()
                        if (!_isStalled) _isStalled=true;
                    }
                } else {
                    // The tacho is moving, get the current point and time for next comparision
                     if (_isStalled) _isStalled=false;
                    begTacho=_encoderMotor.getTachoCount();
                    begTime = System.currentTimeMillis();
                }
                
                // stop any motor action if stalled
                if (_enableStallStop && _isStalled) _killCurrentAction=true;

//                LCD.drawString("ismv=" + _isMoveCommand + " ",0,5);
//                LCD.drawString("stall=" + _isStalled + " ",0,6);
                
            }
        }
    }

    private void motorGoDirection(){
        if (_dirExtend) {
            _encoderMotor.forward(); 
        } else {
            _encoderMotor.backward(); 
        }
        
        // wait until the motor starts moving (with a time limit)
        long begTime = System.currentTimeMillis();
        _enableStallStop=false;
        if (!_isStalled) _isStalled=true;
        while(_isStalled) {
            Thread.yield();
            // kill the move and exit if it takes too long to start    
            if (System.currentTimeMillis() - begTime>(_tick_wait*3)) {
//                LCD.drawString("!!DoAct tmout  ",0,7);
                _killCurrentAction=true; // will cause the loop to immediately finish
                break;
            }
        }
        _enableStallStop=true;
    }
    
    private synchronized void toExtent() {
        // the MoveDetector thread sets _isMoving. The MoveDetector does this when _doActuate is set true. _doActuate is
        // set by doAction() which is called for extend or retract. The Actuator thread calls this method when _doActuate=true
        int power = _realPower;
        int tacho=0;
        
        resetTachoCount();
        _killCurrentAction = false;
        // engage!
        motorGoDirection(); 
        
//        LCD.drawString("toExtent ",0,5);
        while (!_killCurrentAction) {
            tacho = Math.abs(_encoderMotor.getTachoCount());
//            LCD.drawString("t=" + tacho + " ",0,4);
             // reduce speed when near destination when at higher speeds
            if (_distanceTicks-tacho<=4&&power>80) _encoderMotor.setPower(70);
            // exit loop if destination is reached
            if (tacho>=_distanceTicks) break;
            // if power changed during this run.... (used only if immediateReturn=true)
            if (power!=_realPower) {
                power = _realPower;
                _encoderMotor.setPower(power);
            }
            if (_killCurrentAction) break;
            doWait(40);
        }
        // stop the motor
        stop(); 
        if (_distanceTicks-tacho<=4&&power>80) _encoderMotor.setPower(_realPower);
    }

    private static void doWait(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            ; // do nothing
            //Thread.currentThread().interrupt();
//            LCD.drawString("wait interrupted", 0, 7);
//            Button.waitForPress();
        }
    }

    /** Immediately stop any current actuator action.
     */
    public void stop() {
//        dbgcount++;
//        LCD.drawString("go " + dbgcount + " ",0,7);
        
        _killCurrentAction = true;
        _isMoveCommand = false; 
//        _doActuate = false;
        _encoderMotor.stop();
        doWait(10); // give it some time to brake
        _encoderMotor.flt();
    }

    /**Returns the tachometer (encoder) count. The Firgelli L12-NXT-50 & 100 use 0.5 mm/encoder tick. eg: 200 ticks=100 mm.
     * @return tachometer count in encoder ticks.
     * @see #resetTachoCount
     */
    public int getTachoCount() {
       return _encoderMotor.getTachoCount();
    }
    
    /**Resets the tachometer (encoder) count to zero. 
     * @see #getTachoCount
     */
    public void resetTachoCount() {
        _encoderMotor.resetTachoCount();
    }
    
}

