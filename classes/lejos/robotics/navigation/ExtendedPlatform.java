package lejos.robotics.navigation;

import lejos.robotics.TachoMotor;

public class ExtendedPlatform implements SensorPlatform {

	// Motors for rotating sensor:
	TachoMotor directionMotor = null;
	TachoMotor elevationMotor = null;
	
	// Invert motor directions:
	boolean invertDir = false;
	boolean invertElev = false;
	
	// Gear ratios
	float ratioDir = 1.0f;
	float ratioElev = 1.0f;
	
	// Rotation movement constraints:
	int maxDir;
	int minDir;
	int maxElev;
	int minElev;
	
	public ExtendedPlatform(TachoMotor directionMotor, TachoMotor elevationMotor) {
		this.directionMotor = directionMotor;
		this.elevationMotor = elevationMotor;
	}
	
	public ExtendedPlatform(TachoMotor directionMotor, boolean invertDir, TachoMotor elevationMotor, boolean invertElev) {
		this(directionMotor, elevationMotor);
		this.invertDir = invertDir;
		this.invertElev = invertElev;
	}
	
	public ExtendedPlatform(TachoMotor directionMotor, boolean invertDir, double ratioDir, TachoMotor elevationMotor, boolean invertElev, double ratioElev) {
		this(directionMotor, elevationMotor);
		this.invertDir = invertDir;
		this.invertElev = invertElev;
		this.ratioDir = (float)ratioDir;
		this.ratioElev = (float)ratioElev;
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

	public int getElevation() {
		// TODO Deal with zeroed values.
		if(invertElev)
			return (int)(-elevationMotor.getTachoCount() * ratioElev);
		else
			return (int)(elevationMotor.getTachoCount() * ratioElev);
	}

	public int getMaximumDirection() {
		return this.maxDir;
	}

	public int getMaximumElevation() {
		return this.maxElev;
	}

	public int getMinimumDirection() {
		return this.minDir;
	}

	public int getMinimumElevation() {
		return this.minElev;
	}

	public int getSpeed() {
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

	public void rotateLeft() {
		setDirection(getMaximumDirection());
	}

	public void rotateRight() {
		setDirection(getMinimumDirection());
	}
	
	public void rotateUp() {
		setElevation(getMaximumElevation());
	}

	public void rotateDown() {
		setElevation(getMinimumElevation());
	}
	
	public void setDirection(int angle) {
		// TODO: Deal with zeroed value.
		if(invertDir) angle = -angle;
		directionMotor.rotateTo((int)(angle * ratioDir));
	}

	public void setElevation(int angle) {
		// TODO: Deal with zeroed value.
		if(invertElev) angle = -angle;
		elevationMotor.rotateTo((int)(angle * ratioElev));
	}

	public void setMaximumDirection(int maxAngle) {
		this.maxDir = maxAngle;
	}

	public void setMaximumElevation(int maxAngle) {
		this.maxElev = maxAngle;
	}

	public void setMinimumDirection(int minAngle) {
		this.minDir = minAngle;
	}

	public void setMinimumElevation(int minAngle) {
		this.minElev = minAngle;
	}

	public void setSpeed(int speed) {
		directionMotor.setSpeed((int)(speed * ratioDir));
		elevationMotor.setSpeed((int)(speed * ratioElev));
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

	public void stop() {
		this.directionMotor.stop();
		this.elevationMotor.stop();
	}

	public boolean zero() {
		// TODO Auto-generated method stub
		return false;
	}
}
