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

package net.aepik.casl.core.ldap.value;


import net.aepik.casl.core.ldap.SchemaValue;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Une liste de Oid.
 */
public class OidList implements SchemaValue
{

	/**
	 * La liste des oids
	 */
	private Vector<Oid> liste;

	/**
	 * Construit une nouvelle liste d'oids vide.
	 */
	public OidList ()
	{
		liste = new Vector<Oid>();
	}

	/**
	 * Construit une nouvelle liste d'oids à partir d'une représentation
	 * sous forme de chaîne de caractères.
	 * @param str Une représentation d'un OidList sous forme de chaîne.
	 */
	public OidList (String str)
	{
		liste = new Vector<Oid>();
		setValue(str);
	}

	/**
	 * Retourne la valeur du paramêtre.
	 * @return String La valeur du paramêtre sous forme de chaîne de caractères.
	 */
	public String getValue ()
	{
		return toString();
	}

	/**
	 * Retourne l'ensemble des valeurs.
	 * @return String[] Un tableau.
	 */
	public String[] getValues ()
	{
		String[] val = new String[liste.size()];
		Iterator<Oid> it = liste.iterator();
		int i = 0;
		while (it.hasNext())
		{
			val[i] = it.next().toString();
			i++;
		}
		return val;
	}

	/**
	 * Test si la valeur du paramêtre à un format correct.
	 * @return boolean True si le format est correct, false sinon.
	 */
	public static boolean isValidFormat (String str)
	{
		OidList q = new OidList();
		return q.setValue(str);
	}

	/**
	 * Indique si c'est une valeur simple.
	 * @return boolean True si c'est le cas.
	 */
	public boolean isValue ()
	{
		return false;
	}

	/**
	 * Indique si c'est une liste de valeurs.
	 * @return boolean True si c'est le cas.
	 */
	public boolean isValues ()
	{
		return true;
	}

	/**
	 * Modifie la valeur du paramêtre. Les test sont faits avant de modifier
	 * la valeur, pour voir si elle est effectivement au bon format.
	 * @param value Une chaîne de caractères indiquant la nouvelle valeur
	 * 		du paramêtre.
	 * @return boolean True si la modification a réussi, false sinon.
	 */
	public boolean setValue (String value)
	{
		if (value == null || value.length() == 0)
		{
			return false;
		}
		int firstBracket, secondBracket;
		//
		// On regarde si il y a une parenthèse ouvrante dans la chaîne. Si ca
		// n'est pas le cas, on regarde si la chaîne vérifie la méthode
		// isWoid. Alors si elle n'est pas vérifie, cette chaîne est
		// mal formée.
		//
		if ((firstBracket = value.indexOf(40)) < 0)
		{
			return this.setValues(new String[]{ value });
		}
		//
		// Sinon, on regarde si il y a une parenthèse fermante.
		// Il n'y en a pas, la chaîne est mal formée.
		//
		else if ((secondBracket = value.indexOf(41, firstBracket + 1)) >= 0)
		{
			StringTokenizer strtok = new StringTokenizer(
				value.substring(firstBracket + 1, secondBracket), "$");
			String[] values = new String[strtok.countTokens()];
			for (int i = 0; i < values.length; i++)
			{
				values[i] = strtok.nextToken().trim();
			}
			return this.setValues(values);
		}
		return false;
	}

	/**
	 * Modifie la valeur du paramêtre.
	 * @param values Un tableau de valeurs simle
	 * @return boolean True si la modification a réussi, false sinon.
	 */
	public boolean setValues (String[] values)
	{
		if (values == null || values.length == 0)
		{
			return false;
		}
		boolean ok = true;
		Vector<Oid> newListe = new Vector<Oid>();
		for (int i = 0; i < values.length && ok; i++)
		{
			if (ok = Oid.isValidFormat(values[i]))
			{
				newListe.add(new Oid(values[i]));
			}
		}
		if (ok)
		{
			liste = newListe;
		}
		return ok;
	}

	/**
	 * Retourne sous forme de chaîne de caractères la valeur de cet objet.
	 * @return String Une chaîne de caractères.
	 */
	public String toString ()
	{
		String str = "";
		int pos = 0;
		int nbElements = liste.size();
		if (nbElements > 1)
		{
			str += "( ";
			Iterator<Oid> it = liste.iterator();		
			while (it.hasNext())
			{
				str += it.next().toString();
				if (pos < nbElements - 1)
				{
					str += " $ ";
				}
				pos++;
			}
			str += " )";
		}
		else if (nbElements == 1)
		{
			
			str += liste.elementAt(0).toString();
		}
		return str;
	}

}

