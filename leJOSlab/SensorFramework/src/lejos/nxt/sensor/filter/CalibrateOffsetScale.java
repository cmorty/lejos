package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;
import lejos.util.Delay;

/**
 * Converts uncalibrated sensor data into calibrated sensor data. Provides means of calibrating the sensor. <P>
 * The sensor can be calibrated by calling the calibrateLow() and (optionally) the calibrateHigh() methods.
 * @author Aswin Bouwmeester
 *
 */
public class CalibrateOffsetScale implements SampleProvider{
	private SampleProvider source;
	private float offsetCorrection=0;
	private float scaleCorrection=1;
	private float lowRaw=0, lowCorrected=0;
	private float highRaw=1, highCorrected=1;
	private int defaultSampleSize=10;
	
	
	/**
	 * Default constructor for Decorator classes
	 * @param source
	 * Object that serves as a data source for the decorator.
	 * A source can be a sensor driver or another decorator.
	 */
	public CalibrateOffsetScale(SampleProvider source) {
		this.source=source;
	}

	public int getMinimumFetchInterval() {
		return source.getMinimumFetchInterval();
	}

	public float fetchSample() {
		return (source.fetchSample()-offsetCorrection)*scaleCorrection;
	}
	
	/**
	 * Calibrates the sensor by getting <code>sampleSize</code> measurements from the sensor and comparing this to the provided <code>low</code> value.
	 * This method adjust the offset correction.
	 * @param low
	 * The value that the sensor should return after calibration
	 */
	public void calibrateLow(float low) {
		calibrateLow(low,defaultSampleSize);
	}

	
	/**
	 * Calibrates the sensor by getting <code>sampleSize</code> measurements from the sensor and comparing this to the provided <code>low</code> value.
	 * This method adjust the offset correction.
	 * @param low
	 * The value that the sensor should return after calibration
	 * @param sampleSize
	 * The number of data samples to use in the calibration process. A higher samplesize reduces the effect of noise in sensor data.
	 */
	public void calibrateLow(float low, int sampleSize) {
		lowRaw=sample(sampleSize);
		lowCorrected=low;
		calculateOffsetScale();
	}

	/**
	 * Calibrates the sensor by getting <code>sampleSize</code> measurements from the sensor and comparing this to the provided <code>high</code> value.
   * This method adjusts the scale correction.
	 * @param high
	 * The value that the sensor should return after calibration
	 */
	public void calibrateHigh(float high) {
		calibrateHigh(high, defaultSampleSize);
	}

	
	/**
	 * Calibrates the sensor by getting <code>sampleSize</code> measurements from the sensor and comparing this to the provided <code>high</code> value.
   * This method adjusts the scale correction.
	 * @param high
	 * The value that the sensor should return after calibration
	 * @param sampleSize
	 * The number of data samples to use in the calibration process. A higher samplesize reduces the effect of noise in sensor data.
	 */
	public void calibrateHigh(float high, int sampleSize) {
		highRaw=sample(sampleSize);
		highCorrected=high;
		calculateOffsetScale();
	}

	/**
	 * Calculates new offset and scale correction values. Call after calling calibrateLow() and (optionally) calibrateHigh().
	 */
	private void calculateOffsetScale() {
		// TODO: correct for using non zero low values. 
		offsetCorrection=lowRaw-lowCorrected;
		scaleCorrection=highCorrected/(highRaw-offsetCorrection);
	}

	/**
	 * Returns the mean value of <code>sampleSize</code> samples from a sensor. 
	 * @param sampleSize
	 * Number of samples to base the mean value on. Should be 1 ore more.
	 * @return
	 * Mean value of the samples.
	 */
	private float sample(int sampleSize) {
		StatisticsFilter stat=new StatisticsFilter(source);
		stat.setSampleSize(sampleSize);
		stat.setStatistic(StatisticsFilter.MEAN);
		for (int i=1;i<sampleSize;i++) {
			stat.fetchSample();
			Delay.msDelay(getMinimumFetchInterval());
		}
		return stat.fetchSample();
	}

}
