package js.tinyvm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Signature;

/**
 *
 * @author Michael Mirwaldt (programCounterToLineNumberMap() added)
 * @author andys
 */
public class DebugData implements Serializable
{
   public static class LineNo implements Serializable
   {
      public int pc;
      public int line;

      LineNo(int pc, int line)
      {
         this.pc = pc;
         this.line = line;
      }
   }

   public static class LocalVar implements Serializable
   {
      public String name;
      public String signature;
      public String genericSignature;
      public int fromPc;
      public int length;
      public int index;

      LocalVar(String name, String signature, int fromPc, int length, int index)
      {
         super();
         this.name = name;
         this.signature = signature;
         this.fromPc = fromPc;
         this.length = length;
         this.index = index;
      }
   }

   public static class MethodData implements Serializable
   {
      public int id;
      public ClassData classData;
      public String name;
      public String signature;
      public String genericSignature;
      public int modifiers;
      public int codeOffset;
      public int codeLength;
      public int numParamWords;
      public LineNo[] lineNumbers;
      public LocalVar[] localVariables;

      MethodData(int id, ClassData classData, String name, String signature,
            int modifiers, int codeOffset, int codeLength, int numParamWords,
            LineNo[] lineNumbers, LocalVar[] localVariables)
      {
         super();
         this.id = id;
         this.classData = classData;
         this.name = name;
         this.signature = signature;
         this.modifiers = modifiers;
         this.codeOffset = codeOffset;
         this.codeLength = codeLength;
         this.numParamWords = numParamWords;
         this.lineNumbers = lineNumbers;
         this.localVariables = localVariables;
      }

      @Override
      public String toString()
      {
         StringBuilder sb = new StringBuilder();
         sb.append(classData.name);
         sb.append(".");
         sb.append(name);
         sb.append(signature);

         sb.append(" id=");
         sb.append(id);

         return sb.toString();
      }
   }

   public static class FieldData implements Serializable
   {
      public int id;
      public String name;
      public String signature;
      public String genericSignature;
      public int modifiers;
      public int offset;

      FieldData(int id, String name, String signature, int modifiers, int offset)
      {
         super();
         this.id = id;
         this.name = name;
         this.signature = signature;
         this.modifiers = modifiers;
         this.offset = offset;
      }

      @Override
      public String toString()
      {
         StringBuilder sb = new StringBuilder();
         sb.append(name);
         sb.append(signature);

         sb.append(" id=");
         sb.append(id);

         return sb.toString();
      }
   }

   public static class ClassData implements Serializable
   {
      public int id;
      public String name;
      public String signature;
      public String genericSignature;
      public int modifiers;
      public String sourceName;

      public ArrayList<js.tinyvm.DebugData.FieldData> fields;
      public ArrayList<js.tinyvm.DebugData.MethodData> methods;
      public int superclass;
      public int[] interfaces = {};// For interfaces: all parent interfaces
      // public String[]subClasses;//For interfaces: all implementing classes
      // public String[]subInterfaces;//For interfaces: all child interfaces
      public int classSize;

      ClassData(int id, String name, String signature)
      {
         super();
         this.id = id;
         this.name = name;
         this.signature = signature;
         this.fields = new ArrayList<FieldData>();
         this.methods = new ArrayList<MethodData>();
      }

      ClassData(int id, String name, String signature, int modifiers,
            String sourceName, int superclass, int[] interfaces, int classSize)
      {
         super();
         this.id = id;
         this.name = name;
         this.signature = signature;
         this.modifiers = modifiers;
         this.sourceName = sourceName;
         this.fields = new ArrayList<FieldData>();
         this.methods = new ArrayList<MethodData>();
         this.superclass = superclass;
         this.interfaces = interfaces;
         this.classSize = classSize;
      }

      @Override
      public String toString()
      {
         StringBuilder sb = new StringBuilder();
         sb.append(name);

         sb.append(" id=");
         sb.append(id);

         return sb.toString();
      }
   }

   public ArrayList<ClassData> classData = new ArrayList<ClassData>();
   public ArrayList<MethodData> methodData = new ArrayList<MethodData>();
   public int methodTableOffset;

   private ClassData getClassData(Binary binary,
         HashMap<String, ClassData> cache, ClassRecord classRecord)
         throws TinyVMException
   {
      String name = classRecord.getName();
      ClassData cd = cache.get(name);
      if (cd == null)
      {
         int classId = binary.getClassIndex(classRecord);
         if (classRecord.iCF == null)
         {
            cd = new ClassData(classId, name, classRecord.signature());
         } else
         {

            int parent = binary.getClassIndex(classRecord.getParent());
            int modifiers = classRecord.iCF.getAccessFlags();
            String sourceName = classRecord.iCF.getSourceFileName();

            int[] interfaces = {};
            String[] interfaceNames = classRecord.iCF.getInterfaceNames();

            if (interfaceNames != null)
            {
               interfaces = new int[interfaceNames.length];
               int cnt = 0;

               for (String n : interfaceNames)
               {
                  interfaces[cnt++] = binary.getClassIndex(n);
               }
            }
            
           cd = new ClassData(classId, name, classRecord.signature(),
                  modifiers, sourceName, parent, interfaces,
                  classRecord.getClassSize());

             Attribute[]attrs=classRecord.iCF.getAttributes();
            
            Signature sig=(Signature) getAttribute(attrs, Constants.ATTR_SIGNATURE);
            if(sig!=null){
               cd.genericSignature=sig.getSignature();
            }

            Field[] classFields = classRecord.iCF.getFields();
            if (classFields != null)
            {
               for (int i = 0; i < classFields.length; i++)
               {
                  int offset, id;
                  if (classFields[i].isStatic())
                  {
                     offset = classRecord.getStaticFieldOffset(classFields[i]
                           .getName());
                     id = -classRecord.getStaticFieldIndex(classFields[i]
                           .getName()) - 1;
                  } else
                  {
                     offset = classRecord.getInstanceFieldOffset(classFields[i]
                           .getName());
                     id = getInstanceFieldIndex(classRecord,
                           classFields[i].getName());
                  }
                  if (offset != -1){
                     FieldData f = new FieldData(id, classFields[i].getName(),
                           classFields[i].getSignature(), classFields[i]
                                 .getModifiers(), offset);
                     cd.fields.add(f);
                     attrs=classFields[i].getAttributes();
                     sig=(Signature) getAttribute(attrs, Constants.ATTR_SIGNATURE);
                     if(sig!=null){
                        f.genericSignature=sig.getSignature();
                     }
                  }
               }
            }
         }
         cache.put(name, cd);
      }
      return cd;
   }
   
   private Attribute getAttribute(Attribute[]list, int tag){
      for (int i = 0; i < list.length; i++)
      {
         if(list[i].getTag()==tag)return list[i];
      }
      return null;
   }

   private int getInstanceFieldIndex(ClassRecord classRecord, String name)
   {
      int index = -1;
      while (classRecord != null)
      {
         if (index == -1)
         {
            for (int i = 0; i < classRecord.iInstanceFields.size(); i++)
            {
               if (name.equals(classRecord.iInstanceFields.get(i).getName()))
                  index = i;
            }
         } else
         {
            index += classRecord.iInstanceFields.size();
         }
         if (!classRecord.hasParent())
            break;
         classRecord = classRecord.getParent();
      }
      return index;
   }

   void create(Binary binary) throws TinyVMException
   {
      methodTableOffset = binary.iMethodTables.getOffset();
      HashMap<String, ClassData> cache = new HashMap<String, ClassData>();

      // First create the list of class files
      int pSize = binary.iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = binary.iClassTable.get(pIndex);
         classData.add(getClassData(binary, cache, classRecord));
      }

      int pMethodId = 0;
      for (int i = 0; i < binary.iMethodTables.size(); i++)
      {
         RecordTable<MethodRecord> classMethods = binary.iMethodTables.get(i);
         for (int j = 0; j < classMethods.size(); j++)
         {
            MethodRecord method = classMethods.get(j);
            LineNo[] lnos = null;
            LineNumberTable lnt1 = method.iMethod.getLineNumberTable();
            if (lnt1 != null)
            {
               LineNumber[] lnt2 = lnt1.getLineNumberTable();
               if (lnt2 != null)
               {
                  lnos = new LineNo[lnt2.length];
                  for (int l = 0; l < lnt2.length; l++)
                  {
                     lnos[l] = new LineNo(lnt2[l].getStartPC(),
                           lnt2[l].getLineNumber());
                  }
               }else{
                  lnos=new LineNo[0];
               }
            }

            LocalVar[] locals = null;
            LocalVariableTable loc1 = method.iMethod.getLocalVariableTable();
            if (loc1 != null)
            {
               LocalVariable[] loc2 = loc1.getLocalVariableTable();
               if (loc2 != null)
               {
                  locals = new LocalVar[loc2.length];
                  for (int l = 0; l < loc2.length; l++)
                     locals[l] = new LocalVar(loc2[l].getName(),
                           loc2[l].getSignature(), loc2[l].getStartPC(),
                           loc2[l].getLength(), loc2[l].getIndex());
               }else{
                  locals=new LocalVar[0];
               }
            }

            int codeLen = 0;
            Code code1 = method.iMethod.getCode();
            if (code1 != null)
            {
               byte[] code2 = code1.getCode();
               if (code2 != null)
               {
                  codeLen = code2.length;
               }
            }

            ClassData cd = getClassData(binary, cache, method.iClassRecord);
            MethodData md = new MethodData(pMethodId, cd,
                  method.iMethod.getName(), method.iMethod.getSignature(),
                  method.iMethod.getModifiers(), method.iCodeStart, codeLen,
                  method.getNumParameterWords(), lnos, locals);
            cd.methods.add(md);
            methodData.add(md);
            
            Attribute[]attrs=method.iMethod.getAttributes();
            
            Signature sig=(Signature) getAttribute(attrs, Constants.ATTR_SIGNATURE);
            if(sig!=null){
               md.genericSignature=sig.getSignature();
            }
            
            pMethodId++;
         }
      }
   }

   public int getClassNameCount()
   {
      return classData.size();
   }

   public String getClassName(int index)
   {
      return classData.get(index).name;
   }

   public String getClassFilename(int index)
   {
      return classData.get(index).sourceName;
   }

   public int getMethodCount()
   {
      return methodData.size();
   }

   public String getMethodName(int index)
   {
      return methodData.get(index).name;
   }

   public String getMethodSignature(int index)
   {
      return methodData.get(index).signature;
   }

   public String getMethodFilename(int index)
   {
      return methodData.get(index).classData.sourceName;
   }

   public String getMethodClass(int index)
   {
      return methodData.get(index).classData.name;
   }

   public int getLineNumber(int methodIndex, int pc)
   {
      MethodData mdata = methodData.get(methodIndex);
      LineNo[] lnos = mdata.lineNumbers;
      LineNo best = new LineNo(-1, -1);
      if (pc < mdata.codeLength && lnos != null)
      {
         for (LineNo lno : lnos)
         {
            if (pc >= lno.pc && lno.pc > best.pc)
            {
               best = lno;
               if (pc == lno.pc)
               {
                  // early out
                  break;
               }
            }
         }
      }
      return best.line;
   }
   
   /**
    * returns a map with program counters with their associated line numbers 
    * 
    * @param methodIndex
    * @return a map with the program counters as keys and their corresponding line numbers as values. can return an empty map.
    */
   public Map<Integer, Integer> programCounterToLineNumberMap(int methodIndex) {
	   Map<Integer, Integer> programCounterToLineNumberMap = new HashMap<Integer, Integer>();
	   MethodData mdata = methodData.get(methodIndex);
	   LineNo[] lnos = mdata.lineNumbers;
	   if(lnos!=null) {
		   for (LineNo lineNo : lnos) {
			   programCounterToLineNumberMap.put(lineNo.pc, lineNo.line);
		   }
	   }
	   return programCounterToLineNumberMap;
   }

   public static DebugData load(InputStream in) throws IOException
   {
      try
      {
         ObjectInputStream oin = new ObjectInputStream(in);
         DebugData ret = (DebugData) oin.readObject();
         return ret;
      } catch (ClassNotFoundException e)
      {
         IOException e2 = new IOException("failed to load debug data");
         e2.initCause(e);
         throw e2;
      }
   }

   public static DebugData load(File file) throws IOException
   {
      FileInputStream fis = new FileInputStream(file);
      try
      {
         return load(new BufferedInputStream(fis, 4096));
      } finally
      {
         fis.close();
      }
   }

   public static void save(DebugData data, OutputStream out) throws IOException
   {
      ObjectOutputStream oout = new ObjectOutputStream(out);
      oout.writeObject(data);
      oout.flush();
   }

   public static void save(DebugData data, File file) throws IOException
   {
      FileOutputStream fos = new FileOutputStream(file);
      try
      {
         BufferedOutputStream bfos = new BufferedOutputStream(fos, 4096);
         save(data, bfos);
         bfos.flush();
      } finally
      {
         fos.close();
      }
   }
}
