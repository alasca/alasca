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
		SchemaSyntax syntax = schema.getSyntax();
		SchemaObject[] objets = schema.getObjectsInOrder();
		for (SchemaObject o : objets)
		{
			//
			// Il faut fabriquer la chaîne ! La déconstruction se fait dans
			// le SchemaFileReader, la reconstruction se fait donc dans
			// SchemaFileWriter. L'objet ne retourne que son contenu, il n'y
			// a pas de déclaration.
			//
			String objStr = "";
			String oidParamName = null;
			if (o.getType().equals(syntax.getObjectClassType()))
			{
				objStr += syntax.getObjectClassHeader();
			}
			if (o.getType().equals(syntax.getAttributeType()))
			{
				objStr += syntax.getAttributeHeader();
			}
			o.delValue(oidParamName);
			objStr += " ( " + o.toString() + " )";
			//
			// Enfin, on ecrit la chaîne dans le fichier. On fait attention à
			// ce que tout objet soit séparé par un retour à la ligne.
			//
			output.write(objStr, 0, objStr.length());
			output.newLine();
		}
	}

}
