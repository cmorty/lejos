package java.util;

/**
 * Various tools for arrays.
 * NOTE: currently bubblesort is used for sorting arrays.
 * 
 * @author Juan Antonio Brenha Moral, Sven Köhler
 */
public class Arrays {
	//TODO use faster algorithm like quicksort

	private Arrays() {
		// class cannot be instantiated
	}
	
	/****************** FILL ******************/
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param val the value
	 */
	public void fill(boolean[] a, boolean val) {
		fill(a, 0, a.length, val);
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 * @param val the value
	 */
	public void fill(boolean[] a, int fromIndex, int toIndex, boolean val) {
		for (int i=fromIndex; i<toIndex; i++) {
			a[i] = val;
		}
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param val the value
	 */
	public void fill(byte[] a, byte val) {
		fill(a, 0, a.length, val);
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 * @param val the value
	 */
	public void fill(byte[] a, int fromIndex, int toIndex, byte val) {
		for (int i=fromIndex; i<toIndex; i++) {
			a[i] = val;
		}
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param val the value
	 */
	public void fill(short[] a, short val) {
		fill(a, 0, a.length, val);
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 * @param val the value
	 */
	public void fill(short[] a, int fromIndex, int toIndex, short val) {
		for (int i=fromIndex; i<toIndex; i++) {
			a[i] = val;
		}
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param val the value
	 */
	public void fill(char[] a, char val) {
		fill(a, 0, a.length, val);
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 * @param val the value
	 */
	public void fill(char[] a, int fromIndex, int toIndex, char val) {
		for (int i=fromIndex; i<toIndex; i++) {
			a[i] = val;
		}
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param val the value
	 */
	public void fill(int[] a, int val) {
		fill(a, 0, a.length, val);
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 * @param val the value
	 */
	public void fill(int[] a, int fromIndex, int toIndex, int val) {
		for (int i=fromIndex; i<toIndex; i++) {
			a[i] = val;
		}
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param val the value
	 */
	public void fill(long[] a, long val) {
		fill(a, 0, a.length, val);
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 * @param val the value
	 */
	public void fill(long[] a, int fromIndex, int toIndex, long val) {
		for (int i=fromIndex; i<toIndex; i++) {
			a[i] = val;
		}
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param val the value
	 */
	public void fill(float[] a, float val) {
		fill(a, 0, a.length, val);
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 * @param val the value
	 */
	public void fill(float[] a, int fromIndex, int toIndex, float val) {
		for (int i=fromIndex; i<toIndex; i++) {
			a[i] = val;
		}
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param val the value
	 */
	public void fill(double[] a, double val) {
		fill(a, 0, a.length, val);
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 * @param val the value
	 */
	public void fill(double[] a, int fromIndex, int toIndex, double val) {
		for (int i=fromIndex; i<toIndex; i++) {
			a[i] = val;
		}
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param val the value
	 */
	public void fill(Object[] a, Object val) {
		fill(a, 0, a.length, val);
	}
	
	/**
	 * Set array elements to given value.
	 * @param a the array
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 * @param val the value
	 */
	public void fill(Object[] a, int fromIndex, int toIndex, Object val) {
		for (int i=fromIndex; i<toIndex; i++) {
			a[i] = val;
		}
	}
	
	/****************** SORT ******************/ 

	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 */
	public static void sort(byte[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 */
	public static void sort(byte[] a, int fromIndex, int toIndex) {
		// outer loop (backward)
		for (int out = toIndex - 1; out > fromIndex; out--){
			// inner loop (forward)
			for (int in = fromIndex; in < out; in++){
				int in2 = in + 1;
				byte temp = a[in];
				if (temp > a[in2]){ // out of order?
					a[in] = a[in2];
					a[in2] = temp;
				}
			}
		}
	}

	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 */
	public static void sort(short[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 */
	public static void sort(short[] a, int fromIndex, int toIndex) {
		// outer loop (backward)
		for (int out = toIndex - 1; out > fromIndex; out--){
			// inner loop (forward)
			for (int in = fromIndex; in < out; in++){
				int in2 = in + 1;
				short temp = a[in];
				if (temp > a[in2]){ // out of order?
					a[in] = a[in2];
					a[in2] = temp;
				}
			}
		}
	}

	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 */
	public static void sort(char[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 */
	public static void sort(char[] a, int fromIndex, int toIndex) {
		// outer loop (backward)
		for (int out = toIndex - 1; out > fromIndex; out--){
			// inner loop (forward)
			for (int in = fromIndex; in < out; in++){
				int in2 = in + 1;
				char temp = a[in];
				if (temp > a[in2]){ // out of order?
					a[in] = a[in2];
					a[in2] = temp;
				}
			}
		}
	}

	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 */
	public static void sort(int[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 */
	public static void sort(int[] a, int fromIndex, int toIndex) {
		// outer loop (backward)
		for (int out = toIndex - 1; out > fromIndex; out--){
			// inner loop (forward)
			for (int in = fromIndex; in < out; in++){
				int in2 = in + 1;
				int temp = a[in];
				if (temp > a[in2]){ // out of order?
					a[in] = a[in2];
					a[in2] = temp;
				}
			}
		}
	}

	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 */
	public static void sort(long[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 */
	public static void sort(long[] a, int fromIndex, int toIndex) {
		// outer loop (backward)
		for (int out = toIndex - 1; out > fromIndex; out--){
			// inner loop (forward)
			for (int in = fromIndex; in < out; in++){
				int in2 = in + 1;
				long temp = a[in];
				if (temp > a[in2]){ // out of order?
					a[in] = a[in2];
					a[in2] = temp;
				}
			}
		}
	}

	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 */
	public static void sort(float[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 */
	public static void sort(float[] a, int fromIndex, int toIndex) {
		// outer loop (backward)
		for (int out = toIndex - 1; out > fromIndex; out--){
			// inner loop (forward)
			for (int in = fromIndex; in < out; in++){
				int in2 = in + 1;
				float temp = a[in];
				if (Float.compare(temp, a[in2]) > 0){ // out of order?
					a[in] = a[in2];
					a[in2] = temp;
				}
			}
		}
	}

	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 */
	public static void sort(double[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 */
	public static void sort(double[] a, int fromIndex, int toIndex) {
		// outer loop (backward)
		for (int out = toIndex - 1; out > fromIndex; out--){
			// inner loop (forward)
			for (int in = fromIndex; in < out; in++){
				int in2 = in + 1;
				double temp = a[in];
				if (Double.compare(temp, a[in2]) > 0){ // out of order?
					a[in] = a[in2];
					a[in2] = temp;
				}
			}
		}
	}
	
	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 */
	public static void sort(Object[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Sort the specified array in ascending order.
	 * @param a the array to sort
	 * @param fromIndex index of first element (inclusive) to sort
	 * @param toIndex index of last element (exclusive) to sort
	 */
	public static void sort(Object[] a, int fromIndex, int toIndex) {
		//The JDK's implementation doesn't take care of null values
		//and simply assumes that the object implement comparable.
		//So we do the same.
		
		// outer loop (backward)
		for (int out = toIndex - 1; out > fromIndex; out--){
			// inner loop (forward)
			for (int in = fromIndex; in < out; in++){
				int in2 = in + 1;
				@SuppressWarnings("unchecked")
				Comparable<? super Object> temp = (Comparable<? super Object>)a[in];
				if (temp.compareTo(a[in2]) > 0){ // out of order?
					a[in] = a[in2];
					a[in2] = temp;
				}
			}
		}
	}
}
