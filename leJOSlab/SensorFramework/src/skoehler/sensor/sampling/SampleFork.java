package skoehler.sensor.sampling;

import skoehler.sensor.api.VectorData;

public class SampleFork {
    
    private final VectorData source;
    private final int axisCount;
    private final float[] buffer;
    private final VectorData out1;
    private final VectorData out2;
    private int bufSize1;
    private int bufSize2;
    private int bufPos1;
    private int bufPos2;
    
    public SampleFork(final VectorData source, int buffersize) {
        if (buffersize < 1)
            throw new IllegalArgumentException();
            
        final int q = source.getQuantity();
        final int ac = source.getAxisCount();
        this.source = source;
        this.axisCount = ac;
        this.buffer = new float[ac * buffersize];
        
        out1 = new VectorData() {
            
            public int getQuantity() {
                return q;
            }
            
            public int getAxisCount() {
                return ac;
            }
            
            public void fetchSample(float[] dst, int off) {
                SampleFork.this.fetchSamples1(dst, off);
            }
        };
        
        out2 = new VectorData() {
            
            public int getQuantity() {
                return q;
            }
            
            public int getAxisCount() {
                return ac;
            }
            
            public void fetchSample(float[] dst, int off) {
                SampleFork.this.fetchSamples2(dst, off);
            }
        };        
    }
    
    public VectorData getOutput1() {
        return this.out1;
    }
    
    public VectorData getOutput2() {
        return this.out2;
    }

    synchronized void fetchSamples1(float[] dst, int off) {
        while (this.bufSize1 <= 0 && this.bufSize2 >= this.buffer.length) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // TODO replace with other exception?
                throw new RuntimeException();
            }
        }

        if (this.bufSize1 <= 0)
        {
            this.source.fetchSample(this.buffer, this.bufPos1);
            this.bufSize1 += this.axisCount;
            this.bufSize2 += this.axisCount;
            this.notifyAll();
        }

        System.arraycopy(this.buffer, this.bufPos1, dst, off, this.axisCount);
        this.bufPos1 = (this.bufPos1 + this.axisCount) % this.buffer.length;
        this.bufSize1 -= this.axisCount;
        this.notifyAll();
    }
    
    synchronized void fetchSamples2(float[] dst, int off) {
        while (this.bufSize2 <= 0 && this.bufSize1 >= this.buffer.length) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // TODO replace with other exception?
                throw new RuntimeException();
            }
        }
        
        if (this.bufSize2 <= 0)
        {
            this.source.fetchSample(this.buffer, this.bufPos2);
            this.bufSize1 += this.axisCount;
            this.bufSize2 += this.axisCount;
            this.notifyAll();
        }

        System.arraycopy(this.buffer, this.bufPos2, dst, off, this.axisCount);
        this.bufPos2 = (this.bufPos2 + this.axisCount) % this.buffer.length;
        this.bufSize2 -= this.axisCount;
        this.notifyAll();
    }
    
    //TODO implement checks for overflow/underflow
}
