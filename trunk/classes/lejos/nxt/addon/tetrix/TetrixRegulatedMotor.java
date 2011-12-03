package lejos.nxt.addon.tetrix;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

public class TetrixRegulatedMotor extends TetrixEncoderMotor implements RegulatedMotor{
    public TetrixRegulatedMotor(TetrixMotorController mc, int channel) {
    	super(mc, channel);
    }

    public void addListener(RegulatedMotorListener listener) {
    }

    public RegulatedMotorListener removeListener() {
        return null;
    }

    public void stop(boolean immediateReturn) {
    }

    public void flt(boolean immediateReturn) {
    }

    public void waitComplete() {
    }

    public void rotate(int angle) {
    }

    public void rotateTo(int limitAngle) {
    }

    public int getLimitAngle() {
        return 0;
    }

    public void setSpeed(int speed) {
    }

    public int getSpeed() {
        return 0;
    }

    public float getMaxSpeed() {
        return 0.0f;
    }

    public boolean isStalled() {
        return false;
    }

    public void setStallThreshold(int error, int time) {
    }

    public void setAcceleration(int acceleration) {
    }

    public int getRotationSpeed() {
        return 0;
    }
}
