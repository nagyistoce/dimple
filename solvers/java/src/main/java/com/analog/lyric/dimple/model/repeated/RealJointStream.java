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

package com.analog.lyric.dimple.model.repeated;

import com.analog.lyric.dimple.model.Domain;
import com.analog.lyric.dimple.model.RealJoint;
import com.analog.lyric.dimple.model.RealJointDomain;
import com.analog.lyric.dimple.model.VariableBase;

public class RealJointStream extends VariableStreamBase 
{
	public RealJointStream(int numVars)  
	{
		super(new RealJointDomain(numVars));
		// TODO Auto-generated constructor stub
	}

	public RealJointStream(RealJointDomain domain)  
	{
		super(domain);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected VariableBase instantiateVariable(Domain domain)  
	{
		// TODO Auto-generated method stub
		return new RealJoint((RealJointDomain)domain);
	}

}