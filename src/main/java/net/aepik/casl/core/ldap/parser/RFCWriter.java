/*
 * RFCWriter.java		0.1		15/06/2006
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

import java.io.IOException;

public class RFCWriter extends SchemaFileWriter {

////////////////////////////////
// Constructeurs
////////////////////////////////

	public RFCWriter() {
		super();
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	/**
	 * Ecrit l'ensemble des objets du schema dans le flux de sortie.
	**/
	public void write( Schema schema ) throws IOException {

		if( output==null || schema==null || schema.getSyntax()==null )
			return ;

		// D'abord on écrit le DN, s'il y en a un.
		String dn = schema.getProperties().getProperty( "dn" );
		if( dn!=null ) {
			output.write( "dn: " + dn );
			output.newLine();
		}

		// Maintenant, on écrit les objets.

		SchemaSyntax syntax = schema.getSyntax();
		SchemaObject[] objets = schema.getObjectsInOrder();

		for( SchemaObject o : objets ) {

			// Il faut fabriquer la chaîne ! La déconstruction se fait dans
			// le SchemaFileReader, la reconstruction se fait donc dans
			// SchemaFileWriter. L'objet ne retourne que son contenu, il n'y
			// a pas de déclaration.

			String objStr = "";
			String oidParamName = null ;

			if( o.getType().equals( syntax.getObjectDefinitionType() ) ) {
				objStr += syntax.getObjectDefinitionHeader();

			} else if( o.getType().equals( syntax.getAttributeDefinitionType() ) ) {
				objStr += syntax.getAttributeDefinitionHeader();
			}

			o.delValue( oidParamName );
			objStr += " ( " + o.toString() + " )";

			// Enfin, on ecrit la chaîne dans le fichier. On fait attention à
			// ce que tout objet soit séparé par un retour à la ligne.

			output.write( objStr, 0, objStr.length() );
			output.newLine();
		}
	}

}
