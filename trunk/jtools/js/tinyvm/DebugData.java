/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package js.tinyvm;
import java.io.Serializable;
import java.util.ArrayList;
import org.apache.bcel.classfile.LineNumber;
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
       String name;
       String className;
       String file;
       LineNo[] lineNumbers;

       MethodData(String name, String className, String file, LineNo[] numbers)
       {
           this.name = name;
           this.className = className;
           this.file = file;
           this.lineNumbers = numbers;
       }
   }

   ArrayList<String> classNames = new ArrayList<String>();
   ArrayList<MethodData> methodData = new ArrayList<MethodData>();

   void create(Binary binary)
   {
      // First create the list of class files
      int pSize = binary.iClassTable.size();
      for (int pIndex = 0; pIndex < pSize; pIndex++)
      {
         ClassRecord classRecord = binary.iClassTable.get(pIndex);
         classNames.add(classRecord.iName);
      }
      for(int i = 0; i < binary.iMethodTables.size(); i++)
      {
         RecordTable<MethodRecord> classMethods = binary.iMethodTables.get(i);
         for(int j = 0; j < classMethods.size(); j++)
         {
            MethodRecord method = classMethods.get(j);
            LineNo[] lnos = null;
            if (method.iMethod.getLineNumberTable() != null && method.iMethod.getLineNumberTable().getLineNumberTable() != null)
            {
               LineNumber[] nos = method.iMethod.getLineNumberTable().getLineNumberTable();
               lnos = new LineNo[nos.length];
               for(int l = 0; l < nos.length; l++)
                  lnos[l] = new LineNo(nos[l].getStartPC(), nos[l].getLineNumber());
            }
            methodData.add(new MethodData(method.iMethod.getName(), method.iClassRecord.getName(), method.iClassRecord.iCF.getFileName() + "/" + method.iClassRecord.iCF.getSourceFileName(), lnos));
         }
      }
   }

   public String getClassName(int index)
   {
       return classNames.get(index);
   }

   public int getClassNameCount()
   {
       return classNames.size();
   }

   public String getMethodName(int index)
   {
       return methodData.get(index).name;
   }

   public String getMethodFile(int index)
   {
       return methodData.get(index).file;
   }

   public String getMethodClass(int index)
   {
       return methodData.get(index).className;
   }

   public int getLineNumber(int methodIndex, int pc)
   {
      MethodData mdata = methodData.get(methodIndex);
      LineNo [] lnos = mdata.lineNumbers;
      if (lnos == null) return -1;
      LineNo best = new LineNo(-1, -1);
      for(LineNo lno : lnos)
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

}
