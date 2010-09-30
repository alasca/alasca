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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */


package net.aepik.casl.core.ldap.syntax;

import net.aepik.casl.core.ldap.value.*;
import net.aepik.casl.core.ldap.SchemaObject;
import net.aepik.casl.core.ldap.SchemaFileReader;
import net.aepik.casl.core.ldap.SchemaFileWriter;
import net.aepik.casl.core.ldap.SchemaSyntax;
import net.aepik.casl.core.ldap.SchemaValue;
import net.aepik.casl.core.ldap.parser.ADWriter;
import java.lang.String;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Syntaxe propre à IBM Directory Server.
**/

public class ADSyntax extends RFCSyntax {

////////////////////////////////
// Constantes
////////////////////////////////

	public final static String AD_ATTRIBUTE	= "attributeSchema";
	public static final String[][] AD_ATTRIBUTE_PARAMETERS = {
		{ "0",	"cn",								""							},
		{ "1",	"lDAPDisplayName",					""							},
		{ "2",	"SchemaIDGUID:",					""							},
		{ "3",	"adminDisplayName",					""							},
		{ "4",	"attributeSecurityGUID",			""							},
		{ "5",	"attributeSyntax",					"String(Octet)"				},
		{ "5",	"attributeSyntax",					"Object(DN-Binary)"			},
		{ "5",	"attributeSyntax",					"Boolean"					},
		{ "5",	"attributeSyntax",					"Object(DS-DN)"				},
		{ "5",	"attributeSyntax",					"Integer"					},
		{ "5",	"attributeSyntax",					"LargeInteger"				},
		{ "5",	"attributeSyntax",					"String(NT-Sec-Desc)"		},
		{ "5",	"attributeSyntax",					"String(Sid)"				},
		{ "5",	"attributeSyntax",					"String(Unicode)"			},
		{ "5",	"attributeSyntax",					"String(Generalized-Time)"	},
		{ "6",	"isSingleValued",					null						},
		{ "7",	"searchFlags",						""							},
		{ "8",	"isMemberOfPartialAttributeSet",	null						},
		{ "9",	"linkID",							""							},
		{ "10",	"systemFlags",						""							},
		{ "11",	"systemOnly",						""							},
		{ "12",	"mAPIID",							""							},
		{ "13",	"isDefunct",						null						},
		{ "14",	"description",						""							},
		{ "15",	"objectClass",						"attributeSchema"			},
		{ "15",	"objectClass",						"classSchema"				}
	};

	public final static String AD_OBJECT	= "classSchema";
	public static final String[][] AD_OBJECT_PARAMETERS = {
		{ "0",	"cn",							""					},
		{ "1",	"lDAPDisplayName",				""					},
		{ "2",	"SchemaIDGUID:",				""					},
		{ "3",	"adminDisplayName",				""					},
		{ "4",	"rDnAttId",						""					},
		{ "5",	"mustContain",					""					},
		{ "6",	"systemMustContain",			""					},
		{ "7",	"mayContain",					""					},
		{ "8",	"systemMayContain",				""					},
		{ "9",	"possSuperiors",				""					},
		{ "10",	"systemPossSuperiors",			""					},
		{ "11",	"objectClassCategory",			"1"					},
		{ "11",	"objectClassCategory",			"2"					},
		{ "11",	"objectClassCategory",			"3"					},
		{ "12",	"subClassOf",					""					},
		{ "13",	"auxiliaryClass",				""					},
		{ "14",	"systemAuxiliaryClass",			""					},
		{ "15",	"defaultObjectCategory",		""					},
		{ "16",	"defaultHidingValue",			""					},
		{ "17",	"systemFlags",					""					},
		{ "18",	"systemOnly",					""					},
		{ "19",	"defaultSecurityDescriptor",	""					},
		{ "20",	"isDefunct",					null				},
		{ "21",	"description",					""					},
		{ "22",	"objectClass",					"attributeSchema"	},
		{ "22",	"objectClass",					"classSchema"		},
	};

////////////////////////////////
// Constructeurs
////////////////////////////////

	public ADSyntax() {
		super();
		super.attributeHeader = AD_ATTRIBUTE ;
		super.objectClassHeader = AD_OBJECT ;
		super.attributeType = AD_ATTRIBUTE;
		super.objectClassType = AD_OBJECT;
	}

////////////////////////////////
// Methodes
////////////////////////////////

	/**
	 * Créer un reader pour lire un fichier de cette syntaxe.
	 * @return SchemaFileReader Un reader spécifique à cette syntaxe.
	**/
	public SchemaFileReader createSchemaReader() { return null; }

	/**
	 * Créer un nouvel objet SchemaValue d'une valeur donnée.
	 * @param type Un type d'objet.
	 * @param param Le nom du paramêtre, pour déterminer le type de la valeur;
	 *		si le paramètre est null, le type créé est un Oid.
	 * @param str Une chaîne de caractères, ou null pour ne pas initialiser.
	 * @return SchemaValue L'objet de type SchemaValue.
	**/
	public SchemaValue createSchemaValue( String type, String param, String value ) {

		SchemaValue valeur = null ;
		String tmp = value ;

		if( tmp!=null )
			tmp = tmp.trim();

		// Si param est null, on créer un Oid avec la valeur précisée.
		if( param==null && tmp==null ) {
			valeur = new Oid();

		} else if( param==null ) {
			valeur = new Oid( tmp );

		// Sinon, pour différent type d'attribut,
		// on créer un objet particulier.
		} else {

			if( type.equals( attributeType ) ) {

				if( param.equals( AD_ATTRIBUTE_PARAMETERS[0][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[2][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[4][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[5][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[15][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[16][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[17][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[18][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[19][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[20][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[21][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[22][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[24][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[25][1] ) ) {
					if( tmp!=null )
						valeur = new SValue( tmp );
					else
						valeur = new SValue();

				} else if( param.equals( AD_ATTRIBUTE_PARAMETERS[1][1] )
						|| param.equals( AD_ATTRIBUTE_PARAMETERS[3][1] ) ) {
					if( tmp!=null )
						valeur = new QDescription( tmp );
					else
						valeur = new QDescription();

				} else if( param.equals( AD_ATTRIBUTE_PARAMETERS[24][1] ) ) {
					if( tmp!=null )
						valeur = new QString( tmp );
					else
						valeur = new QString();

				} else {
					if( tmp!=null )
						valeur = new SValue( tmp );
					else
						valeur = new SValue();
				}

			} else if( type.equals( objectClassType ) ) {

				if( param.equals( AD_OBJECT_PARAMETERS[0][2] )
						|| param.equals( AD_OBJECT_PARAMETERS[2][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[4][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[11][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[12][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[13][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[17][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[18][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[19][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[20][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[21][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[22][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[24][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[25][1] ) ) {
					if( tmp!=null )
						valeur = new SValue( tmp );
					else
						valeur = new SValue();

				} else if( param.equals( AD_OBJECT_PARAMETERS[1][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[3][1] ) ) {
					if( tmp!=null )
						valeur = new QDescription( tmp );
					else
						valeur = new QDescription();

				} else if( param.equals( AD_OBJECT_PARAMETERS[23][1] ) ) {
					if( tmp!=null )
						valeur = new QString( tmp );
					else
						valeur = new QString();

				} else if( param.equals( AD_OBJECT_PARAMETERS[14][1] ) ) {
					if( tmp!=null )
						valeur = new Oid( tmp );
					else
						valeur = new Oid();

				} else if( param.equals( AD_OBJECT_PARAMETERS[5][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[6][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[7][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[8][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[9][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[10][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[15][1] )
						|| param.equals( AD_OBJECT_PARAMETERS[16][1] ) ) {
					if( tmp!=null )
						valeur = new OidList( tmp );
					else
						valeur = new OidList();

				} else {
					if( tmp!=null )
						valeur = new SValue( tmp );
					else
						valeur = new SValue();
				}
			}
		}

		return valeur;
	}

	/**
	 * Créer un writer pour écrire des données.
	 * @return SchemaFileWriter Un writer spécifique à cette syntaxe.
	**/
	public SchemaFileWriter createSchemaWriter() { return new ADWriter(); }

	/**
	 * Retourne l'ensemble des valeurs possible d'un paramêtre d'attribut.
	 * @param paramName Un nom de paramêtre.
	 * @return String[] Une ensemble de valeurs pour ce nom de paramêtres.
	**/
	public String[] getAttributeParameterDefaultValues( String paramName ) {
		return searchParameterDefaultValues( AD_ATTRIBUTE_PARAMETERS, paramName );
	}

	/**
	 * Retourne l'ensemble des paramêtres d'attribut.
	 * @return String[] Un ensemble de chaînes de caractères.
	**/
	public String[] getAttributeParameters() {
		return searchParameters( AD_ATTRIBUTE_PARAMETERS );
	}

	/**
	 * Retourne le nom du paramêtre renseignant le nom usuel de l'objet.
	 * @param type Le type de l'objet.
	**/
	public String getDisplayNameParameter( String type ) {

		String result = null ;

		if( type!=null ) {
			if( type.equals( attributeType ) ) {
				result = AD_ATTRIBUTE_PARAMETERS[1][1];
			} else if( type.equals( objectClassType ) ) {
				result = AD_OBJECT_PARAMETERS[1][1];
			}
		}

		return result ;
	}

	/**
	 * Retourne l'ensemble des valeurs possible d'un paramêtre d'objet.
	 * @param paramName Un nom de paramêtre.
	 * @return String[] Une ensemble de valeurs pour ce nom de paramêtres.
	**/
	public String[] getObjectParameterDefaultValues( String paramName ) {
		return searchParameterDefaultValues( AD_OBJECT_PARAMETERS, paramName );
	}

	/**
	 * Retourne l'ensemble des paramêtres d'objet.
	 * @return String[] Un ensemble de chaînes de caractères.
	**/
	public String[] getObjectParameters() {
		return searchParameters( AD_OBJECT_PARAMETERS );
	}

	/**
	 * Retourne l'ensemble des autres paramêtres d'attribut possible pour
	 * le paramêtre de nom 'paramName'.
	 * @param paramName Un nom de paramêtre d'attribut.
	 * @return String[] Un ensemble de chaînes de caractères.
	**/
	public String[] getOthersAttributeParametersFor( String paramName ) {
		return searchOthersParametersFor( AD_ATTRIBUTE_PARAMETERS, paramName );
	}

	/**
	 * Retourne l'ensemble des autres paramêtres d'objet possible pour
	 * le paramêtre de nom 'paramName'.
	 * @param paramName Un nom de paramêtre d'attribut.
	 * @return String[] Un ensemble de chaînes de caractères.
	**/
	public String[] getOthersObjectParametersFor( String paramName ) {
		return searchOthersParametersFor( AD_OBJECT_PARAMETERS, paramName );
	}

}
