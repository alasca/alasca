/*
 * SchemaFileReader.java		0.1		23/05/2006
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


package net.aepik.casl.core.ldap;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Cet objet est un parser pour la grammaire décrit dans la RFC 2252.
 * Ce parser ne concerne que les grammaires concernant les objets de type
 * ObjectClass ou AttributeType.
 * <br/><br/>
 * Ce parseur vérifie si la syntaxe en entrée est correcte. Cela évitera
 * une vérification supplémentaire lors de la création des objets adéquates.
 * <br/><br/>
 * Le parser respecte le standard d'écriture définit dans la RFC 2252. De ce
 * fait, en cas de modification de la formalisation, il suffit de créer une
 * classe qui étend celle-ci, et de réécrire la méthode parse(). Le schéma
 * prendra en compte automatiquement le changement, du moment qu'ils
 * obtient bien des objets du schema.
 * <br/><br/>
 * Pour plus de renseignements, lisez la RFC 2252 et notamment les
 * grammaires BNF proposées.
**/

public abstract class SchemaFileReader {

////////////////////////////////
// Attributs
////////////////////////////////

	/** La syntaxe d'entrée **/
	protected SchemaSyntax syntax ;
	/** L'entrée sur laquelle le parser lit **/
	protected BufferedReader input ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	/**
	 * Construit un nouveau parser sans entrée.
	 * @param syntax La syntaxe.
	**/
	public SchemaFileReader( SchemaSyntax syntax ) {
		this.syntax = syntax ;
		this.input = null ;
	}

	/**
	 * Construit un nouveau parser à partir d'une entrée.
	 * @param syntax La syntaxe.
	 * @param in Un objet Reader.
	**/
	public SchemaFileReader( BufferedReader in ) {
		this.syntax = syntax ;
		this.input = in ;
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Retourne le flux d'entrée du parseur.
	 * @return Reader Un flux.
	**/
	public BufferedReader getInput() { return input; }

	/**
	 * Parcourt l'entrée et retourne l'ensemble des objets schéma lus.
	 * @retour Le schéma lu.
	**/
	public abstract Schema read() throws IOException;

	/**
	 * Modifie le flux d'entrée du parseur.
	 * @param in Le nouveau flux d'entrée.
	**/
	public void setInput( BufferedReader in ) { this.input = in; }

}
