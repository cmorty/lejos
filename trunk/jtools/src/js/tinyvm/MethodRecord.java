package js.tinyvm;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import js.tinyvm.io.IByteWriter;
import js.tinyvm.io.IOUtilities;
import js.tinyvm.util.HashVector;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

public class MethodRecord implements WritableData
{
   Method iMethod;
   ClassRecord iClassRecord;
   RecordTable<ExceptionRecord> iExceptionTable = null;
   CodeSequence iCodeSequence = null;

   int iSignatureId; // DONE
   int iNumLocals; // DONE
   int iNumOperands; // DONE
   int iNumParameters; // DONE
   int iNumExceptionHandlers; // DONE
   int iFlags; // DONE
   boolean isCalled;
   int iCodeStart;
   HashSet<MethodRecord> iIsHiddenBy = new HashSet<MethodRecord>();
   int markCount = -1;

   public MethodRecord (Method aEntry, Signature aSignature,
      ClassRecord aClassRec, Binary aBinary, RecordTable<RecordTable<ExceptionRecord>> aExceptionTables,
      HashVector<Signature> aSignatures) throws TinyVMException
   {
      iClassRecord = aClassRec;
      iMethod = aEntry;
      Code pCodeAttrib = iMethod.getCode();
      boolean pNoBody = iMethod.isAbstract() || iMethod.isNative();
      assert pCodeAttrib != null || pNoBody: "Check: body is present";
      assert pCodeAttrib == null || !pNoBody: "Check: no body is present";
      aSignatures.addElement(aSignature);
      iSignatureId = aSignatures.indexOf(aSignature);
      if (iSignatureId >= TinyVMConstants.MAX_SIGNATURES)
      {
         throw new TinyVMException(
            "The total number of unique signatures exceeds "
               + TinyVMConstants.MAX_SIGNATURES);
      }
      iNumLocals = pCodeAttrib == null? 0 : pCodeAttrib.getMaxLocals();
      if (iNumLocals > TinyVMConstants.MAX_LOCALS)
      {
         throw new TinyVMException("Method " + aClassRec.getName() + "."
            + iMethod.getName() + " has " + iNumLocals + " local words. Only "
            + TinyVMConstants.MAX_LOCALS + " are allowed.");
      }
      iNumOperands = pCodeAttrib == null? 0 : pCodeAttrib.getMaxStack();
      if (iNumOperands > TinyVMConstants.MAX_OPERANDS)
      {
         throw new TinyVMException("Method " + aClassRec.getName() + "."
            + iMethod.getName() + " has an operand stack "
            + " whose potential size is " + iNumOperands + ". " + "Only "
            + TinyVMConstants.MAX_OPERANDS + " are allowed.");
      }
      iNumParameters = getNumParamWords(iMethod);
      if (iNumParameters > TinyVMConstants.MAX_PARAMETER_WORDS)
      {
         throw new TinyVMException("Method " + aClassRec.getName() + "."
            + iMethod.getName() + " has " + iNumParameters
            + " parameter words. Only " + TinyVMConstants.MAX_PARAMETER_WORDS
            + " are allowed.");
      }
      if (iMethod.isNative() && !aBinary.isSpecialSignature(aSignature))
      {
         throw new TinyVMException("Method " + aClassRec.getName() + "."
         	+ iMethod.getName() + " is an unknown native method."
         	+ " You are probably using JDK APIs"
            + " or libraries that cannot be run under leJOS.");
      }

      if (pCodeAttrib != null)
      {
         iExceptionTable = new RecordTable<ExceptionRecord>("exceptions", true, false);
         CodeException[] pExcepTable = pCodeAttrib.getExceptionTable();
         iNumExceptionHandlers = pExcepTable.length;
         if (iNumExceptionHandlers > TinyVMConstants.MAX_EXCEPTION_HANDLERS)
         {
            throw new TinyVMException("Method " + aClassRec.getName() + "."
               + iMethod.getName() + " has " + iNumExceptionHandlers
               + " exception handlers. Only "
               + TinyVMConstants.MAX_EXCEPTION_HANDLERS + " are allowed.");
         }
         storeExceptionTable(pExcepTable, aBinary, aClassRec.iCF);
         aExceptionTables.add(iExceptionTable);
      }
      initFlags();
   }

   public int getFlags ()
   {
      return iFlags;
   }

   public ClassRecord getClassRecord ()
   {
      return iClassRecord;
   }

   public void initFlags ()
   {
      iFlags = 0;
      if (iMethod.isNative())
         iFlags |= TinyVMConstants.M_NATIVE;
      if (iMethod.isSynchronized())
         iFlags |= TinyVMConstants.M_SYNCHRONIZED;
      if (iMethod.isStatic())
         iFlags |= TinyVMConstants.M_STATIC;
   }

   public void copyCode (RecordTable<CodeSequence> aCodeSequences, JavaClass aClassFile,
      Binary aBinary)
   {
      Code pCodeAttrib = iMethod.getCode();
      if (pCodeAttrib != null)
      {
         iCodeSequence = new CodeSequence();
         copyCode(pCodeAttrib.getCode(), aClassFile, aBinary);
         aCodeSequences.add(iCodeSequence);
      }
   }

   public void postProcessCode (RecordTable<CodeSequence> aCodeSequences,
      JavaClass aClassFile, Binary aBinary) throws TinyVMException
   {
      Code pCodeAttrib = iMethod.getCode();
      if (pCodeAttrib != null)
      {
         postProcessCode(pCodeAttrib.getCode(), aClassFile, aBinary);
         iCodeStart = (iCodeSequence == null? 0 : iCodeSequence.getOffset());

      }
   }

   /**
    * Number of parameter words, including <code>this</code>.
    * 
    * @param aMethod bcel method object
    */
   public static int getNumParamWords (Method aMethod)
   {
      Type[] types = aMethod.getArgumentTypes();
      int pWords = 0;
      for (int i = 0; i < types.length; i++)
      {
         assert types[i].getType() <= 0xF: "Check: known type";
         switch (types[i].getType())
         {
            case Constants.T_LONG:
            case Constants.T_DOUBLE:
               pWords += 2;
               break;
            default:
               pWords++;
         }
      }
      return pWords + (aMethod.isStatic()? 0 : 1);
   }

   public void storeExceptionTable (CodeException[] aExcepTable,
      Binary aBinary, JavaClass aCF)
   {
      for (int i = 0; i < aExcepTable.length; i++)
      {
         CodeException pExcep = aExcepTable[i];
         try
         {
            iExceptionTable.add(new ExceptionRecord(pExcep, aBinary, aCF));
         }
         catch (Throwable t)
         {
            t.printStackTrace();
         }
      }
   }

   public void copyCode (byte[] aCode, JavaClass aClassFile, Binary aBinary)
   {
      if (aCode == null)
         return;
      iCodeSequence.setBytes(aCode);
   }

   public void postProcessCode (byte[] aCode, JavaClass aClassFile,
      Binary aBinary) throws TinyVMException
   {
      if (aCode == null)
         return;
      CodeUtilities pUtils = new CodeUtilities(iMethod.getName().toString(),
         aClassFile, aBinary);
      byte[] pNewCode = pUtils.processCode(aCode);
      iCodeSequence.setBytes(pNewCode);
   }

   public int getLength ()
   {
      return IOUtilities.adjustedSize(2 + // signature
         2 + // exception table offset
         4 + // flags and code offset
         1 + // number of locals
         1 + // max. operands
         1 + // number of parameters
         1,  // number of exception handlers
         2);
   }

   public void dump (IByteWriter aOut) throws TinyVMException
   {
      try
      {
         aOut.writeU2(iSignatureId);
         aOut.writeU2(iExceptionTable == null? 0 : iExceptionTable.getOffset());
         iCodeStart = (iCodeSequence == null? 0 : iCodeSequence.getOffset());
         aOut.writeU4(iFlags << 24 | iCodeStart);
         aOut.writeU1(iNumLocals);
         aOut.writeU1(iNumOperands);
         aOut.writeU1(iNumParameters);
         aOut.writeU1(iNumExceptionHandlers);
         IOUtilities.writePadding(aOut, 2);
      }
      catch (IOException e)
      {
         throw new TinyVMException(e.getMessage(), e);
      }
   }

   public int getNumParameterWords ()
   {
      return iNumParameters;
   }

   public int getSignatureId ()
   {
      return iSignatureId;
   }

   public boolean equals (Object aOther)
   {
      if (!(aOther instanceof MethodRecord))
         return false;
      
      MethodRecord mr = (MethodRecord)aOther;
      return mr.iMethod.equals(iMethod) &&
              mr.iClassRecord.equals(iClassRecord);
   }

   public int hashCode ()
   {
      return iMethod.hashCode() ^ iClassRecord.hashCode();
   }

   private static final Logger _logger = Logger.getLogger("TinyVM");
   
   public void markCalled (JavaClass aClassFile, Binary aBinary) throws TinyVMException
   {
      // Mark the current method as being called. Then process the associated 
      // byte code looking for other called methods.
      // _logger.log(Level.INFO, "Marking :" + iClassRecord.getName() + " : " + iMethod.getName());
      if (isCalled && markCount == aBinary.getGeneration()) return;
      isCalled = true;
      markCount = aBinary.getGeneration();
      for (Iterator<MethodRecord> iter = iIsHiddenBy.iterator(); iter.hasNext();)
      {
         MethodRecord pMeth = iter.next();
         // _logger.log(Level.INFO, "Marking sub method " + pMeth.iMethod.getName());
         pMeth.iClassRecord.markMethod(pMeth, false);
      }
      Code pCodeAttrib = iMethod.getCode();
      if (pCodeAttrib == null) return;
      byte [] aCode = pCodeAttrib.getCode();
      if (aCode == null) return;
      CodeUtilities pUtils = new CodeUtilities(iMethod.getName().toString(),
         aClassFile, aBinary);
      
      try 
      {
         pUtils.processCalls(aCode, aClassFile, aBinary);
      }
      catch (TinyVMException e)
      {
         Signature s = aBinary.iSignatures.elementAt(this.iSignatureId);
         throw new TinyVMException("Error processing method "+s+" of class "+this.iClassRecord.getName(), e);
         //TODO unfortunately, this method is recursive hence the exception chain gets very long. Not sure how to workaround that.
      }
   }
   
   public boolean isCalled()
   {
       return isCalled;
   }
   
   public int getCodeStart()
   {
       return iCodeStart;
   }
   
   public void setHiddenBy(MethodRecord pRec)
   {
       // Mark this method as being hidden by a sub-class method.
       // We need to mark all such methods if this method is ever marked.
       // _logger.log(Level.INFO, "Count is " + iIsHiddenBy.size());
       if (!iIsHiddenBy.contains(pRec))
       {
          iIsHiddenBy.add(pRec);
       }
   }
   
   public RecordTable<ExceptionRecord> getExceptions()
   {
       return iExceptionTable;
   }
}
   

