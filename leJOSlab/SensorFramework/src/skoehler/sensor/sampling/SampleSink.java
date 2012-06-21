package skoehler.sensor.sampling;

import skoehler.sensor.api.VectorData;
import skoehler.sensor.filter.AbstractFilter;

/**
 * Provides the widget dohicky, etc. //TODO kpt inserted javadoc placholder. Need real.
 * 
 * @author Sven
 *
 */
public class SampleSink extends AbstractFilter {
    
    private final float[] buffer;
    
    public SampleSink(VectorData source) {
    	super(source);
        int ac = source.getAxisCount();
        this.buffer = new float[ac];
        
        //TODO implement means to start/stop thread, set priority, etc.
        Thread t = new Thread() {
                @Override
                public void run() {
                    SampleSink.this.sampleThread();
                }
            };
        t.start();
    }

    @Override
	public int getAxisCount() {
        return this.buffer.length;
    }

    @Override
	public synchronized void fetchSamples(float[] dst, int off) {
        System.arraycopy(this.buffer, 0, dst, off, this.buffer.length);
    }
    
    //TODO implement checks for overflow/underflow

    void sampleThread() {
        float[] buf = new float[this.buffer.length];
        while (!Thread.interrupted()) {
            this.source.fetchSamples(buf, 0);

            synchronized (this) {
                System.arraycopy(buf, 0, this.buffer, 0, buf.length);
            }
        }
    }
}
