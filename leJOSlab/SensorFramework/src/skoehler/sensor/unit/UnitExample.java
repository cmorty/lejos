package skoehler.sensor.unit;

import skoehler.sensor.api.Quantities;
import skoehler.sensor.api.VectorData;
import skoehler.sensor.device.DummySensor;

public class UnitExample {
	public static void main(String[] args) {
		// Units are defines relative to SI Unit, e.g. Kelvin
		Unit kelvin = new Unit(Quantities.TEMPERATURE, 1, 0); 
		Unit celsius = new Unit(Quantities.TEMPERATURE, 1, -273.15f); 
		Unit fahrenheit = new Unit(Quantities.TEMPERATURE, 1.8f, -459.67f);
		
		VectorData r1 = new DummySensor();
		VectorData r2 = Unit.convert(celsius, fahrenheit, r1);
	}
}
