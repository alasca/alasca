/*
 * SUNDSSyntax.java		0.1		23/05/2006
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


package net.aepik.casl.core.ldap.syntax;

/**
 * SUN Directory Server syntax definition.
**/

public class SUNDSSyntax extends RFCSyntax {

////////////////////////////////
// Constantes
////////////////////////////////

	public final static String SUNDS_ATTRIBUTE = "attributeTypes:";
	protected String[][] SUNDS_ATTRIBUTE_PARAMETERS = {
		{ "1",	"NAME",			"" },
		{ "2",	"DESC",			"" },
		{ "3",	"OBSOLETE",		null },
		{ "4",	"SUP",			"" },
		{ "5",	"EQUALITY",		"" },
		{ "6",	"ORDERING",		"" },
		{ "7",	"SUBSTR",		"" },
		{ "8",	"SYNTAX",		"" },
		{ "9",	"SINGLE-VALUE",		null },
		{ "10",	"COLLECTIVE",		null },
		{ "11",	"NO-USER-MODIFICATION",	null },
		{ "12",	"USAGE",		"userApplications" },
		{ "12",	"USAGE",		"directoryOperation" },
		{ "12",	"USAGE",		"distributedOperation" },
		{ "12",	"USAGE",		"dSAOperation" },
		{ "13",	"X-DS-USE",		"" },
		{ "14",	"X-ORIGIN",		"" }
	};

	public final static String SUNDS_OBJECT = "objectClasses:";
	protected String[][] SUNDS_OBJECT_PARAMETERS = {
		{ "1",	"NAME",		"" },
		{ "2",	"DESC",		"" },
		{ "3",	"OBSOLETE",	null },
		{ "4",	"SUP",		"" },
		{ "5",	"ABSTRACT",	null },
		{ "5",	"STRUCTURAL",	null },
		{ "5",	"AUXILIARY",	null },
		{ "6",	"MUST",		"" },
		{ "7",	"MAY",		"" },
		{ "8",	"X-DS-USE",	"" },
		{ "9",	"X-ORIGIN",	"" }
	};

////////////////////////////////
// Constructeurs
////////////////////////////////

	public SUNDSSyntax() {
		super();
		super.attributeDefinitionHeader = SUNDS_ATTRIBUTE ;
		super.objectDefinitionHeader = SUNDS_OBJECT ;
		super.attributeDefinitionType = SUNDS_ATTRIBUTE.substring( 0, SUNDS_ATTRIBUTE.length()-1 );
		super.objectDefinitionType = SUNDS_OBJECT.substring( 0, SUNDS_OBJECT.length()-1 );
		super.RFC_ATTRIBUTE_PARAMETERS = SUNDS_ATTRIBUTE_PARAMETERS;
		super.RFC_OBJECT_PARAMETERS = SUNDS_OBJECT_PARAMETERS;
	}
}
