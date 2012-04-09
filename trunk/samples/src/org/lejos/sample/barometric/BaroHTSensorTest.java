package org.lejos.sample.barometric;

import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.BarometricHTSensor;

/**
 * Simple test for the {@link BarometricHTSensor}. The <a
 * href="http://www.hitechnic.com">HiTechnic</a> barometric sensor is expected
 * to be plugged into {@link SensorPort}.S1
 * 
 * @author Matthias Paul Scholz
 */
public class BaroHTSensorTest {

	public static void main(final String[] args) {
		final BarometricHTSensor barometricSensor = new BarometricHTSensor(
				SensorPort.S1);
		// get pressure in inHg
		final double pressureImperial = barometricSensor.getPressureImperial() / 1000.0;
		// get pressure in hPA
		final double pressureMetric = barometricSensor.getPressureMetric() / 10.0;
		// get temperature in degrees celsius
		final double temperature = barometricSensor.getTemperature() / 10.0;
		// display values on LCD
		System.out.println(pressureImperial + " inHg");
		System.out.println(pressureMetric + " hPa");
		System.out.println(temperature + " ^C");
		// wait for user to shut down
		Button.waitForAnyPress();
	}

}
