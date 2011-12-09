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
import net.aepik.alasca.core.ldap.SchemaFileReader;
import net.aepik.alasca.core.ldap.SchemaObject;
import net.aepik.alasca.core.ldap.SchemaSyntax;
import net.aepik.alasca.core.ldap.SchemaValue;
import java.io.IOException;
import java.util.Vector;

public class RFCReader extends SchemaFileReader
{

	/**
	 * Build a new RFCReader object.
	 * @param syntax The reader syntax.
	 */
	public RFCReader (SchemaSyntax syntax)
	{
		super(syntax);
	}

        /**
         * Parse input lines to return schema objects.
         * @param String[] Input lines
         * @return Schema
         */
        public Schema parse ( String[] lines )
	{
		if (syntax == null)
		{
			return null;
		}

		Schema schema = new Schema(this.getSchemaName(), syntax);
		Vector<SchemaObject> objects = new Vector<SchemaObject>();

		int errorLineNumber = -1;
		int bracketHeight = 0;

		for (int l = 0; l < lines.length && errorLineNumber == -1; l++)
		{
			String line = lines[l].trim().replaceAll("\\s+", " ").replaceAll("\\t+", " ");

			char[] chars = line.toCharArray();
			int openedBracketIndex = -1;
			int closedBracketIndex = -1;
			SchemaObject object = null;
			bracketHeight = 0;

			// Verification sur la syntaxe
			for (int i = 0; i < chars.length; i++)
			{
				switch (chars[i])
				{
					// Parenthèse ouvrante
					// On rentre dans une définition de paramètres de l'objet.
					// On indique qu'il faut créer l'objet.
					case 40:
						if (openedBracketIndex == -1 && bracketHeight == 0)
						{
							openedBracketIndex = i;
						}
						bracketHeight++;
						break;

					// Parenthèse fermante
					// Si c'est une fin de définition de paramètres de l'objet,
					// on indique qu'il faut initialiser l'objet.
					case 41:
						bracketHeight--;
						if (closedBracketIndex == -1 && bracketHeight == 0)
						{
							closedBracketIndex = i;
						}
						break;
				}
			}

			if (bracketHeight != 0)
			{
				errorLineNumber = l;
				continue;
			}

			if (line.toLowerCase().startsWith("dn:"))
			{
				int index = line.indexOf(':');
				String value = line.substring(index+1).trim();
				schema.getProperties().setProperty("dn", value);
				continue;
			}

			if (syntax.isObjectIdentifierHeader(line))
			{
				object = syntax.createSchemaObject(syntax.getObjectIdentifierType(), null);
			}
			else if (syntax.isAttributeHeader(line))
			{
				object = syntax.createSchemaObject(syntax.getAttributeType(), null);
			}
			else if (syntax.isObjectClassHeader(line))
			{
				object = syntax.createSchemaObject(syntax.getObjectClassType(), null);
			}

			if (object != null)
			{
				if (openedBracketIndex > -1 && closedBracketIndex > openedBracketIndex)
				{
					line = line.substring(openedBracketIndex+1, closedBracketIndex).trim();
				}
				if (object.initFromString(line))
				{
					objects.add(object);
				}
				else
				{
					errorLineNumber = l;
				}
			}
		}

		//
		// Test errors. If an error occurs, we keep it in
		// memory so that we could reuse it as a trace.
		//
		if (errorLineNumber != -1)
		{
			this.setErrorMessage(lines[errorLineNumber]);
			this.setErrorLine(errorLineNumber+1);
			return null;
		}
		if (bracketHeight != 0)
		{
			this.setErrorMessage("Missing parentheses");
			this.setErrorLine(errorLineNumber+1);
			return null;
		}
		if (objects.size() == 0 || !schema.addObjects(objects))
		{
			this.setErrorMessage("Empty schema");
			return null;
		}

		//
		// Optional: specify hierarchy if we could.
		//
		for (SchemaObject o : objects)
		{
			SchemaValue parentNameValue = o.getValue("SUP");
			if (parentNameValue == null)
			{
				continue;
			}
			String parentName = parentNameValue.getValue();
			if (parentName == null)
			{
				continue;
			}
			SchemaObject parent = schema.getObjectByName(parentName);
			if (parent == null)
			{
				continue;
			}
			o.setParent(parent);
		}

		return schema;
	}

        /**
         * Read input and return formatted lines.
         * @return String[] Input lines
         */
        public String[] read () throws java.io.IOException
	{
		if (input == null)
		{
			return null;
		}

		int lineNumber = 0;
		String buffer = null;
		String line = null;
		Vector<String> lines = new Vector<String>();

		while ((line = input.readLine()) != null)
		{
			lineNumber++;
			char[] chars = line.toCharArray();

			// Ligne vide, commence par un diese
			if (chars.length == 0 || (chars.length > 0 && chars[0] == 35))
			{
				continue;
			}

			// Commence par un espace ou une tabulation
			if (chars[0] == 32 || chars[0] == 9)
			{
				if (buffer == null)
				{
					buffer = new String();
				}
				buffer += " " + line.substring(1);
				continue;
			}

			if (buffer != null)
			{
				lines.add(buffer);
			}

			buffer = line;
		}

		if (lines.size() == 0)
		{
			this.setErrorMessage("Empty schema");
			return null;
		}

		return lines.toArray(new String[0]);
	}

}
