package lejos.pc.charting;

/** Each logged data item is represented by a 
 * <code>DataItem</code> instance which provide the value as a wrapper and it's datatype.
 * @see DataLogger
 * @see DataLogger.LoggerListener#logLineAvailable
 */
public class DataItem {
    public static final int DT_BOOLEAN = 0;
    public static final int DT_BYTE    = 1;
    public static final int DT_SHORT   = 2;
    public static final int DT_INTEGER = 3;
    public static final int DT_LONG    = 4;
    public static final int DT_FLOAT   = 5;
    public static final int DT_DOUBLE  = 6;
    public static final int DT_STRING  = 7;
    
    /** The <code>Number</code> wrapper object of the datatype. Use <code>datatype</code> to determine correct cast type.
     */
    public Object value = null;

    /** The datatype. 
     *@see #DT_BOOLEAN
     *@see #DT_BYTE
     *@see #DT_SHORT
     *@see #DT_INTEGER
     *@see #DT_LONG
     *@see #DT_FLOAT
     *@see #DT_DOUBLE
     *@see #DT_STRING
     */
    public int datatype = 3;
}
