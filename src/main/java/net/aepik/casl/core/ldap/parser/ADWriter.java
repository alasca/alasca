/*
 * ADWriter.java		0.2		29/06/2006
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
import net.aepik.casl.core.ldap.SchemaFileWriter;
import net.aepik.casl.core.ldap.SchemaObject;
import net.aepik.casl.core.ldap.SchemaSyntax;
import net.aepik.casl.core.ldap.SchemaValue;
import net.aepik.casl.core.ldap.syntax.ADSyntax;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class ADWriter extends SchemaFileWriter {

////////////////////////////////
// Constructeurs
////////////////////////////////

	public ADWriter() {
		super();
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Ecrit l'ensemble des objets du schema dans le flux de sortie.
	 * @param schema Le schema.
	**/
	public void write( Schema schema ) throws IOException {

		if( output==null || schema==null || schema.getSyntax()==null )
			return ;

		SchemaSyntax syntax = schema.getSyntax();
		Properties properties = schema.getProperties();
		SchemaObject[] objets = schema.getObjectsInOrder();

		for( int k=0; objets!=null && k<objets.length; k++ ) {

			SchemaObject objet = objets[k];

			// Que ce soit un objet ou un attribut, pour Active Directory, il y
			// a des clefs obligatoires. Au moment de l'écriture, on va
			// regarder si elles sont présentes dans les propriétés de l'objet
			// (objet ou attribut) traité. Si elles n'y sont pas, on les fabriques
			// à partir des valeurs par défaut définies dans les propriétés
			// du schéma.

			Vector<String> parametresObligatoires = new Vector<String>();
			Vector<SchemaValue> parametresVerrouilles = new Vector<SchemaValue>();

			//-----------------
			// Global
			//-----------------

			String opt_cn = null ;
			String opt_changetype = null ;
			String opt_objectClass = null ;

			// cn
			if( objet.isKeyExists( "cn" ) ) {
				opt_cn = "dn: cn=" + objet.getValue( "cn" ).toString();
				parametresVerrouilles.add( objet.getValue( "cn" ) );
			} else if( properties.getProperty( "dn" )!=null ) {
				String name = ( objet.getName()!=null ) ? objet.getName() : objet.getId();
				opt_cn = "dn: cn=" + name + "," + properties.getProperty( "dn" ) ;
			} else
				continue;

			// changetype
			if( objet.isKeyExists( "changetype" ) ) {
				opt_changetype = "changetype: " + objet.getValue( "changetype" ).toString();
				parametresVerrouilles.add( objet.getValue( "changetype" ) );
			} else
				opt_changetype = "changetype: add";

			// objectClass
			if( objet.isKeyExists( "objectClass" ) ) {
				opt_objectClass = "objectClass: " + objet.getValue( "objectClass" ).toString();
				parametresVerrouilles.add( objet.getValue( "objectClass" ) );
			} else
				opt_objectClass = "objectClass: " + objet.getType();

			parametresObligatoires.add( opt_cn );
			parametresObligatoires.add( opt_changetype );
			parametresObligatoires.add( opt_objectClass );

			//-----------------
			// Objet
			//-----------------

			if( objet.getType().equals( syntax.getObjectClassType() ) ) {

				String opt_defaultObjectCategory = null ;
				String opt_governsID = null ;
				String opt_objectClassCategory = null ;
				String opt_subClassOf = null ;

				// defaultObjectCategory
				if( objet.isKeyExists( "defaultObjectCategory" ) ) {
					opt_defaultObjectCategory = "defaultObjectCategory: "
							+ objet.getValue( "defaultObjectCategory" ).toString();
					parametresVerrouilles.add( objet.getValue( "defaultObjectCategory" ) );
				} else if( properties.getProperty( "defaultObjectCategory" )!=null ) {
					opt_defaultObjectCategory = "defaultObjectCategory: "
							+ properties.getProperty( "defaultObjectCategory" );
				} else {
					String[] tmp = opt_cn.split( "," );
					opt_defaultObjectCategory = "defaultObjectCategory: "
							+ "cn=" + objet.getName();

					for( int i=1; tmp!=null && i<tmp.length; i++ )
 						opt_defaultObjectCategory += "," + tmp[i].trim() ;
				}

				// governsID
				if( objet.isKeyExists( "governsID" ) ) {
					opt_governsID = "governsID: " + objet.getValue( "governsID" ).toString();
					parametresVerrouilles.add( objet.getValue( "governsID" ) );
				} else {
					opt_governsID = "governsID: " + objet.getId();
				}

				// objectClassCategory
				if( objet.isKeyExists( "objectClassCategory" ) ) {
					opt_objectClassCategory = "objectClassCategory: "
							+ objet.getValue( "objectClassCategory" ).toString();
					parametresVerrouilles.add( objet.getValue( "objectClassCategory" ) );
				} else if( properties.getProperty( "objectClassCategory" )!=null ) {
					opt_defaultObjectCategory = "objectClassCategory: "
							+ properties.getProperty( "objectClassCategory" );
				} else {
					opt_defaultObjectCategory = "objectClassCategory: 1";
				}

				// subClassOf
				if( objet.isKeyExists( "subClassOf" ) ) {
					opt_subClassOf = "subClassOf: " + objet.getValue( "subClassOf" ).toString();
					parametresVerrouilles.add( objet.getValue( "subClassOf" ) );
				} else if( properties.getProperty( "subClassOf" )!=null ) {
					opt_subClassOf = "subClassOf: " + properties.getProperty( "subClassOf" );
				} else
					continue;

				parametresObligatoires.add( opt_defaultObjectCategory );
				parametresObligatoires.add( opt_governsID );
				parametresObligatoires.add( opt_objectClassCategory );
				parametresObligatoires.add( opt_subClassOf );

			//-----------------
			// Attribut
			//-----------------
			} else if( objet.getType().equals( syntax.getAttributeType() ) ) {

				String currentAttributeSyntax = null ;
				String[][] oMSyntax = new String[][] {
					{ "String(Octet)",				"4",	"2.5.5.10" 	},
					{ "Object(DN-Binary)",			"127",	"2.5.5.7"	},
					{ "Boolean",					"1",	"2.5.5.8"	},
					{ "Object(DS-DN)",				"127",	"2.5.5.1"	},
					{ "Integer",					"2",	"2.5.5.9"	},
					{ "LargeInteger",				"65",	"2.5.5.16"	},
					{ "String(NT-Sec-Desc)",		"66",	"2.5.5.15"	},
					{ "String(Sid)",				"4",	"2.5.5.17"	},
					{ "String(Unicode)",			"64",	"2.5.5.12"	},
					{ "String(Generalized-Time)",	"24",	"2.5.5.11"	}
				};

				String opt_attributeID = null ;
				String opt_attributeSyntax = null ;
				String opt_isSingleValue = null ;
				String opt_lDAPDisplayName = null ;
				String opt_oMSyntax = null ;

				// attributeID
				if( objet.isKeyExists( "attributeID" ) ) {
					opt_attributeID = "attributeID: " + objet.getValue( "attributeID" ).toString();
					parametresVerrouilles.add( objet.getValue( "attributeID" ) );
				} else
					opt_attributeID = "attributeID: " + objet.getId();

				// attributeSyntax
				if( objet.isKeyExists( "attributeSyntax" ) ) {
					currentAttributeSyntax = objet.getValue( "attributeSyntax" ).toString();
					parametresVerrouilles.add( objet.getValue( "attributeSyntax" ) );
				} else if( properties.getProperty( "attributeSyntax" )!=null ) {
					currentAttributeSyntax = properties.getProperty( "attributeSyntax" );
				} else {
					currentAttributeSyntax = oMSyntax[8][0];
				}

				boolean ok = false ;
				for( int i=0; i<oMSyntax.length && !ok; i++ ) {
					if( oMSyntax[i][0].equals( currentAttributeSyntax ) ) {
						ok = true;
						currentAttributeSyntax = oMSyntax[i][2];
					}
				}

				opt_attributeSyntax = "attributeSyntax: " + currentAttributeSyntax;

				// isSingleValue
				if( objet.isKeyExists( "isSingleValued" ) ) {
					opt_isSingleValue = "isSingleValued: TRUE";
					parametresVerrouilles.add( objet.getValue( "isSingleValued" ) );
				} else
					opt_isSingleValue = "isSingleValued: FALSE";

				// lDAPDisplayName
				if( objet.isKeyExists( "lDAPDisplayName" ) ) {
					opt_lDAPDisplayName = "lDAPDisplayName: " + stripQuotes( objet.getValue( "lDAPDisplayName" ).toString() );
					parametresVerrouilles.add( objet.getValue( "lDAPDisplayName" ) );
				} else {
					opt_lDAPDisplayName = "lDAPDisplayName: " + stripQuotes( objet.getName() );
				}

				// oMSyntax
				String currentOMSyntax = null ;
				for( int i=0; i<oMSyntax.length && currentOMSyntax==null; i++ ) {
					if( oMSyntax[i][2].equals( currentAttributeSyntax ) )
						currentOMSyntax = oMSyntax[i][1] ;
				}

				if( currentOMSyntax!=null )
					opt_oMSyntax = "oMSyntax: " + currentOMSyntax ;
				else
					continue;

				parametresObligatoires.add( opt_attributeID );
				parametresObligatoires.add( opt_attributeSyntax );
				parametresObligatoires.add( opt_isSingleValue );
				parametresObligatoires.add( opt_lDAPDisplayName );
				parametresObligatoires.add( opt_oMSyntax );

			}

			// Si tout est ok : on écrit les données de l'objet.

			for( Enumeration<String> elem = parametresObligatoires.elements();
				elem.hasMoreElements(); )
			{
				String e = elem.nextElement();
				if( e!=null ) {
	         		output.write( e );
         			output.newLine();
         		}
         	}

			// Enfin, on ecrit la chaîne dans le fichier. On fait attention à
			// ce que tout objet soit séparé par un retour à la ligne.

			String[] keys = objet.getKeys();
			SchemaValue[] values = objet.getValues();

			for( int i=0; keys!=null && i<keys.length; i++ ) {

				// Pas de doublons.
				// On n'ecrit pas les parametres verrouilles.

				if( !parametresVerrouilles.contains( values[i] ) ) {
					String[] val = values[i].getValues();
					String objStr = keys[i];

					for( int j=0; val!=null && j<val.length; j++ ) {
						String v = val[j];
						String s = objStr ;

						if( v.length()!=0 )
							s += ": " + stripQuotes( v ) ;
						else
							s += ": TRUE" ;

						output.write( s, 0, s.length() );
						output.newLine();
					}
				}
			}

			output.newLine();

			// On met à jours Active Directory. On passe dans le fichier une
			// commande spécifique, pour dire à AD de prendre en compte les
			// objets nouvellements créés.

			output.write( "dn:" );					output.newLine();
			output.write( "changetype: Modify" );	output.newLine();
			output.write( "add: schemaUpdateNow" );	output.newLine();
			output.write( "schemaUpdateNow: 1" );	output.newLine();
			output.write( "-" );					output.newLine();
			output.newLine();
		}
	}

////////////////////////////////
// Methodes privées
////////////////////////////////

	/**
	 * Permet de supprimer espaces et quotes entourant une chaîne de caractères.
	 * @param str Une chaîne de caractères.
	 * @return String La chaîne de caractères sans espaces et quotes.
	**/
	private String stripQuotes( String str ) {

		if( str==null || str.length()==0 )
			return "";

		int firstQuote, secondQuote ;
		String result = str.trim();

		// On cherche le premier quote. S'il n'y en a pas => faux.
		if( ( firstQuote = result.indexOf( 39 ) )<0 )
			return str;

		// Si il y en a un, cherche le suivant. Si aucun suivant => faux.
		if( ( secondQuote = result.indexOf( 39, firstQuote+1 ) )<0 )
			return str;

		return result.substring( firstQuote+1, secondQuote );
	}

}
