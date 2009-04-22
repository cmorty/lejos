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
    * Return the ClassRecord for a class signature. Handle the special case of
    * arrays of objects.
    * @param className the signature to lookup.
    * @return the associated class record, or null if it is a primitive array.
    * @throws js.tinyvm.TinyVMException
    */
   ClassRecord getClassRecord(String className) throws TinyVMException
   {
      ClassRecord ret;
      // Deal with arrays of objects
      if (className.startsWith("["))
      {
          ret = iBinary.getArrayClassRecord(className);
      }
      else
      {
          ret = iBinary.getClassRecord(className);
          if (ret == null)
          {
             throw new TinyVMException("Bug CU-3: Didn't find class " + className
                + " from class " + iCF.getClassName());
          }
      }
      return ret;
   }


   public void exitOnBadOpCode (int aOpCode) throws TinyVMException
   {
      throw new TinyVMException("Unsupported " + OPCODE_NAME[aOpCode] + " in "
         + iFullName + ".\n"
         + "The following features/conditions are currently unsupported:\n"
         + "- Arithmetic or logical operations on variables of type long.\n"
         + "- Remainder operations on floats or doubles.\n"
         + "- Too many locals ( > 255).\n"
         + "- Too many constants ( > 1024).\n"
         + "- Too many static fields ( > 1024).\n"
         + "- Method code too long ( > 64 Kb!).\n" + "");
   }

   public static String fullMethod (JavaClass aCF, String aMethodName)
   {
      return aCF.getClassName() + ":" + aMethodName;
   }


   public int processConstantIndex (int aPoolIndex) throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aPoolIndex); // TODO catch all (runtime) exceptions

      if (!(pEntry instanceof ConstantInteger)
         && !(pEntry instanceof ConstantFloat)
         && !(pEntry instanceof ConstantString)
         && !(pEntry instanceof ConstantDouble)
         && !(pEntry instanceof ConstantLong))
      {
         throw new TinyVMException("Classfile error: LDC-type instruction "
            + "does not refer to a suitable constant. ");
      }

      ConstantRecord pRecord = new ConstantRecord(iCF.getConstantPool(), pEntry);
      int pIdx = iBinary.getConstantIndex(pRecord);

      if (pIdx == -1)
      {
         throw new TinyVMException("Bug CU-2: Didn't find constant " + pEntry
            + " of class " + iCF.getClassName());
      }
      return pIdx;
   }

   public int processClassIndex (int aPoolIndex) throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aPoolIndex); // TODO catch all (runtime) exceptions
      if (!(pEntry instanceof ConstantClass))
      {
         throw new TinyVMException("Classfile error: Instruction requiring "
            + "CONSTANT_Class entry got "
            + (pEntry == null? "null" : pEntry.getClass().getName()));
      }
      ConstantClass pClassEntry = (ConstantClass) pEntry;
      String pClassName = pClassEntry.getBytes(iCF.getConstantPool());
      if (pClassName.startsWith("["))
      {
         // Handle the special case of array types.
         int pTypeDim = getTypeAndDimensions(pClassName);
         return pTypeDim;
      }
      int pIdx = iBinary.getClassIndex(pClassName);
      if (pIdx == -1)
      {
         throw new TinyVMException("Bug CU-3: Didn't find class " + pEntry
            + " from class " + iCF.getClassName() + " name " + pClassName);
      }
      return pIdx;
   }

   public int processMultiArray (int aPoolIndex) throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aPoolIndex); // TODO catch all (runtime) exceptions
      if (!(pEntry instanceof ConstantClass))
      {
         throw new TinyVMException("Classfile error: Instruction requiring "
            + "CONSTANT_Class entry got "
            + (pEntry == null? "null" : pEntry.getClass().getName()));
      }
      ConstantClass pClassEntry = (ConstantClass) pEntry;
      // TODO fix this?
      int pTypeDim = getTypeAndDimensions(pClassEntry.getBytes(iCF
         .getConstantPool()));


      return pTypeDim;
   }

   public int getTypeAndDimensions (String aMultiArrayDesc) throws TinyVMException
   {
      int i = 0;
      while (aMultiArrayDesc.charAt(i) == '[')
         i++;
      if (i > TinyVMConstants.MAX_DIMS)
      {
         throw new TinyVMException("In " + iFullName
            + ": Multi-dimensional arrays are limited to " + TinyVMConstants.MAX_DIMS );
      }

      int typ = TinyVMType.tinyVMTypeFromSignature(aMultiArrayDesc.substring(i)).type();
      int cls = 0;
      if (typ == TinyVMType.T_OBJECT_TYPE || typ == TinyVMType.T_REFERENCE_TYPE)
      {
          // We have an object type so lookup the class index...
          String name = aMultiArrayDesc.substring(i+1, aMultiArrayDesc.length()-1);
          cls = iBinary.getClassIndex(name);
          if (cls == -1)
          {
             throw new TinyVMException("Bug CU-3: Didn't find class " + name
                + " from class " + aMultiArrayDesc);
          }
          typ = TinyVMType.T_REFERENCE_TYPE;
      }
      // Now construct the array index signature
     int sig = (i << 12) | (typ << 8) | cls;
     return sig;
   }

   /**
    * @return The word that should be written as parameter of putstatic,
    *         getstatic, getfield, or putfield.
    * @throws TinyVMException
    */
   int processField (int aFieldIndex, boolean aStatic) throws TinyVMException
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
      if (aStatic)
      {
         // First find the actual defining class
         StaticFieldRecord pFieldRecord = pClassRecord.getStaticFieldRecord(pName);
         if (pFieldRecord == null)
         {
             throw new TinyVMException("Failed to locate static field " + pName +
                     " refrenced via class " + className + " from class " + iCF.getClassName());            
         }
         int pClassIndex = iBinary.getClassIndex(pFieldRecord.getClassRecord());
         assert pClassIndex >= 0 && pClassIndex <= 0xFF: "Check: class index in range";
         int pFieldIndex = pFieldRecord.getClassRecord().getStaticFieldIndex(pName);
         assert pFieldIndex >= 0 && pFieldIndex <= 0x03FF: "Check: field index in range";

         return (pClassIndex << 16) | pFieldIndex;
      }
      else
      {
         int pOffset = pClassRecord.getInstanceFieldOffset(pName);
         if (pOffset == -1)
         {
            throw new TinyVMException("Error: Didn't find field " + className
               + ":" + pName + " from class " + iCF.getClassName());
         }
         assert pOffset <= TinyVMConstants.MAX_FIELD_OFFSET: "Check: field offset in range";
         TinyVMType fieldType = TinyVMType.tinyVMTypeFromSignature(cnat
            .getSignature(iCF.getConstantPool()));
         return (fieldType.type() << TinyVMConstants.F_SIZE_SHIFT) | pOffset;
      }
   }


   /**
    * Mark the class as being used.
    * @param aPoolIndex
    * @return
    * @throws js.tinyvm.TinyVMException
    */
   public void markClass (int aPoolIndex) throws TinyVMException
   {
      Constant pEntry = iCF.getConstantPool().getConstant(aPoolIndex); // TODO catch all (runtime) exceptions
      if (!(pEntry instanceof ConstantClass))
      {
         throw new TinyVMException("Classfile error: Instruction requiring "
            + "CONSTANT_Class entry got "
            + (pEntry == null? "null" : pEntry.getClass().getName()) + " method " + iFullName);
      }
      ConstantClass pClassEntry = (ConstantClass) pEntry;
      String pClassName = pClassEntry.getBytes(iCF.getConstantPool());
      ClassRecord pClassRecord = getClassRecord(pClassName);
      if (pClassRecord != null)
         iBinary.markClassUsed(pClassRecord, true);
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
    * @return The word that should be written as parameter of an invocation
    *         opcode.
    * @throws TinyVMException
    */
   int processMethod (int aMethodIndex, boolean aSpecial, boolean aInterface)
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
            + " from class " + iCF.getClassName() + " method " + iFullName);
      }

      MethodRecord pMethod;
      if (aInterface)
        pMethod = pClassRecord.getInterfaceMethodRecord(pSig);
      else
        pMethod = pClassRecord.getVirtualMethodRecord(pSig);
      if (pMethod == null)
      {
         throw new TinyVMException("Method " + pSig + " not found  in "
            + className +" interface " + aInterface);
      }
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
      else
        pMethod = pClassRecord.getVirtualMethodRecord(pSig);
      //if (pMethod == null)
          // _logger.log(Level.INFO, "Failed to find " + pSig + " class " + className);
      return pMethod;
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
               int constIdx = processConstantIndex(aCode[i] & 0xFF);
               if( constIdx >= 256)
               {
                  pOutCode[i-1] = (byte) (OP_LDC_1 + (constIdx - 256)/256);
                  if (constIdx > TinyVMConstants.MAX_CONSTANTS) exitOnBadOpCode(pOpCode);

                  //System.out.println("Large constant index value " + constIdx);
               }
               pOutCode[i++] = (byte) constIdx;
               break;
            case OP_LDC_W:
            case OP_LDC2_W:
               int pIdx1 = processConstantIndex((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF));
               pOutCode[i++] = (byte) (pIdx1 >> 8);
               pOutCode[i++] = (byte) (pIdx1 & 0xFF);
               break;
            case OP_ANEWARRAY:
               int pIdx5 = processClassIndex((aCode[i] & 0xFF) << 8
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
            case OP_PUTSTATIC:
            case OP_GETSTATIC:
               int pWord1 = processField((aCode[i] & 0xFF) << 8
                  | (aCode[i + 1] & 0xFF), true);
               pOutCode[i++] = (byte) (pWord1 >> 16);
               int fldIdx = pWord1 & 0x03FF;
               if( fldIdx >= 256)
               {
                  int newOpCode;
                  if (fldIdx > TinyVMConstants.MAX_STATICS) exitOnBadOpCode(pOpCode);

                  if( (aCode[i-2] & 0xFF) == OP_PUTSTATIC)
                     newOpCode = OP_PUTSTATIC_1 + (fldIdx - 256) / 256 * 2;
                  else
                     newOpCode = OP_GETSTATIC_1 + (fldIdx - 256) / 256 * 2;

                  pOutCode[i-2] = (byte) (newOpCode & 0xFF);

                  // System.out.println( "large index of static field " + newOpCode + " - " + fldIdx);
               }
               pOutCode[i++] = (byte) (pWord1 & 0xFF);
               break;
            case OP_PUTFIELD:
            case OP_GETFIELD:
               {
                  int pWord2 = processField((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF), false);
                  int opcode = aCode[i-1] & 0xFF;
                  byte b0 = (byte) (pWord2 >> 8);
                  byte b1 = (byte) (pWord2 & 0xFF);

                  if( false)
                  {
                     int fieldType = (b0 >>> 4) & 0x0F;
                     if( fieldType == TinyVMType.T_BOOLEAN_TYPE || fieldType == TinyVMType.T_BYTE_TYPE)
                        opcode = opcode == OP_GETFIELD ? OP_GETFIELD_S1 : OP_PUTFIELD_S1;
                     else
                     if( fieldType == TinyVMType.T_SHORT_TYPE)
                        opcode = opcode == OP_GETFIELD ? OP_GETFIELD_S2 : OP_PUTFIELD_S2;
                     else
                     if( fieldType == TinyVMType.T_CHAR_TYPE)
                        opcode = opcode == OP_GETFIELD ? OP_GETFIELD_U2 : OP_PUTFIELD_U2;
                     else
                     if( fieldType == TinyVMType.T_INT_TYPE || fieldType == TinyVMType.T_FLOAT_TYPE)
                        opcode = opcode == OP_GETFIELD ? OP_GETFIELD_W4 : OP_PUTFIELD_W4;
                     else
                     if( fieldType == TinyVMType.T_REFERENCE_TYPE)
                        opcode = opcode == OP_GETFIELD ? OP_GETFIELD_A4 : OP_PUTFIELD_A4;

                     if( opcode != OP_GETFIELD && opcode != OP_PUTFIELD)
                     {
                        b0 = b1;
                        b1 = 0;
                        // System.out.println( "quick field opcode: " + opcode + " - " + b0);
                     }
                  }

                  pOutCode[i-1] = (byte) opcode;
                  pOutCode[i++] = b0;
                  pOutCode[i++] = b1;
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
            case OP_FREM:
            case OP_DREM:
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
                  int constIdx = processConstantIndex(aCode[i] & 0xFF);
                  ConstantRecord pRec = iBinary.getConstantRecord(constIdx);
                  pRec.markUsed();
                  i++;
               }
               break;
            case OP_LDC_W:
            case OP_LDC2_W:
               {
                  int constIdx = processConstantIndex((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF));
                  ConstantRecord pRec = iBinary.getConstantRecord(constIdx);
                  pRec.markUsed();
                  i += 2;
               }
               break;

            case OP_ANEWARRAY:
            case OP_NEW:
            case OP_CHECKCAST:
            case OP_INSTANCEOF:
               markClass((aCode[i] & 0xFF) << 8 | (aCode[i + 1] & 0xFF));
               i += 2;
               break;

            case OP_PUTSTATIC:
            case OP_GETSTATIC:
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
            case OP_FREM:
            case OP_DREM:
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
