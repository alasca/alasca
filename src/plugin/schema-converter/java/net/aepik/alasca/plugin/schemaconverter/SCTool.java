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


package net.aepik.alasca.plugin.schemaconverter;

import net.aepik.alasca.plugin.schemaconverter.core.SchemaConverter;
import net.aepik.alasca.plugin.schemaconverter.core.Translator;
import net.aepik.alasca.core.ldap.Schema;
import net.aepik.alasca.core.ldap.SchemaFile;
import net.aepik.alasca.core.ldap.SchemaFileWriter;
import net.aepik.alasca.core.SchemaManager;
import net.aepik.alasca.core.ldap.SchemaSyntax;
import net.aepik.alasca.util.Config;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.ParseException;
import java.util.Properties;

public class SCTool {

	private static Options options = new Options();

	static {
		options.addOption("i", "input-schema", true, "Input file");
		options.addOption("I", "input-syntax", true, "Input schema syntax");
		options.addOption("o", "output-schema", true, "Output file");
		options.addOption("O", "output-syntax", true, "Output schema syntax");
		options.addOption("c", "clear-properties", false, "Do not convert schema properties");
		options.addOption("l", "list-syntaxes", false, "List available syntaxes");
		options.getOption("i").setRequired(true);
		options.getOption("I").setRequired(true);
		options.getOption("o").setRequired(true);
		options.getOption("O").setRequired(true);
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
		catch (Exception e)
		{
			e.printStackTrace();
		};
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
	 * Find a valid dictionnary.
	 * @param String syntaxFrom The input syntax.
	 * @param String syntaxName The output syntax.
	 * @return String
	 */
	private static String findDictionnary ( String syntaxFrom, String syntaxName )
	{
		String dictionnary = null;
		String syntaxTo    = null;
		Translator traduc  = Translator.create(Config.getResourcesPath() + "/schema-converter/dictionary.xml");

		String[] dictionnaries = traduc.getAvailableDictionnaries();

		for (int i=0; i<dictionnaries.length && dictionnary == null; i++)
		{
			traduc.setSelectedDictionnary(dictionnaries[i]);
			String[] srcSyntaxes = traduc.getSourceSyntaxes();
			String[] dstSyntaxes = traduc.getDestinationSyntaxes();

			for (int j=0; j<srcSyntaxes.length && syntaxTo == null; j++)
			{
				String srcSyntax = srcSyntaxes[j];
				if (!srcSyntax.equals(syntaxFrom))
				{
					continue;
				}
				for (int k=0; k<dstSyntaxes.length && syntaxTo == null; k++)
				{
					String dstSyntax = dstSyntaxes[j];
					if (dstSyntax.equals(syntaxName))
					{
						syntaxTo = dstSyntax;
						dictionnary = dictionnaries[i];
					}
				}
			}
		}

		return dictionnary;
	}

	/**
	 * Convert a schema.
	 * @param Schema schema The Schema object to convert.
	 * @param String dictionnary The dictionnary to use.
	 * @param String outSyntax The syntax used to convert the schema.
	 * @return Schema
	 */
	private static Schema convertSchema ( Schema schema, String dictionnary, String outSyntax )
	{
		Translator traduc = Translator.create(Config.getResourcesPath() + "/schema-converter/dictionary.xml");
		SchemaConverter converter = new SchemaConverter(schema, traduc);
		try
		{
			converter.convertTo(dictionnary, outSyntax);
		}
		catch (Exception e)
		{
			return null;
		}
		return schema;
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
		h.printHelp("alascaconv", options, true);
	}

	/**
	 * Launch this tool.
	 */
	public static void main ( String[] args )
	{
		String inFile    = null;
		String inSyntax  = null;
		String outFile   = null;
		String outSyntax = null;
		boolean clear    = false;
		boolean list     = false;

		//
		// Parsing options.
		//
		CommandLineParser parser = new GnuParser();
		try
		{
			CommandLine cmdOptions = parser.parse(options, args);
			inFile    = cmdOptions.getOptionValue("i");
			inSyntax  = cmdOptions.getOptionValue("I");
			outFile   = cmdOptions.getOptionValue("o");
			outSyntax = cmdOptions.getOptionValue("O");
			if (cmdOptions.hasOption("c"))
			{
				clear = true;
			}
			if (cmdOptions.hasOption("l"))
			{
				list = true;
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
		if (list)
		{
			displayAvailableSyntaxes();
			System.exit(0);
		}

		//
		// Launch schema.
		//
		String dictionnary = findDictionnary(inSyntax, outSyntax);
		if (dictionnary == null)
		{
			System.out.println("Can't find valid translation dictionnary");
			System.exit(1);
		}
		SchemaSyntax inSchemaSyntax = createSchemaSyntax(inSyntax);
		if (inSchemaSyntax == null)
		{
			System.out.println("Unknow input syntax (" + inSyntax + ")");
			System.exit(1);
		}
		SchemaSyntax outSchemaSyntax = createSchemaSyntax(outSyntax);
		if (outSchemaSyntax == null)
		{
			System.out.println("Unknow output syntax (" + outSyntax + ")");
			System.exit(1);
		}
		Schema inSchema = createSchema(inSchemaSyntax, inFile);
		if (inSchema == null)
		{
			System.out.println("Failed to read input schema file (" + inFile + ")");
			System.exit(1);
		}

		//
		// Convert schema.
		//
		Schema outSchema = convertSchema(inSchema, dictionnary, outSyntax);
		if (outSchema == null)
		{
			System.out.println("Failed to convert input schema");
			System.exit(1);
		}
		if (clear)
		{
			outSchema.setProperties(new Properties());
		}

		//
		// Write schema.
		//
		SchemaFileWriter schemaWriter = outSchemaSyntax.createSchemaWriter();
		SchemaFile schemaFile = new SchemaFile( outFile, null, schemaWriter );
		schemaFile.setSchema( outSchema );
		if (!schemaFile.write())
		{
			System.out.println("Failed to write output schema file");
			System.exit(1);
		}

		System.exit(0);
	}

}
