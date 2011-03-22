package lejos.nxt.addon;
   
import lejos.nxt.ADSensorPort;
import lejos.nxt.SensorConstants;
import lejos.util.Delay;
 
/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

 /**
  * This class provides basic raw HiTechnic gyroscope sensor read functionality and implements a continuous sampling bias calculator 
  * to provide a bias (offset) value for the sensor readings to be able to establish a zero rate output when the sensor is stationary. The
  * gyroscope's nominal zero angular rate output is ~620 which must be offset to produce zero rate output when we have no angular velocity 
  * rate change (i.e.: static, not moving).
  * <P>
  * This nominal "zero" (stationary/static) output value will vary slightly over time (due to environmental influences, 
  * heat affecting internal resistance, etc.) so drift of the nominal zero rate output value will occur. The continual bias 
  * calculator compensates for this change.
  * <P>
  * The <tt>{@link #readValue}</tt> method implements a bias sampling algorithm to calculate the zero rate  
  * bias and subtracts that from the raw reading returning a biased sensor value. The <tt>{@link #readRawValue}</tt> method
  * returns the raw gyro rate output in the range of 0-1023 (10 bits).
  * <P>
  * There is a 1 second pause during the instantiation of this class so the initial nominal bias offset value can be sampled and calculated. 
  * Ensure that the sensor is static during this time or no bias will be determined and the <tt>readValue()</tt> method will return 
  * nondeterministic results.
  * 
  * <h3>Assumptions:</h3>
  * <ul>
  * <li>The HiTechnic Gyro sensor NGY1044 (or equivalent) is being used. (<a href="http://www.hitechnic.com/" target=-"_blank">
  * http://www.hitechnic.com/</a>)
  * </ul>
  * @author Lawrie Griffiths 
  * @author Kirk P. Thompson - 3/11/2011 &lt;lejos@mosen.net&gt;  
  */
 public class GyroSensor implements SensorConstants{
     private final int BIAS_SAMPLES = 100; // the number of consecutive bias samples to be "close" to decide to do bias calc
     private final int VALUECOUNT_ARRAY_SIZE = 6;
     
     private ADSensorPort port;
     private short biasClosenessHit = 0;
     private int sensorBias=0;
     private int sensorBiasRoot;
     private boolean setBias = true;
     private boolean dirtyArray = false;
     
     private class ValueCount {
         public int value, count;
         ValueCount(int value, int count){
             this.value = value;
             this.count = count;
         }
     }
     private ValueCount[] valueCount = new ValueCount[VALUECOUNT_ARRAY_SIZE];
     
     /**
      * Creates and initializes a  new <code>GyroSensor</code> bound to passed <code>ADSensorPort</code>.
      * 
      * @param port The <code>ADSensorPort</code> the Gyro is connected to
      * @see lejos.nxt.ADSensorPort
      */
     public GyroSensor(ADSensorPort port) {
         this.port = port;
         port.setTypeAndMode(TYPE_CUSTOM, MODE_RAW);
         // get that sensor started and set the bias sample window
         Delay.msDelay(1000);
         initVCArray(readRawValue());
     }

     // return the index for given value
     private int getVCIndex(int value){
         for (int i=0;i<valueCount.length;i++){
             if (valueCount[i].value==value) return i;
         }
         return -1;
     }
     
     // return index for the element with the least weight. Choses first of equals.
     private int getVCIndexLeastCount(){
         int count=Integer.MAX_VALUE;
         int index=0;
         for (int i=0;i<valueCount.length;i++){
             if (valueCount[i].count<count) {
                 count=valueCount[i].count;
                 index=i;
             }
         }
         return index;
     }

     // return index for the element with the most weight. Choses first of equals.
     private int getVCIndexMostCount(){
         int count=Integer.MIN_VALUE;
         int index=0;
         for (int i=0;i<valueCount.length;i++){
             if (valueCount[i].count>count) {
                 count=valueCount[i].count;
                 index=i;
             }
         }
         return index;
     }
     
     private void initVCArray(int value){
         value-=VALUECOUNT_ARRAY_SIZE/2;
         for (int i=0;i<valueCount.length;i++){
             // if the array is not initialized, set consecutive values using the passed value as the midpoint
             if (valueCount[i]==null) {
                 valueCount[i] = new ValueCount(value++, 0);
             } else { //...otherwise, just zero the counts for each defined value
                 valueCount[i].count=0;
             }
         }
         dirtyArray = false;
     }
     
     // add a hit count to the tracking array. If element value doesn't exist, replace the least used one with it.
     private int addValueCount(int value){
         // get index of matching value
         int index = getVCIndex(value);
         // if no match, get index value with the least counts
         if (index==-1) index=getVCIndexLeastCount();
         valueCount[index].value=value;
         valueCount[index].count++;
         dirtyArray = true;
         return index;
     }
     
     private void initBias(int value){
         sensorBiasRoot = value;        
         sensorBias = 0;
         initVCArray(value);
     }

     /** Initialize the bias value. Ensure the sensor is stationary for 1 second during calling this.
      * @see #getBias()
      */
     public void initBias(){
         setBias = true;
     }
     
     /**
      * Read the unmodified gyro rate output value.
      * 
      * @return the un-biased raw gyro value 0-1023
      */
     public int readRawValue(){
         return port.readRawValue();
      }
      
     /**
      * Read the gyro value with bias (offset) applied. 
      * 
      * @return The gyro value with the bias applied
      */
     public int readValue(){
         // get a gyro raw value and...
         int sensorVal = readRawValue();
         // if flagged, use it as the root bias reference (assume that the gyro is stationary) for fuzzy checks later on
         if (setBias) {
             setBias = false;
             initBias(sensorVal);
             sensorBiasRoot=sensorVal;
         }
         
         // If the reading/sensor average is close to the bias baseline, get samples for new bias value if stationary.
         // This method allows the bias value to automatically adjusted when the sensor heats up, gets humid, etc. which causes
         // the nominal "zero" rate output value to drift.
         if (fuzzyTest(sensorVal, sensorBiasRoot, 1)) {
             addValueCount(sensorVal);
             biasClosenessHit++;
             // if we have BIAS_SAMPLES (100) consecutive close vals @ 1 sample per 3ms, assume we are stationary/static. I did lots of testing and
             // am confident that this will work for all land-based vehicles. Even the tinest bump will exceed the closeness test
             // so the assumption of tiny reading variances such as inherent bias changes are accounted for. KPT 3/11/11
             if (biasClosenessHit>BIAS_SAMPLES) {
                 // get the bias value that weighs the most (has the most hits)
                 sensorBias = valueCount[getVCIndexMostCount()].value;
                 // set the root so we can use it to compare against
                 sensorBiasRoot = sensorBias;
                 // we have all our consecutive samples, reset for next round of sampling
                 biasClosenessHit = 0;
                 // zero the counts
                 initVCArray(0);
             }
             // replace the sensorVal so the rate output return is zero
             sensorVal = sensorBias;
         } else {
             // break in "consecutiveness" so we reset the counter. Any bump, movement of the sensor will cause this
             biasClosenessHit = 0;
             // if not already, zero the counts
             if (dirtyArray) initVCArray(0);
         }
         
         // return rate output minus bia/offset
         return sensorVal - sensorBias;
     }
     
     /**
      * Return the current nominal "zero-rate" bias (offset) being used.
      * 
      * @return The calculated bias value
      * @see #initBias()
      */
     public int getBias(){
             return sensorBias;
     }
     
     private boolean fuzzyTest(int testVal, int targetVal, int fuzzyVal) {
         return testVal >= (targetVal - fuzzyVal) && testVal <= (targetVal + fuzzyVal) ? true : false;
     }
 }
