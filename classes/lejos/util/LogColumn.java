package lejos.util;

/**
 * Use to define the header names, datatypes, count, chartable attribute, and range axis ID (for multiple axis charting).
 * 
 * @see Logger#setColumns
 * @see NXTDataLogger
 */
public class LogColumn {
    public static final int    DT_BOOLEAN = 0;
    public static final int    DT_BYTE    = 1;
    public static final int    DT_SHORT   = 2;
    public static final int    DT_INTEGER = 3;        
    public static final int    DT_LONG    = 4;
    public static final int    DT_FLOAT   = 5;
    public static final int    DT_DOUBLE  = 6;
    
    private String name;
    private int datatype=DT_FLOAT; //default is float
    private boolean chartSeries=true; // true = display on chart
    private int rangeAxisID = 0; // zero-based ID of range axis for multi-axis charting. limit to 4 axes
    private int byteCount=4;

    /**
     * @param name name the label/name of the column/series
     */
    public LogColumn(String name) {
        this.name = name;
    }

    /** throws unchecked IllegalArgumentException if bad datatype val.
     * Axis ID default to 1. ChartSeries default to true.
     * @param name The label/name of the column/series
     * @param datatype The datatype of of the column/series
     * @see #setDatatype
     */
    public LogColumn(String name, int datatype) {
        this(name);
        setDatatype(datatype);
    }

    /** throws unchecked IllegalArgumentException if bad datatype val
     * @param name The label/name of the column/series
     * @param datatype The datatype of of the column/series
     * @param chartSeries <code>true</code> to chart the data, <code>false</code> to only log it.
     * @param rangeAxisID Range axis ID 1-4. 1 is default. NOT IMPLEMENTED YET AS OF 6/27/11
     * @see #setDatatype
     */
    public LogColumn(String name, int datatype, boolean chartSeries, int rangeAxisID) {
        this(name, datatype);
        this.chartSeries=chartSeries;
        this.rangeAxisID=rangeAxisID;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    // 

    /** Set the datatype for this column/series. Throws unchecked IllegalArgumentException if bad datatype val
     * @param datatype The datatype. Use one of the constant values list below in "See also".
     * @see Logger
     * @see #DT_BOOLEAN
     * @see #DT_BYTE
     * @see #DT_SHORT
     * @see #DT_INTEGER
     * @see #DT_LONG
     * @see #DT_FLOAT
     * @see #DT_DOUBLE
     * @throws IllegalArgumentException if bad datatype value
     */
    public void setDatatype(int datatype) {
        // validate datatypes
        switch (datatype) { 
            case DT_BOOLEAN :
            case DT_BYTE    :
            case DT_SHORT   :
            case DT_INTEGER : 
            case DT_FLOAT   :
                this.byteCount=4;
                break;
            case DT_LONG    :
            case DT_DOUBLE  :
                this.byteCount=8;
                break;
            default:
                throw new IllegalArgumentException("Invalid datatype " + datatype);
        }
        this.datatype = datatype;
    }

    /**
     * @return the datatype size in bytes
     */
    public int getSize() {
        return this.byteCount;
    }
    
    public int getDatatype() {
        return this.datatype;
    }

    public void setChartSeries(boolean chartSeries) {
        this.chartSeries = chartSeries;
    }

    public boolean isChartSeries() {
        return this.chartSeries;
    }

    /** Throws unchecked IllegalArgumentException if rangeAxisID &lt;1 or rangeAxisID &gt;4.
     * 4 range axes are available for multiple axis charting. Default is 1
     * @param rangeAxisID The range axis ID 1-4
     * @throws IllegalArgumentException
     */
    public void setRangeAxisID(int rangeAxisID) {
        if (rangeAxisID<1 || rangeAxisID>4) throw new IllegalArgumentException("Invalid axis ID " + rangeAxisID);
        this.rangeAxisID = rangeAxisID;
    }

    public int getRangeAxisID() {
        return this.rangeAxisID;
    }
}
