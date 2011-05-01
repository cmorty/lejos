package js.tinyvm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;

import js.tinyvm.io.IByteWriter;
import js.tinyvm.io.IOUtilities;
import js.tinyvm.util.HashVector;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

/**
 * Abstraction for a class record (see vmsrc/language.h).
 */
public class ClassRecord implements WritableData
{
   int iIndex = -1;
   String iName;
   /**
    * On-demand size of the class.
    */
   int iClassSize = -1;
   JavaClass iCF;
   Binary iBinary;
   RecordTable<MethodRecord> iMethodTable = new RecordTable<MethodRecord>("methods", false, false);
   RecordTable<MethodRecord> iIntMethodTable = null;
   final RecordTable<InstanceFieldRecord> iInstanceFields = new RecordTable<InstanceFieldRecord>("instance fields", true,
      false);
   final HashMap<String, StaticValue> iStaticValues = new HashMap<String, StaticValue>();
   final HashMap<String, StaticFieldRecord> iStaticFields = new HashMap<String, StaticFieldRecord>();
   HashMap<Signature, MethodRecord> iMethods = new HashMap<Signature, MethodRecord>();
   final ArrayList<String> iUsedMethods = new ArrayList<String>();
   InterfaceMap iInterfaceMap;
   int iParentClassIndex;
   ClassRecord iArrayElementClass;
   int iNumDims;
   TinyVMType iType;
   int iFlags;
   boolean iUseAllMethods = false;
   boolean isInstanceUsed = false;
   HashSet<ClassRecord> iImplementedBy = new HashSet<ClassRecord>();
   boolean isUsed = false;
   static final String[] wrappers = {"java/lang/Integer",
                                     "java/lang/Boolean",
                                     "java/lang/Character",
                                     "java/lang/Float",
                                     "java/lang/Byte",
                                     "java/lang/Short",
                                     "java/lang/Double",
                                     "java/lang/Long",
                                     "java/lang/Void"
                                    };
   static final String[] primitive = {"int",
                                      "boolean",
                                      "char",
                                      "float",
                                      "byte",
                                      "short",
                                      "double",
                                      "long",
                                      "void"
                                     };

   public void useAllMethods ()
   {
      iUseAllMethods = true;
   }

   public String getName ()
   {
      return iCF.getClassName();
   }
   
   public String getCanonicalName()
   {
	   return getName().replace('$', '.');
   }

   public String getSimpleName()
   {
	   String s = getName();
	   
	   int i = Math.max(s.lastIndexOf('.'), s.lastIndexOf('$'));
	   if (i < 0)
		   return s;
	   
	   return s.substring(i+1);
   }
   
   public String getSourceFilename()
   {
	   return this.iCF.getSourceFileName();
   }

   public int getLength ()
   {
      return IOUtilities.adjustedSize(2 + // Object header
         2 + // class size
         2 + // method table offset
         2 + // instance field table offset
         1 + // number of fields
         1 + // number of methods
         1 + // parent class
         1, // flags
         4);
   }

   public void dump (IByteWriter aOut) throws TinyVMException
   {
      try
      {
         int classIndex = iBinary.getClassIndex("java/lang/Class");
         aOut.writeU2(TinyVMConstants.OBJ_HEADER | classIndex);
         int pAllocSize = getAllocationSize();
         assert pAllocSize != 0: "Check: alloc ok";
         aOut.writeU2(pAllocSize);
         if (isArray())
         {
            aOut.writeU2(iNumDims);
            aOut.writeU2(iBinary.getClassIndex(iArrayElementClass));
            aOut.writeU1(0);
            aOut.writeU1(0);
         }
         else if (isInterface())
         {
            // Interfaces can have at most one method (the static initializer)
            // so we do not need to store the length of the method table. This
            // mean we can (just) fit the information we need to access the
            // interface table.
            aOut.writeU2(iIntMethodTable == null ? 0 : iIntMethodTable.getOffset());
            aOut.writeU2(iInterfaceMap.getOffset());
            aOut.writeU1(iInterfaceMap.getFirst());
            aOut.writeU1(iInterfaceMap.getSize());
         }
         else
         {
            // Normal class record
            int pMethodTableOffset = iMethodTable.getOffset();
            aOut.writeU2(pMethodTableOffset);
            aOut.writeU2(iInstanceFields.getOffset());
            int pNumMethods = iMethodTable.size();
            if (pNumMethods > TinyVMConstants.MAX_METHODS)
            {
               throw new TinyVMException("Class " + iName + ": No more than "
                  + TinyVMConstants.MAX_METHODS + " methods expected");
            }
            aOut.writeU1(pNumMethods);
            int pNumFields = iInstanceFields.size();
            if (pNumFields > TinyVMConstants.MAX_FIELDS)
            {
               throw new TinyVMException("Class " + iName + ": No more than "
                  + TinyVMConstants.MAX_FIELDS + " fields expected");
            }
            aOut.writeU1(pNumFields);
         }
         aOut.writeU1(iParentClassIndex);
         //aOut.writeU1 (iArrayElementType);
         aOut.writeU1(iFlags | (hasReference() ? 0 : TinyVMConstants.C_NOREFS));
         IOUtilities.writePadding(aOut, 4);
      }
      catch (IOException e)
      {
         throw new TinyVMException(e.getMessage(), e);
      }
   }

   public boolean isArray ()
   {
      return iNumDims != 0;
   }

   public boolean isInterface ()
   {
      return iCF.isInterface();
   }

   public boolean isPrimitive()
   {
       return this.iType != TinyVMType.T_REFERENCE;
   }

   public boolean hasStaticInitializer ()
   {
      Method[] methods = iCF.getMethods();
      for (int i = 0; i < methods.length; i++)
      {
         if (methods[i].getName().equals(Constants.STATIC_INITIALIZER_NAME))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * (Call only after record has been processed).
    * @param aSignature
    * @param aStatic
    * @return
    */
   public boolean hasMethod (Signature aSignature, boolean aStatic)
   {
      MethodRecord pRec = iMethods.get(aSignature);
      if (pRec == null)
         return false;
      return ((pRec.getFlags() & TinyVMConstants.M_STATIC) == 0) ^ aStatic;
   }

   public void initFlags ()
   {
      iFlags = 0;
      if (isArray())
         iFlags |= TinyVMConstants.C_ARRAY;
      if (isInterface())
         iFlags |= TinyVMConstants.C_INTERFACE;
      if (hasStaticInitializer())
         iFlags |= TinyVMConstants.C_HASCLINIT;
      if (isPrimitive())
          iFlags |= TinyVMConstants.C_PRIMITIVE;
   }

   /**
    * @return Number of bytes required for object allocation.
    * @throws TinyVMException
    */
   public int getAllocationSize () throws TinyVMException
   {
      return (getClassSize() + 4);
   }

   /**
    * @return Number of bytes occupied by instance fields.
    * @throws TinyVMException
    */
   public int getClassSize () throws TinyVMException
   {
      if (iClassSize != -1)
         return iClassSize;
      iClassSize = computeClassSize();
      return iClassSize;
   }

   /**
    * @return The size of the class in bytes, including any VM space.
    *         This is the exact size required for memory allocation.
    * @throws TinyVMException
    */
   public int computeClassSize () throws TinyVMException
   {
      int pSize = hasParent()? getParent().getClassSize() : 0;
      for (Iterator<InstanceFieldRecord> iter = iInstanceFields.iterator(); iter.hasNext();)
      {
         InstanceFieldRecord pRec = iter.next();
         pSize += pRec.getFieldSize();
      }
      return pSize;
   }
   
   public boolean hasReference() throws TinyVMException
   {
      if (hasParent() && getParent().hasReference())
          return true;
      for (Iterator<InstanceFieldRecord> iter = iInstanceFields.iterator(); iter.hasNext();)
      {
         InstanceFieldRecord pRec = iter.next();
         if (pRec.iType.type() == TinyVMType.T_REFERENCE_TYPE) return true;
      }
      return false;
   }   

   public boolean hasParent ()
   {
      return !"java.lang.Object".equals(iCF.getClassName());
   }
   
   public ClassRecord getParent ()
   {
      assert hasParent(): "Precondition: hasParent()";

      ClassRecord result = iBinary.getClassRecord(iCF.getSuperclassName().replace('.', '/'));
      
      assert result != null: "Postconditon: result != null";
      return result;
   }

   public void initParent () throws TinyVMException
   {
      if (hasParent())
      {
         iParentClassIndex = iBinary.getClassIndex(getParent());
         if (iParentClassIndex == -1)
         {
            throw new TinyVMException("Superclass of " + iCF.getClassName() + " not found");
         }
      }
      else // only java.lang.Object has no super class...
      {
         iParentClassIndex = 0;
         if (!iCF.getClassName().equals("java.lang.Object"))
         {
            throw new TinyVMException("Expected java.lang.Object: "
               + iCF.getClassName());
         }
      }
   }

   public static String getArrayClassName(String aName)
   {
      //System.out.println("process array name " + aName);
      int i = 0;
      while (aName.charAt(i) == '[')
         i++;
      TinyVMType typ = TinyVMType.tinyVMTypeFromSignature(aName.substring(i));
      // if it is an object we return the name, otherwise we return null
      if (typ == TinyVMType.T_OBJECT || typ == TinyVMType.T_REFERENCE)
         return aName.substring(i+1, aName.length()-1);
      else
         return typ.cname();

   }

   static ClassRecord storeArrayClass(String className, HashMap<String, ClassRecord> aClasses,
      RecordTable<ClassRecord> aClassRecords, ClassPath classPath, Binary iBinary) throws TinyVMException
   {
       // find the number of dims
       int dims = 0;
       while(className.charAt(dims) == '[')
          dims++;
       String elementClassName = getArrayClassName(className);
       ClassRecord crec = aClasses.get(elementClassName);
       if (crec == null)
       {
           crec = ClassRecord.getClassRecord(elementClassName,
              classPath, iBinary);
           aClasses.put(elementClassName, crec);
           aClassRecords.add(crec);
       }
       for(int i = dims-1; i >= 0; i--)
       {
           String name = className.substring(i);
           ClassRecord pRec = aClasses.get(name);
           if (pRec == null)
           {
               pRec = PrimitiveClassRecord.getArrayClassRecord(name,
                  iBinary, dims-i, crec);
               aClasses.put(name, pRec);
               aClassRecords.add(pRec);
           }
           crec = pRec;
       }
       return crec;
   }

   public void storeReferredClasses (HashMap<String, ClassRecord> aClasses,
      RecordTable<ClassRecord> aClassRecords, ClassPath aClassPath, ArrayList<String> aInterfaceMethods)
      throws TinyVMException
   {
      // _logger.log(Level.INFO, "Processing CONSTANT_Class entries in " +
      // iName);
      ConstantPool pPool = iCF.getConstantPool();
      Constant[] constants = pPool.getConstantPool();
      for (int i = 0; i < constants.length; i++)
      {
         Constant pEntry = constants[i];
         if (pEntry instanceof ConstantClass)
         {
            String pClassName = ((ConstantClass) pEntry).getBytes(pPool);
            if (aClasses.get(pClassName) == null)
            {
               if (pClassName.startsWith("["))
                  storeArrayClass(pClassName, aClasses, aClassRecords, aClassPath, iBinary);
               else if (aClasses.get(pClassName) == null)
               {
                  ClassRecord pRec = ClassRecord.getClassRecord(pClassName,
                     aClassPath, iBinary);
                  aClasses.put(pClassName, pRec);
                  aClassRecords.add(pRec);
               }
            }
         }
         else if (pEntry instanceof ConstantMethodref)
         {
            String className = ((ConstantMethodref) pEntry).getClass(pPool)
               .replace('.', '/');
            ClassRecord pClassRec = aClasses.get(className);
            if (pClassRec == null)
            {
               if (className.startsWith("["))
                  pClassRec = storeArrayClass(className, aClasses, aClassRecords, aClassPath, iBinary);
               else
               {
                  pClassRec = ClassRecord.getClassRecord(className, aClassPath,
                      iBinary);
                  aClasses.put(className, pClassRec);
                  aClassRecords.add(pClassRec);
               }
            }

            ConstantNameAndType cnat = (ConstantNameAndType) iCF
               .getConstantPool().getConstant(
                  ((ConstantMethodref) pEntry).getNameAndTypeIndex());
            pClassRec.addUsedMethod(cnat.getName(iCF.getConstantPool()) + ":"
               + cnat.getSignature(iCF.getConstantPool()));

         }
         else if (pEntry instanceof ConstantInterfaceMethodref)
         {
            ConstantNameAndType cnat = (ConstantNameAndType) iCF
               .getConstantPool().getConstant(
                  ((ConstantInterfaceMethodref) pEntry).getNameAndTypeIndex());
            aInterfaceMethods.add(cnat.getName(iCF.getConstantPool())
               + ":" + cnat.getSignature(iCF.getConstantPool()));
         }
         else if (pEntry instanceof ConstantNameAndType)
         {
             //System.out.println("N&T: " + ((ConstantNameAndType) pEntry).getName(iCF.getConstantPool()) + " sig " + ((ConstantNameAndType) pEntry).getSignature(
               //iCF.getConstantPool()));
            String sig = ((ConstantNameAndType) pEntry).getSignature(iCF.getConstantPool());
            if (sig.charAt(0) == '[')
               storeArrayClass(sig, aClasses, aClassRecords, aClassPath, iBinary);
            else if (sig.substring(0, 1).equals("("))
            {
               if (!((ConstantNameAndType) pEntry).getName(
                  iCF.getConstantPool()).substring(0, 1).equals("<"))
               {
                  aInterfaceMethods.add(((ConstantNameAndType) pEntry)
                     .getName(iCF.getConstantPool())
                     + ":"
                     + sig);
               }
            }
         }
      }
   }

   public void addUsedMethod (String aRef)
   {
      iUsedMethods.add(aRef);
   }

   public static String cpEntryId (Constant aEntry)
   {
      String pClassName = aEntry.getClass().getName();
      int pDotIdx = pClassName.lastIndexOf('.');
      return pDotIdx == -1? pClassName : pClassName.substring(pDotIdx + 1);
   }

   MethodRecord getMethodRecord (Signature aSig)
   {
      return iMethods.get(aSig);
   }

   MethodRecord getActualVirtualMethodRecord (Signature aSig)
   {
      MethodRecord pRec = getMethodRecord(aSig);
      if (pRec != null)
         return pRec;
      if (!hasParent())
      {
         return null;
      }
      return getParent().getActualVirtualMethodRecord(aSig);
   }


   MethodRecord getSpecialMethodRecord (Signature aSig)
   {
       /* Search for all methods that are implemented directly by this class or
        * by a parent class
        */
      MethodRecord pRec = getMethodRecord(aSig);
      if (pRec != null)
         return pRec;
      if (!hasParent())
      {
         return null;
      }
      return getParent().getSpecialMethodRecord(aSig);
   }


   MethodRecord getVirtualMethodRecord (Signature aSig)
   {
       /* Search for all methods that may be implemented by this class (including
        * interfaces and parent classes.
        */
      MethodRecord pRec = getInterfaceMethodRecord(aSig);
      if (pRec != null)
         return pRec;
      if (!hasParent())
      {
         return null;
      }
      return getParent().getVirtualMethodRecord(aSig);
   }
   
   MethodRecord getInterfaceMethodRecord (Signature aSig)
   {
      /* Search for a method defined by an interface. First search the current
       * interface then search all super interfaces.
       */
      MethodRecord pRec = getMethodRecord(aSig);
      if (pRec != null)
         return pRec;
       String []interfaces = iCF.getInterfaceNames();
       if (interfaces == null) return null;
       for(int i = 0; i < interfaces.length; i++)
       {
           // _logger.log(Level.INFO, "Interface name " + interfaces[i]);
           ClassRecord pInterfaceRecord = iBinary.getClassRecord(interfaces[i].replace('.', '/'));
           pRec = pInterfaceRecord.getInterfaceMethodRecord(aSig);
           if (pRec != null)
               return pRec;
       }
       return null;
   }
   
   int getMethodIndex (MethodRecord aRecord)
   {
      return iMethodTable.indexOf(aRecord);
   }

   int getApparentInstanceFieldOffset (String aName) throws TinyVMException
   {
      /* Locate the record for the field called aName starting at the current
       * class and if needed searching parent fields. When found return the
       * offset of the field within the current class.
       */
      int pOffset = hasParent()? getParent().getClassSize() : 0;
      for (Iterator<InstanceFieldRecord> iter = iInstanceFields.iterator(); iter.hasNext();)
      {
         InstanceFieldRecord pRec = iter.next();
         if (pRec.getName().equals(aName))
            return pOffset;
         pOffset += pRec.getFieldSize();
      }
      if (hasParent())
          return getParent().getApparentInstanceFieldOffset(aName);
      return -1;
   }

   public int getInstanceFieldOffset (String aName) throws TinyVMException
   {
      int offset = getApparentInstanceFieldOffset(aName);
      if (offset < 0)
          return offset;
      // Return the offset allowing for the class header.
      return offset + 4;
   }

   /**
    * @param aName
    * @return Offset relative to the start of the static state block.
    * @throws TinyVMException
    */
   public int getStaticFieldOffset (String aName) throws TinyVMException
   {
      StaticValue pValue = iStaticValues.get(aName);
      if (pValue == null)
         return -1;
      return pValue.getOffset() - iBinary.iStaticState.getOffset();
   }

   /**
    * @param aName the name of the static field.
    * @return The StaticValue for the given name
    * @throws TinyVMException
    */
   public StaticValue getStaticValue (String aName) throws TinyVMException
   {
      return iStaticValues.get(aName);
   }

   public int getStaticFieldIndex (String aName)
   {
      StaticFieldRecord pRecord = iStaticFields.get(aName);
      if (pRecord == null)
         return -1;
      // TBD: This indexOf call is slow
      return iBinary.iStaticFields.indexOf(pRecord);
   }
   
   public StaticFieldRecord getStaticFieldRecord(String aName)
   {
      /* Locate the record for the static field call aName. First search the
       * current class, then if it is not found examine any interfaces and
       * parent classes.
       */
      // First look to see if it is local

      StaticFieldRecord pRec = iStaticFields.get(aName); 
      if (pRec != null) return pRec;
      // now search any interfaces
      String []interfaces = iCF.getInterfaceNames();
      if (interfaces != null)
         for(int i = 0; i < interfaces.length; i++)
         {
            ClassRecord pInterfaceRecord = iBinary.getClassRecord(interfaces[i].replace('.', '/'));
            pRec = pInterfaceRecord.getStaticFieldRecord(aName);
            if (pRec != null)
               return pRec;
         } 
      // Now try any parent class
      if (hasParent())
          return getParent().getStaticFieldRecord(aName);
      return null;
   }

   public void storeConstants (RecordTable<ConstantRecord> aConstantTable,
      RecordTable<ConstantValue> aConstantValues) throws TinyVMException
   {
      // _logger.log(Level.INFO, "Processing other constants in " + iName);

      ConstantPool pPool = iCF.getConstantPool();
      Constant[] constants = pPool.getConstantPool();
      for (int i = 0; i < constants.length; i++)
      {
         Constant pEntry = constants[i];
         if (pEntry instanceof ConstantString
            || pEntry instanceof ConstantDouble
            || pEntry instanceof ConstantFloat
            || pEntry instanceof ConstantInteger
            || pEntry instanceof ConstantLong
            || pEntry instanceof ConstantClass)
         {
            ConstantRecord pRec = new ConstantRecord(pPool, pEntry, iBinary);
            if (aConstantTable.indexOf(pRec) == -1)
            {
               aConstantTable.add(pRec);
               aConstantValues.add(pRec.constantValue());
            }
         }
      }
   }

   public void storeMethods (RecordTable<RecordTable<MethodRecord>> aMethodTables,
      RecordTable<RecordTable<ExceptionRecord>> aExceptionTables, HashVector<Signature> aSignatures, boolean aAll)
      throws TinyVMException
   {
      // _logger.log(Level.INFO, "Processing methods in " + iName);
      Method[] methods = iCF.getMethods();
      for (int i = 0; i < methods.length; i++)
      {
         Method pMethod = methods[i];
         Signature pSignature = new Signature(pMethod.getName(), pMethod
            .getSignature());
         String meth = pMethod.getName() + ":" + pMethod.getSignature();

         if (aAll || iUseAllMethods || iUsedMethods.indexOf(meth) >= 0
            || pMethod.getName().substring(0, 1).equals("<")
            || meth.equals("run:()V"))
         {
            MethodRecord pMethodRecord = new MethodRecord(pMethod, pSignature,
               this, iBinary, aExceptionTables, aSignatures);
            iMethodTable.add(pMethodRecord);
            iMethods.put(pSignature, pMethodRecord);
         }
         else
         {
            // _logger.log(Level.INFO, "Omitting " + meth + " for class " +
            // iName);
         }
      }
      aMethodTables.add(iMethodTable);
   }
   
   
   public void storeOptimizedMethods (RecordTable<RecordTable<MethodRecord>> aMethodTables,
           RecordTable<RecordTable<ExceptionRecord>> aExceptionTables, HashVector<Signature> aSignatures)
      throws TinyVMException
   {
      // _logger.log(Level.INFO, "Processing methods in " + iName);
      RecordTable<MethodRecord> iOptMethodTable = new RecordTable<MethodRecord>("methods", false, false);
      HashMap<Signature, MethodRecord> iOptMethods = new HashMap<Signature, MethodRecord>();
      MethodRecord staticInitRec = null;
      // We always place the static initializer (if any) first to make it easy to find.
      if (this.hasStaticInitializer())
      {
         staticInitRec = iMethods.get(new Signature(Constants.STATIC_INITIALIZER_NAME, "()V"));
         if (staticInitRec != null && (staticInitRec.isCalled || iBinary.useAll()))
         {
            iOptMethodTable.add(staticInitRec);
            iOptMethods.put(aSignatures.elementAt(staticInitRec.iSignatureId), staticInitRec);
            if (staticInitRec.getExceptions() != null) aExceptionTables.add(staticInitRec.getExceptions());
         }
      }
      for (Iterator<MethodRecord> iter = iMethodTable.iterator(); iter.hasNext();)
      {
         MethodRecord pRec = iter.next();
         if (pRec != staticInitRec && (iBinary.useAll() || pRec.isCalled()))
         {
            iOptMethodTable.add(pRec);
            iOptMethods.put(aSignatures.elementAt(pRec.iSignatureId), pRec);
            if (pRec.getExceptions() != null) aExceptionTables.add(pRec.getExceptions());
         }
      }
      iMethodTable = iOptMethodTable;
      iMethods = iOptMethods;
      if (this.isInterface())
      {
         // Special case for interfaces we only store the static constructor (if there is one).
         // Note that we continue to use the full tables for lookups etc. so that we can
         // obtain method signatures.
         if (staticInitRec != null)
         {
            iIntMethodTable = new RecordTable<MethodRecord>("methods", false, false);
            iIntMethodTable.add(staticInitRec);
            aMethodTables.add(iIntMethodTable);
         }
      }
      else
         aMethodTables.add(iMethodTable);
   }

   public void storeOptimizedStaticFields (RecordTable<StaticFieldRecord> aStaticFields,
           RecordTable<StaticValue> aStaticState, int align)
      throws TinyVMException
   {
      Field[] fields = iCF.getFields();
      for (int i = 0; i < fields.length; i++)
      {
         Field pField = fields[i];
         if (pField.isStatic())
         {
            String pName = pField.getName().toString();
            StaticFieldRecord pRec = iStaticFields.get(pName);
            StaticValue pValue = iStaticValues.get(pName);
            if (pValue.getAlignment() == align && (iBinary.useAll() || pRec.used()))
            {
                aStaticState.add(pValue);
                aStaticFields.add(pRec);
            }
         }
      }
   }

   public void storeOptimizedFields (RecordTable<RecordTable<InstanceFieldRecord>> aInstanceFieldTables)
      throws TinyVMException
   {

      aInstanceFieldTables.add(iInstanceFields);
   }

   public void storeFields (RecordTable<RecordTable<InstanceFieldRecord>> aInstanceFieldTables,
      RecordTable<StaticFieldRecord> aStaticFields, RecordTable<StaticValue> aStaticState)
      throws TinyVMException
   {
      Field[] fields = iCF.getFields();
      for (int i = 0; i < fields.length; i++)
      {
         Field pField = fields[i];
         if (pField.isStatic())
         {
            StaticValue pValue = new StaticValue(pField);
            StaticFieldRecord pRec = new StaticFieldRecord(pField, this);
            String pName = pField.getName().toString();
            assert !iStaticValues.containsKey(pName): "Check: value not static";
            iStaticValues.put(pName, pValue);
            iStaticFields.put(pName, pRec);
            aStaticState.add(pValue);
            aStaticFields.add(pRec);
         }
         else
         {
            iInstanceFields.add(new InstanceFieldRecord(pField));
         }
      }
      aInstanceFieldTables.add(iInstanceFields);
   }

   public void storeCode (RecordTable<CodeSequence> aCodeSequences, boolean aPostProcess)
      throws TinyVMException
   {
      for (Iterator<MethodRecord> iter = iMethodTable.iterator(); iter.hasNext();)
      {
        MethodRecord pRec = iter.next();
        if (aPostProcess)
            pRec.postProcessCode(aCodeSequences, iCF, iBinary);
         else
            pRec.copyCode(aCodeSequences, iCF, iBinary);
      }
   }
   
   public void markMethods ()   throws TinyVMException
   {
      for (Iterator<MethodRecord> iter = iMethodTable.iterator(); iter.hasNext();)
      {
         MethodRecord pRec = iter.next();
         pRec.markCalled(iCF, iBinary);
      }
   }
   
   public void markMethod(MethodRecord pRec, boolean directCall) throws TinyVMException
   {
       // Is this a simple class?
       if (directCall) iBinary.markClassUsed(this, (pRec.getFlags() & TinyVMConstants.M_STATIC) == 0);
       pRec.markCalled(iCF, iBinary);
       if (!iImplementedBy.isEmpty())
       {
           // Must be an interface. We need to mark all possible methods that
           // could be called via this interface
           for (Iterator<ClassRecord> iter = iImplementedBy.iterator(); iter.hasNext();)
           {
              ClassRecord pClass = iter.next();
              // _logger.log(Level.INFO, "Mark interface class " + pClass.getName());
              // Does this class (or a super class), have this method?
              MethodRecord pActualMethod = pClass.getActualVirtualMethodRecord(iBinary.iSignatures.elementAt(pRec.getSignatureId()));
              // If so then we need to mark it...
              if (pActualMethod != null)
                 pActualMethod.iClassRecord.markMethod(pActualMethod, false);
           }
          
       }
   }

   public static ClassRecord getClassRecord (String className, ClassPath aCP,
      Binary aBinary) throws TinyVMException
   {
      assert className != null: "Precondition: aName != null";
      assert aCP != null: "Precondition: aCP != null";
      assert aBinary != null: "Precondition: aBinary != null";
      assert className.indexOf('.') == -1: "Precondition: className is in correct form: "
         + className;

      String className2 = className.replace('/', '.');
      
      InputStream pIn;
      try
      {
         pIn = aCP.getInputStream(className);
         assert pIn != null: "Check: pIn != null";
      }
      catch (IOException e)
      {
         throw new TinyVMException("Class " + className2
            + " (file " + className + ".class) not found in CLASSPATH " + aCP);
      }

      ClassRecord pCR = new ClassRecord();
      try
      {
         pCR.iBinary = aBinary;
         pCR.iName = className;
         InputStream pBufIn = new BufferedInputStream(pIn, 4096);
         pCR.iCF = new ClassParser(pBufIn, className).parse();
         pBufIn.close();
      }
      catch (Exception e)
      {
         // TODO refactor exceptions
         throw new TinyVMException(e.getMessage(), e);
      }
      
      String actualName = pCR.iCF.getClassName();
      if (!actualName.equals(className2))
          throw new TinyVMException("found class "+actualName+" instead of requested "+className2);
      
      pCR.iType = TinyVMType.T_REFERENCE;
      pCR.iNumDims = 0;
      pCR.iArrayElementClass = null;
      return pCR;
   }

    @Override
   public String toString ()
   {
      return iName;
   }

    @Override
   public int hashCode ()
   {
      return iName.hashCode();
   }

    @Override
   public boolean equals (Object aObj)
   {
      if (!(aObj instanceof ClassRecord))
         return false;
      ClassRecord pOther = (ClassRecord) aObj;
      return pOther.iName.equals(iName);
   }
   
   public void addInterfaces(ClassRecord pUserClass)
   {
       String []interfaces = iCF.getInterfaceNames();
       if (interfaces == null) return;
       for(int i = 0; i < interfaces.length; i++)
       {
           // _logger.log(Level.INFO, "Interface name " + interfaces[i]);
           ClassRecord pInterfaceRecord = iBinary.getClassRecord(interfaces[i].replace('.', '/'));
           pInterfaceRecord.addInterfaceUser(pUserClass);
           // If this interface extends an existing interface then the parent
           // interfaces do not show up as super-classes instead they show up
           // as interfaces, implemented by this interface. So we add this
           // class to those inetrafces as well.
           pInterfaceRecord.addInterfaces(pUserClass);               
       }
       if (hasParent())
           getParent().addInterfaces(pUserClass);
   }


   public void addInterfaceUser(ClassRecord pRec)
   {
       // Add the supplied class to the list of classes known to implement this
       // interface.
       // _logger.log(Level.INFO, "Adding class " + pRec.getName() + " to interface " + getName());
       if (iImplementedBy.contains(pRec)) return;
       iImplementedBy.add(pRec);
   }
   
   public void findHiddenMethods ()   throws TinyVMException
   {
      // If this class is a sub class and it contains methods that over-ride
      // methods, then we need to add the over-ridding method to the list of
      // methods to be marked that is associated with the over-ridden method!
      for (Iterator<MethodRecord> iter = iMethodTable.iterator(); iter.hasNext();)
      {
         MethodRecord pRec = iter.next();
         if (hasParent () && (pRec.getFlags() & (TinyVMConstants.M_STATIC | TinyVMConstants.M_STATIC)) == 0
             && !(iBinary.iSignatures.elementAt(pRec.iSignatureId)).isInitializer())
         {
             MethodRecord pOverridden = getParent().getVirtualMethodRecord(iBinary.iSignatures.elementAt(pRec.getSignatureId()));
             if (pOverridden != null)
             {
                pOverridden.setHiddenBy(pRec);
                // _logger.log(Level.INFO, "Set " + pOverridden.iClassRecord.getName() + " : " + ((Signature)iBinary.iSignatures.elementAt(pOverridden.iSignatureId)).getImage() + " hidden by "
                          // + getName() + " : " + ((Signature)iBinary.iSignatures.elementAt(pRec.iSignatureId)).getImage());
             }

         }
      }
   }

   public void markUsed()
   {
       if (!isUsed)
       {
           if(hasParent())
              getParent().markUsed();
           if (isArray())
           {
              iArrayElementClass.markUsed();

           }
       }
       isUsed = true;
   }


   public void markInstanceUsed()
   {
       if (!isInstanceUsed)
       {
           if(hasParent())
              getParent().markInstanceUsed();
           if (isArray())
           {
              iArrayElementClass.markInstanceUsed();
           }
       }
       isInstanceUsed = true;
       markUsed();
   }
   
   public boolean used()
   {
       return isUsed;
   }

   public boolean instanceUsed()
   {
       return isInstanceUsed;
   }

   public int getArrayDimension()
   {
       return iNumDims;
   }

   public ClassRecord getArrayElementClass()
   {
       return iArrayElementClass;
   }

   public String signature()
   {
       String sig = "";
       if (isArray())
       {
           sig = "[" + iArrayElementClass.signature();
       }
       else if(isPrimitive())
           sig = iType.signature();
       else
           sig = iType.signature() + this.iName + ";";
       return sig;

   }

   /**
    * Helper method return the wrapper index, if this is a wrapper class
    * @return The wrapper index or -1 if this class is not a wrapper class
    */
   private int getWrapperIndex()
   {
       for(int i = 0; i < wrappers.length; i++)
           if (iName.equals(wrappers[i])) return i;
       return -1;
   }

   /**
    * Determine if this class is a wrapper
    * @return true if the class is a wrapper, otherwise false
    */
   public boolean isWrapper()
   {
      return getWrapperIndex() >= 0;
   }

   /**
    * Return the wrapped class if this class is a wrapper
    * @return the primitive class or null if this class is not a wrapper
    */
   public PrimitiveClassRecord getPrimitiveClass()
   {
      int idx = getWrapperIndex();
      if (idx < 0) return null;
      return (PrimitiveClassRecord) iBinary.getClassRecord(primitive[idx]);
   }

   /**
    * Add the classes which implement this interface into the the set of
    * class records.
    * @param aClassRecords Set of records
    */
   public void storeOptimizedImplementingClasses(RecordTable<ClassRecord> aClassRecords)
   {
      HashSet<ClassRecord> iNewImplementedBy = new HashSet<ClassRecord>();

      for(ClassRecord cr : iImplementedBy)
         if (cr.used() || iBinary.useAll())
         {
            iNewImplementedBy.add(cr);
            aClassRecords.add(cr);
         }
      iImplementedBy = iNewImplementedBy;
   }

   /**
    * Create and store the inetrface map for this interface class.
    * The interface map provides a mechanism for quickly testing if a particular
    * class implements this interface (or a super interface).
    * @param maps the set of ineterface maps.
    * @throws TinyVMException
    */
   public void storeInterfaceMap(RecordTable<InterfaceMap> maps) throws TinyVMException
   {
      if (!isInterface())
         throw new TinyVMException("Attempt to store an interface map for a non interface class " + this.iName);
      iInterfaceMap = new InterfaceMap(iBinary, this);
      maps.add(iInterfaceMap);
   }
}

