/*
 * SchemaValueEditorLauncher.java		0.1		05/07/2006
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

package net.aepik.casl.ui.ldap;

import net.aepik.casl.core.ldap.SchemaObject;
import net.aepik.casl.core.ldap.SchemaValue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Permet de lancer une fenêtre d'édition en cliquant sur un bouton.
 * C'est une sorte de listener qui ne fait qu'afficher une fenêtre.
 * Dans tous les cas, il faut passer un JTextField, la fenêtre d'édition
 * y déposera son résultat.
**/

public class SchemaValueEditorLauncher
		implements
			ActionListener,
			WindowListener
{

////////////////////////////////
// Attributs
////////////////////////////////

	private JButton button ;
	private JTextField composant ;

	private String keyValue ;
	private SchemaObject objet ;
	private SchemaValueEditorFrame fenetre ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public SchemaValueEditorLauncher(
			JButton button,
			JTextField composant,
			SchemaObject objet,
			String keyValue )
	{

		this.button = button ;
		this.composant = composant ;

		this.keyValue = keyValue ;
		this.objet = objet ;
		this.fenetre = null ;
	}

////////////////////////////////
// Méthodes publiques
////////////////////////////////

	public void actionPerformed( ActionEvent e ) {

		Object o = e.getSource();
		SchemaValue tmp = objet.getSyntax().createSchemaValue(
				objet.getType(),
				keyValue,
				composant.getText() );

		if( fenetre==null && o==button ) {
			fenetre = new SchemaValueEditorFrame( tmp );
			fenetre.addValidationListener( this );
			fenetre.setVisible( true );

		} else if( fenetre!=null && o instanceof JButton ) {

			String[] values = fenetre.getFinalValues();
			if( values.length==0 ) {
				composant.setText( "" );
				composant.repaint();

			} else if( tmp.setValues( values ) ) {
				composant.setText( tmp.getValue() );
				composant.repaint();
			}

			windowClosing( null );
		}
	}

	public void windowActivated( WindowEvent e ) {}
	public void windowClosed( WindowEvent e ) {}
 	public void windowClosing( WindowEvent e ) {
 		fenetre.dispose();
 		fenetre = null ;
 	}
	public void windowDeactivated( WindowEvent e ) {}
	public void windowDeiconified( WindowEvent e ) {}
	public void windowIconified( WindowEvent e ) {}
	public void windowOpened( WindowEvent e ) {}

}
