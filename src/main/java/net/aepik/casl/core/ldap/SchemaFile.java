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


package net.aepik.casl.core.ldap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Cet objet ouvre une fichier et créé un flux pour ce fichier. Il utilise
 * un parseur pour obtenir tous les objets de type SchemaObject.
 */
public class SchemaFile
{

	/**
	 * Indicate an error.
	 */
	private boolean error;

	/**
	 * Le nom du fichier schema
	 */
	private String filename;

	/**
	 * Le schéma lu
	 */
	protected Schema schema;

	/**
	 * Le reader associé au fichier
	 */
	private SchemaFileReader reader;

	/**
	 * Le writer associé au fichier
	 */
	private SchemaFileWriter writer;

	/**
	 * Construit un objet SchemaFile.
	 * @param filename Le nom du fichier schema.
	 * @param schema Le schema
	 */
	public SchemaFile (String filename, Schema schema)
	{
		this.error    = false;
		this.schema   = schema;
		this.filename = filename;
		this.reader   = schema.getSyntax().createSchemaReader();
		this.writer   = schema.getSyntax().createSchemaWriter();
	}

	/**
	 * Construit un objet SchemaFile.
	 * @param filename Le nom du fichier schema.
	 * @param reader L'interface d'entrée.
	 * @param writer L'interface de sortie.
	 */
	public SchemaFile (String filename, SchemaFileReader reader, SchemaFileWriter writer)
	{
		this.error    = false;
		this.schema   = null;
		this.filename = filename;
		this.reader   = reader;
		this.writer   = writer;
	}

	/**
	 * Indique si le fichier existe vraiment.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public boolean exists ()
	{
		return (new File(filename)).exists();
	}

	/**
	 * Return an error line.
	 * @return int
	 */
	public int getErrorLine ()
	{
		return this.reader.getErrorLine();
	}

	/**
	 * Return an error message.
	 * @return String
	 */
	public String getErrorMessage ()
	{
		return this.reader.getErrorMessage();
	}

	/**
	 * Retourne le schéma.
	 * @return Schema Le schéma associé au fichier.
	 */
	public Schema getSchema ()
	{
		return schema;
	}

	/**
	 * Indicate if there was an error.
	 * @return boolean
	 */
	public boolean isError ()
	{
		return this.error;
	}

	/**
	 * Parcourt le fichier est créer les objets correspondant.
	 * @return boolean True si l'opération a réussi, false sinon.
	 */
	public boolean read ()
	{
		if (!this.exists())
		{
			return false;
		}
		try
		{
			BufferedReader entree = new BufferedReader(
				new FileReader(new File(this.filename)));
			this.reader.setInput(entree);
			Schema s = this.reader.parse(this.reader.read());
			entree.close();
			if (s != null)
			{
				this.schema = s;
				return true;
			}
			this.error = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Modifie le schéma.
	 * @param newSchema Le nouveau schéma à prendre en compte.
	 */
	public void setSchema (Schema newSchema)
	{
		this.schema = newSchema;
	}

	/**
	 * Ecrit les objets vers l'interface de sortie.
	 * Si le fichier de sortie existe déjà, l'opération échoue.
	 * @return boolean True si l'opération a réussi, false sinon.
	 */
	public boolean write ()
	{
		if (this.exists())
		{
			return false;
		}
		try
		{
			File f = new File(this.filename);
			f.createNewFile();
			BufferedWriter sortie = new BufferedWriter(new FileWriter(f));
			writer.setOutput(sortie);
			writer.write(schema) ;
			sortie.flush();
			sortie.close();
			return true;
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		return false;
	}

}
