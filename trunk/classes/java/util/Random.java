package java.util;

/**
 * Pseudo-random number generation.
 */
public class Random
{
  private int iPrevSeed, iSeed;
  private boolean haveNextNextGaussian;
  private double nextNextGaussian;
  
  public Random (long seed)
  {
    iPrevSeed = 1;
    iSeed = (int) seed;
  }

    public Random()
    {
	this(System.currentTimeMillis());
    }
  
  /**
   * @return A random positive or negative integer.
   */
  public int nextInt()
  {
    int pNewSeed = (iSeed * 48271) ^ iPrevSeed;
    iPrevSeed = iSeed;
    iSeed = pNewSeed;
    return pNewSeed;
  }

    /**
     * Returns a random integer in the range 0...n-1.
     * @param n  the bound
     * @return A random integer in the range 0...n-1.
     */
    public int nextInt (int n)
    {
	int m = nextInt() % n;
	return m >= 0 ? m : m + n;
    }

    /**
     * Returns a random boolean in the range 0-1.
     * @return A random boolean in the range 0-1.
     */
    public boolean nextBoolean(){
    	boolean nextBoolean;
		int nextInt = this.nextInt(2);
		if(nextInt == 1){
			nextBoolean = true;
		}else{
			nextBoolean = false;			
		}
		return nextBoolean;    	
    }

    public double nextDouble()
    {
		int n = Integer.MAX_VALUE;

		// Just to ensure it does not return 1.0
		while (n == Integer.MAX_VALUE)
			n = Math.abs(this.nextInt());

		return n * (1.0 / Integer.MAX_VALUE);
    }
    
    /**
     * Returns the next pseudorandom, Gaussian ("normally") distributed double value with mean 0.0 and standard deviation 1.0 from this random number generator's sequence.
     * @return Returns the next pseudorandom, Gaussian ("normally") distributed double value
     */
    public double nextGaussian(){
    	//http://java.sun.com/j2se/1.4.2/docs/api/java/util/Random.html#nextGaussian()
        if (haveNextNextGaussian) {
            haveNextNextGaussian = false;
            return nextNextGaussian;
	    } else {
	            double v1, v2, s;
	            do { 
	                    v1 = 2 * nextDouble() - 1;   // between -1.0 and 1.0
	                    v2 = 2 * nextDouble() - 1;   // between -1.0 and 1.0
	                    s = v1 * v1 + v2 * v2;
	            } while (s >= 1 || s == 0);
	            double multiplier = Math.sqrt(-2 * Math.log(s)/s);
	            nextNextGaussian = v2 * multiplier;
	            haveNextNextGaussian = true;
	            return v1 * multiplier;
	    }  	
    }  
}
