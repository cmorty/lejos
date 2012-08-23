package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;

/**
 * Provides a buffer to store samples
 * @author Aswin
 *
 */
public abstract class SampleBuffer extends AbstractFilter{
	
	int bufferSize = 5;
	int actualSize=0;
	int currentPos=0;
	float[] sampleBuffer;
	
	public SampleBuffer(SampleProvider source, int bufferSize) {
		super(source);
		if (bufferSize < 1)
			throw new IllegalArgumentException();
		this.bufferSize=bufferSize;
		reset();
		}

	
	public int getBufferSize() {
		return bufferSize;
	}
	
	/**
	 * Empties the sample buffer
	 */
	private void reset() {
		currentPos=0;
		actualSize=0;
		sampleBuffer=new float[bufferSize*elements];
	}
	
	

	int toPos(int i, int index) {
		return index*elements+i;
	}

	public void fetchSample(float[] sample, int off) {
		source.fetchSample(sample,off);
		for (int i=0;i<elements;i++) {
			sampleBuffer[toPos(i,currentPos)]=sample[i+off];
		}

		if (actualSize<bufferSize) 
			actualSize+=1;
		currentPos=(currentPos+1) % bufferSize;
	}

	protected void getOldest(float[] sample, int off) {
		for (int i=0;i<elements;i++) {
			sample[i+off]=sampleBuffer[toPos(i,currentPos)];
		}
	}


}
