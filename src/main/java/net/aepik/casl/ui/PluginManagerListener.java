/*
 * PluginManagerListener.java		0.1		11/07/2006
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


package net.aepik.casl.ui;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

public class PluginManagerListener
		implements ListSelectionListener {

	public void valueChanged( ListSelectionEvent lse ) {

		if( lse.getSource() instanceof JList
				&& ((JList) lse.getSource()).getModel() instanceof DefaultListModel
				&& lse.getValueIsAdjusting()==false ) {
			DefaultListModel listModel = (DefaultListModel) ((JList) lse.getSource()).getModel();

			System.out.println( "ok" );

			for( int x=0; x<listModel.getSize(); x++ )
				listModel.setElementAt( listModel.getElementAt(x), x );
		}
	}

}
