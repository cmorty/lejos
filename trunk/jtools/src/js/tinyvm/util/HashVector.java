package js.tinyvm.util;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

public class HashVector<T>
{
   private HashMap<T, Object> iHashMap;
   private ArrayList<T> iArrayList;

   public HashVector ()
   {
      super();
      iHashMap = new HashMap<T, Object>();
      iArrayList = new ArrayList<T>();
   }

   /**
    * @deprecated method is to be removed since it messes up indices
    */
   public void insertElementAt (T aElement, int aIndex)
   {
      synchronized (iArrayList)
      {
         if (iHashMap.containsKey(aElement))
            return;
         iHashMap.put(aElement, Integer.valueOf(aIndex));
         iArrayList.add(aIndex, aElement);
         
         //FIXME the rest of the values in the hashmap is not updated even though the indexes changed!
      }
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

   /**
    * @deprecated method is to be removed since it doesn't seem to fit into the concept since there is no get method
    */
   public void put (T aKey, Object aElement)
   {
      synchronized (iArrayList)
      {
         if (iHashMap.containsKey(aKey))
            return;
         iHashMap.put(aKey, aElement);
         iArrayList.add(aKey);
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
         Object pElm = iHashMap.get(aKey);
         if (pElm instanceof Integer)
            return ((Integer) pElm).intValue();
         if (pElm == null)
            return -1;
         return iArrayList.indexOf(aKey);
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

