package lejos.pc.tools;

public class NXJUploadException extends Exception {

	private static final long serialVersionUID = -7605663541720174844L;

	public NXJUploadException() {
		// TODO Auto-generated constructor stub
	}

	public NXJUploadException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public NXJUploadException(Throwable arg0) {
		super(arg0);
	}

	public NXJUploadException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		if((getCause()!=null)&&(getCause().getMessage()!=null))
			return getCause().getMessage();
		else
			return super.getMessage();
	}	

}
