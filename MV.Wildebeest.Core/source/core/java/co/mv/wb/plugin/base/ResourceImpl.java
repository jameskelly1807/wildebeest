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

package co.mv.wb.plugin.base;

import co.mv.wb.AssertionFailedException;
import co.mv.wb.AssertionResponse;
import co.mv.wb.AssertionResult;
import co.mv.wb.IndeterminateStateException;
import co.mv.wb.Instance;
import co.mv.wb.JumpStateFailedException;
import co.mv.wb.Logger;
import co.mv.wb.Migration;
import co.mv.wb.MigrationFailedException;
import co.mv.wb.MigrationNotPossibleException;
import co.mv.wb.Resource;
import co.mv.wb.ResourcePlugin;
import co.mv.wb.ResourceType;
import co.mv.wb.State;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Provides a base implementation of {@link Resource}
 * 
 * @author                                      Brendon Matheson
 * @since                                       1.0
 */
public final class ResourceImpl implements Resource
{
	/**
	 * Creates a new BaseResource instance.
	 * 
	 * @param       resourceId                  the ID of the new Resource
	 * @param       type                        the type of the new Resource
	 * @param       name                        the name of the new Resource
	 * @param       plugin                      the plugin for handling the type of the Resource
	 * @since                                   1.0
	 */
	public ResourceImpl(
		UUID resourceId,
		ResourceType type,
		String name,
		ResourcePlugin plugin)
	{
		this.setResourceId(resourceId);
		this.setType(type);
		this.setName(name);
		this.setStates(new ArrayList<>());
		this.setMigrations(new ArrayList<>());
		this.setPlugin(plugin);
	}

	//
	// Properties
	//
	
	// <editor-fold desc="ResourceId" defaultstate="collapsed">

	private UUID _resourceId = null;
	private boolean _resourceId_set = false;

	@Override public UUID getResourceId() {
		if(!_resourceId_set) {
			throw new IllegalStateException("resourceId not set.  Use the HasResourceId() method to check its state before accessing it.");
		}
		return _resourceId;
	}

	private void setResourceId(
		UUID value) {
		if(value == null) {
			throw new IllegalArgumentException("resourceId cannot be null");
		}
		boolean changing = !_resourceId_set || _resourceId != value;
		if(changing) {
			_resourceId_set = true;
			_resourceId = value;
		}
	}

	private void clearResourceId() {
		if(_resourceId_set) {
			_resourceId_set = true;
			_resourceId = null;
		}
	}

	private boolean hasResourceId() {
		return _resourceId_set;
	}

	// </editor-fold>

	// <editor-fold desc="Type" defaultstate="collapsed">

	private ResourceType _type = null;
	private boolean _type_set = false;

	public ResourceType getType() {
		if(!_type_set) {
			throw new IllegalStateException("type not set.");
		}
		if(_type == null) {
			throw new IllegalStateException("type should not be null");
		}
		return _type;
	}

	private void setType(
			ResourceType value) {
		if(value == null) {
			throw new IllegalArgumentException("type cannot be null");
		}
		boolean changing = !_type_set || _type != value;
		if(changing) {
			_type_set = true;
			_type = value;
		}
	}

	private void clearType() {
		if(_type_set) {
			_type_set = true;
			_type = null;
		}
	}

	private boolean hasType() {
		return _type_set;
	}

	// </editor-fold>

	// <editor-fold desc="Name" defaultstate="collapsed">

	private String _name = null;
	private boolean _name_set = false;

	@Override public String getName() {
		if(!_name_set) {
			throw new IllegalStateException("name not set.  Use the HasName() method to check its state before accessing it.");
		}
		return _name;
	}

	private void setName(
		String value) {
		if(value == null) {
			throw new IllegalArgumentException("name cannot be null");
		}
		boolean changing = !_name_set || !_name.equals(value);
		if(changing) {
			_name_set = true;
			_name = value;
		}
	}

	private void clearName() {
		if(_name_set) {
			_name_set = true;
			_name = null;
		}
	}

	private boolean hasName() {
		return _name_set;
	}

	// </editor-fold>

	// <editor-fold desc="States" defaultstate="collapsed">

	private List<State> _states = null;
	private boolean _states_set = false;

	@Override public List<State> getStates() {
		if(!_states_set) {
			throw new IllegalStateException("states not set.  Use the HasStates() method to check its state before accessing it.");
		}
		return _states;
	}

	private void setStates(List<State> value) {
		if(value == null) {
			throw new IllegalArgumentException("states cannot be null");
		}
		boolean changing = !_states_set || _states != value;
		if(changing) {
			_states_set = true;
			_states = value;
		}
	}

	private void clearStates() {
		if(_states_set) {
			_states_set = true;
			_states = null;
		}
	}

	private boolean hasStates() {
		return _states_set;
	}

	// </editor-fold>
	
	// <editor-fold desc="Migrations" defaultstate="collapsed">

	private List<Migration> _migrations = null;
	private boolean _migrations_set = false;

	@Override public List<Migration> getMigrations() {
		if(!_migrations_set) {
			throw new IllegalStateException("migrations not set.  Use the HasMigrations() method to check its state before accessing it.");
		}
		return _migrations;
	}

	private void setMigrations(List<Migration> value) {
		if(value == null) {
			throw new IllegalArgumentException("migrations cannot be null");
		}
		boolean changing = !_migrations_set || _migrations != value;
		if(changing) {
			_migrations_set = true;
			_migrations = value;
		}
	}

	private void clearMigrations() {
		if(_migrations_set) {
			_migrations_set = true;
			_migrations = null;
		}
	}

	public boolean hasMigrations() {
		return _migrations_set;
	}

	// </editor-fold>

	// <editor-fold desc="Plugin" defaultstate="collapsed">

	private ResourcePlugin _plugin = null;
	private boolean _plugin_set = false;

	@Override public ResourcePlugin getPlugin() {
		if(!_plugin_set) {
			throw new IllegalStateException("plugin not set.");
		}
		if(_plugin == null) {
			throw new IllegalStateException("plugin should not be null");
		}
		return _plugin;
	}

	private void setPlugin(
		ResourcePlugin value) {
		if(value == null) {
			throw new IllegalArgumentException("plugin cannot be null");
		}
		boolean changing = !_plugin_set || _plugin != value;
		if(changing) {
			_plugin_set = true;
			_plugin = value;
		}
	}

	private void clearPlugin() {
		if(_plugin_set) {
			_plugin_set = true;
			_plugin = null;
		}
	}

	private boolean hasPlugin() {
		return _plugin_set;
	}

	// </editor-fold>

	@Override public List<AssertionResult> assertState(
		Logger logger,
		Instance instance) throws IndeterminateStateException
	{
		if (logger == null) { throw new IllegalArgumentException("logger cannot be null"); }
		if (instance == null) { throw new IllegalArgumentException("instance cannot be null"); }

		State state = this.getPlugin().currentState(
			this,
			instance);
		
		List<AssertionResult> results = this.assertState(logger, instance, state);
		
		return results;
	}
	
	private List<AssertionResult> assertState(
		Logger logger,
		Instance instance,
		State state)
	{
		if (logger == null) { throw new IllegalArgumentException("logger cannot be null"); }
		if (instance == null) { throw new IllegalArgumentException("instance cannot be null"); }
		if (state == null) { throw new IllegalArgumentException("state cannot be null"); }

		List<AssertionResult> results = new ArrayList<>();
		
		state.getAssertions().forEach(
			assertion ->
			{
				AssertionResponse response = assertion.perform(instance);

				logger.assertionComplete(assertion, response);

				results.add(new ImmutableAssertionResult(
					assertion.getAssertionId(),
					response.getResult(),
					response.getMessage()));
			});
	
		return results;
	}

	@Override public void migrate(
		Logger logger,
		Instance instance,
		UUID targetStateId) throws
			IndeterminateStateException,
			AssertionFailedException,
			MigrationNotPossibleException,
			MigrationFailedException
	{
		if (logger == null) { throw new IllegalArgumentException("logger cannot be null"); }
		if (instance == null) { throw new IllegalArgumentException("instance"); }
		
		State currentState = this.getPlugin().currentState(
			this,
			instance);
		UUID currentStateId = currentState == null ? null : currentState.getStateId();
		List<UUID> workList = new ArrayList<>();
		
		List<List<Migration>> paths = new ArrayList<>();
		List<Migration> thisPath = new ArrayList<>();
		
		findPaths(this, paths, thisPath, currentStateId, targetStateId);
		
		if (paths.size() != 1)
		{
			throw new RuntimeException("multiple possible paths found");
		}
		
		List<Migration> path = paths.get(0);
		
		for (Migration migration : path)
		{
			// Migrate to the next state
			logger.migrationStart(this, migration);
			migration.perform(instance);
			logger.migrationComplete(this, migration);
		
			// Update the state
			this.getPlugin().setStateId(
				logger,
				this,
				instance,
				migration.getToStateId().get());
			
			// Assert the new state
			List<AssertionResult> assertionResults = this.assertState(
				logger,
				instance);

			throwIfFailed(migration.getToStateId().get(), assertionResults);
		}
	}
	
	@Override public void jumpstate(
		Logger logger,
		Instance instance,
		UUID targetStateId) throws
			AssertionFailedException,
			JumpStateFailedException
	{
		if (logger == null) { throw new IllegalArgumentException("logger cannot be null"); }
		if (instance == null) { throw new IllegalArgumentException("instance cannot be null"); }
		if (targetStateId == null) { throw new IllegalArgumentException("targetStateId cannot be null"); }
		
		State targetState = this.stateForId(targetStateId);
		
		if (targetState == null)
		{
			throw new JumpStateFailedException("This resource does not have a state with ID " +
				targetStateId.toString());
		}
		
		// Assert the new state
		List<AssertionResult> assertionResults = this.assertState(
			logger,
			instance,
			targetState);

		throwIfFailed(targetState.getStateId(), assertionResults);
		
		this.getPlugin().setStateId(
			logger,
			this,
			instance,
			targetStateId);
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
		ResourceImpl resource,
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
						State toState = resource.stateForId(migration.getToStateId().get());
						List<Migration> thisPathCopy = new ArrayList<>(thisPath);
						thisPathCopy.add(migration);
						findPaths(resource, paths, thisPathCopy, toState.getStateId(), targetStateId);
					});
		}
	}
	
	@Override public State stateForId(UUID stateId)
	{
		if (stateId == null) { throw new IllegalArgumentException("stateId cannot be null"); }
		
		State result = null;
		
		for(State check : this.getStates())
		{
			if (stateId.equals(check.getStateId()))
			{
				result = check;
				break;
			}
		}
		
		return result;
	}

	@Override public UUID stateIdForLabel(String label)
	{
		if (label == null) { throw new IllegalArgumentException("label cannot be null"); }
		if ("".equals(label)) { throw new IllegalArgumentException("label cannot be empty"); }
		
		State result = null;
		
		for (State check : this.getStates())
		{
			if (check.getLabel().map(x -> label.equals(x)).orElse(false))
			{
				result = check;
			}
		}
		
		return result == null ? null : result.getStateId();
	}
}
