package skoehler.sensor.unit;

import skoehler.sensor.api.VectorData;
import skoehler.sensor.filter.OffsetFilter;
import skoehler.sensor.filter.ScaleFilter;
import skoehler.sensor.filter.ScaleOffsetFilter;

/**
 * Class that can represent units.
 * This could also be the base class of an Enum.
 * This can also be used to create filters.
 */
public class Unit {
	private final int quantity;
	private final float factor;
	private final float offset;
	
	public Unit(int quantity, float factor, float offset)
	{
		this.quantity = quantity;
		this.factor = factor;
		this.offset = offset;
	}
	
	private static float[] fillArray(int count, float v)
	{
		float[] r = new float[count];
		for (int i=0; i<count; i++)
			r[i] = v;
		return r;
	}
	
	public static VectorData convert(Unit from, Unit to, VectorData source)
	{
		if (from.quantity != to.quantity)
			throw new IllegalArgumentException();
		
		float f = to.factor / from.factor;
		float o = to.offset - f * from.offset;
		int ac = source.getAxisCount();
		
		if (o == 0f)
		{
			if (f==1f)
				return source;
			
			return new ScaleFilter(source, fillArray(ac, f));
		}
		if (f == 1f)
			return new OffsetFilter(source, fillArray(ac, o));

		return new ScaleOffsetFilter(source, fillArray(ac, f), fillArray(ac, o));
	}
}
