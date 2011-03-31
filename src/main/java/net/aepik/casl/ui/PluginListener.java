/*
 * PluginListener.java		0.1		20/06/2006
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


package net.aepik.casl.ui;

import net.aepik.casl.core.Plugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Cet écouteur permet de lancer un plugin à partir d'un bouton.
 * Il est utilisé principalement dans la barre de menu.
**/

public class PluginListener implements ActionListener {

////////////////////////////////
// Attributs
////////////////////////////////

	/** Le plugin à prendre en compte **/
	private Plugin plugin ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public PluginListener( Plugin p ) {
		plugin = p;
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Gère le démarrage du plugin.
	 * @param e L'action soulevée par un clic de souris.
	**/
	public void actionPerformed( ActionEvent e ) {
		(new Thread( plugin )).start();
	}
}