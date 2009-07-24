package js.tinyvm;

import java.io.IOException;
import java.util.logging.Logger;

import js.tinyvm.io.IByteWriter;

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
   public TinyVMType getType ()
   {
      if (_value instanceof Double)
      {
         return TinyVMType.T_DOUBLE;
      }
      else if (_value instanceof Float)
      {
         return TinyVMType.T_FLOAT;
      }
      else if (_value instanceof Integer)
      {
         return TinyVMType.T_INT;
      }
      else if (_value instanceof Long)
      {
         return TinyVMType.T_LONG;
      }
      else if (_value instanceof String)
      {
         return TinyVMType.T_OBJECT;
      }
      else if (_value instanceof ClassRecord)
      {
         return TinyVMType.T_CLASS;
      }
      else
      {
         assert false: "Check: known type";
         return null;
      }
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
         return ((String) _value).getBytes().length;
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
       // alignment is the same as length, except for strings which are byte
       // aligned.
       if (_value instanceof String)
           return 1;
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
            byte[] bytes = ((String) _value).getBytes();
            writer.write(bytes);
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

