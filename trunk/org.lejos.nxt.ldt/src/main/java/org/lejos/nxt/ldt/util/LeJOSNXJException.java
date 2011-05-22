package org.lejos.nxt.ldt.util;

/**
 * leJOS-specific exception
 * @author Matthias Paul Scholz
 *
 */
public class LeJOSNXJException extends Exception {

	private static final long serialVersionUID = -3880564716609150775L;

	public LeJOSNXJException() {
		super();
	}

	public LeJOSNXJException(String arg0) {
		super(arg0);
	}

	public LeJOSNXJException(Throwable arg0) {
		super(arg0);
	}

	public LeJOSNXJException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
