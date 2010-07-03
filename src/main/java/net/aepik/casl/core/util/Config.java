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
 * along with Config program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */


package net.aepik.casl.core.util;

import java.io.File;
import java.util.Map;

public class Config
{

	public static final String libraryPathEnvName = "CASL_LIB_PATH";

	public static final String pluginPathEnvName = "CASL_PLUGIN_PATH";

	public static final String resourcesPathEnvName = "CASL_RESOURCES_PATH";

	private static String libraryPath = null;

	private static String pluginPath = null;

	private static String resourcesPath = null;

	static
	{
		readEnvironment();
	}

	public static String getLibraryPath ()
	{
		return Config.libraryPath;
	}

	public static String getPluginPath ()
	{
		return Config.pluginPath;
	}

	public static String getResourcesPath ()
	{
		return Config.resourcesPath;
	}

	public static void readEnvironment ()
	{
		String envLibraryPath = null;
		String envPluginPath = null;
		String envResourcesPath = null;
		Map<String, String> env = System.getenv();
		for (String envName : env.keySet())
		{
			if (envName.equals(libraryPathEnvName))
			{
				envLibraryPath = env.get(envName);
			}
			if (envName.equals(pluginPathEnvName))
			{
				envPluginPath = env.get(envName);
			}
			if (envName.equals(resourcesPathEnvName))
			{
				envResourcesPath = env.get(envName);
			}
		}
		if (envLibraryPath != null)
		{
			Config.libraryPath = envLibraryPath;
		}
		else
		{
			try
			{
				File jarFile = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath());
				Config.libraryPath = jarFile.getParent();
			}
			catch (Exception e)
			{
				Config.libraryPath = "./lib";
			}
		}
		if (envPluginPath != null)
		{
			Config.pluginPath = envPluginPath;
		}
		else if (libraryPath != null)
		{
			Config.pluginPath = libraryPath + "/plugin";
		}
		if (envResourcesPath != null)
		{
			Config.resourcesPath = envResourcesPath;
		}
		else if (libraryPath != null)
		{
			Config.resourcesPath = libraryPath + "/resources";
		}
	}

}
