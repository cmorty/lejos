package lejos.pc.tools;

public class NXJUploadException extends Exception {

	private static final long serialVersionUID = -7605663541720174844L;

	public NXJUploadException() {
		super();
	}

	public NXJUploadException(String arg0) {
		super(arg0);
	}

	public NXJUploadException(Throwable arg0) {
		super(arg0);
	}

	public NXJUploadException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public String getMessage() {
		if((getCause()!=null)&&(getCause().getMessage()!=null))
			return getCause().getMessage();
		else
			return super.getMessage();
	}	

}
