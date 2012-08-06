package skoehler.sensor.sampling;

import skoehler.sensor.api.VectorData;
import skoehler.sensor.filter.AbstractFilter;

/**
 * Provides the widget dohicky, etc. //TODO kpt inserted javadoc placholder. Need real.
 * 
 * @author Sven
 *
 */
public class SamplingThread extends AbstractFilter {
    
    private final int interval;
    private final int axisCount;
    private final float[] buffer;
    private int bufSize;
    private int bufPos;
    
    public SamplingThread(VectorData source, int buffersize, int interval) {
        super(source);
    	if (buffersize < 1)
            throw new IllegalArgumentException();
            
        int ac = source.getElementCount();
        this.interval = interval;
        this.axisCount = ac;
        this.buffer = new float[ac * buffersize];
        
        //TODO implement means to start/stop thread, set priority, etc.
        Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        SamplingThread.this.sampleThread();
                    } catch (InterruptedException e) {
                        // nothing, thread was asked to terminate
                    }
                }
            };
        t.start();
    }

	@Override
	public int getElementCount() {
	    return this.axisCount;
	}

    @Override
	public synchronized void fetchSample(float[] dst, int off) {
        while (this.bufSize <= 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // TODO replace with other exception?
                throw new RuntimeException();
            }
        }

        int pos = this.bufPos;
        System.arraycopy(this.buffer, pos, dst, off, this.axisCount);
        this.bufPos = (pos + this.axisCount) % this.buffer.length;
        this.bufSize -= this.axisCount;
        this.notifyAll();
    }
    
    //TODO implement checks for overflow/underflow

    void sampleThread() throws InterruptedException {
        long t1 = System.currentTimeMillis();
        float[] buf = new float[this.axisCount];
        while (!Thread.interrupted()) {
            t1 += this.interval;
            while (true) {
                long t2 = System.currentTimeMillis();
                if (t2 >= t1)
                    break;
                Thread.sleep(t1 - t2);
            }
            this.source.fetchSample(buf, 0);

            synchronized (this) {
                while (this.bufSize >= this.buffer.length)
                    this.wait();

                int pos = (this.bufPos + this.bufSize) % this.buffer.length;
                System.arraycopy(buf, 0, this.buffer, pos, this.axisCount);
                this.bufSize += this.axisCount;
                this.notifyAll();
            }
        }
    }
}
