package lejos.robotics.proposal;

import java.awt.geom.*;

/**
 * 
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
class SteerAlgorithms {
	
	public static Point2D.Double findPointOnHeading(Point2D.Double original, double heading, double distance) {
	
		// TODO: Do calculation to set theta angle according to "quadrant" of destination point? Probably not needed.
		double head = heading - 180;
		double pax = original.x - distance * Math.cos(Math.toRadians(head));
		double pay = original.y - distance * Math.sin(Math.toRadians(head));
		Point2D.Double Pa = new Point2D.Double(pax,pay);
		return Pa;
	}
	
	public static double getTriangleAngle(Point2D.Double p1, Point2D.Double p2, Point2D.Double pa) {
		// Now calculate lengths of all lines on our P1-Pa-P2 triangle
		double a = Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
		double b = Math.sqrt(Math.pow(pa.x - p1.x, 2) + Math.pow(pa.y - p1.y, 2));
		double c = Math.sqrt(Math.pow(p2.x - pa.x, 2) + Math.pow(p2.y - pa.y, 2));
		
		double angle = Math.pow(c, 2) - Math.pow(a, 2) - Math.pow(b, 2);
		angle = angle / (-2 * a * b);
		angle = Math.acos(angle);
		return Math.toDegrees(angle);
	}
			
	public static double getHeading(double oldHeading, double changeInHeading) {
		double heading = oldHeading + changeInHeading;
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
	public static double getArcLength(Point2D.Double p1, Point2D.Double p2, double radius, double heading, boolean forward) {
		// I accidently got the radius sign confused. +ve radius is supposed to have circle center to left of robot:
		radius = -radius; // Kludge. Should really correct my equations.
		
		Point2D.Double pa = SteerAlgorithms.findPointOnHeading(p1, heading, radius*2);
		double arcLength = SteerAlgorithms.getTriangleAngle(p1, p2, pa);
		arcLength *= -2;
		// TODO: Bit of a hack here. Math should be able to do it without if-branches
		if(radius < 0) arcLength = 360 + arcLength;
		if(!forward) {
			// TODO: This 'if' could really be amalgamated with the if(radius < 0) branch 
			if(arcLength < 0)
				arcLength = arcLength + 360;
			else
				arcLength = arcLength - 360;
		}
		return arcLength;
	}
	
	/**
	 * REDUNDANT - this method is no longer used
	 * Calculates the angle to travel along the circle to get from p1 to p2.
	 * @param p1 Start position
	 * @param p2 Take-off point on circle
	 * @param radius Radius of circle
	 * @return Length of travel along circle, in degrees
	 */
	public static double getArcLengthOld(Point2D.Double p1, Point2D.Double p2, double radius) {
		// I accidently got the radius sign confused. +ve radius is supposed to have circle center to left of robot:
		radius = -radius; // Kludge. Should really correct my equations.
		
		// This equation can't generate angles >180 (the major angle), so if angle is actually >180 it will
		// generate the minor angle rather than the major angle.
		double d = Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2);
		d = Math.sqrt(d);
		
		// The - in front of 2 below is a temp hack. Won't work for reverse movements by robot. 
		double angle = -2 * Math.asin(d / (2 * radius));
		return Math.toDegrees(angle);
	}
	
	/**
	 * 
	 * @param radius
	 * @param z
	 * @return
	 */
	public static double distP2toP3(double radius, double z) {
		// I accidently got the radius sign confused. +ve radius is supposed to have circle center to left of robot:
		radius = -radius; // Kludge. Should really correct my equations.
		
		double x = Math.pow(z, 2) - Math.pow(radius, 2);
		return Math.sqrt(x);
	}
	
	public static double distCtoP3(Point2D.Double c, Point2D.Double p3) {
		double z = Math.pow((p3.x - c.x), 2) + Math.pow((p3.y - c.y), 2);
		z = Math.sqrt(z);
		return z;
	}
	
	public static Point2D.Double findP2(Point2D.Double c, Point2D.Double p3, double radius) {
		// I accidently got the radius sign confused. +ve radius is supposed to have circle center to left of robot:
		radius = -radius; // Kludge. Should really correct my equations.
				
		double z = distCtoP3(c, p3);
		double a1 = p3.x - c.x;
		double o = p3.y - c.y;
		double angle = Math.atan2(o , a1) - Math.asin(radius / z);
		//System.out.println("angle: " + Math.toDegrees(angle));
		
		double x = distP2toP3(radius, z);
		double a2 = x * Math.cos(angle);
		//System.out.println("a2: " + a2);
		double o1 = x * Math.sin(angle);
		//System.out.println("o1: " + o1);
		
		double x2 = p3.x - a2;
		double y2 = p3.y - o1;
		
		return new Point2D.Double(x2, y2);
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
	public static Point2D.Double findCircleCenter(Point2D.Double p1, double radius, double heading) {
		// I accidently got the radius sign confused. +ve radius is supposed to have circle center to left of robot:
		radius = -radius; // Kludge. Should really correct my equations.
				
		double t = heading - 90; // TODO: Need to check if > 360 or < 0? Think cos/sin handle it.
		
		double a = p1.x + radius * Math.cos(Math.toRadians(t));
		double b = p1.y + radius * Math.sin(Math.toRadians(t));
		
		return new Point2D.Double(a,b);
	}
}
