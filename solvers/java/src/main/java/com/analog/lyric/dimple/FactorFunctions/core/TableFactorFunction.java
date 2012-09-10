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

package com.analog.lyric.dimple.FactorFunctions.core;

import com.analog.lyric.dimple.model.Discrete;
import com.analog.lyric.dimple.model.DiscreteDomain;
import com.analog.lyric.dimple.model.Domain;


public class TableFactorFunction extends FactorFunction 
{
	
	//private HashMap<ArrayList<Object>,Double> _lookupTable = null;
	private FactorTable _factorTable;
	//private DiscreteDomain [] _domains;

	public TableFactorFunction(String name,FactorTable factorTable) 
	{		
		super(name);
		
		_factorTable = factorTable;
		//_domains = domains;
		
	}
	
	public TableFactorFunction(String name, int [][] indices, double [] probs, DiscreteDomain ... domains) 
	{
		this(name,new FactorTable(indices,probs,domains));
	}
	public TableFactorFunction(String name, int [][] indices, double [] probs, Discrete... discretes) 
	{
		this(name,new FactorTable(indices,probs,discretes));
	}
	

	@Override
	public double eval(Object... input) 
	{
		return _factorTable.evalAsFactorFunction(input);
	}
	

	public FactorTable getFactorTable()
	{
		return _factorTable;
	}
	
    public FactorTable getFactorTable(Domain [] domainList)
    {
    	//first step, convert domains to DiscreteDOmains
    	//make sure domain lists match
    	if (domainList.length != _factorTable.getDomains().length)
    		throw new RuntimeException("domain lists don't match sizes.  argument size: " + domainList.length + " factorTable's domain size: " + _factorTable.getDomains().length);
    	    	
    	return _factorTable;
    }

}