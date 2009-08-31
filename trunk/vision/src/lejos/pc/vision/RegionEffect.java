package lejos.pc.vision;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

/*
 * Supports overlaying Region information over the video stream
 */
public class RegionEffect extends VisionEffect
{
   public boolean show = true;

   public RegionEffect ()
   {
      super();
   }

   public int process (Buffer inBuffer, Buffer outBuffer)
   {
      int outputDataLength = ((VideoFormat) outputFormat).getMaxDataLength();
      validateByteArraySize(outBuffer, outputDataLength);
      Region[] regions = Vision.getRegions();

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

      if (!show)
         return BUFFER_PROCESSED_OK;

      for (int i = 0; i < regions.length; i++)
      {
         if (regions[i] != null)
         {
            int rx = regions[i].getX();
            int ry = regions[i].getY();
            int rw = regions[i].getWidth();
            int rh = regions[i].getHeight();

            int offx = rx * pixStrideIn;
            int offy = ry * lineStrideIn;

            // System.out.println("rw = " + rw + ", rh = " + rh);

            int width = rw * pixStrideIn;
            int height = (rh - 1) * lineStrideIn;

            // Draw left side
            for (int j = 0; j < rh; j++)
            {
               outData[offx + offy + (j * lineStrideIn)] = (byte) 0;
               outData[offx + offy + (j * lineStrideIn) + 1] = (byte) 0;
               outData[offx + offy + (j * lineStrideIn) + 2] = (byte) 255;
            }

            // Draw right side
            for (int j = 0; j < rh; j++)
            {
               outData[offx + width + offy + (j * lineStrideIn) - 3] = (byte) 0;
               outData[offx + width + offy + (j * lineStrideIn) - 2] = (byte) 0;
               outData[offx + width + offy + (j * lineStrideIn) - 1] = (byte) 255;
            }

            // Draw the bottom
            for (int j = 0; j < rw; j++)
            {
               outData[offx + offy + (j * pixStrideIn)] = (byte) 0;
               outData[offx + offy + (j * pixStrideIn) + 1] = (byte) 0;
               outData[offx + offy + (j * pixStrideIn) + 2] = (byte) 255;
            }

            // Draw the top
            for (int j = 0; j < rw; j++)
            {
               outData[offx + offy + height + (j * pixStrideIn)] = (byte) 0;
               outData[offx + offy + height + (j * pixStrideIn) + 1] = (byte) 0;
               outData[offx + offy + height + (j * pixStrideIn) + 2] = (byte) 255;
            }

            // Write region number
            Font.println("" + (i + 1), Font.FONT_6x11, rx + 5, ry + 10,
               (byte) 255, (byte) 0, (byte) 0, outBuffer);
         }
      }

      return BUFFER_PROCESSED_OK;
   }

   // methods for interface PlugIn
   public String getName ()
   {
      return "Region Effect";
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
         controls[0] = new RegionControl(this);
      }
      return (Object[]) controls;
   }

}