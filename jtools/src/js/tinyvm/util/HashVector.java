package js.tinyvm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HashVector<T>
{
   private HashMap<T, Integer> iHashMap;
   private ArrayList<T> iArrayList;

   public HashVector ()
   {
      super();
      iHashMap = new HashMap<T, Integer>();
      iArrayList = new ArrayList<T>();
   }

   public void addElement (T aElement)
   {
      synchronized (iArrayList)
      {
         if (iHashMap.containsKey(aElement))
            return;
         iHashMap.put(aElement, Integer.valueOf(iArrayList.size()));
         iArrayList.add(aElement);
      }
   }

   public boolean containsKey (Object aKey)
   {
      return iHashMap.containsKey(aKey);
   }

   public int indexOf (Object aKey)
   {
      synchronized (iArrayList)
      {
         Integer pElm = iHashMap.get(aKey);
         if (pElm == null)
             return -1;
         return pElm.intValue();
      }
   }

   public Iterator<T> elements ()
   {
      return iArrayList.iterator();
   }

   public int size ()
   {
      return iArrayList.size();
   }

   public T elementAt (int aIndex)
   {
      return iArrayList.get(aIndex);
   }
}

