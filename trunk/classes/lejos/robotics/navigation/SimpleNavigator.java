package lejos.robotics.navigation;


    
import lejos.nxt.*;
import lejos.robotics.TachoMotor;

/**
 * The SimpleNavigator class can keep track of the robot position and the direction angle it faces;
 * It uses a Pilot  object to control NXT robot movements. The Pilot must be able to  turn in place, 
 * for example using 2 wheel differential steering <br>
 * This Navigator updates its position and direction angle values  when stop() is called or when
 * a movement control method returns after the movement is completed.
 * The movement control  methods can return immediately while the robot is still moving.
 * This can be useful to permit sensor monitoring for example.
 * If you use this option, your code MUST either call stop() or else call updatePosition()
 * after the robot stops moving and before before it  moves again;
 * otherwise, the position information is lost.  Your code may
 * call updatePosition() at any time, for example to  report robot progress . <br>
 *<b>A note about coordinates:</b> All angles are in degrees, distances in the units used to specify robot dimensions.
 * Angles related to positions in the plane are relative to the X axis ;  direction of the Y axis is 90 degrees.
 * The x and y coordinate values and the direction angle are all initialized to 0, so if the first move is forward() the robot will run along
 * the x axis.<br>

 */
public class SimpleNavigator implements Navigator {
    // orientation and co-ordinate data

    private float _heading = 0;
    private float _x = 0;
    private float _y = 0;
    private float _distance0 = 0;
    private float _angle0 = 0;
    private Pilot pilot;

    /**
     * Allocates a SimpleNavigator with a Pilot that you supply.
     * @param  pilot can be  any class that implements the pilot interface
     */
    public SimpleNavigator(Pilot pilot) {
        this.pilot = pilot;
    }

    /**
     * Allocates a SimpleNavigator object and initializes it with a new TachoPilot <br>
     * If you want to use a different Pilot class, use the single parameter constructor.
     * @param wheelDiameter The diameter of the wheel, usually printed on the Lego tire.
     *  Use any units you wish (e.g. 56 mm = 5.6 cm = 2.36 in)
     * @param trackWidth The distance from the center of the left tire to the center
     * of the right tire, same units as wheel diameter
     * @param rightMotor The motor used to drive the right wheel e.g. Motor.C.
     * @param leftMotor The motor used to drive the left wheel e.g. Motor.A.
     * @param reverse  If motor.forward() dives the robot backwars, set this parameter true.
   * 
   * @deprecated The correct way is to create the Pilot in advance and to use that in construction of the
   *             SimpleNavigator. Otherwise the SimpleNavigator needs to know detail it should not care about!
     */
    public SimpleNavigator(float wheelDiameter, float trackWidth, TachoMotor leftMotor, TachoMotor rightMotor, boolean reverse) {
      // In the signature Motor was not changed to TachoMotor. This method only saves one to write "new TachoPilot" at the
      // cost of maintaining this method and comments, thus it should not be used!
        pilot = new TachoPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
    }

    /**
     * Allocates a SimpleNavigator object and initializes it with a new TachoPilot.<br>
     * If you want to use a different Pilot class, use the single parameter constructor.
     * @param wheelDiameter The diameter of the wheel, usually printed on the Lego tire.
     *  Use any units you wish (e.g. 56 mm = 5.6 cm = 2.36 in)
     * @param trackWidth The distance from the center of the left tire to the center
     * of the right tire, sane units as wheel diameter
     * @param rightMotor The motor used to drive the right wheel e.g. Motor.C.
     * @param leftMotor The motor used to drive the left wheel e.g. Motor.A
   * 
   * @deprecated The correct way is to create the Pilot in advance and to use that in construction of the
   *             SimpleNavigator. Otherwise the SimpleNavigator needs to know detail it should not care about!
     */
    public SimpleNavigator(float wheelDiameter, float trackWidth, TachoMotor leftMotor, TachoMotor rightMotor) {
      // In the signature Motor was not changed to TachoMotor. This method only saves one to write "new TachoPilot" at the
      // cost of maintaining this method and comments, thus it should not be used!
        pilot = new TachoPilot(wheelDiameter, trackWidth, leftMotor, rightMotor);
    }

    /**
     * Return the pilot used by this navigator
     * @return the pilot.
     */
    public Pilot getPilot() {
        return pilot;
    }

    public float getX() {
        return _x;
    }
    public float getY() {
        return _y;
    }

    public float getAngle() {
        return _heading;
    }

    public void setPosition(float x, float y, float directionAngle) {
        _x = x;
        _y = y;
        _heading = directionAngle;
    }

    public void setMoveSpeed(float speed) {
        pilot.setMoveSpeed(speed);
    }


    public void setTurnSpeed(float speed) {
        pilot.setTurnSpeed(speed);
    }


    public void forward() {
        reset();
        pilot.forward();
    }


    public void backward() {
        reset();
        pilot.backward();
    }


    public void stop() {
        pilot.stop();
        updatePosition();
    }


    public boolean isMoving() {
        return pilot.isMoving();
    }

    /**
     * Moves the NXT robot a specific distance. A positive value moves it forwards and
     * a negative value moves it backwards.
     * The robot position is updated atomatically when the method returns.
     * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
     */
    public void travel(float distance) {
        travel(distance, false);
    }

    /**
     * Moves the NXT robot a specific distance. A positive value moves it forwards and
     * a negative value moves it backwards.
     *  If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
     * when the robot has stopped.  Otherwise, the robot position is lost.
     * @param distance The positive or negative distance to move the robot, same units as _wheelDiameter
     * @param immediateReturn if true, the method returns immediately, in which case the programmer <br>
     *  is responsible for calling updatePosition() before the robot moves again.
     */
    public void travel(float distance, boolean immediateReturn) {
        reset();
        pilot.travel(distance, immediateReturn);
        if (!immediateReturn) {
            updatePosition();
        }

    }
    public void rotateLeft() {
        reset();
        pilot.steer(200);
    }


    public void rotateRight() {
        reset();
        pilot.steer(-200);
    }

    /**
     * Rotates the NXT robot through a specific number of degrees in a direction (+ or -).
     * Robot position is updated when the method exits.
     * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
     **/
    public void rotate(float angle) {
        rotate(angle, false);
    }

    /**
     * Rotates the NXT robot through a specific number of degrees in a direction (+ or -).
     *  If immediateReturn is true, method returns immidiately and your code MUST call updatePostion()
     * when the robot has stopped.  Otherwise, the robot position is lost.
     * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
     * @param immediateReturn if true, the method returns immediately,
     * in which case your code must call  updatePosition() before the robot moves again.
     */
    public void rotate(float angle, boolean immediateReturn) {
        reset();
        int turnAngle = Math.round(angle);
        pilot.rotate(turnAngle, immediateReturn);
        if (!immediateReturn) {
            updatePosition();
        }
    }

    /**
     * Rotates the NXT robot to point in a specific direction, using the smallest
     * rotation necessary
     * The robot position is updated when this method exits;
     * It will make the smallest rotation necessary.
     * @param angle The angle to rotate to, in degrees.
     */
    public void rotateTo(float angle) {
        rotateTo(angle, false);
    }

    /**
     * Rotates the NXT robot to point in a specific direction relative to the x axis.  It make the smallest
     * rotation  necessary .
     * If immediateReturnis true, method returns immidiately and your code MUST call updatePostion()
     * when the robot has stopped.  Otherwise, the robot position is lost.
     * @param angle The angle to rotate to, in degrees.
     * @param immediateReturn if true,  method returns immediately and your code must call
     * updatePosition() before the robot moves again.
     */
    public void rotateTo(float angle, boolean immediateReturn) {
        float turnAngle = normalize(angle - _heading);
        rotate(turnAngle, immediateReturn);
    }


    public void goTo(float x, float y) {
        goTo(x, y, false);
    }


    public void goTo(float x, float y, boolean immediateReturn) {
        rotateTo(angleTo(x, y));
        travel(distanceTo(x, y), immediateReturn);
        if (!immediateReturn) {
            updatePosition();
        }
    }


    public float distanceTo(float x, float y) {
        float dx = x - _x;
        float dy = y - _y;
        //use hypotenuse formula
        return (float) Math.sqrt(dx * dx + dy * dy);
    }


    public float angleTo(float x, float y) {
        float dx = x - _x;
        float dy = y - _y;
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }

   
    public void updatePosition() {
        float distance = pilot.getTravelDistance() - _distance0;

        float turnAngle = pilot.getAngle() - _angle0;
        double dx = 0;
        double dy = 0;
        double headingRad = (Math.toRadians(_heading));
        if (Math.abs(turnAngle) > .5) {
            double turnRad = Math.toRadians(turnAngle);
            double radius = distance / turnRad;
            dy = radius * (Math.cos(headingRad) - Math.cos(headingRad + turnRad));
            dx = radius * (Math.sin(headingRad + turnRad) - Math.sin(headingRad));
        } else if (Math.abs(distance) > .01) {
            dx = distance * (float) Math.cos(headingRad);
            dy = distance * (float) Math.sin(headingRad);
        }
        _heading = normalize(_heading + turnAngle); // keep angle between -180 and 180
        _x += dx;
        _y += dy;
        _angle0 = pilot.getAngle();
        _distance0 = pilot.getTravelDistance();
    }

    /**
     * Starts  the NXT robot moving in a circular path with a specified radius. <br>
     * The center of the turning circle is on the left side of the robot if parameter radius is positive
     * and on the right if negative.  <br>
     * @param radius - the radius of the circular path. If positive, the left wheel is on the inside of the turn.
     * If negative, the left wheel is on the outside.
     */
    public void arc(float radius) {
        reset();
        pilot.arc(radius);
    }

    /**
     * Moves the NXT robot in a circular arc through the specified angle;  <br>
     * The center of the turning circle is on the left side of the robot if parameter radius is positive
     * and on the right if negative.
     * Robot will stop when total rotation equals angle. If angle is negative, robot will move travel backwards.
     * <br>See also {@link #travelArc(float radius, float distance)}
   * @param radius - the radius of the circular path. If positive, the left wheel is on the inside of the turn.
     * If negative, the left wheel is on the outside.
     */
    public void arc(float radius, int angle) {
        arc(radius, angle, false);
    }

    /**
     * Moves the NXT robot in a circular arc through a specific angle; <br>
     * The center of the turning circle is on the left side of the robot if parameter radius is positive
     * and on the right if negative.
     * Robot will stop when total rotation equals angle. If angle is negative, robot will travel backwards.
     * <br>See also {@link #travelArc(float radius, float distance, boolean immedisteReturn)}
      * @param radius - the radius of the circular path. If positive, the left wheel is on the inside of the turn.
     * If negative, the left wheel is on the outside.
     * @param angle The sign of the angle determines the direction of robot motion
     * @param immediateReturn if true, the method returns immediately and your code must call
     * updatePosition() before the robot moves again.
     */
    public void arc(float radius, int angle, boolean immediateReturn) {
        reset();
        pilot.arc(radius, angle, immediateReturn);
        if (!immediateReturn) {
            updatePosition();
        }
    }
     /**
     * Moves the NXT robot in a circular arc through a specific distance; <br>
     * The center of the turning circle is on the left side of the robot if parameter radius is positive
     * and on the right if negative.
     * Robot will stop when distance traveled equals distance. If distance is negative, robot will travel backwards.
     * <br>See also {@link #arc(float radius, int angle)}
   
     * @param radius  of the turning circle; the sign determines if the center if the turn is left or right of the robot.
     * @param distance The sign of the distance determines the direction of robot motion
     * @param immediateReturn if true, the method returns immediately and your code must call
     * updatePosition() before the robot moves again.
     */

    public void travelArc(float radius, float distance){
      travelArc(radius,distance,false);
    }
        /**
     * Moves the NXT robot in a circular arc through a specific distance; <br>
     * The center of the turning circle is on the left side of the robot if parameter radius is positive
     * and on the right if negative.
     * Robot will stop when distance traveled equals distance. If distance is negative, robot will travel backwards.
     * <br>See also {@link #arc(float radius, float distance, boolean immedisteReturn)}
     * @param radius  of the turning circle; the sign determines if the center if the turn is left or right of the robot.
     * @param distance The sign of the distance determines the direction of robot motion
     * @param immediateReturn if true, the method returns immediately and your code must call
     * updatePosition() before the robot moves again.
     */
    public void travelArc(float radius, float distance, boolean immediateReturn)
    {
            reset();
        pilot.travelArc(radius, distance, immediateReturn);
        if (!immediateReturn) {
            updatePosition();
        }
    }
    /**
     * returns equivalent angle between -180 and +180
     */
    private float normalize(float angle) {
        float a = angle;
        while (a > 180) {
            a -= 360;
        }
        while (a < -180) {
            a += 360;
        }
        return a;
    }

    private void reset() {
        _distance0 = 0;
        _angle0 = 0;
        pilot.reset();
    }
}

