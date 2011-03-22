/*
 * Copyright (C) 2006-2010 Thomas Chemineau
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.aepik.casl.core.ldap.parser;

import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaFileWriter;
import net.aepik.casl.core.ldap.SchemaObject;
import net.aepik.casl.core.ldap.SchemaSyntax;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Write ldap definitions in a Openldap compliant format.
 */
public class OpenldapWriter extends RFCWriter
{

	/**
	 * Build a new OpenldapWriter object.
	 */
	public OpenldapWriter ()
	{
		super();
	}

	/**
	 * Write contents onto output flow.
	 */
	public void write ( Schema schema ) throws IOException
	{
		if (output == null)
		{
			return;
		}
		if (schema == null)
		{
			return;
		}
		if (schema.getSyntax() == null)
		{
			return;
		}
		/*
		Properties objectsIdentifiers = schema.getObjectsIdentifiers();
		for (Enumeration keys = objectsIdentifiers.propertyNames(); keys.hasMoreElements();)
		{
			String key = (String) keys.nextElement();
			String value = objectsIdentifiers.getProperty(key);
			output.write(schema.getSyntax().getObjectIdentifierHeader() + " " + key + " " +value);
			output.newLine();
		}
		*/
		super.write(schema);
	}

}
