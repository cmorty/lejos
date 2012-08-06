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
	Matrix3f rotateAxis=new Matrix3f(1,0,0,0,1,0,0,0,1);
	Vector3f v=new Vector3f();
	float[] sample=new float[3];


	public Align(SampleProvider source) {
		super(source);
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
		axis = axis.toUpperCase();
		double a=Math.toRadians(angle);
		if ("XYZ".indexOf(axis) == -1)
			throw new IllegalArgumentException("Invalid axis");
		if (axis.equals("X")) addXRotation(a);
		if (axis.equals("Y")) addYRotation(a);
		if (axis.equals("Z")) addZRotation(a);
	}


	public int getElementsCount() {
		return 3;
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
		rotateAxis=new Matrix3f(1,0,0,0,1,0,0,0,1);	
	}
		
	private void addXRotation(double a) {
		Matrix3f r=new Matrix3f(1,			0,			0,
														0,			cos(a),	sin(a),
														0,			-sin(a),	cos(a));
		r.mul(rotateAxis);
		rotateAxis=r;
	}
	
	private void addYRotation(double a) {
		Matrix3f r=new Matrix3f(cos(a),	0,			-sin(a),
														0,			1,			0,
														sin(a),	0,			cos(a));
		r.mul(rotateAxis);
		rotateAxis=r;
	}

	private void addZRotation(double a) {
		Matrix3f r=new Matrix3f(cos(a),	sin(a),	0,	
														-sin(a),cos(a),	0,
														0,			0,			1);
		r.mul(rotateAxis);
		rotateAxis=r;
	}
	
	
	/**
	 * Returns sinus, Just to get more readable code
	 * @param angle (in radians)
	 * @return
	 * sinus of angle
	 */
	private float sin(double angle) {
		return (float)Math.sin(angle);
	}
	
	/**
	 * Returns cosinus, Just to get more readable code
	 * @param angle (in radians)
	 * @return
	 * cosinus of angle
	 */
	private float cos(double angle) {
		return (float)Math.cos(angle);
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
