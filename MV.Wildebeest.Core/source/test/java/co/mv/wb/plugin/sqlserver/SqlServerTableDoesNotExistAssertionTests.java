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

package co.mv.wb.plugin.sqlserver;

import co.mv.wb.AssertionFailedException;
import co.mv.wb.AssertionResponse;
import co.mv.wb.Asserts;
import co.mv.wb.IndeterminateStateException;
import co.mv.wb.Migration;
import co.mv.wb.MigrationFailedException;
import co.mv.wb.MigrationNotPossibleException;
import co.mv.wb.PrintStreamLogger;
import co.mv.wb.Resource;
import co.mv.wb.State;
import co.mv.wb.fake.FakeInstance;
import co.mv.wb.impl.FactoryResourceTypes;
import co.mv.wb.plugin.base.ImmutableState;
import co.mv.wb.plugin.base.ResourceImpl;
import co.mv.wb.plugin.database.DatabaseFixtureHelper;
import co.mv.wb.plugin.database.SqlScriptMigration;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class SqlServerTableDoesNotExistAssertionTests
{
	 @Test public void applyForExistingTableFails() throws
		 IndeterminateStateException,
		 AssertionFailedException,
		 MigrationNotPossibleException,
		 MigrationFailedException,
		 SQLException
	 {
		 
		//
		// Setup
		//
		 
		SqlServerProperties properties = SqlServerProperties.get();

		SqlServerDatabaseResourcePlugin resourcePlugin = new SqlServerDatabaseResourcePlugin();
		Resource resource = new ResourceImpl(
			UUID.randomUUID(),
			FactoryResourceTypes.SqlServerDatabase,
			"Database",
			resourcePlugin);

		// Created
		State created = new ImmutableState(UUID.randomUUID());
		resource.getStates().add(created);
		 
		// Schema Loaded
		State schemaLoaded = new ImmutableState(UUID.randomUUID());
		resource.getStates().add(schemaLoaded);
		 
		// Migrate -> created
		Migration tran1 = new SqlServerCreateDatabaseMigration(
			UUID.randomUUID(),
			Optional.empty(),
			Optional.of(created.getStateId()));
		resource.getMigrations().add(tran1);
		 
		// Migrate created -> schemaLoaded
		Migration tran2 = new SqlScriptMigration(
			UUID.randomUUID(),
			Optional.of(created.getStateId()),
			Optional.of(schemaLoaded.getStateId()),
			SqlServerElementFixtures.productCatalogueDatabase());
		resource.getMigrations().add(tran2);

		String databaseName = DatabaseFixtureHelper.databaseName();

		SqlServerDatabaseInstance instance = new SqlServerDatabaseInstance(
			properties.getHostName(),
			properties.hasInstanceName() ? properties.getInstanceName() : null,
			properties.getPort(),
			properties.getUsername(),
			properties.getPassword(),
			databaseName,
			null);
		 
		resource.migrate(new PrintStreamLogger(System.out), instance, schemaLoaded.getStateId());
		
		SqlServerTableDoesNotExistAssertion assertion = new SqlServerTableDoesNotExistAssertion(
			UUID.randomUUID(),
			0,
			"dbo",
			"ProductType");
 
		//
		// Execute
		//
		
		AssertionResponse response = null;
		
		try
		{
			response = assertion.perform(instance);
		}
		finally
		{
			SqlServerUtil.tryDropDatabase(instance);
		}

		//
		// Verify
		//

		assertNotNull("response", response);
		Asserts.assertAssertionResponse(false, "Table ProductType exists", response, "response");
		
	 }
	 
	 @Test public void applyForNonExistentTableSucceeds() throws
		 IndeterminateStateException,
		 AssertionFailedException,
		 MigrationNotPossibleException,
		 MigrationFailedException,
		 SQLException
	 {
		 
		 //
		 // Setup
		 //

		SqlServerProperties properties = SqlServerProperties.get();

		SqlServerDatabaseResourcePlugin resourcePlugin = new SqlServerDatabaseResourcePlugin();
		Resource resource = new ResourceImpl(
			UUID.randomUUID(),
			FactoryResourceTypes.SqlServerDatabase,
			"Database",
			resourcePlugin);

		// Created
		State created = new ImmutableState(UUID.randomUUID());
		resource.getStates().add(created);

		// Migrate -> created
		Migration tran1 = new SqlServerCreateDatabaseMigration(
			UUID.randomUUID(),
			Optional.empty(),
			Optional.of(created.getStateId()));
		resource.getMigrations().add(tran1);
		
		String databaseName = DatabaseFixtureHelper.databaseName();

		SqlServerDatabaseInstance instance = new SqlServerDatabaseInstance(
			properties.getHostName(),
			properties.hasInstanceName() ? properties.getInstanceName() : null,
			properties.getPort(),
			properties.getUsername(),
			properties.getPassword(),
			databaseName,
			null);
		 
		resource.migrate(new PrintStreamLogger(System.out), instance, created.getStateId());
		
		SqlServerTableDoesNotExistAssertion assertion = new SqlServerTableDoesNotExistAssertion(
			UUID.randomUUID(),
			0,
			"dbo",
			"ProductType");
 
		//
		// Execute
		//
		
		AssertionResponse response = null;
		
		try
		{
			response = assertion.perform(instance);
		}
		finally
		{
			SqlServerUtil.tryDropDatabase(instance);
		}

		//
		// Verify
		//

		assertNotNull("response", response);
		Asserts.assertAssertionResponse(true, "Table ProductType does not exist", response, "response");
		
	 }
	 
	 @Test public void applyForNonExistentDatabaseFails() throws SQLException
	 {
		 
		//
		// Setup
		//
		 
		SqlServerProperties properties = SqlServerProperties.get();
	
		String databaseName = DatabaseFixtureHelper.databaseName();

		SqlServerDatabaseInstance instance = new SqlServerDatabaseInstance(
			properties.getHostName(),
			properties.hasInstanceName() ? properties.getInstanceName() : null,
			properties.getPort(),
			properties.getUsername(),
			properties.getPassword(),
			databaseName,
			null);
		 
		SqlServerTableDoesNotExistAssertion assertion = new SqlServerTableDoesNotExistAssertion(
			UUID.randomUUID(),
			0,
			"dbo",
			"ProductType");
 
		//
		// Execute
		//
		
		AssertionResponse response = assertion.perform(instance);

		//
		// Verify
		//

		assertNotNull("response", response);
		Asserts.assertAssertionResponse(
			false, "Database " + databaseName + " does not exist",
			response, "response");

	 }
	 
	 @Test public void applyForNullInstanceFails()
	 {
		// Setup
		SqlServerTableDoesNotExistAssertion assertion = new SqlServerTableDoesNotExistAssertion(
			UUID.randomUUID(),
			0,
			"dbo",
			"TableName");
		
		// Execute and Verify
		try
		{
			AssertionResponse response = assertion.perform(null);
			
			fail("IllegalArgumentException expected");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("e.message", "instance cannot be null", e.getMessage());
		}
	 }
	 
	 @Test public void applyForIncorrectInstanceTypeFails()
	 {
		// Setup
		SqlServerTableDoesNotExistAssertion assertion = new SqlServerTableDoesNotExistAssertion(
			UUID.randomUUID(),
			0,
			"dbo",
			"TableName");
		
		FakeInstance instance = new FakeInstance();
		
		// Execute and Verify
		try
		{
			AssertionResponse response = assertion.perform(instance);
			
			fail("IllegalArgumentException expected");
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("e.message", "instance must be a SqlServerDatabaseInstance", e.getMessage());
		}
	 }
}
