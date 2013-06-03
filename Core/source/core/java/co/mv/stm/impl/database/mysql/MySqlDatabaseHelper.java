/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package co.mv.stm.impl.database.mysql;

import co.mv.stm.impl.database.DatabaseHelper;
import co.mv.stm.model.FaultException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author brendonm
 */
public class MySqlDatabaseHelper
{
	public static boolean schemaExists(
		MySqlDatabaseResourceInstance instance,
		String schemaName)
	{
		if (instance == null) { throw new IllegalArgumentException("instance"); }
		if (schemaName == null) { throw new IllegalArgumentException("schemaName cannot be null"); }
		if ("".equals(schemaName)) { throw new IllegalArgumentException("schemaName cannot be empty"); }
		
		boolean result = false;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			conn = instance.getInfoDataSource().getConnection();
			ps = conn.prepareStatement("SELECT * FROM SCHEMATA WHERE SCHEMA_NAME = ?;");
			ps.setString(1, schemaName);
			
			rs = ps.executeQuery();
		
			result = rs.next();
		}
		catch(SQLException e)
		{
			throw new FaultException(e);
		}
		finally
		{
			try
			{
				DatabaseHelper.release(rs);
				DatabaseHelper.release(ps);
				DatabaseHelper.release(conn);
			}
			catch(SQLException e)
			{
				throw new FaultException(e);
			}
		}
		
		return result;
	}
}