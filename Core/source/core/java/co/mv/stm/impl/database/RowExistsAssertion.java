package co.mv.stm.impl.database;

import co.mv.stm.impl.database.mysql.MySqlDatabaseResourceInstance;
import co.mv.stm.impl.BaseAssertion;
import co.mv.stm.model.Assertion;
import co.mv.stm.model.AssertionFaultException;
import co.mv.stm.model.AssertionResponse;
import co.mv.stm.model.AssertionType;
import co.mv.stm.model.ModelExtensions;
import co.mv.stm.model.ResourceInstance;
import co.mv.stm.model.impl.ImmutableAssertionResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import javax.sql.DataSource;

public class RowExistsAssertion extends BaseAssertion implements Assertion
{
	public RowExistsAssertion(
		UUID assertionId,
		String name,
		int seqNum,
		String sql)
	{
		super(assertionId, name, seqNum, AssertionType.DatabaseRowExists);
		this.setSql(sql);
	}
	
	// <editor-fold desc="Sql" defaultstate="collapsed">

	private String m_sql = null;
	private boolean m_sql_set = false;

	public String getSql() {
		if(!m_sql_set) {
			throw new IllegalStateException("sql not set.  Use the HasSql() method to check its state before accessing it.");
		}
		return m_sql;
	}

	private void setSql(
		String value) {
		if(value == null) {
			throw new IllegalArgumentException("sql cannot be null");
		}
		boolean changing = !m_sql_set || m_sql != value;
		if(changing) {
			m_sql_set = true;
			m_sql = value;
		}
	}

	private void clearSql() {
		if(m_sql_set) {
			m_sql_set = true;
			m_sql = null;
		}
	}

	private boolean hasSql() {
		return m_sql_set;
	}

	// </editor-fold>
	
	@Override public AssertionResponse apply(ResourceInstance instance)
	{
		if (instance == null) { throw new IllegalArgumentException("instance cannot be null"); }
		MySqlDatabaseResourceInstance db = ModelExtensions.As(instance, MySqlDatabaseResourceInstance.class);
		if (db == null)
		{
			throw new IllegalArgumentException("instance must be a DatabaseResourceInstance");
		}

		AssertionResponse result = null;
		
		DataSource ds = db.getAppDataSource();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			try
			{
				conn = ds.getConnection();
				ps = conn.prepareStatement(this.getSql());
				rs = ps.executeQuery();

				int rowCount = 0;
				while(rs.next())
				{
					rowCount ++;
				}

				if (rowCount == 1)
				{
					result = new ImmutableAssertionResponse(true, "Exactly one row exists, as expected");
				}
				else
				{
					result = new ImmutableAssertionResponse(
						false,
						String.format("Expected to find exactly one row, but found %d", rowCount));
				}
			}
			finally
			{
				DatabaseHelper.release(rs);
				DatabaseHelper.release(ps);
				DatabaseHelper.release(conn);
			}
		}
		catch(SQLException e)
		{
			throw new AssertionFaultException(this.getAssertionId(), e);
		}
		
		return result;
	}
}