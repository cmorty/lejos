package lejos.nxt.sensor.sensor;

import lejos.nxt.ADSensorPort;
import lejos.nxt.SensorConstants;
import lejos.nxt.sensor.api.SensorDataProvider;
import lejos.robotics.Color;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * This class is used to obtain readings from a LEGO NXT light sensor. The light
 * sensor can be calibrated to low and high values.
 * 
 */
public class LightSensor implements SensorConstants, SensorDataProvider {
	// TODO: setting the flood light does not work
	ADSensorPort		port;
	private boolean	floodlight	= false;

	/**
	 * Create a light sensor object attached to the specified port. The sensor
	 * will be set to floodlit mode, i.e. the LED will be turned on.
	 * 
	 * @param port
	 *          port, e.g. Port.S1
	 */
	public LightSensor(ADSensorPort port) {
		this(port, true);
	}

	/**
	 * Create a light sensor object attached to the specified port, and sets
	 * floodlighting on or off.
	 * 
	 * @param port
	 *          port, e.g. Port.S1
	 * @param floodlight
	 *          true to set floodit mode, false for ambient light.
	 */
	public LightSensor(ADSensorPort port, boolean floodlight) {
		this.port = port;
		this.floodlight = floodlight;
		port.setTypeAndMode((floodlight ? TYPE_LIGHT_ACTIVE : TYPE_LIGHT_INACTIVE), MODE_PCTFULLSCALE);
	}

	public float fetchData() {
		return port.readRawValue();
	}

	public int getFloodlight() {
		if (this.floodlight == true)
			return Color.RED;
		else
			return Color.NONE;
	}

	public int getMinimumFetchInterval() {
		return 4;
	}

	public boolean isFloodlightOn() {
		return this.floodlight;
	}

	public void setFloodlight(boolean floodlight) {
		port.setType(floodlight ? TYPE_LIGHT_ACTIVE : TYPE_LIGHT_INACTIVE);
		this.floodlight = floodlight;
	}

	public boolean setFloodlight(int color) {
		if (color == Color.RED) {
			port.setType(TYPE_LIGHT_ACTIVE);
			this.floodlight = true;
			return true;
		}
		else if (color == Color.NONE) {
			port.setType(TYPE_LIGHT_INACTIVE);
			this.floodlight = false;
			return true;
		}
		else
			return false;
	}
}
