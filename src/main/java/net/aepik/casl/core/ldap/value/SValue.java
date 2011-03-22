/*
 * SValue.java		0.1		23/05/2006
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

/**
 * Un SValue est une valeur simple: entier, booléen ou simple chaîne.
**/

public class SValue implements SchemaValue {

////////////////////////////////
// Attributs
////////////////////////////////

	/** La valeur **/
	private String value ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public SValue() {
		value = "";
	}

	public SValue( String str ) {
		value = "";
		setValue( str );
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Retourne la valeur du paramêtre.
	 * @return String La valeur du paramêtre sous forme de chaîne de caractères.
	**/
	public String getValue() { return value; }


	/**
	 * Retourne l'ensemble des valeurs.
	 * @return String[] Un tableau.
	**/
	public String[] getValues() {

		String[] val = new String[1];
		val[0] = value;
		return val;
	}

	/**
	 * Indique si c'est une valeur simple.
	 * @return boolean True si c'est le cas.
	**/
	public boolean isValue() { return true; }

	/**
	 * Indique si c'est une liste de valeurs.
	 * @return boolean True si c'est le cas.
	**/
	public boolean isValues() { return false; }

	/**
	 * Modifie la valeur du paramêtre.
	 * @param value Une chaîne de caractères indiquant la nouvelle valeur
	 * 		du paramêtre, cette chaîne doit être quotée.
	 * @return boolean True si la modification a réussi, false sinon.
	**/
	public boolean setValue( String value ) {
		this.value = value;
		return true ;
	}

	/**
	 * Modifie la valeur du paramêtre.
	 * @param values Un tableau de valeurs simle
	 * @return boolean True si la modification a réussi, false sinon.
	**/
	public boolean setValues( String[] values ) {

		if( values!=null && values.length==1 )
			return setValue( values[0] );

		return false;
	}

	/**
	 * Retourne sous forme de chaîne de caractères la valeur de cet objet.
	 * @return String Une chaîne de caractères.
	**/
	public String toString() { return value; }

}
