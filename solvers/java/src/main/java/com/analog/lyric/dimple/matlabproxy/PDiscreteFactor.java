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

import com.analog.lyric.dimple.matlabproxy.PFactor;
import com.analog.lyric.dimple.model.DiscreteFactor;


/*
 * This proxy wraps the TableFunctionBase class
 */
public class PDiscreteFactor  extends PFactor
{

	public PDiscreteFactor(DiscreteFactor impl) 
	{
		super(impl);
	}
	
	public PFactorTable getFactorTable() 
	{
		return new PFactorTable(((DiscreteFactor)this.getModelerObject()).getFactorTable());
	}
	
	public int[][] getPossibleBeliefIndices() 
	{
		return ((DiscreteFactor)_factor).getPossibleBeliefIndices();
	}

	
}