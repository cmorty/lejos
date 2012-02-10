/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

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

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LocalVariable;

/**
 * 
 * @author andys
 */
public class CopyOfDebugData implements Serializable
{
   static public class LineNo implements Serializable
   {
      public int pc;
      public int line;

      LineNo(int pc, int line)
      {
         this.pc = pc;
         this.line = line;
      }
   }

   static public class LocalVar implements Serializable
   {
      public String name;
      public String signature;
      public int fromPc;
      public int toPc;
      public int index;

      LocalVar(String name, String signature, int fromPc, int toPc, int index)
      {
         super();
         this.name = name;
         this.signature = signature;
         this.fromPc = fromPc;
         this.toPc = toPc;
         this.index = index;
      }
   }

   static public class MethodData implements Serializable
   {
      public int id;
      public String name;
      public String signature;
      public int modifiers;
      public int classId;
      public int codeOffset;
      public int numParamWords;
      public LineNo[] lineNumbers;
      public LocalVar[] localVariables;

      MethodData(int id, String name, String signature, int classId,
            int codeOffset, int numParamWords, LineNo[] numbers,
            LocalVar[] locals, int modifiers)
      {
         this.id = id;
         this.name = name;
         this.signature = signature;
         this.modifiers = modifiers;
         this.classId = classId;
         this.codeOffset = codeOffset;
         this.lineNumbers = numbers;
         this.localVariables = locals;
         this.numParamWords = numParamWords;
      }
   }

   static public class ClassData implements Serializable
   {
      public int id;
      public String name;
      public String signature;
      public int modifiers;
      public String sourceName;
      public FieldData[] fields;
      public MethodData[] methods;
      public int superclass;
      public int[] interfaces;// For interfaces: all parent interfaces
      // public String[]subClasses;//For interfaces: all implementing classes
      // public String[]subInterfaces;//For interfaces: all child interfaces
      public int classSize;
      public int[] nestedClasses;

      public ClassData(int id, String name, String signature, int modifiers,
            String sourceName, FieldData[] fields, MethodData[] methods,
            int superclass, int[] interfaces, int classSize, int[] nestedClasses)
      {
         super();
         this.id = id;
         this.name = name;
         this.signature = signature;
         this.modifiers = modifiers;
         this.sourceName = sourceName;
         this.fields = fields;
         this.methods = methods;
         this.superclass = superclass;
         this.interfaces = interfaces;
         this.classSize = classSize;
         this.nestedClasses = nestedClasses;
      }
   }

   static public class FieldData implements Serializable
   {
      public int id;
      public String name;
      public String signature;
      public int modifiers;
      public int offset;

      public FieldData(int id, String name, String signature, int modifiers,
            int offset)
      {
         super();
         this.id = id;
         this.name = name;
         this.signature = signature;
         this.modifiers = modifiers;
         this.offset = offset;
      }
   }

   public ArrayList<ClassData> classData = new ArrayList<ClassData>();
   public ArrayList<MethodData> methodData = new ArrayList<MethodData>();
   public int methodTableOffset;

   void create(Binary binary) throws TinyVMException
   {
      // First create the list of class files
      int pSize = binary.iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = binary.iClassTable.get(pIndex);

         MethodData[] classMethodData = {};
         FieldData[] fields = {};
         int[] inners = {};
         int[] interfaces = {};
         int parent = 0;
         String sourceName = null;
         int modifiers = 0;
         if (classRecord.iCF != null)
         {
            RecordTable<MethodRecord> classMethods = classRecord.iMethodTable;
            classMethodData = new MethodData[classMethods.size()];
            for (int j = 0; j < classMethods.size(); j++)
            {
               MethodRecord method = classMethods.get(j);
               LineNo[] lnos = null;
               if (method.iMethod.getLineNumberTable() != null
                     && method.iMethod.getLineNumberTable()
                           .getLineNumberTable() != null)
               {
                  LineNumber[] nos = method.iMethod.getLineNumberTable()
                        .getLineNumberTable();
                  lnos = new LineNo[nos.length];
                  for (int l = 0; l < nos.length; l++)
                     lnos[l] = new LineNo(nos[l].getStartPC(),
                           nos[l].getLineNumber());
               }
               LocalVar[] locals = null;
               if (method.iMethod.getLocalVariableTable() != null
                     && method.iMethod.getLocalVariableTable()
                           .getLocalVariableTable() != null)
               {
                  LocalVariable[] nos = method.iMethod.getLocalVariableTable()
                        .getLocalVariableTable();
                  locals = new LocalVar[nos.length];
                  for (int l = 0; l < nos.length; l++)
                     locals[l] = new LocalVar(nos[l].getName(),
                           nos[l].getSignature(), nos[l].getStartPC(),
                           nos[l].getStartPC() + nos[l].getLength(),
                           nos[l].getIndex());
               }
               methodData.add(classMethodData[j] = new MethodData(methodData
                     .size(), method.iMethod.getName(), method.iMethod
                     .getSignature(), binary.getClassIndex(method
                     .getClassRecord()), method.getCodeStart(), method
                     .getNumParameterWords(), lnos, locals, method.iMethod
                     .getModifiers()));
            }

            Field[] classFields = classRecord.iCF.getFields();
            ArrayList<FieldData> fieldsZW = new ArrayList<CopyOfDebugData.FieldData>(
                  classFields.length);
            for (int i = 0; i < classFields.length; i++)
            {
               int offset, id;
               if (classFields[i].isStatic())
               {
                  offset = classRecord.getStaticFieldOffset(classFields[i]
                        .getName());
                  id = classRecord
                        .getStaticFieldIndex(classFields[i].getName());
               } else
               {
                  offset = classRecord.getInstanceFieldOffset(classFields[i]
                        .getName());
                  id = getInstanceFieldIndex(classRecord,
                        classFields[i].getName());
               }
               if (offset != -1)
                  fieldsZW.add(new FieldData(id, classFields[i].getName(),
                        classFields[i].getSignature(), classFields[i]
                              .getModifiers(), offset));
            }
            fields = (FieldData[]) fieldsZW.toArray(new FieldData[fieldsZW
                  .size()]);
            parent = binary.getClassIndex(classRecord.getParent());
            modifiers = classRecord.iCF.getAccessFlags();
            sourceName = classRecord.iCF.getSourceFileName();
         }
         classData.add(new ClassData(pIndex, classRecord.getName(), classRecord
               .signature(), modifiers, sourceName, fields, classMethodData,
               parent, interfaces, classRecord.getClassSize(), inners));
      }
      methodTableOffset = binary.iMethodTables.getOffset();
   }

   private int getInstanceFieldIndex(ClassRecord classRecord, String name)
   {
      int offset = classRecord.getClassSize();
      for (InstanceFieldRecord record : classRecord.iInstanceFields)
      {
         if (record.getName().equals(name))
            return offset;
         offset += record.getFieldSize();
      }
      return classRecord.hasParent() ? getInstanceFieldIndex(
            classRecord.getParent(), name) : -1;
   }

   public String getClassName(int index)
   {
      return classData.get(index).name;
   }

   public int getClassCount()
   {
      return classData.size();
   }

   public String getMethodName(int index)
   {
      return methodData.get(index).name;
   }

   public String getMethodFile(int index)
   {
      return classData.get(methodData.get(index).classId).sourceName;
   }

   public String getMethodClass(int index)
   {
      return classData.get(methodData.get(index).classId).name;
   }

   public int getLineNumber(int methodIndex, int pc)
   {
      MethodData mdata = methodData.get(methodIndex);
      LineNo[] lnos = mdata.lineNumbers;
      if (lnos == null)
         return -1;
      LineNo best = new LineNo(-1, -1);
      for (LineNo lno : lnos)
      {
         if (pc == lno.pc)
            return lno.line;
         else if (pc > lno.pc)
         {
            if ((pc - lno.pc) < (pc - best.pc))
            {
               best = lno;
            }
         }
      }
      return best.line;
   }

   public int getMethodCount()
   {
      return methodData.size();
   }

   public static CopyOfDebugData load(InputStream in) throws IOException
   {
      try
      {
         ObjectInputStream oin = new ObjectInputStream(in);
         CopyOfDebugData ret = (CopyOfDebugData) oin.readObject();
         return ret;
      } catch (ClassNotFoundException e)
      {
         IOException e2 = new IOException("failed to load debug data");
         e2.initCause(e);
         throw e2;
      }
   }

   public static CopyOfDebugData load(File file) throws IOException
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

   public static void save(CopyOfDebugData data, OutputStream out)
         throws IOException
   {
      ObjectOutputStream oout = new ObjectOutputStream(out);
      oout.writeObject(data);
      oout.flush();
   }

   public static void save(CopyOfDebugData data, File file) throws IOException
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

   public String getMethodSignature(int index)
   {
      return methodData.get(index).signature;
   }

}
