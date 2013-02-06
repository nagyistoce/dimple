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

package com.analog.lyric.dimple.solvers.minsum;

import java.util.Arrays;
import com.analog.lyric.cs.Sort;
import com.analog.lyric.dimple.FactorFunctions.core.FactorFunction;
import com.analog.lyric.dimple.model.Factor;
import com.analog.lyric.dimple.solvers.core.STableFactorBase;
import com.analog.lyric.dimple.solvers.core.kbest.IKBestFactor;
import com.analog.lyric.dimple.solvers.core.kbest.KBestFactorEngine;
import com.analog.lyric.dimple.solvers.core.kbest.KBestFactorTableEngine;
import com.analog.lyric.dimple.solvers.interfaces.ISolverNode;
import com.analog.lyric.dimple.solvers.interfaces.ISolverVariable;

public class STableFactor extends STableFactorBase implements IKBestFactor
{	
	/*
	 * We cache all of the double arrays we use during the update.  This saves
	 * time when performing the update.
	 */
    protected double[][] _inPortMsgs = new double[0][];
    protected double[][] _outPortMsgs;
    protected double [][] _savedOutMsgArray;
    protected double [] _dampingParams;
    protected int _k;
    protected TableFactorEngine _tableFactorEngine;
    protected KBestFactorEngine _kbestFactorEngine;
    protected boolean _kIsSmallerThanDomain;
    protected boolean _dampingInUse = false;

    public STableFactor(Factor factor) 
	{
    	super(factor);
    	
		_dampingParams = new double[_factor.getSiblings().size()];		
		_tableFactorEngine = new TableFactorEngine(this);
		
		if (factor.getFactorFunction().factorTableExists(getFactor().getDomains()))
			_kbestFactorEngine = new KBestFactorTableEngine(this);
		else
			_kbestFactorEngine = new KBestFactorEngine(this);
		
		//setK(Integer.MAX_VALUE);
		
	}


	public void setK(int k)
	{
		
		_k = k;
		_kbestFactorEngine.setK(k);
		_kIsSmallerThanDomain = false;
		for (int i = 0; i < _inPortMsgs.length; i++)
		{
			if (_k < _inPortMsgs[i].length)
			{
				_kIsSmallerThanDomain = true;
				break;
			}
		}
	}
    
	public void updateEdge(int outPortNum) 
	{		
		if (_kIsSmallerThanDomain)
			_kbestFactorEngine.updateEdge(outPortNum);
		else
			_tableFactorEngine.updateEdge(outPortNum);

	}
	
	
	@Override
	public void update() 
	{
		if (_kIsSmallerThanDomain)
			_kbestFactorEngine.update();
		else
			_tableFactorEngine.update();
	}
	
    
	
    
    
	public void setDamping(int index, double val)
	{
		_dampingParams[index] = val;
		
		if (val != 0)
			_dampingInUse = true;
		
		_savedOutMsgArray = new double[_dampingParams.length][];
		for (int i = 0; i < _inPortMsgs.length; i++)
			_savedOutMsgArray[i] = new double[_inPortMsgs[i].length];

	}
	
	public double getDamping(int index)
	{
		return _dampingParams[index];
	}

	@Override
	public FactorFunction getFactorFunction() 
	{
		return getFactor().getFactorFunction();
	}

	@Override
	public double initAccumulator() 
	{
		return 0;
	}

	@Override
	public double accumulate(double oldVal, double newVal) 
	{
		return oldVal + newVal;
	}

	@Override
	public double combine(double oldVal, double newVal) 
	{
		if (oldVal < newVal)
			return oldVal;
		else
			return newVal;
	}

	@Override
	public void normalize(double[] outputMsg) 
	{
		double minVal = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < outputMsg.length; i++)
			if (outputMsg[i] < minVal)
				minVal = outputMsg[i];
		
		for (int i = 0; i < outputMsg.length; i++)
			outputMsg[i] -= minVal;
	}

	@Override
	public double evalFactorFunction(Object[] inputs) 
	{
		return -Math.log(getFactorFunction().eval(inputs));
	}

	@Override
	public void initMsg(double[] msg) 
	{
		Arrays.fill(msg, Double.POSITIVE_INFINITY);
	}

	@Override
	public double getFactorTableValue(int index) 
	{
		return getFactorTable().getPotentials()[index];
	}
	
	@Override
	public int[] findKBestForMsg(double[] msg, int k) 
	{
		return Sort.quickfindFirstKindices(msg, k);
	}


	@Override
	public double[][] getInPortMsgs() 
	{
		// TODO Auto-generated method stub
		return _inPortMsgs;
	}


	@Override
	public double[][] getOutPortMsgs() 
	{
		// TODO Auto-generated method stub
		return _outPortMsgs;
	}





	@Override
	public void createMessages() 
	{

		int numPorts = _factor.getSiblings().size();
	    _inPortMsgs = new double[numPorts][];	    
	    _outPortMsgs = new double[numPorts][];
	    
	    if (_dampingInUse)
	    	_savedOutMsgArray = new double[numPorts][];
	    
	    
		for (int i = 0; i < _factor.getVariables().size(); i++)
		{
			ISolverVariable sv = _factor.getVariables().getByIndex(i).getSolver();
			Object [] messages = sv.createMessages(this);
			_outPortMsgs[i] = (double[])messages[0];
			_inPortMsgs[i] = (double[])messages[1];
		}		

		for (int port = 0; port < numPorts; port++)
	    {
	    	if (_dampingInUse)
	    		_savedOutMsgArray[port] = new double[_inPortMsgs[port].length];
	    }		
	    
	    setK(Integer.MAX_VALUE);
	}

	
	@Override
	public void initialize(int i)
	{
		SVariable sv = (SVariable)_factor.getSiblings().get(i).getSolver();
		_inPortMsgs[i] = (double[])sv.resetInputMessage(_inPortMsgs[i]);
		
	}



	@Override
	public void moveMessages(ISolverNode other, int portNum, int otherPort) 
	{
		STableFactor tf = (STableFactor)other;
		_inPortMsgs[portNum] = tf._inPortMsgs[otherPort];	    
	    _outPortMsgs[portNum] = tf._outPortMsgs[otherPort];
	    if (_dampingInUse)
	    	_savedOutMsgArray[portNum] = tf._savedOutMsgArray[otherPort];
	    
	}
	
	@Override
	public Object getInputMsg(int portIndex) 
	{
		return _inPortMsgs[portIndex];
	}

	@Override
	public Object getOutputMsg(int portIndex) 
	{
		return _outPortMsgs[portIndex];
	}

}

