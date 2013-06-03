package co.mv.stm.impl.database.mysql;

import co.mv.stm.impl.database.SqlScriptTransition;
import co.mv.stm.model.TransitionFailedException;
import co.zd.helium.fixture.MySqlDatabaseFixture;
import java.util.UUID;
import org.junit.Test;

public class SqlScriptTransitionTests
{
	public SqlScriptTransitionTests()
	{
	}
	
	@Test
	public void performSuccessfully() throws TransitionFailedException
	{
		
		//
		// Fixture Setup
		//
		
		MySqlDatabaseFixture f = new MySqlDatabaseFixture(
			"127.0.0.1",
			3306,
			"root",
			"password",
			"stm_test",
			"");
		f.setUp();
		
		SqlScriptTransition tr = new SqlScriptTransition(
			UUID.randomUUID(),
			UUID.randomUUID(),
			MySqlElementFixtures.realmTypeRefCreateTableStatement());
		
		MySqlDatabaseResourceInstance instance = new MySqlDatabaseResourceInstance(
			"127.0.0.1",
			3306,
			"root",
			"password",
			f.getDatabaseName());
		
		//
		// Execute
		//

		try
		{
			tr.perform(instance);
		}
		finally
		{
			f.tearDown();
		}
		
		//
		// Assert Results
		//
		
	}
}