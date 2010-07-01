/*
 * SchemaObject.java		0.1		23/05/2006
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

import net.aepik.casl.core.ldap.value.*;

import java.lang.NullPointerException;
import java.lang.String;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Un element que manipule le schema. Un objet du schema peut être de deux
 * types principaux : objectClass ou attributeType. Ces deux types spécifient
 * les informations concernant des objets du schema (comme ou, cn, top, ...) et
 * les attributs utilisés par les objets du schema (displayName, uid, ...).
 * <br/><br/>
 * Avec cette classe, il est possible de gérer l'un ou l'autre de ces deux
 * types. En effet, les informations qu'ils contiennent sont généralement
 * variable et diffère d'une implémentation à une autre. De ce fait, ces
 * informations peuvent être définies à la volée, dans la pratique nous
 * utiliserons une syntaxe.
**/

public class SchemaObject {

////////////////////////////////
// Constantes
////////////////////////////////

	public static final int TYPE_OBJECT = 0;
	public static final int TYPE_ATTRIBUTE = 1;

////////////////////////////////
// Attributs
////////////////////////////////

	/** La syntaxe utilisé par l'objet **/
	private SchemaSyntax syntax ;
	/** Indique le type de l'objet, dépend de la syntaxe **/
	private String type ;
	/** Une table d'entrée <parametre-valeur> **/
	private Hashtable<String,SchemaValue> values ;
	/** L'identifiant unique de cet objet **/
	private String id ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	/**
	 * Construit un nouvel objet SchemaObject vide.
	 * @param syntax Une syntaxe pour cette objet.
	 * @param type Le type de l'objet.
	**/
	public SchemaObject( SchemaSyntax syntax, String type, String id ) {

		this.syntax = syntax;
		this.type = type;
		this.values = new Hashtable<String,SchemaValue>();
		this.id = id;
	}

////////////////////////////////
// Methodes
////////////////////////////////

	/**
	 * Ajoute une entrée à l'objet.
	 * @param key La clef pour l'entrée.
	 * @param value La valeur à ajouter.
	 * @return boolean True si l'entrée n'existe pas déjà, false sinon.
	**/
	public boolean addValue( String key, SchemaValue value ) {

		try {
			if( !isKeyExists( key ) ) {
				values.put( key, value );
				return true;
			}
		} catch( NullPointerException e ) {}

		return false;
	}

	/**
	 * Retourne le nombre de valeurs contenues dans cet objet.
	 * @return int Un entier.
	**/
	public int countValues() { return values.size(); }

	/**
	 * Supprime une entrée de l'objet.
	 * @param key La clef spécifiant l'entrée.
	 * @return boolean True si l'entrée n'existe pas déjà, false sinon.
	**/
	public boolean delValue( String key ) {

		try {
			if( isKeyExists( key ) ) {
				values.remove( key );
				return true;
			}
		} catch( NullPointerException e ) {}

		return false;
	}

	/**
	 * Retourne l'identifiant de cet objet.
	 * @return String L'id sous forme de chaîne de caractères.
	**/
	public String getId() { return id; }

	/**
	 * Retourne l'ensemble des clefs.
	 * L'indexation est en correspondance avec les valeurs retournés
	 * par la méthode getValues().
	 * @return String[] L'ensemble des clefs
	**/
	public String[] getKeys() {

		String[] result = new String[values.size()];
		int position = 0;
		for( Enumeration<String> e = values.keys(); e.hasMoreElements(); ) {
			result[position] = e.nextElement();
			position++;
		}

		return result ;
	}

	/**
	 * Retourne le nom usuel de cet objet si il existe.
	 * @return String Le nom usuel, sinon null.
	**/
	public String getName() {

		String keyname = syntax.getDisplayNameParameter( type );

		if( isKeyExists( keyname ) ) {
			String tmp = getValue( keyname ).toString();
			
			if( QDescription.isValidFormat( tmp ) ) {
				int firstQuote = tmp.indexOf( 39 );
				int secondQuote = tmp.indexOf( 39, firstQuote+1 );
				tmp = tmp.substring( firstQuote+1, secondQuote );
			}

			return tmp ;
		}

		return null ;
	}

	/**
	 * Retourne l'ensemble des références vers d'autres objets qui sont
	 * contenues dans les valeurs de toutes les données de cet objet.
	 * @return String[] Un tableau de références vers d'autres objets.
	**/
	public String[] getObjectsReferences() {

		String[] result = null ;

		return result ;
	}

	/**
	 * Retourne la syntaxe de cet objet.
	 * @return SchemaSyntax La syntaxe de cet objet.
	**/
	public SchemaSyntax getSyntax() { return syntax; }

	/**
	 * Retourne le type de cet objet.
	 * @return String Le type de cet objet sous forme de chaîne de caractères.
	**/
	public String getType() { return type; }

	/**
	 * Retourne une valeur pour une clef donnée.
	 * @param key La clef spécifiant l'entrée.
	 * @return String La valeur associée à la clef.
	**/
	public SchemaValue getValue( String key ) {

		try {
			if( isKeyExists( key ) )
				return values.get( key );
		} catch( NullPointerException e ) {}

		return null;
	}

	/**
	 * Retourne l'ensemble des valeurs.
	 * @return SchemaValue[] L'ensemble des valeurs.
	**/
	public SchemaValue[] getValues() {

		SchemaValue[] result = new SchemaValue[values.size()];
		int position = 0;
		for( Enumeration<SchemaValue> e = values.elements(); e.hasMoreElements(); ) {
			result[position] = e.nextElement();
			position++;
		}

		return result ;
	}

	/**
	 * Initialise tous les paramêtres à l'aide d'une chaîne
	 * de caractères.
	 * @param str Une chaîne de caractères.
	**/
	public boolean initFromString( String str ) {

		// En premier lui, il s'agit d'initialiser l'id de
		// l'objet grâce à la chaîne d'initialisation.

		id = syntax.searchSchemaObjectOID( type, str );
		if( id==null )
			return false ;

		// Ensuite, on récupère toutes les valeurs possibles.
		// Le tableau que l'on récupère comprend 2 colonnes, la première
		// contient les clefs et la seconde les valeurs pour ces clefs.

		String[][] pvalues = syntax.searchSchemaObjectValues( type, str );
		if( values==null )
			return false ;

		// On procède à une petite sauvegarde des données.
		// Si l'initialisation échoue, on restaure les anciennes valeurs.

		Hashtable<String,SchemaValue> save = values ;
		Hashtable<String,SchemaValue> values = new Hashtable<String,SchemaValue>();

		// Enfin, on va stocker ces valeurs dans cet objet.
		// Il s'agit de tester si la syntaxe peut créer des valeurs d'objets
		// avec les valeurs qu'on a.

		boolean erreur = false ;
		for( int i=0; i<pvalues.length && !erreur; i++ ) {

			if( pvalues[i][0]!=null && pvalues[i][1]!=null ) {

				SchemaValue value = syntax.createSchemaValue( type, pvalues[i][0], pvalues[i][1] );
				if( value!=null )
					addValue( pvalues[i][0], value );
				else
					erreur = true ;
			}
		}

		// Restauration des anciennes valeurs, si erreur.

		if( erreur ) {
			values = save;
			return false;
		}

		return true;
	}

	/**
	 * Teste si une entrée pour une clef donnée existe.
	 * @param key Une clef.
	 * @return boolean True si l'entrée existe, false sinon.
	**/
	public boolean isKeyExists( String key ) {

		try {
			if( key!=null ) {
				return values.containsKey( key );
			}
		} catch( NullPointerException e ) {}

		return false;
	}

	/**
	 * Modifie la syntaxe utilisée.
	 * @param syntax La nouvelle syntaxe a utiliser.
	**/
	public void setSyntax( SchemaSyntax syntax ) { this.syntax = syntax; }

	/**
	 * Modifie le type de l'objet.
	 * @param type Le nouveau type de l'objet.
	**/
	public void setType( String type ) { this.type = type; }

	/**
	 * Retourne une chaîne de caractères représentant cet objet.
	 * @return String Une chaîne de caractères.
	**/
	public String toString() {

		String[] params_name = syntax.getParameters( type );
		if( params_name==null )
			return "";

		String str = id + " ";
		for( int i=0; i<params_name.length; i++ ) {
			if( isKeyExists( params_name[i] ) ) {
				str += params_name[i] + " " + getValue( params_name[i] ) + " ";
			}
		}

		return str;
	}
}
