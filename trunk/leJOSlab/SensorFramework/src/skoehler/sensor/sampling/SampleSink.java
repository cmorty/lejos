package skoehler.sensor.sampling;

import skoehler.sensor.api.VectorData;

public class SampleSink implements VectorData {
    
    private final VectorData source;
    private final float[] buffer;
    
    public SampleSink(VectorData source) {
        int ac = source.getAxisCount();
        this.source = source;
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

    public int getQuantity() {
        return source.getQuantity();
    }

    public int getAxisCount() {
        return this.buffer.length;
    }

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
