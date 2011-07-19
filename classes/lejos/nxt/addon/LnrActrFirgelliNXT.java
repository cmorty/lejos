package lejos.nxt.addon;

import lejos.nxt.NXTMotor;
import lejos.nxt.TachoMotorPort;

import lejos.robotics.EncoderMotor;
import lejos.robotics.LinearActuator;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/** A Linear Actuator class that provides non-blocking actions and stall detection. Developed for the Firgelli L12-NXT-50 and L12-NXT-100
 * but may work for others. These linear actuators are self contained units which include an electric motor and encoder. They will push 
 * up to 25N and move at 12mm/s unloaded. 
 * <p>
 * See <a href="http://www.firgelli.com">www.firgelli.com.</a>.
 * @author Kirk P. Thompson
 * 
 */
public class LnrActrFirgelliNXT implements LinearActuator{
    private final int MIN_POWER = 30;
    private EncoderMotor _encoderMotor;
    private int _realPower =0;
    private int _userSpecifiedPower =0;
    private int _tick_wait; // this is calculated in setPower() to fit the power setting. Variable because lower powers move it slower.
    private boolean _isMoveCommand = false;
    private boolean _isStalled=false;
    private Thread _stallDetector;
    private Thread _actuator;
    private boolean _dirExtend = true;
    private int _distanceTicks;
    private boolean _killCurrentAction=true;
    private boolean _enableStallStop;
    private Object _lockObj = new Object();
    private int _tachoCount=0;
    
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
        _encoderMotor=encoderMotor;
        _encoderMotor.flt();
        
        setPower(0);
        // set up the threads
        _stallDetector = new Thread(new StallDetector());
        _stallDetector.setPriority(Thread.MAX_PRIORITY - 1);
        _stallDetector.setDaemon(true);
        _stallDetector.start();
        _actuator = new Thread(new Actuator());
        _actuator.setDaemon(true);
        _actuator.start();
        doWait(100); 
    }

    /** Convenience constructor that creates an instance of a <code>NXTMotor</code> using the specified motor port. This instance is then
     * used to drive the actuator motor.
     * @param port The motor port that the linear actuator is attached to.
     * @see lejos.nxt.MotorPort
     * @see NXTMotor
     */
    public LnrActrFirgelliNXT(TachoMotorPort port) {
        this(new NXTMotor(port));
    }
    
    /**Sets the power for the actuator. This is called before the <code>actuate()</code> or <code>actuateTo()</code> method is called 
     * to set the power.
     * Using lower power values and pushing/pulling
     * an excessive load may cause a stall and in this case, stall detection will stop the current actuator action and 
     * set the stalled condition flag.
     * <p>
     * Default power value on instantiation is zero.
     * @param power power setting: 0-100%
     * @see #actuate
     * @see #isStalled
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
         //dbg("_tick_wait " + _tick_wait);
    }
          
    /**
    * Returns the current actuator motor power setting.
    * @return current power 0-100%
    */
    public int getPower() {
        return _userSpecifiedPower;
    }
 
    /**Returns true if the actuator is in motion.
     * @return true if the actuator is in motion.
     */
    public boolean isMoving() {
        return _isMoveCommand; 
    }

    /** Returns true if an <code>actuate()</code> or <code>actuateTo()</code> order ended due to a motor stall. This behaves 
     * like a latch where the 
     * reset of the stall status is done on a new <code>actuate()</code> or <code>actuateTo()</code> order. 
     * @return <code>true</code> if actuator motor stalled during a movement order. <code>false</code> otherwise.
     * @see #actuate
     */
    public boolean isStalled() {
        return _isStalled; 
    }
    
//    private void dbg(String msg) {
//        System.out.println(msg);
//    }
    
    /**Causes the actuator to move <code>distance</code> in encoder ticks. The <code>distance</code> is relative to the actuator 
     * shaft position at the time of calling this method. 
     * Positive values extend the actuator shaft while negative values retract it. 
     * The Firgelli L12-NXT-50 & 100 use 0.5 mm/encoder tick. eg: 200 ticks=100 mm. 
     * <p>
     * Stall detection stops the actuator in the event of a stall condition to help prevent damage to the actuator.
     * <P>
     * If <code>immediateReturn</code> is <code>true</code>, this method returns immediately (does not block) and the actuator stops when the
     * stroke <code>distance</code> is met [or a stall is detected]. If another <code>actuate</code> action is called before the 
     * stroke
     * distance is reached, the current actuator action is cancelled and the new action is initiated.
     * <p>
     * If the stroke <code>distance</code> specified exceeds the maximum 
     * stroke length (fully extended or retracted against an end stop), stall detection will stop the action. It is advisable 
     * not to extend or retract to the 
     * stop as this is hard on the actuator. If you must go all the way to an end stop and rely on stall detection to stop the
     * action, use a lower power setting.
     * 
     * @param distance The Stroke distance in encoder ticks. 
     * @param immediateReturn Set to <code>true</code> to cause the method to immediately return while the action is executed in
     * the background. 
     * <code>false</code> will block until the action is completed, whether successfully or stalled.
     * @see #setPower
     * @see #stop
     * @see #getTachoCount
     * @see #actuateTo
     */
    public synchronized void actuate(int distance, boolean immediateReturn ){
        // set globals
         _dirExtend=distance>=0;
        _distanceTicks = Math.abs(distance);
         // initiate the action
        doAction(immediateReturn);
    }

    /** Causes the actuator to move to absolute <code>position</code> in encoder ticks. The <code>position</code> of the actuator
     * shaft on startup or when set by <code>resetTachoCount()</code> is zero.
     * @param position The absolute shaft position in encoder ticks.
     * @param immediateReturn Set to <code>true</code> to cause the method to immediately return while the action is executed in
     * the background. 
     * @see #actuate
     * @see #resetTachoCount
     */
    public void actuateTo(int position, boolean immediateReturn ){
        int distance = position - _tachoCount;
        actuate(distance, immediateReturn);
    }
    // only called by actuate()
    private void doAction(boolean immediateReturn){
        if (_realPower<=MIN_POWER) return;
        
        // If we already have an active command, signal it to cease and wait until cleared
        if (_isMoveCommand) {
            _killCurrentAction=true;
            synchronized(_lockObj){
                try {
//                    dbg("doact wait");
                    _lockObj.wait();
                } catch (InterruptedException e) {
//                    dbg("wait done");
                }
            }
        }
        
        // initiate the action by waking up the actuator thread to do the action
        synchronized (_actuator) {
            _actuator.notify();
        }
        
        // if told to block, wait until the actuator thread completes its current task. When done, it will do a notify()
        if (!immediateReturn) {
//            dbg("blk wait");
            synchronized(_lockObj){
                try {
                    _lockObj.wait();
                } catch (InterruptedException e) {
//                    dbg("blk wait done");
                }
            }
        }
    }
    
    
    /**This thread does the actuator control
     */
    private class Actuator implements Runnable{
        public void run() {
            while(true) {
                // wait until triggered to do an actuation
                synchronized (_actuator) {
                    try {
                        _actuator.wait();
                    } catch (InterruptedException e) {
                        ; // do nothing and continue
                    }
                }
                // set state to indicate an action is in effect
                _isMoveCommand=true;
                // this blocks. When finished, toExtent() will reset _isMoveCommand, etc. w/ call to stop()
                toExtent(); 
            }
        }
        
        // starts the motor and waits until move is completed or interrupted with _killCurrentAction
        // flag which in effect, causes the thread wait/block until next command is issued (_isMoveCommand set to true)
        private void toExtent() {
            // the MoveDetector thread sets _isMoving. The MoveDetector does this when _doActuate is set true. _doActuate is
            // set by doAction() which is called for extend or retract. The Actuator thread calls this method when _doActuate=true
            int power = _realPower;
            int tacho=0, temptacho=_tachoCount;
            
            _encoderMotor.resetTachoCount();
            _killCurrentAction = false;
            // initiate the actuator action
            motorGoDirection(); 
            // monitor and stop when stalled or action completes
            while (!_killCurrentAction) {
                tacho = _encoderMotor.getTachoCount();
                _tachoCount = temptacho - tacho;
                tacho=Math.abs(tacho);
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
            _tachoCount=temptacho-_encoderMotor.getTachoCount();
            // wake up the any wait in doAction()
            synchronized(_lockObj){
                _lockObj.notify();
            }
            // set the power back (if changed)
            if (_distanceTicks-tacho<=4&&power>80) _encoderMotor.setPower(_realPower);
        }
        // only called by toExtent(). starts the actuator movement and waits until moving or times out (stalled)
        private void motorGoDirection(){
            if (_dirExtend) {
                _encoderMotor.forward(); 
            } else {
                _encoderMotor.backward(); 
            }
            
            // wait until the actuator shaft starts moving (with a time limit)
            long begTime = System.currentTimeMillis();
            // flag to control the stall detector to set the _killCurrentAction flag if stalled. Since we are starting a move, we 
            // want to give some time for the motor to start and actuator to begin movement hence we don't want to issue a stop.
             _enableStallStop=false; 
            // wait until the shaft is moving or we timeout
            _isStalled=true;
            while(_isStalled) {
                doWait(5);
                // kill the move and exit if it takes too long to start    
                if (System.currentTimeMillis() - begTime>(_tick_wait*3)) {
//                    dbg("mgoact tmout");
                    _killCurrentAction=true; // will cause the actuate/monitor loop in toExtent() to never start
                    break;
                }
            }
            _enableStallStop=true;
        }
    }

    /**This thread determines if the actuator has stalled.
     */
    private class StallDetector implements Runnable{
        private final int STALL_COUNT = 2; 
        private int begTacho;
        private long begTime;
        
        public void run() {
            begTacho = _encoderMotor.getTachoCount();
            // monitor tacho and determine if we have a stall condition during an action
            while (true) {                
                doWait(_tick_wait);
                // if no current command, don't mess with flags
                if (!_isMoveCommand) continue;
                
                // Stall check. if no tacho change...
                if (begTacho==_encoderMotor.getTachoCount()) {
                    // ...and we exceed STALL_COUNT wait periods and have been command to move, it probably means we have stalled
                    if (System.currentTimeMillis()- begTime>_tick_wait*STALL_COUNT) {
                        // so set this so toExtent() will exit it's tacho monitor loop and call stopActuator()
                        _isStalled=true;
                    }
                } else {
                    // The tacho is moving, get the current point and time for next comparision
                    _isStalled=false;
                    begTacho=_encoderMotor.getTachoCount();
                    begTime = System.currentTimeMillis();
                }
                
                // stop any motor action if stalled and flag allows it
                if (_enableStallStop && _isStalled){
                    _killCurrentAction=true;
//                    dbg("stall & kill");
                }
            }
        }
    }
    
    private static void doWait(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            ; // do nothing
        }
    }

    /**Immediately stop any current actuator action.
     * @see #actuate
     */
    public void stop() {
        _killCurrentAction = true;
        _isMoveCommand = false; 
        _encoderMotor.stop();
    }

    /** Returns the absolute tachometer (encoder) position of the actuator shaft. The zero position of the actuator shaft is where 
     * <code>resetTachoCount()</code> was last called or the position of the shaft when instantiated. 
     * <p>
     * The Firgelli L12-NXT-50 & 100 use 0.5 mm/encoder tick. eg: 200 ticks=100 mm. 
     * 
     * @return tachometer count in encoder ticks.
     * @see #resetTachoCount
     */
    public int getTachoCount() {
       //return _encoderMotor.getTachoCount();
       return _tachoCount;
    }
    
    /**Resets the tachometer (encoder) count to zero at the current actuator shaft position.
     * @see #getTachoCount
     */
    public void resetTachoCount() {
        //_encoderMotor.resetTachoCount();
         _tachoCount=0;
    }
    
}

