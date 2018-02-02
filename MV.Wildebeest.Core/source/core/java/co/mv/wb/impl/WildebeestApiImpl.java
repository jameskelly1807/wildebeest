// Wildebeest Migration Framework
// Copyright © 2013 - 2018, Matheson Ventures Pte Ltd
//
// This file is part of Wildebeest
//
// Wildebeest is free software: you can redistribute it and/or modify it under
// the terms of the GNU General Public License v2 as published by the Free
// Software Foundation.
//
// Wildebeest is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along with
// Wildebeest.  If not, see http://www.gnu.org/licenses/gpl-2.0.html

package co.mv.wb.impl;

import co.mv.wb.AssertionFailedException;
import co.mv.wb.AssertionResponse;
import co.mv.wb.AssertionResult;
import co.mv.wb.FileLoadException;
import co.mv.wb.IndeterminateStateException;
import co.mv.wb.Instance;
import co.mv.wb.InvalidStateSpecifiedException;
import co.mv.wb.JumpStateFailedException;
import co.mv.wb.LoaderFault;
import co.mv.wb.Migration;
import co.mv.wb.MigrationFailedException;
import co.mv.wb.MigrationPlugin;
import co.mv.wb.OutputFormatter;
import co.mv.wb.PluginBuildException;
import co.mv.wb.Resource;
import co.mv.wb.ResourcePlugin;
import co.mv.wb.ResourceType;
import co.mv.wb.State;
import co.mv.wb.TargetNotSpecifiedException;
import co.mv.wb.UnknownStateSpecifiedException;
import co.mv.wb.Wildebeest;
import co.mv.wb.WildebeestApi;
import co.mv.wb.framework.ArgumentNullException;
import co.mv.wb.plugin.base.ImmutableAssertionResult;
import co.mv.wb.plugin.base.dom.DomInstanceLoader;
import co.mv.wb.plugin.base.dom.DomPlugins;
import co.mv.wb.plugin.base.dom.DomResourceLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides a generic interface that can be adapted to different environments.  For example the WildebeestCommand
 * command-line interface delegates to WildebeestApiImpl to drive commands.
 * 
 * @author                                      Brendon Matheson
 * @since                                       1.0
 */
public class WildebeestApiImpl implements WildebeestApi
{
	/**
	 * Creates a new WildebeestApiImpl using the supplied {@link PrintStream} for user output and the supplied
	 * ResourceHelper.
	 * 
	 * @param       output                      the PrintStream that should be used for output to the user.
	 * @since                                   1.0
	 */
	public WildebeestApiImpl(
		PrintStream output)
	{
		this.setOutput(output);
	}

	// <editor-fold desc="Output" defaultstate="collapsed">

	private PrintStream _output = null;
	private boolean _output_set = false;

	private PrintStream getOutput() {
		if(!_output_set) {
			throw new IllegalStateException("output not set.");
		}
		if(_output == null) {
			throw new IllegalStateException("output should not be null");
		}
		return _output;
	}

	private void setOutput(
		PrintStream value) {
		if(value == null) {
			throw new IllegalArgumentException("output cannot be null");
		}
		boolean changing = !_output_set || _output != value;
		if(changing) {
			_output_set = true;
			_output = value;
		}
	}

	private void clearOutput() {
		if(_output_set) {
			_output_set = true;
			_output = null;
		}
	}

	private boolean hasOutput() {
		return _output_set;
	}

	// </editor-fold>

	// <editor-fold desc="ResourcePlugins" defaultstate="collapsed">

	private Map<ResourceType, ResourcePlugin> _resourcePlugins = null;
	private boolean _resourcePlugins_set = false;

	private Map<ResourceType, ResourcePlugin> getResourcePlugins() {
		if(!_resourcePlugins_set) {
			throw new IllegalStateException("resourcePlugins not set.");
		}
		if(_resourcePlugins == null) {
			throw new IllegalStateException("resourcePlugins should not be null");
		}
		return _resourcePlugins;
	}

	public void setResourcePlugins(Map<ResourceType, ResourcePlugin> value) {
		if(value == null) {
			throw new IllegalArgumentException("resourcePlugins cannot be null");
		}
		boolean changing = !_resourcePlugins_set || _resourcePlugins != value;
		if(changing) {
			_resourcePlugins_set = true;
			_resourcePlugins = value;
		}
	}

	private void clearResourcePlugins() {
		if(_resourcePlugins_set) {
			_resourcePlugins_set = true;
			_resourcePlugins = null;
		}
	}

	private boolean hasResourcePlugins() {
		return _resourcePlugins_set;
	}

	// </editor-fold>

	// <editor-fold desc="MigrationPlugins" defaultstate="collapsed">

	private Map<Class, MigrationPlugin> _migrationPlugins = null;
	private boolean _migrationPlugins_set = false;

	private Map<Class, MigrationPlugin> getMigrationPlugins() {
		if(!_migrationPlugins_set) {
			throw new IllegalStateException("migrationPlugins not set.");
		}
		if(_migrationPlugins == null) {
			throw new IllegalStateException("migrationPlugins should not be null");
		}
		return _migrationPlugins;
	}

	public void setMigrationPlugins(Map<Class, MigrationPlugin> value) {
		if(value == null) {
			throw new IllegalArgumentException("migrationPlugins cannot be null");
		}
		boolean changing = !_migrationPlugins_set || _migrationPlugins != value;
		if(changing) {
			_migrationPlugins_set = true;
			_migrationPlugins = value;
		}
	}

	private void clearMigrationPlugins() {
		if(_migrationPlugins_set) {
			_migrationPlugins_set = true;
			_migrationPlugins = null;
		}
	}

	private boolean hasMigrationPlugins() {
		return _migrationPlugins_set;
	}

	// </editor-fold>

	public Resource loadResource(
		File resourceFile) throws
			FileLoadException,
			LoaderFault,
			PluginBuildException
	{
		if (resourceFile == null) { throw new IllegalArgumentException("resourceFile cannot be null"); }

		// Get the absolute file for this resource - this ensures that getParentFile works correctly
		resourceFile = resourceFile.getAbsoluteFile();

		// Load Resource
		String resourceXml;
		try
		{
			resourceXml = readAllText(resourceFile);
		}
		catch (IOException ex)
		{
			throw new FileLoadException(resourceFile);
		}

		Resource resource = null;

		if (resourceXml != null)
		{
			DomResourceLoader resourceLoader = DomPlugins.resourceLoader(
				ResourceTypeServiceBuilder
					.create()
					.withFactoryResourceTypes()
					.build(),
				resourceXml);

			resource = resourceLoader.load(resourceFile.getParentFile());
		}

		return resource;
	}

	public Instance loadInstance(
		File instanceFile) throws
			FileLoadException,
			LoaderFault,
			PluginBuildException
	{
		if (instanceFile == null) { throw new IllegalArgumentException("instanceFile"); }

		// Load Instance
		String instanceXml;
		try
		{
			instanceXml = readAllText(instanceFile);
		}
		catch (IOException ex)
		{
			throw new FileLoadException(instanceFile);
		}

		Instance instance = null;

		if (instanceXml != null)
		{
			DomInstanceLoader instanceLoader = new DomInstanceLoader(
				DomPlugins.instanceBuilders(),
				instanceXml);
			instance = instanceLoader.load();
		}

		return instance;
	}

	public List<AssertionResult> assertState(
		Resource resource,
		Instance instance) throws
			IndeterminateStateException
	{
		if (resource == null) throw new ArgumentNullException("resource");
		if (instance == null) throw new ArgumentNullException("instance");

		ResourcePlugin resourcePlugin = WildebeestApiImpl.getResourcePlugin(
			this.getResourcePlugins(),
			resource.getType());

		State state = resourcePlugin.currentState(
			resource,
			instance);

		List<AssertionResult> result = new ArrayList<>();

		state.getAssertions().forEach(
			assertion ->
			{
				this.getOutput().println(OutputFormatter.assertionStart(assertion));

				AssertionResponse response = assertion.perform(instance);

				this.getOutput().println(OutputFormatter.assertionComplete(
					assertion,
					response));

				result.add(new ImmutableAssertionResult(
					assertion.getAssertionId(),
					response.getResult(),
					response.getMessage()));
			});

		return result;
	}

	public void state(
		Resource resource,
		Instance instance) throws
			IndeterminateStateException
	{
		if (resource == null) { throw new IllegalArgumentException("resource cannot be null"); }
		if (instance == null) { throw new IllegalArgumentException("instance cannot be null"); }

		ResourcePlugin resourcePlugin = WildebeestApiImpl.getResourcePlugin(
			this.getResourcePlugins(),
			resource.getType());

		State state = resourcePlugin.currentState(
			resource,
			instance);

		if (state == null)
		{
			this.getOutput().println("Current state: non-existent");
		}
		else
		{
			if (state.getLabel().isPresent())
			{
				this.getOutput().println(String.format("Current state: %s", state.getLabel()));
			}
			else
			{
				this.getOutput().println(String.format("Current state: %s", state.getStateId().toString()));
			}

			this.assertState(
				resource,
				instance);
		}
	}

	public void migrate(
		Resource resource,
		Instance instance,
		Optional<String> targetState) throws
			AssertionFailedException,
			TargetNotSpecifiedException,
			IndeterminateStateException,
			InvalidStateSpecifiedException,
			MigrationFailedException,
			UnknownStateSpecifiedException
	{
		if (resource == null) throw new ArgumentNullException("resource");
		if (instance == null) throw new ArgumentNullException("instance");
		if (targetState == null) throw new ArgumentNullException("targetState");

		ResourcePlugin resourcePlugin = WildebeestApiImpl.getResourcePlugin(
			this.getResourcePlugins(),
			resource.getType());

		// Resolve the target state
		Optional<String> ts = targetState.isPresent()
			? targetState
			: resource.getDefaultTarget();

		// Perform migration
		if (!ts.isPresent())
		{
			throw new TargetNotSpecifiedException();
		}

		UUID targetStateId = WildebeestApiImpl.getTargetStateId(
			resource,
			ts.get());

		State currentState = resourcePlugin.currentState(
			resource,
			instance);

		UUID currentStateId = currentState == null ? null : currentState.getStateId();

		List<List<Migration>> paths = new ArrayList<>();
		List<Migration> thisPath = new ArrayList<>();

		WildebeestApiImpl.findPaths(resource, paths, thisPath, currentStateId, targetStateId);

		if (paths.size() != 1)
		{
			throw new RuntimeException("multiple possible paths found");
		}

		List<Migration> path = paths.get(0);

		for (Migration migration : path)
		{
			MigrationPlugin migrationPlugin = this.getMigrationPlugins().get(migration.getClass());

			if (migrationPlugin == null)
			{
				throw new RuntimeException(String.format(
					"No MigrationPlugin found for migration of type %s",
					migration.getClass().getName()));
			}

			Optional<State> fromState = migration.getFromStateId().map(stateId -> Wildebeest.stateForId(
				resource,
				stateId));
			Optional<State> toState = migration.getToStateId().map(stateId -> Wildebeest.stateForId(
				resource,
				stateId));

			// Migrate to the next state
			this.getOutput().println(OutputFormatter.migrationStart(
				resource,
				migration,
				fromState,
				toState));

			migrationPlugin.perform(
				this.getOutput(),
				migration,
				instance);

			this.getOutput().println(OutputFormatter.migrationComplete(
				resource,
				migration));

			// Update the state
			resourcePlugin.setStateId(
				this.getOutput(),
				resource,
				instance,
				migration.getToStateId().get());

			// Assert the new state
			List<AssertionResult> assertionResults = this.assertState(
				resource,
				instance);

			WildebeestApiImpl.throwIfFailed(migration.getToStateId().get(), assertionResults);
		}
	}

	public void jumpstate(
		Resource resource,
		Instance instance,
		String targetState) throws
			AssertionFailedException,
			IndeterminateStateException,
			InvalidStateSpecifiedException,
			JumpStateFailedException,
			UnknownStateSpecifiedException
	{
		if (resource == null) { throw new IllegalArgumentException("resource cannot be null"); }
		if (instance == null) { throw new IllegalArgumentException("instance cannot be null"); }
		if (targetState != null && "".equals(targetState.trim()))
		{
			throw new IllegalArgumentException("targetState cannot be empty");
		}

		ResourcePlugin resourcePlugin = WildebeestApiImpl.getResourcePlugin(
			this.getResourcePlugins(),
			resource.getType());

		UUID targetStateId = getTargetStateId(
			resource,
			targetState);

		State state = Wildebeest.stateForId(
			resource,
			targetStateId);

		if (targetState == null)
		{
			throw new JumpStateFailedException("This resource does not have a state with ID " +
				targetStateId.toString());
		}

		// Assert the new state
		List<AssertionResult> assertionResults = this.assertState(
			resource,
			instance);

		WildebeestApiImpl.throwIfFailed(state.getStateId(), assertionResults);

		resourcePlugin.setStateId(
			this.getOutput(),
			resource,
			instance,
			targetStateId);
	}

	private static UUID stateIdForLabel(
		Resource resource,
		String label)
	{
		if (resource == null) { throw new IllegalArgumentException("resource cannot be null"); }
		if (label == null) { throw new IllegalArgumentException("label cannot be null"); }
		if ("".equals(label)) { throw new IllegalArgumentException("label cannot be empty"); }

		State result = null;

		for (State check : resource.getStates())
		{
			if (check.getLabel().map(label::equals).orElse(false))
			{
				result = check;
			}
		}

		return result == null ? null : result.getStateId();
	}

	private static UUID getTargetStateId(
		Resource resource,
		String targetState) throws
            InvalidStateSpecifiedException,
            UnknownStateSpecifiedException
	{
		if (resource == null) { throw new IllegalArgumentException("resource cannot be null"); }

		final String stateSpecificationRegex = "[a-zA-Z0-9][a-zA-Z0-9\\-\\_ ]+[a-zA-Z0-9]";
		if (targetState != null && !targetState.matches(stateSpecificationRegex))
		{
            throw new InvalidStateSpecifiedException(targetState);
		}

		UUID targetStateId = null;
		if (targetState != null)
		{
			try
			{
				targetStateId = UUID.fromString(targetState);
			}
			catch(IllegalArgumentException e)
			{
				targetStateId = WildebeestApiImpl.stateIdForLabel(
					resource,
					targetState);
			}
            
            // If we still could not find the specified state, then throw
            if (targetStateId == null)
            {
                throw new UnknownStateSpecifiedException(targetState);
            }
		}
		
		return targetStateId;
	}
	
	private static String readAllText(File file) throws
		IOException
	{
		if (file == null) { throw new IllegalArgumentException("file cannt be null"); }
		if (!file.isFile())
		{
			throw new IllegalArgumentException(String.format(
				"%s is not a plain file",
				file.getAbsolutePath()));
		}
	
		String result;

		BufferedReader br = null;
		try
		{
			StringBuilder sb = new StringBuilder();
			br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while (line != null)
			{
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			result = sb.toString();
		}
		finally
		{
			if (br != null)
			{
				br.close();
			}
		}
		
		return result;
	}

	/**
	 * Looks up the ResourcePlugin for the supplied ResourceType.
	 *
	 * @param       resourcePlugins             the set of available ResourcePlugins.
	 * @param       resourceType                the ResourceType for which a plugin should be retrieved.
	 * @return                                  the ResourcePlugin that corresponds to the supplied ResourceType.
	 * @since                                   4.0
	 */
	private static ResourcePlugin getResourcePlugin(
		Map<ResourceType, ResourcePlugin> resourcePlugins,
		ResourceType resourceType)
	{
		if (resourcePlugins == null) { throw new IllegalArgumentException("resourcePlugins cannot be null"); }
		if (resourceType == null) { throw new IllegalArgumentException("resourceType cannot be null"); }

		ResourcePlugin resourcePlugin = resourcePlugins.get(resourceType);

		if (resourcePlugin == null)
		{
			throw new RuntimeException(String.format(
				"resource plugin for resource type %s not found",
				resourceType.getUri()));
		}

		return resourcePlugin;
	}

	private static void throwIfFailed(
		UUID stateId,
		List<AssertionResult> assertionResults) throws AssertionFailedException
	{
		if (stateId == null) { throw new IllegalArgumentException("stateId cannot be null"); }
		if (assertionResults == null) { throw new IllegalArgumentException("assertionResults cannot be null"); }

		// If any assertions failed, throw
		for(AssertionResult assertionResult : assertionResults)
		{
			if (!assertionResult.getResult())
			{
				throw new AssertionFailedException(stateId, assertionResults);
			}
		}
	}

	private static void findPaths(
		Resource resource,
		List<List<Migration>> paths,
		List<Migration> thisPath,
		UUID fromStateId,
		UUID targetStateId)
	{
		if (resource == null) { throw new IllegalArgumentException("resource"); }
		if (paths == null) { throw new IllegalArgumentException("paths"); }
		if (thisPath == null) { throw new IllegalArgumentException("thisPath"); }

		// Have we reached the target state?
		if ((fromStateId == null && targetStateId == null) ||
			(fromStateId != null && fromStateId.equals(targetStateId)))
		{
			paths.add(thisPath);
		}

		// If we have not reached the target state, keep traversing the graph
		else
		{
			resource.getMigrations()
				.stream()
				.filter(m ->
					(!m.getFromStateId().isPresent() && fromStateId == null) ||
						(m.getFromStateId().isPresent() && m.getFromStateId().equals(fromStateId)))
				.forEach(
					migration ->
					{
						State toState = Wildebeest.stateForId(
							resource,
							migration.getToStateId().get());
						List<Migration> thisPathCopy = new ArrayList<>(thisPath);
						thisPathCopy.add(migration);
						findPaths(resource, paths, thisPathCopy, toState.getStateId(), targetStateId);
					});
		}
	}
}