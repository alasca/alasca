/*
 * SchemaFileWriter.java		0.1		15/06/2006
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

import java.io.BufferedWriter;
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

public abstract class SchemaFileWriter {

////////////////////////////////
// Attributs
////////////////////////////////

	/** La sortie sur laquelle écrire **/
	protected BufferedWriter output ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	/**
	 * Construit un nouveau parser sans entrée.
	**/
	public SchemaFileWriter() {
		this.output = null ;
	}

	/**
	 * Construit un nouveau parser à partir d'une entrée.
	 * @param out Un objet Writer.
	**/
	public SchemaFileWriter( BufferedWriter out ) {
		this.output = out ;
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Retourne le flux de sortie.
	 * @return BufferedWriter Un flux.
	**/
	public BufferedWriter getOutput() { return output; }

	/**
	 * Ecrit l'ensemble des objets du schema dans le flux de sortie.
	 * @param schema Le schéma à écrire.
	**/
	public abstract void write( Schema schema ) throws IOException;

	/**
	 * Modifie le flux de sortie.
	 * @param out Le nouveau flux de sortie.
	**/
	public void setOutput( BufferedWriter out ) { this.output = out; }

}
