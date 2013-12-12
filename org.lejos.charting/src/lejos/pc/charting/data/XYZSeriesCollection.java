package lejos.pc.charting.data;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;

/**
 * XYZ dataset for bubble chart (or other renderer that requires a XYZDataset) ability. Assumes that 
 * the series will be added to XYZSeriesCollection with 
 * XYSeriesCollection.addSeries(XYSeries) and that the series XYSeries param is populated with 
 * XYSeries.add(XYZDataItem) method and not any of the add() methods that use primitives (i.e. XYSeries.add(double x, double y))
 * 
 * @author kirk
 *
 */
public class XYZSeriesCollection extends XYSeriesCollection implements XYZDataset {

    public Number getZ(int series, int item) {
        XYSeries ts = this.getSeries(series);
        XYZDataItem xyItem = (XYZDataItem) ts.getDataItem(item);
        return xyItem.getZ();
    }

    public double getZValue(int series, int item) {
        Double d1 = (Double) getZ(series, item);
        return d1.doubleValue();
    }

}
