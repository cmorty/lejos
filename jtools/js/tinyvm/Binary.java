package js.tinyvm;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

import js.common.ToolProgressMonitor;
import js.tinyvm.io.IByteWriter;
import js.tinyvm.util.HashVector;

/**
 * Abstraction for dumped binary.
 */
public class Binary
{
   // State that is written to the binary:
   final RecordTable<WritableData> iEntireBinary = new RecordTable<WritableData>("binary", true, false);

   // Contents of binary:
   final MasterRecord iMasterRecord = new MasterRecord(this);
   RecordTable<ClassRecord> iClassTable = new RecordTable<ClassRecord>("class table", false, false);
   RecordTable<StaticValue> iStaticState = new RecordTable<StaticValue>("static state", true, true);
   RecordTable<StaticFieldRecord> iStaticFields = new RecordTable<StaticFieldRecord>("static fields", true, false);
   RecordTable<ConstantRecord> iConstantTable = new RecordTable<ConstantRecord>("constants", false, false);
   RecordTable<RecordTable<MethodRecord>> iMethodTables = new RecordTable<RecordTable<MethodRecord>>("methods", true, false);
   RecordTable<RecordTable<ExceptionRecord>> iExceptionTables = new RecordTable<RecordTable<ExceptionRecord>>("exceptions", false, false);
   final RecordTable<RecordTable<InstanceFieldRecord>> iInstanceFieldTables = new RecordTable<RecordTable<InstanceFieldRecord>>("instance fields",
      true, false);
   final RecordTable<CodeSequence> iCodeSequences = new RecordTable<CodeSequence>("code", true, false);
   RecordTable<ConstantValue> iConstantValues = new RecordTable<ConstantValue>("constant values", true,
      false);
   final RecordTable<EntryClassIndex> iEntryClassIndices = new RecordTable<EntryClassIndex>(
      "entry class indices", true, false);

   // Other state:
   final HashSet<Signature> iSpecialSignatures = new HashSet<Signature>();
   final HashMap<String, ClassRecord> iClasses = new HashMap<String, ClassRecord>();
   final HashVector<Signature> iSignatures = new HashVector<Signature>();
   int usedClassCount = 0;
   int markGeneration = 0;

   /**
    * Constructor.
    */
   public Binary ()
   {}

   /**
    * Dump.
    * 
    * @param writer
    * @throws TinyVMException
    */
   public void dump (IByteWriter writer) throws TinyVMException
   {
      iEntireBinary.dump(writer);
   }

   //
   // TODO public interface
   //

   //
   // TODO protected interface
   //

   //
   // classes
   //

   /**
    * Add a class.
    * 
    * @param className class name with '/'
    * @param classRecord
    */
   protected void addClassRecord (String className, ClassRecord classRecord)
   {
      assert className != null: "Precondition: className != null";
      assert classRecord != null: "Precondition: classRecord != null";
      assert className.indexOf('.') == -1: "Precondition: className is in correct form";

      iClasses.put(className, classRecord);
      iClassTable.add(classRecord);
   }

   /**
    * Has class in binary a public static void main (String[] args) method?
    * 
    * @param className class name with '/'
    * @return
    */
   public boolean hasMain (String className)
   {
      assert className != null: "Precondition: className != null";
      assert className.indexOf('.') == -1: "Precondition: className is in correct form";

      ClassRecord pRec = getClassRecord(className);
      return pRec.hasMethod(new Signature("main", "([Ljava/lang/String;)V"),
         true);
   }

   /**
    * Get class record with given signature.
    * 
    * @param className class name with '/'
    * @return class record or null if not found
    */
   public ClassRecord getClassRecord (String className)
   {
      assert className != null: "Precondition: className != null";
      assert className.indexOf('.') == -1: "Precondition: className is in correct form";

      return iClasses.get(className);
   }

   /**
    * Get the class record for an object array.
    * 
    * @param arrayClassName
    * @return class record or null if not found or the array is a primitive array.
    */
   public ClassRecord getArrayClassRecord (String arrayClassName)
   {
       assert arrayClassName.startsWith("[") : "Array class name does not begin with [";
       String className = ClassRecord.getArrayClassName(arrayClassName);
       if (className == null) return null;
       return iClasses.get(className);
   }

   /**
    * Get index of class in binary by its signature.
    * 
    * @param className class name with '/'
    * @return index of class in binary or -1 if not found
    */
   public int getClassIndex (String className)
   {
      assert className != null: "Precondition: className != null";
      assert className.indexOf('.') == -1: "Precondition: className is in correct form";

      return getClassIndex(getClassRecord(className));
   }

   /**
    * Get index of class in binary by its class record.
    * 
    * @param classRecord
    * @return index of class in binary or -1 if not found
    */
   public int getClassIndex (ClassRecord classRecord)
   {
      if (classRecord == null)
      {
         return -1;
      }

      return iClassTable.indexOf(classRecord);
   }
   
   /**
    * Mark the given class as actually used.
    * 
    * @param classRecord the class to be marked
    */
   public void markClassUsed(ClassRecord classRecord, boolean instance)
   {
       if (instance && !classRecord.instanceUsed())
       {
           classRecord.markInstanceUsed();
           usedClassCount++;
       }
       if (!classRecord.used())
       {
           classRecord.markUsed();
           usedClassCount++;
       }
   }

   /**
    * Return the current marking generation. This is used to ensure that for
    * each new iteration (or generation) of the recursive mark we will
    * walk all of the code at least once.
    * @return current generation
    */
   public int getGeneration()
   {
      return markGeneration;
   }

   //
   // constants
   //

   /**
    * Get constant record with given index.
    * 
    * @param index
    * @return constant record or null if not found
    */
   public ConstantRecord getConstantRecord (int index)
   {
      assert index >= 0: "Precondition: index >= 0";

      return iConstantTable.get(index);
   }

   /**
    * Get index of constant in binary by its constant record.
    * 
    * @param constantRecord
    * @return index of constant in binary or -1 if not found
    */
   public int getConstantIndex (ConstantRecord constantRecord)
   {
      if (constantRecord == null)
      {
         return -1;
      }

      return iConstantTable.indexOf(constantRecord);
   }

   //
   // processing
   //

   /**
    * Create closure.
    * 
    * @param entryClassNames names of entry class with '/'
    * @param classPath class path
    * @param all do not filter classes?
    */
   public static Binary createFromClosureOf (String[] entryClassNames,
      ClassPath classPath, boolean all) throws TinyVMException
   {
      Binary result = new Binary();
      // From special classes and entry class, store closure
      result.processClasses(entryClassNames, classPath);
      // Store special signatures
      result.processSpecialSignatures();
      result.processConstants();
      result.processMethods(all);
      result.processFields();
      if (!all)
      {
         // Remove unused methods/classes/fields/constants.
         result.markUsed(entryClassNames);
         result.processOptimizedClasses();
         result.processOptimizedConstants();
         result.processOptimizedMethods();
         result.processOptimizedFields();
      }
      // Copy code as is (first pass)
      result.processCode(false);
      result.storeComponents();
      result.initOffsets();
      // Post-process code after offsets are set (second pass)
      result.processCode(true);

      assert result != null: "Postconditon: result != null";
      return result;
   }

   public void processClasses (String[] entryClassNames, ClassPath classPath)
      throws TinyVMException
   {
      assert entryClassNames != null: "Precondition: entryClassNames != null";
      assert classPath != null: "Precondition: classPath != null";

      ArrayList<String> pInterfaceMethods = new ArrayList<String>();

      // Add special all classes first
      String[] specialClasses = SpecialClassConstants.CLASSES;
      //_logger.log(Level.INFO, "Starting with " + specialClasses.length
      //   + " special classes.");
      for (int i = 0; i < specialClasses.length; i++)
      {
         String className = specialClasses[i];
         ClassRecord classRecord = ClassRecord.getClassRecord(className,
            classPath, this);
         addClassRecord(className, classRecord);
         // classRecord.useAllMethods();
      }

      // Now add entry classes
      // _logger.log(Level.INFO, "Starting with " + entryClassNames.length
      //    + " entry classes.");
      for (int i = 0; i < entryClassNames.length; i++)
      {
         String className = entryClassNames[i];
         ClassRecord classRecord = ClassRecord.getClassRecord(className,
            classPath, this);
         // Convert name into standard form.
         className = classRecord.getName().replace('.', '/');
         classRecord = ClassRecord.getClassRecord(className, classPath, this);
         entryClassNames[i] = className;
         addClassRecord(className, classRecord);
         classRecord.useAllMethods();
         // Update table of indices to entry classes
         iEntryClassIndices.add(new EntryClassIndex(this, className));
      }

      // Now add the closure.
      // _logger.log(Level.INFO, "Starting with " + iClassTable.size()
      //    + " classes.");
      // Yes, call iClassTable.size() in every pass of the loop.
      for (int pIndex = 0; pIndex < iClassTable.size(); pIndex++)
      {
         ClassRecord classRecord = iClassTable.get(pIndex);
         classRecord.storeReferredClasses(iClasses, iClassTable, classPath,
            pInterfaceMethods);
      }

      // Initialize indices and flags
      int pSize = iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = iClassTable.get(pIndex);
         for (int i = 0; i < pInterfaceMethods.size(); i++)
         {
            classRecord.addUsedMethod(pInterfaceMethods.get(i));
         }

         classRecord.iIndex = pIndex;
         classRecord.initFlags();
         classRecord.initParent();
      }
   }
   
   public void processOptimizedClasses () throws TinyVMException
   {
      RecordTable<ClassRecord> iNewClassTable = new RecordTable<ClassRecord>("class table", false, false);
      int pSize = iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = iClassTable.get(pIndex);
         if (classRecord.used()) iNewClassTable.add(classRecord);
      }
      iClassTable = iNewClassTable;
      pSize = iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = iClassTable.get(pIndex);
         classRecord.initParent();
      }

   }
   
   public void markUsed (String[] entryClassNames)
      throws TinyVMException
   {
      /* First stage of unused method/class/field elimination.
       * Starting with the callable root methods we need to mark all callable
       * methods. We recursively walk the code for each method marking and
       * walking new methods as we come to them.
       * As we walk the code we also mark all used classes and fields.
       * We need to take particular care with interfaces and with
       * over-ridden methods to ensure that all possible destinations are
       * included.
       */
      
      /* For interfaces we need to ensure that for every method in an interface
       * that ends up being marked we locate all possible implementations
       * of that method. To do that we search all classes for those that
       * implement an interface and associate those classes with the interface.
       * Then when we mark a method in the interface we can mark all possible
       * implementations.
       *
       * We also need to handle marking methods that may be over-ridden by a 
       * method in a sub class. We locate all such methods and link them to the
       * "super-method" if this gets marked we also mark the sub-methods.
       */
      int pSize = iClassTable.size();
      // Mark special classes as being used (they may be generated by the vm
      String[] specialClasses = SpecialClassConstants.CLASSES;
      for (int i = 0; i < specialClasses.length; i++)
      {
         String className = specialClasses[i];
         ClassRecord classRecord = getClassRecord(className);
         classRecord.markUsed();
         classRecord.markInstanceUsed();
      }
      // Add the run method that is called directly from the vm
      Signature staticInit = new Signature("<clinit>()V");
      Signature runMethod = new Signature("run()V");

      // Now add entry classes      
      for (int i = 0; i < entryClassNames.length; i++)
      {
         ClassRecord classRecord = getClassRecord(entryClassNames[i]);
         classRecord.markUsed();
         classRecord.markInstanceUsed();
         classRecord.markMethods();
      }

      // We now add in the static initializers of all marked classes. 
      // We also add in the special entry points that may be called
      // directly from the VM.
      // Note: The set of used classes may increase as a result of marking.
      // in which case we do the whole thing over again.
      int classCount;
      do {
         classCount = usedClassCount;
         markGeneration++;
         // First make sure all interfaces implementors and hidden 
         // methods are exposed
         for (int pIndex = 0; pIndex < pSize; pIndex++)
         {
            ClassRecord classRecord = iClassTable.get(pIndex);
            if (classRecord.used())
            {
               if (classRecord.instanceUsed())
               {
                  classRecord.addInterfaces(classRecord);
                  classRecord.findHiddenMethods();
               }
            }
         } 
         // Now recursively mark any classes that can be called directly by
         // the VM
         for (int pIndex = 0; pIndex < pSize; pIndex++)
         {
            ClassRecord classRecord = iClassTable.get(pIndex);
            if (classRecord.used())
            {

               if (classRecord.hasMethod(runMethod, false))
               {
                   MethodRecord pRec = classRecord.getMethodRecord(runMethod);
                   classRecord.markMethod(pRec, true);             
               }

               if (classRecord.hasStaticInitializer())
               {
                   MethodRecord pRec = classRecord.getMethodRecord(staticInit);
                   classRecord.markMethod(pRec, true);
               }
            }
         }
         // Finally mark starting from all of the entry classes
         for (int i = 0; i < entryClassNames.length; i++)
         {
            ClassRecord classRecord = getClassRecord(entryClassNames[i]);
            classRecord.markMethods();
         }

      } while (classCount != usedClassCount);

   }

   public void processSpecialSignatures ()
   {
      for (int i = 0; i < SpecialSignatureConstants.SIGNATURES.length; i++)
      {
         Signature pSig = new Signature(SpecialSignatureConstants.SIGNATURES[i]);
         iSignatures.addElement(pSig);
         iSpecialSignatures.add(pSig);
      }
   }

   public boolean isSpecialSignature (Signature aSig)
   {
      return iSpecialSignatures.contains(aSig);
   }

   public void processConstants () throws TinyVMException
   {
      int pSize = iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord pRec = iClassTable.get(pIndex);
         pRec.storeConstants(iConstantTable, iConstantValues);
      }
   }
   
   public void processOptimizedConstants () throws TinyVMException
   {
      int pSize = iConstantTable.size();
      RecordTable<ConstantRecord> iOptConstantTable = new RecordTable<ConstantRecord>("constants", false, false);
      RecordTable<ConstantValue> iOptConstantValues = new RecordTable<ConstantValue>("constant values", true, false);
      
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ConstantRecord pRec = iConstantTable.get(pIndex);
         if (pRec.used())
         {
            iOptConstantTable.add(pRec);
            iOptConstantValues.add(pRec.constantValue());
         }
      }
      iConstantTable = iOptConstantTable;
      iConstantValues = iOptConstantValues;
   }

   /**
    * Calls storeMethods on all the classes of the closure previously computed
    * with processClasses.
    * 
    * @throws TinyVMException
    */
   public void processMethods (boolean iAll) throws TinyVMException
   {
      int pSize = iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = iClassTable.get(pIndex);
         classRecord.storeMethods(iMethodTables, iExceptionTables, iSignatures,
            iAll);
      }
   }
   
   
   public void processOptimizedMethods () throws TinyVMException
   {
      /* This is the second stage of the unused methods elimination code.
       * We need to re-create the method and exception tables so that they
       * only contain methods that are actually called.
       */
      int pSize = iClassTable.size();
      // We need an optimized version of the method and exception tables
      // so create new ones and repopulate.
      iMethodTables = new RecordTable<RecordTable<MethodRecord>>("methods", true, false);
      iExceptionTables = new RecordTable<RecordTable<ExceptionRecord>>("exceptions", false, false);

      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = iClassTable.get(pIndex);
         classRecord.storeOptimizedMethods(iMethodTables, iExceptionTables, iSignatures);
      }
   }

   public void processFields () throws TinyVMException
   {
      int pSize = iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord pRec = iClassTable.get(pIndex);
         pRec.storeFields(iInstanceFieldTables, iStaticFields, iStaticState);
      }
   }
   
   public void processOptimizedFields () throws TinyVMException
   {
      int pSize = iClassTable.size();
      // We need an optimized version of the static tables
      // so create new ones and repopulate.

      iStaticState = new RecordTable<StaticValue>("static state", true, true);
      iStaticFields = new RecordTable<StaticFieldRecord>("static fields", true, false);

      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord pRec = iClassTable.get(pIndex);
         pRec.storeOptimizedFields(iInstanceFieldTables, iStaticFields, iStaticState);
      }
   }

   public void processCode (boolean aPostProcess) throws TinyVMException
   {
      int pSize = iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord pRec = iClassTable.get(pIndex);
         pRec.storeCode(iCodeSequences, aPostProcess);
      }
   }

   //
   // storing
   //

   public void storeComponents ()
   {
      // Master record and class table are always the first two:
      iEntireBinary.add(iMasterRecord);
      iEntireBinary.add(iClassTable);
      // 5 aligned components:
      iEntireBinary.add(iStaticState);
      iEntireBinary.add(iStaticFields);
      iEntireBinary.add(iConstantTable);
      iEntireBinary.add(iMethodTables);
      iEntireBinary.add(iExceptionTables);
      // 4 unaligned components:
      iEntireBinary.add(iInstanceFieldTables);
      iEntireBinary.add(iCodeSequences);
      iEntireBinary.add(iConstantValues);
      iEntireBinary.add(iEntryClassIndices);
   }

   public void initOffsets () throws TinyVMException
   {
      iEntireBinary.initOffset(0);
   }

   public int getTotalNumMethods ()
   {
      int pTotal = 0;
      int pSize = iMethodTables.size();
      for (int i = 0; i < pSize; i++)
      {
         pTotal += iMethodTables.get(i).size();
      }
      return pTotal;
   }

   public int getTotalNumInstanceFields ()
   {
      int pTotal = 0;
      int pSize = iInstanceFieldTables.size();
      for (int i = 0; i < pSize; i++)
      {
         pTotal += iInstanceFieldTables.get(i).size();
      }
      return pTotal;
   }

   public int getTotalNumExceptionRecords()
   {
      int pTotal = 0;
      int pSize = iExceptionTables.size();
      for (int i = 0; i < pSize; i++)
      {
         pTotal += iExceptionTables.get(i).size();
      }
      return pTotal;
   }
           
   // private static final Logger _logger = Logger.getLogger("TinyVM");


   public void log(ToolProgressMonitor monitor) throws TinyVMException {
	   
     // all classes
     for (int pIndex = 0; pIndex < iClassTable.size(); pIndex++)
     {
       ClassRecord pRec = iClassTable.get(pIndex);
       monitor.log("Class " + pIndex + ": " + pRec.getName());
     }
     /*
     int pSize = iSignatures.size();
     for (int i = 0; i < pSize; i++)
     {
       Signature pSig = (Signature) iSignatures.elementAt (i);
       monitor.log("Signature " + i + ": " + pSig.getImage());
     }
	 */
     int pSize = iMethodTables.size();
	 int methodNo = 0;
     for (int i = 0; i < pSize; i++)
     {
		RecordTable<MethodRecord> rt = iMethodTables.get(i);
        int cnt = rt.size();
		for(int j = 0; j < cnt; j++)
		{
			MethodRecord mr = rt.get(j);
            if ((mr.iFlags & TinyVMConstants.M_NATIVE) == 0)
                monitor.log("Method " + methodNo + ": Class: " + mr.iClassRecord.getName() + " Signature: " + 
                             (iSignatures.elementAt(mr.iSignatureId)).getImage() + " PC " + mr.getCodeStart() + " Signature id " + mr.iSignatureId);
            else
                monitor.log("Method " + methodNo + ": Class: " + mr.iClassRecord.getName() + " Signature: " + 
                             (iSignatures.elementAt(mr.iSignatureId)).getImage() + " Native id " + mr.iSignatureId);
			methodNo++;
			
		}
     }
     monitor.log("Master record    : " + iMasterRecord.getLength() + " bytes.");
     monitor.log("Class records    : " + iClassTable.size() + " (" + iClassTable.getLength() + " bytes).");
     monitor.log("Field records    : " + getTotalNumInstanceFields() + " (" + iInstanceFieldTables.getLength() + " bytes).");
     monitor.log("Static fields    : " + iStaticFields.size() + " (" + iStaticFields.getLength() + " bytes).");
     monitor.log("Static state     : " + iStaticState.size() + " (" + iStaticState.getLength() + " bytes).");
     monitor.log("Constant records : " + iConstantTable.size() + " (" + iConstantTable.getLength() + " bytes).");
     monitor.log("Constant values  : " + iConstantValues.size() + " (" + iConstantValues.getLength() + " bytes).");
     monitor.log("Method records   : " + getTotalNumMethods() + " (" + iMethodTables.getLength() + " bytes).");
     //monitor.log("Exception records: " + iExceptionTables.size() + " (" + iExceptionTables.getLength() + " bytes).");
     monitor.log("Exception records: " + getTotalNumExceptionRecords() + " (" + iExceptionTables.getLength() + " bytes).");
     monitor.log("Code             : " + iCodeSequences.size() + " (" + iCodeSequences.getLength() + " bytes).");
     monitor.log("Total            : " + iEntireBinary.getLength() + " bytes.");
   }
   
}

