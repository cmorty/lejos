package lejos.util;

/**
 * Remote PID tuning extension for <code>NXTDataLogger</code> using the <code>LogMessageManager</code>. 
 * Provides an out-of-the-box tuning interface in the NXT Charting Logger for the 
 * <code>PIDController</code> class or your own custom PID tuning class.
 * <p>
 * You can create your own PID tuning implementation
 * by implementing <code>PIDTuningProvider</code> in your class and passing an instance of it to the 
 * <code>{@link PIDTuner#PIDTuner(PIDTuningProvider, LogMessageManager)}</code> constructor.
 * 
 * @author Kirk P. Thompson
 * @see LogMessageManager
 *
 */
public class PIDTuner extends LogMessageTypeHandler {
	private PIDTuningProvider pIDtuner;
	
	
	/**
	 * Create a <code>PIDTuner</code> instance using a passed implementation of <code>PIDTuningProvider</code> and
	 * the <code>LogMessageManager</code> singleton instance. 
	 * 
	 * @param tuner The instantiated <code>PIDTuningProvider</code> implementation
	 * @param lmm The <code>LogMessageManager</code> singleton. See {@link LogMessageManager#getLogMessageManager(NXTDataLogger)}.
	 * @see PIDTuningProvider
	 */
	public PIDTuner(PIDTuningProvider tuner, LogMessageManager lmm){
		super(lmm);
		if (tuner==null) {
			throw new IllegalArgumentException("no PIDTuningProvider");
		}
		this.pIDtuner = tuner;
	}
	
	/**
	 * Create a <code>PIDTuner</code> instance using the passed <code>PIDController</code> and
	 * <code>LogMessageManager</code> singleton instance. 
	 * <p>
	 * This constructor will call <code>PIDController.registerDataLogger()</code> to set the headers 
	 * for <code>NXTDataLogger</code> and do the corresponding <code>writeLog</code> calls. You do 
	 * not need to do any <code>LogColumn</code> setup in your code as the 
	 * <code>PIDController.registerDataLogger()</code> will do this for you.
	 * 
	 * @param thePIDController The instantiated <code>PIDController</code> 
	 * @see PIDController
	 * @see LogMessageManager
	 * @see PIDController#registerDataLogger(Logger)
	 */
	public PIDTuner(PIDController thePIDController,  LogMessageManager lmm){
		super(lmm);
		if (thePIDController==null) {
			throw new IllegalArgumentException("no PIDController");
		}
		this.pIDtuner = new PrivatePIDTuner(thePIDController);
	}
	
	private class PrivatePIDTuner implements PIDTuningProvider {
		private PIDController myPIDController;
		public PrivatePIDTuner(PIDController myPIDController) {
			this.myPIDController = myPIDController;
			this.myPIDController.registerDataLogger(getNXTDataLogger());
		}
		
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getKp()
		 */
		public float getKp() {
			return myPIDController.getPIDParam(PIDController.PID_KP);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setKp(float)
		 */
		public void setKp(float kp) {
			myPIDController.setPIDParam(PIDController.PID_KP, kp);			
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getKi()
		 */
		public float getKi() {
			return myPIDController.getPIDParam(PIDController.PID_KI);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setKi(float)
		 */
		public void setKi(float ki) {
			myPIDController.setPIDParam(PIDController.PID_KI, ki);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getKd()
		 */
		public float getKd() {
			return myPIDController.getPIDParam(PIDController.PID_KD);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setKd(float)
		 */
		public void setKd(float kd) {
			myPIDController.setPIDParam(PIDController.PID_KD, kd);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getRampExponent()
		 */
		public float getRampExponent() {
			return myPIDController.getPIDParam(PIDController.PID_RAMP_POWER);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setRampExponent(float)
		 */
		public void setRampExponent(float rampExponent) {
			myPIDController.setPIDParam(PIDController.PID_RAMP_POWER, rampExponent);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getRampTriggerThreshold()
		 */
		public float getRampTriggerThreshold() {
			return myPIDController.getPIDParam(PIDController.PID_RAMP_THRESHOLD);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setRampTriggerThreshold(float)
		 */
		public void setRampTriggerThreshold(float rampTriggerThreshold) {
			myPIDController.setPIDParam(PIDController.PID_RAMP_THRESHOLD, rampTriggerThreshold);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getMVDeadband()
		 */
		public float getMVDeadband() {
			return myPIDController.getPIDParam(PIDController.PID_DEADBAND);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setMVDeadband(float)
		 */
		public void setMVDeadband(float mVDeadband) {
			myPIDController.setPIDParam(PIDController.PID_DEADBAND, mVDeadband);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getMVHighLimit()
		 */
		public float getMVHighLimit() {
			return myPIDController.getPIDParam(PIDController.PID_LIMITHIGH);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setMVHighLimit(float)
		 */
		public void setMVHighLimit(float mVHighLimit) {
			myPIDController.setPIDParam(PIDController.PID_LIMITHIGH, mVHighLimit);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getMVLowLimit()
		 */
		public float getMVLowLimit() {
			return myPIDController.getPIDParam(PIDController.PID_LIMITLOW);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setMVLowLimit(float)
		 */
		public void setMVLowLimit(float mVLowLimit) {
			myPIDController.setPIDParam(PIDController.PID_LIMITLOW, mVLowLimit);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getIntegralWindupLowLimit()
		 */
		public float getIntegralWindupLowLimit() {
			return myPIDController.getPIDParam(PIDController.PID_I_LIMITLOW);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setIntegralWindupLowLimit(float)
		 */
		public void setIntegralWindupLowLimit(float integralWindupLowLimit) {
			myPIDController.setPIDParam(PIDController.PID_I_LIMITLOW, integralWindupLowLimit);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getIntegralWindupHighLimit()
		 */
		public float getIntegralWindupHighLimit() {
			return myPIDController.getPIDParam(PIDController.PID_I_LIMITHIGH);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setIntegralWindupHighLimit(float)
		 */
		public void setIntegralWindupHighLimit(float integralWindupHighLimit) {
			myPIDController.setPIDParam(PIDController.PID_I_LIMITHIGH, integralWindupHighLimit);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getSP()
		 */
		public float getSP() {
			return myPIDController.getPIDParam(PIDController.PID_SETPOINT);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setSP(float)
		 */
		public void setSP(float sP) {
			myPIDController.setPIDParam(PIDController.PID_SETPOINT, sP);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#getDelay()
		 */
		public int getDelay() {
			return myPIDController.getDelay();
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setDelay(int)
		 */
		public void setDelay(int delay) {
			myPIDController.setDelay(delay);
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#isIntegralFrozen()
		 */
		public boolean isIntegralFrozen() {
			return myPIDController.isIntegralFrozen();
		}
		/* (non-Javadoc)
		 * @see lejos.util.PIDTuningProvider#setIntegralFrozen(boolean)
		 */
		public void setIntegralFrozen(boolean integralFrozen) {
			myPIDController.freezeIntegral(integralFrozen);
		}
	}
	
	/** Implementation use only. You do not need to use this method.
	 * @see lejos.util.LogMessageTypeHandler#getHandlerTypeID()
	 */
	@Override
	protected final int getHandlerTypeID() {
		return TYPE_PID_TUNER;
	}
	
	@Override
	void processMessage(byte[] message, int typeID) {
		// PID tuner ignores broadcast messages
		if (typeID==TYPE_ALWAYS_RECEIVE) return;
		
//		System.out.println(getHandlerID() + ": hndlr " + (message[0] & 0xff));
		// Skip processing if not for me
		if (getHandlerID() != (message[0] & 0xff)) return; // byte 0 => handler ID
		
		// get the command
		int command = message[1] & 0xff;
		
		
		// init working vals
		float sendValue=Float.NaN;
		byte[] buf;
		float messageValue;
		int intMessageValue;
		
		// process the command
		switch (command) {
			case 0:
				messageValue=bytesToFloat(message);
				pIDtuner.setKp(messageValue);
				logComment("setKp: " + messageValue);
				break;
			case 1:
				sendValue = pIDtuner.getKp(); 
				break;
			case 2:
				messageValue=bytesToFloat(message);
				pIDtuner.setKi(messageValue);
				logComment("setKi: " + messageValue);
				break;
			case 3:
				sendValue = pIDtuner.getKi();
				break;
			case 4:
				messageValue=bytesToFloat(message);
				pIDtuner.setKd(messageValue);
				logComment("setKd: " + messageValue);
				break;
			case 5:
				sendValue = pIDtuner.getKd();
				break;
			case 6:
				messageValue=bytesToFloat(message);
				pIDtuner.setRampExponent(messageValue);
				logComment("setRampExponent: " + messageValue);
				break;
			case 7:
				sendValue = pIDtuner.getRampExponent();
				break;
			case 8:
				messageValue=bytesToFloat(message);
				pIDtuner.setRampTriggerThreshold(messageValue);
				logComment("setRampTriggerThreshold: " + messageValue);
				break;
			case 9:
				sendValue = pIDtuner.getRampTriggerThreshold();
				break;
			case 10:
				messageValue=bytesToFloat(message);
				pIDtuner.setMVDeadband(messageValue);
				logComment("setMVDeadband: " + messageValue);
				break;
			case 11:
				sendValue = pIDtuner.getMVDeadband();
				break;
			case 12:
				messageValue=bytesToFloat(message);
				pIDtuner.setMVHighLimit(messageValue);
				logComment("setMVHighLimit: " + messageValue);
				break;
			case 13:
				sendValue = pIDtuner.getMVHighLimit();
				break;
			case 14:
				messageValue=bytesToFloat(message);
				pIDtuner.setMVLowLimit(messageValue);
				logComment("setMVLowLimit: " + messageValue);
				break;
			case 15:
				sendValue = pIDtuner.getMVLowLimit();
				break;
			case 16:
				messageValue=bytesToFloat(message);
				pIDtuner.setIntegralWindupLowLimit(messageValue);
				logComment("setIntegralWindupLowLimit: " + messageValue);
				break;
			case 17:
				sendValue = pIDtuner.getIntegralWindupLowLimit();
				break;
			case 18:
				messageValue=bytesToFloat(message);
				pIDtuner.setIntegralWindupHighLimit(messageValue);
				logComment("setIntegralWindupHighLimit: " + messageValue);
				break;
			case 19:
				sendValue = pIDtuner.getIntegralWindupHighLimit();
				break;
			case 20:
				messageValue=bytesToFloat(message);
				pIDtuner.setSP(messageValue);
				logComment("setSP: " + messageValue);
				break;
			case 21:
				sendValue = pIDtuner.getSP();
				break;
			case 22:
				intMessageValue=EndianTools.decodeIntBE(message, HEADER_DATA_OFFSET);
				pIDtuner.setDelay(intMessageValue);
				logComment("setDelay: " + intMessageValue);
				break;
			case 23:
				buf = new byte[4];
				EndianTools.encodeIntBE(pIDtuner.getDelay(), buf, 0);
				sendMessage(command - 1, buf);
				return;
			case 24:
				pIDtuner.setIntegralFrozen(message[HEADER_DATA_OFFSET]!=0);
				logComment("setIntegralFrozen: " + (message[HEADER_DATA_OFFSET]==0?"false":"true"));
				break;
			case 25:
				buf = new byte[1];
				buf[0] = (byte)(pIDtuner.isIntegralFrozen()?1:0);
				sendMessage(command - 1, buf);
				return;
			default:	
				// do nothing and return if unrecognized command
				return;
		}
		
		// send a reply message if value defined
		if (!Float.isNaN(sendValue)) {
			// shift the command to associated SET so the GUI is instructed to SET the JTextField with returned value
			buf = new byte[4];
			EndianTools.encodeIntBE(Float.floatToIntBits(sendValue), buf, 0);
			sendMessage(command - 1, buf);
		} 
			
	}
	
	private float bytesToFloat(byte[] buf){
		// include the 2 offset to skip over first handler ID and command byte
		float t = Float.intBitsToFloat(EndianTools.decodeIntBE(buf, HEADER_DATA_OFFSET));
		//System.out.println("flt=" + t);
		return t;
	}
	
}
