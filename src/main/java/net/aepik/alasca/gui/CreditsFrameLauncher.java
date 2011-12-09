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


package net.aepik.alasca.gui;

import net.aepik.alasca.core.Manager;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class CreditsFrameLauncher extends JDialog implements ActionListener
{

	private static final long serialVersionUID = 0;

	private JButton closeButton;

	private CreditsFrame creditsFrame;

	public CreditsFrameLauncher ( ManagerFrame f, Manager m )
	{
		this.closeButton = new JButton("Fermer");
		this.closeButton.addActionListener(this);
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		p.add(closeButton);
		this.creditsFrame = new CreditsFrame(f, m, p, true);
		this.creditsFrame.setModal(true);
		this.creditsFrame.setVisible(true);
	}

	public void actionPerformed ( ActionEvent e )
	{
		Object o = e.getSource();
		if (o == this.closeButton)
		{
			this.creditsFrame.dispose();
		}
	}

}
