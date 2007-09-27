package org.lejos.nxt.ldt.util;

public class LeJOSNXJException extends Exception {

	private static final long serialVersionUID = -3880564716609150775L;

	public LeJOSNXJException() {
		// TODO Auto-generated constructor stub
	}

	public LeJOSNXJException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public LeJOSNXJException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public LeJOSNXJException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		if(getCause()!=null)
			return getCause().getMessage();
		else
			return super.getMessage();
	}
}
