package lejos.robotics.mapping;

/**
 * 
 * Experimental class used to sort a set of points
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class BubbleSort {
	  private float[] a;

	  private int nElems;
	  
	  public BubbleSort(float[] elements){
		  a = elements;
		    nElems = elements.length;
	  }

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
	  
	  private void swap(int one, int two) {
	    float temp = a[one];
	    a[one] = a[two];
	    a[two] = temp;
	  }
	  
	  //   displays array contents
	  public void display() {
	    for (int j = 0; j < nElems; j++)
	      System.out.print(a[j] + " ");
	    System.out.println("");
	  }
}
