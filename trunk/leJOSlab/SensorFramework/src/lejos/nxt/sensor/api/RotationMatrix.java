package lejos.nxt.sensor.api;

/**
 * The RotationMatrix class holds a rotation Matrix, aka Direction Cosine Matrix.<p>
 * @author Aswin
 *
 */
public class RotationMatrix extends Matrix3f {
	private int orthoFrequency=100;
	private int count=0;
	public RotationMatrix() {
		super();
		setIdentity();
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
		addRotation(axis,angle, false);
	}
	
	public void addRotation(String axis, int angle, boolean inverse) {
		axis = axis.toUpperCase();
		double a=Math.toRadians(angle);
		if ("XYZ".indexOf(axis) == -1)
			throw new IllegalArgumentException("Invalid axis");
		if (axis.equals("X")) addXRotation(a, inverse);
		if (axis.equals("Y")) addYRotation(a, inverse);
		if (axis.equals("Z")) addZRotation(a, inverse);
	}

	/**
	 * Restores the orthogonality of the matrix. 
	 */
	public void orthoNormalize() {
		Vector3f c1Old=new Vector3f(), c2Old=new Vector3f(), c3Old=new Vector3f();
		Vector3f c1New=new Vector3f(), c2New=new Vector3f(), c3New=new Vector3f();
		getColumn(0,c1Old);
		getColumn(1,c2Old);
		getColumn(2,c3Old);
		
		c1New.cross(c2Old,c3Old);
		c2New.cross(c3Old,c1Old);
		
		c1New.normalize();
		c2New.normalize();
		c3New.normalize(c3Old);
		
		setColumn(0,c1New);
		setColumn(1,c2New);
		setColumn(2,c3New);
		
		count=0;
	}
	
	
	
	private void addXRotation(double a, boolean inverse) {
		Matrix3f r=new Matrix3f(1,			0,			0,
														0,			cos(a),	sin(a),
														0,			-sin(a),	cos(a));
		if (inverse) r.transpose();
		this.mul(r);
		if (++count==orthoFrequency) orthoNormalize();
	}
	
	private void addYRotation(double a, boolean inverse) {
		Matrix3f r=new Matrix3f(cos(a),	0,			-sin(a),
														0,			1,			0,
														sin(a),	0,			cos(a));
		if (inverse) r.transpose();
		this.mul(r);
		if (++count==orthoFrequency) orthoNormalize();
	}

	private void addZRotation(double a, boolean inverse) {
		Matrix3f r=new Matrix3f(cos(a),	sin(a),	0,	
														-sin(a),cos(a),	0,
														0,			0,			1);
		if (inverse) r.transpose();
		this.mul(r);
		if (++count==orthoFrequency) orthoNormalize();
	}
	
	RotationMatrix movement=null;
	/**
	 * Adds a very small rotation to the matrix. Used for periodical updates.
	 * @param rotate
	 */
	public void addSmallRotation(Vector3f rotate) {
		if (movement==null)
			movement=new RotationMatrix();
		movement.m01= -rotate.z;
		movement.m02= rotate.y;
		movement.m10= rotate.z;
		movement.m12= -rotate.x;
		movement.m20= -rotate.y;
		movement.m21= rotate.x;
		this.mul(movement);
		if (++count==orthoFrequency) orthoNormalize();
	}
	
	public void invert() {
		transpose();
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

}
