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

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;

/**
 *
 * @author andys
 */
public class DebugData implements Serializable
{
   static private class LineNo implements Serializable
   {
      int pc;
      int line;

      LineNo(int pc, int line)
      {
         this.pc = pc;
         this.line = line;
      }
   }

   static private class MethodData implements Serializable
   {
      ClassData classData;
      String name;
      String signature;
      int codeLength;
      LineNo[] lineNumbers;

      MethodData(ClassData cd, String name, String signature, int codeLength, LineNo[] numbers)
      {
         this.classData = cd;
         this.name = name;
         this.signature = signature;
         this.codeLength = codeLength;
         this.lineNumbers = numbers;
      }
   }

   static private class ClassData implements Serializable
   {
      String name;
      String file;

      public ClassData(String name, String file)
      {
         this.name = name;
         this.file = file;
      }
   }
   ArrayList<ClassData> classData = new ArrayList<ClassData>();
   ArrayList<MethodData> methodData = new ArrayList<MethodData>();

   private ClassData getClassData(HashMap<String, ClassData> cache, ClassRecord classRecord)
   {
      String name = classRecord.getName();
      ClassData cd = cache.get(name);
      if (cd == null)
      {
         cd = new ClassData(name, classRecord.getSourceFilename());
         cache.put(name, cd);
      }
      return cd;
   }

   void create(Binary binary)
   {
      HashMap<String, ClassData> cache = new HashMap<String, ClassData>();

      // First create the list of class files
      int pSize = binary.iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = binary.iClassTable.get(pIndex);
         classData.add(getClassData(cache, classRecord));
      }
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
                     lnos[l] = new LineNo(lnt2[l].getStartPC(), lnt2[l].getLineNumber());
                  }
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

            ClassData cd = getClassData(cache, method.iClassRecord);
            methodData.add(new MethodData(cd, method.iMethod.getName(), method.iMethod.getSignature(),
                    codeLen, lnos));
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
      return classData.get(index).file;
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
      return methodData.get(index).classData.file;
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

   public static DebugData load(InputStream in) throws IOException
   {
      try
      {
         ObjectInputStream oin = new ObjectInputStream(in);
         DebugData ret = (DebugData) oin.readObject();
         return ret;
      }
      catch (ClassNotFoundException e)
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
      }
      finally
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
      }
      finally
      {
         fos.close();
      }
   }
}
