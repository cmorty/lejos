package lejos.nxt.sensor.api;


/**
 * A 3-element vector that is represented by single-precision floating point 
 * x,y,z coordinates. <p>
 * This class is a placeholder for the javax.vecmath.Vector3f class. 
 * The vecmath package is not included (yet) in Lejos. But Lejos could benefit from it when dealing
 * with data from triaxis sensors.
 *
 */
public class Vector3f {
	public float x=0,y=0,z=0;
	
	public Vector3f() {
	}
	
	public Vector3f(float x, float y, float z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}


	public void set(float x, float y, float z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}

	public void scale(float multiplier) {
		this.x*=multiplier;
		this.y*=multiplier;
		this.z*=multiplier;
	}
	
  /**
   * Returns the squared length of this vector.
   * @return the squared length of this vector
   */
  public final float lengthSquared()
  {
      return (this.x*this.x + this.y*this.y + this.z*this.z);
  }

  /**
   * Returns the length of this vector.
   * @return the length of this vector
   */
  public final float length()
  {
      return (float)
           Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
  }


/**
   * Sets this vector to be the vector cross product of vectors v1 and v2.
   * @param v1 the first vector
   * @param v2 the second vector
   */
  public final void cross(Vector3f v1, Vector3f v2)
  {
      float x,y;

      x = v1.y*v2.z - v1.z*v2.y;
      y = v2.x*v1.z - v2.z*v1.x;
      this.z = v1.x*v2.y - v1.y*v2.x;
      this.x = x;
      this.y = y;
  }

/**
 * Computes the dot product of this vector and vector v1.
 * @param v1 the other vector
 * @return the dot product of this vector and v1
 */
public final float dot(Vector3f v1)
  {
    return (this.x*v1.x + this.y*v1.y + this.z*v1.z);
  }

 /**
   * Sets the value of this vector to the normalization of vector v1.
   * @param v1 the un-normalized vector
   */
  public final void normalize(Vector3f v1)
  {
      float norm;

      norm = (float) (1.0/Math.sqrt(v1.x*v1.x + v1.y*v1.y + v1.z*v1.z));
      this.x = v1.x*norm;
      this.y = v1.y*norm;
      this.z = v1.z*norm;
  }

  /**
   * Normalizes this vector in place.
   */
  public final void normalize()
  {
      float norm;

      norm = (float)
             (1.0/Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z));
      this.x *= norm;
      this.y *= norm;
      this.z *= norm;
  }


/** 
  *   Returns the angle in radians between this vector and the vector
  *   parameter; the return value is constrained to the range [0,PI]. 
  *   @param v1    the other vector 
  *   @return   the angle in radians in the range [0,PI] 
  */   
 public final float angle(Vector3f v1) 
 { 
    double vDot = this.dot(v1) / ( this.length()*v1.length() );
    if( vDot < -1.0) vDot = -1.0;
    if( vDot >  1.0) vDot =  1.0;
    return((float) (Math.acos( vDot )));
 } 

}
