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

import co.mv.wb.framework.ArgumentNullException;

/**
 * Defines a meta classification for plugins to help with discoverability and documentation generation.  PluginGroup
 * plays no role in Wildebeest workflows.
 *
 * @author                                      Brendon Matheson
 * @since                                       4.0
 */
public class PluginGroup
{
	private final String _uri;
	private final String _name;
	private final String _description;

	public PluginGroup(
		String uri,
		String name,
		String description)
	{
		if (uri == null) throw new ArgumentNullException("uri");
		if (name == null) throw new ArgumentNullException("name");
		if (description == null) throw new ArgumentNullException("description");

		_uri = uri;
		_name = name;
		_description = description;
	}

	public String getUri()
	{
		return _uri;
	}

	public String getName()
	{
		return _name;
	}

	public String getDescription()
	{
		return _description;
	}
}
