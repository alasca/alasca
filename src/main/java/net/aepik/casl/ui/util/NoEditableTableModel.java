/*
 * MyTableModel.java		0.1		07/06/2006
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


package net.aepik.casl.ui.util;

import javax.swing.table.DefaultTableModel;

public class NoEditableTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 0;

	protected boolean[] columnEditable ;

	public NoEditableTableModel( Object[][] data, Object[] columnNames ) {
		super( data, columnNames );

		columnEditable = new boolean[ columnIdentifiers.size() ];
		for( boolean b: columnEditable ) {
			b = false ;
		}
	}

	public boolean isCellEditable( int rowIndex, int columnIndex ) {
		return columnEditable[ columnIndex ] ;
	}

	public void setEditableColumn( boolean[] editableColumn ) {
		this.columnEditable = editableColumn ;
	}

}
