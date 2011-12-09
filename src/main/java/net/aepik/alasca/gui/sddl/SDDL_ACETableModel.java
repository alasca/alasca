/*
 * SDDL_ACEFrame.java		0.1		26/05/2006
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


package net.aepik.alasca.gui.sddl;

import net.aepik.alasca.core.sddl.SDDL_ACEString;

import javax.swing.table.AbstractTableModel;

public class SDDL_ACETableModel extends AbstractTableModel {

	private static final long serialVersionUID = 0;

////////////////////////////////
// Attributs
////////////////////////////////

	private String[][] values;
	private Object[][] datas;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public SDDL_ACETableModel( String[][] tableVal ) {

		this.values = tableVal ;
		this.datas = new Object[tableVal.length][2];

		for( int i=0; i<tableVal.length; i++ ) {
			datas[i][0] = new Boolean( false );
			datas[i][1] = tableVal[i][1];
		}
	}

////////////////////////////////
// Methodes publiques
////////////////////////////////

	public int getRowCount() {
		return datas.length ;
	}

    public Class getColumnClass(int c) {
		return getValueAt(0,c).getClass();
	}

	public int getColumnCount() {

		if( datas.length>0 )
			return datas[0].length ;

		return 0;
	}

	public Object getValueAt( int row, int column ) {

		if( datas.length>0
				&& row>=0
				&& row<datas.length
				&& column>=0
				&& column<datas[0].length )
			return datas[row][column];

		return null;
	}

	public String getGlobalStringValue() {

		String str = "";

		for( int i=0; i<this.getRowCount(); i++ ) {
			if( ((Boolean) this.getValueAt( i,0 )).booleanValue() )
				str += values[i][0] ;
		}

		return str ;
	}

    public boolean isCellEditable( int row, int column ) {

        if( datas.length>0
        		&& row>=0
        		&& row<datas.length
        		&& column==1 ) {
            return false;
        } else {
            return true;
        }
    }

	public void setValueAt( Object obj, int row, int column ) {
		datas[row][column] = obj;
		fireTableCellUpdated( row, column );
	}

	public boolean setFromGlobalString( String str ) {

		if( str!=null ) {
			String[] tab = SDDL_ACEString.stringToTabOfValues( str, values );

			for( int i=0; tab!=null && i<tab.length; i++ ) {
				boolean ok = true ;

				for( int j=0; j<values.length && ok; j++ ) {
					if( tab[i].equals( values[j][0] ) ) {
						setValueAt( new Boolean( true ), j, 0 );
						ok = false ;
					}
				}
			}

			return true;
		}

		return false;
	}

}
