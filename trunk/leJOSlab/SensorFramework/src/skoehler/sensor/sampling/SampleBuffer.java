package skoehler.sensor.sampling;

import lejos.util.Delay;
import skoehler.sensor.api.VectorData;
import skoehler.sensor.filter.AbstractFilter;

public class SampleBuffer extends AbstractFilter{
	float[] buffer;
	int axisCount;
	int	refreshRate=33;
	boolean	running	= true;

	public SampleBuffer(VectorData source) {
		super(source);
		axisCount=getAxisCount();
		buffer=new float[axisCount];
		Runner runner = new Runner();
		runner.setDaemon(true);
		runner.start();
	}
	
	/**
	 * Pauzes refreshing of the buffer
	 */
	public void pauze() {
		running = false;
	}

	/**
	 * Resumes refreshing of the buffer
	 */
	public void resume() {
		running = true;
	}
	
	public void setRefreshRate(int refreshRate) {
		this.refreshRate = refreshRate;
	}
	
	/**
	 * Returns the refresh rate of the  buffer
	 * @return
	 * refreshRate in mSec
	 */
	public int getRefreshRate() {
		return refreshRate;
	}

	@Override
	public int getAxisCount() {
		return source.getAxisCount();
	}

	@Override
	public void fetchSample(float[] dst, int off) {
		for (int axis=0;axis<axisCount;axis++) 
			dst[axis+off]=buffer[axis];
	}
	
	/**
	 * Wrapper method for fetching data from the <code>source</code>. Can be used
	 * to add aditional processing to fetchData.
	 */
	protected void fetchAndProcess(float[] buf) {
		source.fetchSample(buf,0);
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
					fetchAndProcess(buffer);
					time = System.currentTimeMillis() - time;
				}
				Delay.msDelay(refreshRate - time);
			}
		}

	}

}
