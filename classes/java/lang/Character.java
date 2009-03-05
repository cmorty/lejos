package java.lang;

public final class Character implements Comparable
{
    public static final int MIN_RADIX = 2;
    public static final int MAX_RADIX = 36;
	
	//MISSING everything
    
    private final char value;
    
    public Character(char c)
    {
    	this.value = c;
    }
    
    public char charValue()
    {
    	return this.charValue();
    }
    
	public int compareTo(Object o)
	{
		Character ob = (Character)o;
		if (this.value == ob.value)
			return 0;
		
		return (this.value > ob.value) ? 1 : -1;
	}
	
	public boolean equals(Object o)
	{
		//instanceof returns false for o==null
		return (o instanceof Character)
			&& (this.value == ((Character)o).value);
	}
	
	public int hashCode()
	{
		return this.value;
	}

	public String toString()
	{
		return Character.toString(this.value);
	}
	
	public static String toString(char c)
	{
		return new String(new char[] { c });
	}
	
	public static Character valueOf(char c)
	{
		return new Character(c);
	}
}
