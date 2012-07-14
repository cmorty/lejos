package skoehler.sensor.filter;

import lejos.nxt.sensor.api.Matrix3f;
import lejos.nxt.sensor.api.Vector3f;
import skoehler.sensor.api.VectorData;

public class Align extends AbstractFilter{
	Matrix3f rotateAxis=new Matrix3f(1,0,0,0,1,0,0,0,1);
	Vector3f v=new Vector3f();
	float[] sample=new float[3];


	public Align(VectorData source) {
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
		if ("XYZ".indexOf(axis) == -1)
			throw new IllegalArgumentException("Invalid axis");
		if (axis.equals("X")) addXRotation(Math.toRadians(angle));
		if (axis.equals("Y")) addYRotation(Math.toRadians(angle));
		if (axis.equals("Z")) addZRotation(Math.toRadians(angle));
	}

	@Override
	public int getAxisCount() {
		return 3;
	}

	@Override
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
	 * Resets the sensor to be aligned with the robot.
	 */
	public void reset() {
		rotateAxis=new Matrix3f(1,0,0,0,1,0,0,0,1);	
	}
		
	private void addXRotation(double a) {
		Matrix3f r=new Matrix3f(1,0,0,0,cos(a),-sin(a),0,sin(a),cos(a));
		r.mul(rotateAxis);
		rotateAxis=r;
	}
	
	private void addYRotation(double a) {
		Matrix3f r=new Matrix3f(cos(a),0,sin(a),0,1,0,-sin(a),0,cos(a));
		r.mul(rotateAxis);
		rotateAxis=r;
	}

	private void addZRotation(double a) {
		Matrix3f r=new Matrix3f(cos(a),-sin(a),0,sin(a),cos(a),0,0,0,1);
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
		return (float)Math.sin(angle);
	}

}
