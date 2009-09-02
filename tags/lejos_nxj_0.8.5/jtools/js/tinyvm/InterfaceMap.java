package js.tinyvm;

import java.io.IOException;
import java.util.logging.Logger;

import js.tinyvm.io.IByteWriter;

/**
 * This class provides a represntation of an interface map.
 * The map is used at run time to determine if a class implements a particular
 * interface. The map is stored per interface and consists of a bitmap with 1
 * bits for each class that implements the interface. The linker sorts the
 * classes to try and place classes that implement the same interfaces close
 * to each other. The map then takes advantage of this by using a sparse
 * implementation with a simple base and length positioning the actual variable
 * sized bitmap over the part of the class space that contains the required
 * classes.
 * @author andy
 */
public class InterfaceMap extends WritableDataWithOffset
{
   private ClassRecord interfaceRecord;
   private Binary iBinary;
   private int firstClass;
   private int lastClass;
   private int size;

   public InterfaceMap(Binary iBinary, ClassRecord crec) throws TinyVMException
   {
      if (!crec.isInterface())
         throw new TinyVMException("Attempt to create an interface map for a non interface class " + crec.iName);
      this.iBinary = iBinary;
      interfaceRecord = crec;
      findBounds();
   }

   /**
    * Search the list of implemnting classes to determine that upper and lower
     * bounds for the map.
    */
   private void findBounds()
   {
      // Search the list of implemnting classes to determine that upper and lower
      // bounds for the map
      lastClass = -1;
      firstClass = TinyVMConstants.MAX_CLASSES + 1;
      for(ClassRecord cr : interfaceRecord.iImplementedBy)
      {
         int index = iBinary.getClassIndex(cr);
         if (index > lastClass) lastClass = index;
         if (index < firstClass) firstClass = index;
      }
      if (lastClass < 0)
         size = 0;
      else
         size = lastClass - firstClass + 1;
      //System.out.println("Interface map for " + interfaceRecord.iName + " first " + firstClass + " last " + lastClass + " size " + size);
   }

   /**
    * Return the index of the first class implementing this interface.
    * @return index of the first class entry
    */
   public int getFirst()
   {
      return firstClass;
   }

   /**
    * Return the number of "live" bits in the map. Any class index that is
    * < firstClass or >= firstClass + size is not in the map.
    * @return number of live bits
    */
   public int getSize()
   {
      return size;
   }

   /**
    * return the number of bytes that make up this interface map.
    * @return size in bytes
    */
   public int getLength ()
   {

      return (size + 7)/8;
   }

   /**
    * Create the inetrface map, with one bit for each class that implements
    * this interface.
    *
    * @return array of bytes containing the map
    * @throws TinyVMException
    */
   private byte[] createMap() throws TinyVMException
   {
      byte [] map = new byte[getLength()];
      for(ClassRecord cr : interfaceRecord.iImplementedBy)
      {
         int index = iBinary.getClassIndex(cr) - firstClass;
         if (index >= size)
            throw new TinyVMException("Class index for " + cr.iName + " exceeds map size " + size + " for interface " + interfaceRecord.iName);
         map[index/8] |= 1 << (index % 8);
      }
      return map;
   }

   /**
    * Write the map to the executable file.
    * @param aOut access to the file writer
    * @throws TinyVMException
    */
   public void dump (IByteWriter aOut) throws TinyVMException
   {
      byte [] map = createMap();
      try
      {
         for(byte b : map)
            aOut.writeU1(b);
      }
      catch (IOException e)
      {
         throw new TinyVMException(e.getMessage(), e);
      }
   }

   private static final Logger _logger = Logger.getLogger("TinyVM");
}

