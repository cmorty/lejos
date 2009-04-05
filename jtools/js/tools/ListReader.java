package js.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ListReader
{
   public static ArrayList<String> loadStrings (File aFile)
   {
      ArrayList<String> pVec = new ArrayList<String>();
      if (!aFile.exists())
         return pVec;
      try
      {
         BufferedReader pReader = new BufferedReader(new FileReader(aFile));
         String pLine;
         while ((pLine = pReader.readLine()) != null)
         {
            pLine = pLine.trim();
            if (pLine.startsWith("#") || pLine.equals(""))
               continue;
            pVec.add(pLine);
         }
         pReader.close();
      }
      catch (IOException aE)
      {
         aE.printStackTrace();
      }
      return pVec;
   }
}