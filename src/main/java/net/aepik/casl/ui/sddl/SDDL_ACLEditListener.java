/*
 * SchemaListener.java		0.1		08/06/2006
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


package net.aepik.casl.ui.sddl;

import net.aepik.casl.core.sddl.SDDL_ACLString;
import net.aepik.casl.ui.sddl.SDDL_ACLFrame;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * Cette classe est un écouteur qui va permettre d'associer une ouverture
 * de fenêtre pour configurer une ACL de language SDDL.
**/

public class SDDL_ACLEditListener implements ActionListener {

////////////////////////////////
// Attributs
////////////////////////////////

	private Window parent ;
	private JTextField value ;
	private JButton button ;

////////////////////////////////
// Constructeur
////////////////////////////////

	public SDDL_ACLEditListener( Window parent, JTextField value, JButton editButton ) {

		this.parent = parent ;
		this.value = value ;
		this.button = editButton ;
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Gère les actions de la vue et permet de modifier les données.
	 * @param e L'action soulevée par un élément du panel.
	**/
	public void actionPerformed( ActionEvent e ) {

		Object o = e.getSource();
		if( o==button ) {

			SDDL_ACLString acl = new SDDL_ACLString();
			if( acl.initFromString( value.getText() ) ) {
				SDDL_ACLFrame frame = new SDDL_ACLFrame( parent, acl, value );
				frame.setVisible( true );
			} else {
				SDDL_ACLFrame frame = new SDDL_ACLFrame( parent, null, value );
				frame.setVisible( true );
			}
		}

	}

}