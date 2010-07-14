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


package net.aepik.casl.plugin.schemaconverter;

import net.aepik.casl.plugin.schemaconverter.core.SchemaConverter;
import net.aepik.casl.plugin.schemaconverter.core.Translator;
import net.aepik.casl.plugin.schemaconverter.ui.SchemaConverterFrame;
import net.aepik.casl.plugin.schemaconverter.ui.SchemaConverterListener;
import net.aepik.casl.core.PluginImpl;
import net.aepik.casl.core.util.Config;
import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.SchemaManager;
import net.aepik.casl.core.ldap.SchemaSyntax;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class SCPlugin extends PluginImpl
{

	/**
	 * Indicates if this plugin is runnable.
	 * @return boolean
	 */
	public boolean canRun ()
	{
		boolean result = false ;
		try
		{
			String schemaId = schemaManager.getCurrentSchemaId();
			if (schemaId != null)
			{
				result = true;
			}
		}
		catch (Exception e) {}
		return result;
	}

	/**
	 * Retourne une catégorie.
	 * @return String Une catégorie.
	 */
	public String getCategory ()
	{
		return "";
	}

	/**
	 * Retourne une description du plugin.
	 * @return String Une description.
	 */
	public String getDescription ()
	{
		return "Cet outil permet de convertir un schéma dans une autre syntaxe."
			+ " Il prend en compte les syntaxes fournies par CASL et utilise"
			+ " un dictionnaire de traduction à base de XML.";
	}

	/**
	 * Retourne le nom du plugin.
	 * @return String Un nom sous forme de chaîne de caractères.
	 */
	public String getName ()
	{
		return "Convertisseur de schéma";
	}

	/**
	 * Retourne la version du plugin.
	 * @return String Une version.
	 */
	public String getVersion ()
	{
		return "1.1.0";
	}

	/**
	 * Permet de lancer l'application.
	 */
	public void run ()
	{
		Translator traduc = Translator.create(Config.getResourcesPath() + "/traduc.xml");
		if (traduc != null && this.canRun())
		{
			try
			{
				String schemaId = schemaManager.getCurrentSchemaId();
				Schema currentSchema = schemaManager.getCurrentSchema();
				SchemaConverter c = new SchemaConverter(currentSchema, traduc);
				SchemaConverterFrame cf = new SchemaConverterFrame(parentFrame, this, c, schemaId);
				SchemaConverterListener cl = new SchemaConverterListener(c, cf);
				cf.addConverterListener(cl);
				cf.setVisible(true);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(
					parentFrame,
					"Erreur d'éxécution",
					"Erreur",
					JOptionPane.ERROR_MESSAGE
				);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(
				parentFrame,
				"Impossible de charger le dictionnaire de données",
				"Erreur",
				JOptionPane.ERROR_MESSAGE
			);
		}
	}

}
