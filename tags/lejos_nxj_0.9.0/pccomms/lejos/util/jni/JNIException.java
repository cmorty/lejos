package lejos.util.jni;

public class JNIException extends Exception {

	public JNIException() {
		super();
	}

	public JNIException(String message) {
		super(message);
	}

	public JNIException(Throwable cause) {
		super(cause);
	}

	public JNIException(String message, Throwable cause) {
		super(message, cause);
	}

}
