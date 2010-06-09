package js.tinyvm;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.JavaClass;

public class CodeUtilities implements OpCodeConstants, OpCodeInfo
{
   String iFullName;
   JavaClass iCF;
   Binary iBinary;

   public CodeUtilities (String aMethodName, JavaClass aCF, Binary aBinary)
   {
      iFullName = fullMethod(aCF, aMethodName);
      iCF = aCF;
      iBinary = aBinary;
   }

   /**
    * Return the ClassRecord for a class signature.
    * @param className the signature to lookup.
    * @return the associated class record, or null if it is a primitive array.
    * @throws js.tinyvm.TinyVMException
    */
   ClassRecord getClassRecord(String className) throws TinyVMException
   {
      ClassRecord ret;
      ret = iBinary.getClassRecord(className);
      if (ret == null)
      {
         throw new TinyVMException("Bug CU-3: Didn't find class " + className
            + " from class " + iCF.getClassName() + " method " + this.iFullName);
      }
      return ret;
   }

   ClassRecord getClassRecord(int aPoolIndex) throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aPoolIndex); // TODO catch all (runtime) exceptions
      if (!(pEntry instanceof ConstantClass))
      {
         throw new TinyVMException("Classfile error: Instruction requiring "
            + "CONSTANT_Class entry got "
            + (pEntry == null? "null" : pEntry.getClass().getName()));
      }
      ConstantClass pClassEntry = (ConstantClass) pEntry;
      return getClassRecord(pClassEntry.getBytes(iCF.getConstantPool()));
   }


   public void exitOnBadOpCode (int aOpCode) throws TinyVMException
   {
      throw new TinyVMException("Unsupported " + OPCODE_NAME[aOpCode] + " in "
         + iFullName + ".\n"
         + "The following features/conditions are currently unsupported:\n"
         + "- Too many locals ( > 255).\n"
         + "- Too many constants ( > 1024).\n"
         + "- Too many static fields ( > 1024).\n"
         + "- Method code too long ( > 64 Kb!).\n" + "");
   }

   public static String fullMethod (JavaClass aCF, String aMethodName)
   {
      return aCF.getClassName() + ":" + aMethodName;
   }

   /**
    * Return the index of a given constant record
    * @param pRecord
    * @return the index of the record
    * @throws js.tinyvm.TinyVMException
    */
   int getConstantIndex(ConstantRecord pRecord) throws TinyVMException
   {
      int pIdx = iBinary.getConstantIndex(pRecord);

      if (pIdx == -1)
      {
         throw new TinyVMException("Bug CU-2: Didn't find constant " + pRecord.toString()
            + " of class " + iCF.getClassName());
      }
      return pIdx;
   }


   /**
    * Process a constant index.
    * Given a reference to the constant pool, return the corresponding index
    * into the leJOS constant table.
    * @param aPoolIndex the constant pool index
    * @return The constant table index
    * @throws js.tinyvm.TinyVMException
    */
   public int processConstantIndex (int aPoolIndex) throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aPoolIndex);
      if (!(pEntry instanceof ConstantInteger)
         && !(pEntry instanceof ConstantFloat)
         && !(pEntry instanceof ConstantString)
         && !(pEntry instanceof ConstantDouble)
         && !(pEntry instanceof ConstantLong)
         && !(pEntry instanceof ConstantClass))
      {
         throw new TinyVMException("Classfile error: LDC-type instruction "
            + "does not refer to a suitable constant. Class " + iCF.getClassName() + " method " + iFullName);
      }

      ConstantRecord pRecord = new ConstantRecord(iCF.getConstantPool(), pEntry, iBinary);
      return getConstantIndex(pRecord);
   }

   /**
    * Process a class index.
    * Given a constant pool index for a class object, return the corresponding
    * index into the leJOS class table.
    * @param aPoolIndex the constant pool index
    * @return the class table index
    * @throws js.tinyvm.TinyVMException
    */
   public int processClassIndex (int aPoolIndex) throws TinyVMException
   {
      ClassRecord pClassRecord = getClassRecord(aPoolIndex);

      int pIdx = iBinary.getClassIndex(pClassRecord);
      if (pIdx == -1)
      {
         throw new TinyVMException("Bug CU-3: Didn't find class " + pClassRecord.iName
            + " from class " + iCF.getClassName() + " method " + this.iFullName);
      }
      return pIdx;
   }

   /**
    * Process and array index.
    * Given an index into the constant pool for an array class, return the
    * corresponding index into the leJOS class table.
    * @param aPoolIndex the constant index for the array
    * @return the class table index.
    * @throws js.tinyvm.TinyVMException
    */
   public int processArray(int aPoolIndex) throws TinyVMException
   {
      ClassRecord pClassRecord = getClassRecord(aPoolIndex);
      ClassRecord pArray = iBinary.getClassRecordForArray(pClassRecord);
      if (pArray == null)
      {
         throw new TinyVMException("Classfile error: Failed to locate array class for " +
                 pClassRecord.iName + " in class " + iCF.getClassName() + " method " + this.iFullName);
      }
      int pIdx = iBinary.getClassIndex(pArray);
      if (pIdx == -1)
      {
         throw new TinyVMException("Bug CU-3: Didn't find class " + pClassRecord.iName
            + " from class " + iCF.getClassName() + " method " + this.iFullName);
      }
      return pIdx;
   }


   public int processMultiArray (int aPoolIndex) throws TinyVMException
   {
      ClassRecord pClassRecord = getClassRecord(aPoolIndex);

      int pIdx = iBinary.getClassIndex(pClassRecord);
      if (pIdx == -1)
      {
         throw new TinyVMException("Bug CU-3: Didn't find class " + pClassRecord.iName
            + " from class " + iCF.getClassName() + " method " + this.iFullName);
      }
      return pIdx;
   }


   StaticFieldRecord getStaticFieldRecord(int aFieldIndex)  throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aFieldIndex); // TODO catch all (runtime) exceptions
      if (!(pEntry instanceof ConstantFieldref))
      {
         throw new TinyVMException("Classfile error: Instruction requiring "
            + "CONSTANT_Fieldref entry got "
            + (pEntry == null? "null" : pEntry.getClass().getName()));
      }
      ConstantFieldref pFieldEntry = (ConstantFieldref) pEntry;
      String className = pFieldEntry.getClass(iCF.getConstantPool()).replace(
         '.', '/');
      ClassRecord pClassRecord = getClassRecord(className);
      if (pClassRecord == null)
      {
         throw new TinyVMException("Attempt to use a field from a primitive array " +
                 className + " from class " + iCF.getClassName() + " method " + iFullName);

      }
      ConstantNameAndType cnat = (ConstantNameAndType) iCF.getConstantPool()
         .getConstant(pFieldEntry.getNameAndTypeIndex());
      String pName = cnat.getName(iCF.getConstantPool());
      // First find the actual defining class
      StaticFieldRecord pFieldRecord = pClassRecord.getStaticFieldRecord(pName);
      if (pFieldRecord == null)
      {
          throw new TinyVMException("Failed to locate static field " + pName +
                  " refrenced via class " + className + " from class " + iCF.getClassName());
      }
      return pFieldRecord;
   }

   /**
    * Mark the class as being used.
    * @param aPoolIndex
    * @throws js.tinyvm.TinyVMException
    */
   public void markClass (int aPoolIndex) throws TinyVMException
   {
      ClassRecord pClassRecord = getClassRecord(aPoolIndex);
      iBinary.markClassUsed(pClassRecord, true);
   }

   /**
    * Mark an array as being used.
    * @param aPoolIndex The constant pool index for the array.
    * @throws js.tinyvm.TinyVMException
    */
   public void markArray(int aPoolIndex) throws TinyVMException
   {
       ClassRecord pClassRecord = getClassRecord(aPoolIndex);
       ClassRecord pArray = iBinary.getClassRecordForArray(pClassRecord);
       if (pArray == null)
       {
         throw new TinyVMException("Classfile error: Failed to locate array class for " +
                 pClassRecord.iName + " in class " + iCF.getClassName() + " method " + this.iFullName);
       }
       iBinary.markClassUsed(pArray, true);
   }

   /**
    * Mark a primitive array as being used.
    * @param type The primitive type of the array.
    * @throws js.tinyvm.TinyVMException
    */
   public void markPrimitiveArray(byte type) throws TinyVMException
   {
       ClassRecord pClassRecord = getClassRecord(TinyVMType.tinyVMType(type).cname());
       ClassRecord pArray = iBinary.getClassRecordForArray(pClassRecord);
       if (pArray == null)
       {
         throw new TinyVMException("Classfile error: Failed to locate array class for " +
                 pClassRecord.iName + " in class " + iCF.getClassName() + " method " + this.iFullName);
       }
       iBinary.markClassUsed(pArray, true);
   }
   /**
    * Mark the static field as being used.
    * @param aFieldIndex
    * @throws js.tinyvm.TinyVMException
    */
   void markStaticField (int aFieldIndex) throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aFieldIndex); // TODO catch all (runtime) exceptions
      if (!(pEntry instanceof ConstantFieldref))
      {
         throw new TinyVMException("Classfile error: Instruction requiring "
            + "CONSTANT_Fieldref entry got "
            + (pEntry == null? "null" : pEntry.getClass().getName()));
      }
      ConstantFieldref pFieldEntry = (ConstantFieldref) pEntry;
      String className = pFieldEntry.getClass(iCF.getConstantPool()).replace(
         '.', '/');
      ClassRecord pClassRecord = getClassRecord(className);
      if (pClassRecord == null) return;
      ConstantNameAndType cnat = (ConstantNameAndType) iCF.getConstantPool()
         .getConstant(pFieldEntry.getNameAndTypeIndex());
      String pName = cnat.getName(iCF.getConstantPool());
      iBinary.markClassUsed(pClassRecord, false);
      StaticFieldRecord pFieldRecord = pClassRecord.getStaticFieldRecord(pName);
      if (pFieldRecord == null)
      {
          throw new TinyVMException("Failed to mark/locate static field " + pName +
                 " refrenced via class " + className + " from class " + iCF.getClassName());
      }
      iBinary.markClassUsed(pFieldRecord.getClassRecord(), false);
      pFieldRecord.markUsed();
   }

   /**
    * Obtain the class that contains a specified static field.
    * @param aFieldIndex The index in the constant pool of the static field
    * @return The class record of the defining class
    * @throws js.tinyvm.TinyVMException
    */
   ClassRecord getStaticFieldClass (int aFieldIndex) throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aFieldIndex); // TODO catch all (runtime) exceptions
      if (!(pEntry instanceof ConstantFieldref))
      {
         throw new TinyVMException("Classfile error: Instruction requiring "
            + "CONSTANT_Fieldref entry got "
            + (pEntry == null? "null" : pEntry.getClass().getName()));
      }
      ConstantFieldref pFieldEntry = (ConstantFieldref) pEntry;
      String className = pFieldEntry.getClass(iCF.getConstantPool()).replace(
         '.', '/');
      ClassRecord pClassRecord = getClassRecord(className);
      if (pClassRecord == null)
      {
          throw new TinyVMException("Classfile error: Failed to find class "
            + className);

      }
      return pClassRecord;
   }


   /**
    * Return the name of a static field
    * @param aFieldIndex The constant pool index for the field.
    * @throws js.tinyvm.TinyVMException
    */
   String getStaticFieldName (int aFieldIndex) throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aFieldIndex); // TODO catch all (runtime) exceptions
      if (!(pEntry instanceof ConstantFieldref))
      {
         throw new TinyVMException("Classfile error: Instruction requiring "
            + "CONSTANT_Fieldref entry got "
            + (pEntry == null? "null" : pEntry.getClass().getName()));
      }
      ConstantFieldref pFieldEntry = (ConstantFieldref) pEntry;
      String className = pFieldEntry.getClass(iCF.getConstantPool()).replace(
         '.', '/');
      ClassRecord pClassRecord = getClassRecord(className);
      if (pClassRecord == null)
      {
          throw new TinyVMException("Classfile error: Failed to find class "
            + className);

      }
      ConstantNameAndType cnat = (ConstantNameAndType) iCF.getConstantPool()
         .getConstant(pFieldEntry.getNameAndTypeIndex());
      String pName = cnat.getName(iCF.getConstantPool());
      return pName;

   }

   /**
    * Check the field reference to see if it is for the special TYPE entry in a
    * primitive class.
    * @param aFieldIndex The constant pool index for the field
    * @throws js.tinyvm.TinyVMException
    */
   boolean isWrapperTYPEField (int aFieldIndex) throws TinyVMException
   {
      ClassRecord pClass = getStaticFieldClass(aFieldIndex);
      if (!pClass.isWrapper()) return false;
      String pName = getStaticFieldName(aFieldIndex);
      if (pName.equals("TYPE")) return true;
      return false;
   }

   /**
    * Mark a constant as being used.
    * @param aPoolIndex The constant pool index.
    * @throws js.tinyvm.TinyVMException
    */
   void markConstant(int aPoolIndex) throws TinyVMException
   {
      int constIdx = processConstantIndex(aPoolIndex);
      ConstantRecord pRec = iBinary.getConstantRecord(constIdx);
      pRec.markUsed();
   }
   /**
    * @return The word that should be written as parameter of an invocation
    *         opcode.
    * @throws TinyVMException
    */
   int processMethod (int aMethodIndex, boolean aSpecial, boolean aInterface)
      throws TinyVMException
   {
      MethodRecord pMethod = findMethod (aMethodIndex, aSpecial, aInterface);
      ClassRecord pTopClass = pMethod.getClassRecord();
      if (aSpecial)
      {
         int pClassIndex = iBinary.getClassIndex(pTopClass);
         assert pClassIndex != -1 && pClassIndex < TinyVMConstants.MAX_CLASSES: "Check: class index in range";
         int pMethodIndex = pTopClass.getMethodIndex(pMethod);
         assert pMethodIndex != -1
            && pMethodIndex < TinyVMConstants.MAX_METHODS: "Check: method index in range";
         // _logger.log(Level.INFO, "processMethod: special: " + pClassIndex
         //    + ", " + pMethodIndex);
         return (pClassIndex << 8) | (pMethodIndex & 0xFF);
      }
      else
      {
         int pNumParams = pMethod.getNumParameterWords() - 1;
         assert pNumParams < TinyVMConstants.MAX_PARAMETER_WORDS: "Check: number of parameters not to high";
         int pSignature = pMethod.getSignatureId();
         assert pSignature < TinyVMConstants.MAX_SIGNATURES: "Check: signature in range";
         return (pNumParams << TinyVMConstants.M_ARGS_SHIFT) | pSignature;
      }
   }

   /**
    * @return The word that should be written as parameter of an invocation
    *         opcode.
    * @throws TinyVMException
    */
   MethodRecord findMethod (int aMethodIndex, boolean aSpecial, boolean aInterface)
      throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aMethodIndex); // TODO catch all (runtime) exceptions
      if (!(pEntry instanceof ConstantCP))
      {
         throw new TinyVMException("Classfile error: Instruction requiring "
            + "CONSTANT_MethodRef or CONSTANT_InterfaceMethodRef " + "got "
            + (pEntry == null? "null" : pEntry.getClass().getName()));
      }
      ConstantCP pMethodEntry = (ConstantCP) pEntry;
      String className = pMethodEntry.getClass(iCF.getConstantPool()).replace(
         '.', '/');
      ConstantNameAndType pNT = (ConstantNameAndType) iCF.getConstantPool()
         .getConstant(pMethodEntry.getNameAndTypeIndex());
      Signature pSig = new Signature(pNT.getName(iCF.getConstantPool()), pNT
         .getSignature(iCF.getConstantPool()));
      if (className.startsWith("["))
      {
          // For arrays we use the methods contained in Object
             className = "java/lang/Object";
      }
      ClassRecord pClassRecord = getClassRecord(className);
      if (pClassRecord == null)
      {
         throw new TinyVMException("Bug CU-7: Didn't find class " + className
            + " from class " + iCF.getClassName());
      }
      MethodRecord pMethod;
      if (aInterface)
        pMethod = pClassRecord.getInterfaceMethodRecord(pSig);
      else if (aSpecial)
        pMethod = pClassRecord.getSpecialMethodRecord(pSig);
      else
        pMethod = pClassRecord.getVirtualMethodRecord(pSig);
      //if (pMethod == null)
          // _logger.log(Level.INFO, "Failed to find " + pSig + " class " + className);
      return pMethod;
   }
   
   /**
    * Process a constant load operation.
    * Given a reference to the constant pool, return the corresponding 
    * instruction and index required to load it.
    * @param aPoolIndex the constant pool index
    * @return The constant load instruction.
    * @throws js.tinyvm.TinyVMException
    */
   public int genConstantLoad (int aPoolIndex) throws TinyVMException
   {
      int idx = processConstantIndex(aPoolIndex);
      if (idx > TinyVMConstants.MAX_CONSTANTS) exitOnBadOpCode(OP_LDC);

      ConstantRecord pRecord = iBinary.getConstantRecord(idx);
      // Decide if we can use the optimized version of constant load to access
      // this value.
      int instruction;
      if (pRecord.constantValue().getAlignment() == 4 && idx < 256)
      {
         instruction = OP_LDC;
         iBinary.constOpLoads++;
      }
      else
      {
          // need to use normal form.
          instruction = OP_LDC_1 + idx/256;
          idx = idx % 256;
          iBinary.constNormLoads++;
          if (pRecord.constantValue().getTypeIndex() > TinyVMType.T_VOID_TYPE)
             iBinary.constString++;
      }
      return (instruction << 8) | idx;
   }

   /**
    * Generate and instruction to access a static field.
    * @param aPoolIndex The field to access
    * @param optInst The optimized version of the instruction
    * @param normInst The normal version of the instruction.
    * @return
    * @throws TinyVMException
    */
   public int genStaticAccess(int aPoolIndex, int optInst, int normInst) throws TinyVMException
   {
      StaticFieldRecord fieldRec = getStaticFieldRecord(aPoolIndex);
      ClassRecord classRec = fieldRec.getClassRecord();
      int classIndex = iBinary.getClassIndex(classRec);
      assert classIndex >= 0 && classIndex <= 0xFF: "Check: class index in range";
      String name = fieldRec.getName();
      int fieldIndex = classRec.getStaticFieldIndex(name);
      StaticValue valueRec = classRec.getStaticValue(name);
      int offset = classRec.getStaticFieldOffset(name);
      int instruction;
      // Can we generate optimized version of the instruction?      
      if (valueRec.getAlignment() == 4 && offset/4 < 256)
      {
          instruction = optInst;
          offset = offset/4;
          iBinary.staticOpLoads++;
      }
      else
      {
          // Need to use the normal form, check the index is ok
          if (fieldIndex > TinyVMConstants.MAX_STATICS) exitOnBadOpCode(optInst);
          offset = fieldIndex % 256;
          instruction = normInst + (fieldIndex/256);
          iBinary.staticNormLoads++;
      }
      return (instruction << 16) | (classIndex << 8) | offset;
   }

   /**
    * Generate an instruction to access an instance field.
    * We use an optimized version of the opcode if the field is aligned correctly.
    * @param aFieldIndex The field we need to access.
    * @param optInst The optimized version of the instruction.
    * @param normInst The normal version of the instruction.
    * @return The field access instruction
    * @throws TinyVMException
    */
   int genFieldAccess (int aFieldIndex, int optInst, int normInst) throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aFieldIndex); // TODO catch all (runtime) exceptions
      if (!(pEntry instanceof ConstantFieldref))
      {
         throw new TinyVMException("Classfile error: Instruction requiring "
            + "CONSTANT_Fieldref entry got "
            + (pEntry == null? "null" : pEntry.getClass().getName()));
      }
      ConstantFieldref pFieldEntry = (ConstantFieldref) pEntry;
      String className = pFieldEntry.getClass(iCF.getConstantPool()).replace(
         '.', '/');
      ClassRecord pClassRecord = getClassRecord(className);
      if (pClassRecord == null)
      {
         throw new TinyVMException("Attempt to use a field from a primitive array " +
                 className + " from class " + iCF.getClassName() + " method " + iFullName);

      }
      ConstantNameAndType cnat = (ConstantNameAndType) iCF.getConstantPool()
         .getConstant(pFieldEntry.getNameAndTypeIndex());
      String pName = cnat.getName(iCF.getConstantPool());
      int pOffset = pClassRecord.getInstanceFieldOffset(pName);
      if (pOffset == -1)
      {
         throw new TinyVMException("Error: Didn't find field " + className
            + ":" + pName + " from class " + iCF.getClassName());
      }
      assert pOffset <= TinyVMConstants.MAX_FIELD_OFFSET: "Check: field offset in range";
      TinyVMType fieldType = TinyVMType.tinyVMTypeFromSignature(cnat
         .getSignature(iCF.getConstantPool()));
      // Decide which form of the instruction to use.
      int instruction;
      if ((fieldType.type() == TinyVMType.T_INT_TYPE || fieldType.type() == TinyVMType.T_FLOAT_TYPE ||
              fieldType.type() == TinyVMType.T_REFERENCE_TYPE) && (pOffset & 0x3) == 0)
      {
         instruction = optInst;
         iBinary.fieldOpOp++;
      }
      else
      {
         instruction = normInst;
         iBinary.fieldNormOp++;
      }
      return (instruction << 16) | (fieldType.type() << TinyVMConstants.F_SIZE_SHIFT) | pOffset;
   }

   static int getAndCopyFourBytesInt( byte[] aCode, int ix, byte[] pOutCode, int ox)
   {
      int a = 0;
      
      a |= (aCode[ ix + 0] & 0xFF) << 24;
      a |= (aCode[ ix + 1] & 0xFF) << 16;
      a |= (aCode[ ix + 2] & 0xFF) << 8;
      a |= (aCode[ ix + 3] & 0xFF) << 0;

      for( int i = 0; i < 4; i ++)
         pOutCode[ ox + i] = aCode[ ix + i];

      return a;
   }   
   
   static int getFourBytesInt( byte[] aCode, int ix )
   {
      int a = 0;
      
      a |= (aCode[ ix + 0] & 0xFF) << 24;
      a |= (aCode[ ix + 1] & 0xFF) << 16;
      a |= (aCode[ ix + 2] & 0xFF) << 8;
      a |= (aCode[ ix + 3] & 0xFF) << 0;
      return a;
   }

   public byte[] processCode (byte[] aCode) throws TinyVMException
   {
      byte[] pOutCode = new byte[aCode.length];
      int i = 0;
      while (i < aCode.length)
      {
         pOutCode[i] = aCode[i];
         int pOpCode = pOutCode[i] & 0xFF;

         i++;
         //System.out.println("Opcode " + OPCODE_NAME[pOpCode] + " addr " + i);
         switch (pOpCode)
         {
            case OP_LDC:
               {
                  int inst = genConstantLoad(aCode[i] & 0xFF);
                  pOutCode[i-1] = (byte) (inst >> 8);
                  pOutCode[i++] = (byte) (inst & 0xFF);           
               }
               break;
            case OP_LDC_W:
               {
                  // Convert long version into short version plus noop
                  int inst = genConstantLoad((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF));
                  pOutCode[i-1] = (byte) (inst >> 8);
                  pOutCode[i++] = (byte) (inst & 0xFF);
                  pOutCode[i++] = (byte) OP_NOP;
                  iBinary.constWideLoads++;
               }
               break;
            case OP_LDC2_W:
               int pIdx1 = processConstantIndex((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF));
               pOutCode[i++] = (byte) (pIdx1 >> 8);
               pOutCode[i++] = (byte) (pIdx1 & 0xFF);
               break;
            case OP_ANEWARRAY:
               int pIdx5 = processArray((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF));
               // Write the two byte signature
               pOutCode[i++] = (byte) (pIdx5 >> 8);
               pOutCode[i++] = (byte) (pIdx5 & 0xFF);
               break;
            case OP_MULTIANEWARRAY:
               int pIdx2 = processMultiArray((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF));
               // Write the two byte signature
               pOutCode[i++] = (byte) (pIdx2 >> 8);
               pOutCode[i++] = (byte) (pIdx2 & 0xFF);
               // Include the number of actual dimensions required
               pOutCode[i] = aCode[i];
               i++;
               break;
            case OP_CHECKCAST:
            case OP_INSTANCEOF:
               int pIdx3 = processClassIndex((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF));
               pOutCode[i++] = (byte) (pIdx3 >> 8);
               pOutCode[i++] = (byte) (pIdx3 & 0xFF);
               break;
            case OP_NEW:
               int pIdx4 = processClassIndex((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF));
               assert pIdx4 < TinyVMConstants.MAX_CLASSES: "Check: class index in range";
               pOutCode[i++] = (byte) (pIdx4 >> 8);
               pOutCode[i++] = (byte) (pIdx4 & 0xFF);
               break;
            case OP_GETSTATIC:
               {
                  int idx = (aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF);
                  if (isWrapperTYPEField(idx))
                  {
                     // If this is one of the rather odd static fields in the
                     // wrapper classes, then replace reads of it with a load
                     // of the appropriate class constant.
                     idx = getConstantIndex(getStaticFieldClass(idx).getPrimitiveClass().getClassConstant());
                     pOutCode[i-1] = (byte)OP_LDC_1;
                     pOutCode[i++] = (byte) (idx & 0xFF);
                     pOutCode[i++] = (byte) OP_NOP;
                     break;
                  }
                  // fall through
                  int inst = genStaticAccess((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF), OP_GETSTATIC, OP_GETSTATIC_1);
                  pOutCode[i-1] = (byte)(inst >> 16);
                  pOutCode[i++] = (byte)(inst >> 8);
                  pOutCode[i++] = (byte)inst;
               }
               break;
            case OP_PUTSTATIC:
               {
                  int inst = genStaticAccess((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF), OP_PUTSTATIC, OP_PUTSTATIC_1);
                  pOutCode[i-1] = (byte)(inst >> 16);
                  pOutCode[i++] = (byte)(inst >> 8);
                  pOutCode[i++] = (byte)inst;
               }
               break;
            case OP_GETFIELD:
               {
                  int inst = genFieldAccess((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF), OP_GETFIELD, OP_GETFIELD_1);
                  pOutCode[i-1] = (byte) (inst >> 16);
                  pOutCode[i++] = (byte)(inst >> 8);
                  pOutCode[i++] = (byte) inst;
               }
               break;
            case OP_PUTFIELD:
               {
                  int inst = genFieldAccess((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF), OP_PUTFIELD, OP_PUTFIELD_1);
                  pOutCode[i-1] = (byte) (inst >> 16);
                  pOutCode[i++] = (byte)(inst >> 8);
                  pOutCode[i++] = (byte) inst;
               }
               break;
            case OP_INVOKEINTERFACE:
               // Opcode is changed:
               pOutCode[i - 1] = (byte) OP_INVOKEVIRTUAL;
               int pWord3 = processMethod((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF), false, true);
               pOutCode[i++] = (byte) (pWord3 >> 8);
               pOutCode[i++] = (byte) (pWord3 & 0xFF);
               pOutCode[i++] = (byte) OP_NOP; // before: count
               pOutCode[i++] = (byte) OP_NOP; // before: 0
               break;
            case OP_INVOKESPECIAL:
            case OP_INVOKESTATIC:
               // Opcode is changed:
               int pWord4 = processMethod((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF), true, false);
               pOutCode[i++] = (byte) (pWord4 >> 8);
               pOutCode[i++] = (byte) (pWord4 & 0xFF);
               break;
            case OP_INVOKEVIRTUAL:
               // Opcode is changed:
               int pWord5 = processMethod((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF), false, false);
               pOutCode[i++] = (byte) (pWord5 >> 8);
               pOutCode[i++] = (byte) (pWord5 & 0xFF);
               break;
            case OP_LOOKUPSWITCH:
               {
                  int oi = i;
                  while( (i % 4) != 0)
                     i ++;

                  int dft = getAndCopyFourBytesInt( aCode, i, pOutCode, oi); i += 4; oi += 4;
                  int npairs = getAndCopyFourBytesInt( aCode, i, pOutCode, oi); i += 4; oi += 4;
                  //System.out.println( "lookupswitch: dft: " + dft + ", npairs: " + npairs + ", padding: " + (i - oi));

                  for( int k = 0; k < npairs; k ++)
                  {
                     int idx = getAndCopyFourBytesInt( aCode, i, pOutCode, oi); i += 4; oi += 4;
                     int off = getAndCopyFourBytesInt( aCode, i, pOutCode, oi); i += 4; oi += 4;
                     //System.out.println( "lookupswitch: idx: " + idx + ", off: " + off);
                  }

                  while( oi < i)
                     pOutCode[oi++] = 0;
               }
               break;

            case OP_TABLESWITCH:
               {
                  int oi = i;
                  while( (i % 4) != 0)
                     i ++;

                  int dft = getAndCopyFourBytesInt( aCode, i, pOutCode, oi); i += 4; oi += 4;
                  int low = getAndCopyFourBytesInt( aCode, i, pOutCode, oi); i += 4; oi += 4;
                  int hig = getAndCopyFourBytesInt( aCode, i, pOutCode, oi); i += 4; oi += 4;
                  //System.out.println( "tableswitch: dft: " + dft + ", low: " + low + ", hig: " + hig + ", padding: " + (i - oi));

                  for( int k = low; k <= hig; k ++)
                  {
                     int idx = getAndCopyFourBytesInt( aCode, i, pOutCode, oi); i += 4; oi += 4;
                     //System.out.println( "tableswitch: idx: " + idx);
                  }

                  while( oi < i)
                     pOutCode[oi++] = 0;
               }
               break;

            case OP_WIDE:
                if( (aCode[i] & 0xFF) == OP_IINC && aCode[i+1] == 0)
                {
                   for( int k = 0; k < 5; k ++, i ++)
                      pOutCode[i] = aCode[i];
                   break;
                }
                // Fall through

            case OP_GOTO_W:
            case OP_JSR_W:
               exitOnBadOpCode(pOpCode);
               break;
            case OP_BREAKPOINT:
            {
               throw new TinyVMException("Invalid opcode detected: " + pOpCode
                  + " " + OPCODE_NAME[pOpCode]);
            }
            default:
               int pArgs = OPCODE_ARGS[pOpCode];
               if (pArgs == -1)
               {
                  throw new TinyVMException("Bug CU-1: Got " + pOpCode + " in "
                     + iFullName + ".");
               }
               for (int ctr = 0; ctr < pArgs; ctr++)
                  pOutCode[i + ctr] = aCode[i + ctr];
               i += pArgs;
               break;
         }
      }
      return pOutCode;
   }

   public void processCalls (byte[] aCode, JavaClass aClassFile, Binary aBinary) throws TinyVMException
   {
      int i = 0;
      while (i < aCode.length)
      {
         int pOpCode = aCode[i] & 0xFF;

         i++;
         //System.out.println("Opcode " + OPCODE_NAME[pOpCode] + " addr " + i);
         switch (pOpCode)
         {
            case OP_LDC:
               {
                  markConstant(aCode[i] & 0xFF);
                  i++;
               }
               break;
            case OP_LDC_W:
            case OP_LDC2_W:
               {
                  markConstant((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF));
                  i += 2;
               }
               break;

            case OP_ANEWARRAY:
                markArray((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF));
                i += 2;
                break;
            case OP_NEWARRAY:
                markPrimitiveArray(aCode[i]);
                i += 1;
                break;
            case OP_MULTIANEWARRAY:
                markClass((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF));
                i += 3;
                break;
            case OP_NEW:
            case OP_CHECKCAST:
            case OP_INSTANCEOF:
               markClass((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF));
               i += 2;
               break;
            case OP_GETSTATIC:
               {
                  int idx = (aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF);
                  if (isWrapperTYPEField(idx))
                  {
                     getStaticFieldClass(idx).getPrimitiveClass().getClassConstant().markUsed();
                  }
                  else
                     markStaticField((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF));
                  i += 2;
                  break;
              }
           case OP_PUTSTATIC:
               markStaticField((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF));
               i += 2;
               break;
            case OP_INVOKEINTERFACE:
               // Opcode is changed:
               // _logger.log(Level.INFO, "Interface");
               MethodRecord pMeth0 = findMethod((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF), false, true); 
               if (pMeth0 != null) pMeth0.getClassRecord().markMethod(pMeth0, true);
               i += 4;
               break;
            case OP_INVOKEVIRTUAL:
               // Opcode is changed:
               MethodRecord pMeth1 = findMethod((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF), false, false); 
               if (pMeth1 != null) pMeth1.getClassRecord().markMethod(pMeth1, true);
               i += 2;
               break;
            case OP_INVOKESPECIAL:
            case OP_INVOKESTATIC:
               // Opcode is changed:
               MethodRecord pMeth2 = findMethod((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF), true, false); 
               if (pMeth2 != null) pMeth2.getClassRecord().markMethod(pMeth2, true);
               i += 2;
               break;
            case OP_LOOKUPSWITCH:
               {
                   while( (i % 4) != 0)
                     i ++;

                  int dft = getFourBytesInt( aCode, i); i += 4;
                  int npairs = getFourBytesInt( aCode, i); i += 4;
                  //System.out.println( "lookupswitch: dft: " + dft + ", npairs: " + npairs + ", padding: " + (i - oi));
                  i += 8*npairs;
               }
               break;

            case OP_TABLESWITCH:
               {
                  while( (i % 4) != 0)
                     i ++;

                  int dft = getFourBytesInt( aCode, i); i += 4;
                  int low = getFourBytesInt( aCode, i); i += 4;
                  int hig = getFourBytesInt( aCode, i); i += 4;
                  //System.out.println( "tableswitch: dft: " + dft + ", low: " + low + ", hig: " + hig + ", padding: " + (i - oi));

                  for( int k = low; k <= hig; k ++)
                  {
                     i += 4;
                     //System.out.println( "tableswitch: idx: " + idx);
                  }

              }
               break;

            case OP_WIDE:
                if( (aCode[i] & 0xFF) == OP_IINC && aCode[i+1] == 0)
                {
                   i += 5;
                   break;
                }
                // Fall Through
            case OP_GOTO_W:
            case OP_JSR_W:
            //case OP_FREM:
            //case OP_DREM:
               exitOnBadOpCode(pOpCode);
               break;             
            default:
               int pArgs = OPCODE_ARGS[pOpCode];
               if (pArgs == -1)
               {
                  throw new TinyVMException("Bug CU-1: Got " + pOpCode + " in "
                     + iFullName + ".");
               }
               i += pArgs;
         }

      }
   }

   // private static final Logger _logger = Logger.getLogger("TinyVM");
}
