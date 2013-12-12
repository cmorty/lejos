package lejos.pc.charting.data;

import org.jfree.data.xy.XYDataItem;

/**
 * XYZ dataItem used polymorphically in XYSeries tracking/management in XYSeriesCollection. Utilized
 * by subclass XYZSeriesCollection.
 * @author kirk
 *
 */
public class XYZDataItem extends XYDataItem {
    /** The z-value. */
    private Number z;
    
    public XYZDataItem(Number x, Number y, Number z) {
        super(x, y);
        if (z == null) {
            throw new IllegalArgumentException("Null 'z' argument.");
        }
        this.z = z;
    }

    public XYZDataItem(double x, double y, double z) {
        this(new Double(x), new Double(y), new Double(z));
    }

    /**
     * Clone from an exiting XYZDataItem
     * 
     * @param xyzCloneItem
     */
    public XYZDataItem(XYZDataItem xyzCloneItem) {
        this(xyzCloneItem.getX(), xyzCloneItem.getY(), xyzCloneItem.getZ());
    }

    /**
     * Returns the z-value as a double primitive.
     *
     * @return The z-value 
     */
    public double getZValue() {
        double result = Double.NaN;
        if (this.z != null) {
            result = this.z.doubleValue();
        }
        return result;
    }
    
    /**
     * Returns the z-value.
     *
     * @return The z-value (possibly <code>null</code>).
     */
    public Number getZ() {
        return this.z;
    }
    
    /**
     * Sets the z-value for this data item.  Note that there is no
     * corresponding method to change the x-value.
     *
     * @param z  the new z-value.
     */
    public void setZ(double z) {
        setZ(new Double(z));
    }

    /**
     * Sets the z-value for this data item.  Note that there is no
     * corresponding method to change the x-value.
     *
     * @param z  the new z-value (<code>null</code> permitted).
     */
    public void setZ(Number z) {
        this.z = z;
    }
}
