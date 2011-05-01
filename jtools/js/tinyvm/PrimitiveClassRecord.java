package js.tinyvm;


import java.util.HashMap;

import java.util.ArrayList;

import org.apache.bcel.generic.Type;


import js.tinyvm.util.HashVector;



/**
 * Abstraction for a class record (see vmsrc/language.h).
 */
public class PrimitiveClassRecord extends ClassRecord
{
   ConstantRecord classConstant;

   @Override
   public String getName ()
   {
      return iName;
   }
   
   @Override
   public String getSimpleName()
   {
	   //TODO Integer[].class.getSimpleName() would return "Integer[]" instead of "java.lang.Integer[]"
	   return getCanonicalName();
   }
   
   @Override
   public String getCanonicalName()
   {
	   String s = this.getName();
	   
	   if (s.equals("[V"))
		   return "reserved";
	   
	   if (s.startsWith("["))
		   s = Type.getType(s).toString();
	   
	   return s;
   }
   
   @Override
   public String getSourceFilename()
   {
	   return null;
   }

   @Override
   public boolean isInterface ()
   {
      return false;
   }

   @Override
   public boolean hasStaticInitializer ()
   {
      return false;
   }

   /**
    * (Call only after record has been processed).
    */
    @Override
   public boolean hasMethod (Signature aSignature, boolean aStatic)
   {
      return false;
   }
   
    @Override
   public boolean hasReference() throws TinyVMException
   {
      return isArray() && !this.iArrayElementClass.isPrimitive();
   }   

    @Override
   public boolean hasParent ()
   {
      return true;
   }
   
    @Override
   public ClassRecord getParent ()
   {
      ClassRecord result = iBinary.getClassRecord("java.lang.Object".replace('.', '/'));
      
      assert result != null: "Postconditon: result != null";
      return result;
   }

   @Override
   public void storeReferredClasses (HashMap<String, ClassRecord> aClasses,
      RecordTable<ClassRecord> aClassRecords, ClassPath aClassPath, ArrayList<String> aInterfaceMethods)
      throws TinyVMException
   {

   }

    @Override
   public void addUsedMethod (String aRef)
   {
   }

   
    @Override
   MethodRecord getInterfaceMethodRecord (Signature aSig)
   {
       return null;
   }
   
   
    @Override
   public StaticFieldRecord getStaticFieldRecord(String aName)
   {
      return getParent().getStaticFieldRecord(aName);
   }

    @Override
   public void storeConstants (RecordTable<ConstantRecord> aConstantTable,
      RecordTable<ConstantValue> aConstantValues) throws TinyVMException
   {
      // Make sure the primitive classes are available as constants.
      ConstantRecord pRec = new ConstantRecord(this, iBinary);
      int idx = aConstantTable.indexOf(pRec);
      if (idx == -1)
      {
         aConstantTable.add(pRec);
         aConstantValues.add(pRec.constantValue());
         classConstant = pRec;
      }
      else
          classConstant = aConstantTable.get(idx);
   }

    @Override
   public void storeMethods (RecordTable<RecordTable<MethodRecord>> aMethodTables,
      RecordTable<RecordTable<ExceptionRecord>> aExceptionTables, HashVector<Signature> aSignatures, boolean aAll)
      throws TinyVMException
   {
      aMethodTables.add(iMethodTable);

   }
   
   
    @Override
   public void storeOptimizedMethods (RecordTable<RecordTable<MethodRecord>> aMethodTables,
           RecordTable<RecordTable<ExceptionRecord>> aExceptionTables, HashVector<Signature> aSignatures)
      throws TinyVMException
   {
      aMethodTables.add(iMethodTable);

   }

    @Override
   public void storeOptimizedStaticFields (RecordTable<StaticFieldRecord> aStaticFields, RecordTable<StaticValue> aStaticState, int align)
      throws TinyVMException
   {
   }

    @Override
   public void storeOptimizedFields (RecordTable<RecordTable<InstanceFieldRecord>> aInstanceFieldTables)
      throws TinyVMException
   {
      aInstanceFieldTables.add(iInstanceFields);
   }

    @Override
   public void storeFields (RecordTable<RecordTable<InstanceFieldRecord>> aInstanceFieldTables,
      RecordTable<StaticFieldRecord> aStaticFields, RecordTable<StaticValue> aStaticState)
      throws TinyVMException
   {
      aInstanceFieldTables.add(iInstanceFields);
   }

    @Override
   public void storeCode (RecordTable<CodeSequence> aCodeSequences, boolean aPostProcess)
      throws TinyVMException
   {

   }
   
    @Override
   public void markMethods ()   throws TinyVMException
   {

   }
   
    @Override
   public void markMethod(MethodRecord pRec, boolean directCall) throws TinyVMException
   {

   }

   public static ClassRecord getClassRecord (String className, Binary aBinary, byte typ) throws TinyVMException
   {
      assert className != null: "Precondition: aName != null";
      assert aBinary != null: "Precondition: aBinary != null";
      assert className.indexOf('.') == -1: "Precondition: className is in correct form: "
         + className;


      PrimitiveClassRecord pCR = new PrimitiveClassRecord();
      pCR.iBinary = aBinary;
      pCR.iName = className;
      pCR.iCF = null;
      pCR.iType = TinyVMType.tinyVMType(typ);
      pCR.iNumDims = 0;
      return pCR;
   }

   public static ClassRecord getArrayClassRecord (String className, Binary aBinary, int dims, ClassRecord elem) throws TinyVMException
   {
      assert className != null: "Precondition: aName != null";
      assert aBinary != null: "Precondition: aBinary != null";
      assert className.indexOf('.') == -1: "Precondition: className is in correct form: "
         + className;


      PrimitiveClassRecord pCR = new PrimitiveClassRecord();
      pCR.iBinary = aBinary;
      pCR.iName = className;
      pCR.iCF = null;
      pCR.iType = TinyVMType.T_OBJECT;
      pCR.iNumDims = dims;
      pCR.iArrayElementClass = elem;
      return pCR;
   }

   /**
    * @return Number of bytes required for object allocation.
    * For arrays we return the size of a single element.
    * @throws TinyVMException
    */
    @Override
   public int getAllocationSize () throws TinyVMException
   {
      if (this.isArray())
      {
          if (this.iArrayElementClass.isPrimitive())
              return this.iArrayElementClass.getAllocationSize();
          else
              return TinyVMType.T_OBJECT.size();
      }
      else
          return this.iType.size();
   }


    @Override
   public void addInterfaces(ClassRecord pUserClass)
   {

        getParent().addInterfaces(pUserClass);
   }

   public ConstantRecord getClassConstant()
   {
       return classConstant;
   }
}

