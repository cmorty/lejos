package lejos.robotics.proposal;

import java.awt.geom.*;

import lejos.geom.Point;
import lejos.robotics.Movement;
import lejos.robotics.Pose;

/**
 * These methods can be used to to calculate theoretical routes and for displaying graphical representations of
 * the path of a robot. Specifically getAvailablePaths() and getBestPath() are useful for this.
 * 
 * TODO: Note, the upcoming space-search algorithm that finds the shortest path should only drive the straight
 * segment in reverse IF the distance is 1/2 the circumference of the minimum circle. The reasoning is that
 * the vehicle will drive a maximum distance of 1/2 circumference for the arc turn, so the same distance in
 * reverse for the straight segment is also acceptable. Later, when the circles at the target location are
 * factored in, this will also have some sort of effect on the final solution. 
 * @author bb
 * @version November 2009
 *
 */
public class ArcAlgorithms { // TODO Make package level when done testing?
	
	public static Movement [] getBestPath(Pose start, float turnRadius1, Pose destination, float turnRadius2) {
		// Get all paths TODO: This can probably be steamlined with arrays. Sort out Path (Move) container first.
		Movement [][] paths1 = getAvailablePaths(start, turnRadius1, destination, turnRadius2);
		Movement [][] paths2 = getAvailablePaths(start, turnRadius1, destination, -turnRadius2);
		Movement [][] paths3 = getAvailablePaths(start, -turnRadius1, destination, turnRadius2);
		Movement [][] paths4 = getAvailablePaths(start, -turnRadius1, destination, -turnRadius2);
		
		final int PATHS_PER_ARRAY = 4;
		final int ALL_PATHS = PATHS_PER_ARRAY * 4;
		Movement [][] paths = new Movement[ALL_PATHS][3];
		// TODO: This can probably be steamlined with arrays. Sort out Path (Move) container first.
		System.arraycopy(paths1, 0, paths, 0, PATHS_PER_ARRAY);
		System.arraycopy(paths2, 0, paths, 4, PATHS_PER_ARRAY);
		System.arraycopy(paths3, 0, paths, 8, PATHS_PER_ARRAY);
		System.arraycopy(paths4, 0, paths, 12, PATHS_PER_ARRAY);
		
		return getBestPath(paths);
	}
	
	public static Movement [][] getAvailablePaths(Pose start, float turnRadius1, Pose destination, float turnRadius2) {
		
		// TODO: These variables can perhaps be calculated based on existing parameters?
		final int PATHS = 4; // Currently doesn't calculate Pilot.backward() movement along P2 to P3
		final int MOVES_PER_PATH = 3;
				
		Movement [] [] paths = new Movement [PATHS] [MOVES_PER_PATH];
				
		// Draw start circle:
		Point2D.Float startCircle = ArcAlgorithms.findCircleCenter(start.getLocation(), turnRadius1, start.getHeading());
		
		// Draw target circle:
		Point2D.Float targetCircle = ArcAlgorithms.findCircleCenter(destination.getLocation(), turnRadius2, destination.getHeading());
		
		// Calculate "inner circle" (sometimes it is outer circle)
		float innerRadius = turnRadius1 - turnRadius2;
		
		float newHeading;
		Point2D.Float p2inner;
				
		// TODO: Special case if radius = 0 for both? 
		
		// Special case if radius is the same for both turns. 
		if(innerRadius == 0) {
			newHeading = ArcAlgorithms.getHeading(startCircle, targetCircle); 
			p2inner = startCircle; 
		} else { // END OF EQUAL RADII CODE
			// Find the p2 equivalent on the inner circle 
			p2inner = ArcAlgorithms.findP2(startCircle, targetCircle, innerRadius);
			
			// To find arcLength, need to make new p1 that sits on inner circle.
			Point2D.Float p1inner = ArcAlgorithms.findPointOnHeading(start.getLocation(), start.getHeading() + 90, turnRadius1);
			
			// Find new heading:
			float sArc = ArcAlgorithms.getArc(p1inner, p2inner, innerRadius, start.getHeading(), true);
			newHeading = (float)ArcAlgorithms.getHeading(start.getHeading(), sArc);
		} // END OF UNEQUAL RADII CODE
		
		// Find points p2 and p3:
		Point2D.Float p2 = ArcAlgorithms.findPointOnHeading(p2inner, newHeading - 90, turnRadius2);
		Point2D.Float p3 = ArcAlgorithms.findPointOnHeading(targetCircle, newHeading - 90, turnRadius2);
		
		// Find distance to drive straight segment:
		float p2p3 = ArcAlgorithms.distBetweenPoints(p2, p3);
		
		// Find arc lengths (forward and backward) to drive on startCircle:
		float startArcF = ArcAlgorithms.getArc(start.getLocation(), p2, turnRadius1, start.getHeading(), true);
		float startArcB = ArcAlgorithms.getArcBackward(startArcF);
						
		// Find arc lengths (forward and backward) to drive on targetCircle:
		float targetArcF = -ArcAlgorithms.getArc(destination.getLocation(), p3, turnRadius2, destination.getHeading(), false);
		float targetArcB = ArcAlgorithms.getArcBackward(targetArcF); // Prefer this for speed. It is exact.
		
		// TODO: This can probably be steamlined with arrays. Sort out Path (Move) container first.
		paths[0][0] = new Movement(Movement.MovementType.ARC, false, startArcF, turnRadius1);
		paths[0][1] = new Movement(Movement.MovementType.TRAVEL, p2p3, 0, false);
		paths[0][2] = new Movement(Movement.MovementType.ARC, false, targetArcF, turnRadius2);
		
		paths[1][0] = new Movement(Movement.MovementType.ARC, false, startArcF, turnRadius1);
		paths[1][1] = new Movement(Movement.MovementType.TRAVEL, p2p3, 0, false);
		paths[1][2] = new Movement(Movement.MovementType.ARC, false, targetArcB, turnRadius2);
		
		paths[2][0] = new Movement(Movement.MovementType.ARC, false, startArcB, turnRadius1);
		paths[2][1] = new Movement(Movement.MovementType.TRAVEL, p2p3, 0, false);
		paths[2][2] = new Movement(Movement.MovementType.ARC, false, targetArcF, turnRadius2);
		
		paths[3][0] = new Movement(Movement.MovementType.ARC, false, startArcB, turnRadius1);
		paths[3][1] = new Movement(Movement.MovementType.TRAVEL, p2p3, 0, false);
		paths[3][2] = new Movement(Movement.MovementType.ARC, false, targetArcB, turnRadius2);
		
		return paths;
	}
	
	// TODO: Terminology: Waypoints? Paths? Routes?  
	/**
	 * If the destination point is within the turning circle, the moves generated by that circle
	 * will all have Float.NaN for the distanceTraveled and arcAngle values.
	 *  
	 */
	public static Movement [][] getAvailablePaths(Pose start, Point destination, float r) {
		
		// TODO: These variables can perhaps be calculated based on existing parameters?
		final int PATHS = 4; // Currently doesn't calculate Pilot.backward() movement along P2 to P3
		final int MOVES_PER_PATH = 2;
				
		Movement [] [] paths = new Movement [PATHS] [MOVES_PER_PATH];
		
		// TODO: Use Point instead of Point2D.Float? 
		Point2D.Float p1 = new Point2D.Float(start.getX(), start.getY());
		// the Point destination below should really return float, not double. Not sure why Laurie returns a double.
		Point2D.Float p3 = new Point2D.Float((float)destination.getX(), (float)destination.getY());
		
		for(int i = 0;i<PATHS;i++) { 
			float radius = r;
			// TODO: Wrong. This should only get the paths with the sign of the radius parameter.
			// getBestPath needs to call it twice in order to get the negative version, then amalgamate the arrays.
			
			if(i>=PATHS/2) radius = -r; // Do calculations for +ve radius then -ve radius
			
			// Find two arc angles:
			Point2D.Float c = ArcAlgorithms.findCircleCenter(p1, radius, start.getHeading());
			Point2D.Float p2 = ArcAlgorithms.findP2(c, p3, radius);
			float arcLengthForward = ArcAlgorithms.getArc(p1, p2, radius, start.getHeading(), true);
			//double arcLengthBackward = ArcAlgorithms.getArc(p1, p2, radius, start.getHeading(), false);
			float arcLengthBackward = ArcAlgorithms.getArcBackward(arcLengthForward); // faster
			
			// Find straight line:
			double z = ArcAlgorithms.distBetweenPoints(c, p3);
			double p2p3 = ArcAlgorithms.distP2toP3(radius, z);
			
			paths[i][0] = new Movement(Movement.MovementType.ARC, false, (float)arcLengthForward, radius);
			paths[i][1] = new Movement(Movement.MovementType.TRAVEL, (float)p2p3, 0, false);
			i++;
			paths[i][0] = new Movement(Movement.MovementType.ARC, false, (float)arcLengthBackward, radius);
			paths[i][1] = new Movement(Movement.MovementType.TRAVEL, (float)p2p3, 0, false);
		}

		return paths;
	}
	
	/**
	 * 
	 * @param start
	 * @param destination
	 * @param radius
	 * @return
	 */
	public static Movement [] getBestPath(Pose start, Point destination, float radius) {
		// Get all paths
		Movement [][] paths = getAvailablePaths(start, destination, radius);
		return getBestPath(paths);
	}

	/**
	 * This method takes an array of paths (an array of Movement) and selects the shortest path.
	 * @param paths Any number of paths.
	 * @return The shortest path.
	 */
	public static Movement [] getBestPath(Movement [][] paths) {
		Movement [] bestPath = null;
		
		// Now see which one has shortest travel distance:
		float minDistance = Float.POSITIVE_INFINITY;
		for(int i=0;i<paths.length;i++) {
			float dist = 0;
			for(int j=0;j<paths[i].length;j++) {
				dist += Math.abs(paths[i][j].getDistanceTraveled());
			}
			if(dist < minDistance) {
				minDistance = dist;
				bestPath = paths[i];
			}
		}
		
		return bestPath;
	}
	
	public static Point2D.Float findPointOnHeading(Point2D.Float original, float heading, float distance) {
	
		// TODO: Do calculation to set theta angle according to "quadrant" of destination point? Probably not needed.
		double head = heading - 180;
		double pax = original.x - distance * Math.cos(Math.toRadians(head));
		double pay = original.y - distance * Math.sin(Math.toRadians(head));
		Point2D.Float Pa = new Point2D.Float((float)pax, (float)pay);
		return Pa;
	}
	
	// TODO: Reorder triangle order so middle variable is apex of angle.
	public static float getTriangleAngle(Point2D.Float p1, Point2D.Float p2, Point2D.Float pa) {
		// Now calculate lengths of all lines on our P1-Pa-P2 triangle
		double a = distBetweenPoints(p1, p2);
		//System.out.println("a: " + a);
		double b = distBetweenPoints(p1, pa);
		//System.out.println("b: " + b);
		double c = distBetweenPoints(pa, p2);
		//System.out.println("c: " + c);
		
		double angle = Math.pow(c, 2) - Math.pow(a, 2) - Math.pow(b, 2);
		//System.out.println("tri angle: " + angle);
		angle = angle / (-2 * a * b);
		//System.out.println("tri angle (after div): " + angle);
		// TODO: At this point if angle is outside -1 to +1 then Math.acos() causes NaN. I think artifact decimal numbers
		// were causing the number to be >1 when it should not have been. This is a kludge to fix that:
		if(angle < -1 & angle > -1.1) angle = -1;
		if(angle > 1 & angle < 1.1) angle = 1;
		
		angle = Math.acos(angle);
		//System.out.println("tri angle (after acos): " + angle);
		return (float)Math.toDegrees(angle);
	}
		
	public static float getHeading(float oldHeading, float changeInHeading) {
		float heading = oldHeading + changeInHeading;
		if(heading >=360) heading -= 360;
		if(heading <0) heading += 360;
		return heading;
	}
	
	/**
	 * Calculates the angle to travel along the circle to get from p1 to p2.
	 * @param p1 Start position
	 * @param p2 Take-off point on circle
	 * @param radius Radius of circle
	 * @param heading Start heading vehicle is pointed, in degrees.
	 * @param forward Will the vehicle be moving forward along the circle arc?
	 * @return Length of travel along circle, in degrees
	 * 
	 */
	public static float getArc(Point2D.Float p1, Point2D.Float p2, float radius, float heading, boolean forward) {
		// I accidently got the radius sign confused. +ve radius is supposed to have circle center to left of robot:
		radius = -radius; // Kludge. Should really correct my equations.
		
		Point2D.Float pa = ArcAlgorithms.findPointOnHeading(p1, heading, radius*2);
		//System.out.println("pa: " + pa);
		float arcLength = ArcAlgorithms.getTriangleAngle(p1, p2, pa);
		arcLength *= -2;
		//System.out.println("arclength: " + arcLength);
		// TODO: Bit of a hack here. Math should be able to do it without if-branches
		if(radius < 0) arcLength = 360 + arcLength;
		if(!forward) {
			// TODO: This 'if' could really be amalgamated with the if(radius < 0) branch 
			if(arcLength < 0)
				arcLength = arcLength + 360;
			else
				arcLength = arcLength - 360;
		}
		//if(radius < 0) return -arcLength; // TODO: Does this fix bug for calculating startArc?
		return arcLength;
	}
	
	/**
	 * Quick calculation of reverse arc instead of going through getArcLength() math again.
	 * @param forwardArc
	 * @return
	 */
	public static float getArcBackward(float forwardArc) {
		float backwardArc = 0;
		
		if(forwardArc < 0)
			backwardArc = 360 + forwardArc;
		else if(forwardArc > 0)
			backwardArc = -360 + forwardArc;
		
		return backwardArc;
	}
	
		
	/**
	 * REDUNDANT - this method is no longer used
	 * Calculates the angle to travel along the circle to get from p1 to p2.
	 * @param p1 Start position
	 * @param p2 Take-off point on circle
	 * @param radius Radius of circle
	 * @return Length of travel along circle, in degrees
	 */
	public static double getArcOld(Point2D.Float p1, Point2D.Float p2, double radius) {
		// I accidently got the radius sign confused. +ve radius is supposed to have circle center to left of robot:
		radius = -radius; // Kludge. Should really correct my equations.
		
		// This equation can't generate angles >180 (the major angle), so if angle is actually >180 it will
		// generate the minor angle rather than the major angle.
		double d = distBetweenPoints(p1, p2);
		
		// The - in front of 2 below is a temp hack. Won't work for reverse movements by robot. 
		double angle = -2 * Math.asin(d / (2 * radius));
		return Math.toDegrees(angle);
	}
	
	/**
	 * TODO: This method should be made private when algorithm migrated from CoordinatesGUI.
	 * 
	 * @param radius
	 * @param z
	 * @return
	 */
	public static double distP2toP3(double radius, double z) {
		double x = Math.pow(z, 2) - Math.pow(radius, 2);
		return Math.sqrt(x);
	}
	
	public static float distBetweenPoints(Point2D.Float a, Point2D.Float b) {
		double z = Math.pow((b.x - a.x), 2) + Math.pow((b.y - a.y), 2);
		return (float)Math.sqrt(z);
	}
	
	/**
	 * Calculates the heading designated by two points.
	 * @param from Starting point.
	 * @param to Final point.
	 * @return Heading in degrees (0-360) 
	 */
	public static float getHeading(Point2D.Float from, Point2D.Float to) {
		Point2D.Float xAxis = new Point2D.Float(from.x + 30, from.y);
		float heading = ArcAlgorithms.getTriangleAngle(from, to, xAxis);
		if(to.y < from.y) heading = 360 - heading;
		return heading;
	}
	
	/**
	 * This method finds P2 if the vehicle is traveling to a point (with no heading). To find P2
	 * when heading matters, use a different method... TODO
	 * @param c
	 * @param p3
	 * @param radius
	 * @return
	 */
	public static Point2D.Float findP2(Point2D.Float c, Point2D.Float p3, float radius) {
		// I accidently got the radius sign confused. +ve radius is supposed to have circle center to left of robot:
		radius = -radius; // Kludge. Should really correct my equations.
				
		double z = distBetweenPoints(c, p3);
		double a1 = p3.x - c.x;
		double o = p3.y - c.y;
		double angle = Math.atan2(o , a1) - Math.asin(radius / z);
		//System.out.println("findP2 angle: " + Math.toDegrees(angle));
		
		double x = distP2toP3(radius, z);
		double a2 = x * Math.cos(angle);
		//System.out.println("a2: " + a2);
		double o1 = x * Math.sin(angle);
		//System.out.println("o1: " + o1);
		
		double x2 = p3.x - a2;
		double y2 = p3.y - o1;
		
		return new Point2D.Float((float)x2, (float)y2);
	}
	
	/**
	 * Calculates the center of a circle that rests on the tangent of the vehicle's starting heading. 
	 * It can calculate a circle to the right OR left of the heading tangent.
	 * To calculate a circle on the left side of the heading tangent, feed it a negative radius.
	 * @param x Starting x coordinate of vehicle.
	 * @param y Starting y coordinate of vehicle.
	 * @param radius Turning radius of vehicle. A negative value produces a circle to the left of the heading.
	 * @param heading Start heading of vehicle, in degrees (not radians).
	 * @return
	 */
	public static Point2D.Float findCircleCenter(Point2D.Float p1, float radius, float heading) {
		// I accidently got the radius sign confused. +ve radius is supposed to have circle center to left of robot:
		radius = -radius; // Kludge. Should really correct my equations.
				
		double t = heading - 90; // TODO: Need to check if > 360 or < 0? Think cos/sin handle it.
		
		double a = p1.x + radius * Math.cos(Math.toRadians(t));
		double b = p1.y + radius * Math.sin(Math.toRadians(t));
		
		return new Point2D.Float((float)a,(float)b);
	}
	
	public static Point2D.Float findP3(Point2D.Double startCircle, float rs, Point2D.Double targetCircle, float rt) {
		Point2D.Float p3 = null;
		
		// TODO
		
		return p3;
	}

}
