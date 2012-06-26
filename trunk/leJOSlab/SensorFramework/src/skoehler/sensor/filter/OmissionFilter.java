package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * Filter to skip/omit a number of samples from a sample stream, e.g. to reduce the number of samples after low-pass filtering.
 */
public class OmissionFilter extends AbstractFilter{
    private final int omission;
    
    public OmissionFilter(VectorData source, int omission) {
        super(source);
    	if (omission < 0)
            throw new IllegalArgumentException();
        
        this.omission = omission;
    }

    @Override
	public int getAxisCount() {
        return this.source.getAxisCount();
    }

    @Override
	public void fetchSample(float[] dst, int off) {
        for (int i=0; i<=this.omission; i++)
            this.source.fetchSample(dst, off);
    }

}
