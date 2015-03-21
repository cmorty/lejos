package lejos.robotics.kinematics;

public class PointUnreachableException extends Exception {

    public PointUnreachableException() {
        super();
    }

    public PointUnreachableException(String message, Throwable cause) {
        super(message, cause);
    }

    public PointUnreachableException(String message) {
        super(message);
    }

    public PointUnreachableException(Throwable cause) {
        super(cause);
    }
}
