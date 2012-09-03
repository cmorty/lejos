package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;
import lejos.util.Delay;

/**
 * An inertial navigation system (INS) that keeps track of the robots attitude (angular position in a 3D space). <P>
 * 
 * The INS gives accurate, low noise, low drift data and is highly resistant to external disturbances as it fuses information from  multiple sensors to a single attitude. 
 * The INS runs in its own thread and fetches data from attached sensors at regular intervals.
 * A triaxis gyro sensor is mandatory. The data from the gyro makes the filter accurate and gives low noise levels.
 * A triaxis compass and a triaxis accelerometer are both optional. They provide absolute attitude that is used to correct for gyro drift.
 * The accelerometer prevents drift over the X and y axes, the compass prevents drift over the Z axis.<p>
 * <ul>
 * <li>Use the Angle SampleProvider to get Roll, Pitch and Yaw</li>
 * <li>Use the DCMSampleProvider to get the rotation Matrix</li> 
 * </ul>
 * 
 * The INS can be used to transform spatial data in respect to a robot/body coordinate system to a world coordinate system. This is usefull to convert output
 * from a range sensor to world coordinates or to calculate the acceleration, heading or rotional speed of a tilted robot.
 * <ul>
 * <li>Use the transformW() method to transform a vector to world coordinates.</li>
 * <li>Use the transformB() method to transform a world coordinate to robot (or body) coordinates.</li> 
 * <li>Use the getAttitudeMatrix to get the rotation matrix</li>  
 * </ul>
 * <p>
 * 
 * For an explanation of the math and theory that is the basis of this filter see @see <a href="http://gentlenav.googlecode.com/files/DCMDraft2.pdf">this document.</a><p> 
 * For examples of how to use the filter see @see <a href="http://nxttime.wordpress.com/category/filter/nonlinear-complementary-filter/">my blog.</a><p> 
 * 
 * The class supports tuning of the filter. 
 * <ul>
 * <li>Use the various getters and setters to examine and modify filter parameters</li>
 * </ul>
 * @author Aswin Bouwmeester
 * @version 1.0
 */

public class INS {
	
	// filter parameters
	protected Vector3f				 			iCorrection				= new Vector3f(); // holds the integral. Used for correcting gyro drift.
	protected Vector3f							pCorrection 			= new Vector3f();	//The matrix that holds the P error for the PI correction
	protected Vector3f							piCorrection			= new Vector3f(); //The vector that holds the PI error for the PI correction 
	protected float									accelI						= 0.0007f; 				//The I factor of the filter. Determines how fast the offset of the gyro is corrected.
	protected float									accelP						= 0.007f; 				//The P factor of the filter. Determines how strong the correction by the accelerometer is.
	protected float									magnetoI					= 0.0015f;				//0.01f; //The I factor of the filter. Determines how fast the offset of the gyro is corrected.
	protected float									magnetoP					= 0.055f; 				//The P factor of the filter. Determines how strong the correction by the accelerometer is.
	
	// filter status
	protected RotationMatrix				attitude					= new RotationMatrix(); 	//direction cosine matrix that holds the attitude of the sensor.
	protected RotationMatrix				attitudeT					= new RotationMatrix(); 	//Transpose of the attitude matrix.
	protected boolean								running						= false; 														//Indicates that the filter is running.

	
	// Accelerometer and associated data 
	protected SampleProvider				accelerometer			= null; 						//holds the accelrometer object
	private float[]									accelSample				= new float[3];
	private Vector3f								accel							= new Vector3f(); 	//vector to hold accelromer readings.
	private Vector3f								accelN						= new Vector3f(); 	//vector to hold normalised accelromer readings.
	private float										accelMargin				= 0.1f;
	private boolean									newAccelData			= false;
	protected Vector3f							accelError				= new Vector3f();
	
	// gyro and associated data
	protected SampleProvider				gyro							= null; 						//holds the gyro object
	private float[]									rateSample				= new float[3];
	private Vector3f								rate							= new Vector3f(); 	//The vector that holds the reading from the gyro
	

	//magnetometer and associated data
	protected SampleProvider				compass						= null; 						// holds the compass object
	private float[]									magnetoSample			= new float[3];
	private Vector3f								magneto						= new Vector3f(); 	// the vector that holds the readings from the magnetometer
	private Vector3f								magnetoN					= new Vector3f(); 	// the vector that holds the normalised readings from the magnetometer
	private float										magnetoMargin			= 0.3f;
	private boolean									newMagnetoData		= false;
	protected Vector3f							magnetoError			= new Vector3f();
	private int											nn								= 0;
	private int											everynn						= 1;

	// sampleProviders
	SampleProvider 									angleProvider			=	null;	
	SampleProvider 									dcmProvider				= null;	

	// properties of the earth magnetic field
	protected float									fieldStrength			= 0f;

	// tuning and debugging
	private static float						alpha							= 0.01f; 						//aplha is used for the low pass filter that keeps track of the filter frequency.
	private float										targetFrequency		= 60; 							// The Target frequency the filter runs at.
	private float										Frequency					= targetFrequency; 	// holds the frequency the filter is actually running at.
	
	// utility members
	private float										dt								= 0; 								//holds the time interval between filter iterations.
	private Vector3f								xRow							= new Vector3f();
	private Vector3f								yRow							= new Vector3f();
	private Vector3f								zRow							= new Vector3f();
	private Vector3f								northB						= new Vector3f();
	private Vector3f								northW						= new Vector3f();
	Vector3f 												temp							= new Vector3f();
	private Timer										timer							= new Timer();
	

	
	// constructors
	
	/**
	 * Constructor for the INS for just a gyro
	 * @param gyro
	 * a gyro that support three axes
	 */
	public INS(SampleProvider gyro) {
		this.gyro = gyro;
		startThread();
	}

	/**
	 * Constructor for the INS for gyro and accelerometer
	 * @param gyro
	 * a gyro that support three axes
	 * @param accelerometer
	 * a accelerometer that support three axes
	 */
	public INS(SampleProvider gyro, SampleProvider accelerometer) {
		this.gyro = gyro;
		this.accelerometer = accelerometer;
		startThread();
	}
	
	/**
	 * Constructor for the INS for gyro,accelerometer and compass
	 * @param gyro
	 * a gyro that support three axes
	 * @param accelerometer
	 * a accelerometer that support three axes
	 * @param compass
	 * a magnetometer that support three axes
	 */
	public INS(SampleProvider gyro, SampleProvider accelerometer, SampleProvider compass) {
		this.gyro = gyro;
		this.accelerometer = accelerometer;
		this.compass=compass;
		startThread();
	}	


	
	// core filter methods
	
	//TODO: compare methods below
	/** 
	 * Take the accelerometer and magnetometer readings a and m as 3 dimensional vectors. 
	 * The a x m cross-product points to either East or West regardless of the orientation of the phone. 
	 * Whether the cross product points to East or West depends on your sign conventions (upwards is positive or negative) and the handedness of your coordinate system (left- or right-handed).
	 * You might want to apply at least a simple moving average to your data before computing the cross-product.
	 * 
	 * In general, it is not a good idea to use roll, pitch and yaw, so please don't. Anyway, you do not need it.
	 * 
	 * The accelerometer reading a, the cross-product a x m, and the cross-product of accelerometer reading and the cross-product a x (a x m) gives you 3 pair-wise perpendicular vectors. 
	 * Make them unit vectors by multiplying them with the reciprocal of their respective length. 
	 * These 3 unit vectors will given you a rotation matrix.
	 */
	
	/**
	 * Calculates the initial attitude of the body.<p>
	 * This is done by sampling the accelerometer and compass for N times. The result is averaged and used to build up the
	 * attitude matrix. <p>
	 * If a compass is in use then the magnetic field strength is calculated as well.
	 */
	private void calculateInitialAttitude() {
		if (accel != null || compass != null) {
			int N = 100;
			int t = 0;
			Timer iterationTimer = new Timer();
			fieldStrength = 0;
			Vector3f accelTemp = new Vector3f(0, 0, 0);
			Vector3f magnetoTemp = new Vector3f(0, 0, 0);
			// get N samples from the sensors
			for (int i = 0; i < N; i++) {
				if (targetFrequency > 0)
					iterationTimer.waitUntilDeltaT((long) (Math.pow(10, 9) / targetFrequency));
				fetchSamples();
				if (newAccelData) {
					accelTemp.add(accelN);
				}
				if (newMagnetoData) {
					magnetoTemp.add(magneto);
					t++;
				}
			}
			if (accelerometer != null) {
				accelTemp.normalize();
				attitude.setRow(2, accelTemp);
				attitude.m02 = -attitude.m20;
				attitude.m12 = -attitude.m21;
			}
			if (compass != null && t > 0) {
				magnetoTemp.scale(1.0f / t);
				fieldStrength = magnetoTemp.length();
				transformW(magnetoTemp);
				magnetoTemp.z = 0;
				magnetoTemp.normalize();
				transformB(magnetoTemp);
				attitude.setRow(0, magnetoTemp);
				temp.cross(accelTemp, magnetoTemp);
				attitude.setRow(1, temp);
			}
		}
	}

	/**
	 * Updates the attitude matrix. Contains one iteration of the filter algorithm. <p>
	 * These steps are taken:<br>
	 * <li>Read the sensors</li>
	 * <li>Calculate iteration time (dt)</li>
	 * <li>Calculate angular displacement from gyro and dt </li>
	 * <li>Add correction term from previous iteration</li>
	 * <li>Update direction cosine matrix (DCM) with angular displacement</li>
	 * <li>Remove numerical errors from the DMC</li>
	 * <li>Calculate error between DCM and accelerometer (X and Y axis)</li>
	 * <li>Calculate error between DCM and magnetometer ( Z axis)</li>
	 * <li>Calculate correction term (P+I) from these errors to be used in the next iteration</li>
	 */ 
	private void update() {
		fetchSamples();
		dt = timer.getDeltaT();
		rate.scale(dt);
		rate.add(piCorrection);
		attitude.addSmallRotation(rate);
		if (newAccelData) 
			calculateAccelError();
		if (newMagnetoData) 
			calculateMagnetoError();
		if (newAccelData || newMagnetoData) 
			calcErrorTerm();
		else
			piCorrection.set(0,0,0);
	}
	
	/**
	 * Calculates the error between accelerometer and DCM. <p>
	 * the error is the cross product of the Z-row of the DCM and the normalized acceleration vector;
	 */
	private void calculateAccelError ()  {
		// 
		attitude.getRow(2, zRow);
		accelError.cross(accelN,zRow);
	}

	

	/**
	 * Calculates the error between magnetometer and DCM. <p>
	 * the error is the cross product of the X-row of the DCM and the X and Y components (in world frame) of the magnetometer vector;
	 */
	private void calculateMagnetoError() {
		transformW(magnetoN,northW);
		northW.z=0;
		northW.normalize();
		transformB(northW, northB);
		//northB.normalize();
		attitude.getRow(0, xRow);
		magnetoError.cross(northB,xRow);
	}

	/**
	 * Calculates the error term to be used in the update routine of the filter.<p>
	 * First the P components of each of the sensors is calculated and summed to giva a P term. Then the I component of each of the sensors is calculated and used to update the 
	 * I term of the filter. The error term is the sum of the P and I terms. 
	 */
	private void calcErrorTerm(){
		pCorrection.set(0,0,0);
		if (newAccelData) {
			// p term;
			temp.scale(accelP,accelError);
			pCorrection.add(temp);
			// i term
			accelError.scale(accelI * dt);
			iCorrection.add(accelError);
		}
		if (newMagnetoData) {
			// p term;
			temp.scale(magnetoP, magnetoError);
			pCorrection.add(temp);
			// i term
			magnetoError.scale(magnetoI*dt);
			iCorrection.add(magnetoError);
		}
		// correction term (p+i)
		piCorrection.set(pCorrection);
		piCorrection.add(iCorrection);
	}
	
	
// methods to start, stop and pause the filter
	
	/**
	 * Starts the iteration of the filter.  
	 */
	public void start() {
		stop();
		reset();
		running = true;
	}
	
	/**
	 * Starts the inner filter class that takes care of iteration of the filter
	 */
	private void startThread(){
		Filter filter = new Filter();
		filter.setDaemon(true);
		filter.start();
	}
	
	/**
	 * Stops the filter from running
	 */
	public void stop() {
		running = false;
	}

	
	/**
	 * Resets the filter, and sets initial state of the DCM
	 */

	private void reset() {
		attitude.setIdentity();
		//iCorrection.set(0,0,0);
		piCorrection.set(0,0,0);
		calculateInitialAttitude();
		timer.reset();
	}

	/**
	 * Resumes running of the filter without resetting the current state
	 */
	public void resume() {
		running = true;
	}
	
	
// transformation methods world => body, body => world	
	/**
	 * Transforms a vector in place from a world frame to a body frame 
	 * @param t
	 * The vector to transform
	 */
	public void transformB(Vector3f t) {
		attitudeT.transpose(attitude);
		attitudeT.transform(t);
	}


	/**
	 * Sets a vector to be the the body frame equivalent of the other vector
	 * @param t
	 * The world frame vector to transform
	 * @param result
	 * The body frame vector 
	 */
	public void transformB(Vector3f t, Vector3f result) {
		attitudeT.transpose(attitude);
		attitudeT.transform(t,result);
	}

	/**
	 * Transforms a vector in place from a body frame to a world frame
	 * @param t
	 * The vector to transform
	 */
	public void transformW(Vector3f t) {
		attitude.transform(t);
	}

	/**
	 * Sets a vector to be the the world frame equivalent of the other vector
	 * @param t
	 * The body frame vector to transform
	 * @param result
	 * The world frame vector 
	 */
	public void transformW(Vector3f t, Vector3f result) {
		attitude.transform(t,result);
	}


	// SampleProviders



/**
 * SampleProvider for Direction Cosine Matrix (rotation matrix)
 * @return
 * rotation matrix in row. column order
 */
public SampleProvider getDCMProvider() {
	if (dcmProvider==null)
		dcmProvider=new DcmProvider();
	return dcmProvider;
}
	
	private class DcmProvider implements SampleProvider{

		public DcmProvider() {
		}

		public int getQuantity() {
			return Quantities.DCM;
		}

		public int getElementsCount() {
			return 9;
		}

		public void fetchSample(float[] dst, int off) {
			dst[0+off]=attitude.m00;
			dst[1+off]=attitude.m01;
			dst[2+off]=attitude.m02;
			dst[3+off]=attitude.m10;
			dst[4+off]=attitude.m11;
			dst[5+off]=attitude.m12;
			dst[6+off]=attitude.m20;
			dst[7+off]=attitude.m21;
			dst[8+off]=attitude.m22;
		}

		public float fetchSample() {
			return 0;
		}
		
	}
	


	/** SampleProvider for roll, pitch and yaw angles
	 * @return
	 * roll, pitch and yaw angles in radians
	 */
	public SampleProvider getAngleProvider() {
		if (angleProvider==null)
			angleProvider=new AngleProvider();
		return angleProvider;
	}
		
		private class AngleProvider implements SampleProvider{

			public AngleProvider() {
			}

			public int getQuantity() {
				return Quantities.ANGLE;
			}

			public int getElementsCount() {
				return 3;
			}

			public void fetchSample(float[] dst, int off) {
				dst[0+off]=getRoll();
				dst[1+off]=getPitch();
				dst[2+off]=getYaw();
			}

			public float fetchSample() {
				return getRoll();
			}
			
		}
	
	
	

	/**
	 * Returns the Direction Cosine Matix (DCM) of the filter
	 * @return 
	 * Returns the attitude matrix <p>
	 * WARNING: This matrix is constantly being update by the filter
	 */
	public Matrix3f getAttitudeMatrix() {
		return attitude;
	}
	
	/**
	 * Returns Pitch 
	 * @return
	 * returns the pitch, the rotation around the Y axis. 
	 */
	public float getPitch() {
		return (float)-Math.asin(attitude.m20);
	}

	
	/**
	 * Returns Roll
	 * @return
	 * returns the roll, the rotation around the X axis. 
	 */

	public float getRoll() {
		return (float)Math.atan2(attitude.m21, attitude.m22);
	}

	
	/**
	 * Returns Yaw
	 * @return
	 * returns the yaw, the rotation around the Z axis. 
	 */
	public float getYaw() {
		return (float)Math.atan2(attitude.m10, attitude.m00);
	}
	
	

	
// methods to retreive filter performance	and state
	
	/**
	 * @return 
	 * Returns the target frequency for the the filter.
	 */
	public float getTargetFrequency() {
		return targetFrequency;
	}
	
	/**
	 * @return 
	 * Indicates whether the filter is running
	 */
	public boolean isRunning() {
		return running;
	}

	
// getters and setters	
	
	
	/**
	 * Gives an indication of the iteration speed of the filter
	 * @return 
	 * Returns the actual freqency in Hertz the filter runs at. 
	 */
	public float getFrequency() {
		return Frequency;
	}


	/**
	 * @return 
	 * Returns the I-factor of the filter
	 */
	public float getI() {
		return accelI;
	}

	/**
	 * @return 
	 * Returns the P-factor of the filter
	 */
	public float getP() {
		return accelP;
	}
	

	/**
	 * The I-factor for the accelerometer controls how fast the filter adjusts to changes in gyro offset over the X and Y axes.
	 *  * @param ki
	 *          the I-factor of the filter
	 */
	public void setAccelI(float ki) {
		accelI = ki;
	}
	
	/**
	 * The P-factor of the filter controls how fast the filter corrects the gyro data with accelerometer data
	 * When the P-factor is too high the filter will suffer from noise.
	 * When it is too low the filter will recover slowly from gyro and integration errors.
	 * @param kp
	 *          the P-factor of the filter
	 */
	public void setAccelP(float kp) {
		accelP = kp;
	}
	
	/**
	 * Sets the iteration speed of the filter
	 * @param targetFrequency
	 *          the target frequency the filter runs at. Lower this value to save
	 *          CPU time, raise it to increase the quality of the filter on fast
	 *          moving robots. Set to 0 to run the filter at maximum speed. If the
	 *          targetFrequnecy is too high for the filter to operate it will run at maximum speed possible.
	 */
	public void setTargetFrequency(float targetFrequency) {
		this.targetFrequency = targetFrequency;
		Frequency=targetFrequency;
	}



	// methods to interface with sensors
	
	
	/**
	 * Fetches data from all attached sensors. It also normalizes the accelerometer and magnetometer vectors and 
	 * tests for disturbances of these vectors. If new undisturbed data is available the new<i>Unit</i>Data flag will be set.
	 */
	private void fetchSamples() {
		if (gyro != null) {
			// fetch rate sensor
			gyro.fetchSample(rateSample, 0);
			rate.set(rateSample);
		}
		if (accelerometer != null) {
			// read the accelerometer;
			accelerometer.fetchSample(accelSample, 0);
			accel.set(accelSample);
			accelN.normalize(accel);
			newAccelData=true;
			// Test for big acceleration and ignore measurement when true;
			if (Math.abs(1.0-accel.length()/9.81)>accelMargin) {
				newAccelData=false;
			}
		}
		if (compass!=null) {
			if (nn++==everynn) {
				nn=0;
				// read the magnetometer;
				compass.fetchSample(magnetoSample, 0);
				magneto.set(magnetoSample);
				magnetoN.normalize(magneto);
				newMagnetoData=true;
				// test for compass disturbances and ignore measurement when true;
				if (fieldStrength != 0) 
					if (Math.abs(1.0-magneto.length()/fieldStrength)>magnetoMargin) 
						newMagnetoData=false;
			}
			else
				newMagnetoData=false;
		}
	}



	// Inner classes


	/**
	 * This inner class is responsible for  iteration  the filter in its own thread
	 * @author Aswin
	 *
	 */
	private class Filter extends Thread {
		Timer iterationTimer=new Timer();
		Timer speedTimer=new Timer();
		protected Filter() {
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void run() {
			while (true) {
				if (running) {
					update();
					if (targetFrequency > 0)
						iterationTimer.waitUntilDeltaT( (long) ( Math.pow(10,9)/targetFrequency));
				}
				Frequency = (1.0f - alpha) * Frequency + alpha / ( speedTimer.getDeltaT());
			}
		}
	}
	

	
	/**
	 * Utility class to time at nanosecond resolution
	 * @author Aswin
	 *
	 */
	private class Timer {
	long lastTime;
	float factor=(float) Math.pow(10, -9);
	
	public Timer() {
			reset();
	}
	
	public void reset() {
		lastTime=System.nanoTime();
	}
	
	public float getDeltaT(){
		float deltaT=(System.nanoTime()-lastTime)*factor;
		lastTime=System.nanoTime();
		return deltaT;
	}
	
	public void waitUntilDeltaT(long deltaT) {
		Delay.msDelay((long) (Math.max(0,(lastTime + deltaT - System.nanoTime())*Math.pow(10,-6))));
		lastTime=System.nanoTime();
	}
}
	
	


}
