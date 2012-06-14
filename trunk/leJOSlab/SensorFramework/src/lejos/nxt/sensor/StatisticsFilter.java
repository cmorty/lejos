package lejos.nxt.sensor;

import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * Class that applies a statitical filter on sensory data.
 * 
 * When calling getValue the class gets a new value from its source and 
 * returns the selected statistic (mean, median, min, max, sum) from the last N values.
 * 
 * @author Aswin Bouwmeester
 *
 */
public class StatisticsFilter implements SensorDataProvider{
	static final int mean=0;
	static final int median=1;
	static final int min=2;
	static final int max=3;
	static final int sum=4;
	
	SensorDataProvider source;
	private int statistic=0;
	private int sampleSize=5;
	private int actualSize=0;
	private int start=0;
	private float[] buffer = new float[sampleSize];

	
	
	/**
	 * Constructor of the StatisticsFilter
	 * @param source
	 * The source where the filter gets its data from
	 */
	public StatisticsFilter(SensorDataProvider source) {
		this.source=source;
	}
	
	/**
	 * Sets the statistic that the class returns upon calling getValue()
	 * @param stat
	 * mean=0, median=1, min=2, max=3, sum=4
	 */
	public void setStatistic(int stat) {
		if (stat<0 || stat>4) throw new IllegalArgumentException("Invalid statistic");
		statistic=stat;
	}
	
	/**
	 * returns the currently selected statistic
	 * @return
	 * mean=0, median=1, min=2, max=3, sum=4
	 */
	public int getStatistic() {
		return statistic;
	}
	
	/**
	 * Sets the number of values to base the statistics on
	 * @param sampleSize
	 */
	public void setSampleSize(int sampleSize) {
		this.sampleSize=sampleSize;
		buffer = new float[sampleSize];
	}
	
	/**
	 * Returns the number of samples statistics are based on
	 * @return
	 */
	public int getSampleSize() {
		return sampleSize;
	}
	
	/**
	 * Adds a value to the buffer and discards the oldest value when the buffer is full
	 * @param value
	 */
	private void add(float value) {
		buffer[(start+actualSize) % sampleSize]=value;
		if (actualSize==sampleSize) 
			start = (start + 1) % sampleSize;
		else 
			actualSize++;
	}
	
		
	/**
	 * @return
	 * The sum of all the values in the buffer
	 */
	private float getSum() {
		float ret=0;
		for (int i=0;i<actualSize;i++) 
			ret+=buffer[i];
		return ret;
	}
	
	/**
	 * @return
	 * The mean of all values in the buffer 
	 */
	private float getMean() {
		if (actualSize>0) 
			return getSum()/actualSize;
		else return Float.NaN;
	}
	
	
	
	/**
	 * @return
	 * The biggest value in the buffer
	 */
	private float getMax(){
		float ret=Float.NEGATIVE_INFINITY;
		for (int i=0;i<actualSize;i++) 
			ret=Math.max(buffer[i],ret);
		return ret;
	}
	
	/**
	 * @return
	 * The smallest value in the buffer
	 */
	private float getMin(){
		float ret=Float.POSITIVE_INFINITY;
		for (int i=0;i<actualSize;i++) 
			ret=Math.min(buffer[i],ret);
		return ret;
	}

	/**
	 * @return
	 * The median value in the buffer
	 */
	private float getMedian(){
		return getRanked((int)Math.floor(actualSize/2));
	}

	
	
	/**
	 * @param rank
	 * @return
	 * The value of the N biggest item. This is used for other statistics like median, min and max,  
	 */
	private float getRanked(int rank) {
		// copy buffer to array
		float[] temp=new float[actualSize];
		int index=0;
		for (int i=0;i<actualSize;i++) 
			temp[index++]=buffer[i ];
		
		// sort the array
		int out, in;
		float hold;
		for(out=actualSize-1; out>1; out--)  
	     for(in=0; in<out; in++)    
	      if( temp[in] > temp[in+1] ) {
	      	hold=temp[in];
	      	temp[in]=temp[in+1];
	      	temp[in+1]=hold;
	      }

		return temp[rank];
	}
	

	public int getRefreshRate() {
		return source.getRefreshRate();
	}

	public float fetchData() {
		add(source.fetchData());
		switch(statistic) {
			case (0): 
				return getMean();
			case (1):
				return getMedian();
			case (2):
				return getMin();
			case (3):
				return getMax();
			case (4):
				return getSum();
		}
		return 0;
	}

}
