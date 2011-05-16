package lejos.pc.comm;

/**
 * An exception thrown by a NXTComm implementation.
 */
public class NXTCommException extends Exception {

	private static final long serialVersionUID = 8129230555756024038L;

	public NXTCommException() {
		super();
	}

	public NXTCommException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public NXTCommException(String arg0) {
		super(arg0);
	}

	public NXTCommException(Throwable arg0) {
		super(arg0);
	}

	public String getMessage() {
		//TODO get rid of this, and output cause-message at appropriate places
		if((getCause()!=null)&&(getCause().getMessage()!=null))
			return getCause().getMessage();
		
		return super.getMessage();
	}
	
}
