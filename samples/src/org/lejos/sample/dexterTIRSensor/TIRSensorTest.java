package org.lejos.sample.dexterTIRSensor;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.DexterTIRSensor;

/**
 * Simple test for the {@link DexterTIRSensor}. The <a
 * href="http://dexterindustries.com/TIR_Sensor.html">Thermal Infrared
 * Sensor</a> is expected to be plugged into {@link SensorPort}.S1
 * 
 * @author Matthias Paul Scholz
 */
public class TIRSensorTest {

	public static void main(final String[] args) {
		final DexterTIRSensor dexterTIRSensor = new DexterTIRSensor(
				SensorPort.S1, 0x0E);
		while (!Button.ESCAPE.isDown()) {
			// get ambient temperature in Celsius
			final float ambientTemperatureInCelsius = dexterTIRSensor
					.getAmbientTemperatureCelsius();
			// get ambient temperature in Kelvin
			final float ambientTemperatureInKelvin = dexterTIRSensor
					.getAmbientTemperatureKelvin();
			// get object temperature in Celsius
			final float objectTemperatureInCelsius = dexterTIRSensor
					.getObjectTemperatureCelsius();
			// get object temperature in Kelvin
			final float objectTemperatureInKelvin = dexterTIRSensor
					.getObjectTemperatureKelvin();
			// display values
			LCD.drawString("Ambient ^C:", 0, 0);
			LCD.drawString(Float.toString(ambientTemperatureInCelsius), 0, 1);
			LCD.drawString("Ambient ^K:", 0, 2);
			LCD.drawString(Float.toString(ambientTemperatureInKelvin), 0, 3);
			LCD.drawString("Object ^C:", 0, 4);
			LCD.drawString(Float.toString(objectTemperatureInCelsius), 0, 5);
			LCD.drawString("Object ^K:", 0, 6);
			LCD.drawString(Float.toString(objectTemperatureInKelvin), 0, 7);
		}
	}

}
