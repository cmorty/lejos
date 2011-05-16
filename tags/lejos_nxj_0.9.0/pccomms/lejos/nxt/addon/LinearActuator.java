package lejos.nxt.addon;

//import lejos.nxt.Button;
//import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.util.Delay;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/** A Linear Actuator class that provides non-blocking actions and stall detection. Developed for the Firgelli L12-NXT-50 and L12-NXT-100
 * but may work for others. These linear actuators are self contained units which include an electric motor and encoder. They will push 
 * up to 25N and move at 12mm/s unloaded. See <a href="http://www.firgelli.com">www.firgelli.com.</a>.
 * @author Kirk P. Thompson, 3/3/2011 &lt;lejos@mosen.net&gt;
 */
public class LinearActuator {
    private MotorPort _motorPort;
    private int _power =0;
    private int _tick_wait=40; // this is calculated in setPower() to fit the power setting. Variable because lower powers move it slower.
    private volatile boolean _killThread = false;
    private volatile boolean _isMoving = false;
    private volatile boolean _doActuate = false;;
    private Thread moveDetector;
    private Thread actuator;
    private int _direction = MotorPort.FORWARD;
    private int _distanceTicks;
    private boolean _killCurrentAction;

    /** Create a <tt>LinearActuator</tt> instance.
     * Use this constructor to assign a variable of type <tt>MotorPort</tt> connected to a particular port.
     * @param motorPort The MotorPort (A,B,C) port to which the linear actuator is connected
     */
    public LinearActuator(MotorPort motorPort) {
        this._motorPort= motorPort;
        // set up the threads
        moveDetector = new Thread(new MoveDetector());
        moveDetector.setPriority(Thread.MAX_PRIORITY - 1);
        moveDetector.setDaemon(true);
        moveDetector.start();
        actuator = new Thread(new Actuator());
        actuator.setDaemon(true);
        actuator.start();
        doWait(100);
    }

    /** Sets the power for the actuator. This must be called before the <tt>extend()</tt> and <tt>retract()</tt> methods are called.
     * Values below 50 will be set as 50. Using lower power values and pushing/pulling
     * an excessive load may cause a stall. Stall detection will stop the current actuator action.
     * @param power power setting: 50 - 100
     * @see #extend
     * @see #retract
     */
    public void setPower(int power){
        this._power = (power>100)?100:power;
        if(power<50) this._power = 50;
        // calc encoder tick/ms based on my testing. y=mm/sec, x=power
        // y = 0.000043x3 - 0.009243x2 + 0.770185x - 15.223827
        // R2 = 0.997733
        // + 20%
        _tick_wait = (int)((500/(0.000043f*Math.pow(power,3)-0.009243f*Math.pow(power,2)+0.770185f*power-15.223827f))*1.2f);        
//        LCD.drawString(this._power + "," + _tick_wait + " ",0,7);
    }

    /** Returns true if the actuator is in motion.
     * @return true if the actuator is in motion.
     */
    public boolean isMoving() {
        return _isMoving;
    }
    
    private void doAction(boolean immediateReturn){
        if (_killThread || (_power==0)) return;
        // signal any current action to cease and wait until cleared
        _killCurrentAction=true;
        while(_killCurrentAction) Thread.yield();
        // initiate the action
        _doActuate=true;
        // wait until the MoveDetector thread sets _isMoving
        while(!_isMoving) Thread.yield();
        // if told to block, wait until the actuator thread calls stopActuator() (via toExtent())
        if (!immediateReturn) {while(_doActuate) Thread.yield();}
    }

    /**Causes the actuator to extend <tt>distance</tt> in encoder ticks. The <tt>distance</tt> is relative to the actuator shaft position at the time 
     * calling this method. Stall detection stops the actuator in the event of a stall condition.
     * <P>
     * If <tt>immediateReturn</tt> is true, this method returns immediately (does not block) and the actuator stops when the
     * stroke <tt>distance</tt> is met [or a stall is detected]. If <tt>extend</tt> or <tt>retract</tt> is called before the stroke
     * distance is reached, the current extension action is canceled. 
     * <p>
     * If the stroke <tt>distance</tt> specified exceeds the maximum 
     * stroke length (fully extended), the
     * actuator shaft will hit the end stop and the stall detection will stop the extension. It is advisable not to extend to the 
     * stop as this is hard on the actuator. If you must go all the way to an end stop, use a lower power setting.
     * 
     * @param distance The Stroke distance in encoder ticks. See <tt>{@link #getTachoCount}</tt>.
     * @param immediateReturn Set to <tt>true</tt> to cause the extension to occur in its own thread and immediately return.
     * @see #retract
     * @see #setPower
     */
    public void extend(int distance, boolean immediateReturn ){
        // set globals
        _direction = MotorPort.FORWARD;
        _distanceTicks = distance;
         // initiate the action
        doAction(immediateReturn);
    }
    
    /**Causes the actuator to retract <tt>distance</tt> in encoder ticks. The <tt>distance</tt> is relative to the actuator shaft position at the time 
     * calling this method. Stall detection stops the actuator in the event of a stall condition.
     * <P>
     * If <tt>immediateReturn</tt> is true, this method returns immediately (does not block) and the actuator stops when the
     * stroke <tt>distance</tt> is met [or a stall is detected]. If <tt>extend</tt> or <tt>retract</tt> is called before the 
     * stroke distance is reached, the current retraction action is canceled. 
     * <p>
     * If the stroke <tt>distance</tt> specified exceeds the maximum 
     * stroke length (fully retracted), the
     * actuator shaft will hit the end stop and the stall detection will stop the retraction. It is advisable not to retract to the 
     * stop as this is hard on the actuator. If you must go all the way to an end stop, use a lower power setting.
     * 
     * @param distance The Stroke distance in encoder ticks. See <tt>{@link #getTachoCount}</tt>.
     * @param immediateReturn Set to <tt>true</tt> to cause the retraction to occur in its own thread and immediately return.
     * @see #extend
     * @see #setPower
     */
    public void retract(int distance, boolean immediateReturn ){
        // set globals
        _direction = MotorPort.BACKWARD;
        _distanceTicks = distance;
         // initiate the action
        doAction(immediateReturn);
    }

    /** This thread does the actuator control
     */
    private class Actuator implements Runnable{
        private final int ACTUATE_POLL_DELAY = 50;
        public void run() {
            while(!_killThread) {
                doWait(ACTUATE_POLL_DELAY);
                if (_doActuate) {
                    toExtent(_direction, _distanceTicks);
                    _doActuate = false;
                }
                if (_killCurrentAction) _killCurrentAction=false;
            }
        }
    }

    /** Shut down the worker threads for this class and null the MotorPort ref. After this is called,
     * a new instance must be created,
     */
    public void shutdown(){
        _killThread=true;
        doWait(500);
        _motorPort=null;
    }

    /** This thread determines if the actuator is moving.
     */
    private class MoveDetector implements Runnable{
        private final int STALL_COUNT = 3; // FORWARD requires 3
        private final int WAIT_PERIOD = _tick_wait/4;
        private int begTacho;
        private long begTime;
        private boolean initActuate = true;
        
        public void run() {
            while (!_killThread) {                
                Delay.msDelay(WAIT_PERIOD);
                // start the logic when an actuator action is initiated from doAction()
                if (_doActuate) {
                    // on initial actuator action...
                    if (initActuate) {
                        _isMoving = true;
                        initActuate = false;
//                        LCD.drawString("mv=t ",0,4);
                        // set begin tacho and time
                        begTacho = _motorPort.getTachoCount();
                        begTime = System.currentTimeMillis();
                    }
                    // if no tacho change...
                    if (begTacho==_motorPort.getTachoCount()) {
                        // ...and we exceed STALL_COUNT wait periods, it probably means we have stalled
                        if (System.currentTimeMillis()- begTime>_tick_wait*STALL_COUNT) {
                            // so set this so toExtent() will exit it's tacho monitor loop and call stopActuator()
                            _isMoving = false;
//                            LCD.drawString("stall ",6,4);
                        }
                    } else {
                        // The tacho is moving, get the current point and time for comparision
                        begTacho = _motorPort.getTachoCount();
                        begTime = System.currentTimeMillis();
                    }
                } else {
                    // no actuator action has been specified...
                    _isMoving = false;
                    initActuate = true;
                }
//                if (Button.ESCAPE.isPressed()) { // TODO remove after testing
//                    stopActuator();
//                    Delay.msDelay(333);
//                }
            }
        }
    }

    private synchronized void toExtent(int direction, int ticks) {
        // the MoveDetector thread sets _isMoving. The MoveDetector does this when _doActuate is set true. _doActuate is
        // set by doAction() which is called for extend or retract. The Actuatro thread calls this method when _doActuate=true
        int power = _power;
        int tacho=0;
        _motorPort.resetTachoCount();
        _motorPort.controlMotor(_power, direction);
//        LCD.drawString("startActuator ",0,5);
//        LCD.drawString("      ",6,4);
        while (_isMoving && !_killCurrentAction) {
            tacho = Math.abs(getTachoCount());
            //             LCD.drawString("t=" + tacho + " ",0,4);
             // reduce speed when near destination when at higher speeds
            if (ticks-tacho<=4&&_power>80) _motorPort.controlMotor(70, direction);
            // exit loop if destination is reached
            if (tacho>=ticks) break;
            // if power changed during this run.... (used only if immediateReturn=true)
            if (power!=_power) {
                power = _power;
                _motorPort.controlMotor(power, direction);
            }
            doWait(_tick_wait);
        }
        stopActuator();
    }

    private static void doWait(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            ;
            //Thread.currentThread().interrupt();
//            LCD.drawString("wait interrupted", 0, 7);
//            Button.waitForPress();
        }
    }

    /** Immediately stop any current actuator action.
     */
    public void stopActuator() {
        _killCurrentAction = true;
        _doActuate = false;
        _motorPort.controlMotor(0, MotorPort.STOP);
        doWait(20);
        _motorPort.controlMotor(0, MotorPort.FLOAT);
//        LCD.drawString("stopActuator  ",0,5);
    }

    /**Returns the tachometer (encoder) count. The Firgelli L12-NXT-50 & 100 use 0.5 mm/encoder tick. eg: 200 ticks=100 mm.
     * @return tachometer count in encoder ticks.
     */
    public int getTachoCount() {
        return _motorPort.getTachoCount();
    }
    
}

