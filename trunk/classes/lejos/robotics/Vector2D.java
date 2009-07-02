package lejos.robotics;


/**
 * This class implements a 2 dimensional mathematical vector, and the common
 * vector arithmetic operations.
* includes polar representation consistant with rectangular <br>
* as long as data is changed by method invocation.  <br>
* theta in degrees
 *  all retun values are float because the trig functions do not achieve double
 * precision yet 
 * @author Roger Glassey
*/

public class Vector2D {
/**
* polar coordinates
*/	public static double pi = Math.PI;
	private double x= 0;
	private double y=0;
	private double r=0;
	private double theta=0;
	public float getX(){return (float) x;}
	public float getY(){return (float) y;}
	public float getR(){return (float)r;}
	public float getTheta(){return (float)theta;}// in degrees
/** 
* Constructors
*/
	public  Vector2D(double x, double y)
    {
		setValues(x,y);
	}
	public Vector2D(){}
	
/**
*@param x,y are new coordinates
*/
	public void setValues(double x, double y){
		this.x = x;
		this.y = y;
		toPolar();
	}
		
/**
* @param r is radius, thets is angle
*/
	public void setPolarValues(double r, double theta) {
		this.r = (float)r;
		this.theta = (float)theta;
		toRect();
	}
/**
* @param adds to theta
 * return  this rotated by  a  degrees
*/
	public Vector2D rotate(double a){
		Vector2D v = copy(this);
        v.theta += a;
        v.toRect();
        return v;
	}
/**
* called by all methods that change x,y
*/	
	private  void toPolar(){
		r = (float) Math.sqrt(x*x+y*y);
		theta = (float)Math.toDegrees( Math.atan2(y,x));			
	}
/**
* called by all methods that change r,theta
*/	
	private void toRect(){
		double angle = Math.toRadians(theta);
		x = r * Math.cos(angle);
		y = r * Math.sin(angle);
	}

	
/**
 * vector subtraction
 * @return this - v;
*/
	public Vector2D subtract(Vector2D v)
    {
		return  new Vector2D(this.x - v.x,this.y - v.y );
	}
	
/**
 * vector addition
@ return this = p+q
*/
	public Vector2D add(Vector2D v)
    {
       return  new Vector2D(this.x + v.x,this.y+v.y );
	}
/**
* Scalar multiplication 
*/
	public Vector2D multiply(float scale)
    {
        return new Vector2D(this.x * scale, this.y * scale);
	}
 /**
  * @param v
  * @return a copy of the input vector
  */
    public Vector2D copy(Vector2D v)
    {
        return new Vector2D(v.getX(),v.getY());
    }

    public float innerProduct(Vector2D v)
    {
      return (float) (this.x * v.x + this.y * v.y);
    }

}