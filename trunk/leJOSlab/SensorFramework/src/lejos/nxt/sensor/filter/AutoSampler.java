package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;
import lejos.util.Delay;

/**
 * The samplebuffer creates a seperate thread that fetches samples from a source at a fixed time interval.<p>
 * 
 * @author Aswin
 *
 */
public class AutoSampler extends AbstractFilter {
	
	//TODO: add synchronisation
	//TODO: add functionality to add listeners
	
	float[] buffer;
	boolean	running	= true;
	float	sampleRate=20;
	boolean newSampleAvailable=false;

	public AutoSampler(SampleProvider source) {
		this(source,20);
	}
	
	public AutoSampler(SampleProvider source, float sampleRate) {
		super(source);
		this.sampleRate=sampleRate;
		buffer=new float[elements];
		Runner runner = new Runner();
		runner.setDaemon(true);
		runner.start();
	}
	

	public boolean isNewSampleAvailable() {
		return newSampleAvailable;
	}

	public synchronized void fetchSample(float[] dst, int off) {
		for (int axis=0;axis<elements;axis++) 
			dst[axis+off]=buffer[axis];
		newSampleAvailable=false;
	}
	
	
	/**
	 * Seperate thread to continuously update the buffer with most recent sensor
	 * data at fixed interval.
	 * 
	 * @author Aswin Bouwmeester
	 * 
	 */
	private class Runner extends Thread {

		@Override
		public void run() {
			long time;
			while (true) {
				time = 0;
				if (running) {
					time = System.currentTimeMillis();
					source.fetchSample(buffer,0);
					newSampleAvailable=true;
					time = System.currentTimeMillis() - time;
				}
				Delay.msDelay((long) ((1000/sampleRate) - time));
			}
		}

	}

	public float getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(float rate) {
		sampleRate=rate;
	}


	public void start() {
		buffer=new float[elements];
		running=true;
	}

	public void stop() {
		running=false;
	}

}
