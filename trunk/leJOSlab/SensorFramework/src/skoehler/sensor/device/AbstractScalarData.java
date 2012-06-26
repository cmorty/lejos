package skoehler.sensor.device;

import skoehler.sensor.api.ScalarData;
import skoehler.sensor.api.VectorData;
import skoehler.sensor.filter.AbstractFilter;

/**
 * Provides the widget dohicky, etc. //TODO kpt inserted javadoc placholder. Need real.
 * 
 * @author Sven
 *
 */
public abstract class AbstractScalarData extends AbstractFilter implements ScalarData {

	public AbstractScalarData(VectorData source) {
		super(source);
	}

	@Override
	public final int getAxisCount() {
		return 1;
	}

	@Override
	public final void fetchSample(float[] dst, int off) {
		dst[off] = this.fetchSample();
	}

}
