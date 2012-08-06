package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;


/**
 * This filter provides running satistics for samples <p>
 * Statistics are calculated over the a number of most recent samples. 
 * The number of samples to use is 5 by default and can be changed with setSampleSize() method or in the constructor. <p>
 * The filter supports the following statistics: MEAN, MEDIAN, SUM, MIN, MAX. The statistic is MEAN by default and can be set with setSatistic method or in the constructor.
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

	
	
	private int sampleSize = 5;
	private int actualSize=0;
	private int currentPos=0;
	private float[] statBuffer;
	private final float[] sampleBuffer;
	


	public StatisticsFilter(SampleProvider source) {
		this(source,MEAN,5);
	}

	public StatisticsFilter(SampleProvider source, int statistic) {
		this(source,statistic,5);
	}

	public StatisticsFilter(SampleProvider source, int statistic, int sampleSize) {
		super(source);
		sampleBuffer=new float[elements];
		this.statistic=statistic;
		this.sampleSize=sampleSize;
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
		statBuffer=new float[sampleSize*elements];
	}
	
	public void setStatistic(int statistic) {
		if (statistic<0 || statistic >4) throw new IllegalArgumentException("Invalid statistic");
		this.statistic=statistic;
	}
	

	private int toPos(int i, int index) {
		return index*elements+i;
	}

	public void fetchSample(float[] sample, int off) {
		source.fetchSample(sampleBuffer, 0);
		for (int i=0;i<elements;i++) {
			statBuffer[toPos(i,currentPos)]=sampleBuffer[i];
		}
		if (actualSize<sampleSize) 
			actualSize+=1;
		currentPos=(currentPos+1) % sampleSize;
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
	}
	
	
	
	private void getRank(float[] sample,int off, int rank) {
		float[] temp = new float[actualSize];
		int out, in;
		float hold;

		if (actualSize==0 || rank>actualSize) {
			for (int axis=0;axis<elements;axis++) {
				sample[axis+off]=Float.NaN;
			}
		}
		else 
		{
		for (int i=0;i<elements;i++) {
			int index = 0;
			for (int j = 0; j < actualSize; j++)
				temp[index++] = statBuffer[toPos(i,j)];
	
			// sort the array
			for (out = actualSize - 1; out > 1; out--)
				for (in = 0; in < out; in++)
					if (temp[in] > temp[in + 1]) {
						hold = temp[in];
						temp[in] = temp[in + 1];
						temp[in + 1] = hold;
					}
			sample[i+off]=temp[rank];
			}
		}
	}
	

	private void getMedian(float[] sample, int off) {
		getRank(sample, off,(int) Math.floor(actualSize/2.0));
	}

	private void getSum(float[] sample, int off) {
		for (int i=0;i<elements;i++) {
			sample[i+off]=0;
			for (int j=0;j<actualSize;j++) {
				sample[i+off]+=statBuffer[toPos(i,j)];
			}
		}
	}

	private void getMax(float[] sample, int off) {
		for (int i=0;i<elements;i++) {
			sample[i+off]=Float.NEGATIVE_INFINITY;
			for (int j=0;j<actualSize;j++) {
				sample[i+off]=Math.max(statBuffer[toPos(i,j)],sample[i+off]);
			}
		}
	}

	private void getMin(float[] sample, int off) {
		for (int i=0;i<elements;i++) {
			sample[i]=Float.POSITIVE_INFINITY;
			for (int j=0;j<actualSize;j++) {
				sample[i+off]=Math.min(statBuffer[toPos(i,j)],sample[i]+off);
			}
		}
	}

	private void getMean(float[] sample, int off) {
		getSum(sample, off);
		for (int i=0;i<elements;i++) {
			sample[i+off]/=actualSize;
		}
	}
}
	
