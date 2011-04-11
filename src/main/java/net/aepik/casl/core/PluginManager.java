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

package net.aepik.casl.core;

import java.io.File ;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.*;

/**
 * Un objet PluginsManager gère l'ensemble des plugins de l'application.
 * Ce manager permet d'éxécuter les plugins, de les lister, de les instancier
 * en mémoire.
 */

public class PluginManager
{

	/*
	 * Le nom de l'interface de plugin dans le paquetage.
	 */
	public final static String PLUGIN_INTERFACE_NAME = "net.aepik.casl.core.Plugin";

	/**
	 * Le manager de l'application
	 */
	private Manager manager;

	/**
	 * Le chemin du répertoire contenant tous les plugins
	 */
	private String path;

	/**
	 * L'ensemble des plugins
	 */
	private Plugin[] plugins;

	/**
	 * L'ensemble des plugins triés par ordres alphabétique
	 */
	private Plugin[] pluginsAlpha;

	/**
	 * L'ensemble des noms de jarFile
	 */
	private File[] pluginsFiles;

	/**
	 * Build a new PluginManager object.
	 * @param m A Manager object.
	 * @param path A path.
	 */
	public PluginManager (Manager m, String path)
	{
		this.manager = m;
		this.path = path ;
		this.plugins = new Plugin[0];
		this.pluginsAlpha = new Plugin[0];
	}

	/**
	 * Build a Plugin instance.
	 * @param name The name of the instance.
	 * @return Plugin A Plugin object.
	 */
	public static Plugin createPluginInstance (String name)
	{
		if (name.length() <= 6 || !name.substring(name.length()-6).equals(".class"))
		{
			return null;
		}
		Class classTmp = Class.forName(
			name.substring(0, name.length()-6).replace('/', '.'),
			false,
			new URLClassLoader(new URL[]{
				file.toURI().toURL()
			})
		);
		Class[] interfaces = ((Class<?>) classTmp.getSuperclass()).getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
		{
			if (interfaces[i].getName().equals(PLUGIN_INTERFACE_NAME))
			{
				ok = true;
				pluginMainClass = classTmp;
			}
		}
	}

	/**
	 * Retourne le manager de l'application.
	 * @return Manager Le manager de l'application.
	 */
	public Manager getManager ()
	{
		return manager;
	}

	/**
	 * Retourne l'ensemble des plugins.
	 * @return String[] L'ensemble des noms de plugins.
	 */
	public Plugin[] getPlugins ()
	{
		return pluginsAlpha;
	}

	/**
	 * Trouve les plugins dans le répertoire des plugins.
	 * Pour accéder aux plugins, il faut appeler la méthode getPlugins().
	 * @return boolean True si le chargement des plugins a réussi, false sinon.
	 */
	public boolean loadPlugins ()
	{
		File pluginsDir = new File(path);
		if (!pluginsDir.exists())
		{
			return false;
		}
		Vector<Plugin> pluginsVector = new Vector<Plugin>();
		for (File file : pluginsDir.listFiles())
		{
			String filename = file.getName();
			if (file.isDirectory())
			{
				continue;
			}
			if (filename.length() <= 4 || !filename.substring(filename.length()-4).equals(".jar"))
			{
				continue;
			}
			try
			{
				boolean ok = false;
				Class pluginMainClass = null;
				JarFile jar = new JarFile(file);
				Enumeration<JarEntry> jarFiles = jar.entries();
				while (!ok && jarFiles.hasMoreElements())
				{
					JarEntry jarFile = jarFiles.nextElement();
					String jarName = jarFile.getName();
					if (jarName.length() <= 6 || !jarName.substring(jarName.length()-6).equals(".class"))
					{
						continue;
					}
					String className = jarName.substring(0, jarName.length()-6).replace('/', '.');
					Class classTmp = Class.forName(
						className,
						false,
						new URLClassLoader(new URL[]{
							file.toURI().toURL()
						})
					);
					Class[] interfaces = ((Class<?>) classTmp.getSuperclass()).getInterfaces();
					for (int i = 0; i < interfaces.length && !ok; i++)
					{
						if (interfaces[i].getName().equals(PLUGIN_INTERFACE_NAME))
						{
							ok = true;
							pluginMainClass = classTmp;
						}
					}
				}
				jar.close();
				if (ok)
				{
					pluginsVector.add((Plugin) pluginMainClass.newInstance());
				}
			}
			catch (Exception e)
			{
				ok = false;
			}
		}
		if (pluginsVector.size() == 0)
		{
			return false;
		}
		plugins = new Plugin[ pluginsVector.size() ];
		pluginsAlpha = new Plugin[ plugins.length ];
		int compteur = 0;
		for( Enumeration<Plugin> e = pluginsVector.elements(); e.hasMoreElements(); ) {
			plugins[ compteur ] = e.nextElement();
			pluginsAlpha[ compteur ] = plugins[ compteur ];
			compteur++;
		}

		Arrays.sort( pluginsAlpha );

		return true ;
	}

}
