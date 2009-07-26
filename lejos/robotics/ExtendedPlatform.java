package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

public class ExtendedPlatform extends SimplePlatform implements	ElevationPlatform {

	// Motors for rotating sensor:
	TachoMotor elevationMotor = null;
	
	// Invert motor directions:
	boolean invertElev = false;
	
	// Gear ratios
	float ratioElev = 1.0f;
	
	// Rotation movement constraints:
	int maxElev;
	int minElev;
	
	public ExtendedPlatform(TachoMotor directionMotor, TachoMotor elevationMotor) {
		this(directionMotor, false, elevationMotor, false);
	}
	
	public ExtendedPlatform(TachoMotor directionMotor, boolean invertDir, TachoMotor elevationMotor, boolean invertElev) {
		this(directionMotor, invertDir, 1.0f, elevationMotor, invertDir, 1.0f);
	}
	
	public ExtendedPlatform(TachoMotor directionMotor, boolean invertDir, double ratioDir, TachoMotor elevationMotor, boolean invertElev, double ratioElev) {
		super(directionMotor, invertDir, ratioDir);
		this.elevationMotor = elevationMotor;
		this.invertElev = invertElev;
		this.ratioElev = (float)ratioElev;
	}

	public int getElevation() {
		// TODO Deal with zeroed values.
		if(invertElev)
			return (int)(-elevationMotor.getTachoCount() * ratioElev);
		else
			return (int)(elevationMotor.getTachoCount() * ratioElev);
	}

	public int getMaximumElevation() {
		return this.maxElev;
	}

	public int getMinimumElevation() {
		return this.minElev;
	}

	public int getElevationSpeed() {
		return (int)(elevationMotor.getSpeed() / ratioElev);
	}

	public void scanUp() {
		setElevation(getMaximumElevation());
	}

	public void scanDown() {
		setElevation(getMinimumElevation());
	}
	
	public void setElevation(int angle) {
		// TODO: Deal with zeroed value.
		if(invertElev) angle = -angle;
		elevationMotor.rotateTo((int)(angle * ratioElev));
	}

	public void setMaximumElevation(int maxAngle) {
		this.maxElev = maxAngle;
	}

	public void setMinimumElevation(int minAngle) {
		this.minElev = minAngle;
	}

	public void setElevationSpeed(int speed) {
		elevationMotor.setSpeed((int)(speed * ratioElev));
	}

	public void stopElevation() {
		this.elevationMotor.stop();
	}
}
