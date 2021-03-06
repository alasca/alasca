/*
 * Copyright (C) 2006-2011 Thomas Chemineau
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

package net.aepik.alasca.core.ldap.parser;

import net.aepik.alasca.core.ldap.Schema;
import net.aepik.alasca.core.ldap.SchemaObject;
import net.aepik.alasca.core.ldap.SchemaSyntax;
import net.aepik.alasca.core.ldap.SchemaValue;
import java.io.IOException;

/**
 * Write ldap definitions in a Openldap 2.4 compliant format.
 */
public class Openldap24Writer extends OpenldapWriter
{

	/**
	 * Write contents onto output flow.
	 * @param Schema
	 */
	public void writeObjects ( Schema schema ) throws IOException
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
		String eol = System.getProperty("line.separator");
		String str = "dn: cn=" + schema.getName() + ",cn=schema,cn=config" + eol
		           + "objectClass: olcSchemaConfig" + eol
		           + "cn: " + schema.getName() + eol;
		output.write(str, 0, str.length());
		super.writeObjects(schema);
		output.newLine();
	}

	/**
	 * Return the string representation of a SchemaObject object.
	 * @param object A SchemaObject object.
	 * @return String Its String representation.
	 */
	public String valueOf (SchemaObject object)
	{
		if (object == null)
		{
			return "";
		}
		String str = "";
		String eol = System.getProperty("line.separator");
		String type = object.getType();
		SchemaSyntax syntax = object.getSyntax();
		if (type.equals(syntax.getObjectIdentifierType()))
		{
			String[] keys = object.getKeys();
			SchemaValue value = object.getValue(keys[0]);
			str = keys[0] + " " + value.toString();
		}
		else
		{
			String[] params_name = syntax.getParameters(type);
			str = object.getId();
			for (int i = 0; i < params_name.length; i++)
			{
				if (object.isKeyExists(params_name[i]))
				{
					str += " " + params_name[i] + " " + object.getValue(params_name[i]);
				}
			}
			str = str.trim();
		}
		if (type.equals(syntax.getObjectClassType()))
		{
			str = syntax.getObjectClassHeader() + " ( " + str + " )";
		}
		if (type.equals(syntax.getAttributeType()))
		{
			str = syntax.getAttributeHeader() + " ( " + str + " )";
		}
		if (type.equals(syntax.getObjectIdentifierType()))
		{
			str = syntax.getObjectIdentifierHeader() + " " + str;
		}
		return str;
	}

}
