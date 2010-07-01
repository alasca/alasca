/*
 * QDescription.java		0.1		23/05/2006
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

import net.aepik.casl.core.ldap.SchemaSyntax;

import java.lang.String;

/**
 * Un objet QuotedDescription est un objet dont la valeur est une chaîne de
 * caractères quotées, et dont les caractères sont alpha-numériques ou
**/

public class QDescription extends QString {

////////////////////////////////
// Constructeurs
////////////////////////////////

	public QDescription() {
		super();
	}

	public QDescription( String str ) {
		super( str );
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Test si la valeur du paramêtre à un format correct.
	 * @return boolean True si le format est correct, false sinon.
	**/
	public static boolean isValidFormat( String str ) {

		QDescription q = new QDescription();
		return q.setValue( str );
	}

	/**
	 * Modifie la valeur du paramêtre.
	 * @param value Une chaîne de caractères indiquant la nouvelle valeur
	 * 		du paramêtre, cette chaîne doit être quotée.
	 * @return boolean True si la modification a réussi, false sinon.
	**/
	public boolean setValue( String value ) {

		int firstQuote, secondQuote ;

		if( value==null || value.length()==0 )
			return false;

		// On cherche le premier quote. S'il n'y en a pas => faux.
		if( ( firstQuote = value.indexOf( 39 ) )<0 )
			return false;

		// Si il y en a un, cherche le suivant. Si aucun suivant => faux.
		if( ( secondQuote = value.indexOf( 39, firstQuote+1 ) )<0 )
			return false;

		// On regarde si la chaîne avant le premier quote est consituté
		// uniquement d'espaces, ainsi que la chaîne après le second quote.
		// Si c'est le cas, on vérifie l'intérieur de la chaîne.
		if( SchemaSyntax.isWhsp( value.substring( 0, firstQuote ) )
				&& SchemaSyntax.isWhsp( value.substring( secondQuote+1 ) )
				&& SchemaSyntax.isKeyString(
					value.substring( firstQuote+1, secondQuote ) ) ) {
			this.value = value.substring( firstQuote+1, secondQuote );
			return true;
		}

		return false;
	}

	/**
	 * Modifie la valeur du paramêtre.
	 * @param values Un tableau de valeurs simle
	 * @return boolean True si la modification a réussi, false sinon.
	**/
	public boolean setValues( String[] values ) {

		int firstQuote, secondQuote ;

		if( value==null || values.length!=1 )
			return false;

		firstQuote = values[0].indexOf( 39 );
		secondQuote = values[0].indexOf( 39, firstQuote+1 );

		if( firstQuote==-1 && secondQuote==-1
				&& SchemaSyntax.isKeyString( values[0] ) ) {
			value = values[0];
			return true ;
		}

		return false;
	}

}
