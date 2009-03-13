package java.lang;

/**
 * Minimal Double implementation to support parseDouble(), isNaN(), and NaN.
 * @author bb
 *
 */
public class Double {

	private double value;
	
	public static final double NaN = 0.0D / 0.0D;
	
	public Double(double val) {
		this.value = val;
	}
	
	static public boolean isNaN(double val) {
    	return(val == Double.NaN);
    }
	
	public boolean isNaN() {
    	return(this.value == Double.NaN);
    }
	
	/**
	 * Converts a String value into a float 
	 * @param s String representation of float. Must only contain numbers and an optional decimal, and optional - sign at front.
	 * @return float number
	 */
	public static double parseDouble(String s) throws NumberFormatException {
		boolean negative = (s.charAt(0) == '-'); // Check if negative symbol.
		double result = 0.0D; // Starting value
		int index = s.indexOf('.');
		
		if(index > -1) {
		// Means the decimal place exists, add values to right of it
			int divisor = 1;
			for(int i=index+1;i<s.length();i++) {
				divisor *= 10;
				int curVal = (s.charAt(i)-48); // Convert char to int
				if(curVal > 9|curVal < 0)
					throw new NumberFormatException();
				result += (curVal/divisor);
			}
		}
		
		// Now add number characters to left of decimal
		int multiplier = 1;
		if(index < 0) // i.e. -1
			index = s.length(); // If number string had no decimal
		int finish = negative ? 1 : 0; // Determine finishing position
		
		for(int i=index-1;i>= finish;i--) {
			int curVal = (s.charAt(i) - 48); // Convert char to int
			if(curVal > 9|curVal < 0)
				throw new NumberFormatException();
			result += (curVal * multiplier);
			multiplier *= 10;
		}	
		
		return negative ? -result : result;
	}
}
