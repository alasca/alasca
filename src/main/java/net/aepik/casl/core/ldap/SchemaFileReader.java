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
import java.io.IOException;

/**
 * Cet objet est un parser pour la grammaire décrit dans la RFC 2252.
 * Ce parser ne concerne que les grammaires concernant les objets de type
 * ObjectClass ou AttributeType.
 * <br/><br/>
 * Ce parseur vérifie si la syntaxe en entrée est correcte. Cela évitera
 * une vérification supplémentaire lors de la création des objets adéquates.
 * <br/><br/>
 * Le parser respecte le standard d'écriture définit dans la RFC 2252. De ce
 * fait, en cas de modification de la formalisation, il suffit de créer une
 * classe qui étend celle-ci, et de réécrire la méthode parse(). Le schéma
 * prendra en compte automatiquement le changement, du moment qu'ils
 * obtient bien des objets du schema.
 * <br/><br/>
 * Pour plus de renseignements, lisez la RFC 2252 et notamment les
 * grammaires BNF proposées.
**/

public abstract class SchemaFileReader
{

	/**
	 * La syntaxe d'entrée
	 */
	protected SchemaSyntax syntax;

	/**
	 * L'entrée sur laquelle le parser lit
	 */
	protected BufferedReader input;

	/**
	 * Error message
	 */
	protected String errorMessage;

	/**
	 * Error line
	 */
	protected int errorLine;

	/**
	 * Schema name.
	 * @var String
	 */
	protected String schemaName;

	/**
	 * Construit un nouveau parser sans entrée.
	 * @param syntax La syntaxe.
	 */
	public SchemaFileReader (SchemaSyntax syntax)
	{
		this.syntax = syntax ;
		this.input = null ;
	}

	/**
	 * Construit un nouveau parser à partir d'une entrée.
	 * @param syntax La syntaxe.
	 * @param in Un objet Reader.
	 */
	public SchemaFileReader (BufferedReader in)
	{
		this.syntax = syntax ;
		this.input = in ;
	}

	/**
	 * Return an error line.
	 * @return int
	 */
	public int getErrorLine ()
	{
		return this.errorLine;
	}

	/**
	 * Return an error message.
	 * @return String
	 */
	public String getErrorMessage ()
	{
		return this.errorMessage;
	}

	/**
	 * Retourne le flux d'entrée du parseur.
	 * @return Reader Un flux.
	 */
	public BufferedReader getInput ()
	{
		return this.input;
	}

	/**
	 * Return the name of the readed schema.
	 * @return String
	 */
	public String getSchemaName ()
	{
		return this.schemaName;
	}

	/**
	 * Parse input lines to return schema objects.
	 * @param String[] Input lines
	 * @return Schema
	 */
	public abstract Schema parse ( String[] lines );

	/**
	 * Read input and return formatted lines.
	 * @return String[] Input lines
	 */
	public abstract String[] read () throws IOException;

	/**
	 * Set the error line.
	 * @param int errorLine
	 * @return void
	 */
	protected void setErrorLine (int errorLine)
	{
		this.errorLine = errorLine;
	}

	/**
	 * Set an error message.
	 * @param String errorMessage
	 * @return void
	 */
	protected void setErrorMessage (String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	/**
	 * Set buffer reader.
	 * @param BufferedReader input The new buffer reader.
	 */
	public void setInput (BufferedReader input)
	{
		this.input = input;
	}

	/**
	 * Set the name of the schema.
	 * @param String The name of the schema.
	 */
	public void setSchemaName ( String name )
	{
		this.schemaName = name;
	}

}

