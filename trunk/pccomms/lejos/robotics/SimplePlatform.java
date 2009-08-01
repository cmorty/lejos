package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

public class SimplePlatform implements RotationPlatform {

	// Motor for rotating sensor:
	private TachoMotor directionMotor = null;
	
	// Invert motor directions:
	private boolean invertDir = false;
	
	// Gear ratios
	private float ratioDir = 1.0f;
	
	// Rotation movement constraints:
	private int maxDir;
	private int minDir;
	
	public SimplePlatform(TachoMotor directionMotor) {
		this(directionMotor, false);
	}
	
	public SimplePlatform(TachoMotor directionMotor, boolean invertDir) {
		this(directionMotor, false, 1.0f);
	}
	
	public SimplePlatform(TachoMotor directionMotor, boolean invertDir, double ratioDir) {
		this.directionMotor = directionMotor;
		this.invertDir = invertDir;
		this.ratioDir = (float)ratioDir;
	}
	
	public int getAbsoluteDirection(Pose pose) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getDirection() {
		// TODO Deal with zeroed values.
		if(invertDir)
			return (int)(-directionMotor.getTachoCount() * ratioDir);
		else
			return (int)(directionMotor.getTachoCount() * ratioDir);
	}
	
	public int getMaximumDirection() {
		return this.maxDir;
	}

	public int getMinimumDirection() {
		return this.minDir;
	}

	public int getRotationSpeed() {
		return (int)(directionMotor.getSpeed() / ratioDir);
	}

	public float getXOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getYOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getZOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void scanLeft() {
		setDirection(getMaximumDirection());
	}

	public void scanRight() {
		setDirection(getMinimumDirection());
	}
	
	public void setDirection(int angle) {
		// TODO: Deal with zeroed value.
		if(invertDir) angle = -angle;
		directionMotor.rotateTo((int)(angle * ratioDir));
	}

	public void setMaximumDirection(int maxAngle) {
		this.maxDir = maxAngle;
	}

	public void setMinimumDirection(int minAngle) {
		this.minDir = minAngle;
	}

	public void setRotationSpeed(int speed) {
		directionMotor.setSpeed((int)(speed * ratioDir));
	}

	public void setXOffset() {
		// TODO Auto-generated method stub
	}

	public void setYOffset() {
		// TODO Auto-generated method stub
	}

	public void setZOffset() {
		// TODO Auto-generated method stub
	}

	public void stopRotation() {
		this.directionMotor.stop();
	}

	public boolean zero() {
		// TODO Auto-generated method stub
		return false;
	}	
}