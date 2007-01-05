package lejos.robotics;


	import lejos.nxt.*;

//	 !! For rotation, it should straighten out when stopped to try to keep
//	 the angle somewhat accurate.

//	 !! Should all methods call stop() first in case it was roaming?
//	 OR methods account for NXT currently in moving mode?

//	 !! All methods that change x, y must be synchronized.

	/**
	 * The SimpleNavigator class contains methods for performing basic navigational
	 * movements. This class uses the tachometers built into the NXT motors to monitor 
	 * the wheels of the differential drive.  This class also assumes the Motor.forward()
	 * command will cause the drive wheels to move in a forward direction.<BR>
	 * Note: This class will only work for robots using two motors to steer differentially
	 * that can rotate within its footprint (i.e. turn on one spot).
	 * 
	 * @author <a href="mailto:bbagnall@escape.ca">Brian Bagnall</a>
	 * @version 0.1  19-August-2001
	 */
	public class SimpleNavigator implements Navigator {

	   // orientation and co-ordinate data
	   private float angle;
	   private float x;
	   private float y;

	   // Motors for differential steering:
	   private Motor left;
	   private Motor right;
	   
	   // Movement values:
	   private float COUNTS_PER_CM;
	   private float COUNTS_PER_DEGREE;
	   
	   // Internal states
	   private boolean moving;
	   
	   /**
	   * Allocates a RotationNavigator object and initializes it with the proper motors.
	   * The x and y values will each equal 0 (cm's) on initialization, and the starting
	   * angle is 0 degrees, so if the first move is forward() the robot will run along
	   * the x axis. <BR>
	   * @param wheelDiameter The diameter of the wheel, usually printed right on the
	   * wheel, in centimeters (e.g. 49.6 mm = 4.96 cm) 
	   * @param driveLength The distance from the center of the left tire to the center
	   * of the right tire, in centimeters.
	   * @param rightMotor The motor used to drive the right wheel e.g. Motor.C.
	   * @param leftMotor The motor used to drive the left wheel e.g. Motor.A.
	   */
	   
	   public SimpleNavigator(float wheelDiameter, float driveLength, Motor leftMotor, Motor rightMotor) {
	      this.right = rightMotor;
	      this.left = leftMotor;
	      
	      left.resetTachoCount();
	      right.resetTachoCount();

	      // Set coordinates and starting angle:
	      angle = 0.0f;
	      x = 0.0f;
	      y = 0.0f;
	      
	      moving = false;
	      
	      // Calculate the counts per centimeter
	      float wheelCircumference = wheelDiameter * (float)Math.PI;
	      COUNTS_PER_CM = 360/wheelCircumference;
	      
	      // Calculate counts per degree
	      float fullRotation = (driveLength * (float)Math.PI);
	      COUNTS_PER_DEGREE = (fullRotation/wheelCircumference);
	   }
	   
	   /**
	   * Overloaded SimpleNavigator constructor that assumes the following:<BR>
	   * Left motor = Motor.A   Right motor = Motor.C <BR>
	   * @param wheelDiameter The diameter of the wheel, usually printed right on the
	   * wheel, in centimeters (e.g. 49.6 mm = 4.96 cm) 
	   * @param driveLength The distance from the center of the left tire to the center
	   * of the right tire, in centimeters.
	   */
	   public SimpleNavigator(float wheelDiameter, float driveLength) {
	      this(wheelDiameter, driveLength, Motor.A, Motor.C);
	   }
	   
	   /**
	   * Returns the current x coordinate of the NXT.
	   * Note: At present it will only give an updated reading when the NXT is stopped.
	   * @return float Present x coordinate.
	   */
	   public float getX() {
	      // !! In future, if NXT is on the move it should return the present calculation of x
	      return x;
	   }
	   
	   /**
	   * Returns the current y coordinate of the NXT.
	   * Note: At present it will only give an updated reading when the NXT is stopped.
	   * @return float Present y coordinate.
	   */
	   public float getY() {
	      return y;
	   }

	   /**
	   * Returns the current angle the NXT robot is facing.
	   * Note: At present it will only give an updated reading when the NXT is stopped.
	   * @return float Angle in degrees.
	   */
	   public float getAngle() {
	      return angle;
	   }

	   /**
	   * Rotates the NXT robot a specific number of degrees in a direction (+ or -).
	   * This method will return once the rotation is complete.
	   *
	   * @param angle Angle to rotate in degrees. A positive value rotates left, a negative value right.
	   */
	   public void rotate(float angle) {
	      
	      // keep track of angle
	      this.angle = this.angle + angle;
	      this.angle = (int)this.angle % 360; // Must be < 360 degrees
	      
	      // Is it possible to do the following with modulo (%) ???
	      while(this.angle < 0)
	         this.angle += 360;  // Must be > 0
	      
	      // Calculate the number of intervals of rotation sensor to count
	      int count = (int)(COUNTS_PER_DEGREE * angle);
	      
	      if (count > 0) {
	         right.rotate((int) count);
	         left.rotate((int) -count);
	      }
	      else if (count < 0) {
	         right.rotate((int) -count);
	         left.rotate((int) count);
	      }
	      
	      while (left.isRotating() || right.isRotating());
	   }

	   /**
	   * Rotates the NXT robot to point in a certain direction. It will take the shortest
	   * path necessary to point to the desired angle. Method returns once rotation is complete.
	   *
	   * @param angle The angle to rotate to, in degrees.
	   */
	   public void gotoAngle(float angle) {
	      // in future, use modulo instead of while loop???
	      float difference = angle - this.angle;
	      while(difference > 180)
	         difference = difference - 360; // shortest path to goal angle
	      while(difference < -180)
	         difference = difference + 360; // shortest path to goal angle
	      rotate(difference);
	   }

	   /**
	   * Rotates the NXT robot towards the target point and moves the required distance.
	   *
	   * @param x The x coordinate to move to.
	   * @param y The y coordinate to move to.
	   */
	   public void gotoPoint(float x, float y) {

	      // Determine relative points
	      float x1 = x - this.x;
	      float y1 = y - this.y;

	      // Calculate angle to go to:
	      float angle = (float)Math.atan2(y1,x1);

	      // Calculate distance to travel:
	      float distance;
	      if(y1 != 0)
	         distance = y1/(float)Math.sin(angle);
	      else
	         distance = x1/(float)Math.cos(angle);

	      // Convert angle from rads to degrees:
	      angle = (float)Math.toDegrees(angle);
	      
	      // Now convert theory into action:
	      gotoAngle(angle);
	      travel(Math.round(distance));
	   }

	   /**
	   * Moves the NXT robot a specific distance. A positive value moves it forwards and
	   * a negative value moves it backwards. Method returns when movement is done.
	   *
	   * @param dist The positive or negative distance to move the robot (in centimeters).
	   */
	   public void travel(int dist) {
	   // !! The command != STOP lines need to be tested!!!
	   // !! Should stop and exit travel() when travel() interrupted by stop()
	      int counts = (int)(dist * COUNTS_PER_CM);
	      
		  left.resetTachoCount();
		  right.resetTachoCount();
		  
	      if(dist > 0) {
	    	  left.rotate(counts);
	    	  right.rotate(counts);
	      } else
	      if(dist < 0) {
	    	  left.rotate(-counts);
	    	  right.rotate(-counts);
	      }
	      while (left.isRotating() || right.isRotating());
	      moving = true;
	      stop();
	   }

	   /**
	   * Moves the NXT robot forward until stop() is called.
	   *
	   * @see Navigator#stop().
	   */
	   public void forward() {
		  moving = true;
		  left.resetTachoCount();
		  right.resetTachoCount();
		  left.forward();
		  right.forward();
	   }

	   /**
	   * Moves the NXT robot backward until stop() is called.
	   *
	   * @see Navigator#stop().
	   */
	   public void backward() {
		  moving = true;
		  left.resetTachoCount();
		  right.resetTachoCount();
		  left.backward();
		  right.backward();
	   }

	   /**
	   * Halts the NXT robot and calculates new x, y coordinates.
	   *
	   * @see Navigator#forward().
	   */
	   public void stop() {
	      if(moving) {
	    	 moving = false;
	         left.stop();
	         right.stop();
	         
	         // Recalculate x-y coordinates based on rotation sensors

	         int rotAvg = (left.getTachoCount() + right.getTachoCount()) / 2;
	         float centimeters = rotAvg / COUNTS_PER_CM;
	      
	         // update x, y coordinates
	         x = x + (float)(Math.cos(Math.toRadians(angle)) * centimeters);
	         y = y + (float)(Math.sin(Math.toRadians(angle)) * centimeters);
	      }
	   }
	}


