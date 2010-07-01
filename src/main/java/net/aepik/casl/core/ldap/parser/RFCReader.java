/*
 * RFCReader.java		0.1		13/06/2006
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


package net.aepik.casl.core.ldap.parser;

import net.aepik.casl.core.ldap.Schema;
import net.aepik.casl.core.ldap.SchemaFileReader;
import net.aepik.casl.core.ldap.SchemaObject;
import net.aepik.casl.core.ldap.SchemaSyntax;

import java.io.IOException;
import java.lang.StringBuffer;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

public class RFCReader extends SchemaFileReader {

////////////////////////////////
// Constructeurs
////////////////////////////////

	public RFCReader( SchemaSyntax syntax ) {
		super( syntax );
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Parcourt l'entrée et retourne l'ensemble des objets schéma lus.
	 * @retour Le schéma lu.
	**/
	public Schema read() throws IOException {

		if( input==null || syntax==null )
			return null ;

		Schema schema = new Schema( syntax );
		SchemaObject objet = null;
		boolean creationObjet = false;
		boolean initialisationObjet = false;
		boolean parametreObjet = false;
		Vector<SchemaObject> objets = new Vector<SchemaObject>();

		int nbOccurences = 0;
		int hauteurParenthese=0;
		StringBuffer buffer = new StringBuffer();
		StringBuffer bufferBackup = new StringBuffer();

		// Du a des problèmes sur la lecture de fichier entre les
		// différentes plateformes, on va lire le fichier ligne par
		// ligne, en laissant Java s'occuper des caractères de fin de ligne
		// et de fin de fichier.

		String ligne;
		while( ( ligne = input.readLine() )!=null && hauteurParenthese>=0 ) {

			int debut = 0;
			char[] chars = ligne.toCharArray();

			// On check si il y a 2 espaces à la suite en début de ligne.
			// Si oui, on saute le premier.
			if( chars.length>=1 && chars[0]==32 )
				debut++;

			for( int i=debut; i<chars.length && hauteurParenthese>=0; i++ ) {

				////////////////////////////////
				// On teste le caractère
				////////////////////////////////

				// Parenthèse ouvrante !
				// On rentre dans une définition de paramètres de l'objet.
				// On indique qu'il faut créer l'objet.
				if( chars[i]==40 ) {

					if( hauteurParenthese==0 ) {
						bufferBackup = buffer;
						buffer = new StringBuffer();
						parametreObjet = true;
						creationObjet = true;
					} else {
						buffer.append( chars[i] );
					}
					hauteurParenthese++;

				// Parenthèse fermante !
				// Si c'est une fin de définition de paramètres de l'objet,
				// on indique qu'il faut initialiser l'objet.
				} else if( chars[i]==41 ) {

					hauteurParenthese--;
					if( hauteurParenthese==0 ) {
						bufferBackup = buffer;
						buffer = new StringBuffer();
						parametreObjet = false;
						initialisationObjet = true;
					} else {
						buffer.append( chars[i] );
					}

				// On copie dans tous les autres cas.	
				} else
					buffer.append( chars[i] );

				////////////////////////////////
				// On teste le buffer
				////////////////////////////////

				// Ici, on test les occurences pour les propriétés du schéma.
				if( bufferBackup.toString().toLowerCase().startsWith( "dn:" ) ) {

					String str = bufferBackup.toString();
					int index = str.indexOf( ':' );
					String key = str.substring( 0, index ).trim();
					String value = str.substring( index+1 ).trim();
					schema.getProperties().setProperty( "dn", value );
				}

				// La définition d'un objet se fait obligatoirement en dehors
				// d'un quelconque parenthesage.
				if( creationObjet ) {

					if( syntax.isAttributeDefinitionHeader( bufferBackup.toString() ) )
						objet = syntax.createSchemaObject( syntax.getAttributeDefinitionType(), null );
					else if( syntax.isObjectDefinitionHeader( bufferBackup.toString() ) )
						objet = syntax.createSchemaObject( syntax.getObjectDefinitionType(), null );

					if( objet!=null ) {
						buffer = new StringBuffer();
						nbOccurences++;
					}

					creationObjet = false;

				// Si on est en creation d'objet et que l'objet n'est pas null,
				// on initialise cet objet à partir de ses paramêtres.
				} else if( initialisationObjet && objet!=null ) {

					if( objet.initFromString( bufferBackup.toString() ) ) {
						objets.add( objet );
						objet = null;

					// Une erreur est survenue, on annule tout.
					// Pour sortir de la boucle, on simule une erreur de parenthesage.
					}// else
					//	hauteurParenthese = -1;

					initialisationObjet = false;
				}
			}

			// Si c'est un retour à la ligne,
			if( !parametreObjet ) {
				bufferBackup = buffer;
				buffer = new StringBuffer();
			}
		}

		if( hauteurParenthese!=0
				|| nbOccurences==0
				|| !schema.addObjects( objets ) )
			return null ;

		return schema ;
	}

}
