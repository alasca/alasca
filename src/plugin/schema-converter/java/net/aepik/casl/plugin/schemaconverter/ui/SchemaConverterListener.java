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
 
 
package net.aepik.casl.plugin.schemaconverter.ui;

import net.aepik.casl.plugin.schemaconverter.core.SchemaConverter;
import net.aepik.casl.plugin.schemaconverter.core.Translator;
import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaFile;
import net.aepik.casl.core.ldap.SchemaFileWriter;
import net.aepik.casl.core.SchemaManager;
import net.aepik.casl.core.ldap.SchemaSyntax;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SchemaConverterListener
		 implements
		 	ActionListener,
		 	WindowListener {

////////////////////////////////
// Attributs
////////////////////////////////

	/** Le convertisseur **/
	private SchemaConverter converter ;
	/** La fenêtre pour ce convertisseur **/
	private SchemaConverterFrame converterFrame ;

////////////////////////////////
// Constructeurs
////////////////////////////////	

	public SchemaConverterListener( SchemaConverter c, SchemaConverterFrame cf ) {

		converter = c;
		converterFrame = cf;
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	public void actionPerformed( ActionEvent e ) {

		Object o = e.getSource();

		// On récupère toutes les infos importantes : nom de fichier et syntaxe.
		// On créé dynamiquement une nouvelle instance de la syntaxe.
		// On ouvre le fichier et on le parcourt avec la syntaxe.
		// Si le fichier est chargé correctement, on l'ajoute à la vue
		// principale et on ferme cette fenêtre.
		if( o==converterFrame.boutonOk ) {
			try {

				String dictionnary = converterFrame.getSelectedDictionnaryName();
				String syntax = converterFrame.getSelectedSyntaxName();

				converter.convertTo( dictionnary, syntax );
				windowClosing( null );


				JOptionPane.showMessageDialog(
						converterFrame,
						"La conversion du schéma s'est effectuée avec succès.",
						"Succès",
						JOptionPane.INFORMATION_MESSAGE );

			// Erreur lors de la lecture du fichier.					
			} catch( Exception ex ) {
				JOptionPane.showMessageDialog( converterFrame, "Erreur de conversion.", "Erreur", JOptionPane.ERROR_MESSAGE );
			}

		// Bouton suivant
		} else if( o==converterFrame.boutonSuivant ) {
			converterFrame.switchToNextPanel();

		// Bouton précédent
		} else if( o==converterFrame.boutonPrecedent ) {
			converterFrame.switchToPreviousPanel();

		// On annule : toutes les informations sont perdues.
		} else if( o==converterFrame.boutonAnnuler ) {
			windowClosing( null );
		}
	}

	// Actions liées à la fenêtre
	public void windowActivated( WindowEvent e ) {}
	public void windowClosed( WindowEvent e ) {}
 	public void windowClosing( WindowEvent e ) { converterFrame.setVisible( false ); }
	public void windowDeactivated( WindowEvent e ) {}
	public void windowDeiconified( WindowEvent e ) {}
	public void windowIconified( WindowEvent e ) {}
	public void windowOpened( WindowEvent e ) {}

}
