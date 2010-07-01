/*
 * Plugin.java		0.2		19/06/2006
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

import javax.swing.JFrame;
import java.lang.Runnable;

public abstract class PluginImpl implements Plugin {

////////////////////////////////
// Attributs
////////////////////////////////

	/** Le manager de schÃ©ma Ã  prendre en compte **/
	protected SchemaManager schemaManager ;
	/** La fenÃªtre mÃ¨re **/
	protected JFrame parentFrame ;

////////////////////////////////
// Methodes abstraites
////////////////////////////////

	/**
	 * Indique si le plugin peut-Ãªtre Ã©xÃ©cutÃ©.
	 * @return boolean True si c'est le cas, false sinon.
	**/
	public abstract boolean canRun();

	/**
	 * Retourne une catÃ©gorie.
	 * @return String Une catÃ©gorie.
	**/
	public abstract String getCategory();

	/**
	 * Retourne une description du plugin.
	 * @return String Une description.
	**/
	public abstract String getDescription();

	/**
	 * Retourne le nom du plugin.
	 * @return String Un nom sous forme de chaÃ®ne de caractÃ¨res.
	**/
	public abstract String getName();

	/**
	 * Permet de lancer le plugin dans un thread particulier.
	**/
	public abstract void run();

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Compare ce plugin Ã  un autre.
	 * @return int Le retour habituel de la methode compareTo.
	**/
	public int compareTo( Plugin p ) {
		if( p!=null )
			return this.toString().compareTo( p.toString() );
		return 1 ;
	}

	/**
	 * Indique la fenÃªtre par rapport Ã  laquelle l'Ã©ventuelle fenÃªtre
	 * du plugin doit se positionner.
	 * @param f Un objet de type JFrame.
	**/
	public void setRelativeTo( JFrame f ) { parentFrame = f ; }

	/**
	 * Passe un manager de schÃ©mas en paramÃªtre.
	 * @param m Le manager de schÃ©mas.
	**/
	public void setSchemaManager( SchemaManager m ) { schemaManager = m; }

	/**
	 * Cet objet sous forme de chaÃ®ne de caractÃ¨re.
	 * @return String Une chaÃ®ne de caractÃ¨res.
	**/
	public String toString() { return getName(); }

}
