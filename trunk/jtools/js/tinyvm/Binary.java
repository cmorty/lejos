package js.tinyvm;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

import js.common.ToolProgressMonitor;
import js.tinyvm.io.IByteWriter;
import js.tinyvm.util.HashVector;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.Type;

/**
 * Abstraction for dumped binary.
 */
public class Binary
{
   // State that is written to the binary:
   final RecordTable<WritableData> iEntireBinary = new RecordTable<WritableData>("binary", true, true);
   final RecordTable<WritableData> iStaticStorage = new RecordTable<WritableData>("binary", true, true);

   // Contents of binary:
   final MasterRecord iMasterRecord = new MasterRecord(this);
   RecordTable<ClassRecord> iClassTable = new RecordTable<ClassRecord>("class table", false, true);
   RecordTable<StaticValue> iStaticState = new RecordTable<StaticValue>("static state", true, true);
   RecordTable<StaticFieldRecord> iStaticFields = new RecordTable<StaticFieldRecord>("static fields", true, true);
   RecordTable<ConstantRecord> iConstantTable = new RecordTable<ConstantRecord>("constants", false, true);
   RecordTable<RecordTable<MethodRecord>> iMethodTables = new RecordTable<RecordTable<MethodRecord>>("methods", true, true);
   RecordTable<RecordTable<ExceptionRecord>> iExceptionTables = new RecordTable<RecordTable<ExceptionRecord>>("exceptions", false, true);
   RecordTable<RecordTable<InstanceFieldRecord>> iInstanceFieldTables = new RecordTable<RecordTable<InstanceFieldRecord>>("instance fields",
      true, true);
   final RecordTable<CodeSequence> iCodeSequences = new RecordTable<CodeSequence>("code", true, true);
   RecordTable<ConstantValue> iConstantValues = new RecordTable<ConstantValue>("constant values", true,
      true);
   final RecordTable<EntryClassIndex> iEntryClassIndices = new RecordTable<EntryClassIndex>(
      "entry class indices", true, true);
   final RecordTable<InterfaceMap> iInterfaceMaps = new RecordTable<InterfaceMap>("interface", true, true);

   // Other state:
   final HashSet<Signature> iSpecialSignatures = new HashSet<Signature>();
   final HashMap<String, ClassRecord> iClasses = new HashMap<String, ClassRecord>();
   final HashVector<Signature> iSignatures = new HashVector<Signature>();
   final DebugData debugData = new DebugData();
   int usedClassCount = 0;
   int markGeneration = 0;
   boolean useAll = false;
   // Optimal order for storing constants/statics etc. Note we store 4 byte
   // items first to maximize the chance of using optimized load/store operations
   // on them.
   final int[] alignments = {4, 8, 2, 1};

   int constOpLoads = 0;
   int constNormLoads = 0;
   int constWideLoads = 0;
   int constString = 0;
   int staticOpLoads = 0;
   int staticNormLoads = 0;
   int fieldOpOp = 0;
   int fieldNormOp = 0;

   int interfaceClasses = 0;
   int usedInterfaceClasses = 0;
   int implementedInterfaces = 0;
   int usedImplementedInterfaces = 0;

   /**
    * Constructor.
    * @param useAll true if all classes/methods etc. should be included
    */
   public Binary (boolean useAll)
   {
      this.useAll = useAll;
   }

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
   
   /**
    * Dump debug data.
    *
    * @param fos FileOutputStream
    * @throws TinyVMException
    */
   public void dumpDebug (OutputStream fos) throws IOException
   {
	   DebugData.save(this.debugData, fos);
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
    * Return the class the represents an array of the given type and dimension.
    * 
    * 
    * @param elementClass
    * @return class record or null if not found or the array is a primitive array.
    * @throws TinyVMException
    */
   public ClassRecord getClassRecordForArray (ClassRecord elementClass) throws TinyVMException
   {
      int dims = 1;
      if (elementClass.isArray())
      {
          dims += elementClass.getArrayDimension();
      }
      int pSize = iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
          ClassRecord pRec = iClassTable.get(pIndex);
          if (pRec.getArrayDimension() == dims && pRec.getArrayElementClass() == elementClass)
              return pRec;
      }
      // Not found so we will create one...
      String sig = "";
      for(int i = 0; i < dims; i++)
          sig += "[";
      sig += elementClass.signature();
      return ClassRecord.storeArrayClass(sig, iClasses, iClassTable, null, this);
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
    * @param instance
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

   /**
    * Return true if unused methods/classes etc. should still be included in
    * the output file.
    * @return
    */
   public boolean useAll()
   {
       return useAll;
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
    * @return
    * @throws TinyVMException
    */
   public static Binary createFromClosureOf (String[] entryClassNames,
      ClassPath classPath, boolean all) throws TinyVMException
   {
      Binary result = new Binary(all);
      // From special classes and entry class, store closure
      result.processClasses(entryClassNames, classPath);
      // Store special signatures
      result.processSpecialSignatures();
      result.processConstants();
      result.processMethods();
      result.processFields();
      // Remove unused methods/classes/fields/constants.
      result.markUsed(entryClassNames);
      result.processOptimizedClasses();
      result.processOptimizedConstants();
      result.processOptimizedMethods();
      result.processOptimizedFields();
      // Copy code as is (first pass)
      result.processCode(false);
      result.storeComponents();
      result.initOffsets();
      // Post-process code after offsets are set (second pass)
      result.processCode(true);

      result.debugData.create(result);
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
      String[] specialClasses = SpecialConstants.CLASSES;
      //_logger.log(Level.INFO, "Starting with " + specialClasses.length
      //   + " special classes.");
      for (int i = 0; i < specialClasses.length; i++)
      {
         String className = specialClasses[i];
         if (className.charAt(0) == '[')
            ClassRecord.storeArrayClass(className, iClasses, iClassTable, classPath, this);
         else if (className.indexOf('/') != -1)
            addClassRecord(className, ClassRecord.getClassRecord(className, classPath, this));
         else
            addClassRecord(className, PrimitiveClassRecord.getClassRecord(className, this, (byte)i));
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

   /**
    * Optimize the number and order of classes.
    * We make a pass of all of the classes and select only those that are
    * actually used. We also optimize the order of the classes in an attempt
    * to minimize the size of the interface maps.
    * @throws TinyVMException
    */
   public void processOptimizedClasses () throws TinyVMException
   {
      RecordTable<ClassRecord> iNewClassTable = new RecordTable<ClassRecord>("class table", false, true);
      int pSize = iClassTable.size();
      // First copy over the special classes
      for (int pIndex = 0; pIndex < SpecialConstants.CLASSES.length; pIndex++)
      {
         ClassRecord classRecord = iClassTable.get(pIndex);
         iNewClassTable.add(classRecord);
      }
      // Now we add in any classes that have interfaces. This keeps them all
      // together and keeps the interface map small. Note duplicates are
      // not allowed so we can add the same entry multiple times.
      for (int pIndex = SpecialConstants.CLASSES.length; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = iClassTable.get(pIndex);
         if (classRecord.isInterface() && (useAll() || classRecord.used()))
            classRecord.storeOptimizedImplementingClasses(iNewClassTable);
      }
      // now add in the rest of the used classes
      for (int pIndex = SpecialConstants.CLASSES.length; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = iClassTable.get(pIndex);
         if (useAll() || classRecord.used())
            iNewClassTable.add(classRecord);
      }
      iClassTable = iNewClassTable;
      pSize = iClassTable.size();
      // Now we selected all of the classes we can fix up any linkages (which
      // may use the class index) and create the interface maps.
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = iClassTable.get(pIndex);
         classRecord.initParent();
         if (classRecord.isInterface())
            classRecord.storeInterfaceMap(iInterfaceMaps);
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
      String[] specialClasses = SpecialConstants.CLASSES;
      for (int i = 0; i < specialClasses.length; i++)
      {
         String className = specialClasses[i];
         ClassRecord classRecord = getClassRecord(className);
         classRecord.markUsed();
         classRecord.markInstanceUsed();
      }
      // Add the run method that is called directly from the vm
      Signature staticInit = new Signature(Constants.STATIC_INITIALIZER_NAME, "()V");
      Signature runMethod = new Signature("run", "()V");
      Signature mainMethod = new Signature("main", "([Ljava/lang/String;)V");
      // Now add entry classes      
      for (int i = 0; i < entryClassNames.length; i++)
      {
         ClassRecord classRecord = getClassRecord(entryClassNames[i]);
         classRecord.markUsed();
         classRecord.markInstanceUsed();
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
               if (useAll) classRecord.markMethods();
            }
         }
         // Finally mark starting from all of the entry classes
         for (int i = 0; i < entryClassNames.length; i++)
         {
            ClassRecord classRecord = getClassRecord(entryClassNames[i]);
            if (classRecord.hasMethod(mainMethod, true))
            {
                MethodRecord pRec = classRecord.getMethodRecord(mainMethod);
                classRecord.markMethod(pRec, true);
            }
         }

      } while (classCount != usedClassCount);
   }

   public void processSpecialSignatures ()
   {
      for (int i = 0; i < SpecialConstants.SIGNATURES.length; i++)
      {
         Signature pSig = new Signature(SpecialConstants.SIGNATURES[i]);
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

   /**
    * Store constant values in an optimal fashion.
    * We only include constants that are actually used. We also arrange to store
    * constant values correctly aligned so that they can be accessed directly
    * by the VM. We order the constants to allow fast access to the commanly used
    * int/float types.
    * @throws TinyVMException
    */
   public void processOptimizedConstants () throws TinyVMException
   {
      int pSize = iConstantTable.size();
      RecordTable<ConstantRecord> iOptConstantTable = new RecordTable<ConstantRecord>("constants", false, true);
      RecordTable<ConstantValue> iOptConstantValues = new RecordTable<ConstantValue>("constant values", true, true);

      for(int align : alignments)
      {
         for (int pIndex = 0; pIndex < pSize; pIndex++)
         {
            ConstantRecord pRec = iConstantTable.get(pIndex);
            if (pRec.constantValue().getAlignment() == align && (useAll() || pRec.used()))
            {
               iOptConstantTable.add(pRec);
               iOptConstantValues.add(pRec.constantValue());
            }
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
   public void processMethods () throws TinyVMException
   {
      int pSize = iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = iClassTable.get(pIndex);
         classRecord.storeMethods(iMethodTables, iExceptionTables, iSignatures,
            useAll());
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
      iMethodTables = new RecordTable<RecordTable<MethodRecord>>("methods", true, true);
      iExceptionTables = new RecordTable<RecordTable<ExceptionRecord>>("exceptions", false, true);

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

   public void printInterfaces () throws TinyVMException
   {
      int pSize = iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord pRec = iClassTable.get(pIndex);
         if (pRec.isInterface() && pRec.used())
         {
             System.out.println("Interface: " + pRec.iName + " is implemented by:");
             for(ClassRecord cr : pRec.iImplementedBy)
             {
                System.out.println("Active class: " + cr.iName + " id " + this.getClassIndex(cr));
             }
         }
      }

      
   }

   public void processOptimizedFields () throws TinyVMException
   {
      int pSize = iClassTable.size();
      // We need an optimized version of the static tables
      // so create new ones and repopulate.

      iStaticState = new RecordTable<StaticValue>("static state", true, true);
      iStaticFields = new RecordTable<StaticFieldRecord>("static fields", true, true);
      iInstanceFieldTables = new RecordTable<RecordTable<InstanceFieldRecord>>("instance fields", true, true);
      for(int align : alignments)
      {
          for (int pIndex = 0; pIndex < pSize; pIndex++)
          {
             ClassRecord pRec = iClassTable.get(pIndex);
             pRec.storeOptimizedStaticFields(iStaticFields, iStaticState, align);
          }
      }
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord pRec = iClassTable.get(pIndex);
         pRec.storeOptimizedFields(iInstanceFieldTables);
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
      // Master record and class table are always the first two, all
      // tables are aligned on 4 byte boundaries.
      iEntireBinary.add(iMasterRecord);
      iEntireBinary.add(iClassTable);
      // We do not need to store the static fields, just calculate layout
      iStaticStorage.add(iStaticState);
      iEntireBinary.add(iStaticFields);
      iEntireBinary.add(iConstantTable);
      iEntireBinary.add(iMethodTables);
      iEntireBinary.add(iExceptionTables);
      iEntireBinary.add(iInstanceFieldTables);
      iEntireBinary.add(iConstantValues);
      iEntireBinary.add(iInterfaceMaps);
      iEntireBinary.add(iEntryClassIndices);
      iEntireBinary.add(iCodeSequences);
   }

   public void initOffsets () throws TinyVMException
   {
      iEntireBinary.initOffset(0);
      iStaticStorage.initOffset(0);
   }

   public void setRunTimeOptions(int opt)
   {
       iMasterRecord.setRunTimeOptions(opt);
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
       monitor.log("Class " + pIndex + ": " + pRec.getCanonicalName());
     }
     int pSize = iMethodTables.size();
     int methodNo = 0;
     for (int i = 0; i < pSize; i++)
     {
        RecordTable<MethodRecord> rt = iMethodTables.get(i);
        int cnt = rt.size();
        for(int j = 0; j < cnt; j++)
        {
           MethodRecord mr = rt.get(j);
           
           // String s = "Method " + methodNo + ": Class: " + mr.iClassRecord.getName() + " Signature: " +
           //    (iSignatures.elementAt(mr.iSignatureId)).getImage();
      
           Signature sig = iSignatures.elementAt(mr.iSignatureId); 
           String s = "Method " + methodNo + ": " + toPrettyString(sig, mr.iClassRecord.getCanonicalName(), mr.iClassRecord.getSimpleName(), true);
      
           if ((mr.iFlags & TinyVMConstants.M_NATIVE) == 0)
              monitor.log(s + " PC " + mr.getCodeStart() + " Signature id " + mr.iSignatureId);
           else
              monitor.log(s + " Native id " + mr.iSignatureId);
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
     monitor.log("Exception records: " + getTotalNumExceptionRecords() + " (" + iExceptionTables.getLength() + " bytes).");
     monitor.log("Interface maps   : " + iInterfaceMaps.size() + " (" + iInterfaceMaps.getLength() + " bytes).");
     monitor.log("Code             : " + iCodeSequences.size() + " (" + iCodeSequences.getLength() + " bytes).");
     monitor.log("Total            : " + iEntireBinary.getLength() + " bytes.");
     monitor.log("Run time options : " + iMasterRecord.getRunTimeOptions());
     monitor.log("Constant loads   : " + this.constNormLoads + "N " + this.constOpLoads + "O " + this.constWideLoads + "W " + this.constString + "S");
     monitor.log("Static load/store: " + this.staticNormLoads + "N " + this.staticOpLoads + "O");
     monitor.log("Field  load/store: " + this.fieldNormOp + "N " + this.fieldOpOp + "O");
     //printInterfaces();
   }
   
   private static String toPrettyString(Signature sig, String fullclass, String simpleclass, boolean omitReturn)
   {
	   String name = sig.getName();
	   String descriptor = sig.getDescriptor();
	   
	   boolean omitEmptyArgs;
	   String friendlyName;
	   
	   Type[] args = Type.getArgumentTypes(descriptor);
	   Type rv = Type.getReturnType(descriptor);
	   
	   if (sig.isConstructor())
	   {
		   // alternative: friendlyName = simpleclass;
		   friendlyName = name;
		   omitEmptyArgs = false;
		   omitReturn |= rv.equals(Type.VOID);
	   }
	   else if (sig.isStaticInitializer())
	   {
		   // alternative: friendlyName = "static{}"
		   friendlyName = name;
		   omitEmptyArgs = true;
		   omitReturn |= rv.equals(Type.VOID);
	   }
	   else
	   {
		   friendlyName = name;
		   omitEmptyArgs = false;
	   }
		
	   StringBuilder sb = new StringBuilder();
	   if (!omitReturn)
	   {
		   sb.append(rv);
		   sb.append(' ');
	   }
	   if (fullclass != null)
	   {
		   sb.append(fullclass);
		   sb.append(".");
	   }
	   sb.append(friendlyName);
	   if (!omitEmptyArgs || args.length > 0)
	   {
		   sb.append('(');
		   for (int j=0; j<args.length; j++)
		   {
			   if (j > 0)
				   sb.append(", ");
			   sb.append(args[j]);
		   }
		   sb.append(')');
	   }
	   
	   return sb.toString();
   }

}

