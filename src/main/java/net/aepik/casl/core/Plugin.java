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

import java.lang.Comparable;
import java.lang.Runnable;
import javax.swing.JFrame;

/**
 * Cette classe dÃ©finie les mÃ©thodes que les plugins doivent implÃ©menter
 * pour Ãªtre pris en compte par l'application.
 * <br/>
 * Un plugin est instancier par l'application, sans lui passer aucun paramÃªtre.
**/

public interface Plugin extends Runnable, Comparable<Plugin> {

	/**
	 * Indique si le plugin peut-Ãªtre Ã©xÃ©cutÃ©.
	 * @return boolean True si c'est le cas, false sinon.
	**/
	public boolean canRun();

	/**
	 * Compare ce plugin Ã  un autre.
	 * @return int Le retour habituel de la methode compareTo.
	**/
	public int compareTo( Plugin p );

	/**
	 * Retourne une catÃ©gorie.
	 * @return String Une catÃ©gorie.
	**/
	public String getCategory();

	/**
	 * Retourne une description du plugin.
	 * @return String Une description.
	**/
	public String getDescription();

	/**
	 * Retourne le nom du plugin.
	 * @return String Un nom sous forme de chaÃ®ne de caractÃ¨res.
	**/
	public String getName();

	/**
	 * Retourne la version du plugin.
	 * @return String Une version.
	**/
	public String getVersion();

	/**
	 * Indique la fenÃªtre par rapport Ã  laquelle l'Ã©ventuelle fenÃªtre
	 * du plugin doit se positionner.
	 * @param f Un objet de type JFrame.
	**/
	public void setRelativeTo( JFrame f );

	/**
	 * Passe un manager de schÃ©mas en paramÃªtre.
	 * @param m Le manager de schÃ©mas.
	**/
	public void setSchemaManager( SchemaManager m );

}
