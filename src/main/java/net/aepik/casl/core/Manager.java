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

package net.aepik.casl.core;

import net.aepik.casl.Version;
import net.aepik.casl.core.util.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Permet de gérer les différents composants de l'application.
 * Pilote le manager de schéma et le manager de plugins.
 */

public class Manager
{

	/**
	 * Manager properties.
	 */
	private Properties properties;

	/**
	 * Schema manager.
	 */
	private SchemaManager schemas;

	/**
	 * Plugin manager.
	 */
	private PluginManager plugins;

	/**
	 * Update is available or not.
	 * @var boolean
	 */
	private boolean updateAvailable;

	/**
	 * Create a new Manager object.
	 * @param String configFile The XML configuration file.
	 */
	public Manager () throws IOException
	{
		this.schemas = new SchemaManager(this);
		this.plugins = new PluginManager(this, Config.getPluginPath());
		this.properties = new Properties();
		this.updateAvailable = false;
		this.loadProperties();
	}

	/**
	 * Retourne le manager de plugins.
	 * @return PluginManager Le manager de plugins.
	 */
	public PluginManager getPluginManager ()
	{
		return this.plugins;
	}

	/**
	 * Retourne la valeur de la propriÃ©tÃ© de clef key.
	 * @param key Une clef.
	 * @return String La valeur correspondant Ã  la clef.
	 */
	public String getProperty (String key)
	{
		if (key != null)
		{
			return this.properties.getProperty(key);
		}
		return null;
	}

	/**
	 * Retourne le manager de schÃ©mas.
	 * @return SchemaManager Le manager de schÃ©mas de cette classe.
	 */
	public SchemaManager getSchemaManager ()
	{
		return this.schemas;
	}

	/**
	 * Return if there is update.
	 * @return boolean
	 */
	public boolean getUpdateAvailable ()
	{
		return this.updateAvailable;
	}

	/**
	 * Load plugins.
	 */
	public void loadPluginManager ()
	{
		File pluginDir = new File(Config.getPluginPath());
		if (pluginDir.exists())
		{
			plugins.loadPlugins();
			for (Plugin plugin : plugins.getPlugins())
			{
				plugin.setSchemaManager(schemas);
			}
		}
	}

	/**
	 * Load properties.
	 * @param String configFile The XML configuration file path.
	 */
	private void loadProperties ()
	{
		this.properties = new Properties();
		this.properties.setProperty("FrameTitle", Version.getProjectName() + " " + Version.getVersion());
		this.properties.setProperty("FrameStatus", "");
		this.properties.setProperty("IconFile", Config.getResourcesPath() + "/casl.png");
		this.properties.setProperty("PluginDir", Config.getPluginPath());
		this.properties.setProperty("Version", Version.getVersion());
	}

       /**
	 * Set that an update is available.
	 * @param boolean True or false
	 */
	public void setUpdateAvailable ( boolean b )
	{
		this.updateAvailable = b;
	}

}

