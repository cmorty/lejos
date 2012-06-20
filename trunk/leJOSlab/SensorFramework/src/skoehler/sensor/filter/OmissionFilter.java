package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * Filter to skip/omit a number of samples from a sample stream, e.g. to reduce the number of samples after low-pass filtering.
 */
public class OmissionFilter implements VectorData {
    private final VectorData source;
    private final int omission;
    
    public OmissionFilter(VectorData source, int omission) {
        if (omission < 0)
            throw new IllegalArgumentException();
        
        this.source = source;
        this.omission = omission;
    }

    public int getQuantity() {
        return this.source.getQuantity();
    }

    public int getAxisCount() {
        return this.source.getAxisCount();
    }

    public void fetchSamples(float[] dst, int off) {
        for (int i=0; i<=this.omission; i++)
            this.source.fetchSamples(dst, off);
    }

}
