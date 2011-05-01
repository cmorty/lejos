package js.tinyvm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

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
	   ClassData classData;
       String name;
       LineNo[] lineNumbers;

       MethodData(ClassData cd, String name, LineNo[] numbers)
       {
    	   this.classData = cd;
           this.name = name;
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
	   String name = classRecord.iName;
	   ClassData cd = cache.get(name);
	   if (cd == null)
	   {
	       if (classRecord instanceof PrimitiveClassRecord)
	      	 cd = new ClassData(name, null);
	       else
	      	 cd = new ClassData(classRecord.getName(), classRecord.iCF.getSourceFileName());
	       
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
            
            ClassData cd = getClassData(cache, method.iClassRecord);
            methodData.add(new MethodData(cd, method.iMethod.getName(), lnos));
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

   public String getMethodFile(int index)
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
		if (lnos != null)
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
   
}
