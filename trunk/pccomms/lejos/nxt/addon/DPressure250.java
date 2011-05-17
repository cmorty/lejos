package lejos.nxt.addon;

import lejos.nxt.ADSensorPort;
import lejos.nxt.SensorConstants;
import lejos.robotics.PressureDetector;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Support for Dexter Industries DPressure250
 * Not tested.
 * 
 * @author Lawrie Griffiths
 *
 */
public class DPressure250 implements SensorConstants, PressureDetector {
	private ADSensorPort port;
	private static final float DPRESS_VREF = 4.85f; // NXT voltage
	private static final float DPRESS_DIVISOR = 0.00369f; 
	private static final float DPRESS_OFFSET = 0.04f;
	
    public DPressure250(ADSensorPort port) {
		this.port = port;
		port.setTypeAndMode(TYPE_CUSTOM, MODE_RAW);
    }
    
    /**
     * Get the pressure reading in kilopascals
     * 
     * @return the pressure in kPa
     */
    public float getPressure() {
    	float vOut = ((port.readRawValue() * DPRESS_VREF) / 1023f);
    	return ((vOut / DPRESS_VREF) - DPRESS_OFFSET) / DPRESS_DIVISOR;
    }
}
