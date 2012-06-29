package lejos.nxt.sensor.api;



public class Matrix3f {
  /** 
    * The first matrix element in the first row.
    */
    public	float	m00;

  /** 
    * The second matrix element in the first row.
    */
    public	float	m01;

  /** 
    * The third matrix element in the first row.
    */
    public	float	m02;

  /** 
    * The first matrix element in the second row.
    */
    public	float	m10;

  /** 
    * The second matrix element in the second row.
    */
    public	float	m11;

  /** 
    * The third matrix element in the second row.
    */
    public	float	m12;

  /** 
    * The first matrix element in the third row.
    */
    public	float	m20;

  /** 
    * The second matrix element in the third row.
    */
    public	float	m21;

  /** 
    * The third matrix element in the third row.
    */
    public	float	m22;

  

    /**
     * Constructs and initializes a Matrix3f from the specified nine values.
     * @param m00 the [0][0] element
     * @param m01 the [0][1] element
     * @param m02 the [0][2] element
     * @param m10 the [1][0] element
     * @param m11 the [1][1] element
     * @param m12 the [1][2] element
     * @param m20 the [2][0] element
     * @param m21 the [2][1] element
     * @param m22 the [2][2] element
     */
    public Matrix3f(float m00, float m01, float m02,
		    float m10, float m11, float m12,
		    float m20, float m21, float m22)
    {
	this.m00 = m00;
	this.m01 = m01;
	this.m02 = m02;

	this.m10 = m10;
	this.m11 = m11;
	this.m12 = m12;

	this.m20 = m20;
	this.m21 = m21;
	this.m22 = m22;

    }

    /**
     * Constructs and initializes a Matrix3f from the specified 
     * nine-element array.   this.m00 =v[0], this.m01=v[1], etc.
     * @param v the array of length 9 containing in order
     */
    public Matrix3f(float[] v)
    {
	this.m00 = v[ 0];
	this.m01 = v[ 1];
	this.m02 = v[ 2];

	this.m10 = v[ 3];
	this.m11 = v[ 4];
	this.m12 = v[ 5];

	this.m20 = v[ 6];
	this.m21 = v[ 7];
	this.m22 = v[ 8];

    }


 
 


    /**
     * Constructs and initializes a Matrix3f to all zeros.
     */
    public Matrix3f()
    {
	this.m00 = (float) 0.0;
	this.m01 = (float) 0.0;
	this.m02 = (float) 0.0;

	this.m10 = (float) 0.0;
	this.m11 = (float) 0.0;
	this.m12 = (float) 0.0;

	this.m20 = (float) 0.0;
	this.m21 = (float) 0.0;
	this.m22 = (float) 0.0;

    }

 
 

    /**
     * Multiplies each element of this matrix by a scalar.
     * @param scalar  the scalar multiplier
     */
    public final void mul(float scalar)
    {
       m00 *= scalar;
       m01 *= scalar;
       m02 *= scalar;

       m10 *= scalar;
       m11 *= scalar;
       m12 *= scalar;

       m20 *= scalar;
       m21 *= scalar;
       m22 *= scalar;
    }

   /**
     * Sets the value of this matrix to the result of multiplying itself
     * with matrix m1.
     * @param m1 the other matrix
     */  
    public final void mul(Matrix3f m1)
    {
          float       m00, m01, m02,
                      m10, m11, m12,
                      m20, m21, m22;

            m00 = this.m00*m1.m00 + this.m01*m1.m10 + this.m02*m1.m20;
            m01 = this.m00*m1.m01 + this.m01*m1.m11 + this.m02*m1.m21;
            m02 = this.m00*m1.m02 + this.m01*m1.m12 + this.m02*m1.m22;
 
            m10 = this.m10*m1.m00 + this.m11*m1.m10 + this.m12*m1.m20;
            m11 = this.m10*m1.m01 + this.m11*m1.m11 + this.m12*m1.m21;
            m12 = this.m10*m1.m02 + this.m11*m1.m12 + this.m12*m1.m22;
 
            m20 = this.m20*m1.m00 + this.m21*m1.m10 + this.m22*m1.m20;
            m21 = this.m20*m1.m01 + this.m21*m1.m11 + this.m22*m1.m21;
            m22 = this.m20*m1.m02 + this.m21*m1.m12 + this.m22*m1.m22;
 
            this.m00 = m00; this.m01 = m01; this.m02 = m02;
            this.m10 = m10; this.m11 = m11; this.m12 = m12;
            this.m20 = m20; this.m21 = m21; this.m22 = m22;
    }

    /**
    * Multiply this matrix by the tuple t and place the result
    * back into the tuple (t = this*t).
    * @param t  the tuple to be multiplied by this matrix and then replaced
    */
    public final void transform(Vector3f t) {
     float x,y,z;
     x = m00* t.x + m01*t.y + m02*t.z; 
     y = m10* t.x + m11*t.y + m12*t.z; 
     z = m20* t.x + m21*t.y + m22*t.z; 
     t.set(x,y,z);
    }



}
