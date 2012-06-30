/**
 * 
 */
package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;

/**
 * This filter rotates a point in space around the origin.
 * Used to translate from one (sensor) system of axis, to another (robot) system of axis.
 * Helpful for misaligned sensors or sensors that can rotate. <br>
 * 
 * Class still needs proper testing and debugging
 * @author Aswin
 *
 */
public class Align implements SensorVectorDataProvider {
	Matrix3f rotateAxis=new Matrix3f(1,0,0,0,1,0,0,0,1);
	private SensorVectorDataProvider	source;
	
	
	public Align(SensorVectorDataProvider source) {
		this.source=source;
	}

	public int getMinimumFetchInterval() {
		return source.getMinimumFetchInterval();
	}


	public void fetchData(Vector3f data) {
		source.fetchData(data);
		rotateAxis.transform(data);
	}
	
	/**
	 * specifies the orientation of the sensor in respect to the robot (in degrees).

	 * @param aroundX
	 * The first rotation, around the x-axis of the sensor
	 * @param aroundY
	 * The second rotation, around the y-axis of the sensor
	 * @param aroundZ
	 * The third rotation, around the z-axis of the sensor
	 */
	public void setSensorOrientation(int aroundX, int aroundY, int aroundZ) {
  	double theta=Math.toRadians(aroundX);
  	double phi=Math.toRadians(aroundY);
  	double psi=Math.toRadians(aroundZ);

  	rotateAxis.m00= (cos(theta)*cos(psi));
  	rotateAxis.m01= (sin(phi)*sin(theta)*cos(psi)-cos(phi)*sin(psi));
  	rotateAxis.m02= (cos(phi)*sin(theta)*cos(psi)+sin(phi)*sin(psi));
  	rotateAxis.m10= (cos(theta)*sin(psi));
  	rotateAxis.m11= (sin(phi)*sin(theta)*cos(psi)+cos(phi)*cos(psi));
  	rotateAxis.m12= (cos(phi)*sin(theta)*sin(psi)+sin(phi)*cos(psi));
  	rotateAxis.m20= (-sin(theta));
  	rotateAxis.m21= (sin(phi)*cos(theta));
  	rotateAxis.m22= (cos(phi)*cos(theta));
  }
	
	/**
	 * Adds a rotation the the rotation matrix. <p>
	 * The alignment of a sensor can be regarded as a series of rotations of the sensor where the sensor starts of aligned with the robot.
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
