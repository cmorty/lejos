package java.util;

/**
 * Maps keys to objects. It has a fixed-size table, so don't
 * expect it to scale well.
 */
public class Hashtable
{
  private static final int TABLE_SIZE = 32;
  private Object[] iTable;
  
  public Hashtable()
  {  
    this.iTable = new Object[TABLE_SIZE];
  }

	public synchronized Object get (Object aKey)
	{
		Object pElement = iTable[getTableIndex(aKey)];
		if (pElement != null)
		{
			KeyValuePair pKeyValuePair = getKeyValuePair(pElement, aKey);
			if (pKeyValuePair != null)
			{
				return pKeyValuePair.iValue;
			}
		}
		return null;
	}
  
	public synchronized Object put (Object aKey, Object aValue)
	{
		Object r;
		int pIndex = getTableIndex(aKey);    
		Object pElement = iTable[pIndex];
		if (pElement == null)
		{
			r = null;
			iTable[pIndex] = new KeyValuePair(aKey, aValue);
		}
		else
		{
			KeyValuePair pKeyValuePair = getKeyValuePair (pElement, aKey);
			if (pKeyValuePair != null)
			{
				r = pKeyValuePair.iValue;
				pKeyValuePair.iValue = aValue;
			}
			else
			{
				r = null;
				pKeyValuePair = new KeyValuePair(aKey, aValue);
				if (pElement instanceof Vector)
					((Vector)pElement).addElement(pKeyValuePair);
				else
				{
					Vector pVector = new Vector();
					pVector.addElement(pElement);
					pVector.addElement(pKeyValuePair);
					iTable[pIndex] = pVector;
				}
			}
		}
		return r;
	}

  /**
   * 
   * @return An Enumeration object containing all keys for this Hashtable
   */
  public Enumeration keys() {
	  return new Enumeration() {
		  int cur = 0;
		  /* Our Hashtable stores more than one object in iTable if
		   * getTableIndex() hashes it to the same slot. When this happens
		   * it stores multiple items in a Vector. In this case, nextElement()
		   * needs to keep track of which vector it is at via curVector.
		   */
		  int curVector = -1; 
		  public boolean hasMoreElements() {
			  /* Difficult to work with our current Hashtable code due to iTable gaps.*/
			   
			  int i=cur;
			  // This little detour is to check if there are any more objects in a Vector (if used)
			  if(i < TABLE_SIZE && iTable[i] instanceof Vector) {
				  Vector v = (Vector)iTable[i];
				  if(curVector + 1 >= v.size())
					  i++;
			  }
			  for(;i<TABLE_SIZE;i++)
				  if(iTable[i] != null)
					  return true;
			  return false;
		  }
		  
		  public Object nextElement() {
			  /* Difficult to work with our current Hashtable code 
			     due to iTable gaps. */
			  // Go thru iTable until object found
			  while(cur < TABLE_SIZE) {
				  if(iTable[cur] instanceof KeyValuePair) {
					  KeyValuePair kvp = (KeyValuePair)iTable[cur];
					  cur++;
					  if(kvp != null) return kvp.iKey;
				  } else if (iTable[cur] instanceof Vector) {
					  curVector++;
					  while(curVector >= 0) {
						  Vector v = (Vector)iTable[cur];
						  if (curVector >= v.size()) {
							  curVector = -1;
							  break;
						  }
						  KeyValuePair kvp = (KeyValuePair)v.elementAt(curVector);
						  if(kvp == null) {
							  curVector = -1;
						  } else {
							  return kvp.iKey;
						  }
					  }
				  }
				  cur++;
			  }
			  return null;
		  }
	  };
  }
  
	private KeyValuePair getKeyValuePair(Object aPivot, Object aKey)
	{
		if (aPivot instanceof Vector)
		{
			Vector pVec = (Vector) aPivot;
			int pSize = pVec.size();
			for (int i = 0; i < pSize; i++)
			{
				KeyValuePair pPair = (KeyValuePair) pVec.elementAt(i);
				if (aKey.equals(pPair.iKey))
				{
					return pPair;
				}
			}
		}
		else
		{
			// Not a Vector, must be a lone KeyValuePair
			KeyValuePair pPair = (KeyValuePair)aPivot;
			if (aKey.equals(pPair.iKey))
			{
				return pPair;
			}
		}
		return null;
	}
  
	private int getTableIndex (Object aKey)
	{
		int pHash = aKey.hashCode() % TABLE_SIZE;
		if (pHash < 0)
			return pHash + TABLE_SIZE;
		return pHash;
	}

	private static class KeyValuePair
	{ 
		Object iKey;
		Object iValue;
		
		public KeyValuePair(Object key, Object value)
		{
			this.iKey = key;
			this.iValue = value;
		}
	}
}
