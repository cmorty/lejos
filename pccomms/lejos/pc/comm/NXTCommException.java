package lejos.pc.comm;

public class NXTCommException extends Exception {

	private static final long serialVersionUID = 8129230555756024038L;

	public NXTCommException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NXTCommException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public NXTCommException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public NXTCommException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		if((getCause()!=null)&&(getCause().getMessage()!=null))
			return getCause().getMessage();
		else
			return super.getMessage();
	}
	
}
