/*
 * QDescriptionList.java		0.1		23/05/2006
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


package net.aepik.casl.core.ldap.value;

import net.aepik.casl.core.ldap.SchemaValue;
import net.aepik.casl.core.ldap.SchemaSyntax;

import java.lang.String;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Une liste de QDescription.
**/

public class QDescriptionList implements SchemaValue {

////////////////////////////////
// Attributs
////////////////////////////////

	/** La liste des oids **/
	private Vector<QDescription> liste ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	/**
	 * Construit une nouvelle liste d'oids vide.
	**/
	public QDescriptionList() {
		liste = new Vector<QDescription>();
	}

	/**
	 * Construit une nouvelle liste d'oids à partir d'une représentation
	 * sous forme de chaîne de caractères.
	 * @param str Une représentation d'un OidList sous forme de chaîne.
	**/
	public QDescriptionList( String str ) {
		liste = new Vector<QDescription>();
		setValue( str );
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Retourne la valeur du paramêtre.
	 * @return String La valeur du paramêtre sous forme de chaîne de caractères.
	**/
	public String getValue() { return toString(); }

	/**
	 * Retourne l'ensemble des valeurs.
	 * @return String[] Un tableau.
	**/
	public String[] getValues() {

		String[] val = new String[liste.size()];
		Iterator<QDescription> it = liste.iterator();

		int i = 0;
		while( it.hasNext() ) {
			val[i] = it.next().toString() ;
			i++;
		}

		return val;
	}

	/**
	 * Test si la valeur du paramêtre à un format correct.
	 * @return boolean True si le format est correct, false sinon.
	**/
	public static boolean isValidFormat( String str ) {

		QDescriptionList q = new QDescriptionList();
		return q.setValue( str );
	}

	/**
	 * Indique si c'est une valeur simple.
	 * @return boolean True si c'est le cas.
	**/
	public boolean isValue() { return false; }

	/**
	 * Indique si c'est une liste de valeurs.
	 * @return boolean True si c'est le cas.
	**/
	public boolean isValues() { return true; }

	/**
	 * Modifie la valeur du paramêtre. Les test sont faits avant de modifier
	 * la valeur, pour voir si elle est effectivement au bon format.
	 * @param value Une chaîne de caractères indiquant la nouvelle valeur
	 * 		du paramêtre.
	 * @return boolean True si la modification a réussi, false sinon.
	**/
	public boolean setValue( String value ) {

		Vector<QDescription> newListe = new Vector<QDescription>();
		boolean ok = false ;
		int firstBracket, secondBracket;

		if( value==null || value.length()==0 )
			return false;

		// On regarde si il y a une parenthèse ouvrante dans la chaîne. Si ca
		// n'est pas le cas, on regarde si la chaîne vérifie la méthode
		// isWoid. Alors si elle n'est pas vérifie, cette chaîne est
		// mal formée.
		if( ( firstBracket = value.indexOf( 40 ) )<0 ) {
			if( ok = QDescription.isValidFormat( value ) ) {
				newListe.add( new QDescription( value ) ) ;
				ok = true ;
			}

		// Sinon, on regarde si il y a une parenthèse fermante.
		// Il n'y en a pas, la chaîne est mal formée.
		} else if( ( secondBracket = value.indexOf( 41, firstBracket+1 ) )>=0 ) {

			int begin=0, firstQuote=0, secondQuote=-1;
			boolean fin = false ;
			ok = true ;
			String value2 = value.substring( firstBracket+1, secondBracket ).trim();

			try {
				do {
					begin = secondQuote+1;
		
					// Fin de chaîne.
					if( begin>=value2.length() ) {
						fin = true;

					// Sinon on cherche le premier quote
					// Si il n'y en a pas, on teste si le reste est une chaîne
					// comportant uniquement des espaces.
					} else if( ( firstQuote = value2.indexOf( 39, begin ) )<0 ) {
						if( !SchemaSyntax.isWhsp( value2.substring( begin ) ) ) {
							ok = false ;
						} else {
							fin = true ;
						}

					// Sinon, on regarde si on peut avoir un second quote
					// Si on en a pas, il y a une erreur de syntaxe.
					} else if( ( secondQuote = value2.indexOf( 39, firstQuote+1 ) )<0 ) {
						ok = false ;

					// Tout est ok, on récupère la sous-chaîne et on la teste.
					} else {
						String tmp = value2.substring( firstQuote, secondQuote+1 );
						if( ok = QDescription.isValidFormat( tmp ) ) {
							newListe.add( new QDescription( tmp ) );
						}
					}
		
				} while( ok && !fin );
			} catch( Exception e ) { return false; }
		}

		if (ok)
		{
			liste = newListe ;
		}

		return ok;
	}

	/**
	 * Modifie la valeur du paramêtre.
	 * @param values Un tableau de valeurs simle
	 * @return boolean True si la modification a réussi, false sinon.
	**/
	public boolean setValues( String[] values ) {

		if( values==null || values.length==0 )
			return false;

		boolean ok = true ;
		Vector<QDescription> newListe = new Vector<QDescription>();

		for( int i=0; i<values.length && ok; i++ ) {
			if( ok = QDescription.isValidFormat( values[i] ) )
				newListe.add( new QDescription( values[i] ) );
		}

		if( ok )
			liste = newListe;
		return ok;
	}

	/**
	 * Retourne sous forme de chaîne de caractères la valeur de cet objet.
	 * @return String Une chaîne de caractères.
	**/
	public String toString() {

		String str = "";
		Iterator<QDescription> it = liste.iterator();

		int pos = 0;
		int max = liste.size()-1;
		while( it.hasNext() ) {
			str += it.next().toString();

			if( pos<max ) {
				str += " ";
			}
			pos++;
		}

		if (pos > 1)
		{
			str = "( " + str + " )";
		}

		return str ;
	}

}
