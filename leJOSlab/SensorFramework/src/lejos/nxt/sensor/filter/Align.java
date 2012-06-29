/**
 * 
 */
package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;

/**
 * This filter rotates a point in space around the origin.
 * Used to translate from one (sensor) system of axis, to another (robot) system of axis.
 * Helpful for misaligned sensors or sensors that can rotate.
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

  	rotateAxis.m00=(float) (Math.cos(theta)*Math.cos(psi));
  	rotateAxis.m01=(float) (Math.sin(phi)*Math.sin(theta)*Math.cos(psi)-Math.cos(phi)*Math.sin(psi));
  	rotateAxis.m02=(float) (Math.cos(phi)*Math.sin(theta)*Math.cos(psi)+Math.sin(phi)*Math.sin(psi));
  	rotateAxis.m10=(float) (Math.cos(theta)*Math.sin(psi));
  	rotateAxis.m11=(float) (Math.sin(phi)*Math.sin(theta)*Math.cos(psi)+Math.cos(phi)*Math.cos(psi));
  	rotateAxis.m12=(float) (Math.cos(phi)*Math.sin(theta)*Math.sin(psi)+Math.sin(phi)*Math.cos(psi));
  	rotateAxis.m20=(float) (-Math.sin(theta));
  	rotateAxis.m21=(float) (Math.sin(phi)*Math.cos(theta));
  	rotateAxis.m22=(float) (Math.cos(phi)*Math.cos(theta));
  }

}
