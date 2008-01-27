package java.lang;

/**
 * An immutable string of characters.
 */
public final class String
{
  // NOTE: The state of this class is mapped to
  // native code (see vmsrc/classes.h).

  char[] characters; // package protected, so StringUtils can access it

  /**
   * Create a String from a character array.
   * @param c the character array
   * @param off the offset - usually 0
   * @param len the length of the String - must not be greater than c.length
   **/
  public String (char[] c, int off, int len)
  {
    characters = new char[len];
    System.arraycopy (c, off, characters, 0, len);
  }

  /**
   * Return the length of the String in characters
   * @return the length of the String
   **/
  public int length()
  {
    return characters.length;
  }

  /**
   * Return the character at the given index
   * @return the characters at the given index
   **/
  public char charAt(int index) 
  {
    return characters[index];
  }

  /**
   * Find the index of a character.
   * @param ch The character to find.
   * @return The index of the character. -1 means it wasn't found.
   */
  public int indexOf(int ch) {
      return indexOf(ch, 0);
  }

  /**
   * Returns the index within this string of the first occurrence of the
   * specified character, starting the search at the specified index.
   *
   * @param   ch          a character (Unicode code point).
   * @param   fromIndex   the index to start the search from.
   * @return  the index of the first occurrence of the character in the
   *          character sequence represented by this object that is greater
   *          than or equal to <code>fromIndex</code>, or <code>-1</code>
   *          if the character does not occur.
   */
  public int indexOf(int ch, int fromIndex) {
      int offset = 0; // Assume string always starts at 0 in characters[]
		int max = offset + characters.length;
		char v[] = characters;

		if (fromIndex < 0) {
			fromIndex = 0;
		} else if (fromIndex >= characters.length) {
			// Note: fromIndex might be near -1>>>1.
			return -1;
		}

		int i = offset + fromIndex;
		for (; i < max; i++) {
			if (v[i] == ch) {
				return i - offset;
			}
		}
		return -1;
  }
  
  /**
	 * Finds the location of a string within this string
	 * 
	 * @param str
	 * @return Index of string location, -1 if string does not exist.
	 */
  public int indexOf(String str) {
      return indexOf(str, 0);
  }
  
  /**
   * 
   * @param str
   * @param fromIndex
   * @return Index of string location, -1 if string does not exist.
   */
   
  public synchronized int indexOf(String str, int fromIndex) {
      return String.indexOf(characters, 0, characters.length,
                            str.toCharArray(), 0, str.length(), fromIndex);
  }
  
  public int lastIndexOf(String str) {
      return lastIndexOf(str, characters.length);
  }

  public int lastIndexOf(String str, int fromIndex) {
      return lastIndexOf(characters, 0, characters.length,
                         str.characters, 0, str.length(), fromIndex);
  }
  
  /**
   * Code shared by String and StringBuffer to do searches. The
   * source is the character array being searched, and the target
   * is the string being searched for.
   *
   * @param   source       the characters being searched.
   * @param   sourceOffset offset of the source string.
   * @param   sourceCount  count of the source string.
   * @param   target       the characters being searched for.
   * @param   targetOffset offset of the target string.
   * @param   targetCount  count of the target string.
   * @param   fromIndex    the index to begin searching from.
   */
  static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
                         char[] target, int targetOffset, int targetCount,
                         int fromIndex) {
      /*
       * Check arguments; return immediately where possible. For
       * consistency, don't check for null str.
       */
      int rightIndex = sourceCount - targetCount;
      if (fromIndex < 0) {
          return -1;
      }
      if (fromIndex > rightIndex) {
          fromIndex = rightIndex;
      }
      /* Empty string always matches. */
      if (targetCount == 0) {
          return fromIndex;
      }

      int strLastIndex = targetOffset + targetCount - 1;
      char strLastChar = target[strLastIndex];
      int min = sourceOffset + targetCount - 1;
      int i = min + fromIndex;

  startSearchForLastChar:
      while (true) {
          while (i >= min && source[i] != strLastChar) {
              i--;
          }
          if (i < min) {
              return -1;
          }
          int j = i - 1;
          int start = j - (targetCount - 1);
          int k = strLastIndex - 1;

          while (j > start) {
              if (source[j--] != target[k--]) {
                  i--;
                  continue startSearchForLastChar;
              }
          }
          return start - sourceOffset + 1;
      }
  }
  
  /**
   * Code shared by String and StringBuffer to do searches. The
   * source is the character array being searched, and the target
   * is the string being searched for.
   *
   * @param   source       the characters being searched.
   * @param   sourceOffset offset of the source string.
   * @param   sourceCount  count of the source string.
   * @param   target       the characters being searched for.
   * @param   targetOffset offset of the target string.
   * @param   targetCount  count of the target string.
   * @param   fromIndex    the index to begin searching from.
   */
  static int indexOf(char[] source, int sourceOffset, int sourceCount,
                     char[] target, int targetOffset, int targetCount,
                     int fromIndex) {
      if (fromIndex >= sourceCount) {
          return (targetCount == 0 ? sourceCount : -1);
      }
      if (fromIndex < 0) {
          fromIndex = 0;
      }
      if (targetCount == 0) {
          return fromIndex;
      }

      char first  = target[targetOffset];
      int max = sourceOffset + (sourceCount - targetCount);

      for (int i = sourceOffset + fromIndex; i <= max; i++) {
          /* Look for first character. */
          if (source[i] != first) {
              while (++i <= max && source[i] != first);
          }

          /* Found first character, now look at the rest of v2 */
          if (i <= max) {
              int j = i + 1;
              int end = j + targetCount - 1;
              for (int k = targetOffset + 1; j < end && source[j] ==
                       target[k]; j++, k++);

              if (j == end) {
                  /* Found whole string. */
                  return i - sourceOffset;
              }
          }
      }
      return -1;
  }
  
  /**
   * Converts the String into an array of characters
   * @return the character Array
   **/
  public char[] toCharArray()
  {
    int len = characters.length;
    char[] ca = new char[len];
    System.arraycopy (characters, 0, ca, 0, len);
    return ca;
  }

  public synchronized String substring(int start) {
      return substring(start, characters.length);
  }

  public synchronized String substring(int start, int end) {
      // THIS SHOULD REALLY THROW StringIndexOutOfBoundsException
	  int len = end - start;
	  return new String(characters, start, len);
  }
  
  /**
   * Converts an Object to a String
   * @return the String that represents the object
   **/
  public static String valueOf (Object aObj)
  {
    return aObj.toString();
  }

  /**
   * Returns itself.
   * @return the String itself
   */
  public String toString()
  {
    return this;
  }
  
  /**
   * Compares the String with an Object
   * @return true if the String is equal to the object, false otherwise
   **/
  public boolean equals(Object other)
  {
    if (other == null)
      return false;
      
    try {
      String os = (String)other;
      if (os.characters.length != characters.length)
         return false;
         
      for (int i=0; i<characters.length; i++)
      {
        if (characters[i] != os.characters[i])
          return false;
      }
      
      return true;
    } catch (ClassCastException e) {
    }    
    return false;
  }
}

