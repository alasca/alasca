/*
 * TableCellJRadioButtonEditor.java		0.1		31/05/2006
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


package net.aepik.alasca.gui.util;

import java.awt.Component;
import java.util.Enumeration;
import java.util.EventObject;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class TableCellJRadioButtonEditor implements TableCellEditor {

////////////////////////////////
// Methodes
////////////////////////////////

	public Component getTableCellEditorComponent(
			JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column ) {

		if( value instanceof Boolean && table!=null ) {
			for( int i=0; i<table.getRowCount(); i++ ) {
				table.setValueAt( new Boolean( false ), i, column );
			}

			table.setValueAt( !((Boolean) value).booleanValue(), row, column );
		}

		return null;
	}

	public void addCellEditorListener( CellEditorListener l ) {}
	public void cancelCellEditing() {}
	public Object getCellEditorValue() { return null ; }
	public boolean isCellEditable( EventObject anEvent ) { return true ; }
	public void removeCellEditorListener( CellEditorListener l ) {}
	public boolean shouldSelectCell( EventObject anEvent ) { return false ; }
	public boolean stopCellEditing() { return true ; }
}
