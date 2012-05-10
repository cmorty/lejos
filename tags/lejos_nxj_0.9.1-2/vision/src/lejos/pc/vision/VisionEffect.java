package lejos.pc.vision;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.format.RGBFormat;

/**
 * Abstract Effect that specific Vision Effects inherit from
 * 
 * @author Lawrie Griffiths
 */
public abstract class VisionEffect implements Effect
{

   protected Format inputFormat;
   protected Format outputFormat;
   protected Format[] inputFormats;
   protected Format[] outputFormats;

   /**
    * Create the Effect. Only 24-bit color is supported.
    */
   public VisionEffect ()
   {
      inputFormats = new Format[]
      {
         new RGBFormat(null, Format.NOT_SPECIFIED, Format.byteArray,
            Format.NOT_SPECIFIED, 24, 3, 2, 1, 3, Format.NOT_SPECIFIED,
            Format.TRUE, Format.NOT_SPECIFIED)
      };

      outputFormats = new Format[]
      {
         new RGBFormat(null, Format.NOT_SPECIFIED, Format.byteArray,
            Format.NOT_SPECIFIED, 24, 3, 2, 1, 3, Format.NOT_SPECIFIED,
            Format.TRUE, Format.NOT_SPECIFIED)
      };
   }

   // Methods for interface codec

   /**
    * Get the supported input formats
    * 
    * @return the supported input formats
    */
   public Format[] getSupportedInputFormats ()
   {
      return inputFormats;
   }

   /**
    * Get the supported output formats that matches the input format
    * 
    * @return the supported output formats
    */
   public Format[] getSupportedOutputFormats (Format input)
   {
      if (input == null)
         return outputFormats;

      if (matches(input, inputFormats) != null)
      {
         return new Format[]
         {
            outputFormats[0].intersects(input)
         };
      }
      else
      {
         return new Format[0];
      }
   }

   /**
    * Set the input format.
    * 
    * @param input the required input format
    * @return the input format
    */
   public Format setInputFormat (Format input)
   {
      inputFormat = input;
      return input;
   }

   /**
    * Set the output format. Ensures size and line stride are in the expected
    * 24-bit 3-byte stride format.
    * 
    * @param output the output format
    * @return the output format
    */
   public Format setOutputFormat (Format output)
   {
      if (output == null || matches(output, outputFormats) == null)
         return null;
      RGBFormat incoming = (RGBFormat) output;

      Dimension size = incoming.getSize();
      int maxDataLength = incoming.getMaxDataLength();
      int lineStride = incoming.getLineStride();
      float frameRate = incoming.getFrameRate();

      if (size == null)
         return null;

      if (maxDataLength < size.width * size.height * 3)
         maxDataLength = size.width * size.height * 3;

      if (lineStride < size.width * 3)
         lineStride = size.width * 3;

      outputFormat = outputFormats[0].intersects(new RGBFormat(size,
         maxDataLength, null, frameRate, Format.NOT_SPECIFIED,
         Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
         Format.NOT_SPECIFIED, lineStride, Format.NOT_SPECIFIED,
         Format.NOT_SPECIFIED));

      return outputFormat;
   }

   /**
    * Does nothing
    */
   public void open ()
   {}

   /**
    * Does nothing
    */
   public void close ()
   {}

   /**
    * Does nothing
    */
   public void reset ()
   {}

   // methods for interface javax.media.Controls

   /**
    * Returns null
    * 
    * @return null
    */
   public Object getControl (String controlType)
   {
      return null;
   }

   /**
    * Returns null
    * 
    * @return null
    */

   public Object[] getControls ()
   {
      return null;
   }

   // Utility methods.

   /**
    * Select the first output format that matches the input format.
    * 
    * @return first matching output format ot null if none match
    */
   protected Format matches (Format in, Format outs[])
   {
      for (int i = 0; i < outs.length; i++)
      {
         if (in.matches(outs[i]))
            return outs[i];
      }
      return null;
   }

   /**
    * Validate that the Buffer conforms to the expected format, and create a new
    * byte array if not.
    */
   protected byte[] validateByteArraySize (Buffer buffer, int newSize)
   {
      Object objectArray = buffer.getData();
      byte[] typedArray;

      if (objectArray instanceof byte[])
      { // is correct type AND not null
         typedArray = (byte[]) objectArray;
         if (typedArray.length >= newSize)
         { // is sufficient capacity
            return typedArray;
         }

         byte[] tempArray = new byte[newSize]; // re-alloc array
         System.arraycopy(typedArray, 0, tempArray, 0, typedArray.length);
         typedArray = tempArray;
      }
      else
      {
         typedArray = new byte[newSize];
      }

      buffer.setData(typedArray);
      return typedArray;
   }
}