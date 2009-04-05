package js.tinyvm.util;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

public class HashVector
{
   private HashMap iHashMap;
   private ArrayList iArrayList;

   private class IntWrap
   {
      int iV;

      IntWrap (int aV)
      {
         iV = aV;
      }
   }

   public HashVector ()
   {
      super();
      iHashMap = new HashMap();
      iArrayList = new ArrayList();
   }

   public void insertElementAt (Object aElement, int aIndex)
   {
      synchronized (iArrayList)
      {
         if (iHashMap.containsKey(aElement))
            return;
         iHashMap.put(aElement, new IntWrap(aIndex));
         iArrayList.add(aIndex, aElement);
      }
   }

   public void addElement (Object aElement)
   {
      synchronized (iArrayList)
      {
         if (iHashMap.containsKey(aElement))
            return;
         iHashMap.put(aElement, new IntWrap(iArrayList.size()));
         iArrayList.add(aElement);
      }
   }

   public void put (Object aKey, Object aElement)
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
         if (pElm instanceof IntWrap)
            return ((IntWrap) pElm).iV;
         if (pElm == null)
            return -1;
         return iArrayList.indexOf(aKey);
      }
   }

   public Iterator elements ()
   {
      return iArrayList.iterator();
   }

   public int size ()
   {
      return iArrayList.size();
   }

   public Object elementAt (int aIndex)
   {
      return iArrayList.get(aIndex);
   }
}

