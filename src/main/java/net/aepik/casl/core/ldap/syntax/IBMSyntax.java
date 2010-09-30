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

/**
 * Syntax for IBM Directory Server (Tivoli ?).
 */
public class IBMSyntax extends RFCSyntax
{

	/**
	 * Attribute definition (and used for type).
	 */
	public final static String IBM_ATTRIBUTE = "attributeTypes:";

	/**
	 * ObjectClass definition (and used for type).
	 */
	public final static String IBM_OBJECT = "objectClasses:";

	/**
	 * Build a new IBMSyntax object.
	 */
	public IBMSyntax ()
	{
		super();
		super.attributeHeader = IBM_ATTRIBUTE ;
		super.objectClassHeader = IBM_OBJECT ;
		super.attributeType = IBM_ATTRIBUTE.substring(0, IBM_ATTRIBUTE.length() - 1);
		super.objectClassType = IBM_OBJECT.substring(0, IBM_OBJECT.length() - 1);
	}

}
