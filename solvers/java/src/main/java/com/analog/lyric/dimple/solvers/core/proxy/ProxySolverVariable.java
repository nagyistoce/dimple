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

package com.analog.lyric.dimple.solvers.core.proxy;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import com.analog.lyric.dimple.exceptions.DimpleException;
import com.analog.lyric.dimple.model.variables.Variable;
import com.analog.lyric.dimple.solvers.core.SVariableBase;
import com.analog.lyric.dimple.solvers.interfaces.ISolverFactorGraph;
import com.analog.lyric.dimple.solvers.interfaces.ISolverNode;
import com.analog.lyric.dimple.solvers.interfaces.ISolverVariable;

/**
 * @since 0.05
 */
public abstract class ProxySolverVariable<MVariable extends Variable, Delegate extends ISolverVariable>
	extends SVariableBase<MVariable>
	implements IProxySolverVariable<Delegate>
{
	/*--------------
	 * Construction
	 */
	
	/**
	 * @param modelVariable
	 */
	protected ProxySolverVariable(MVariable modelVariable, ISolverFactorGraph parent)
	{
		super(modelVariable, parent);
	}

	/*---------------------
	 * ISolverNode methods
	 */
	
	@Override
	public double getBetheEntropy()
	{
		return requireDelegate("getBetheEntropy").getBetheEntropy();
	}

	@Override
	public double getInternalEnergy()
	{
		return requireDelegate("getInternalEnergy").getInternalEnergy();
	}

	@Override
	public double getScore()
	{
		return requireDelegate("getScore").getScore();
	}

	@Override
	public void initialize()
	{
		clearFlags();
		requireDelegate("initialize").initialize();
	}

	@Override
	public void update()
	{
		requireDelegate("update").update();
	}

	@Override
	public void updateEdge(int outPortNum)
	{
		throw unsupported("updateEdge");
	}
	
	@Override
	protected void doUpdateEdge(int edge)
	{
		throw unsupported("doUpdateEdge");
	}

	/*-------------------------
	 * ISolverVariable methods
	 */
	
	@Override
	public void createNonEdgeSpecificState()
	{
		ISolverVariable delegate = getDelegate();
		if (delegate != null)
		{
			delegate.createNonEdgeSpecificState();
		}
	}

	@Override
	public @Nullable Object getBelief()
	{
		final ISolverVariable delegate = getDelegate();
		return delegate != null ? delegate.getBelief() : null;
	}

	@Override
	public boolean guessWasSet()
	{
		return requireDelegate("guessWasSet").guessWasSet();
	}
	
	@Override
	public Object getGuess()
	{
		return requireDelegate("getGuess").getGuess();
	}

	@Override
	public void setGuess(@Nullable Object guess)
	{
		requireDelegate("setGuess").setGuess(guess);
	}

	@Override
	public Object getValue()
	{
		return requireDelegate("getValue").getValue();
	}

	@Override
	public void moveNonEdgeSpecificState(ISolverNode other)
	{
		if (other instanceof IProxySolverNode)
		{
			other = Objects.requireNonNull(((IProxySolverNode<?>)other).getDelegate());
		}
		requireDelegate("moveNonEdgeSpecificState").moveNonEdgeSpecificState(other);
	}

	/*-------------------------
	 * ISolverVariable methods
	 */
	
	@Override
	public void setInputOrFixedValue(@Nullable Object input, @Nullable Object fixedValue)
	{
		ISolverVariable delegate = getDelegate();
		if (delegate != null)
		{
			delegate.setInputOrFixedValue(input, fixedValue);
		}
	}
	
	/*---------------
	 * Local methods
	 */
	
	/**
	 * Returns non-null delegate or throws an error indicating method requires that
	 * delegate solver has been set.
	 * @since 0.06
	 */
	protected Delegate requireDelegate(String method)
	{
		Delegate delegate = getDelegate();
		if (delegate == null)
		{
			throw new DimpleException("Delegate solver required by '%s' has not been set.", method);
		}
		return delegate;
	}
	
	protected RuntimeException unsupported(String method)
	{
		return DimpleException.unsupportedMethod(getClass(), method,
			"Not supported for proxy solver because graph topology may be different.");
	}
}
