package lejos.pc.vision;

/*
 * Author: Konrad Rzeszutek <konrad@darnok.org>
 * 
 * This code is freely distributed. You can use as you want.
 */

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

/**
 * Motion detection effect.
 * 
 * The engine has two steps. First the input image and the reference image
 * (which is the image from the previous frame) is compared. Whenever a pixel
 * has changed consideribly (determined by the <b>threshold </b> variable) a
 * internal black-white-red image is marked (at the same exact location where
 * the change occured). Therefore in the first step, the internal
 * black-white-red image is has lit up clusters in the space where a change has
 * occured.
 * <p>
 * The next step is to eliminate these clusters that are too small, but still
 * appeared in our black-white-red image. Only the big clusters are left (and
 * are colored red). During this process we keep a track of couunt of the big
 * clusters. If the count is greater than <b>blob_threshold </b> then the input
 * frame is determined to have consideribly motion as to the previous frame.
 * 
 * <br>
 * <br>
 * Many of the ideas have been taken from
 * <ol>
 * <li><a href="http://gspy.sourceforge.net/">A Gnome Security Camera </a> by
 * Lawrance P. Glaister.
 * <li>Digital Image Processing by Kenneth R. Castleman; ISBN 0-13-211467-4
 * <li>Computer Graphics Principles and Practice by Foley, van Dam, Feiner,
 * Hughes; ISBN 0-201-84840-6
 * <li>Java Media Format Sample Programs (mainly the one dealing with building
 * an Effect plugin)
 * </ol>
 * 
 * <br>
 * <br>
 * 
 * @author : Konrad Rzeszutek <konrad@darnok.org>
 * 
 * Modified by Lawrie Griffiths to fit in with lejos vision system.
 */
public class MotionDetectionEffect extends VisionEffect
{

   private int[] blobCount = new int[Region.MAX_REGIONS];

   /**
    * Optimization. Anything above 0 turns it on. By default its disabled.
    */
   public int OPTIMIZATION = 0;

   /**
    * Maximum threshold setting. Setting the threshold above this means to get
    * the motion detection to pass the frame you pretty much have to full the
    * whole frame with lots of motions (ie: drop the camera)
    */
   public int THRESHOLD_MAX = 10000;

   /**
    * By what value you should increment.
    */
   public int THRESHOLD_INC = 1000;

   /**
    * The initial threshold setting.
    */
   public int THRESHOLD_INIT = 1000;

   private byte[] refData;
   private byte[] bwData;

   private int avg_ref_intensity;
   private int avg_img_intensity;

   /**
    * The threshold for determing if the pixel at a certain location has changed
    * consideribly.
    */
   public int threshold = 10;

   /**
    * Our threshold for determinig if the input image has enough motion.
    *  
    */
   public int blob_threshold = THRESHOLD_INIT;

   /**
    * Turn debugging on. Slows down the effect but shows how motion detection
    * effect works.
    */
   public boolean debug = false;

   /**
    * Initialize the Motion effect plugin.
    */
   public MotionDetectionEffect ()
   {
      super();
   }

   /**
    * Process the image, detecting motion in the regions
    * 
    * @param inBuffer the input Buffer
    * @param outBuffer the output Buffer
    * @return BUFFER_PROCESSED_OK or BUFFER_PROCESSED_FAILED
    */
   public int process (Buffer inBuffer, Buffer outBuffer)
   {
      /*
       * Optimization ideas:
       *  - first scale down the image. - convert the image to an int[][] array
       * (instead of using byte[][])
       *  - then do all the calculation on int[][] array instead of masking the
       * bits.
       * 
       * Furthermore, only do the comparison every 5 frames instead of every
       * frame.
       */

      // Validate and create the necessary buffers
      int outputDataLength = ((VideoFormat) outputFormat).getMaxDataLength();

      validateByteArraySize(outBuffer, outputDataLength);

      outBuffer.setLength(outputDataLength);
      outBuffer.setFormat(outputFormat);
      outBuffer.setFlags(inBuffer.getFlags());

      // Get the data portion of the buffers

      byte[] inData = (byte[]) inBuffer.getData();
      byte[] outData = (byte[]) outBuffer.getData();

      // Get the input format

      RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
      Dimension sizeIn = vfIn.getSize();

      // Get the stride lengths

      int pixStrideIn = vfIn.getPixelStride();
      int lineStrideIn = vfIn.getLineStride();

      int r, g, b;
      int ip, op;
      byte result;
      int avg = 0;
      int refDataInt = 0;
      int inDataInt = 0;
      int correction;

      // If a snapshot has been requested, write the JPEG image to the selected
      // file

      if (Vision.takeSnapshot())
      {
         try
         {
            Vision.writeImage(Vision.snapshotFilename, inData,
               Vision.imageWidth, Vision.imageHeight);
         }
         catch (Exception e)
         {
            System.out.println("Failed to take snapshot");
         }
         finally
         {
            Vision.setSnapshot(false);
         }
      }

      // If we have no reference data, create it, copy input to output
      // and don't attempt to detect motion

      if (refData == null)
      {
         refData = new byte[outputDataLength];
         bwData = new byte[outputDataLength];

         System.arraycopy(inData, 0, refData, 0, inData.length);
         System.arraycopy(inData, 0, outData, 0, inData.length);

         for (ip = 0; ip < outputDataLength; ip++)
         {
            avg += (int) (refData[ip] & 0xFF);
         }

         avg_ref_intensity = avg / outputDataLength;
         return BUFFER_PROCESSED_OK;
      }

      // Check the output buffer

      if (outData.length < sizeIn.width * sizeIn.height * 3)
      {
         System.out.println("the buffer is not full");
         return BUFFER_PROCESSED_FAILED;
      }

      // Calculate the average intensity

      for (ip = 0; ip < outputDataLength; ip++)
      {
         avg += (int) (inData[ip] & 0xFF);
      }

      avg_img_intensity = avg / outputDataLength;

      // Calculate the correction factor as the absolute value of
      // the difference between the image and the reference integrity

      correction = (avg_ref_intensity < avg_img_intensity)? avg_img_intensity
         - avg_ref_intensity : avg_ref_intensity - avg_img_intensity;

      //      System.out.println(avg_ref_intensity + "; "+avg_img_intensity+" =
      // "+correction);
      //

      avg_ref_intensity = avg_img_intensity;
      ip = op = 0;

      /**
       * Compare the reference frame with the new frame. We lite up only the
       * pixels which changed, the rest are discarded (on the b/w image - used
       * for determing the motion)
       *  
       */

      for (int ii = 0; ii < outputDataLength / pixStrideIn; ii++)
      {

         refDataInt = (int) refData[ip] & 0xFF;
         inDataInt = (int) inData[ip++] & 0xFF;
         r = (refDataInt > inDataInt)? refDataInt - inDataInt : inDataInt
            - refDataInt;

         refDataInt = (int) refData[ip] & 0xFF;
         inDataInt = (int) inData[ip++] & 0xFF;
         g = (refDataInt > inDataInt)? refDataInt - inDataInt : inDataInt
            - refDataInt;

         refDataInt = (int) refData[ip] & 0xFF;
         inDataInt = (int) inData[ip++] & 0xFF;
         b = (refDataInt > inDataInt)? refDataInt - inDataInt : inDataInt
            - refDataInt;

         // intensity normalization

         r -= (r < correction)? r : correction;
         g -= (g < correction)? g : correction;
         b -= (b < correction)? b : correction;

         result = (byte) (java.lang.Math
            .sqrt((double) ((r * r) + (g * g) + (b * b)) / 3.0));

         // black/white image now.

         if (result > (byte) threshold)
         {
            bwData[op++] = (byte) 255;
            bwData[op++] = (byte) 255;
            bwData[op++] = (byte) 255;
         }
         else
         {
            bwData[op++] = (byte) result;
            bwData[op++] = (byte) result;
            bwData[op++] = (byte) result;
         }
      }

      // Now eliminate insignificant blobs and count how many
      // there are in each region

      Region[] regions = Vision.getRegions();

      for (int i = 0; i < regions.length; i++)
         blobCount[i] = 0;

      // blob elimination

      for (op = lineStrideIn + 3; op < outputDataLength - lineStrideIn - 3; op += 3)
      {
         for (int i = 0; i < 1; i++)
         {
            if (((int) bwData[op + 2] & 0xFF) < 255)
               break;
            if (((int) bwData[op + 2 - lineStrideIn] & 0xFF) < 255)
               break;
            if (((int) bwData[op + 2 + lineStrideIn] & 0xFF) < 255)
               break;
            if (((int) bwData[op + 2 - 3] & 0xFF) < 255)
               break;
            if (((int) bwData[op + 2 + 3] & 0xFF) < 255)
               break;
            if (((int) bwData[op + 2 - lineStrideIn + 3] & 0xFF) < 255)
               break;
            if (((int) bwData[op + 2 - lineStrideIn - 3] & 0xFF) < 255)
               break;
            if (((int) bwData[op + 2 + lineStrideIn - 3] & 0xFF) < 255)
               break;
            if (((int) bwData[op + 2 + lineStrideIn + 3] & 0xFF) < 255)
               break;
            bwData[op] = (byte) 0;
            bwData[op + 1] = (byte) 0;
            int yy = (op / lineStrideIn);
            int xx = (op % lineStrideIn) / pixStrideIn;

            for (int j = 0; j < Region.MAX_REGIONS; j++)
            {
               if (regions[j] != null && regions[j].inRegion(xx, yy))
               {
                  blobCount[j]++;
               }
            }
         }
      }

      // Call Motion Listeners for regions whose blob count
      // exceeds the current threshold

      for (int i = 0; i < Region.MAX_REGIONS && regions[i] != null; i++)
      {
         // System.out.println("Region " + i + ", count = " + blobCount[i]);

         if (blobCount[i] > blob_threshold)
         {

            // System.out.println("Motion detected in region " + i);

            // Call the motion listeners for this region

            MotionListener[] ml = regions[i].getMotionListeners();

            for (int j = 0; j < ml.length; j++)
               ml[j].motionDetected(i + 1);

            // If in debug mode split the screen into 4 and show the original
            // picture, the reference picture and the blobs detected

            if (debug)
            {
               sample_down(inData, outData, 0, 0, sizeIn.width, sizeIn.height,
                  lineStrideIn, pixStrideIn);
               Font.println("original picture", Font.FONT_8x8, 0, 0,
                  (byte) 255, (byte) 255, (byte) 255, outBuffer);
               sample_down(refData, outData, 0, sizeIn.height / 2,
                  sizeIn.width, sizeIn.height, lineStrideIn, pixStrideIn);
               Font.println("reference picture", Font.FONT_8x8, 0,
                  sizeIn.height, (byte) 255, (byte) 255, (byte) 255, outBuffer);
               sample_down(bwData, outData, sizeIn.width / 2, 0, sizeIn.width,
                  sizeIn.height, lineStrideIn, pixStrideIn);
               Font.println("motion detection pic", Font.FONT_8x8,
                  sizeIn.width / 2, 0, (byte) 255, (byte) 255, (byte) 255,
                  outBuffer);
            }
            else
            {

               // Otherwise copy the input to the output

               System.arraycopy(inData, 0, outData, 0, inData.length);
            }

            // Whether debug or not make the current picture the new reference
            // picture

            System.arraycopy(inData, 0, refData, 0, inData.length);

            return BUFFER_PROCESSED_OK;
         }
      }

      // If no motion detected, just copy the input to the output

      System.arraycopy(inData, 0, outData, 0, inData.length);

      return BUFFER_PROCESSED_OK;
   }

   // methods for interface PlugIn

   /**
    * get the name of the effect
    * 
    * @return "Motion Detection Effect"
    */
   public String getName ()
   {
      return "Motion Detection Effect";
   }

   private Control[] controls;

   /**
    * Getter for array on one control for adjusing motion threshold and setting
    * debug.
    * 
    * @return an array of one MotionDetectionControl
    */
   public Object[] getControls ()
   {
      if (controls == null)
      {
         controls = new Control[1];
         controls[0] = new MotionDetectionControl(this);
      }
      return (Object[]) controls;
   }

   // Utility methods.

   /**
    * Reduce the data to one quarter size
    */
   void sample_down (byte[] inData, byte[] outData, int X, int Y, int width,
      int height, int lineStrideIn, int pixStrideIn)
   {
      int p1, p2, p3, p4, op, y;

      for (y = 0; y < (height / 2); y++)
      {
         p1 = (y * 2) * lineStrideIn; // upper left cell
         p2 = p1 + pixStrideIn; // upper right cell
         p3 = p1 + lineStrideIn; // lower left cell
         p4 = p3 + pixStrideIn; // lower right cell
         op = lineStrideIn * y + (lineStrideIn * Y) + (X * pixStrideIn);
         for (int i = 0; i < (width / 2); i++)
         {
            outData[op++] = (byte) (((int) (inData[p1++] & 0xFF)
               + ((int) inData[p2++] & 0xFF) + ((int) inData[p3++] & 0xFF) + ((int) inData[p4++] & 0xFF)) / 4); // blue
                                                                                                                // cells
                                                                                                                // avg
            outData[op++] = (byte) (((int) (inData[p1++] & 0xFF)
               + ((int) inData[p2++] & 0xFF) + ((int) inData[p3++] & 0xFF) + ((int) inData[p4++] & 0xFF)) / 4); // blue
                                                                                                                // cells
                                                                                                                // avg
            outData[op++] = (byte) (((int) (inData[p1++] & 0xFF)
               + ((int) inData[p2++] & 0xFF) + ((int) inData[p3++] & 0xFF) + ((int) inData[p4++] & 0xFF)) / 4); // blue
                                                                                                                // cells
                                                                                                                // avg
            p1 += 3;
            p2 += 3;
            p3 += 3;
            p4 += 3;
         }
      }
   }
}