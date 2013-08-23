package com.analog.lyric.dimple.model;

import java.util.Arrays;

import com.google.common.math.DoubleMath;
import com.google.common.primitives.Doubles;

/**
 * A discrete domain defined by a range of doubles separated by a constant positive interval.
 * <p>
 * @see DiscreteDomain#range(double, double)
 * @see DiscreteDomain#range(double, double, double)
 */
public class DoubleRangeDomain extends TypedDiscreteDomain<Double>
{
	private static final long serialVersionUID = 1L;
	
	private final double _lowerBound;
	private final double _upperBound;
	private final double _interval;
	private final int _size;
	private final double _tolerance;
	
	/*--------------
	 * Construction
	 */
	
	private DoubleRangeDomain(double lowerBound, double upperBound, double interval, double tolerance, int size)
	{
		super(computeHashCode(lowerBound, upperBound, interval, tolerance));
		
		if (interval <= 0.0)
		{
			throw new IllegalArgumentException(
				String.format("Non-positive interval '%g' for double range domain", interval));
		}
		
		if (tolerance < 0.0)
		{
			throw new IllegalArgumentException(
				String.format("Negative tolerance '%g' for double range domain", tolerance));
		}
		
		if (tolerance >= interval/2)
		{
			throw new IllegalArgumentException(
				String.format("Tolerance '%g' is too large for interval '%g' in double range domain",
					tolerance, interval));
		}
		
		if (upperBound < lowerBound)
		{
			throw new IllegalArgumentException(
				String.format("Bad double range [%g,%g]: upper bound lower than lower bound", lowerBound, upperBound));
		}
		
		_lowerBound = lowerBound;
		_upperBound = upperBound;
		_size = size;
		_interval = interval;
		_tolerance = tolerance;
	}

	static DoubleRangeDomain create(double lowerBound, double upperBound, double interval, double tolerance)
	{
		if (!Doubles.isFinite(tolerance))
		{
			tolerance = defaultToleranceForInterval(interval);
		}
		
		int size = 1 + (int)((tolerance + upperBound - lowerBound) / interval);
		
		return new DoubleRangeDomain(lowerBound, upperBound, interval, tolerance, size);
	}
	
	private static int computeHashCode(double ... values)
	{
		return Arrays.hashCode(values);
	}

	/*----------------
	 * Object methods
	 */
	
	@Override
	public boolean equals(Object that)
	{
		if (this == that)
		{
			return true;
		}
		
		if (that instanceof DoubleRangeDomain)
		{
			DoubleRangeDomain thatRange = (DoubleRangeDomain)that;
			return
				_lowerBound == thatRange._lowerBound &&
				_upperBound == thatRange._upperBound &&
				_interval == thatRange._interval &&
				_tolerance == thatRange._tolerance;
		}
		
		return false;
	}
	
	/*------------------------
	 * DiscreteDomain methods
	 */

	/**
	 * {@inheritDoc}
	 * <p>
	 * Use {@link #getDoubleElement(int)} instead of this method to avoid allocating an {@link Double} object.
	 */
	@Override
	public Double getElement(int i)
	{
		return getDoubleElement(i);
	}

	@Override
	public Double[] getElements()
	{
		Double[] elements = new Double[_size];
		
		for (int i = 0; i < _size; ++i)
		{
			elements[i] = getDoubleElement(i);
		}
		
		return elements;
	}

	@Override
	public int size()
	{
		return _size;
	}

	/**
	 * {@inheritDoc}
	 * @see #getIndex(double)
	 */
	@Override
	public int getIndex(Object value)
	{
		if (value instanceof Number)
		{
			return getIndex(((Number)value).doubleValue());
		}
		
		return -1;
	}
	
	/**
	 * Same as {@link #getIndex(Object)} but taking a double.
	 */
	public int getIndex(double value)
	{
		int index = (int)Math.rint((value - _lowerBound) / _interval);
		return index < _size && DoubleMath.fuzzyEquals(_lowerBound + index * _interval, value, _tolerance) ? index : - 1;
	}

	/*---------------------------
	 * DoubleRangeDomain methods
	 */
	
	/**
	 * Same as {@link #getElement(int)} but returning an unboxed double.
	 */
	public double getDoubleElement(int i)
	{
		assertIndexInBounds(i, _size);
		return _lowerBound + i * _interval;
	}
	
	/**
	 * The interval separating consecutive elements of the domain.
	 * <p>
	 * Guaranteed to be positive.
	 */
	public double getInterval()
	{
		return _interval;
	}
	
	/**
	 * Returns the lower bound for the domain which is also the same as the first element.
	 * <p>
	 * @see #getUpperBound()
	 */
	public double getLowerBound()
	{
		return _lowerBound;
	}
	
	public double getUpperBound()
	{
		return _upperBound;
	}
	
	/**
	 * The tolerance is the maximum difference allowed between a value and a domain element
	 * for it to be consider to be the same element.
	 * <p>
	 * The tolerance must be positive and less than half the value of {@link #getInterval()} and is typically
	 * much smaller than that.
	 */
	public double getTolerance()
	{
		return _tolerance;
	}

	static public double defaultToleranceForInterval(double interval)
	{
		return Math.abs(interval * 1e-6);
	}
}
