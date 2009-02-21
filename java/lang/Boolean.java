package java.lang;

/**
 * Wrapper class for booleans.
 * @author Sven Köhler
 */
public final class Boolean implements Comparable
{
	public static final Boolean FALSE = new Boolean(false);
	public static final Boolean TRUE = new Boolean(true);
	
	//MISSING implements Serializable
	//MISSING public static final Class TYPE
	//MISSING public static getBoolean(String name);
	
	private final boolean value;
	
	public Boolean(boolean b)
	{
		this.value = b;
	}
	
	public Boolean(String s)
	{
		this.value = Boolean.parseBoolean(s);
	}
	
	public boolean booleanValue()
	{
		return this.value;
	}
	
	public int compareTo(Object o)
	{
		Boolean ob = (Boolean)o;
		if (this.value == ob.value)
			return 0;
		
		//false is less than true
		return this.value ? 1 : -1;
	}
	
	public boolean equals(Object o)
	{
		//instanceof returns false for o==null
		return (o instanceof Boolean)
			&& (this.value == ((Boolean)o).value);
	}
	
	public int hashCode()
	{
		return this.value ? 1231 : 1237;
	}
	
	public static boolean parseBoolean(String s)
	{
		//FIXME actually, this should be equalsIgnoreCase
		return "true".equals(s);
	}
	
	public String toString()
	{
		return Boolean.toString(this.value);
	}
	
	public static String toString(boolean b)
	{
		return b ? "true" : "false";
	}
	
	public static Boolean valueOf(boolean b)
	{
		return b ? TRUE : FALSE;
	}
	
	public static Boolean valueOf(String s)
	{
		return parseBoolean(s) ? TRUE : FALSE;
	}
}
