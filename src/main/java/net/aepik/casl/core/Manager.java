/*
 * Manager.java		0.1		20/06/2006
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

import net.aepik.casl.core.ldap.SchemaManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Permet de gÃ©rer les diffÃ©rents composants de l'application.
 * Pilote le manager de schÃ©ma et le manager de plugins, charge
 * les paramÃªtres de configuration depuis un fichier XML.
**/

public class Manager {

////////////////////////////////
// Attributs
////////////////////////////////

	/** Les propriÃ©tÃ©s du manager **/
	private Properties properties ;

	/** Le gestionnaire de schÃ©ma **/
	private SchemaManager schemas ;
	/** Le gestionnaire de plugins **/
	private PluginManager plugins ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public Manager( String configFile ) throws IOException {

		properties = new Properties();
		schemas = new SchemaManager( this );

		if( loadProperties( configFile ) ) {
			plugins = new PluginManager( this, properties.getProperty( "PluginDir" ) );
		}
	}

////////////////////////////////
// MÃ©thodes publiques
////////////////////////////////

	/**
	 * Retourne le manager de plugins.
	 * @return PluginManager Le manager de plugins.
	**/
	public PluginManager getPluginManager() { return plugins ; }

	/**
	 * Retourne la valeur de la propriÃ©tÃ© de clef key.
	 * @param key Une clef.
	 * @return String La valeur correspondant Ã  la clef.
	**/
	public String getProperty( String key ) {

		if( key!=null )
			return properties.getProperty( key );
		return null;
	}

	/**
	 * Retourne le manager de schÃ©mas.
	 * @return SchemaManager Le manager de schÃ©mas de cette classe.
	**/
	public SchemaManager getSchemaManager() { return schemas; }

	/**
	 * Charge les plugins en mÃ©moire.
	**/
	public void loadPluginManager() {

		if( properties.getProperty( "PluginDir" )!=null ) {
			plugins.loadPlugins();

			Plugin[] tab = plugins.getPlugins();
			for( int i=0; tab!=null && i<tab.length; i++ )
				tab[i].setSchemaManager( schemas );
		}
	}

////////////////////////////////
// MÃ©thodes privÃ©es
////////////////////////////////

	/**
	 * Charge les paramÃªtres de l'application en mÃ©moire.
	**/
	private boolean loadProperties( String configFile ) throws IOException {

		try {
			FileInputStream in = new FileInputStream( new File( configFile ) );
			properties.loadFromXML( in );
			in.close();
			return true ;

		} catch( InvalidPropertiesFormatException e ) {
			throw new IOException( "Error loading configuration file:\nFormat error [" + e + "]" );
		} catch( Exception e ) {
			e.printStackTrace();
			throw new IOException( "Error loading configuration file:\nGeneral error [" + e + "]" );
		}

		//return false ;
	}
}
