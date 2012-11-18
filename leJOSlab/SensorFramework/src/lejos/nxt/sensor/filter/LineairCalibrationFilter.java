package lejos.nxt.sensor.filter;

import java.io.IOException;

import lejos.nxt.sensor.api.FilterProperties;
import lejos.nxt.sensor.api.SampleProvider;
import lejos.util.LogColumn;
import lejos.util.NXTDataLogger;

/**
 * This filter is used to calibrate sensors for offset and scale errors using lineair interpolation. <br> 
 * It has two modes of operation, in operational mode it calibrates a sensors by correcting samples coming from the sensor. 
 * in calibration mode the filter calculates calibration parameters based on samples from the sensor and user specified reference values.
 * 
 * <b>Operational mode</b><br>
 * In operational mode the filter corrects incoming samples for offset and scale errors before passing it back to the program. 
 * The correction algorhithm uses calibration parameters that are calculated beforehand. 
 * These calibration parameters could be stored on the filesystem and loaded using the load() method. 
 * Or they could be calculated by the object itself using in calibration mode. 
 * 
 * <b>How it works</b><br>
 * Correction is done using two calibration parameters, offset and scale. 
 * The offset parameter corrects for offset errors, i.e. the sample is always a fixed amount off the truth: 2 becomes 3, 6 becomes 7.
 * The scale parameter corrects for scale errors, i.e. the sample is always a fixed percentage off the thruth: 2 becomes 3, 6 becoms 9.
 * The combined correction formula is correctedValue = ( rawValue - offsetCorrection ) / scaleCorrection.
 * This calibration technique works on all sensors who's output is lineair to the input. 
 * 
 *  <b>How to use the filter in operational mode</b><br>
 *  In operational mode it is assumed that calibration parameters are calculated beforehand by the filter itself or loaded from the file system using the load() method.
 *  Like any SampleProvider a sample is fetched from the filter using the fetchSample() method. 
 *  
 *  
 * 
 *  <b>Calibration mode</b><br>
 *  In calibration mode the two calibration parameters are calculated. This is done by comparing samples to a user specified reference value and/or range.
 *  Once calibration parameters are calculated they can be used immidiately or stored to the filesystem for later use using the store() method.
 * 
 * <b>How it works</b><br>
 * The CalibratorFiltersupports both offset and scale calibration. During calibration the filter collects minimum and maximum sample values. 
 * From this it calculates offset (as the average of the minimum and maximum value corrected for the reference value) 
 * and scale (as the difference between maximum and minimum value scaled for range). 
 * To minimize the effect of sensor noise during calculation sample values are lowpassed before they are being used for calibration.<p>
 * 
 * <b>How to use the filter</b><br>
 * The filter is used in the program like any othe SampleProvider. 
 * Calibration is started using the startCalibration method and ended with the endCalibration method. 
 * During calibration the program that uses the filter must fetch samples to collect data for calibrating.
 * At the end the calibration process the calculated calibration settings can be stored using the store() method.
 * Calibration can be pauzed if needed. <p>
 * 
 * <b>How to tune the calibration proces</b><br>
 * There are three important parameters to the calibration process that can be modified by the user program.
 * <ul>
 * <li>
 * The reference value. 
 * This is the expected output of the sensor. For calibrating a (motionless) gyro sensor this will be 0. 
 * For calibrating a range sensor for example this should be the range to the object the sensor is calibrated to.
 * The reference value is used in calculating the offset parameter, it is not used in calculating scale.
 * The reference has a default value of 0.
 * </li>
 * <li>
 * The range value.
 * This is the expected range of the sensor output. For calibrationg an accelerometer this could be 2*9.81 when the output should be in m/s^2.
 * The range value is used in calculating the scale parameter, it is not used in calculating offset.
 * The range has a default value of 2, meaning sample values are normalized to a range of -1 to 1.
 * </li>
 * <li>
 * The timeConstant value.
 * This is the timeConstant value of a low-pass filter that is used in calibration mode. 
 * A low pass filter is used during calibration to for two reasons. 
 * First to overcome the effects of sensor noise. These effects can seriously affect range calculation. 
 * Secondly it filters out the side effect of user manipulation when turning the sensor as part of the calibration proces.
 * The parameter affects the amount of smoothing of the low-pass filter.
 * The higher the value, the smoother the samples. Smoother samples are less affected by sensor noise or external shocks but take longer to settle.
 * The time constant has a default value of 0, meaning no smoothing is done by default. 
 * </li>
 * </ul>
 * 
 * <b>Monitoring the calibration process</b><br>
 * The calibration proces can be monitored using the NXTChartingLogger. 
 * The calibration program is responsible for connecting to the NXTChartingLogger and creating a NXTDataLogger object.
 * Data logging is enabled by specifiying a NXTDataLogger object to the filter using the setNXTDataLogger method.
 * The filter outputs the sample, the  minimum, the maximum, the offset and the scale for each element of each sample taken.
 * Data is only logged during the calibration proces.
 * Tip: A line can be hidden in the NXTChartingLogger by clicking on the line in the legend.
 * <p>
 * 
 * @author Aswin Bouwmeester
 *
 */
public class LineairCalibrationFilter extends LowPassFilter {
	private boolean	calibrateForScale		= false;
	private boolean	calibrateForOffset	= true;
	private float[]	reference;
	private float[]	range;
	private float[]	min;
	private float[]	max;
	private float[]	offset;
	private float[]	scale;
	private boolean	calibrationMode			= false;
	NXTDataLogger		log;
	private final FilterProperties props = this.getFilterProperties();
	private float persistantTimeConstant=0;



	public LineairCalibrationFilter(SampleProvider source) {
		super(source, 0);
		reference = new float[elements];
		range = new float[elements];
		min = new float[elements];
		max = new float[elements];
		offset = new float[elements];
		scale = new float[elements];
		
		for (int i = 0; i < elements; i++) {
			reference[i] = 0;
			range[i] = 2;
			offset[i]=0;
			scale[i]=1;
		}
	}

	/** Disables or enables scale calibration. By default scale calibration is disabled.
	 * @param calibrate
	 * A True value means that the sensor is calibrated for scale
	 */
	public void calibrateForScale(boolean calibrate) {
		calibrateForScale = calibrate;
	}

	/** Disables or enables scale calibration. By default offset calibration is enabled.
	 * @param calibrate
	 * A True value means that the sensor is calibrated for offset
	 */
	public void calibrateForOffset(boolean calibrate) {
		calibrateForOffset = calibrate;
	}

	/**
	 * Sets the reference value of all elements in the sample to the same value.
	 * @param referenceValue
	 * The reference value to be used. 
	 */
	public void setReference(float referenceValue) {
		for (int i = 0; i < elements; i++)
			reference[i] = referenceValue;
	}

	/**Sets the reference value of all elements in the sample individually. <p>
	 * For calibrating a motionless accelerometer for offset this array could be {0, 0, 9.81f}
	 * as the sensor should not experience gravity over the X and Y axes and 1G (-9.81 m/s^2) over the Z axis.
	 * @param referenceValues
	 * An array with reference values.
	 */
	public void setReference(float[] referenceValues) {
		System.arraycopy(referenceValues, 0, reference, 0, elements);
	}

	/**
	 * Sets the expected range of all elements in the samples when calibrating for scale.
	 * For calibrating an accelerometer using the six way tumble method this value could be 19.62 m/s^2 (equals 2G). 
	 * @param rangeValue
	 * The range value to be used. 
	 */
	public void setRange(float rangeValue) {
		for (int i = 0; i < elements; i++)
			range[i] = rangeValue;
	}

	/**
	 * Sets the expected range of all elements in the samples individually when calibrating for scale.
	 * @param rangeValues
	 * The range value to be used. 
	 */
	public void setRange(float[] rangeValues) {
		System.arraycopy(rangeValues, 0, range, 0, elements);
	}
	
	@Override
	public void setTimeConstant(float timeConstant) {
		if (!calibrationMode) this.timeConstant=timeConstant;
		persistantTimeConstant=timeConstant;
	}

	/**
	 * Starts a calibration proces. 
	 * Resets collected minimum and maximum values. After starting calibration
	 * new minimum and maximum values are calculated on each fetched sample.
	 * From this updated offset and scale parameters are calculated. 
	 */
	public void startCalibration() {
		calibrationMode = true;
		timeConstant=persistantTimeConstant;
		for (int i = 0; i < elements; i++) {
			min[i] = Float.POSITIVE_INFINITY;
			max[i] = Float.NEGATIVE_INFINITY;
			offset[i]=0;
			scale[i]=1;
		}
		if (log != null) log.writeComment("Start Calibration");
	}

	/**
	 * Halts the process of updating calibration parameters.
	 */
	public void stopCalibration() {
		calibrationMode = false;
		timeConstant=0;
		if (log != null) log.writeComment("Stop Calibration");
	}

	/**
	 * Resumes the process of updating calibration parameters after a stop.
	 */
	public void resumeCalibration() {
		calibrationMode = true;
		timeConstant=persistantTimeConstant;
		if (log != null) log.writeComment("Resume Calibration");
	}

	/**
	 * Stores the calibration parameters, offset and/or scale depending on current settings,
	 * to a filterProperties file. Stored parameters can later be used by the CalibrateFilter.
	 * @param name
	 * A name to use for storing calibration parameters
	 */
	public void store(String name) {
		try {
			props.load(name);
			if (calibrateForOffset)
				props.setPropertyArray("offset", offset);
			if (calibrateForScale)
				props.setPropertyArray("scale", scale);
			props.store();
			if (log != null) log.writeComment("Store Calibration as " + name);

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load(String name) {
		try{
			props.load(name);
			offset=props.getPropertyArray("offset",offset);
			scale=props.getPropertyArray("scale",scale);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 *  Fetches a sample from the sensor and updates calibration parameters when the calibration process is running.
	 * Optionally outputs logging information to the NXTChartingLogger.
	 */
	@Override
	public void fetchSample(float[] dst, int off) {
		super.fetchSample(dst, off);
		if (!calibrationMode) {
		for (int i=0;i<elements;i++)
			dst[i+off]=(dst[i+off]-offset[i])/scale[i];
		}
		else {
			for (int i = 0; i < elements; i++) {
				if (min[i] > dst[i + off])
					min[i] = dst[i + off];
				if (max[i] < dst[i + off])
					max[i] = dst[i + off];
				if (calibrateForOffset)
					offset[i] = reference[i] + min[i] + (max[i] - min[i]) / 2;
				if (calibrateForScale)
					scale[i] = (max[i] - min[i]) / (range[i]);
			}
		if (log != null ) log(dst, off);
		}
	}
	
	/**
	 * Enables datalogging to a NXTChartingLogger
	 * @param log
	 * Specify null to disable logging.
	 */
	public void setNXTDataLogger(NXTDataLogger log) {
	this.log = log;
	if (log != null) {
		LogColumn[] columns=new LogColumn[elements*5];
		for (int i = 0; i < elements; i++) {
			columns[0+i*5]= new LogColumn("Sample "+i, LogColumn.DT_FLOAT);
			columns[1+i*5]= new LogColumn("Offset "+i, LogColumn.DT_FLOAT); 
			columns[2+i*5]= new LogColumn("Scale "+i, LogColumn.DT_FLOAT);
			columns[3+i*5]= new LogColumn("Minimum "+i, LogColumn.DT_FLOAT); 
			columns[4+i*5]= new LogColumn("Maximum "+i, LogColumn.DT_FLOAT);
		}
		log.setColumns(columns);
		}
	}
	
	private void log(float[] dst, int off) {
		for (int i = 0; i < elements; i++) {
			log.writeLog(dst[off + i]);
			log.writeLog(offset[i]);
			log.writeLog(scale[i]);
			log.writeLog((min[i] != Float.NEGATIVE_INFINITY) ? min[i] : 0);
			log.writeLog((max[i] != Float.POSITIVE_INFINITY) ? max[i] : 0);
		}
		log.finishLine();
	}
		

}
