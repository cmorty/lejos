package skoehler.sensor.filter;

import skoehler.sensor.api.VectorData;

/**
 * This filter provides running satistics for samples <p>
 * Statistics are calculated over the most recent samples. 
 * The number of samples to use is 5 by default and can be changed with setSampleSize() method. <p>
 * The filter supports the following statistics: MEAN, MEDIAN, SUM, MIN, MAX. The statistic is MEAN by default and can be set with srtSatistic method.
 * 
 * @author Aswin
 *
 */
public class StatisticsFilter extends AbstractFilter{
	public static final int MEAN = 0;
	public static final int MEDIAN = 1;
	public static final int MIN = 2;
	public static final int MAX = 3;
	public static final int SUM = 4;
	
	private int statistic=MEAN;

	
	
	private final int axisCount;
	private int sampleSize = 5;
	private int actualSize=0;
	private int currentPos=0;
	private float[] statBuffer;
	private final float[] sampleBuffer;
	


	public StatisticsFilter(VectorData source) {
		super(source);
		axisCount=source.getAxisCount();
		sampleBuffer=new float[axisCount];
		reset();
	}
	
	public void setSampleSize(int sampleSize) {
		this.sampleSize=sampleSize;
		reset();
	}
	
	public int getSampleSize() {
		return sampleSize;
	}
	
	/**
	 * Empties the statistics buffer
	 */
	public void reset() {
		currentPos=0;
		actualSize=0;
		statBuffer=new float[sampleSize*axisCount];
	}
	
	public void setStatistic(int statistic) {
		if (statistic<0 || statistic >4) throw new IllegalArgumentException("Invalid statistic");
		this.statistic=statistic;
	}
	


	@Override
	public int getAxisCount() {
		return axisCount;
	}

	private int toPos(int axis, int index) {
		return index*axisCount+axis;
	}

	/* (non-Javadoc)
	 * @see skoehler.sensor.filter.AbstractFilter#fetchSample(float[], int)
	 */
	@Override
	public void fetchSample(float[] sample, int off) {
		source.fetchSample(sampleBuffer, 0);
		for (int axis=0;axis<axisCount;axis++) {
			statBuffer[toPos(axis,currentPos)]=sampleBuffer[axis];
		}
		switch(statistic) {
			case MEAN:
				getMean(sample,off);
				break;
			case MIN:
				getMin(sample,off);
				break;
			case MAX:
				getMax(sample,off);
				break;
			case SUM:
				getSum(sample,off);
				break;
			case MEDIAN:
				getMedian(sample,off);
				break;
		}
		if (actualSize<sampleSize) 
			actualSize+=1;
		currentPos=(currentPos+1) % sampleSize;
	}
	
	
	
	private void getRank(float[] sample,int off, int rank) {
		float[] temp = new float[actualSize];
		int out, in;
		float hold;

		if (actualSize==0 || rank>actualSize) {
			for (int axis=0;axis<axisCount;axis++) {
				sample[axis+off]=Float.NaN;;
			}
		}
		else 
		{
		for (int axis=0;axis<axisCount;axis++) {
			int index = 0;
			for (int i = 0; i < actualSize; i++)
				temp[index++] = statBuffer[toPos(axis,i)];
	
			// sort the array
			for (out = actualSize - 1; out > 1; out--)
				for (in = 0; in < out; in++)
					if (temp[in] > temp[in + 1]) {
						hold = temp[in];
						temp[in] = temp[in + 1];
						temp[in + 1] = hold;
					}
			sample[axis+off]=temp[rank];
			}
		}
	}
	

	private void getMedian(float[] sample, int off) {
		getRank(sample, off,(int) Math.floor(actualSize/2.0));
	}

	private void getSum(float[] sample, int off) {
		for (int axis=0;axis<axisCount;axis++) {
			sample[axis+off]=0;
			for (int i=0;i<actualSize;i++) {
				sample[axis+off]+=statBuffer[toPos(axis,i)];
			}
		}
	}

	private void getMax(float[] sample, int off) {
		for (int axis=0;axis<axisCount;axis++) {
			sample[axis+off]=Float.NEGATIVE_INFINITY;
			for (int i=0;i<actualSize;i++) {
				sample[axis+off]=Math.max(statBuffer[toPos(axis,i)],sample[axis+off]);
			}
		}
	}

	private void getMin(float[] sample, int off) {
		for (int axis=0;axis<axisCount;axis++) {
			sample[axis]=Float.POSITIVE_INFINITY;
			for (int i=0;i<actualSize;i++) {
				sample[axis+off]=Math.min(statBuffer[toPos(axis,i)],sample[axis]+off);
			}
		}
	}

	private void getMean(float[] sample, int off) {
		getSum(sample, off);
		for (int axis=0;axis<axisCount;axis++) {
			sample[axis+off]/=actualSize;
		}
	}
}
	
