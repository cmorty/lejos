package lejos.nxt.addon;

import lejos.robotics.Encoder;

public interface LinearActuator extends Encoder {
    public void setPower(int power);
    public int getPower();
    public void extend(int distance, boolean immediateReturn);
    public void retract(int distance, boolean immediateReturn);
    public boolean isMoving();
    public boolean StallWasDetected();
    public void stop();
}
