package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;
import lejos.util.Delay;

/**
 * The AutoSampler creates a separate thread that fetches samples from a source at 
 * fixed intervals.<p>
 * 
 * @author Aswin
 *
 */
public class AutoSampler extends AbstractFilter {
	
	//TODO: add functionality to add listeners
	
	float[] buffer;
	boolean	running	= true;
	float	sampleRate;
	int	interval;

	boolean newSampleAvailable=false;

	/**
	 * @param source
	 * A SampleProvider
	 * @param sampleRate
	 * The sample rate expressed in Hertz (Samples / second)
	 */
	public AutoSampler(SampleProvider source, float sampleRate) {
		super(source);
		setSampleRate(sampleRate);
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
	 * Separate thread to continuously update the buffer with most recent sensor
	 * data at fixed interval.
	 * 
	 * @author Aswin Bouwmeester
	 * 
	 */
	private class Runner extends Thread {


		@Override
		public void run() {
			long nextTime=System.currentTimeMillis();
			long currentTime;
			while (true) {
				nextTime += interval;
				if (running) {
					source.fetchSample(buffer,0);
					newSampleAvailable=true;
				}
				currentTime=System.currentTimeMillis();
				if (currentTime<nextTime)
					Delay.msDelay(nextTime-currentTime);
			}
		}

	}

	/**
	 * @return rate in Hz
	 */
	public float getSampleRate() {
		return sampleRate;
	}

	/**
//	 * @param rate in Hz
	 */
	public void setSampleRate(float rate) {
		sampleRate=rate;
		interval=(int) (1000/sampleRate);
	}


	public void start() {
		buffer=new float[elements];
		running=true;
	}

	public void stop() {
		running=false;
	}

}
