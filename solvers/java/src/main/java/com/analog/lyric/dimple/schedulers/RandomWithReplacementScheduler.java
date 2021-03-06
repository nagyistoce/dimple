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

package com.analog.lyric.dimple.schedulers;

import java.util.Collections;
import java.util.List;

import com.analog.lyric.dimple.model.core.FactorGraph;
import com.analog.lyric.dimple.options.BPOptions;
import com.analog.lyric.dimple.schedulers.schedule.ISchedule;
import com.analog.lyric.dimple.schedulers.schedule.RandomWithReplacementSchedule;

/**
 * @author jeffb
 * 
 *         This creates a dynamic schedule, which updates factors in a randomly
 *         chosen sequence with replacement. Prior to each factor update, the
 *         corresponding edges of the connected variables are updated. The
 *         number of factors updated per iteration is equal to the total number
 *         of factors in the graph. However, since the factors are chosen
 *         randomly with replacement, not all factors are necessarily updated in
 *         a single iteration.
 * 
 *         WARNING: This schedule DOES NOT respect any existing sub-graph
 *         scheduler associations. That is, if any sub-graph already has an
 *         associated scheduler, that scheduler is ignored in creating this
 *         schedule.
 */
public class RandomWithReplacementScheduler extends StatelessBPScheduler
{
	private static final long serialVersionUID = 1L;

	@Override
	public ISchedule createSchedule(FactorGraph g)
	{
		return new RandomWithReplacementSchedule(this, g);
	}
	
	@Override
	public List<SchedulerOptionKey> applicableSchedulerOptions()
	{
		return Collections.singletonList(BPOptions.scheduler);
	}
}
