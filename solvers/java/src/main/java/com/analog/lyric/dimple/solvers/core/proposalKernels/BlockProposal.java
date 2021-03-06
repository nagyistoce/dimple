/*******************************************************************************
*   Copyright 2014 Analog Devices, Inc.
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

package com.analog.lyric.dimple.solvers.core.proposalKernels;

import com.analog.lyric.dimple.model.values.RealValue;
import com.analog.lyric.dimple.model.values.Value;

/**
 * @since 0.06
 * @author jeffb
 */
public class BlockProposal
{
	public final Value[] value;
	public final double forwardEnergy;	// -log p(x_previous -> x_proposed)
	public final double reverseEnergy;	// -log p(x_proposed -> x_previous)

	/**
	 * Construct with no Hastings term
	 * @since 0.06
	 */
	public BlockProposal(Value[] value)
	{
		this.value = value;
		this.forwardEnergy = 0;
		this.reverseEnergy = 0;
	}
	
	/**
	 * General constructor
	 * @since 0.06
	 */
	public BlockProposal(Value[] value, double forwardEnergy, double reverseEnergy)
	{
		this.value = value;
		this.forwardEnergy = forwardEnergy;
		this.reverseEnergy = reverseEnergy;
	}
	
	/**
	 * Construct with no Hastings term; for all real valued proposal
	 * @since 0.06
	 */
	public BlockProposal(double[] value)
	{
		int length = value.length;
		this.value = new Value[length];
		for (int i = 0; i < length; i++)
			this.value[i] = RealValue.create(value[i]);
		this.forwardEnergy = 0;
		this.reverseEnergy = 0;
	}
	
	/**
	 * General constructor; for all real valued proposal
	 * @since 0.06
	 */
	public BlockProposal(double[] value, double forwardEnergy, double reverseEnergy)
	{
		int length = value.length;
		this.value = new Value[length];
		for (int i = 0; i < length; i++)
			this.value[i] = RealValue.create(value[i]);
		this.forwardEnergy = forwardEnergy;
		this.reverseEnergy = reverseEnergy;
	}

}
