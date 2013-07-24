package co.zd.wb.model.mysql;

import co.zd.helium.fixture.MySqlDatabaseFixture;
import co.zd.wb.model.IndeterminateStateException;
import co.zd.wb.model.State;
import co.zd.wb.model.base.ImmutableState;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class MySqlDatabaseResourceTests
{
	public MySqlDatabaseResourceTests()
	{
	}
	
	//
	// currentState()
	//
	
	@Test public void currentStateForNonExistentDatabaseSucceds() throws IndeterminateStateException
	{
		
		//
		// Fixture Setup
		//

		MySqlProperties mySqlProperties = MySqlProperties.get();

		MySqlDatabaseResource resource = new MySqlDatabaseResource(
			UUID.randomUUID(),
			"Database");

		MySqlDatabaseInstance instance = new MySqlDatabaseInstance(
			mySqlProperties.getHostName(),
			mySqlProperties.getPort(),
			mySqlProperties.getUsername(),
			mySqlProperties.getPassword(),
			"non_existent_schema");

		//
		// Execute
		//
		
		State state = resource.currentState(instance);
		
		//
		// Assert Results
		//
		
		Assert.assertEquals("state", null, state);
		
	}
	
	@Test public void currentStateForExistentDatabaseSucceds() throws IndeterminateStateException
	{
		
		//
		// Fixture Setup
		//
		
		MySqlProperties mySqlProperties = MySqlProperties.get();

		UUID knownStateId = UUID.randomUUID();
		
		MySqlDatabaseFixture database = new MySqlDatabaseFixture(
			mySqlProperties.getHostName(),
			mySqlProperties.getPort(),
			mySqlProperties.getUsername(),
			mySqlProperties.getPassword(),
			"stm",
			MySqlElementFixtures.stmStateCreateTableStatement() +
			MySqlElementFixtures.stmStateInsertRow(knownStateId));
		
		try
		{
			database.setUp();

			MySqlDatabaseResource resource = new MySqlDatabaseResource(
				UUID.randomUUID(),
				"Database");
			resource.getStates().add(new ImmutableState(knownStateId));

			MySqlDatabaseInstance instance = new MySqlDatabaseInstance(
				mySqlProperties.getHostName(),
				mySqlProperties.getPort(),
				mySqlProperties.getUsername(),
				mySqlProperties.getPassword(),
				database.getDatabaseName());

			//
			// Execute
			//

			State state = resource.currentState(instance);

			//
			// Assert Results
			//

			Assert.assertEquals("state.stateId", knownStateId, state.getStateId());
		}
		finally
		{
		
			//
			// Fixture Tear-Down
			//

			database.tearDown();
			
		}
	}
	
	@Test public void currentStateForDatabaseWithMultipleStateRowsFails()
	{
		
		//
		// Fixture Setup
		//
		
		MySqlProperties mySqlProperties = MySqlProperties.get();
		
		MySqlDatabaseFixture database = new MySqlDatabaseFixture(
			mySqlProperties.getHostName(),
			mySqlProperties.getPort(),
			mySqlProperties.getUsername(),
			mySqlProperties.getPassword(),
			"stm",
			MySqlElementFixtures.stmStateCreateTableStatement() +
			MySqlElementFixtures.stmStateInsertRow(UUID.randomUUID()) +
			MySqlElementFixtures.stmStateInsertRow(UUID.randomUUID()));
		database.setUp();

		MySqlDatabaseResource resource = new MySqlDatabaseResource(
			UUID.randomUUID(),
			"Database");
		
		MySqlDatabaseInstance instance = new MySqlDatabaseInstance(
			mySqlProperties.getHostName(),
			mySqlProperties.getPort(),
			mySqlProperties.getUsername(),
			mySqlProperties.getPassword(),
			database.getDatabaseName());

		//
		// Execute
		//
		
		IndeterminateStateException caught = null;
		
		try
		{
			resource.currentState(instance);
			
			Assert.fail("IndeterminateStateException expected");
		}
		catch(IndeterminateStateException e)
		{
			caught = e;
		}
		
		//
		// Assert Results
		//
		
		Assert.assertTrue("exception message", caught.getMessage().startsWith("Multiple rows found"));
		
		//
		// Fixture Tear-Down
		//
		
		database.tearDown();
		
	}
	
	@Test public void currentStateForDatabaseWithUnknownStateIdDeclaredFails()
	{
		
		//
		// Fixture Setup
		//
		
		MySqlProperties mySqlProperties = MySqlProperties.get();
		
		UUID knownStateId = UUID.randomUUID();
		
		MySqlDatabaseFixture database = new MySqlDatabaseFixture(
			mySqlProperties.getHostName(),
			mySqlProperties.getPort(),
			mySqlProperties.getUsername(),
			mySqlProperties.getPassword(),
			"stm",
			MySqlElementFixtures.stmStateCreateTableStatement() +
			MySqlElementFixtures.stmStateInsertRow(knownStateId));
		database.setUp();

		MySqlDatabaseResource resource = new MySqlDatabaseResource(
			UUID.randomUUID(),
			"Database");
		
		MySqlDatabaseInstance instance = new MySqlDatabaseInstance(
			mySqlProperties.getHostName(),
			mySqlProperties.getPort(),
			mySqlProperties.getUsername(),
			mySqlProperties.getPassword(),
			database.getDatabaseName());

		//
		// Execute
		//
		
		IndeterminateStateException caught = null;
		try
		{
			resource.currentState(instance);
			
			Assert.fail("IndeterminateStateException expected");
		}
		catch(IndeterminateStateException e)
		{
			caught = e;
		}
		
		//
		// Assert Results
		//
		
		Assert.assertTrue(
			"exception message",
			caught.getMessage().startsWith("The resource is declared to be in state"));
		
		//
		// Fixture Tear-Down
		//
		
		database.tearDown();
		
	}
	
	@Ignore @Test public void currentStateForDatabaseWithInvalidStateTableSchemaFaults()
	{
		throw new UnsupportedOperationException();
	}
}