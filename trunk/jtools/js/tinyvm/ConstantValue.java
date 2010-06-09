package js.tinyvm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.WriteAbortedException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.logging.Logger;

import js.tinyvm.io.IByteWriter;
import js.tinyvm.io.IOUtilities;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantClass;


/**
 * This class represents a constant value of a basic type.
 */
public class ConstantValue extends WritableDataWithOffset
{
   Binary iBinary;
   /**
    * The dereferenced value.
    */
   Object _value;

   /**
    * Constructor.
    * 
    * @param pool constant pool
    * @param constant constant
    * @param aBinary
    */
   public ConstantValue (ConstantPool pool, Constant constant, Binary aBinary)
   {
      iBinary = aBinary;
      _value = value(pool, constant);
      assert _value != null: "Postconditon: result != null";
   }

   public ConstantValue(ClassRecord crec, Binary aBinary)
   {
      iBinary = aBinary;
      _value = crec;
   }

   // use Object.equals() for equality
   // use Object.hashCode() for hash code

   /**
    * Dereferenced value.
    */
   public Object value ()
   {
      assert _value != null: "Postconditon: result != null";
      return _value;
   }

   /**
    * Get type of this value.
    */
   public int getTypeIndex ()
   {
      if (_value instanceof Double)
      {
         return TinyVMType.T_DOUBLE.type();
      }
      else if (_value instanceof Float)
      {
         return TinyVMType.T_FLOAT.type();
      }
      else if (_value instanceof Integer)
      {
         return TinyVMType.T_INT.type();
      }
      else if (_value instanceof Long)
      {
         return TinyVMType.T_LONG.type();
      }
      else if (_value instanceof String)
      {
         return this.checkOptimizedString((String) _value) ? iBinary.getClassIndex("java/lang/String") : iBinary.getClassIndex("[C");
      }
      else if (_value instanceof ClassRecord)
      {
         return TinyVMType.T_CLASS.type();
      }
      else
      {
         assert false: "Check: known type";
         return 0;
      }
   }
   
	private static byte[] getStringData(String str)
	{
		int len = str.length();
		byte[] r = new byte[len];
		for (int i=0; i<len; i++)
		{
			char c = str.charAt(i);
         assert c <= 0xff : "Invalid optimized string value";
			r[i] = (byte)c;
		}
		return r;
	}
   
   /**
    * Check to see if we can store this string in an optimized form.
    * @param str
    * @return
    */
   private boolean checkOptimizedString(String str)
   {
		int len = str.length();
      if (len >= TinyVMConstants.MAX_OPTIMIZED_STRING) return false;
		for (int i=0; i<len; i++)
			if (str.charAt(i) > 0xFF) return false;
      return true;
   }

   /**
    * Get length in bytes of value.
    */
   public int getLength ()
   {
      if (_value instanceof Double)
      {
         return 8;
      }
      else if (_value instanceof Float)
      {
         return 4;
      }
      else if (_value instanceof Integer)
      {
         return 4;
      }
      else if (_value instanceof Long)
      {
         return 8;
      }
      else if (_value instanceof String)
      {
         int len = ((String)_value).length();
         return this.checkOptimizedString((String)_value) ?  len: IOUtilities.adjustedSize(2 + 2 + 2 + 2 + (len*2), 4);
      }
      else if (_value instanceof ClassRecord)
      {
         return 1;
      }
      else
      {
         assert false: "Check: known type";
         return -1;
      }
   }

   /**
    * Returns the ideal data alignment for this data type.
    * @return the alignment in bytes.
    */
   public int getAlignment()
   {
       // alignment is the same as length, except for strings.
       if (_value instanceof String)
          // Note we cheat a little here, by specifying an 8 byte alignmant
          // for none optimized strings, event though they only require 4 byte
          // alignment. This ensures that we do not place strings in the
          // area that can be used for fast access to 4 byte constants.
          return this.checkOptimizedString((String)_value) ? 1 : 8;
       else
           return getLength();
   }

   /**
    * Dump.
    * 
    * @param writer byte writer
    */
   public void dump (IByteWriter writer) throws TinyVMException
   {
      assert writer != null: "Precondition: writer != null";

      try
      {
         // Constant values are dumped in system byte order.
         if (_value instanceof Double)
         {
            double doubleValue = ((Double) _value).doubleValue();
            long longValue = Double.doubleToLongBits(doubleValue);
            writer.writeU4((int)(longValue >> 32));
            writer.writeU4((int)(longValue & 0xffffffff));
         }
         else if (_value instanceof Float)
         {
            writer
               .writeU4(Float.floatToIntBits(((Float) _value).floatValue()));
         }
         else if (_value instanceof Integer)
         {
            writer.writeU4(((Integer) _value).intValue());
         }
         else if (_value instanceof Long)
         {
            long longValue = ((Long) _value).longValue();
            writer.writeU4((int)(longValue >> 32));
            writer.writeU4((int)(longValue & 0xffffffff));
         }
         else if (_value instanceof String)
         {
            if (this.checkOptimizedString((String) _value))
            {
               byte[] bytes = getStringData((String) _value);
               writer.write(bytes);
            }
            else
            {
               // non-optimized case, we write out a character array object
               int len = ((String)_value).length();
               int classIndex = iBinary.getClassIndex("[C");
               writer.writeU2(TinyVMConstants.ARRAY_HEADER | classIndex);
               writer.writeU2(0); // sync bytes
               writer.writeU2(len); // array length
               writer.writeU2(2); // offset to start of data
               for(int i = 0; i < len; i++)
                  writer.writeU2(((String)_value).charAt(i));
               IOUtilities.writePadding(writer, 4);
            }
         }
         else if (_value instanceof ClassRecord)
         {
            int pIdx = iBinary.getClassIndex(((ClassRecord) _value));
            writer.writeU1(pIdx);
         }
         else
         {
            assert false: "Check: known entry type";
         }
      }
      catch (IOException e)
      {
         throw new TinyVMException(e.getMessage(), e);
      }
   }

   //
   // protected interface
   //

   /**
    * Get value from constant.
    * 
    * @param pool constant pool
    * @param constant constant to get value from
    * @return Double, Float, Integer, Long or String
    */
   private Object value (ConstantPool pool, Constant constant)
   {
      assert pool != null: "Precondition: pool != null";
      assert constant != null: "Precondition: constant != null";
      
      Object result = null;
      if (constant instanceof ConstantDouble)
      {
         result = new Double(((ConstantDouble) constant).getBytes());
      }
      else if (constant instanceof ConstantFloat)
      {
         result = new Float(((ConstantFloat) constant).getBytes());
      }
      else if (constant instanceof ConstantInteger)
      {
         result = new Integer(((ConstantInteger) constant).getBytes());
      }
      else if (constant instanceof ConstantLong)
      {
         result = new Long(((ConstantLong) constant).getBytes());
      }
      else if (constant instanceof ConstantString)
      {
         result = new String(((ConstantString) constant).getBytes(pool));
      }
      else if (constant instanceof ConstantClass)
      {
         result = iBinary.getClassRecord(((ConstantClass)constant).getBytes(pool));
      }
      else
      {
         assert false: "Check: known type";
      }

      assert result != null: "Postconditon: result != null";
      return result;
   }

   public void markUsed()
   {
       if (_value instanceof ClassRecord)
       {
           ((ClassRecord)_value).markUsed();
       }
   }
   private static final Logger _logger = Logger.getLogger("TinyVM");
}

