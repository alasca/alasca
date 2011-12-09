/*
 * Copyright (C) 2010 Thomas Chemineau
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


package net.aepik.alasca.plugin.schemaquery;

import net.aepik.alasca.core.ldap.Schema;
import net.aepik.alasca.core.ldap.SchemaFile;
import net.aepik.alasca.core.ldap.SchemaObject;
import net.aepik.alasca.core.SchemaManager;
import net.aepik.alasca.core.ldap.SchemaSyntax;
import net.aepik.alasca.core.ldap.SchemaValue;
import net.aepik.alasca.util.Config;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.ParseException;

public class SchemaQueryTool
{

	private static Options options = new Options();

	static {
		options.addOption("i", "input-schema", true, "Input file");
		options.addOption("I", "input-syntax", true, "Input schema syntax");
		options.addOption("a", "list-attributes", false, "List attributes");
		options.addOption("o", "list-objectclasses", false, "List objectclasses");
		options.addOption("l", "list-syntaxes", false, "List available syntaxes");
		options.getOption("i").setRequired(true);
		options.getOption("I").setRequired(true);
	}

	/**
	 * Create a SchemaSyntax object from a valid syntax name.
	 * @param String syntaxName A valid syntax name.
	 * @return SchemaSyntax
	 */
	private static SchemaSyntax createSchemaSyntax ( String syntaxName )
	{
		SchemaSyntax syntax = null;
		try
		{
			String syntaxClassName = Schema.getSyntaxPackageName() + "." + syntaxName;
			@SuppressWarnings("unchecked")
			Class<SchemaSyntax> syntaxClass = (Class<SchemaSyntax>) Class.forName(syntaxClassName);
			syntax = syntaxClass.newInstance();
		}
		catch (Exception e){};
		return syntax;
	}

	/**
	 * Create a Schema object from a SchemaSyntax object and a schema file.
	 * @param SchemaSyntax syntax A SchemaSyntax object.
	 * @param String fileName The file name of the schema to parse.
	 * @return Schema
	 */
	private static Schema createSchema ( SchemaSyntax syntax, String fileName )
	{
		Schema schema = null;
		try
		{
			SchemaFile schemaFile = Schema.createAndLoad(syntax, fileName, true);
			schema = schemaFile.getSchema();
		}
		catch (Exception e){};
		return schema;
	}

	/**
	 * Print attributes found in schema.
	 * @param Schema schema A schema object.
	 */
	private static void displayAttributes (Schema schema)
	{
		String type = schema.getSyntax().getAttributeType();
		for(SchemaObject attr : schema.getObjectsInOrder(type))
		{
			System.out.println(attr.getName());
		}
	}

	/**
	 * Print object classes found in schema.
	 * @param Schema schema A schema object.
	 */
	private static void displayObjectClasses (Schema schema)
	{
		String type = schema.getSyntax().getObjectClassType();
		for(SchemaObject objt : schema.getObjectsInOrder(type))
		{
			System.out.println(objt.getNameFirstValue());
		}
	}

	/**
	 * Print available syntaxes.
	 */
	private static void displayAvailableSyntaxes ()
	{
		for (String syntax : Schema.getSyntaxes())
		{
			System.out.println(syntax);
		}
	}

	/**
	 * Print usage.
	 */
	private static void displayHelp ()
	{
		HelpFormatter h = new HelpFormatter();
		h.setLeftPadding(4);
		h.printHelp("alascaquery", options, true);
	}

	/**
	 * Launch this tool.
	 */
	public static void main ( String[] args )
	{
		String inFile    = null;
		String inSyntax  = null;
		boolean attr     = false;
		boolean objt     = false;
		boolean synt     = false;

		//
		// Parsing options.
		//
		CommandLineParser parser = new GnuParser();
		try
		{
			CommandLine cmdOptions = parser.parse(options, args);
			inFile    = cmdOptions.getOptionValue("i");
			inSyntax  = cmdOptions.getOptionValue("I");
			if (cmdOptions.hasOption("a"))
			{
				attr = true;
			}
			if (cmdOptions.hasOption("o"))
			{
				objt = true;
			}
			if (cmdOptions.hasOption("l"))
			{
				synt = true;
			}
			if (cmdOptions.getOptions().length == 0)
			{
				displayHelp();
				System.exit(2);
			}
		}
		catch (MissingArgumentException e)
		{
			System.out.println("Missing arguments\n");
			displayHelp();
			System.exit(2);
		}
		catch (ParseException e)
		{
			System.out.println("Wrong arguments\n");
			displayHelp();
			System.exit(2);
		}

		//
		// Print query options.
		//
		if (synt)
		{
			displayAvailableSyntaxes();
			System.exit(0);
		}

		//
		// Launch schema.
		//
		SchemaSyntax inSchemaSyntax = createSchemaSyntax(inSyntax);
		if (inSchemaSyntax == null)
		{
			System.out.println("Unknow input syntax (" + inSyntax + ")");
			System.exit(1);
		}
		Schema inSchema = createSchema(inSchemaSyntax, inFile);
		if (inSchema == null)
		{
			System.out.println("Failed to read input schema file (" + inFile + ")");
			System.exit(1);
		}

		//
		// List if asks
		//
		if (attr)
		{
			displayAttributes(inSchema);
		}
		if (objt)
		{
			displayObjectClasses(inSchema);
		}

		System.exit(0);
	}

}
