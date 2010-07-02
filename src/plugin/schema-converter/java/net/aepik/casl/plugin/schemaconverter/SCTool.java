/*
 * Copyright (C) 2006 Thomas Chemineau
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


package net.aepik.casl.plugin.schemaconverter;

import net.aepik.casl.plugin.schemaconverter.core.SchemaConverter;
import net.aepik.casl.plugin.schemaconverter.core.Translator;
import net.aepik.casl.plugin.schemaconverter.ui.SchemaConverterFrame;
import net.aepik.casl.plugin.schemaconverter.ui.SchemaConverterListener;
import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaManager;
import net.aepik.casl.core.ldap.SchemaSyntax;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class SCTool {

	private static Options options = new Options();
	static {
		options.addOption("in", true, "Input file");
		options.addOption("insyntax", true, "Input schema syntax");
		options.addOption("out", true, "Output file");
		options.addOption("outsyntax", true, "Output schema syntax");
	}

	private static SchemaSyntax createSchemaSyntax ( String syntaxName )
	{
		SchemaSyntax syntax = null;
		try
		{
			String syntaxClass = Schema.getSyntaxPackageName() + "." + syntaxName;
			syntax = ((Class<SchemaSyntax>) Class.forName(syntaxClass)).newInstance();
		}
		catch (Exception e){};
		return syntax;
	}

	private static Schema createSchema ( SchemaSyntax syntax, String fileName )
	{
		Schema schema = null;
		try
		{
			schema = Schema.create(syntax, fileName);
		}
		catch (Exception e){};
		return schema;
	}

	private static Schema convertSchema ( Schema schema, String syntaxName )
	{
		String dictionnary = null;
		String syntaxTo    = null;
		String syntaxFrom  = schema.getSyntax().toString();
		Translator traduc  = Translator.create("./lib/resources/traduc.xml");

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

		System.out.println(dictionnary);
		System.out.println(syntaxTo);

		return schema;
	}

	public static void main ( String[] args )
	{
		String inFile    = null;
		String inSyntax  = null;
		String outFile   = null;
		String outSyntax = null;

		//
		// Parsing options.
		//
		CommandLineParser parser = new GnuParser();
		try
		{
			CommandLine cmdOptions = parser.parse(options, args);
			if (cmdOptions.hasOption("in"))
			{
				inFile = cmdOptions.getOptionValue("in");
			}
			if (cmdOptions.hasOption("insyntax"))
			{
				inSyntax = cmdOptions.getOptionValue("inSyntax");
			}
			if (cmdOptions.hasOption("out"))
			{
				outFile = cmdOptions.getOptionValue("out");
			}
			if (cmdOptions.hasOption("outsyntax"))
			{
				outSyntax = cmdOptions.getOptionValue("outSyntax");
			}
			if (cmdOptions.getOptions().length != 4)
			{
				System.out.println("Wrong number of arguments");
				System.exit(2);
			}
		}
		catch (ParseException e)
		{
			System.out.println("Wrong arguments");
			System.exit(2);
		}

		//
		// Launch schema.
		//
		/*try
		{*/
			SchemaSyntax inSchemaSyntax = createSchemaSyntax(inSyntax);
			Schema inSchema = createSchema(inSchemaSyntax, inFile);
			Schema outSchema = convertSchema(inSchema, outSyntax);
		/*}
		catch (Exception e)
		{
			System.out.println("Unable to convert schema");
			System.exit(1);
		}*/

	}

}
