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

package com.analog.lyric.dimple.environment;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import net.jcip.annotations.ThreadSafe;

import com.analog.lyric.dimple.events.IDimpleEventListener;
import com.analog.lyric.dimple.events.IDimpleEventSource;
import com.analog.lyric.dimple.events.IModelEventSource;
import com.analog.lyric.dimple.model.core.FactorGraph;
import com.analog.lyric.dimple.options.DimpleOptionHolder;
import com.analog.lyric.util.misc.Nullable;

/**
 * 
 * @since 0.07
 * @author Christopher Barber
 */
@ThreadSafe
public class DimpleEnvironment extends DimpleOptionHolder
{
	/*--------------
	 * Static state
	 */
	
	private static final AtomicReference<DimpleEnvironment> _globalInstance =
		new AtomicReference<>(new DimpleEnvironment());
	
	private static final ThreadLocal<DimpleEnvironment> _threadInstance = new ThreadLocal<DimpleEnvironment>() {
		@Override
		protected DimpleEnvironment initialValue()
		{
			return global();
		}
	};
	
	/*----------------
	 * Instance state
	 */
	
	// NOTE: although the environment is typically accessed through a thread-local
	// variable, there will typically be only one environment shared across all threads
	// so care should be taken to ensure that the code is thread safe.
	
	/**
	 * Logging instance for this environment.
	 */
	private final AtomicReference<Logger> _logger = new AtomicReference<>();
	
	/*--------------
	 * Construction
	 */
	
	/**
	 * Constructs a new instance of a Dimple environment.
	 * <p>
	 * @since 0.07
	 */
	public DimpleEnvironment()
	{
		_logger.set(getDefaultLogger());
	}
	
	/*----------------
	 * Static methods
	 */
	
	/**
	 * The default global instance of the dimple environment.
	 * <p>
	 * This is used as the initial value of the {@link #local} environment
	 * for newly created threads. Most users should use {@link #local} instead of this.
	 * <p>
	 * @see #setGlobal(DimpleEnvironment)
	 * @since 0.07
	 */
	public static DimpleEnvironment global()
	{
		return _globalInstance.get();
	}
	
	/**
	 * The thread-specific instance of the dimple environment.
	 * <p>
	 * This is initialized to be the same as the {@link #global} instance the
	 * first time this is invoked, but can be overridden. Users should remember
	 * that modifications to this environment will affect other threads unless
	 * the environment has been set to a value unique to the current thread.
	 * <p>
	 * @see #setLocal(DimpleEnvironment)
	 * @since 0.07
	 */
	public static DimpleEnvironment local()
	{
		return _threadInstance.get();
	}
	
	/**
	 * Sets the global dimple environment to a new instance.
	 * @param env is a non-null environment instance.
	 * @since 0.07
	 */
	public static void setGlobal(DimpleEnvironment env)
	{
		_globalInstance.set(env);
	}
	
	/**
	 * Sets the thread-specific dimple environment to a new instance.
	 * @param env is a non-null environment instance.
	 * @since 0.07
	 */
	public static void setLocal(DimpleEnvironment env)
	{
		_threadInstance.set(env);
	}
	
	/*----------------------------
	 * IDimpleEventSource methods
	 */

	@Override
	public @Nullable FactorGraph getContainingGraph()
	{
		return null;
	}

	@Override
	public @Nullable IDimpleEventListener getEventListener()
	{
		// TODO: move event listener here from FactorGraph
		return null;
	}

	@Override
	public @Nullable IDimpleEventSource getEventParent()
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation returns the string {@code "DimpleEnvironment"}.
	 */
	@Override
	public String getEventSourceName()
	{
		return "DimpleEnvironment";
	}

	@Override
	public @Nullable IModelEventSource getModelEventSource()
	{
		return null;
	}

	@Override
	public void notifyListenerChanged()
	{
	}
	
	/*-----------------
	 * Logging methods
	 */
	
	/**
	 * Returns default logger instance.
	 * <p>
	 * This is obtained by invoking
	 * {@linkplain Logger#getLogger(String) Logger.getLogger("com.analog.lyric.dimple")}.
	 * <p>
	 * @since 0.07
	 */
	public static Logger getDefaultLogger()
	{
		return Logger.getLogger("com.analog.lyric.dimple");
	}
	
	/**
	 * Logs a warning message using the thread-specific Dimple logger.
	 * <p>
	 * This is simply shorthand for:
	 * <blockquote>
     *   local().logger().warning(String.format(format, args));
	 * </blockquote>
	 * <p>
	 * @param format a non-null String for use with {@link String#format}.
	 * @param args zero or more format arguments.
	 * @since 0.07
	 */
	public static void logWarning(String format, Object ... args)
	{
		logWarning(String.format(format, args));
	}
	
	/**
	 * Logs a warning message using the thread-specific Dimple logger.
	 * <p>
	 * This is simply shorthand for:
	 * <blockquote>
     *   local().logger().warning(message);
	 * </blockquote>
	 * <p>
	 * @since 0.07
	 */
	public static void logWarning(String message)
	{
		local().logger().warning(message);
	}
	
	/**
	 * Logs an error message using the thread-specific Dimple logger.
	 * <p>
	 * This is simply shorthand for:
	 * <blockquote>
     *   local().logger().severe(String.format(format, args));
	 * </blockquote>
	 * <p>
	 * @param format a non-null String for use with {@link String#format}.
	 * @param args zero or more format arguments.
	 * @since 0.07
	 */
	public static void logError(String format, Object ... args)
	{
		logError(String.format(format, args));
	}
	
	/**
	 * Logs a warning message using the thread-specific Dimple logger.
	 * <p>
	 * This is simply shorthand for:
	 * <blockquote>
     *   local().logger().severe(message);
	 * </blockquote>
	 * <p>
	 * @since 0.07
	 */
	public static void logError(String message)
	{
		local().logger().severe(message);
	}
	
	/**
	 * The environment-specific logger instance.
	 * <p>
	 * By default this is set to {@link #getDefaultLogger()} when the environment is initialized.
	 * <p>
	 * Logging is typically configured using Java system properties, which is described
	 * in the documentation for {@link java.util.logging.LogManager}. You can also configure
	 * the logger object directly.
	 * <p>
	 * @see #setLogger
	 * @see #logWarning
	 * @see #logError
	 * @see java.util.logging.Logger
	 * @see java.util.logging.LogManager
	 * @since 0.07
	 */
	public Logger logger()
	{
		return _logger.get();
	}
	
	/**
	 * Sets the environment-specific {@link #logger} instance.
	 * <p>
	 * This can be used to replace the logger with special implementation
	 * for test purposes when testing logging behavior.
	 * <p>
	 * @param logger
	 * @return the previous logger instance.
	 * @since 0.07
	 */
	public Logger setLogger(Logger logger)
	{
		return _logger.getAndSet(logger);
	}
}
