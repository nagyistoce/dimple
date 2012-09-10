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

package com.analog.lyric.dimple.matlabproxy;

import com.analog.lyric.dimple.model.DimpleException;
import com.analog.lyric.dimple.model.NodeId;
import com.analog.lyric.dimple.model.Real;
import com.analog.lyric.dimple.model.RealDomain;

/*
 * This proxy wraps the Real class
 */
public class PRealVariable extends PVariableBase
{
	public PRealVariable(Real impl)
	{
		_variable = impl;
	}

	
	public PRealVariable(PRealVariable vOther)
	{
		_variable = vOther._variable;
	}
	

	public PRealVariable()  {this(new RealDomain(), null);}
	public PRealVariable(RealDomain domain)  {this(domain, null);}
	public PRealVariable(Object input)  {this(new RealDomain(), input);}
	
	public PRealVariable(RealDomain domain, Object input) 
	{
		_variable = new Real(NodeId.getNext(),"real",domain,input);
	}
	
	protected Object clone()
	{
		PRealVariable vCopy = new PRealVariable((Real)_variable);
		return vCopy;
	}
	
	public Object getInput() 
	{
		return _variable.getInputObject();
	}
	
	public void setInput(Object input) 
	{
    	if (getModelerObject().getParentGraph() != null && getModelerObject().getParentGraph().isSolverRunning()) 
    		throw new DimpleException("No changes allowed while the solver is running.");


		_variable.setInputObject(input);
	}

	public Object getBelief() 		// Leaves open the format of the beliefs for a particular solver
	{
		return _variable.getBeliefObject();
	}
	
	//Should this be common mentod?
	public PRealDomain getDomain()
	{
		return new PRealDomain((RealDomain)_variable.getDomain());
	}



	
	@Override
	public boolean isDiscrete() 
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isReal() 
	{
		// TODO Auto-generated method stub
		return true;
	}



	@Override
	public boolean isFactor() 
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isVariable() 
	{
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isJoint()
	{
		return false;
	}
}