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
            
        final int ac = source.getAxisCount();
        this.source = source;
        this.axisCount = ac;
        this.buffer = new float[ac * buffersize];
        
        out1 = new VectorData() {
            
            public int getQuantity() {
                return source.getQuantity();
            }
            
            public int getAxisCount() {
                return ac;
            }
            
            public void fetchSamples(float[] dst, int off) {
                SampleFork.this.fetchSamples1(dst, off);
            }
        };
        
        out2 = new VectorData() {
            
            public int getQuantity() {
                return source.getQuantity();
            }
            
            public int getAxisCount() {
                return ac;
            }
            
            public void fetchSamples(float[] dst, int off) {
                SampleFork.this.fetchSamples2(dst, off);
            }
        };
        
        //TODO implement means to start/stop thread, set priority, etc.
        Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        SampleFork.this.sampleThread();
                    } catch (InterruptedException e) {
                        // nothing, thread was asked to terminate
                    }
                }
            };
        t.start();
    }
    
    public VectorData getOutput1() {
        return this.out1;
    }
    
    public VectorData getOutput2() {
        return this.out2;
    }

    void fetchSamples1(float[] dst, int off) {
        while (this.bufSize1 <= 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // TODO replace with other exception?
                throw new RuntimeException();
            }
        }

        int pos = this.bufPos1;
        System.arraycopy(this.buffer, pos, dst, off, this.axisCount);
        this.bufPos1 = (pos + this.axisCount) % this.buffer.length;
        this.bufSize1 -= this.axisCount;
        this.notifyAll();
    }
    
    void fetchSamples2(float[] dst, int off) {
        while (this.bufSize2 <= 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // TODO replace with other exception?
                throw new RuntimeException();
            }
        }

        int pos = this.bufPos2;
        System.arraycopy(this.buffer, pos, dst, off, this.axisCount);
        this.bufPos2 = (pos + this.axisCount) % this.buffer.length;
        this.bufSize2 -= this.axisCount;
        this.notifyAll();
    }
    
    //TODO implement checks for overflow/underflow

    void sampleThread() throws InterruptedException {
        float[] buf = new float[this.axisCount];
        while (!Thread.interrupted()) {
            this.source.fetchSamples(buf, 0);

            synchronized (this) {
                while (this.bufSize1 >= this.buffer.length || this.bufSize2 >= this.buffer.length)
                    this.wait();

                int pos = (this.bufPos1 + this.bufSize1) % this.buffer.length;
                System.arraycopy(buf, 0, this.buffer, pos, this.axisCount);
                this.bufSize1 += this.axisCount;
                this.bufSize2 += this.axisCount;
                this.notifyAll();
            }
        }
    }
}
