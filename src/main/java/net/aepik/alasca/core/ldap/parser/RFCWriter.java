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


package net.aepik.alasca.core.ldap.parser;

import net.aepik.alasca.core.ldap.Schema;
import net.aepik.alasca.core.ldap.SchemaFileWriter;
import net.aepik.alasca.core.ldap.SchemaObject;
import net.aepik.alasca.core.ldap.SchemaSyntax;
import java.io.IOException;

/**
 * Write ldap definitions in a RFC compliant format.
 */
public class RFCWriter extends SchemaFileWriter
{

	/**
	 * Build a new RFCWriter object.
	 */
	public RFCWriter ()
	{
		super();
	}

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
		SchemaSyntax syntax = schema.getSyntax();
		SchemaObject[] objets = schema.getObjectsInOrder();
		for (SchemaObject o : objets)
		{
			String objStr = this.valueOf(o);
			output.write(objStr, 0, objStr.length());
			output.newLine();
		}
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
		String str = object.toString();
		String type = object.getType();
		SchemaSyntax syntax = object.getSyntax();
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
