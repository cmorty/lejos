package java.lang;

/**
 * An expandable string of characters. Actually not very expandable!
 * 09/25/2001 added number formatting thanks to Martin E. Nielsen.
 * You must ensure that the buffer is large enough to take the formatted
 * number.
 *<P>
 * @author <a href="mailto:martin@egholm-nielsen.dk">Martin E. Nielsen</a>
 * @author Sven Köhler
 */
public class StringBuilder implements CharSequence
{
	private static final int INITIAL_CAPACITY = 10;
	private static final int CAPACITY_INCREMENT_NUM = 3;	//numerator of the increment factor
	private static final int CAPACITY_INCREMENT_DEN = 2;	//denominator of the increment factor
	
	private char[] characters;
	private int curLen = 0;
	
	public void ensureCapacity(int minCapacity)
	{
		int cl = characters.length;
		if (cl < minCapacity)
		{
			cl = cl * CAPACITY_INCREMENT_NUM / CAPACITY_INCREMENT_DEN + 1;
			while (cl < minCapacity)
				cl = cl * CAPACITY_INCREMENT_NUM / CAPACITY_INCREMENT_DEN + 1;
			
			char[] newData = new char[cl];
			System.arraycopy(characters, 0, newData, 0, curLen);
			characters = newData;
		}
	}

  public StringBuilder ()
  {
    characters = new char[INITIAL_CAPACITY];
  }
  
  public StringBuilder (String aString)
  {
    characters = aString.toCharArray();
    curLen = aString.length();
  }

  public StringBuilder (int length)
  {
    if (length < 0)
    	throw new NegativeArraySizeException("length is negative");
    
    characters = new char[length];
  }

  public StringBuilder delete(int start, int end)
  {
	  if (start < 0 || start > curLen)
		  throw new StringIndexOutOfBoundsException(start);
	  if (end < start)
		  throw new StringIndexOutOfBoundsException();
	  if (end > curLen)
		  end = curLen;
	  
      System.arraycopy(characters, end, characters, start, curLen - end);
      curLen -= end - start;
      
      return this;
  }

  public StringBuilder append (String s)
  {
	  return this.appendInternal(s);
  }

  public StringBuilder append (Object aObject)
  {
	  return this.appendInternal(String.valueOf(aObject));
  }

  public StringBuilder append (boolean aBoolean)
  {
    return this.appendInternal(String.valueOf(aBoolean));
  }
  
  public StringBuilder append (char aChar)
  {
	  int newLen = curLen +1;
	  ensureCapacity(newLen);
	  
	  characters[curLen] = aChar;
	  curLen = newLen;
	  
	  return this;
  }

	public StringBuilder append(char[] c)
	{
		return this.append(c, 0, c.length);
	}
	
	public StringBuilder append(char[] c, int off, int len)
	{
		int newLen = curLen + len;
		ensureCapacity(newLen);
	  
		for (int i=0; i<len; i++)
			characters[curLen + i] = c[off + i];		
		curLen = newLen;	  
		
		return this;
	}

	public StringBuilder append(CharSequence cs)
	{
		return this.append(cs, 0, cs.length());
	}
	
	public StringBuilder append(CharSequence cs, int start, int end)
	{
		int len = end - start;
		int newLen = curLen + len;
		ensureCapacity(newLen);
	  
		for (int i=0; i<len; i++)
			characters[curLen + i] = cs.charAt(start + i);		
		curLen = newLen;	  
		
		return this;
	}

  public StringBuilder append (int i)
  {
	  int intLen = StringUtils.exactStringLength(i, 10);
	  int newLen = curLen + intLen;
	  ensureCapacity(newLen);

	  StringUtils.getIntChars(characters, newLen, i, 10);	  
	  curLen = newLen;
	  
	  return this;
  }

  public StringBuilder append (long aLong)
  {
	  int intLen = StringUtils.exactStringLength(aLong, 10);
	  int newLen = curLen + intLen;
	  ensureCapacity(newLen);

	  StringUtils.getLongChars(characters, newLen, aLong, 10);	  	  
	  curLen = newLen;
	  
	  return this;
  }

	public StringBuilder append (float aFloat)
	{
		ensureCapacity(curLen + StringUtils.MAX_FLOAT_CHARS);
		curLen = StringUtils.getFloatChars(aFloat, characters, curLen);
    	return this;
	}

	public StringBuilder append (double aDouble)
	{
		ensureCapacity(curLen + StringUtils.MAX_DOUBLE_CHARS);
		curLen = StringUtils.getDoubleChars(aDouble, characters, curLen);
    	return this;
	}
  
  /**
   * Appends a string with no null checking
   */
  private StringBuilder appendInternal(String s) {
	  if (s == null)
		  s = "null";
	  
    // Reminder: compact code more important than speed
    char[] sc = s.characters;
    int sl = sc.length;
    
    int newlen = curLen + sl;
    this.ensureCapacity(newlen);
    
    System.arraycopy (sc, 0, characters, curLen, sl);    
    curLen = newlen;
    
    return this;
  }
  
  public int indexOf(String str) {
      return indexOf(str, 0);
  }

  public int indexOf(String str, int fromIndex) {
      return String.indexOf(characters, 0, curLen,
                            str.characters, 0, str.characters.length, fromIndex);
  }

  public int lastIndexOf(String str) {
      // Note, synchronization achieved via other invocations
      return lastIndexOf(str, curLen);
  }

  public int lastIndexOf(String str, int fromIndex) {
      return String.lastIndexOf(characters, 0, curLen,
                            str.characters, 0, str.characters.length, fromIndex);
  }
  
  @Override
  public String toString()
  {
    return new String (characters, 0, curLen);
  }

  public char charAt(int i)
  {
	  if (i < 0 || i >= curLen)
		  throw new StringIndexOutOfBoundsException(i);
	  
        return characters[i];
  }
  
  public void setCharAt(int i, char ch)
  {
	  if (i < 0 || i >= curLen)
		  throw new StringIndexOutOfBoundsException(i);
	  
        characters[i] = ch;
  }
  
  public int length()
  {
        return curLen;
  }

  /**
  * Retrieves the contents of the StringBuilder in the form of an array of characters.
  */
  public char[] getChars()
  {
    char[] r = new char[curLen];
    System.arraycopy(characters, 0, r, 0, curLen);
    return r;
  }
  
  public String substring(int start) {
      return substring(start, curLen);
  }

  public String substring(int start, int end) {
	  if (start < 0 || start > curLen)
		  throw new StringIndexOutOfBoundsException(start);
	  if (end > curLen)
		  throw new StringIndexOutOfBoundsException(end);
	  if (end < start)
		  throw new StringIndexOutOfBoundsException(end - start);
	  
	  int len = end - start;
	  return new String(characters, start, len);
  }
  
  public CharSequence subSequence(int start, int end)
  {
	  return substring(start, end);
  }
}


