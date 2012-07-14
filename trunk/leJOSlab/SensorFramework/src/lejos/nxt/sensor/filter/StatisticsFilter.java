package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;

/**
 * Sensor Data Provider that applies a statitical filter on sensory data.
 * 
 * getValue returns the selected statistic (mean, median, min, max, sum) from
 * the last N values.
 * 
 * @author Aswin Bouwmeester
 * 
 */
public class StatisticsFilter implements SampleProvider {
	public static final int MEAN = 0;
	public static final int MEDIAN = 1;
	public static final int MIN = 2;
	public static final int MAX = 3;
	public static final int SUM = 4;

	private SampleProvider source;
	private int statistic = 0;
	private int sampleSize = 5;
	private int actualSize = 0;
	private int start = 0;
	private float[] buffer = new float[sampleSize];

	/**
	 * Constructor of the StatisticsFilter
	 * 
	 * @param source
	 *          The source where the filter gets its data from
	 */
	public StatisticsFilter(SampleProvider source) {
		this.source = source;
	}

	public float fetchSample() {
		add(source.fetchSample());
		switch (statistic) {
			case (MEAN):
				return getMean();
			case (MEDIAN):
				return getMedian();
			case (MIN):
				return getMin();
			case (MAX):
				return getMax();
			case (SUM):
				return getSum();
			default:
		}
		return 0;
	}
	
	public int getMinimumFetchInterval() {
		return source.getMinimumFetchInterval();
	}


	/**
	 * @return The biggest value in the buffer
	 */
	public float getMax() {
		float ret = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < actualSize; i++)
			ret = Math.max(buffer[i], ret);
		return ret;
	}

	/**
	 * @return The mean of all values in the buffer
	 */
	public float getMean() {
		if (actualSize > 0)
			return getSum() / actualSize;
		else
			return Float.NaN;
	}

	/**
	 * @return The median value in the buffer
	 */
	public float getMedian() {
		return getRanked((int) Math.floor(actualSize / 2));
	}

	/**
	 * @return The smallest value in the buffer
	 */
	public float getMin() {
		float ret = Float.POSITIVE_INFINITY;
		for (int i = 0; i < actualSize; i++)
			ret = Math.min(buffer[i], ret);
		return ret;
	}

	/**
	 * @param rank
	 * @return The value of the N biggest item. This is used for other statistics
	 *         like median, min and max,
	 */
	public float getRanked(int rank) {
		// copy buffer to array
		float[] temp = new float[actualSize];
		int index = 0;
		for (int i = 0; i < actualSize; i++)
			temp[index++] = buffer[i];

		// sort the array
		int out, in;
		float hold;
		for (out = actualSize - 1; out > 1; out--)
			for (in = 0; in < out; in++)
				if (temp[in] > temp[in + 1]) {
					hold = temp[in];
					temp[in] = temp[in + 1];
					temp[in + 1] = hold;
				}

		return temp[rank];
	}


	/**
	 * Returns the number of samples statistics are based on
	 * 
	 * @return
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * returns the currently selected statistic
	 * 
	 * @return mean=0, median=1, min=2, max=3, sum=4
	 */
	public int getStatistic() {
		return statistic;
	}

	/**
	 * @return The sum of all the values in the buffer
	 */
	public float getSum() {
		float ret = 0;
		for (int i = 0; i < actualSize; i++)
			ret += buffer[i];
		return ret;
	}

	/**
	 * Sets the number of values to base the statistics on
	 * 
	 * @param sampleSize
	 */
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
		buffer = new float[sampleSize];
	}

	/**
	 * Sets the statistic that the class returns upon calling getValue()
	 * 
	 * @param stat
	 *          mean=0, median=1, min=2, max=3, sum=4
	 */
	public void setStatistic(int stat) {
		if (stat < 0 || stat > 4)
			throw new IllegalArgumentException("Invalid statistic");
		statistic = stat;
	}

	/**
	 * Adds a value to the buffer and discards the oldest value when the buffer is
	 * full
	 * 
	 * @param value
	 */
	private void add(float value) {
		buffer[(start + actualSize) % sampleSize] = value;
		if (actualSize == sampleSize)
			start = (start + 1) % sampleSize;
		else
			actualSize++;
	}

}
