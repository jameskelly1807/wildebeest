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

import co.mv.wb.framework.ArgumentNullException;
import co.mv.wb.framework.DatabaseHelper;

import java.sql.SQLException;

public class SqlServerUtil
{
	public static void createDatabase(
		SqlServerDatabaseInstance instance) throws SQLException
	{
		if (instance == null) throw new ArgumentNullException("instance");

		DatabaseHelper.execute(
			instance.getAdminDataSource(),
			"CREATE DATABASE [" + instance.getDatabaseName() + "]",
			false);
	}

	public static void tryDropDatabase(
		SqlServerDatabaseInstance instance)
	{
		try
		{
			DatabaseHelper.execute(
				instance.getAdminDataSource(),
				"DROP DATABASE [" + instance.getDatabaseName() + "];",
				false);
		}
		catch (SQLException ex)
		{
			// Hide
		}
	}
}
