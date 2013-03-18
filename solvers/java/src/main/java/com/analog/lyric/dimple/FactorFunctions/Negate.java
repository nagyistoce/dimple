/*******************************************************************************
*   Copyright 2012 Analog Devices, Inc.
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
********************************************************************************/

package com.analog.lyric.dimple.FactorFunctions;

import com.analog.lyric.dimple.FactorFunctions.core.FactorFunction;
import com.analog.lyric.dimple.FactorFunctions.core.FactorFunctionUtilities;


/**
 * Deterministic negation. This is a deterministic directed factor
 * (if smoothing is not enabled).
 * 
 * Optional smoothing may be applied, by providing a smoothing value in
 * the constructor. If smoothing is enabled, the distribution is
 * smoothed by exp(-difference^2/smoothing), where difference is the
 * distance between the output value and the deterministic output value
 * for the corresponding inputs.
 * 
 * The variables are ordered as follows in the argument list:
 * 
 * 1) Output (negation of input)
 * 2) Input (double or integer)
 * 
 */
public class Negate extends FactorFunction
{
	protected double _beta = 0;
	protected boolean _smoothingSpecified = false;
	public Negate() {this(0);}
	public Negate(double smoothing)
	{
		super("Negate");
		if (smoothing > 0)
		{
			_beta = 1 / smoothing;
			_smoothingSpecified = true;
		}
	}
	
    @Override
    public double evalEnergy(Object ... arguments)
    {
    	double out = FactorFunctionUtilities.toDouble(arguments[0]);
    	double in = FactorFunctionUtilities.toDouble(arguments[1]);
    	double negation = -in;

    	if (_smoothingSpecified)
    	{
    		double diff = negation - out;
    		double potential = diff*diff;
    		return potential*_beta;
    	}
    	else
    	{
    		return (negation == out) ? 0 : Double.POSITIVE_INFINITY;
    	}
    }
    
    
    @Override
    public final boolean isDirected()	{return true;}
    @Override
	public final int[] getDirectedToIndices() {return new int[]{0};}
    @Override
	public final boolean isDeterministicDirected() {return !_smoothingSpecified;}
    @Override
	public final void evalDeterministicFunction(Object... arguments)
    {
    	arguments[0] = -FactorFunctionUtilities.toDouble(arguments[1]);		// Replace the output value
    }
}