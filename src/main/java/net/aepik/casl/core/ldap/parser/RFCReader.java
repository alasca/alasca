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
import net.aepik.casl.core.ldap.SchemaFileReader;
import net.aepik.casl.core.ldap.SchemaObject;
import net.aepik.casl.core.ldap.SchemaSyntax;
import net.aepik.casl.core.ldap.SchemaValue;
import java.io.IOException;
import java.util.Vector;

public class RFCReader extends SchemaFileReader
{

	public RFCReader (SchemaSyntax syntax)
	{
		super(syntax);
	}

	public Schema read() throws IOException
	{
		if (input == null || syntax == null)
		{
			return null;
		}

		Schema schema = new Schema( syntax );
		SchemaObject object = null;
		boolean createObject = false;
		boolean initialiseObject = false;
		boolean objectParameter = false;
		boolean objectError = false;
		Vector<SchemaObject> objects = new Vector<SchemaObject>();

		int nbOccurences = 0;
		int nbLines = 0;
		int bracketHeight = 0;
		int bracketLine = 0;
		StringBuffer buffer = new StringBuffer();
		StringBuffer bufferBackup = new StringBuffer();

		//
		// Du a des problèmes sur la lecture de fichier entre les
		// différentes plateformes, on va lire le fichier ligne par
		// ligne, en laissant Java s'occuper des caractères de fin de ligne
		// et de fin de fichier.
		//

		String line;
		while ((line = input.readLine()) != null && bracketHeight >= 0 && !objectError)
		{
			int debut = 0;
			char[] chars = line.toCharArray();

			//
			// Est une ligne, ou commence par un diese = un commentaire
			// Inutile d'aller plus loin
			//
			if (chars.length == 0 || (chars.length > 0 && chars[0] == 35))
			{
				nbLines++;
				continue;
			}

			//
			// Commence par un espace = continuité de la ligne précédente
			//
			if (chars[0] == 32 || chars[0] == 9)
			{
				debut++;
			}

			for (int i = debut; i < chars.length && bracketHeight >= 0 && !objectError; i++)
			{
				if (debut == chars.length - 1 && bracketHeight == 0)
				{
					bufferBackup = buffer;
					buffer = new StringBuffer();
					objectParameter = false;
					createObject = true;
				}
				else
				{
					switch (chars[i])
					{
						//
						// Parenthèse ouvrante !
						// On rentre dans une définition de paramètres de l'objet.
						// On indique qu'il faut créer l'objet.
						//
						case 40:
							if (bracketHeight == 0)
							{
								bufferBackup = buffer;
								buffer = new StringBuffer();
								objectParameter = true;
								createObject = true;
							} else {
								buffer.append(chars[i]);
							}
							bracketHeight++;
							break;

						//
						// Parenthèse fermante !
						// Si c'est une fin de définition de paramètres de l'objet,
						// on indique qu'il faut initialiser l'objet.
						//
						case 41:
							bracketHeight--;
							if (bracketHeight == 0)
							{
								bufferBackup = buffer;
								buffer = new StringBuffer();
								objectParameter = false;
								initialiseObject = true;
								bracketLine = nbLines;
							} else {
								buffer.append(chars[i]);
							}
							break;

						//
						// On copie dans tous les autres cas.
						//
						default:
							buffer.append(chars[i]);
							break;
					}
				}

				//
				// Test if we are at the end of the line
				//
				if (i == chars.length - 1 && bracketHeight == 0 && !createObject && !initialiseObject)
				{
					bufferBackup = buffer;
					buffer = new StringBuffer();
					objectParameter = false;
					createObject = true;
					initialiseObject = true;
				}

				//
				// Test des propriétés du schéma (tout sauf attributs
				// et classes d'objets)
				//
				if (bufferBackup.toString().toLowerCase().startsWith("dn:"))
				{
					String str = bufferBackup.toString();
					int index = str.indexOf(':');
					String value = str.substring(index+1).trim();
					schema.getProperties().setProperty("dn", value);
				}

				//
				// La définition d'un objet se fait obligatoirement en dehors
				// d'un quelconque niveau de parenthèse.
				//
				if (createObject)
				{
					if (syntax.isObjectIdentifierHeader(bufferBackup.toString()))
					{
						object = syntax.createSchemaObject(syntax.getObjectIdentifierType(), null);
					}
					else if (syntax.isAttributeHeader(bufferBackup.toString()))
					{
						object = syntax.createSchemaObject(syntax.getAttributeType(), null);
					}
					else if (syntax.isObjectClassHeader(bufferBackup.toString()))
					{
						object = syntax.createSchemaObject(syntax.getObjectClassType(), null);
					}
					if (object != null)
					{
						buffer = new StringBuffer();
						nbOccurences++;
					}
					createObject = false;
				}

				//
				// Si on est en creation d'objet et que l'objet n'est pas null,
				// on initialise cet objet à partir de ses paramêtres.
				//
				if (initialiseObject && object != null)
				{
					String def = bufferBackup.toString().trim().replaceAll("\\s+", " ").replaceAll("\\t+", " ");
					if (object.initFromString(def))
					{
						objects.add(object);
						object = null;
					}
					else
					{
						objectError = true;
					}
					initialiseObject = false;
				}

			}

			//
			// Si c'est un retour à la ligne
			//
			if (!objectParameter)
			{
				bufferBackup = buffer;
				buffer = new StringBuffer();
			}
			else
			{
				buffer.append(" ");
			}

			nbLines++;
		}

		//
		// Test errors. If an error occurs, we keep it in
		// memory so that we could reuse it as a trace.
		//
		if (objectError)
		{
			this.setErrorMessage(bufferBackup.toString());
			this.setErrorLine(nbLines+1);
			return null;
		}
		if (bracketHeight != 0)
		{
			this.setErrorMessage("Missing parentheses");
			this.setErrorLine(bracketLine+2);
			return null;
		}
		if (nbOccurences == 0 || !schema.addObjects(objects))
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

		return schema ;
	}

}
