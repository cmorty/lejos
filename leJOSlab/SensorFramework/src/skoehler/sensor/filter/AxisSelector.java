package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;
import skoehler.sensor.device.AbstractScalarData;

/**
 * Provides the widget dohicky, etc. //TODO kpt inserted javadoc placholder. Need real.
 * 
 * @author Sven
 *
 */
public class AxisSelector extends AbstractScalarData {
	
	private final float[] buffer;
	private final int axis;
	
	public AxisSelector(VectorData source, int axis) {
		super(source);
		int ac = source.getAxisCount();
		if (axis < 0 || axis >= ac)
			throw new IllegalArgumentException();
		
		this.buffer = new float[ac];
		this.axis = axis;
	}

	public float fetchSample() {
		this.source.fetchSamples(this.buffer, 0);
		return this.buffer[this.axis];
	}

}
