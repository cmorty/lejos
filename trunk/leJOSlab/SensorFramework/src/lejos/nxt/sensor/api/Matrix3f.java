package lejos.nxt.sensor.api;





public class Matrix3f {
	/**
	 * The first matrix element in the first row.
	 */
	public float	m00;

	/**
	 * The second matrix element in the first row.
	 */
	public float	m01;

	/**
	 * The third matrix element in the first row.
	 */
	public float	m02;

	/**
	 * The first matrix element in the second row.
	 */
	public float	m10;

	/**
	 * The second matrix element in the second row.
	 */
	public float	m11;

	/**
	 * The third matrix element in the second row.
	 */
	public float	m12;

	/**
	 * The first matrix element in the third row.
	 */
	public float	m20;

	/**
	 * The second matrix element in the third row.
	 */
	public float	m21;

	/**
	 * The third matrix element in the third row.
	 */
	public float	m22;

	/**
	 * Constructs and initializes a Matrix3f from the specified nine values.
	 * 
	 * @param m00
	 *          the [0][0] element
	 * @param m01
	 *          the [0][1] element
	 * @param m02
	 *          the [0][2] element
	 * @param m10
	 *          the [1][0] element
	 * @param m11
	 *          the [1][1] element
	 * @param m12
	 *          the [1][2] element
	 * @param m20
	 *          the [2][0] element
	 * @param m21
	 *          the [2][1] element
	 * @param m22
	 *          the [2][2] element
	 */
	public Matrix3f(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
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
	 * Constructs and initializes a Matrix3f from the specified nine-element
	 * array. this.m00 =v[0], this.m01=v[1], etc.
	 * 
	 * @param v
	 *          the array of length 9 containing in order
	 */
	public Matrix3f(float[] v) {
		this.m00 = v[0];
		this.m01 = v[1];
		this.m02 = v[2];

		this.m10 = v[3];
		this.m11 = v[4];
		this.m12 = v[5];

		this.m20 = v[6];
		this.m21 = v[7];
		this.m22 = v[8];

	}

	/**
	 * Constructs and initializes a Matrix3f to all zeros.
	 */
	public Matrix3f() {
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
	 * 
	 * @param scalar
	 *          the scalar multiplier
	 */
	public final void mul(float scalar) {
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
	 * Sets the value of this matrix to the result of multiplying itself with
	 * matrix m1.
	 * 
	 * @param m1
	 *          the other matrix
	 */
	public final void mul(Matrix3f m1) {
		float m00, m01, m02, m10, m11, m12, m20, m21, m22;

		m00 = this.m00 * m1.m00 + this.m01 * m1.m10 + this.m02 * m1.m20;
		m01 = this.m00 * m1.m01 + this.m01 * m1.m11 + this.m02 * m1.m21;
		m02 = this.m00 * m1.m02 + this.m01 * m1.m12 + this.m02 * m1.m22;

		m10 = this.m10 * m1.m00 + this.m11 * m1.m10 + this.m12 * m1.m20;
		m11 = this.m10 * m1.m01 + this.m11 * m1.m11 + this.m12 * m1.m21;
		m12 = this.m10 * m1.m02 + this.m11 * m1.m12 + this.m12 * m1.m22;

		m20 = this.m20 * m1.m00 + this.m21 * m1.m10 + this.m22 * m1.m20;
		m21 = this.m20 * m1.m01 + this.m21 * m1.m11 + this.m22 * m1.m21;
		m22 = this.m20 * m1.m02 + this.m21 * m1.m12 + this.m22 * m1.m22;

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
	 * Multiply this matrix by the tuple t and place the result back into the
	 * tuple (t = this*t).
	 * 
	 * @param t
	 *          the tuple to be multiplied by this matrix and then replaced
	 */
	public final void transform(Vector3f t) {
		float x, y, z;
		x = m00 * t.x + m01 * t.y + m02 * t.z;
		y = m10 * t.x + m11 * t.y + m12 * t.z;
		z = m20 * t.x + m21 * t.y + m22 * t.z;
		t.set(x, y, z);
	}

	/**
	 * Multiply this matrix by the tuple t and place the result back into the
	 * tuple (t = this*t).
	 * 
	 * @param t
	 *          the tuple to be multiplied by this matrix and then replaced
	 */
	public final void transform(Vector3f t, Vector3f r) {
		float x, y, z;
		x = m00 * t.x + m01 * t.y + m02 * t.z;
		y = m10 * t.x + m11 * t.y + m12 * t.z;
		z = m20 * t.x + m21 * t.y + m22 * t.z;
		r.set(x, y, z);
	}

  /**
   * Sets the specified row of this matrix3f to the three values provided.
   * @param row the row number to be modified (zero indexed)
   * @param x the first column element
   * @param y the second column element
   * @param z the third column element
   */
  public final void setRow(int row, float x, float y, float z)
  {
switch (row) {
case 0:
    this.m00 = x;
    this.m01 = y;
    this.m02 = z;
    break;

case 1:
    this.m10 = x;
    this.m11 = y;
    this.m12 = z;
    break;

case 2:
    this.m20 = x;
    this.m21 = y;
    this.m22 = z;
    break;

default:
  throw new ArrayIndexOutOfBoundsException("Matrix3f");
}
  }

  /**
   * Sets the specified row of this matrix3f to the Vector provided.
   * @param row the row number to be modified (zero indexed)
   * @param v the replacement row
   */
  public final void setRow(int row, Vector3f v)
  {
switch (row) {
case 0:
    this.m00 = v.x;
    this.m01 = v.y;
    this.m02 = v.z;
    break;

case 1:
    this.m10 = v.x;
    this.m11 = v.y;
    this.m12 = v.z;
    break;

case 2:
    this.m20 = v.x;
    this.m21 = v.y;
    this.m22 = v.z;
    break;

default:
  throw new ArrayIndexOutOfBoundsException("Matrix3f");
}
  }

  /**
   * Sets the specified row of this matrix3f to the three values provided.
   * @param row the row number to be modified (zero indexed)
   * @param v the replacement row
   */
  public final void setRow(int row, float v[])
  {
switch (row) {
case 0:
    this.m00 = v[0];
    this.m01 = v[1];
    this.m02 = v[2];
    break;

case 1:
    this.m10 = v[0];
    this.m11 = v[1];
    this.m12 = v[2];
    break;

case 2:
    this.m20 = v[0];
    this.m21 = v[1];
    this.m22 = v[2];
    break;

default:
  throw new ArrayIndexOutOfBoundsException("Matrix3f");
}
  }

  /**
   * Sets the specified column of this matrix3f to the three values provided.
   * @param column the column number to be modified (zero indexed)
   * @param x the first row element
   * @param y the second row element
   * @param z the third row element
   */
  public final void setColumn(int column, float x, float y, float z)
  {
switch (column) {
case 0:
    this.m00 = x;
    this.m10 = y;
    this.m20 = z;
    break;

case 1:
    this.m01 = x;
    this.m11 = y;
    this.m21 = z;
    break;

case 2:
    this.m02 = x;
    this.m12 = y;
    this.m22 = z;
    break;

default:
  throw new ArrayIndexOutOfBoundsException("Matrix3f");
}
  }

  /**
   * Sets the specified column of this matrix3f to the vector provided.
   * @param column the column number to be modified (zero indexed)
   * @param v the replacement column
   */
  public final void setColumn(int column, Vector3f v)
  {
switch (column) {
case 0:
    this.m00 = v.x;
    this.m10 = v.y;
    this.m20 = v.z;
    break;

case 1:
    this.m01 = v.x;
    this.m11 = v.y;
    this.m21 = v.z;
    break;

case 2:
    this.m02 = v.x;
    this.m12 = v.y;
    this.m22 = v.z;
    break;

default:
  throw new ArrayIndexOutOfBoundsException("Matrix3f");
}
  }

  /**
   * Sets the specified column of this matrix3f to the three values provided.
   * @param column the column number to be modified (zero indexed)
   * @param v the replacement column
   */
  public final void setColumn(int column, float v[])
  {
switch (column) {
case 0:
    this.m00 = v[0];
    this.m10 = v[1];
    this.m20 = v[2];
    break;

case 1:
    this.m01 = v[0];
    this.m11 = v[1];
    this.m21 = v[2];
    break;

case 2:
    this.m02 = v[0];
    this.m12 = v[1];
    this.m22 = v[2];
    break;

default:
  throw new ArrayIndexOutOfBoundsException("Matrix3f");
}
  }

	/**
	 * Sets the value of this matrix to its transpose.
	 */
	public final void transpose() {
		float temp;

		temp = this.m10;
		this.m10 = this.m01;
		this.m01 = temp;

		temp = this.m20;
		this.m20 = this.m02;
		this.m02 = temp;

		temp = this.m21;
		this.m21 = this.m12;
		this.m12 = temp;
	}

	/**
	 * Sets the value of this matrix to the transpose of the argument matrix.
	 * 
	 * @param m1
	 *          the matrix to be transposed
	 */
	public final void transpose(Matrix3f m1) {
		if (this != m1) {
			this.m00 = m1.m00;
			this.m01 = m1.m10;
			this.m02 = m1.m20;

			this.m10 = m1.m01;
			this.m11 = m1.m11;
			this.m12 = m1.m21;

			this.m20 = m1.m02;
			this.m21 = m1.m12;
			this.m22 = m1.m22;
		}
		else
			this.transpose();
	}
  /**
   * Copies the matrix values in the specified row into the vector parameter. 
   * @param row  the matrix row
   * @param v    the vector into which the matrix row values will be copied
   */
  public final void getRow(int row, Vector3f v) {
       if( row == 0 ) {
         v.x = m00;
         v.y = m01;
         v.z = m02;
      } else if(row == 1) {
         v.x = m10;
         v.y = m11;
         v.z = m12;
      } else if(row == 2) {
         v.x = m20;
         v.y = m21;
         v.z = m22;
      } else {
        throw new ArrayIndexOutOfBoundsException("Matrix3f");
      }

  }

  /**
   * Copies the matrix values in the specified row into the array parameter. 
   * @param row  the matrix row
   * @param v    the array into which the matrix row values will be copied 
   */  
  public final void getRow(int row, float v[]) {
      if( row == 0 ) {
         v[0] = m00; 
         v[1] = m01;
         v[2] = m02;
      } else if(row == 1) {
         v[0] = m10;
         v[1] = m11;
         v[2] = m12;
      } else if(row == 2) {
         v[0] = m20;
         v[1] = m21;
         v[2] = m22;
      } else {
        throw new ArrayIndexOutOfBoundsException("Matrix3f");
      }

  }

  /**
   * Copies the matrix values in the specified column into the vector 
   * parameter.
   * @param column  the matrix column
   * @param v    the vector into which the matrix row values will be copied
   */  
  public final void getColumn(int column, Vector3f v) {
      if( column == 0 ) {
         v.x = m00;
         v.y = m10;
         v.z = m20;
      } else if(column == 1) {
         v.x = m01;
         v.y = m11;
         v.z = m21;
      }else if(column == 2){
         v.x = m02;
         v.y = m12;
         v.z = m22;
      } else {
        throw new ArrayIndexOutOfBoundsException("Matrix3f");
      }

  }

  /**  
   * Copies the matrix values in the specified column into the array 
   * parameter.
   * @param column  the matrix column
   * @param v    the array into which the matrix row values will be copied
   */  
  public final void getColumn(int column, float v[]) {
      if( column == 0 ) {
         v[0] = m00;
         v[1] = m10;
         v[2] = m20;
      } else if(column == 1) {
         v[0] = m01;
         v[1] = m11;
         v[2] = m21;
      }else if(column == 2) {
         v[0] = m02;
         v[1] = m12;
         v[2] = m22;
      }else {
        throw new ArrayIndexOutOfBoundsException("Matrix3f");
      }
  }

  
  /**
   * Sets this Matrix3f to identity.
   */
  public final void setIdentity()
  {
this.m00 = (float) 1.0;
this.m01 = (float) 0.0;
this.m02 = (float) 0.0;

this.m10 = (float) 0.0;
this.m11 = (float) 1.0;
this.m12 = (float) 0.0;

this.m20 = (float) 0.0;
this.m21 = (float) 0.0;
this.m22 = (float) 1.0;
  }

}
