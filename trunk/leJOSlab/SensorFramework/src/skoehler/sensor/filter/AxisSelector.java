package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;
import skoehler.sensor.device.AbstractScalarData;

public class AxisSelector extends AbstractScalarData {
	
	private final VectorData source;
	private final float[] buffer;
	private final int axis;
	
	public AxisSelector(VectorData source, int axis) {
		int ac = source.getAxisCount();
		if (axis < 0 || axis >= ac)
			throw new IllegalArgumentException();
		
		this.source = source;
		this.buffer = new float[ac];
		this.axis = axis;
	}

	public int getQuantity() {
		return this.source.getQuantity();
	}

	public float fetchSample() {
		this.source.fetchSamples(this.buffer, 0);
		return this.buffer[this.axis];
	}

}
