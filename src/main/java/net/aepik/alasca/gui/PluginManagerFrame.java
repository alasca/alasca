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

import net.aepik.alasca.core.Plugin;
import net.aepik.alasca.core.PluginManager;
import net.aepik.alasca.gui.util.DescriptiveInternalFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

/**
 * Fenêtre pour lister les extentions disponibles, avec leurs
 * descriptions respectives.
 */
public class PluginManagerFrame extends JFrame implements ActionListener
{

	private static final long serialVersionUID = 0;

	/**
	 * La fen$etre parente
	 */
	private JFrame mainFrame;

	/**
	 * Le manager de plugins
	 */
	private PluginManager pluginManager;

	/**
	 * Le bouton fermer
	 */
	private JButton closeButton = new JButton("Fermer");

	/**
	 * Build a new PluginManagerFrame object.
	 * @param owner Parent frame
	 * @param pM The plugin manager
	 */
	public PluginManagerFrame (JFrame owner, PluginManager pM)
	{
		super();
		this.setSize(500, 400);
		this.setResizable(false);
		this.setLocationRelativeTo(owner);
		this.setTitle("Liste des extentions");
		this.setIconImage(owner.getIconImage());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.mainFrame = owner;
		this.pluginManager = pM;
		if (this.pluginManager.getPlugins() != null)
		{
			for (Plugin plugin : this.pluginManager.getPlugins())
			{
				plugin.setRelativeTo(this.mainFrame);
			}
		}
		this.initFrame();
	}

	/**
	 * Manager actions of this object.
	 */
	public void actionPerformed ( ActionEvent e )
	{
		Object o =e.getSource();
		if (o == closeButton)
		{
			setVisible(false);
			dispose();
		}
	}

	/**
	 * Initialize element of this frame.
	 */
	private void initFrame ()
	{
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(this.closeButton);

		JScrollPane listeScroller = new JScrollPane(new PluginManagerPanel(pluginManager));
		listeScroller.setBackground(Color.white);
		listeScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel listePanel = new JPanel(new BorderLayout());
		listePanel.add(listeScroller, BorderLayout.CENTER);
		listePanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 3, 6));

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(listePanel, BorderLayout.CENTER);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

		this.closeButton.addActionListener(this);
		this.getContentPane().add(
			new DescriptiveInternalFrame(
				this.mainFrame.getIconImage(),
				"Sélectionner un plugin à éxécuter.\nUn plugin est éxécutable si"
				+ " toutes les conditions de son éxécution sont"
				+ " réunies.",
				mainPanel
			)
		);
	}

}
