package lejos.pc.vision;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

/*
 * Supports detecting colors and light level in regions @author Lawrie Griffiths
 */
public class ColorEffect extends VisionEffect
{
   private static final int INIT_PIXEL_THRESHOLD = 16;
   private static final int LIGHT_THRESHOLD = 192;
   public static final int MAX_PIXEL_THRESHOLD = 40;
   public static final int MIN_PIXEL_THRESHOLD = 0;
   public static final int PIXEL_THRESHOLD_INC = 4;
   public static final float INIT_PROPORTION = 0.25f;
   public static final float MAX_PROPORTION = 0.5f;
   public static final float MIN_PROPORTION = 0.05f;
   public static final float PROPORTION_INC = 0.05f;

   public int[] averageRed = new int[Region.MAX_REGIONS];
   public int[] averageGreen = new int[Region.MAX_REGIONS];
   public int[] averageBlue = new int[Region.MAX_REGIONS];

   public static int pixelThreshold = INIT_PIXEL_THRESHOLD;
   public static float requiredProportion = INIT_PROPORTION;

   /*
    * Detect colors and light in regions
    */
   public ColorEffect ()
   {
      super();
   }

   /*
    * Look for colors in regions, and if found, call the associated color
    * listener. Also looks for overall light value and calls associated LightSensor
    * Listener. Copies into to output.
    */
   public int process (Buffer inBuffer, Buffer outBuffer)
   {
      int outputDataLength = ((VideoFormat) outputFormat).getMaxDataLength();
      validateByteArraySize(outBuffer, outputDataLength);

      outBuffer.setLength(outputDataLength);
      outBuffer.setFormat(outputFormat);
      outBuffer.setFlags(inBuffer.getFlags());

      byte[] inData = (byte[]) inBuffer.getData();
      byte[] outData = (byte[]) outBuffer.getData();

      RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
      Dimension sizeIn = vfIn.getSize();

      int pixStrideIn = vfIn.getPixelStride();
      int lineStrideIn = vfIn.getLineStride();

      if (outData.length < sizeIn.width * sizeIn.height * 3)
      {
         System.out.println("the buffer is not full");
         return BUFFER_PROCESSED_FAILED;
      }

      System.arraycopy(inData, 0, outData, 0, inData.length);

      // Find the regions

      Region[] regions = Vision.getRegions();

      // Look for color listeners

      // Examine each non-null region

      int bestRegion = -1;
      int bestListener = -1;
      float bestProportion = -1;

      for (int i = 0; i < regions.length; i++)
      {
         if (regions[i] != null)
         {

            // Find the color listeners for this region

            ColorListener[] cl = regions[i].getColorListeners();
            int[] colors = regions[i].getColors();

            // Continue if no listeners

            if (cl.length == 0)
               continue;

            // Get region size

            int rx = regions[i].getX();
            int ry = regions[i].getY();
            int width = regions[i].getWidth();
            int height = regions[i].getHeight();

            // Look for the color associated with each listener

            for (int j = 0; j < cl.length; j++)
            {

               // Separate R, G, B values

               int r = (colors[j] >> 16) & 0xFF;
               int g = (colors[j] >> 8) & 0xFF;
               int b = colors[j] & 0xFF;

               // System.out.println("Region " + (i+1) + " looking for " +
               // Integer.toHexString(colors[j]));

               int pixCount = 0, totalPixs = 0;
               int aR = 0, aG = 0, aB = 0;

               // Examine each pixel in the region

               for (int ii = ry; ii < ry + height; ii++)
               {
                  for (int jj = rx; jj < rx + width; jj++)
                  {
                     int pos = ii * lineStrideIn + jj * pixStrideIn;

                     int tr = inData[pos + 2] & 0xFF;
                     int tg = inData[pos + 1] & 0xFF;
                     int tb = inData[pos] & 0xFF;

                     // Keep running totals for average values for R, G, and B
                     // for the region

                     aR += tr;
                     aG += tg;
                     aB += tb;

                     totalPixs++;

                     // Count the pixel if it is within the threshold for R, G,
                     // and B values

                     if (Math.abs(tr - r) <= pixelThreshold
                        && Math.abs(tg - g) <= pixelThreshold
                        && Math.abs(tb - b) <= pixelThreshold)
                     {
                        pixCount++;
                     }
                  }
               }
               // System.out.println("Region " + (i+1) + " matched " + pixCount
               // + " out of " + totalPixs);

               // Calculate the average R, G and B values for the region

               averageRed[i] = aR / totalPixs;
               averageGreen[i] = aG / totalPixs;
               averageBlue[i] = aB / totalPixs;

               if (Vision.captureColor)
                  System.out.println("Color = " + aR / totalPixs + " , " + aG
                     / totalPixs + " , " + aB / totalPixs);

               // Find the best color match

               float thisProportion = (float) pixCount / (float) totalPixs;

               if (thisProportion > bestProportion
                  && thisProportion > requiredProportion)
               {
                  bestProportion = thisProportion;
                  bestRegion = i;
                  bestListener = j;
               }
            }
         }
      }

      // Call the best Color Listener

      if (bestRegion >= 0)
      {
         regions[bestRegion].getColorListeners()[bestListener].colorDetected(
            bestRegion + 1, regions[bestRegion].getColors()[bestListener]);
      }

      // Look for light listeners

      bestRegion = -1;
      bestListener = -1;
      bestProportion = -1;

      // Examine each non-null region

      for (int i = 0; i < regions.length; i++)
      {
         if (regions[i] != null)
         {

            // Get the light listeners

            LightListener[] ll = regions[i].getLightListeners();

            // If no light listeners, continue

            if (ll.length == 0)
               continue;

            // Get the region dimensions

            int rx = regions[i].getX();
            int ry = regions[i].getY();
            int width = regions[i].getWidth();
            int height = regions[i].getHeight();

            // Examine each pixel

            for (int j = 0; j < ll.length; j++)
            {

               int pixCount = 0, totalPixs = 0;

               for (int ii = ry; ii < ry + height; ii++)
               {
                  for (int jj = rx; jj < rx + width; jj++)
                  {
                     int pos = ii * lineStrideIn + jj * pixStrideIn;

                     int tr = inData[pos + 2] & 0xFF;
                     int tg = inData[pos + 1] & 0xFF;
                     int tb = inData[pos] & 0xFF;

                     totalPixs++;

                     // Count the pixel if each of R, G and B is above the
                     // threshold
                     if (tr >= LIGHT_THRESHOLD && tg >= LIGHT_THRESHOLD
                        && tb >= LIGHT_THRESHOLD)
                     {
                        pixCount++;
                     }
                  }
               }

               // System.out.println("Matched " + pixCount + " out of " +
               // totalPixs);

               // Find the best Listener

               float thisProportion = (float) pixCount / (float) totalPixs;
               // if (thisProportion > requiredProportion)
               //   System.out.println("Region " + (i+1) + " proportion = " +
               // thisProportion);
               if (thisProportion > bestProportion
                  && thisProportion > requiredProportion)
               {
                  bestProportion = thisProportion;
                  bestRegion = i;
                  bestListener = j;
               }
            }
         }
      }

      // Call all the light listeners for the best region

      if (bestRegion > 0)
      {
         LightListener[] ll = regions[bestRegion].getLightListeners();
         for (int i = 0; i < ll.length; i++)
            ll[i].lightDetected(bestRegion + 1);
      }

      return BUFFER_PROCESSED_OK;
   }

   /*
    * Get the name of the Effect @return "Color Effect"
    */
   public String getName ()
   {
      return "Color Effect";
   }

   private Control[] controls;

   /**
    * Getter for array on one control for adjusing motion threshold and setting
    * debug.
    * 
    * @return an array of one ColorDetectionControl
    */
   public Object[] getControls ()
   {
      if (controls == null)
      {
         controls = new Control[1];
         controls[0] = new ColorDetectionControl(this);
      }
      return (Object[]) controls;
   }

}