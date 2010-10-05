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

package net.aepik.casl.core.ldap;

import java.lang.String;

/**
 * Cette classe définie toutes les méthodes qui doivent être implémentées
 * pour être correctement utilisée par un objet du schema.
 * <br/><br/>
 * Un ensemble de méthodes statiques est proposées, permettant de tester des
 * chaînes de caractères, ou des caractères.
 */

public abstract class SchemaSyntax
{

	/**
	 * L'entête de définition d'un attribut
	 */
	protected String attributeHeader;

	/**
	 * L'entête de définition d'un objet
	 */
	protected String objectClassHeader;

	/**
	 * L'entête de définition d'un identifiant d'objet
	 */
	protected String objectIdentifierHeader;

	/**
	 * Le type attribut
	 */
	protected String attributeType;

	/**
	 * Le type objet
	 */
	protected String objectClassType;

	/**
	 * Le type identifiant d'objet
	 */
	protected String objectIdentifierType;

	/**
	 * Build a new SchemaSyntax object.
	 */
	public SchemaSyntax ()
	{
		this.attributeType          = null;
		this.attributeHeader        = null;
		this.objectClassType        = null;
		this.objectClassHeader      = null;
		this.objectIdentifierType   = null;
		this.objectIdentifierHeader = null;
	}

	/**
	 * Build a new SchemaSyntax object.
	 * @param String attributeType
	 * @param String attributeHeader
	 * @param String objectClassType
	 * @param String objectClassHeader
	 * @param String objectIdentifierType
	 * @param String objectIdentifierHeader
	 */
	public SchemaSyntax (
		String attributeType,
		String attributeHeader,
		String objectClassType,
		String objectClassHeader,
		String objectIdentifierType,
		String objectIdentifierHeader )
	{
		this.attributeType          = attributeType;
		this.attributeHeader        = attributeHeader;
		this.objectClassType        = objectClassType;
		this.objectClassHeader      = objectClassHeader;
		this.objectIdentifierType   = objectIdentifierType;
		this.objectIdentifierHeader = objectIdentifierHeader;
	}

	/**
	 * Créer un reader pour lire un fichier de cette syntaxe.
	 * @return SchemaFileReader Un reader spécifique à cette syntaxe.
	 */
	public abstract SchemaFileReader createSchemaReader ();

	/**
	 * Créer un nouvel objet SchemaObject d'un type donné.
	 * @param type Un type d'objet.
	 * @param id L'identifiant de l'objet.
	 * @return SchemaObject L'objet de type SchemaObject.
	 */
	public abstract SchemaObject createSchemaObject ( String type, String id );

	/**
	 * Créer un nouvel objet SchemaValue d'une valeur donnée.
	 * @param type Un type d'objet.
	 * @param param Le nom du paramêtre, pour déterminer le type de la valeur.
	 * @param value Une chaîne de caractères, ou null pour ne pas l'initialiser.
	 * @return SchemaValue L'objet de type SchemaValue.
	 */
	public abstract SchemaValue createSchemaValue ( String type, String param, String value );

	/**
	 * Créer un writer pour écrire des données.
	 * @return SchemaFileWriter Un writer spécifique à cette syntaxe.
	 */
	public abstract SchemaFileWriter createSchemaWriter ();

	/**
	 * Retourne l'ensemble des valeurs possible d'un paramêtre d'attribut.
	 * @param paramName Un nom de paramêtre.
	 * @return String[] Une ensemble de valeurs pour ce nom de paramêtres.
	 */
	public abstract String[] getAttributeParameterDefaultValues ( String paramName );

	/**
	 * Retourne l'ensemble des paramêtres d'attribut.
	 * @return String[] Un ensemble de chaînes de caractères.
	 */
	public abstract String[] getAttributeParameters ();

	/**
	 * Retourne le nom du paramêtre renseignant le nom usuel de l'objet.
	 * @param type Le type de l'objet.
	 */
	public abstract String getDisplayNameParameter ( String type );

	/**
	 * Retourne l'ensemble des valeurs possible d'un paramêtre d'objet.
	 * @param paramName Un nom de paramêtre.
	 * @return String[] Une ensemble de valeurs pour ce nom de paramêtres.
	 */
	public abstract String[] getObjectParameterDefaultValues ( String paramName );

	/**
	 * Retourne l'ensemble des paramêtres d'objet.
	 * @return String[] Un ensemble de chaînes de caractères.
	 */
	public abstract String[] getObjectParameters ();

	/**
	 * Retourne l'ensemble des autres paramêtres d'attribut possible pour
	 * le paramêtre de nom 'paramName'.
	 * @param paramName Un nom de paramêtre d'attribut.
	 * @return String[] Un ensemble de chaînes de caractères.
	 */
	public abstract String[] getOthersAttributeParametersFor ( String paramName );

	/**
	 * Retourne l'ensemble des autres paramêtres d'objet possible pour
	 * le paramêtre de nom 'paramName'.
	 * @param paramName Un nom de paramêtre d'attribut.
	 * @return String[] Un ensemble de chaînes de caractères.
	 */
	public abstract String[] getOthersObjectParametersFor ( String paramName );

	/**
	 * Retourne l'OID d'un objet du schéma à partir d'une chaîne
	 * d'initialisation d'un tel objet.
	 * @param type Le type utilisé : objet ou attribut.
	 * @param initStr Une chaîne qui permet d'initialiser un objet du schéma.
	 * @return String L'OID si il est trouvé, false sinon.
	 */
	public abstract String searchSchemaObjectOID ( String type, String initStr );

	/**
	 * Retourne les valeurs d'un objet du schéma à partir d'une chaîne
	 * d'initialisation d'un tel objet.
	 * @param type Le type utilisé : objet ou attribut.
	 * @param initStr Une chaîne qui permet d'initialiser un objet du schéma.
	 * @return String[][] Un tableau de 2 caractères, la première colonne
	 * comprends les paramêtres, la seconde les valeurs associées sous forme
	 * de chaîne de caractères.
	 */
	public abstract String[][] searchSchemaObjectValues ( String type, String initStr );

	/**
	 * Retourne l'entête de définition d'un attribut.
	 * @return String L'entête de définition.
	 */
	public String getAttributeHeader ()
	{
		return attributeHeader;
	}

	/**
	 * Retourne le nom descriptif du type AttributeType.
	 * Il sera considéré par la suite comme un type d'un objet du schema.
	 * @return String Une chaîne de caractères.
	 */
	public String getAttributeType ()
	{
		return attributeType;
	}

	/**
	 * Retourne l'entête de définition d'un objet.
	 * @return String L'entête de définition.
	 */
	public String getObjectClassHeader ()
	{
		return objectClassHeader;
	}

	/**
	 * Retourne le nom descriptif du type ObjectClass.
	 * Il sera considéré par la suite comme un type d'un objet du schema.
	 * @return String Une chaîne de caractères.
	 */
	public String getObjectClassType ()
	{
		return objectClassType;
	}

	/**
	 * Retourne l'entête de définition d'un identifiant d'objet.
	 * @return String L'entête de définition.
	 */
	public String getObjectIdentifierHeader ()
	{
		return objectIdentifierHeader;
	}

	/**
	 * Retourne le nom descriptif du type ObjectIdentifier.
	 * Il sera considéré par la suite comme un type d'un objet du schema.
	 * @return String Une chaîne de caractères.
	 */
	public String getObjectIdentifierType ()
	{
		return objectIdentifierType;
	}

	/**
	 * Retourne l'ensemble des autres paramêtres possibles pour
	 * le paramêtre de nom 'paramName', pour le type 'type'.
	 * @param type Le type de paramêtre : objet ou attribut.
	 * @param paramName Un nom de paramêtre d'attribut.
	 * @return String[] Un ensemble de chaînes de caractères.
	 */
	public String[] getOthersParametersFor ( String type, String paramName )
	{
		if (type.equals(getObjectClassType()))
		{
			return getOthersObjectParametersFor(paramName);
		}
		else if (type.equals(getAttributeType()))
		{
			return getOthersAttributeParametersFor(paramName);
		}
		return null;
	}

	/**
	 * Retourne l'ensemble des valeurs possible d'un paramêtre d'objet.
	 * @param type Le type de parametre : objet ou attribut.
	 * @param paramName Un nom de paramêtre.
	 * @return String[] Une ensemble de valeurs pour ce nom de paramêtres.
	 */
	public String[] getParameterDefaultValues ( String type, String paramName )
	{
		if (type.equals(getObjectClassType()))
		{
			return getObjectParameterDefaultValues(paramName);
		}
		else if (type.equals(getAttributeType()))
		{
			return getAttributeParameterDefaultValues(paramName);
		}
		return null;
	}

	/**
	 * Retourne l'ensemble des paramêtres d'objet.
	 * @param type Le type de parametre : objet ou attribut.
	 * @return String[] Un ensemble de chaînes de caractères.
	 */
	public String[] getParameters ( String type )
	{
		if (type.equals(getObjectClassType()))
		{
			return getObjectParameters();
		}
		else if (type.equals(getAttributeType()))
		{
			return getAttributeParameters();
		}
		return null;
	}

	/**
	 * Test si un caractère sous forme d'entier décimal
	 * est un caractère alphabétique ou non.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isAlpha ( int character )
	{
		// On regarde si le character répond au regex [a-zA-Z]
		return (   (character >= 97 && character <= 122)
			|| (character >= 65 && character <= 90));
	}

	/**
	 * Test si une chaîne de caractères est composée
	 * uniquement de caractères vérifiant isKey().
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isAnhString ( String str )
	{
		boolean ok = true;
		char[] strTab = str.toCharArray();
		for (int i = 0; i < strTab.length && ok; i++)
		{
			if (!isKey(strTab[i]))
			{
				ok = false;
			}
		}
		return ok;
	}

	/**
	 * Test si la chaîne de caractères désigne un attribut.
	 * @param str Une chaîne de caractères.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public boolean isAttributeHeader ( String str )
	{	
		if (attributeHeader == null)
		{
			return false;
		}
		return str.trim().equals(attributeHeader);
	}

	/**
	 * Test si la chaîne de caractères est un paramètre
	 * d'un attribut.
	 * @param str Une chaîne de caractères.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public boolean isAttributeDefinitionParam ( String str )
	{
		String[] params = getAttributeParameters();
		if (params == null || params.length == 0)
		{
			return false;
		}
		String str2 = str.trim();
		boolean ok = false;
		for (int i = 0; i < params.length; i++)
		{
			if (str2.equals(params[i]))
			{
				ok = true;
			}
		}
		return ok;
	}

	/**
	 * Test si un caractère sous forme d'entier décimal
	 * est un caractère héxadécimale ou non.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isHexa ( int character )
	{
		// On regarde si le character répond au regex [a-fA-F]
		return (   (character >= 97 && character <= 102)
			|| (character >= 65 && character <= 70));
	}

	/**
	 * Test si une chaîne de caractères est composée
	 * uniquement de caractères alphabétiques.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isLetterString ( String str )
	{
		boolean ok = true;
		char[] strTab = str.toCharArray();
		for (int i = 0; i < strTab.length && ok; i++)
		{
			if (!isAlpha(strTab[i]))
			{
				ok = false;
			}
		}
		return ok;
	}

	/**
	 * Test si un caractère sous forme d'entier décimal
	 * est un caractère alpha-numérique ou est "-" ou ";".
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isKey ( int character )
	{
		return (   isAlpha(character)
			|| isNumeric(character)
			|| character == 45
			|| character == 59);
	}

	/**
	 * Test si une chaîne de caractères est composée
	 * uniquement de caractères alphabétiques.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isKeyString ( String str )
	{
		if (str.length() > 0)
		{
			String subStr = str.substring(1);
			return isAlpha(str.charAt(0)) && isAnhString(subStr);
		}
		return false;
	}

	/**
	 * Test si un caractère sous forme d'entier décimal
	 * est un caractère numérique ou non.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isNumeric ( int character )
	{
		// On regarde si le character répond au regex [0-9]
		return character >= 48 && character <= 57;
	}

	/**
	 * Test si une chaîne est un id numérique d'objet: une chaîne de caractères
	 * composée de points et de nombre exclusivement.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isNumericOid ( String str )
	{
		if (str == null || str.length() == 0)
		{
			return false;
		}
		boolean ok = true;
		StringBuffer buffer = new StringBuffer();
		char[] strtab = str.toCharArray();
		for (int i = 0; i < strtab.length && ok; i++)
		{
			switch (strtab[i])
			{
				//
				// Si c'est un point, on regarde s'il n'est pas
				// en première position ou dernière position. Sinon, on
				// regarde si le caractère précédent n'est pas un point aussi.
				// Ensuite, on regarde si le buffer est numérique ou non.
				//
				case 46:
					if (i == 0 || i == strtab.length-1
						   || strtab[i-1]==strtab[i]
						   || !isNumericString(buffer.toString()))
					{
						ok = false;
					}
					break;
				default:
					buffer.append(strtab[i]);
					break;
			}
		}
		//
		// Une fois la boucle terminée, il peut rester des infos dans le
		// buffer, on va tester si c'est numérique aussi.
		//
		if (!isNumericString(buffer.toString()))
		{
			ok = false;
		}
		return ok;
	}


	/**
	 * Test si une chaîne de caractère vérifie la syntaxe BNF
	 * numericoid [ "{" numericstring "}" ].
	 * @param str Une chaîne de caractères.
	 * @return boolean True si c'est le cas.
	 */
	public static boolean isNumericOidLen ( String str )
	{
		int firstBracket, secondBracket;
		if (str == null || str.length() == 0)
		{
			return false;
		}
		//
		// On regarde si il y a une parenthèse ouvrante dans la chaîne. Si ca
		// n'est pas le cas, on regarde si la chaîne vérifie la méthode
		// isNumericOid. Alors si elle n'est pas vérifie, cette chaîne est
		// mal formée.
		//
		if ((firstBracket = str.indexOf(40)) < 0)
		{
			return isNumericOid(str);
		}
		//
		// Sinon, on regarde si il y a une parenthèse fermante.
		// Il n'y en a pas, la chaîne est mal formée.
		//
		if ((secondBracket = str.indexOf(41, firstBracket + 1)) < 0)
		{
			return false;
		}
		//
		// Enfin, on teste si la chaîne avant la parenthèse vérifie isNumericOid,
		// que la seconde parenthèse est le dernier caractère de cette chaîne,
		// et que la chaîne entre les parenthèse vérifie isNumericString.
		//
		if (isNumericOid(str.substring(0, firstBracket)) && secondBracket == str.length() - 1)
		{
			return isNumericString(str.substring(firstBracket + 1, secondBracket));
		}
		//
		// Dans tous les autres cas, on retourne faux.
		//
		return false;
	}

	/**
	 * Test si une chaîne de caractères est composée
	 * uniquement de caractères numériques.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isNumericString ( String str )
	{
		if (str == null || str.length() == 0)
		{
			return false;
		}
		boolean ok = true;
		char[] strTab = str.toCharArray();
		for (int i = 0; i < strTab.length && ok; i++)
		{
			ok &= isNumeric(strTab[i]);
		}
		return ok;
	}

	/**
	 * Test si la chaîne de caractères désigne un objet.
	 * @param str Une chaîne de caractères.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public boolean isObjectClassHeader ( String str )
	{
		if (objectClassHeader == null)
		{
			return false;
		}
		return str.trim().equals(objectClassHeader);
	}

	/**
	 * Test si la chaîne de caractères est un paramètre
	 * d'un objet.
	 * @param str Une chaîne de caractères.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public boolean isObjectDefinitionParam ( String str )
	{
		String[] params = getObjectParameters();
		if (params == null || params.length == 0)
		{
			return false;
		}
		String str2 = str.trim();
		boolean ok = false;
		for (int i = 0; i < params.length; i++)
		{
			if (str2.equals(params[i]))
			{
				ok = true;
			}
		}
		return ok;
	}

	/**
	 * Test si la chaîne de caractères désigne un identifiant d'objet.
	 * @param str Une chaîne de caractères.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public boolean isObjectIdentifierHeader ( String str )
	{
		if (objectIdentifierHeader == null)
		{
			return false;
		}
		return str.trim().equals(objectIdentifierHeader);
	}

	/**
	 * Test si un caractère sous forme d'entier décimal
	 * est répond à la régle k de la grammaire BNF de la RF 2252.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isPrintable( int character )
	{
		// a | d | """ | "(" | ")" | "+" | "," |
		// "-" | "." | "/" | ":" | "?" | " "
		return (   isAlpha(character)
			|| isNumeric(character)
			|| character == 34
			|| character == 40
			|| character == 41
			|| character == 43
			|| character == 44
			|| character == 45
			|| character == 46
			|| character == 47
			|| character == 58
			|| character == 63
			|| character == 32);
	}

	/**
	 * Test si une chaîne de caractères est composée
	 * uniquement de caractères vérifiant isP().
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isPrintableString ( String str )
	{
		if (str == null || str.length() == 0)
		{
			return false;
		}
		boolean ok = true;
		char[] strTab = str.toCharArray();
		for (int i = 0; i < strTab.length && ok; i++)
		{
			if (!isPrintable(strTab[i]))
			{
				ok = false;
			}
		}
		return ok;
	}

	/**
	 * Test si une chaîne de caractères est composée
	 * uniquement d'espaces.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isSpace ( String str )
	{
		if (str == null)
		{
			return false;
		}
		boolean ok = true;
		char[] strTab = str.toCharArray();
		for (int i = 0; i < strTab.length && ok; i++)
		{
			if (strTab[i] != 32)
			{
				ok = false;
			}
		}
		return ok;
	}

	/**
	 * Test si une chaîne de caractères vérifie la règle
	 * "dstring" de la grammaire BNF de la RFC 2252.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isString ( String str )
	{
		// A faire : Vérifier si UTF 8 !
		return str != null && str.length() > 0;
	}

	/**
	 * Teste si une chaîne de caractères est encadrée
	 * d'espaces.
	 * @param str Une chaîne de caractères.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isSurroundedBySpaces ( String str )
	{
		String str2 = str.trim();
		return !str2.equals(str);
	}

	/**
	 * Test si une chaîne de caractères vérifie isSpace.
	 * @return boolean True si c'est le cas, false sinon.
	 */
	public static boolean isWhsp ( String str )
	{
		if (str != null)
		{
			return isSpace(str);
		}
		return true;
	}

	/**
	 * Retourne le nom de cette syntaxe.
	 * @return String Le nom de cette syntaxe.
	 */
	public String toString ()
	{
		String packageName = getClass().getPackage().getName();
		String classFullName = getClass().getName();
		String currentSyntaxName = classFullName.substring(packageName.length() + 1);
		return currentSyntaxName;
	}

}

