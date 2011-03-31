/*
 * Oid.java		0.1		23/05/2006
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

/**
 * Un Oid est un identifiant qui peut avoir comme valeur soit descriptive, soit
 * une valeur numérique, avec plus ou moins d'espaces
 * autour.
 */
public class Oid implements SchemaValue
{

	/**
	 * Value.
	 */
	private String value;

	/**
	 * Whether or not this value is a numeric oid.
	 */
	private boolean isNumeric;

	/**
	 * Build a new Oid object.
	 */
	public Oid ()
	{
		value = "";
		isNumeric = false;
	}

	/**
	 * Build a new Oid object.
	 * @param str The value for this new Oid object.
	 */
	public Oid ( String str )
	{
		value = "";
		isNumeric = false;
		this.setValue(str);
	}

	/**
	 * Retourne la valeur du paramêtre.
	 * @return String La valeur du paramêtre sous forme de chaîne de caractères.
	 */
	public String getValue ()
	{
		return value;
	}

	/**
	 * Retourne l'ensemble des valeurs.
	 * @return String[] Un tableau.
	 */
	public String[] getValues ()
	{
		String[] val = new String[1];
		val[0] = value;
		return val;
	}

	/**
	 * Test si la valeur du paramêtre à un format correct.
	 * @return boolean True si le format est correct, false sinon.
	 */
	public static boolean isValidFormat ( String str )
	{
		Oid q = new Oid();
		return q.setValue(str);
	}

	/**
	 * Indicates whether or not if this value is numeric.
	 * @return boolean True if this value is numeric.
	 */
	public boolean isNumeric ()
	{
		return this.isNumeric;
	}

	/**
	 * Indique si c'est une valeur simple.
	 * @return boolean True si c'est le cas.
	 */
	public boolean isValue ()
	{
		return true;
	}

	/**
	 * Indique si c'est une liste de valeurs.
	 * @return boolean True si c'est le cas.
	 */
	public boolean isValues ()
	{
		return false;
	}

	/**
	 * Modifie la valeur du paramêtre.
	 * @param value Une chaîne de caractères indiquant la nouvelle valeur
	 * 		du paramêtre, cette chaîne doit être quotée.
	 * @return boolean True si la modification a réussi, false sinon.
	 */
	public boolean setValue ( String value )
	{
		boolean ret = false;
		if (SchemaSyntax.isNumericOid(value))
		{
			this.value = value;
			this.isNumeric = true;
			ret = true;
		}
		else if (SchemaSyntax.isKeyString(value))
		{
			this.value = value;
			this.isNumeric = false;
			ret = true;
		}
		return ret;
	}

	/**
	 * Modifie la valeur du paramêtre.
	 * @param values Un tableau de valeurs simle
	 * @return boolean True si la modification a réussi, false sinon.
	 */
	public boolean setValues ( String[] values )
	{
		if (values != null && values.length == 1)
		{
			return setValue(values[0]);
		}
		return false;
	}

	/**
	 * Retourne sous forme de chaîne de caractères la valeur de cet objet.
	 * @return String Une chaîne de caractères.
	 */
	public String toString ()
	{
		return value;
	}

}
