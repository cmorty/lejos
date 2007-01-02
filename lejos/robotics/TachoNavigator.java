package lejos.robotics;


	import lejos.nxt.*;

//	 !! For rotation, it should straighten out when stopped to try to keep
//	 the angle somewhat accurate.

//	 !! Should all methods call stop() first in case it was roaming?
//	 OR methods account for RCX currently in moving mode?

//	 !! All methods that change x, y must be synchronized.

	/**
	 * The TachoNavigator class contains methods for performing basic navigational
	 * movements. This class uses two rotation sensors to monitor the wheels of the
	 * differential drive. For this class to work properly, the rotation sensors
	 * should record positive (+) values when the wheels move forward, and negative (-)
	 * values when the wheels move backward. This class also assumes the Motor.forward()
	 * command will cause the drive wheels to move in a forward direction.<BR>
	 * Note: This class will only work for robots using two motors to steer differentially
	 * that can rotate within its footprint (i.e. turn on one spot).
	 * 
	 * @author <a href="mailto:bbagnall@escape.ca">Brian Bagnall</a>
	 * @version 0.1  19-August-2001
	 */
	public class TachoNavigator implements Navigator {

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
	   private byte command;
	   
	   // command constants:
	   private byte STOP = 0;
	   private byte FORWARD = 1;
	   private byte BACKWARD = 2;
	   private byte LEFT_ROTATE = 3; // Unused
	   private byte RIGHT_ROTATE = 4; // Unused
	   
	   /**
	   * Allocates a RotationNavigator object and initializes if with the proper motors and sensors.
	   * The x and y values will each equal 0 (cm's) on initialization, and the starting
	   * angle is 0 degrees, so if the first move is forward() the robot will run along
	   * the x axis. <BR>
	   * Note: If you find your robot is going backwards or in circles when you tell it to go forwards, try
	   * rotating the wires to the motor ports by 90 or 180 degrees.
	   * @param wheelDiameter The diameter of the wheel, usually printed right on the
	   * wheel, in centimeters (e.g. 49.6 mm = 4.96 cm) 
	   * @param driveLength The distance from the center of the left tire to the center
	   * of the right tire, in centimeters.
	   * @param ratio The ratio of sensor rotations to wheel rotations.<BR>
	   *  e.g. 3 complete rotations of the sensor for every one turn of the wheel = 3f<BR>
	   * 1 rotation of the sensor for every 2 turns of the wheel = 0.5f
	   * @param rightMotor The motor used to drive the right wheel e.g. Motor.C.
	   * @param leftMotor The motor used to drive the left wheel e.g. Motor.A.
	   * @param rightRot Sensor used to read rotations from the right wheel. e.g. Sensor.S3
	   * @param leftRot Sensor used to read rotations from the left wheel. e.g. Sensor.S1
	   */
	   
	   public TachoNavigator(float wheelDiameter, float driveLength, Motor leftMotor, Motor rightMotor) {
	      this.right = rightMotor;
	      this.left = leftMotor;
	      
	      left.resetTachoCount();
	      right.resetTachoCount();

	      // Set coordinates and starting angle:
	      angle = 0.0f;
	      x = 0.0f;
	      y = 0.0f;
	      
	      moving = false;
	      command = STOP;
	      
	      // Calculate the counts per centimeter
	      float wheelCircumference = wheelDiameter * (float)Math.PI;
	      COUNTS_PER_CM = 360/wheelCircumference;
	      
	      // Calculate counts per degree
	      float fullRotation = (driveLength * (float)Math.PI);
	      COUNTS_PER_DEGREE = (fullRotation/wheelCircumference);
	      
	      // Thread is for keeping the RCX straight when driving by
	      // monitoring the rotation sensors while moving.
	      SteerThread steering = new SteerThread();
	      steering.setDaemon(true);
	      steering.start();
	   }
	   
	   /**
	   * Overloaded RotationNavigator constructor that assumes the following:<BR>
	   * Left motor = Motor.A   Right motor = Motor.C <BR>
	   * Left rotation sensor = Sensor.S1   Right rotation sensor = Sensor.S3
	   * @param wheelDiameter The diameter of the wheel, usually printed right on the
	   * wheel, in centimeters (e.g. 49.6 mm = 4.96 cm) 
	   * @param driveLength The distance from the center of the left tire to the center
	   * of the right tire, in centimeters.
	   * @param ratio The ratio of sensor rotations to wheel rotations.<BR>
	   *  e.g. 3 complete rotations of the sensor for every one turn of the wheel = 3f<BR>
	   * 1 rotation of the sensor for every 2 turns of the wheel = 0.5f
	   */
	   public TachoNavigator(float wheelDiameter, float driveLength) {
	      this(wheelDiameter, driveLength, Motor.A, Motor.C);
	   }
	   
	   /**
	   * Returns the current x coordinate of the RCX.
	   * Note: At present it will only give an updated reading when the RCX is stopped.
	   * @return float Present x coordinate.
	   */
	   public float getX() {
	      // !! In future, if RCX is on the move it should return the present calculation of x
	      return x;
	   }
	   
	   /**
	   * Returns the current y coordinate of the RCX.
	   * Note: At present it will only give an updated reading when the RCX is stopped.
	   * @return float Present y coordinate.
	   */
	   public float getY() {
	      return y;
	   }

	   /**
	   * Returns the current angle the RCX robot is facing.
	   * Note: At present it will only give an updated reading when the RCX is stopped.
	   * @return float Angle in degrees.
	   */
	   public float getAngle() {
	      return angle;
	   }

	   /**
	   * Rotates the RCX robot a specific number of degrees in a direction (+ or -).
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
	      
	      left.resetTachoCount();
	      right.resetTachoCount();
	      
	      if (angle > 0) {
	         right.forward();
	         left.backward();
	         while(left.getTachoCount() > -count || 
	        	   right.getTachoCount() < count) {}
	      }
	      else if (angle < 0) {
	         right.backward();
	         left.forward();
	         while(left.getTachoCount() < count || 
	        		 right.getTachoCount() > count) {}
	      }

	      right.stop();
	      left.stop();
	   }

	   /**
	   * Rotates the RCX robot to point in a certain direction. It will take the shortest
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
	   * Rotates the RCX robot towards the target point and moves the required distance.
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
	   * Moves the RCX robot a specific distance. A positive value moves it forwards and
	   * a negative value moves it backwards. Method returns when movement is done.
	   *
	   * @param dist The positive or negative distance to move the robot (in centimeters).
	   */
	   public void travel(int dist) {
	   // !! The command != STOP lines need to be tested!!!
	   // !! Should stop and exit travel() when travel() interrupted by stop()
	      int counts = (int)(dist * COUNTS_PER_CM);
	      
	      if(dist > 0) {
	         forward();
	         while(command != STOP && (left.getTachoCount() < counts || 
	        		                   right.getTachoCount() < counts)) {
	            Thread.yield();
	         }
	      } else
	      if(dist < 0) {
	         backward();
	         while(command != STOP && (left.getTachoCount() > counts || 
	        		                   right.getTachoCount() > counts)) {
	            Thread.yield();
	         }
	      }
	      stop();
	   }

	   /**
	   * Moves the RCX robot forward until stop() is called.
	   *
	   * @see Navigator#stop().
	   */
	   public void forward() {
		  left.resetTachoCount();
		  right.resetTachoCount();
	      command = FORWARD;
	   }

	   /**
	    * Inner class that monitors the rotation sensors and keeps the
	    * navigator steering straight.
	    */
	   private class SteerThread extends Thread {
	   public void run() {
	      while(true) {
	         while(command == FORWARD) {

	        	left.forward();
	            right.forward();
	            moving = true;
	            if(left.getTachoCount() > right.getTachoCount()) {
	               left.flt();
	               while(left.getTachoCount() > right.getTachoCount()) {}
	               left.forward();
	            }
	              
	            if(right.getTachoCount() > left.getTachoCount()) {
	               right.flt();
	               while(right.getTachoCount() > left.getTachoCount()) {}
	               right.forward();
	            }
	            Thread.yield();
	         }
	         
	         while(command == BACKWARD) {

	            left.backward();
	            right.backward();
	            moving = true;
	            if(left.getTachoCount() > right.getTachoCount()) {
	               left.flt();
	               while(left.getTachoCount() > right.getTachoCount()) {}
	               left.backward();
	            }
	              
	            if(right.getTachoCount() > left.getTachoCount()) {
	               right.flt();
	               while(right.getTachoCount() > left.getTachoCount()) {}
	               right.backward();
	            }
	            Thread.yield();
	         }
	         moving = false;
	         Thread.yield();
	      }
	   }
	   }
	   
	   /**
	   * Moves the RCX robot backward until stop() is called.
	   *
	   * @see Navigator#stop().
	   */
	   public void backward() {
		  left.resetTachoCount();
		  right.resetTachoCount();
	      command = BACKWARD;
	   }

	   /**
	   * Halts the RCX robot and calculates new x, y coordinates.
	   *
	   * @see Navigator#forward().
	   */
	   public void stop() {
	      if(moving) {
	         command = STOP;
	         while(moving) {
	            Thread.yield();
	         }
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

