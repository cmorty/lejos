package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;

/**
 * Rotates spatial sample data in 1,2, or 3 dimensions. <P>
 * This class can be used to convert samples taken in one coordinate frame (for example sensor frame)
 * to another coordinate frame (for example robot frame). The differences in orientation between the
 * two frames is specified as a serie of rotations using the addRotation() method.  
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
	 * The alignment of a sensor can be expressed as a series of rotations of the sensor where the sensor starts of aligned with the robot.
	 * Each rotation is a rotation around one of the sensor axis. The position of a sensor that points backwards in an upward angle of 45 degrees for example
	 * can be described with two subsequent rotations. The first rotation is 180 degrees around the Z-axis, the second rotation is 45 degrees around the Y-axis. <br>
	 * Please note that the order of rotations does matter. Also note that this class uses the right hand system for rotations.
	 * @param axis
	 * The axis that the sensor has rotated around: X, Y or Z.
	 * @param angle
	 * The angle of rotation expressed in degrees. A positive angle means a counter clockwise rotation.
	 * 
	 */
	public void addRotation(String axis, int angle) {
		rotateAxis.addRotation(axis, angle, true);
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
	 * Resets the to coordinate systems te be aligned.
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
