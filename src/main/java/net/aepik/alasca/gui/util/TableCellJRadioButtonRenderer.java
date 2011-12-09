/*
 * TableCellBooleanRenderer.java		0.1		31/05/2006
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
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TableCellJRadioButtonRenderer implements TableCellRenderer {

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

		if(  value instanceof Boolean ) {
			JRadioButton obj = new JRadioButton( null, null,
					((Boolean) table.getValueAt( row, column)).booleanValue() );
			obj.setBackground( table.getBackground() );
			return obj;
		}

		return null;
   	}

}
