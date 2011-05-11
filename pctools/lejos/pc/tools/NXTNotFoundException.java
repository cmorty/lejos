package lejos.pc.tools;

public class NXTNotFoundException extends Exception {

	private static final long serialVersionUID = -7605663541720174844L;

	public NXTNotFoundException() {
		super();
	}

	public NXTNotFoundException(String arg0) {
		super(arg0);
	}

	public NXTNotFoundException(Throwable arg0) {
		super(arg0);
	}

	public NXTNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public String getMessage() {
		if((getCause()!=null)&&(getCause().getMessage()!=null))
			return getCause().getMessage();
		else
			return super.getMessage();
	}	

}
