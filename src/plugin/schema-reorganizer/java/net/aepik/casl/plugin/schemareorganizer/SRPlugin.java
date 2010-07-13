/*
 * SRPlugin.java		0.1		23/06/2006
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


package net.aepik.casl.plugin.schemareorganizer;

import net.aepik.casl.core.History;
import net.aepik.casl.core.PluginImpl;
import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaObject;
import net.aepik.casl.core.ldap.SchemaManager;
import net.aepik.casl.core.ldap.SchemaSyntax;
import net.aepik.casl.core.ldap.SchemaValue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class SRPlugin extends PluginImpl {

	/**
	 * Indique si le plugin peut-être éxécuté.
	 * @return boolean True si c'est le cas, false sinon.
	**/
	public boolean canRun() {
		
		boolean result = false ;

		try {
			String schemaId = schemaManager.getCurrentSchemaId();
			if( schemaId!=null )
				result = true ;
		} catch( Exception e ) {}

		return result ;
	}

	/**
	 * Retourne une catégorie.
	 * @return String Une catégorie.
	**/
	public String getCategory() {
		return "Organisation";
	}

	/**
	 * Retourne une description du plugin.
	 * @return String Une description.
	**/
	public String getDescription() {
		return "Ce plugin réorganise le schéma en plaçant les objets par ordre"
				+ " de déclaration, c'est à dire qu'ils seront désormais"
				+ " déclarés avant d'être utilisés par d'autres objets.";
	}

	/**
	 * Retourne le nom du plugin.
	 * @return String Un nom sous forme de chaîne de caractères.
	**/
	public String getName() {
		return "Réorganisateur de schéma";
	}

	/**
	 * Retourne la version du plugin.
	 * @return String Une version.
	**/
	public String getVersion() { return "1.0.0"; }

	/**
	 * Permet de lancer l'application.
	**/
	public void run() {

		if( canRun() ) {

			String id = schemaManager.getCurrentSchemaId();
			String texte = "Réorganiser le schéma consiste à restructurer l'ordre d'agencement\n"
					+ "des objets dans le schéma. Pour être plus précis, il s'agit de placer\n"
					+ "les objets par ordre, de tel façon que les objets soient déclarés avant\n"
					+ "d'être appelés par d'autres objets. Cette réorganisation n'affecte le\n"
					+ "schéma que lors de son écriture dans un fichier.\n\n"
					+ "Etes vous sûr de vouloir réorganiser le schéma maintenant\n"
					+ id + " ?";

			int result = JOptionPane.showConfirmDialog(
					parentFrame,
					texte,
					"Confirmation",
					JOptionPane.YES_NO_OPTION );

			// Si c'est approuvé, on lance la réorganisation du schéma.

			if( result==JOptionPane.YES_OPTION ) {

				// La réorganisation du schéma doit réécrire l'historique
				// de lecture du schéma. L'algorithme consiste à regarder,
				// pour chaque objet du schéma, si les objets qu'il peut/doit
				// contenir sont définis avant lui dans l'historique.

				Schema s = schemaManager.getCurrentSchema();
				SchemaObject[] objets = s.getObjectsInOrder();
				History history = s.getHistory();

				for( SchemaObject objet : objets ) {
					int historyPosition = history.getIndexOf( objet );
					SchemaValue[] valeurs = objet.getValues();

					for( SchemaValue valeur : valeurs ) {
						String[] svaleurs = valeur.getValues();

						for( String svaleur : svaleurs ) {
							SchemaObject o = null ;
							int oPosition = -1;

							if( SchemaSyntax.isNumericOid( svaleur ) ) {
								o = s.getObject( svaleur );
							} else if( SchemaSyntax.isKeyString( svaleur ) ) {
								o = s.getObjectByName( svaleur );
							}

							if( o!=null )
								oPosition = history.getIndexOf( o );

							if( oPosition>historyPosition ) {
								history.moveElement( historyPosition, oPosition );
								historyPosition = oPosition ;
							}
						}
					}
				}

				JOptionPane.showMessageDialog( parentFrame,
					"La structure du schéma " + id + "\na été réorganisée avec succès.",
					"Succès",
					JOptionPane.INFORMATION_MESSAGE );
				s.notifyUpdates(true);
			}
		}
	}

}
