package lejos.pc.charting;

/** Each logged data item is represented by a 
 * <code>DataItem</code> instance
 */
class DataItem {
    public static int DT_INTEGER = 3;
    public static int DT_LONG    = 4;
    public static int DT_FLOAT   = 5;
    public static int DT_DOUBLE  = 6;
    public static int DT_STRING  = 7;
    
    /** The wrapper object of the datatype.
     */
    public Object value = null;
    
    /** The datatype 
     * DT_INTEGER = 3<BR>
     * DT_LONG    = 4<BR>
     * DT_FLOAT   = 5<BR>
     * DT_DOUBLE  = 6<BR>
     * DT_STRING  = 7
     */
    public int datatype = 3;
}
