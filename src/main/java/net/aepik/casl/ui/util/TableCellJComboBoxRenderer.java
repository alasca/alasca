/*
 * TableCellJComboBoxEditor.java		0.1		22/06/2006
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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TableCellJComboBoxRenderer implements TableCellRenderer {

////////////////////////////////
// Attributs
////////////////////////////////

	/** La liste des valeurs possibles **/
	private String[] values ;

////////////////////////////////
// Constructeurs
////////////////////////////////

	public TableCellJComboBoxRenderer( String[] values ) {
		this.values = values ;
	}

////////////////////////////////
// Methodes
////////////////////////////////

	
	public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
	      	int row,
	      	int column ) {

		if(  value instanceof String ) {
			JComboBox obj = new JComboBox( values );
			obj.setSelectedItem( (String) value );
			obj.setEnabled( !isSelected );

			return obj;
		}

		return null;
   	}

}
