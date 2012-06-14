package lejos.nxt.sensor;

import lejos.util.Delay;

/**
 * This class provides a way to buffer output from the sensor. The buffer is automaticaly updated with new data from the sensor at fixed intervals.
 * 
 * @author Aswin Bouwmeester
 *
 */
public class SensorDataBuffer implements SensorDataProvider{
	protected SensorDataProvider source;
	protected int refreshRate;
	protected float currentValue;
	public boolean	running=true;
	
	/**
	 * Default constructor for SensorDataBuffer
	 * * @param source
	 * Object that serves as a data source for the decorator.
	 * A source can be a sensor driver or another decorator.
	 */
	public SensorDataBuffer(SensorDataProvider source) {
		this.source=source;
		this.refreshRate=source.getRefreshRate();
		Runner runner = new Runner();
		runner.setDaemon(true);
		runner.start();
	}

	public int getRefreshRate() {
		return refreshRate;
	}
	
	/**
	 * Sets the refresh rate of the buffer. <p>
	 * If the resfresh rate is smaller than the refreshRate supported by the sensor this method wail throw an exception. 
	 * @param refreshRate
	 * refreshRate in Msec. 
	 */
	public void setRefreshRate(int refreshRate){
		if (refreshRate<source.getRefreshRate()) throw new IllegalArgumentException("Invalid Refreshrate");
		this.refreshRate=refreshRate;
	}

	/* 
	 * Fetches the data from the buffer
	 */
	public float fetchData() {
		return currentValue;
	}
	
	/**
	 * Pauzes refreshing of the buffer
	 */
	public void pauze() {
		running=false;
	}
	
	/**
	 * Resumes refreshing of the buffer
	 */
	public void resume() {
		running=true;
	}
	
	/**
	 * Wrapper method for fetching data from the <code>source</code>. Can be used to add aditional processing to fetchData.
	 * @return
	 * data value from the sensor
	 */
	protected float fetchAndProcess() {
		return source.fetchData();
	}
	
	/**
	 * Seperate thread to continuously update the buffer with most recent sensor data at fixed interval.
	 * @author Aswin Bouwmeester
	 *
	 */
	private class Runner extends Thread {
		public void run() {
			long time;
			while (true) {
				time=0;
				if (running) {
					time = System.currentTimeMillis();
					currentValue = fetchAndProcess();
					time = System.currentTimeMillis() - time;
				}
				Delay.msDelay(refreshRate - time);
			}
		}

	}
}
