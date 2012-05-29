package lejos.pc.vision;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

/*
 * Flips image Horizontally @author Lawrie Griffiths
 */
public class FlipEffect extends VisionEffect
{
   public boolean flip = false;

   /**
    * Create flip effect
    */
   public FlipEffect ()
   {
      super();
   }

   /**
    * Flip the data in each line
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

      int lines = inData.length / lineStrideIn;
      int pixsPerLine = lineStrideIn / pixStrideIn;

      if (!flip)
      {
         System.arraycopy(inData, 0, outData, 0, inData.length);
         return BUFFER_PROCESSED_OK;
      }

      // Flip each line horizoontally

      byte[] buf = new byte[lineStrideIn];
      int pos = 0;

      for (int i = 0; i < lines; i++)
      {
         for (int j = 0; j < pixsPerLine; j++)
         {
            for (int k = 0; k < 3; k++)
               buf[lineStrideIn - (j * pixStrideIn) - 3 + k] = inData[pos
                  + (j * pixStrideIn) + k];
         }
         System.arraycopy(buf, 0, outData, pos, lineStrideIn);
         pos += lineStrideIn;
      }
      return BUFFER_PROCESSED_OK;
   }

   // methods for interface PlugIn

   /**
    * @return "Flip Effect"
    */
   public String getName ()
   {
      return "Flip Effect";
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
         controls[0] = new FlipControl(this);
      }
      return (Object[]) controls;
   }

}