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

package co.mv.wb;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
{
	co.mv.wb.plugin.base.AllTests.class,
	co.mv.wb.plugin.composite.AllTests.class,
	co.mv.wb.plugin.generaldatabase.AllTests.class,
	co.mv.wb.plugin.mysql.AllTests.class,
	co.mv.wb.plugin.postgresql.AllTests.class,
	co.mv.wb.plugin.sqlserver.AllTests.class
})
public class AllTests
{
}
