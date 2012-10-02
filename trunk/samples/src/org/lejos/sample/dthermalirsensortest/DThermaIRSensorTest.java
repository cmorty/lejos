package org.lejos.sample.dthermalirsensortest;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.DThermalIR;

/**
 * Simple test for the {@link DThermalIR} sensor driver. The <a
 * href="http://dexterindustries.com/TIR_Sensor.html">Thermal Infrared
 * Sensor</a> is expected to be plugged into {@link SensorPort}.S1
 * 
 * @author Matthias Paul Scholz
 */
public class DThermaIRSensorTest {

	public static void main(final String[] args) {
		final DThermalIR dexterTIRSensor = new DThermalIR(SensorPort.S1);
		while (!Button.ESCAPE.isDown()) {
			// get ambient temperature in Celsius
			final float ambientTemperatureInCelsius = dexterTIRSensor
					.readAmbient();
			// get object temperature in Celsius
			final float objectTemperatureInCelsius = dexterTIRSensor
					.readObject();
			// get emissivity
			final float emissivity = dexterTIRSensor.readEmissivity();
			// display values
			LCD.drawString("Ambient ^C:", 0, 0);
			LCD.drawString(Float.toString(ambientTemperatureInCelsius), 0, 1);
			LCD.drawString("Object ^C:", 0, 3);
			LCD.drawString(Float.toString(objectTemperatureInCelsius), 0, 4);
			LCD.drawString("Emissivity:", 0, 6);
			LCD.drawString(Float.toString(emissivity), 0, 7);
		}
	}

}
