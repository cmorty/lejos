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

	/**
	 * Method used to sort an Array
	 * @param a the array to sort
	 */
	public static void sort(byte[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Method used to sort an Array
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
	 * Method used to sort an Array
	 * @param a the array to sort
	 */
	public static void sort(short[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Method used to sort an Array
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
	 * Method used to sort an Array
	 * @param a the array to sort
	 */
	public static void sort(char[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Method used to sort an Array
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
	 * Method used to sort an Array
	 * @param a the array to sort
	 */
	public static void sort(int[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Method used to sort an Array
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
	 * Method used to sort an Array
	 * @param a the array to sort
	 */
	public static void sort(long[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Method used to sort an Array
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
	 * Method used to sort an Array
	 * @param a the array to sort
	 */
	public static void sort(float[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Method used to sort an Array
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
	 * Method used to sort an Array
	 * @param a the array to sort
	 */
	public static void sort(double[] a) {
		sort(a, 0, a.length);
	}
	
	/**
	 * Method used to sort an Array
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
}
