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

import com.analog.lyric.dimple.FactorFunctions.core.FactorTable;
import com.analog.lyric.dimple.FactorFunctions.core.TableFactorFunction;

public class PTableFactorFunction extends PFactorFunction
{

	public PTableFactorFunction(TableFactorFunction tff)
	{
		super(tff);
	}

	public PTableFactorFunction(String name, PDiscreteDomain [] domains)
	{
		super(new TableFactorFunction(name,new FactorTable(PHelpers.convertDomains(domains))));
	}

	public PTableFactorFunction(String name, Object values,PDiscreteDomain [] domains)
	{
		super(new TableFactorFunction(name,new FactorTable(values,PHelpers.convertDomains(domains))));
	}

	public PTableFactorFunction(String name, int [][] indices, double [] values, PDiscreteDomain [] domains)
	{
		super(new TableFactorFunction(name, new FactorTable(indices,values, PHelpers.convertDomains(domains))));
	}

	public PFactorTable getFactorTable()
	{
		return new PFactorTable(((TableFactorFunction)getModelerObject()).getFactorTable());
	}


}