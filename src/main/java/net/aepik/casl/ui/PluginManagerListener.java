/*
 * Copyright (C) 2006-2011 Thomas Chemineau
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

public class PluginManagerListener implements ListSelectionListener
{

	public void valueChanged (ListSelectionEvent e)
	{
		if (!(e.getSource() instanceof JList))
		{
			return;
		}
		if (!(((JList) e.getSource()).getModel() instanceof DefaultListModel))
		{
			return;
		}
		if (e.getValueIsAdjusting())
		{
			return;
		}
		DefaultListModel listModel = (DefaultListModel) ((JList) e.getSource()).getModel();
		for (int x = 0; x < listModel.getSize(); x++)
		{
			listModel.setElementAt(listModel.getElementAt(x), x);
		}
	}

}
