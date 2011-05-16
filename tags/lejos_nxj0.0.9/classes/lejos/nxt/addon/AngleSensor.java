package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/** Supports the angle sensor of HiTechnic.
* This Java implementation was based on the NXC implementation on http://www.hitechnic.com/cgi-bin/commerce.cgi?preadd=action&key=NAA1030.
* Works clockwise, i.e. rotating clockwise increases angle value and rotating counter-clockwise decreases angle value.
*
* @author Michael Mirwaldt (epcfreak@gmail.com)<br/>
* date 2nd April 2011
*/

public class AngleSensor extends I2CSensor {
   
   protected final int HTANGLE_MODE_CALIBRATE = 0x43;
   protected final int HTANGLE_MODE_RESET = 0x52;

   public AngleSensor(I2CPort port, int address, int mode, int type) {
      super(port, address, mode, type);
   }

   public AngleSensor(I2CPort port, int mode) {
      super(port, mode);
   }

   public AngleSensor(I2CPort port) {
      super(port);
   }
   
   /** reads the current angle
    *
    * @return angle. Value lies between 0-359
    */
   public int getAngle() {
      byte buf[] = new byte[2];
      getData(0x42, buf, 2);
      int bits9to2 = buf[0];
      int bit1 = buf[1]&0x01;
      
      // prevent byte with value above 127 to become a negative number when it is converted to an integer
      if(bits9to2 < 0) {
         bits9to2+=256;
      }
      return bits9to2 * 2 + bit1;
   }
   
   /** @see #getAccAngle()
    *
    */
   public long getAccumulatedAngle() {
      return getAccAngle();
   }
   
   /** reads the current accumulated angle
    *
    * @return the current accumulated angle. Value lies between -2147483648 to 2147483647.
    */
   public long getAccAngle() {
      byte buf[] = new byte[4];
      getData(0x44, buf, 4);      
      int bits32to24 = buf[0];
      int bits23to18 = buf[1];
      int bits17to09 = buf[2];
      int bits08to01 = buf[3];
      
      // prevent bytes with values above 127 to become negative numbers when they are converted to integers
      if(bits32to24 < 0) {
          bits32to24+=256;
      }
      if(bits23to18 < 0) {
         bits23to18+=256;
      }
      if(bits17to09 < 0) {
         bits17to09+=256;
      }
      if(bits08to01 < 0) {
         bits08to01+=256;
      }
      
      return bits32to24*0x1000000 | bits23to18*0x10000 | bits17to09*0x100 | bits08to01;
   }
   
   /** reads the current rotations per minute
    *
    * @return current rotations per minute. Value lies between -1000 to 1000.
    */
   public int getRPM() {
      byte buf[] = new byte[2];
      getData(0x48, buf, 2);   
      int bits17to09 = buf[0];
      int bits08to01 = buf[1];
      
      // prevent byte with value above 127 to become a negative number when it is converted to integers
      if(bits08to01 < 0) {
         bits08to01+=256;
      }
      
      return bits17to09*0x100 | bits08to01;
   }
   
   
   /** @see #getAccAngle()
    *
    */
   public int getRotationsPerMinute() {
      return getRPM();
   }
   
   /** @see #resetAccAngle()
    *
    */
   public void resetAccumulatedAngle() {
      resetAccAngle();
   }
   
   
   /** Reset the rotation count of accumulated angle to zero. 
    * Not saved in EEPORM.
    */
   public void resetAccAngle() {
      sendData(0x41, (byte) HTANGLE_MODE_RESET);
   }
   
   /** Calibrate the zero position of angle.
    * Zero position is saved in EEPROM on sensor.
    * Thread sleeps for 50ms while that is done.
    */
   public void calibrateAngle() {
      sendData(0x41, (byte) HTANGLE_MODE_CALIBRATE);
      try {
         // Time to allow burning EEPROM
         Thread.sleep(50);
      } catch (InterruptedException e) {
         // ignore because it does not seem to cause trouble
      } 
   }
}
