package junit.util;

/**
 * Some utility methods used in leJOSUnit.
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class Util {

    // static attributes

    /** fixed message for an overflow in time display */
    private static final char[] OVERFLOW = { 'o', 'v', 'e', 'r', 'f' };

    // static methods

    /**
     * Copies one array to another.
     * 
     * Taken from System. Can NOT be used, as it is in package scope.
     * 
     * @param src the source array 
     * @param srcOffset the offset within source array 
     * @param dest the destination array 
     * @param destOffset the offset within destination array 
     * @param length the number of chars to copy
     */
    // TODO cross port RCX/NXT: needed for RCX
//    public static void arraycopy(
//            char[] src, int srcOffset, 
//            char[] dest, int destOffset, int length) {
//        for (int i = 0; i < length; i++) {
//            dest[i + destOffset] = src[i + srcOffset];
//        }
//    }

    /**
     * Concatenates two Strings without using StringBuffer, to 
     * avoid linking StringBuffer and therefore Math library.
     * 
     * @param s1 the first string
     * @param s2 the second string
     * @return the concatenated string. Will be allocated new.
     */
    public static String concat(String s1, String s2) {
        char[] cbuf;
        // allocate buffer with required buffer size
        cbuf = new char [s1.length() + s2.length()];
        // copy s1 into the buffer
        System.arraycopy (
            s1, 0, cbuf, 0, s1.length());
        // copy s2 into the buffer
        System.arraycopy (
            s2, 0, cbuf, s1.length(), s2.length());
        return new String (cbuf, 0, cbuf.length);
    }

    /**
     * Concatenates three Strings without using StringBuffer, to 
     * avoid linking StringBuffer and therefore Math library.
     * 
     * @param s1 the first string
     * @param s2 the second string
     * @param s3 the third string
     * @return the concatenated string. Will be allocated new.
     */
    public static String concat(String s1, String s2, String s3) {
        char[] cbuf;
        // allocate buffer with required buffer size
        cbuf = new char [s1.length() + s2.length() + s3.length()];
        // copy s1 into the buffer
        System.arraycopy (
            s1, 0, cbuf, 0, s1.length());
        // copy s2 into the buffer
        System.arraycopy (
            s2, 0, cbuf, s1.length(), s2.length());
        // copy s3 into the buffer
        System.arraycopy (
            s3, 0, cbuf, s1.length() + s2.length(), s3.length());
        return new String (cbuf, 0, cbuf.length);
    }

    /**
     * Show elapsed time in an optimal way.
     * 
     * @param t the elapsed time
     * @return the string (as char[]) to display. Always returns 5 characters.
     */
    public static char[] convertTime(int t) {
        char[] cbuf = new char[] {
            // init with blanks
            '0', '0', '0', 'm', 's' };
        // optimized display for elapsed time
        if (t < 1000) {
            // if the time less than 1 sec, show in milliseconds
            // first convert the time in a number as string
            // append then "ms" at the end
            // if too few characters, 0's are prefilled 
            String s = new Integer(t).toString();
            char[] c = s.toCharArray();
            // 3 means middle of buffer, copy length of number
            //    length=1:    3-1=2, 1
            //    length=2:    3-2=1, 2
            //    length=3:    3-3=0, 3
            System.arraycopy(c, 0, cbuf, 3 - c.length, c.length);
            return cbuf;
        } else if (t < 10000000) {
            String sbuf = new Integer(t).toString();
            // if the time is more than 1 sec, and less than 1000 seconds,
            // show in seconds
            // do NOT use floating point arithemtic to
            // avoid using the math library

            // algorithm: print whole int to string buffer
            // cut the last characters

            //
            //           01234  decPoint  
            // 
            // 1234567 = 1234s  4
            //  123456 = 123.s  3
            //   12345 = 12.3s  2
            //    1234 = 1.23s  1
            //
            int decPoint =
                (t >= 1000000)
                    ? 4
                    : (t >= 100000)
                    ? 3
                    : (t >= 10000)
                    ? 2
                    : (t > 1000)
                    ? 1
                    : 0;

            // first digit will be taken always
            cbuf[0] = sbuf.charAt(0);
            // set '.'. If at position 4, will be overwritten with 's'
            cbuf[decPoint] = '.';
            // last digit always 's'
            cbuf[4] = 's';

            // set the chars according the table above
            if (decPoint == 1) {
                cbuf[2] = sbuf.charAt(1);
                cbuf[3] = sbuf.charAt(2);
            } else if (decPoint == 2) {
                cbuf[1] = sbuf.charAt(1);
                cbuf[3] = sbuf.charAt(2);
            } else if (decPoint == 3) {
                cbuf[1] = sbuf.charAt(1);
                cbuf[2] = sbuf.charAt(2);
            } else if (decPoint == 4) {
                cbuf[1] = sbuf.charAt(1);
                cbuf[2] = sbuf.charAt(2);
                cbuf[3] = sbuf.charAt(3);
            }
            return cbuf;
        } else {
            return OVERFLOW;
        }
    }
}
