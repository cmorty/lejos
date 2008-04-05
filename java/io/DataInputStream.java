
package java.io;


/**
 * Reads java data types transmitted as bytes over an InputStream.
 */
public class DataInputStream extends InputStream {
    
   protected InputStream  in;
    
   public DataInputStream(InputStream in)
   {
      this.in = in; 
   }
   
   
   public int available() throws IOException {
       return this.in.available();
   }

/**
   * Reads the next byte of data from this input stream. The value 
   * byte is returned as an <code>int</code> in the range 
   * <code>0</code> to <code>255</code>. If no byte is available 
   * because the end of the stream has been reached, the value 
   * <code>-1</code> is returned. This method blocks until input data 
   * is available, the end of the stream is detected, or an exception 
   * is thrown. 
   * <p>
   * This method
   * simply performs <code>in.read()</code> and returns the result.
   *
   * @return     the next byte of data, or <code>-1</code> if the end of the
   *             stream is reached.
   * @exception  IOException  if an I/O error occurs.
   * @see        java.io.FilterInputStream#in
   */
   public int read() throws IOException {
      return in.read();
   }
    
   public final int read(byte b[]) throws IOException {
      return in.read(b, 0, b.length);
   }

   public final int read(byte b[], int off, int len) throws IOException {
      return in.read(b, off, len);
   }
/*
   public final void readFully(byte b[]) throws IOException {
      readFully(b, 0, b.length);
   }
*/
/*   public final void readFully(byte b[], int off, int len) throws IOException {
	  if (len < 0)
	     throw new IOException();

    	int n = 0;
    	while (n < len) {
         int count = in.read(b, off + n, len - n);
    	   if (count < 0)
    		   throw new IOException();
    	   n += count;
      }
   }
*/
/*   public final int skipBytes(int n) throws IOException {

      int total = 0;
      int cur = 0;
      
      while ((total<n) && ((cur = (int) in.skip(n-total)) > 0)) {
         total += cur;
      }
      return total;
   }
*/
   public final boolean readBoolean() throws IOException {
      int ch = in.read();
      if (ch < 0)
         throw new EOFException();
      return (ch != 0);
   }

   public final byte readByte() throws IOException {
      int K = 256;
	   int ch = in.read();
      if (ch < 0)
         throw new EOFException();
      while(ch>128)ch = ch - K;;
      return (byte)(ch);
   }
/*
   public final int readUnsignedByte() throws IOException {
	   int ch = in.read();
	   if (ch < 0)
	      throw new IOException();
	   return ch;
   }
*/
   public final short readShort() throws IOException 
   {
      byte b0 = readByte();  
      byte b1 = readByte(); // Note if b2> 127, it is will be negataive.
      int x= b1;  // low order byte
      x = x & 0x000000FF;// keep only the low order byte
      x = x | b0 <<8;
      return (short)x;
   }
   
   public final int readInt() throws IOException 
   {
      byte b0 = readByte();
      byte b1 = readByte();
      byte b2 = readByte();
      byte b3 = readByte();
      int x= b3;  
      x = x & 0x000000FF; // keep only low order byte
      x = x | b2<<8; 
      x = x & 0x0000FFFF; // keep only 2 low order bytes
      x = x | b1<<16;
      x = x & 0x00FFFFFF; // etc
      x = x | b0<<24;  //high byte
      return x;
   }
   
   public final char readChar() throws IOException {
      InputStream in = this.in;
      int ch1 = in.read();
      int ch2 = in.read();
      if ((ch1 | ch2) < 0)
         throw new EOFException();
      return (char)((ch1 << 8) + (ch2 << 0));
   }

  
/*
   public final long readLong() throws IOException {
      InputStream in = this.in;
      return ((long)(readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
   }
   */

   public final float readFloat() throws IOException {
      return Float.intBitsToFloat(readInt());
   }

/*
   public final double readDouble() throws IOException {
      return Double.longBitsToDouble(readLong());
   }*/
   
	public String readLine() throws IOException{
		StringBuffer strb = new StringBuffer();
		boolean endOfLine = false;
		while(!endOfLine){
			char c = readChar();
			if(c=='\n'){endOfLine = true;}
			else if(c==(char)-1){endOfLine = true;}
			else{strb.append(c);}
		}
		return strb.toString();
	}
	
	public void close() throws IOException {
		in.close();
	}
}