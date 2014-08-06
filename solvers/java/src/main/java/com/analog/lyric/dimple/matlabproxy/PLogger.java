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

package com.analog.lyric.dimple.matlabproxy;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.analog.lyric.dimple.environment.DimpleEnvironment;

/**
 * Dimple logger interface for MATLAB.
 * @since 0.07
 * @author Christopher Barber
 */
public enum PLogger
{
	INSTANCE;
	
	public void logError(String message)
	{
		DimpleEnvironment.logError(message);
	}

	public void logWarning(String message)
	{
		DimpleEnvironment.logWarning(message);
	}

	/**
	 * This configures the Dimple logger to log to System.err at the specified level.
	 * <p>
	 * More powerful logging patterns can be accomplished by configuring system properties.
	 * This provides an easy way to simply log to the console. This will override any previous
	 * configuration.
	 * <p>
	 * @param levelName is one of ALL, ERROR/SEVERE, WARNING, INFO, OFF
	 * @since 0.07
	 */
	public void logToConsole(String levelName)
	{
		Level level = stringToLevel(levelName);

		Logger logger = DimpleEnvironment.local().logger();
		logger.setUseParentHandlers(false);
		logger.setLevel(level);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(level);
		logger.addHandler(handler);
	}
	
	static Level stringToLevel(String levelName)
	{
		switch (levelName.toUpperCase())
		{
		case "ALL":
			return Level.ALL;
		case "INFO":
			return Level.INFO;
		case "WARNING":
			return Level.WARNING;
		case "ERROR":
		case "SEVERE":
			return Level.SEVERE;
		case "OFF":
			return Level.OFF;
		default:
			throw new Error(String.format("Log level '%s' not one of ALL, INFO, WARNING, ERROR, OFF", levelName));
		}
	}
	
	/**
	 * Resets the java.util.logging configuration and reloads default Dimple logger.
	 * <p>
	 * Note that this calls {@link LogManager#reset} so it may affect the configuration
	 * of non-Dimple loggers.
	 * <p>
	 * @since 0.07
	 */
	public void resetConfiguration()
	{
		LogManager.getLogManager().reset();
		DimpleEnvironment.local().setLogger(DimpleEnvironment.getDefaultLogger());
	}
}
