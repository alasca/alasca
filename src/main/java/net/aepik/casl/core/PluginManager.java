/*
 * PluginsManager.java		0.1		20/06/2006
 * 
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

package net.aepik.casl.core;

import java.io.File ;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.*;

/**
 * Un objet PluginsManager gÃ¨re l'ensemble des plugins de l'application.
 * Ce manager permet d'Ã©xÃ©cuter les plugins, de les lister, de les instancier
 * en mÃ©moire.
**/

public class PluginManager {

////////////////////////////////
// Constantes
////////////////////////////////

	/** Le nom de l'interface de plugin dans le paquetage. **/
	public final static String PLUGIN_INTERFACE_NAME = "net.aepik.casl.core.Plugin" ;

////////////////////////////////
// Attributs
////////////////////////////////

	/** Le manager de l'application **/
	private Manager manager ;
	/** Le chemin du rÃ©pertoire contenant tous les plugins **/
	private String path ;
	/** L'ensemble des plugins **/
	private Plugin[] plugins ;
	private Plugin[] pluginsAlpha ;
	/** L'ensemble des noms de jarFile **/
	private File[] pluginsFiles ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public PluginManager( Manager m, String path ) {
		manager = m;
		this.path = path ;
		this.plugins = new Plugin[0];
		this.pluginsAlpha = new Plugin[0];
	}

////////////////////////////////
// MÃ©thodes publiques
////////////////////////////////

	/**
	 * Retourne le manager de l'application.
	 * @return Manager Le manager de l'application.
	**/
	public Manager getManager() { return manager; }

	/**
	 * Retourne l'ensemble des plugins.
	 * @return String[] L'ensemble des noms de plugins.
	**/
	public Plugin[] getPlugins() { return pluginsAlpha; }

	/**
	 * Trouve les plugins dans le rÃ©pertoire des plugins.
	 * Pour accÃ©der aux plugins, il faut appeler la mÃ©thode getPlugins().
	 * @return boolean True si le chargement des plugins a rÃ©ussi, false sinon.
	**/
	public boolean loadPlugins() {

		// On teste si le rÃ©pertoire des plugins existe. Si ca n'est pas
		// le cas, on ne va pas plus loin.

		File pluginsDir = new File( path );
		if( !pluginsDir.exists() )
			return false ;

		// On rÃ©cupÃ¨re la liste des fichiers contenus dans le rÃ©pertoire
		// des plugins. Puis on parcours cette liste pour charger chaque
		// plugin. La liste des plugins correctement chargÃ©s est dans le
		// vecteur pluginsVector.

		pluginsFiles = pluginsDir.listFiles();
		Vector<Plugin> pluginsVector = new Vector<Plugin>();

		for( int i=0; pluginsFiles!=null && i<pluginsFiles.length; i++ ) {

			// On teste si le plugin est uniquement un fichier .jar
			// Si ca n'est pas le cas, on passe au suivant.

			String pluginName = pluginsFiles[i].getName();
			if( pluginsFiles[i].isDirectory()
					|| pluginName.length()<=4
					|| !pluginName.substring(
						pluginName.length()-4 ).equals( ".jar" ) )
				continue;

			// L'Ã©tape suivante consiste donc Ã  charger le plugin
			// dynamiquement dans la JVM Java Ã  l'aide de la classe
			// URLClassLoader. A la moindre erreur de chargement,
			// le plugin ne sera pas gardÃ©.

			boolean ok = false ;
			try {

				File pluginFile = pluginsFiles[i];
				URL pluginURL = pluginFile.toURL();
				URLClassLoader loader = new URLClassLoader( new URL[]{ pluginURL } );
	
				// On charge chaque maintenant le fichier en mÃ©moire. Par la
				// suite, on va crÃ©er l'instance et la stocker dans le
				// vecteur de plugin.

				JarFile jar = new JarFile( pluginFile );
				Enumeration<JarEntry> jarFiles = jar.entries();
				Class pluginMainClass = null ;

				while( !ok && jarFiles.hasMoreElements() ) {
					JarEntry jarFile = jarFiles.nextElement();

					// Pour chaque fichier dans le jar, on regarde si c'est un
					// fichier de classe Java.

					if( jarFile.getName().length()>6
							&& jarFile.getName().substring(
								jarFile.getName().length()-6 ).equals( ".class" ) ) {

						// Et que cette classe implÃ©mente l'interface Plugin.
						// Pour ca, on rÃ©cupÃ¨re tout d'abord le nom complet de
						// la classe, puis les interfaces qu'elle implÃ©mente
						// en la chargeant dynamiquement.

						String className = jarFile.getName().substring( 0, jarFile.getName().length()-6 );
						className = className.replace( '/', '.' );

						Class classTmp = Class.forName( className, false, loader );
						Class<?> classSuperclass = classTmp.getSuperclass();
						Class[] classInterfaces = classSuperclass.getInterfaces();

						for( int j=0; classInterfaces!=null && !ok && j<classInterfaces.length; j++ ) {
							if( classInterfaces[j].getName().equals( PLUGIN_INTERFACE_NAME ) ) {
								ok = true ;
								pluginMainClass = classTmp ;
							}
						}
					}
				}

				jar.close();

				// Si ok est true, cela veut dire que le plugin est correct.
				// On l'ajoute donc Ã  la liste des plugins, aprÃ¨s l'avoir
				// instanciÃ©.
	
				if( ok ) {
					pluginsVector.add( (Plugin) pluginMainClass.newInstance() );
				}

			} catch( Exception debug ) {
				ok = false;
			}
		}

		if( pluginsVector.size()==0 )
			return false ;

		// On remet tout dans le tableau fixe des plugins.
		// Seul les plugins correctement chargÃ©s sont prÃ©sents.

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
