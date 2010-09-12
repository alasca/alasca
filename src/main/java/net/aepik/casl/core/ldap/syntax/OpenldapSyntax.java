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


package net.aepik.casl.core.ldap.syntax;

import net.aepik.casl.core.ldap.parser.OpenldapWriter;
import net.aepik.casl.core.ldap.SchemaFileWriter;

/**
 * OpenLDAP syntax.
 */
public class OpenldapSyntax extends RFCSyntax
{

	/**
	 * Attribut definition (and used for type).
	 */
	public final static String OPENLDAP_ATTRIBUTE = "attributetype";

	/**
	 * ObjectClass definition (and used for type).
	 */
	public final static String OPENLDAP_OBJECT = "objectclass";

	/**
	 * Build a new OpenldapSyntax object.
	 */
	public OpenldapSyntax()
	{
		super();
		super.attributeDefinitionHeader = OPENLDAP_ATTRIBUTE ;
		super.objectDefinitionHeader = OPENLDAP_OBJECT ;
		super.attributeDefinitionType = OPENLDAP_ATTRIBUTE ;
		super.objectDefinitionType = OPENLDAP_OBJECT ;
	}

	/**
	 * Créer un writer pour écrire des données.
	 * @return SchemaFileWriter Un writer spécifique à cette syntaxe.
	 */
	public SchemaFileWriter createSchemaWriter ()
	{
		return new OpenldapWriter();
	}

}
