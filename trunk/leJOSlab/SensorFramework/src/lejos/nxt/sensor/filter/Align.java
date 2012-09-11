package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;

/**
 * Rotates spatial sample data in 1,2, or 3 dimensions. <P>
 * This class can be used to convert samples taken in one coordinate frame (for example sensor frame)
 * to another coordinate frame (for example robot frame). The difference in orientation between the
 * two frames is specified as a series of rotations that is needed to align the source (sensor) frame 
 * to the target (robot) frame using the addRotation() method.   <P>
 * 
 * For example. If a robot has an US sensor attached that is pointing to the left then the filter should be configured using
 * addRotation("Z",-45), as one needs to rotate the sensor clockwise around its Z axis with 45 degrees to align it with the robot.
 * As a result a measured range of 100cm will be translated in a range of {70.7, 70,7, 0}. 
 * This indicates that the object is 70.7 cm in front of the robot and 70.7 cm to the left of the robot.
 * @author Aswin
 *
 */
public class Align extends AbstractFilter{
	RotationMatrix rotateAxis=new RotationMatrix();
	Vector3f v=new Vector3f();
	float[] sample=new float[3];


	public Align(SampleProvider source) {
		super(source);
		elements=3;
	}
	
	/**
	 * Adds a rotation the the rotation matrix. <p>
	 * The alignment of a sensor can be expressed as a series of rotations of the sensor where the sensor starts of at its mounting position and 
	 * ends up aligned with the robot.
	 * Each rotation is a rotation around one of the sensor axis. <br>
	 * Please note that the order of rotations does matter. Also note that this class uses the right hand system for rotations.
	 * @param axis
	 * The axis that the sensor has rotated around: X, Y or Z.
	 * @param angle
	 * The angle of rotation expressed in degrees. A positive angle means a counter clockwise rotation when looking into the axis.
	 * 
	 */
	public void addRotation(String axis, int angle) {
		rotateAxis.addRotation(axis, angle, false);
	}



	/** Fetches a sample from the source and rotates it
	 */
	public void fetchSample(float[] dst, int off) {
		source.fetchSample(sample, 0);
		v.x=sample[0];
		v.y=sample[1];
		v.z=sample[2];
		rotateAxis.transform(v);
		dst[0+off]=v.x;
		dst[1+off]=v.y;
		dst[2+off]=v.z;
	}
	
	/**
	 * Reset aligns the source and target coordinate systems.
	 */
	public void reset() {
		rotateAxis.setIdentity();	
	}
		
	protected void printMatrix() {
		printMatrix(rotateAxis);
	}
	
	/**
	 * Used for debugging. Outputs the elements of a matrix
	 * @param m
	 */
	private void printMatrix (Matrix3f m) {
		System.out.println();
		System.out.println(fmt(m.m00) + " " + fmt(m.m01) + " " + fmt(m.m02));
		System.out.println(fmt(m.m10) + " " + fmt(m.m11) + " " + fmt(m.m12));
		System.out.println(fmt(m.m20) + " " + fmt(m.m21) + " " + fmt(m.m22));
	}
	


}
