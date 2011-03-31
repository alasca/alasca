/*
 * ADCTAutoGenTableModel.java		0.1		26/05/2006
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


package net.aepik.casl.plugin.adctautogen;

import net.aepik.casl.core.ldap.SchemaObject;

import javax.swing.table.AbstractTableModel;

/**
 * Ce modèle de données pour une JTable permet de faire afficher deux colonnes.
 * La première colonne contient des cases cochables pour les objets de schémas
 * de la seconde colonne.
**/

public class ADCTAutoGenTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 0;

////////////////////////////////
// Attributs
////////////////////////////////

	private SchemaObject[] objets ;
	private String[] datas;
	private boolean[] ok;				// Indique si on peut éditer l'objet.

////////////////////////////////
// Constructeurs
////////////////////////////////

	/**
	 * Construit un nouveau model de données.
	**/
	public ADCTAutoGenTableModel( SchemaObject[] objets, String defaultValue ) {

		this.objets = objets;
		this.datas = new String[objets.length];
		this.ok = new boolean[objets.length];

		for( int i=0; i<objets.length; i++ ) {
			if( !objets[i].isKeyExists( "changetype" ) ) {
				ok[i] = true;
				datas[i] = defaultValue ;
			} else {
				ok[i] = false;
				datas[i] = objets[i].getValue( "changetype" ).toString();
			}
		}
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	public int getRowCount() { return datas.length ; }

    public Class getColumnClass( int c ) {
		return getValueAt( 0, c ).getClass();
	}

	public int getColumnCount() { return 3; }

	public Object getValueAt( int row, int column ) {

		if( datas.length>0
				&& row>=0
				&& row<datas.length
				&& column>=0
				&& column<datas.length ) {

			if( column==0 ) {
				return objets[row].getId() ;

			} else if( column==1 ) {
				return objets[row].getName();

			} else if( column==2 ) {
				return datas[row];
			}
		}

		return null;
	}

    public boolean isCellEditable( int row, int column ) {

        if( datas.length>0
        		&& row>=0
        		&& row<datas.length
        		&& column==2 ) {
			return ok[row];
        }
		return false;
    }

	public void setValueAt( Object obj, int row, int column ) {

		if( column==2 && obj instanceof String )
			datas[row] = (String) obj;
	}
}
