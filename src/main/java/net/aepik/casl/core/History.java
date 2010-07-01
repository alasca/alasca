/*
 * History.java		0.1		23/06/2006
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


package net.aepik.casl.core;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Un historique permet de garder en mÃÂ©moire un ordre de donnÃÂ©es.
 * Par exemple, un ordre de lecture de donnÃÂ©es dans un fichier.
**/

public class History {

////////////////////////////////
// Attributs
////////////////////////////////

	/** Un vecteur contenant les donnÃÂ©es **/
	private Vector<Object> datas;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public History() {
		datas = new Vector<Object>();
	}

////////////////////////////////
// MÃÂ©thodes publiques
////////////////////////////////

	/**
	 * Retourne une ÃÂ©numÃÂ©ration de l'historique.
	 * @return Enumeration<Object> Une ÃÂ©numÃÂ©ration de l'historique.
	**/
	public Enumeration<Object> elements() {
		return datas.elements();
	}

	/**
	 * Retourne le premier ÃÂ©lement.
	 * @return Object Un objet contenu ÃÂ  la premiÃÂ¨re position de l'historique.
	**/
	public Object getElementInFirstPosition() {

		Object result = null;

		if( datas.size()!=0 ) {
			result = datas.firstElement();
		}

		return result;
	}

	/**
	 * Retourne le dernier ÃÂ©lement.
	 * @return Object Un objet contenu ÃÂ  la derniÃÂ¨re position de l'historique.
	**/
	public Object getElementInLastPosition() {

		Object result = null;

		if( datas.size()!=0 ) {
			result = datas.lastElement();
		}

		return result;
	}

	/**
	 * Retourne la position d'un objet dans l'historique.
	 * @return int Un entier indiquant la position de l'objet passÃÂ© en paramÃÂ¨tre
	 *		de cette mÃÂ©thode.
	**/
	public int getIndexOf( Object o ) { return datas.indexOf( o ); }

	/**
	 * InsÃÂ¨re un ÃÂ©lÃÂ©ment au dÃÂ©but de l'historique.
	 * @param o Un objet ÃÂ  insÃÂ©rer.
	**/
	public void insertElementInFirstPosition( Object o ) {
		datas.add( 0, o );
	}

	/**
	 * InsÃÂ¨re un ÃÂ©lÃÂ©ment ÃÂ  la fin de l'historique.
	 * @param o Un objet ÃÂ  insÃÂ©rer.
	**/
	public void insertElementInLastPosition( Object o ) {
		datas.add( o );
	}

	/**
	 * DÃÂ©place l'objet ÃÂ  la position i dans l'historique vers la position j.
	 * @param i La position initiale dans de l'objet ÃÂ  dÃÂ©placer.
	 * @param j La position future de l'objet.
	**/
	public void moveElement( int i, int j ) {

		if( i>=0 && i<datas.size() && j!=i && j>=0 && j<datas.size() ) {

			Object o = datas.elementAt( i );
			datas.removeElementAt( i );
			datas.insertElementAt( o, j );
		}
	}

	/**
	 * Retourne un ÃÂ©lement et le supprime de l'historique.
	 * @return Object Un objet contenu ÃÂ  la premiÃÂ¨re position de l'historique.
	**/
	public void removeElement( Object o ) {

		if( datas.contains( o ) ) {
			datas.remove( o );
		}
	}

	/**
	 * Retourne le premier ÃÂ©lement et le supprime de l'historique.
	 * @return Object Un objet contenu ÃÂ  la premiÃÂ¨re position de l'historique.
	**/
	public Object removeElementInFirstPosition() {

		Object result = null;

		if( datas.size()!=0 ) {
			result = datas.firstElement();
			datas.remove( result );
		}

		return result;
	}

	/**
	 * Retourne le dernier ÃÂ©lement et le supprime de l'historique.
	 * @return Object Un objet contenu ÃÂ  la derniÃÂ¨re position de l'historique.
	**/
	public Object removeElementInLastPosition() {

		Object result = null;

		if( datas.size()!=0 ) {
			result = datas.lastElement();
			datas.remove( result );
		}

		return result;
	}

}
