package lejos.util;

/**
 * 
 * Bublesort is a class designed to sort an Array using Bublesort Method.
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class BubbleSort {

	private float[] a;
	private int nElems;

	/**
	 * Constructor
	 * 
	 * @param elements
	 */
	public BubbleSort(float[] elements){
		a = elements;
		nElems = elements.length;
	}

	/**
	 * Method used to sort an Array
	 * 
	 * @return
	 */
	public float[] sort() {
		int out, in;

		for (out = nElems - 1; out > 1; out--){
			// outer loop (backward)
			for (in = 0; in < out; in++){
				// inner loop (forward)
				if (a[in] > a[in + 1]){ // out of order?
					swap(in, in + 1); // swap them
				}
			}
		}
	    
		return a;
	}
	
	/**
	 * Internal method used in Bubble method
	 * @param one
	 * @param two
	 */
	private void swap(int one, int two) {
		float temp = a[one];
		a[one] = a[two];
		a[two] = temp;
	}
	  
	/**
	 * Method used to display a sorted array
	 */
	public void display() {
		for (int j = 0; j < nElems; j++){
			System.out.print(a[j] + " ");
		}
	}
}
